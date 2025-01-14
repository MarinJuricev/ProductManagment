resource "aws_iam_role" "ssm_ec2_role" {
  name = "${var.project}-ec2-ssm-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ssm_ec2_policy" {
  role       = aws_iam_role.ssm_ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_role_policy_attachment" "ec2_instance_policy" {
  role       = aws_iam_role.ssm_ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ReadOnlyAccess"
}

resource "aws_iam_policy" "ec2_s3_access_policy" {
  name        = "${var.project}-ec2-s3-access"
  description = "Policy to allow EC2 to access CodeDeploy S3 bucket"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "s3:GetObject",
          "s3:ListBucket"
        ],
        Resource = [
          aws_s3_bucket.codedeploy_bucket.arn,
          "${aws_s3_bucket.codedeploy_bucket.arn}/*"
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "attach_ec2_s3_policy" {
  role       = aws_iam_role.ssm_ec2_role.name
  policy_arn = aws_iam_policy.ec2_s3_access_policy.arn
}

resource "aws_iam_instance_profile" "ec2_instance_profile" {
  name = "${var.project}-ec2-ssm-profile"
  role = aws_iam_role.ssm_ec2_role.name
}

resource "aws_security_group" "ec2" {
  name        = "${var.project}-sg"
  description = "Allow only over vpn"
  vpc_id      = var.vpc_id

  # Loop through each allowed ingress CIDR
  #Allow http from vpn
  dynamic "ingress" {
    for_each = var.allowed_ingress_cidrs
    content {
      from_port   = 80
      to_port     = 80
      protocol    = "tcp"
      cidr_blocks = [ingress.value]
    }
  }
  #Allow https from all for mobile testing
    ingress {
      from_port   = 443
      to_port     = 443
      protocol    = "tcp"
      cidr_blocks = ["0.0.0.0/0"]
    }

  #Allow ssh from vpn
  dynamic "ingress" {
    for_each = var.allowed_ingress_cidrs
    content {
      from_port   = 22
      to_port     = 22
      protocol    = "tcp"
      cidr_blocks = [ingress.value]
    }
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "ec2" {
  ami                    = var.ec2_ami_image
  instance_type          = var.ec2_instance_type
  subnet_id              = var.ec2_subnet
  vpc_security_group_ids = [aws_security_group.ec2.id]
  key_name               = var.ec_ssh_keypair
  iam_instance_profile   = aws_iam_instance_profile.ec2_instance_profile.name

  tags = {
    Name = "ec2-${var.project}"
  }

  root_block_device {
    volume_size = 20
    volume_type = "gp3"
  }
}

output "instance_public_ip" {
  value = aws_instance.ec2.public_ip
}

output "instance_id" {
  value = aws_instance.ec2.id
}

resource "aws_ssm_document" "update_per_boot_script" {
  name          = "${var.project}-update-per-boot-script"
  document_type = "Command"

  content = jsonencode({
    schemaVersion = "2.2",
    description   = "Update the /var/lib/cloud/scripts/per-boot/setup.sh script on EC2",
    mainSteps = [
      {
        action = "aws:runShellScript"
        name   = "updateScript"
        inputs = {
          runCommand = [
            "cat << 'EOF' > /var/lib/cloud/scripts/per-boot/setup.sh",
            "#!/bin/bash",
            "yum update -y",
            "yum install docker -y",
            "systemctl start docker",
            "systemctl enable docker",
            "usermod -aG docker ec2-user",
            # Docker Compose installation
            "curl -L 'https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-$(uname -s)-$(uname -m)' -o /usr/local/bin/docker-compose",
            "chmod +x /usr/local/bin/docker-compose",
            # product user setup
            "useradd -m -s /bin/bash product || true",  # avoid error if user exists
            "passwd -d product",
            "usermod -aG docker product",
            "chown product:docker /var/run/docker.sock",
            # Directory creation
            "mkdir -p /home/product/project_files_dev /home/product/project_files_prod",
            "chown product:product /home/product/project_files_dev /home/product/project_files_prod",
            # Clear /etc/environment file
            "truncate -s 0 /etc/environment",
            # Secrets
            "echo 'export AWS_ACCESS_KEY_ID=${var.AWS_ACCESS_KEY_ID}' | tee -a /etc/environment",
            "echo 'export AWS_SECRET_ACCESS_KEY=${var.AWS_SECRET_ACCESS_KEY}' | tee -a /etc/environment",
            "echo 'export AWS_REGION=eu-central-1' | tee -a /etc/environment",
            "echo 'export FIREBASE_CREDENTIALS=${var.FIREBASE_CREDENTIALS}' | tee -a /etc/environment",
            "echo 'export EMAIL_SERVICE_API_KEY=${var.EMAIL_SERVICE_API_KEY}' | tee -a /etc/environment",
            # CodeDeploy agent installation
            "yum install -y ruby aws-cli",
            "cd /home/ec2-user",
            "wget https://aws-codedeploy-eu-central-1.s3.eu-central-1.amazonaws.com/latest/install",
            "chmod +x ./install",
            "./install auto",
            "service codedeploy-agent start",
            "systemctl enable codedeploy-agent",
            "EOF",
            # Make the script executable
            "chmod +x /var/lib/cloud/scripts/per-boot/setup.sh",
            # Run the script immediately
            "sudo /var/lib/cloud/scripts/per-boot/setup.sh",
          ]
        }
      }
    ]
  })
}

resource "aws_ssm_association" "update_per_boot_script_association" {
  name              = aws_ssm_document.update_per_boot_script.name
  association_name  = "${var.project}-update-per-boot-script-association"
  document_version  = "$LATEST" # Always use the latest version of the document

  # Schedule (Optional) - to run the script periodically
  # schedule_expression = "rate(24 hours)" # Run every 24 hours, adjust as needed

  # Define targets as a block
  targets {
    key    = "InstanceIds"
    values = [aws_instance.ec2.id]
  }
}
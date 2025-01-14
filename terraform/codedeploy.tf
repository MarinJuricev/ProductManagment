resource "aws_codedeploy_app" "codedeploy_application" {
  name             = var.project
  compute_platform = "Server"
}

resource "aws_iam_role" "codedeploy_service_role" {
  name = "${var.project}-codedeploy-service-role"
  assume_role_policy = jsonencode({
    Version : "2012-10-17",
    Statement : [
      {
        Effect : "Allow",
        Principal : {
          Service : "codedeploy.amazonaws.com"
        },
        Action : "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "codedeploy_service_role_policy_attach" {
  role       = aws_iam_role.codedeploy_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSCodeDeployRole"
}

resource "aws_codedeploy_deployment_group" "codedeploy_deployment_group" {
  app_name              = aws_codedeploy_app.codedeploy_application.name
  deployment_group_name = "${var.project}-deployment-group"
  service_role_arn      = aws_iam_role.codedeploy_service_role.arn

  ec2_tag_set {
    ec2_tag_filter {
      key   = "Name"
      type  = "KEY_AND_VALUE"
      value = "ec2-${var.project}"
    }
  }

  deployment_style {
    deployment_option = "WITHOUT_TRAFFIC_CONTROL"
    deployment_type   = "IN_PLACE"
  }
}

resource "aws_s3_bucket" "codedeploy_bucket" {
  bucket        = "${var.project}-codedeploy-bucket"
  force_destroy = true
  tags = {
    Name = "${var.project}-codedeploy-bucket"
  }
}

resource "aws_iam_policy" "gh_s3_access_policy" {
  name        = "${var.project}-gh-s3-access"
  description = "Policy to allow GitHub Actions Role to access CodeDeploy S3 bucket"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:ListBucket",
          "s3:DeleteObject"
        ],
        Resource = [
          aws_s3_bucket.codedeploy_bucket.arn,
          "${aws_s3_bucket.codedeploy_bucket.arn}/*"
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "attach_gh_s3_policy" {
  role       = var.product_github_role
  policy_arn = aws_iam_policy.gh_s3_access_policy.arn
}
#Network
vpc_id                = "vpc-04c9269b106e1ddd8"
allowed_ingress_cidrs = ["18.192.233.157/32"]
ec2_subnet            = "subnet-0e2d27148d678700f" #prod Public Subnet (AZ1)

#EC2
ec2_ami_image     = "ami-00f07845aed8c0ee7"
ec2_instance_type = "t3a.medium"

#Project
project          = "product-inventory-kmp"
project_dns_dev  = "inventory-kmp"
ecr_repositories = ["backend"]
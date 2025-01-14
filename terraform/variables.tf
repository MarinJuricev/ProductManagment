variable "vpc_id" {
  type    = string
  default = "vpc-04c9269b106e1ddd8"
}

variable "region" {
  type    = string
  default = "eu-central-1"
}

variable "allowed_ingress_cidrs" {
  type    = list(string)
  default = ["18.192.233.157/32"]
}

variable "ec2_subnet" {
  type    = string
  default = "subnet-04897e795488bcd64"
}

variable "project" {
  type    = string
  default = "product-inventory-kmp"
}

variable "ec2_ami_image" {
  description = "Base AMI image #Amazon Linux 2023"
  type        = string
  default     = "ami-00f07845aed8c0ee7"
}

variable "ec2_instance_type" {
  description = "EC2 instance size"
  type        = string
  default     = "t2.micro"
}

variable "route53_domain_com" {
  type    = string
  default = "product.com"
}

variable "route53_domain_dev" {
  type    = string
  default = "product.dev"
}

variable "project_dns_dev" {
  type    = string
  default = "default-dev-url"
}

variable "product_github_role" {
  type    = string
  default = "github-actions-role"
}

variable "ec_ssh_keypair" {
  type    = string
  default = "product-master"
}

variable "AWS_ACCESS_KEY_ID" {
  description = "The AWS Access Key ID"
  type        = string
  default     = "YOUR_AWS_ACCESS_KEY_ID"
}

variable "AWS_SECRET_ACCESS_KEY" {
  description = "The AWS Secret Access Key"
  type        = string
  default     = "YOUR_AWS_SECRET_ACCESS_KEY"
}

variable "FIREBASE_CREDENTIALS" {
  description = "App firebase credentials"
  type        = string
  default     = "FIREBASE_CREDENTIALS"
}

variable "EMAIL_SERVICE_API_KEY" {
  description = "App email service api key"
  type        = string
  default     = "EMAIL_SERVICE_API_KEY"
}

variable "ecr_repositories" {
  type        = list(string)
  description = "List of ECR repositories"
}
terraform {
  required_version = "~> 1.9.5"
  backend "s3" {
    bucket = "product-projects-tf-states"
    key    = "198310435290/projects/product-inventory-kmp/inventory.tfstate"
    region = "eu-central-1"
  }
}

provider "aws" {
  region = var.region

  default_tags {
    tags = {
      Provider = "Terraform"
      Project  = var.project
    }
  }
}
resource "aws_ecr_repository" "ecr_repositories" {
  count        = length(var.ecr_repositories)
  name         = "${var.project}-${var.ecr_repositories[count.index]}"
  force_delete = true
}
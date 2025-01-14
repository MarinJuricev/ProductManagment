data "aws_route53_zone" "dev_zone" {
  name = var.route53_domain_dev
}

resource "aws_route53_record" "dev-api" {
  zone_id = data.aws_route53_zone.dev_zone.zone_id
  name    = "${var.project_dns_dev}.${data.aws_route53_zone.dev_zone.name}"
  type    = "A"
  ttl     = 300

  records = [aws_instance.ec2.public_ip]
}
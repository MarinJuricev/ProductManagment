#!/bin/bash
source /etc/environment
aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin 198310435290.dkr.ecr.eu-central-1.amazonaws.com
docker-compose -f /home/MarinJuricev/project_files_dev/docker-compose.yml pull
docker-compose -f /home/MarinJuricev/project_files_dev/docker-compose.yml up -d --force-recreate
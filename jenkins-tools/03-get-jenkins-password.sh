#!/bin/bash
#
# ====================================================================
# Jenkins Tools
# ====================================================================
#
# Location:
#
#     ./jenkins-tools
#
# One-time setup (after creating or copying these scripts):
#
#     cd jenkins-tools
#     chmod +x *.sh
#
# Run this script from the jenkins-tools directory:
#
#     ./03-get-jenkins-password.sh
#
# The script automatically locates the running Jenkins container
# using the Docker Compose service label and retrieves the initial
# administrator password.
#
# ====================================================================
#
#
# Displays the Jenkins initial administrator password.
#
# Requirements:
#
#     - Docker must be running
#     - Jenkins container must be running
#     - Jenkins container must be created by Docker Compose
#

set -e

JENKINS_CONTAINER=$(docker ps -qf label=com.docker.compose.service=jenkins)

if [ -z "$JENKINS_CONTAINER" ]; then
    echo "Jenkins container is not running"
    exit 1
fi

docker exec "$JENKINS_CONTAINER" \
cat /var/jenkins_home/secrets/initialAdminPassword
#!/bin/bash
#
# ====================================================================
# Jenkins Tools
# ====================================================================
#
# Location:
#
#     ~/jenkins-tools
#
# One-time setup (after creating or copying these scripts):
#
#     cd ~/jenkins-tools
#     chmod +x *.sh
#
# Run this script from the jenkins-tools directory:
#
#     ./02-start-jenkins.sh
#
# ====================================================================
#
# Starts the Jenkins service defined in docker-compose.yml.
#
# This script:
#
#   • Builds the Jenkins image if required
#   • Starts the Jenkins container in detached mode
#   • Creates the compose network and volumes if needed
#   • Leaves all other compose services stopped
#
# Jenkins Web UI:
#
#     http://localhost:8080
#
# ====================================================================

set -e

# Change to the project root (parent of this script's directory)
PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

cd "$PROJECT_DIR"

echo "==========================================="
echo "Starting Jenkins using Docker Compose..."
echo "==========================================="

docker compose up -d --build jenkins

echo
echo "==========================================="
echo "Jenkins is starting..."
echo
echo "Project:"
pwd
echo
echo "Open:"
echo "http://localhost:8080"
echo
echo "Check status:"
echo "docker compose ps"
echo
echo "View logs:"
echo "docker compose logs -f jenkins"
echo
echo "Retrieve the initial admin password:"
echo "~/jenkins-tools/03-get-jenkins-password.sh"
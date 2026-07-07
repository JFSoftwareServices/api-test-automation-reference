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
#     ./01-clean-docker.sh
#
# ====================================================================
#
#
# --------------------------------------------------------------------
# Docker Factory Reset
#
# WARNING:
# This script deletes EVERYTHING managed by Docker:
#
#   - Running containers
#   - Stopped containers
#   - Images
#   - Volumes
#   - User-created networks
#   - Build cache
#   - Docker system cache
#
# Only Docker's default networks remain:
#   bridge
#   host
#   none
#
# Use only if you want a completely fresh Docker environment.
# --------------------------------------------------------------------

set -e

echo "=================================================="
echo " Docker Factory Reset"
echo "=================================================="

echo
echo "Stopping running containers..."
docker ps -q | xargs -r docker stop

echo
echo "Removing all containers..."
docker ps -aq | xargs -r docker rm -f

echo
echo "Removing all images..."
docker images -aq | xargs -r docker rmi -f

echo
echo "Removing all Docker volumes..."
docker volume ls -q | xargs -r docker volume rm -f

echo
echo "Removing all user-defined Docker networks..."

docker network ls --format '{{.Name}}' \
| grep -Ev '^(bridge|host|none)$' \
| xargs -r docker network rm

echo
echo "Removing Docker build cache..."
docker builder prune -af

echo
echo "Running Docker system prune..."
docker system prune -a --volumes -f

echo
echo "Remaining Docker resources:"
docker system df

echo
echo "Docker is now completely clean."
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
#     ./04-check-ports.sh
#
# ====================================================================
#
#
# Displays all listening TCP and UDP ports together with
# the owning process.
#

echo
echo "Listening Ports"
echo "=============================="

sudo ss -tulpn

echo
echo "---------------------------------------------"
echo

sudo lsof -i -P -n | grep LISTEN
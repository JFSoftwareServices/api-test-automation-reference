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
#     ./05-free-port.sh
#
# ====================================================================
#
#
# Kills the process currently listening on a port.
#
# Example:
#
#     ./05-free-port.sh 8080
#
#

PORT=$1

if [ -z "$PORT" ]; then
    echo
    echo "Usage:"
    echo
    echo "./05-free-port.sh <port>"
    echo
    exit 1
fi

PID=$(sudo lsof -t -i:$PORT)

if [ -z "$PID" ]; then
    echo
    echo "Port $PORT is already free."
    exit 0
fi

echo
echo "Process using port $PORT:"
ps -fp "$PID"

echo
echo "Stopping process..."

sudo kill -9 "$PID"

echo
echo "Port $PORT is now free."
# ═══════════════════════════════════════════════════════════════════════════════
# Dockerfile — JFSoftwareServices API Test Automation Reference
# ═══════════════════════════════════════════════════════════════════════════════
#
# 🚀 HOW TO BUILD AND RUN
# ───────────────────────────────────────────────────────────────────────────────
#
# 1. Build the image, if src/ changes (your test code), pom.xml changes (dependencies), Dockerfile changes, anything copied into the image changes
#
#     docker build -t jf-tests:local .
#
#
# 2. Run with default (DEV) environment:
#
#     docker run --rm jf-tests:local
#
#
# 3. Run with QA environment:
#
#     docker run --rm -e ENV=qa jf-tests:local
#
#
# 4. Run with staging environment:
#
#     docker run --rm -e ENV=staging jf-tests:local
#
#
# 5. Override test suite:
#
#     docker run --rm -e SUITE=testng.xml jf-tests:local
#
#
# 6. Full example (QA + custom URL):
#
#     docker run --rm \
#       -e ENV=qa \
#       -e BASE_URL_JSON=https://qa.example.com \
#       jf-tests:local
#
#
# NOTES:
#
# - "docker build" creates the image
# - "docker run" executes the container
# - No build happens automatically on "docker run"
#
# ═══════════════════════════════════════════════════════════════════════════════
#
# This Dockerfile uses a multi-stage build.
#
# A stage begins with a FROM instruction and ends immediately before the next
# FROM instruction (or at the end of the file).
#
# Each stage has its own filesystem and starts from its own base image.
# Files are NOT shared automatically between stages. To transfer files from
# one stage to another, Docker provides:
#
#     COPY --from=<stage-name> <source> <destination>
#
# Multi-stage builds help to:
#
#   • Separate dependency preparation from test execution.
#   • Keep the final image simple.
#   • Improve build performance by allowing Docker to cache expensive steps.
#
# Stages:
#
#   deps
#     Downloads Maven dependencies into ~/.m2
#
#   test-runner
#     Executes TestNG suite using cached dependencies
#
# ═══════════════════════════════════════════════════════════════════════════════


# ==============================================================================
# Stage 1: Dependency Cache
# ==============================================================================

FROM maven:3.9.9-eclipse-temurin-21 AS deps

WORKDIR /build

COPY pom.xml .

RUN mvn -q dependency:go-offline \
        -Dmaven.repo.local=/root/.m2/repository \
    || mvn dependency:resolve \
        -Dmaven.repo.local=/root/.m2/repository


# ==============================================================================
# Stage 2: Test Runner
# ==============================================================================

FROM maven:3.9.9-eclipse-temurin-21 AS test-runner

LABEL maintainer="JFSoftwareServices"
LABEL description="API Test Automation Reference"
LABEL org.opencontainers.image.source="https://github.com/JFSoftwareServices/api-test-automation-reference"

WORKDIR /app

COPY --from=deps /root/.m2 /root/.m2

COPY pom.xml .
COPY src/ src/
COPY testng.xml testng-parallel.xml ./
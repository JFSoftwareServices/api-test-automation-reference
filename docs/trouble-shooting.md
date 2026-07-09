# Troubleshooting Guide

## Overview

This guide provides solutions to the most common issues encountered when using the API Test Automation Reference framework.

The framework consists of several integrated technologies including:

* Java
* Maven
* REST Assured
* TestNG
* Docker
* Docker Compose
* Jenkins
* GitHub Codespaces
* Allure

Because these components work together, an issue in one area can sometimes appear as a failure elsewhere. This guide helps isolate and resolve common problems.

---

# Troubleshooting Strategy

When something fails, investigate in the following order:

```text id="rjqw9v"
Project

    │

    ▼

Build

    │

    ▼

Docker

    │

    ▼

Jenkins

    │

    ▼

Tests

    │

    ▼

Environment
```

Working through the stack systematically usually leads to the root cause more quickly.

---

# Maven Build Fails

## Symptoms

```text id="u5jvt3"
BUILD FAILURE
```

or

```text id="h54j2o"
Could not resolve dependencies
```

## Possible Causes

* Internet connectivity issues
* Incorrect dependency versions
* Corrupted local Maven repository
* Invalid `pom.xml`

## Resolution

Verify Maven installation:

```bash id="h1eb4v"
mvn -v
```

Clean and rebuild:

```bash id="jz2q4l"
mvn clean test
```

If necessary, delete the local Maven cache:

```text id="vn0w0s"
~/.m2/repository
```

Docker-based builds use the shared Maven cache volume instead.

---

# Tests Are Not Executed

## Symptoms

```text id="m4pxbl"
BUILD SUCCESS

Tests run: 0
```

## Possible Causes

* Incorrect TestNG suite
* Incorrect package names
* Test annotations missing
* Wrong Maven command

## Resolution

Verify:

* `testng.xml`
* `@Test` annotations
* package structure
* `-DsuiteXmlFile` parameter

---

# Docker Build Fails

## Symptoms

```text id="q6lbb9"
docker build failed
```

## Possible Causes

* Docker daemon not running
* Dockerfile syntax error
* Build context incorrect
* Missing project files

## Resolution

Check Docker:

```bash id="tv0dqt"
docker version
```

Rebuild:

```bash id="bwm0qv"
docker compose build
```

---

# Docker Compose Cannot Start

## Symptoms

```text id="cv6dco"
service failed to start
```

or

```text id="t0uznm"
invalid compose project
```

## Possible Causes

* Invalid YAML
* Missing volume definition
* Missing environment variables
* Incorrect service name

## Resolution

Validate:

```bash id="k6zjme"
docker compose config
```

Check for:

* indentation errors
* undefined volumes
* undefined networks
* missing environment variables

---

# Environment Variables Not Set

## Symptoms

```text id="rtm5nn"
The "ENV" variable is not set.
```

## Resolution

Set the variables before running Maven or Docker Compose.

Example:

```bash id="du2x6e"
export ENV=dev
export SUITE=testng.xml
export BASE_URL_JSON=https://jsonplaceholder.typicode.com
```

Or provide them inline:

```bash id="tdt3ny"
ENV=dev docker compose run --rm tests-dev
```

See `environment-configuration.md` for more information.

---

# Jenkins Cannot Find Allure Results

## Symptoms

```text id="qh4y8v"
target/allure-results does not exist
```

## Possible Causes

* Tests failed before generating results
* Incorrect Docker volume mapping
* Wrong Jenkins workspace
* Wrong report path

## Resolution

Verify inside the Jenkins workspace:

```bash id="q0uw4v"
ls -la target
```

Check:

```bash id="g6d87p"
ls -la target/allure-results
```

If Docker is used, confirm that the `target` directory is correctly mounted between the test container and the Jenkins workspace.

---

# Jenkins Cannot Find Test Reports

## Symptoms

JUnit publisher reports:

```text id="c9jvzh"
No test report files were found
```

## Resolution

Verify:

```text id="j9xgjr"
target/surefire-reports/
```

exists inside the Jenkins workspace.

---

# Docker Volume Issues

## Symptoms

Test results disappear after the container exits.

## Possible Causes

* Missing volume mapping
* Wrong host directory
* Incorrect `${WORKSPACE}` value

## Resolution

Confirm that the Docker Compose service mounts:

```text id="llm14l"
${WORKSPACE}/target:/app/target
```

when executed from Jenkins.

For local execution, an appropriate local directory should be mounted instead.

---

# Jenkins Workspace Issues

## Symptoms

```text id="ny0fmn"
target directory does not exist
```

## Resolution

Display the current workspace:

```bash id="cqlkjl"
pwd
```

Display the Jenkins workspace variable:

```bash id="v09rcl"
echo $WORKSPACE
```

Verify that the mounted directory matches the Jenkins workspace.

---

# Docker Cannot Connect to Docker

## Symptoms

```text id="fyj0ln"
Cannot connect to the Docker daemon
```

## Resolution

Verify:

```bash id="dvrghg"
docker version
```

Ensure the Docker socket is mounted:

```text id="9oyrxv"
/var/run/docker.sock
```

This is required when Jenkins launches Docker containers.

---

# Jenkins Pipeline Fails

## Symptoms

Pipeline stops unexpectedly.

## Resolution

Check:

* Jenkins Console Output
* Docker logs
* TestNG output
* Maven output

The console log usually identifies the failing stage.

---

# API Tests Fail

## Symptoms

HTTP status assertions fail.

Example:

```text id="kwxj0z"
Expected 200

Actual 404
```

## Possible Causes

* Incorrect endpoint
* Wrong environment
* Invalid test data
* API unavailable

## Resolution

Verify:

* base URLs
* endpoint paths
* authentication
* environment configuration

---

# Parallel Execution Problems

## Symptoms

Tests fail only when executed in parallel.

## Possible Causes

* Shared mutable state
* Shared test data
* Race conditions

## Resolution

Ensure tests are:

* thread-safe
* independent
* isolated from one another

See `parallel-execution.md` for detailed guidance.

---

# GitHub Codespaces Issues

## Symptoms

Docker commands fail.

## Resolution

Confirm that the Dev Container has started successfully and that Docker-in-Docker is available.

Verify:

```bash id="cx9w9r"
docker version
```

Also verify:

```bash id="jsl5ko"
mvn -v
```

Both commands should execute successfully.

---

# IntelliJ Issues

## Symptoms

Project does not build.

## Resolution

Verify:

* Java 21 SDK
* Maven installation
* Imported Maven project
* Dependencies downloaded successfully

Reload the Maven project if required.

---

# Useful Diagnostic Commands

Display Docker containers:

```bash id="i0vh4o"
docker ps
```

Display Docker Compose services:

```bash id="jlwm4w"
docker compose ps
```

Display Jenkins logs:

```bash id="cm5v9v"
docker compose logs -f jenkins
```

Display Maven version:

```bash id="b4lxxy"
mvn -v
```

Display Java version:

```bash id="bvx2dw"
java -version
```

Verify Docker:

```bash id="vhsb83"
docker version
```

Display workspace:

```bash id="7l2vqa"
pwd
```

---

# When Reporting Issues

When raising an issue or asking for assistance, include:

* operating system
* Java version
* Maven version
* Docker version
* Docker Compose version
* Jenkins version
* framework version
* complete error message
* relevant console output
* steps to reproduce the issue

Providing this information significantly speeds up troubleshooting.

---

# Related Documentation

* `getting-started.md`
* `devcontainer.md`
* `docker.md`
* `docker-compose.md`
* `environment-configuration.md`
* `jenkins-setup.md`
* `jenkins-pipeline.md`
* `parallel-execution.md`
* `allure-reporting.md`

---

# Summary

Most issues encountered with the API Test Automation Reference framework fall into one of five categories: configuration, build, Docker, Jenkins, or test implementation. By following a structured troubleshooting approach and using the diagnostic commands provided in this guide, developers can quickly identify and resolve problems while maintaining a consistent development and CI/CD experience across local machines, GitHub Codespaces, Docker, and Jenkins.

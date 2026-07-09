# Dockerfile Guide

## Overview

The Dockerfile defines the environment used to execute the API test automation framework.

Rather than requiring every developer or Jenkins agent to install Java, Maven and the project dependencies locally, the framework packages everything into a Docker image that can be executed consistently on any machine capable of running Docker.

The Dockerfile is located in the project root.

```text
Dockerfile
```

The image built from this Dockerfile is used by the Docker Compose test services:

* tests-dev
* tests-qa
* tests-staging
* tests-prod

---

# Why Use Docker?

One of the biggest challenges with automated testing is ensuring every developer and every CI server runs tests in an identical environment.

Without Docker:

```
Developer A
Java 21
Maven 3.9

↓

Developer B
Java 17
Maven 3.8

↓

CI Server
Java 21
Maven 3.9

↓

Different behaviour
```

With Docker:

```
Developer

↓

Docker Image

↓

Exactly the same environment

↓

Identical test execution
```

This eliminates the "works on my machine" problem.

---

# How the Dockerfile Fits into the Architecture

The Dockerfile creates the image used by every test container.

```
Developer

      │

docker compose

      │

Build Docker Image

      │

Dockerfile

      │

Docker Image

      │

tests-dev
tests-qa
tests-staging
tests-prod

      │

Maven

      │

TestNG

      │

REST Assured

      │

API Endpoints
```

---

# Build Process

When Docker Compose starts a test service, it first checks whether the image already exists.

If not, Docker executes the Dockerfile.

```
docker compose run tests-dev

        │

Image exists?

        │

   Yes ─────────────► Start Container

        │

        No

        │

Build Image

        │

Dockerfile

        │

Create Image

        │

Start Container
```

---

# Working Directory

The Dockerfile defines a working directory.

For example:

```dockerfile
WORKDIR /app
```

This means all subsequent Docker instructions execute relative to `/app`.

Project files are copied into this location.

Example:

```
Container

/app

    ├── pom.xml
    ├── src
    ├── target
    └── testng.xml
```

The framework expects Maven to execute from this directory.

---

# Copying Project Files

The Dockerfile copies the project into the image.

Conceptually:

```
Local Project

↓

Docker Build

↓

Docker Image

↓

/app
```

This gives the container everything required to execute the framework.

---

# Installing Dependencies

During the Docker build, Maven downloads all required project dependencies.

These include:

* REST Assured
* TestNG
* Allure
* Jackson
* Apache HTTP Client
* Logging libraries

Downloading dependencies during the image build significantly reduces startup time for subsequent test executions.

---

# Multi-Stage Builds

The framework uses a multi-stage Docker build.

A multi-stage build allows a single Dockerfile to produce multiple images with different purposes.

Conceptually:

```
Dockerfile

      │

      ├──────── builder
      │
      ├──────── test-runner
      │
      └──────── future stages
```

Each stage has its own name.

Docker Compose selects the required stage using:

```yaml
target: test-runner
```

This tells Docker to build only the image required for executing automated tests.

Using named build stages keeps the Dockerfile flexible and allows additional stages to be introduced later without requiring multiple Dockerfiles.

---

# The Test Runner Stage

The framework currently executes tests using the `test-runner` stage.

This stage contains everything required to execute the automation framework, including:

* Java
* Maven
* Project source code
* Test resources
* Project dependencies

When Docker Compose starts a test service, it creates a container from this stage.

```
Dockerfile

        │

test-runner Stage

        │

Docker Image

        │

Container

        │

Maven Test Execution
```

---

# Running Maven

The container executes Maven using a command similar to:

```bash
mvn test
```

Docker Compose supplies additional parameters at runtime, such as:

* environment
* suite
* base URLs

The Docker image itself remains environment independent.

---

# Environment Variables

Configuration is supplied using environment variables rather than modifying source code.

For example:

```
ENV

↓

Docker Compose

↓

Container

↓

Maven

↓

System Properties

↓

Framework Configuration
```

This allows the same image to execute against multiple environments.

---

# Build Once, Execute Anywhere

A key design goal of the framework is to build the Docker image once and reuse it for multiple environments.

```
Docker Image

       │

       ├── Development

       ├── QA

       ├── Staging

       └── Production
```

Only the configuration changes between environments.

---

# Interaction with Docker Compose

Docker Compose builds the Docker image using:

```yaml
build:
  context: .
  dockerfile: Dockerfile
  target: test-runner
```

The Dockerfile is responsible only for creating the image.

Docker Compose is responsible for:

* creating containers
* passing environment variables
* mounting volumes
* attaching networks
* starting containers

Keeping these responsibilities separate makes the solution easier to maintain.

---

# Interaction with Jenkins

Jenkins never runs Maven directly.

Instead, Jenkins executes Docker Compose.

```
Jenkins

      │

Docker Compose

      │

Dockerfile

      │

Docker Image

      │

Test Container

      │

Maven

      │

REST Assured Tests
```

This ensures Jenkins executes tests in exactly the same environment used during local development.

---

# Relationship with the Dev Container

The development environment and the execution environment serve different purposes.

The Dev Container provides a consistent development experience for developers using GitHub Codespaces or Visual Studio Code Dev Containers.

The Dockerfile defines the runtime environment used to execute automated tests.

```
Developer

↓

Dev Container

↓

Write Code

──────────────────────────────

Dockerfile

↓

Build Image

↓

Execute Tests
```

Although both use Docker technology, they solve different problems.

---

# Summary

The Dockerfile is responsible for creating the reusable execution environment for the automation framework.

Its responsibilities include:

* defining the runtime environment
* installing Java and Maven
* copying the project
* restoring project dependencies
* creating the test execution image
* supporting repeatable execution across local development and CI/CD

Docker Compose and Jenkins build upon this image to execute automated tests in a consistent, predictable manner regardless of where the framework is run.

# Dev Container Guide

## Overview

This framework includes a Dev Container configuration that provides a consistent development environment for contributors.

The purpose of the Dev Container is to ensure that developers have the same core tooling and versions regardless of where they work.

The Dev Container is used by:

* GitHub Codespaces
* VS Code Dev Containers running locally

It provides the environment required to develop, test, and run the framework.

---

# Why Use a Dev Container?

Without a Dev Container, each developer must manually install and configure:

* Java
* Maven
* Git tooling
* Docker tooling
* IDE extensions
* Project-specific settings

This can lead to differences between developer machines.

Example:

```
Developer A

Java 21
Maven 3.9.16


Developer B

Java 17
Maven 3.8.x
```

The same code may behave differently because the environments are different.

The Dev Container solves this by defining the development environment as code.

---

# Development Architecture

When using GitHub Codespaces, the framework uses the following architecture:

```text
GitHub Codespace

        |
        |
        v

Dev Container
(Java 21 + Maven 3.9.16 + Docker CLI)

        |
        |
        v

Docker Engine

        |
        |
        +----------------+
        | Jenkins        |
        | Container      |
        +----------------+

                |
                |
                v

        Test Containers

        tests-dev
        tests-qa
        tests-staging
        tests-prod
```

The responsibilities are separated:

| Component         | Responsibility                      |
| ----------------- | ----------------------------------- |
| GitHub Codespace  | Cloud development environment       |
| Dev Container     | Developer tooling and configuration |
| Docker Engine     | Runs containers                     |
| Jenkins Container | Executes CI/CD pipelines            |
| Test Containers   | Execute automated tests             |

---

# Dev Container Configuration

The Dev Container configuration is located at:

```
.devcontainer/devcontainer.json
```

Example:

```json
{
  "name": "api-test-automation-reference",

  "image": "mcr.microsoft.com/devcontainers/java:21",

  "features": {
    "ghcr.io/devcontainers/features/git:1": {},

    "ghcr.io/devcontainers/features/docker-in-docker:4": {
      "moby": false
    },

    "ghcr.io/devcontainers/features/java:1": {
      "version": "21",
      "installMaven": true,
      "mavenVersion": "3.9.16"
    }
  }
}
```

The configuration defines the tools and features available inside the development environment.

---

# Included Tooling

The Dev Container provides:

## Java

Version:

```
Java 21
```

Java is required to build and execute the automation framework.

---

## Maven

Version:

```
Maven 3.9.16
```

Maven manages:

* Project dependencies
* Compilation
* Test execution
* Build lifecycle

Verify:

```bash
mvn -version
```

---

## Git

Git tooling is installed through:

```json
"ghcr.io/devcontainers/features/git:1"
```

This allows developers to:

* Clone repositories
* Commit changes
* Push changes
* Manage branches

---

## Docker CLI and Docker Compose Support

The Dev Container provides Docker command-line support through:

```json
"ghcr.io/devcontainers/features/docker-in-docker:4"
```

This allows developers to run:

```bash
docker --version

docker compose version

docker ps
```

and execute Docker workflows.

The Dev Container provides Docker tooling access.

Docker is then used to run:

* Jenkins container
* Test execution containers
* Supporting services

---

# VS Code Extensions

The Dev Container automatically installs recommended extensions.

Configured extensions include:

## Java Support

```
vscjava.vscode-java-pack
vscjava.vscode-maven
```

Provides:

* Java language support
* Maven integration
* Test execution support

---

## Docker Support

```
ms-azuretools.vscode-docker
```

Provides:

* Dockerfile support
* Container visibility
* Docker Compose assistance

---

## XML and API Support

```
redhat.vscode-xml
42crunch.vscode-openapi
```

Provides:

* XML editing
* OpenAPI specification support

---

## Git Support

```
eamodio.gitlens
```

Provides:

* Git history
* Repository insights
* Change tracking

---

# Automatic Setup Commands

The Dev Container executes setup commands automatically.

## postCreateCommand

Configured as:

```json
"postCreateCommand":
"mvn -v && java -version && echo '✓ Development environment ready'"
```

Runs after the container is created.

Purpose:

* Verify Maven installation
* Verify Java installation
* Confirm the environment is ready

---

## postStartCommand

Configured as:

```json
"postStartCommand":
"mvn -q dependency:resolve || true"
```

Runs whenever the container starts.

Purpose:

* Resolve Maven dependencies
* Improve startup experience

The `|| true` prevents startup failure if dependency resolution has an issue.

---

# Using the Dev Container

## GitHub Codespaces

1. Open the repository in GitHub.
2. Select:

```
Code → Codespaces → Create codespace
```

3. Wait for the container to initialise.

Verify:

```bash
java -version

mvn -version
```

---

## VS Code Local Development

Requirements:

* Docker Desktop
* VS Code
* Dev Containers extension

Open the repository:

```
VS Code
    |
    |
Dev Containers: Reopen in Container
```

The same development environment will be created locally.

---

# IntelliJ IDEA Users

The Dev Container is primarily designed for:

* GitHub Codespaces
* VS Code Dev Containers

Developers using IntelliJ IDEA can still work with the project.

They should configure:

| Tool   | Version                          |
| ------ | -------------------------------- |
| Java   | 21                               |
| Maven  | 3.9.x                            |
| Docker | Required for container execution |

The source code remains unchanged.

The difference is that IntelliJ users manage their development environment locally rather than through the Dev Container.

---

# Dev Container vs Docker Test Containers

These are different concepts.

## Dev Container

Purpose:

```
Developer environment
```

Provides:

* Java
* Maven
* IDE tooling
* Docker CLI

## Test Containers

Purpose:

```
Test execution environment
```

Provides:

* Maven test execution
* Test isolation
* Repeatable CI execution

Example:

```text
Developer

    |
    |
Dev Container

    |
    |
docker compose

    |
    |
tests-qa container

    |
    |
API Tests
```

---

# When Should Developers Use Each Approach?

| Scenario                      | Recommended Approach |
| ----------------------------- | -------------------- |
| Learning the framework        | GitHub Codespaces    |
| Contributing changes          | Dev Container        |
| Running tests locally         | Maven or Docker      |
| Running CI/CD simulation      | Jenkins Docker setup |
| Enterprise pipeline execution | Jenkins              |

---

# Related Documentation

Further information:

```
docs/project-architecture.md

docs/docker.md

docs/docker-compose.md

docs/jenkins-setup.md

docs/jenkins-pipeline.md
```

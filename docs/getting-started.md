# Getting Started Guide

## Overview

This guide explains how to get started with the API Test Automation Reference Framework.

This repository is designed to be used in two ways:

### 1. Reference and Learning Framework

The project demonstrates how an enterprise-style API automation framework can be structured.

It provides examples of:

* API automation using Java, RestAssured and TestNG
* Environment-based configuration
* Sequential and parallel test execution
* Docker-based test execution
* Jenkins CI/CD integration
* Quality gates and production promotion workflows
* Allure reporting

The repository can be used as a learning resource to understand the complete automation lifecycle.

---

### 2. Starting Point for a New Automation Project

The repository can also be cloned and adapted as a foundation for a new automation framework.

Typical changes would include:

* Replace example API tests
* Configure application-specific endpoints
* Add authentication handling
* Add project-specific validations
* Extend reporting and CI/CD configuration

The intention is that teams can start from a working framework rather than creating all infrastructure from scratch.

---

# Development and Execution Model

The framework is designed around a consistent execution model.

The same automation code can execute in different environments:

```
Developer Machine
        |
        |
GitHub Codespaces
        |
        |
Docker Containers
        |
        |
Jenkins CI/CD
```

The tests themselves do not change between environments.

The difference is only how the execution environment is provided.

---

# 1. Clone the Repository

Clone the repository:

```bash
git clone <repository-url>

cd api-test-automation-reference
```

The repository contains:

```
api-test-automation-reference

├── src
│   └── test
│       └── java
│           └── API automation tests
│
├── docker
│   └── Jenkins Docker configuration
│
├── jenkins-tools
│   └── Jenkins helper scripts
│
├── docs
│   └── Additional documentation
│
├── docker-compose.yml
├── Dockerfile
├── Jenkinsfile
├── pom.xml
└── .devcontainer
    └── devcontainer.json
```

---

# 2. Choose Development Environment

There are three supported approaches.

---

# Option 1: GitHub Codespaces (Recommended)

GitHub Codespaces provides a ready-to-use development environment based on the included Dev Container.

The Dev Container provides:

- Java 21
- Maven 3.9.16
- Git tooling
- Docker command-line support for running containers and Docker Compose workflows
- VS Code extensions
- Project-specific settings

The Dev Container is the same environment used to develop this framework.

To start:

1. Open the repository in GitHub.
2. Select:

```
Code → Codespaces → Create codespace
```

3. Wait for the environment to initialise.

Verify:

```bash
java -version

mvn -version
```

Expected:

```
Java 21

Apache Maven 3.9.x
```

More information:

```
docs/devcontainer.md
```

---

# Option 2: VS Code Dev Containers Locally

Developers can use the same Dev Container locally.

Requirements:

* Docker Desktop
* VS Code
* Dev Containers extension

Open the project:

```
VS Code
    |
    |
Dev Containers: Reopen in Container
```

The container will provide the same Java and Maven environment as GitHub Codespaces.

---

# Option 3: Local IDE Development

Developers can also use IntelliJ IDEA or another Java IDE.

The project does not require a specific IDE.

The local environment should provide:

| Tool   | Version                          |
| ------ | -------------------------------- |
| Java   | 21                               |
| Maven  | 3.9.x                            |
| Docker | Required for container execution |

Example IntelliJ configuration:

```
Project SDK:
Java 21

Build Tool:
Maven
```

The Dev Container provides the recommended standard environment, but developers using local IDEs can configure an equivalent setup.

---

# 3. Verify the Project

Check Java:

```bash
java -version
```

Check Maven:

```bash
mvn -version
```

Compile:

```bash
mvn clean compile
```

Expected:

```
BUILD SUCCESS
```

---

# 4. Running Tests Locally

Tests are executed using Maven and TestNG.

## Run Default Tests

```bash
mvn test
```

---

## Run Sequential Tests

The sequential suite uses:

```
testng.xml
```

Example:

```bash
mvn test \
-DsuiteXmlFile=testng.xml
```

---

## Run Parallel Tests

The parallel suite uses:

```
testng-parallel.xml
```

Example:

```bash
mvn test \
-DsuiteXmlFile=testng-parallel.xml
```

Parallel execution is controlled by TestNG:

```xml
<suite parallel="methods" thread-count="8">
```

More information:

```
docs/parallel-execution.md
```

---

# 5. Environment Configuration

The framework supports external configuration through environment variables.

This allows the same tests to execute against different environments.

Example:

```bash
export ENV=qa

export BASE_URL_JSON=https://jsonplaceholder.typicode.com

export BASE_URL_XML=https://jsonplaceholder.typicode.com

export LOG_REQUESTS=false

export LOG_RESPONSES=false
```

Run:

```bash
mvn test
```

Supported environments:

```
dev
qa
staging
prod
```

---

# 6. Running Tests Using Docker

The framework supports containerised test execution.

Docker provides:

* Consistent runtime environment
* Isolated dependencies
* Repeatable execution
* CI/CD compatibility

Architecture:

```
Docker Compose

      |
      |

Test Container

      |
      |

Java + Maven + TestNG

      |
      |

API Tests
```

Each environment has a dedicated container:

```
tests-dev

tests-qa

tests-staging

tests-prod
```

Example:

```bash
docker compose \
--profile qa \
run --rm tests-qa
```

More information:

```
docs/docker.md

docs/docker-compose.md
```

---

# 7. Jenkins CI/CD Execution

The framework includes Jenkins integration for complete CI/CD execution.

Jenkins provides:

* Automated builds
* Docker-based execution
* Environment promotion
* Approval gates
* Allure reports

There are two common ways to run Jenkins.

---

# Jenkins Running Locally Using Docker

For developers with Docker installed locally:

```
Developer Machine

        |

Docker Compose

        |

Jenkins Container

        |

Test Containers
```

Start Jenkins:

```bash
./jenkins-tools/01-start-jenkins.sh
```

Access:

```
http://localhost:8080
```

---

# Jenkins Running Inside GitHub Codespaces

When working in GitHub Codespaces:

```
GitHub Codespace

        |

Dev Container

        |

Docker Engine

        |

Jenkins Container

        |

Test Containers
```

The same Docker Compose configuration can be used.

The difference is that Docker is provided by the Codespace environment rather than the developer's local machine.

---

# 8. Jenkins One-Time Setup

Before executing the pipeline, Jenkins requires initial configuration.

This includes:

* Installing required Jenkins plugins
* Configuring Allure Commandline
* Creating the pipeline job
* Connecting the repository
* Validating Docker permissions

Detailed instructions:

```
docs/jenkins-setup.md
```

---

# 9. Jenkins Pipeline Workflow

The pipeline follows an environment promotion model:

```
DEV

 |

QA

 |

STAGING

 |

Production Approval Gate

 |

PROD
```

Production deployment requires manual approval.

The approval step allows a user to:

* Continue promotion to production
* Abort the deployment

More information:

```
docs/jenkins-pipeline.md
```

---

# 10. Allure Reporting

The framework generates Allure test reports.

Reports provide:

* Test execution history
* Passed and failed tests
* Failure details
* Attachments
* Test timing information

More information:

```
docs/allure-reporting.md
```

---

# Recommended Learning Path

For developers new to the framework:

## Step 1

Understand the project structure:

```
docs/project-architecture.md
```

## Step 2

Understand the Dev Container:

```
docs/devcontainer.md
```

## Step 3

Understand Docker execution:

```
docs/docker.md

docs/docker-compose.md
```

## Step 4

Understand tests:

```
docs/test-framework.md
```

## Step 5

Understand CI/CD:

```
docs/jenkins-setup.md

docs/jenkins-pipeline.md
```

---

# Next Steps

After successfully running the reference project:

1. Replace the example API tests.
2. Add application-specific endpoints.
3. Configure authentication.
4. Add project-specific validations.
5. Extend CI/CD workflows.
6. Add additional reporting and quality controls.

This framework is intended to be adapted and extended rather than used unchanged.


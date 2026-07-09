# API Test Automation Reference Framework

A reusable Java API test automation framework built using:

* **Java 21**
* **Rest Assured**
* **TestNG**
* **Maven**
* **Docker**
* **Docker Compose**
* **Jenkins**
* **GitHub Codespaces**
* **Allure Reporting**

This repository is designed to serve two purposes:

1. A **reference framework** that can be cloned and adapted as the foundation for a new API automation project.
2. A **learning resource** that explains modern API automation architecture, containerisation, CI/CD practices, and framework design decisions.

The goal is to demonstrate how a maintainable API automation framework can be structured to support:

* local development
* cloud-based development environments
* containerised execution
* continuous integration
* environment promotion
* automated reporting

---

# Repository Purpose

This repository is intentionally created as a **reference implementation**, not as a finished application.

The framework provides the core engineering structure required for an enterprise-style API automation solution while allowing teams to replace the example tests with their own APIs, environments and business requirements.

The expected workflow is:

```
Clone Repository

        |

        v

Understand Framework Structure

        |

        v

Replace Example API Tests

        |

        v

Configure Environments

        |

        v

Extend Framework Capabilities

        |

        v

Integrate With Delivery Pipeline
```

Developers can either:

* start building immediately using the existing framework structure
* study the implementation to understand how each component works and why it exists

---

# Who Is This Repository For?

This framework is intended for:

* Software Development Engineers in Test (SDETs)
* Test Automation Engineers
* QA Engineers
* Software Engineers responsible for API quality
* Developers learning automation architecture
* Teams requiring a reusable API automation foundation

## Experienced Engineers

Experienced automation engineers can clone the repository and begin replacing the example tests with their own API validations.

The framework provides:

* project structure
* build configuration
* test execution model
* Docker execution model
* Jenkins integration
* reporting approach

## Engineers Learning Automation

Engineers learning modern automation practices can use the documentation to understand:

* why specific technologies are used
* how components interact
* how local development connects to CI/CD execution
* how tests move through environments

---

# Key Features

## Test Automation

* Java 21 based automation framework
* REST API testing using Rest Assured
* TestNG execution framework
* Maven dependency management
* Environment-based configuration
* Sequential and parallel test execution

## Development

* GitHub Codespaces support
* VS Code Dev Container configuration
* Consistent Java and Maven tooling
* Docker development support

## Containerisation

* Docker-based test execution
* Docker Compose orchestration
* Separate test runner containers
* Jenkins containerised execution

## CI/CD

* Jenkins pipeline integration
* Automated environment promotion
* Manual production approval gate
* Quality checkpoints
* Allure reporting integration

---

# Technology Stack

| Technology        | Purpose                                 |
| ----------------- | --------------------------------------- |
| Java 21           | Programming language                    |
| Maven             | Build and dependency management         |
| Rest Assured      | REST API automation                     |
| TestNG            | Test execution framework                |
| Docker            | Containerised execution                 |
| Docker Compose    | Multi-container orchestration           |
| Jenkins           | CI/CD automation and promotion workflow |
| GitHub Codespaces | Cloud development environment           |
| Dev Containers    | Consistent development tooling          |
| Allure            | Test reporting                          |

---

# Getting Started

The recommended starting point is the Getting Started guide.

The guide explains:

* cloning the repository
* opening the project in GitHub Codespaces
* using the Dev Container
* running tests
* understanding the project layout

Start here:

[Getting Started Guide](docs/getting-started.md)

---

# Execution Models

The framework is designed so the same automation code can execute in multiple environments.

The objective is to keep the test framework independent from the execution platform.

| Execution Mode    | Description                                                            |
| ----------------- | ---------------------------------------------------------------------- |
| GitHub Codespaces | Cloud development environment using the included Dev Container         |
| Local Development | IDE-based development using IntelliJ IDEA, VS Code or another Java IDE |
| Docker            | Tests executed inside isolated Docker containers                       |
| Jenkins CI/CD     | Automated execution through the Jenkins pipeline                       |

---

# Development Environment

The recommended development environment is the included Dev Container.

The Dev Container provides:

* Java 21
* Maven 3.9.16
* Git tooling
* Docker support
* VS Code extensions
* Project-specific settings

This is the same development environment used to build the framework.

Developers can use:

* GitHub Codespaces
* VS Code Dev Containers locally

Developers who prefer IntelliJ IDEA or another Java IDE can also use the framework.

The local environment should provide equivalent tooling:

* Java 21
* Maven 3.9.x
* Docker (if running containerised tests)

The automation code remains unchanged regardless of development environment.

More information:

[Dev Container Guide](docs/devcontainer.md)

---

# Running Tests

Tests can be executed using multiple approaches depending on the required workflow.

## Maven Execution

Tests can execute directly using Maven:

```bash
mvn test
```

Parallel execution:

```bash
mvn test -DsuiteXmlFile=testng-parallel.xml
```

---

## Docker Execution

Tests can execute inside Docker containers:

```bash
docker compose run --rm tests-dev
```

Docker provides a consistent execution environment containing:

* Java version
* Maven version
* dependencies
* test configuration

More information:

[Test Execution Guide](docs/test-execution.md)

---

# CI/CD Pipeline

The repository includes a Jenkins pipeline demonstrating how API automation can be integrated into a continuous integration and environment promotion workflow.

The pipeline demonstrates a typical enterprise-style flow:

```text id="9z7l5f"
Developer Commit

        |

        v

Jenkins Pipeline Trigger

        |

        v

Checkout Source Code

        |

        v

Build Test Container Images

        |

        v

DEV Environment Tests

        |

        v

QA Environment Tests

        |

        v

STAGING Environment Tests

        |

        v

Production Approval Gate

        |

        v

PROD Environment Tests

        |

        v

Allure Reporting
```

Production deployment is protected through a manual approval step, allowing a reviewer to approve or reject promotion.

The pipeline demonstrates:

* automated test execution
* environment-specific configuration
* containerised execution
* promotion controls
* reporting integration

More information:

* [Jenkins Pipeline Guide](docs/jenkins-pipeline.md)
* [Promotion Pipeline Guide](docs/promotion-pipeline.md)

---

# Project Architecture

The framework follows a layered architecture where development environments, containers, test execution and CI/CD tooling work together.

High-level view:

```text id="j6m3pq"
Developer

    |

    v

GitHub Repository

    |

    v

GitHub Codespaces
or
Local Development Environment

    |

    v

Dev Container

(Java + Maven + Docker Support)

    |

    v

Docker Engine

    |

    v

Test Runner Container

    |

    v

Rest Assured + TestNG Framework

    |

    v

API Under Test

    |

    v

Test Results

    |

    v

Allure Reports
```

---

## Docker Compose Architecture

When running the framework through Docker Compose, the containers are orchestrated together.

```text id="p9k7yw"
Developer

        |

        v

Docker Compose

        |

        +---------------------+

        |                     |

        v                     v

Jenkins Container       Test Runner Container

        |                     |

        |                     |

        |                     v

        |              Maven Test Execution

        |                     |

        |                     v

        |              Rest Assured Tests

        |                     |

        |                     v

        |              API Endpoints

        |

        v

Jenkins Pipeline Control
```

Docker Compose is responsible for creating and connecting the required containers.

The Jenkins container manages the CI/CD workflow.

The test runner container executes the automated tests.

More information:

[Project Architecture Guide](docs/project-architecture.md)

---

# Project Structure

The repository is organised to separate application code, infrastructure configuration and documentation.

Example structure:

```text id="v8q2xm"
api-test-automation-reference/

├── .devcontainer/
│   └── devcontainer.json
│
├── docs/
│   ├── getting-started.md
│   ├── project-architecture.md
│   ├── devcontainer.md
│   ├── docker.md
│   ├── docker-compose.md
│   ├── dockerfile.md
│   ├── dockerfile-jenkins.md
│   ├── jenkins-setup.md
│   ├── jenkins-pipeline.md
│   ├── test-execution.md
│   ├── environment-configuration.md
│   ├── parallel-execution.md
│   ├── promotion-pipeline.md
│   ├── allure-reporting.md
│   ├── shell-scripts.md
│   └── troubleshooting.md
│
├── docker/
│   └── jenkins/
│       └── Dockerfile.jenkins
│
├── jenkins-tools/
│
├── src/
│
├── Dockerfile
├── docker-compose.yml
├── Jenkinsfile
├── pom.xml
├── testng.xml
├── testng-parallel.xml
└── README.md
```

---

# Documentation

The framework includes detailed documentation explaining each major component.

The README provides the overview.

The individual guides provide the implementation details.

## Documentation Index

| Guide                                                          | Description                                                           |
| -------------------------------------------------------------- | --------------------------------------------------------------------- |
| [Getting Started](docs/getting-started.md)                     | Clone the repository, configure the environment and run the framework |
| [Project Architecture](docs/project-architecture.md)           | Overall framework design and component relationships                  |
| [Dev Container](docs/devcontainer.md)                          | GitHub Codespaces, VS Code Dev Containers and development setup       |
| [Docker](docs/docker.md)                                       | Docker concepts used by the framework                                 |
| [Docker Compose](docs/docker-compose.md)                       | Container orchestration and service communication                     |
| [Dockerfile](docs/dockerfile.md)                               | API test runner image creation                                        |
| [Jenkins Dockerfile](docs/dockerfile-jenkins.md)               | Custom Jenkins image configuration                                    |
| [Jenkins Setup](docs/jenkins-setup.md)                         | One-time Jenkins installation and configuration                       |
| [Jenkins Pipeline](docs/jenkins-pipeline.md)                   | Pipeline stages and execution workflow                                |
| [Test Execution](docs/test-execution.md)                       | Running tests locally, in Docker and Jenkins                          |
| [Environment Configuration](docs/environment-configuration.md) | Environment variables and configuration management                    |
| [Parallel Execution](docs/parallel-execution.md)               | Parallel execution, thread safety and test independence               |
| [Promotion Pipeline](docs/promotion-pipeline.md)               | Environment promotion and production approval workflow                |
| [Allure Reporting](docs/allure-reporting.md)                   | Test reporting and result analysis                                    |
| [Shell Scripts](docs/shell-scripts.md)                         | Helper scripts used for Jenkins and Docker operations                 |
| [Troubleshooting](docs/troubleshooting.md)                     | Common problems and diagnostic steps                                  |

---

# Recommended Learning Path

For engineers learning the framework, the recommended reading order is:

```text id="q2r6lw"
1. Getting Started

        |

2. Project Architecture

        |

3. Dev Container

        |

4. Docker

        |

5. Docker Compose

        |

6. Dockerfile

        |

7. Jenkins Dockerfile

        |

8. Jenkins Setup

        |

9. Jenkins Pipeline

        |

10. Test Execution

        |

11. Environment Configuration

        |

12. Parallel Execution

        |

13. Promotion Pipeline

        |

14. Allure Reporting

        |

15. Troubleshooting
```

This order introduces the framework from development environment through to full CI/CD execution.

---
# Reporting

The framework integrates with **Allure Reporting** to provide detailed test execution visibility.

Allure reports provide information including:

* Test execution summary
* Passed and failed tests
* Test execution duration
* Historical trends
* Test categorisation
* Attachments
* Request and response details (when enabled)

The reporting workflow is:

```text id="3v7z7s"
Test Execution

      |

      v

Test Results Generated

      |

      v

Allure Results Directory

      |

      v

Allure Report Generation

      |

      v

Jenkins Report Publishing
```

More information:

[Allure Reporting Guide](docs/allure-reporting.md)

---

# Environment Configuration

The framework supports execution against multiple environments:

```text id="w6u0ye"
DEV

 |

 v

QA

 |

 v

STAGING

 |

 v

PROD
```

Environment-specific values are externalised through configuration and environment variables.

This allows the same automation codebase to execute against different environments without modification.

Examples of configurable values include:

* API base URLs
* Execution environment
* Test suite selection
* Logging options
* Timeout settings

More information:

[Environment Configuration Guide](docs/environment-configuration.md)

---

# Parallel Test Execution

The framework supports both sequential and parallel execution using TestNG.

Parallel execution is designed to improve execution speed while maintaining reliability.

The framework supports:

* parallel test execution
* thread-safe test execution
* isolated test data strategies
* configurable TestNG execution models

It is important to distinguish between:

* framework thread safety
* test independence

A thread-safe framework allows parallel execution.

However, tests should also be logically independent if they are to run concurrently.

More information:

[Parallel Execution Guide](docs/parallel-execution.md)

---

# Shell Utilities

The repository includes helper scripts in the `jenkins-tools` directory.

These scripts simplify common operational tasks such as:

* starting Jenkins
* stopping Jenkins
* viewing logs
* checking container status
* retrieving Jenkins setup information

Example:

```bash id="7j8ncz"
./jenkins-tools/01-start-jenkins.sh
```

The scripts provide a simpler interface over Docker and Docker Compose commands.

More information:

[Shell Scripts Guide](docs/shell-scripts.md)

---

# Troubleshooting

The framework combines multiple technologies:

* Java
* Maven
* Docker
* Docker Compose
* Jenkins
* TestNG
* Allure

Problems can occur at different layers.

The troubleshooting guide provides solutions for common issues including:

* Maven failures
* Docker problems
* Jenkins configuration issues
* missing test reports
* environment configuration problems
* container communication issues

More information:

[Troubleshooting Guide](docs/troubleshooting.md)

---

# Contributing

Suggestions, improvements and enhancements are welcome.

The purpose of this repository is to maintain a practical reference implementation demonstrating modern API automation engineering practices.

Potential contributions include:

* additional API examples
* framework improvements
* documentation enhancements
* CI/CD improvements
* additional reporting capabilities
* new automation patterns

Before contributing, ensure changes:

* follow the existing project structure
* include appropriate documentation updates
* maintain compatibility with the supported tool versions

---

# License

This project is provided as a reference implementation for learning and building API automation frameworks.

It demonstrates engineering practices and architectural patterns that can be adapted for commercial automation solutions.

Review the repository license before using this framework in commercial projects.

---

# Summary

The **API Test Automation Reference Framework** demonstrates how a modern API automation solution can be designed, developed and integrated into a CI/CD workflow.

The framework provides:

* a reusable automation foundation
* a consistent development environment
* containerised execution
* Jenkins integration
* environment promotion workflow
* automated reporting
* documentation explaining the design decisions

The repository can be used as:

* a starting point for a new API automation project
* a reference implementation for engineering teams
* a learning resource for modern test automation practices

Start with:

[Getting Started Guide](docs/getting-started.md)

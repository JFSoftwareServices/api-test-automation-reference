# Project Architecture Guide

## Overview

The API Test Automation Reference Framework is an enterprise-style API automation framework demonstrating how automated tests can be:

* developed
* executed locally
* executed inside containers
* integrated into a CI/CD pipeline
* promoted through multiple environments

The framework combines:

* Java
* RestAssured
* TestNG
* Maven
* Docker
* Docker Compose
* Jenkins
* Allure reporting

The repository is designed to serve two purposes:

1. **Learning reference**

   Developers can explore the framework to understand modern test automation architecture and CI/CD practices.

2. **Project starting point**

   Teams can clone the repository and use it as a foundation for creating a new API automation project.

---

# High-Level Architecture

At a high level, the framework supports multiple ways of working.

```text
                         Developer

                             |
                             |
                             v

                    GitHub Repository

                             |
              +--------------+--------------+
              |                             |
              v                             v

       Dev Container                 Local IDE
       (Codespaces / VS Code)        (IntelliJ etc.)

              |                             |
              |                             |
              +--------------+--------------+
                             |
                             v

                           Maven

                             |
                             |
                             v

                    API Automation Framework

                             |
                             |
                             v

                    Docker Execution Layer

                             |
                             |
                             v

                     Jenkins CI/CD Pipeline

                             |
                             |
                             v

                  Environment Promotion Flow

                  DEV → QA → STAGING → PROD

                             |
                             |
                             v

                       Allure Reports
```

---

# Development Options

The framework supports different development approaches.

## GitHub Codespaces

The repository includes a Dev Container definition.

Location:

```text
.devcontainer/devcontainer.json
```

The Dev Container provides a consistent development environment including:

* Java 21
* Maven 3.9.16
* Git
* Docker CLI support
* VS Code extensions
* Project-specific settings

Developers opening the repository in GitHub Codespaces can use the same development environment used when building the framework.

---

## Local Development

Developers can also clone the repository and use a local IDE such as IntelliJ IDEA.

The local environment must provide:

* Java 21
* Maven 3.9.16
* Git
* Docker (if running containerised execution)

The framework does not require a specific IDE.

The IDE is only a development tool. Maven remains responsible for building and executing the tests.

---

# Repository Structure

```text
api-test-automation-reference

│
├── src
│   └── test
│       ├── java
│       │   ├── API clients
│       │   ├── Test classes
│       │   ├── Assertions
│       │   └── Utilities
│       │
│       └── resources
│           ├── TestNG suites
│           └── Configuration
│
├── docker
│   └── Jenkins configuration
│
├── jenkins-tools
│   └── Jenkins helper scripts
│
├── docs
│   └── Framework documentation
│
├── .devcontainer
│   └── Development environment definition
│
├── Dockerfile
│
├── docker-compose.yml
│
├── Jenkinsfile
│
└── pom.xml
```

---

# Core Components

## Automation Framework

Location:

```text
src/test/java
```

Contains reusable automation components:

* API clients
* request builders
* response validation
* assertions
* utilities

The framework separates reusable functionality from individual tests.

Example:

```text
API Client

    |

    |

Test Class

    |

    |

Assertions
```

This allows new tests to be added without duplicating framework code.

---

# Maven

Location:

```text
pom.xml
```

Maven manages:

* dependencies
* compilation
* test execution
* plugins

Tests can be executed using:

```bash
mvn test
```

Maven execution is the same whether running:

* locally
* inside Docker
* through Jenkins

---

# Test Execution Architecture

The framework supports sequential and parallel execution.

## Sequential Execution

Uses:

```text
testng.xml
```

Execution:

```text
Test 1

 |

Test 2

 |

Test 3
```

Useful for:

* debugging
* simple validation

---

## Parallel Execution

Uses:

```text
testng-parallel.xml
```

Execution:

```text
Thread 1 → Test A

Thread 2 → Test B

Thread 3 → Test C
```

Benefits:

* faster execution
* better CI utilisation
* reduced pipeline duration

---

# Docker Architecture

Docker provides consistent execution environments.

The basic Docker execution model is:

```text
                    Developer

                        |
                        |
                        v

                 Docker Compose

                        |
        +---------------+---------------+
        |                               |
        v                               v

 Jenkins Container              Test Runner Container

        |                               |
        |                               |
        |                              Maven

        |                               |
        |                              TestNG

        |                               |
        |                         RestAssured

        |                               |
        |                         API Endpoints
```

Docker Compose is responsible for creating and configuring containers.

It does not execute the tests itself.

The containers execute the processes defined by their images.

---

# Jenkins and Docker Relationship

This project uses Jenkins running inside Docker.

The Jenkins container has access to the host Docker engine through:

```yaml
- /var/run/docker.sock:/var/run/docker.sock
```

This allows Jenkins to create and manage test containers.

The execution flow is:

```text
Developer

   |

   |

Jenkins Pipeline Starts

   |

   |

Jenkins Container

   |

   |

Docker Socket

   |

   |

Docker Engine

   |

   |

Docker Compose

   |

   |

Test Runner Container

   |

   |

Maven

   |

   |

TestNG

   |

   |

API Tests
```

The important relationship is:

* Jenkins controls the pipeline
* Docker executes the workloads
* Test containers execute the automation tests

Jenkins itself does not run the Maven tests directly.

---

# CI/CD Pipeline Flow

The Jenkins pipeline follows this flow:

```text
Git Push

   |

   |

Jenkins Pipeline

   |

   |

Build Test Images

   |

   |

Run DEV Tests

   |

   |

Run QA Tests

   |

   |

Run STAGING Tests

   |

   |

Production Approval Gate

   |

   |

Run PROD Tests

   |

   |

Generate Allure Report
```

---

# Environment Promotion

The framework demonstrates a controlled promotion process:

```text
          DEV

           |

           v

          QA

           |

           v

       STAGING

           |

           v

 Production Approval

           |

           v

          PROD
```

Production execution requires explicit approval.

The user can:

* approve promotion
* reject promotion

This represents a common enterprise release control mechanism.

---

# Reporting Architecture

Test execution produces Allure results.

Flow:

```text
Test Execution

      |

      |

target/allure-results

      |

      |

Jenkins Allure Plugin

      |

      |

HTML Report
```

Reports provide:

* execution summary
* passed/failed tests
* failures
* attachments
* diagnostic information

---

# Design Principles

## Configuration Outside Code

Environment-specific values are supplied externally.

Example:

```text
BASE_URL_JSON

BASE_URL_XML

ENV

LOG_REQUESTS

LOG_RESPONSES
```

The same test code can run against multiple environments.

---

## Containerised Execution

The same automation framework can execute:

* locally
* in GitHub Codespaces
* in Docker
* in Jenkins

without changing the test implementation.

---

## Reusable Architecture

The framework encourages:

* reusable components
* maintainable tests
* separation of configuration and code
* CI/CD integration

---

# Related Documentation

Further documentation:

```text
docs/getting-started.md

docs/devcontainer.md

docs/docker.md

docs/docker-compose.md

docs/dockerfile.md

docs/jenkins-setup.md

docs/jenkins-pipeline.md

docs/allure-reporting.md

docs/test-execution.md
```
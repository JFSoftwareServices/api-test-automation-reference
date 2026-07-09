# Test Execution Guide

## Overview

This document explains the different ways to execute tests in the API Test Automation Reference framework.

The framework supports running tests through:

* Maven directly
* Docker containers
* GitHub Codespaces
* Local development environments such as IntelliJ IDEA
* Jenkins CI/CD pipelines

The same test code is used across all execution methods.

The execution environment changes, but the automation framework remains the same.

---

# Execution Options

The framework supports the following execution models:

```text
Developer Machine

    |

    +----------------+
    |                |
    v                v

Maven             Docker

                    |

                    v

              Test Container


Jenkins

    |

    v

Docker

    |

    v

Test Container
```

---

# Option 1: GitHub Codespaces

## Overview

GitHub Codespaces provides a cloud development environment configured using:

```text
.devcontainer/devcontainer.json
```

The Dev Container provides:

* Java 21
* Maven 3.9.16
* Git
* Docker support
* VS Code extensions
* Project-specific settings

When a developer opens the repository in Codespaces, the environment is created automatically.

---

## Start Codespace

1. Open the GitHub repository

2. Select:

```text
Code

↓

Codespaces

↓

Create codespace on main
```

3. Wait for the Dev Container to build.

Verify:

```bash
java -version
```

Expected:

```text
Java 21
```

Verify Maven:

```bash
mvn -version
```

Expected:

```text
Apache Maven 3.9.16
```

Verify Docker:

```bash
docker --version
```

---

# Running Tests With Maven

Tests can be executed directly using Maven.

Example:

```bash
mvn test
```

Maven will:

1. Read `pom.xml`
2. Download dependencies
3. Compile source code
4. Execute TestNG tests
5. Generate reports

---

# Running a Specific Test Suite

The framework uses TestNG suites.

Example:

```bash
mvn test \
-DsuiteXmlFile=testng.xml
```

The suite controls:

* which tests execute
* parallel configuration
* execution order

---

# Running Tests With Environment Variables

Configuration is externalised using environment variables.

Example:

```bash
export ENV=qa

export BASE_URL_JSON=https://example.com/api

export SUITE=testng.xml
```

Execute:

```bash
mvn test \
-DsuiteXmlFile=$SUITE \
-Denv=$ENV \
-Dbase.url.json=$BASE_URL_JSON
```

Benefits:

* no code changes between environments
* easier CI/CD integration
* safer configuration management

---

# Option 2: Running Tests With Docker

## Overview

The test framework can execute inside a Docker container.

The Docker container provides:

* Java runtime
* Maven
* dependencies
* test execution environment

Architecture:

```text
Docker Image

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

---

# Build Test Image

From the project root:

```bash
docker compose build tests-dev
```

This creates the test execution image.

---

# Run Development Tests

Example:

```bash
docker compose run --rm tests-dev
```

Execution:

```text
Docker Compose

       |

       |

tests-dev container

       |

       |

mvn test
```

After execution:

* container is removed
* images remain

---

# Why Use Docker For Tests?

Running tests in Docker provides:

* consistent runtime
* identical environment across machines
* reduced "works on my machine" issues
* easier CI/CD execution

---

# Docker Compose Test Services

The project defines separate services:

```text
tests-dev

tests-qa

tests-staging

tests-prod
```

Each service uses the same test framework.

The difference is configuration.

Example:

```text
Same Code

+

Different Environment

=

Different Execution Target
```

---

# Running QA Tests

Example:

```bash
docker compose run --rm tests-qa
```

The container executes:

```text
Maven

↓

TestNG

↓

REST Assured Tests

↓

QA API Environment
```

---

# Running Staging Tests

Example:

```bash
docker compose run --rm tests-staging
```

---

# Running Production Tests

Example:

```bash
docker compose run --rm tests-prod
```

Production execution should normally be controlled through Jenkins approval gates.

---

# Option 3: IntelliJ IDEA

## Overview

Developers who prefer IntelliJ IDEA can use the same repository.

The project does not require GitHub Codespaces.

The local machine must provide:

* Java 21
* Maven 3.9.16
* Git
* Docker (if running container tests)

---

# Open Project In IntelliJ

1. Clone repository:

```bash
git clone <repository-url>
```

2. Open:

```text
pom.xml
```

3. Allow IntelliJ to import Maven dependencies.

4. Run tests using:

```text
Maven

↓

Lifecycle

↓

test
```

or from the terminal:

```bash
mvn test
```

---

# Relationship Between Codespaces and IntelliJ

The Dev Container is primarily designed for:

* GitHub Codespaces
* VS Code Dev Container environments

It does not automatically configure IntelliJ.

However, IntelliJ developers can still use the project.

They need equivalent local tools:

```text
Dev Container

Java 21

Maven 3.9.16

Git

Docker


=

Local IntelliJ Environment

Java 21

Maven 3.9.16

Git

Docker
```

The project remains the same.

---

# Parallel Test Execution

The framework supports TestNG parallel execution.

Example:

```xml
<suite
    parallel="methods"
    thread-count="8">
</suite>
```

Execution:

```text
Thread 1 --> Test A

Thread 2 --> Test B

Thread 3 --> Test C

Thread 4 --> Test D
```

Benefits:

* faster execution
* better CI feedback
* improved resource usage

---

# Sequential Test Execution

Some tests should execute in order.

Example:

```text
Create Customer

      |

Update Customer

      |

Delete Customer
```

Sequential execution is useful for:

* workflow testing
* dependent scenarios
* end-to-end validation

---

# Test Reports

After execution:

```text
target/

    |

    +-- surefire-reports

    |

    +-- allure-results
```

---

## Surefire Reports

Generated by Maven Surefire.

Contains:

* test execution results
* failures
* errors

---

## Allure Results

Generated during test execution.

Contains:

```text
*-result.json

*-container.json

attachments
```

These files are consumed by Allure.

---

# Troubleshooting

## Maven Cannot Find Dependencies

Run:

```bash
mvn dependency:resolve
```

---

## Docker Container Cannot See Results

Check:

```bash
ls -la target
```

Expected:

```text
allure-results

surefire-reports
```

---

## Java Version Incorrect

Check:

```bash
java -version
```

The framework requires:

```text
Java 21
```

---

## Maven Version Incorrect

Check:

```bash
mvn -version
```

Recommended:

```text
Maven 3.9.16
```

---

# Recommended Development Workflow

A typical developer workflow:

```text
Clone Repository

        |

        |

Open Codespace / IntelliJ

        |

        |

Run Tests Locally

        |

        |

Commit Changes

        |

        |

Push To GitHub

        |

        |

Jenkins Executes Pipeline

        |

        |

Reports Generated
```

---

# Summary

The framework supports multiple execution environments while maintaining one automation codebase.

The recommended usage:

| User               | Recommended Approach |
| ------------------ | -------------------- |
| New contributor    | GitHub Codespaces    |
| VS Code user       | Dev Container        |
| IntelliJ developer | Local Java + Maven   |
| CI/CD execution    | Jenkins + Docker     |
| Quick validation   | Maven test           |

The goal is to provide a consistent API automation framework that can be developed locally, executed in containers, and promoted through enterprise CI/CD pipelines.

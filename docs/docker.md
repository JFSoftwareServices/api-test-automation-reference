# Docker Guide

## Overview

This framework uses Docker to provide consistent and isolated test execution environments.

Docker ensures that the same automation tests can run consistently across:

* Developer machines
* GitHub Codespaces
* CI/CD environments
* Jenkins pipelines

Instead of depending on each machine having identical Java, Maven, and dependency versions, Docker packages the required runtime environment.

---

# Why Docker Is Used

Without Docker, test execution depends on the developer environment.

Example:

```text
Developer A

Java 21
Maven 3.9.16
Correct dependencies

✓ Tests pass


Developer B

Java 17
Different Maven version
Missing tools

✗ Tests fail
```

Docker removes these differences.

The execution becomes:

```text
Docker Image

+

Test Code

+

Configuration

=

Repeatable Test Execution
```

---

# Docker Concepts Used in This Project

This framework uses the following Docker concepts:

| Concept               | Purpose                                                  |
| --------------------- | -------------------------------------------------------- |
| Image                 | Template containing Java, Maven and project requirements |
| Container             | Running instance of an image                             |
| Dockerfile            | Instructions to build an image                           |
| Docker Compose        | Defines and manages multiple containers                  |
| Volume                | Persists or shares data                                  |
| Network               | Allows containers to communicate                         |
| Environment Variables | Provides runtime configuration                           |

---

# Docker Architecture

The framework Docker architecture is:

```text
                 Developer

                    |
                    |

              Docker Compose

                    |
        +-----------+-----------+
        |                       |
        v                       v

 Jenkins Container       Test Runner Container

        |                       |
        |                       |
        +-----------+-----------+

                    |

               Test Execution

                    |

                 Maven

                    |

                TestNG

                    |

              API Endpoints
```

---

# Docker Image

An image is a packaged environment used to create containers.

This project creates a test execution image from:

```text
Dockerfile
```

The image contains:

* Java runtime
* Maven
* Project source code
* Dependencies
* Test execution capability

A single image can create multiple containers.

Example:

```text
Test Image

       |
       |
       +---- tests-dev container

       |
       |
       +---- tests-qa container

       |
       |
       +---- tests-staging container
```

---

# Dockerfile

Location:

```text
Dockerfile
```

The Dockerfile defines how the test image is created.

Example structure:

```dockerfile
FROM eclipse-temurin:21

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:resolve

COPY . .

CMD ["mvn","test"]
```

---

# WORKDIR

The framework uses:

```dockerfile
WORKDIR /app
```

This defines the working directory inside the container.

Inside the container:

```text
/app

├── pom.xml
├── src
└── target
```

When commands run:

```bash
mvn test
```

they execute from:

```text
/app
```

---

# Build Context

Docker builds images from a context.

Example:

```yaml
build:
  context: .
  dockerfile: Dockerfile
```

Meaning:

```text
Current project directory

        |

        |

Docker can access these files

        |

        |

Dockerfile
pom.xml
src/
```

---

# Docker Containers

A container is a running instance of an image.

Example:

```text
Image:

api-test-framework-image


creates


Container:

tests-dev
```

Containers are temporary.

This project uses:

```bash
docker compose run --rm
```

The `--rm` option removes the container after execution.

Example:

```bash
docker compose run --rm tests-qa
```

Flow:

```text
Create container

        |

Run tests

        |

Generate results

        |

Remove container
```

The test results must therefore be stored outside the container using volumes.

---

# Volumes

Containers are temporary.

If results are created inside a container:

```text
Container

/app/target/allure-results
```

they disappear when the container is removed.

Volumes allow data to be shared.

Example:

```yaml
volumes:
  - ${WORKSPACE}/target:/app/target
```

This maps:

```text
Jenkins Workspace

/var/jenkins_home/workspace/project/target


        ↓


Container

/app/target
```

The test container writes:

```text
/app/target/allure-results
```

Jenkins can then access:

```text
workspace/target/allure-results
```

---

# Environment Variables

The framework avoids hardcoding environment details.

Example:

```yaml
environment:

  ENV: ${ENV}

  BASE_URL_JSON: ${BASE_URL_JSON}

  LOG_REQUESTS: ${LOG_REQUESTS}
```

Values are supplied when the container starts.

Example:

```bash
ENV=qa

BASE_URL_JSON=https://qa.api.com
```

The same container can then run against different environments.

---

# Docker Networks

Containers communicate through Docker networks.

The framework creates:

```text
framework-net
```

Example:

```yaml
networks:

  framework-net:
```

Containers attached to the same network can communicate.

Example:

```text
Jenkins Container

        |

framework-net

        |

Test Container
```

---

# Docker Compose Profiles

The framework uses profiles to select environments.

Example:

```yaml
profiles:
  - qa
```

This allows specific test containers to run.

Example:

Run QA:

```bash
docker compose \
--profile qa \
run --rm tests-qa
```

Run DEV:

```bash
docker compose \
--profile dev \
run --rm tests-dev
```

---

# Local Docker Execution

Tests can be executed without Jenkins.

Example:

```bash
docker compose \
--profile dev \
run --rm tests-dev
```

Execution flow:

```text
Developer

 |

docker compose

 |

tests-dev container

 |

Maven

 |

TestNG

 |

API Tests
```

---

# Docker Execution Inside GitHub Codespaces

GitHub Codespaces provides a development environment using:

```text
.devcontainer/devcontainer.json
```

The Dev Container provides:

* Java
* Maven
* Git
* Docker CLI

Docker commands run from inside the Codespace.

Architecture:

```text
GitHub Codespace

        |

Dev Container

        |

Docker Engine

        |

Test Containers
```

---

# Docker Execution Inside Jenkins

Jenkins also uses Docker to execute tests.

Architecture:

```text
Jenkins Container

        |

Docker Socket

        |

Test Containers

        |

Maven Test Execution
```

The Jenkins container does not run the tests directly.

Instead:

1. Jenkins starts the pipeline
2. Jenkins calls Docker Compose
3. Docker creates test containers
4. Tests execute
5. Results are returned to Jenkins

---

# Useful Docker Commands

## List Running Containers

```bash
docker ps
```

---

## List All Containers

```bash
docker ps -a
```

---

## View Container Logs

```bash
docker logs <container>
```

---

## List Images

```bash
docker images
```

---

## Build Images

```bash
docker compose build
```

---

## Start Jenkins

```bash
docker compose up -d jenkins
```

---

## Stop Containers

```bash
docker compose down
```

---

## Remove Containers and Volumes

Use carefully:

```bash
docker compose down -v
```

This removes persistent volumes.

---

# Troubleshooting

## Problem: Allure Results Missing

Check:

Inside container:

```bash
ls -la /app/target/allure-results
```

On Jenkins:

```bash
ls -la $WORKSPACE/target/allure-results
```

The directories should contain:

```text
*-result.json

*-container.json

attachments
```

---

## Problem: Maven Dependencies Download Every Time

Ensure the Maven cache volume exists:

```yaml
volumes:

  - maven-cache:/root/.m2
```

---

## Problem: Container Cannot Access Docker

Check:

```bash
docker ps
```

inside the Jenkins container.

The Jenkins container requires:

```yaml
- /var/run/docker.sock:/var/run/docker.sock
```

---

# Summary

Docker provides the execution foundation for this framework.

The key idea is:

```text
Same Code

+

Same Docker Image

+

Different Configuration

=

Consistent Test Execution
```

Docker allows the framework to move from:

```text
Developer Laptop

        ↓

GitHub Codespaces

        ↓

Jenkins CI/CD

        ↓

Enterprise Pipeline
```

without changing the automation code.

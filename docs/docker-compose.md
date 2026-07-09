# Docker Compose Guide

## Overview

This framework uses Docker Compose to define and orchestrate the services required for local and CI/CD execution.

Docker Compose provides a single configuration file that describes:

* Jenkins CI server
* Test execution containers
* Networks
* Persistent storage
* Environment configuration
* Container relationships

The Docker Compose configuration is located at:

```text
docker-compose.yml
```

---

# Why Docker Compose Is Used

Without Docker Compose, each container would need to be created manually.

Example:

```bash
docker run ...
docker network create ...
docker volume create ...
```

Docker Compose replaces this with a single declarative configuration.

Example:

```bash
docker compose up
```

Docker Compose then:

1. Creates required networks
2. Creates required volumes
3. Builds images
4. Starts containers
5. Connects services together

---

# Architecture Overview

The Compose architecture for this framework is:

```text
                         Developer

                             |
                             |
                             v

                    docker-compose.yml

                             |
              +--------------+--------------+
              |                             |
              v                             v

       Jenkins Container             Test Runner Containers


                                             |
                                             |
                         +-------------------+-------------------+
                         |                   |                   |
                         v                   v                   v

                    tests-dev            tests-qa          tests-staging
                         |
                         |
                         v

                    Maven + TestNG

                         |
                         |
                         v

                    API Endpoints
```

---

# Compose File Structure

The file contains:

```yaml
name:
networks:
volumes:
services:
```

Example:

```yaml
name: api-test-promotion-pipeline

networks:
  framework-net:

volumes:
  maven-cache:

services:
  jenkins:
  tests-dev:
  tests-qa:
  tests-staging:
  tests-prod:
```

---

# Project Name

The Compose project name is:

```yaml
name: api-test-promotion-pipeline
```

This controls the names Docker assigns to resources.

Example:

```text
api-test-promotion-pipeline_jenkins
```

Volumes are also created using this prefix.

Example:

```text
api-test-promotion-pipeline_maven-cache
```

---

# Networks

The framework creates a dedicated Docker network:

```yaml
networks:

  framework-net:
    driver: bridge
```

The network allows containers belonging to this project to communicate.

Example:

```text
Jenkins Container

        |

framework-net

        |

Test Runner Container
```

The bridge driver creates an isolated Docker network.

---

# Volumes

## Maven Cache Volume

The framework uses:

```yaml
volumes:

  maven-cache:
```

This stores Maven dependencies.

Mounted into containers:

```yaml
- maven-cache:/root/.m2
```

Purpose:

Without this volume:

```text
Every execution

      |

Download Maven dependencies

      |

Longer execution time
```

With the cache:

```text
First execution

      |

Download dependencies


Future executions

      |

Reuse cached dependencies
```

---

# Jenkins Service

The Jenkins service runs the CI/CD controller.

Example:

```yaml
services:

  jenkins:
```

Responsibilities:

* Run Jenkins server
* Execute pipeline jobs
* Trigger Docker-based test execution
* Generate reports

---

# Jenkins Image Build

Jenkins is built from:

```yaml
build:

  context: ./docker/jenkins

  dockerfile: Dockerfile.jenkins
```

The custom Jenkins image includes required tools and plugins.

The Dockerfile is located:

```text
docker/jenkins/Dockerfile.jenkins
```

---

# Jenkins Volumes

Jenkins uses:

```yaml
volumes:

- /var/jenkins_home:/var/jenkins_home

- /var/run/docker.sock:/var/run/docker.sock

- maven-cache:/root/.m2
```

## Jenkins Home

```text
/var/jenkins_home
```

Stores:

* Jenkins configuration
* Jobs
* Plugins
* Credentials
* Build history

---

## Docker Socket

```text
/var/run/docker.sock
```

This is a critical part of the architecture.

It allows Jenkins inside the container to communicate with Docker.

Flow:

```text
Jenkins Container

        |

        |

Docker Socket

        |

        |

Docker Engine

        |

        |

Test Containers
```

Without this mount Jenkins cannot create the test execution containers.

---

# Test Runner Services

The framework defines separate test services:

```text
tests-dev

tests-qa

tests-staging

tests-prod
```

Each service uses the same Docker image but receives different configuration.

Example:

```text
Same Image

      +

Different Environment Variables

      =

Different Execution Environment
```

---

# Test Runner Image

Each test service uses:

```yaml
build:

  context: .

  dockerfile: Dockerfile

  target: test-runner
```

This creates the automation execution image.

The image contains:

* Java
* Maven
* Source code
* Dependencies

---

# Docker Build Target

The Dockerfile contains build stages.

Example:

```yaml
target: test-runner
```

means:

Build only the stage called:

```text
test-runner
```

This allows different images to be created from the same Dockerfile.

---

# Test Environment Configuration

Each test container receives configuration:

Example:

```yaml
environment:

  ENV: ${ENV}

  BASE_URL_JSON: ${BASE_URL_JSON}

  BASE_URL_XML: ${BASE_URL_XML}

  LOG_REQUESTS: ${LOG_REQUESTS}

  LOG_RESPONSES: ${LOG_RESPONSES}
```

The test code does not contain environment-specific URLs.

Instead:

```text
Jenkins

 |

Environment Variables

 |

Docker Container

 |

Maven Test Execution
```

---

# Target Volume Mount

The test containers mount:

```yaml
- ${WORKSPACE}/target:/app/target
```

This is required because containers are temporary.

The test container writes:

```text
/app/target/allure-results
```

The mount makes the results available outside the container:

```text
Jenkins Workspace

/target/allure-results
```

This allows Jenkins to publish Allure reports.

---

# Docker Compose Profiles

Profiles control which test containers are started.

Example:

```yaml
profiles:

- dev
```

The framework has:

```text
dev

qa

staging

prod
```

---

# Running Tests Locally

## DEV

```bash
docker compose \
--profile dev \
run --rm tests-dev
```

Flow:

```text
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

## QA

```bash
docker compose \
--profile qa \
run --rm tests-qa
```

---

## STAGING

```bash
docker compose \
--profile staging \
run --rm tests-staging
```

---

# Starting Jenkins

Start Jenkins only:

```bash
docker compose up -d jenkins
```

Check status:

```bash
docker compose ps
```

View logs:

```bash
docker compose logs -f jenkins
```

---

# Jenkins Triggered Execution

When Jenkins runs the pipeline:

1. Jenkins receives the build request
2. Jenkins executes the Jenkinsfile
3. Jenkins calls Docker Compose
4. Docker Compose creates the test container
5. Maven executes tests
6. Results are written to the workspace
7. Jenkins publishes reports

Architecture:

```text
Jenkins Pipeline

        |

        |

docker compose run

        |

        |

Test Runner Container

        |

        |

Maven Test Execution

        |

        |

Allure Results
```

---

# Useful Commands

## View Running Services

```bash
docker compose ps
```

---

## Build Images

```bash
docker compose build
```

---

## Start Services

```bash
docker compose up
```

---

## Start in Background

```bash
docker compose up -d
```

---

## Stop Services

```bash
docker compose down
```

---

## Remove Containers and Volumes

Use carefully:

```bash
docker compose down -v
```

This removes:

* containers
* networks
* volumes

Persistent Jenkins data may be lost depending on volume configuration.

---

# Troubleshooting

## Check Container Logs

Example:

```bash
docker compose logs tests-dev
```

---

## Check Environment Variables

Inside a container:

```bash
env
```

---

## Check Allure Results

Inside test container:

```bash
ls -la /app/target/allure-results
```

From Jenkins workspace:

```bash
ls -la target/allure-results
```

Expected files:

```text
*-result.json

*-container.json

attachments/
```

---

# Summary

Docker Compose provides the orchestration layer for this framework.

The key responsibilities are:

| Component             | Responsibility                |
| --------------------- | ----------------------------- |
| Jenkins Service       | CI/CD controller              |
| Test Services         | Execute automation tests      |
| Volumes               | Persist and share data        |
| Networks              | Container communication       |
| Profiles              | Select execution environments |
| Environment Variables | Configure execution           |

The overall execution model is:

```text
docker-compose.yml

        |

        |

Creates Containers

        |

        |

Jenkins Controls Execution

        |

        |

Test Containers Run Maven Tests

        |

        |

Allure Reports Published
```

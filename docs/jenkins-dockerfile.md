# Jenkins Dockerfile Guide

## Overview

The Jenkins Dockerfile defines the custom Jenkins CI server image used by this framework.

The file is responsible for creating a Jenkins environment that contains:

* Jenkins server
* Required Jenkins plugins
* Java runtime
* Docker tooling
* Configuration required to execute Docker-based test pipelines

The Dockerfile is located at:

```text
docker/
 └── jenkins/
     └── Dockerfile.jenkins
```

---

# Why Use a Custom Jenkins Image?

A standard Jenkins image provides a working Jenkins installation, but additional configuration is required for this framework.

The custom image allows the project to define:

* Required Jenkins plugins
* Docker support
* Pipeline capabilities
* Reproducible Jenkins setup

Instead of manually configuring every Jenkins installation, the environment can be recreated from source control.

---

# Jenkins Architecture

The Jenkins container is responsible for orchestration.

It does not execute the API tests directly.

The architecture is:

```text
Developer

    |

    |

Jenkins Container

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

TestNG + REST Assured

    |

    |

API Endpoints
```

---

# Jenkins Dockerfile Responsibilities

The Jenkins Dockerfile is responsible for creating the Jenkins controller environment.

It provides:

| Component            | Purpose                   |
| -------------------- | ------------------------- |
| Jenkins              | CI/CD orchestration       |
| Plugins              | Pipeline functionality    |
| Docker CLI           | Ability to control Docker |
| Java                 | Jenkins runtime           |
| Plugin configuration | Consistent setup          |

---

# Base Image

The Dockerfile starts from the official Jenkins image.

Example:

```dockerfile
FROM jenkins/jenkins:lts
```

The LTS (Long Term Support) version is used because Jenkins is a critical CI/CD component.

LTS releases provide:

* stability
* longer support periods
* predictable upgrades

---

# Installing Docker Support

The Jenkins container needs Docker access because Jenkins launches test containers.

The architecture uses Docker socket mounting:

```yaml
volumes:

- /var/run/docker.sock:/var/run/docker.sock
```

This allows:

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

Jenkins does not contain a separate Docker daemon.

It communicates with the host Docker daemon.

---

# Jenkins Plugins

Plugins are installed during image creation.

The plugin list is maintained separately:

```text
docker/jenkins/plugins.txt
```

Example:

```text
workflow-aggregator
pipeline-stage-view
docker-workflow
junit
allure-jenkins-plugin
```

These provide capabilities such as:

* Jenkins pipelines
* Docker integration
* Test reporting
* Build visualisation
* Allure reporting

---

# Plugin Installation

The Jenkins Dockerfile installs plugins automatically.

Conceptually:

```text
plugins.txt

      |

      |

Jenkins Plugin Installer

      |

      |

Jenkins Image

      |

      |

Running Jenkins Container
```

This avoids manually installing plugins after every Jenkins rebuild.

---

# Jenkins Home Directory

Jenkins stores all configuration under:

```text
/var/jenkins_home
```

This contains:

```text
/var/jenkins_home

    ├── jobs
    ├── plugins
    ├── users
    ├── credentials
    └── configuration files
```

The Docker Compose configuration persists this using:

```yaml
volumes:

- /var/jenkins_home:/var/jenkins_home
```

This means:

* container can be recreated
* Jenkins data survives

---

# Docker Socket Access

The Jenkins container requires access to Docker.

The Docker Compose configuration mounts:

```yaml
- /var/run/docker.sock:/var/run/docker.sock
```

This enables commands such as:

```bash
docker compose run tests-qa
```

from inside Jenkins.

Execution flow:

```text
Jenkins Pipeline

        |

        |

docker compose command

        |

        |

Docker Engine

        |

        |

Test Runner Container
```

---

# Relationship With Docker Compose

The Jenkins Dockerfile creates the Jenkins image.

Docker Compose runs the container.

Responsibilities are separated:

## Dockerfile

Creates:

```text
Jenkins Image
```

Responsible for:

* software installation
* plugins
* image configuration

## Docker Compose

Creates:

```text
Jenkins Container
```

Responsible for:

* ports
* volumes
* networking
* environment variables

---

# Relationship With Test Dockerfile

This project contains two Docker images.

## Jenkins Image

Created from:

```text
docker/jenkins/Dockerfile.jenkins
```

Purpose:

```text
Run Jenkins
```

---

## Test Runner Image

Created from:

```text
Dockerfile
```

Purpose:

```text
Execute API automation tests
```

---

The relationship:

```text
                 Docker Compose

                       |

        +--------------+--------------+

        |                             |

        v                             v


 Jenkins Image                 Test Runner Image

        |                             |

 Jenkins Container            Test Containers

        |                             |

 Controls execution            Runs Maven tests
```

---

# Building the Jenkins Image

The image is built using:

```bash
docker compose build jenkins
```

or:

```bash
docker compose up -d --build jenkins
```

---

# Starting Jenkins

Start Jenkins:

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

# First-Time Setup

After starting Jenkins:

1. Open Jenkins:

```text
http://localhost:8080
```

2. Retrieve the initial administrator password:

```bash
docker exec <jenkins-container> \
cat /var/jenkins_home/secrets/initialAdminPassword
```

3. Complete Jenkins setup.

4. Configure:

* Allure Commandline tool
* Global tools
* Credentials
* Pipeline jobs

Detailed instructions are documented separately in:

```text
jenkins-setup.md
```

---

# Why This Approach?

Using a Dockerised Jenkins controller provides:

* repeatable CI/CD setup
* easier onboarding
* version-controlled configuration
* reduced manual setup
* consistent environments

A developer can clone the repository and recreate the CI environment without manually installing Jenkins.

---

# Summary

The Jenkins Dockerfile creates the CI/CD control environment.

Its purpose is not to run tests directly.

Its responsibility is:

```text
Create Jenkins Environment

        |

        |

Run Pipelines

        |

        |

Trigger Test Containers

        |

        |

Collect Results
```

The separation between Jenkins and test execution containers keeps the architecture clean, scalable and easier to maintain.

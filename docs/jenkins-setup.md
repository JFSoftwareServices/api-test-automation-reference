# Jenkins One-Time Setup Guide

## Overview

This document describes the one-time configuration required after starting Jenkins for the first time.

The Jenkins Docker image provides the base Jenkins environment, plugins, and Docker integration. However, some configuration is intentionally performed through the Jenkins UI because it depends on the specific installation.

After completing this setup, Jenkins is ready to execute the API test automation pipeline.

---

# Jenkins Setup Flow

The overall setup process is:

```text
Clone Repository

        |

Start Jenkins Container

        |

Initial Jenkins Configuration

        |

Install / Configure Tools

        |

Create Pipeline

        |

Execute Automated Tests
```

---

# Prerequisites

Before starting Jenkins, ensure:

* Docker is installed
* Docker Compose is available
* Repository has been cloned
* Jenkins Docker image can be built

Verify Docker:

```bash
docker --version
```

Verify Docker Compose:

```bash
docker compose version
```

---

# Start Jenkins

From the project root:

```bash
docker compose up -d --build jenkins
```

Check the container:

```bash
docker compose ps
```

Expected:

```text
jenkins   running
```

---

# Access Jenkins

Open:

```text
http://localhost:8080
```

The first startup may take several minutes while:

* Jenkins initialises
* Plugins are loaded
* Configuration is created

---

# Retrieve Initial Administrator Password

The password is stored inside the Jenkins home directory.

Run:

```bash
docker exec <jenkins-container-name> \
cat /var/jenkins_home/secrets/initialAdminPassword
```

Example:

```bash
docker exec api-test-promotion-pipeline-jenkins-1 \
cat /var/jenkins_home/secrets/initialAdminPassword
```

Enter this password into the Jenkins setup screen.

---

# Complete Jenkins Installation Wizard

During first startup:

1. Enter administrator password
2. Install suggested plugins (if required)
3. Create administrator account
4. Confirm Jenkins URL

The required plugins are already included in the custom Jenkins image.

---

# Verify Installed Plugins

Navigate:

```text
Manage Jenkins

        ↓

Plugins
```

Confirm the following plugins exist:

## Pipeline

Required for Jenkinsfile execution.

Examples:

```text
workflow-aggregator
pipeline-stage-view
```

---

## Docker Support

Required because Jenkins launches Docker containers.

Example:

```text
docker-workflow
```

---

## Test Reporting

Required for publishing test results.

Examples:

```text
junit
testng-plugin
allure-jenkins-plugin
htmlpublisher
```

---

# Configure Docker Access

The Jenkins container communicates with the Docker engine through:

```text
/var/run/docker.sock
```

The Docker Compose configuration provides this:

```yaml
volumes:

- /var/run/docker.sock:/var/run/docker.sock
```

Verify from inside Jenkins:

```bash
docker exec -it <jenkins-container> bash
```

Then:

```bash
docker ps
```

Expected:

Jenkins should be able to see Docker containers.

---

# Configure Allure Commandline

Allure reports require the Allure commandline tool.

Navigate:

```text
Manage Jenkins

        ↓

Tools

        ↓

Allure Commandline installations
```

Add:

```text
Name:

allure
```

Enable:

```text
Install automatically
```

Select:

```text
Install from Maven Central
```

Example version:

```text
2.43.0
```

Save.

---

# Verify Allure Configuration

Run a pipeline execution.

Successful configuration produces logs similar to:

```text
Using Allure CLI:

/var/jenkins_home/tools/.../allure/bin/allure

Allure report was successfully generated
```

---

# Configure Pipeline Job

Create a new Jenkins job.

Navigate:

```text
New Item
```

Select:

```text
Pipeline
```

Example name:

```text
api-test-promotion-pipeline
```

---

# Pipeline Configuration

Configure:

```text
Pipeline Definition:

Pipeline script from SCM
```

Select:

```text
Git
```

Provide repository URL.

Example:

```text
https://github.com/<organisation>/<repository>
```

Branch:

```text
main
```

Script path:

```text
Jenkinsfile
```

Save.

---

# Configure Credentials

If the repository is private, configure credentials.

Navigate:

```text
Manage Jenkins

        ↓

Credentials
```

Add:

* GitHub token
* SSH key
* Other required secrets

Do not store credentials inside:

* Jenkinsfile
* Dockerfiles
* source code

---

# Configure Build Parameters

The pipeline supports environment-driven execution.

Typical parameters include:

```text
ENV

SUITE

BASE_URL_JSON

BASE_URL_XML
```

These allow the same pipeline to execute against different environments.

Example:

```text
ENV=qa

SUITE=testng.xml
```

---

# Jenkins Pipeline Execution Model

Once configured, execution follows:

```text
Jenkins Pipeline

        |

        |

Checkout Source Code

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

Allure Results

        |

        |

Jenkins Report
```

---

# Quality Gates and Promotion

The pipeline supports promotion through environments.

Example flow:

```text
Development

      |

      v

QA Tests

      |

      v

Staging Tests

      |

      v

Production Approval

      |

      v

Production Tests
```

Promotion decisions are controlled by Jenkins pipeline stages.

Example:

```text
QA Passed

     |

     v

Manual Approval

     |

     v

Continue to Production
```

The approval step allows a user to:

* continue promotion
* abort deployment

---

# Jenkins Workspace and Test Results

The Jenkins workspace contains:

```text
workspace/

    project files

    target/

        allure-results/

        surefire-reports/
```

Allure consumes:

```text
target/allure-results
```

and generates:

```text
allure-report
```

---

# Persistent Jenkins Data

Jenkins data is stored in:

```text
/var/jenkins_home
```

This contains:

* jobs
* plugins
* credentials
* build history
* configuration

The Docker Compose configuration persists this data.

Recreating the container does not remove Jenkins configuration.

---

# Restarting Jenkins

Restart container:

```bash
docker compose restart jenkins
```

Stop:

```bash
docker compose down
```

Start again:

```bash
docker compose up -d jenkins
```

---

# Future Automation

The current approach uses a combination of:

* Docker image configuration
* Jenkins UI configuration
* Jenkinsfile automation

A future improvement would be adding:

* Jenkins Configuration as Code (JCasC)
* automated credentials provisioning
* automated tool installation

This would allow a completely unattended Jenkins setup.

---

# Summary

The Jenkins one-time setup prepares Jenkins to:

* run pipelines
* communicate with Docker
* execute test containers
* generate Allure reports
* manage environment promotion

After completing this document once, normal usage becomes:

```text
Commit Code

↓

Push to GitHub

↓

Jenkins Pipeline Runs

↓

Tests Execute

↓

Reports Generated
```

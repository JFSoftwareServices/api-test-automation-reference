# Jenkins Pipeline (Jenkinsfile) Guide

## Overview

The Jenkins pipeline is defined in:

```text
Jenkinsfile
```

The Jenkinsfile is the executable definition of the CI/CD workflow.

It describes:

* what stages execute
* when tests run
* which Docker containers are started
* how results are collected
* when promotion is allowed
* how failures are handled

The Jenkinsfile allows the complete test automation process to be executed consistently by Jenkins.

---

# Pipeline Execution Model

The pipeline follows this architecture:

```text
Developer

    |

    |

Git Push

    |

    |

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

TestNG + REST Assured

    |

    |

Allure Results

    |

    |

Quality Gate

    |

    |

Promotion Decision
```

---

# Jenkinsfile Responsibilities

The Jenkinsfile is responsible for orchestration only.

It does not contain:

* test logic
* API validation logic
* environment configuration
* Maven configuration

Those responsibilities belong to:

| Component      | Responsibility                  |
| -------------- | ------------------------------- |
| Jenkinsfile    | Pipeline control                |
| Docker Compose | Container execution             |
| Dockerfile     | Test runtime                    |
| Maven          | Build and dependency management |
| TestNG         | Test execution                  |
| REST Assured   | API testing                     |
| Allure         | Reporting                       |

---

# Pipeline Structure

A typical pipeline contains:

```groovy
pipeline {

    agent any

    stages {

        stage('Checkout') {
        }

        stage('Build') {
        }

        stage('Execute Tests') {
        }

        stage('Publish Results') {
        }

        stage('Quality Gate') {
        }

        stage('Approval') {
        }

        stage('Production Promotion') {
        }

    }
}
```

---

# Agent Configuration

Example:

```groovy
agent any
```

This means Jenkins can execute the pipeline on any available agent.

The pipeline does not require the Jenkins server to have:

* Maven installed
* Java installed
* test dependencies installed

because execution happens inside Docker containers.

---

# Environment Variables

The pipeline uses environment variables to control execution.

Examples:

```text
ENV

SUITE

BASE_URL_JSON

BASE_URL_XML

LOG_REQUESTS

LOG_RESPONSES
```

Example:

```groovy
environment {

    ENV = 'qa'

    SUITE = 'testng.xml'

}
```

The same pipeline can execute against:

```text
Development

QA

Staging

Production
```

without changing test code.

---

# Checkout Stage

Purpose:

Retrieve the latest source code.

Example:

```groovy
stage('Checkout') {

    steps {

        checkout scm

    }

}
```

After checkout:

```text
Jenkins Workspace

    |

    +-- pom.xml

    +-- src/

    +-- Dockerfile

    +-- docker-compose.yml

    +-- Jenkinsfile
```

---

# Build Stage

The build stage creates the Docker test image.

Example:

```groovy
stage('Build Test Container') {

    steps {

        sh '''
        docker compose build tests-qa
        '''

    }

}
```

Execution:

```text
Dockerfile

    |

    |

Docker Image

    |

    |

Test Runner Container
```

---

# Test Execution Stage

The pipeline starts the selected test container.

Example:

```groovy
stage('Execute Tests') {

    steps {

        sh '''
        docker compose run --rm tests-qa
        '''

    }

}
```

Execution flow:

```text
Jenkins

    |

    |

docker compose run

    |

    |

tests-qa container

    |

    |

mvn test

    |

    |

TestNG
```

---

# Why docker compose run --rm?

The command:

```bash
docker compose run --rm tests-qa
```

means:

## run

Create and start a temporary container.

## --rm

Remove the container after execution.

Benefits:

* clean environment every run
* no stopped containers accumulating
* reproducible execution

Example:

Before:

```text
Docker Engine

    tests-qa-container
```

After:

```text
Container removed

Images remain
```

---

# Test Environment Selection

The pipeline can select different environments.

Example:

```groovy
parameters {

    choice(
        name: 'ENVIRONMENT',
        choices: [
            'dev',
            'qa',
            'staging',
            'prod'
        ]
    )

}
```

The selected environment determines which Docker Compose service executes.

Example:

```text
dev

    |

tests-dev


qa

    |

tests-qa


staging

    |

tests-staging
```

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
Test 1 ----\
Test 2 -----\
Test 3 -------> TestNG Thread Pool
Test 4 -----/
```

Benefits:

* faster feedback
* reduced pipeline duration
* better CI efficiency

---

# Sequential Test Execution

Some tests require ordering.

Example:

```text
Create Account

      |

Validate Account

      |

Delete Account
```

Sequential execution is useful when:

* tests have dependencies
* order matters
* validating workflows

---

# Publishing Test Results

After execution, results are available:

```text
target/

    |

    +-- surefire-reports

    |

    +-- allure-results
```

Jenkins publishes:

* JUnit results
* Allure reports

Example:

```groovy
junit(
    testResults: 'target/surefire-reports/*.xml'
)
```

---

# Allure Reporting Stage

Allure consumes:

```text
target/allure-results
```

Example:

```groovy
allure([
    includeProperties: false,
    results: [
        [
            path: 'target/allure-results'
        ]
    ]
])
```

Generated report includes:

* execution summary
* passed tests
* failed tests
* error details
* attachments
* execution history

---

# Failure Handling

The pipeline should always publish reports even when tests fail.

Example:

```groovy
post {

    always {

        allure([
            results: [
                [
                    path: 'target/allure-results'
                ]
            ]
        ])

    }

}
```

This allows failures to be investigated.

---

# Quality Gates

A quality gate determines whether promotion can continue.

Example:

```text
QA Tests

   |

   |

Passed?

   |

 +----+

 |    |

Yes  No

 |    |

Continue Stop
```

Examples of quality gates:

* all tests passed
* no critical failures
* acceptable defect threshold
* manual approval completed

---

# Manual Production Approval

Production promotion should normally require approval.

Example:

```groovy
stage('Production Approval') {

    steps {

        input message:
        'Proceed to Production?'

    }

}
```

The user can choose:

```text
Continue

or

Abort
```

---

# Promotion Flow

Example:

```text
Development

      |

      v

QA Testing

      |

      v

Staging Testing

      |

      v

Manual Approval

      |

      v

Production
```

A failed stage stops promotion.

---

# Pipeline Cleanup

Temporary containers should be removed.

Example:

```groovy
post {

    always {

        sh '''
        docker compose down
        '''

    }

}
```

This prevents unused resources.

---

# Pipeline Troubleshooting

## Check Jenkins Workspace

```bash
pwd

ls -la
```

Expected:

```text
pom.xml

src/

target/
```

---

## Check Docker Containers

Inside Jenkins:

```bash
docker ps
```

Jenkins should be able to communicate with Docker.

---

## Check Allure Results

```bash
ls -la target/allure-results
```

Expected:

```text
*-result.json

*-container.json
```

---

# Recommended Pipeline Improvements

Future enhancements:

## Jenkins Configuration as Code

Automate:

* plugins
* tools
* credentials
* jobs

## Pipeline Libraries

Move reusable logic into shared libraries.

Example:

```text
vars/

    runDockerTests.groovy

    publishAllure.groovy
```

## Cloud Agents

Move from static Jenkins containers to:

* Kubernetes agents
* AWS agents
* ephemeral build workers

---

# Summary

The Jenkinsfile provides the automation layer between source control and test execution.

The complete lifecycle is:

```text
Code Commit

      |

      |

Jenkins Pipeline

      |

      |

Docker Test Container

      |

      |

Maven Test Execution

      |

      |

Allure Reporting

      |

      |

Quality Decision

      |

      |

Promotion
```

The Jenkinsfile keeps the CI/CD process repeatable, automated and environment-independent.

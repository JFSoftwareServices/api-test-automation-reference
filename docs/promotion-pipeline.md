# Promotion Pipeline Guide

## Overview

The API Test Automation Reference framework supports a controlled promotion model that allows the same automated test suite to be executed against multiple environments before reaching production.

Rather than executing production tests immediately, the pipeline promotes the application through progressively higher environments, increasing confidence at each stage.

A typical promotion flow is:

```text
Development

      │

      ▼

QA

      │

      ▼

Staging

      │

      ▼

Manual Approval

      │

      ▼

Production
```

This approach helps identify defects as early as possible while reducing the risk of deploying to production.

---

# Why Use a Promotion Pipeline?

Running tests against multiple environments provides several benefits:

* validates deployments before production
* catches environment-specific issues
* increases confidence in releases
* provides clear quality gates
* allows manual approval before production

The same automated tests are reused throughout the pipeline.

---

# Promotion Philosophy

One of the key principles of this framework is:

> **Build once. Test many. Promote with confidence.**

The automation code should never change between environments.

Only the configuration changes.

For example:

```text
Development

BASE_URL_JSON=https://dev.example.com

↓

QA

BASE_URL_JSON=https://qa.example.com

↓

Staging

BASE_URL_JSON=https://staging.example.com

↓

Production

BASE_URL_JSON=https://api.example.com
```

The tests remain identical.

---

# Promotion Architecture

```text
GitHub Repository

        │

        ▼

Jenkins Pipeline

        │

        ▼

Development Tests

        │

        ▼

QA Tests

        │

        ▼

Staging Tests

        │

        ▼

Manual Approval

        │

        ▼

Production Tests
```

Every stage provides additional confidence before promotion.

---

# Development Environment

Purpose:

* verify new functionality
* validate feature development
* provide rapid feedback

Typical tests:

* smoke tests
* developer validation
* API contract tests

Docker Compose service:

```text
tests-dev
```

Example:

```bash
docker compose run --rm tests-dev
```

---

# QA Environment

Purpose:

* verify integration
* regression testing
* functional validation

Typical tests:

* regression suite
* API validation
* business workflow testing

Docker Compose service:

```text
tests-qa
```

Example:

```bash
docker compose run --rm tests-qa
```

Promotion to the next stage only occurs if the QA quality gate passes.

---

# Staging Environment

Purpose:

Staging should closely resemble the production environment.

Typical activities:

* full regression testing
* end-to-end validation
* release verification
* production readiness checks

Docker Compose service:

```text
tests-staging
```

Example:

```bash
docker compose run --rm tests-staging
```

---

# Production Environment

Production testing should normally be limited to:

* smoke tests
* health checks
* non-destructive API validation

Docker Compose service:

```text
tests-prod
```

Example:

```bash
docker compose run --rm tests-prod
```

Production should never be reached automatically without organisational approval unless your deployment policy explicitly allows it.

---

# Quality Gates

A quality gate determines whether the pipeline is allowed to continue.

Typical quality gates include:

* all automated tests passed
* no critical failures
* no infrastructure errors
* test execution completed successfully

Conceptually:

```text
Tests Complete

      │

      ▼

All Tests Passed?

      │

  ┌───┴────┐
  │        │
 Yes       No
  │        │
  ▼        ▼

Continue  Stop Pipeline
```

If a quality gate fails, promotion stops.

---

# Manual Approval

Before production, many organisations require a manual approval step.

Jenkins supports this using the `input` step.

Conceptually:

```groovy
stage('Production Approval') {
    steps {
        input message: 'Promote to Production?'
    }
}
```

During execution:

```text
Staging Complete

        │

        ▼

Approval Required

        │

  ┌─────┴─────┐
  │           │

Proceed     Abort
```

Only authorised users should approve production promotion.

---

# Jenkins Promotion Flow

A simplified Jenkins pipeline may look like:

```text
Checkout

      │

      ▼

Build Test Container

      │

      ▼

Run Development Tests

      │

      ▼

Quality Gate

      │

      ▼

Run QA Tests

      │

      ▼

Quality Gate

      │

      ▼

Run Staging Tests

      │

      ▼

Quality Gate

      │

      ▼

Manual Approval

      │

      ▼

Run Production Tests
```

Each stage depends on the success of the previous stage.

---

# Environment Configuration

Each environment supplies different configuration values.

Example:

| Environment | Docker Service  | Example `ENV` | Example Base URL              |
| ----------- | --------------- | ------------- | ----------------------------- |
| Development | `tests-dev`     | `dev`         | `https://dev.example.com`     |
| QA          | `tests-qa`      | `qa`          | `https://qa.example.com`      |
| Staging     | `tests-staging` | `staging`     | `https://staging.example.com` |
| Production  | `tests-prod`    | `prod`        | `https://api.example.com`     |

Only the configuration changes.

The framework code remains unchanged.

---

# Docker Compose Relationship

The Jenkins pipeline selects the appropriate Docker Compose service.

```text
Jenkins

      │

      ▼

docker compose run

      │

      ▼

tests-dev

or

tests-qa

or

tests-staging

or

tests-prod
```

Each service executes the same Docker image with different configuration.

---

# Test Results

Every environment produces:

```text
target/

    ├── surefire-reports/

    └── allure-results/
```

Jenkins publishes:

* JUnit reports
* Allure reports

This provides a complete execution history for each promotion stage.

---

# Failure Behaviour

Promotion stops immediately if a stage fails.

Example:

```text
Development

      │

      ▼

QA

      │

      ▼

FAILED

      │

      ▼

Pipeline Stops

      │

      ▼

Staging Skipped

      │

      ▼

Production Skipped
```

This prevents unstable software from progressing further.

---

# Best Practices

## Keep environments consistent

Differences between environments should be limited to configuration rather than infrastructure wherever possible.

---

## Use the same test suite

Avoid maintaining separate test code for each environment.

The framework is designed so that the same tests execute everywhere.

---

## Protect Production

Production should generally require:

* successful lower-environment execution
* manual approval (where appropriate)
* minimal, non-destructive validation

---

## Publish Reports

Generate Allure reports for every stage.

Historical reports make it easier to:

* identify regressions
* compare releases
* investigate failures

---

# Future Enhancements

Organisations may choose to extend the promotion pipeline with:

* automated deployment stages
* security scanning
* performance testing
* contract testing
* API compatibility checks
* notifications (Slack, Microsoft Teams, email)
* release tagging
* deployment rollback

The framework has been designed so these stages can be added without changing the core test architecture.

---

# Related Documentation

For additional information, see:

* `project-architecture.md`
* `docker-compose.md`
* `environment-configuration.md`
* `jenkins-setup.md`
* `jenkins-pipeline.md`
* `test-execution.md`
* `allure-reporting.md`

---

# Summary

The promotion pipeline provides a structured, repeatable approach to validating software before production.

By combining Docker, Jenkins, TestNG, REST Assured and Allure, the framework supports a modern CI/CD workflow where the same automation framework is promoted through Development, QA, Staging and Production using environment-specific configuration rather than environment-specific code.

This approach improves reliability, simplifies maintenance, and helps ensure that production deployments are backed by consistent automated validation at every stage.

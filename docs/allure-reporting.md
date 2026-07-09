# Allure Reporting Guide

## Overview

Allure is the primary reporting solution used by the API Test Automation Reference framework.

It transforms the raw execution data produced during test execution into rich, interactive reports that help developers, testers and stakeholders understand exactly what happened during a test run.

Unlike standard Maven or TestNG reports, Allure provides detailed information about:

* test execution status
* execution history
* execution duration
* request and response attachments
* stack traces
* categories of failures
* trends across multiple executions

---

# Why Allure?

Traditional test reports typically answer one question:

> **Did the tests pass or fail?**

Allure answers many more:

* Which tests failed?
* Why did they fail?
* How long did they take?
* Which API was being tested?
* Which requests were sent?
* Which responses were received?
* Has this test been failing recently?
* Which environment was executed?

This makes troubleshooting significantly easier.

---

# Reporting Architecture

```text
REST Assured Tests

        │

        ▼

TestNG

        │

        ▼

Allure Results

(target/allure-results)

        │

        ▼

Allure Report

(target/allure-report)

        │

        ▼

Developer / Jenkins
```

---

# During Test Execution

When Maven executes the tests:

```bash
mvn test
```

Allure listeners generate result files inside:

```text
target/

    └── allure-results/
```

These files describe the execution but are **not** intended to be read directly.

---

# Allure Results Directory

Example:

```text
target/

└── allure-results/

    ├── *.json
    ├── *.txt
    ├── attachments/
    └── environment.properties
```

Typical contents include:

* test results
* execution metadata
* attachments
* screenshots (UI projects)
* request/response logs
* environment information

---

# Generating a Report

Once the tests have completed, generate the HTML report.

Example:

```bash
allure generate target/allure-results --clean
```

This creates:

```text
target/

    └── allure-report/
```

---

# Viewing the Report

To serve the report locally:

```bash
allure serve target/allure-results
```

or open the generated report:

```text
target/allure-report/index.html
```

---

# Running Inside Docker

When tests execute inside Docker, the report files are still written to:

```text
/app/target/allure-results
```

The Docker volume mapping exposes these files back to the host machine.

Example:

```text
Host Machine

target/

        ▲

        │

Docker Volume

        │

        ▼

Container

/app/target/
```

This allows Jenkins to publish the reports after the container exits.

---

# Running Inside Jenkins

The Jenkins pipeline performs the following sequence:

```text
Run Tests

      │

      ▼

Generate Allure Results

      │

      ▼

Publish Allure Report

      │

      ▼

View Report in Jenkins
```

The Allure Jenkins plugin reads:

```text
target/allure-results/
```

and generates the report automatically.

---

# Report Overview

The landing page provides a high-level summary of the execution.

Typical information includes:

* total tests
* passed
* failed
* broken
* skipped
* execution duration

Example:

```text
Total Tests      120

Passed           118

Failed             2

Broken             0

Skipped            0
```

---

# Test Details

Selecting a test displays detailed information including:

* execution status
* execution duration
* description
* parameters
* attachments
* stack trace
* failure message

---

# Attachments

Allure supports attaching additional information to each test.

Typical API project attachments include:

* HTTP request
* HTTP response
* request headers
* response headers
* JSON payloads
* XML payloads
* log files

Example:

```text
CustomerRequest.json

CustomerResponse.json

Headers.txt
```

These greatly simplify debugging.

---

# Environment Information

Allure can display execution environment details.

Typical examples include:

```text
Environment: QA

Java Version: 21

Maven Version: 3.9.16

Framework Version: 1.0.0
```

This information is especially useful when comparing executions across environments.

---

# Categories

Failures can be grouped into categories.

Examples:

```text
API Failures

Infrastructure

Timeouts

Assertion Failures

Configuration Errors
```

This helps identify recurring patterns.

---

# Timeline View

The Timeline page displays when tests executed.

Example:

```text
Thread 1

█████████████

Thread 2

██████████

Thread 3

████████████████
```

This is particularly useful when executing tests in parallel.

---

# Parallel Execution

The framework supports parallel TestNG execution.

Allure visualises:

* thread usage
* execution overlap
* execution duration

This helps identify:

* bottlenecks
* long-running tests
* inefficient parallelisation

---

# History

If report history is preserved between Jenkins builds, Allure can display trends such as:

* pass rate
* failure rate
* execution duration
* historical stability

Example:

```text
Build 101 ✓

Build 102 ✓

Build 103 ✗

Build 104 ✓

Build 105 ✓
```

Trend reporting helps identify intermittent or flaky tests.

---

# Integration with Jenkins

The framework integrates with the Jenkins Allure plugin.

Typical pipeline flow:

```text
Checkout

      │

      ▼

Execute Tests

      │

      ▼

Generate Allure Results

      │

      ▼

Publish Allure Report

      │

      ▼

Review Results
```

Once published, the report can be accessed directly from the Jenkins build page.

---

# Troubleshooting

## No Report Generated

Verify:

```text
target/allure-results/
```

contains files.

---

## Empty Report

Check that:

* tests actually executed
* Allure listener is configured
* no files were deleted before publishing

---

## Jenkins Cannot Find Results

Verify that the Jenkins workspace contains:

```text
target/

    └── allure-results/
```

If Docker is used, confirm that the Docker volume mapping correctly exposes the `target` directory to Jenkins.

---

## Missing Attachments

Ensure that the framework attaches request and response data during test execution.

Without attachments, the report will still work but will contain less diagnostic information.

---

# Best Practices

* Publish an Allure report for every Jenkins build.
* Preserve report history where possible.
* Attach API requests and responses for failed tests.
* Include environment information in every execution.
* Review failed tests before promoting to higher environments.
* Use the Timeline view to optimise parallel execution.

---

# Related Documentation

* `test-execution.md`
* `environment-configuration.md`
* `docker-compose.md`
* `jenkins-pipeline.md`
* `promotion-pipeline.md`

---

# Summary

Allure provides rich, interactive reporting that extends far beyond simple pass/fail results. By capturing detailed execution data, attachments, timing information and historical trends, it enables faster diagnosis of failures and provides clear visibility into the quality of each test execution.

Within this framework, Allure integrates seamlessly with Maven, Docker and Jenkins, making it an essential part of the continuous testing and promotion process.

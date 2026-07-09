# Parallel Test Execution Guide

## Overview

One of the key objectives of the API Test Automation Reference framework is to execute automated tests efficiently while maintaining reliability and repeatability.

The framework uses **TestNG's built-in parallel execution capabilities** to execute multiple tests concurrently, significantly reducing overall execution time compared to sequential execution.

Parallel execution is particularly beneficial for:

* API regression suites
* Smoke test suites
* Large collections of independent tests
* Continuous Integration (CI) pipelines

---

# Why Parallel Execution?

Executing tests sequentially means each test waits for the previous one to finish.

Example:

```text
Test 1
   │
   ▼
Test 2
   │
   ▼
Test 3
   │
   ▼
Test 4
```

If each test takes:

```text
30 seconds
```

Then:

```text
4 Tests

×

30 Seconds

=

120 Seconds
```

Parallel execution allows multiple tests to execute simultaneously.

Example:

```text
Thread 1 ──► Test 1

Thread 2 ──► Test 2

Thread 3 ──► Test 3

Thread 4 ──► Test 4
```

The overall execution time is greatly reduced.

---

# Parallel Execution Architecture

Within this framework, parallel execution follows the architecture below.

```text
Developer

      │

      ▼

Maven

      │

      ▼

TestNG

      │

      ▼

Thread Pool

      │

      ├───────────────┐
      │               │
      ▼               ▼

Thread 1         Thread 2

      │               │
      ▼               ▼

REST Assured    REST Assured

      │               │
      └───────┬───────┘
              ▼

        API Environment
```

TestNG creates a configurable thread pool and distributes the test methods across the available threads.

---

# How TestNG Executes Tests

TestNG supports several parallel execution strategies.

| Mode        | Description                                  |
| ----------- | -------------------------------------------- |
| `methods`   | Executes individual test methods in parallel |
| `classes`   | Executes test classes in parallel            |
| `tests`     | Executes `<test>` sections in parallel       |
| `instances` | Executes class instances in parallel         |

The reference framework uses:

```xml
parallel="methods"
```

This provides a good balance between execution speed and simplicity.

---

# TestNG Configuration

Parallel execution is configured in the TestNG suite file.

Example:

```xml
<suite
    name="API Test Suite"
    parallel="methods"
    thread-count="8">

    <test name="Regression">
        <classes>
            <class name="tests.UsersApiTest"/>
            <class name="tests.PostsApiTest"/>
        </classes>
    </test>

</suite>
```

The key configuration values are:

| Setting        | Purpose                               |
| -------------- | ------------------------------------- |
| `parallel`     | Determines what executes concurrently |
| `thread-count` | Maximum number of concurrent threads  |

---

# Thread Count

The framework currently uses:

```text
thread-count="8"
```

This allows up to eight test methods to execute simultaneously.

Example:

```text
Thread 1 → GET Users

Thread 2 → POST User

Thread 3 → DELETE User

Thread 4 → GET Posts

Thread 5 → PUT Post

Thread 6 → PATCH User

Thread 7 → GET Comments

Thread 8 → GET Albums
```

The optimal thread count depends on:

* available CPU cores
* available memory
* API performance
* network latency
* test independence

Increasing the thread count does not always improve performance.

---

# Thread Safety

Parallel execution requires tests to be thread-safe.

A thread-safe test ensures that one executing thread cannot interfere with another.

Avoid shared mutable state such as:

* shared objects
* static variables
* global collections
* shared request specifications

Instead, each thread should work with its own independent data.

---

# REST Assured Thread Safety

The framework creates request specifications independently for each executing thread.

Conceptually:

```text
Thread 1

Request Specification A

───────────────

Thread 2

Request Specification B

───────────────

Thread 3

Request Specification C
```

Each thread builds and executes its own HTTP requests.

This avoids interference between concurrent tests.

---

# Independent Tests

Parallel execution works best when tests are independent.

Good examples include:

* Retrieve customer
* Retrieve products
* Retrieve orders
* Retrieve users

These tests can execute simultaneously because they do not depend on one another.

---

# Tests That Should Not Run in Parallel

Some tests naturally depend on execution order.

For example:

```text
Create Customer

      │

      ▼

Update Customer

      │

      ▼

Delete Customer
```

Running these simultaneously may produce unpredictable results.

Such workflow tests should execute sequentially.

---

# Parallel vs Sequential

## Parallel

```text
Thread 1 → Test A

Thread 2 → Test B

Thread 3 → Test C

Thread 4 → Test D
```

Advantages:

* faster execution
* better CPU utilisation
* shorter CI pipelines

---

## Sequential

```text
Test A

↓

Test B

↓

Test C

↓

Test D
```

Advantages:

* predictable order
* easier debugging
* suitable for dependent workflows

---

# Parallel Execution in Docker

When using Docker:

```text
Docker Container

        │

        ▼

Maven

        │

        ▼

TestNG

        │

        ▼

8 Worker Threads

        │

        ▼

REST Assured Tests
```

Parallel execution occurs inside the container.

The container simply provides the execution environment.

---

# Parallel Execution in GitHub Codespaces

GitHub Codespaces executes the framework in exactly the same way.

```text
GitHub Codespace

        │

        ▼

Dev Container

        │

        ▼

Maven

        │

        ▼

TestNG

        │

        ▼

Parallel Threads
```

The execution model is identical to a local machine.

---

# Parallel Execution in Jenkins

Jenkins starts the Docker test container.

Inside the container:

```text
Jenkins

      │

      ▼

Docker Compose

      │

      ▼

Test Runner Container

      │

      ▼

Maven

      │

      ▼

TestNG

      │

      ▼

Multiple Threads
```

Jenkins itself does not execute the tests in parallel.

It simply starts the container.

TestNG performs the parallel execution.

---

# Viewing Parallel Execution

Allure provides a Timeline view showing how tests executed concurrently.

Example:

```text
Thread 1

████████████

Thread 2

██████████

Thread 3

██████████████

Thread 4

████████
```

This helps identify:

* slow tests
* idle threads
* bottlenecks
* opportunities for optimisation

---

# Best Practices

## Keep Tests Independent

Tests should not rely on:

* execution order
* shared data
* previous test results

---

## Avoid Shared State

Avoid:

* mutable static variables
* singleton test data
* shared request objects

---

## Choose an Appropriate Thread Count

Too many threads can reduce performance.

Select a thread count appropriate for:

* available CPU resources
* API capacity
* CI infrastructure

---

## Separate Workflow Tests

Dependent workflow tests should be executed sequentially.

Independent validation tests should execute in parallel.

---

## Monitor Performance

Regularly review:

* execution duration
* Allure Timeline
* Jenkins build duration
* API response times

Small improvements across many tests can significantly reduce total pipeline execution time.

---

# Troubleshooting

## Tests Fail Only in Parallel

Possible causes include:

* shared state
* race conditions
* static variables
* non-thread-safe code

---

## API Rate Limits

Executing many requests simultaneously may exceed API rate limits.

Possible solutions:

* reduce thread count
* introduce throttling
* use dedicated test environments

---

## Increasing Threads Doesn't Improve Performance

Common reasons include:

* network latency
* API bottlenecks
* CPU limitations
* insufficient Docker resources

More threads do not automatically produce faster execution.

---

# Related Documentation

* `test-execution.md`
* `project-architecture.md`
* `docker.md`
* `docker-compose.md`
* `allure-reporting.md`
* `jenkins-pipeline.md`

---

# Summary

Parallel execution is a core capability of the API Test Automation Reference framework. By leveraging TestNG's parallel execution model, the framework significantly reduces test execution time while maintaining a single, reusable automation codebase.

Whether tests are executed locally, in GitHub Codespaces, inside Docker containers, or through Jenkins CI/CD pipelines, the underlying parallel execution model remains the same. With well-designed, independent tests and appropriate thread management, parallel execution improves feedback times, accelerates continuous integration, and supports efficient enterprise-scale API test automation.

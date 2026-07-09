# Environment Configuration Guide

## Overview

The API Test Automation Reference framework is designed to execute against multiple environments without requiring changes to the test code.

Rather than hard-coding URLs, credentials or execution settings, the framework externalises configuration using environment variables and Maven system properties.

This approach allows the same automation framework to execute against:

* Development
* QA
* Staging
* Production

using exactly the same source code.

---

# Configuration Philosophy

The framework follows a simple principle:

> **Test code should never know which environment it is running against.**

Instead:

* the execution environment supplies the configuration
* the framework reads the configuration
* the tests execute accordingly

For example, a test should never contain:

```java
String baseUrl = "https://qa.example.com";
```

Instead, it should obtain the value from the framework configuration.

---

# Configuration Flow

The configuration flows through the framework as shown below.

```text
Developer

      |

      |

Environment Variables

      |

      |

Docker Compose / Maven / Jenkins

      |

      |

Java System Properties

      |

      |

Framework Configuration

      |

      |

REST Assured

      |

      |

API Under Test
```

The source of the configuration changes depending on how the tests are executed, but the framework consumes it in the same way.

---

# Configuration Sources

The framework supports several configuration sources.

## Local Maven

A developer may provide values directly from the command line.

Example:

```bash
mvn test \
-Denv=qa \
-Dbase.url.json=https://jsonplaceholder.typicode.com \
-DsuiteXmlFile=testng.xml
```

---

## Environment Variables

Configuration can also be supplied using environment variables.

Example:

```bash
export ENV=qa
export BASE_URL_JSON=https://jsonplaceholder.typicode.com
export SUITE=testng.xml
```

The values are then passed into Maven.

---

## Docker Compose

Docker Compose injects environment variables into the test container.

Example:

```yaml
environment:
  ENV: ${ENV}
  SUITE: ${SUITE}
  BASE_URL_JSON: ${BASE_URL_JSON}
  BASE_URL_XML: ${BASE_URL_XML}
  LOG_REQUESTS: ${LOG_REQUESTS}
  LOG_RESPONSES: ${LOG_RESPONSES}
```

The container receives these values at runtime.

---

## Jenkins

Jenkins supplies the configuration when executing the pipeline.

Typical sources include:

* pipeline parameters
* environment variables
* Jenkins credentials
* secret text
* secret files

Jenkins passes the values to Docker Compose, which then passes them into the test container.

---

# Configuration Hierarchy

The framework follows the configuration hierarchy below.

```text
Jenkins Parameters
        │
        ▼
Environment Variables
        │
        ▼
Maven System Properties
        │
        ▼
Framework Configuration
        │
        ▼
Test Execution
```

---

# Standard Configuration Properties

The framework currently supports the following properties.

| Property             | Purpose                      |
| -------------------- | ---------------------------- |
| `ENV`                | Target execution environment |
| `SUITE`              | TestNG suite file            |
| `BASE_URL_JSON`      | Base URL for JSON APIs       |
| `BASE_URL_XML`       | Base URL for XML APIs        |
| `LOG_REQUESTS`       | Enable request logging       |
| `LOG_RESPONSES`      | Enable response logging      |
| `CONNECTION_TIMEOUT` | HTTP connection timeout      |
| `READ_TIMEOUT`       | HTTP read timeout            |

Additional properties can easily be added as the framework evolves.

---

# ENV

The `ENV` property identifies the target environment.

Example:

```text
dev
qa
staging
prod
```

Example:

```bash
export ENV=qa
```

or

```bash
mvn test -Denv=qa
```

---

# SUITE

The `SUITE` property specifies the TestNG suite to execute.

Example:

```text
testng.xml
```

Future examples might include:

```text
testng-parallel.xml
smoke.xml
regression.xml
```

---

# BASE_URL_JSON

This property defines the JSON API endpoint.

Example:

```text
https://jsonplaceholder.typicode.com
```

The framework automatically uses this value when creating REST Assured requests.

---

# BASE_URL_XML

Some organisations expose XML-based APIs alongside REST APIs.

This property allows the XML endpoint to be configured independently.

---

# Logging Configuration

Logging can be enabled or disabled without modifying source code.

Example:

```text
LOG_REQUESTS=true

LOG_RESPONSES=true
```

This is useful for:

* debugging
* API investigation
* CI troubleshooting

Production pipelines may disable verbose logging.

---

# HTTP Timeouts

The framework allows HTTP timeout configuration.

Example:

```text
CONNECTION_TIMEOUT=5000

READ_TIMEOUT=10000
```

This avoids recompiling code when timeout values change.

---

# Local Development Example

Example:

```bash
export ENV=dev

export BASE_URL_JSON=https://jsonplaceholder.typicode.com

export SUITE=testng.xml

mvn test \
-DsuiteXmlFile=$SUITE \
-Denv=$ENV \
-Dbase.url.json=$BASE_URL_JSON
```

---

# Docker Example

Docker Compose supplies configuration automatically.

Example:

```yaml
environment:
  ENV: ${ENV}
  SUITE: ${SUITE}
  BASE_URL_JSON: ${BASE_URL_JSON}
```

Execution:

```bash
docker compose run --rm tests-dev
```

---

# Jenkins Example

The Jenkins pipeline typically executes:

```text
Pipeline Parameters

        │

        ▼

Docker Compose

        │

        ▼

Test Container

        │

        ▼

Maven

        │

        ▼

Framework Configuration
```

The pipeline itself does not need to know how the framework consumes the values.

---

# Adding New Configuration Properties

New properties can be introduced without modifying the execution model.

Typical process:

1. Add the environment variable.
2. Pass it into Docker Compose.
3. Pass it into Maven (if required).
4. Read it within the framework.
5. Use it in the relevant component.

Because configuration is externalised, no architectural changes are required.

---

# Best Practices

## Do

* Keep configuration outside the source code.
* Use environment variables for deployment-specific values.
* Use meaningful property names.
* Document new configuration properties.

## Avoid

* Hard-coded URLs.
* Hard-coded credentials.
* Environment-specific logic inside tests.
* Duplicate configuration values.

---

# Secrets and Credentials

Sensitive information should never be committed to source control.

Examples include:

* usernames
* passwords
* API keys
* access tokens
* certificates

Instead, use:

* Jenkins Credentials
* GitHub Secrets
* Docker Secrets (where appropriate)
* organisation-approved secret management solutions

Tests should retrieve secrets from secure configuration rather than embedding them in code.

---

# Troubleshooting

## Property Not Found

Check:

```bash
echo $ENV
```

or

```bash
printenv
```

Verify the value is available before running Maven or Docker Compose.

---

## Docker Container Receives Blank Values

Confirm that Docker Compose is passing the variables:

```yaml
environment:
  ENV: ${ENV}
```

If `${ENV}` is not defined, Docker Compose substitutes an empty value.

---

## Jenkins Pipeline Uses Unexpected Values

Verify:

* pipeline parameters
* environment block
* Jenkins credentials
* shell commands
* Docker Compose arguments

Remember that Jenkins controls the values supplied to Docker Compose.

---

# Summary

The framework separates configuration from test implementation.

Configuration flows from the execution environment into the framework, allowing the same automation codebase to run consistently across local development, Docker containers, GitHub Codespaces and Jenkins pipelines.

This design improves portability, maintainability and makes it easier to promote the same tests through Development, QA, Staging and Production without modifying the underlying test code.

## Configuration Matrix

The framework supports multiple execution environments while maintaining a consistent configuration model.

Although the source of the configuration changes depending on how the framework is executed, the framework consumes the values in exactly the same way.

| Property             | IntelliJ IDEA                                    | GitHub Codespaces             | Docker Compose                        | Jenkins Pipeline                                |
| -------------------- | ------------------------------------------------ | ----------------------------- | ------------------------------------- | ----------------------------------------------- |
| `ENV`                | Maven VM option or terminal environment variable | Terminal environment variable | Docker Compose `environment:` section | Jenkins build parameter or environment variable |
| `SUITE`              | Maven command-line argument                      | Maven command-line argument   | Docker Compose `environment:` section | Jenkins build parameter                         |
| `BASE_URL_JSON`      | Environment variable or Maven property           | Environment variable          | Docker Compose `environment:` section | Jenkins build parameter or environment variable |
| `BASE_URL_XML`       | Environment variable or Maven property           | Environment variable          | Docker Compose `environment:` section | Jenkins build parameter or environment variable |
| `LOG_REQUESTS`       | Environment variable                             | Environment variable          | Docker Compose `environment:` section | Jenkins build parameter                         |
| `LOG_RESPONSES`      | Environment variable                             | Environment variable          | Docker Compose `environment:` section | Jenkins build parameter                         |
| `CONNECTION_TIMEOUT` | Environment variable                             | Environment variable          | Docker Compose `environment:` section | Jenkins build parameter                         |
| `READ_TIMEOUT`       | Environment variable                             | Environment variable          | Docker Compose `environment:` section | Jenkins build parameter                         |

Regardless of where the values originate, the configuration flows through the framework in the same way:

```text
Developer / CI System

        │

        ▼

Environment Variables
or
Maven System Properties

        │

        ▼

Docker Compose (optional)

        │

        ▼

Test Runner Container (optional)

        │

        ▼

Java System Properties

        │

        ▼

Framework Configuration

        │

        ▼

REST Assured

        │

        ▼

API Under Test
```

### Configuration Examples

#### IntelliJ IDEA

Run using Maven with command-line properties:

```bash
mvn test \
-Denv=qa \
-DsuiteXmlFile=testng.xml \
-Dbase.url.json=https://jsonplaceholder.typicode.com
```

---

#### GitHub Codespaces

Export environment variables before executing Maven:

```bash
export ENV=qa
export SUITE=testng.xml
export BASE_URL_JSON=https://jsonplaceholder.typicode.com

mvn test
```

---

#### Docker Compose

Docker Compose injects the variables into the test container:

```yaml
environment:
  ENV: ${ENV}
  SUITE: ${SUITE}
  BASE_URL_JSON: ${BASE_URL_JSON}
```

Execution:

```bash
docker compose run --rm tests-qa
```

---

#### Jenkins Pipeline

The Jenkins pipeline typically passes values into Docker Compose before starting the test container.

Example execution flow:

```text
Jenkins Parameters

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

Framework Configuration
```

The key principle is that **the automation code is identical regardless of how it is executed**. Only the source of the configuration changes, allowing the same framework to be developed locally, executed in GitHub Codespaces, run inside Docker containers, and promoted through Jenkins CI/CD pipelines without modifying the tests themselves.

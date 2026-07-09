# Shell Scripts Guide

## Overview

The API Test Automation Reference framework includes a collection of shell scripts that simplify common development and Jenkins administration tasks.

Rather than remembering lengthy Docker or Docker Compose commands, developers can execute a small number of reusable scripts.

The scripts are located in the project's `jenkins-tools/` directory and are intended to automate routine operations such as starting Jenkins, stopping services, viewing logs and retrieving the initial administrator password.

The scripts are designed to:

* reduce repetitive command-line work
* provide consistent project setup
* simplify onboarding for new developers
* minimise typing errors
* document common operational tasks

---

# Directory Structure

```text id="d10q4r"
jenkins-tools/

├── 01-start-jenkins.sh
├── 02-stop-jenkins.sh
├── 03-get-jenkins-password.sh
├── 04-jenkins-status.sh
├── 05-jenkins-logs.sh
└── ...
```

Your project may contain additional scripts as it evolves.

---

# Why Use Shell Scripts?

Without helper scripts, developers would need to remember commands such as:

```bash id="u9g7am"
docker compose up -d --build jenkins
```

or

```bash id="9ck1mx"
docker compose logs -f jenkins
```

Shell scripts encapsulate these commands behind descriptive names, making the framework easier to use and maintain.

---

# Typical Workflow

A typical Jenkins workflow using the supplied scripts is:

```text id="y0f2k8"
Start Jenkins

      │

      ▼

Retrieve Admin Password

      │

      ▼

Complete One-Time Setup

      │

      ▼

Create Jenkins Pipeline

      │

      ▼

Run Tests

      │

      ▼

View Logs (if required)

      │

      ▼

Stop Jenkins
```

---

# Starting Jenkins

Script:

```text id="k3vwn4"
01-start-jenkins.sh
```

Purpose:

* builds the Jenkins Docker image (if required)
* starts the Jenkins container
* displays useful status information
* provides links to Jenkins

Internally, the script executes a command similar to:

```bash id="9b5pvg"
docker compose up -d --build jenkins
```

---

# Stopping Jenkins

Script:

```text id="gmx0vn"
02-stop-jenkins.sh
```

Purpose:

* stops the Jenkins container
* leaves project files unchanged

Typical command:

```bash id="xyh7hf"
docker compose down
```

---

# Retrieving the Initial Administrator Password

Script:

```text id="syohx6"
03-get-jenkins-password.sh
```

Purpose:

Displays the initial Jenkins administrator password after the first startup.

Internally this usually reads:

```text id="m5pjlwm"
/var/jenkins_home/secrets/initialAdminPassword
```

This script is typically required only once during the initial Jenkins setup.

---

# Checking Jenkins Status

Script:

```text id="tlfn8d"
04-jenkins-status.sh
```

Purpose:

Displays the current status of the Docker containers.

Typical command:

```bash id="p9v0x4"
docker compose ps
```

Example output:

```text id="bwt0mz"
NAME                 STATUS

jenkins              Up

tests-dev            Exited
```

---

# Viewing Jenkins Logs

Script:

```text id="kndq3r"
05-jenkins-logs.sh
```

Purpose:

Displays the Jenkins container logs.

Typical command:

```bash id="a6j6ee"
docker compose logs -f jenkins
```

This is useful for diagnosing:

* startup issues
* plugin installation
* pipeline failures
* Docker problems

---

# Script Execution

Shell scripts must normally be executable.

Example:

```bash id="upk8ln"
chmod +x jenkins-tools/*.sh
```

They can then be executed using:

```bash id="3gohuj"
./jenkins-tools/01-start-jenkins.sh
```

or

```bash id="r0q90u"
./jenkins-tools/05-jenkins-logs.sh
```

---

# Running from the Project Root

The scripts are designed to be executed from the project root directory.

Example:

```text id="v5qj9o"
api-test-automation-reference/

├── docker-compose.yml
├── Dockerfile
├── Jenkinsfile
└── jenkins-tools/
```

Many scripts determine the project root automatically before executing Docker Compose commands.

A typical implementation is:

```bash id="glab7r"
PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_DIR"
```

This ensures Docker Compose always runs against the correct project.

---

# Relationship with Docker Compose

The shell scripts are convenience wrappers around Docker Compose.

```text id="i4r4zn"
Developer

      │

      ▼

Shell Script

      │

      ▼

Docker Compose

      │

      ▼

Docker Engine

      │

      ▼

Jenkins Container
```

The scripts do not replace Docker Compose; they simply make it easier to use.

---

# Relationship with Jenkins

Once Jenkins is running, most day-to-day activities are performed through the Jenkins web interface.

The shell scripts are primarily used for:

* starting Jenkins
* stopping Jenkins
* checking status
* viewing logs
* initial setup

Pipeline execution is managed by Jenkins itself.

---

# Extending the Scripts

Additional helper scripts can be added as the framework evolves.

Examples include:

* rebuilding Docker images
* cleaning Docker volumes
* removing unused containers
* generating Allure reports
* running local smoke tests
* backing up Jenkins configuration

Keeping these operations in dedicated scripts promotes consistency across the development team.

---

# Best Practices

* Keep each script focused on a single task.
* Use descriptive filenames with numeric prefixes to indicate a logical order.
* Include comments to explain non-obvious commands.
* Use `set -e` to stop execution if a command fails.
* Resolve the project root automatically before running Docker Compose commands.
* Avoid embedding machine-specific paths wherever possible.

---

# Related Documentation

* `docker.md`
* `docker-compose.md`
* `dockerfile-jenkins.md`
* `jenkins-setup.md`
* `jenkins-pipeline.md`
* `project-architecture.md`

---

# Summary

The shell scripts included with the API Test Automation Reference framework provide a simple, consistent interface for common operational tasks. By wrapping frequently used Docker Compose commands, they reduce repetitive work, improve usability, and help ensure that developers interact with the framework in a predictable and repeatable manner. While the scripts simplify everyday operations, Docker Compose and Jenkins remain the underlying technologies responsible for container management and continuous integration.

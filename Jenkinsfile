// ═══════════════════════════════════════════════════════════════════════════
// JFSoftwareServices — API Test Automation Framework CI/CD
// Enterprise CI/CD Pipeline (Docker-Driven Execution Model)
// ═══════════════════════════════════════════════════════════════════════════
//
// PIPELINE TRIGGER STRATEGY
// ──────────────────────────
// Triggered primarily via GitHub push webhook (githubPush()).
//
// The pipeline can also be started manually from Jenkins UI or via API,
// depending on job configuration.
//
// Branch behavior (enforced by pipeline logic):
//   • Feature branch → DEV-only execution (if configured)
//   • main branch    → full promotion pipeline (DEV → QA → STAGING → PROD)
// ───────────────────────────────────────────────────────────────────────────
//
// PIPELINE MODES
// ──────────────
//
// MODE 1 — promotion (default CI/CD flow)
// Full sequential pipeline execution:
//
//   DEV → QA → STAGING → (manual approval) → PROD
//
// Used for:
//   • main branch pushes
//   • release branches
//
// MODE 2 — single (debug / targeted execution)
// Executes one environment only (if pipeline parameterized accordingly):
//
//   dev | qa | staging | prod
//
// Used for:
//   • debugging pipeline failures
//   • isolated environment validation
//   • optional feature branch runs (if enabled)
//
// ───────────────────────────────────────────────────────────────────────────
//
// PROMOTION PIPELINE OVERVIEW
// ───────────────────────────
//
//  ┌──────────────┐
//  │  Checkout    │
//  │ & Validate   │
//  └──────┬───────┘
//         │
//  ┌──────▼───────┐
//  │ Build Images │
//  └──────┬───────┘
//         │
//  ┌──────▼──────────────┐
//  │        DEV          │
//  │ TestNG (optional    │
//  │ parallel execution) │
//  │   tests-dev         │
//  └──────┬──────────────┘
//         │ PASS
//  ┌──────▼──────────────┐
//  │         QA          │
//  │ TestNG (optional    │
//  │ parallel execution) │
//  │   tests-qa          │
//  └──────┬──────────────┘
//         │ PASS
//  ┌──────▼──────────────┐
//  │       STAGING       │
//  │ TestNG (optional    │
//  │ parallel execution) │
//  │   tests-staging     │
//  └──────┬──────────────┘
//         │ PASS
//  ┌──────▼──────────────────────┐
//  │   Production Approval Gate  │◀── Manual intervention required
//  └──────┬──────────────────────┘
//         │ Approved
//  ┌──────▼──────────────┐
//  │        PROD         │
//  │ TestNG (optional    │
//  │ parallel execution) │
//  │   tests-prod        │
//  └──────┬──────────────┘
//         │
//  ┌──────▼──────────────┐
//  │ Publish Reports     │
//  │ (Allure Dashboard)  │
//  └─────────────────────┘
//
// ───────────────────────────────────────────────────────────────────────────
//
// ARCHITECTURE OVERVIEW
// ─────────────────────
//
//   Jenkins (orchestration layer)
//          ↓
//   Docker Compose (execution layer)
//          ↓
//   Test Containers (Maven + TestNG + RestAssured)
//          ↓
//   Allure Results (mounted back to workspace)
//
// Jenkins does not execute tests directly.
// It orchestrates container execution and enforces pipeline gates.
//
// ───────────────────────────────────────────────────────────────────────────
//
// TEST EXECUTION MODEL
// ─────────────────────
//
// Each environment maps to a Docker Compose profile:
//
//   dev      → tests-dev
//   qa       → tests-qa
//   staging  → tests-staging
//   prod     → tests-prod
//
// Inside containers:
//   • testng.xml            → sequential execution
//   • testng-parallel.xml   → parallel execution (TestNG internal parallelism)
//
// NOTE:
// This is NOT distributed test sharding — parallelism is limited to
// TestNG execution within a single container unless externally scaled.
//
// ───────────────────────────────────────────────────────────────────────────
//
// QUALITY GATES
// ─────────────
// • Any non-zero container exit code fails the current stage
// • Failed stage stops further pipeline progression
// • Production deployment requires manual approval (input gate)
// • Allure reports are published after pipeline execution
//
// ───────────────────────────────────────────────────────────────────────────
//
// FUTURE ENHANCEMENTS
// ────────────────────
// • Slack notifications
// • Email alerts
// • Docker registry publishing
// • Kubernetes migration (ephemeral test pods)
// • Cross-container distributed test sharding
//
// ═══════════════════════════════════════════════════════════════════════════

pipeline {
    agent any

    triggers {
        githubPush()
    }

    parameters {
      choice(
        name: 'SUITE',
        choices: ['testng.xml', 'testng-parallel.xml'],
        description: 'TestNG suite executed inside containers'
      )

      booleanParam(
        name: 'DEPLOY_TO_PROD',
        defaultValue: false,
        description: 'Deploy to production after approval'
      )

      booleanParam(
        name: 'LOG_REQUESTS',
        defaultValue: false,
        description: 'Log HTTP requests during test execution'
      )

      booleanParam(
        name: 'LOG_RESPONSES',
        defaultValue: false,
        description: 'Log HTTP responses during test execution'
      )
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    environment {
        COMPOSE = 'docker compose'
        BASE_URL_JSON = 'https://jsonplaceholder.typicode.com'
        BASE_URL_XML = 'https://jsonplaceholder.typicode.com'
        CONNECTION_TIMEOUT = '5000'
        READ_TIMEOUT = '10000'
    }

    stages {

        stage('Checkout') {
            steps {
                cleanWs()
                checkout scm
            }
        }

        stage('Build Test Images') {
            steps {
                sh "${COMPOSE} build tests-dev tests-qa tests-staging tests-prod"
            }
        }

        // ─────────────────────────────────────────────
        // DEV
        // ─────────────────────────────────────────────
        stage('DEV') {

            environment{
                ENV = 'dev'
                LOG_REQUESTS = "${params.LOG_REQUESTS}"
                LOG_RESPONSES = "${params.LOG_RESPONSES}"
            }

            steps {
                script {
                    runEnv('dev')
                }
            }
        }

        // ─────────────────────────────────────────────
        // QA
        // ─────────────────────────────────────────────
        stage('QA') {
            environment{
                ENV = 'qa'
                LOG_REQUESTS = "${params.LOG_REQUESTS}"
                LOG_RESPONSES = "${params.LOG_RESPONSES}"
            }
            steps {
                script {
                    runEnv('qa')
                }
            }
        }

        // ─────────────────────────────────────────────
        // STAGING
        // ─────────────────────────────────────────────
        stage('STAGING') {
            environment{
                ENV = 'staging'
                LOG_REQUESTS = "${params.LOG_REQUESTS}"
                LOG_RESPONSES = "${params.LOG_RESPONSES}"
            }
            steps {
                script {
                    runEnv('staging')
                }
            }
        }

        // ─────────────────────────────────────────────
        // PRODUCTION GATE
        // ─────────────────────────────────────────────
        stage('Production Approval') {
           when {
                    expression {
                        params.DEPLOY_TO_PROD
                 }
            }
            steps {
                    timeout(time: 24, unit: 'HOURS') {
                    input message: 'Deploy ${env.JOB_NAME} build ${env.BUILD_NUMBER} to production?'
                }
            }
        }

        stage('PROD') {
            when {
                    expression {
                        params.DEPLOY_TO_PROD
                 }
            }
            environment{
                ENV = 'prod'
                LOG_REQUESTS = 'false'
                LOG_RESPONSES = 'false'
            }
            steps {
                script {
                    runEnv('prod')
                }
            }
        }
        
        // ─────────────────────────────────────────────
        // REPORTING
        // ─────────────────────────────────────────────
        stage('Allure Report') {
            steps {
                allure([
                    results: [[path: 'target/allure-results']]
                ])
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// ENV EXECUTION FUNCTION
// ═══════════════════════════════════════════════════════════════════════════

def runEnv(String env) {

    echo "Running tests for environment: ${env}"
    echo "Using suite: ${params.SUITE}"

    sh '''
        echo "=================================="
        echo "Environment Validation"
        echo "=================================="

        test -n "$ENV" || (echo "ERROR: ENV is empty" && exit 1)
        test -n "$BASE_URL_JSON" || (echo "ERROR: BASE_URL_JSON is empty" && exit 1)
        test -n "$BASE_URL_XML" || (echo "ERROR: BASE_URL_XML is empty" && exit 1)

        echo "ENV              = $ENV"
        echo "BASE_URL_JSON    = $BASE_URL_JSON"
        echo "BASE_URL_XML     = $BASE_URL_XML"

        echo "Environment variables validated"
        echo "=================================="
    '''

    sh """
        echo "Starting ${env} tests..."

        SUITE=${params.SUITE} \
        ${COMPOSE} \
            --profile ${env} \
            run --rm \
            tests-${env}
    """
}
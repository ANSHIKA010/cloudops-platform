# Product and Scope

**Status:** Proposed

## Problem statement

Deploying and operating containerized applications requires developers to work
across CI/CD tools, Kubernetes commands, logs, metrics, and alerting systems.
CloudOps provides a single workflow for requesting deployments, tracking their
lifecycle, observing application health, and investigating failures.

## Product goals

- Make a container deployment understandable and repeatable for a developer.
- Provide a reliable record of desired state, execution status, and history.
- Surface health, logs, resource usage, and actionable failure information.
- Keep infrastructure-provider details behind a stable application boundary.
- Demonstrate production-minded Java, distributed-systems, and DevOps design.

## Non-goals for the first release

- Replacing a full cloud provider or Kubernetes control plane.
- Supporting arbitrary infrastructure-as-code execution.
- Automatic production remediation without explicit policy and approval.
- Multi-cloud placement optimization or billing reconciliation.
- Training a proprietary AI model.

## Actors

| Actor | Responsibilities |
| --- | --- |
| Developer | Registers an application, requests deployments, views status and diagnostics |
| Operator | Manages environments, policies, incidents, and failed operations |
| Platform administrator | Manages users, roles, clusters, credentials, and global configuration |
| CI/CD system | Creates or promotes deployments through a service identity |
| Deployment provider | Applies desired state to a simulator or Kubernetes cluster |
| Analysis service | Converts failure evidence into a structured diagnostic recommendation |

## System boundary

CloudOps owns deployment intent, workflow state, access policy, audit history,
and its normalized operational view. Kubernetes owns workload scheduling and
runtime state. An external identity provider may own user identity. Monitoring
backends own raw time-series and log storage when introduced.

## Core domain language

- **Application**: a deployable service owned by a team.
- **Environment**: a logical target such as development, staging, or production.
- **Deployment**: an immutable request to run a particular application version.
- **Release**: the artifact and configuration promoted across environments.
- **Deployment status**: the controlled lifecycle state of a deployment.
- **Cluster**: a provider target capable of running workloads.
- **Incident analysis**: evidence-based explanation and remediation suggestions.
- **Audit event**: an immutable record of a security- or operation-relevant action.

## Assumptions and constraints

- Java 21 and Spring Boot form the primary backend.
- PostgreSQL is the transactional source of truth.
- The first implementation is a modular monolith with explicit module boundaries.
- Deployment execution begins with a simulator and later gains a Kubernetes adapter.
- Kafka and Redis are introduced only for requirements that justify their operational cost.
- Local development must remain possible with Docker Compose.


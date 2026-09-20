# ADR-0001: Start with a modular monolith

- **Status:** Accepted
- **Date:** 2026-09-20
- **Owner:** CloudOps project
- **Related requirements:** NFR-005, NFR-012, NFR-013

## Context

CloudOps spans identity, application catalog, deployment orchestration,
operations, incident analysis, notifications, and audit. These capabilities need
clear ownership, but the early project has a small team, evolving requirements,
and a strong need for simple local development and transactional correctness.

## Decision

Build the primary Java backend as a modular monolith. Organize code by business
capability and enforce module boundaries. Domain and application code depend on
ports; HTTP, persistence, messaging, and deployment-provider integrations are
adapters. Modules may be extracted only when evidence supports independent
scaling, deployment, failure isolation, or team ownership.

## Alternatives considered

- **Microservices immediately:** improves independent deployment in theory, but
  introduces distributed transactions, contract versioning, observability,
  deployment, and local-development cost before the boundaries are proven.
- **Traditional technical layers across the entire application:** initially
  familiar, but makes domain ownership unclear and encourages cross-feature coupling.
- **Single unstructured application:** fastest for a prototype, but undermines
  maintainability and makes later extraction unnecessarily difficult.

## Consequences

The project can use local transactions and a single deployable while retaining
explicit domain boundaries. Tests and refactoring remain simpler. The application
can still scale horizontally as a unit. Some modules may consume unnecessary
resources when only one workload needs scaling, and weak boundary enforcement
could degrade the design into a coupled monolith.

## Validation

- Add automated architecture tests for allowed module dependencies.
- Keep domain packages free of framework and infrastructure imports.
- Measure deployment and analysis workloads before separating workers/services.
- Revisit when independent scaling, release cadence, security isolation, or team
  ownership produces a demonstrated constraint.


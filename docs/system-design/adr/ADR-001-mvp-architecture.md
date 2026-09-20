# ADR-001: Modular Monolith with a Simulated Provider Port

- **Status:** Proposed
- **Date:** 2026-09-20
- **Decision owner:** Project author
- **Milestone:** September 30, 2026 vertical-slice MVP

## Context

The README proposes a Java 21/Spring Boot modular monolith, PostgreSQL persistence, Kafka, Redis, a simulated provider that can later be replaced by Kubernetes, and a separate FastAPI analysis service. Only the Spring Boot/PostgreSQL foundation is currently present. Ten calendar days remain before the target demo.

Trying to introduce every proposed component would test integration breadth rather than prove the core user journey. The milestone needs a visible, persistent deployment lifecycle and a design seam for future infrastructure execution.

## Decision

Build one Spring Boot deployable with a deployment module whose application service depends on a `DeploymentProvider` port. For this milestone:

- PostgreSQL is the source of truth.
- HTTP commands submit and explicitly execute a deployment request.
- Execution is synchronous and controlled by the application service.
- A deterministic simulated adapter satisfies the provider port and makes no external infrastructure changes.
- Lifecycle rules live in the domain model and permit `REQUESTED -> DEPLOYING -> RUNNING`; invalid transitions are rejected.
- Package boundaries should reflect domain, application, adapters-in, and adapters-out responsibilities without introducing separate Maven modules.

## Consequences

### Benefits

- The demo proves an end-to-end business path with a real database.
- Unit tests can exercise lifecycle rules without Spring or PostgreSQL.
- A later Kubernetes adapter can implement the same provider port.
- Local development and failure diagnosis remain manageable.

### Trade-offs

- Synchronous execution is not suitable for long-running real deployments.
- The MVP does not demonstrate event delivery, idempotency, retry, or distributed-service behavior.
- A simulated success is evidence of orchestration and persistence, not evidence that a container was deployed.

## Follow-up decision triggers

- Introduce asynchronous execution only when a real provider or latency requirement demands it.
- Decide on an outbox/Kafka design before promising reliable operational events.
- Decide idempotency semantics before adding Redis.
- Record separate ADRs before splitting services or adding Kubernetes-specific domain concepts.

## Related requirements

- [MVP-FR-001 through MVP-FR-006 and MVP-NFR-001 through MVP-NFR-006](../mvp-requirements.md)

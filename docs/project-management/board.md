# CloudOps Delivery Board

**Milestone:** Demonstrable vertical-slice MVP by September 30, 2026  
**Last updated:** September 20, 2026  
**WIP rule:** At most one main implementation task may be in **In Progress**. Documentation/coordination tasks do not consume that implementation slot.  
**Done rule:** Move a task to **Done** only when every acceptance criterion is met and the listed tests/evidence have been observed.

## Scope guardrail

The milestone proves: **submit request -> persist `REQUESTED` -> execute through simulated provider -> persist `RUNNING` -> retrieve by ID**. It does not claim to deploy a real container. See [MVP requirements](../system-design/mvp-requirements.md) and [ADR-001](../system-design/adr/ADR-001-mvp-architecture.md).

## Backlog

### COPS-108 — Document deferred architecture backlog

- **Priority:** P2
- **Estimate:** 1 hour
- **Dependencies:** COPS-106
- **Acceptance criteria:** Kafka/outbox, Redis/idempotency, Kubernetes adapter, AI analysis, auth, and monitoring are recorded as separate future outcomes with decision triggers; none is described as implemented.
- **Learning objective:** Distinguish evolutionary architecture from speculative complexity.
- **Links:** [Out-of-scope requirements](../system-design/mvp-requirements.md#explicitly-out-of-scope-for-this-milestone), [ADR-001 follow-ups](../system-design/adr/ADR-001-mvp-architecture.md#follow-up-decision-triggers)

## Ready

### COPS-102 — Persist deployment requests with Flyway and JPA

- **Priority:** P0
- **Estimate:** 4 hours
- **Dependencies:** COPS-101
- **Acceptance criteria:** Versioned migration creates the deployment table; JPA mapping validates against it; repository save/load preserves ID, request fields, state, and UTC timestamps; repository integration tests pass with PostgreSQL Testcontainers.
- **Learning objective:** Understand schema ownership, persistence mapping, and why migration tests catch drift.
- **Links:** [MVP-FR-001, MVP-FR-003](../system-design/mvp-requirements.md#functional-requirements), [MVP-NFR-001, MVP-NFR-003](../system-design/mvp-requirements.md#non-functional-requirements)

### COPS-103 — Submit and retrieve deployment API

- **Priority:** P0
- **Estimate:** 5 hours
- **Dependencies:** COPS-102
- **Acceptance criteria:** `POST /api/v1/deployments` returns `201` and `REQUESTED`; `GET` returns the stored request; invalid input is `400`; unknown ID is `404`; controller and integration tests pass.
- **Learning objective:** Separate transport DTOs, application use cases, domain objects, and persistence models.
- **Links:** [MVP-FR-001 through MVP-FR-003](../system-design/mvp-requirements.md#functional-requirements), [MVP-NFR-004](../system-design/mvp-requirements.md#non-functional-requirements)

### COPS-104 — Execute through simulated provider

- **Priority:** P0
- **Estimate:** 5 hours
- **Dependencies:** COPS-103
- **Acceptance criteria:** Execute endpoint invokes a provider port; simulated adapter performs no external change; transitions are persisted; repeated execution returns `409`; unit/integration tests pass.
- **Learning objective:** Apply ports-and-adapters thinking and keep infrastructure details outside lifecycle rules.
- **Links:** [MVP-FR-004 through MVP-FR-006](../system-design/mvp-requirements.md#functional-requirements), [ADR-001](../system-design/adr/ADR-001-mvp-architecture.md)

### COPS-105 — Prove the full vertical slice

- **Priority:** P0
- **Estimate:** 4 hours
- **Dependencies:** COPS-104
- **Acceptance criteria:** A clean-database Testcontainers test covers submit, execute, retrieve, invalid input, unknown ID, and conflict; full Maven test suite passes.
- **Learning objective:** Design tests around observable behavior and boundaries rather than implementation details.
- **Links:** [MVP-NFR-002 through MVP-NFR-004](../system-design/mvp-requirements.md#non-functional-requirements)

### COPS-106 — Operational readiness and local demo script

- **Priority:** P1
- **Estimate:** 3 hours
- **Dependencies:** COPS-105
- **Acceptance criteria:** health endpoint is verified; README contains exact clean-start and demo commands plus expected responses; no undocumented manual database edits are needed.
- **Learning objective:** Treat operability and reproducibility as part of delivery.
- **Links:** [MVP-NFR-005, MVP-NFR-006](../system-design/mvp-requirements.md#non-functional-requirements), [existing local setup](../../README.md#local-setup)

### COPS-107 — Rehearse and harden the demo

- **Priority:** P1
- **Estimate:** 3 hours
- **Dependencies:** COPS-106
- **Acceptance criteria:** Demo succeeds twice from a clean database; timing and talking points fit ten minutes; known limitations are stated; only defects that threaten the slice are fixed.
- **Learning objective:** Communicate what the system proves without overstating simulated behavior.
- **Links:** [MVP demo story](../system-design/mvp-requirements.md#demo-story), [ADR trade-offs](../system-design/adr/ADR-001-mvp-architecture.md#trade-offs)

## In Progress

No main implementation task is currently in progress.

## Review/Test

No tasks are currently in review/test.

## Blocked

No tasks are currently blocked.

## Done

### COPS-101 — Define and test the deployment lifecycle model

- **Priority:** P0
- **Estimate:** 4 hours
- **Dependencies:** COPS-100
- **Acceptance criteria:** Request fields and invariants are documented; domain model supports `REQUESTED -> DEPLOYING -> RUNNING`; invalid transitions fail explicitly; fast unit tests cover valid and invalid transitions without Spring.
- **Learning objective:** Model business invariants and state transitions before introducing HTTP or database concerns.
- **Links:** [MVP-FR-002, MVP-FR-004, MVP-FR-005](../system-design/mvp-requirements.md#functional-requirements), [MVP-NFR-002](../system-design/mvp-requirements.md#non-functional-requirements), [ADR-001 decision](../system-design/adr/ADR-001-mvp-architecture.md#decision)
- **Evidence (2026-09-21):** Framework-free deployment aggregate, specification value object, lifecycle state, and explicit transition exception are implemented. The domain contract documents trimmed/non-blank field rules, maximum lengths, `1..5` replicas, and the milestone transition sequence. The targeted domain run passes 12 tests, and the full `mvnw.cmd test` run passes all 13 tests with no failures.

### COPS-100 — Verify the repository foundation

- **Priority:** P0
- **Estimate:** 1 hour
- **Dependencies:** JDK 21 and Docker Desktop
- **Acceptance criteria:** Java 21, Maven wrapper, Spring Boot context, PostgreSQL Testcontainers, Flyway, and Compose setup are verified; baseline test suite passes; any discrepancy is documented.
- **Learning objective:** Establish a trustworthy baseline before feature work.
- **Links:** [MVP-NFR-001, MVP-NFR-003, MVP-NFR-005](../system-design/mvp-requirements.md#non-functional-requirements), [local setup](../../README.md#local-setup)
- **Evidence (2026-09-21):** Microsoft OpenJDK `21.0.12.1` compiled the project. `cloudops-api\\mvnw.cmd test` passed the Spring Boot context test with PostgreSQL Testcontainers and Flyway; Docker Desktop supplied the test database.

### COPS-001 — Audit repository and establish milestone controls

- **Priority:** P0
- **Estimate:** 2 hours
- **Dependencies:** None
- **Acceptance criteria:** Implemented baseline is separated from proposed scope; smallest credible MVP is documented; dated schedule and evidence-based board exist; system-design source gap is visible.
- **Learning objective:** Convert a broad product idea into a testable delivery slice.
- **Links:** [MVP requirements](../system-design/mvp-requirements.md), [ADR-001](../system-design/adr/ADR-001-mvp-architecture.md), [delivery schedule](schedule-2026-09-20-to-09-30.md)
- **Evidence:** The detached worktree at commit `6e5a726` contained only application bootstrap/configuration, Compose PostgreSQL, and a context-load test. A fuller uncommitted `docs/system-design` set was later found in the durable main checkout; the milestone records remain **Proposed** and require reconciliation with those canonical design documents before implementation.

## Session update protocol

At the end of a session, answer:

1. Which task ID did you work on?
2. Which files changed?
3. Which exact test command did you run, and what was its result?
4. Which acceptance criteria can the code/test evidence now prove?
5. What is the smallest next step?

Update task state only from those answers and repository evidence. Partial work remains **In Progress**; code awaiting its required tests moves to **Review/Test**.

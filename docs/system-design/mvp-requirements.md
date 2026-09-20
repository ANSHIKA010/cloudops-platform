# CloudOps Vertical-Slice MVP Requirements

**Status:** Proposed for the September 30, 2026 demo  
**Source:** The repository README, the code present at commit `6e5a726`, and the broader proposed design set found in the durable project checkout  
**Important:** These requirements describe intended MVP behavior. They are not evidence that the behavior is implemented.

The `MVP-FR-*` and `MVP-NFR-*` IDs below are milestone acceptance slices, not replacements for the canonical `FR-*` and `NFR-*` IDs in `02-requirements.md`. Route shape and synchronous-versus-asynchronous execution must be reconciled with the canonical API and architecture documents before implementation.

## Problem statement

The current repository starts a Spring Boot application with PostgreSQL support, but it does not yet accept or track deployment requests. The smallest credible CloudOps demonstration is one persisted request moving through a controlled lifecycle by way of a replaceable provider boundary.

## Demo story

1. A caller submits a service name, Docker image reference, and replica count.
2. CloudOps validates and persists the request with status `REQUESTED`.
3. A caller asks CloudOps to execute that request.
4. A simulated provider performs no real infrastructure change and reports success.
5. CloudOps persists status `RUNNING` and the caller can retrieve the deployment by ID.
6. Invalid input and unknown IDs return consistent, test-covered errors.

## Functional requirements

| ID | Requirement | MVP acceptance signal |
|---|---|---|
| MVP-FR-001 | Submit a deployment request through `POST /api/v1/deployments`. | Valid input returns `201`, a generated ID, and status `REQUESTED`; the record exists in PostgreSQL. |
| MVP-FR-002 | Validate `serviceName`, `image`, and `replicas`. | Blank names/images and replicas outside the agreed range return `400` with field-level errors and create no record. |
| MVP-FR-003 | Retrieve a deployment through `GET /api/v1/deployments/{id}`. | A stored ID returns its representation; an unknown ID returns `404`. |
| MVP-FR-004 | Execute a requested deployment through `POST /api/v1/deployments/{id}/execute`. | The application uses a provider port, records the controlled transition `REQUESTED -> DEPLOYING -> RUNNING`, and returns the final state. |
| MVP-FR-005 | Reject invalid lifecycle commands. | Executing a non-`REQUESTED` deployment returns `409` and does not alter persisted state. |
| MVP-FR-006 | Provide a simulated deployment provider. | The happy-path demo requires no Kubernetes cluster or Docker API access and is deterministic. |

The precise JSON schema and the replica limit are design decisions to settle in task `COPS-101` before controller implementation.

## Non-functional requirements

| ID | Requirement | Verification |
|---|---|---|
| MVP-NFR-001 | Database schema is versioned, not generated implicitly. | Flyway migration exists and startup succeeds with `ddl-auto: validate`. |
| MVP-NFR-002 | Core lifecycle rules do not depend on Spring MVC or JPA. | Unit tests exercise transitions without starting the application context. |
| MVP-NFR-003 | The vertical slice is integration tested against PostgreSQL. | Testcontainers test covers submit, execute, retrieve, and persistence. |
| MVP-NFR-004 | API failures are predictable. | MVC/integration tests assert status codes and a stable error shape. |
| MVP-NFR-005 | Local setup is reproducible and contains no personal secrets. | README commands work with Compose defaults and the repository contains no committed credentials beyond documented local-only values. |
| MVP-NFR-006 | The demo remains operable. | `/actuator/health` reports `UP`; a documented demo script completes from a clean database. |

## Explicitly out of scope for this milestone

- Real Docker or Kubernetes deployment
- Kafka event delivery and an outbox
- Redis caching or idempotency
- FastAPI/AI failure analysis
- Authentication, authorization, multi-tenancy, and a web UI
- Continuous monitoring, logs, metrics dashboards, retries, rollback, and deployment deletion
- Production readiness, scaling, high availability, or cloud hosting

These are valid later increments, not implied capabilities of the September MVP.

## Open questions to resolve before coding the HTTP layer

1. What exact Docker image-reference formats should the MVP accept beyond “non-blank”? Prefer minimal validation unless a concrete requirement exists.
2. Is `replicas` limited to `1..5` for the demo, or should another bound be documented?
3. Should timestamps be emitted as UTC ISO-8601 values? The proposed answer is yes.

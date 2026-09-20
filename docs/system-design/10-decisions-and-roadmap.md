# Decisions and Roadmap

**Status:** Living document

## Accepted direction

- Java 21 and Spring Boot for the primary API.
- PostgreSQL as the transactional source of truth with Flyway migrations.
- Modular monolith first, with infrastructure behind ports/adapters.
- Asynchronous deployment lifecycle with durable intent.
- Simulator provider before Kubernetes integration.
- AI analysis is advisory, evidence-based, redacted, and isolated from the core path.

The rationale for significant decisions belongs in [ADRs](adr/README.md).

## Open decisions

| ID | Question | Decision trigger |
| --- | --- | --- |
| OD-001 | Which identity provider and token model will be used? | Before user-facing authentication work |
| OD-002 | Database-backed worker first or Kafka from the start? | Before asynchronous executor implementation |
| OD-003 | How are Kubernetes credentials isolated per environment/tenant? | Before Kubernetes adapter implementation |
| OD-004 | Which logs, metrics, and traces backends will be supported? | Before operational-data integration |
| OD-005 | What is the tenancy model: team isolation or true multi-tenancy? | Before external/shared deployment |
| OD-006 | Which AI provider, data policy, and evaluation set are acceptable? | Before incident-analysis integration |
| OD-007 | What production approval and promotion workflow is required? | Before production environment support |

## Principal risks

| Risk | Mitigation direction |
| --- | --- |
| Scope becomes a collection of tools rather than a coherent product | Deliver vertical workflows and enforce non-goals per milestone |
| Provider ambiguity causes duplicate or incorrect operations | Idempotency, operation IDs, reconciliation, explicit state machine |
| Authorization leaks resources across teams | Ownership-aware repositories, deny by default, negative tests |
| Kubernetes access becomes overly privileged | Narrow adapter service accounts, namespace isolation, admission policy |
| Telemetry volume or AI calls create uncontrolled cost | Retention, quotas, sampling, budgets, and cost metrics |
| AI output is trusted as fact or executed unsafely | Evidence citations, confidence, schema validation, human authorization |
| Premature Kafka/Redis adoption increases operational burden | Add only against measured consistency, throughput, or latency needs |

## Delivery roadmap

### Phase 1: Foundation

- Application and deployment domain model.
- Flyway schema and persistence adapters.
- Deployment state machine and simulator provider.
- REST contracts, validation, error model, idempotency, and audit basics.
- Unit, integration, architecture, and concurrency tests.

### Phase 2: Secure control plane

- OIDC authentication, role/ownership authorization, and environment policy.
- Durable background execution, retries, outbox/events, and reconciliation.
- Operational metrics, tracing, structured logs, dashboards, and runbooks.

### Phase 3: Kubernetes operations

- Kubernetes provider adapter with minimal RBAC.
- Workload health, events, bounded logs, restart, and scaling.
- Staging deployment pipeline, security scans, backup and restore exercise.

### Phase 4: Assisted incident response

- Evidence collection and redaction.
- Structured AI analysis with evaluation cases and cost controls.
- Notifications, incident workflow, and operator feedback loop.

### Phase 5: Advanced platform capabilities

- Promotion approvals, multiple clusters, stronger tenant isolation.
- Capacity-based autoscaling and higher availability where measurements justify it.

## Definition of done for a capability

- Requirement and acceptance behavior are linked.
- Domain rules and failure modes are tested.
- API/event contracts are documented and compatible.
- Authorization and sensitive-data handling are reviewed.
- Metrics, logs, traces, dashboards, and alerts are defined.
- Migration, deployment, rollback, and runbook impact are addressed.
- Relevant design document and ADR are updated.


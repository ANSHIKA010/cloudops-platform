# Requirements

**Status:** Proposed baseline

Requirement IDs are stable references for design, implementation, and tests.
Priorities follow MoSCoW: Must, Should, Could, Won't for the current release.

## Functional requirements

| ID | Priority | Requirement | Acceptance signal |
| --- | --- | --- | --- |
| FR-001 | Must | A user can register and update an application with ownership metadata. | Valid application is persisted and retrievable. |
| FR-002 | Must | A user can create an immutable deployment request containing image, environment, resources, ports, and configuration references. | API returns an ID and initial lifecycle state. |
| FR-003 | Must | The system validates deployment input and environment policy before execution. | Invalid requests fail with field-level or policy-level errors. |
| FR-004 | Must | The system executes a deployment through a provider abstraction. | Simulator execution works without coupling the domain to Kubernetes. |
| FR-005 | Must | A user can retrieve current status and ordered deployment history. | State and transitions are queryable by deployment ID. |
| FR-006 | Must | Deployment lifecycle transitions obey an explicit state machine. | Invalid transitions are rejected and audited. |
| FR-007 | Must | Retried create requests do not create duplicate deployments. | Reusing an idempotency key returns the original result. |
| FR-008 | Should | A user can cancel an eligible in-progress deployment. | Cancellation produces a valid terminal or compensating state. |
| FR-009 | Should | A user can view normalized workload health and recent operational events. | Health is correlated to application and deployment. |
| FR-010 | Should | A user can view bounded, access-controlled workload logs. | Logs support time and workload filters without exposing secrets. |
| FR-011 | Should | A user can restart or scale a workload subject to policy. | Requested action is authorized, tracked, and audited. |
| FR-012 | Should | Failed deployments can be submitted for AI-assisted incident analysis. | Response contains evidence, probable cause, severity, and suggested actions. |
| FR-013 | Should | Operators receive notifications for configured failure conditions. | Delivery attempts and outcomes are traceable. |
| FR-014 | Must | Security- and deployment-relevant actions produce immutable audit events. | Actor, action, target, result, and timestamp are recorded. |
| FR-015 | Must | Users see and modify only resources allowed by role and ownership policy. | Authorization tests cover cross-team access. |
| FR-016 | Could | A release can be promoted between environments with approvals. | Promotion preserves artifact identity and records approval. |
| FR-017 | Could | Operators can manage more than one cluster. | Placement is explicit and tenant-safe. |

## Non-functional requirements

These are initial engineering targets for an MVP-sized installation and must be
revised after load testing and real usage data.

| ID | Quality attribute | Initial target | Verification |
| --- | --- | --- | --- |
| NFR-001 | Availability | API monthly availability target of 99.9%, excluding planned maintenance. | Synthetic probe and SLI dashboard. |
| NFR-002 | API latency | 95% of non-streaming read requests under 300 ms and writes under 500 ms at expected load. | Load test and server-side histogram. |
| NFR-003 | Acceptance | Persist and acknowledge a valid deployment request within 1 second at p95; execution is asynchronous. | End-to-end load test. |
| NFR-004 | Scale | Baseline design supports 100 concurrent users, 1,000 applications, and 10,000 retained deployments. | Capacity test with representative data. |
| NFR-005 | Consistency | Deployment intent and lifecycle transitions are strongly consistent in PostgreSQL. | Transaction and concurrency tests. |
| NFR-006 | Durability | No acknowledged deployment intent is lost after a single application-instance failure. | Failure-injection test. |
| NFR-007 | Recovery | Initial RPO <= 15 minutes and RTO <= 60 minutes for the control-plane database. | Backup restoration exercise. |
| NFR-008 | Security | TLS in transit, encryption at rest in hosted environments, least privilege, and no plaintext secrets in source or logs. | Security review and automated scans. |
| NFR-009 | Auditability | Audit records identify actor, action, resource, outcome, correlation ID, and time. | Audit contract tests. |
| NFR-010 | Observability | Every request and asynchronous operation has correlated logs, metrics, and trace context. | Telemetry integration test. |
| NFR-011 | Accessibility | Dashboard targets WCAG 2.2 AA for core workflows. | Automated and manual accessibility review. |
| NFR-012 | Maintainability | Domain logic remains independent of HTTP, persistence, messaging, and Kubernetes clients. | Architecture tests and code review. |
| NFR-013 | Portability | Local development runs with documented prerequisites and containerized dependencies. | Clean-machine setup test. |
| NFR-014 | Privacy | Sensitive values are redacted; retention and deletion rules are configurable. | Redaction and retention tests. |
| NFR-015 | Cost | Expensive telemetry and AI analysis have retention, quota, and timeout controls. | Usage and cost dashboard. |

## Key business rules

- A deployment belongs to exactly one application and one environment.
- A deployment references an immutable image identifier; production should prefer a digest.
- Lifecycle transitions are explicit and append an audit/history record.
- Terminal deployments cannot return to an active state.
- Only authorized actors can deploy to or operate on an environment.
- AI recommendations are advisory and must identify supporting evidence; they do not silently execute remediation.


# Reliability and Scalability

**Status:** Proposed

## Reliability model

The CloudOps API is a control plane. It must preserve acknowledged intent even
when a worker, provider, or downstream dependency is temporarily unavailable.
Asynchronous execution isolates user-facing request latency from deployment time.

## Failure handling

| Failure | Expected behavior |
| --- | --- |
| API instance stops after acknowledgement | Persisted work is claimed by another worker. |
| Provider times out | Operation remains reconcilable; retry with bounded exponential backoff and jitter. |
| Duplicate request/event | Idempotency or deduplication prevents duplicated state changes. |
| Database unavailable | Reject writes safely; do not claim success; alert on exhausted connection pool. |
| Kafka unavailable | Outbox remains durable and publication resumes after recovery. |
| Analysis service unavailable | Core deployment flow continues; analysis is marked retryable or unavailable. |
| Kubernetes reports unknown state | Reconcile desired and observed state before deciding success or failure. |
| Poison message | Stop infinite retries, preserve context in a dead-letter path, alert an operator. |

## Resilience patterns

- Explicit connection, request, and job timeouts.
- Exponential backoff with jitter and a finite retry budget.
- Circuit breakers only where they reduce cascading failure and are observable.
- Bulkheads for deployment, telemetry, notification, and AI workloads.
- Backpressure and bounded queues; never rely on unbounded in-memory work.
- Reconciliation for ambiguous provider results.
- Graceful shutdown so claimed work is completed or safely released.

## Scalability approach

Start with measured vertical scaling and stateless API replicas. Scale workers
independently by queue depth and operation latency once separated. Keep database
queries indexed and paginated, avoid high-cardinality metrics, and store bulk
logs/metrics outside PostgreSQL.

Before raising NFR-004, run tests for:

- concurrent deployment creation and lifecycle updates;
- hot applications/environments and optimistic-lock contention;
- worker backlog recovery after provider outage;
- log query limits and telemetry-cardinality growth;
- database connection limits as API and worker replicas increase.

## Disaster recovery

- Define backup frequency and retention from the RPO, not convenience.
- Restore backups in an isolated environment on a schedule.
- Version infrastructure and configuration required to recreate the control plane.
- Document ownership, invocation, validation, and communication for recovery.
- Reconcile provider state after database restoration before accepting operations.

## SLI/SLO candidates

- API availability and latency.
- Successful deployment-request acceptance.
- Time from accepted request to terminal provider outcome.
- Work queue age and retry exhaustion rate.
- Reconciliation lag between desired and observed state.
- Notification and incident-analysis success rate.


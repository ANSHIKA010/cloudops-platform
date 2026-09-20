# Observability

**Status:** Proposed

## Objectives

Telemetry must help an operator answer: what failed, who and what were affected,
where time was spent, whether the failure is ongoing, and what action is safe.

## Logs

Use structured logs with timestamp, level, service, environment, correlation ID,
trace ID, operation ID, deployment ID, event type, and stable error code where
relevant. Do not log tokens, passwords, secret values, full environment-variable
sets, or unbounded provider responses.

## Metrics

Initial platform metrics include:

- HTTP request rate, error rate, and duration by route template and status class.
- Deployment operations by type, state, outcome, and environment class.
- Queue depth, oldest-work age, processing duration, retry count, and dead letters.
- Provider request duration, timeout rate, reconciliation lag, and API errors.
- Database pool saturation, query duration, lock contention, and migration status.
- JVM heap, garbage collection, threads, CPU, and process restarts.
- AI-analysis duration, outcome, token/cost estimate, and redaction failures.

Avoid user IDs, deployment IDs, exception messages, URLs, or other unbounded
labels in metrics.

## Distributed tracing

Propagate W3C trace context through HTTP and asynchronous messages. Create spans
around use cases, database calls, event publication/consumption, provider calls,
and analysis requests. Sampling must retain errors and unusually slow operations
without making telemetry cost unbounded.

## Dashboards

1. **Platform overview:** traffic, errors, latency, saturation, availability.
2. **Deployment pipeline:** accepted, queued, running, failed, duration, backlog.
3. **Dependencies:** PostgreSQL, broker, cache, Kubernetes, analysis, notifications.
4. **Business operations:** active applications, deployment outcomes, environment health.
5. **SLOs:** objectives, error-budget consumption, and burn rate.

## Alerting

Alerts must be actionable, severity-based, routed to an owner, and linked to a
runbook. Prefer symptom/SLO alerts over single-host noise. Candidate alerts are
fast error-budget burn, sustained deployment backlog, database saturation,
provider-wide failures, reconciliation lag, and exhausted dead-letter retries.

## Incident-analysis guardrails

The AI feature receives minimized, redacted evidence. Its output is structured,
labels uncertainty, cites evidence identifiers, and distinguishes observation
from inference. Model failures never block core deployment operations. Suggested
commands are never executed without a separate authorized workflow.


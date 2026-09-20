# Deployment and Operations

**Status:** Proposed; local PostgreSQL setup is implemented

## Environments

| Environment | Purpose | Data and access expectations |
| --- | --- | --- |
| Local | Developer feedback with Compose and simulator | Synthetic data; developer-owned |
| Test | Automated integration and contract tests | Ephemeral and isolated |
| Staging | Production-like validation and release rehearsal | Sanitized data; restricted access |
| Production | User workloads | Least privilege, audited access, backups, SLOs |

Environment configuration is externalized and validated at startup. Secrets are
resolved from the environment's secret manager, not committed configuration.

## CI pipeline

Every change should pass:

1. Formatting and static analysis.
2. Unit, architecture, and authorization tests.
3. Integration tests with real PostgreSQL through Testcontainers.
4. API/event contract and migration tests.
5. Dependency, secret, source, image, and infrastructure scans.
6. Reproducible application and container builds with immutable versions.

## CD pipeline

- Promote the same immutable artifact between environments.
- Run backward-compatible migrations before dependent code paths activate.
- Use health, readiness, startup, and graceful-shutdown behavior appropriately.
- Prefer rolling or canary release for the control plane.
- Gate production on policy and required approval.
- Verify telemetry and a smoke test before considering deployment complete.
- Roll back application code quickly; roll forward database migrations safely.

## Kubernetes baseline

- Non-root containers, read-only filesystem where practical, dropped capabilities.
- CPU/memory requests and limits based on measurements.
- Pod disruption budgets and topology spread when availability requires them.
- Network policies and namespace/service-account isolation.
- Minimal Kubernetes RBAC for the provider adapter.
- Autoscaling driven by meaningful saturation or queue metrics.
- Signed images, trusted registries, vulnerability policy, and SBOM generation.

## Required runbooks

- API error-rate or latency breach.
- PostgreSQL unavailable, saturated, or failed migration.
- Deployment queue stalled or rapidly growing.
- Kubernetes provider credentials or connectivity failure.
- Reconciliation reports ambiguous or divergent state.
- Dead-letter replay.
- Secret/key rotation.
- Backup restoration and regional/environment recovery.
- Security incident containment and audit export.

Each runbook names the alert, impact, prerequisites, safe diagnostic commands,
mitigation, rollback, escalation owner, and post-recovery checks.


# Data Design

**Status:** Proposed

## Source of truth

PostgreSQL is authoritative for user-visible configuration, deployment intent,
lifecycle state, and audit metadata. Kubernetes, log storage, and metrics
systems are external sources whose data is normalized or referenced rather than
blindly duplicated.

## Initial domain entities

| Entity | Important fields |
| --- | --- |
| User | ID, external subject, display name, status |
| Team | ID, name, ownership metadata |
| Application | ID, team ID, name, description, repository, timestamps |
| Environment | ID, name, type, cluster ID, policy, approval rules |
| Cluster | ID, provider type, endpoint reference, capabilities, status |
| Deployment | ID, application ID, environment ID, image, specification snapshot, status, version |
| Deployment transition | ID, deployment ID, from/to status, reason, actor, timestamp |
| Operation | ID, target, type, requested by, status, timestamps |
| Incident analysis | ID, deployment ID, evidence references, severity, result, model metadata |
| Audit event | ID, actor, action, resource, outcome, correlation ID, timestamp |
| Outbox event | ID, aggregate, event type, payload, occurred/published timestamps |

## Modeling rules

- Use generated opaque identifiers and never expose database sequence meaning.
- Add optimistic versioning to mutable aggregates to prevent lost updates.
- Store a deployment specification snapshot so history is reproducible.
- Treat image tags as user input; resolve and retain digests where possible.
- Store secret references, never secret values, in deployment specifications.
- Store timestamps in UTC and render them in the user's chosen time zone.
- Use database constraints for invariants that can be expressed structurally.
- Apply Flyway migrations; never rely on schema auto-creation outside tests.

## Consistency and concurrency

Deployment creation, its initial history entry, audit metadata, and any outbox
record belong in one transaction. Workers claim work with bounded leases or
row-locking semantics. Provider calls occur outside long-running database
transactions, and repeated calls must be safe through operation identifiers.

## Retention baseline

Retention is configurable by environment and compliance needs. Initial proposal:

| Data | Retention target |
| --- | --- |
| Deployment metadata and transitions | 1 year |
| Audit events | 1 year, append-only |
| Application logs | 14-30 days depending on environment |
| Metrics | 30 days at full resolution; longer at reduced resolution |
| AI prompts/evidence | Minimum necessary period; redact secrets and personal data |
| Idempotency records | At least the maximum client retry window |

## Backup and migration

- Automated database backups must meet NFR-007.
- Restoration must be tested, not merely configured.
- Migrations are forward-compatible during rolling deployments.
- Destructive schema changes use expand-migrate-contract steps.
- Large data migrations run as observable, resumable jobs.


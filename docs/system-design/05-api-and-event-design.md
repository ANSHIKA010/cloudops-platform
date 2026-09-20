# API and Event Design

**Status:** Proposed

## REST principles

- Resource-oriented paths under `/api/v1`.
- JSON request and response bodies with explicit validation.
- `202 Accepted` for asynchronous commands and `201 Created` for completed creation.
- RFC 9457 problem details for errors, with stable application error codes.
- Cursor-based pagination for large or changing collections.
- ISO 8601 UTC timestamps and opaque resource identifiers.
- Correlation IDs accepted from trusted callers or generated at ingress.
- Backward-compatible changes within a major API version.

## Candidate endpoints

```text
POST   /api/v1/applications
GET    /api/v1/applications/{applicationId}
POST   /api/v1/applications/{applicationId}/deployments
GET    /api/v1/deployments/{deploymentId}
GET    /api/v1/deployments/{deploymentId}/events
POST   /api/v1/deployments/{deploymentId}/cancellation
POST   /api/v1/deployments/{deploymentId}/analysis
GET    /api/v1/operations/{operationId}
```

## Idempotency

State-changing create/command endpoints accept an `Idempotency-Key`. The key is
scoped to the authenticated principal, operation, and target. The server stores
a normalized request hash and original outcome. Reusing a key with different
input returns a conflict; reusing it with identical input returns the original
result. Records expire only after the documented retry window.

## Error contract

An error response should contain:

```json
{
  "type": "https://cloudops.example/problems/invalid-transition",
  "title": "Deployment transition is not allowed",
  "status": 409,
  "code": "DEPLOYMENT_INVALID_TRANSITION",
  "detail": "A RUNNING deployment cannot return to PENDING.",
  "instance": "/api/v1/deployments/dep_123",
  "correlationId": "..."
}
```

Do not expose stack traces, credentials, provider tokens, or raw confidential
configuration in an error response.

## Event envelope

Events describe facts in past tense and include enough metadata for safe replay:

```json
{
  "eventId": "...",
  "eventType": "deployment.status-changed.v1",
  "occurredAt": "2026-09-20T10:00:00Z",
  "aggregateId": "...",
  "aggregateVersion": 3,
  "correlationId": "...",
  "causationId": "...",
  "tenantId": "...",
  "payload": {}
}
```

Delivery is at least once. Consumers deduplicate by event ID, tolerate ordering
within documented boundaries, and route repeatedly failing records to a dead
letter path with alerts and a replay runbook. Sensitive values do not belong in
event payloads.


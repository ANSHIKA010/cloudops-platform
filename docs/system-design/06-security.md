# Security

**Status:** Proposed

## Security objectives

- Only authenticated identities can access non-public capabilities.
- Every operation is authorized for role, team ownership, environment, and action.
- Credentials and workload secrets are not stored or logged in plaintext.
- Tenant/team data is isolated at every query and provider boundary.
- Privileged and security-relevant activity is attributable and auditable.

## Authentication and authorization

Use standards-based OIDC/OAuth 2.0 for interactive users and workload identities
for automation. Validate issuer, audience, signature, lifetime, and token type.
Keep coarse permissions in roles and enforce resource ownership/environment
policy in application use cases. Default to deny.

Candidate roles:

- `VIEWER`: view allowed applications and operational state.
- `DEVELOPER`: create non-production deployments and operate owned applications.
- `OPERATOR`: manage deployments and incidents in assigned environments.
- `ADMIN`: manage platform configuration, identity mappings, and clusters.

Production deployment and remediation should support explicit approval policy
rather than relying on a broad role alone.

## Threat model baseline

| Threat | Primary controls |
| --- | --- |
| Broken object-level authorization | Ownership-aware queries, policy checks, negative authorization tests |
| Malicious image or supply-chain compromise | Registry allowlist, digest pinning, signing/attestation policy, vulnerability scanning |
| Secret exposure | Secret references, managed secret store, redaction, restricted logs |
| Injection | Structured APIs, validation, parameterized queries, no shell interpolation |
| SSRF through provider or webhook input | Destination allowlists, egress policy, URL validation |
| Replay or duplicate commands | Short-lived tokens, idempotency keys, event deduplication |
| Privilege escalation in Kubernetes | Per-environment service accounts, minimal RBAC, admission policy |
| Prompt injection or unsafe AI output | Treat logs as untrusted data, structured output validation, no automatic execution |
| Denial of service | Rate limits, quotas, bounded payloads, timeouts, backpressure |

## Secrets and cryptography

- Use a managed secret store in hosted environments and rotate credentials.
- Encrypt network traffic with TLS and use provider-managed encryption at rest.
- Never include secrets in Git, images, URLs, events, traces, or AI prompts.
- Keep key and certificate rotation documented and observable.
- Mask sensitive configuration in APIs and the dashboard.

## Security assurance

- Dependency, secret, static-analysis, container, and IaC scans in CI.
- Authorization tests for every resource type and cross-team boundary.
- Audit log access is restricted and audit records are tamper-evident where required.
- Threat-model review for new trust boundaries or privileged operations.
- Documented vulnerability reporting, patching, and incident-response process.


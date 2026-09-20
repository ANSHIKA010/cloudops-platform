# Architecture Decision Records

ADRs capture consequential decisions so future contributors understand both the
choice and the trade-offs. ADRs are immutable after acceptance; supersede an old
record with a new one instead of rewriting history.

## Index

- [ADR-0001: Start with a modular monolith](0001-modular-monolith.md)

## Template

Copy this structure into the next numbered Markdown file:

```markdown
# ADR-NNNN: Short decision title

- Status: Proposed | Accepted | Superseded
- Date: YYYY-MM-DD
- Owners: names or team
- Related requirements: FR-NNN, NFR-NNN

## Context

What problem, constraints, and forces require a decision?

## Decision

What did we decide?

## Alternatives considered

What credible alternatives were evaluated and why were they not selected?

## Consequences

What positive, negative, operational, security, and cost consequences follow?

## Validation

How will we know the decision works, and when should it be revisited?
```


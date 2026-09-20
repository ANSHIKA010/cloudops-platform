# Repository Working Agreement

## Artifact persistence

- Store project documentation, plans, ADRs, and other deliverables inside this repository, not only in a Codex-managed worktree or temporary directory.
- Preserve completed project artifacts in Git on a named branch and push that branch to the configured remote when access is available.
- Never overwrite or discard uncommitted user work while moving artifacts into the durable checkout.
- If a project-specific `SKILL.md` is created, keep its source in this repository at a clearly named, user-agreed path and preserve it in Git. Do not leave the only copy in the Codex cache or a temporary workspace.
- Before reporting that preservation is complete, verify the commit, branch, remote tracking state, and clean working tree.

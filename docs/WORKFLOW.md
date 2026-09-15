# Development and Review Workflow

This file defines the approved AI-led implementation workflow with owner-controlled product decisions and Git-based operative memory.

## 1. Guiding principle

The repository must distinguish approved product state, implemented state, accepted/manual state, work in progress, recommendations, unresolved questions, verification actually performed and branch lifecycle.

Never confuse a chat suggestion, old checkpoint, branch name or green CI result with current project truth.

## 2. Current branch model

Lifecycle is controlled by `docs/BRANCH_STATUS.md`.

The owner has authorized integrated-MVP implementation. The former `main` + Player-successor split has been semantically converged and validated at:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

Actions `34917259324` / #1694 — **SUCCESS**.

After promotion, `main` is the normal integrated trunk. The old Player successor remains historical/frozen evidence.

Normal work thereafter uses short-lived outcome-oriented branches from current `main`; shared foundations integrate early; permanent Player/Desktop/Server silos are prohibited unless a later concrete need changes that direction.

## 3. Owner vs technical responsibility

Ask the owner to decide:

- product behavior/workflow;
- UX/game semantics;
- visibility/privacy;
- MVP vs later scope;
- destructive/safety behavior;
- meaningful cost, security/privacy, compatibility or irreversible-lock-in tradeoffs.

Technical agents normally decide and document:

- database/table layout;
- class/type decomposition;
- endpoint/request shapes;
- migration mechanics;
- internal sync structures;
- canonical serialization;
- PDF-rendering internals inside approved behavior;
- testing architecture;
- branch/package granularity;
- reversible implementation conventions/provider mechanics inside approved boundaries.

Do **not** ask the owner to rubber-stamp low-level engineering choices they cannot meaningfully evaluate.

## 4. Communication model

Agents perform technical heavy lifting but explain meaningful work in practical terms:

- what changed;
- why it matters;
- important approach;
- owner-relevant consequences/tradeoffs;
- what was verified;
- known limitations and next action.

Avoid dumping implementation alternatives on the owner when no owner decision is required.

## 5. Product/design state

Current integrated-MVP product definition is closed enough for implementation. Do not reopen foundational product questions merely because coding has begun.

For a genuinely new owner-consequential choice:

1. identify the real product need;
2. explain realistic owner-relevant alternatives;
3. recommend one;
4. obtain owner choice;
5. record it in Git.

For routine technical choices, decide/document without creating ceremonial approval work.

## 6. Work item lifecycle

### A — establish authority

Before implementation:

1. read mandatory continuity files;
2. identify current branch/topology;
3. read `PROJECT_STATE`, `LATEST` and applicable decisions/checkpoints;
4. identify any real owner-action boundary;
5. identify existing implemented/accepted evidence and material unknowns.

Do not resume from historical `next` prose when current docs supersede it.

### B — technical design

Choose the simplest safe design satisfying approved behavior. Reuse proven project patterns, avoid generalized infrastructure without measured need, record durable conventions that later contributors need, and escalate only owner-consequential tradeoffs.

### C — implement

Within current authorization, agents may write/refactor code, create/update tests, change build/configuration, execute checks, diagnose/repair failures and update technical documentation.

Keep batches coherent and outcome-oriented.

### D — verify

Record exact revision/build, commands/checks, passes/failures, untested areas, environment/device type and evidence type (automated/local/emulator/physical owner).

Never describe unexecuted tests as passed or infer owner acceptance from CI.

### E — update operative memory

Before meaningful work is complete, update applicable truth:

- `docs/PROJECT_STATE.md`;
- `docs/checkpoints/LATEST.md` when resume changes;
- `docs/BRANCH_STATUS.md` when topology changes;
- decisions only for genuine product/architecture decisions;
- conventions when durable;
- product/roadmap/architecture/testing docs when operational truth changes;
- checkpoints/evidence as appropriate.

A continuation-critical fact must not remain only in chat.

### F — owner communication

Explain what now works/changed, what was tested, known limitations, any genuine owner/manual gate, branch/revision and exact next action.

Do not ask the owner to review internal minutiae merely to complete a process checkbox.

### G — integrate

Use short-lived outcome branches from current `main`, integrate shared contracts early, preserve coherent/buildable normal merge points, and leave durable evidence at meaningful milestones.

## 7. Current technical direction

The active technical handoff is `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md`.

Current direction includes Ktor shared networking, versioned HTTP/JSON, mutation IDs + revisions, SQLDelight outbox/scoped sync, Neon serverless driver, explicit hosted SQL migrations, JSONB PC snapshots + relational auth/index metadata, Descope token validation + app-owned authorization, R2 recommendation pending activation, versioned JSON import/export, versioned full backup archive and one canonical PC/PDF-export semantic path.

These are engineering choices unless later evidence creates a material owner-level consequence.

## 8. Current verification posture

The integrated convergence baseline has already passed the successor Player guard suite plus aggregate shared tests, Android build, Desktop build and backend type-check.

As hosted implementation becomes real, add focused tests for migrations, authorization, revisions/idempotency/tombstones, sync, assets, backup, PDF completeness and later combat authority. Prefer invariant tests over arbitrary coverage targets.

See `docs/TESTING.md`.

## 9. Failed or partial work

Partial work is acceptable if clearly recorded. State what completed, what remains, exact failure/blocker, relevant branch/commit and exact next action.

Never hide an unfinished migration, failing test or unresolved manual gate behind a generic `in progress` label.

## 10. Current implementation sequence

Implementation is **AUTHORIZED and IN PROGRESS**.

Current sequence:

```text
validated baseline convergence
-> promote to main
-> Shared Integrated-MVP Spine
-> hosted foundation
-> Player <-> hosted end-to-end
-> Desktop/admin/content/live waves
-> combat exchange/handoff
-> SRD clarification
-> backup/operator completion
-> integrated owner QA
```

Routine packages proceed without owner rubber-stamping. Return to the owner only for material product/scope/security/privacy/cost/lock-in/destructive-behavior decisions, required external account/service actions or manual/physical QA gates.

## 11. Secrets and credentials

Never commit passwords, tokens, API keys, database credentials, production/release signing keys, private certificates or other secrets.

Use secure local/CI/provider secret storage. The sole-admin Desktop may later hold scoped provider credentials locally when useful, but only through suitable OS/protected credential storage, never plaintext tracked files.

# Development and Review Workflow

This file defines the approved AI-led implementation workflow with owner-controlled product decisions and Git-based operative memory.

## 1. Guiding principle

The repository must distinguish approved product state, implemented state, accepted/manual state, work in progress, recommendations, unresolved questions, verification actually performed and branch lifecycle.

Never confuse a chat suggestion, old checkpoint, branch name or green CI result with current project truth.

## 2. Branch model

Lifecycle is controlled by `docs/BRANCH_STATUS.md`.

`main` is the sole normal integrated trunk. Normal work uses short-lived outcome-oriented branches from current `main`; shared foundations integrate early; permanent Player/Desktop/Server silos are prohibited unless a later concrete need changes that direction.

## 3. Owner vs technical responsibility

Ask the owner to decide product behavior/workflow, UX/game semantics, visibility/privacy, MVP scope, destructive/safety behavior and meaningful cost/security/privacy/compatibility/irreversible-lock-in tradeoffs.

Technical agents normally decide and document database/table layout, class/type decomposition, endpoint/request shapes, migrations, internal sync structures, serialization, rendering internals, test architecture, branch/package granularity and reversible provider mechanics inside approved boundaries.

Do not ask the owner to rubber-stamp low-level engineering choices.

## 4. Communication model

Agents perform technical heavy lifting but explain meaningful work in practical terms: what changed, why it matters, the important approach, owner-relevant consequences/tradeoffs, what was verified, known limitations and the exact next action.

## 5. Product/design state

Current integrated-MVP product definition is closed enough for implementation. Do not reopen foundational product questions merely because coding has begun.

For a genuinely new owner-consequential choice, explain realistic alternatives, recommend one, obtain the owner's choice and record it in Git. For routine technical choices, decide/document without ceremonial approval work.

## 6. Work item lifecycle

### A — establish authority

Before implementation:

1. read mandatory continuity files;
2. identify current branch/topology;
3. read `PROJECT_STATE`, `LATEST` and applicable decisions/checkpoints;
4. identify real owner-action/external-provider boundaries;
5. identify existing implemented/accepted evidence and material unknowns.

Do not resume from historical `next` prose when current docs supersede it.

### B — technical design

Choose the simplest safe design satisfying approved behavior. Reuse proven project patterns and avoid generalized infrastructure without measured need.

### C — implement

Within authorization, agents may write/refactor code, create/update tests, change build/configuration, execute checks, diagnose/repair failures and update technical documentation.

Keep batches coherent and outcome-oriented.

### D — verify

Record exact revision/build, commands/checks, passes/failures, untested areas, environment/device type and evidence type. Never describe unexecuted tests as passed or infer owner acceptance from CI.

### E — update operative memory

Before meaningful work is complete, update applicable truth in `PROJECT_STATE`, `LATEST`, `BRANCH_STATUS`, decisions/conventions and relevant roadmap/architecture/testing/checkpoint files. A continuation-critical fact must not remain only in chat.

### F — owner communication

Explain what now works/changed, what was tested, known limitations, any genuine owner/manual/external gate, branch/revision and exact next action.

### G — integrate

Use short-lived outcome branches, preserve coherent/buildable merge points, verify expected heads and leave durable evidence at meaningful milestones.

## 7. External-provider handoff protocol

Cloudflare, Descope, Neon and other authenticated providers require an explicit capability check.

### If the agent has authenticated provider capability

Proceed only within the already approved scope/security/cost boundary. Do not enable paid/overage resources or expose secrets.

### If the agent does not have authenticated provider capability

Do **not** keep trying alternate access methods after the limitation is established. Do not convert the work item into a long sequence of speculative provider probes.

Instead:

1. finish all safe repository/CI work that does not require provider authentication;
2. identify the **first** unavoidable owner-side provider action;
3. prepare one owner-action packet containing:
   - what the action does and why it is needed;
   - exact ordered commands/clicks;
   - expected output/visible result;
   - secrets/values that must not be pasted into chat or Git;
   - the non-secret evidence the owner should return;
   - stop/error conditions;
4. stop at that boundary;
5. after the owner returns evidence, validate it and continue without repeating completed investigation;
6. if another inaccessible provider action is later required, stop again and create the next bounded handoff.

Prefer **one external handoff per task**. A task may contain substantial repository work; it does not need to be artificially split into tiny steps merely because an external handoff exists.

Use `docs/recovery/EXTERNAL_PROVIDER_HANDOFF_PROMPT.md` when handing continuation to a fresh worker/chat.

## 8. Current technical direction

The active technical architecture includes Ktor shared networking, versioned HTTP/JSON, mutation IDs + revisions, SQLDelight outbox/scoped sync, Neon PostgreSQL, explicit hosted SQL migrations, JSONB PC snapshots + relational auth/index metadata, Descope identity proof + application-owned authorization, versioned JSON import/export, full backup direction and one canonical PC/PDF-export semantic path.

Object storage remains deferred until real asset integration requires it.

## 9. Verification posture

Use focused tests plus Scaffold/integration/manual evidence appropriate to the risk. Prefer invariant tests over arbitrary coverage targets. Real-provider behavior requires real-provider evidence; green repository CI cannot substitute for deployment/integration evidence or owner physical/visual QA.

See `docs/TESTING.md`.

## 10. Failed or partial work

Partial work is acceptable if clearly recorded. State what completed, what remains, exact failure/blocker, relevant branch/commit and exact next action.

Never hide an unfinished migration, failing test or unresolved manual/provider gate behind generic `in progress` wording.

## 11. Current implementation sequence

Implementation is **AUTHORIZED and IN PROGRESS**.

Current broad sequence:

```text
completed convergence/shared/hosted foundations
-> completed Wave 4 Player <-> Server
-> Wave 5 Desktop/admin work (ACTIVE)
-> content/authoring Managers
-> DM live workspace
-> combat exchange/handoff
-> SRD clarification
-> backup/operator completion
-> integrated owner QA
```

The current detailed continuation is controlled by `docs/checkpoints/LATEST.md`.

## 12. Secrets and credentials

Never commit or request passwords, OTPs, tokens, API keys, database credentials, production/release signing keys, private certificates or other secrets.

Use secure local/CI/provider secret storage. Owner-side commands should reference locally stored credentials/environment variables without revealing values in chat output.

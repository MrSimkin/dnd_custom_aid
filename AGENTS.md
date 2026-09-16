# AGENTS.md — Mandatory Project Operating Rules

This file applies to every human contributor, ChatGPT conversation, coding agent, autonomous agent or other AI working in this repository.

## 1. Repository authority and operative memory

The repository is the project's durable source of truth **and operative memory**. Do not rely on chat memory, hidden context or previous conversations to determine current project state; those are clues only.

The owner authorized integrated-MVP implementation on 2026-09-14 (Chile local time). `main` is the single normal integrated-MVP trunk. Historical Player/convergence branches remain evidence, not normal resume points.

`docs/BRANCH_STATUS.md` controls branch lifecycle. `docs/PROJECT_STATE.md` controls current global state. `docs/checkpoints/LATEST.md` controls the practical resume point. Any continuation-critical fact must be written to Git.

## 2. Mandatory read order before substantial work

Read, in order:

1. `README.md`;
2. `AGENTS.md`;
3. `MANIFEST.md`;
4. `docs/PROJECT_STATE.md`;
5. `docs/checkpoints/LATEST.md`;
6. the current checkpoint referenced by `LATEST.md`;
7. `docs/BRANCH_STATUS.md`;
8. `docs/DECISIONS.md` + `docs/DECISIONS_RECENT.md` + relevant detailed decisions;
9. `docs/CONVENTIONS.md`;
10. `docs/PRODUCT.md`;
11. `docs/ROADMAP.md`;
12. `docs/WORKFLOW.md`;
13. `docs/ARCHITECTURE.md`;
14. `docs/TESTING.md`;
15. current feature/checkpoint files relevant to the task.

Historical checkpoints remain evidence, but their old `next` instructions may be superseded.

## 3. Branch interpretation

- `main` = sole normal integrated-MVP trunk;
- current work uses short-lived outcome-oriented branches from current `main`;
- old Player successor, convergence and frozen QA refs are historical evidence unless a newer checkpoint explicitly reactivates them;
- branch existence alone never establishes authority.

Do not create permanent Player/Server/Desktop silos or resume historical repair cycles without new evidence.

## 4. Owner authority and working relationship

The owner decides consequential matters that genuinely require product ownership, including product behavior, workflow/UX, game semantics, privacy/visibility, MVP scope, destructive behavior and meaningful cost/security/compatibility/lock-in tradeoffs.

Technical agents own routine implementation details such as schema layout, class/type decomposition, endpoint/request shapes, migration mechanics, internal sync structures, serialization, rendering internals, tests and bounded branch/package granularity.

Do not ask the owner to rubber-stamp ordinary engineering.

## 5. Explain technical work

Meaningful technical work must remain understandable to the owner. Explain proportionately:

- what changed;
- why;
- the practical technical approach;
- meaningful consequences/tradeoffs;
- verification performed;
- remaining limitations and exact next action.

Use representative SQL when it materially improves understanding of relational behavior.

## 6. Proportionality

This is a personal, deliberately limited project. Prefer the simplest safe implementation that satisfies actual approved requirements. Do not import enterprise/SaaS machinery without a concrete need.

When a recurring technical convention becomes necessary and none exists, choose the simplest maintainable option consistent with approved architecture, record it when continuity requires it, and escalate only meaningful product/cost/security/privacy/compatibility consequences.

## 7. Approved product/architecture baseline

Current integrated direction includes:

- one Player + hosted/shared + DM ecosystem;
- paper-first Player play;
- project-specific local-first sync with stable IDs/revisions/idempotency/tombstones/conflicts;
- complete DM Live capability on Android/tablet and Desktop;
- rich Desktop authoring/management/admin surfaces;
- explicit single-authority combat resume/handoff;
- object storage, meaningful audit/recovery and full backup/export;
- official-SRD-only grounded clarification in MVP;
- complete cross-surface PC Sheet PDF export.

Do not silently trim approved MVP capabilities merely to finish sooner or reopen settled product decisions without new evidence.

## 8. No silent invention or status inflation

Distinguish clearly:

- **Approved** — explicitly accepted direction;
- **Proposed/Recommended** — awaiting a real owner decision;
- **Pending** — genuinely requires future action;
- **Implemented** — present in code and verified at the stated level;
- **Accepted** — passed the required owner/manual gate where one exists.

Do not infer `Accepted` from green CI, branch location or repository implementation.

## 9. Change workflow

For substantial work:

1. verify branch/topology;
2. read current authority files/checkpoint;
3. identify material unknowns and real owner-action boundaries;
4. make routine technical decisions autonomously;
5. implement the smallest coherent batch;
6. run focused + aggregate checks appropriate to risk;
7. update operative-memory documentation;
8. leave durable Git evidence;
9. summarize verification and the exact next action.

Integrated-MVP implementation is already authorized. Do not invent a new owner approval gate for each technical package.

## 10. External-provider capability boundary — mandatory

Provider/account work must never become an unbounded agent retry loop.

When a task reaches Cloudflare, Descope, Neon or another external provider:

1. determine whether the current execution environment has **actual authenticated capability** to perform the required provider action;
2. if it does, proceed only within the already authorized security/cost/scope boundary;
3. if it does **not**, stop attempting alternate connection paths once that limitation is established;
4. complete all safe repository/CI preparation first;
5. produce one exact owner-action packet containing: what must be done, why, ordered commands/clicks, expected result, secrets that must not be pasted, safe evidence to return, and explicit stop/error conditions;
6. the owner performs the authenticated provider action locally/in the provider UI;
7. resume only from the returned non-secret evidence and do not repeat already completed investigation;
8. stop again at the next unavoidable external-provider action rather than chaining multiple inaccessible provider tasks.

Do not request passwords, OTPs, API keys, deployment tokens, database credentials, session/refresh JWTs or other secrets in chat or Git.

The reusable prompt for this workflow is `docs/recovery/EXTERNAL_PROVIDER_HANDOFF_PROMPT.md`.

## 11. Definition of done and continuity

A batch is not complete merely because code exists. As applicable it must have implementation, relevant checks, appropriate behavior verification, current-state docs, consequential decisions/conventions, visible unresolved items and an exact next action.

`docs/checkpoints/LATEST.md` must be refreshed when practical continuation changes.

## 12. Technical quality, credentials and signing material

Prefer maintainable, readable, testable code over clever code. Keep dependencies justified and proportional.

Never commit passwords, API tokens, database/provider credentials, access/refresh/session tokens, production/release signing keys, private certificates or other confidentiality-dependent material.

Development-only Android debug signing exists only for stable CI/QA APK updates and is not a production trust boundary.

## 13. Recovery from inconsistency

If documents disagree:

1. do not guess silently;
2. prefer the most specific later approved decision/clarification over older prose;
3. prefer newer explicitly dated current-state documentation when authority is otherwise equal;
4. `docs/BRANCH_STATUS.md` controls branch lifecycle;
5. `docs/PROJECT_STATE.md` controls current state;
6. `docs/checkpoints/LATEST.md` controls practical continuation;
7. the current checkpoint controls its specific milestone/provider evidence;
8. historical checkpoints remain evidence only;
9. repair stale governance when it could misdirect future work.

Ask the owner only for a genuinely material unresolved product/scope/risk ambiguity.

## 14. Current project stage

Integrated-MVP implementation is **IN PROGRESS**.

Current authoritative position as of 2026-09-16:

- Wave 4 Player <-> Server is complete/integrated for its recorded scope;
- Wave 5 Desktop workbench/local campaign is complete, merged and owner-QA accepted;
- Wave 5 hosted Campaign membership administration core is complete and merged through PR #43;
- current focused branch: `wave5/desktop-hosted-campaign-administration`;
- current draft PR: #44;
- bounded PR #44 repository implementation is complete/CI-verified;
- the next substantive gate is explicit real DEV Worker deployment/integration, followed by owner Windows Desktop QA;
- repository implementation must not be described as real provider verification until that evidence exists.

Do not restart provider activation, Android hosted-session work, completed Wave 4 packages, PR #42 or PR #43.

Use `docs/checkpoints/LATEST.md`, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md` and the current checkpoint for exact hashes/runs and the practical continuation.

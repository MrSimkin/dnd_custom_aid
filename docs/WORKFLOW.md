# Development and Review Workflow

This file defines the approved operating workflow for AI-led implementation with owner-controlled product decisions and Git-based operative memory.

## 1. Guiding principle

The repository must always distinguish:

- approved/accepted project state;
- implemented but not owner-accepted state;
- work in progress;
- proposed/recommended decisions;
- unresolved questions;
- verification actually performed;
- branch role/lifecycle.

The goal is to prevent a future agent from confusing an experiment, chat suggestion, historical checkpoint, branch name, green CI result or remembered discussion with current project truth.

## 2. Current branch model

Current lifecycle is controlled by `docs/BRANCH_STATUS.md`.

Before the planned convergence there are two active authoritative lines:

- `main` — global integrated-MVP product/design/architecture/governance truth;
- `implementation/phase4a-successor-cycle` — current Player runtime/QA authority.

D-0073 defines the approved convergence direction. After explicit owner implementation authorization:

1. create a dedicated convergence branch from current `main`;
2. deliberately reconcile the Player successor runtime;
3. preserve valid Player runtime/migrations/tests/evidence from the successor;
4. preserve later integrated product/architecture/governance truth from `main`;
5. reconcile shared CI/navigation intentionally;
6. validate;
7. merge the coherent baseline to `main`;
8. use `main` as the normal integrated trunk thereafter.

Do not force-move either current line or merge merely for cosmetic linearity.

## 3. Owner vs technical responsibility

D-0073 is controlling.

### Ask the owner to decide

- product behavior and workflow;
- UX/game semantics;
- visibility/privacy expectations;
- MVP vs later scope;
- user-facing destructive/safety behavior;
- meaningful cost, security/privacy, compatibility or irreversible-lock-in tradeoffs.

### Technical agents normally decide and document

- database/table layout;
- class/type decomposition;
- endpoint/request shapes;
- migration mechanics;
- internal sync structures;
- canonical import serialization;
- testing architecture;
- branch/package granularity;
- reversible implementation conventions;
- provider-specific mechanics inside already-approved service/cost/security boundaries.

Do **not** ask the owner to rubber-stamp low-level engineering choices they cannot meaningfully evaluate.

Escalate only when a technical choice materially changes product behavior, cost, risk, privacy/security, irreversible lock-in or approved scope.

## 4. Communication model

Agents perform the technical heavy lifting, but meaningful work must remain understandable to the owner.

Explain in practical terms:

- what changed;
- why it matters;
- the important approach;
- owner-relevant consequences/tradeoffs;
- what was actually verified;
- known limitations and the next action.

Avoid dumping implementation alternatives on the owner when the alternatives do not require an owner decision.

## 5. Product/design before consequential architecture

The product/design and integrated-MVP boundary are now closed under D-0071/D-0072/D-0073.

Future work should not reopen foundational product questions merely because implementation begins.

For a genuinely new owner-consequential choice:

1. identify the product need;
2. explain realistic owner-relevant alternatives;
3. recommend one;
4. obtain owner choice;
5. record it in Git.

For routine technical decisions, choose/document them without creating ceremonial approval work.

## 6. Work item lifecycle

### Step A — establish authority

Before implementation:

1. read mandatory continuity files;
2. identify active branch/topology from `docs/BRANCH_STATUS.md`;
3. read `docs/PROJECT_STATE.md`, `docs/checkpoints/LATEST.md` and applicable decisions/checkpoints;
4. identify the actual authorization boundary;
5. identify existing approved/implemented/accepted evidence;
6. identify material unknowns.

Do not start from historical branch/checkpoint prose merely because it says `next`.

### Step B — technical design

For low-level engineering:

1. choose the simplest safe design satisfying approved behavior;
2. reuse proven project patterns where they fit;
3. avoid generalized infrastructure without measured need;
4. record durable technical conventions/assumptions when future contributors need them;
5. escalate only owner-consequential tradeoffs.

### Step C — implement

After authorization is clear, the coding agent may:

- write/refactor code within approved behavior;
- create/update tests;
- update build/configuration;
- execute available checks;
- diagnose and repair failures;
- update technical documentation.

Keep batches coherent and outcome-oriented. Do not bundle unrelated behavior merely to reduce commit count.

### Step D — verify

Record:

- exact revision/build;
- commands/checks executed;
- what passed;
- what failed;
- what was not tested and why;
- environment/device type;
- whether evidence is automated, local integration, emulator/simulator or physical owner/device.

Never describe unexecuted tests as passed or infer owner acceptance from CI.

### Step E — update operative memory

Before meaningful work is complete, update applicable truth:

- `docs/PROJECT_STATE.md`;
- `docs/checkpoints/LATEST.md` when resume changes;
- `docs/BRANCH_STATUS.md` when topology/lifecycle changes;
- decision records only when a genuine decision changes;
- `docs/CONVENTIONS.md` for durable conventions where useful;
- product/roadmap/architecture/testing docs when operational truth changes;
- feature/checkpoint evidence as appropriate.

A fact needed for continuation must not remain only in chat.

### Step F — owner review

Explain:

- what now works or what project understanding changed;
- what was tested;
- known limitations;
- any genuine owner decision/manual gate;
- branch/revision containing the work;
- exact next action.

Do not ask the owner to review internal technical minutiae merely to complete a process checkbox.

### Step G — publish/integrate

Before convergence:

- current Player runtime fixes/evidence belong on the Player successor only within its valid authority;
- global readiness/governance belongs on `main`;
- convergence itself requires explicit implementation authorization.

After successful convergence:

- `main` becomes the integrated trunk;
- use short-lived outcome-oriented branches;
- integrate shared foundations early;
- avoid permanent Player/Desktop/Server silos;
- keep normal `main` merge points coherent/buildable.

## 7. Current technical implementation direction

`docs/checkpoints/2026-09-14_INTEGRATED_MVP_TECHNICAL_READINESS_REVIEW.md` records the delegated technical recommendations established before implementation, including:

- Ktor Client for shared native networking;
- small versioned HTTP/JSON API;
- client mutation IDs + optimistic revisions;
- SQLDelight outbox + scoped project-specific sync;
- Neon serverless driver initially from Cloudflare Worker;
- explicit hosted SQL migrations;
- versioned JSONB PC snapshot + relational authorization/index metadata;
- Descope client authentication plus server-side token validation;
- R2 Standard as the preferred first object storage, pending owner/service activation;
- versioned JSON canonical import/export documents;
- on-demand versioned full backup archive with manifest/integrity information.

These remain engineering choices unless later evidence creates a material owner-level consequence.

## 8. Current verification posture

The authoritative Player successor CI includes permanent guard scripts plus aggregate shared tests, Android build, Desktop build and backend type-check.

The integrated baseline must preserve those guards.

As hosted implementation becomes real, add focused tests for migrations, authorization, revisions/idempotency/tombstones, sync, backup, assets and later combat authority rather than chasing generic coverage percentages.

See `docs/TESTING.md`.

## 9. Failed or partial work

Partial work is acceptable if clearly recorded.

If work cannot be completed:

- state what completed;
- state what remains;
- record exact failure/blocker;
- preserve relevant branch/commit;
- give exact next action.

Never hide an unfinished migration, failing test, uncertain behavior or unresolved manual gate behind a generic `in progress` label.

## 10. Current authorization gate

Product design, exact MVP scope, implementation governance and technical readiness are complete enough to start the integrated build.

The owner has **not yet authorized product-code implementation** merely by requesting technical review.

The next genuine owner decision is:

> authorize the integrated-MVP implementation, starting with the protected `main` + Player successor convergence.

Once authorized, routine low-level technical packages proceed without owner rubber-stamping. Return to the owner only for material product/scope/security/cost choices or required external account/service actions.

## 11. Secrets and credentials

Never commit passwords, tokens, API keys, database credentials, production/release signing keys, private certificates or other secrets.

Use placeholders in docs/config and secure local/CI secret storage.

The sole-admin Desktop may later keep scoped provider credentials locally when useful, but only using suitable OS/local protected credential storage; never plaintext tracked files.
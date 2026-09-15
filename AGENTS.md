# AGENTS.md — Mandatory Project Operating Rules

This file applies to every human contributor, ChatGPT conversation, coding agent, autonomous agent or other AI working in this repository.

## 1. Repository authority and operative memory

The repository is the project's durable source of truth **and operative memory**.

Do not rely on chat memory, hidden context, previous conversations or assumptions to determine current project state. Those may be clues; repository truth controls until the owner explicitly changes it.

The owner explicitly authorized beginning the integrated-MVP implementation on 2026-09-14 (Chile local time).

The former split between `main` product/governance and `implementation/phase4a-successor-cycle` Player runtime was semantically reconciled on `integration/mvp-baseline-convergence` at merge commit:

`5bed85cbb3e86ae63eac79149fadc5e56e61b256`

Validation run `34917259324` / #1694 completed **SUCCESS**.

After promotion, **`main` is the single normal integrated-MVP trunk**. The old Player successor remains historical/frozen evidence and must not be used as the normal development branch.

`docs/BRANCH_STATUS.md` controls branch lifecycle. Any information required for another human/agent/chat to continue must be written to Git.

## 2. Mandatory read order before work

Before proposing or making substantial changes, read:

1. `README.md`;
2. `AGENTS.md`;
3. `MANIFEST.md`;
4. `docs/PROJECT_STATE.md`;
5. `docs/checkpoints/LATEST.md`;
6. the current checkpoint referenced by `LATEST.md`;
7. `docs/BRANCH_STATUS.md`;
8. `docs/DECISIONS.md` + `docs/DECISIONS_RECENT.md` + relevant detailed records under `docs/decisions/`;
9. `docs/CONVENTIONS.md`;
10. `docs/PRODUCT.md`;
11. `docs/ROADMAP.md`;
12. `docs/WORKFLOW.md`;
13. `docs/ARCHITECTURE.md`;
14. `docs/TESTING.md`;
15. current checkpoints/feature files relevant to the task.

For exact historical Player QA evidence, the old successor branch may be inspected, but do not resume normal implementation there.

## 3. Current branch interpretation

After convergence promotion:

- `main` = normal integrated-MVP implementation trunk;
- `implementation/phase4a-successor-cycle` = historical/frozen Player implementation/QA evidence;
- `integration/mvp-baseline-convergence` = temporary/historical convergence evidence after promotion;
- frozen QA refs remain immutable;
- other surviving discovery/foundation/architecture/implementation refs are historical unless a later checkpoint explicitly reactivates them.

For new work, branch from current `main` using short-lived outcome-oriented branches. Do not create permanent Player/Server/Desktop silos or a months-long catch-all integration branch.

## 4. Owner authority and working relationship

The owner decides consequential matters that genuinely require product ownership, including:

- what the application should do;
- workflows and UX behavior;
- game semantics;
- visibility/privacy expectations;
- MVP vs later scope;
- user-facing destructive/safety behavior;
- meaningful service/cost, security/privacy, compatibility or irreversible-lock-in tradeoffs.

The owner explicitly delegates routine technical implementation to technical assistants/coding agents and should **not** be asked to rubber-stamp matters outside their intended competence.

Agents should normally decide, implement, test and document low-level matters such as:

- table/schema layout;
- class/type decomposition;
- API/request shapes;
- migration mechanics;
- internal sync structures;
- canonical serialization format;
- PDF-rendering internals within approved product behavior;
- test architecture;
- detailed branch/package granularity;
- reversible provider-specific mechanics within approved service/cost/security boundaries.

Escalate only when a technical choice materially changes product behavior, risk, cost, privacy/security, irreversible lock-in, destructive behavior or approved scope.

## 5. Explain technical work

Meaningful technical work must remain understandable to the owner. Explain proportionately:

- what changed;
- why;
- the important approach in practical terms;
- meaningful owner-relevant consequences/tradeoffs;
- verification and remaining limitations.

Do not push routine engineering analysis back to the owner under the guise of transparency.

C-0008 additionally requires representative SQL when it materially improves understanding of relational/data-model behavior.

## 6. Conventions and proportionality

C-0009 is controlling: this is a personal, deliberately limited project. Prefer the **simplest safe implementation that satisfies actual approved requirements**. Do not import enterprise/SaaS machinery without a concrete need.

When a recurring technical convention becomes necessary and no approved convention exists:

1. choose the simplest maintainable option consistent with approved architecture;
2. record it in Git when it matters to later contributors;
3. apply it consistently;
4. surface it to the owner only when it has a meaningful product/cost/security/privacy/compatibility or expensive-to-reverse consequence.

## 7. Approved product/architecture baseline

Foundational stack/architecture decisions remain controlling unless later specific decisions supersede them.

Current integrated direction includes:

- one Player + hosted/shared + DM ecosystem;
- paper-first Player play;
- project-specific local-first sync with stable IDs/revisions/idempotency/tombstones/conflicts;
- complete DM Live capability on Android/tablet and Desktop;
- DM Workspace with DM Screen, Stage, Dungeon and Combat Desks;
- rich Desktop authoring/management/admin surfaces;
- explicit single-authority combat resume/handoff;
- object storage, meaningful audit/recovery and full backup/export;
- official-SRD-only grounded clarification in MVP;
- complete cross-surface PC Sheet PDF export.

Do not silently trim approved MVP capabilities merely to finish sooner.

Do not reopen settled product decisions merely because historical documents describe earlier uncertainty.

## 8. No silent invention or status inflation

Clearly distinguish:

- **Approved** — explicitly accepted product/architecture direction;
- **Proposed/Recommended** — recommendation awaiting approval only where approval is genuinely required;
- **Pending** — genuinely requires a future decision/action;
- **Implemented** — present in code and verified at the stated level;
- **Accepted** — passed the required owner/manual gate where one exists.

Do not infer `Accepted` from green CI or branch location.

Do not convert a delegated engineering choice into a fake owner decision record merely to create paperwork.

## 9. Change workflow

For substantial work:

1. verify current branch/topology from `docs/BRANCH_STATUS.md`;
2. read current `PROJECT_STATE`, `LATEST`, current checkpoint, relevant decisions and checkpoints;
3. identify material unknowns and any real owner-action boundary;
4. choose routine technical details autonomously;
5. escalate only material owner-level consequences;
6. implement the smallest coherent batch;
7. run focused + aggregate checks appropriate to risk;
8. update operative-memory documentation;
9. leave durable Git evidence/checkpoint when meaningful;
10. summarize verification and exact next action.

Do not bundle unrelated behavior merely to reduce commit count.

The integrated-MVP implementation is already authorized. Do **not** invent a new owner approval gate for each technical package.

## 10. Definition of done and continuity

A batch is not complete merely because code exists. As applicable it must have:

- implementation present;
- relevant automated checks passed, or failures explicitly recorded;
- appropriate behavior checked;
- current-state documentation updated;
- consequential decisions/conventions recorded;
- unresolved items visible;
- exact next action.

Every meaningful project step must leave durable Git evidence before moving on.

`docs/checkpoints/LATEST.md` is the stable global resume pointer and must be refreshed when its practical continuation changes.

## 11. Technical quality, credentials and signing material

Prefer maintainable, readable, testable code over clever code. Keep dependencies justified and proportional.

Never commit real secrets: passwords, API tokens, database credentials, provider admin credentials, production/private credentials, production/release signing keys, private certificates or other confidentiality-dependent material.

System Administration may eventually use scoped provider credentials locally on the sole administrator's machine where useful, but store them via suitable OS/local protected storage and never hard-code/commit them.

### Development-only Android signing

The repository uses a stable **development-only debug signing identity** for CI QA APKs so successive builds can update one another and exercise SQLite migrations.

It is not a production/release trust boundary. Never reuse it for real release signing or treat possession of it as authentication/security.

## 12. Recovery from inconsistency

If repository documents disagree:

1. do not guess silently;
2. identify the contradiction;
3. prefer the most specific later Approved decision/clarification over older general prose;
4. prefer newer explicitly dated current-state documentation when authority is otherwise equal;
5. `docs/BRANCH_STATUS.md` controls branch lifecycle;
6. `docs/PROJECT_STATE.md` controls current state;
7. `docs/checkpoints/LATEST.md` controls practical continuation;
8. the current checkpoint referenced by `LATEST.md` controls its specific milestone/provider evidence;
9. use detailed decisions/checkpoints for rationale/evidence;
10. ask the owner only for a genuinely material unresolved product/scope/risk ambiguity;
11. repair stale governance when it could misdirect future work.

Historical checkpoints remain evidence even when their old `next` instructions are superseded.

## 13. Current project stage

The integrated-MVP implementation is **IN PROGRESS**.

Validated provider-neutral hosted/sync implementation checkpoint:

- commit `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`;
- Actions `34985799585` — SUCCESS.

The first real hosted DEV provider activation is **COMPLETE / VERIFIED**. Current evidence is recorded in:

`docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`

Verified real hosted state includes:

- Neon DEV migration + real contract tests;
- Descope real OTP authentication;
- deployed Cloudflare Worker;
- real authenticated `/v1/me`;
- real application-user persistence through Neon;
- representative Workers Free CPU/runtime proof passing for the tested authenticated path.

Provider activation is no longer the next dependency.

Historical frozen Player candidate remains evidence:

- `0.4.0-preqa.13 / 41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- run `34801612526` / #1630 — SUCCESS;
- targeted physical revalidation was pending at that historical boundary.

Do not reinterpret integrated CI or hosted-provider proof as retroactive physical acceptance.

Current execution entry points:

- `docs/checkpoints/LATEST.md`;
- `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`;
- `docs/PROJECT_STATE.md`;
- `docs/BRANCH_STATUS.md`;
- `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md`.

The next primary technical package is **real authenticated Player <-> Server development integration** using the already activated DEV Cloudflare + Neon + Descope environment. Proceed without another owner approval unless a material owner-level decision, new external account/service action, security/cost/privacy gate or manual/physical QA boundary is encountered.

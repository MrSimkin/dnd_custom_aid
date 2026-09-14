# AGENTS.md — Mandatory Project Operating Rules

This file applies to every human contributor, ChatGPT conversation, coding agent, autonomous agent or other AI working in this repository.

## 1. Repository authority and operative memory

The repository is the project's durable source of truth **and its operative memory**.

Do not rely on chat memory, hidden context, previous conversations or assumptions to determine current project state. Those may be clues; repository truth controls until the owner explicitly changes it.

Until the planned convergence is executed there are **two active authoritative lines with different roles**:

- `main` — canonical global navigation plus current integrated-MVP product/architecture/governance decisions;
- `implementation/phase4a-successor-cycle` — authoritative current Player/Phase 4A runtime, repair and QA line.

`main` is therefore not yet the latest Player runtime. Do not collapse these valid lines or infer that one supersedes all content on the other. `docs/BRANCH_STATUS.md` is the canonical lifecycle map.

D-0073 defines the intended transition: after explicit implementation authorization, create a dedicated convergence branch from current `main`, deliberately reconcile the authoritative Player successor runtime, validate it, then merge the coherent integrated baseline to `main`. After that successful convergence, `main` becomes the normal integrated-MVP trunk and the old Player successor becomes frozen historical evidence.

Any information needed for another chat, AI, agent or human to continue must be written to Git.

## 2. Mandatory read order before work

Before proposing or making changes, read:

1. `README.md`;
2. `AGENTS.md`;
3. `MANIFEST.md`;
4. `docs/PROJECT_STATE.md`;
5. `docs/checkpoints/LATEST.md`;
6. `docs/BRANCH_STATUS.md`;
7. `docs/DECISIONS.md` plus `docs/DECISIONS_RECENT.md` and relevant detailed records under `docs/decisions/`;
8. `docs/CONVENTIONS.md`;
9. `docs/PRODUCT.md`;
10. `docs/ROADMAP.md`;
11. `docs/WORKFLOW.md`;
12. `docs/ARCHITECTURE.md`;
13. `docs/TESTING.md`;
14. current checkpoints/feature files relevant to the task.

For current Player source/evidence before convergence, switch to `implementation/phase4a-successor-cycle` and re-read that branch's `docs/checkpoints/LATEST.md`, `docs/PROJECT_STATE.md`, `docs/BRANCH_STATUS.md` and relevant checkpoints before touching Player runtime code.

Do not start implementation from a historical branch merely because it has a newer-looking name or a frozen QA label.

## 3. Current branch interpretation

Before convergence:

- `main` = global integrated-MVP product/design/architecture/governance truth;
- `implementation/phase4a-successor-cycle` = current Player runtime/QA authority;
- all other surviving discovery/foundation/architecture/implementation branches are historical milestone or audit evidence unless a later checkpoint changes their status;
- frozen QA refs remain immutable.

Do not force-move either active line over the other.

When convergence is explicitly authorized, use the semantic precedence from D-0073 and the current technical-readiness checkpoint:

- Player runtime, SQLDelight migrations, Player tests/guards and exact Player evidence: successor authority;
- later integrated product/architecture/governance documentation: current `main` authority;
- shared navigation/CI files: deliberate reconciliation, not blind branch preference.

After successful validated convergence, update this file and `docs/BRANCH_STATUS.md` so `main` becomes the single normal integrated trunk.

## 4. Owner authority and working relationship

The owner decides consequential matters that genuinely require product ownership, including:

- what the application should do;
- workflows and UX behavior;
- game semantics;
- visibility/privacy expectations;
- MVP vs later scope;
- user-facing destructive/safety behavior;
- meaningful service/cost, security/privacy, compatibility or irreversible-lock-in tradeoffs.

The owner explicitly delegates routine technical implementation to the technical assistant/coding agents and should **not** be asked to rubber-stamp matters outside their intended competence.

Agents should normally decide, implement, test and document low-level matters such as:

- table/schema layout;
- class/type decomposition;
- API/request shapes;
- migration mechanics;
- internal sync structures;
- canonical serialization format;
- test architecture;
- detailed branch/package granularity;
- reversible provider-specific mechanics within approved service/cost/security boundaries.

Escalate a technical choice only when it materially changes product behavior, risk, cost, privacy/security, irreversible lock-in or approved scope.

D-0073 is controlling for this owner-vs-technical boundary and supersedes older wording that required ceremonial owner approval for every new durable implementation convention.

## 5. Explain technical work

Meaningful technical work must remain understandable to the owner. Explain proportionately:

- what is changing;
- why;
- the important approach in practical terms;
- meaningful owner-relevant consequences/tradeoffs;
- verification and remaining limitations.

Do not push routine engineering analysis back to the owner under the guise of transparency.

C-0008 additionally requires representative SQL when it materially improves understanding of relational/data-model behavior.

## 6. Conventions and proportionality

C-0009 is controlling: this is a personal, deliberately limited project. Prefer the **simplest safe implementation that satisfies actual approved requirements**. Do not import enterprise/SaaS machinery without a concrete need.

When a recurring coding/naming/structure/testing/documentation convention becomes necessary and no approved convention exists:

1. choose the simplest maintainable option consistent with approved architecture;
2. record it in Git when it will matter to later contributors;
3. apply it consistently;
4. surface it to the owner only when it has a meaningful product, cost, security/privacy, compatibility or expensive-to-reverse consequence.

Do not create process overhead merely to obtain owner approval for low-level reversible implementation style.

## 7. Approved architecture and product baseline

D-0034 through D-0043 remain the foundational stack/architecture set.

D-0068/D-0069 define the approved DM Workspace/Desk family and live-product direction. D-0070 defines the shared Player/DM rules-question capability.

D-0071 defines the integrated Player + Server + DM architecture. D-0072 closes the detailed DM Desktop/Manager product definition. D-0073 closes the exact MVP boundary, implementation governance and planned branch convergence.

Do not reopen those decisions merely because historical documents describe earlier uncertainty.

The integrated MVP is intentionally substantial; internal waves are engineering organization, not permission to silently trim approved product capabilities.

## 8. No silent invention or status inflation

Clearly distinguish:

- **Approved** — explicitly accepted by the owner and durably recorded;
- **Proposed/Recommended** — technically/product recommended but not owner-approved where approval is actually required;
- **Pending** — genuinely requires a future decision/action;
- **Implemented** — present in code and verified at the stated level;
- **Accepted** — passed the required owner/manual gate where one exists.

Do not infer `Accepted` from `Implemented`, green CI or branch location.

Do not convert a delegated low-level engineering choice into a fake owner decision record merely to create paperwork.

## 9. Change workflow

For substantial work:

1. verify the applicable active branch/topology from `docs/BRANCH_STATUS.md`;
2. read current `PROJECT_STATE`, `LATEST`, relevant decisions and checkpoints;
3. identify material unknowns and the real owner authorization boundary;
4. make routine technical choices autonomously within D-0073;
5. escalate only material owner-level consequences;
6. implement the smallest coherent batch;
7. run appropriate focused and aggregate checks;
8. update operative-memory documentation;
9. leave durable Git evidence/checkpoint when meaningful;
10. summarize verification and exact next action.

Do not bundle unrelated behavior merely to reduce commit count.

Before the first integrated product-code batch, the planned `main` + Player successor convergence requires explicit owner implementation authorization. This documentation/readiness work does not itself grant that authorization.

## 10. Definition of done and continuity checkpoints

A change is not complete merely because code exists. A completed batch has, as applicable:

- implementation present;
- relevant automated checks passed, or failures explicitly recorded;
- appropriate behavior checked;
- current-state documentation updated;
- consequential decisions/conventions recorded;
- unresolved items visible;
- exact next action.

Every meaningful project step must leave durable Git evidence before moving on.

`docs/checkpoints/LATEST.md` is the stable global resume pointer and must be refreshed when its practical continuation point changes. Before convergence, the Player successor's own `LATEST.md` remains the exact Player-runtime evidence pointer.

## 11. Technical quality, credentials and signing material

Prefer maintainable, readable, testable code over clever code. Keep dependencies justified and proportional.

Never commit real secrets: passwords, API tokens, database credentials, provider admin credentials, production/private credentials, production/release signing keys, private certificates or other confidentiality-dependent material.

System Administration may eventually store scoped provider credentials locally on the sole administrator's machine where this improves convenience, but those credentials must use suitable local/OS protected storage and never be hard-coded or committed.

### Development-only Android signing

The repository uses a stable **development-only debug signing identity** for CI QA APKs so successive builds can update one another in place and exercise SQLite migrations.

That identity is not a production/release trust boundary.

Rules:

- never reuse it for a production/release build;
- never treat possession of it as authentication/security;
- any future real release signing identity remains private and must not be committed;
- do not expose/reproduce signing material in chat/docs unnecessarily;
- design release signing separately if/when a real release pipeline exists.

## 12. Recovery from inconsistency

If repository documents disagree:

1. do not guess silently;
2. identify the contradiction;
3. prefer the most specific later Approved decision/clarification over older general prose;
4. prefer newer explicitly dated current-state documentation when authority is otherwise equal;
5. `docs/BRANCH_STATUS.md` controls current branch lifecycle/role;
6. use detailed `docs/decisions/` and checkpoints for rationale/evidence;
7. ask the owner only for a genuinely material product/scope/risk ambiguity not already resolved;
8. repair/document stale governance when it would misdirect later work.

Historical checkpoints remain evidence even when their old `next` instructions are superseded.

## 13. Current project stage and authorization boundary

The integrated product design and MVP boundary are closed under D-0071/D-0072/D-0073. The technical-readiness review is recorded in:

`docs/checkpoints/2026-09-14_INTEGRATED_MVP_TECHNICAL_READINESS_REVIEW.md`

Current Player frozen physical candidate remains:

- version `0.4.0-preqa.13`;
- versionCode/build `41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold run `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted physical revalidation pending on the historical Player line.

This evidence must be preserved; automation is not retroactive physical owner acceptance.

However, D-0073 supersedes the older process rule that DM/integrated implementation could not even begin until a standalone Phase 4A owner closure. The intended next implementation activity is now the protected integrated baseline convergence, **but it still requires explicit owner authorization to begin product-code implementation**.

Current execution entry points:

- global/integrated planning and governance: `main` -> `docs/checkpoints/LATEST.md`;
- current Player source/evidence before convergence: `implementation/phase4a-successor-cycle` -> that branch's `docs/checkpoints/LATEST.md`;
- technical readiness: `docs/checkpoints/2026-09-14_INTEGRATED_MVP_TECHNICAL_READINESS_REVIEW.md`;
- repository topology: `docs/BRANCH_STATUS.md`.

Until explicit implementation authorization is granted, documentation/readiness corrections are allowed but do not start the convergence or product-code build.
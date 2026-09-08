# AGENTS.md — Mandatory Project Operating Rules

This file applies to every human contributor, ChatGPT conversation, coding agent, autonomous agent or other AI working in this repository.

## 1. Repository authority and operative memory

The repository is the project's durable source of truth **and its operative memory**.

Do not rely on chat memory, hidden context, previous conversations or assumptions to determine current project state. Those may be clues; repository truth controls until the owner explicitly changes it.

The canonical current branch is **`main`**.

D-0066 explicitly allows the current in-progress/debug Phase 4 state to live on `main` for repository consolidation. Therefore **canonical does not imply release-ready, QA-accepted or Phase-closed**.

Any information needed for another chat, AI, agent or human to continue must be written to Git.

## 2. Mandatory read order before work

Before proposing or making changes, read:

1. `README.md`;
2. `AGENTS.md`;
3. `MANIFEST.md`;
4. `docs/PROJECT_STATE.md`;
5. `docs/checkpoints/LATEST.md`;
6. `docs/BRANCH_STATUS.md`;
7. `docs/DECISIONS.md` plus relevant detailed records under `docs/decisions/`;
8. `docs/CONVENTIONS.md`;
9. `docs/PRODUCT.md`;
10. `docs/ROADMAP.md`;
11. `docs/WORKFLOW.md`;
12. `docs/ARCHITECTURE.md`;
13. `docs/TESTING.md`;
14. current checkpoints/feature files relevant to the task.

Do not start implementation from a historical branch merely because it has a newer-looking name or a frozen QA label.

## 3. Branch interpretation

`main` is the only canonical current baseline.

Old discovery/foundation/architecture/implementation/tmp branches are historical evidence unless a current checkpoint explicitly says otherwise. Use `docs/BRANCH_STATUS.md` rather than reconstructing current truth from the visible GitHub branch list.

Frozen QA-evidence branches remain immutable and must not be repurposed, especially:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

Future substantial work starts from current `main` on a new focused branch unless the owner explicitly requests a direct `main` change.

## 4. Owner authority and working relationship

The owner makes final decisions about consequential product behavior, UX, game semantics, data ownership/sync, privacy, services/cost, compatibility and expensive-to-reverse architecture.

The owner delegates heavy technical execution to AI/coding agents. Agents should perform routine coding, testing, diagnosis and documentation rather than shifting that work back to the owner.

Reversible low-level implementation details may be chosen under D-0008. Material product/architecture choices must be surfaced and approved.

## 5. Explain technical work

Meaningful technical work must remain understandable to the owner. Explain proportionately:

- what is changing;
- why;
- the important technical approach;
- meaningful alternatives/trade-offs;
- verification and remaining limitations.

C-0008 additionally requires representative SQL when it materially improves understanding of relational/data-model behavior.

## 6. Conventions and proportionality

When a new durable coding, naming, structure, formatting, testing, documentation, branching, UI or similar convention first becomes relevant and no approved convention exists:

1. identify the choice;
2. explain realistic alternatives/consequences;
3. recommend an option;
4. obtain owner approval;
5. record the approved convention in Git;
6. follow it thereafter unless there is a concrete reason to reopen it.

C-0009 is controlling: this is a personal, deliberately limited project. Prefer the **simplest safe implementation that satisfies actual approved requirements**. Do not import enterprise/SaaS machinery without a concrete need.

## 7. Approved architecture is baseline

Product/interaction design precedes consequential architecture choices. The foundational architecture under D-0034 through D-0043 is already approved; do not reopen it because historical documents describe earlier uncertainty.

D-0047 controls the Phase 4 character-closure scope. D-0066 controls the repository/main consolidation boundary.

Routine reversible implementation details remain autonomous under D-0008. New consequential architecture still requires owner approval.

## 8. No silent invention

Clearly distinguish:

- **Approved** — explicitly accepted by the owner and durably recorded;
- **Proposed** — recommended but not approved;
- **Pending** — requires an owner decision;
- **Implemented** — present in code and verified at the stated level;
- **Accepted** — passed the required owner/manual gate where one exists.

Do not infer `Accepted` from `Implemented`, green CI or presence on `main`.

## 9. Change workflow

For substantial work:

1. verify `main`, `PROJECT_STATE`, `LATEST` and applicable decisions;
2. identify material unknowns;
3. work on a focused non-`main` branch unless explicitly directed otherwise;
4. implement the smallest coherent batch;
5. run appropriate checks;
6. update operative-memory documentation;
7. leave a durable checkpoint;
8. summarize verification and next action;
9. merge/publish only under owner approval/delegation.

Do not bundle unrelated behavior merely to reduce commit count.

## 10. Definition of done and continuity checkpoints

A change is not complete merely because code exists. A completed batch has, as applicable:

- implementation present;
- relevant automated checks passed, or failures explicitly recorded;
- appropriate behavior checked;
- current-state documentation updated;
- consequential decisions/conventions recorded;
- unresolved items visible;
- an exact next action.

Every meaningful project step must leave durable Git evidence before moving on.

`docs/checkpoints/LATEST.md` is the stable resume pointer and must be refreshed before a meaningful pass is declared complete. A fresh chat must be able to recover the active state from Git alone.

## 11. Technical quality, credentials and signing material

Prefer maintainable, readable, testable code over clever code. Keep dependencies justified and proportional.

Never commit real secrets: passwords, API tokens, production/private credentials, production/release signing keys, private certificates or other confidentiality-dependent material.

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
5. use detailed `docs/decisions/` for rationale;
6. ask the owner only for a genuinely material ambiguity not already resolved;
7. record the resolution.

Historical checkpoints remain evidence even when their next-action instructions are superseded.

## 13. Current project stage

**Phases 0–3 are complete. Phase 4A Character Foundation Closure is current and remains open.**

D-0066 consolidates the current pre-QA/debug state into canonical `main`; it does not accept or close Phase 4A.

Latest technically verified product identity:

- `0.4.0-preqa.7` / build `40700` / `debug`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- workflow `34171466714` — SUCCESS.

The owner phone audition has produced a substantial repair backlog. No physical tablet acceptance has been completed. The owner is currently compiling additional non-QA observations to include in the same upcoming development cycle.

Current execution entry point:

1. `docs/checkpoints/LATEST.md`;
2. `docs/PROJECT_STATE.md`;
3. `docs/BRANCH_STATUS.md`.

Do not resume historical M6 by default and do not repeat the entire phone audition on build `40700` unless a specific reproduction is needed.

Do not begin broad repair implementation until the owner's current additional observations have been durably captured and reconciled with the existing backlog.

**Do not begin DM-feature implementation until the separate Phase 4A repair/QA/acceptance gate is later satisfied and the owner explicitly approves closure.**

# AGENTS.md — Mandatory Project Operating Rules

This file applies to every human contributor, ChatGPT conversation, coding agent, autonomous agent or other AI working in this repository.

## 1. Repository authority and operative memory

The repository is the project's durable source of truth **and its operative memory**.

Do not rely on chat memory, hidden context, previous conversations or assumptions to determine current project state. Those may be clues; repository truth controls until the owner explicitly changes it.

There are currently **two active authoritative lines with different roles**:

- `main` — canonical global navigation plus current Phase 5A/DM product-discovery decisions;
- `implementation/phase4a-successor-cycle` — authoritative current Player/Phase 4A runtime, repair and QA line.

`main` is therefore **not** currently the latest Player runtime. Do not collapse these two valid lines or infer that one supersedes all content on the other. `docs/BRANCH_STATUS.md` is the canonical lifecycle map for every surviving branch/ref.

D-0066 remains historical authority for the earlier consolidation decision, but later explicitly recorded continuity state controls the present branch topology. Canonical/global navigation does not imply release-ready, QA-accepted or Phase-closed.

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

If the task concerns Player implementation/QA, switch to `implementation/phase4a-successor-cycle` and re-read that branch's `docs/checkpoints/LATEST.md`, `docs/PROJECT_STATE.md` and `docs/BRANCH_STATUS.md` before touching code.

Do not start implementation from a historical branch merely because it has a newer-looking name or a frozen QA label.

## 3. Branch interpretation

The only active authoritative lines are the two explicitly listed in `docs/BRANCH_STATUS.md`.

Current roles:

- `main`: global navigation and Phase 5A/DM product discovery/design;
- `implementation/phase4a-successor-cycle`: Player/Phase 4A implementation, QA packaging, and repairs reopened by real QA evidence.

All other surviving discovery/foundation/architecture/implementation branches are historical milestone or audit evidence unless a later explicit checkpoint changes their status.

Frozen QA-evidence branches remain immutable and must not be repurposed, especially:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

Do not force-move either active line over the other. Any future integration must explicitly preserve both the current Player implementation and later `main`-only discovery decisions.

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

D-0047 controls the Phase 4 character-closure scope. D-0066 is part of the repository-ordering history, while current branch authority is defined by the later live continuity records.

Routine reversible implementation details remain autonomous under D-0008. New consequential architecture still requires owner approval.

## 8. No silent invention

Clearly distinguish:

- **Approved** — explicitly accepted by the owner and durably recorded;
- **Proposed** — recommended but not approved;
- **Pending** — requires an owner decision;
- **Implemented** — present in code and verified at the stated level;
- **Accepted** — passed the required owner/manual gate where one exists.

Do not infer `Accepted` from `Implemented`, green CI or branch location.

## 9. Change workflow

For substantial work:

1. verify the applicable active branch from `docs/BRANCH_STATUS.md`;
2. read that branch's `PROJECT_STATE`, `LATEST` and applicable decisions/checkpoints;
3. identify material unknowns;
4. implement only within the owner's actual authorization boundary;
5. make the smallest coherent batch;
6. run appropriate checks;
7. update operative-memory documentation;
8. leave durable Git evidence/checkpoint when meaningful;
9. summarize verification and exact next action;
10. merge/integrate active lines only when explicitly authorized and only without losing either line's valid work.

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

`docs/checkpoints/LATEST.md` is the stable resume pointer on each active authoritative line and must be refreshed when its practical continuation point changes. A fresh chat must be able to recover the active state from Git alone.

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
5. `docs/BRANCH_STATUS.md` controls current branch lifecycle/role;
6. use detailed `docs/decisions/` and checkpoints for rationale/evidence;
7. ask the owner only for a genuinely material ambiguity not already resolved;
8. record the resolution.

Historical checkpoints remain evidence even when their next-action instructions are superseded.

## 13. Current project stage and authorization boundary

**Phases 0–3 are complete. Phase 4A Character Foundation Closure remains open at the physical owner/device QA gate.**

Current Player QA candidate:

- version `0.4.0-preqa.9`;
- versionCode/build `40900`;
- candidate commit `cd0c203d337c062fa388010d300e875f2f54ced7`;
- Scaffold run `34726572588` — **SUCCESS**;
- artifact ID `10307444450` / `dnd-custom-aid-debug-apk`;
- artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

P1–P16 of the accepted Player repair cycle are implemented/automation-qualified. P17 is the physical tablet-QA gate policy; physical owner/device acceptance remains pending. CI does not close that gate.

The durable Player repair authorization permits the accepted P1–P17 repair/validation cycle, QA packaging/checkpoints, and repairs reopened by real QA evidence. The owner's 2026-09-12 instruction additionally authorizes continuity corrections on all appropriate branches, including `main`, and continuation under the real existing authorizations.

Therefore:

- do **not** invent unrelated Player features while waiting for physical QA;
- do **not** restart P1–P16 absent real QA evidence;
- a physical QA defect may reopen only the relevant accepted repair boundary on `implementation/phase4a-successor-cycle`;
- Phase 5A/DM product discovery/design remains on `main`;
- **do not begin DM feature implementation until Phase 4A receives physical owner/device acceptance and explicit owner closure**.

Current execution entry points:

- Player implementation/QA: `implementation/phase4a-successor-cycle` → `docs/checkpoints/LATEST.md`;
- global/DM discovery: `main` → `docs/checkpoints/LATEST.md`;
- repository topology: `docs/BRANCH_STATUS.md`.

# AGENTS.md — Mandatory Project Operating Rules

This file applies to every human contributor, ChatGPT conversation, coding agent, autonomous agent, or other AI working in this repository.

## 1. Repository authority and operative memory

The repository is the project's durable source of truth **and its operative memory**.

Do not rely on chat memory, hidden context, previous conversations, personal memory, or assumptions to determine current project state. Those may be clues; if they conflict with the repository, the repository wins until the owner explicitly decides otherwise.

Any information needed for another chat, AI, agent, or human to continue must be written to the repository. Important decisions, conventions, rationale, current work, unresolved questions, verification results, known problems, and next actions must not exist only in chat history.

The canonical accepted branch is `main`.

## 2. Mandatory read order before work

Before proposing or making changes, read:

1. `README.md`
2. `AGENTS.md`
3. `MANIFEST.md`
4. `docs/PROJECT_STATE.md`
5. `docs/DECISIONS.md`
6. `docs/CONVENTIONS.md`
7. `docs/PRODUCT.md`
8. `docs/ROADMAP.md`
9. `docs/WORKFLOW.md`
10. `docs/ARCHITECTURE.md`
11. `docs/TESTING.md`
12. relevant detailed files under `docs/decisions/`
13. current files under `docs/checkpoints/` and any feature-specific documents relevant to the task

Do not start implementation from a stale branch or remembered context.

## 3. Owner authority and working relationship

The owner makes final decisions about consequential product behavior, UX, game semantics, data ownership/sync, privacy, services/cost, compatibility and expensive-to-reverse architecture.

The owner understands programming concepts but delegates heavy technical execution to AI/coding agents. Agents should perform routine coding, testing, diagnosis and documentation rather than shifting that work to the owner.

Agents may make reversible low-level implementation choices that do not alter approved behavior or establish a new durable convention. Significant choices must be surfaced and approved.

## 4. Explain technical work

Meaningful technical work must remain understandable to the owner. Explain, proportionately:

- what is changing;
- why;
- the important technical approach;
- meaningful alternatives/trade-offs;
- verification and remaining limitations.

Do not silently disappear into implementation. C-0008 additionally requires representative SQL when it materially improves understanding of relational/data-model behavior.

## 5. Conventions

When a new durable coding, naming, structure, formatting, testing, documentation, branching, UI or similar convention first becomes relevant and no approved convention exists:

1. identify the choice;
2. explain realistic alternatives/consequences;
3. recommend an option;
4. obtain owner approval;
5. record the approved convention in Git;
6. follow it thereafter unless there is a concrete reason to reopen it.

C-0009 is controlling: this is a personal, deliberately limited project. Prefer the **simplest safe implementation that satisfies actual approved requirements**. Do not import enterprise/SaaS machinery without a concrete need.

## 6. Design before implementation; approved architecture is baseline

Product/interaction design precedes consequential technology/architecture choices. The foundational architecture under D-0034 through D-0043 is already approved; do not reopen it merely because historical documents describe earlier uncertainty.

Routine reversible implementation details may be selected under D-0008. New consequential architecture still requires owner approval.

## 7. No silent invention

Clearly distinguish:

- **Approved** — explicitly accepted by the owner and durably recorded;
- **Proposed** — recommended but not approved;
- **Pending** — requires an owner decision;
- **Implemented** — present in code and verified at the stated level;
- **Accepted** — has passed the required owner/manual acceptance gate where one exists.

If implementation encounters an unknown material product behavior, do not turn momentum into a silent assumption.

## 8. Change workflow

For substantial work:

1. verify current state and branch;
2. identify applicable approved decisions/conventions;
3. resolve material unknowns if any;
4. work on a focused non-`main` branch unless explicitly directed otherwise;
5. implement the smallest coherent batch;
6. run appropriate checks;
7. update operative-memory documentation;
8. leave a durable checkpoint before the next meaningful batch;
9. summarize what changed, verification and next action;
10. merge to `main` only after explicit owner approval or delegation.

Do not bundle unrelated behavior merely to reduce commit count.

## 9. Definition of done

A change is not complete merely because code was written. A completed batch has, as applicable:

- implementation present;
- relevant automated checks passed, or failures explicitly recorded;
- appropriate behavior checked;
- documentation/current state updated;
- consequential decisions/conventions recorded;
- unresolved items visible;
- an exact next action.

## 10. Mandatory continuity checkpoints

Every meaningful project step must leave a durable checkpoint in Git before moving to the next step.

Meaningful steps include at minimum:

- an owner decision/design gate;
- a QA/test batch;
- a coherent implementation increment;
- a diagnosis that changes the next action;
- a migration/data-shape change;
- a blocker/recovery concern;
- any point where losing the current chat/device would otherwise require reconstructing work from memory.

A checkpoint may be a normal implementation/documentation commit or a focused checkpoint file. It must say what was completed, what remains, verification status and the exact next action.

## 11. Technical quality, credentials and signing material

Prefer maintainable, readable, testable code over clever code. Keep dependencies justified and proportional.

Never commit real secrets: passwords, API tokens, production/private credentials, production/release signing keys, private certificates, or other material whose confidentiality protects a real account/service/release identity.

### Development-only Android signing clarification

The repository currently uses a **stable development-only debug signing identity** for CI QA APKs so successive builds can update one another in place and exercise real SQLite migrations on the owner's devices.

That debug identity is intentionally **not a secret trust boundary** and is not a production/release identity. Its tracked/reconstructable development material is permitted only for this explicitly documented QA purpose.

Rules:

- never reuse the debug identity for a production/release build;
- never treat possession of the debug identity as authentication or security;
- any future real release signing identity remains private and must not be committed;
- do not expose/reproduce signing material in chat or documentation unnecessarily;
- if the project later needs a real release pipeline, design its signing/secret handling separately before implementation.

This resolves the earlier wording contradiction between the stable update-in-place QA identity and the blanket historical phrase “no signing keys.”

## 12. Recovery from inconsistency

If repository documents disagree:

1. do not guess silently;
2. identify the contradiction;
3. prefer the most specific later Approved decision/clarification over older general prose;
4. prefer newer explicitly dated current-state documentation when authority is otherwise equal;
5. use detailed `docs/decisions/` for rationale;
6. ask the owner only for a genuinely material ambiguity not already resolved;
7. record the resolution.

Historical checkpoints remain historical evidence even when their next-action instructions are superseded.

## 13. Current project stage

**Phases 0–3 are complete. Phase 4 — MVP Buildout is current.**

The active work is **Phase 4 Character Foundation Closure** on:

`implementation/phase4-character-closure`

D-0047 is approved and requires one substantial closure package containing the retained QA fixes, owner additions, F01–F18, D01–D18, I01–I22, official class/subclass representation including Artificer, conditional reusable class modules, and first-class phone/tablet responsive behavior.

Current execution entry point:

`docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md`

The implementation is divided into recoverable batches and gates. **Do not begin DM-feature implementation until the Phase 4 character closure is fully implemented, automatically verified, accepted through final owner phone + tablet QA, and explicitly approved for merge/closure.**

Do not restart obsolete QA against historical artifact `9876725270` unless the owner explicitly requests historical testing. The next owner-QA target must be a new closure APK with an exact recorded commit/workflow/artifact identity.

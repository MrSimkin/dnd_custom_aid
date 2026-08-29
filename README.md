# dnd_custom_aid

Personal tabletop RPG assistant project beginning with D&D, with Android phone/tablet use and a desktop-friendly administration workflow under design.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent, or other AI can resume the project from the repository alone.

Before changing anything, read these files in order:

1. `AGENTS.md` — mandatory operating rules for humans and AI agents.
2. `MANIFEST.md` — map of authoritative/project-memory files.
3. `docs/PROJECT_STATE.md` — current verified state and next action.
4. `docs/DECISIONS.md` — significant decisions already made and decisions still pending.
5. `docs/CONVENTIONS.md` — approved recurring project conventions.
6. `docs/PRODUCT.md` — current approved product direction and MVP.
7. `docs/ROADMAP.md` — development phases and current phase.
8. `docs/WORKFLOW.md` — how alternatives are discussed and changes are designed, implemented, tested, documented, reviewed, and merged.
9. `docs/ARCHITECTURE.md` — architecture status and rationale when architecture work begins.
10. `docs/TESTING.md` — verification rules and test status expectations.
11. Relevant `docs/discovery/` notes for historical rationale/current design context.

Then read any feature-specific or technical files relevant to the task.

## Canonical source of truth

- `main` is the canonical accepted project state (D-0007).
- Git is the project's operative memory (D-0012).
- Repository files, not chat memory, determine durable project truth.
- Significant decisions must be recorded in `docs/DECISIONS.md` and/or the appropriate authoritative product file.
- Durable conventions must be recorded in `docs/CONVENTIONS.md`.
- `docs/PROJECT_STATE.md` must be updated whenever meaningful work changes project reality or next action.
- Discovery notes preserve exploratory reasoning but do not override authoritative confirmed product/decision records.

## Working relationship

AI/coding agents perform the heavy technical execution. The owner remains the decision owner.

Meaningful technical work must be explained: what is being done, why, important alternatives, and relevant consequences. New durable conventions are discussed with the owner when they first arise, then recorded in Git and followed consistently.

## Design before stack

The project does **not** begin by selecting an Android framework or technology stack.

The approved sequence is:

1. understand the product and users;
2. explore realistic alternatives with the owner;
3. design intended behavior and interactions;
4. record the approved design in Git;
5. evaluate technical alternatives against that design;
6. obtain owner approval for consequential technology/architecture choices;
7. then scaffold and implement.

See D-0011 and `docs/ROADMAP.md`.

## Current status

The project is in **Phase 1 — Product Discovery and Design**, with the core first-use product baseline now substantially defined.

Current discovery work is on branch `discovery/initial-product-picture` in PR #2. Three clarification rounds now establish the paper-first/full digital character-backup workflow, grouped DM audit/correction, campaign-scoped roles and unassigned PCs, account/invitation/moderation direction, mixed broader D&D 5e/5.5e rules direction, SRD-only MVP clarification, Quick/Developed NPC workflows, complete/extensible monster stat blocks, prepared and on-the-fly encounters, offline-resilient DM combat behavior, and the approved MVP boundary.

The seven previously highlighted product questions are resolved. `D-0010 — Initial product scope / MVP` is now **Approved**.

The next major project step is to evaluate architecture and technology-stack alternatives against the approved MVP—especially Android phone/tablet behavior, desktop administration, offline combat, synchronization, PDF generation, SRD retrieval/clarification, maintainability and personal-scale/no-cost hosting—and obtain owner approval before implementation.

No application technology stack has been selected and no application code exists yet.

See `docs/PROJECT_STATE.md` for the authoritative current status.
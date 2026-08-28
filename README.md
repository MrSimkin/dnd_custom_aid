# dnd_custom_aid

Android companion application project for phone and tablet, intended to support both players and Dungeon Masters.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent, or other AI can resume the project from the repository alone.

Before changing anything, read these files in order:

1. `AGENTS.md` — mandatory operating rules for humans and AI agents.
2. `MANIFEST.md` — inventory of project-control files and authority boundaries.
3. `docs/PROJECT_STATE.md` — current verified state and next action.
4. `docs/DECISIONS.md` — significant decisions already made and decisions still pending.
5. `docs/CONVENTIONS.md` — approved recurring project conventions.
6. `docs/PRODUCT.md` — product scope, design/discovery approach, and unresolved questions.
7. `docs/ROADMAP.md` — development phases and current phase.
8. `docs/WORKFLOW.md` — how alternatives are discussed and changes are designed, implemented, tested, documented, reviewed, and merged.
9. `docs/ARCHITECTURE.md` — architecture status and rationale when architecture work begins.
10. `docs/TESTING.md` — verification rules and test status expectations.

Then read any feature-specific or technical files relevant to the task.

## Canonical source of truth

- `main` is the canonical accepted project state (D-0007).
- Git is the project's operative memory (D-0012).
- Repository files, not chat memory, determine durable project truth.
- A future agent must not assume that information remembered from a prior chat is current unless it is also recorded in the repository.
- Significant decisions must be recorded in `docs/DECISIONS.md`.
- Durable conventions must be recorded in `docs/CONVENTIONS.md`.
- `docs/PROJECT_STATE.md` must be updated whenever meaningful work changes the project's actual state or next action.

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

The repository foundation/governance rules have been owner-approved. No application architecture, technology stack, screens, feature set, visual design, or game-system-specific behavior has been approved yet.

The next substantive phase is **Product Discovery and Design**.

See `docs/PROJECT_STATE.md` for the authoritative current status.

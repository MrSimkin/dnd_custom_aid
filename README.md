# dnd_custom_aid

Personal tabletop RPG assistant project beginning with D&D, with Android phone/tablet live use and a desktop-friendly DM preparation/administration workflow under design.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent, or other AI can resume the project from the repository alone.

After reading this README, continue with these files in order:

1. `AGENTS.md` — mandatory operating rules for humans and AI agents.
2. `MANIFEST.md` — map of authoritative/project-memory files.
3. `docs/PROJECT_STATE.md` — current verified state and next action.
4. `docs/DECISIONS.md` — significant decisions already made and decisions still pending.
5. `docs/CONVENTIONS.md` — approved recurring project conventions.
6. `docs/PRODUCT.md` — current approved product direction and MVP.
7. `docs/ROADMAP.md` — development phases and current phase.
8. `docs/WORKFLOW.md` — how alternatives are discussed and changes are designed, implemented, tested, documented, reviewed, and merged.
9. `docs/ARCHITECTURE.md` — architecture evaluation/status and rationale.
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

The project is closing **Phase 1 — Product Discovery and Design**. The product/MVP baseline is approved and the final pre-merge tension pass is complete; PR #2 is undergoing final documentation/mergeability verification before canonical `main` moves into Phase 2.

Current discovery work is on branch `discovery/initial-product-picture` in PR #2. The approved baseline includes:

- paper-first live character use with a durable, freshness-visible digital backup;
- Android as the primary live/table surface;
- a narrower desktop/laptop DM preparation/administration companion using the same shared domain data;
- **multicampaign MVP behavior** with campaign-scoped roles and moderation;
- grouped DM audit/correction and preserved/unassigned PCs;
- campaign invitations/rejoining with reusable revocable campaign invites;
- mixed D&D 5e/5.5e/homebrew campaign freedom while MVP rules clarification remains official-SRD-only;
- Quick/Developed NPC workflows and complete/extensible monster stat blocks;
- prepared and on-the-fly encounters;
- local-first authoritative DM combat with opportunistic hosted synchronization and provisional offline player views;
- the approved MVP/non-MVP boundary.

`D-0010 — Initial product scope / MVP` and `D-0033 — Final pre-merge product tension resolutions` are **Approved**.

No remaining identified product-level contradiction or behavioral ambiguity blocks Phase 1 closure. The intentionally pending foundational decision is **D-0009 — Application architecture and implementation technology**.

After PR #2 is owner-approved and merged, the next project step is **architecture and technology evaluation** against the approved MVP—starting with overall application topology/surface relationship, then Android, desktop administration, multicampaign data, local-first combat synchronization, authentication/security, PDF generation, SRD retrieval/clarification, maintainability/testing and personal-scale/no-cost hosting.

No application technology stack has been selected and no application code exists yet.

See `docs/PROJECT_STATE.md` for the authoritative current status.

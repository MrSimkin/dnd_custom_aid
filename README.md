# dnd_custom_aid

Personal tabletop RPG assistant project beginning with D&D, with Android phone/tablet live use and a native desktop DM preparation/administration workflow.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent, or other AI can resume the project from the repository alone.

After reading this README, continue with these files in order:

1. `AGENTS.md` — mandatory operating rules for humans and AI agents.
2. `MANIFEST.md` — map of authoritative/project-memory files.
3. `docs/PROJECT_STATE.md` — current verified state and next action.
4. `docs/DECISIONS.md` — chronological significant-decision log.
5. `docs/decisions/` — approved dedicated architecture-decision checkpoints not yet consolidated into the chronological log.
6. `docs/CONVENTIONS.md` — approved recurring project conventions.
7. `docs/PRODUCT.md` — current approved product direction and MVP, subject to later approved decision amendments.
8. `docs/ROADMAP.md` — development phases and current phase.
9. `docs/WORKFLOW.md` — how changes are designed, implemented, tested, documented, reviewed, and merged.
10. `docs/ARCHITECTURE.md` — current approved architecture record and rationale.
11. `docs/TESTING.md` — verification rules and test status expectations.
12. Relevant `docs/discovery/` notes only when historical rationale is needed.

Then read any feature-specific or technical files relevant to the task.

## Canonical source of truth

- `main` is the canonical accepted project state (D-0007).
- Git is the project's operative memory (D-0012).
- Repository files, not chat memory, determine durable project truth.
- Significant approved decisions must be recorded durably; during the current architecture branch, D-0036 through D-0043 are stored under `docs/decisions/` pending chronological-log consolidation.
- Durable conventions belong in `docs/CONVENTIONS.md`.
- `docs/PROJECT_STATE.md` is the authoritative current-state/next-action snapshot.
- Discovery notes preserve exploratory reasoning but do not override confirmed product/decision records.

## Working relationship

AI/coding agents perform the heavy technical execution. The owner remains the decision owner for consequential product/architecture choices.

Meaningful technical work must be explained. New durable conventions are discussed with the owner when they first arise, then recorded and followed consistently.

C-0009 is controlling: **this is a personal/small-scale project. Prefer the simplest safe solution that satisfies real requirements and do not add enterprise machinery without a concrete reason.**

## Design before implementation

The approved sequence is:

1. understand the product and users;
2. explore/design intended behavior;
3. record the approved product baseline;
4. evaluate architecture against that design;
5. obtain owner approval for consequential architecture choices;
6. consolidate/review the architecture branch;
7. scaffold and implement incrementally.

Phase 1 completed the product/design work. The consequential Phase 2 architecture choices D-0034 through D-0043 are now owner-approved. The project is at step 6: bounded documentation consolidation/review before the first scaffold.

## Approved product baseline

The product baseline includes:

- paper-first live character use with a durable, freshness-visible digital backup;
- Android as the primary live/table surface;
- a native desktop/laptop DM preparation/administration companion;
- multicampaign MVP behavior with campaign-scoped roles and moderation;
- grouped DM audit/correction and preserved/unassigned PCs;
- campaign invitations/rejoining with reusable revocable campaign invites;
- mixed D&D 5e/5.5e/homebrew campaign freedom while MVP rules clarification remains official-SRD-only;
- Quick/Developed NPC workflows and complete/extensible monster stat blocks;
- prepared and on-the-fly encounters;
- local-first authoritative DM combat with opportunistic hosted synchronization and provisional player views;
- local character-sheet PDF export on both Android and desktop.

## Approved architecture snapshot

- Android: **Kotlin + Jetpack Compose**, minimum Android 11 / API 30.
- Desktop DM administration: **Kotlin + Compose Multiplatform Desktop**.
- Local persistence: **SQLite + SQLDelight**, local-first where practical.
- Hosted database: **Neon PostgreSQL**.
- Backend/API: **Cloudflare Workers**, implemented in TypeScript.
- Authentication: **Descope**; application/domain authorization remains project-owned.
- Native clients never connect directly to Neon or hold DB credentials.
- Synchronization is project-owned; ordinary data uses revision/idempotency safeguards while active combat uses stricter DM-authority semantics.
- PDF export: PdfBox-Android on Android and Apache PDFBox on desktop, local/offline.
- SRD clarification: official Spanish SRD 5.1 + SRD 5.2.1 in PostgreSQL, initial full-text retrieval, initially Cloudflare Workers AI for grounded answers; no vector machinery unless testing proves it necessary.
- Initial project/testing structure is deliberately small under D-0043/C-0009.

See `docs/ARCHITECTURE.md` for the full current architecture record.

## Current status

**Phase 1 is complete and merged. Phase 2 architecture selection is complete in substance. No application code has been scaffolded yet.**

The active branch is being consolidated/reviewed before merge to canonical `main`. Older wording in `docs/DECISIONS.md`/`docs/PRODUCT.md` may still describe some architecture choices as undecided; approved D-0036 through D-0043 and `docs/PROJECT_STATE.md` control until that documentation cleanup is complete.

After the architecture branch is accepted into `main`, the next project action is the **initial implementation scaffold**, not another broad architecture-discovery round.

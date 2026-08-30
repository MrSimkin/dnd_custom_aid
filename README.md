# dnd_custom_aid

Personal tabletop RPG assistant project beginning with D&D, with Android phone/tablet live use and a native desktop DM preparation/administration workflow.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent, or other AI can resume the project from the repository alone.

After reading this README, continue with these files in order:

1. `AGENTS.md` — mandatory operating rules for humans and AI agents.
2. `MANIFEST.md` — map of authoritative/project-memory files and implemented code areas.
3. `docs/PROJECT_STATE.md` — current verified state and next action.
4. `docs/DECISIONS.md` — chronological significant-decision log.
5. `docs/CONVENTIONS.md` — approved recurring project conventions.
6. `docs/PRODUCT.md` — current approved product direction and MVP.
7. `docs/ROADMAP.md` — development phases and current phase.
8. `docs/WORKFLOW.md` — how changes are designed, implemented, tested, documented, reviewed, and merged.
9. `docs/ARCHITECTURE.md` — current approved architecture record and rationale.
10. `docs/TESTING.md` — verification rules, commands, and current test status.
11. Relevant `docs/decisions/` files when detailed architecture rationale is useful.
12. Relevant `docs/discovery/` notes only when historical rationale is needed.

Then read any feature-specific or technical files relevant to the task.

## Canonical source of truth

- `main` is the canonical accepted project state (D-0007).
- Git is the project's operative memory (D-0012).
- Repository files, not chat memory, determine durable project truth.
- Significant approved decisions are recorded in `docs/DECISIONS.md`; detailed Phase 2 records remain under `docs/decisions/` for deeper rationale.
- Durable conventions belong in `docs/CONVENTIONS.md`.
- `docs/PROJECT_STATE.md` is the authoritative current-state/next-action snapshot.
- Discovery notes preserve exploratory reasoning but do not override confirmed product/decision records.

## Working relationship

AI/coding agents perform the heavy technical execution. The owner remains the decision owner for consequential product/architecture choices.

Meaningful technical work must be explained. New durable conventions are discussed with the owner when they first arise, then recorded and followed consistently.

C-0009 is controlling: **this is a personal/small-scale project. Prefer the simplest safe solution that satisfies real requirements and do not add enterprise machinery without a concrete reason.**

The 2026-08-30 proportionality audit made that concrete: do not build generalized sync/realtime/provider-abstraction infrastructure just because it is technically possible.

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
- local-first authoritative DM combat with opportunistic hosted synchronization;
- player combat public view with only ephemeral local **Next turn** and visible-condition convenience while temporarily offline, discarded on reconnect;
- local character-sheet PDF export on both Android and desktop.

## Approved architecture snapshot

- Android: **Kotlin + Jetpack Compose**, minimum Android 11 / API 30.
- Desktop DM administration: **Kotlin + Compose Multiplatform Desktop**.
- Local persistence: **SQLite + SQLDelight** where offline/local behavior provides real value.
- Desktop MVP: **Save locally + explicit Sync**; failed Sync never discards local work.
- Hosted database: **Neon PostgreSQL**.
- Backend/API: **Cloudflare Worker**, implemented in TypeScript.
- Authentication: **Descope**; application/domain authorization remains project-owned.
- Native clients never connect directly to Neon or hold DB credentials.
- Ordinary synchronization is deliberately small: stable IDs, idempotent mutations, revisions, a small outbox where needed, and simple tombstones.
- Active DM combat uses one authoritative DM device plus an increasing combat sequence/version in MVP; authority-generation/handoff machinery is deferred.
- Ordinary HTTP/request-response and simple polling/refresh come before WebSockets/Durable Objects/realtime infrastructure.
- Provider replaceability means keeping vendor-specific code reasonably localized, not building provider abstraction frameworks.
- PDF export: PdfBox-Android on Android and Apache PDFBox on desktop, local/offline.
- SRD clarification: official Spanish SRD 5.1 + SRD 5.2.1 in PostgreSQL, initial full-text retrieval, initially Cloudflare Workers AI for grounded answers; no vector machinery unless testing proves it necessary.

See `docs/ARCHITECTURE.md` for the full architecture record.

## Implemented scaffold

The focused branch `implementation/initial-scaffold` now contains the minimal buildable foundation approved by D-0043:

- `shared/` — one Kotlin Multiplatform shared module with SQLDelight foundation and smoke tests;
- `androidApp/` — Android Kotlin/Jetpack Compose application shell;
- `desktopApp/` — Kotlin/Compose Multiplatform Desktop application shell;
- `backend/` — TypeScript Cloudflare Worker shell with a minimal health endpoint;
- `database/` — PostgreSQL migration/data-loading area, intentionally without speculative domain schema;
- `.github/workflows/scaffold-check.yml` — one simple build/test workflow.

The scaffold intentionally does **not** yet implement authentication, campaigns, characters, combat, PDF export, SRD retrieval, Neon integration, realtime transport, R2, Durable Objects, queues, or deployment automation.

## Development prerequisites

Current scaffold prerequisites:

- JDK 17;
- Gradle 9.5;
- Android SDK platform 36;
- Node.js 22;
- npm.

`minSdk` remains API 30 / Android 11. The scaffold currently uses stable Android compile/target SDK 36 rather than depending on the Android 17/API 37 preview SDK channel.

A Gradle wrapper JAR is not currently committed because the available repository connector could not safely transfer the official binary wrapper JAR. CI therefore provisions pinned Gradle 9.5 directly. Local development currently needs Gradle 9.5 available on PATH until the wrapper is added through a normal local Git workflow.

## Build and verification commands

From repository root:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Run the desktop shell locally with:

```bash
gradle :desktopApp:run
```

Backend checks:

```bash
cd backend
npm install
npm run check
```

Run the local Worker development server with:

```bash
cd backend
npm install
npm run dev
```

The same core Kotlin and backend checks are executed by GitHub Actions. See `docs/TESTING.md` for the current verified revision and test status.

## Current status

**Phase 1 and architecture selection are canonical on `main`. The initial scaffold is implemented and CI-verified on `implementation/initial-scaffold`, pending owner review/merge into `main`.**

No broad MVP feature implementation has begun. After the scaffold is accepted into `main`, the next meaningful task is selecting and implementing the first small end-to-end vertical slice from approved scope.

Do not begin another broad architecture-discovery round or activate deferred infrastructure without a concrete new requirement.

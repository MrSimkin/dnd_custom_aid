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

## Approved architecture snapshot

- Android: **Kotlin + Jetpack Compose**, minimum Android 11 / API 30.
- Desktop DM administration: **Kotlin + Compose Multiplatform Desktop**.
- Local persistence: **SQLite + SQLDelight** where offline/local behavior provides real value.
- Desktop MVP: **Save locally + explicit Sync**; failed Sync never discards local work.
- Hosted database: **Neon PostgreSQL**.
- Backend/API: **Cloudflare Worker**, implemented in TypeScript.
- Authentication: **Descope**; application/domain authorization remains project-owned.
- Native clients never connect directly to Neon or hold DB credentials.
- HTTP/request-response and simple polling/refresh come before realtime infrastructure.
- Provider replaceability means sensible code locality, not provider-abstraction frameworks.

See `docs/ARCHITECTURE.md` for the full record.

## Implemented technical foundation

PR #4 merged the initial scaffold into canonical `main` on 2026-08-30.

The repository now contains:

- `shared/` — one Kotlin Multiplatform shared module with SQLDelight foundation and smoke tests;
- `androidApp/` — Android Kotlin/Jetpack Compose application shell using stable Material 3;
- `desktopApp/` — Kotlin/Compose Multiplatform Desktop shell using the stable Material line rather than an alpha-only Desktop Material 3 dependency;
- `backend/` — TypeScript Cloudflare Worker shell with a minimal health endpoint;
- `database/` — PostgreSQL migration/data-loading area without speculative domain schema;
- `.github/workflows/scaffold-check.yml` — one simple build/test workflow.

The scaffold intentionally does **not** yet implement authentication, hosted synchronization, campaigns, characters, combat, PDF export, SRD retrieval, realtime transport, or deployment automation.

## Development prerequisites

Current scaffold prerequisites:

- JDK 17;
- Gradle 9.5;
- Android SDK platform 36;
- Node.js 22;
- npm.

`minSdk` remains API 30 / Android 11. Android stays on the stable SDK 36 path instead of depending on the Android 17/API 37 preview SDK channel.

A Gradle wrapper JAR is not currently committed because the repository connector could not safely transfer the official binary wrapper JAR. CI provisions pinned Gradle 9.5 directly; a normal local Git workflow can add the standard wrapper later.

## Build and verification commands

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Desktop shell:

```bash
gradle :desktopApp:run
```

Backend:

```bash
cd backend
npm install
npm run check
```

See `docs/TESTING.md` for the latest verified revision.

## Current status

**Phases 0–2 are complete and canonical on `main`. Phase 3 — the first real vertical slice — is now current.**

The first selected slice is intentionally small: **local Android campaign creation and active-campaign selection**, backed by shared Kotlin + SQLDelight. It should not introduce Descope, Neon, hosted sync, realtime, PDF, or SRD infrastructure.

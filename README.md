# dnd_custom_aid

Personal tabletop RPG assistant project beginning with D&D, with Android phone/tablet live use and a native desktop DM preparation/administration workflow.

## Start here

This repository is designed so a new human collaborator, ChatGPT conversation, coding agent, or other AI can resume the project from Git alone.

Read in this order:

1. `AGENTS.md` — mandatory operating rules;
2. `MANIFEST.md` — map of authoritative/project-memory files and implemented areas;
3. `docs/PROJECT_STATE.md` — authoritative current state and next action for the branch you are on;
4. `docs/checkpoints/LATEST.md` — exact practical resume pointer for that branch;
5. `docs/BRANCH_STATUS.md` — canonical lifecycle map for every surviving branch/ref;
6. `docs/DECISIONS.md` plus relevant detailed records under `docs/decisions/`;
7. `docs/CONVENTIONS.md`;
8. `docs/PRODUCT.md`;
9. `docs/ROADMAP.md`;
10. `docs/WORKFLOW.md`;
11. `docs/ARCHITECTURE.md`;
12. `docs/TESTING.md`;
13. relevant current checkpoints/feature files.

## Current repository authority

There are currently **two active authoritative lines**:

- `main` — canonical global navigation plus current Phase 5A/DM product-discovery decisions;
- `implementation/phase4a-successor-cycle` — authoritative current Player/Phase 4A runtime, repair and QA line.

These branches intentionally contain different valid work. `main` is **not** currently the latest Player runtime, and the Player successor branch must not overwrite later DM/Phase 5A discovery records on `main`.

Use `docs/BRANCH_STATUS.md` rather than reconstructing authority from branch names or commit chronology.

All other surviving implementation/discovery/architecture/foundation branches are historical milestone/audit refs unless that file explicitly says otherwise. Frozen QA refs remain immutable evidence.

## Working relationship

AI/coding agents perform the heavy technical execution. The owner remains the decision owner for consequential product/UX/game-semantic/data/privacy/service/compatibility choices.

Meaningful work must be explained and persisted in Git. C-0009 remains controlling: this is a personal/small-scale project, so use the simplest safe implementation that satisfies real requirements and do not import enterprise machinery without a concrete reason.

## Approved architecture snapshot

- Android: **Kotlin + Jetpack Compose**, minimum Android 11 / API 30.
- Android targets **phone and tablet**, portrait and landscape.
- Desktop DM administration: **Kotlin + Compose Multiplatform Desktop**.
- Local persistence: **SQLite + SQLDelight** where offline/local behavior provides real value.
- Desktop MVP direction: **Save locally + explicit Sync** when hosted sync is implemented.
- Hosted database: **Neon PostgreSQL**.
- Backend/API: **Cloudflare Worker**, TypeScript.
- Authentication: **Descope**; application/domain authorization remains project-owned.
- Native clients never connect directly to Neon or hold DB credentials.
- HTTP/request-response and simple polling/refresh come before realtime infrastructure.
- Provider replaceability means sensible code locality, not provider-abstraction frameworks.

See `docs/ARCHITECTURE.md` for the full record.

## Current Player / Phase 4A reality

Phases 0–3 are complete. Phase 4A Character Foundation Closure remains open, but its accepted Player repair implementation is no longer an unfinished coding backlog.

The accepted P1–P16 repair set is implemented and automation-qualified on `implementation/phase4a-successor-cycle`.

Current physical owner-QA candidate:

- version: `0.4.0-preqa.9`;
- versionCode/build: `40900`;
- candidate commit: `cd0c203d337c062fa388010d300e875f2f54ced7`;
- Scaffold workflow run: `34726572588` — **SUCCESS**;
- artifact ID: `10307444450`;
- artifact name: `dnd-custom-aid-debug-apk`;
- artifact digest: `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

P17 is the already-defined physical tablet-QA gate policy, not another hidden implementation increment. Physical owner/device acceptance has **not** yet occurred, and Phase 4A is **not** explicitly closed.

For exact Player continuation, remain on `implementation/phase4a-successor-cycle` and read:

1. `docs/checkpoints/LATEST.md`;
2. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_QA_CANDIDATE.md`;
3. `docs/PROJECT_STATE.md`.

Do not restart P1–P16 absent actual physical QA evidence that reopens a specific repair boundary.

## Current DM / Phase 5A reality

`main` contains later accepted Phase 5A/DM product-discovery/design records, including the current Desk-family and shared Player/DM rules-question direction.

This is an active **discovery/design** line, not an implementation authorization.

**DM feature implementation remains blocked until Phase 4A receives physical owner/device acceptance and explicit owner closure.**

For DM/product discovery, use `main` and follow its `docs/checkpoints/LATEST.md`.

## Current authorization boundary

The durable Player authorization covers the accepted P1–P17 Phase 4A repair/validation cycle, QA packaging/checkpoints, and repairs reopened by real QA evidence.

The owner's 2026-09-12 instruction additionally authorizes continuity correction on all appropriate branches, including `main`, and continuation within the real existing authorizations.

That does **not** authorize:

- unrelated Player feature invention while the project is at the owner-QA gate;
- self-awarded physical owner acceptance from CI;
- destructive history rewriting;
- DM feature implementation before explicit Phase 4A closure.

## Build and verification commands

Kotlin / Android / Desktop / SQLDelight:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Backend:

```bash
cd backend
npm install --no-package-lock
npm run check
```

Current CI uses JDK 17, Gradle 9.5, Android SDK platform 36 and Node.js 22.

See `docs/TESTING.md` for verification rules.

## Development signing note

Development CI uses a stable **debug-only** Android signing identity so successive QA APKs can update one another in place and exercise real migrations. It is not a production/release identity and must never be reused for a real release.

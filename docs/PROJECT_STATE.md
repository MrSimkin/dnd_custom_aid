# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `main` — Phase 3 feature branch to be created next  
**Open review:** none; PR #4 merged 2026-08-30  
**Phase:** Phase 3 — First Vertical Slice  
**Status:** Architecture and minimal scaffold are canonical and CI-verified. No broad MVP feature implementation has begun.

## 1. Canonical baseline

The approved product/architecture baseline is D-0034 through D-0043 plus the 2026-08-30 C-0009 proportionality clarifications.

Key implementation constraints:

- personal/small-scale project; simplest safe solution first;
- Android native Kotlin + Jetpack Compose, minimum API 30;
- native Kotlin + Compose Multiplatform Desktop;
- SQLite + SQLDelight selectively for useful local/offline behavior;
- Desktop Save locally + explicit Sync;
- Cloudflare Worker/HTTP first; realtime/storage extras only when a real feature needs them;
- Neon PostgreSQL and Descope are approved but should not be activated before a feature needs hosted data/authentication;
- no generalized provider/sync frameworks;
- one authoritative DM combat device + increasing sequence/version for MVP;
- player offline turn/condition convenience is ephemeral and never synchronized.

## 2. Canonical scaffold

PR #4 merged on 2026-08-30. Merge commit: `d50409270db52df05508f91363bf76385030a77d`.

`main` now contains:

- `shared/` — one Kotlin Multiplatform module;
- SQLDelight starter database/code generation and an in-memory SQLite smoke test;
- `androidApp/` — native Android Kotlin/Jetpack Compose shell;
- `desktopApp/` — Kotlin/Compose Multiplatform Desktop shell;
- `backend/` — TypeScript Cloudflare Worker shell with a minimal `/health` endpoint;
- `database/` — PostgreSQL migration/data-loading area without speculative domain tables;
- one simple GitHub Actions workflow.

Android uses stable Material 3. Desktop remains on the stable Compose Material line because the current official KMP Material 3 path uses a separate alpha dependency; no alpha dependency was added merely for UI symmetry.

## 3. Toolchain

- JDK 17
- Gradle 9.5
- Kotlin 2.4.10
- Android Gradle Plugin 9.3.2
- Android `minSdk 30`, compile/target SDK 36
- stable AndroidX Compose BOM path
- Compose Multiplatform 1.12.0 for Desktop
- SQLDelight 2.3.2
- Node.js 22 in CI
- TypeScript 6.0.3
- Wrangler 4.127.1

Android intentionally remains on stable SDK 36 rather than the Android 17/API 37 preview SDK channel.

## 4. Last verification

Final scaffold branch head: `2f8746de1053bf97cc18d7a522f2027e91879251`.

GitHub Actions run #14 passed on that exact head:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

- shared Kotlin: success;
- SQLDelight generation/smoke test: success;
- Android debug assembly: success;
- Desktop build: success.

Backend:

```bash
cd backend
npm install
npm run check
```

- Wrangler type generation: success;
- TypeScript type check: success.

No meaningful device UX testing has occurred yet because only shell UI exists.

## 5. Known non-blocking tooling notes

- The KMP `androidLibrary` target DSL currently emits a deprecation warning; the current official Kotlin KMP application template still uses that API, so no custom workaround is warranted.
- The Gradle wrapper JAR is not committed because the available repository connector could not safely transfer the official binary JAR. CI provisions Gradle 9.5 directly. A normal local Git workflow can add the standard wrapper later.

Neither blocks Phase 3.

## 6. Explicitly not implemented yet

- Descope authentication;
- Neon/Hyperdrive connection;
- R2 / Durable Objects / WebSockets / queues;
- Workers AI;
- PDFBox integration;
- synchronization outbox/revision behavior;
- campaign/character/NPC/monster/encounter/combat domain models;
- deployment/release automation.

Do not infer implementation merely from approved architecture.

## 7. Phase 3 first vertical slice

Selected slice: **local Android campaign creation and active-campaign selection**.

Why this slice:

- campaign selection is already approved MVP behavior;
- it creates a real user workflow rather than more infrastructure;
- it proves Android Material 3 UI + shared Kotlin + SQLDelight persistence;
- campaign identity becomes a useful parent boundary for later character/combat data;
- it avoids Descope, Neon, hosted synchronization, realtime, PDF and SRD work.

Initial slice boundary:

1. Android shows locally stored campaigns.
2. User can create a campaign with a nonblank name.
3. Campaign persists locally.
4. User can select one campaign as active.
5. Active selection persists locally across app restart.
6. Duplicate campaign names are allowed; identity is by stable ID.

Explicitly out of scope for this slice:

- rename/delete;
- invites/membership/roles;
- remote accounts;
- hosted campaign synchronization;
- desktop campaign UI;
- characters, encounters or combat.

## 8. Immediate next action

Create a focused Phase 3 branch from current `main` and implement the local campaign create/select slice. Keep the data model minimal and add only tests needed to protect campaign creation, persistence and active selection.

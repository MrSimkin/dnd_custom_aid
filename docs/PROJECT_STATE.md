# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `implementation/local-campaign-selection`  
**Open review:** none; no Phase 3 PR has been opened  
**Phase:** Phase 3 — First Vertical Slice  
**Status:** First vertical slice is code-complete and CI-green on the working branch. The Android UI now follows the approved Spanish product-language convention. Manual Android device/emulator UX verification is still pending.

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

`main` contains the verified Phase 2 scaffold:

- `shared/` — one Kotlin Multiplatform module;
- SQLDelight starter database/code generation;
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

Latest verified feature code head: `c909cd34f34137b2ce74691fe195dfd48f6a02f2`.

GitHub Actions run #25 passed on that exact head:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

- shared Kotlin and campaign repository tests: success;
- SQLDelight generation: success;
- Android debug assembly: success;
- Desktop build: success;
- Android debug APK artifact upload: success.

Backend:

```bash
cd backend
npm install
npm run check
```

- Wrangler type generation: success;
- TypeScript type check: success.

The campaign persistence test closes SQLite, reopens the same database file, and verifies both the campaign and active selection survive the reopen.

Manual Android device/emulator UX verification has not yet occurred.

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
- character/NPC/monster/encounter/combat domain models;
- hosted campaign synchronization, membership or roles;
- deployment/release automation.

Do not infer implementation merely from approved architecture.

## 7. Phase 3 first vertical slice

Selected slice: **local Android campaign creation and active-campaign selection**.

Implemented on `implementation/local-campaign-selection`:

1. Android shows locally stored campaigns.
2. User can create a campaign with a nonblank name.
3. Campaign names are trimmed before persistence.
4. Campaigns use stable UUID identity; duplicate display names remain valid distinct campaigns.
5. Campaigns persist in local SQLite through SQLDelight.
6. User can select one campaign as active.
7. Active selection persists locally across database reopen/app restart at the storage level.
8. Android uses a single simple `LazyColumn` campaign screen; no navigation framework, ViewModel layer, DI container, coroutine stack or reactive database extension was added for this slice.
9. End-user campaign UI text is Spanish under C-0006.

Local schema is intentionally limited to:

```sql
campaign(id, name)
app_state(singleton, active_campaign_id)
```

Explicitly out of scope for this slice:

- rename/delete;
- invites/membership/roles;
- remote accounts;
- hosted campaign synchronization;
- desktop campaign UI;
- characters, encounters or combat.

## 8. Immediate next action

Manually verify the Android campaign workflow on an emulator or physical device: launch, create at least two campaigns, select each in turn, restart the app, and confirm the active selection remains correct and the screen behaves acceptably on the intended phone/tablet form factors.

After manual UX verification, the branch can be prepared for review. Creating a PR and merging remain separate repository actions and require their normal authorization.

# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `implementation/initial-scaffold`  
**Open review:** scaffold PR to be created after this handoff commit  
**Phase:** Phase 2 — scaffold implementation complete and verified; review/merge is the remaining Phase 2 gate  
**Status:** Minimal implementation foundation exists on the focused branch and passes CI. No broad MVP feature implementation has begun.

## 1. Canonical baseline

The approved product and architecture baseline remains D-0034 through D-0043 plus the 2026-08-30 C-0009 proportionality clarifications already merged to `main` through PR #3.

Key implementation constraints remain:

- personal/small-scale project; simplest safe solution first;
- Android native Kotlin + Jetpack Compose, minimum API 30;
- native Kotlin + Compose Multiplatform Desktop;
- SQLite + SQLDelight selectively for useful local/offline behavior;
- Desktop Save locally + explicit Sync;
- Cloudflare Worker/HTTP first; realtime/storage extras only when a real feature needs them;
- Neon PostgreSQL for hosted relational data;
- Descope authentication only when authentication is implemented;
- no generalized provider/sync frameworks;
- one authoritative DM combat device + increasing sequence/version for MVP;
- player offline turn/condition convenience is ephemeral and never synchronized.

## 2. Implemented scaffold

Branch `implementation/initial-scaffold` currently contains:

- `shared/` — one Kotlin Multiplatform shared module;
- SQLDelight starter database/schema generation and an in-memory SQLite smoke test;
- `androidApp/` — native Android Kotlin/Jetpack Compose shell;
- `desktopApp/` — Kotlin/Compose Multiplatform Desktop shell;
- `backend/` — TypeScript Cloudflare Worker shell with a minimal health endpoint;
- `database/` — PostgreSQL migration/data-loading area without speculative domain tables;
- `.github/workflows/scaffold-check.yml` — one simple CI workflow.

No real campaign/character/combat/auth/PDF/SRD/realtime/domain implementation exists yet.

## 3. Current toolchain

The verified scaffold uses:

- JDK 17;
- Gradle 9.5;
- Kotlin 2.4.10;
- Android Gradle Plugin 9.3.2;
- Android `minSdk 30`, compile/target SDK 36;
- Android Jetpack Compose through the stable AndroidX Compose BOM path;
- Compose Multiplatform 1.12.0 for Desktop;
- SQLDelight 2.3.2;
- Node.js 22 in CI;
- TypeScript 6.0.3;
- Wrangler 4.127.1.

Android intentionally stays on stable SDK 36 for this scaffold rather than depending on Android 17/API 37 preview-channel tooling. This does not change the approved Android 11/API 30 minimum.

## 4. Verification

Final pre-handoff scaffold code head verified: `335cf523785a7f503186acd1057ebce72c121b27`.

GitHub Actions run #9 completed successfully on that exact head:

### Kotlin job — success

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

This verifies:

- shared Kotlin compilation;
- SQLDelight code generation;
- in-memory SQLite smoke schema/query test;
- Android debug APK assembly;
- Desktop compilation/build.

### Backend job — success

```bash
cd backend
npm install
npm run check
```

This verifies Wrangler type generation and TypeScript type checking.

No emulator/device UX test has been performed yet because the scaffold contains no meaningful user workflow.

## 5. Known non-blocking tooling note

The current Kotlin/AGP toolchain emits a deprecation warning for the KMP Android library target DSL used by the `shared` module. The current official Kotlin KMP application template still uses the same `androidLibrary` block, so the project deliberately does not churn a green scaffold merely to silence that tooling-transition warning.

There is also currently no committed Gradle wrapper JAR because the repository connector could not safely transfer the official binary wrapper JAR. CI provisions Gradle 9.5 directly. A normal local Git workflow can add the standard wrapper later without changing architecture.

Neither item blocks the scaffold.

## 6. Explicit non-implementation

Do not infer that architecture selections are already wired merely because their folders/providers were approved. The scaffold does **not** yet contain:

- Descope authentication;
- Neon/Hyperdrive connection;
- R2;
- Durable Objects;
- WebSockets;
- queues;
- Workers AI;
- PDFBox integration;
- campaign/character/NPC/monster/encounter/combat domain schema;
- synchronization outbox/revision behavior;
- deployment/release automation.

Introduce these only through concrete approved vertical slices/features.

## 7. Immediate next action

1. Commit this scaffold handoff documentation to `implementation/initial-scaffold`.
2. Open the scaffold PR against `main`.
3. Audit the complete branch diff, CI status, and mergeability.
4. Merge only under owner authorization/delegation.
5. After scaffold acceptance into `main`, Phase 2 closes.
6. Select the first deliberately small Phase 3 vertical slice based on architectural value versus implementation cost.

## 8. Next-phase principle

The first vertical slice should prove a useful real workflow rather than activate infrastructure for its own sake. Prefer the smallest slice that exercises meaningful shared domain logic + local persistence + one real UI surface. Add hosted/auth/sync pieces only if that selected user workflow genuinely needs them.

# Repository Manifest

This file inventories the project-control files and implemented code areas so a fresh human or AI can orient quickly without guessing what is authoritative or what actually exists.

## Root control files

### `README.md`
**Role:** project entry point, mandatory read order, current scaffold setup/build instructions.

### `AGENTS.md`
**Role:** mandatory operating rules for humans and AI/coding agents.

### `MANIFEST.md`
**Role:** inventory of durable project-control files and implemented code areas.

## `docs/`

### `docs/PROJECT_STATE.md`
**Role:** authoritative snapshot of current branch/review state, implemented reality, verification, blockers, and next action.

### `docs/DECISIONS.md`
**Role:** chronological significant-decision log. D-0009 is resolved; D-0034 through D-0043 define the approved initial architecture, with the 2026-08-30 proportionality clarifications controlling implementation meaning.

### `docs/decisions/`
**Role:** detailed rationale records for consequential architecture decisions. They support `docs/DECISIONS.md`; they are not a second pending decision queue.

### `docs/CONVENTIONS.md`
**Role:** durable owner-approved recurring project conventions, including C-0008 (representative SQL when useful) and C-0009 (personal-scale proportionality).

### `docs/PRODUCT.md`
**Role:** approved product scope, MVP, design direction, user groups, constraints, and product boundaries.

### `docs/ROADMAP.md`
**Role:** staged development plan and current phase.

### `docs/WORKFLOW.md`
**Role:** approved discussion, implementation, verification, review, merge, and continuity workflow.

### `docs/ARCHITECTURE.md`
**Role:** current approved architecture record and rationale.

### `docs/TESTING.md`
**Role:** verification policy, actual scaffold commands, current successful CI evidence, and known non-blocking tooling warnings.

## Implemented scaffold areas

### `shared/`
**Role:** one Kotlin Multiplatform shared module for genuinely shared domain/data logic.

**Current scaffold contents:**
- Kotlin shared source area;
- SQLDelight configuration and starter `app_meta` smoke schema;
- desktop/JVM SQLDelight smoke test using an in-memory SQLite driver;
- no real campaign/character/combat/sync domain model yet.

Keep this as one shared module initially. Do not split it into architecture-layer Gradle modules unless actual complexity later proves that useful.

### `androidApp/`
**Role:** Android application shell.

**Technology:** native Kotlin + Jetpack Compose; `minSdk 30`, current stable scaffold compile/target SDK 36.

**Current state:** builds a debug APK; feature implementation has not begun.

### `desktopApp/`
**Role:** native DM desktop/laptop application shell.

**Technology:** Kotlin + Compose Multiplatform Desktop.

**Current state:** builds successfully and can be run through Gradle; feature implementation has not begun.

### `backend/`
**Role:** project-owned TypeScript Cloudflare Worker/API area.

**Current scaffold contents:**
- minimal Worker entry point;
- health endpoint;
- Wrangler configuration;
- TypeScript/Wrangler type-check script.

**Not present yet:** Descope verification, Neon/Hyperdrive integration, authorization, synchronization endpoints, Workers AI, R2, Durable Objects, WebSockets, queues, or deployment automation.

### `database/`
**Role:** PostgreSQL schema/migration/data-loading area for hosted relational data.

**Current state:** migration area exists deliberately without speculative application tables. Real schema should be introduced only by approved vertical slices/features.

### `gradle/` and root Gradle files
**Role:** Kotlin/Android/Desktop dependency and build configuration.

**Current scaffold:** stable toolchain pinned for reproducible CI. A Gradle wrapper JAR is not currently committed because the repository connector could not safely transfer the official binary JAR; CI provisions Gradle 9.5 explicitly.

### `.github/workflows/scaffold-check.yml`
**Role:** one deliberately small CI safety check.

**Current checks:**
- JDK 17;
- Android SDK platform 36;
- Gradle 9.5;
- shared desktop tests including SQLDelight smoke test;
- Android debug build;
- Desktop build;
- Node 22 backend install/type check.

It is not a deployment pipeline.

## `docs/discovery/`
**Role:** historical product-discovery material and rationale. Discovery notes do not override approved product/decision records.

## `assets/character-sheets/`
**Role:** owner-provided character-sheet presentation assets and owner-side layout change log.

### `assets/character-sheets/templates/`
Blank/custom PDF presentation templates supplied by the owner; not the authoritative character data model.

### `assets/character-sheets/CHANGE_REQUESTS.md`
Owner-side InDesign/PDF layout change requests exposed by implementation needs.

## `docs/templates/`
Contains optional feature/decision/handoff templates. Use them proportionately; do not manufacture documentation merely because a template exists.

## Current exclusions

The scaffold intentionally does **not** yet contain:

- Descope authentication integration;
- Neon database connection;
- Hyperdrive configuration;
- R2;
- Durable Objects;
- WebSockets;
- queues;
- generalized provider abstractions;
- generalized synchronization framework;
- campaign/character/combat feature models;
- PDF implementation;
- SRD retrieval/Workers AI implementation;
- deployment/release automation.

Add these only when an approved concrete feature actually needs them.

## Authority rule

If documents appear to conflict:

1. follow `AGENTS.md` for governance/working rules;
2. follow `docs/DECISIONS.md` for approved significant decisions and later approved clarifications;
3. use matching `docs/decisions/` files for deeper rationale;
4. follow `docs/CONVENTIONS.md` for approved recurring practices;
5. follow `docs/PRODUCT.md` for approved requirements/direction;
6. follow `docs/PROJECT_STATE.md` for current implementation/work status;
7. treat `docs/discovery/` as historical/provisional context unless conclusions were promoted;
8. surface material contradictions instead of guessing.

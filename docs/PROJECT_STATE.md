# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** none; character-foundation scope is being defined before implementation  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Phase 3 is complete and canonical on `main` after PR #5. Post-merge CI is green. The owner selected the initial Phase 4 order: character data foundation first, then DM combat tracker.

## 1. Canonical baseline

The approved product/architecture baseline is D-0034 through D-0043 plus the 2026-08-30 C-0009 proportionality clarifications.

Key implementation constraints:

- personal/small-scale project; simplest safe solution first;
- Android native Kotlin + Jetpack Compose, minimum API 30;
- native Kotlin + Compose Multiplatform Desktop;
- SQLite + SQLDelight selectively for useful local/offline behavior;
- Desktop Save locally + explicit Sync;
- Cloudflare Worker/HTTP first; realtime/storage extras only when a real feature needs them;
- Neon PostgreSQL and Descope are approved but should not be activated before a selected feature needs hosted data/authentication;
- no generalized provider/sync frameworks;
- one authoritative DM combat device + increasing sequence/version for MVP;
- player offline turn/condition convenience is ephemeral and never synchronized.

## 2. Canonical implementation state

Phase 2 scaffold is canonical through PR #4 (`d50409270db52df05508f91363bf76385030a77d`).

Phase 3 first vertical slice is canonical through PR #5, merged on 2026-08-30 as `dc1304080f0b71bcb44690b5ee317f3877385286`.

The repository currently contains:

- `shared/` — Kotlin Multiplatform shared logic and SQLDelight persistence;
- `androidApp/` — native Android Jetpack Compose app;
- `desktopApp/` — Compose Multiplatform Desktop shell;
- `backend/` — TypeScript Cloudflare Worker shell with `/health`;
- `database/` — PostgreSQL migration/data-loading area;
- one GitHub Actions build/test workflow.

Implemented user functionality:

1. Android lists locally stored campaigns.
2. A campaign can be created with a nonblank trimmed name.
3. Campaigns use stable UUID identity and duplicate display names are valid.
4. Campaigns persist in local SQLite through SQLDelight.
5. One campaign can be selected as active.
6. Active selection persists across database/app restart.
7. Current end-user campaign UI is Spanish.

Local Phase 3 schema:

```sql
campaign(id, name)
app_state(singleton, active_campaign_id)
```

## 3. Verification

PR #5 final review CI passed on head `124626aa6f0fabd449ee5823c1651e3cc01f3e70`.

Post-merge GitHub Actions run #34 also passed on canonical merge commit `dc1304080f0b71bcb44690b5ee317f3877385286`:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

and:

```bash
cd backend
npm install
npm run check
```

Successful checks include shared Kotlin/campaign tests, SQLDelight generation, Android debug assembly, Desktop build, APK artifact upload, Wrangler type generation and TypeScript type checking.

Verified behavior includes campaign-name validation/trimming, duplicate-name identity separation, campaign persistence, active-campaign switching and database close/reopen recovery.

Manual Android verification on 2026-08-30 passed on both a phone and tablet:

- install/launch succeeded;
- campaign create/select/restart behavior matched expectations;
- Spanish UI was present;
- tablet presentation was acceptable;
- landscape worked but underused horizontal space.

## 4. Known non-blocking follow-up

- Increase information density / reduce unnecessary dead space where appropriate.
- Improve wide/tablet-landscape use when future screens contain enough content to justify adaptive layouts.
- Add application theme support after theme behavior is explicitly specified.
- KMP `androidLibrary` target DSL emits a tooling-transition deprecation warning; no workaround is currently justified.
- Gradle wrapper JAR is not committed because the repository connector could not safely transfer the official binary; CI provisions Gradle 9.5.

## 5. Explicitly not implemented yet

- Descope authentication;
- Neon/hosted data connection and campaign synchronization;
- invitations/membership/roles;
- synchronization outbox/revision behavior;
- character/NPC/monster/encounter/combat domain models;
- PDFBox character-sheet export;
- SRD retrieval / Workers AI rules clarification;
- realtime Cloudflare infrastructure;
- deployment/release automation.

Do not infer implementation merely from approved architecture.

## 6. Phase 4 approved order

The owner approved this initial Phase 4 sequence on 2026-08-30:

1. **Character data foundation / Android character-sheet workflow.**
2. **DM combat tracker**, after enough stable reusable character data exists to feed quick views and combat participant snapshots.

This does not mean every character-sheet feature must be complete before combat starts. The intended dependency is a stable character-data foundation, not the entire final sheet/PDF/audit/sync system.

Existing product decisions constrain the character foundation:

- each character belongs to exactly one campaign;
- character identity is stable and globally unique;
- PC-style records may exist without an assigned player account;
- ownership and current control are separate relationships from character existence;
- the durable digital character is a complete backup/reference state, not a guided/legal character builder;
- mixed SRD generations and homebrew must be representable rather than rejected;
- useful freshness/last-updated information is required;
- mechanical edits eventually require grouped compensating audit history;
- live combat state remains separate from durable character-sheet state.

## 7. Immediate next action

Define and obtain owner approval for the first character-foundation slice boundary and its durable data shape before creating the implementation branch. Favor a small set of real structured character data that can grow additively toward the complete sheet and feed later DM quick views/combat, without prematurely implementing PDF export, hosted sync/auth, the full audit subsystem or a guided character builder.

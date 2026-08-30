# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `architecture/phase2-topology`  
**Open review:** none yet  
**Phase:** Phase 2 — Technical Options and Foundation / Architecture & Technology Evaluation  
**Status:** Phase 1 is complete. Architecture evaluation is active on `architecture/phase2-topology`. Four architecture decisions are approved: multi-client target shape, MVP localhost desktop administration, native Android with Kotlin + Jetpack Compose, and Room/SQLite as Android structured local persistence. No application code has been scaffolded and the broader architecture/technology set remains incomplete.

## 1. Current product baseline

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D.

Approved product shape:

- Android phone/tablet is the primary at-the-table/live-use surface.
- Desktop/laptop is an intentionally narrower DM preparation/administration companion using the same shared campaign/domain data.
- A true native desktop application is an expected later product feature in addition to a web-capable desktop surface.
- The MVP is **multicampaign**.
- Paper is normally the authoritative live character surface; the latest intentionally reconciled digital character is the durable backup/reference baseline and exposes freshness/last-update information.
- Campaigns may mix D&D 5e/SRD 5.1, D&D 5.5e/SRD 5.2.1 and homebrew; the application is not a rules enforcer.
- MVP rules clarification is official-SRD-only, may use both supported SRDs, answers in Spanish, and preserves source/version provenance.
- Monster records are complete for human use while mechanics are selectively structured and future additive enrichment must remain possible.
- Live combat is local-first and DM-authoritative: DM actions commit locally first, hosted sync is secondary/opportunistic, and older remote state must not overwrite newer authoritative DM state.
- Player offline combat-view edits are provisional and yield to authoritative DM state on reconnection.
- Campaign moderation and global application administration are separate authority layers.
- Campaign invitations are campaign-scoped, reusable until revoked/regenerated, and permit direct join without a second DM approval step.

Detailed authoritative product behavior lives in `docs/PRODUCT.md` and approved product decisions through D-0033 in `docs/DECISIONS.md`.

## 2. Phase 1 closure

Phase 1 — Product Discovery and Design is **complete**.

PR #2 was owner-approved and merged into `main` at merge commit `b5a059b8e7fb9312232ad684356af05e27331b65`.

The final Phase 1 audit found no remaining product-level contradiction or behavioral ambiguity from the identified set. D-0009 remains intentionally pending until the consequential Phase 2 architecture/technology set is sufficiently resolved.

## 3. Approved Phase 2 architecture decisions

### A-0001 — Multi-client target shape

Approved 2026-08-30.

The intended system has separate clients centered on the same durable shared domain/backend:

- dedicated Android client;
- web-capable desktop administration client;
- expected future true native desktop client.

The clients are intentionally separate product surfaces rather than one shared UI stretched across all platforms. Protocol/API details remain undecided.

### A-0002 — MVP desktop administration is local-web/localhost

Approved 2026-08-30.

The MVP desktop DM administration web client runs locally on the user's PC through a localhost application/server. It may require Internet connectivity for shared durable data. Desktop offline synchronization is not an MVP requirement. The web client should remain deployable as a hosted web application later without fundamental redesign.

### A-0003 — Native Android with Kotlin + Jetpack Compose

Approved 2026-08-30.

The Android client will be built natively in Kotlin using Jetpack Compose. Flutter, React Native and shared-UI Compose Multiplatform are not the starting approach. Kotlin Multiplatform may be reconsidered later for selective non-UI sharing only if concrete reuse justifies it.

### A-0004 — Room/SQLite for Android structured local persistence

Approved 2026-08-30.

The Android client uses Room, backed by SQLite, as its primary structured on-device persistence technology. Room will persist authoritative local live-combat state and other durable structured local/cached data that must survive process/restart interruption. DataStore may later be used for small preferences/settings, but not as the primary domain/combat store.

The Room schema does not need to mirror the hosted database schema. Local and hosted storage may use different representations because they have different responsibilities.

A-0004 does not yet select combat snapshot/event/outbox semantics, synchronization scheduling, WorkManager usage, hosted database/provider, API style or conflict-resolution protocol.

Full rationale and consequences are in `docs/ARCHITECTURE.md`.

## 4. Current technical state

No application code exists yet.

Approved:

- target multi-client topology;
- local-web/localhost MVP desktop delivery;
- native Kotlin + Jetpack Compose Android client;
- Room/SQLite Android structured local persistence.

Still unresolved includes:

- combat local state/change model and synchronization queue/outbox semantics;
- minimum Android version;
- multicampaign domain/data-model boundaries;
- combat synchronization/reconciliation mechanism;
- API protocol/contracts and networking approach;
- hosted backend/database provider;
- authentication/authorization implementation;
- local web implementation framework/launcher details;
- future native desktop framework;
- PDF generation/rendering technology;
- SRD storage/retrieval/clarification implementation;
- build/module structure, test stack and CI.

Named technologies mentioned earlier, including Neon/Postgres, remain candidates only unless separately approved.

## 5. Architecture evaluation order

Current sequence:

1. **Overall multi-client topology** — approved A-0001.
2. **Android client approach** — approved A-0003.
3. **MVP desktop delivery** — approved A-0002.
4. **Android structured local persistence technology** — approved A-0004.
5. **Local-first combat state/change persistence and synchronization queue semantics.**
6. Combat synchronization/reconciliation and player public projection.
7. Multicampaign domain/data-model boundaries.
8. Hosted backend/database/authentication/authorization/moderation boundaries.
9. PDF generation/rendering.
10. SRD corpus storage/retrieval/clarification and provenance.
11. Testing/build/CI and durable module/project conventions.

Some adjacent items may be discussed out of numerical order where one decision materially constrains another, but do not silently select unresolved consequential technologies.

## 6. Immediate next decision

The next owner-facing technical question is **how authoritative combat changes are persisted locally and queued for synchronization**.

The comparison should decide whether the Android client uses:

- only mutable current-state snapshots;
- a full event-sourced combat log;
- or a hybrid model combining current-state snapshots with a durable pending-operation/outbox queue.

The decision must preserve:

- immediate local durability before network work;
- same-device recovery after process/app/device interruption;
- efficient current-state reads for the combat UI;
- safe retries without duplicate remote application;
- explicit ordering/version semantics suitable for one authoritative DM device;
- a path to synchronized player public projections;
- enough history for synchronization correctness without accidentally turning combat into an analytics/event-sourcing product feature.

Do not infer the hosted provider or generic domain conflict strategy from this combat-specific decision.

## 7. Handoff

A fresh agent should read, in order, `README.md`, `AGENTS.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/PRODUCT.md`, `docs/ROADMAP.md`, `docs/WORKFLOW.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`, and `docs/architecture/2026-08-30_FUTURE_DESKTOP_REQUIREMENT.md`.

Treat Phase 1 product decisions as closed unless a genuinely new requirement or contradiction emerges. Treat A-0001 through A-0004 as approved Phase 2 architecture decisions on the active working branch. The broader D-0009 architecture decision remains incomplete; implementation/scaffolding must not begin yet.
# D-0038 — Local persistence uses SQLDelight/SQLite with deliberately simple project-owned synchronization

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Amended:** 2026-08-30 by the pre-main proportionality audit

Android and desktop use **SQLite through SQLDelight** as their durable local relational persistence layer. Application workflows should be local-first where this provides real user value: native clients read/write local persistence and synchronize with the hosted backend without making important offline-capable workflows depend synchronously on network availability.

## Local persistence and offline scope

- Android and desktop may share Kotlin persistence/domain/synchronization code where this reduces duplication without forcing UI parity.
- Relevant authorized campaign/domain data may be cached locally for the workflows that genuinely benefit from offline use.
- Offline support is **selective, not universal**. Joining campaigns, invitation management, account recovery, rules-AI clarification and similar inherently hosted workflows may require Internet access.
- Offline support must never imply downloading data the current user is not authorized to access.
- Active DM combat must continue locally without successful server contact, preserving D-0025/D-0033 authority semantics.
- Desktop preparation supports meaningful offline work through the local database under D-0036.

## Ordinary local mutations and synchronization

- Local domain changes and their pending-sync/outbox records are committed atomically in the same local SQLite transaction where applicable.
- The project implements its own **small, application-specific** synchronization behavior through the approved Cloudflare backend/API and Neon PostgreSQL; it does not build a generalized synchronization platform.
- Mutable synchronized entities use stable globally unique identities that can be created locally without first asking the hosted database for an identifier.
- Synchronization operations use idempotent mutation identifiers and retry-safe semantics where needed.
- Ordinary durable entities use explicit revision/version checks for optimistic concurrency rather than blind last-write-wins.
- Deliberate deletion uses a simple tombstone/soft-deletion mechanism sufficient to stop stale offline clients from unintentionally resurrecting deleted records. Exact retention timing is an implementation detail.
- If an uncommon genuine concurrent-edit conflict cannot be resolved safely, the application may surface a simple human choice rather than implementing a generalized automatic merge engine.

### Desktop MVP behavior

Desktop synchronization is intentionally user-driven and understandable:

1. **Save** writes the current work to local SQLite.
2. **Sync** sends pending local changes and retrieves applicable remote changes.
3. If Sync fails or there is no connection, the locally saved work remains intact and can be synchronized later.

A continuous background synchronization service is not required for Desktop MVP.

## Live-combat authority is simpler than ordinary multi-writer synchronization

Generic durable-data conflict rules must not override the stronger live-combat rule: one DM device is authoritative for an active encounter at a time.

For MVP:

- DM combat actions commit locally first.
- Each authoritative combat update uses a monotonically increasing combat sequence/version sufficient to reject delayed or stale older updates.
- The hosted copy must never replace a newer local authoritative DM state with an older snapshot.
- **Authority generations/lineages are not an MVP mechanism.** They are deferred until a real cross-device DM transfer/handoff feature exists.
- Seamless simultaneous authoritative editing by multiple DM devices is outside MVP.

This deliberately solves the actual same-device/offline/retry problem without pre-building a future distributed-authority system.

## Player offline combat convenience is ephemeral and never authoritative

A player who temporarily loses connectivity may continue using only a tiny local convenience layer over the last received public combat projection:

- advance the displayed current turn with a local **Next turn** action;
- add or remove visible conditions locally.

These temporary player changes:

- are **not uploaded** to the server;
- do not enter the synchronization outbox;
- never modify DM-authoritative combat state;
- need not be durable across app restart;
- are discarded/replaced by the latest DM public projection when connectivity returns.

This is a local continuity aid, not a second combat state or conflict-resolution domain.

## Transport proportionality

Start with ordinary HTTP request/response synchronization and simple refresh/polling where adequate. **WebSockets, Durable Objects, queues or other realtime coordination infrastructure are deferred** until actual use demonstrates a need that simpler HTTP behavior cannot satisfy.

## SQLDelight rationale

SQLDelight is selected over Room for this project because:

- the local schema, queries and migrations remain explicit SQL artifacts;
- SQL is a natural and inspectable project language for the owner;
- SQLDelight generates type-safe Kotlin APIs from SQL and supports the Android/JVM desktop targets needed here;
- a shared Kotlin persistence layer can be reused selectively across both native clients;
- migration/query behavior can be tested directly.

Room remains a technically strong alternative and may provide tighter Jetpack integration and more uniform bundled SQLite behavior across targets, but those advantages do not outweigh SQLDelight's SQL-first model and cross-client fit for this project.

## Boundaries not frozen by this decision

This decision does **not** freeze:

- every local or hosted table/column;
- exact UUID variant;
- exact outbox table shape;
- exact revision/tombstone field names;
- exact conflict-resolution UI;
- exact retry/background scheduling implementation where one is actually useful;
- final network serialization/protocol format.

Those may be selected during scaffolding as routine implementation details so long as they preserve the deliberately simple behavior above.

This resolves the local persistence/offline synchronization/reconciliation portion of D-0009. D-0009 is now resolved/Approved by the complete D-0034 through D-0043 architecture set.

> Detail-record note: this file preserves the fuller rationale for D-0038. The chronological authoritative entry is consolidated in `docs/DECISIONS.md`.

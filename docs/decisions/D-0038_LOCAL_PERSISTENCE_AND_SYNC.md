# D-0038 — Local persistence uses SQLDelight/SQLite with project-owned offline synchronization

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

Android and desktop will use **SQLite through SQLDelight** as their durable local relational persistence layer. Application workflows should be local-first where practical: the native clients read/write local persistence and synchronize with the hosted backend rather than making normal UI behavior depend synchronously on network availability.

## Local persistence and offline scope

- Android and desktop may share Kotlin persistence/domain/synchronization code where this reduces duplication without forcing UI parity.
- Relevant authorized campaign/domain data may be cached locally so normal client-appropriate workflows can continue offline where practical.
- Offline support must never imply downloading data the current user is not authorized to access.
- Active DM combat must continue locally without successful server contact, preserving D-0025/D-0033 authority semantics.
- Desktop preparation should support meaningful offline work consistent with D-0036.

## Local mutations and synchronization

- Local domain changes and their pending-sync/outbox records must be committed atomically in the same local SQLite transaction where applicable.
- The project will implement its own synchronization protocol through the approved Cloudflare backend/API and Neon PostgreSQL rather than adding a third-party synchronization platform as a foundational dependency.
- Mutable synchronized entities use stable globally unique identities that can be created locally without first asking the hosted database for an identifier.
- Synchronization operations should use idempotent mutation identifiers and retry-safe semantics.
- Ordinary durable entities use explicit revision/version checks for optimistic concurrency rather than blind last-write-wins.
- Ambiguous concurrent edits must be detected and reconciled explicitly; the exact user-facing conflict-resolution UI remains a later design/implementation decision.
- Synchronized deletion requires a tombstone/soft-deletion strategy sufficient to prevent stale offline clients from unintentionally resurrecting deliberately deleted records. Exact retention/cleanup timing remains an implementation detail to be tested at real scale.

## Live-combat authority is a separate synchronization domain

Generic durable-data conflict rules must not override the stronger live-combat authority rules already approved.

- One DM device lineage is authoritative for an active encounter at a time.
- Authoritative live-combat synchronization must identify the active authority lineage/generation and an ordered mutation or sequence position so stale hosted state cannot overwrite newer authoritative local DM state.
- A newer authority generation outranks all states from an older authority generation regardless of their local sequence number.
- Within the same authority generation, newer authoritative sequence/state outranks older state.
- Player devices receive synchronized public projections and never gain authority over DM combat state.
- Permitted offline player combat-view edits remain provisional/non-authoritative and yield to DM authority on reconciliation.

## SQLDelight rationale

SQLDelight is selected over Room for this project because:

- the local schema, queries and migrations remain explicit SQL artifacts;
- SQL is a natural and inspectable project language for the owner;
- SQLDelight generates type-safe Kotlin APIs from SQL and supports the Android/JVM desktop targets needed here;
- a shared Kotlin persistence layer can be reused selectively across both native clients;
- migration/query behavior can be tested directly.

Room remains a technically strong alternative and may provide tighter Jetpack integration and more uniform bundled SQLite behavior across targets, but those advantages do not outweigh SQLDelight's SQL-first model and cross-client fit for this project.

## Boundaries not frozen by this decision

This decision does **not** yet freeze:

- every local or hosted table/column;
- exact UUID variant;
- exact outbox table shape;
- exact revision/tombstone field names;
- exact conflict-resolution UI;
- exact background scheduling/retry library;
- final Android minimum SDK;
- final network serialization/protocol format.

Those may be selected during detailed architecture/scaffolding so long as they preserve this approved behavior.

This resolves the local persistence/offline synchronization/reconciliation portion of D-0009. D-0009 remains Pending until the remaining consequential architecture choices are approved.

> Safety checkpoint note: this decision is stored as a dedicated decision file on the active architecture branch so it is durable immediately. It should also be consolidated into the chronological `docs/DECISIONS.md` log before the architecture branch is merged.

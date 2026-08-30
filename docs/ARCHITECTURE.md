# Architecture Record

## Current status

**Phase:** Phase 2 architecture selection — **complete for initial scaffolding**  
**Architecture state:** Foundational choices approved.  
**Application code:** Not scaffolded yet.

Approved Phase 2 choices are D-0034 through D-0043. Together they resolve the consequential architecture questions originally tracked under D-0009 sufficiently to begin implementation scaffolding.

The project deliberately targets a **personal/small-scale architecture**. C-0009 is controlling: choose the simplest safe implementation that satisfies real approved requirements and do not import enterprise-grade complexity without a concrete reason.

## Approved architecture

### D-0034 — Hosted providers

- **Neon PostgreSQL** — durable hosted relational database.
- **Cloudflare** — stable complementary backend/API, connectivity/pooling, object storage and realtime coordination where useful.
- **Descope** — authentication only.
- Avoid foundational dependence on beta/preview provider features merely because they are temporarily free.
- Keep provider boundaries reasonably replaceable.

### D-0035 — Android client

- Native **Kotlin + Jetpack Compose**.
- Adaptive phone/tablet UI.
- No Flutter/React Native foundation.

### D-0036 — DM desktop client

- Native **Kotlin + Compose Multiplatform Desktop**.
- Desktop preparation/administration should support meaningful local/offline use.
- Android and desktop may share Kotlin logic selectively without sharing UI or requiring feature parity.

### D-0037 — Domain/data boundaries

- One shared relational PostgreSQL model with explicit global/campaign scope.
- Internal application identity is global and separate from external auth identity.
- Campaign roles are membership relationships.
- Characters belong to one campaign; existence, ownership and current control are distinct.
- Mutable personal-library content is separate from campaign copies; official versioned SRD content may be referenced canonically and copied when customized.
- Saved encounters, live encounters, durable character state, character audit history and live combat state remain distinct.

### D-0038 — Local persistence and synchronization

- Android and desktop use **SQLite via SQLDelight**.
- Native workflows are local-first where practical.
- Authorized useful subsets may be cached for offline work.
- Local mutation + outbox persistence is atomic where applicable.
- Project-owned synchronization goes through Cloudflare to Neon.
- Ordinary durable data uses stable global IDs, idempotent mutations, revisions and tombstones; avoid blind last-write-wins.
- Live combat uses separate DM-authoritative lineage/sequence semantics so stale hosted state cannot overwrite newer local DM state.
- Player combat projections remain non-authoritative.

### D-0039 — Hosted API/authorization boundary

- Native clients never connect directly to Neon and never hold PostgreSQL credentials.
- Remote reads/writes/synchronization pass through project-owned Cloudflare API/backend endpoints.
- Descope establishes identity; application logic maps it to the internal user and enforces domain permissions.
- PostgreSQL constraints/foreign keys enforce structural integrity.
- Use minimum sufficient runtime database privileges.
- Blanket enterprise-style RLS, role hierarchies, duplicated authorization engines and extensive security-audit machinery are **not** MVP requirements. Add them only for a concrete risk.

### D-0040 — PDF export

- Character-sheet PDF export is required on **Android and DM desktop**.
- Generation is local/offline from owner-controlled non-fillable PDF templates.
- Android uses **PdfBox-Android**; desktop uses **Apache PDFBox**.
- Template/layout metadata may be shared where practical without inventing a generalized cross-platform PDF subsystem.
- Export may deliberately use unsaved edits under D-0027 without saving them.

### D-0041 — SRD retrieval and clarification

- Official Spanish **SRD 5.1** and **SRD 5.2.1** are stored as versioned/provenance-preserving PostgreSQL sections/chunks.
- Initial retrieval uses PostgreSQL full-text search rather than a vector database.
- Retrieved official excerpts are supplied to a replaceable LLM integration, initially **Cloudflare Workers AI**.
- Exact model is configuration, not architecture.
- MVP answers are grounded in retrieved official SRD content, in Spanish, with source/version identification.
- Embeddings/vector/hybrid retrieval are deferred unless testing proves ordinary full-text retrieval inadequate.

### D-0042 — Android minimum version

- **minSdk 30 / Android 11**.
- The project intentionally prioritizes the real device set and a modern Android UX over hypothetical legacy-device reach.
- `compileSdk` and `targetSdk` follow current tooling/platform requirements during implementation and are not permanent compatibility promises.

### D-0043 — Minimal project structure, backend language, testing and CI

Initial implementation is deliberately small:

- **shared** — genuinely shared Kotlin domain/persistence/sync logic;
- **androidApp** — Android-specific app and Jetpack Compose UI;
- **desktopApp** — desktop-specific app and Compose Desktop UI;
- **backend** — Cloudflare Worker/API in **TypeScript**;
- **database** — PostgreSQL schema/migrations and related load scripts.

Exact generated folder/module names may vary slightly if standard tooling makes that sensible. The boundaries matter more than literal names.

Testing initially protects material risks: domain/sync behavior, combat authority, stale revisions/idempotency, SQLDelight migrations, and consequential backend auth/sync behavior. Android/desktop visual quality relies substantially on practical manual testing on the real relevant devices.

Use one simple GitHub Actions build/test workflow. No coverage gates, emulator farm, staging ceremony, automatic production deployment, enterprise quality platform or speculative module hierarchy is required.

## Governing architecture principles

- **Personal/small-scale proportionality:** C-0009.
- Shared durable domain truth lives in PostgreSQL; active DM combat is locally authoritative while running.
- Native applications should remain useful offline where reasonable.
- No privileged database credentials on clients.
- Authentication identity is distinct from domain authorization.
- Prefer stable/GA dependencies and reasonable migration paths.
- Share Kotlin code only where it genuinely reduces duplication; do not force cross-platform UI parity.
- Explain relational/data-model choices with representative SQL when useful under C-0008.
- Add complexity only in response to an actual requirement, measured problem or concrete risk.

## Architecture gate consequence

The architecture-selection gate is complete for the **initial scaffold**. D-0009 should now be treated as resolved/Approved by D-0034 through D-0043.

Low-level reversible implementation details may be chosen during scaffolding under D-0008 and existing conventions. A genuinely new consequential architecture/product choice must still be surfaced to the owner rather than silently assumed.

## Immediate next action

Before implementation begins, perform a short **documentation consolidation/review** on the active architecture branch:

1. consolidate D-0036 through D-0043 into the chronological `docs/DECISIONS.md` log and change D-0009's old Pending wording to resolved/Approved;
2. remove or amend stale product/state wording that still says desktop delivery/PDF or architecture choices are undecided;
3. verify `MANIFEST.md`, `docs/PRODUCT.md`, `docs/PROJECT_STATE.md`, `docs/CONVENTIONS.md` and the decision log agree on the approved baseline;
4. review the architecture branch against `main` before merge under D-0007.

That consolidation is documentation cleanup of already-approved decisions, not a new architecture exercise.

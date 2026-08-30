# Architecture Record

## Current status

**Phase:** Phase 2 — Technical Options and Foundation / Architecture & Technology Evaluation  
**Architecture state:** Nearly complete; final pre-scaffolding conventions remain.  
**Application code:** Not scaffolded.

Approved Phase 2 choices now include D-0034 through D-0042. The project deliberately targets a personal/small-scale architecture: choose the simplest safe implementation that satisfies approved requirements and do not import enterprise-grade complexity without a concrete reason (C-0009).

## Approved architecture choices

### D-0034 — Hosted providers
- **Neon PostgreSQL**: durable shared relational database.
- **Cloudflare**: stable complementary backend/API/connectivity/object-storage/realtime infrastructure where useful.
- **Descope**: authentication only.
- Avoid foundational dependence on Neon beta/preview features merely because they are currently free.
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
- PostgreSQL constraints/foreign keys enforce structural relational integrity.
- Use the minimum sufficient runtime database privileges.
- Do not build blanket enterprise-style RLS, role hierarchies, duplicated authorization engines, extensive security audit infrastructure or similar machinery unless a concrete project risk justifies it. RLS is optional/selective, not an MVP requirement.

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

### D-0042 — Minimum Android version
- **minSdk 30 / Android 11**.
- Android 10 and earlier are intentionally unsupported.
- The floor reflects the actual relevant device set and the owner's preference for a modern, polished Android UX rather than hypothetical legacy coverage.
- `compileSdk` and `targetSdk` remain maintenance/tooling values and may advance independently.

These decisions resolve their respective portions of D-0009. D-0009 remains Pending only until the minimal pre-scaffolding project/build/testing conventions are approved and the architecture set is consolidated.

## Governing architecture principles

- Personal/small-scale proportionality under C-0009 is a first-class constraint.
- Shared durable domain truth lives in PostgreSQL; active DM combat is locally authoritative while running.
- Native applications should remain useful offline where reasonable.
- No privileged database credentials on clients.
- Authentication identity is distinct from domain authorization.
- Prefer stable/GA dependencies and reasonable migration paths.
- Share Kotlin code where it genuinely reduces duplication; do not force cross-platform UI parity.
- Explain relational/data-model choices with representative SQL when useful under C-0008.
- Favor modern Android UX over unused legacy-device coverage.

## Remaining consequential decision

**Testing/build/CI and only the durable module/project conventions needed before scaffolding.**

This should be intentionally small. Decide only the minimum structure needed so AI agents can scaffold, build and test the Android + desktop Kotlin project consistently without prematurely designing a large modular architecture.

## Current decision under evaluation

Determine the minimal Gradle/Kotlin Multiplatform project shape, shared-code boundary, baseline automated tests/checks, and CI behavior required before application code is scaffolded. Avoid speculative feature modules, enterprise clean-architecture layers, release pipelines, coverage gates, elaborate environments or other ceremony not justified by the personal MVP.

## Important non-requirements

MVP does not require Android/desktop parity, player desktop support, desktop combat tracking, concurrent multi-device DM editing, a full D&D rules engine, automatic paper/digital merge, house-rule-aware rules clarification, co-DMs, multiple RPG systems, enterprise-grade security/observability, speculative scale infrastructure, or enterprise CI/release governance.

## Convention relationship

Durable working conventions belong in `docs/CONVENTIONS.md`. In particular:

- C-0008: show representative SQL when useful for owner review.
- C-0009: personal-scale proportionality; choose the simplest safe solution and avoid enterprise overengineering.

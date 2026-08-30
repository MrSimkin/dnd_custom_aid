# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `architecture/approved-backend-and-android`  
**Open review:** draft PR #3 — `Finalize approved Phase 2 architecture decisions`  
**Phase:** Phase 2 — architecture selection and documentation consolidation complete; owner merge review before scaffolding  
**Status:** Foundational architecture D-0034 through D-0043 is owner-approved, consolidated and committed remotely. No application code has been scaffolded yet.

## 1. Approved product baseline

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D.

- Android phone/tablet is the primary live/table surface.
- Desktop/laptop is a native DM preparation/administration companion with meaningful local/offline capability.
- MVP is multicampaign with campaign-scoped roles/permissions.
- Paper is normally live character authority; digital is the latest intentionally saved/reconciled durable baseline.
- Mixed D&D 5e/SRD 5.1, D&D 5.5e/SRD 5.2.1 and homebrew campaign content is allowed; the app is not a rules enforcer.
- Live combat is local-first and DM-authoritative; hosted sync is secondary/opportunistic and stale remote state cannot overwrite newer authoritative local state.
- Character-sheet PDF export is required on both Android and DM desktop.
- Campaign moderation and application-global administration remain distinct.

## 2. Approved Phase 2 architecture

### D-0034 — Providers
- Neon PostgreSQL for hosted durable relational data.
- Cloudflare for suitable stable backend/API/connectivity/object-storage/realtime infrastructure.
- Descope for authentication only.

### D-0035 — Android
- Native Kotlin + Jetpack Compose.

### D-0036 — Desktop
- Native Kotlin + Compose Multiplatform Desktop.
- Offline-capable preparation/administration is preferred over browser-only dependence.

### D-0037 — Domain boundaries
- One shared relational PostgreSQL model with explicit global/campaign scope.
- Global internal users; campaign membership/roles; characters belong to one campaign; ownership/control are separate.
- Personal reusable content, campaign copies, official SRD sources, saved encounters, live encounters, character state/history and combat state remain appropriately distinct.

### D-0038 — Local persistence/sync
- SQLite via SQLDelight on Android and desktop.
- Local-first workflows where practical.
- Project-owned Cloudflare↔Neon synchronization with outbox, stable IDs, idempotent mutations, revisions and tombstones for ordinary data.
- Separate stricter DM-authoritative combat reconciliation.

### D-0039 — Hosted API/authorization
- Native clients do not connect directly to Neon or hold DB credentials.
- Remote data access flows through Cloudflare backend/API.
- Descope identity maps to internal application identity; domain authorization remains project-owned.
- Use ordinary PostgreSQL integrity constraints and minimum sufficient server-side privileges.
- Do not introduce blanket enterprise-style RLS/role/security machinery without a concrete need.

### D-0040 — PDF export
- Android and DM desktop both generate character-sheet PDFs locally/offline.
- Android uses PdfBox-Android; desktop uses Apache PDFBox.
- Owner InDesign-generated PDFs remain presentation templates, not the data model.

### D-0041 — SRD retrieval/clarification
- Official Spanish SRD 5.1 and SRD 5.2.1 are stored as versioned/provenance-preserving PostgreSQL chunks.
- Retrieval initially uses PostgreSQL full-text search.
- Relevant excerpts are sent to a replaceable LLM integration, initially Cloudflare Workers AI.
- No embeddings/vector database unless real testing proves ordinary full-text retrieval insufficient.

### D-0042 — Android compatibility floor
- `minSdk = 30` / Android 11.
- Optimize for the real device set and modern Android UX instead of hypothetical legacy support.

### D-0043 — Initial code/test structure
- Keep a few obvious code areas: shared Kotlin logic, Android app, desktop app, TypeScript Cloudflare backend, and SQL/database work.
- Share logic only where genuinely common; do not share UI merely for symmetry.
- Automated tests protect consequential data/sync/combat-authority/backend behavior and database migrations.
- One simple GitHub Actions build/test workflow; no enterprise testing/deployment ceremony.

## 3. Governing implementation attitude

C-0009 is controlling: this is a personal, deliberately limited project. Use the **simplest safe architecture that satisfies actual approved requirements**. Do not add commercial-SaaS/enterprise layers, generalized infrastructure or speculative scale machinery merely because they are industry patterns.

C-0008 remains active: use concise representative SQL when it helps the owner review relational/data decisions.

## 4. Architecture gate

The consequential architecture questions tracked by D-0009 are resolved by D-0034 through D-0043. `docs/DECISIONS.md` now records D-0009 as Approved/resolved and chronologically consolidates D-0036 through D-0043.

`docs/PRODUCT.md`, `README.md`, `AGENTS.md`, `MANIFEST.md`, `docs/ARCHITECTURE.md`, `docs/ROADMAP.md`, `docs/TESTING.md` and `docs/CONVENTIONS.md` have been aligned to the selected architecture.

No further broad architecture discovery is required before the first scaffold.

## 5. Immediate next work

PR #3 is the formal owner review point for the completed Phase 2 architecture branch.

Before scaffolding:

1. perform final branch/PR verification;
2. owner approves and merges PR #3 into canonical `main` under D-0007;
3. begin the initial implementation scaffold from the newly canonical architecture state.

The first scaffold should implement only the approved foundation, not begin broad MVP feature buildout simultaneously.

## 6. Durable checkpoint

The active remote safety checkpoint is `architecture/approved-backend-and-android`. All approved architecture decisions through D-0043 and the documentation consolidation are committed remotely on that branch.

## 7. Handoff

A fresh agent should read `README.md`, `AGENTS.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/PRODUCT.md`, `docs/ARCHITECTURE.md`, `docs/CONVENTIONS.md`, `docs/ROADMAP.md`, and `docs/TESTING.md`.

Treat D-0034 through D-0043 as approved architecture. The next gate is owner review/merge of PR #3, followed by scaffolding. Apply C-0009 aggressively: personal-scale proportionality beats enterprise completeness.
# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `architecture/approved-backend-and-android`  
**Open review:** none  
**Phase:** Phase 2 — architecture selection complete; documentation consolidation/review before scaffolding  
**Status:** Foundational architecture D-0034 through D-0043 is owner-approved and committed remotely. No application code has been scaffolded yet.

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

The consequential architecture questions tracked by D-0009 are now sufficiently resolved by D-0034 through D-0043 to begin scaffolding after documentation consolidation/review.

`docs/DECISIONS.md` still contains older chronological wording in D-0009 and currently stops its consolidated architecture entries at D-0035. The dedicated approved decision files under `docs/decisions/` are authoritative checkpoints until that consolidation is completed. This is documentation debt, not an unresolved architecture choice.

## 5. Immediate next work

Perform one bounded documentation consolidation/review before scaffolding:

- consolidate D-0036 through D-0043 into `docs/DECISIONS.md` and mark D-0009 resolved/Approved;
- amend stale `docs/PRODUCT.md` wording that still describes desktop implementation form as undecided and ensure desktop PDF export is visible;
- verify `MANIFEST.md`, `docs/ARCHITECTURE.md`, `docs/CONVENTIONS.md` and this file agree;
- compare the active architecture branch to `main` for owner review/merge under D-0007.

After that checkpoint, the next project phase is **initial implementation scaffolding**, not further broad architecture discovery.

## 6. Durable checkpoint

The active remote safety checkpoint is `architecture/approved-backend-and-android`. D-0036 through D-0043 are stored as dedicated approved files under `docs/decisions/` pending chronological-log consolidation.

## 7. Handoff

A fresh agent should read `README.md`, `AGENTS.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/PRODUCT.md`, `docs/ARCHITECTURE.md`, `docs/CONVENTIONS.md`, and all dedicated files under `docs/decisions/` not yet consolidated.

Do not reopen approved architecture merely because older summary wording has not yet been cleaned up. Apply C-0009 aggressively: personal-scale proportionality beats enterprise completeness.

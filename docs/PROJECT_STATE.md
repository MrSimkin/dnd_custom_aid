# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `architecture/approved-backend-and-android`  
**Open review:** draft PR #3 — `Finalize approved Phase 2 architecture decisions`  
**Phase:** Phase 2 — architecture selection complete; pre-main proportionality cleanup and final review  
**Status:** Foundational architecture D-0034 through D-0043 is owner-approved. A pre-main audit simplified synchronization/offline/infrastructure implementation without changing the selected stack. No application code has been scaffolded yet.

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
- Cloudflare Worker/API as the initial hosted application gateway/backend.
- Descope for authentication only.
- Workers AI is used only for the SRD clarification feature when implemented.
- R2, Durable Objects, WebSockets, queues and other Cloudflare services are deferred until an implemented feature actually needs them.

### D-0035 — Android
- Native Kotlin + Jetpack Compose.

### D-0036 — Desktop
- Native Kotlin + Compose Multiplatform Desktop.
- Offline-capable preparation/administration through local SQLite/SQLDelight.
- Desktop MVP behavior is deliberately simple: **Save locally; Sync explicitly**.
- Failed Sync retains local saved work; continuous background sync is not required.

### D-0037 — Domain boundaries
- One shared relational PostgreSQL model with explicit global/campaign scope.
- Global internal users; campaign membership/roles; characters belong to one campaign; ownership/control are separate.
- Personal reusable content, campaign copies, official SRD sources, saved encounters, live encounters, character state/history and combat state remain appropriately distinct.

### D-0038 — Local persistence/sync
- SQLite via SQLDelight on Android and desktop.
- Offline/local-first support is selective, not universal.
- Ordinary synchronization is small and application-specific: outbox where needed, stable IDs, idempotent mutations, optimistic revisions and simple tombstones.
- Rare genuine ordinary conflicts may be surfaced to the user rather than handled by a generalized merge engine.
- DM combat uses one authoritative DM device and an increasing combat sequence/version for MVP.
- Authority generations/lineages are deferred until an actual cross-device DM transfer/handoff feature exists.
- Player offline combat convenience is only ephemeral local **Next turn** plus add/remove visible conditions; it is never uploaded and is replaced by DM state on reconnect.
- HTTP request/response and simple refresh/polling are preferred before realtime transport.

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
- Automated tests protect consequential data/sync/combat-sequence/backend behavior and database migrations.
- One simple GitHub Actions build/test workflow; no enterprise testing/deployment ceremony.
- Provider replaceability means sensible code locality, not provider-abstraction factories/frameworks.

## 3. Governing implementation attitude

C-0009 is controlling: this is a personal, deliberately limited project. Use the **simplest safe architecture that satisfies actual approved requirements**. Do not add commercial-SaaS/enterprise layers, generalized infrastructure or speculative scale machinery merely because they are industry patterns.

The pre-main proportionality audit made that principle concrete:

- provider selected ≠ activate every provider service;
- HTTP before realtime;
- Save+Sync instead of a Desktop sync platform;
- one DM device + sequence instead of pre-building distributed authority handoff;
- ephemeral player offline convenience instead of player conflict reconciliation;
- selective offline support rather than universal offline behavior;
- localized vendor integration rather than abstraction-framework ceremony.

C-0008 remains active: use concise representative SQL when it helps the owner review relational/data decisions.

## 4. Architecture gate

The consequential architecture questions tracked by D-0009 are resolved by D-0034 through D-0043. The pre-main proportionality audit does not reopen D-0009; it simplifies the implementation meaning of already-approved decisions.

`docs/PRODUCT.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`, `docs/CONVENTIONS.md` and the detailed D-0036–D-0043 records are being aligned to this simplified interpretation before canonical merge.

No further broad architecture discovery is required before the first scaffold.

## 5. Immediate next work

Finish the bounded consolidation/contradiction sweep, verify every change is committed remotely, and update PR #3 to reflect the final simplified architecture.

Then the remaining gate is explicit owner authorization to merge PR #3 into canonical `main` under D-0007.

After that merge, begin the initial implementation scaffold from the newly canonical architecture state. The first scaffold should implement only the approved foundation, not begin broad MVP feature buildout simultaneously.

## 6. Durable checkpoint

The active remote safety checkpoint is `architecture/approved-backend-and-android`. All architecture work and pre-main audit simplifications must be committed remotely before merge is proposed.

## 7. Handoff

A fresh agent should read `README.md`, `AGENTS.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/PRODUCT.md`, `docs/ARCHITECTURE.md`, `docs/CONVENTIONS.md`, `docs/ROADMAP.md`, `docs/TESTING.md`, and the detailed architecture records under `docs/decisions/` when deeper rationale is needed.

Treat D-0034 through D-0043 as approved architecture, with the 2026-08-30 proportionality clarifications controlling where older wording is more elaborate. The next gate is owner review/merge of PR #3, followed by scaffolding. Apply C-0009 aggressively: personal-scale proportionality beats enterprise completeness.

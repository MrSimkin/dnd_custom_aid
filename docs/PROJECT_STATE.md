# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `architecture/approved-backend-and-android`  
**Open review:** none  
**Phase:** Phase 2 — Technical Options and Foundation / Architecture & Technology Evaluation  
**Status:** Architecture evaluation is active. D-0034 through D-0039 are owner-approved and committed remotely; no application code has been scaffolded yet. The next owner-facing architecture decision is PDF generation/rendering.

## 1. Approved product baseline

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D.

- Android phone/tablet is the primary live/table surface.
- Desktop/laptop is a native DM preparation/administration companion with meaningful local/offline capability.
- MVP is multicampaign with campaign-scoped roles/permissions.
- Paper is normally live character authority; digital is the latest intentionally saved/reconciled durable baseline.
- Mixed D&D 5e/SRD 5.1, D&D 5.5e/SRD 5.2.1 and homebrew campaign content is allowed; the app is not a rules enforcer.
- Live combat is local-first and DM-authoritative; hosted sync is secondary/opportunistic and stale remote state cannot overwrite newer authoritative local state.
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

## 3. Governing implementation attitude

C-0009 is now explicit: this is a personal, deliberately limited project. Use the **simplest safe architecture that satisfies actual approved requirements**. Do not add commercial-SaaS/enterprise layers, generalized infrastructure or speculative scale machinery merely because they are industry patterns.

C-0008 also remains active: use concise representative SQL when it helps the owner review relational/data decisions.

## 4. Still unresolved before scaffolding

- PDF generation/rendering technology and template strategy.
- SRD corpus storage/retrieval/clarification architecture.
- Minimum Android version where relevant.
- Testing/build/CI and only the module/project conventions actually needed to scaffold safely.

D-0009 remains Pending until the required architecture set is complete.

## 5. Immediate next decision

Evaluate **PDF generation/rendering** narrowly against the approved personal-project requirements:

- character-sheet export must work without requiring continuous connectivity;
- normal export uses the latest saved character state;
- if unsaved edits exist, the user may deliberately export current edited values without saving them or creating audit history;
- owner-provided/custom PDF character-sheet assets should be usable where practical;
- avoid commercial licensing, server-side dependency and unnecessary cross-platform abstraction unless they provide a concrete benefit.

## 6. Durable checkpoint

The active remote safety checkpoint is `architecture/approved-backend-and-android`. Approved architecture decisions D-0036 through D-0039 are also stored as dedicated files under `docs/decisions/` pending consolidation into the chronological `docs/DECISIONS.md` log before merge.

## 7. Handoff

A fresh agent should read `README.md`, `AGENTS.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/PRODUCT.md`, `docs/ARCHITECTURE.md`, `docs/CONVENTIONS.md`, and the dedicated files in `docs/decisions/` not yet consolidated.

Continue Phase 2 from PDF generation/rendering. Apply C-0009 aggressively: personal-scale proportionality beats enterprise completeness.

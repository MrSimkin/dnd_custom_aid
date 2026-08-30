# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `architecture/approved-backend-and-android`  
**Open review:** none  
**Phase:** Phase 2 — Technical Options and Foundation / Architecture & Technology Evaluation  
**Status:** Architecture evaluation is active. Hosted backend/provider topology and Android client approach are now owner-approved and recorded; no application code has been scaffolded yet. The next owner-facing architecture decision is desktop/laptop administration delivery approach.

## 1. Current product baseline

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D.

Approved product shape:

- Android phone/tablet is the primary at-the-table/live-use surface.
- Desktop/laptop is an intentionally narrower DM preparation/administration companion using the same shared campaign/domain data.
- The MVP is **multicampaign**.
- Paper is normally the authoritative live character surface; the latest intentionally reconciled digital character is the durable backup/reference baseline and exposes freshness/last-update information.
- Campaigns may mix D&D 5e/SRD 5.1, D&D 5.5e/SRD 5.2.1 and homebrew; the application is not a rules enforcer.
- MVP rules clarification is official-SRD-only, may use both supported SRDs, answers in Spanish, and preserves source/version provenance.
- Monster records are complete for human use while mechanics are selectively structured and future additive enrichment must remain possible.
- Live combat is local-first and DM-authoritative: DM actions commit locally first, hosted sync is secondary/opportunistic, and older remote state must not overwrite newer authoritative DM state.
- Player offline combat-view edits are provisional and yield to authoritative DM state on reconnection.
- Campaign moderation and global application administration are separate authority layers.
- Campaign invitations are campaign-scoped, reusable until revoked/regenerated, and permit direct join without a second DM approval step.

Detailed authoritative behavior lives in `docs/PRODUCT.md` and approved decisions in `docs/DECISIONS.md`.

## 2. Approved Phase 2 architecture choices

### Hosted backend/provider topology — D-0034

- **Neon** is selected for hosted PostgreSQL as the durable shared relational database.
- **Cloudflare** is selected as the preferred stable complementary application-infrastructure platform for web hosting, API/backend execution, database connectivity/pooling, object storage and realtime transport/coordination where appropriate.
- **Descope** is selected for authentication only.
- Campaign roles, ownership/control, moderation and application authorization remain project-owned in PostgreSQL/application logic.
- The initial architecture must not depend on Neon beta/preview backend features merely because they are temporarily free.
- Provider boundaries must preserve practical migration options; no provider is assumed permanent.

### Android client — D-0035

- Native **Kotlin** is selected for Android.
- **Jetpack Compose** is selected for Android UI.
- Phone and tablet layouts must be adaptive.
- Flutter and React Native are not selected for the initial app.
- Kotlin Multiplatform is not required initially, though unnecessary Android coupling in domain/business logic should be avoided where straightforward.

D-0009 remains intentionally Pending because several consequential architecture choices remain unresolved.

## 3. Current technical state

No application code exists yet.

Approved:

- hosted database/provider: Neon PostgreSQL;
- application-infrastructure provider: Cloudflare for suitable stable services;
- authentication provider: Descope, authentication only;
- Android language/UI: Kotlin + Jetpack Compose.

Still unresolved:

- desktop/laptop administration implementation form and technology;
- multicampaign domain/data-model boundaries;
- local Android persistence technology;
- combat synchronization/reconciliation implementation;
- minimum Android version;
- PDF generation/rendering technology;
- SRD storage/retrieval/clarification implementation;
- build system details, project/module layout, test stack and CI.

Implementation/scaffolding remains blocked until the required architecture set is sufficiently complete and owner-approved.

## 4. Current architecture principles

- Shared durable domain data is hosted in PostgreSQL.
- Active DM combat remains locally authoritative under D-0025/D-0033 and synchronizes opportunistically.
- Descope identity is separate from internal application identity and domain authorization.
- Irreplaceable domain truth must not live only in Cloudflare or Descope.
- Prefer stable/GA capabilities over avoidable beta/preview dependencies.
- Prefer no-cost personal-scale operation where practical while preserving low-cost paid and migration paths.
- Avoid speculative cross-platform or future-system complexity until a real requirement justifies it.

## 5. Architecture evaluation order

1. overall Android + desktop/laptop topology and shared-domain relationship;
2. Android client approach;
3. desktop/laptop administration delivery approach;
4. multicampaign domain/data-model boundaries;
5. local-first combat persistence, authority, synchronization and reconciliation;
6. hosted backend/database/authentication/authorization and moderation boundaries;
7. PDF generation/rendering;
8. SRD corpus storage/retrieval/clarification and provenance;
9. testing/build/CI and durable project/module conventions.

Items 2 and the provider portion of item 6 are now approved. The current next decision is item 3.

## 6. Immediate next decision

Evaluate the **desktop/laptop DM administration delivery approach**.

Compare at least:

- normal browser-based web application;
- native desktop application;
- local web application;
- another option only if it materially fits the approved requirements better.

The desktop surface is intentionally narrower than Android and is primarily for DM preparation/administration: monster/NPC entry and organization, saved encounter preparation, campaign/account/PC administration and related desktop-friendly workflows. It does not require player desktop support, full Android parity or desktop combat tracking in MVP.

Evaluation should consider:

- comfortable keyboard/mouse data-entry workflows;
- deployment/update burden;
- reuse of the approved Cloudflare/backend topology;
- security/authentication fit with Descope;
- offline requirements, if any, for desktop preparation;
- maintainability and AI-assisted development;
- free/low-cost hosting feasibility;
- future migration and extensibility;
- avoidance of unnecessary duplicate application logic.

## 7. Durable checkpoint

The approved backend/provider and Android-client decisions are committed to the remote branch `architecture/approved-backend-and-android`. This branch is the active safety checkpoint for the current Phase 2 architecture session until review/merge.

## 8. Handoff

A fresh agent should read, in order, `README.md`, `AGENTS.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/PRODUCT.md`, `docs/ROADMAP.md`, `docs/WORKFLOW.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`, and relevant discovery notes only when historical rationale is needed.

Treat Phase 1 product decisions as closed unless a genuinely new requirement or contradiction emerges. Continue Phase 2 from the desktop/laptop administration delivery decision. Consequential choices remain owner-controlled.
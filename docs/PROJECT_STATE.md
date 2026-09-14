# Project State — global repository navigation

**Last verified:** 2026-09-14  
**Canonical global branch:** `main`  
**Authoritative Player implementation branch before convergence:** `implementation/phase4a-successor-cycle`  
**Observed Player branch HEAD:** `b9dea8ad6b17dcf3feeabba263eff1ee498f1536`  
**Frozen Player candidate:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`  
**Current global design state:** D-0071/D-0072/D-0073 closed  
**Technical readiness:** COMPLETE  
**Implementation authorization:** PENDING — no product-code/convergence work authorized by the review itself

## 1. Current authority/topology

Until the planned convergence is executed there are two valid active lines:

- `main` — current integrated-MVP product/design/architecture/governance truth;
- `implementation/phase4a-successor-cycle` — current Player runtime/local-migration/test/QA authority.

Do not mechanically overwrite one with the other.

D-0073 defines the transition. After explicit owner implementation authorization, create a dedicated convergence branch from current `main`, reconcile the Player successor deliberately, validate it, and merge the coherent baseline to `main`. After successful convergence, `main` becomes the normal integrated trunk and the Player successor becomes historical evidence.

## 2. Current Player state

Current frozen Player candidate:

- version `0.4.0-preqa.13`;
- versionCode/build `41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation pending;
- Phase 4A formally open on the historical Player line.

The Actions run was independently rechecked during technical readiness and is successful on the exact candidate SHA.

Observed successor branch HEAD `b9dea8ad...` is later documentation, not a different frozen APK candidate.

Automation is not physical owner acceptance. Preserve the existing evidence and do not restart historical Player repair work without new evidence.

## 3. Controlling integrated-MVP decisions

### D-0071 — integrated Player + Server + DM architecture

Controls the one-product ecosystem, paper/local/server authority, project-specific sync, revisions/idempotency/tombstones/conflicts, hosted foundation, object storage, audit/recovery/backup, full DM Desktop operational capability, combat authority fallback and official-SRD clarification direction.

### D-0072 — DM Desktop product/Managers

Closes the detailed Desktop product definition.

Desktop is one workbench with:

1. **Live / Workspace** — DM Screen, Stage Desk, Dungeon Desk, Combat Desk;
2. **Prepare / Manage** — PCs, NPCs, Monsters, Homebrew & Rules, Stage/Places, Dungeon/Zones, Encounters, Media/Handouts;
3. **Administration** — Campaign Manager plus System Administration.

Desktop Live uses the same game/domain semantics as Android/tablet; UX/presentation is desktop-specific.

### D-0073 — exact MVP boundary and implementation governance

Closes the exact MVP/post-MVP boundary, dependency-driven implementation strategy, planned branch convergence and owner-vs-technical responsibility rule.

Routine low-level engineering is delegated to technical agents. The owner is not to be used as a rubber stamp for schema/API/migration/sync/test minutiae.

## 4. Approved Desktop Prepare/Manage scope

### Monster Manager

Full human-usable editor, Personal/Campaign/Official scope, independent copies/provenance, dirty-improvisation cleanup, simple Packages, import/export and advisory Creature Creator Assistant. No false mathematical balance authority.

### NPC Manager

Quick NPC -> Developed NPC -> optional mechanics/stat block, with ideation/helper support and import/export. NPCs do not require combat statistics.

### Homebrew & Rules

Rules plus structured custom races/sub-races, classes/subclasses, backgrounds, feats, spells, items/magic items and other justified content families. Integrates with relevant Player/DM surfaces without becoming an automatic legality/rules engine. Homebrew-aware AI remains outside this MVP.

### Stage / Dungeon preparation

Stage/Place/Shop/NPC relationships, Scene Spine, Dungeon topology, rich Zone Briefs, Encounter Readiness, clocks and advisory triggers. Partial/paper-backed preparation remains valid; no VTT geometry/simulation.

### Encounter Manager

Personal reusable encounters, campaign copies, expected/reserve/conditional participants, encounter guidance, saved vs live lifecycle, archive vs save-as-new-template and import/export.

### PC Manager/Audit

Complete authorized PC inspection, grouped audit/history, compensating corrections, owner/controller administration, freeze/lifecycle, duplication and approved PDF export concepts. Not a Desktop Player character builder.

### Media & Handouts

Object-storage-backed images/maps/documents/handouts with stable logical references, DM-only vs Player-safe distinction, explicit reveal and direct upload from consuming Managers.

## 5. Campaign/System Administration

Campaign Manager covers lifecycle, membership, roles, invites, kick/ban, PC assignment shortcuts and campaign-wide status/navigation.

System Administration is designed for the owner as sole global administrator. Desktop may be a practical operator console; prefer backend/project APIs, but scoped provider integrations and locally protected admin credentials are allowed when genuinely useful.

Secrets must never be hard-coded, committed or stored casually in plaintext.

Full server backup/export remains MVP. Polished destructive whole-server restore is later; normal mistakes use object-level recovery/history.

## 6. Integrated-MVP boundary

The next cycle targets:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

In-scope items must not be silently demoted merely to make the MVP appear finished sooner: Player hosted integration, full DM live Android/Desktop capability, combat authority resume, all approved Managers, structured homebrew/import/export, object storage/media, PC audit, Campaign/System Admin, audit/recovery/backup and official-SRD clarification.

Explicitly deferred/generalized unless evidence requires otherwise: full VTT, comprehensive automatic character/legality engine, simultaneous authoritative co-DM combat, generic realtime/WebSockets requirement, Durable Objects/queues by default, generic ACL/sync platforms, executable homebrew engine, homebrew-aware AI, public marketplace/community, every external import format, polished one-click catastrophic restore, exhaustive event sourcing, enterprise observability and generic RPG framework.

## 7. Technical-readiness findings

Current checkpoint:

`docs/checkpoints/2026-09-14_INTEGRATED_MVP_TECHNICAL_READINESS_REVIEW.md`

Key findings:

- backend is currently only a `/health` scaffold;
- hosted PostgreSQL migrations are not yet implemented;
- Desktop is a placeholder shell;
- the mature technical asset to preserve is the Player/shared Kotlin + SQLDelight implementation;
- current branch divergence is substantial in commit count but technically reconcilable because `main` has not built a competing backend/Desktop/Player runtime;
- the successor's versioned Player backup serialization is a strong basis for hosted current-PC snapshots;
- do not mirror the entire local SQLDelight character graph into hosted PostgreSQL merely for symmetry;
- preserve current local migrations and Player CI guards during convergence;
- stale mandatory entry/governance documents found during review are repaired by the technical-readiness documentation pass.

## 8. Delegated technical direction

Current preferred technical implementation choices:

- **Ktor Client** in shared Kotlin for Android/Desktop HTTP networking;
- small versioned HTTP/JSON API with stable machine-readable errors;
- client mutation UUIDs + optimistic expected/base revisions;
- SQLDelight local outbox + scoped project-specific push/pull sync;
- Neon **serverless driver** from Cloudflare Worker initially;
- no Hyperdrive unless measured need later demonstrates value;
- explicit SQL migrations under `database/migrations/`;
- hosted PC current state as versioned JSONB snapshot plus relational authorization/index/public-projection metadata;
- Descope native Android authentication, standards-based Desktop OIDC/native flow and server-side token validation;
- Cloudflare **R2 Standard** as the preferred first object-storage provider, pending owner/service activation;
- versioned application-owned JSON document family for canonical imports/exports;
- versioned on-demand full backup archive with manifest and integrity information, no queue infrastructure by default.

These are technical recommendations/delegated engineering choices, not new owner product decisions unless later evidence creates a material cost/security/product consequence.

## 9. Implementation organization

### 8A — dependency-driven waves — CLOSED

Use integrated dependency waves and frequent cross-client integration. Parallel work is allowed after shared semantics exist, but streams must not redefine contracts independently.

### 8B — Git convergence — CLOSED

Deliberately reconcile `main` and the Player successor through a dedicated convergence branch. After validation, merge to `main`; then use short-lived outcome-oriented branches and frequent reintegration.

### 8C — first shared-spine direction — CLOSED / delegated

The first foundation covers identity, campaigns/membership/role, PC owner/controller, stable IDs, revisions, tombstones, scope and copy provenance. Exact schema/classes/API/migrations/tests are technical responsibilities.

## 10. Owner-vs-technical responsibility

Owner decisions:

- product behavior/workflow;
- visibility/privacy;
- MVP vs later scope;
- user-facing destructive/safety behavior;
- meaningful cost/security/convenience/lock-in tradeoffs.

Delegated technical decisions:

- schema/table layout;
- class/type decomposition;
- endpoint/request shapes;
- migration mechanics;
- internal sync structures;
- canonical serialization;
- testing architecture;
- technical package/branch granularity.

Escalate only when a technical choice materially changes product behavior, security/privacy, cost, irreversible lock-in or approved scope.

## 11. Exact continuation — owner authorization gate

Technical readiness is complete. No unresolved low-level engineering question currently requires owner choice.

The next genuine owner intervention is:

> **Authorize beginning the integrated-MVP implementation, starting with the protected `main` + Player-successor convergence.**

Until explicit authorization:

- documentation/readiness corrections are allowed;
- do not execute the product-code convergence;
- do not begin hosted/DM implementation.

After authorization:

1. refresh both refs;
2. execute/validate the dedicated convergence branch;
3. merge coherent baseline to `main`;
4. proceed with delegated technical packages;
5. return to the owner only for material product/scope/security/cost decisions, required external account/service setup, or physical/manual acceptance gates.

A likely early external action is enabling R2 if needed and securely configuring Cloudflare/Neon/Descope project secrets; this is an account/service task, not a request to decide technical schema/API design.

## 12. Release/acceptance status

The project remains development/debug and is not release-ready.

The integrated-MVP strategy and technical readiness do not retroactively fabricate Player physical acceptance. Historical evidence remains preserved at its actual scope.
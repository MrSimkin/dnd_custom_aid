# Roadmap

This roadmap defines the current development sequence. Detailed product behavior remains controlled by the approved decision records and current checkpoints.

## Phase 0 — Project Foundation

**Status:** Complete.

## Phase 1 — Product Discovery and Design

**Status:** Current integrated-MVP product definition complete.

The DM Desktop/Manager scope, exact MVP boundary, implementation governance and complete PC Sheet PDF-export behavior are closed. Reopen product discovery only when implementation exposes a real unresolved user-facing decision or the owner deliberately changes scope.

## Phase 2 — Technical Foundation

**Status:** Architecture/readiness complete; implementation authorized; integrated trunk established.

Approved foundation remains Kotlin/Compose Android, Kotlin + Compose Multiplatform Desktop, SQLDelight/SQLite local persistence, TypeScript Cloudflare Worker/API, Neon PostgreSQL and Descope authentication.

Preferred delegated technical direction remains Ktor Client, versioned HTTP/JSON API, optimistic revisions + mutation IDs, project-specific SQLDelight outbox/push-pull sync, explicit SQL migrations, hosted PC JSONB snapshots plus relational auth/index metadata, versioned app-owned import/export, on-demand versioned backups and one canonical PC/export snapshot for cross-surface PDF generation.

Cloudflare R2 Standard remains the current object-storage recommendation but has not been activated.

## Phase 3 — First Vertical Slice

**Status:** Complete.

## Phase 4A — Player Character Foundation

**Status:** Mature runtime integrated into the current baseline; historical physical evidence remains bounded.

The former Player successor head at convergence was `b9dea8ad6b17dcf3feeabba263eff1ee498f1536`.

Historical frozen candidate:

- `0.4.0-preqa.13 / 41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- artifact `10331503478`;
- targeted physical cross-device revalidation was still pending at that old boundary.

Its runtime/migrations/tests/guard scripts are integrated into `main`. Do not restart historical repair cycles without new evidence.

## Phase 4B — Integrated MVP Build

**Status:** **IN PROGRESS — owner implementation authorization granted.**

Current verified hosted/sync checkpoint:

`734477b4e276810de1581dbc2d0a8458ad953f85`

Validation run:

`34979121449` — **SUCCESS**.

The build targets one coherent product:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Internal waves are engineering controls, not separate products.

### Wave 1 — integrated baseline convergence

**Status:** COMPLETE / INTEGRATED.

The semantic convergence was promoted to `main`; the former Player successor and convergence branch are historical evidence only. `main` is the normal integrated trunk.

### Wave 2 — Shared Integrated-MVP Spine

**Status:** COMPLETE / INTEGRATED.

Implemented shared semantics include:

- global account/identity;
- Campaign;
- Membership + campaign role;
- PC owner vs current controller;
- stable IDs;
- revisions and stale-write protection;
- tombstones/non-resurrection;
- Personal/Campaign/System-or-Official scopes where valid;
- independent-copy provenance;
- basic sync metadata and invariant tests.

The implementation deliberately avoided pre-modeling every future domain entity or creating a giant universal `SyncEntity` abstraction.

### Wave 3 — Hosted foundation

**Status:** **IN PROGRESS.**

Already integrated through PR #21:

- shared spine contracts used by hosted/client work;
- `/v1` hosted API/auth/domain foundation;
- hosted PostgreSQL migrations/contracts + CI validation;
- shared Android/Desktop HTTP transport;
- durable SQLDelight hosted outbox;
- local-first campaign creation + idempotent hosted delivery;
- authenticated account/campaign bootstrap into local state with revision/tombstone/conflict protection.

**Next package:** explicit hosted campaign/membership lifecycle and scoped change semantics so clients consume real lifecycle/removal events rather than infer them from absence in a list/bootstrap response.

After that, continue toward hosted PC current-state/snapshot sync, application-owned authorization completion, audit/recovery/backup foundations, and other hosted dependencies in the order they become necessary.

Do not introduce generalized event sourcing, CRDTs, a generic sync platform, queues or realtime infrastructure by default.

#### Provider activation gate

No external provider resource needs to be created yet.

Activate the first real hosted development environment only after the remaining local campaign/membership change semantics are stable and before Wave 4 remembered-auth/real-hosted-PC integration reaches the owner-facing Player flow.

At that gate activate Cloudflare Worker/API runtime, Neon PostgreSQL and Descope authentication. Start with development/test resources and keep credentials outside Git. R2 remains deferred until Media/Handouts/assets actually require object storage.

### Wave 4 — Player ↔ Server end-to-end

Preserve the mature Player UX/runtime while adding remembered authentication, campaigns, hosted PC sync, conflicts/freshness, assets/history and public combat projection foundations.

Establish the canonical PC/export snapshot used later by all PDF-export surfaces.

### Wave 5 — Desktop shell + Campaign Administration

Build the real Desktop navigation/workbench and Campaign Manager on the same shared domain semantics.

### Wave 6 — reusable/persistent content architecture

Establish Personal → Campaign independent-copy/provenance semantics for Monsters, NPCs, Homebrew/Rules, Places/Zones, Encounters and related reusable material.

### Wave 7 — Desktop authoring Managers

Implement Monster/Creature Creator, NPC, Homebrew & Rules, Stage/Place, Dungeon/Zone, Encounter, PC Manager/Audit and Media/Handouts authoring/management workflows.

Cross-surface PC Sheet PDF export may be implemented across Waves 4–7 as dependencies become available; it must use one canonical semantic export path rather than separate incompatible exporters.

### Wave 8 — DM Live Workspace

Implement DM Screen, Stage Desk, Dungeon Desk and Combat Desk on Android/tablet and Desktop with shared game/domain semantics and platform-appropriate UX.

### Wave 9 — live combat exchange

Implement local-first single-device combat authority, hosted opportunistic exchange, Player public projection, stale-update rejection and explicit tablet/Desktop authority resume/handoff.

### Wave 10 — SRD retrieval + grounded clarification

Complete SRD 5.1/5.2.1 PostgreSQL retrieval and grounded Player/DM natural-language clarification. Homebrew-aware AI remains post-MVP.

### Wave 11 — backup/recovery/operator completion

Complete verifiable full server backup/export, meaningful recovery/admin tooling and sole-admin operational surfaces.

### Wave 12 — integrated owner-facing QA

Exercise representative Player + Server + DM flows together, including auth/campaigns, PC sync/audit, content copies, authoring, live combat/handoff, offline/reconnect, backup, PDF export and official-SRD clarification.

## Integrated-MVP protection

Do not silently demote Desktop live parity, combat handoff/resume, authoring Managers, structured homebrew/import-export, object storage/media, PC audit/correction, PC Sheet PDF export, Campaign/System Administration, backup/recovery or official-SRD clarification to stretch goals.

Still deferred unless concrete evidence requires them: full VTT/grid/LOS/fog, automatic character legality/rules engine, simultaneous authoritative co-DM combat, generalized realtime/WebSockets, Durable Objects/queues by default, generic ACL/CRDT/sync platforms, automatic encounter-balance authority, homebrew-aware AI, public marketplace/community, every third-party import format, polished one-click catastrophic restore, exhaustive event sourcing, enterprise observability and generic RPG framework.

## Git/development rule

`main` is the integrated trunk. Use short-lived outcome-oriented branches and frequent reintegration. Shared contracts merge early. Durable decisions live in documentation; branches are temporary implementation vehicles.

## Collaboration rule

The owner decides product behavior/workflow/scope/privacy and meaningful cost/security/convenience tradeoffs. Technical agents own routine schema/API/class/migration/sync/rendering/test/package decisions.

Do not stop for owner rubber-stamping of ordinary engineering. Escalate only material product/scope/security/privacy/cost/lock-in/destructive behavior, required external account/service actions, or manual/physical QA gates.

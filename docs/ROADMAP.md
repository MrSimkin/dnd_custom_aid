# Roadmap

This roadmap defines the current development sequence. Detailed product behavior remains controlled by approved decision records and current checkpoints.

## Phase 0 — Project Foundation

**Status:** Complete.

## Phase 1 — Product Discovery and Design

**Status:** Current integrated-MVP product definition complete.

The DM Desktop/Manager scope, exact MVP boundary, implementation governance and PC Sheet PDF-export behavior are closed. Reopen product discovery only when implementation exposes a real unresolved user-facing decision or the owner deliberately changes scope.

## Phase 2 — Technical Foundation

**Status:** Architecture/readiness complete; implementation authorized; integrated trunk established.

Approved foundation remains Kotlin/Compose Android, Kotlin + Compose Multiplatform Desktop, SQLDelight/SQLite local persistence, TypeScript Cloudflare Worker/API, Neon PostgreSQL and Descope identity proof.

Preferred technical direction remains Ktor Client, versioned HTTP/JSON API, optimistic revisions + mutation IDs, project-specific SQLDelight outbox/push-pull sync, explicit SQL migrations, hosted PC JSONB snapshots plus relational auth/index metadata, versioned app-owned import/export, on-demand versioned backups and one canonical PC/export snapshot for cross-surface PDF generation.

Object storage is required by the MVP but provider selection remains deferred until Media/Handouts/assets reach implementation. Do not assume R2 merely because Cloudflare is already active.

## Phase 3 — First Vertical Slice

**Status:** Complete.

## Phase 4A — Player Character Foundation

**Status:** Mature runtime integrated into the current baseline; historical physical evidence remains bounded.

Historical frozen candidate:

- `0.4.0-preqa.13 / 41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- targeted physical cross-device revalidation was still pending at that old boundary.

Its runtime/migrations/tests/guard scripts are integrated into `main`. Do not restart historical repair cycles without new evidence.

## Phase 4B — Integrated MVP Build

**Status:** **IN PROGRESS — owner implementation authorization granted.**

Current provider-neutral implementation checkpoint:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

Validation run:

`34985799585` — **SUCCESS**.

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
- sync metadata and invariant tests.

### Wave 3 — Hosted foundation

**Status:** COMPLETE / INTEGRATED / REAL DEV ENVIRONMENT VERIFIED.

Provider-neutral hosted work is integrated through PR #25 and includes:

- `/v1` hosted API/auth/domain foundation;
- explicit PostgreSQL migrations/contracts + CI validation;
- shared Android/Desktop HTTP transport;
- provider-neutral access-token boundary;
- durable SQLDelight hosted outbox;
- local-first campaign creation + idempotent hosted delivery;
- authenticated account/campaign bootstrap;
- campaign membership lifecycle reconciliation;
- hosted PC current-state snapshots;
- server-side application authorization;
- optimistic revisions/idempotency/conflict/tombstone semantics;
- safe same-identity hosted reconciliation.

The first real DEV provider gate has also been completed:

- Neon migration + real contract tests verified;
- Descope real OTP login verified;
- Cloudflare Worker deployed;
- real authenticated `/v1/me` -> application user -> Neon persistence verified;
- representative Workers Free CPU/runtime proof passed for the tested authenticated path.

See `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`.

Do not repeat provider activation and do not introduce generalized event sourcing, CRDTs, queues, WebSockets or a generic sync platform by default.

### Wave 4 — Player <-> Server end-to-end

**Status:** NEXT ACTIVE IMPLEMENTATION WAVE.

Preserve the mature Player UX/runtime while wiring the real hosted environment through existing contracts.

Dependency order:

1. remembered Android Descope session/token acquisition at the platform edge;
2. feed token into the existing `HostedAccessTokenProvider` seam;
3. owner-facing hosted account/campaign bootstrap;
4. campaign create/select + durable hosted delivery while preserving local-first behavior;
5. PC snapshot push/pull;
6. second-device observation;
7. offline edit/reconnect/convergence;
8. membership-revoke and Player/DM authorization validation.

Future materially heavier Worker routes should receive representative CPU/runtime profiling. Do not silently move to paid Workers if a route exceeds the Free budget; reassess under D-0075.

Establish the canonical PC/export snapshot used later by all PDF-export surfaces as dependencies become available.

### Wave 5 — Desktop shell + Campaign Administration

Build the real Desktop navigation/workbench and Campaign Manager on the same shared domain semantics.

### Wave 6 — reusable/persistent content architecture

Establish Personal -> Campaign independent-copy/provenance semantics for Monsters, NPCs, Homebrew/Rules, Places/Zones, Encounters and related reusable material.

### Wave 7 — Desktop authoring Managers

Implement Monster/Creature Creator, NPC, Homebrew & Rules, Stage/Place, Dungeon/Zone, Encounter, PC Manager/Audit and Media/Handouts authoring/management workflows.

Object-storage provider selection/activation occurs only when Media/Handouts/assets actually require it and must receive a fresh `$0` review.

Cross-surface PC Sheet PDF export may be implemented across Waves 4–7 as dependencies become available; it must use one canonical semantic export path rather than separate incompatible exporters.

### Wave 8 — DM Live Workspace

Implement DM Screen, Stage Desk, Dungeon Desk and Combat Desk on Android/tablet and Desktop with shared game/domain semantics and platform-appropriate UX.

### Wave 9 — live combat exchange

Implement local-first single-device combat authority, hosted opportunistic exchange, Player public projection, stale-update rejection and explicit tablet/Desktop authority resume/handoff.

### Wave 10 — SRD retrieval + grounded clarification

Complete SRD 5.1/5.2.1 PostgreSQL retrieval and grounded Player/DM natural-language clarification. Workers AI remains a conditional later provider only while it can be used safely at `$0`. Homebrew-aware AI remains post-MVP.

### Wave 11 — backup/recovery/operator completion

Complete verifiable full server backup/export, meaningful recovery/admin tooling and sole-admin operational surfaces.

### Wave 12 — integrated owner-facing QA

Exercise representative Player + Server + DM flows together, including auth/campaigns, PC sync/audit, content copies, authoring, live combat/handoff, offline/reconnect, backup, PDF export and official-SRD clarification.

## Security work across waves

Security is continuous rather than a separate enterprise phase. Current visible residuals include:

- JWT/fail-closed verification review;
- object-level authorization regression coverage;
- SQL/query and error/log hygiene;
- replay/idempotency authorization;
- exact investigation of the locally reported **3 high severity npm vulnerabilities**;
- least-privilege Neon runtime-role evaluation;
- production Descope region/configuration review.

Do not run `npm audit fix --force` blindly and do not make destructive live privilege/credential changes without a deliberate plan.

## Integrated-MVP protection

Do not silently demote Desktop live parity, combat handoff/resume, authoring Managers, structured homebrew/import-export, object storage/media, PC audit/correction, PC Sheet PDF export, Campaign/System Administration, backup/recovery or official-SRD clarification to stretch goals.

Still deferred unless concrete evidence requires them: full VTT/grid/LOS/fog, automatic character legality/rules engine, simultaneous authoritative co-DM combat, generalized realtime/WebSockets, Durable Objects/queues by default, generic ACL/CRDT/sync platforms, automatic encounter-balance authority, homebrew-aware AI, public marketplace/community, every third-party import format, polished one-click catastrophic restore, exhaustive event sourcing, enterprise observability and generic RPG framework.

## Git/development rule

`main` is the integrated trunk. Use short-lived outcome-oriented branches and frequent reintegration. Shared contracts merge early. Durable decisions live in documentation; branches are temporary implementation vehicles.

## Collaboration rule

The owner decides product behavior/workflow/scope/privacy and meaningful cost/security/convenience tradeoffs. Technical agents own routine schema/API/class/migration/sync/rendering/test/package decisions.

Do not stop for owner rubber-stamping of ordinary engineering. Escalate only material product/scope/security/privacy/cost/lock-in/destructive behavior, required new external account/service actions, or manual/physical QA gates.

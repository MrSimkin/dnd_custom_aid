# Roadmap

This roadmap defines the current development sequence. Detailed product behavior remains controlled by approved decisions and current checkpoints.

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
- targeted physical cross-device revalidation was still pending at that historical boundary.

Its runtime/migrations/tests/guard scripts are integrated into `main`. Do not restart historical repair cycles without new evidence.

## Phase 4B — Integrated MVP Build

**Status:** **IN PROGRESS — owner implementation authorization granted.**

The build targets one coherent product:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Internal waves are engineering controls, not separate products.

### Wave 1 — integrated baseline convergence

**Status:** COMPLETE / INTEGRATED.

The semantic convergence was promoted to `main`; former Player successor/convergence branches are historical evidence only.

### Wave 2 — Shared Integrated-MVP Spine

**Status:** COMPLETE / INTEGRATED.

Implemented shared semantics include global identity, Campaign, Membership/role, PC owner vs controller, stable IDs, revisions/stale-write protection, tombstones/non-resurrection, valid content scopes, independent-copy provenance, sync metadata and invariant tests.

### Wave 3 — Hosted foundation

**Status:** COMPLETE / INTEGRATED / REAL DEV ENVIRONMENT VERIFIED.

Provider-neutral hosted work is integrated through PR #25 and includes `/v1` API/auth/domain foundations, PostgreSQL migrations/contracts, shared Android/Desktop transport, provider-neutral access-token seam, durable SQLDelight outbox, campaign lifecycle/bootstrap, hosted PC snapshots, server-side authorization, optimistic revisions/idempotency/conflict/tombstone semantics and same-identity reconciliation.

The first real DEV provider gate is complete:

- Neon migration + contract tests verified;
- Descope OTP verified;
- Cloudflare Worker deployed;
- real authenticated `/v1/me` -> application user -> Neon persistence verified;
- representative Workers Free CPU/runtime proof passed for the tested path.

Do not repeat provider activation and do not introduce generalized event sourcing, CRDTs, queues, WebSockets or a generic sync platform by default.

### Wave 4 — Player <-> Server end-to-end

**Status:** ACTIVE — campaign + PC hosted sync integrated; multi-client convergence safety NEXT.

Completed Wave 4 steps:

1. **COMPLETE / INTEGRATED / OWNER-PHYSICAL PASS** — remembered Android Descope session/token acquisition;
2. **COMPLETE / INTEGRATED / OWNER-PHYSICAL PASS** — token feeds the existing `HostedAccessTokenProvider` seam;
3. **COMPLETE / INTEGRATED / OWNER-PHYSICAL PASS** — ordinary Player hosted account/campaign bootstrap;
4. **COMPLETE / INTEGRATED / OWNER-PHYSICAL PASS** — local-first campaign create + durable hosted delivery;
5. **COMPLETE / INTEGRATED / OWNER-PHYSICAL PASS** — PC snapshot push/pull plus recovery of the real blocked `VALIDATION_FAILED` mutation after the wire-envelope serializer repair.

PR #34 integrated commit:

`75d5acf354b41185255ff7d1a5eb4a689f300721`

Validation:

- exact-head Actions `35027987125` / #1939 — SUCCESS;
- post-merge Actions `35028893643` / #1940 — SUCCESS.

One precise owner observation is carried forward rather than overclaimed: an unchanged repeat Player sync followed by another empty-outbox diagnostic was requested but not separately reported before consolidation. Include that in the next batched physical gate.

See `docs/checkpoints/2026-09-15_ANDROID_HOSTED_CAMPAIGN_PC_SYNC_HANDOFF.md`.

#### Next implementation batch — multi-client PC convergence safety

Harden the sync model so a newer server revision cannot silently overwrite an unsent local edit from another client.

Add enough last-synchronized baseline knowledge to distinguish:

- old local copy still clean -> safe to apply newer hosted state;
- old local copy edited locally while hosted state also advanced -> preserve local data and surface explicit conflict;
- offline local edit with unchanged server -> reconnect and deliver normally;
- fresh second-client state -> pull the same stable hosted campaign/PC identity.

Preserve local-first semantics, stable IDs, optimistic revisions, idempotency, tombstones/non-resurrection, explicit conflicts, DM authority vs PC ownership, owner vs controller distinction and non-destructive local recovery.

The owner explicitly prefers **batched development/testing**. Accumulate closely related implementation behind automated CI and stop at a natural physical boundary rather than asking for an APK install after every small change.

The next consolidated physical gate should cover:

- carried-forward unchanged-repeat no-op confirmation;
- fresh second-client observation;
- offline edit/reconnect with no remote change;
- remote-newer clean-local convergence;
- concurrent local + remote edit preservation as an explicit conflict.

#### Following Wave 4 boundary

After multi-client convergence, validate:

- membership revoke enforcement;
- Player/DM authorization boundaries;
- owner vs controller behavior under hosted authority changes.

The debug-only `DnD Aid - Hosted DEV Auth` activity remains verification infrastructure, not final Player login UX.

Future materially heavier Worker routes should receive representative CPU/runtime profiling. Do not silently move to paid Workers if a route exceeds the Free budget; reassess under D-0075.

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

Complete SRD 5.1/5.2.1 PostgreSQL retrieval and grounded Player/DM natural-language clarification. Workers AI remains conditional later only while safe under `$0`. Homebrew-aware AI remains post-MVP.

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

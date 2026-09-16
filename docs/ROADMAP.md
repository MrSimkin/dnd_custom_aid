# Roadmap

This roadmap defines the current development sequence. Detailed product behavior remains controlled by approved decision records and current checkpoints.

## Phase 0 — Project Foundation

**Status:** Complete.

## Phase 1 — Product Discovery and Design

**Status:** Current integrated-MVP product definition complete.

Reopen product discovery only when implementation exposes a real unresolved user-facing decision or the owner deliberately changes scope.

## Phase 2 — Technical Foundation

**Status:** Architecture/readiness complete; implementation authorized; integrated trunk established.

Approved foundation remains Kotlin/Compose Android, Kotlin + Compose Multiplatform Desktop, SQLDelight/SQLite local persistence, TypeScript Cloudflare Worker/API, Neon PostgreSQL and Descope identity proof.

Preferred technical direction remains Ktor Client, versioned HTTP/JSON API, optimistic revisions + mutation IDs, project-specific SQLDelight outbox/push-pull sync, explicit SQL migrations, hosted PC JSONB snapshots plus relational auth/index metadata, versioned app-owned import/export, on-demand versioned backups and one canonical PC/export snapshot for cross-surface PDF generation.

Object storage is required by the MVP but provider selection remains deferred until Media/Handouts/assets reach implementation.

## Phase 3 — First Vertical Slice

**Status:** Complete.

## Phase 4A — Player Character Foundation

**Status:** Mature runtime integrated into the current baseline; historical physical evidence remains bounded.

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

### Wave 2 — Shared Integrated-MVP Spine

**Status:** COMPLETE / INTEGRATED.

Implemented shared semantics include global account/identity, Campaign, Membership + campaign role, PC owner/current controller distinction, stable IDs, revisions/stale-write protection, tombstones/non-resurrection, scope/provenance semantics and sync metadata/invariants.

### Wave 3 — Hosted foundation

**Status:** COMPLETE / INTEGRATED / REAL DEV ENVIRONMENT VERIFIED.

Provider-neutral hosted work includes the `/v1` API/auth/domain foundation, explicit PostgreSQL contracts, shared native transport, provider-neutral access-token seam, durable hosted outbox, local-first campaign delivery, campaign membership lifecycle reconciliation, hosted PC snapshots, application authorization, optimistic revisions/idempotency/conflict/tombstone behavior and safe same-identity reconciliation.

The real DEV provider activation completed Neon migration/contracts, Descope OTP login, Cloudflare Worker deployment, authenticated `/v1/me` -> application user -> Neon persistence and representative Workers Free CPU/runtime proof.

### Wave 4 — Player <-> Server end-to-end

**Status:** COMPLETE / INTEGRATED for the recorded scope.

Completed work includes remembered Android Descope authentication/session reuse, hosted campaign bootstrap/delivery, PC snapshot push/pull, multi-client convergence, explicit conflict resolution and membership revoke/reinstate authorization behavior. Recorded owner physical/manual gates are complete where the checkpoints state so.

Do not restart Wave 4 merely because older roadmap prose once listed intermediate steps as pending.

### Wave 5 — Desktop shell + Campaign Administration

**Status:** ACTIVE.

Completed:

- Desktop workbench + persistent local campaign context — merged/owner-QA accepted via PR #42;
- hosted Campaign membership administration backend/database/Shared core — merged via PR #43 with post-merge CI pass.

Current package:

- `wave5/desktop-hosted-campaign-administration` / draft PR #44;
- Desktop Descope email-OTP/session acquisition;
- hosted campaign bootstrap/convergence;
- real Campaign Administration roster + Player moderation consumption;
- non-secret diagnostics;
- Desktop font/theme preview settings follow-up;
- bounded repository implementation is complete and CI-verified.

**Current gate:** explicit real DEV Worker deployment/integration evidence, then owner Windows Desktop QA. Repository CI does not substitute for those gates.

### Wave 6 — reusable/persistent content architecture

Establish Personal -> Campaign independent-copy/provenance semantics for Monsters, NPCs, Homebrew/Rules, Places/Zones, Encounters and related reusable material.

### Wave 7 — Desktop authoring Managers

Implement Monster/Creature Creator, NPC, Homebrew & Rules, Stage/Place, Dungeon/Zone, Encounter, PC Manager/Audit and Media/Handouts authoring/management workflows.

Object-storage provider selection/activation occurs only when Media/Handouts/assets actually require it and must receive a fresh `$0` review.

Cross-surface PC Sheet PDF export may be implemented across relevant waves as dependencies become available; it must use one canonical semantic export path.

### Wave 8 — DM Live Workspace

Implement DM Screen, Stage Desk, Dungeon Desk and Combat Desk on Android/tablet and Desktop with shared game/domain semantics and platform-appropriate UX.

### Wave 9 — live combat exchange

Implement local-first single-device combat authority, hosted opportunistic exchange, Player public projection, stale-update rejection and explicit tablet/Desktop authority resume/handoff.

### Wave 10 — SRD retrieval + grounded clarification

Complete SRD 5.1/5.2.1 PostgreSQL retrieval and grounded Player/DM natural-language clarification. Workers AI remains conditional only while usable safely under the `$0` policy. Homebrew-aware AI remains post-MVP.

### Wave 11 — backup/recovery/operator completion

Complete verifiable full server backup/export, meaningful recovery/admin tooling and sole-admin operational surfaces.

### Wave 12 — integrated owner-facing QA

Exercise representative Player + Server + DM flows together, including auth/campaigns, PC sync/audit, content copies, authoring, live combat/handoff, offline/reconnect, backup, PDF export and official-SRD clarification.

## Security work across waves

Security is continuous rather than a separate enterprise phase. Visible residuals include JWT/fail-closed verification review, object-level authorization regression coverage, SQL/query and error/log hygiene, replay/idempotency authorization, dependency vulnerabilities, least-privilege Neon runtime-role evaluation and production identity/configuration review.

Do not run `npm audit fix --force` blindly and do not make destructive live privilege/credential changes without a deliberate plan.

## Integrated-MVP protection

Do not silently demote Desktop live parity, combat handoff/resume, authoring Managers, structured homebrew/import-export, object storage/media, PC audit/correction, PC Sheet PDF export, Campaign/System Administration, backup/recovery or official-SRD clarification to stretch goals.

Still deferred unless concrete evidence requires them: full VTT/grid/LOS/fog, automatic legality engine, simultaneous authoritative co-DM combat, generalized realtime/WebSockets, generic ACL/CRDT/sync platforms, automatic encounter-balance authority, homebrew-aware AI, public marketplace/community, exhaustive event sourcing, enterprise observability and a generic RPG framework.

## Git/development rule

`main` is the integrated trunk. Use short-lived outcome-oriented branches and frequent reintegration. Shared contracts merge early. Durable decisions live in documentation; branches are temporary implementation vehicles.

## External-provider rule

When execution reaches an authenticated provider action the agent cannot actually perform, stop at one bounded owner handoff instead of retrying access indefinitely. See `AGENTS.md`, `docs/WORKFLOW.md` and `docs/recovery/EXTERNAL_PROVIDER_HANDOFF_PROMPT.md`.

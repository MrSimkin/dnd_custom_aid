# Project State — global repository navigation

**Last verified:** 2026-09-15 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Verified hosted/sync implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Checkpoint Actions:** `34985799585` — **SUCCESS**  
**Historical Player successor:** `implementation/phase4a-successor-cycle` at `b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

## 1. Current authority/topology

The former Player/integrated split has been reconciled. **`main` is the single normal integrated-MVP development trunk.**

The old Player successor and convergence line remain historical/frozen evidence. Do not resume ordinary implementation from them merely because older documents mention them.

New implementation should use short-lived outcome-oriented branches from current `main`, validate proportionately, and reintegrate early. Do not recreate permanent Player/Server/Desktop silos.

## 2. Current verified implementation baseline

Wave 2 — **Shared Integrated-MVP Spine** — is complete and integrated, including:

- account/identity;
- Campaign;
- Membership + campaign role;
- PC owner vs current controller;
- stable object identities;
- monotonic revisions and stale-write semantics;
- tombstones/non-resurrection semantics;
- Personal/Campaign/System/Official scope models where valid;
- independent-copy provenance;
- local sync metadata and invariant tests.

Wave 3 — **provider-neutral hosted foundation** — is integrated through PR #25. The repository now includes:

- hosted `/v1` API/auth/domain foundations;
- explicit PostgreSQL hosted schema/migrations/contracts and PostgreSQL CI validation;
- shared Android/Desktop Ktor HTTP transport;
- provider-neutral token acquisition/verification seams;
- durable SQLDelight hosted outbox;
- atomic local-first campaign creation + hosted delivery using stable mutation identity;
- retry classification and confirmed-success acknowledgement;
- authenticated hosted account/campaign bootstrap;
- explicit campaign membership lifecycle reconciliation (`ACTIVE`, `KICKED`, `BANNED`) and campaign soft-deletion state;
- hosted PC current-state snapshot read/write using the existing versioned app-owned Player serialization as JSONB;
- server-side PC authorization using active campaign membership plus DM authority or Player owner/controller authority;
- DM authority explicitly separate from PC ownership;
- optimistic PC revisions, mutation idempotency, stale-write rejection and tombstone handling;
- durable PC snapshot outbox delivery with authoritative revision acknowledgement;
- same-identity hosted reconciliation distinct from user-facing backup restore-as-copy;
- guards against equal-revision overwrite, local-ahead overwrite and tombstone resurrection;
- non-destructive local recovery behavior when hosted PC state is deleted.

PR #25 merged as:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

Post-merge Actions run `34985799585` passed backend, hosted PostgreSQL contracts, shared/Kotlin tests, Android build, Desktop build, APK upload and all preserved Player guards.

This technical success does **not** retroactively convert historical Player physical QA into a PASS.

## 3. Current implementation boundary

The shared/backend/database layers are ready for a real hosted development environment. Android owner-facing composition remains intentionally local-only; it does not yet acquire a real remembered Descope session or drive the hosted campaign/PC sync paths from Player UI.

That is now the designed external-provider activation boundary, not an invitation to invent another parallel abstraction.

The next meaningful package is **real authenticated Player↔Server development integration**.

## 4. Exact next dependency

Before remembered Player authentication and real hosted Player round trips are wired into the owner-facing Android flow, the owner must activate/authorize development resources for:

1. **Cloudflare** — development Worker/API runtime;
2. **Neon** — development PostgreSQL database;
3. **Descope** — development authentication/identity project.

Then continue, in dependency order:

1. configure the development Worker against Neon + Descope without committing secrets;
2. apply/verify hosted SQL migrations;
3. wire remembered Android auth/session into the existing shared token-provider seam;
4. wire hosted campaign bootstrap/create/select while preserving local-first behavior;
5. wire PC snapshot push/pull through the existing sync foundation;
6. prove first authenticated real hosted round trip;
7. prove second-device observation, offline/reconnect/convergence and membership-revoke enforcement;
8. only then deepen DM Android/tablet/Desktop integration in dependency order.

## 5. Controlling integrated-MVP product direction

The approved product is one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Current controlling decisions define integrated Player/Server/DM architecture, paper/local/server authority, full DM Desktop product, authoring/management surfaces, MVP scope/governance and cross-surface PC Sheet PDF export.

## 6. Protected integrated-MVP scope

Do not silently demote the following to stretch goals:

- Player hosted/shared integration, remembered auth and campaign switching;
- project-specific revisions/idempotency/outbox/tombstone/conflict sync;
- object storage and Media/Handouts;
- complete DM live Workspace on Android/tablet and Desktop;
- explicit DM combat authority resume/handoff;
- Monster + Creature Creator Assistant;
- NPC Manager/helper;
- Homebrew & Rules Manager;
- Stage/Place/Scene Spine preparation;
- Dungeon/Zone/Encounter Readiness/clocks/triggers preparation;
- Encounter Manager;
- PC Manager/Audit/correction;
- PC Sheet PDF export on Player Android, DM Android/tablet and DM Desktop;
- Campaign Manager + System Administration;
- meaningful audit/history/recovery;
- full verifiable server backup/export;
- official SRD storage/retrieval/grounded Player+DM clarification.

The project remains paper-first and intentionally not a VTT, automatic legality/rules engine, generalized sync platform, marketplace/social product or enterprise infrastructure exercise.

## 7. Current technical direction

The approved technical direction remains:

- Kotlin/Compose Android + Kotlin/Compose Multiplatform Desktop;
- SQLDelight/SQLite local persistence;
- TypeScript Cloudflare Worker API;
- Neon PostgreSQL hosted persistence;
- Descope identity proof with application-owned authorization;
- Ktor Client shared Android/Desktop HTTP;
- versioned `/v1` HTTP/JSON API;
- client mutation UUIDs + optimistic revisions;
- durable SQLDelight outbox + scoped push/pull synchronization;
- explicit hosted SQL migrations and contract validation;
- hosted PC current state as versioned JSONB snapshot plus relational authorization/index/public-projection metadata;
- versioned app-owned JSON import/export;
- versioned on-demand backup archives with manifest/integrity data;
- one canonical PC/export snapshot + shared PDF-export semantics with platform-specific rendering;
- Cloudflare R2 Standard only when object-storage work is actually reached.

Exact low-level implementation choices remain delegated unless they create a material owner-level product/security/privacy/cost/lock-in consequence.

## 8. External-service activation boundary

The previous “do not activate yet” gate has now been reached.

Provider activation is **not automatically authorized by repository implementation authorization**. Accounts/resources remain owner-controlled.

Immediately before activation, verify current provider plan/region/pricing/security details and present material choices to the owner. Use development/test resources first. Secrets/tokens/connection credentials never enter Git.

Use `docs/technical/HOSTED_PROVIDER_ACTIVATION_GATE.md` for the prepared handoff.

**R2 is later** and should not be activated in this first Cloudflare + Neon + Descope step.

## 9. Security visibility note

At the current checkpoint, GitHub repository metadata reports `private: false` even though the repository has previously been described conversationally as private.

This is an owner-level privacy/security discrepancy. Do not change visibility autonomously. The owner should verify intended repository visibility before provider integration.

Regardless of visibility, never commit provider secrets.

## 10. Owner-vs-technical responsibility

The owner decides product behavior/workflow, visibility/privacy, MVP-vs-later scope, destructive/safety behavior, external account activation and meaningful cost/security/convenience/lock-in tradeoffs.

Technical agents decide routine schema/table layout, type decomposition, endpoint/request shapes, migration mechanics, sync structures, serialization, PDF rendering internals, tests and package/branch granularity.

Do not pause for ceremonial owner approval of routine engineering.

## 11. Release/acceptance status

The project remains development/debug and is not release-ready. Integrated implementation progress does not fabricate historical physical acceptance or replace future integrated owner-facing QA.

Historical frozen Player candidate remains `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`; targeted cross-device physical revalidation was pending at that historical boundary.

# Project State — global repository navigation

**Last verified:** 2026-09-15 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Hosted/sync implementation checkpoint:** `734477b4e276810de1581dbc2d0a8458ad953f85`  
**Checkpoint Actions:** `34979121449` — **SUCCESS**  
**Historical Player successor:** `implementation/phase4a-successor-cycle` at `b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

## 1. Current authority/topology

The former Player/integrated split has been reconciled. **`main` is the single normal integrated-MVP development trunk.**

The convergence line and former Player successor remain historical/frozen evidence. They should not receive ordinary new implementation work and should not be used as resume points merely because older documents mention pending work.

New implementation should use short-lived outcome-oriented branches from current `main`, validate proportionately, and reintegrate early.

Do not recreate permanent Player/Server/Desktop mainline silos.

## 2. Current verified implementation baseline

The integrated convergence baseline preserved the mature Player runtime, SQLDelight migrations, tests/guards, current product/architecture/governance and PDF-export direction.

Implementation has progressed materially beyond convergence.

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

Wave 3 — **Hosted foundation** — is **IN PROGRESS**. PRs #15–#21 established, in dependency order:

- the shared integrated spine;
- hosted `/v1` API/auth/domain foundations;
- hosted PostgreSQL schema/contracts and CI validation;
- shared Android/Desktop HTTP transport;
- durable SQLDelight hosted outbox persistence;
- atomic local campaign creation + hosted delivery with stable mutation identity and retry classification;
- authenticated hosted account/campaign bootstrap into the local spine with revision/tombstone/conflict protection.

PR #21 merged as:

`734477b4e276810de1581dbc2d0a8458ad953f85`

Post-merge Actions run `34979121449` passed all preserved Player guards, shared/Kotlin tests, Android build, Desktop build, APK upload, backend checks and hosted PostgreSQL migration/contract checks.

This technical success does **not** retroactively convert historical Player physical QA into a PASS.

## 3. Controlling integrated-MVP product direction

The approved product is one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Current controlling decisions define:

- integrated Player/Server/DM architecture and paper/local/server authority;
- full DM Desktop product and authoring/management surfaces;
- exact integrated-MVP boundary and owner-vs-technical implementation governance;
- complete cross-surface PC Sheet PDF export.

In ordinary conversation prefer descriptive names such as “the PDF export decision” rather than repeatedly referring to numeric decision IDs unless the exact repository reference matters.

## 4. Protected integrated-MVP scope

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

## 5. Current technical direction

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
- Cloudflare R2 Standard when object-storage work is actually reached.

Exact low-level implementation choices remain delegated unless they create a material owner-level product/security/privacy/cost/lock-in consequence.

## 6. Exact next implementation package

Continue Wave 3 with **explicit hosted campaign/membership lifecycle and scoped change semantics**.

Current bootstrap/list behavior must not force clients to infer `KICKED`, `BANNED`, deletion or other lifecycle state from absence alone. The next package should make those hosted changes explicit enough for safe client reconciliation while remaining project-specific.

Do not turn this into generalized event sourcing, CRDTs, queues, WebSockets or a generic sync platform without a concrete requirement.

After that package, continue in dependency order toward hosted PC current-state/snapshot sync and the Wave 4 Player↔Server end-to-end path.

## 7. Owner-vs-technical responsibility

The owner decides product behavior/workflow, visibility/privacy, MVP-vs-later scope, destructive/safety behavior and meaningful cost/security/convenience/lock-in tradeoffs.

Technical agents decide routine schema/table layout, type decomposition, endpoint/request shapes, migration mechanics, sync structures, serialization, PDF rendering internals, tests and package/branch granularity.

Do not pause for ceremonial owner approval of routine engineering.

## 8. External-service activation boundary

**No external provider must be activated yet.** Current local/shared/backend/database contracts can continue through CI without speculative provider setup.

The first activation gate is the first package that needs a **real authenticated end-to-end hosted development environment**, after the remaining campaign/membership change semantics are stable and before remembered Player authentication/real hosted PC sync is wired into the owner-facing Player flow.

At that gate, activate only:

1. **Cloudflare** — development Worker/API runtime;
2. **Neon** — development PostgreSQL database;
3. **Descope** — development authentication/identity project.

**R2 is later** and should be activated only when Media/Handouts/assets reach object-storage integration.

Provider setup rules:

- accounts/resources remain owner-controlled;
- begin with development/test resources, not production;
- choose plan, region and project settings deliberately at activation time;
- secrets/tokens/connection credentials never enter Git;
- runtime secrets belong in provider/runtime secret stores or ignored local development configuration;
- public/non-secret project identifiers may be documented when useful;
- immediately before activation, present the owner with any material plan/region/cost/security/privacy/lock-in choices and the exact owner actions required.

## 9. Release/acceptance status

The project remains development/debug and is not release-ready. Integrated implementation progress does not fabricate historical physical acceptance or replace future integrated owner-facing QA.

Historical frozen Player candidate remains `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`; targeted cross-device physical revalidation was pending at that historical boundary.

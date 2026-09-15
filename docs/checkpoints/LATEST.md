# Latest project checkpoint — global resume map

**Updated:** 2026-09-15 (Chile local time)  
**Normal implementation trunk:** `main`  
**Hosted/sync implementation checkpoint:** `da0083680565b2382605b422c8a10d21f2bf7e6a`  
**PR #23 validation:** Actions run `34981524994` — **SUCCESS**  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `docs/checkpoints/2026-09-15_HOSTED_SYNC_FOUNDATION.md` — current implementation/provider-activation checkpoint and authoritative exact next package;
2. `docs/PROJECT_STATE.md` — global product/engineering state and execution rules;
3. `docs/BRANCH_STATUS.md` — branch lifecycle/resume rule;
4. `docs/ROADMAP.md` — overall implementation-wave sequence;
5. `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` — broader engineering baseline;
6. approved integrated-MVP decision records for product-scope questions.

For ordinary implementation resume, **the exact-next-package statement here and in the current hosted-sync checkpoint supersedes older Wave 3 “next package” wording elsewhere until the next broader documentation consolidation.**

## Current repository state

`main` is the sole normal integrated-MVP development trunk. Historical Player/convergence branches remain evidence only.

Wave 2 — Shared Integrated-MVP Spine — is complete.

Wave 3 — Hosted foundation — is **IN PROGRESS through PR #23**. The repository now includes:

- shared identity/campaign/membership/role/PC-authority/scope/revision/tombstone/provenance semantics;
- hosted `/v1` API/auth/domain foundation;
- hosted PostgreSQL contracts and CI validation;
- shared Android/Desktop HTTP transport;
- durable SQLDelight hosted outbox;
- atomic local-first campaign creation + idempotent hosted delivery;
- authenticated hosted account/campaign bootstrap with revision/tombstone/conflict protection;
- explicit campaign membership lifecycle reconciliation: `ACTIVE`, `KICKED`, `BANNED`, plus campaign soft-deletion metadata;
- a preserved active-campaign projection distinct from lifecycle state;
- no fabricated removal from simple response absence.

PR #23 merged as:

`da0083680565b2382605b422c8a10d21f2bf7e6a`

Its exact branch head `40db41116530fc593eebe05d7185d4797234fae7` passed Actions run `34981524994` across backend, hosted PostgreSQL contracts, shared/Kotlin tests, Android, Desktop, APK upload and all preserved Player guards. Post-merge `main` validation run `34981917563` was started for the integrated merge.

## Exact next technical package

Continue Wave 3 with **hosted PC current-state/snapshot sync foundation**.

Use the approved hybrid representation rather than mirroring the entire local SQLDelight graph:

- relational PC identity/campaign/owner/controller/revision/lifecycle metadata;
- versioned application-owned PC snapshot JSONB;
- server-side campaign/ownership/control authorization;
- optimistic stale-revision rejection;
- mutation idempotency;
- local durable-outbox integration;
- safe pull/reconciliation;
- evolution/reuse of the existing versioned Player serialization family.

Keep audit/history/recovery separate from current state. Do not expand into generalized event sourcing, CRDTs, queues, WebSockets or a generic sync platform without concrete evidence.

## External-service activation

**You do not need to sign up for or create provider resources yet.**

The next PC snapshot package can still be designed, implemented and tested locally/in CI. The first activation gate is immediately before we need a real authenticated end-to-end hosted development round trip in the owner-facing Player integration.

At that gate we will configure a development environment using:

- **Cloudflare** — Worker/API runtime;
- **Neon** — PostgreSQL;
- **Descope** — authentication.

R2 remains later, when Media/Handouts/assets actually need object storage.

Accounts/resources remain owner-controlled. We will start with development/test resources rather than production. Credentials stay outside Git; secrets go directly into provider/runtime secret stores or ignored local development configuration. Before activation, the owner receives the exact signup/configuration checklist and any material plan/region/cost/security/privacy/lock-in choices.

## Historical Player evidence remains bounded

The historical frozen Player candidate remains evidence only; current integrated CI does not retroactively establish physical acceptance.

## Product scope

The integrated MVP remains one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Approved protected scope and owner-vs-technical responsibility boundaries remain unchanged.

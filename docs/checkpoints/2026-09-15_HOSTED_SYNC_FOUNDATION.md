# Hosted/sync foundation checkpoint — 2026-09-15

**Current implementation checkpoint:** `main` at `da0083680565b2382605b422c8a10d21f2bf7e6a`  
**PR #23 branch validation:** Actions run `34981524994` — **SUCCESS**  
**Post-merge validation:** Actions run `34981917563` — started for the same integrated code  
**Integrated-MVP implementation authorization:** **GRANTED**

> For implementation resume, the exact-next-package statement in this checkpoint and in `docs/checkpoints/LATEST.md` supersedes older Wave 3 “next package” wording elsewhere until the next broader documentation consolidation.

## 1. Current operational truth

The integrated baseline convergence is complete and `main` is the sole normal implementation trunk.

Wave 2 — Shared Integrated-MVP Spine — is implemented and integrated. The repository contains the shared account/campaign/membership/role/PC-authority/scope/revision/tombstone/provenance/sync-metadata foundation needed by later hosted and client work.

Wave 3 — Hosted foundation — is **IN PROGRESS** and materially implemented through PR #23. PRs #15–#23 established, in dependency order:

- the shared integrated-MVP spine;
- hosted `/v1` API/auth/domain foundations;
- hosted PostgreSQL schema/contracts and CI validation;
- shared Android/Desktop HTTP transport contracts;
- a durable SQLDelight hosted outbox;
- atomic local campaign creation plus hosted delivery with idempotent retry semantics;
- authenticated account/campaign bootstrap back into the local spine with revision/tombstone conflict protection;
- explicit hosted campaign-membership lifecycle state, including `ACTIVE` / `KICKED` / `BANNED` plus campaign tombstone metadata, without inferring removal from list absence.

PR #23 merged as `da0083680565b2382605b422c8a10d21f2bf7e6a`. Its exact branch head `40db41116530fc593eebe05d7185d4797234fae7` passed backend checks, hosted database contracts, shared/Kotlin tests, Android build, Desktop build, APK upload and all preserved Player guard scripts in run `34981524994`.

## 2. Current sync behavior established

Campaign creation is local-first. Local campaign creation and durable outbox enqueue are atomic. Hosted delivery reuses the same mutation identity across retries. Confirmed idempotent replay is acknowledged as success; transient/auth failures preserve local data and the outbox entry; permanent conflicts are not silently discarded.

Authenticated hosted bootstrap imports the current account and hosted campaign state into the existing local spine while protecting locally newer revisions, local tombstones and same-revision divergent state.

Lifecycle reconciliation is now explicit:

- `/v1/campaigns` remains the active/usable campaign projection;
- `/v1/campaign-memberships` is an authenticated read-only lifecycle snapshot;
- membership status is explicitly `ACTIVE`, `KICKED` or `BANNED`;
- campaign soft-deletion metadata is transported explicitly;
- kick/ban or hosted campaign deletion never destroys the local campaign row merely because hosted access changed;
- absence from a lifecycle response remains non-semantic and does not fabricate removal.

No new hosted schema migration was required for this package because the existing membership status and campaign soft-delete fields already carry the required state.

## 3. Exact next implementation package

Continue Wave 3 with **hosted PC current-state/snapshot sync foundation**.

The package should build on the already-approved model rather than mirror the entire local SQLDelight character graph into PostgreSQL. The target remains:

- relational PC identity/campaign/owner/controller/revision/lifecycle metadata;
- versioned application-owned PC snapshot JSONB;
- server-side authorization and stale-revision protection;
- mutation idempotency and durable local outbox integration;
- safe pull/reconciliation without destructive local-data assumptions;
- reuse/evolution of the existing versioned Player serialization family rather than a second incompatible character model.

Keep audit/history/recovery separate from the current snapshot and do not expose the full PC snapshot through later tiny public-identity/combat projections.

This is still local/shared/backend contract work first. Do not jump to Player UI wiring or generalized realtime infrastructure.

## 4. External-provider activation gate

**Do not create provider accounts/projects/resources yet.** The PC snapshot contract, persistence rules, serialization boundary, authorization semantics and local sync behavior can still be implemented and validated in CI without speculative external setup.

The first provider activation gate remains the first package that needs a **real authenticated end-to-end hosted development environment**, after the PC hosted-sync contracts are sufficiently stable and immediately before remembered Player authentication / real hosted PC round-trips are wired into the owner-facing flow.

At that gate, activate only the providers needed for the first real hosted round trip:

1. **Cloudflare** — development Worker/API runtime.
2. **Neon** — development PostgreSQL database.
3. **Descope** — development authentication/identity project.

**Cloudflare R2 is not part of that first activation gate.** Activate R2 later when Media/Handouts/assets actually reach object-storage integration.

Provider setup rule:

- accounts/resources remain owner-controlled;
- start with development/test resources, not production;
- choose plan/region/project settings deliberately at activation time from then-current requirements;
- secrets/tokens/connection credentials must never be committed to Git;
- runtime secrets belong in provider/runtime secret stores or ignored local development configuration;
- public/non-secret project identifiers/endpoints may be documented when useful;
- immediately before activation, present the owner with the exact signup/configuration steps and any material plan/region/cost/security/privacy/lock-in choices.

## 5. Repository rule

Branch from current `main`, use short-lived outcome-oriented branches, validate proportionately, and reintegrate early. Historical Player and convergence branches remain evidence only.

Do not restart old Player repair work from historical pending language, and do not treat this technical progress as retroactive physical acceptance of the historical Player candidate.

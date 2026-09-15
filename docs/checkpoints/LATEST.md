# Latest project checkpoint — global resume map

**Updated:** 2026-09-15 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified implementation checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Post-merge validation:** Actions `34985799585` — **SUCCESS**  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `docs/checkpoints/2026-09-15_PLAYER_SERVER_PROVIDER_BOUNDARY.md` — current implementation/provider-boundary checkpoint and exact continuation;
2. `docs/technical/HOSTED_PROVIDER_ACTIVATION_GATE.md` — safe development-provider activation handoff;
3. `docs/PROJECT_STATE.md` — current global product/engineering state;
4. `docs/BRANCH_STATUS.md` — branch lifecycle/resume rule;
5. `docs/ROADMAP.md` — overall implementation-wave sequence;
6. `docs/technical/INTEGRATED_MVP_IMPLEMENTATION_BASELINE.md` — engineering baseline;
7. approved integrated-MVP decision records for product-scope questions.

If older checkpoints or operational prose conflict with this file, this file and the current provider-boundary checkpoint control the practical resume point unless a later merged checkpoint explicitly supersedes them.

## Current repository state

`main` is the sole normal integrated-MVP development trunk. Historical Player/convergence branches are evidence only.

Wave 2 — Shared Integrated-MVP Spine — is complete.

Wave 3 provider-neutral hosted foundation is integrated through PR #25. Current implementation includes:

- shared identity/campaign/membership/role/PC-authority/scope/revision/tombstone/provenance semantics;
- hosted `/v1` API/auth/domain foundation;
- hosted PostgreSQL migration/contracts and CI validation;
- shared Android/Desktop HTTP transport and provider-neutral access-token boundary;
- durable SQLDelight hosted outbox;
- local-first campaign creation + idempotent hosted delivery;
- authenticated account/campaign bootstrap;
- explicit campaign membership lifecycle reconciliation and campaign soft-deletion state;
- hosted PC current-state snapshots using the existing versioned app-owned Player backup document as JSONB;
- server-side DM/Player authorization with DM authority distinct from PC ownership;
- optimistic PC revisions, idempotency, stale-conflict handling and tombstone/non-resurrection behavior;
- durable PC snapshot delivery and safe same-identity pull/reconciliation;
- protection against equal-revision/local-ahead silent overwrite and destructive hosted deletion of local recovery data.

PR #25 merged as:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

Post-merge Actions run `34985799585` completed SUCCESS across backend, hosted PostgreSQL contracts, shared/Kotlin tests, Android, Desktop, APK upload and all preserved Player guards.

## Exact next dependency

The next meaningful package is **real authenticated Player↔Server development integration**.

Provider-neutral prerequisites are sufficiently complete. Do not create another parallel auth/networking/sync abstraction merely to postpone the external environment.

Before owner-facing remembered authentication and real hosted round trips are wired into Player Android, the owner must authorize/create development resources for:

1. Cloudflare — Worker/API runtime;
2. Neon — PostgreSQL;
3. Descope — authentication/identity.

After activation, continue with remembered Android session/token acquisition feeding the existing shared token-provider seam, hosted campaign bootstrap/create/select, hosted PC push/pull, then two-device/offline/reconnect/revoke validation.

## External-service safety

No provider account/resource activation is authorized implicitly by this checkpoint. Accounts/resources remain owner-controlled.

Before activation, re-check current provider plan/region/pricing/security details and surface material cost/privacy/security/lock-in choices. Use development/test resources first. Never commit secrets.

R2 remains later, when Media/Handouts/assets actually need object storage.

## Security visibility note

At checkpoint capture, GitHub repository metadata reports `private: false`, although the project has previously been described conversationally as private. The owner should verify intended visibility before provider integration. Do not change repository visibility autonomously.

Regardless of repository visibility, provider credentials and secrets must remain outside Git.

## Historical Player evidence remains bounded

Current integrated CI does not retroactively establish physical acceptance of the historical frozen Player candidate.

## Product scope

The integrated MVP remains one ecosystem:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Protected integrated-MVP scope and owner-vs-technical responsibility boundaries remain unchanged.

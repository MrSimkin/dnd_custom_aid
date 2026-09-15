# Player↔Server provider-boundary checkpoint

**Captured:** 2026-09-15 (Chile local time)  
**Status:** ACTIVE implementation resume checkpoint  
**Normal implementation trunk:** `main`  
**Verified implementation commit:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Post-merge validation:** Actions `34985799585` — **SUCCESS**  
**Owner integrated-MVP implementation authorization:** **GRANTED**

This checkpoint is the durable handoff after the provider-neutral hosted/sync foundation reached the first real external-provider activation boundary.

## 1. What is integrated

Wave 2 — Shared Integrated-MVP Spine — is complete.

Wave 3 provider-neutral hosted work is integrated through PR #25, including:

- application identity/account, Campaign, Membership/role and PC owner/controller semantics;
- stable IDs, monotonic revisions, stale-write rejection and tombstone/non-resurrection semantics;
- hosted `/v1` Worker API/auth/domain foundation;
- explicit PostgreSQL migrations/contracts validated in CI;
- shared Android/Desktop Ktor transport with provider-neutral access-token abstraction;
- durable SQLDelight hosted outbox;
- atomic local-first campaign creation + durable hosted mutation delivery;
- idempotent retry using stable mutation identity;
- authenticated hosted account/campaign bootstrap;
- explicit campaign membership lifecycle reconciliation (`ACTIVE`, `KICKED`, `BANNED`) and campaign soft-deletion metadata;
- hosted PC current-state snapshot read/write;
- reuse of the existing versioned app-owned `CharacterBackupDocument` as the PC JSONB current-state payload rather than a second server character model;
- server-side PC authorization based on active membership plus DM authority or Player owner/controller authority;
- DM authority kept distinct from PC ownership: DM-created PCs remain unassigned until a later explicit PC-management action; Player first upload may bind that Player as owner/controller;
- PC optimistic revisions, mutation-id idempotency, stale conflict handling and hosted tombstone behavior;
- durable PC snapshot outbox delivery and authoritative revision acknowledgement;
- same-identity hosted reconciliation distinct from user-facing backup restore-as-copy;
- protection against equal-revision overwrite, local-ahead overwrite and tombstone resurrection;
- non-destructive handling of hosted PC deletion so local recovery data is retained.

PR #25 merged to `main` as:

`8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`

The exact post-merge `main` run `34985799585` completed SUCCESS for backend, PostgreSQL contracts, shared/Kotlin tests, Android build, Desktop build, APK upload and every preserved Player guard.

## 2. Current code boundary

The shared/backend/database layers are ready for a real hosted environment, but the owner-facing Android composition remains intentionally local-only.

Do not mistake that for missing provider-neutral foundation. The remaining Player↔Server step needs real identity/session and a real deployed hosted development environment so that the application can perform an authenticated end-to-end round trip.

The existing backend already supports a provider-neutral/static local verifier seam for tests/local development and a Descope/JWKS production-mode verifier path. The shared client already isolates access-token acquisition behind `HostedAccessTokenProvider`.

Do not add another parallel networking/auth abstraction merely to postpone provider activation.

## 3. Exact next dependency

The next normal package is **Player↔Server real authenticated development integration**.

That package should begin only after the owner activates development resources for:

1. **Cloudflare** — development Worker/API runtime;
2. **Neon** — development PostgreSQL database;
3. **Descope** — development authentication/identity project.

Then continue in dependency order with:

1. configure the development Worker against Neon and Descope without committing secrets;
2. apply/verify hosted SQL migrations in the development database;
3. wire Android remembered authentication/session to the existing shared access-token boundary;
4. wire owner-facing campaign bootstrap/create/select to the hosted campaign sync path while preserving local-first behavior;
5. wire PC current-state push/pull to the existing hosted PC snapshot sync path;
6. prove the first real Player↔Server round trip;
7. prove second-device observation, offline edit/reconnect/convergence, Player access, DM access and membership-revoke enforcement before moving deeper into DM surfaces.

## 4. External-provider activation is an owner gate

Do not autonomously create provider accounts, projects, billing commitments, production resources, secrets or deployments.

At activation time, first verify current provider plans/options and present the owner with any material choices involving:

- cost/plan;
- region/data location;
- account ownership/recovery;
- security/privacy;
- irreversible or meaningful vendor lock-in.

Use development/test resources first. Do not create production resources unless separately justified.

Secrets, bearer tokens and database credentials must never enter Git. Store runtime secrets in provider secret stores or ignored local configuration. Public/non-secret project identifiers may be documented where useful.

**R2 is not part of this activation gate.** Activate R2 only when Media/Handouts/assets actually reach object-storage integration.

## 5. Existing runtime configuration contract

The current Worker composition expects the established bindings/configuration family, including:

- `APP_ENV`;
- `DATABASE_URL`;
- `AUTH_MODE`;
- `DESCOPE_PROJECT_ID`;
- `DESCOPE_JWKS_URL`;
- local-development static-auth values where explicitly permitted by the Worker code.

`AUTH_MODE=static` is a local-only development/testing seam. Do not deploy static bearer authentication as a production authentication mode.

The Android application currently has no provider-specific auth SDK/session composition wired into `MainActivity`; that is intentional at this checkpoint.

## 6. Security note: repository visibility

At checkpoint capture, the GitHub API reports repository metadata as `private: false` even though the project has previously been described conversationally as private.

Treat this as an owner-level security/privacy discrepancy. Do not change repository visibility autonomously. Before any secret-bearing or provider-integration work, the owner should verify that the repository visibility matches their intent.

Regardless of visibility, never commit provider credentials or secrets.

## 7. Resume rule

A new implementation chat/agent must:

1. read `AGENTS.md`;
2. read `README.md` and `MANIFEST.md`;
3. read `docs/checkpoints/LATEST.md` and this checkpoint;
4. read `docs/PROJECT_STATE.md` and `docs/BRANCH_STATUS.md`;
5. verify current remote `main` and its latest CI before writing;
6. inspect whether later merged work supersedes this checkpoint;
7. continue from current `main`, not from historical Player/convergence branches;
8. preserve Player behavior and permanent Player CI guards;
9. return to the owner only for material product/scope/security/privacy/cost/lock-in/destructive behavior, external-provider activation, or manual/physical QA gates.

If no later implementation supersedes this checkpoint, the next dependency is the owner-controlled Cloudflare + Neon + Descope development activation described above.

## 8. Historical Player acceptance remains separate

Current integrated CI success does not retroactively convert the historical physical Player QA candidate into a PASS. The historical frozen Player evidence remains bounded and should not be rewritten as current physical acceptance.

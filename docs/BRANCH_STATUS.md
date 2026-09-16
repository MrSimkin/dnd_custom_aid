# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified current main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**Lifecycle state:** repository implementation complete / DEV Worker deployment verified / owner Windows Desktop live QA pending

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal integrated-MVP trunk. Current verified integrated head:

`f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`

That commit merged PR #43. Post-merge Scaffold `35142092743` completed **SUCCESS**.

## 2. Completed Wave 5 predecessors

### `wave5/desktop-workbench-shell` / PR #42

Completed and merged. Owner Windows QA passed persistent Desktop local campaign state/context, Spanish product UI, Application Settings and bounded QA diagnostics.

### `wave5/campaign-membership-administration-core` / PR #43

Completed and merged as `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`.

Integrated scope includes active-DM-only member roster, Player `KICK` / `BAN` / `LIFT_BAN`, idempotent/no-op moderation, campaign-revision discipline, provider-neutral Shared Campaign Administration client and hosted database contract coverage.

Do not resume or reopen either completed branch for current implementation.

## 3. Current focused branch / PR

Branch:

`wave5/desktop-hosted-campaign-administration`

PR:

`#44 — feat: add Desktop hosted campaign administration` — **draft**

Created from:

`f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`

Package objective:

**Desktop hosted authentication/session acquisition + real Campaign Administration consumption**

The package also owns the approved Desktop font catalogue + font/theme preview settings follow-up because this is the next genuine Desktop feature build.

## 4. Repository implementation / CI

Repository implementation is complete and CI-verified for the bounded package.

The deployment-prep head:

`a6c0532878e8ef49ddfb894fa71076c1af73587a`

passed Scaffold:

`35163179550` — **SUCCESS**.

Implemented scope includes:

- Desktop-only Descope email OTP/session adapter with provider-neutral Shared token seam;
- memory-only session/refresh JWT handling and refresh-before-use behavior;
- hosted auth wired into the Desktop workbench;
- canonical hosted campaign bootstrap/convergence through the existing Shared service;
- real hosted member roster consumption;
- server-authoritative Player moderation with confirmation and authoritative refresh after success;
- no moderation affordances for DM rows;
- non-secret hosted diagnostics;
- preview-oriented Desktop font/theme settings;
- bundled Geist and Mona Sans Condensed Desktop resources with license/provenance records;
- focused auth/moderation/font-preference tests;
- preservation of local-only campaign operation without hosted auth.

## 5. DEV deployment — verified

The Campaign Administration Worker code was explicitly deployed to the existing DEV Worker:

`dnd-custom-aid-api`

from repository head:

`a6c0532878e8ef49ddfb894fa71076c1af73587a`

Cloudflare deployment Version ID:

`130d35e7-7903-47b2-8203-d74f9ec3db55`

Secret-free post-deployment checks:

- `/health` -> HTTP 200;
- unauthenticated `/v1/campaigns/<zero-uuid>/members` -> HTTP 401 / `UNAUTHENTICATED`.

The new roster route is therefore live through route recognition and authentication enforcement. Do not claim authenticated Desktop/bootstrap/roster/moderation acceptance yet.

## 6. Current package gate

Current progression:

1. **COMPLETE:** repository implementation and CI verification;
2. **COMPLETE:** explicit DEV Worker deployment / route-presence verification;
3. **NEXT:** owner Windows Desktop live-QA preflight using `docs/technical/DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_QA_HANDOFF.md`;
4. review real roster evidence;
5. if a suitable real Player membership exists, run a bounded moderation mutation test;
6. complete settings/sign-out/relaunch/local-data-preservation QA;
7. fix any defects in narrowly scoped commits with CI verification;
8. mark PR ready and merge only after required owner QA passes, with expected-head safety and post-merge `main` verification.

Do not perform Kick/Ban/Lift-Ban merely to satisfy a checklist before confirming the intended Player membership is safe to mutate.

## 7. Authentication boundary

Desktop platform code implements provider-specific authentication outside Shared; Shared receives only the provider-neutral `HostedAccessTokenProvider`.

Session and refresh JWTs are secrets and remain memory-only. They must not be committed, logged, exposed through diagnostics or stored in ordinary Desktop preferences.

Sign-out must not delete local campaign/character state.

## 8. Protected invariants

Preserve local-first persistence, stable identity, membership/role/ownership/current-control distinctions, DM authority distinct from ownership, stale-revision/idempotency/tombstone/no-silent-overwrite semantics, the hard USD $0 policy and public-repository secret hygiene.

Do not delete/reset the existing local Desktop QA campaign as part of hosted testing.

## 9. Explicit non-goals

This branch does not own invitation creation/revoke/regenerate, kicked-member rejoin, co-DM moderation, role editing, PC Manager/Audit, full PC hosted synchronization, Live Combat, generalized RBAC/ACL, Media/Handouts/object storage, System Administration, backup/export/PDF hardening or unrelated infrastructure.

## 10. Non-authoritative stray ref

`__should_not_create__` remains non-authoritative, contains no unique current work and is not a continuation point. Opportunistic safe deletion is optional and not a blocker.

## 11. Exact resume rule

Resume only on PR #44 / `wave5/desktop-hosted-campaign-administration` from `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md` or a newer current-package checkpoint. Do not redeploy Cloudflare unless later Worker code actually changes. The current owner action is the bounded Windows Desktop live-QA preflight.
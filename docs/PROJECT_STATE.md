# Project State — global repository navigation

**Last verified:** 2026-09-16 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Verified current main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Verified post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Current package checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md`  
**Deployed repository head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**

## 1. Current authority/topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches and reintegrates only after proportionate verification.

`docs/checkpoints/LATEST.md` controls the practical resume point. `docs/BRANCH_STATUS.md` controls branch lifecycle. Historical checkpoints remain evidence for the state that existed when they were written.

PR #43 is complete and merged. Draft PR #44 contains the bounded Desktop hosted Campaign Administration package. Repository implementation and DEV Worker deployment are now verified; real Windows Desktop integration/owner QA remains pending.

## 2. Integrated foundation

The integrated foundation includes:

- account/global identity;
- Campaign;
- Membership + campaign role;
- PC owner vs current controller;
- stable object identities;
- monotonic revisions and stale-write semantics;
- tombstones/non-resurrection semantics;
- durable local hosted outbox;
- authenticated hosted account/campaign bootstrap;
- explicit membership lifecycle (`ACTIVE`, `KICKED`, `BANNED`);
- hosted PC snapshot read/write;
- active-membership plus DM-or-owner/controller server-side PC authorization;
- mutation idempotency and optimistic revisions;
- local-first PC delivery/reconciliation;
- explicit reviewed conflict resolution;
- permanent hosted-sync QA diagnostics;
- persistent Desktop local SQLite campaign context/workbench shell;
- active-DM-only hosted Campaign Administration roster and moderation API/client contract.

## 3. Hosted DEV architecture

Verified architecture remains:

```text
Android / Desktop clients
        |
        v
Cloudflare Worker/API <---- Descope identity proof
        |
        v
Neon PostgreSQL
```

Native clients do not hold database credentials. The hard external-service operating budget remains USD $0 unless the owner explicitly changes it.

The current Cloudflare Worker is `dnd-custom-aid-api` at `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`.

The Wave 5 Campaign Administration code was explicitly redeployed from repository head `a6c0532878e8ef49ddfb894fa71076c1af73587a`. Cloudflare reported Version ID `130d35e7-7903-47b2-8203-d74f9ec3db55`.

Post-deployment secret-free evidence:

- `/health` -> HTTP 200;
- unauthenticated `/v1/campaigns/<zero-uuid>/members` -> HTTP 401 / `UNAUTHENTICATED`.

Therefore the new roster route is live through route recognition/auth enforcement. This is not yet evidence that authenticated Desktop bootstrap/roster/moderation works end-to-end.

## 4. Completed predecessor packages

### Wave 4 Player <-> Server — complete

Integrated for remembered authentication, campaign bootstrap/delivery, PC push/pull, multi-client convergence, explicit conflict resolution and membership revoke/reinstate authorization behavior.

### Wave 5 package A — Desktop shell/local campaign — complete

PR #42 merged as `fb113909cb53b2463bd643cae7d2f54f0673fec4`; post-merge Scaffold `35138029472` passed.

Owner Windows Desktop QA passed workbench launch, local campaign persistence/context, Spanish product language, persistent Application Settings and QA/diagnostic copy surface.

### Wave 5 package B — hosted Campaign membership administration core — complete

PR #43 merged as `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`; post-merge Scaffold `35142092743` passed.

Integrated behavior includes:

- ACTIVE-DM-only hosted member roster;
- member account display identity, role and lifecycle status;
- Player `KICK`, `BAN`, `LIFT_BAN` actions;
- `LIFT_BAN = BANNED -> KICKED`;
- idempotent/no-op moderation;
- campaign revision changes only for real lifecycle changes;
- no membership-row/PC/owner-controller destruction;
- provider-neutral Shared Campaign Administration client;
- focused backend/Shared/PostgreSQL contract verification.

## 5. Wave 5 package C — current PR #44

Current package:

**Desktop hosted authentication/session acquisition + real Campaign Administration consumption**

Implemented and CI-verified:

- Desktop-specific Descope email-OTP/session adapter through `HostedAccessTokenProvider`;
- memory-only session/refresh JWT handling with refresh-before-use and fail-closed clearing;
- hosted auth/session UX without disabling local-only work;
- hosted account/campaign discovery and canonical bootstrap;
- real hosted member-roster UI;
- Player-only Kick/Ban/Lift-Ban UI with confirmation, server authority and authoritative refresh;
- no DM-row moderation affordances;
- non-secret diagnostics;
- font/theme preview settings follow-up;
- bundled Geist and Mona Sans Condensed with provenance/licensing;
- focused auth/moderation/preference/fallback tests.

DEV Worker deployment is now verified. Remaining acceptance is real Desktop behavior.

## 6. Current owner QA gate

Use:

`docs/technical/DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_QA_HANDOFF.md`

The immediate owner preflight must verify:

1. Desktop launches using the already-proven Windows QA toolchain;
2. existing local campaign data remains present;
3. real DEV email-OTP login succeeds without exposing OTP/JWT values;
4. hosted bootstrap/canonical campaign context succeeds;
5. real hosted roster loads;
6. DM rows have no Player moderation controls;
7. whether a suitable real Player row exists and which moderation actions are visible;
8. font/theme preview surfaces are present.

Do not perform moderation mutations until the roster evidence is reviewed. If no suitable Player exists, stop instead of inventing data, editing Neon ad hoc or expanding this package into invitation/rejoin functionality.

## 7. Authentication/session rules

Desktop keeps provider-specific Descope code outside Shared; Shared remains provider-neutral.

Session/refresh JWTs remain memory-only and must not be logged, exposed through diagnostics, committed or persisted in ordinary Desktop preferences. Sign-out clears hosted session state without deleting local campaign/character state.

## 8. Campaign Administration rules

For a real hosted active-DM campaign, the Desktop surface must:

- display the authoritative roster;
- use human display names where available;
- expose moderation only for Player rows;
- preserve `LIFT_BAN = BANNED -> KICKED`;
- wait for server confirmation;
- refresh authoritative roster state after success;
- surface errors without silently changing local truth.

## 9. Explicit current-package non-goals

Do not expand PR #44 into invitations/rejoin, co-DM administration, PC Manager/Audit, ownership/control shortcuts, full new PC sync architecture, Managers, Media/Handouts/object storage, Live Combat, System Administration, backup/export/PDF hardening, broad Player redesign or generalized RBAC/ACL.

## 10. Security / operating residuals

D-0075 remains controlling: repository intentionally public, hard USD $0 external-service budget, never commit secrets, and paid/overage commitments require explicit owner approval.

The known owner-local backend install continues to report **3 high severity npm vulnerabilities**. Do not run `npm audit fix --force` blindly; inspect exact packages/reachability/fixed versions in a later explicit security-hardening pass.

Preserve fail-closed authorization, least privilege, local-first safety, stable identities, stale-revision protection, idempotency, tombstones/non-resurrection and no-silent-overwrite guarantees.

Do not delete/reset local data or hosted data merely to make QA pass.

## 11. Resume rule

Read, in order:

1. `docs/checkpoints/LATEST.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md`;
3. `docs/technical/DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_QA_HANDOFF.md`;
4. `docs/BRANCH_STATUS.md`;
5. this file;
6. the implementation checkpoint and D-0072/D-0073/D-0075 as needed.

Continue on `wave5/desktop-hosted-campaign-administration`. Do not redeploy Cloudflare again unless later code changes require it. The current substantive gate is owner Windows Desktop live-QA preflight.
# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_IDENTITY_MAPPING_RESOLVED.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Desktop live-QA preflight:** **OTP AUTH PASS / LOCAL DATA PASS / ZERO HOSTED CAMPAIGNS UNDER OUTLOOK DEV IDENTITY**  
**Identity mapping:** **RESOLVED — TWO DISTINCT DEV LOGIN IDENTITIES, NO DUPLICATE-IDENTITY DEFECT SHOWN**  
**Current gate:** Desktop re-authentication with historical Gmail DM identity, hosted bootstrap + roster read only; no moderation mutation yet  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_IDENTITY_MAPPING_RESOLVED.md`;
3. this file;
4. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_QA_PREFLIGHT_ZERO_CAMPAIGNS.md`;
5. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md`;
6. `docs/PROJECT_STATE.md`;
7. `docs/BRANCH_STATUS.md`;
8. relevant implementation/decision checkpoints as needed.

Newer current-package records control over older operational prose. Historical checkpoints remain evidence for the state they recorded.

## Current sequence

```text
Wave 4 Player <-> Server                              COMPLETE / INTEGRATED
Wave 5 Desktop shell + local campaign                 COMPLETE / OWNER-QA PASS / MERGED (#42)
hosted membership administration core                COMPLETE / MERGED (#43) / POST-MERGE CI PASS
        |
        v
Desktop hosted auth + real Campaign Administration   REPO IMPLEMENTED / CI VERIFIED / PR #44 DRAFT
        |
        v
DEV Worker deployment                                VERIFIED
        |
        v
Windows Desktop live-QA preflight                    PARTIAL PASS
  launch/local data -> PASS
  real email OTP login -> PASS
  Outlook DEV identity -> 0 hosted campaigns
        |
        v
Neon + Descope identity discovery                    COMPLETE
  Outlook app_user -> no memberships
  historical Gmail app_user -> ACTIVE DM on Hosted Batch Test
  two distinct Descope users/login IDs -> confirmed
        |
        v
Desktop sign-out + Gmail DM login                    NEXT
        |
        v
Hosted bootstrap + roster retrieval                  READ-ONLY QA
        |
        +--> if only DM exists, stop and create bounded reversible Player fixture separately
        |
        v
Bounded moderation QA                                PENDING
        |
        v
Final settings/sign-out/relaunch QA                  REQUIRED BEFORE MERGE
        |
        v
PR readiness / merge / post-merge verification
```

## Verified deployment state

The owner deployed exact repository head `a6c0532878e8ef49ddfb894fa71076c1af73587a` to the existing Cloudflare Worker `dnd-custom-aid-api` using repository-pinned Wrangler `4.127.1` and the expected existing authenticated Cloudflare account.

Cloudflare deployment completed successfully and reported Worker Version ID `130d35e7-7903-47b2-8203-d74f9ec3db55`.

Secret-free post-deployment probes passed:

- `GET /health` -> `200`;
- unauthenticated Campaign Administration roster route -> `401 UNAUTHENTICATED`.

## Desktop/identity QA result so far

Windows Desktop launched normally, preserved the existing local campaign and successfully authenticated through the real Descope email-OTP flow.

Bootstrap returned zero hosted campaigns under the Outlook DEV login because the corresponding application user has no membership rows. This is valid behavior.

Read-only Neon + Descope discovery established that the historical Wave 4 hosted DM state belongs to the separate Gmail DEV login. That application user still has ACTIVE DM membership in hosted campaign `31762fa9-b01a-4f3d-80e5-877a07c63e62` (`Hosted Batch Test`), which remains undeleted at revision 0.

The provider shows two enabled users with distinct login IDs and distinct immutable user IDs. The apparent cross-platform identity split is therefore resolved as test-account separation, not evidence that Desktop created a duplicate identity.

Do not patch authentication code, merge provider users or migrate memberships based on the earlier same-email recollection.

## Current owner/manual gate

No external database/provider mutation is needed.

In the Desktop app:

1. sign out of the current Outlook-backed hosted session;
2. sign in with the historical Gmail DEV identity through normal email OTP;
3. let bootstrap complete;
4. confirm `Hosted Batch Test` appears as hosted context;
5. retrieve the hosted member roster;
6. report roles/statuses/buttons;
7. do not click Kick/Ban/Lift Ban yet.

If only the DM row is present, stop. The next step will be a separately reviewed, reversible QA fixture using an already-existing DEV application user as PLAYER rather than inventing another account.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget remains USD $0;
- never commit, paste, log or expose secrets/tokens/credentials/OTP codes;
- do not reset/delete databases, local campaigns, PCs, outboxes or application state to make QA pass;
- preserve membership/role/ownership/current-control distinctions;
- preserve stable identity, stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees;
- DM authority is not PC ownership;
- Live Combat belongs to a later wave;
- avoid generalized RBAC/ACL or speculative infrastructure;
- do not run `npm audit fix --force` blindly; the known 3 high-severity dependency findings remain a later explicit hardening item.

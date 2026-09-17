# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_IDENTITY_CONTINUITY_INVESTIGATION.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Desktop live-QA preflight:** **OTP AUTH PASS / LOCAL DATA PASS / ZERO HOSTED CAMPAIGNS**  
**Neon discovery:** **CURRENT DESKTOP USER HAS NO MEMBERSHIP / HISTORICAL DM MEMBERSHIP STILL ACTIVE**  
**Current gate:** read-only Descope identity-continuity audit; no identity, membership or moderation mutation yet  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_IDENTITY_CONTINUITY_INVESTIGATION.md`;
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
  hosted bootstrap -> 0 hosted campaigns
        |
        v
Read-only Neon identity/membership discovery         COMPLETE
  Desktop app_user -> no membership
  historical app_user -> ACTIVE DM on Hosted Batch Test
        |
        v
Read-only Descope identity continuity audit          NEXT
        |
        +--> determine why same email produced different Descope subjects over time
        |
        v
Identity repair/test-data decision                    BLOCKED UNTIL DESCOPE EVIDENCE
        |
        v
Real roster + bounded moderation QA                  PENDING
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

## Desktop/Neon QA result so far

Windows Desktop launched normally, preserved the existing local campaign and successfully authenticated through the real Descope email-OTP flow.

Bootstrap returned zero hosted campaigns because the current Desktop-authenticated application user has no membership rows.

Read-only Neon discovery established:

- current Desktop application user `f34bc5f0-4d35-4d09-b771-505b3851440c` has no campaign membership;
- historical application user `4ba0f476-2eba-4eff-b4e0-78bb9372a8b4` still has ACTIVE DM membership in hosted campaign `31762fa9-b01a-4f3d-80e5-877a07c63e62` (`Hosted Batch Test`);
- the hosted campaign remains undeleted at revision 0.

The owner confirmed the same email address was used for the earlier Android hosted/Descope QA and the current Desktop OTP login.

Android and Desktop use the same Descope project ID and Worker. Backend identity resolution maps JWT `sub` to UNIQUE `app_user.descope_subject`, so the two application users imply different Descope subjects were observed over time.

The current Desktop REST OTP endpoints match Descope's documented Sign-Up-or-In/Verify flow. Do not patch auth code or insert membership rows merely to make QA proceed.

## Current owner/provider gate

Use the existing Descope DEV project's **Users** page read-only.

Search for the exact email used in both Android and Desktop QA and report:

1. number of matching current users;
2. whether the exact email is present as a Login ID for each match;
3. current status;
4. creation date/time if visible;
5. any visible login-ID spelling/case/alias difference.

Do not edit, merge, disable, delete, invite or recreate users. Do not create a Management Key for this investigation.

A plausible current hypothesis is historical Descope user deletion/recreation: the same email could then create a new immutable Descope user ID while old Neon application rows remain. Treat this as a hypothesis until the Descope user table confirms or contradicts it.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget remains USD $0;
- never commit, paste, log or expose secrets/tokens/credentials/OTP codes;
- do not reset/delete databases, local campaigns, PCs, outboxes or application state to make QA pass;
- do not mutate Descope identity or Neon membership while identity continuity is unresolved;
- preserve membership/role/ownership/current-control distinctions;
- preserve stable identity, stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees;
- DM authority is not PC ownership;
- Live Combat belongs to a later wave;
- avoid generalized RBAC/ACL or speculative infrastructure;
- do not run `npm audit fix --force` blindly; the known 3 high-severity dependency findings remain a later explicit hardening item.

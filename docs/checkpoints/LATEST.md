# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Current gate:** owner Windows Desktop live-QA preflight, then bounded real moderation QA before merge  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md`;
3. this file;
4. `docs/PROJECT_STATE.md`;
5. `docs/BRANCH_STATUS.md`;
6. `docs/technical/DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_QA_HANDOFF.md` for the current owner action;
7. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_IMPLEMENTED.md` for implementation evidence;
8. relevant decisions/checkpoints as needed.

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
  /health -> 200
  unauth roster route -> 401 UNAUTHENTICATED
        |
        v
Owner Windows Desktop live-QA preflight              NEXT
  launch + preserve local data
  real email OTP login
  hosted bootstrap/campaign context
  real roster retrieval
        |
        v
Bounded moderation QA if suitable Player exists      PENDING ROSTER EVIDENCE
        |
        v
Final settings/sign-out/relaunch QA                  REQUIRED BEFORE MERGE
        |
        v
PR readiness / merge / post-merge verification
```

## Verified deployment state

The owner deployed exact repository head `a6c0532878e8ef49ddfb894fa71076c1af73587a` to the existing Cloudflare Worker `dnd-custom-aid-api` using the repository-pinned Wrangler `4.127.1` and the expected existing authenticated Cloudflare account.

Cloudflare deployment completed successfully and reported Worker Version ID `130d35e7-7903-47b2-8203-d74f9ec3db55`.

Secret-free post-deployment probes passed:

- `GET /health` -> `200` with normal service health JSON;
- unauthenticated `GET /v1/campaigns/<zero-uuid>/members` -> `401 UNAUTHENTICATED`.

The `401` proves the new Campaign Administration member-roster route is live through route recognition and authentication enforcement. Do not describe authenticated Desktop behavior or moderation as verified yet.

## Current owner/manual gate

Use:

`docs/technical/DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_QA_HANDOFF.md`

The next bounded owner action is Windows Desktop live-QA preflight only:

1. launch with the already-recorded portable JDK 17 + Gradle 9.5 toolchain;
2. confirm existing local data survives;
3. perform real email-OTP login locally;
4. verify hosted bootstrap/canonical campaign context;
5. retrieve the real roster;
6. report whether a suitable Player row exists and which moderation actions are visible;
7. confirm font/theme preview surfaces are present.

Do not perform Kick/Ban/Lift Ban until roster evidence is reviewed. If no suitable Player membership exists, stop rather than inventing data or editing Neon ad hoc.

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
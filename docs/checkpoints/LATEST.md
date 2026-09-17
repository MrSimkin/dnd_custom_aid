# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_ROSTER_DM_GUARD_VERIFIED.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Identity mapping:** **RESOLVED — TWO DISTINCT DEV LOGIN IDENTITIES**  
**Real Desktop Gmail DM QA:** **OTP PASS / BOOTSTRAP PASS / ROSTER PASS / DM GUARD PASS**  
**Current gate:** bounded reversible Neon PLAYER QA fixture using the existing Outlook DEV app_user; then real Kick/Ban/Lift-Ban UI QA  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_ROSTER_DM_GUARD_VERIFIED.md`;
3. this file;
4. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_IDENTITY_MAPPING_RESOLVED.md`;
5. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md`;
6. `docs/PROJECT_STATE.md`;
7. `docs/BRANCH_STATUS.md`;
8. relevant implementation/decision checkpoints as needed.

## Current sequence

```text
Wave 4 Player <-> Server                              COMPLETE / INTEGRATED
Wave 5 Desktop shell + local campaign                 COMPLETE / OWNER-QA PASS / MERGED (#42)
hosted membership administration core                COMPLETE / MERGED (#43)
        |
        v
Desktop hosted auth + Campaign Administration        REPO IMPLEMENTED / CI VERIFIED / PR #44 DRAFT
        |
        v
DEV Worker deployment                                VERIFIED
        |
        v
Desktop Outlook login / zero-membership behavior     PASS / EXPECTED
        |
        v
Identity mapping                                     RESOLVED
  Outlook DEV identity -> app_user with no memberships
  Gmail DEV identity   -> historical ACTIVE DM on Hosted Batch Test
        |
        v
Desktop Gmail DM login + bootstrap                   PASS
  hosted campaigns = 1
  applied = 1
  conflicts = 0
        |
        v
Real hosted roster retrieval                         PASS
  roster revision = 0
  exactly one DM row
  no moderation controls on DM row
        |
        v
Create reversible PLAYER QA fixture                  NEXT OWNER/NEON ACTION
        |
        v
Real Kick/Ban/Lift-Ban UI QA                         PENDING
        |
        v
Final settings/sign-out/relaunch QA                  REQUIRED BEFORE MERGE
        |
        v
PR readiness / merge / post-merge verification
```

## Verified real Desktop hosted behavior

The owner authenticated Desktop using the historical Gmail DEV identity. Bootstrap returned:

`Campañas alojadas: 1 · aplicadas: 1 · conflictos: 0`

`Hosted Batch Test` appeared in the Desktop campaign surface. The hosted membership resolved as DM / ACTIVE.

Campaign Administration fetched the real hosted roster successfully. The UI showed roster revision `0` and exactly one hosted member row: the existing DM membership. The DM row exposed no Player moderation actions and explicitly stated that Player moderation actions do not apply to DM members.

This verifies real authenticated Desktop -> Worker -> Neon Campaign Administration roster behavior and the DM moderation guard.

## Current owner/provider gate

The hosted roster has no Player membership, so moderation cannot yet be exercised.

Use the already-existing Outlook DEV application user as a reversible QA PLAYER fixture in `Hosted Batch Test`.

The fixture must:

- add only one `campaign_membership` row;
- use the existing Outlook `app_user`;
- role = `PLAYER`;
- initial status = `ACTIVE`;
- preserve the existing Gmail DM row unchanged;
- create no new Descope user, campaign, PC or provider resource;
- be verified immediately after insertion;
- remain removable after QA.

Do not alter the DM membership and do not perform unrelated database cleanup.

After the Player row is visible in Desktop, perform moderation through the Desktop UI only, with authoritative roster refresh after each step.

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

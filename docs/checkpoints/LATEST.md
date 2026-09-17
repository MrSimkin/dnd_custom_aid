# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_OWNER_QA_COMPLETE.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Owner Windows QA:** **PASS**  
**Canonical DEV identity:** **OUTLOOK = OWNER/DM DEFAULT**  
**Current hosted baseline:** Outlook `DM / ACTIVE`; Gmail no campaign membership; campaign revision `4`; Desktop bootstrap `1 / 1 / 0`  
**Current gate:** final exact-head CI -> mark PR ready -> merge with expected-head safety -> post-merge verification  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_OWNER_QA_COMPLETE.md`;
3. this file;
4. `docs/PROJECT_STATE.md`;
5. `docs/BRANCH_STATUS.md`;
6. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_MODERATION_SEQUENCE_VERIFIED.md`;
7. `docs/checkpoints/2026-09-16_DEV_CANONICAL_OUTLOOK_MIGRATION_COMPLETE.md`;
8. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md`.

## Current sequence

```text
Wave 4 Player <-> Server                              COMPLETE / INTEGRATED
Wave 5 Desktop shell + local campaign                 COMPLETE / OWNER-QA PASS / MERGED (#42)
hosted membership administration core                COMPLETE / MERGED (#43)
        |
        v
Desktop hosted auth + Campaign Administration        REPO IMPLEMENTED / CI VERIFIED / PR #44
        |
        v
DEV Worker deployment                                PASS
        |
        v
Real Desktop auth/bootstrap/roster                   PASS
        |
        v
Real moderation sequence                             PASS
  ACTIVE -> KICKED -> BANNED -> KICKED
  campaign revision 0 -> 1 -> 2 -> 3
        |
        v
Canonical Outlook DEV owner/DM migration             PASS
  Outlook -> DM / ACTIVE
  Gmail -> no current campaign membership
  campaign revision 3 -> 4
        |
        v
Desktop Outlook canonical-DM verification            PASS
  bootstrap 1 / 1 / 0
  roster revision 4
        |
        v
Settings persistence + session/relaunch QA           PASS
        |
        v
Final exact-head CI                                  NEXT
        |
        v
PR ready / merge / post-merge verification
```

## Owner QA result

The complete owner-facing acceptance gate passed on Windows:

- existing local campaigns preserved;
- real email-OTP authentication passed;
- hosted campaign bootstrap/convergence passed;
- authoritative roster retrieval passed;
- DM moderation guard passed;
- Player moderation passed `ACTIVE -> KICKED -> BANNED -> KICKED`;
- authoritative revision progression passed;
- Outlook canonical DM migration and real Desktop verification passed at revision `4`;
- font/theme preview cards are present;
- settings persist across relaunch;
- hosted session does not persist across application shutdown;
- explicit sign-out clears hosted session while preserving local campaign data and settings.

## Canonical DEV identity

Normal hosted DEV testing uses the Outlook-backed application identity as owner/DM.

Gmail is historical/inactive by default and may be used only when a test deliberately requires a secondary identity. Historical Gmail mutation receipts remain untouched.

## Merge-readiness gate

The immediately preceding head `281275638855a1200cecee9f1beef806638d11e6` passed Scaffold `35168371109`.

During readiness review:

- `main` remained exactly at package base `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`;
- PR #44 had no unresolved review threads;
- PR #44 had no conversation comments;
- no new application code has been added since the deployed/tested implementation head.

Wait for Scaffold on the final readiness-documentation head. If it passes and `main` remains unchanged, mark PR #44 ready and merge with expected-head protection, then verify post-merge `main` and CI.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget remains USD $0;
- never commit, paste, log or expose secrets/tokens/credentials/OTP codes;
- preserve historical identity/audit evidence;
- preserve membership/role/ownership/current-control distinctions;
- preserve stable identity, stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees;
- DM authority is not PC ownership;
- do not redeploy Cloudflare unless Worker code actually changes;
- do not run `npm audit fix --force` blindly; the known 3 high-severity dependency findings remain a later explicit hardening item.

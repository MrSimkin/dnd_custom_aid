# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DEV_CANONICAL_OUTLOOK_MIGRATION_COMPLETE.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Identity mapping:** **RESOLVED — TWO DISTINCT DEV LOGIN IDENTITIES**  
**Real Desktop moderation QA:** **ACTIVE -> KICKED -> BANNED -> KICKED PASS / REVISION 0 -> 1 -> 2 -> 3**  
**Canonical DEV identity:** **OUTLOOK = OWNER/DM DEFAULT; MIGRATION COMPLETE**  
**Current DB state:** Outlook `DM / ACTIVE`; Gmail no campaign membership; campaign revision `4`  
**Current gate:** Desktop Outlook bootstrap/DM roster verification, then final settings persistence + sign-out/relaunch QA  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DEV_CANONICAL_OUTLOOK_MIGRATION_COMPLETE.md`;
3. this file;
4. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_MODERATION_SEQUENCE_VERIFIED.md`;
5. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_IDENTITY_MAPPING_RESOLVED.md`;
6. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md`;
7. `docs/PROJECT_STATE.md`;
8. `docs/BRANCH_STATUS.md`;
9. relevant implementation/decision checkpoints as needed.

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
Real Desktop hosted auth/bootstrap/roster            PASS
        |
        v
Real moderation sequence                             PASS
  ACTIVE -> KICKED        revision 0 -> 1
  KICKED -> BANNED        revision 1 -> 2
  BANNED -> KICKED        revision 2 -> 3
        |
        v
Canonical DEV identity decision                      OUTLOOK OWNER/DM DEFAULT
        |
        v
Controlled Gmail -> Outlook membership migration    PASS
  Outlook -> DM / ACTIVE
  Gmail -> no campaign membership
  campaign revision 3 -> 4
  historical receipts preserved
        |
        v
Desktop Outlook bootstrap + DM roster verification  NEXT
        |
        v
Final settings persistence + sign-out/relaunch QA   REQUIRED BEFORE MERGE
        |
        v
PR readiness / merge / post-merge verification
```

## Verified real Desktop hosted behavior

Real DEV Campaign Administration has already been physically verified through Desktop -> Worker -> Neon:

- real Descope email OTP authentication;
- hosted campaign bootstrap;
- real roster retrieval;
- DM moderation guard;
- `ACTIVE -> KICKED`;
- `KICKED -> BANNED`;
- `BANNED -> KICKED` via Lift Ban;
- authoritative refresh after each transition;
- campaign revision advanced exactly once per real lifecycle change: `0 -> 1 -> 2 -> 3`.

## Canonical DEV identity

Future normal hosted DEV testing uses the Outlook-backed application identity as the canonical owner/DM account.

The controlled Neon migration is complete. Post-migration verification returned exactly one campaign membership for `Hosted Batch Test`:

- Outlook app_user `f34bc5f0-4d35-4d09-b771-505b3851440c`;
- `DM / ACTIVE`;
- campaign revision `4`.

Gmail is no longer a member of the hosted campaign. Gmail remains historical/inactive by default and may later be used deliberately as a secondary identity when a two-user test requires one.

Do not rewrite historical Gmail mutation receipts; they truthfully record earlier activity.

## Current owner/manual gate

In Desktop:

1. sign out of any Gmail hosted session;
2. authenticate with Outlook through normal email OTP;
3. let hosted bootstrap complete;
4. verify `Hosted Batch Test` appears;
5. open/select it as needed;
6. open Campaign Administration and refresh the hosted roster;
7. verify Outlook is `DM / ACTIVE` at revision `4`;
8. verify Gmail does not appear as a campaign member.

Then complete final settings persistence and sign-out/relaunch QA before PR #44 readiness review.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget remains USD $0;
- never commit, paste, log or expose secrets/tokens/credentials/OTP codes;
- do not reset/delete databases, local campaigns, PCs, outboxes or application state to make QA pass;
- preserve historical mutation receipts/audit evidence;
- preserve membership/role/ownership/current-control distinctions;
- preserve stable identity, stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees;
- DM authority is not PC ownership;
- Live Combat belongs to a later wave;
- avoid generalized RBAC/ACL or speculative infrastructure;
- do not run `npm audit fix --force` blindly; the known 3 high-severity dependency findings remain a later explicit hardening item.

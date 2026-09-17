# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DEV_CANONICAL_OUTLOOK_MIGRATION_PREFLIGHT.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Identity mapping:** **RESOLVED — TWO DISTINCT DEV LOGIN IDENTITIES**  
**Real Desktop moderation QA:** **ACTIVE -> KICKED -> BANNED -> KICKED PASS / REVISION 0 -> 1 -> 2 -> 3**  
**Canonical DEV identity decision:** **OUTLOOK = OWNER/DM DEFAULT; GMAIL = HISTORICAL/SECONDARY BY EXPLICIT TEST ONLY**  
**Current DB state:** Gmail `DM / ACTIVE`; Outlook has no membership; campaign revision `3`; no Gmail/Outlook PC references  
**Current gate:** controlled Gmail->Outlook active membership migration, then final settings/sign-out/relaunch QA  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DEV_CANONICAL_OUTLOOK_MIGRATION_PREFLIGHT.md`;
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
Read-only post-rollback DB diagnostics               COMPLETE
  campaign revision = 3
  Gmail = DM / ACTIVE
  Outlook = no membership
  no Gmail/Outlook PC references
  historical Gmail mutation receipts preserved
        |
        v
Controlled Gmail -> Outlook active membership move  NEXT OWNER/NEON ACTION
        |
        v
Desktop Outlook bootstrap + DM roster verification  PENDING
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

## Canonical DEV identity decision

The owner has standardized future normal hosted DEV testing on the Outlook-backed application identity.

Outlook becomes the canonical owner/DM account. Gmail remains historical/inactive by default and may later be used deliberately as a secondary Player identity when a two-user scenario is needed.

Do not rewrite historical identity/audit evidence merely to make old operations appear to belong to Outlook. Existing Gmail `app_user` and historical `mutation_receipt` rows remain valid history.

## Current verified database state

Read-only diagnostics after the failed migration/rollback established:

- `Hosted Batch Test` exists, undeleted, revision `3`;
- exactly one ACTIVE DM exists;
- Gmail app_user `4ba0f476-2eba-4eff-b4e0-78bb9372a8b4` = `DM / ACTIVE`;
- Outlook app_user `f34bc5f0-4d35-4d09-b771-505b3851440c` has no campaign membership;
- no PC in this campaign references Gmail or Outlook as owner/controller;
- no PC in another campaign references Gmail as owner/controller;
- Gmail has historical mutation receipts; those must remain unchanged.

The prior migration assumption that Outlook was still `PLAYER / KICKED` was stale, so that transaction correctly did not complete.

## Current owner/provider gate

Perform one controlled administrative transaction based on the actual current state:

1. assert campaign revision `3` and undeleted state;
2. assert Gmail is the sole `DM / ACTIVE`;
3. assert Outlook has no membership in the campaign;
4. insert Outlook as `DM / ACTIVE`;
5. remove Gmail's DM membership;
6. advance campaign revision exactly once `3 -> 4`;
7. preserve both app_user rows and all historical mutation receipts;
8. verify exactly one remaining membership: Outlook `DM / ACTIVE`.

After success, sign out of Gmail in Desktop and authenticate with Outlook. Confirm `Hosted Batch Test` bootstraps and Outlook resolves as DM / ACTIVE at revision `4`.

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

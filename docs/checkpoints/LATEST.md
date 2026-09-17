# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_OUTLOOK_CANONICAL_DM_VERIFIED.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Identity mapping:** **RESOLVED — TWO DISTINCT DEV LOGIN IDENTITIES**  
**Real Desktop moderation QA:** **ACTIVE -> KICKED -> BANNED -> KICKED PASS / REVISION 0 -> 1 -> 2 -> 3**  
**Canonical DEV identity:** **OUTLOOK = OWNER/DM DEFAULT; MIGRATION + DESKTOP VERIFICATION COMPLETE**  
**Current DB/Desktop state:** Outlook `DM / ACTIVE`; Gmail no campaign membership; campaign revision `4`; Desktop bootstrap `1 / 1 / 0`  
**Current gate:** final settings persistence + session sign-out/relaunch QA, then PR readiness review  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_OUTLOOK_CANONICAL_DM_VERIFIED.md`;
3. this file;
4. `docs/checkpoints/2026-09-16_DEV_CANONICAL_OUTLOOK_MIGRATION_COMPLETE.md`;
5. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_MODERATION_SEQUENCE_VERIFIED.md`;
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
Desktop Outlook bootstrap + DM roster verification  PASS
  bootstrap 1 / 1 / 0
  Outlook -> DM / ACTIVE
  roster revision 4
  Gmail absent
        |
        v
Final settings persistence + sign-out/relaunch QA   NEXT / REQUIRED BEFORE MERGE
        |
        v
PR readiness / merge / post-merge verification
```

## Verified real Desktop hosted behavior

Real DEV Campaign Administration has been physically verified through Desktop -> Worker -> Neon for:

- real Descope email OTP authentication;
- hosted campaign bootstrap;
- real roster retrieval;
- DM moderation guard;
- `ACTIVE -> KICKED`;
- `KICKED -> BANNED`;
- `BANNED -> KICKED` via Lift Ban;
- authoritative refresh after each transition;
- campaign revision advanced exactly once per real lifecycle change: `0 -> 1 -> 2 -> 3`;
- controlled migration to canonical Outlook owner/DM identity at revision `4`;
- real Desktop Outlook bootstrap `1 / 1 / 0`;
- real Outlook `DM / ACTIVE` roster at revision `4` with Gmail absent.

## Canonical DEV identity

Future normal hosted DEV testing uses the Outlook-backed application identity as the canonical owner/DM account.

Gmail is historical/inactive by default and may later be used deliberately as a secondary identity when a multi-user test requires one. Do not rewrite historical Gmail mutation receipts; they truthfully record earlier activity.

## Current owner/manual gate

Complete the final Desktop persistence/session QA:

1. in `Configuración`, verify font and theme choices are shown as preview cards rather than plain selectors;
2. change a deliberately visible set of device-local preferences (theme/font plus at least one scale/density setting);
3. note the chosen values;
4. close the Desktop application **without signing out first**;
5. relaunch the application;
6. verify local campaign data and selected preferences persisted;
7. verify hosted session did **not** persist and Desktop begins signed out;
8. authenticate again with Outlook and verify `Hosted Batch Test` resolves as `DM / ACTIVE`, revision `4`;
9. explicitly sign out;
10. verify local campaign data/settings remain intact while hosted session becomes signed out.

After this passes, perform PR #44 readiness review and final exact-head CI before merge.

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

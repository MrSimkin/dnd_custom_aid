# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_MODERATION_SEQUENCE_VERIFIED.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Identity mapping:** **RESOLVED — TWO DISTINCT DEV LOGIN IDENTITIES**  
**Real Desktop Gmail DM QA:** **OTP PASS / BOOTSTRAP PASS / ROSTER PASS / DM GUARD PASS**  
**Real moderation:** **ACTIVE -> KICKED -> BANNED -> KICKED PASS / REVISION 0 -> 1 -> 2 -> 3**  
**Current gate:** remove bounded Outlook PLAYER QA fixture safely, then final settings/sign-out/relaunch QA  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_MODERATION_SEQUENCE_VERIFIED.md`;
3. this file;
4. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_ROSTER_DM_GUARD_VERIFIED.md`;
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
Desktop Outlook zero-membership behavior             PASS / EXPECTED
        |
        v
Identity mapping                                     RESOLVED
        |
        v
Desktop Gmail DM login + bootstrap                   PASS
        |
        v
Real hosted roster + DM guard                        PASS
        |
        v
Bounded Outlook PLAYER fixture                       PASS / QA ONLY
        |
        v
Real moderation sequence                             PASS
  ACTIVE -> KICKED        revision 0 -> 1
  KICKED -> BANNED        revision 1 -> 2
  BANNED -> KICKED        revision 2 -> 3
        |
        v
Remove temporary PLAYER fixture                      NEXT OWNER/NEON ACTION
        |
        v
Final settings persistence + sign-out/relaunch QA    REQUIRED BEFORE MERGE
        |
        v
PR readiness / merge / post-merge verification
```

## Verified real Desktop hosted behavior

The owner authenticated Desktop using the historical Gmail DEV identity. Bootstrap returned:

`Campañas alojadas: 1 · aplicadas: 1 · conflictos: 0`

`Hosted Batch Test` appeared and resolved as `DM / ACTIVE`. Campaign Administration fetched the real roster and exposed no Player moderation actions on the DM row.

A bounded QA fixture added the already-existing Outlook DEV application user as `PLAYER / ACTIVE`. The owner then exercised all approved moderation transitions through Desktop with authoritative refresh after each action:

1. `ACTIVE -> KICKED` (`Activo -> Expulsado`), revision `0 -> 1`;
2. `KICKED -> BANNED` (`Expulsado -> Bloqueado`), revision `1 -> 2`;
3. `BANNED -> KICKED` (`Bloqueado -> Expulsado`), revision `2 -> 3`.

The DM membership remained `ACTIVE` and unchanged throughout. The Player row remained visible throughout. UI actions changed correctly for each lifecycle state. Lift Ban correctly resulted in `KICKED`, not `ACTIVE`.

## Current owner/provider gate

Remove only the temporary Outlook QA membership from `Hosted Batch Test`.

Cleanup must verify first that:

- the campaign still exists and is undeleted;
- the Gmail membership is still `DM / ACTIVE`;
- the Outlook fixture is exactly `PLAYER / KICKED`;
- the Outlook fixture user does not own or control any PC in this campaign.

Then delete exactly that one membership row and verify only the DM membership remains. Do not delete either `app_user`, the campaign, PCs, mutation receipts or Descope users. Do not reset campaign revision `3`.

After cleanup, refresh Desktop roster. Then perform final settings persistence and sign-out/relaunch QA before PR readiness review.

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

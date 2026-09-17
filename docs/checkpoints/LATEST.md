# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_PLAYER_FIXTURE_VISIBLE.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Identity mapping:** **RESOLVED — TWO DISTINCT DEV LOGIN IDENTITIES**  
**Real Desktop Gmail DM QA:** **OTP PASS / BOOTSTRAP PASS / ROSTER PASS / DM GUARD PASS**  
**PLAYER fixture:** **VISIBLE / ACTIVE / BOTH MODERATION ACTIONS PRESENT**  
**Current gate:** real Desktop moderation sequence `ACTIVE -> KICKED -> BANNED -> KICKED`, one transition at a time  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_PLAYER_FIXTURE_VISIBLE.md`;
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
Desktop Outlook login / zero-membership behavior     PASS / EXPECTED
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
Reversible ACTIVE PLAYER fixture visible             PASS
  DM -> ACTIVE / no controls
  PLAYER -> ACTIVE / Expulsar + Bloquear
  roster revision = 0
        |
        v
Real moderation: ACTIVE -> KICKED                    NEXT
        |
        v
Real moderation: KICKED -> BANNED                    PENDING
        |
        v
Real moderation: BANNED -> KICKED (Lift Ban)         PENDING
        |
        v
Fixture cleanup + final settings/sign-out/relaunch   REQUIRED BEFORE MERGE
        |
        v
PR readiness / merge / post-merge verification
```

## Verified real Desktop hosted behavior

The owner authenticated Desktop using the historical Gmail DEV identity. Bootstrap returned:

`Campañas alojadas: 1 · aplicadas: 1 · conflictos: 0`

`Hosted Batch Test` appeared in Desktop and resolved as DM / ACTIVE.

Campaign Administration fetched the real hosted roster successfully. The DM row exposed no Player moderation actions.

A bounded QA fixture then added the already-existing Outlook DEV application user as `PLAYER / ACTIVE` in the same hosted campaign. After roster refresh, Desktop showed two hosted members at roster revision `0`:

- Gmail DEV identity: `DM / ACTIVE`, no moderation controls;
- Outlook DEV identity: `PLAYER / ACTIVE`, with both `Expulsar` and `Bloquear` controls.

This establishes the correct real precondition for moderation QA.

## Current owner/manual gate

Perform moderation through Desktop only, one transition at a time.

First action only:

1. click `Expulsar` on the ACTIVE Player row;
2. accept the confirmation dialog if the target and action are correct;
3. wait for server confirmation/authoritative roster refresh;
4. verify Player status changes to KICKED/Expulsado;
5. verify the displayed roster/campaign revision advances exactly once from `0` to `1`;
6. report the new Player row and available buttons before doing anything else.

Do not click `Bloquear` until the KICK result has been reviewed.

Do not mutate the Gmail DM row and do not manually edit Neon during the moderation sequence unless a defect requires diagnosis.

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

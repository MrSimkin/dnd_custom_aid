# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_MODERATION_BAN_VERIFIED.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Worker Version ID:** `130d35e7-7903-47b2-8203-d74f9ec3db55`  
**Identity mapping:** **RESOLVED — TWO DISTINCT DEV LOGIN IDENTITIES**  
**Real Desktop Gmail DM QA:** **OTP PASS / BOOTSTRAP PASS / ROSTER PASS / DM GUARD PASS**  
**PLAYER fixture:** **VISIBLE / ACTIVE / MODERATION UI VERIFIED**  
**Real moderation:** **ACTIVE -> KICKED PASS / KICKED -> BANNED PASS / REVISION 0 -> 1 -> 2**  
**Current gate:** final real Desktop moderation `BANNED -> KICKED` via Lift Ban, then fixture cleanup + settings/sign-out/relaunch QA  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_MODERATION_BAN_VERIFIED.md`;
3. this file;
4. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_MODERATION_KICK_VERIFIED.md`;
5. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_PLAYER_FIXTURE_VISIBLE.md`;
6. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_ROSTER_DM_GUARD_VERIFIED.md`;
7. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_IDENTITY_MAPPING_RESOLVED.md`;
8. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_DEV_DEPLOYMENT_VERIFIED.md`;
9. `docs/PROJECT_STATE.md`;
10. `docs/BRANCH_STATUS.md`;
11. relevant implementation/decision checkpoints as needed.

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
Reversible ACTIVE PLAYER fixture visible             PASS
        |
        v
Real moderation: ACTIVE -> KICKED                    PASS
  revision 0 -> 1
        |
        v
Real moderation: KICKED -> BANNED                    PASS
  revision 1 -> 2
        |
        v
Real moderation: BANNED -> KICKED (Lift Ban)         NEXT
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

`Hosted Batch Test` appeared in Desktop and resolved as DM / ACTIVE. Campaign Administration fetched the real hosted roster successfully and correctly exposed no Player moderation actions on the DM row.

A bounded QA fixture added the already-existing Outlook DEV application user as `PLAYER / ACTIVE` in the same hosted campaign. Desktop displayed both rows correctly at roster revision `0`.

Real moderation then passed two sequential transitions through Desktop -> Worker -> Neon with authoritative refresh after each action:

1. `ACTIVE -> KICKED` (`Activo -> Expulsado`)
   - revision `0 -> 1`;
   - DM unchanged;
   - Player remained present;
   - only `Bloquear` remained.

2. `KICKED -> BANNED` (`Expulsado -> Bloqueado`)
   - revision `1 -> 2`;
   - DM unchanged;
   - Player remained present;
   - only `Levantar bloqueo` remained.

## Current owner/manual gate

Perform exactly one final Desktop moderation action:

1. click `Levantar bloqueo` on the currently `Bloqueado` Player row;
2. confirm only if the dialog clearly targets that Player and the lift-ban action;
3. wait for server confirmation and authoritative roster refresh;
4. verify Player status becomes `KICKED` / `Expulsado`;
5. verify roster revision advances exactly once from `2` to `3`;
6. verify DM remains `ACTIVE` and unchanged;
7. verify only `Bloquear` remains on the Player row;
8. stop for review before fixture cleanup or further actions.

Do not manually edit Neon during this moderation sequence unless a defect requires diagnosis.

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

# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main before current package:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`  
**Current focused branch:** `wave4/membership-revoke-authorization`  
**Current PR:** #41  
**Automated behavior/test head:** `a188417f8173573271346246e5cc129dabdf45cc`  
**Automated Scaffold:** `35118236579` — **SUCCESS**  
**Physical gate:** **OWNER-PHYSICAL PASS**  
**Current package:** membership revoke + Player/DM authorization  
**Current action:** documentation-head CI, merge PR #41, verify post-merge `main`

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. `docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_PHYSICAL_QA_COMPLETE.md` — current completion evidence;
3. this file — practical resume point;
4. `docs/BRANCH_STATUS.md` — branch lifecycle;
5. `docs/PROJECT_STATE.md` — global implementation state;
6. `docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md` — preceding convergence evidence;
7. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0`, public-repository and secret-hygiene policy.

If older operational prose conflicts with this file or the current completion checkpoint, the newer completion checkpoint controls.

## Current Wave 4 state

```text
remembered Android Descope session/token             COMPLETE / OWNER-PHYSICAL PASS
owner-facing hosted account/campaign bootstrap       COMPLETE / OWNER-PHYSICAL PASS
campaign create + durable hosted delivery            COMPLETE / OWNER-PHYSICAL PASS
PC snapshot push/pull + blocked-row recovery         COMPLETE / OWNER-PHYSICAL PASS
unchanged-sync/no-op confirmation                    COMPLETE / OWNER-PHYSICAL PASS
multi-client PC convergence safety                    COMPLETE / OWNER-PHYSICAL PASS
PR #40 merge + post-merge main                       COMPLETE / VERIFIED
membership revoke + Player/DM authorization          AUTOMATED VERIFIED / OWNER-PHYSICAL PASS
        |
        v
PR #41 DOCUMENTATION-HEAD CI + MERGE                 CURRENT CLOSURE ACTION
```

## Membership revoke / authorization result

Production code already enforces ACTIVE campaign membership plus DM-or-owner/controller authority for hosted PC access. The current package added dynamic-transition contracts rather than replacing the authorization architecture.

Automated coverage proves Player owner/controller revoke, BANNED inactivity, independent DM authority, DM revoke, hosted-PC preservation and stable `403 FORBIDDEN` behavior.

The bounded real-DEV physical gate exercised the actual DM membership for `Hosted Batch Test` through `ACTIVE -> KICKED -> ACTIVE` on Android build `0.4.0-preqa.15 / 41500`.

While KICKED:

- active hosted campaign projection became `NO`;
- eligible campaigns became `0`;
- hosted PC sync stopped;
- no conflicts or pending/blocked outbox work appeared;
- the local campaign and PC remained present and viewable.

After restoration to ACTIVE:

- active hosted campaign projection returned to `YES`;
- eligibility returned to `1`;
- the hosted PC returned as unchanged;
- no conflicts or outbox work appeared.

The membership ended in its original ACTIVE state. Physical testing specifically covered DM revoke/reinstate; Player revoke remains automated evidence and must not be mislabeled as physical.

## Current closure rule

PR #41 is now eligible for normal closure. Verify the documentation-only head in Scaffold, merge only if green and unchanged, then verify post-merge `main`.

Do not add final Kick/Ban UI, invitation/rejoin UX, Campaign Manager administration, generalized RBAC/ACL or unrelated Wave 5 work to PR #41.

## Permanent safety rules

- Repository is intentionally public under D-0075.
- External-service operating budget remains **USD $0** unless explicitly changed.
- Never expose database credentials, provider tokens, private keys, authorization headers or session/refresh tokens.
- Do not reset local databases/app data, clear outboxes, delete PCs/campaigns, or reinstall merely to make QA pass.
- Membership, campaign role, PC ownership and current control remain distinct.
- DM authority does not imply PC ownership.
- Preserve stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees.

## Next product boundary

Final moderation/Campaign Manager UI is **not** automatically opened by this validation package. Stop after PR #41 integration unless a separate package is explicitly scoped/authorized.

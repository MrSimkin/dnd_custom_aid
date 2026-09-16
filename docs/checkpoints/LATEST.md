# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated behavior main:** `6f7165e6e5ae56a4b1985f037a656bf527b94d01`  
**Post-merge Scaffold:** `35123027446` — **SUCCESS**  
**PR #41:** **MERGED**  
**Membership revoke + Player/DM authorization:** **COMPLETE / INTEGRATED / AUTOMATED VERIFIED / OWNER-PHYSICAL PASS**  
**Current focused implementation package:** none opened by this closure

## Read first

1. `AGENTS.md` — mandatory project operating rules;
2. `docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_PHYSICAL_QA_COMPLETE.md` — latest completed Wave 4 authorization evidence;
3. this file — practical resume point;
4. `docs/BRANCH_STATUS.md` — branch lifecycle;
5. `docs/PROJECT_STATE.md` — global implementation state;
6. `docs/checkpoints/2026-09-16_MULTI_CLIENT_PC_CONVERGENCE_PHYSICAL_QA_COMPLETE.md` — preceding convergence evidence;
7. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md` — controlling `$0`, public-repository and secret-hygiene policy.

If older operational prose conflicts with this file or the latest completion checkpoint, the newer completion record controls.

## Current Wave 4 state

```text
remembered Android Descope session/token             COMPLETE / OWNER-PHYSICAL PASS
owner-facing hosted account/campaign bootstrap       COMPLETE / OWNER-PHYSICAL PASS
campaign create + durable hosted delivery            COMPLETE / OWNER-PHYSICAL PASS
PC snapshot push/pull + blocked-row recovery         COMPLETE / OWNER-PHYSICAL PASS
unchanged-sync/no-op confirmation                    COMPLETE / OWNER-PHYSICAL PASS
multi-client PC convergence safety                    COMPLETE / OWNER-PHYSICAL PASS
membership revoke + Player/DM authorization          COMPLETE / OWNER-PHYSICAL PASS
PR #41 merge + post-merge main                       COMPLETE / VERIFIED
```

## Membership revoke / authorization completion

Automated coverage proves dynamic Player owner/controller revoke, BANNED inactivity, independent DM authority, DM revoke, hosted-PC preservation and stable `403 FORBIDDEN` behavior.

The bounded real-DEV physical gate specifically exercised the actual DM membership for `Hosted Batch Test` through `ACTIVE -> KICKED -> ACTIVE` on Android `0.4.0-preqa.15 / 41500`.

While KICKED, hosted eligibility stopped, no PC sync occurred, no conflicts/outbox work appeared, and the local campaign/PC remained present and viewable. After restoration to ACTIVE, eligibility and unchanged PC synchronization resumed cleanly.

Physical testing covered DM revoke/reinstate. Player revoke remains automated evidence and must not be mislabeled as physical.

Closure documentation head `8335112cc9721a32b66e294e00c72ccdd7f75b7d` passed Scaffold `35122878536`. PR #41 merged as `6f7165e6e5ae56a4b1985f037a656bf527b94d01`; post-merge Scaffold `35123027446` passed.

## Permanent safety rules

- Repository is intentionally public under D-0075.
- External-service operating budget remains **USD $0** unless explicitly changed.
- Never expose database credentials, provider tokens, private keys, authorization headers or session/refresh tokens.
- Do not reset local databases/app data, clear outboxes, delete PCs/campaigns, or reinstall merely to make QA pass.
- Membership, campaign role, PC ownership and current control remain distinct.
- DM authority does not imply PC ownership.
- Preserve stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees.

## Next product boundary

No new implementation branch/package is opened by this closure.

Final DM Kick/Ban UI, invitation/rejoin UX, Campaign Manager administration and broader moderation workflows are separate product work. Scope/authorize the next package explicitly before implementation rather than treating it as a continuation of PR #41.

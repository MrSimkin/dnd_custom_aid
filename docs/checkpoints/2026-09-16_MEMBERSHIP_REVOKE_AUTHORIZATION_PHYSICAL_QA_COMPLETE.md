# Membership revoke + Player/DM authorization — physical QA complete

**Date:** 2026-09-16 (Chile local time)  
**Branch:** `wave4/membership-revoke-authorization`  
**PR:** #41  
**Integrated base main:** `587a000dae7ff9b9f997dd138b0ebbaeca201256`  
**Automated behavior/test head:** `a188417f8173573271346246e5cc129dabdf45cc`  
**Automated Scaffold:** `35118236579` — **SUCCESS**  
**Pre-physical documentation head:** `f93c766a95947bbfbc876c662281855f77612abf`  
**Exact-head Scaffold:** `35118646816` — **SUCCESS**

## Result

The membership-revoke and hosted PC authorization package is **AUTOMATED VERIFIED / OWNER-PHYSICAL PASS**.

The production implementation already contained the required active-membership and object-authority enforcement. This package added missing transition-focused automated evidence and completed a bounded real-DEV physical revoke/reinstate test without destructive cleanup or credential exposure.

No new generic RBAC/ACL framework or final moderation UI was introduced.

## 1. Automated evidence

`database/tests/0005_membership_revoke_authorization_contract.sql` proves:

- an ACTIVE Player owner/controller can initially read/write its hosted PC;
- `ACTIVE -> KICKED` removes Player hosted PC read/write authority;
- revoke does not delete the PC, rewrite owner/controller identity, or advance revision as a side effect;
- an independently ACTIVE DM retains authority after the Player revoke;
- `BANNED` remains inactive;
- revoking the DM removes DM hosted PC read/write authority;
- the hosted PC remains intact after revoke transitions.

`backend/test/membership-revoke-authorization.test.mjs` proves authorization failure is surfaced as stable `HTTP 403 / FORBIDDEN` without hosted-state leakage.

Existing Shared client regression proves explicit `KICKED` disables hosted campaign use while preserving the local campaign.

Scaffold `35118236579` passed the new database contract, earlier hosted contracts, backend Node/type checks, Shared/Kotlin, Android, Desktop and permanent Player guards.

## 2. Physical preflight

Device/build:

- Android phone previously used for clean convergence;
- `0.4.0-preqa.15` / `41500`;
- DEBUG QA.

Preflight QA log at epoch `1789574372` showed:

- hosted campaigns: `1`;
- eligible campaigns: `1`;
- applied/reconciled campaigns: `1`;
- hosted PCs: `1`;
- unchanged PCs: `1`;
- no campaign or PC conflicts;
- no queued, retryable or blocked mutations;
- local hosted outbox empty;
- `Hosted Batch Test` returned as active hosted campaign = `YES`;
- membership bootstrap applied = `YES`;
- eligible for PC sync = `YES`.

The exact DEV membership row was then identified read-only as:

- campaign `31762fa9-b01a-4f3d-80e5-877a07c63e62`;
- user `4ba0f476-2eba-4eff-b4e0-78bb9372a8b4`;
- role `DM`;
- status `ACTIVE`.

## 3. Physical revoke

The exact identified DM membership was changed reversibly from `ACTIVE` to `KICKED`. The update returned exactly that row with status `KICKED`.

Android QA log at epoch `1789574898` then showed:

- hosted membership lifecycle rows: `1`;
- eligible campaigns: `0`;
- applied/reconciled campaigns: `1`;
- hosted PCs: `0`;
- no campaign or PC conflicts;
- no queued, acknowledged, retryable or blocked mutations;
- local hosted outbox empty;
- `Hosted Batch Test` returned as active hosted campaign = `NO`;
- membership bootstrap applied = `YES`;
- eligible for PC sync = `NO`;
- hosted revision = `n/a`.

This demonstrates that explicit inactive membership state was received and applied while hosted PC synchronization was disabled.

The owner then checked the normal/local app UI while the membership remained KICKED and confirmed:

- the campaign remained present locally;
- the existing PC remained present locally;
- the PC could still be opened/viewed;
- no local edit/save was performed during the revoke interval.

Therefore membership revoke stopped hosted eligibility without silently wiping cached local campaign/PC data.

## 4. Physical reinstate

The same exact DM membership row was restored from `KICKED` to `ACTIVE`; the update returned exactly one row with status `ACTIVE`.

Android QA log at epoch `1789576310` then showed:

- hosted campaigns: `1`;
- eligible campaigns: `1`;
- applied/reconciled campaigns: `1`;
- hosted PCs: `1`;
- unchanged PCs: `1`;
- no campaign or PC conflicts;
- no queued, acknowledged, retryable or blocked mutations;
- local hosted outbox empty;
- `Hosted Batch Test` returned as active hosted campaign = `YES`;
- membership bootstrap applied = `YES`;
- eligible for PC sync = `YES`.

Hosted eligibility and normal no-op PC synchronization therefore resumed cleanly after reinstatement.

## 5. Coverage statement

The physical gate specifically exercised the real DEV **DM membership** lifecycle `ACTIVE -> KICKED -> ACTIVE`.

Player owner/controller revoke behavior is proven by the automated PostgreSQL authorization contract, not by this physical phone exercise. Do not describe this checkpoint as a physical Player revoke test.

Taken together, automated and physical evidence now cover the approved membership revoke + Player/DM authorization boundary proportionately.

## 6. Safety properties preserved

The test did not:

- delete membership, campaign or PC rows;
- modify owner/controller identity;
- clear app data or reinstall;
- clear the hosted outbox;
- introduce unrelated PC edits;
- expose database credentials, provider tokens or session material;
- use production resources.

The real DEV membership ended in its original `ACTIVE` state.

## 7. Package closure

PR #41 may now be closed through normal documentation-head CI and merge verification.

Do not extend this PR into final DM Kick/Ban UI, invitation/rejoin UX, Campaign Manager administration, generalized RBAC/ACL, or unrelated Wave 5 work. Those are separate product packages and may require their own scope/owner decisions.

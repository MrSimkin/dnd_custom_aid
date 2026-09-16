# Membership revoke + Player/DM authorization — physical QA complete

**Date:** 2026-09-16 (Chile local time)  
**PR:** #41 — **MERGED**  
**Integrated main:** `6f7165e6e5ae56a4b1985f037a656bf527b94d01`  
**Post-merge Scaffold:** `35123027446` — **SUCCESS**  
**Automated behavior/test head:** `a188417f8173573271346246e5cc129dabdf45cc`  
**Automated Scaffold:** `35118236579` — **SUCCESS**  
**Physical gate:** **OWNER-PHYSICAL PASS**

## Result

The membership-revoke and hosted PC authorization package is **COMPLETE / INTEGRATED / AUTOMATED VERIFIED / OWNER-PHYSICAL PASS**.

Production code already contained the required active-membership and object-authority enforcement. The package added missing transition-focused automated evidence and completed a bounded real-DEV physical revoke/reinstate test without destructive cleanup or credential exposure.

No generic RBAC/ACL framework or final moderation UI was introduced.

## 1. Automated evidence

`database/tests/0005_membership_revoke_authorization_contract.sql` proves:

- an ACTIVE Player owner/controller can initially read/write its hosted PC;
- `ACTIVE -> KICKED` removes Player hosted PC read/write authority;
- revoke does not delete the PC, rewrite owner/controller identity, or advance revision as a side effect;
- an independently ACTIVE DM retains authority after Player revoke;
- `BANNED` remains inactive;
- revoking the DM removes DM hosted PC read/write authority;
- the hosted PC remains intact after revoke transitions.

`backend/test/membership-revoke-authorization.test.mjs` proves authorization failure is surfaced as stable `HTTP 403 / FORBIDDEN` without hosted-state leakage.

Existing Shared client regression proves explicit `KICKED` disables hosted campaign use while preserving the local campaign.

Scaffold `35118236579` passed the new database contract, earlier hosted contracts, backend Node/type checks, Shared/Kotlin, Android, Desktop and permanent Player guards.

## 2. Physical preflight

Android `0.4.0-preqa.15 / 41500` preflight at epoch `1789574372` showed one active/eligible hosted campaign, one unchanged hosted PC, no conflicts and an empty outbox.

The exact DEV membership row was identified read-only as:

- campaign `31762fa9-b01a-4f3d-80e5-877a07c63e62`;
- user `4ba0f476-2eba-4eff-b4e0-78bb9372a8b4`;
- role `DM`;
- status `ACTIVE`.

## 3. Physical revoke

The exact DM membership was changed reversibly from `ACTIVE` to `KICKED`; the update returned exactly that row.

Android QA at epoch `1789574898` showed:

- explicit lifecycle membership still returned/applied;
- active hosted campaign projection = `NO`;
- eligible campaigns = `0`;
- hosted PCs = `0`;
- no campaign or PC conflicts;
- no queued, retryable or blocked mutations;
- empty local hosted outbox.

While the membership remained KICKED, the owner confirmed in the normal/local UI that the campaign remained present, the existing PC remained present, and the PC could still be opened/viewed. No edit/save was performed during the revoke interval.

Therefore revoke stopped hosted eligibility without silently wiping cached local campaign/PC data.

## 4. Physical reinstate

The same exact DM membership row was restored from `KICKED` to `ACTIVE`; exactly one row returned as ACTIVE.

Android QA at epoch `1789576310` showed:

- active hosted campaign projection = `YES`;
- eligible campaigns = `1`;
- hosted PCs = `1`;
- unchanged PCs = `1`;
- no campaign or PC conflicts;
- no queued, retryable or blocked mutations;
- empty local hosted outbox.

Hosted eligibility and normal no-op PC synchronization resumed cleanly. The real DEV membership ended in its original ACTIVE state.

## 5. Coverage statement

The physical gate specifically exercised the real DEV **DM membership** lifecycle `ACTIVE -> KICKED -> ACTIVE`.

Player owner/controller revoke behavior is proven by the automated PostgreSQL authorization contract, not by this physical phone exercise. Do not describe this checkpoint as a physical Player revoke test.

Taken together, automated and physical evidence cover the approved membership revoke + Player/DM authorization boundary proportionately.

## 6. Safety properties preserved

The test did not delete membership/campaign/PC rows, modify owner/controller identity, clear app data, reinstall, clear the hosted outbox, introduce unrelated PC edits, expose secrets, or use production resources.

## 7. Integration closure

Closure documentation head `8335112cc9721a32b66e294e00c72ccdd7f75b7d` passed Scaffold `35122878536`.

PR #41 then merged into `main` as:

`6f7165e6e5ae56a4b1985f037a656bf527b94d01`

Post-merge Scaffold `35123027446` completed **SUCCESS** across backend, hosted-database and Kotlin/Android/Desktop jobs.

The package is therefore integrated and closed. Do not reopen or rerun the physical scenario unless later behavior changes touch membership lifecycle or hosted authorization.

## 8. Separate next product boundary

Final DM Kick/Ban UI, invitation/rejoin UX, Campaign Manager administration and any broader moderation workflow are separate product packages. They are not implicitly authorized by this completed validation package and should be scoped separately before implementation.

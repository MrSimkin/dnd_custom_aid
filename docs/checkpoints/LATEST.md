# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified starting main for current package:** `40b29006052c9986e2d79227a6053f36241de1e6`  
**Current focused branch:** `wave5/desktop-workbench-shell`  
**Current PR:** #42  
**Verified behavior/test head:** `c5e23ebbec495e3aea2b36a4cbe695c9cc586bd4`  
**Scaffold:** `35125170321` — **SUCCESS**  
**Current package:** Wave 5 — Desktop workbench shell + local campaign context  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_READY_FOR_MANUAL_QA.md`  
**Current gate:** **OWNER WINDOWS DESKTOP MANUAL QA**  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_READY_FOR_MANUAL_QA.md` — exact current gate;
3. this file;
4. `docs/BRANCH_STATUS.md`;
5. `docs/PROJECT_STATE.md`;
6. `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`;
7. `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`;
8. `docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_PHYSICAL_QA_COMPLETE.md` for latest completed Wave 4 evidence;
9. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md`.

Newer current-package records control over older operational prose.

## Current sequence

```text
Wave 4 Player <-> Server                           COMPLETE / INTEGRATED
membership revoke + Player/DM authorization       COMPLETE / OWNER-PHYSICAL PASS
        |
        v
Wave 5 Desktop shell + Campaign Administration    ACTIVE
        |
        v
desktop workbench + local campaign context        AUTOMATED VERIFIED
        |
        v
OWNER WINDOWS DESKTOP AUDITION                     CURRENT GATE
```

## Automated result

The placeholder Desktop window has been replaced with a real persistent workbench using the existing Shared `CampaignRepository` and SQLDelight schema.

Implemented in this package:

- Desktop SQLite/JDBC local persistence at owner-profile scope;
- explicit database-driver lifetime;
- Dashboard;
- Campaigns list/create/active selection;
- Campaign Administration bound to the same active campaign context;
- approved workbench chrome and stable navigation destinations;
- persistence-across-reopen regression test.

Desktop and Android still have separate local database files. Hosted Desktop authentication/sync is **not** activated by this package.

Exact head `c5e23ebbec495e3aea2b36a4cbe695c9cc586bd4` passed Scaffold `35125170321`, including Shared Desktop tests, Desktop build, Android build/guards, backend checks and hosted database contracts.

## Exact next owner action

Perform the guided Windows Desktop manual QA described in the current checkpoint. Instructions must be delivered one exact step at a time.

Do not merge PR #42 before the user-visible workbench has passed that manual gate.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget remains USD $0;
- never commit or paste secrets/tokens/credentials;
- do not reset/delete databases, clear outboxes, delete campaign/PC data or reinstall merely to make QA pass;
- preserve membership/role/ownership/current-control distinctions;
- preserve stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees;
- Live Combat belongs to Wave 6;
- invitation/Kick/Ban/Unban, hosted Desktop sync, Managers, Media/Handouts, System Administration and backup/export remain later bounded packages.

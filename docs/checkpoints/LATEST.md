# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified starting main for current package:** `fb113909cb53b2463bd643cae7d2f54f0673fec4`  
**Current focused branch:** `wave5/campaign-membership-administration-core`  
**Current PR:** not opened yet  
**Current package:** Wave 5 — hosted Campaign membership administration core  
**Current checkpoint:** `docs/checkpoints/2026-09-16_CAMPAIGN_MEMBERSHIP_ADMINISTRATION_CORE_OPEN.md`  
**Current gate:** implementation + automated verification  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_CAMPAIGN_MEMBERSHIP_ADMINISTRATION_CORE_OPEN.md`;
3. this file;
4. `docs/BRANCH_STATUS.md`;
5. `docs/PROJECT_STATE.md`;
6. `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`;
7. `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`;
8. `docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_READY_FOR_MANUAL_QA.md` for the completed predecessor package;
9. `docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_PHYSICAL_QA_COMPLETE.md` for latest completed Wave 4 membership evidence;
10. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md`.

Newer current-package records control over older operational prose.

## Current sequence

```text
Wave 4 Player <-> Server                              COMPLETE / INTEGRATED
membership revoke + Player/DM authorization          COMPLETE / OWNER-PHYSICAL PASS
        |
        v
Wave 5 Desktop shell + Campaign Administration       ACTIVE
        |
        +-- Desktop workbench + local campaign       COMPLETE / OWNER-QA PASS / MERGED
        |
        v
hosted Campaign membership administration core      CURRENT PACKAGE
```

## Completed predecessor

PR #42 (`wave5/desktop-workbench-shell`) merged to `main` as:

`fb113909cb53b2463bd643cae7d2f54f0673fec4`

Post-merge Scaffold:

`35138029472` — **SUCCESS**.

The owner manually accepted the Desktop shell, Spanish product-language repair, persistent Application Settings, QA/diagnostic copyability and persisted local campaign context. The manual QA campaign remained intact with UUID `30609c9d-89f7-42ef-85dd-a7a35df3c506`.

Desktop and Android continue to use separate local database files. Hosted Desktop authentication/synchronization has not yet been activated.

## Current package

`wave5/campaign-membership-administration-core` starts exactly from the verified PR #42 merge commit.

Its bounded goal is to provide the hosted/shared Campaign Administration contracts needed before Desktop can safely expose real member administration:

- active-DM-only member roster;
- explicit Player Kick / Ban / Lift Ban lifecycle actions;
- preserve membership rows and all PC/local data;
- `LIFT_BAN` returns `BANNED -> KICKED`, while invitation/rejoin later owns `KICKED -> ACTIVE`;
- Shared provider-neutral client contract;
- backend/database/Shared automated evidence.

Invitation/rejoin, Desktop auth/session acquisition, final Desktop member UI, PC assignment shortcuts and co-DM workflows remain separate later packages.

## Owner-approved future Desktop settings requirement

At the next genuine Desktop feature build:

- expand the Desktop font catalogue to Android-equivalent choices where supported;
- replace Font and Theme selectors with Android-like preview cards/forms so the owner can preview the result before selecting it.

This requirement is recorded in the current package checkpoint and does not reorder the roadmap.

## Exact next action

Continue implementation on `wave5/campaign-membership-administration-core`, add focused automated contracts/tests, update operative memory, and run Scaffold on the exact package head.

No owner/manual action is currently required.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget remains USD $0;
- never commit or paste secrets/tokens/credentials;
- do not reset/delete databases, clear outboxes, delete campaign/PC data or reinstall merely to make QA pass;
- preserve membership/role/ownership/current-control distinctions;
- preserve stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees;
- Live Combat belongs to a later wave;
- avoid generalized RBAC/ACL or speculative infrastructure.

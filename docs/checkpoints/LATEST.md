# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified starting main for current package:** `fb113909cb53b2463bd643cae7d2f54f0673fec4`  
**Current focused branch:** `wave5/campaign-membership-administration-core`  
**Current PR:** #43  
**Verified implementation head:** `fcaa533f79332f6a2f13fb06b7f1bb889dd1982c`  
**Verified implementation Scaffold:** `35140381721` — **SUCCESS**  
**Current package:** Wave 5 — hosted Campaign membership administration core  
**Current checkpoint:** `docs/checkpoints/2026-09-16_CAMPAIGN_MEMBERSHIP_ADMINISTRATION_CORE_AUTOMATED_VERIFIED.md`  
**Current gate:** final documentation-head Scaffold -> merge PR #43 -> post-merge `main` Scaffold  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_CAMPAIGN_MEMBERSHIP_ADMINISTRATION_CORE_AUTOMATED_VERIFIED.md`;
3. this file;
4. `docs/BRANCH_STATUS.md`;
5. `docs/PROJECT_STATE.md`;
6. `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`;
7. `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`;
8. `docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_READY_FOR_MANUAL_QA.md` for the completed first Wave 5 package;
9. `docs/checkpoints/2026-09-16_MEMBERSHIP_REVOKE_AUTHORIZATION_PHYSICAL_QA_COMPLETE.md` for completed Wave 4 membership evidence;
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
        +-- hosted membership administration core   AUTOMATED VERIFIED / PR #43
        |
        v
final doc-head CI -> merge -> post-merge CI          CURRENT GATE
```

## Current package result

The repository now contains the backend/database/Shared core for hosted Campaign member administration:

- active-DM-only member roster;
- Player `KICK`, `BAN`, `LIFT_BAN` lifecycle actions;
- idempotent/no-op moderation;
- campaign revision changes only for actual lifecycle changes;
- no membership/PC/owner-controller destruction as a moderation side effect;
- provider-neutral Shared hosted Campaign Administration client;
- focused backend and PostgreSQL contract coverage.

`LIFT_BAN` is intentionally `BANNED -> KICKED`. Invitation/rejoin later owns `KICKED -> ACTIVE`.

Exact implementation head `fcaa533f79332f6a2f13fb06b7f1bb889dd1982c` passed Scaffold `35140381721` across backend, hosted database, Shared/Kotlin, Android guards/build and Desktop build.

No owner/manual gate is required for this backend/shared-only package.

## Deployment boundary

The repository has no automatic Cloudflare Worker deployment workflow. The new routes are repository/API-contract verified but must **not** be described as already deployed to the real DEV Worker.

Real DEV deployment belongs with the first real Desktop-hosted consumer package that needs these routes.

## Owner-approved Desktop settings follow-up

At the next genuine Desktop feature build:

- expand the Desktop font catalogue toward the Android-equivalent choices where supported;
- replace plain Font and Theme selectors with Android-like preview cards/forms so the result can be previewed before selection.

The durable record is `docs/technical/DESKTOP_APPLICATION_SETTINGS_FOLLOWUPS.md`. This requirement does not reorder the roadmap.

## Exact next action

1. Let the final documentation-only PR #43 head pass Scaffold.
2. If the PR head remains unchanged and CI is green, merge PR #43 to `main`.
3. Verify post-merge `main` Scaffold.
4. Start the next bounded Wave 5 package from current `main`: Desktop hosted authentication/session acquisition + real Campaign Administration consumption; deploy/verify the DEV Worker explicitly when those new routes are first needed.

If PR #43 is already merged and post-merge CI is green when this file is read, skip steps 1–3 and continue with step 4.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget remains USD $0;
- never commit or paste secrets/tokens/credentials;
- do not reset/delete databases, clear outboxes, delete campaign/PC data or reinstall merely to make QA pass;
- preserve membership/role/ownership/current-control distinctions;
- preserve stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees;
- Live Combat belongs to a later wave;
- avoid generalized RBAC/ACL or speculative infrastructure.
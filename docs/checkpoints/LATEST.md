# Latest project checkpoint — global resume map

**Updated:** 2026-09-16 (Chile local time)  
**Normal implementation trunk:** `main`  
**Verified integrated main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** none yet  
**Package branch base:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Current package:** Wave 5 — Desktop hosted authentication/session acquisition + real Campaign Administration consumption  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_OPENED.md`  
**Current gate:** implementation + focused automated verification; then explicit DEV deployment/integration and owner Windows Desktop QA before merge  
**Owner implementation authorization:** **GRANTED**

## Read first

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_OPENED.md`;
3. this file;
4. `docs/BRANCH_STATUS.md`;
5. `docs/PROJECT_STATE.md`;
6. `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`;
7. `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`;
8. `docs/decisions/D-0075_ZERO_BUDGET_PROVIDER_POLICY_AND_OWNER_GUIDANCE.md`;
9. `docs/technical/DESKTOP_APPLICATION_SETTINGS_FOLLOWUPS.md`;
10. `docs/checkpoints/2026-09-16_CAMPAIGN_MEMBERSHIP_ADMINISTRATION_CORE_AUTOMATED_VERIFIED.md` for the completed PR #43 predecessor.

Newer current-package records control over older operational prose. Historical checkpoints remain evidence for the state they recorded.

## Current sequence

```text
Wave 4 Player <-> Server                              COMPLETE / INTEGRATED
Wave 5 Desktop shell + local campaign                 COMPLETE / OWNER-QA PASS / MERGED (#42)
hosted membership administration core                COMPLETE / MERGED (#43) / POST-MERGE CI PASS
        |
        v
Desktop hosted auth + real Campaign Administration   ACTIVE
        |
        +-- provider/session adapter                  ACTIVE PACKAGE
        +-- hosted campaign bootstrap/selection       ACTIVE PACKAGE
        +-- real member roster + moderation           ACTIVE PACKAGE
        +-- font/theme preview settings follow-up     ACTIVE PACKAGE
        +-- diagnostics/tests                         ACTIVE PACKAGE
        |
        v
DEV Worker deployment/integration -> owner QA         REQUIRED BEFORE MERGE
```

## Integrated predecessor result

PR #43 — `feat: add hosted campaign membership administration core` — is merged in `main` at `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`.

Post-merge Scaffold `35142092743` completed **SUCCESS**.

The integrated repository therefore contains the active-DM-only hosted member roster, Player `KICK` / `BAN` / `LIFT_BAN` lifecycle actions, idempotent/no-op moderation, campaign-revision discipline, provider-neutral Shared administration client and hosted PostgreSQL contract `0006`.

`LIFT_BAN` remains intentionally `BANNED -> KICKED`; invitation/rejoin later owns `KICKED -> ACTIVE`.

## Current package

Branch `wave5/desktop-hosted-campaign-administration` was created directly from the verified merged `main` and had zero unique commits at opening.

The package owns the smallest coherent Desktop hosted workflow:

- real Desktop hosted authentication/session acquisition while keeping Shared provider-neutral;
- hosted campaign discovery/bootstrap using existing Shared seams and canonical campaign UUIDs;
- real Campaign Administration member roster for an authenticated active DM;
- server-authoritative Kick/Ban/lift-Ban actions with post-mutation refresh;
- understandable hosted/session states without making local-only work unusable offline;
- non-secret QA diagnostics;
- the already-owner-approved Desktop font catalogue and font/theme preview selector follow-up;
- focused automated tests.

Explicit non-goals include invitations/rejoin, co-DM moderation, role editing, PC Manager/Audit, full PC hosted synchronization, generalized RBAC/ACL, Live Combat, Media/Handouts and unrelated infrastructure.

## Deployment boundary

The repository still has no automatic Cloudflare Worker deployment workflow. PR #43 proves the repository/API contract, but the new Campaign Administration routes must not be described as live on the real DEV Worker until explicit deployment evidence exists.

This current package owns that deployment/integration gate because it is the first real Desktop consumer of those routes.

If deployment requires owner-local/provider credentials unavailable safely to the Work environment, finish all safe repository work first and stop only at that narrow gate. Never request secrets in chat or Git.

## Desktop authentication boundary

Android uses the Descope Android SDK and keeps provider details outside Shared. Desktop must preserve the same separation but must not import an Android-only SDK merely because both targets run Kotlin.

For this package, secure session persistence is optional; an honest memory-only session is preferable to writing refresh/session JWTs into ordinary preferences or logs. Sign-out must never delete local campaign/character data.

## Desktop settings follow-up

This is the next genuine Desktop feature build, so `docs/technical/DESKTOP_APPLICATION_SETTINGS_FOLLOWUPS.md` is active scope:

- expand the Desktop font catalogue toward the current selectable Android catalogue using real renderable fonts and legitimate public-repository licensing;
- replace the plain Font selector with preview-oriented choices rendered in the candidate font;
- replace the plain Theme selector with preview cards/forms;
- preserve text-size, spacing-density, workspace-density, theme/font application and persistence.

## Permanent safety rules

- repository intentionally public;
- hard external-service budget remains USD $0;
- never commit, paste, log or expose secrets/tokens/credentials/OTP codes;
- do not reset/delete databases, local campaigns, PCs, outboxes or application state to make QA pass;
- preserve membership/role/ownership/current-control distinctions;
- preserve stable identity, stale-revision, idempotency, tombstone/non-resurrection and no-silent-overwrite guarantees;
- DM authority is not PC ownership;
- Live Combat belongs to a later wave;
- avoid generalized RBAC/ACL or speculative infrastructure.

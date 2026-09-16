# Campaign membership administration core — automated verification complete

**Date:** 2026-09-16 (Chile local time)  
**Wave:** 5 — Desktop shell + Campaign Administration  
**Branch:** `wave5/campaign-membership-administration-core`  
**PR:** #43  
**Starting integrated `main`:** `fb113909cb53b2463bd643cae7d2f54f0673fec4`  
**Verified implementation head:** `fcaa533f79332f6a2f13fb06b7f1bb889dd1982c`  
**Scaffold:** `35140381721` — **SUCCESS**

## Result

The hosted Campaign membership administration core is **IMPLEMENTED / AUTOMATED VERIFIED / READY FOR MERGE AFTER FINAL DOCUMENTATION-HEAD CI**.

No owner/manual QA gate is required for this backend/shared-only package. The package establishes the hosted contracts required before a real Desktop Campaign Administration surface can safely consume member roster/moderation behavior.

## Implemented contract

Backend routes now support:

- `GET /v1/campaigns/{campaignId}/members` — authenticated active-DM-only roster;
- `POST /v1/campaigns/{campaignId}/members/{userId}/moderation` — authenticated active-DM-only Player moderation.

The roster projects the application user ID, display name, campaign role and membership lifecycle status.

Supported Player actions are:

- `KICK`: `ACTIVE -> KICKED`;
- `BAN`: `ACTIVE|KICKED -> BANNED`;
- `LIFT_BAN`: `BANNED -> KICKED`.

`LIFT_BAN` deliberately does not activate membership. A later invitation/rejoin package owns `KICKED -> ACTIVE`, preserving the approved rule that a kicked user may rejoin with a valid invitation while a banned user must first have the ban lifted.

Repeated/no-op actions are idempotent. `KICK` cannot weaken an existing `BANNED` state. Campaign revision advances only when lifecycle state actually changes.

## Safety and authorization

The implementation preserves the existing authority distinctions:

- campaign membership != campaign role != PC ownership != PC control;
- only an ACTIVE DM membership may read the Campaign Administration roster or moderate a Player;
- non-DM/unauthorized access remains fail-closed through the generic hosted `403 / FORBIDDEN` boundary;
- moderation does not delete membership rows;
- moderation does not delete hosted PCs;
- moderation does not rewrite PC owner/controller identity;
- moderation does not clear local caches or outboxes;
- co-DM moderation/generalized RBAC was not invented.

## Shared client

Shared now contains a provider-neutral hosted Campaign Administration client that:

- uses the existing `HostedAccessTokenProvider` boundary;
- reads the member roster;
- submits explicit moderation actions;
- preserves typed hosted authentication/error behavior;
- can later be consumed by Desktop without embedding provider/database credentials in the native client.

## Automated verification

Exact implementation head `fcaa533f79332f6a2f13fb06b7f1bb889dd1982c` passed Scaffold `35140381721`.

The successful gate covered:

- backend TypeScript type-check and Node tests;
- focused Campaign Administration HTTP authorization/lifecycle tests;
- hosted PostgreSQL contracts including `database/tests/0006_campaign_membership_administration_contract.sql`;
- Shared/Desktop tests including the hosted Campaign Administration client tests;
- permanent Android Player guards and Android debug build;
- Desktop build;
- APK artifact upload.

## Real DEV deployment boundary

The repository has no automatic Cloudflare Worker deployment workflow. Therefore this checkpoint proves the **repository/API contract**, not that the new routes are already live in the real DEV Worker.

Real DEV deployment remains an explicit provider/local operation. It should occur when the next real Desktop-hosted consumer package actually requires these routes, together with the corresponding authenticated integration gate. Do not describe these routes as deployed before that happens.

## Explicit non-goals preserved

This package does not implement:

- Desktop provider/session acquisition or login UI;
- Desktop hosted synchronization;
- final Desktop Campaign Administration member UI;
- invitation create/revoke/regenerate/rejoin;
- role editing/co-DM workflows;
- PC ownership/control assignment shortcuts;
- PC Manager/Audit;
- live Combat;
- generalized RBAC/ACL;
- destructive membership/account cleanup;
- Cloudflare Worker deployment.

## Owner-approved Desktop settings follow-up

The next genuine Desktop feature build must also apply the already-recorded settings UX follow-up in `docs/technical/DESKTOP_APPLICATION_SETTINGS_FOLLOWUPS.md`:

- expand the Desktop font catalogue toward Android-equivalent choices where supported;
- replace plain Theme and Font selectors with Android-like preview cards/forms so the result can be previewed before selection.

This does not reorder Wave 5 dependencies.

## Merge / resume rule

PR #43 may merge only after the final documentation-only branch head passes Scaffold and the PR head remains unchanged.

After merge, verify `main` CI. Then continue from current `main` with the next bounded Wave 5 Desktop-hosted consumer package: Desktop hosted authentication/session acquisition and real Campaign Administration consumption, including explicit DEV Worker deployment/integration when those routes are first required.

If this checkpoint is being read after PR #43 has already merged and post-merge CI is green, treat this package as **COMPLETE / INTEGRATED** and do not reopen it without a concrete defect.
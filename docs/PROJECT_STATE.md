# Project State — global repository navigation

**Last verified:** 2026-09-16 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Verified current main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Verified post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** none yet  
**Current package checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_OPENED.md`

## 1. Current authority/topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches and reintegrates only after proportionate verification.

`docs/checkpoints/LATEST.md` controls the practical resume point. `docs/BRANCH_STATUS.md` controls branch lifecycle. Historical checkpoints remain evidence for the state that existed when they were written.

PR #43 is completed and merged. The current branch is `wave5/desktop-hosted-campaign-administration`, created from the verified PR #43 merge with zero unique commits at opening.

## 2. Integrated foundation

The integrated foundation includes:

- account/global identity;
- Campaign;
- Membership + campaign role;
- PC owner vs current controller;
- stable object identities;
- monotonic revisions and stale-write semantics;
- tombstones/non-resurrection semantics;
- durable local hosted outbox;
- authenticated hosted account/campaign bootstrap;
- explicit membership lifecycle (`ACTIVE`, `KICKED`, `BANNED`);
- hosted PC snapshot read/write;
- active-membership plus DM-or-owner/controller server-side PC authorization;
- mutation idempotency and optimistic revisions;
- local-first PC delivery/reconciliation;
- explicit reviewed conflict resolution;
- permanent hosted-sync QA diagnostics;
- persistent Desktop local SQLite campaign context and workbench shell;
- active-DM-only hosted Campaign Administration roster and moderation API/client contract.

## 3. Hosted DEV architecture and deployment boundary

Verified architecture remains:

```text
Android / Desktop clients
        |
        v
Cloudflare Worker/API <---- Descope identity proof
        |
        v
Neon PostgreSQL
```

Android hosted behavior is physically verified through Wave 4. Native clients do not hold database credentials. The hard external-service operating budget remains USD $0 unless the owner changes it.

The repository does not automatically deploy the Cloudflare Worker. The PR #43 Campaign Administration routes are repository/API-contract verified but must not be described as live on the real DEV Worker until explicit deployment evidence exists.

The current Desktop hosted package owns that deployment/integration gate because it is the first real consumer of those routes.

## 4. Completed predecessor packages

### Wave 4 Player <-> Server — complete

Wave 4 is integrated for remembered authentication, campaign bootstrap/delivery, PC push/pull, multi-client convergence, explicit conflict resolution and membership revoke/reinstate authorization behavior.

### Wave 5 package A — Desktop shell/local campaign — complete

PR #42 merged as `fb113909cb53b2463bd643cae7d2f54f0673fec4`; post-merge Scaffold `35138029472` passed.

Owner Windows Desktop QA passed the workbench shell, local campaign persistence/context, Spanish product language, persistent Application Settings and QA/diagnostic copy surface.

### Wave 5 package B — hosted Campaign membership administration core — complete

PR #43 merged as `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`; post-merge Scaffold `35142092743` passed.

Integrated behavior includes:

- ACTIVE-DM-only hosted member roster;
- member application account identity/display name, campaign role and lifecycle status;
- Player `KICK`, `BAN`, `LIFT_BAN` actions;
- `LIFT_BAN` = `BANNED -> KICKED`;
- idempotent/no-op moderation;
- campaign revision changes only for real lifecycle changes;
- no membership-row/PC/owner-controller destruction;
- provider-neutral Shared Campaign Administration client;
- hosted PostgreSQL contract `0006` and focused backend/Shared tests.

## 5. Wave 5 package C — active

Current package:

**Desktop hosted authentication/session acquisition + real Campaign Administration consumption**

Branch:

`wave5/desktop-hosted-campaign-administration`

Required bounded scope:

- Desktop-specific hosted auth/session adapter plugged into `HostedAccessTokenProvider`;
- keep Shared provider-neutral and provider credentials/secrets out of the repository;
- hosted account/campaign discovery and bootstrap through the existing Shared service;
- canonical hosted campaign UUID convergence without a second link table or silent duplicate;
- real hosted Campaign Administration roster;
- Kick/Ban/lift-Ban UX that waits for server authority and refreshes authoritative state after success;
- hosted/session UX that does not disable offline/local-only work;
- non-secret copyable diagnostics;
- Desktop settings follow-up: current Android selectable font catalogue where technically/licensing-wise appropriate, real font rendering, font previews and theme previews;
- focused automated tests.

## 6. Authentication/session direction

Android keeps Descope-specific implementation in Android code and exposes only a short-lived token provider to Shared. Desktop must preserve that architecture.

The Descope Kotlin SDK in use is Android-specific; Desktop must not introduce it as if it were a general JVM/Desktop SDK. A Desktop platform adapter may use the documented Descope authentication HTTP contract with the public project ID already present in the repository.

Session/refresh JWTs are secrets. They must not be logged, exposed in diagnostics, committed or stored in ordinary preferences. For this bounded package, a memory-only Desktop session is an acceptable honest limitation if secure persisted credential storage is not introduced.

Sign-out clears hosted session state only; it must not delete local campaign/character state.

## 7. Campaign identity/bootstrap direction

Use `HostedApiClient.campaigns()` and `HostedCampaignBootstrapService` rather than inventing a Desktop-only API model or campaign-link table.

Hosted campaign identity remains the canonical campaign UUID. Hosted bootstrap must preserve revisions, tombstones, conflict semantics and membership lifecycle behavior already implemented in Shared.

A local-only campaign remains local-only unless a real hosted campaign with the same canonical identity is bootstrapped; hosted access loss must not destroy local data.

## 8. Campaign Administration behavior

For an authenticated active DM and a hosted campaign:

- show the real member roster with display name, role and lifecycle status;
- do not make UUIDs the primary human label;
- expose only valid Player moderation actions;
- `KICK` must never weaken an existing ban;
- `LIFT_BAN` returns the member to `KICKED`, not `ACTIVE`;
- do not simulate success locally before the server confirms;
- refresh/reconcile authoritative state after a mutation;
- errors must not silently mutate local truth;
- no co-DM moderation in this package.

## 9. Desktop settings follow-up

This package is the next genuine Desktop feature build, so the owner-approved follow-up is active scope:

- use the actual current Android selectable catalogue as the parity reference, excluding Android-hidden historical choices;
- real named fonts must render as themselves; no fake labels over a common fallback;
- use legitimate free/public-repository licensing and avoid paid dependencies;
- replace the plain font selector with visual samples rendered in each candidate font;
- replace the plain theme dropdown with preview cards/forms;
- preserve the current Desktop theme catalogue unless evidence requires parity expansion;
- preserve text-size, spacing density, workspace density, persistence and application of all settings.

## 10. Explicit current-package non-goals

Do not expand this package into invitations/rejoin, co-DM role administration, PC Manager/Audit, ownership/control shortcuts, full PC hosted synchronization, Managers, Media/Handouts/object storage, Live Combat, System Administration, backup/export/PDF hardening, broad Player redesign, generalized RBAC/ACL or unrelated infrastructure.

## 11. Security / operating residuals

D-0075 remains controlling: repository intentionally public, hard USD $0 external-service budget, never commit secrets, and paid/overage commitments require explicit owner approval.

Preserve fail-closed authorization, least privilege, local-first safety, stable identities, stale-revision protection, idempotency, tombstones/non-resurrection and no-silent-overwrite guarantees.

Do not use destructive recovery or delete/reset the owner's existing local QA campaign merely to make hosted QA pass.

## 12. QA / acceptance gate

Automated tests and Scaffold must pass on the exact implementation head.

They do not substitute for:

1. explicit real DEV Worker deployment/integration evidence for the PR #43 routes; and
2. owner Windows Desktop QA for hosted login, roster/moderation behavior and visual settings previews.

Do not merge the owner-testable Desktop surface before required owner QA passes.

## 13. Resume rule

Read, in order:

1. `docs/checkpoints/LATEST.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_OPENED.md`;
3. `docs/BRANCH_STATUS.md`;
4. D-0072, D-0073 and D-0075 as needed;
5. `docs/technical/DESKTOP_APPLICATION_SETTINGS_FOLLOWUPS.md`;
6. the completed PR #43 automated-verification checkpoint for predecessor contract evidence.

Continue on `wave5/desktop-hosted-campaign-administration`; do not restart PR #43 work or reopen the completed Desktop-shell package.

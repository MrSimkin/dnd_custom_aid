# Project State — global repository navigation

**Last verified:** 2026-09-16 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Verified current main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Verified post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Current package checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_IMPLEMENTED.md`  
**Latest verified implementation head:** `58dc05933e35d47bc8f65f0249a2a0b74d6206c4`  
**Latest verified implementation Scaffold:** `35150382923` — **SUCCESS**

## 1. Current authority/topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches and reintegrates only after proportionate verification.

`docs/checkpoints/LATEST.md` controls the practical resume point. `docs/BRANCH_STATUS.md` controls branch lifecycle. Historical checkpoints remain evidence for the state that existed when they were written.

PR #43 is completed and merged. The current branch is `wave5/desktop-hosted-campaign-administration`, with draft PR #44. Its bounded repository implementation is complete and CI-verified; explicit DEV integration and owner Windows Desktop QA remain pending.

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

The current Desktop hosted package owns that deployment/integration gate because it is the first real Desktop consumer of those routes.

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

## 5. Wave 5 package C — repository implementation complete / external verification pending

Current package:

**Desktop hosted authentication/session acquisition + real Campaign Administration consumption**

Branch:

`wave5/desktop-hosted-campaign-administration`

PR:

`#44 — draft`

Exact verified implementation head:

`58dc05933e35d47bc8f65f0249a2a0b74d6206c4`

Scaffold:

`35150382923` — **SUCCESS**

Implemented bounded scope:

- Desktop-specific Descope email-OTP/session adapter plugged into `HostedAccessTokenProvider` while Shared remains provider-neutral;
- memory-only session/refresh JWT handling with refresh-before-use and fail-closed session clearing;
- hosted auth/session UX wired into the Desktop workbench without disabling local-only work;
- hosted account/campaign discovery and canonical bootstrap through the existing Shared service;
- canonical hosted campaign UUID convergence without a second link table or silent duplicate;
- real hosted Campaign Administration roster;
- valid Player-only Kick/Ban/Lift-ban UX with confirmation, server authority and authoritative refresh after success;
- no DM-row moderation affordances;
- non-secret copyable diagnostics;
- Desktop font/theme preview settings follow-up;
- bundled Geist and Mona Sans Condensed resources with licensing/provenance records;
- conditional honest availability for other Android-equivalent named font families;
- focused auth, moderation and preference/fallback tests.

The repository implementation is not equivalent to real DEV integration. External deployment/integration evidence remains the next gate.

## 6. Authentication/session implementation

Desktop preserves the same architectural boundary as Android: provider-specific Descope code stays in platform code, while Shared sees only `HostedAccessTokenProvider`.

The Desktop adapter uses the provider authentication HTTP contract rather than importing the Android-specific Descope SDK as a false JVM/Desktop dependency.

Session/refresh JWTs remain memory-only. They are not logged, exposed in diagnostics, committed or stored in ordinary Desktop preferences. Refresh failure clears hosted session state rather than weakening authentication guarantees.

Sign-out clears hosted session state only; it does not delete local campaign/character state.

## 7. Campaign identity/bootstrap implementation

Desktop uses the existing hosted API/client and `HostedCampaignBootstrapService` rather than a Desktop-only campaign-link model.

Hosted campaign identity remains the canonical campaign UUID. The implementation preserves the established Shared convergence and membership semantics rather than creating silent local duplicates.

Local-only campaign work remains usable without hosted auth, and hosted session/access changes do not delete local campaign data.

## 8. Campaign Administration implementation

For the hosted administration surface, the repository implementation now:

- reads the real hosted member roster;
- uses display name where available instead of making UUIDs the primary human label;
- exposes moderation only for Player rows;
- provides `KICK` / `BAN` for active Players, `BAN` for kicked Players and `LIFT_BAN` for banned Players;
- never offers moderation for DM rows;
- preserves `LIFT_BAN = BANNED -> KICKED`, not `ACTIVE`;
- waits for server confirmation before treating moderation as successful;
- refreshes authoritative roster state after a successful mutation;
- surfaces hosted errors without silently mutating local truth.

Real DEV behavior remains to be verified after explicit deployment/integration.

## 9. Desktop settings follow-up — implemented

The owner-approved settings follow-up is implemented and CI-verified:

- the current Android selectable font catalogue remains the parity reference;
- Geist and Mona Sans Condensed are guaranteed bundled Desktop choices using existing repository assets with explicit license/provenance records;
- other Android-equivalent named choices are offered only when the exact system font family is actually available;
- unavailable/removed persisted named choices safely fall back to system sans rather than presenting a false label;
- font selection uses visual samples rendered in the candidate font;
- theme selection uses preview cards with representative palette/surface content;
- the existing Desktop theme catalogue is preserved;
- text-size, spacing density, workspace density and preference persistence remain present;
- focused tests cover persistence and font fallback/availability semantics.

Visual behavior and normal close/relaunch persistence still require owner Windows Desktop QA on the owner-testable build.

## 10. Explicit current-package non-goals

Do not expand this package into invitations/rejoin, co-DM role administration, PC Manager/Audit, ownership/control shortcuts, full PC hosted synchronization, Managers, Media/Handouts/object storage, Live Combat, System Administration, backup/export/PDF hardening, broad Player redesign, generalized RBAC/ACL or unrelated infrastructure.

## 11. Security / operating residuals

D-0075 remains controlling: repository intentionally public, hard USD $0 external-service budget, never commit secrets, and paid/overage commitments require explicit owner approval.

Preserve fail-closed authorization, least privilege, local-first safety, stable identities, stale-revision protection, idempotency, tombstones/non-resurrection and no-silent-overwrite guarantees.

Do not use destructive recovery or delete/reset the owner's existing local QA campaign merely to make hosted QA pass.

## 12. QA / acceptance gate

Repository implementation and automated verification are complete for the current bounded code state.

Still required before merge:

1. explicit real DEV Worker deployment/integration evidence for the PR #43 Campaign Administration routes;
2. real Desktop authentication/bootstrap/roster/moderation integration verification against that DEV environment;
3. owner Windows Desktop QA for hosted flows, local-data preservation and visual/settings persistence behavior;
4. narrowly scoped fixes + CI if integration/QA finds defects;
5. final PR readiness and merge verification only after owner QA passes.

A green Scaffold does not substitute for these external/manual gates.

## 13. Resume rule

Read, in order:

1. `docs/checkpoints/LATEST.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_IMPLEMENTED.md`;
3. `docs/BRANCH_STATUS.md`;
4. D-0072, D-0073 and D-0075 as needed;
5. `docs/technical/DESKTOP_APPLICATION_SETTINGS_FOLLOWUPS.md` for historical requirement context;
6. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_OPENED.md` for package opening rationale;
7. the completed PR #43 automated-verification checkpoint for predecessor contract evidence.

Continue on `wave5/desktop-hosted-campaign-administration`; do not restart PR #43 work or reopen the completed Desktop-shell package. The next substantive gate is explicit DEV Worker deployment/integration, not more speculative repository implementation.

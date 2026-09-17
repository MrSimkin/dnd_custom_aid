# Project State — global repository navigation

**Last verified:** 2026-09-16 (Chile local time)  
**Owner integrated-MVP implementation authorization:** **GRANTED**  
**Normal integrated trunk:** `main`  
**Verified current main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Verified post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44  
**Current package checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_OWNER_QA_COMPLETE.md`  
**Deployed repository head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**DEV Worker deployment:** **VERIFIED**  
**Owner Windows QA:** **PASS**

## 1. Current authority/topology

`main` is the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches and reintegrates only after proportionate verification.

`docs/checkpoints/LATEST.md` controls the practical resume point. `docs/BRANCH_STATUS.md` controls branch lifecycle. Historical checkpoints remain evidence for the state that existed when they were written.

PR #42 and PR #43 are complete and merged. PR #44 contains the bounded Desktop hosted Campaign Administration package. Repository implementation, real DEV Worker deployment and full owner Windows acceptance QA are complete. The only remaining package gate is final exact-head CI followed by PR-ready/merge/post-merge verification.

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
- persistent Desktop local SQLite campaign context/workbench shell;
- active-DM-only hosted Campaign Administration roster and moderation API/client contract.

## 3. Hosted DEV architecture

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

Native clients do not hold database credentials. The hard external-service operating budget remains USD $0 unless the owner explicitly changes it.

The current Cloudflare Worker is `dnd-custom-aid-api` at `https://dnd-custom-aid-api.mrsimkin-dev.workers.dev`.

The Wave 5 Campaign Administration code was explicitly deployed from repository head `a6c0532878e8ef49ddfb894fa71076c1af73587a`; Cloudflare reported Version ID `130d35e7-7903-47b2-8203-d74f9ec3db55`.

Deployment evidence passed `/health -> 200` and unauthenticated roster-route recognition/auth enforcement -> `401 UNAUTHENTICATED`.

Authenticated real Desktop behavior is now also verified end-to-end against the live Worker and Neon database.

## 4. Completed Wave 5 predecessor packages

### Package A — Desktop shell/local campaign — complete

PR #42 merged as `fb113909cb53b2463bd643cae7d2f54f0673fec4`. Owner Windows QA passed persistent Desktop local campaign state/context, Spanish product UI, Application Settings and bounded QA diagnostics.

### Package B — hosted Campaign membership administration core — complete

PR #43 merged as `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`.

Integrated behavior includes ACTIVE-DM-only roster, Player `KICK` / `BAN` / `LIFT_BAN`, idempotent/no-op moderation, campaign-revision discipline, provider-neutral Shared client and hosted database contract coverage.

## 5. Wave 5 package C — PR #44

Package objective:

**Desktop hosted authentication/session acquisition + real Campaign Administration consumption**

Implemented and verified:

- Desktop-specific Descope email-OTP/session adapter through `HostedAccessTokenProvider`;
- memory-only session/refresh JWT handling with refresh-before-use and fail-closed clearing;
- hosted auth/session UX without disabling local-only work;
- hosted account/campaign discovery and canonical bootstrap;
- real hosted member-roster UI;
- Player-only Kick/Ban/Lift-Ban UI with confirmation, server authority and authoritative refresh;
- no DM-row moderation affordances;
- non-secret diagnostics;
- font/theme preview settings follow-up;
- bundled Geist and Mona Sans Condensed with provenance/licensing;
- focused auth/moderation/preference/fallback tests.

## 6. Owner Windows QA — complete

The owner physically verified:

- Desktop launch and preservation of existing local campaigns;
- real DEV OTP login;
- hosted bootstrap and roster retrieval;
- DM moderation guard;
- real moderation `ACTIVE -> KICKED -> BANNED -> KICKED` with revisions `0 -> 1 -> 2 -> 3`;
- canonical Outlook owner/DM migration at revision `4`;
- Outlook Desktop bootstrap `1 hosted / 1 applied / 0 conflicts` and `DM / ACTIVE` roster at revision `4`;
- Gmail absent from the current roster;
- font/theme visual preview cards;
- device-local preference persistence across application relaunch;
- memory-only hosted session behavior: application relaunch starts signed out;
- Outlook reauthentication restores the hosted DM context;
- explicit sign-out clears hosted session while preserving local campaign data and settings.

Normal hosted DEV owner/DM testing now uses the Outlook-backed application identity. Gmail is historical/inactive by default and may be used only when a deliberate multi-user test requires a secondary identity. Historical Gmail mutation receipts remain truthful and untouched.

## 7. Current package gate

1. **COMPLETE:** repository implementation and CI verification;
2. **COMPLETE:** explicit DEV Worker deployment / route verification;
3. **COMPLETE:** real Desktop auth/bootstrap/roster QA;
4. **COMPLETE:** real moderation sequence QA;
5. **COMPLETE:** canonical Outlook DEV identity migration + Desktop verification;
6. **COMPLETE:** settings persistence and sign-out/relaunch/local-data-preservation QA;
7. **NEXT:** exact-head Scaffold on final readiness documentation;
8. if green and `main` remains unchanged, mark PR #44 ready and merge with expected-head protection;
9. verify post-merge `main` and post-merge CI;
10. advance to the next Wave 5 package.

## 8. Authentication/session rules

Desktop keeps provider-specific Descope code outside Shared; Shared remains provider-neutral.

Session/refresh JWTs remain memory-only and must not be logged, exposed through diagnostics, committed or persisted in ordinary Desktop preferences. Sign-out clears hosted session state without deleting local campaign/character state. Real Windows QA confirmed those behaviors.

## 9. Campaign Administration rules

For a real hosted active-DM campaign, Desktop displays the authoritative roster, exposes moderation only for Player rows, preserves `LIFT_BAN = BANNED -> KICKED`, waits for server confirmation, refreshes authoritative state after success, and surfaces errors without silently changing local truth. Real DEV QA confirmed the full approved moderation state machine.

## 10. Explicit current-package non-goals

PR #44 does not own invitations/rejoin, co-DM administration, role editing, PC Manager/Audit, ownership/control shortcuts, full new PC sync architecture, Managers, Media/Handouts/object storage, Live Combat, System Administration, backup/export/PDF hardening, broad Player redesign or generalized RBAC/ACL.

## 11. Security / operating residuals

D-0075 remains controlling: repository intentionally public, hard USD $0 external-service budget, never commit secrets, and paid/overage commitments require explicit owner approval.

The known owner-local backend install continues to report **3 high severity npm vulnerabilities**. Do not run `npm audit fix --force` blindly; inspect exact packages/reachability/fixed versions in a later explicit security-hardening pass.

Preserve fail-closed authorization, least privilege, local-first safety, stable identities, stale-revision protection, idempotency, tombstones/non-resurrection and no-silent-overwrite guarantees.

## 12. Resume rule

Read, in order:

1. `docs/checkpoints/LATEST.md`;
2. `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_OWNER_QA_COMPLETE.md`;
3. `docs/BRANCH_STATUS.md`;
4. this file;
5. earlier package checkpoints only as needed.

Do not redeploy Cloudflare unless Worker code changes. The current action is final exact-head CI and PR #44 merge readiness.

# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified current main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_IMPLEMENTED.md`  
**Latest verified implementation head:** `58dc05933e35d47bc8f65f0249a2a0b74d6206c4`  
**Latest verified implementation Scaffold:** `35150382923` — **SUCCESS**  
**Lifecycle state:** repository implementation complete / explicit DEV integration + owner QA pending

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal integrated-MVP trunk. Current verified integrated head:

`f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`

That commit merged PR #43. Post-merge Scaffold `35142092743` completed **SUCCESS**.

## 2. Completed Wave 5 predecessors

### `wave5/desktop-workbench-shell` / PR #42

Completed and merged. Owner Windows QA passed persistent Desktop local campaign state/context, Spanish product UI, Application Settings and bounded QA diagnostics.

### `wave5/campaign-membership-administration-core` / PR #43

Completed and merged as `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`.

Integrated scope includes active-DM-only member roster, Player `KICK` / `BAN` / `LIFT_BAN`, idempotent/no-op moderation, campaign-revision discipline, provider-neutral Shared Campaign Administration client and hosted database contract `0006`.

Do not resume or reopen either completed branch for current implementation.

## 3. Current focused branch / PR

Branch:

`wave5/desktop-hosted-campaign-administration`

PR:

`#44 — feat: add Desktop hosted campaign administration` — **draft**

Created from:

`f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`

Package objective:

**Desktop hosted authentication/session acquisition + real Campaign Administration consumption**

The package also owns the approved Desktop font catalogue + font/theme preview settings follow-up because this is the next genuine Desktop feature build.

## 4. Verified repository implementation

Verified implementation head:

`58dc05933e35d47bc8f65f0249a2a0b74d6206c4`

Scaffold:

`35150382923` — **SUCCESS**.

Verified repository scope now includes:

- Desktop-only Descope email OTP/session adapter with provider-neutral Shared token seam;
- memory-only session/refresh JWT handling and refresh-before-use behavior;
- hosted auth wired into the Desktop workbench;
- canonical hosted campaign bootstrap/convergence through the existing Shared service;
- real hosted member roster consumption;
- server-authoritative Player moderation with confirmation and authoritative refresh after success;
- no moderation affordances for DM rows;
- non-secret hosted diagnostics;
- preview-oriented Desktop font/theme settings;
- bundled Geist and Mona Sans Condensed Desktop resources with license/provenance records;
- honest conditional availability for other Android-equivalent named font families;
- focused auth/moderation/font-preference tests;
- preservation of local-only campaign operation without hosted auth.

The repository implementation phase is complete enough to advance to external DEV integration. This does not yet establish that the relevant routes are live on the real DEV Worker.

## 5. Current package gate

Current progression:

1. **COMPLETE:** repository implementation and exact-head Scaffold verification;
2. **NEXT:** inspect and perform only the safe/authorized explicit DEV Worker deployment/integration path;
3. obtain explicit evidence that the Campaign Administration routes are live and usable on DEV;
4. perform bounded owner Windows Desktop QA for real auth/bootstrap/roster/moderation plus settings previews/persistence and local-data preservation;
5. fix any QA/integration defects in narrowly scoped commits with CI verification;
6. finalize PR documentation and final-head verification as needed;
7. mark PR ready and merge only after required owner QA passes, with expected-head safety and post-merge `main` verification.

A green repository build alone does not replace real external integration or Desktop visual/behavioral QA.

## 6. Deployment boundary

There is no automatic Cloudflare Worker deployment workflow in the repository. The PR #43 routes are repository/API-contract verified but must not yet be described as live on the real DEV Worker without explicit deployment evidence.

This branch is the first Desktop consumer and therefore owns explicit DEV deployment/integration evidence.

If deployment requires credentials available only in the owner's local/provider environment, finish safe repository work first and stop only at that narrow gate. Never request those credentials in chat or Git.

## 7. Authentication boundary

The current Android Descope SDK is Android-specific. Desktop platform code implements the provider-specific authentication boundary outside Shared; Shared continues to receive only the provider-neutral `HostedAccessTokenProvider`.

Session and refresh JWTs are secrets and remain memory-only in the bounded implementation. They must not be committed, logged, exposed through diagnostics or stored in ordinary Desktop preferences.

Sign-out must not delete local campaign/character state.

## 8. Protected invariants

Preserve local-first persistence, stable identity, membership/role/ownership/current-control distinctions, DM authority distinct from ownership, stale-revision/idempotency/tombstone/no-silent-overwrite semantics, the hard USD $0 policy and public-repository secret hygiene.

Do not delete/reset the existing local Desktop QA campaign as part of hosted testing.

## 9. Explicit non-goals

This branch does not own invitation creation/revoke/regenerate, kicked-member rejoin, co-DM moderation, role editing, PC Manager/Audit, full PC hosted synchronization, Live Combat, generalized RBAC/ACL, Media/Handouts/object storage, System Administration, backup/export/PDF hardening or unrelated infrastructure.

## 10. Non-authoritative stray ref

`__should_not_create__` remains non-authoritative, contains no unique current work and is not a continuation point. Opportunistic safe deletion is optional and not a blocker.

## 11. Exact resume rule

Resume only on PR #44 / `wave5/desktop-hosted-campaign-administration` from `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_IMPLEMENTED.md` or a newer current-package checkpoint. Treat PR #43 as integrated and its historical pre-merge wording as historical evidence rather than current branch truth.

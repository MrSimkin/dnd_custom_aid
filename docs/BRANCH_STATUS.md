# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified current main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44 — draft  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_AUTH_SLICE_VERIFIED.md`  
**Latest verified implementation head:** `9c6994cff2782e2c3c8f37e08d4f0e483ae13322`  
**Latest verified Scaffold:** `35146769605` — **SUCCESS**  
**Lifecycle state:** active implementation

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

The package also owns the already-approved Desktop font catalogue + font/theme preview settings follow-up because this is the next genuine Desktop feature build.

## 4. Verified progress

Verified implementation head:

`9c6994cff2782e2c3c8f37e08d4f0e483ae13322`

Scaffold:

`35146769605` — **SUCCESS**.

Verified slice:

- Desktop-only Descope email OTP/session adapter;
- provider-neutral Shared token seam preserved;
- memory-only session/refresh JWT handling;
- refresh-before-use logic with focused tests;
- sign-out clears hosted session state without local campaign/character deletion.

The workbench UI has not yet consumed this adapter; hosted campaign bootstrap, roster/moderation UI and the font/theme preview follow-up remain active package work.

## 5. Current package gate

Current progression:

1. wire Desktop hosted auth/session state into the workbench;
2. consume hosted campaign discovery/bootstrap and canonical campaign identity;
3. consume real hosted member roster and server-authoritative moderation;
4. implement settings font catalogue and preview-oriented font/theme selectors;
5. extend non-secret diagnostics and focused tests;
6. pass Scaffold on the exact implementation head;
7. explicitly deploy/verify the DEV Worker routes if repository automation still cannot do so;
8. perform bounded owner Windows Desktop QA;
9. only then finalize PR documentation, re-run final-head Scaffold if needed, mark PR ready, merge with expected-head safety and verify post-merge `main`.

A green build alone does not replace real Desktop visual/behavioral QA.

## 6. Deployment boundary

There is no automatic Cloudflare Worker deployment workflow in the repository. The PR #43 routes are repository/API-contract verified but not yet claimed live on the real DEV Worker.

This branch is the first Desktop consumer and therefore owns explicit DEV deployment/integration evidence.

If deployment requires credentials available only in the owner's local/provider environment, finish safe repository work first and stop only at that narrow gate. Never request those credentials in chat or Git.

## 7. Authentication boundary

The current Android Descope SDK is Android-specific. Desktop platform code may implement the provider's documented public authentication HTTP contract, but Shared must continue to receive only a provider-neutral `HostedAccessTokenProvider`.

Session and refresh JWTs are secrets. Do not commit, log, diagnose or store them in ordinary Desktop preferences. Memory-only session storage remains the bounded approach for this package unless a platform-secure persistence mechanism is deliberately added.

Sign-out must not delete local campaign/character state.

## 8. Protected invariants

Preserve local-first persistence, stable identity, membership/role/ownership/current-control distinctions, DM authority distinct from ownership, stale-revision/idempotency/tombstone/no-silent-overwrite semantics, the hard USD $0 policy and public-repository secret hygiene.

Do not delete/reset the existing local Desktop QA campaign as part of hosted testing.

## 9. Explicit non-goals

This branch does not own invitation creation/revoke/regenerate, kicked-member rejoin, co-DM moderation, role editing, PC Manager/Audit, full PC hosted synchronization, Live Combat, generalized RBAC/ACL, Media/Handouts/object storage, System Administration, backup/export/PDF hardening or unrelated infrastructure.

## 10. Non-authoritative stray ref

`__should_not_create__` remains non-authoritative, contains no unique current work and is not a continuation point. Opportunistic safe deletion is optional and not a blocker.

## 11. Exact resume rule

Resume only on PR #44 / `wave5/desktop-hosted-campaign-administration` from `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_AUTH_SLICE_VERIFIED.md` or a newer current-package checkpoint. Treat PR #43 as integrated and its historical pre-merge wording as historical evidence rather than current branch truth.

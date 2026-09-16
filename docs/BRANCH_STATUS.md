# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified current main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** none yet  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_CAMPAIGN_ADMINISTRATION_OPENED.md`  
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

## 3. Current focused branch

Branch:

`wave5/desktop-hosted-campaign-administration`

Created from:

`f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`

Opening state:

- exact verified merged `main` base;
- zero unique implementation commits;
- no package PR opened yet.

Package objective:

**Desktop hosted authentication/session acquisition + real Campaign Administration consumption**

The package also owns the already-approved Desktop font catalogue + font/theme preview settings follow-up because this is the next genuine Desktop feature build.

## 4. Current package gate

Current progression:

1. establish package checkpoint/current-state continuity;
2. implement Desktop hosted auth/session adapter without exposing provider details through Shared;
3. consume hosted campaign discovery/bootstrap and canonical campaign identity;
4. consume real hosted member roster and server-authoritative moderation;
5. implement settings font catalogue and preview-oriented font/theme selectors;
6. extend non-secret diagnostics and focused tests;
7. pass Scaffold on the exact implementation head;
8. explicitly deploy/verify the DEV Worker routes if repository automation still cannot do so;
9. perform bounded owner Windows Desktop QA;
10. only then finalize PR documentation, re-run final-head Scaffold if needed, merge with expected-head safety and verify post-merge `main`.

A green build alone does not replace real Desktop visual/behavioral QA.

## 5. Deployment boundary

There is no automatic Cloudflare Worker deployment workflow in the repository. The PR #43 routes are repository/API-contract verified but not yet claimed live on the real DEV Worker.

This branch is the first Desktop consumer and therefore owns explicit DEV deployment/integration evidence.

If deployment requires credentials available only in the owner's local/provider environment, finish safe repository work first and stop only at that narrow gate. Never request those credentials in chat or Git.

## 6. Authentication boundary

The current Android Descope SDK is Android-specific. Desktop platform code may implement the provider's documented public authentication HTTP contract, but Shared must continue to receive only a provider-neutral `HostedAccessTokenProvider`.

Session and refresh JWTs are secrets. Do not commit, log, diagnose or store them in ordinary Desktop preferences. Memory-only session storage is the default bounded approach for this package unless a platform-secure persistence mechanism is deliberately added.

Sign-out must not delete local campaign/character state.

## 7. Protected invariants

Preserve local-first persistence, stable identity, membership/role/ownership/current-control distinctions, DM authority distinct from ownership, stale-revision/idempotency/tombstone/no-silent-overwrite semantics, the hard USD $0 policy and public-repository secret hygiene.

Do not delete/reset the existing local Desktop QA campaign as part of hosted testing.

## 8. Explicit non-goals

This branch does not own invitation creation/revoke/regenerate, kicked-member rejoin, co-DM moderation, role editing, PC Manager/Audit, full PC hosted synchronization, Live Combat, generalized RBAC/ACL, Media/Handouts/object storage, System Administration, backup/export/PDF hardening or unrelated infrastructure.

## 9. Non-authoritative stray ref

`__should_not_create__` remains non-authoritative, contains no unique current work and is not a continuation point. Opportunistic safe deletion is optional and not a blocker.

## 10. Exact resume rule

Resume only on `wave5/desktop-hosted-campaign-administration` from the latest current-package checkpoint. Treat PR #43 as integrated and its historical pre-merge wording as historical evidence rather than current branch truth.

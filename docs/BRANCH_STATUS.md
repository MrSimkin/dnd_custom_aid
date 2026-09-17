# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified current main:** `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`  
**Post-merge Scaffold:** `35142092743` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-hosted-campaign-administration`  
**Current PR:** #44  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_OWNER_QA_COMPLETE.md`  
**Deployed code head:** `a6c0532878e8ef49ddfb894fa71076c1af73587a`  
**Deployed-head Scaffold:** `35163179550` — **SUCCESS**  
**Lifecycle state:** repository implementation complete / DEV deployment verified / full owner Windows QA PASS / final exact-head CI pending

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal integrated-MVP trunk. Current verified integrated head:

`f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`

That commit merged PR #43. Post-merge Scaffold `35142092743` completed **SUCCESS**.

During the PR #44 readiness sweep, `main` remained exactly at this package base.

## 2. Completed Wave 5 predecessors

### `wave5/desktop-workbench-shell` / PR #42

Completed and merged. Owner Windows QA passed persistent Desktop local campaign state/context, Spanish product UI, Application Settings and bounded QA diagnostics.

### `wave5/campaign-membership-administration-core` / PR #43

Completed and merged as `f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`.

Integrated scope includes active-DM-only member roster, Player `KICK` / `BAN` / `LIFT_BAN`, idempotent/no-op moderation, campaign-revision discipline, provider-neutral Shared Campaign Administration client and hosted database contract coverage.

Do not resume either completed branch for current implementation.

## 3. Current focused branch / PR

Branch:

`wave5/desktop-hosted-campaign-administration`

PR:

`#44 — feat: add Desktop hosted campaign administration`

Created from:

`f58ae3a2c48f79383f96d42b5a4c098b1fdd8ded`

Package objective:

**Desktop hosted authentication/session acquisition + real Campaign Administration consumption**

The package also owns the approved Desktop font catalogue + font/theme preview settings follow-up.

## 4. Repository implementation / CI

Repository implementation is complete. The deployed implementation head:

`a6c0532878e8ef49ddfb894fa71076c1af73587a`

passed Scaffold `35163179550` — **SUCCESS**.

The immediately preceding documentation/readiness head `281275638855a1200cecee9f1beef806638d11e6` passed Scaffold `35168371109` — **SUCCESS**.

The final readiness documentation commit still requires its own exact-head Scaffold before merge.

## 5. DEV deployment and real integration — verified

The Campaign Administration Worker code was explicitly deployed to the existing DEV Worker `dnd-custom-aid-api` from repository head `a6c0532878e8ef49ddfb894fa71076c1af73587a`.

Cloudflare Version ID: `130d35e7-7903-47b2-8203-d74f9ec3db55`.

Verified real DEV behavior includes:

- Worker route presence/auth enforcement;
- real Desktop Descope OTP authentication;
- hosted bootstrap/convergence;
- authoritative member roster;
- DM moderation guard;
- real `ACTIVE -> KICKED -> BANNED -> KICKED` moderation;
- authoritative revision progression `0 -> 1 -> 2 -> 3`;
- controlled canonical Outlook owner/DM migration at revision `4`;
- Outlook Desktop bootstrap `1 / 1 / 0` and sole `DM / ACTIVE` roster at revision `4`;
- Gmail absent from the current campaign membership;
- settings preview cards and persistence;
- memory-only hosted-session behavior across app shutdown;
- explicit sign-out preserving local data/settings.

## 6. Current package gate

Current progression:

1. **COMPLETE:** repository implementation;
2. **COMPLETE:** repository CI for implementation;
3. **COMPLETE:** DEV Worker deployment;
4. **COMPLETE:** real Windows Desktop auth/bootstrap/roster QA;
5. **COMPLETE:** real moderation QA;
6. **COMPLETE:** Outlook canonical DEV owner/DM migration + verification;
7. **COMPLETE:** final settings persistence / shutdown-session / explicit-sign-out QA;
8. **NEXT:** exact-head Scaffold for final readiness documentation;
9. confirm `main` still equals package base;
10. mark PR #44 ready for review;
11. merge with expected-head safety;
12. verify post-merge `main` and post-merge CI.

No unresolved PR review threads or conversation comments were present during the readiness sweep.

## 7. Authentication boundary

Desktop provider-specific authentication remains outside Shared. Session and refresh JWTs are memory-only and must not be committed, logged, exposed through diagnostics or stored in ordinary Desktop preferences. Real owner QA verified that application shutdown and explicit sign-out clear hosted session state without deleting local campaign data/settings.

## 8. Canonical DEV identity

Normal hosted DEV owner/DM testing uses the Outlook-backed application identity.

Gmail is historical/inactive by default and may later be used only when a test explicitly requires a secondary identity. Historical Gmail mutation receipts are preserved.

## 9. Protected invariants

Preserve local-first persistence, stable identity, membership/role/ownership/current-control distinctions, DM authority distinct from ownership, stale-revision/idempotency/tombstone/no-silent-overwrite semantics, the hard USD $0 policy and public-repository secret hygiene.

## 10. Explicit non-goals

This branch does not own invitation creation/revoke/regenerate, kicked-member rejoin, co-DM moderation, role editing, PC Manager/Audit, full new PC hosted synchronization, Live Combat, generalized RBAC/ACL, Media/Handouts/object storage, System Administration, backup/export/PDF hardening or unrelated infrastructure.

## 11. Non-authoritative stray refs

Any stray branch whose name indicates it should not have been created and which contains no unique work is non-authoritative and must never be used as a continuation point. Safe deletion is housekeeping, not a package blocker.

## 12. Exact resume rule

Resume only on PR #44 / `wave5/desktop-hosted-campaign-administration` from `docs/checkpoints/2026-09-16_DESKTOP_HOSTED_OWNER_QA_COMPLETE.md` or a newer checkpoint. Do not redeploy Cloudflare unless Worker code changes. The current action is final exact-head CI, then ready/merge/post-merge verification.

# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified starting main:** `fb113909cb53b2463bd643cae7d2f54f0673fec4`  
**Current focused branch:** `wave5/campaign-membership-administration-core`  
**Current PR:** #43  
**Verified implementation head:** `fcaa533f79332f6a2f13fb06b7f1bb889dd1982c`  
**Verified implementation Scaffold:** `35140381721` — **SUCCESS**  
**Current checkpoint:** `docs/checkpoints/2026-09-16_CAMPAIGN_MEMBERSHIP_ADMINISTRATION_CORE_AUTOMATED_VERIFIED.md`  
**Lifecycle state:** automated verified / final documentation-head CI then merge

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal integrated-MVP trunk. Use short-lived outcome-oriented branches from verified `main`, merge only after proportionate verification, verify post-merge `main`, and refresh durable checkpoints when operational truth changes.

Current package base is the PR #42 merge commit:

`fb113909cb53b2463bd643cae7d2f54f0673fec4`

PR #42 post-merge Scaffold `35138029472` completed **SUCCESS**.

## 2. Completed Wave 5 predecessor

Branch `wave5/desktop-workbench-shell` / PR #42 is completed and merged. Accepted evidence includes persistent Desktop local campaign state, Spanish product UI, Desktop Application Settings, bounded QA diagnostics and owner Windows Desktop QA PASS.

Do not reopen that branch for the current package.

## 3. Current focused branch / PR

Branch:

`wave5/campaign-membership-administration-core`

PR:

`#43 — feat: add hosted campaign membership administration core`

Base:

`fb113909cb53b2463bd643cae7d2f54f0673fec4`

Verified implementation head:

`fcaa533f79332f6a2f13fb06b7f1bb889dd1982c`

Scaffold:

`35140381721` — **SUCCESS**.

Implemented/verified scope:

- active-DM-only hosted campaign member roster;
- Player `KICK`, `BAN`, `LIFT_BAN` lifecycle actions;
- `LIFT_BAN` = `BANNED -> KICKED`;
- idempotent/no-op moderation and campaign-revision discipline;
- no deletion of membership rows, PCs or owner/controller identity;
- provider-neutral Shared Campaign Administration client;
- focused backend/database/Shared contract tests.

No owner/manual gate is required for this package.

## 4. Current gate

The implementation head is already green. The only pre-merge gate is Scaffold on the final documentation-only PR head produced by the closure commit.

If that run is green and the PR head remains unchanged, merge PR #43 to `main` and verify post-merge `main` CI.

If PR #43 is already merged when this file is read, treat this package as completed once post-merge `main` CI is green.

## 5. Deployment boundary

The repository has no automatic Cloudflare Worker deployment workflow. The new Campaign Administration routes are repository/API-contract verified but are not yet claimed as deployed to the real DEV Worker.

Explicit DEV deployment/authenticated integration belongs to the next real Desktop-hosted consumer package when it first needs those routes.

## 6. Next Desktop settings requirement

At the next genuine Desktop feature build, apply `docs/technical/DESKTOP_APPLICATION_SETTINGS_FOLLOWUPS.md`:

- expand the Desktop font catalogue toward Android-equivalent choices where supported;
- replace plain Font/Theme selectors with Android-like preview cards/forms.

This does not reorder the dependency sequence.

## 7. Explicit current-package non-goals

The current package does not implement Desktop provider/session acquisition, Desktop hosted sync, final Desktop member UI, invitations/rejoin, co-DM workflows, PC assignment/PC Manager, Live Combat, generalized RBAC/ACL or destructive cleanup.

## 8. Protected invariants

Preserve local-first persistence, stable identity, membership/role/ownership/current-control distinctions, DM authority distinct from ownership, stale-revision/idempotency/tombstone/no-silent-overwrite semantics, the hard USD $0 policy and public-repository secret hygiene.

## 9. Non-authoritative stray ref

An accidental branch ref named `__should_not_create__` was created during tool operation and points to the same verified implementation commit `fcaa533f79332f6a2f13fb06b7f1bb889dd1982c`.

It contains no unique commits, is **not** an active branch, must never be used as authority or continuation, and may be deleted later through normal Git/GitHub branch cleanup when a branch-delete capability is available. Do not force-move or repurpose it.

## 10. Exact resume rule

Continue on `wave5/campaign-membership-administration-core` / PR #43 until the final documentation-head CI and merge/post-merge verification are complete.

After that, start the next bounded Wave 5 package from current `main`: Desktop hosted authentication/session acquisition + real Campaign Administration consumption, with explicit DEV Worker deployment/integration when the new routes are first required.
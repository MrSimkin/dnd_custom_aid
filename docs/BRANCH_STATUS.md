# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified starting main:** `fb113909cb53b2463bd643cae7d2f54f0673fec4`  
**Current focused branch:** `wave5/campaign-membership-administration-core`  
**Current PR:** not opened yet  
**Current checkpoint:** `docs/checkpoints/2026-09-16_CAMPAIGN_MEMBERSHIP_ADMINISTRATION_CORE_OPEN.md`  
**Lifecycle state:** implementation in progress / automated gate pending

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal integrated-MVP trunk. Use short-lived outcome-oriented branches from verified `main`, merge only after proportionate verification, verify post-merge `main`, and refresh durable checkpoints when operational truth changes.

Do not create permanent Player/Server/Desktop silos.

Current verified `main` is the PR #42 merge commit:

`fb113909cb53b2463bd643cae7d2f54f0673fec4`

Post-merge Scaffold `35138029472` completed **SUCCESS**.

## 2. Completed Wave 5 predecessor

Branch `wave5/desktop-workbench-shell` / PR #42 is completed and merged. It is no longer the active implementation branch.

Accepted package contents include:

- persistent Desktop SQLite/JDBC local store;
- Shared `CampaignRepository` local campaign semantics;
- workbench shell and local campaign context;
- Spanish product-language repair;
- persistent Desktop presentation settings;
- bounded QA/diagnostic copy surface;
- owner Windows Desktop manual QA PASS.

The completed checkpoint remains:

`docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_READY_FOR_MANUAL_QA.md`

## 3. Current focused branch

Branch:

`wave5/campaign-membership-administration-core`

Base:

`fb113909cb53b2463bd643cae7d2f54f0673fec4`

Purpose:

> hosted Campaign membership administration core

The package is intentionally backend/shared-first because real Desktop Campaign Administration requires authenticated hosted member/moderation contracts before user-facing Desktop controls can safely consume them.

Current bounded scope:

- DM-only campaign member roster;
- Player `KICK`, `BAN`, `LIFT_BAN` lifecycle actions;
- lifecycle-preserving/idempotent moderation;
- no deletion of membership rows, PCs, ownership/control or local cache;
- Shared client contract;
- focused backend/database/Shared verification.

`LIFT_BAN` means `BANNED -> KICKED`; later invitation/rejoin owns `KICKED -> ACTIVE`.

## 4. Explicit current-package boundary

Excluded from this package:

- Desktop provider/session acquisition;
- Desktop hosted sync;
- final Desktop member moderation UI;
- invitations/rejoin;
- co-DM role/moderation workflows;
- PC assignment shortcuts/PC Manager;
- Live Combat;
- generalized RBAC/ACL.

## 5. Desktop settings follow-up already approved by owner

At the next genuine Desktop feature build, expand the Desktop font catalogue toward the Android-equivalent choices where supported and replace plain Font/Theme selection with Android-like preview cards/forms.

This is a durable follow-up and does not change branch/package order.

## 6. Completed Wave 4 baseline

Wave 4 Player <-> Server work, including multi-client convergence and membership revoke/authorization, remains integrated and closed. Do not reopen completed branches unless later behavior actually touches their contracts.

## 7. Protected invariants

Preserve local-first persistence, stable identity, membership/role/ownership/current-control distinctions, DM authority distinct from ownership, stale-revision/idempotency/tombstone/no-silent-overwrite semantics, the hard USD $0 policy and public-repository secret hygiene.

## 8. Historical branches

Completed/frozen Wave 4 and older branches remain evidence only unless a newer checkpoint explicitly reactivates one. Do not force-move or repurpose them.

## 9. Exact resume rule

Continue on `wave5/campaign-membership-administration-core`; read the current package checkpoint first. No owner/manual action is currently required. Implement the bounded contracts, verify them, then open the package PR only after a coherent verified head exists.

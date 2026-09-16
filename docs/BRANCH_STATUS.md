# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified starting main:** `40b29006052c9986e2d79227a6053f36241de1e6`  
**Current focused branch:** `wave5/desktop-workbench-shell`  
**Current PR:** #42  
**Verified behavior/test head:** `c5e23ebbec495e3aea2b36a4cbe695c9cc586bd4`  
**Scaffold:** `35125170321` — **SUCCESS**  
**Current checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_READY_FOR_MANUAL_QA.md`  
**Lifecycle state:** automated verified / owner Desktop manual QA pending

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal integrated-MVP trunk. Use short-lived outcome-oriented branches from verified `main`, merge only after proportionate verification, verify post-merge `main`, and refresh durable checkpoints when operational truth changes.

Do not create permanent Player/Server/Desktop silos.

## 2. Current focused branch / PR

Branch:

`wave5/desktop-workbench-shell`

PR:

`#42 — feat: establish Desktop workbench shell and local campaign context`

Base:

`40b29006052c9986e2d79227a6053f36241de1e6`

Verified behavior/test head:

`c5e23ebbec495e3aea2b36a4cbe695c9cc586bd4`

Scaffold:

`35125170321` — **SUCCESS**

The package replaces the placeholder Desktop surface with a persistent workbench using existing Shared Campaign semantics. Desktop owns its own local SQLite file; hosted Desktop synchronization is not part of this package.

## 3. Verified package contents

Included and automated-verified:

- `DesktopDatabaseFactory` / explicit database-handle lifetime;
- Shared `CampaignRepository` reuse;
- persistent campaign state across Desktop close/reopen;
- top toolbar, left navigation, central work area, contextual campaign panel and bottom status strip;
- functioning Dashboard;
- functioning Campaigns list/create/active selection;
- Campaign Administration bound to the active campaign;
- stable placeholders for later approved destinations.

The first CI attempt failed only because a deprecated test-only temp-directory helper was rejected. Production Desktop compilation/build succeeded even in that attempt. The test helper was corrected, and the exact repaired head passed completely.

## 4. Current manual gate

PR #42 must remain open until the owner completes the bounded Windows Desktop audition recorded in:

`docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_READY_FOR_MANUAL_QA.md`

The gate verifies launch/usability, campaign create/select behavior, persistence after relaunch and Campaign Administration context consistency.

Manual guidance must be one exact step at a time. Do not reset/delete the Desktop database merely to make the gate pass.

## 5. Explicit package boundary

Still excluded:

- invitations/join/rejoin;
- Kick/Ban/Unban UI;
- hosted Desktop authentication/full sync;
- live combat authority/resume/handoff;
- substantive Managers;
- Media/Handouts/object storage;
- System Administration;
- backup/export/PDF hardening;
- generalized RBAC/ACL.

D-0072 and D-0073 remain controlling.

## 6. Completed Wave 4 baseline

PR #40 multi-client convergence and PR #41 membership revoke/authorization are integrated and closed. Do not reopen those branches for Wave 5 work unless later behavior actually touches their contracts.

## 7. Protected invariants

Preserve local-first persistence, stable identity, membership/role/ownership/current-control distinctions, DM authority distinct from ownership, stale-revision/idempotency/tombstone/no-silent-overwrite semantics, the hard USD $0 policy and public-repository secret hygiene.

## 8. Historical branches

Completed/frozen Wave 4 and older branches remain evidence only unless a newer checkpoint explicitly reactivates one. Do not force-move or repurpose them.

## 9. Exact resume rule

Continue on `wave5/desktop-workbench-shell`; read the manual-QA checkpoint first. The only current owner action is the guided Windows Desktop audition.

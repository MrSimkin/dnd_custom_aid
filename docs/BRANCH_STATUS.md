# Branch status and repository-ordering map

**Updated:** 2026-09-16 (Chile local time)  
**Owner implementation authorization:** GRANTED  
**Normal integrated trunk:** `main`  
**Verified starting main for current package:** `40b29006052c9986e2d79227a6053f36241de1e6`  
**Starting Scaffold:** `35123470001` — **SUCCESS**  
**Current focused branch:** `wave5/desktop-workbench-shell`  
**Current package:** Wave 5 — Desktop workbench shell + local campaign context  
**Current package checkpoint:** `docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_PACKAGE_OPEN.md`  
**Current lifecycle state:** implementation active; no owner/manual gate yet

This file is the canonical branch-lifecycle map. Branch existence alone never establishes authority.

## 1. `main` — sole normal integrated-MVP trunk

`main` remains the sole normal integrated-MVP trunk. New work uses short-lived outcome-oriented branches from verified `main`, merges only after proportionate verification, verifies post-merge `main`, and refreshes durable checkpoints when operational truth changes.

Do not create permanent Player/Server/Desktop silos or months-long catch-all integration branches.

## 2. Current focused branch

Branch:

`wave5/desktop-workbench-shell`

Exact base:

`40b29006052c9986e2d79227a6053f36241de1e6`

Base Scaffold:

`35123470001` — **SUCCESS**

Purpose:

> establish the real Desktop workbench shell and persistent local campaign context using existing Shared campaign semantics.

The inspected Desktop application is currently a placeholder window. Shared already provides `CampaignRepository` and Desktop SQLite JDBC support, so this package should add only the missing Desktop database/wiring layer and the first real workbench UI.

Expected first functioning workbench destinations are Dashboard, Campaigns and Campaign Administration. Other approved Desktop destinations may appear as stable navigation placeholders, but their substantive implementation belongs to later packages.

## 3. Current package boundary

Included:

- persistent Desktop SQLDelight database wiring;
- Shared `CampaignRepository` reuse;
- approved workbench chrome: top toolbar, left navigation, central work area, optional contextual area, bottom status strip;
- local campaign list/create/active selection;
- active-campaign context surfaced in Dashboard and Campaign Administration;
- automated Desktop/persistence verification and later bounded visual audition.

Excluded from this first slice:

- invitation/rejoin workflows;
- Kick/Ban/Unban product UI;
- hosted Desktop authentication/full sync activation;
- live combat authority/resume/handoff;
- substantive Managers, Media/Handouts, System Administration or backup/export implementation;
- generalized RBAC/ACL.

D-0072 and D-0073 remain controlling. Routine technical implementation does not require another owner decision.

## 4. Completed Wave 4 baseline

PR #40 multi-client convergence and PR #41 membership revoke/authorization are integrated and closed. PR #41 merged as `6f7165e6e5ae56a4b1985f037a656bf527b94d01`; its post-merge Scaffold `35123027446` passed.

The current branch starts from later documentation/device-inventory `main` head `40b29006052c9986e2d79227a6053f36241de1e6`, whose Scaffold `35123470001` passed all backend, hosted-database, Kotlin, Android and Desktop checks.

Do not resume completed Wave 4 branches for new work.

## 5. Protected invariants

Preserve:

- local-first persistence and stable object identity;
- membership/role/ownership/current-control distinctions;
- DM campaign authority distinct from PC ownership;
- stale-revision, mutation-idempotency, tombstone/non-resurrection and no-silent-overwrite semantics;
- non-destructive handling of local user data;
- project-specific authorization rather than generalized RBAC/ACL infrastructure;
- hard external-service budget of USD $0 unless owner changes it;
- public-repository secret hygiene.

## 6. Historical branches

`wave4/membership-revoke-authorization`, `implementation/phase4a-successor-cycle`, `integration/mvp-baseline-convergence` and other completed/frozen refs remain historical evidence only unless explicitly reactivated by a later checkpoint.

Do not force-move or repurpose historical refs.

## 7. Exact resume rule

Continue on `wave5/desktop-workbench-shell` and read `docs/checkpoints/2026-09-16_DESKTOP_WORKBENCH_SHELL_PACKAGE_OPEN.md` first. No owner interaction is required until a genuine visual/manual QA gate or material product/security/cost/destructive-behavior ambiguity appears.

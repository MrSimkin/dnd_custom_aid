# Desktop workbench shell + local campaign context — package open

**Date:** 2026-09-16 (Chile local time)  
**Wave:** 5 — Desktop shell + Campaign Administration  
**Branch:** `wave5/desktop-workbench-shell`  
**Starting verified main:** `40b29006052c9986e2d79227a6053f36241de1e6`  
**Starting Scaffold:** `35123470001` — **SUCCESS**

## Purpose

Open the first bounded Wave 5 implementation slice from the verified integrated trunk. The goal is to replace the placeholder Desktop window with the real workbench frame and make it operate on the existing Shared local Campaign model rather than inventing Desktop-only campaign state.

## Repository findings before implementation

- `desktopApp` is currently only a placeholder Compose Desktop window.
- Shared already provides `CampaignRepository` with local campaign list/create/upsert/active-selection behavior.
- Shared already includes SQLDelight SQLite JDBC support for the Desktop target.
- Android already demonstrates the intended pattern: platform database factory -> Shared `AppDatabase` -> Shared repositories.
- The missing Desktop persistence seam is therefore a small platform database-factory/wiring layer, not a new repository or campaign model.

## Approved product frame reused

D-0072 remains controlling for the Desktop workbench direction:

1. top toolbar;
2. left navigation;
3. central work area;
4. optional contextual/properties area;
5. bottom status strip.

Campaign Manager / Campaign Administration is a main workspace destination, not a small modal. Desktop completeness is the long-term direction, but this package intentionally implements only the first coherent shell/context slice.

## Scope of this package

Implement the smallest useful real Desktop workbench:

- persistent Desktop SQLDelight database creation/wiring;
- Shared `CampaignRepository` as the campaign source of truth;
- real Desktop workbench chrome and navigation;
- Dashboard, Campaigns, and Campaign Administration as functioning first destinations;
- local campaign creation and active-campaign selection through Shared semantics;
- selected/active campaign context visible to the workbench;
- placeholders for later approved Desktop destinations where useful for stable navigation;
- proportionate automated coverage for new Desktop persistence/state seams;
- manual Desktop visual/interaction audition only after automated verification is green.

Routine UI decomposition and persistence mechanics are delegated engineering choices and do not require another owner decision.

## Explicit non-goals

Do not pull these into this first Wave 5 slice:

- invitation generation/join flows;
- Kick/Ban/Unban product UI;
- hosted Desktop authentication or full Desktop sync activation;
- live combat authority, resume or handoff;
- final Managers/editor implementation;
- Media/Handouts/object-storage activation;
- System Administration implementation;
- backup/export/PDF hardening;
- generalized RBAC/ACL infrastructure.

Existing membership/ownership/controller semantics and all Wave 4 convergence/authorization invariants remain protected.

## Verification plan

Before any owner/manual gate:

1. focused Shared Desktop tests for the new database factory/persistence seam;
2. Desktop compile/package checks;
3. aggregate Scaffold on the exact branch head;
4. inspect resulting behavior for accidental scope bleed.

Only after that should the owner be asked to launch and visually audition the Desktop workbench. Manual instructions must be given one exact step at a time.

## Current owner action

None. Implementation may proceed autonomously until a genuine visual/manual QA gate or material product decision is reached.

# Desktop workbench shell + local campaign context — owner QA complete, repairs pending

**Date:** 2026-09-16 (Chile local time)  
**Wave:** 5 — Desktop shell + Campaign Administration  
**Branch:** `wave5/desktop-workbench-shell`  
**PR:** #42  
**Verified behavior/test head:** `c5e23ebbec495e3aea2b36a4cbe695c9cc586bd4`  
**Scaffold:** `35125170321` — **SUCCESS**  
**Starting integrated main:** `40b29006052c9986e2d79227a6053f36241de1e6`

## Result

The first Wave 5 Desktop package is **BEHAVIORALLY OWNER-QA PASS / PRE-MERGE REPAIRS REQUIRED**.

The previous placeholder Desktop window has been replaced with a real local-first workbench shell that uses existing Shared campaign semantics. No hosted Desktop synchronization or final Campaign Administration moderation behavior has been claimed or implemented by this package.

## Implemented

### Persistent Desktop local store

Shared provides `DesktopDatabaseFactory` and `DesktopDatabaseHandle` for the JVM/Desktop target.

- SQLite JDBC is used through the existing Shared dependency.
- Default owner-local database path is `~/.dnd_custom_aid/dnd_custom_aid.db`.
- the SQLDelight driver lifetime is explicit and closed when the Desktop application exits;
- the same Shared `AppDatabase` schema and `CampaignRepository` semantics are reused.

Desktop and Android still have separate local database files. Cross-device convergence remains a hosted-sync concern and is not implied by this local Desktop store.

### Workbench shell

The Desktop app provides the approved first workbench frame:

1. top toolbar;
2. left navigation;
3. central work area;
4. contextual Campaign panel where relevant;
5. bottom status strip.

Navigation includes approved long-term Desktop destinations while substantive implementation remains bounded to this package.

### Functioning destinations in this package

- **Dashboard** — shows local campaign count and current active campaign;
- **Campaigns** — lists campaigns, creates local campaigns through Shared `CampaignRepository`, and switches the active campaign;
- **Campaign Administration** — opens against the same selected campaign context and clearly defers membership/moderation controls to later packages.

Other workbench destinations are navigation placeholders only.

## Automated verification

Exact behavior/test head:

`c5e23ebbec495e3aea2b36a4cbe695c9cc586bd4`

Scaffold:

`35125170321` — **SUCCESS**

Verified:

- Shared Desktop test compilation/execution;
- Desktop database persistence across close/reopen;
- Desktop application compilation/build;
- Android build and permanent Player guards;
- backend type-check;
- hosted PostgreSQL contracts;
- APK artifact upload.

An earlier run `35124891611` failed only in the new test source because deprecated `createTempDir` is treated as an error. Production Desktop compilation/build had already succeeded in that run. The test helper was replaced with `kotlin.io.path.createTempDirectory`; the exact repaired head then passed completely.

## Owner Windows Desktop QA — 2026-09-16

### Behavioral gate — PASS

Owner manually verified on the recorded Windows QA workstation:

- Desktop workbench launches successfully;
- major chrome/navigation is usable at the tested Windows setup with no obvious clipping in the tested views;
- `Campaigns` opens correctly;
- campaign-name entry enables the create action as expected;
- local campaign `QA Wave 5 - 2026-09-16` was created successfully;
- the new campaign appears in the local campaign list and becomes active automatically;
- the right-side campaign context updates to the same active campaign;
- Campaign Administration reflects the same campaign context;
- test campaign ID remained `30609c9d-89f7-42ef-85dd-a7a35df3c506`;
- after normal app close/relaunch, the campaign, active selection and same UUID persisted;
- Dashboard reported one local campaign after restart;
- Player Characters, Media / Handouts, Managers, Combat, System Administration and Export / Backup each presented clearly as deferred placeholders rather than pretending functionality exists;
- the app closed normally and the Gradle run completed `BUILD SUCCESSFUL`.

The informational Wave 5 notice was considered useful because it states the current boundary instead of implying hosted Desktop sync/moderation already exist.

### Pre-merge repair requirements

1. **Desktop product-language pass** — current shell is substantially English. The owner confirmed that only the technical-assistant chat should stay English; the product UI should be Spanish where appropriate. `Dashboard` and `Backup` are explicitly acceptable product terms in Spanish and may remain unchanged.
2. **Desktop Application Settings** — add a Desktop settings destination/surface aligned closely with Android settings where applicable, including theme, fonts, density/spacing and equivalent presentation preferences. Reuse the same concepts rather than creating a disconnected Desktop settings model.
3. **QA diagnostics / copyable data** — expose a minimal QA-oriented diagnostics/log surface and make useful values copyable. Whether this remains temporary QA tooling or becomes a permanent feature is intentionally undecided. Candidate data includes build/revision, active campaign name/ID and relevant lifecycle/repository actions/errors. Avoid turning this into a generalized logging subsystem prematurely.

These repair requirements do not invalidate the behavioral PASS, but PR #42 must remain open until they are implemented, automated verification is green again, and the owner rechecks the visible repair surface.

## Scope clarification preserved

The owner asked whether Desktop PC download/sync should already be testable. This was a clarification only and **does not change the approved plan or current package sequence**. This package remains the shell/local-campaign slice; hosted Desktop synchronization and later PC Manager/Audit behavior remain follow-up work under the existing roadmap/decisions.

## Non-goals preserved

This package does not implement:

- invitation/join/rejoin flows;
- Kick/Ban/Unban controls;
- hosted Desktop authentication/full synchronization;
- live combat authority/resume/handoff;
- substantive Managers;
- Media/Handouts/object-storage activation;
- System Administration;
- backup/export/PDF hardening;
- generalized RBAC/ACL.

## Current action

Implement the three pre-merge repair requirements on the current short-lived branch, rerun automated verification, then perform a bounded owner visual recheck before merging PR #42.

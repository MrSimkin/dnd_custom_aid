# Desktop workbench shell + local campaign context — ready for manual QA

**Date:** 2026-09-16 (Chile local time)  
**Wave:** 5 — Desktop shell + Campaign Administration  
**Branch:** `wave5/desktop-workbench-shell`  
**PR:** #42  
**Verified behavior/test head:** `c5e23ebbec495e3aea2b36a4cbe695c9cc586bd4`  
**Scaffold:** `35125170321` — **SUCCESS**  
**Starting integrated main:** `40b29006052c9986e2d79227a6053f36241de1e6`

## Result so far

The first Wave 5 Desktop package is **AUTOMATED VERIFIED / OWNER DESKTOP QA PENDING**.

The previous placeholder Desktop window has been replaced with a real local-first workbench shell that uses existing Shared campaign semantics. No hosted Desktop synchronization or final Campaign Administration moderation behavior has been claimed or implemented by this package.

## Implemented

### Persistent Desktop local store

Shared now provides `DesktopDatabaseFactory` and `DesktopDatabaseHandle` for the JVM/Desktop target.

- SQLite JDBC is used through the existing Shared dependency.
- Default owner-local database path is `~/.dnd_custom_aid/dnd_custom_aid.db`.
- the SQLDelight driver lifetime is explicit and closed when the Desktop application exits;
- the same Shared `AppDatabase` schema and `CampaignRepository` semantics are reused.

Desktop and Android still have separate local database files. Cross-device convergence remains a hosted-sync concern and is not implied by this local Desktop store.

### Workbench shell

The Desktop app now provides the approved first workbench frame:

1. top toolbar;
2. left navigation;
3. central work area;
4. contextual Campaign panel where relevant;
5. bottom status strip.

Navigation includes the approved long-term Desktop destinations while substantive implementation is bounded to this package.

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

## Scope review

Comparison against starting main shows only the intended package surfaces:

- Desktop `Main.kt` workbench replacement;
- Desktop Shared database factory;
- one Desktop persistence test;
- current package/checkpoint documentation.

No Wave 4 behavior, backend authorization, hosted schema, Android product UI, combat logic, media/object storage, or final moderation feature was changed.

## Manual QA gate

Because the new shell is user-visible, PR #42 must remain open until a bounded Windows Desktop audition is completed.

The manual gate should establish:

1. the Desktop workbench launches successfully;
2. the major chrome/navigation is usable at the owner's normal desktop setup;
3. a local campaign can be created;
4. campaign selection updates the active context consistently;
5. closing and relaunching the Desktop app preserves the campaign and active selection;
6. Campaign Administration reflects the same selected campaign;
7. no obvious clipping, unusable layout, or accidental implication that deferred destinations are already implemented.

The owner should not delete the new Desktop database or reset any existing project data merely to perform this gate.

Manual instructions are to be given one exact step at a time by the technical-assistant chat.

## Manual QA observations in progress — 2026-09-16

Owner Windows Desktop audition has started successfully.

### Passed so far

- Desktop workbench launches successfully on the owner workstation.
- Main shell/chrome is visible and usable at the tested Windows setup with no obvious clipping in the initial views.
- `Campaigns` opens correctly.
- Campaign-name entry enables the create action as expected.
- Local campaign `QA Wave 5 - 2026-09-16` was created successfully.
- The newly created campaign appears in the local campaign list and becomes the active campaign automatically.
- The right-side campaign-context panel updates to the same active campaign.
- Test campaign ID: `30609c9d-89f7-42ef-85dd-a7a35df3c506`.

### Repair / follow-up requirements discovered during audition

1. **Desktop product-language pass:** the current shell is substantially English. The owner confirmed that only the technical-assistant chat should stay English; the product UI should be Spanish where appropriate. `Dashboard` and `Backup` are explicitly acceptable product terms in Spanish and may remain unchanged.
2. **Desktop Application Settings:** add a Desktop settings destination/surface with options closely aligned to the Android app where applicable, including theme, fonts, density/spacing and equivalent presentation preferences. Reuse the same concepts rather than creating a disconnected Desktop settings model.
3. **QA diagnostics / copyable data:** the next Desktop build should expose a QA-oriented log/diagnostic surface and make useful diagnostic values copyable. Whether this remains temporary QA tooling or becomes a permanent feature is intentionally undecided. Candidate data includes build/revision, active campaign name/ID, local database/location context where safe, and relevant lifecycle/repository actions/errors. Exact scope should stay minimal and useful rather than becoming a general logging subsystem prematurely.

These findings do not invalidate the current manual session; continue the bounded behavior/persistence gate, then repair the findings before merge.

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

## Current owner action

Perform the guided Desktop manual QA gate. Do not merge PR #42 before that evidence is reviewed and recorded.

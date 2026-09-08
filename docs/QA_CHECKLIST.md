# Post-build QA Checklist

This file is the reusable manual QA suite required by C-0010.

It is intentionally small and practical. Run the relevant sections after a build reaches a manual-testable state. Record pass/fail and concrete observations in the active checkpoint/current-state record.

## 1. Device-priority rule

Test the feature first on its intended primary device/form factor.

- Player character-sheet workflows: **phone first**.
- DM combat tracker/live DM board: **tablet first**.
- DM preparation/administration workflows: **desktop first**.

Secondary form-factor checks are useful when practical, but they are not a substitute for the required acceptance device.

## 2. Persistent regression core

### Application/campaign baseline

1. Application installs/updates as expected for the current development-signing path.
2. Application launches without crash.
3. User-facing UI encountered during the test is Spanish.
4. Campaign list opens.
5. A campaign can be created with a nonblank name.
6. Active campaign can be selected/switched.
7. Campaign data and active selection survive a full app close/reopen.

### Durable-data sanity

8. Existing data expected to survive the build remains present after update/migration when an in-place update path is applicable.
9. Newly saved durable data survives leaving/reopening its screen.
10. Newly saved durable data survives a full app restart.

## 3. Build `40700` status

The build-specific visual-audition guide for `0.4.0-preqa.7` / build `40700` is now **historical input**, not the current next action.

The owner sufficiently covered phone Stages A–F and recorded substantial cross-cutting defects. Do not rerun that entire audition merely because the repository is now consolidated into `main`.

Historical guide:

`docs/PREQA_OWNER_VISUAL_AUDITION.md`

Recorded results live in the Stage A–F checkpoints and `docs/checkpoints/LATEST.md`.

Build `40700` is not formally accepted and is not a frozen replacement M6 candidate.

## 4. Next successor-build QA approach

After the owner's remaining non-QA observations are recorded and one coherent repair batch is implemented, test the successor build in two layers.

### Layer A — persistent regression core

Run the install/launch/campaign/durable-data checks above plus any migration-specific checks required by the actual code changes.

### Layer B — targeted repair retest

Retest the repaired families rather than blindly repeating every old screen:

- app-wide spacing/margins/row efficiency;
- shared editor/IME behavior;
- card direct-reorder/action density and move feedback;
- phone portrait/landscape responsiveness;
- tablet/wide redesign when available;
- Conjuros fixed-control/content viewport;
- Gestión/death-save compactness;
- Consumible/Munición UX;
- provenance/origin (`Fuente`) information architecture;
- terminology/localization corrections;
- scroll/context preservation;
- typography/40% spacing experiments where included in the successor build.

Where an owner observation was explicitly promoted to an app-wide rule, sample representative surfaces rather than requiring identical reports from every card/window.

## 5. Future formal M6

When the owner explicitly freezes an exact repaired candidate, use the full acceptance matrix in `docs/TESTING.md`.

The first formal M6 test remains the mandatory in-place upgrade/data-preservation test before any clean install.

Do not reuse historical frozen candidates as active merely because their branches still exist.

## 6. Result recording

Record each manual QA pass with:

- build/commit or CI run producing the APK;
- device/form factor used;
- suite/sections executed;
- passed checks;
- failed checks/defects;
- non-blocking UX observations;
- whether the build is accepted for the tested feature.

Do not mark a feature manually accepted merely because CI is green or because its code is present on `main`.

## 7. Suite evolution

When a feature is accepted:

- keep only materially useful regression checks in the persistent core;
- add focused checks for the next feature/repair batch;
- remove obsolete build-specific checks when they no longer serve a real regression purpose;
- avoid turning this file into an exhaustive enterprise test catalog.

C-0009 proportionality remains controlling.

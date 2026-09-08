# Post-build QA Checklist

This file is the reusable manual QA suite required by C-0010.

It is intentionally small and practical. Run the relevant sections after a build reaches a manual-testable state. Record pass/fail and concrete observations in `docs/PROJECT_STATE.md` or the active work record.

## 1. Device-priority rule

Test the feature first on its intended primary device/form factor.

- Player character-sheet workflows: **phone first**.
- DM combat tracker/live DM board: **tablet first**.
- DM preparation/administration workflows: **desktop first**.

Secondary form-factor checks are useful when practical, but they are not a substitute for primary-device acceptance.

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

## 3. Current Phase 4 pre-QA manual entry point

The old V4 build-specific checklist has been retired from the active suite. Its assumptions about text-scale steps, font candidates and presentation controls were superseded by the later D-0047 closure and Pass 03–07 repair line.

Current pre-QA owner review uses:

- exact review identity `0.4.0-preqa.7` / build `40700`;
- `docs/PREQA_OWNER_VISUAL_AUDITION.md` for staged typography, text-scale, spacing, columns/rotation, keyboard and fixed/sticky-footprint review;
- the persistent regression core above for install/launch/campaign/durable-data sanity.

Before recording the visual audition as complete, also smoke-check:

1. a representative existing campaign and character open normally;
2. General, Habilidades, Combate, Gestión, Equipo, Trasfondo, Rasgos, Conjuros and Notas remain reachable as applicable;
3. representative conditional modules still appear/hide without deleting stored data;
4. save/reopen and full app restart preserve representative edits;
5. phone and tablet portrait/landscape remain operable;
6. representative editors remain usable with the software keyboard visible;
7. sticky/fixed controls in Habilidades, Gestión, Equipo, Rasgos, Conjuros and conditional collections do not obscure required content/actions;
8. ordinary Equipo remains readable at the new wide maximum of 5 columns;
9. spacing 60% does not visibly shrink intrinsic control/touch targets;
10. a large text-scale sample remains scrollable and actionable.

This is still **pre-QA visual audition**, not formal M6 acceptance.

When the owner explicitly freezes a replacement formal M6 candidate, use the full acceptance matrix in `docs/TESTING.md`, beginning with the mandatory in-place upgrade/data-preservation test before any clean install. Do not reuse the historical M5 candidate as active merely because it remains frozen evidence.

## 4. Result recording

Record each manual QA pass with:

- build/commit or CI run producing the APK;
- device/form factor used;
- suite/sections executed;
- passed checks;
- failed checks/defects;
- non-blocking UX observations;
- whether the build is accepted for the tested feature.

Do not mark a feature manually accepted merely because CI is green. Automated verification and intended-device QA are separate gates.

## 5. Suite evolution

When a feature is accepted:

- keep only materially useful regression checks in the persistent core;
- add a focused section for the next feature under development;
- remove obsolete build-specific checks when they no longer serve a real regression purpose;
- avoid turning this file into an exhaustive enterprise test catalog.

C-0009 proportionality remains controlling.

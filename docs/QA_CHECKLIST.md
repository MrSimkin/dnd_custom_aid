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

Run these when the relevant surface still exists in the build:

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

## 3. Current feature suite — Phase 4 character data foundation

**Primary acceptance device:** Android phone.

### Navigation and creation

1. Open an active campaign.
2. Open `Personajes`.
3. Create a character with a nonblank name.
4. Confirm the character appears in the campaign character list.
5. Open the character editor.

### Multiclass and hit dice

6. Add at least two class entries.
7. Use different hit-die sizes for the two classes.
8. Set different remaining hit-dice values.
9. Confirm total level/class presentation is understandable.

### Core character data

10. Edit all six ability scores.
11. Edit Armor Class.
12. Edit maximum, current and temporary HP.
13. Edit initiative modifier.
14. Edit speed.
15. Edit proficiency bonus.
16. Edit all six saving-throw modifiers.
17. Edit passive Perception.
18. Set and clear an optional spell save DC.

### Skills

19. Confirm all 18 standard D&D skills are present.
20. Edit several final skill modifiers.
21. Mark at least one skill as proficient.
22. Mark at least one skill as expertise.
23. Confirm training markers do not force/recalculate the saved final modifier.

### Permissive/homebrew data

24. Enter at least one deliberately unusual/gifted mechanical value that ordinary D&D arithmetic would not normally produce.
25. Confirm it can be saved and reopened without correction or rejection by the app.

### Save and persistence

26. Save the character.
27. Leave the editor and reopen the character.
28. Confirm classes, hit dice, abilities, combat-reference values, saves and edited skills remain correct.
29. Fully close and reopen the application.
30. Confirm campaign and character data remain present and correct.

### Phone usability review

31. Check whether the amount of scrolling is reasonable for data entry.
32. Check whether labels and field grouping are understandable.
33. Check whether class/hit-die entry feels clear.
34. Check whether skill entry is practical rather than excessively repetitive or confusing.
35. Record any density, spacing, keyboard/input or navigation problems.

## 4. Secondary checks for this character build

These are useful but are not required before the **phone-first** character-sheet acceptance decision unless a defect suggests broader testing is needed:

- tablet portrait sanity check;
- tablet landscape sanity check;
- wide-layout/density observations;
- theme behavior once theme support exists.

## 5. Result recording

Record each manual QA pass with:

- build/commit or CI run producing the APK;
- device/form factor used;
- suite/sections executed;
- passed checks;
- failed checks/defects;
- non-blocking UX observations;
- whether the build is accepted for the tested feature.

Do not mark a feature manually accepted merely because CI is green. Automated verification and intended-device QA are separate gates.

## 6. Suite evolution

When a feature is accepted:

- keep only materially useful regression checks in the persistent core;
- add a focused section for the next feature under development;
- remove obsolete build-specific checks when they no longer serve a real regression purpose;
- avoid turning this file into an exhaustive enterprise test catalog.

C-0009 proportionality remains controlling.

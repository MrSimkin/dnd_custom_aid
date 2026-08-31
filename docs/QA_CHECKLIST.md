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

## 3. Current feature suite — Phase 4 character data foundation V4

**Primary acceptance device:** Android phone.

V4 implements D-0046: deterministic sheet arithmetic is calculated from source values, while explicit signed adjustments preserve gifts/homebrew/house-rule exceptions.

### Navigation and migration

1. Install/update over the previous stable-signed V3 QA build without uninstalling.
2. Confirm existing campaign and character records remain present.
3. Open an existing V3 character and confirm its previously stored displayed initiative, saving-throw, skill and passive-Perception totals remain numerically unchanged after migration.
4. Saving-throw proficiency on migrated V3 characters is expected to begin unchecked because V3 never stored that metadata. Confirm no proficiency was silently guessed.
5. Confirm `Resumen` and `Habilidades` remain available and understandable.

### Classes and hit dice

6. Add at least two classes with different levels/dice/remaining dice.
7. Confirm `Artífice` appears in alphabetical order among the class names.
8. Confirm `Otro` remains last and exposes custom/homebrew text entry.
9. Confirm `d8`, `d10`, `d12`, etc. stay on one line rather than wrapping vertically.
10. Confirm class rows remain compact and multiple classes do not destabilize portrait or landscape grouping.

### Ability scores and automatic modifiers

11. Confirm all six ability scores fit compactly in one row on the intended phone layout.
12. Enter representative scores and verify automatic modifiers, for example:
    - 16 → `+3`;
    - 14 → `+2`;
    - 10 → `+0`;
    - 9 → `-1`;
    - 7 → `-2`.
13. Confirm the modifiers are displayed values, not separate manually editable fields.

### Initiative

14. Set Dexterity to 14. With initiative adjustment `0`, confirm Initiative is `+2`.
15. Set initiative adjustment to `+1`; confirm the displayed Initiative becomes `+3`.
16. Use a negative adjustment and confirm it is accepted and reflected correctly.

### Saving throws

17. With Strength 16 and proficiency bonus +3, confirm Strength save is `+3` when not proficient and adjustment is 0.
18. Toggle Strength-save proficiency; confirm the total becomes `+6`.
19. Add save adjustment `+2`; confirm the total becomes `+8`.
20. Confirm the saving-throw proficiency control is binary and visually distinct from the skill none/Competente/Pericia control.
21. Confirm signed save adjustments can represent arbitrary positive/negative exceptions.

### Skills

22. Confirm all 18 standard D&D skills are present and show their associated ability.
23. With Strength 16 and proficiency bonus +3, use Atletismo with adjustment 0 and verify:
    - no proficiency → `+3`;
    - Competente → `+6`;
    - Pericia → `+9`.
24. Add Atletismo adjustment `+2`; confirm Pericia total becomes `+11`.
25. Confirm the compact training control uses one fixed footprint and clearly communicates:
    - empty = no proficiency;
    - one check = Competente;
    - double check = Pericia.
26. Confirm changing training changes the calculated standard total while retaining the explicit adjustment.

### Passive Perception

27. With Wisdom 12 (+1), Percepción Competente and proficiency bonus +3, confirm Percepción total is `+4` and Percepción pasiva is `14` when passive adjustment is 0.
28. Set passive-Perception adjustment to `+2`; confirm Percepción pasiva becomes `16`.

### Explicit/manual reference values

29. Confirm AC, max/current/temp HP, speed, proficiency bonus and optional spell save DC remain editable explicit values.
30. Confirm `Referencia de combate` preserves the approved semantic order:
    1. CA / Iniciativa / Velocidad;
    2. PG actuales / PG máximos / PG temporales;
    3. Bonificador por competencia / Percepción pasiva / CD de salvación de conjuros.
31. Judge whether labels/abbreviations remain recognizable at phone width; abbreviation is allowed, obscurity is not.

### Presentation controls

32. Confirm `Por habilidades` / `Por atributo` is now a direct two-state segmented control with a clear active state.
33. Confirm both presentation modes remain understandable and the selected view persists as a device/user preference.
34. Confirm Back, Settings/gear and similar icon-only controls retain stable icon/touch geometry when text scale changes.

### Settings

35. Check all text scales: 80 / 90 / 100 / 115 / 130%; specifically confirm 115% and 130% menus/layout remain usable rather than becoming malformed.
36. Check all V4 font candidates:
    - Manrope;
    - Sora;
    - Barlow Condensed;
    - IBM Plex Sans Condensed.
37. Confirm Atkinson is no longer offered.
38. Check themes: System / Light / Dark / Light Gray / Dark Purple.
39. Confirm Light Gray is visibly distinct from Light.
40. Confirm Dark Purple reads clearly purple and is visibly distinct from ordinary Dark.
41. Confirm chosen UI preferences survive a full app restart.

### Regression checks retained from V3

42. Open the software keyboard near the bottom of the sheet and confirm lower editable content remains reachable.
43. While editing unsaved data, rotate portrait ↔ landscape and confirm the same character/tab/draft remains active.
44. Turn the screen off/on and confirm the same character/tab/draft remains active.
45. Check landscape grouping with multiple classes and both skill presentation modes.
46. Save, leave/reopen the character, then fully close/reopen the app; confirm durable values, adjustments, proficiency/training state and classes persist correctly.

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

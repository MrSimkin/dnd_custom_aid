# Project State

**Last verified:** 2026-08-31  
**Canonical branch:** `main`  
**Current working branch:** `implementation/character-data-foundation`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Phase 3 remains complete/canonical on `main`. Phase 4 character data/persistence is functionally stable. **V4 presentation + D-0046 derived-value implementation is complete and CI-green; manual owner phone QA is now the acceptance gate before PR/merge.**

## 1. Canonical baseline

Current controlling decisions:

- `docs/decisions/D-0044_PHASE4_CHARACTER_FOUNDATION_ORDER.md` — Phase 4 order;
- `docs/decisions/D-0045_CHARACTER_SHEET_PRESENTATION.md` — character-sheet presentation/UX;
- `docs/decisions/D-0046_CHARACTER_DERIVED_VALUES_AND_ADJUSTMENTS.md` — standard-derived values + explicit adjustment model.

Key constraints remain:

- Android native Kotlin + Jetpack Compose, minimum API 30;
- local SQLite/SQLDelight persistence;
- user-facing UI in Spanish;
- paper-first digital backup/reference rather than guided/legal character builder;
- mixed SRD generations, homebrew, gifts and exceptions must remain representable;
- deterministic arithmetic is calculated when inputs are known, while explicit adjustments preserve exceptions;
- persistent character-sheet state remains separate from live combat state;
- phone-first manual QA for character-sheet workflows;
- no hosted auth/sync/realtime work before a concrete workflow requires it.

## 2. Canonical accepted state

Phase 2 scaffold is canonical through PR #4 (`d50409270db52df05508f91363bf76385030a77d`).

Phase 3 campaign create/select is canonical through PR #5, merged as `dc1304080f0b71bcb44690b5ee317f3877385286`. Post-merge CI and manual phone/tablet verification passed.

No Phase 4 character PR has been opened or merged yet.

## 3. Phase 4 order

Owner-approved order:

1. **Character data foundation / Android character workflow.**
2. **DM combat tracker**, consuming the stable character model/projections rather than duplicating PC data.

The full final character-sheet/PDF/audit/sync feature set is not required before combat. A useful stable character data foundation is the prerequisite.

## 4. Durable character model — V4 / D-0046

Stored character inputs/state include:

- stable UUID + campaign association;
- name/status/freshness;
- multiclass class/level entries;
- hit-die size + remaining hit dice per class;
- six ability scores;
- AC;
- max/current/temp HP;
- speed;
- proficiency bonus;
- optional spell save DC;
- saving-throw proficiency + explicit adjustment per ability;
- skill training + explicit adjustment per standard skill;
- initiative adjustment;
- passive-Perception-specific adjustment.

Calculated values:

- ability modifier = floor((score − 10) / 2);
- Initiative = Dexterity modifier + initiative adjustment;
- saving throw = ability modifier + proficiency bonus when proficient + adjustment;
- skill = associated ability modifier + training contribution + adjustment;
- training contribution: none 0× PB, Competente 1× PB, Pericia 2× PB;
- Passive Perception = 10 + final Perception skill + passive-specific adjustment.

AC, HP, speed, proficiency bonus and spell save DC remain explicit/manual in this slice.

This is calculation assistance, not legality/rules enforcement.

## 5. V3 → V4 migration

Migration `shared/src/commonMain/sqldelight/io/github/mrsimkin/dndcustomaid/shared/db/2.sqm` converts existing V3 final totals to D-0046 adjustments while preserving displayed values wherever the old data permits:

- initiative total preserved exactly;
- skill totals preserved exactly using stored V3 training state;
- Passive Perception preserved exactly;
- saving-throw totals preserved exactly.

V3 never stored saving-throw proficiency metadata. Migration therefore initializes save proficiency to false and encodes the previous displayed total in the adjustment. It deliberately does **not** infer proficiency from class or arithmetic.

Legacy V3 final-total SQLite columns remain only as migration scaffolding where needed; the shared domain contract treats inputs/proficiency/training/adjustments as authoritative rather than maintaining competing final totals.

## 6. V4 Android editor

Current Android route uses `CharacterEditorScreenV4`.

### Navigation and tabs

- `Resumen` and `Habilidades` remain the approved current tabs;
- root/campaign/character selection, selected tab and unsaved draft remain saveable across ordinary Android recreation;
- V3 keyboard/IME accessibility behavior is retained.

### Classes / hit dice

- `Artífice` is alphabetized with the SRD class names;
- `Otro` remains last and opens custom/homebrew entry;
- class/row padding is reduced;
- common hit-die choices remain d4/d6/d8/d10/d12 + custom;
- the die selector is widened and `d8`, `d10`, etc. are forced to remain on one line.

### Ability / derived mechanics

- all six ability scores remain in one compact row;
- automatic ability modifiers are displayed beside/below scores;
- Initiative, skills, saving throws and Passive Perception use D-0046 calculations;
- compact signed adjustment fields provide the exception/homebrew escape path;
- saving throws use their own binary proficiency control;
- skill training retains one fixed-footprint control with empty / one-check / double-check vector states for none / Competente / Pericia.

### Skill organization

- `Por habilidades` remains default;
- `Por atributo` remains alternate;
- preference remains device/user presentation state, not character data;
- V4 replaces the dropdown with a direct two-state segmented selector with visible active state.

### Combat reference

Approved semantic order is implemented:

1. CA / Iniciativa / Velocidad;
2. PG actuales / PG máximos / PG temporales;
3. Bonificador por competencia / Percepción pasiva / CD de salvación de conjuros.

Portrait and landscape may use different geometry, but subgroup identity/order remain stable. Labels may abbreviate for density but should remain recognizable.

### Icon actions

Back and Settings/gear use stable Canvas/vector icon buttons rather than typography-scaled glyph text.

## 7. V4 Settings

Text-size choices remain:

- 80%;
- 90%;
- 100% default;
- 115%;
- 130%.

V4 changes the settings selectors to a more vertically responsive layout for larger text scales.

Font QA set:

- Manrope — sans;
- Sora — sans;
- Barlow Condensed — condensed;
- IBM Plex Sans Condensed — condensed.

Atkinson Hyperlegible Next is removed. There is no serif option.

Theme identities remain:

- Sistema;
- Claro;
- Oscuro;
- Gris claro;
- Morado oscuro.

V4 makes Gris claro visibly grayer and Morado oscuro more clearly purple/distinct from ordinary Dark. Final palette acceptance remains manual QA.

## 8. Automated verification

Verified V4 code head:

`3c21cf649b31687180b73a8d314ca56eb937d147` — `Remove obsolete V3 character editor`.

GitHub Actions **run #107 / `33358486525`** passed on that head:

- backend dependency install/type check: **success**;
- shared Kotlin tests including D-0046 arithmetic/migration tests: **success**;
- Android debug build: **success**;
- Desktop build: **success**;
- stable-signed Android debug APK upload: **success**.

The code run also verifies:

- floor-correct negative ability modifiers;
- derived skill/save/initiative/passive calculations with arbitrary adjustments;
- persistence/reopen of saving-throw proficiency and adjustments;
- Phase 3 → current migration;
- explicit V3 → V4 migration preserving legacy displayed totals and not inventing save proficiency.

Documentation-only commits made after this code run do not change the APK under test.

## 9. QA history

### V1 phone QA

**FUNCTIONAL QA PASS; UX NOT ACCEPTED.**

### V2 phone QA — `113fe27c42e15ff0950d53e854796f26de6671b4`

**NEEDS CHANGES; persistence pass.**

### V3 phone QA — `f728acd7ec10f4fae2df093ec8b16db4c8d2ba90`

**FUNCTIONAL PASS; PRESENTATION NEEDS A SMALL FOLLOW-UP.**

V3 established passing behavior for:

- in-place update/data preservation;
- two-tab organization;
- keyboard/IME access;
- rotation + screen-off/on state preservation;
- landscape;
- persistence;
- both skill presentation concepts.

V4 must preserve those successes while validating the new presentation and derived-value model.

## 10. Current manual acceptance gate

**Do not open or merge a PR yet.**

Immediate next action: install the stable-signed V4 APK from run #107 over the V3 build and execute the V4 phone-first suite in `docs/QA_CHECKLIST.md`, emphasizing:

- V3 data migration preserving displayed totals;
- expected unchecked save proficiency after migration;
- automatic ability modifiers;
- derived initiative/save/skill/passive arithmetic + explicit adjustments;
- compact save and skill proficiency controls;
- class/hit-die polish;
- segmented skill-view selector;
- four V4 fonts and five text scales;
- revised Light Gray / Dark Purple palettes;
- stable vector Back/Settings controls;
- approved combat-reference order/label clarity;
- keyboard, recreation/state, landscape and persistence regressions.

Record all owner observations in Git. Only after intended-device V4 QA is accepted should the character branch proceed to PR/review/merge.

## 11. Explicitly deferred

- spell lists/slots;
- inventory/equipment/currencies;
- attacks/actions;
- features/traits;
- broader proficiencies/languages;
- biography/personality;
- PDF export;
- grouped audit-history implementation;
- ownership/control/accounts UI;
- hosted sync/auth;
- broad character legality/checking;
- DM combat tracker implementation itself.

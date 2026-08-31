# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `implementation/character-data-foundation`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Phase 3 remains complete/canonical on `main`. Phase 4 character data/persistence is functionally accepted on Android phone. V3 manual QA passed the functional/regression gate. **The V4 presentation + derived-sheet-value scope is now owner-approved and has no remaining product-decision blocker before implementation.**

## 1. Canonical baseline

The approved product/architecture baseline is D-0034 through D-0043 plus the 2026-08-30 C-0009 proportionality clarifications. Phase 4 ordering and the first character-slice boundary are recorded in `docs/decisions/D-0044_PHASE4_CHARACTER_FOUNDATION_ORDER.md`.

Current controlling character decisions:

- `docs/decisions/D-0045_CHARACTER_SHEET_PRESENTATION.md` — presentation/UX;
- `docs/decisions/D-0046_CHARACTER_DERIVED_VALUES_AND_ADJUSTMENTS.md` — approved standard-derived values + explicit adjustment model.

Key constraints remain:

- Android native Kotlin + Jetpack Compose, minimum API 30;
- SQLite/SQLDelight local persistence where useful;
- no speculative auth/hosted sync/realtime infrastructure before a selected workflow needs it;
- character data may represent mixed SRD generations, homebrew and owner-granted exceptions;
- the application is not a guided/legal character builder or automatic rules enforcer;
- ordinary deterministic sheet arithmetic should be calculated when its source data is already present, with explicit adjustments preserving exceptions;
- persistent character-sheet state remains separate from live combat working state;
- manual feature acceptance uses the intended primary device first and the repeatable suite in `docs/QA_CHECKLIST.md`.

## 2. Canonical accepted state

Phase 2 scaffold is canonical through PR #4 (`d50409270db52df05508f91363bf76385030a77d`).

Phase 3 campaign create/select is canonical through PR #5, merged as `dc1304080f0b71bcb44690b5ee317f3877385286`. Post-merge Phase 3 CI passed and manual verification passed on both phone and tablet.

No Phase 4 character PR has been opened or merged yet.

## 3. Phase 4 approved order

Owner-approved order:

1. **Character data foundation / Android character workflow.**
2. **DM combat tracker**, consuming stable character data/projections instead of inventing a duplicate PC model.

The entire final character-sheet/PDF/audit/sync feature set is not a prerequisite for combat. The prerequisite is a useful stable character data foundation.

## 4. Character foundation — approved durable direction

The character slice includes:

- stable UUID and explicit campaign association;
- name and lifecycle status;
- last-saved/updated freshness data;
- multiclass-aware class/level entries;
- hit-die size and remaining hit dice per class entry;
- STR/DEX/CON/INT/WIS/CHA scores;
- Armor Class;
- maximum/current/temporary HP;
- speed;
- proficiency bonus;
- optional spell save DC;
- standard skills and saving throws;
- passive Perception;
- initiative.

### D-0046 derived-value model

The owner approved **calculated standard value + optional explicit adjustment**.

- ability modifier = derived automatically from score using floor semantics;
- skill total = associated ability modifier + training contribution + explicit adjustment;
- saving throw total = ability modifier + proficiency bonus when proficient + explicit adjustment;
- saving-throw proficiency is binary and distinct from the skill three-state training model;
- Passive Perception = `10 + final Perception total + passive-specific adjustment`;
- Initiative = Dexterity modifier + explicit adjustment.

Skill training contribution:

- none = 0 × proficiency bonus;
- Competente = 1 × proficiency bonus;
- Pericia = 2 × proficiency bonus.

Proficiency bonus, AC, HP, speed and spell save DC remain explicit/manual in this slice.

This is calculation assistance, **not rules enforcement**. Adjustments preserve arbitrary gift/homebrew/house-rule totals.

### V3 migration rule

Migration should preserve old displayed totals wherever possible:

- skill adjustment = old skill final total − newly calculated standard;
- initiative adjustment = old initiative total − Dexterity modifier;
- passive adjustment = old passive total − (`10 + migrated final Perception total`).

V3 did not store saving-throw proficiency. Therefore V4 must not guess it. Existing saves migrate with proficiency `false` plus an adjustment that preserves the old displayed total. Players can mark the appropriate save proficiencies after migration.

## 5. Durable character implementation currently on the working branch

The branch currently contains the V3 model/UI, including:

- `CharacterSheet`, `CharacterClassLevel`, `CharacterSkill`, lifecycle/status and skill enums in shared Kotlin;
- SQLDelight `character`, `character_class` and `character_skill` tables;
- migration `1.sqm` from Phase 3 campaign-only storage;
- `CharacterRepository` create/list/read/save behavior;
- persistence tests for 18 skills, campaign isolation, multiclass hit dice, reopen, permissive values and migration;
- Android campaign → character list → character editor workflow;
- stable CI-only debug signing;
- `docs/QA_CHECKLIST.md`;
- `docs/CHARACTER_SHEET_UX.md`;
- D-0045 and D-0046;
- both owner paper-sheet PDFs under `assets/character-sheets/templates/`.

The stable signing material is public/debug-only test material and must never be reused as a production/release signing key.

## 6. QA history

### First phone QA

**FUNCTIONAL QA PASS; UX NOT ACCEPTED.**

### Second phone QA — build `113fe27c42e15ff0950d53e854796f26de6671b4`

**NEEDS CHANGES; persistence pass.**

### Third phone QA — V3 / `f728acd7ec10f4fae2df093ec8b16db4c8d2ba90`

**FUNCTIONAL PASS; PRESENTATION NEEDS A SMALL FOLLOW-UP.**

V3 passed:

- in-place update/data preservation;
- tabs and general density;
- six abilities in one row;
- class/custom-class workflow;
- keyboard/IME access;
- rotation and screen-off/on state preservation;
- landscape;
- persistence;
- both `Por habilidades` and `Por atributo` presentation concepts.

## 7. Presentation decisions after V3 QA

### Tabs

Accepted:

1. `Resumen`;
2. `Habilidades`.

Future tabs are added only when their feature domains exist.

### Skills/attribute view

- `Por habilidades` default;
- `Por atributo` alternate;
- user/device presentation preference;
- V4 replaces the dropdown with a compact two-state segmented/slider-like control with a clear active state.

### Class selector

- exact Spanish SRD 5.2.1 classes;
- `Artífice` alphabetized with real classes;
- `Otro` last as custom/homebrew escape.

### Global Settings

Font scales remain **80 / 90 / 100 / 115 / 130%**, default **100%**. V4 must make >100% menu/layout behavior visually sound.

V4 font QA set:

- **Manrope** — sans;
- **Sora** — sans;
- **Barlow Condensed** — condensed;
- **IBM Plex Sans Condensed** — condensed;
- Atkinson removed;
- no serif option.

Themes remain System, Light, Dark, Light Gray and Dark Purple. Light Gray must become visibly gray; Dark Purple must read clearly purple and remain distinct from Dark.

### Skill training control

Keep the compact interaction, but use a **single fixed-footprint** state indicator:

- empty = none;
- single check = Competente;
- double check = Pericia.

Do not allocate two boxes/twice the width for Pericia. Vector icons are acceptable/preferred if clearer.

### Icon controls

Back, Settings/gear and similar icon actions must use proper stable icon buttons/vector icons rather than typography-scaled text glyphs.

### Combat reference

Approved semantic order:

1. `CA` / `Iniciativa` / `Velocidad`;
2. `PG actuales` / `PG máximos` / `PG temporales`;
3. `Bonificador por competencia` / `Percepción pasiva` / `CD de salvación de conjuros`.

Abbreviation is allowed when density requires it, but abbreviations must remain recognizable. `CA` and `PG` are good conventional examples; opaque labels such as V3 `Comp.` / `Perc. pas.` should be avoided.

## 8. V3 automated verification

GitHub Actions run **#84 / `33352541814`** passed on `f728acd7ec10f4fae2df093ec8b16db4c8d2ba90`:

- backend checks: success;
- shared Kotlin tests/build: success;
- Android debug assembly/APK upload: success;
- Desktop build: success.

## 9. Explicitly deferred from this first character slice

- spell lists and spell slots;
- inventory/equipment/currencies;
- attacks/actions;
- features/traits;
- broader proficiencies/languages;
- biography/personality;
- PDF export;
- grouped audit-history implementation;
- ownership/control/accounts UI;
- hosted synchronization/auth;
- broad automatic legality/rules enforcement or character checking;
- combat tracker implementation itself.

## 10. Current acceptance gate / immediate next action

**Do not open or merge a PR yet.**

There is **no remaining owner product-decision blocker before V4 implementation**.

V4 should:

- migrate the V3 durable model to D-0046 inputs/proficiency/adjustments while preserving displayed totals where possible;
- show automatic ability modifiers;
- add distinct binary saving-throw proficiency controls;
- calculate skills, saves, Passive Perception and Initiative under D-0046;
- keep explicit adjustment escape paths compact;
- slightly reduce remaining class/box padding;
- prevent `d8`, `d10`, etc. wrapping;
- alphabetize `Artífice` and keep `Otro` last;
- make 115%/130% layouts and menus responsive;
- test the four approved V4 font candidates;
- correct Light Gray and Dark Purple palettes;
- implement the segmented skills/attribute view selector;
- implement fixed-footprint empty/check/double-check skill training indicators;
- replace text-glyph Back/Settings with stable icon buttons;
- apply approved combat-reference order and responsive recognizable labels;
- preserve V3 keyboard, lifecycle/navigation, landscape and persistence successes.

After V4 is CI-green, produce another stable-signed phone APK and run focused owner QA. Once accepted, proceed to character-foundation PR/review/merge. The following major product slice remains the tablet-primary DM combat tracker.

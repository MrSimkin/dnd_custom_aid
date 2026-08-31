# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `implementation/character-data-foundation`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Phase 3 remains complete/canonical on `main`. Phase 4 character data/persistence is functionally accepted on Android phone. V3 manual phone QA passed the functional/regression gate and validated the current tabs, keyboard, lifecycle-state preservation, landscape behavior and persistence. **A smaller V4 presentation-polish pass is still required before PR/merge.**

## 1. Canonical baseline

The approved product/architecture baseline is D-0034 through D-0043 plus the 2026-08-30 C-0009 proportionality clarifications. Phase 4 ordering and the first character-slice boundary are recorded in `docs/decisions/D-0044_PHASE4_CHARACTER_FOUNDATION_ORDER.md`. Current approved character-sheet presentation preferences are recorded in `docs/decisions/D-0045_CHARACTER_SHEET_PRESENTATION.md`.

Key constraints remain:

- Android native Kotlin + Jetpack Compose, minimum API 30;
- SQLite/SQLDelight local persistence where useful;
- no speculative auth/hosted sync/realtime infrastructure before a selected workflow needs it;
- character data may represent mixed SRD generations, homebrew and owner-granted exceptions;
- the application is not a guided/legal character builder or automatic rules enforcer;
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

## 4. Character foundation — approved first-slice data

The first character slice includes:

- stable UUID and explicit campaign association;
- name and lifecycle status;
- last-saved/updated freshness data;
- multiclass-aware class/level entries;
- hit-die size and remaining hit dice per class entry;
- STR/DEX/CON/INT/WIS/CHA scores;
- Armor Class;
- maximum/current/temporary HP;
- initiative modifier;
- speed;
- proficiency bonus;
- six final saving-throw modifiers;
- passive Perception;
- optional spell save DC;
- all 18 standard D&D skills, each with final modifier plus descriptive proficiency/expertise state.

Stored final values are authoritative sheet data. They are not recalculated or rejected because ordinary D&D arithmetic would produce another value. This deliberately supports gifts, homebrew and house rules.

A future character-check/validation feature is deferred until late development after the relevant character/rules exceptions have been clarified substantially.

## 5. Durable character implementation on the working branch

`implementation/character-data-foundation` includes:

- `CharacterSheet`, `CharacterClassLevel`, `CharacterSkill`, lifecycle/status and skill enums in shared Kotlin;
- SQLDelight tables for `character`, `character_class` and `character_skill`;
- migration `1.sqm` from the Phase 3 campaign-only database to the character schema;
- `CharacterRepository` create/list/read/save behavior;
- persistence tests covering all 18 skills, campaign isolation, multiclass + independent hit dice, database reopen, permissive gifted values and Phase 3 migration;
- Android campaign → character list → character editor workflow;
- stable CI-only debug signing so future QA APKs can update one another in place;
- C-0010 intended-device/manual-QA convention and reusable `docs/QA_CHECKLIST.md`;
- `docs/CHARACTER_SHEET_UX.md` as the detailed UX/QA/design-reference working record;
- `docs/decisions/D-0045_CHARACTER_SHEET_PRESENTATION.md` as the owner-approved presentation decision record;
- both owner paper-sheet PDF references under `assets/character-sheets/templates/`.

The stable signing material is deliberately public/debug-only test material. It must never be reused as a production/release signing key.

## 6. QA history before V3

### First phone QA

**Result:** FUNCTIONAL QA PASS; UX NOT ACCEPTED.

The first editor validated the character model, persistence and workflow, but was too form-like, spacious and scroll-heavy. It also exposed numeric-input, keyboard obstruction, class/hit-die, proficiency touch-target, landscape-use, typography/theme and information-hierarchy needs.

### Second phone QA — build `113fe27c42e15ff0950d53e854796f26de6671b4`

**Result:** NEEDS CHANGES.  
**Persistence regression:** pass.

This pass drove V3 requirements: six abilities in one row, compact classes/hit dice, exact class selector, skill→ability labels, two skills/attribute views, global Settings, keyboard/IME correction, lifecycle/navigation restoration and stable landscape grouping.

## 7. D-0045 presentation decisions after V3 QA

### Tabs

Accepted:

1. **Resumen** — fast-reference overview.
2. **Habilidades** — abilities/saves/skills relationship area.

The owner confirmed both labels and the tabbed structure are understandable and preferable to the earlier continuous form.

### Skill presentation

- **Por habilidades / By skills** is the default;
- **Por atributo / By attribute** is the alternate;
- both structures passed V3 QA;
- the preference remains user/device presentation state rather than character mechanics data;
- the current dropdown works, but the next iteration should use a compact two-state segmented/slider-like selector with a visible active indicator.

### Class selector

- exact Spanish class list from official SRD 5.2.1;
- plus `Artífice`;
- plus final `Otro` escape path exposing an open custom/homebrew field;
- V3 confirmed the choices work;
- `Artífice` should be alphabetized with the real class names rather than appended at the end;
- `Otro` remains last.

### Global Settings

Font scale options remain **80 / 90 / 100 / 115 / 130%** with **100% default**.

V3 QA found menu/layout rendering becomes visually wrong above 100%; treat this as a responsive-layout defect before changing the approved scale.

Font candidates after V3 QA:

- **Manrope** — retained;
- **Barlow Condensed** — retained;
- **Atkinson Hyperlegible Next** — not accepted in its current result; owner requested another condensed replacement/alternative. Exact replacement remains unresolved.

Theme identities remain:

- System;
- Light;
- Dark;
- Light Gray;
- Dark Purple.

Current palettes need revision:

- Light Gray is almost indistinguishable from Light;
- Dark Purple is too close to Dark and reads too wine/burgundy rather than clearly purple.

### Skill training control

The V3 compact interaction type is liked and should be retained, but its abbreviated letters/state indication are not clear enough. Improve the visible state without returning to the older oversized control.

Changing `Competente` / `Pericia` continues to leave the manually stored final numeric modifier unchanged unless a later explicit decision says otherwise.

### Combat reference

`Referencia de combate` is accepted as a useful group, but its **internal order is not yet accepted**. The two custom paper sheets should guide a more coherent subgroup order rather than an arbitrary wide-row sequence. Detailed comparison and a proposed order are in `docs/CHARACTER_SHEET_UX.md`.

## 8. Third character-editor implementation (V3)

V3 routes Android through `CharacterEditorScreenV3` and includes:

- saveable root/campaign/character navigation state;
- saveable selected editor tab and unsaved draft state;
- `adjustResize` + IME/navigation padding for lower-content accessibility;
- `Resumen` and `Habilidades` tabs;
- compact six-ability row;
- compact class rows and quantity-before-die hit-dice entry;
- exact SRD 5.2.1 Spanish class selector + Artífice + Otro/custom;
- Por habilidades and Por atributo presentation modes;
- visible skill→ability association;
- persistent device presentation settings;
- initial global font-size/font/theme Settings.

## 9. V3 automated verification

Verified code head:

`f728acd7ec10f4fae2df093ec8b16db4c8d2ba90` — `Wire downloadable fonts through GMS provider`.

GitHub Actions run **#84 / `33352541814`** passed:

- backend dependency install/type check: **success**;
- shared Kotlin tests/build: **success**;
- Android debug assembly: **success**;
- Desktop build: **success**;
- Android debug APK artifact upload: **success**.

## 10. V3 intended-device manual QA

**Primary device:** Android phone.  
**Overall:** **FUNCTIONAL PASS; PRESENTATION NEEDS CHANGES.**

### Passed / accepted

- in-place update and existing data preservation;
- current two-tab organization;
- overall density and grouping substantially improved;
- all six ability scores fit compactly in one row;
- combat-reference fields form a useful group;
- one class entry is compact enough in principle;
- quantity-before-die hit-dice logic is correct;
- class selector choices are correct;
- `Otro` custom entry works;
- multiple class rows cause no layout issue;
- Por habilidades structure passes;
- Por atributo structure passes;
- compact skill-training control type is liked;
- rotation/start-page regression is fixed;
- screen-off/on navigation regression is fixed;
- keyboard no longer covers lower content;
- landscape behavior passes;
- persistence passes.

### Remaining V4 changes

- slightly reduce remaining internal class/box padding;
- fix the hit-die `dX` selector so `d8`, `d10`, etc. never stack vertically (`d` above the number);
- alphabetize `Artífice` with the real class names;
- make font scales above 100% render menus/layout correctly;
- replace/retest Atkinson with another condensed font candidate;
- make `Gris claro` visibly more distinct from `Claro`;
- make `Morado oscuro` clearly purple and distinct from ordinary Dark;
- replace the Por habilidades / Por atributo dropdown with a direct segmented/slider-like two-state selector;
- retain the compact proficiency/training control but make its letters/state clearer;
- revise `Referencia de combate` internal ordering/grouping using the custom paper sheets;
- clarify the owner note `no modifiers on sheet, some over abreviations` before converting it into a code change.

## 11. Paper character-sheet design references

Stored durably in the repository:

- `assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf`;
- `assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf`.

The detailed paper-derived grouping analysis is maintained in `docs/CHARACTER_SHEET_UX.md`.

For `Referencia de combate`, the paper sheets support preserving semantic subgroups rather than flattening values into a single arbitrary row. Current proposed next-pass digital grouping is:

1. core reference: CA / initiative / speed;
2. HP cluster: current / maximum / temporary HP;
3. secondary reference: proficiency / passive Perception / spell save DC.

This proposal is **not yet owner-approved as final order**.

## 12. Explicitly deferred from this first character slice

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
- automatic legality/rules enforcement or character checking;
- combat tracker implementation itself.

## 13. Current acceptance gate / immediate next action

**Do not open or merge a PR yet.**

The character data/persistence foundation is functionally stable enough that the next work should be a **small V4 presentation-polish pass**, not another structural rewrite.

V4 should address only the remaining items in section 10 while preserving the now-passing keyboard, lifecycle/navigation, landscape and persistence behavior. After V4 is CI-green, produce another stable-signed phone APK and run focused owner QA.

Once that presentation pass is accepted, proceed to character-foundation PR/review/merge. The following major product slice remains the tablet-primary DM combat tracker.

# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `implementation/character-data-foundation`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Phase 3 remains complete/canonical on `main`. Phase 4 character data/persistence is functionally accepted on Android phone. The third character-sheet UX iteration is now implemented and CI-green; **manual owner phone QA is the current acceptance gate before any PR/merge**.

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

Accepted improvements:

- editor visibly more compact and organized;
- no presentation regression versus first editor;
- numeric-field behavior acceptable;
- hit-die selector improved;
- skill training selector functional/understandable;
- landscape generally liked;
- readability good;
- persistence remained correct.

Remaining findings that drove V3:

- all six ability scores should fit in one row;
- class entries should be much denser and use natural `3d10` ordering;
- class selector must use exact Spanish SRD 5.2.1 classes plus `Artífice` and `Otro` custom;
- skill rows/training controls need more density;
- each skill should show its associated ability;
- support **By skills** and **By attribute**, with By skills default and the preference stored per user/device rather than per character;
- keyboard still hid lower content;
- dynamic classes disrupted landscape visual grouping;
- rotation and screen off/on returned to the campaign/start page;
- tabbed organization was wanted;
- global Settings should include typography scale, font family/style and themes.

The saving-throw “pin” is resolved: the supplied paper PDFs were the intended reference and there is no additional hidden requirement.

## 7. D-0045 decisions now resolved for V3

Approved initial tabs:

1. **Resumen** — fast-reference overview.
2. **Habilidades** — abilities/saves/skills relationship area.

Future tabs are added only when their feature domains are actually implemented; no empty speculative tabs.

Approved skill presentation:

- **By skills** is the default;
- **By attribute** is the alternate;
- this is a user/device presentation preference, not character data;
- the sheet-local gear is the intended control.

Approved class selector:

- exact Spanish class list from official SRD 5.2.1;
- plus `Artífice`;
- plus `Otro`, which exposes an open custom/homebrew field.

Approved first global Settings:

- font scale: **80 / 90 / 100 / 115 / 130%**;
- default scale: **100%**;
- fonts: **Manrope**, **Atkinson Hyperlegible Next**, **Barlow Condensed**;
- no serif option;
- themes: **System, Light, Dark, Light Gray, Dark Purple**;
- Dark Purple is an explicit owner-preferred theme option.

The three fonts are intentionally an initial reversible set: owner QA may replace them later without changing the settings model.

Still intentionally unresolved:

- whether changing skill proficiency/training should recalculate the stored final numeric skill modifier. V3 therefore leaves the final modifier independent.
- final visual styling beyond the already approved hierarchy/density direction.

## 8. Third character-editor implementation (V3)

V3 is implemented on the working branch and routes the Android app through `CharacterEditorScreenV3`.

### Navigation/lifecycle

- root screen, selected campaign and selected character IDs use saveable state;
- editor tab and unsaved editor draft use saveable state;
- ordinary Activity/configuration recreation is intended to preserve the current workflow instead of returning to the campaign page;
- `adjustResize`, IME padding, navigation-bar padding and extra lower scroll space are combined to address the previous software-keyboard obstruction.

### `Resumen`

- compact persistent editor header with back, character identity, save and global settings access;
- identity/status group;
- compact multiclass rows;
- all six ability scores in one row;
- dense combat/reference grouping.

### Classes / hit dice

- one compact row per class where practical;
- exact Spanish SRD 5.2.1 selector plus `Artífice` and `Otro`;
- `Otro` exposes an open custom class field;
- level is compact;
- hit-dice quantity precedes die type, matching tabletop notation such as `3d10`;
- common hit-die selector: d4/d6/d8/d10/d12 plus custom escape.

### `Habilidades`

- default **Por habilidades** view;
- six abilities shown compactly;
- saving throws shown as their own compact group;
- every skill visibly shows the associated ability abbreviation;
- skill modifier and training controls are denser than V2;
- wide/landscape skill list uses multiple columns;
- **Por atributo** alternate view groups each ability with its save and related skills;
- switching view does not change character mechanics/data;
- selected view persists as a device/user presentation preference.

### Global Settings

Persistent device preferences use a small local preferences store and currently expose:

- 80 / 90 / 100 / 115 / 130% text scale;
- Manrope / Atkinson Hyperlegible Next / Barlow Condensed;
- System / Light / Dark / Light Gray / Dark Purple themes.

Font retrieval uses Android/Google Fonts downloadable-font support with the standard Google Play Services font-provider certificates. If the chosen families are visually disliked in phone QA, replacing them remains a reversible presentation change.

## 9. V3 automated verification

Latest verified code head:

`f728acd7ec10f4fae2df093ec8b16db4c8d2ba90` — `Wire downloadable fonts through GMS provider`.

GitHub Actions run **#84 / `33352541814`** passed on that head:

- backend dependency install/type check: **success**;
- shared Kotlin tests/build: **success**;
- Android debug assembly: **success**;
- Desktop build: **success**;
- Android debug APK artifact upload: **success**.

Two preceding V3 runs failed only while adapting the selected downloadable fonts to the Compose dependency's explicit Google Fonts provider API. The final fix added the standard provider certificate resource and passed CI. These intermediate failures are implementation history, not an open blocker.

**Automated verification does not constitute owner UX acceptance.**

## 10. Paper character-sheet design references

Stored durably in the repository:

- `assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf`;
- `assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf`.

Detailed visual review is recorded in `docs/CHARACTER_SHEET_UX.md`.

The owner confirmed that the relevant main-page layouts are alternatives for the same character-sheet page. The paper designs also demonstrate both ability/skill grouping approaches now implemented digitally. They are hierarchy/grouping references, not instructions to reproduce a paper page literally.

## 11. Explicitly deferred from this first character slice

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

The paper PDFs contain examples of several deferred areas, but their presence in a reference sheet does not move those features into the current implementation slice.

## 12. Current acceptance gate / immediate next action

**Do not open or merge a PR yet.**

Immediate next action: install the stable-signed V3 APK from run #84 on the owner's Android phone and perform targeted manual QA covering:

- in-place update and prior local character data preservation;
- initial Settings behavior, persistence, all five text scales, all three font families and all five themes;
- `Resumen` density, six-ability row and compact class/hit-dice entry;
- exact class choices including Artífice and `Otro` custom entry;
- `Habilidades` default By-skills presentation and visible skill→ability association;
- By-attribute alternate presentation and device-wide preference persistence;
- skill-training independence from manually entered final modifiers;
- software-keyboard access to lower skills/content;
- portrait↔landscape and screen-off/on state preservation, including unsaved draft state;
- landscape grouping after adding multiple classes;
- save/reopen/full-restart persistence regression;
- overall owner UX acceptance.

Record all owner observations in Git. Only after manual QA is accepted should the character branch proceed to PR/review/merge. The following major product slice remains the tablet-primary DM combat tracker once this character-data foundation reaches an accepted stable state.

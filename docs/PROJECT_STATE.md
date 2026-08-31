# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `implementation/character-data-foundation`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Phase 3 remains complete/canonical on `main`. Phase 4 character data/persistence is functionally accepted on Android phone. V3 manual QA passed the functional/regression gate. **V4 is now a focused presentation + derived-sheet-value pass, but one consequential storage/calculation decision must be owner-approved before coding the derived-value changes.**

## 1. Canonical baseline

The approved product/architecture baseline is D-0034 through D-0043 plus the 2026-08-30 C-0009 proportionality clarifications. Phase 4 ordering and the first character-slice boundary are recorded in `docs/decisions/D-0044_PHASE4_CHARACTER_FOUNDATION_ORDER.md`. Current approved character-sheet presentation preferences are recorded in `docs/decisions/D-0045_CHARACTER_SHEET_PRESENTATION.md`.

Key constraints remain:

- Android native Kotlin + Jetpack Compose, minimum API 30;
- SQLite/SQLDelight local persistence where useful;
- no speculative auth/hosted sync/realtime infrastructure before a selected workflow needs it;
- character data may represent mixed SRD generations, homebrew and owner-granted exceptions;
- the application is not a guided/legal character builder or automatic rules enforcer;
- ordinary deterministic sheet arithmetic may still be automated when its source data is present;
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

## 4. Character foundation — current first-slice data

The implemented first character slice currently includes:

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
- six saving-throw modifiers;
- passive Perception;
- optional spell save DC;
- all 18 standard D&D skills with modifier plus descriptive proficiency/expertise state.

The earlier slice deliberately stored final modifiers so gifted/homebrew values were never rejected. **After the V3 QA clarification and SRD character-creation review, that storage rule is now under targeted reconsideration for deterministic derived values.** Do not silently migrate the schema until the owner approves the exception strategy.

Confirmed SRD/presentation direction:

- ability modifiers are derived from ability scores and must be displayed automatically;
- saving throws need proficiency state and are normally ability modifier + proficiency bonus when proficient;
- skills are normally associated ability modifier + proficiency bonus when proficient, with expertise multiplying the proficiency contribution where applicable;
- passive Perception is based on the Wisdom (Perception) check modifier;
- the app still must support gifts/homebrew/house-rule exceptions without becoming a legality checker.

A future broader character-check/validation feature remains deferred until late development.

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

## 7. Presentation decisions after V3 QA

### Tabs

Accepted:

1. **Resumen** — fast-reference overview.
2. **Habilidades** — abilities/saves/skills relationship area.

Future tabs are added only when relevant feature domains exist.

### Skill presentation

- **Por habilidades / By skills** is default;
- **Por atributo / By attribute** is alternate;
- preference remains user/device presentation state rather than character mechanics data;
- next control should be a compact two-state segmented/slider-like selector with a visible active indicator.

### Class selector

- exact Spanish class list from official SRD 5.2.1;
- plus `Artífice` alphabetized with the real classes;
- plus final `Otro` escape path exposing custom/homebrew entry.

### Global Settings

Font scale options remain **80 / 90 / 100 / 115 / 130%** with **100% default**.

V3 QA found menu/layout rendering becomes visually wrong above 100%; fix responsiveness rather than silently changing the approved scale.

V4 font comparison:

- **Manrope** — normal-width sans;
- **Sora** — proposed replacement normal-width sans candidate for Atkinson;
- **Barlow Condensed** — condensed;
- **IBM Plex Sans Condensed** — additional condensed candidate;
- Atkinson is removed;
- no serif option.

Theme identities remain System, Light, Dark, Light Gray and Dark Purple. Light Gray needs visibly more gray; Dark Purple must read clearly purple rather than wine/burgundy and be distinct from Dark.

### Skill training control

Keep the compact interaction type, but use intuitive visual states:

- empty box = no proficiency;
- checked box = Competente;
- double-check style box = Pericia.

### Icon controls

Back, Settings/gear and similar icon-only actions must be proper icon buttons with stable touch-target/icon geometry, not typography-scaled text glyphs.

### Combat reference

Approved semantic order:

1. `CA` / `Iniciativa` / `Velocidad`;
2. `PG actuales` / `PG máximos` / `PG temporales`;
3. `Bonificador por competencia` / `Percepción pasiva` / `CD de salvación de conjuros`.

Use clearer/full Spanish terminology rather than opaque abbreviations such as `Comp.` or `Perc. pas.` where practical.

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

Passed/accepted:

- in-place update and existing data preservation;
- current two-tab organization;
- overall density and grouping substantially improved;
- six ability scores fit in one row;
- one class entry compact enough in principle;
- quantity-before-die logic correct;
- class choices/custom path work;
- multiple classes do not destabilize layout;
- both skills presentation modes work;
- keyboard/IME obstruction fixed;
- rotation/screen-off state loss fixed;
- landscape passes;
- persistence passes.

Remaining V4 presentation work is detailed in `docs/CHARACTER_SHEET_UX.md` and D-0045.

## 11. SRD character-sheet context review

The owner explicitly requested a quick but thorough check of PC creation context rather than treating the current fields as arbitrary form data.

Official Spanish SRD 5.2.1 confirms the following relevant relationships:

- ability score → automatic ability modifier;
- saving throw total → ability modifier + proficiency bonus when proficient;
- skill total → associated ability modifier + proficiency bonus when proficient;
- proficiency bonus is reused across those trained values;
- passive Perception → 10 + Wisdom (Perception) check modifier;
- ordinary initiative begins from Dexterity modifier;
- spell save DC has a standard formula once spellcasting ability is known;
- AC and HP have more contextual calculation rules and should not be casually converted into simplistic automatic fields in this presentation pass.

This review improves sheet context but does **not** authorize a guided/legal character builder.

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

Before V4 implementation, obtain owner approval for the durable representation of standard-derived saving-throw/skill values while preserving homebrew/gift exceptions.

Once that is resolved, V4 is a focused pass:

- automatic displayed ability modifiers;
- distinct saving-throw proficiency control;
- remaining spacing/hit-die/class ordering fixes;
- improved typography/theme settings and stable icon buttons;
- segmented skills/attribute view selector;
- empty/check/double-check skill training indicator;
- approved combat-reference ordering and clearer labels;
- preserve the now-passing keyboard, lifecycle/navigation, landscape and persistence behavior.

After V4 is CI-green, produce another stable-signed phone APK and run focused owner QA. Once accepted, proceed to character-foundation PR/review/merge. The following major product slice remains the tablet-primary DM combat tracker.

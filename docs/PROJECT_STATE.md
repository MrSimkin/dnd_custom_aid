# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `implementation/character-data-foundation`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Phase 3 remains complete/canonical on `main`. Phase 4 character data/persistence is functionally accepted on Android phone, but the character-sheet editor is **not yet accepted for PR/merge** after a second UX QA pass. The revised editor improved density/input/landscape behavior, but another focused UX iteration is required before owner review.

## 1. Canonical baseline

The approved product/architecture baseline is D-0034 through D-0043 plus the 2026-08-30 C-0009 proportionality clarifications. Phase 4 ordering and the first character-slice boundary are recorded in `docs/decisions/D-0044_PHASE4_CHARACTER_FOUNDATION_ORDER.md`.

Key constraints remain:

- Android native Kotlin + Jetpack Compose, minimum API 30;
- SQLite/SQLDelight local persistence where useful;
- no speculative auth/hosted sync/realtime infrastructure before a selected workflow needs it;
- character data may represent mixed SRD generations, homebrew and owner-granted exceptions;
- the application is not a guided/legal character builder or automatic rules enforcer;
- persistent character-sheet state remains separate from live combat working state;
- manual feature acceptance uses the intended primary device first and the repeatable suite in `docs/QA_CHECKLIST.md`.

## 2. Canonical implementation state

Phase 2 scaffold is canonical through PR #4 (`d50409270db52df05508f91363bf76385030a77d`).

Phase 3 campaign create/select slice is canonical through PR #5, merged as `dc1304080f0b71bcb44690b5ee317f3877385286`.

Post-merge Phase 3 CI passed for shared tests, SQLDelight generation, Android assembly/APK upload, Desktop build and backend type checking. Manual Phase 3 verification passed on both phone and tablet.

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

## 5. Current branch implementation

`implementation/character-data-foundation` currently adds:

- `CharacterSheet`, `CharacterClassLevel`, `CharacterSkill`, lifecycle/status and skill enums in shared Kotlin;
- SQLDelight tables for `character`, `character_class` and `character_skill`;
- migration `1.sqm` from the Phase 3 campaign-only database to the character schema;
- `CharacterRepository` create/list/read/save behavior;
- persistence tests covering the full 18-skill set, campaign isolation, multiclass + independent hit dice, database reopen, permissive gifted values and Phase 3 database migration;
- Android campaign → character list → character editor flow;
- Spanish editor UI for classes/hit dice, abilities, combat reference, saves and skills;
- stable CI-only debug signing so future test APK artifacts can update one another in place;
- C-0010 intended-device/manual-QA convention and reusable `docs/QA_CHECKLIST.md`;
- second-pass Android character editor UX implementation;
- `docs/CHARACTER_SHEET_UX.md` as the active detailed character-sheet UX/QA/design-reference working record.

The stable signing material is deliberately public/debug-only test material. It must never be reused as a production/release signing key.

## 6. Latest verified implementation/build

Second-pass revised editor implementation:

- `0ac36fa60f64e810b07865f2afeef542185c6fc7` — `Implement second-pass character editor UX`;
- `113fe27c42e15ff0950d53e854796f26de6671b4` — `Route character editing through revised phone UX`.

GitHub Actions run #66 / `33348810081` passed:

- backend TypeScript/Worker checks: success;
- Kotlin shared/tests/build: success;
- Android debug APK build/upload: success;
- Desktop build: success as part of the existing scaffold verification command.

The revised APK updated the prior stable-signed character build in place and preserved owner test data.

## 7. First intended-device QA — functional character foundation

**Primary device:** Android phone.  
**Result:** **FUNCTIONAL QA PASS; UX NOT ACCEPTED.**

The first editor successfully validated the character data model, persistence and workflow, but owner QA found the presentation too form-like, too spacious and too scroll-heavy. It also exposed numeric-input, keyboard obstruction, class/hit-die, skill-training touch-target, landscape-use, font/theme and general information-hierarchy requirements.

Those findings produced the second-pass editor implemented at `113fe27c`.

The complete first-pass findings remain preserved in Git history and are consolidated with current findings in `docs/CHARACTER_SHEET_UX.md`.

## 8. Second intended-device QA — revised editor

**Primary device:** Android phone.  
**Build:** `113fe27c42e15ff0950d53e854796f26de6671b4`.  
**Overall result:** **NEEDS CHANGES.**  
**Persistence regression:** pass.

### Improvements accepted/confirmed

- revised editor visibly differs from the first version;
- more compact and more organized;
- no observed presentation regression versus the old editor;
- numeric-field behavior now works acceptably;
- hit-die selector is an improvement;
- skill training selector works and is understandable;
- landscape mode is generally liked;
- readability is good;
- save/reopen/full app persistence remained correct.

### Required next changes

- compact further; all six ability scores should be able to fit in one row when practical;
- one class entry should become substantially more compact, ideally one row;
- hit-dice display/input logic should read quantity before die type, e.g. `3d10`;
- class selector should use known classes, include Artificer / Artífice (Eberron), and include `Otro` exposing an open custom/homebrew field;
- skill training control remains too large and needs a denser treatment;
- every skill should show its associated ability;
- support two skill/ability presentation concepts: ability-centered grouping versus a separate skill list with visible ability association;
- software keyboard still hides lower content: IME obstruction remains a blocking UX defect;
- landscape grouping is generally good, but adding classes disrupts the grouping/layout by causing dynamic reflow that breaks visual groups;
- tabs/section navigation discussed earlier have not yet been implemented and remain a design item for owner review;
- rotation portrait ↔ landscape currently returns to the start/campaign page;
- screen off/on also returns to the start/campaign page;
- preserve current navigation/editor state across ordinary Android recreation/configuration changes.

### Explicitly unresolved / pinned

- whether skill training/proficiency should change/recalculate the final stored numeric skill modifier;
- saving-throw presentation: owner explicitly paused this topic to provide a clearer explanation later;
- exact tab/accordion/hybrid navigation structure;
- exact final class list beyond Artificer and `Otro`;
- exact font families and exact font-size steps.

Do not silently decide these items.

## 9. Initial Settings direction — next build

The owner wants the first small application Settings surface in the next build.

Initial global settings scope:

- font size;
- font family/style;
- theme.

Theme choices must include at least:

1. System
2. Light
3. Dark
4. Light Gray
5. Dark Purple — explicit owner personal preference

A small sheet-local gear/basic-settings control is also a candidate for character-sheet-specific presentation preferences such as ability/skill grouping mode. Global font/theme preferences and sheet-specific view preferences should remain conceptually distinct unless implementation proves combining them simpler and clearer.

## 10. Paper character-sheet design references

The owner supplied two custom five-page paper character sheets as inspiration:

- `Hoja de PJ - 5.0 - Simkin.pdf`;
- `Hoja de PJ v2 - 5.0 - Simkin.pdf`.

Detailed visual review is recorded in `docs/CHARACTER_SHEET_UX.md`.

Key finding: the paper designs already demonstrate both ability/skill grouping approaches currently under discussion. In particular, the v2 sheet contains one layout that groups skills directly with abilities and another that separates skills/saves while labeling each skill with its associated ability. These references should inform digital hierarchy/grouping without forcing a literal paper-page reproduction.

For durable repository continuity, the binary PDFs should be uploaded by the owner to:

`assets/character-sheets/templates/`

The current GitHub connector can edit text records but cannot safely commit these binary PDFs directly.

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

The paper PDFs contain examples of several deferred areas, but their presence in a reference sheet does not automatically move those features into the current implementation slice.

## 12. Current acceptance gate / immediate next action

Do **not** open or merge a PR yet.

Before the next owner QA APK, perform a focused third character-editor pass, guided by `docs/CHARACTER_SHEET_UX.md`, that:

- fixes rotation/screen-off navigation-state loss;
- fully solves keyboard/IME obstruction;
- further compacts abilities/classes/skills;
- uses natural `3d10`-style hit-dice ordering;
- adds known-class selection with Artificer and `Otro` custom entry;
- shows skill→ability association;
- introduces the first Settings surface for font size, font family/style and the five requested themes;
- preserves good landscape use while stabilizing group placement with dynamic class rows;
- leaves saving-throw redesign and automatic skill recalculation unresolved until owner clarification;
- discusses the tab/section-navigation choice with the owner rather than silently finalizing it.

After that implementation is CI-green, produce a new stable-signed phone APK and rerun intended-device QA. The following major product slice remains the tablet-primary DM combat tracker after the character-data foundation reaches an accepted stable state.

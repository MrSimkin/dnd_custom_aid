# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `implementation/character-data-foundation`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Phase 3 is complete and canonical on `main` after PR #5. Phase 4 begins with the approved Android/local character data foundation; DM combat tracker follows once enough stable character data exists.

## 1. Canonical baseline

The approved product/architecture baseline is D-0034 through D-0043 plus the 2026-08-30 C-0009 proportionality clarifications. Phase 4 ordering and the first character-slice boundary are recorded in `docs/decisions/D-0044_PHASE4_CHARACTER_FOUNDATION_ORDER.md`.

Key constraints remain:

- Android native Kotlin + Jetpack Compose, minimum API 30;
- SQLite/SQLDelight local persistence where useful;
- no speculative auth/hosted sync/realtime infrastructure before a selected workflow needs it;
- character data may represent mixed SRD generations, homebrew and owner-granted exceptions;
- the application is not a guided/legal character builder or automatic rules enforcer;
- persistent character-sheet state remains separate from live combat working state.

## 2. Canonical implementation state

Phase 2 scaffold is canonical through PR #4 (`d50409270db52df05508f91363bf76385030a77d`).

Phase 3 campaign create/select slice is canonical through PR #5, merged as `dc1304080f0b71bcb44690b5ee317f3877385286`.

Post-merge run #34 passed on that merge commit for shared tests, SQLDelight generation, Android assembly/APK upload, Desktop build and backend type checking. Manual Phase 3 verification passed on both phone and tablet.

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
- character persistence tests covering the full 18-skill set, campaign isolation, multiclass + independent hit dice, database reopen and permissive gifted mechanical values;
- Android campaign → character list → character editor flow;
- Spanish editor UI for classes/hit dice, abilities, combat reference, saves and skills.

The shared data-layer test revision `c805eb29043c42ad8c6b14ba35ff8ec449a93999` passed CI before the Android editor was added.

## 6. Validation still in progress

Before review, the branch must still prove:

1. the final Android/editor revision compiles and all existing/shared tests remain green;
2. the SQLDelight migration preserves a real Phase 3 campaign database while adding character tables;
3. an APK can be installed over the previous Phase 3 build without losing campaigns;
4. real phone/tablet manual use can create, edit, save and reopen a multiclass character including skills and hit dice.

Do not open or merge a PR until these checks are complete and owner review occurs.

## 7. Explicitly deferred from this first character slice

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

## 8. Known non-blocking UI follow-up

- Increase information density where useful.
- Improve wide/tablet-landscape use once richer screens provide enough content to design around.
- Add theme support after exact behavior is specified.
- The current character editor is intentionally a functional first layout; visual organization should be judged on real phone/tablet use before becoming a durable sheet-layout convention.

## 9. Immediate next action

Finish CI/migration validation on the current character branch, fix any compile or migration issues, produce a debug APK, and perform owner manual testing on phone/tablet. After acceptance, prepare a focused PR for explicit owner review. The following major product slice remains the DM combat tracker.

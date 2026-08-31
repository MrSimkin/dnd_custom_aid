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
- persistence tests covering the full 18-skill set, campaign isolation, multiclass + independent hit dice, database reopen, permissive gifted values and Phase 3 database migration;
- Android campaign → character list → character editor flow;
- Spanish editor UI for classes/hit dice, abilities, combat reference, saves and skills;
- stable CI-only debug signing so future test APK artifacts can update one another in place.

CI evidence:

- run #47 / head `c805eb29043c42ad8c6b14ba35ff8ec449a93999`: shared character data/tests green;
- run #50 / head `8ac4d56a8d27f162b83ba6accd8b8a1af48cd6ea`: Android character UI/navigation revision green;
- run #51 / head `70ae2e0f276f996f92b94ed664ba41cdd902970e`: migration-preservation test, shared tests, SQLDelight generation, Android debug build/APK, Desktop build and backend checks all green;
- runs #57 and #58: stable debug-signing setup green; APK signing certificate is identical across separate CI runs (`SHA-256 bb96ed194bee843eecfa2a6c2c076f169672ee84437177d62010eae11bb6ce4b`).

The stable signing material is deliberately public/debug-only test material. It must never be reused as a production/release signing key.

## 6. Phase 3 debug-signature transition

The Phase 3 APK previously installed by the owner was signed with an ephemeral GitHub-runner debug key. The new stable CI debug key is necessarily different; Android therefore cannot normally install the new APK over that old Phase 3 package.

This means one clean uninstall/reinstall is required at this transition. Existing on-device Phase 3 test data will be removed by that uninstall.

The Phase 3→character-schema data migration is still covered by the automated migration test: it creates the old campaign-only schema, preserves an existing campaign through `AppDatabase.Schema.migrate`, then successfully uses the new character tables.

After installing the new stable-signed APK, future CI test APKs using this signing identity can update it in place, allowing real device migration/persistence testing across subsequent development builds.

## 7. Remaining manual acceptance gate

Automated validation is complete for the implemented code. Before PR review, manual device verification remains:

1. uninstall the old Phase 3 debug app once because its signing identity differs;
2. install the new stable-signed character-foundation APK;
3. create/select a campaign and enter `Personajes`;
4. create a character;
5. add at least two classes with different hit-die sizes and remaining hit dice;
6. edit ability scores, combat-reference values, saving throws and several skills, including proficiency/expertise markers;
7. deliberately enter at least one unusual/gifted mechanical value to confirm the UI accepts it;
8. save, leave the editor, reopen the character and confirm values remain;
9. fully close/restart the app and confirm campaign/character data remains;
10. sanity-check usability on phone and tablet, especially tablet/landscape now that the app has a richer screen.

Do not open or merge a PR until this manual UX/persistence check is complete and owner review occurs.

## 8. Explicitly deferred from this first character slice

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

## 9. Known non-blocking UI follow-up

- Increase information density where useful.
- Improve wide/tablet-landscape use once richer screens provide enough content to design around.
- Add theme support after exact behavior is specified.
- The current character editor is intentionally a functional first layout; visual organization should be judged on real phone/tablet use before becoming a durable sheet-layout convention.

## 10. Immediate next action

Owner installs/tests the stable-signed character-foundation APK on phone/tablet and reports concrete UX/data issues. Record results on this branch. After acceptance and any focused fixes, prepare a PR for explicit owner review. The following major product slice remains the DM combat tracker.

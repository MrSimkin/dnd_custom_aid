# Project State

**Last verified:** 2026-08-30  
**Canonical branch:** `main`  
**Current working branch:** `implementation/character-data-foundation`  
**Open review:** none  
**Phase:** Phase 4 — MVP Buildout  
**Status:** Phase 3 is complete and canonical on `main` after PR #5. Phase 4 begins with the approved Android/local character data foundation; DM combat tracker follows once enough stable character data exists. The character data/persistence workflow has now passed intended-device functional QA on an Android phone, but the first editor presentation requires a focused UX/design revision before PR review.

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
- stable CI-only debug signing so future test APK artifacts can update one another in place;
- C-0010 intended-device/manual-QA convention and reusable `docs/QA_CHECKLIST.md`.

CI evidence:

- run #47 / head `c805eb29043c42ad8c6b14ba35ff8ec449a93999`: shared character data/tests green;
- run #50 / head `8ac4d56a8d27f162b83ba6accd8b8a1af48cd6ea`: Android character UI/navigation revision green;
- run #51 / head `70ae2e0f276f996f92b94ed664ba41cdd902970e`: migration-preservation test, shared tests, SQLDelight generation, Android debug build/APK, Desktop build and backend checks all green;
- runs #57 and #58: stable debug-signing setup green; APK signing certificate is identical across separate CI runs (`SHA-256 bb96ed194bee843eecfa2a6c2c076f169672ee84437177d62010eae11bb6ce4b`).

The stable signing material is deliberately public/debug-only test material. It must never be reused as a production/release signing key.

## 6. Phase 3 debug-signature transition

The Phase 3 APK previously installed by the owner was signed with an ephemeral GitHub-runner debug key. The new stable CI debug key is necessarily different; Android therefore cannot normally install the new APK over that old Phase 3 package.

This means one clean uninstall/reinstall was required at this transition. Existing on-device Phase 3 test data was not expected to survive that uninstall.

The Phase 3→character-schema data migration is still covered by the automated migration test: it creates the old campaign-only schema, preserves an existing campaign through `AppDatabase.Schema.migrate`, then successfully uses the new character tables.

After installing the new stable-signed APK, future CI test APKs using this signing identity can update it in place, allowing real device migration/persistence testing across subsequent development builds.

## 7. Intended-device QA result — character foundation

**Primary device:** Android phone.  
**Result:** **FUNCTIONAL QA PASS.**  
**Interpretation:** character data entry/persistence behavior is accepted at the functional level; the current editor UX/presentation is not ready for PR review and requires a focused redesign pass.

The owner reported the following observations on the phone build:

- too much unused/free space;
- no meaningful UI design yet; presentation feels like a generic form rather than a PC character sheet;
- UX can be improved substantially;
- fields that are numeric-only currently accept letters and should use numeric-oriented input/keyboard behavior;
- when the software keyboard appears it obscures the lower part of the sheet/content;
- the screen should be substantially more compact and require much less scrolling;
- tabs and/or accordions are promising organizational approaches to evaluate;
- application/user UI needs adjustable font-size support;
- the current font is not yet accepted; font choice remains open;
- hit-die size should use a constrained/dropdown-style control with the associated number immediately adjacent;
- marking a skill as `Competente` is currently difficult and needs a much easier interaction;
- many mechanically constrained/static choices should not behave like arbitrary open text when a known choice set exists;
- class selection should use a known-class-oriented control rather than a large free-text block, while preserving a path for nonstandard/homebrew content under the product's permissive rules philosophy;
- the class block consumes too much vertical space;
- landscape should use the additional width meaningfully, likely through a wider/multi-column arrangement rather than merely stretching the portrait form.

### UX requirements that are clear enough to implement in the next pass

1. Numeric-only mechanical fields use appropriate numeric keyboard/input behavior and do not accept arbitrary letters.
2. Keyboard/IME appearance must not leave the active/lower editable area inaccessible or covered without a practical way to scroll/reveal it.
3. The character editor must be materially more compact and reduce unnecessary vertical scrolling.
4. Proficiency/expertise interaction must have a larger/easier touch target than the current control.
5. Class and hit-die entry should use constrained/common-value controls where useful, while retaining an explicit escape path for custom/homebrew values when needed.
6. Phone landscape must make meaningful use of available horizontal width rather than only stretching a single portrait column.
7. User-adjustable application font size is now an explicit desired capability; exact levels/control location are not yet specified.

### UX choices still intentionally open

- exact navigation organization: tabs, accordions, or a hybrid;
- exact font family;
- final sheet visual language/component styling;
- exact number and grouping of landscape columns;
- exact predefined class list and how `Otra/Personalizada` is exposed;
- exact font-size choices and where the setting lives.

Do not turn these open items into durable conventions without owner review.

## 8. Current acceptance gate

The first functional character build has served its purpose: it validates the data model, persistence and workflow on the intended phone device.

Before opening a PR, perform a focused **character-sheet UX revision** on the same branch, then produce a new stable-signed APK and rerun the intended-device phone QA suite with emphasis on:

- compactness and scrolling;
- keyboard/input behavior;
- class/hit-die controls;
- skill proficiency/expertise interaction;
- character-sheet-like information hierarchy;
- landscape width usage;
- font/readability behavior.

Tablet QA is not required for this character slice at present because the owner has explicitly scoped current acceptance to the intended phone experience. The tablet experience will be evaluated when a tablet-primary feature, especially the DM combat tracker, is under development.

Do not open or merge a PR until the redesigned phone UX is manually rechecked and owner review occurs.

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
- automatic legality/rules enforcement or character checking;
- combat tracker implementation itself.

## 10. Known non-blocking broader UI follow-up

- Add theme support after exact theme behavior is specified.
- The current font family is not accepted; font selection remains a future explicit design choice.
- User-adjustable font size is required, but the exact setting model is still pending.

## 11. Immediate next action

Redesign the Android phone character editor around the recorded QA findings without changing the accepted character data model. Keep the work proportional: fix the concrete input/keyboard problems, substantially improve density and sheet-like organization, introduce better constrained controls where appropriate, and improve landscape width use. Produce a new APK for phone-first QA before PR review. The following major product slice remains the tablet-primary DM combat tracker.

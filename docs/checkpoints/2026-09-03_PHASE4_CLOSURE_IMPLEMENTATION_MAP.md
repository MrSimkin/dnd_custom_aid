# Phase 4 Character Foundation Closure — implementation map and gates

**Date:** 2026-09-03  
**Status:** implementation map for owner-approved D-0047 scope  
**Branch:** `implementation/phase4-character-closure`  
**Canonical `main`:** untouched

## 1. Purpose

Implement the complete owner-approved Phase 4 character closure in reviewable increments, preserve migration/data safety, produce one new final Android phone+tablet QA APK, and then move toward the DM stage rather than extending the character phase indefinitely.

Authoritative design inputs:

- `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md`;
- `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md`;
- `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_SCOPE_APPROVED_AND_AUDITED.md`;
- earlier D-0044/D-0045/D-0046 decisions and existing Phase 4 UX/persistence records.

## 2. Prototype audit — what already exists

The focused branch contains a preliminary shared/data prototype created before the owner required the proposal review.

Green prototype checkpoint:

- commit `89aad12a094476c7b6798f6f0626bf978a5d0831`;
- CI run `33779104922` — PASS.

Prototype durable model/schema already includes:

- class/subclass name, source, rules family and catalog keys;
- Inspiration;
- death-save successes/failures;
- structured languages/tools/armor/weapons/other proficiencies;
- Weapon Mastery records;
- generic Resources;
- generic class-option records capable of Artificer/Technique/Metamagic/Invocation-like choices;
- Forms;
- Companions;
- pin/favorite-like state on combat entries, traits, spells, resources/forms/class options;
- migration `6.sqm` and round-trip/migration tests.

This prototype is reusable only where it matches D-0047.

### Known prototype mismatches/gaps

- no new closure Android UI was implemented in the prototype;
- the catalog currently treats Arcana Unleashed entries through an `upcoming` path even though official D&D Beyond source content is already present as of 2026-09-01; catalog provenance must be corrected;
- no approved `Gestión` surface yet;
- no Conditions/Exhaustion durable model yet;
- no structured Defenses model yet;
- no general structured special senses/movement model yet;
- no Concentration working-state record yet;
- no Rest-assistant configuration/recovery mapping yet;
- no consumable/ammunition behavior beyond existing quantity;
- inventory has a simple location field but no intentional container/location management UX;
- no portrait/token storage/reference yet;
- no reconciliation snapshot records yet;
- no own-format backup/import flow yet;
- no XP/Milestone progress state yet;
- no custom-skill durable records yet;
- favorite coverage is incomplete for all newly approved record types;
- no Table/read-only mode yet;
- no simple dice-roller surface yet;
- no temporary-effect records yet;
- no module override/hide-not-delete settings yet;
- no configurable haptic preference yet;
- no Supercompact view yet;
- no global D16 context-preservation framework/pass yet;
- no full tablet navigation-rail/master-detail pass yet;
- existing Equipment/Spells/Traits/Notes/Combat UX still requires the approved closure improvements and global IME/action consistency pass.

## 3. Migration strategy

The currently distributed owner QA APK is on the pre-closure schema lineage. The next APK must install over it without data loss.

### Rule

Do not rewrite the already-green prototype migration merely to compress migration count. Use an additive **schema 7** migration for the remaining approved durable fields/domains unless implementation discovers a concrete safety reason to do otherwise.

Expected upgrade path for the owner device:

`existing schema -> existing Phase 4 migrations -> schema 6 prototype additions -> schema 7 closure additions`.

This also makes migration debugging more legible and avoids silently changing the meaning of a migration that has already been committed/tested.

### Mandatory migration tests

- current schema create-from-scratch;
- prior owner-APK schema -> current schema;
- schema 5 -> schema 6 -> schema 7 preservation;
- existing Background Raza/Religión preservation;
- existing spells/sources/prepared/shared slots preservation;
- existing inventory/currency/traits/notes/combat preservation;
- existing class rows migrated with subclass/provenance defaults without guessing;
- new fields receive safe empty/default values;
- hide/disable module settings never delete module records.

## 4. Increment plan

### Increment A — authoritative shared model + catalog reconciliation

Goal: make the shared model fully represent D-0047 before UI expansion.

Tasks:

- review/reuse schema-6 prototype fields;
- correct Arcana Unleashed catalog availability/provenance;
- add missing official catalog/module suggestions found by audit;
- ensure subclass is first-class per class row and source/version variants are distinguishable;
- add schema-7 durable domains for:
  - Conditions/Exhaustion;
  - Defenses;
  - special senses/movement;
  - Concentration;
  - Rest/recovery metadata needed by generic Resources without hard rules enforcement;
  - intentional inventory container/location and consumable/ammunition metadata;
  - portrait/token reference metadata;
  - reconciliation checkpoints/snapshots;
  - XP/Milestone progress state;
  - custom skills tied to an ability;
  - temporary effects;
  - module visibility/override state;
  - Table/read-only mode;
  - per-character haptic preference if D-0047 implementation keeps haptics character-specific;
  - any explicit Supercompact configuration that cannot be derived from Favorites;
- extend Favorite/Quick Access state consistently across relevant record types;
- preserve D-0046 calculated-value + adjustment philosophy.

Gate A:

- shared desktop tests PASS;
- schema create/migration tests PASS;
- repository holistic round-trip PASS;
- catalog tests assert key current/legacy official options and custom escape path;
- no Android owner APK is designated yet.

### Increment B — reusable interaction foundation

Goal: fix cross-cutting UX once rather than repeatedly in each tab.

Tasks:

- reusable app-wide IME-safe editor/dialog shell;
- consistent `Guardar` / `Cancelar` / create semantics;
- standard add/edit/delete/duplicate/overflow icon vocabulary with stable touch targets/accessibility descriptions;
- reusable row/card/panel visual grammar under D05;
- reusable contextual toolbar under D08;
- reusable sort/filter/search state helpers that do not mutate manual order;
- reusable real drag feedback with lift/destination/reflow;
- configurable haptic service/setting under D14;
- reusable inline validation under D10;
- reusable named destructive confirmation under D11;
- reusable useful empty state under D12;
- D15 saved/dirty state indicator and I20 leave-with-unsaved-changes guard;
- D16 context-preservation foundations for tab/list/filter/search/sort/scroll/selection where technically feasible.

Gate B:

- Android assemble PASS;
- focused state/helper tests PASS where logic is testable;
- no known editor with critical actions still hidden behind IME in code review/smoke coverage;
- existing Android Back hierarchy preserved.

### Increment C — PC Settings consolidation

Goal: make PC Settings the character-wide configuration home.

Tasks:

- move lifecycle status control out of General and into PC Settings;
- spellcasting visibility retained with hide-not-delete behavior;
- conditional module auto-suggestions + manual overrides;
- hiding a module preserves stored data;
- configurable haptic feedback;
- Supercompact entry/configuration;
- Table/read-only mode configuration;
- XP/Milestone mode configuration;
- entry to global Application Settings;
- responsive phone/tablet PC Settings layout;
- use D16 context preservation when entering/leaving settings and sub-settings.

Gate C:

- save/reopen/recreate/rotation tests for settings state where applicable;
- hide/show module data preservation tests;
- Android Back: sub-settings -> PC Settings -> character editor -> character list remains correct;
- Android assemble PASS.

### Increment D — `Gestión` live character maintenance

Goal: implement the approved general management surface.

Tasks:

- Conditions + Exhaustion;
- Concentration current effect/spell reference;
- generic Resources with one-tap spend/recover + exact edit;
- Short/Long Rest assistant:
  - preview proposed recovery;
  - selective apply;
  - no silent legality enforcement;
  - custom/manual recovery descriptions supported;
- temporary effects/bonuses;
- reconciliation checkpoint creation/view;
- death saves surfaced here and/or Combat when relevant, with 0-HP contextual presentation;
- Inspiration operational control where its final compact placement works best;
- relevant hit-dice/resource recovery without duplicate authoritative data.

Gate D:

- resource/rest calculations and selective apply tests;
- no Rest action changes a resource not explicitly covered/selected;
- persistence/reopen/recreation PASS;
- portrait/landscape responsive smoke PASS.

### Increment E — General/Habilidades/Combat completeness improvements

Tasks:

- General removes lifecycle status and gains cleaner class+subclass+source presentation;
- known classes may suggest common hit die while remaining editable;
- freshness/last-updated visible;
- portrait/token presentation;
- structured defenses, senses and special movement placed where quick reference is strongest;
- Passive Insight + Passive Investigation;
- custom skills integrated into both Habilidades organization modes;
- Combat entries show action type + damage/effect type at glance;
- quick HP damage/heal/temp-HP interaction;
- death-save contextual surface at 0 HP;
- Favorite/Quick Access integration;
- simple dice roller from appropriate attacks/saves/skills without automatic rule resolution.

Gate E:

- derived-value D-0046 regressions PASS;
- custom skills compute using assigned ability + training + adjustment;
- HP quick operations preserve exact manual edit path;
- no dice-roll action writes unintended character mechanics.

### Increment F — Equipo closure

Tasks:

- materially denser ordinary Equipment rows;
- rich detail only on open/edit;
- materially compact Monedas grid/editors;
- independent Manual/A–Z display for ordinary and special Equipment;
- manual order preserved when switching sort modes;
- real drag feedback in Manual mode;
- carried/equipped/stored/special filters;
- total carried weight + attunement count;
- location/container chips and container/location editing;
- consumable/ammunition quick decrement/adjust behavior;
- duplicate action;
- search;
- collapse sections where useful;
- tablet/wide multi-column and master-detail behavior.

Gate F:

- sort/manual-order persistence tests;
- quantity/weight/attunement calculations tests;
- IME-safe ordinary and special item editors;
- phone/tablet portrait/landscape layout smoke.

### Increment G — Rasgos + Conjuros + Notas + Trasfondo closure

Rasgos:

- group/filter/search by source/type;
- remaining/max usage meter;
- Favorite support;
- duplicate;
- real drag feedback/manual ordering;
- responsive tablet grouping.

Conjuros:

- Manual/A–Z within level/source rules without destroying manual order;
- source/prepared/concentration/ritual filters;
- V/S/M + Concentration + Ritual + Prepared badges;
- sticky/collapsible spell-level headers;
- Favorite support;
- duplicate;
- real drag feedback;
- numeric spell level input preserved;
- shared slot synchronization preserved;
- tablet master-detail where useful.

Notas:

- short preview on cards;
- real drag feedback;
- duplicate;
- long text internal scrolling preserved;
- two+ column responsive layout where available width supports it;
- D16 preserves list/scroll context.

Trasfondo:

- compact identity/personality area;
- long story collapsible;
- Raza and Religión / Fe persistence preserved;
- responsive wide layout.

Gate G:

- spell source/prepared/slot persistence regression PASS;
- Notes/Traits/Background persistence PASS;
- manual sort order preservation PASS;
- IME-safe editors PASS.

### Increment H — conditional class/subclass modules

Goal: implement reusable module UIs over the shared data model.

Modules:

- Artífice;
- Formas;
- Técnicas;
- Metamagia;
- Pactos;
- Compañeros.

Rules:

- visible set = union of class/subclass suggestions + manual PC Settings overrides;
- no duplicate module tabs;
- hide-not-delete;
- source/provenance visible;
- Favorite integration;
- manual/A–Z where list-like;
- generic Resources linked conceptually rather than duplicated;
- Spells remain authoritative for actual spell records;
- companions remain durable character entities, not live combat participant state.

Gate H:

- representative official trigger tests:
  - Druid -> Forms;
  - Battle Master -> Techniques;
  - Sorcerer -> Metamagic;
  - Warlock -> Pacts;
  - Artificer -> Artifice;
  - Beast Master/Battle Smith/Reanimator/Vestige -> Companions as relevant;
- multiclass union test;
- custom/manual override test;
- hide/show persistence test.

### Increment I — Supercompact + Table mode + adaptive tablet shell

Goal: make wide/tablet use materially better rather than stretched-phone use.

Tasks:

- Supercompact operational projection using real durable values + Favorites;
- adaptive column count based on useful minimum content width;
- phone portrait/landscape layouts;
- tablet portrait/landscape layouts;
- wide navigation rail under D02;
- master-detail under D03 for list-heavy modules;
- D01 sticky compact character header;
- D13 sticky group headers where useful;
- D16 global context preservation verified across phone and tablet;
- Table/read-only mode visually suppresses structural-edit affordances while leaving explicitly allowed live controls usable.

Gate I:

- state restoration across rotate/recreate/full app reopen;
- last-open-tab per character persisted;
- Supercompact contains no duplicate authoritative state;
- Table mode cannot accidentally perform blocked structural mutations;
- layout smoke at phone/tablet portrait/landscape and larger text scale.

### Increment J — own-format backup + reconciliation completion

Tasks:

- export the application's own character backup format;
- import/restore that same format;
- include a format/schema version;
- preserve stable identity carefully: default import behavior must avoid accidental duplicate-ID collision in one campaign/device; exact restore semantics should be explicit in implementation docs;
- no third-party D&D Beyond/PDF/import parser implied;
- final reconciliation checkpoint UX integration.

Gate J:

- export -> import round trip of a richly populated test character;
- malformed/unsupported backup gives understandable error without data loss;
- existing character is never overwritten silently;
- import remains local/offline-capable.

## 5. Global responsive/UX invariants for every increment

Every user-visible increment must preserve:

- phone + tablet intent;
- portrait + landscape sanity;
- app text scaling;
- keyboard/IME action reachability;
- Android Back hierarchy;
- screen-off/recreation state;
- save/reopen persistence;
- D16 parent-context preservation where technically feasible;
- minimum accessible touch targets even when visual affordances are compact;
- user-facing Spanish terminology;
- no automatic legality/rules enforcement.

Do not defer all tablet work to the final increment. Build responsive primitives early and use Increment I for holistic/tablet-specific integration and polish.

## 6. Automated gate strategy

After each consequential shared/schema increment:

- `:shared:desktopTest` or equivalent full shared test surface;
- migration/persistence regression;
- Android debug assemble;
- Desktop build to catch shared API breakage;
- backend type-check even when backend is unchanged, preserving the established standard gate.

After Android-only increments, run at minimum Android assemble plus relevant shared tests; use the full standard gate at milestone closures and before any QA APK designation.

A green CI run is necessary but never substitutes for real-device visual/IME/drag/tablet QA.

## 7. Final owner-QA candidate gate

Before giving the owner an APK:

- freeze code for the candidate;
- run full standard CI gate;
- record exact tested commit;
- record workflow run;
- record artifact ID/name;
- record artifact ZIP hash and extracted APK hash when available;
- verify later documentation commits do not alter the candidate code identity;
- do not silently patch the candidate after owner testing begins. Any code change creates a new candidate identity.

## 8. Final phone + tablet owner-QA matrix

The final QA is a closure acceptance pass, not a restart of every historical check verbatim.

### A. Migration/data preservation

- install over previous owner APK;
- campaigns/characters remain;
- existing Background, Combat, Equipment, Traits, Spells, Notes remain;
- new fields initialize safely.

### B. Global interaction

- all editor actions reachable with keyboard visible;
- outside-tap/Back keyboard behavior does not destroy draft;
- consistent Add/Edit/Delete/Duplicate vocabulary;
- unsaved-change guard;
- dirty/saved indicator;
- D16 context preservation;
- haptic ON/OFF behavior.

### C. Phone portrait + landscape

- all general tabs/modules;
- dense Equipment/Monedas;
- Gestión;
- conditional modules;
- Supercompact;
- sorting/filtering/search/drag;
- Table mode;
- larger text sample.

### D. Tablet portrait + landscape

- navigation rail usefulness;
- master-detail usefulness;
- additional columns/density;
- no giant stretched phone components;
- Supercompact visual usefulness/beauty;
- rotation/context preservation;
- keyboard/editor behavior.

### E. Data-heavy workflows

- class/subclass/provenance/multiclass;
- module auto suggestion + manual override + hide/show no-delete;
- rest/resource/conditions/concentration/effects;
- equipment containers/consumables;
- spell filters/sort/shared slots;
- companions/forms/techniques/metamagic/pacts/artifice;
- own-format backup round trip.

### F. Resilience

- repeated tab/module switching;
- rotation with drafts;
- screen off/on;
- full close/reopen;
- saved order/filter/presentation preferences where intended;
- no unexpected data loss.

## 9. Scope-stop rule

Once the approved D-0047 package passes automated gates and owner phone+tablet QA with no blocking issue, **Phase 4 should close**.

New unrelated player-character ideas discovered during that QA should normally be recorded for later rather than expanding this closure indefinitely, unless the owner explicitly identifies them as a blocker to using the character foundation or to beginning the DM stage.

## 10. Exact continuation after this map

Implementation may now resume on `implementation/phase4-character-closure`, beginning with **Increment A**.

Before writing code, compare the prototype schema/catalog at `89aad12...` to Increment A and reuse only aligned pieces. Do not restart from scratch merely because the design review interrupted implementation.

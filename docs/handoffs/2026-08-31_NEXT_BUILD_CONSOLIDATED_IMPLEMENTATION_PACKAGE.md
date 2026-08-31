# Next-build consolidated implementation package

**Status:** Proposed for owner approval before production coding  
**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Purpose

This package consolidates the complete post-run-#180 next-build scope into one implementation sequence.

It combines:
- the accepted run-#180 baseline;
- approved corrective UX work from D-0053 through D-0056;
- approved new top-level domains: `Trasfondo`, `Rasgos`, `Conjuros`, `Notas`;
- character-level `PC Settings`;
- multiclass spellcasting-source behavior;
- migration/default rules;
- cross-domain ownership rules;
- responsive/navigation rules from the final design gate.

No production code should begin until this package is owner-approved and checkpointed.

---

# 1. Baseline and recovery contract

## Canonical implementation baseline

Use the current `implementation/character-data-foundation` branch as the working branch until the next-build implementation is complete and validated.

Run #180 remains the accepted functional baseline. It is not the final merge candidate because the approved corrective UX backlog and new domains remain outstanding.

## Permanent safety rules

Before any existing file is modified:
1. refetch current contents/blob SHA;
2. prefer narrow edits/patch-capable workflows;
3. avoid whole-file replacement of large source files for small changes;
4. make a durable Git checkpoint after every meaningful implementation increment, diagnosis, owner QA result, blocker, or recovery concern;
5. CI failure blocks progression to dependent increments until diagnosed and checkpointed.

The prior accidental truncation of `CharacterEditorV4.kt` is treated as a permanent warning against risky full-file replacement.

---

# 2. Implementation sequence

The final APK may contain the complete scope, but implementation proceeds in small independent increments.

## Increment A — Baseline verification and implementation map

Before changing production behavior:
- verify branch HEAD and run-#180 baseline files;
- identify exact files/tables/repositories affected by each planned increment;
- confirm current CI is green;
- create a short implementation-start checkpoint.

**Gate A:** baseline green and recovery point recorded.

---

## Increment B — Run-#180 corrective UX backlog

Implement the already-approved corrections before introducing new product domains where practical.

### General
- nonzero `Ajuste adicional` collapsed state uses a compact marker rather than a second text line;
- exact adjustment remains available in tap breakdown;
- `Velocidad` displays imperial first plus approximate metric wherever presented.

### Combate
- logical-row vertical centering when adjacent content wraps;
- attack/action editor is IME-safe;
- outside tap while keyboard/editor is active does not silently dismiss/discard;
- replace up/down reorder controls with drag-and-drop while persisting exact order.

### Equipo
- same IME-safe editor and explicit-dismiss behavior as Combate;
- inventory drag-and-drop preserving `sortOrder`;
- materially more compact responsive currency layout;
- attacks/actions and inventory exploit wider layouts with multi-column presentation where appropriate;
- special equipment remains visually separated while retaining the unified inventory model;
- icon-only controls use vectors/content descriptions rather than Unicode glyphs.

### Settings / Habilidades
- remove or replace redundant/disliked font candidates according to owner QA; replacements must be owner-auditioned rather than invented silently;
- correct theme issues already recorded: `Gris`, `Azul noche`, light adaptations, `Matrix` rename, `Pergamino`, and replacement for oversized theme dropdown interaction;
- each font option renders in its own font family in the selector;
- `Habilidades -> Por atributo` returns to and retains the approved compact two-column layout;
- solve 115%/130% responsiveness without collapsing that layout to one column;
- correct attribute/modifier alignment.

**Checkpoint B1:** General + Combate corrections.  
**Checkpoint B2:** Equipo corrections.  
**Checkpoint B3:** Settings + Habilidades corrections.

**Gate B:** CI green after each checkpoint; existing run-#180 functionality still passes automated regression coverage.

Note: unresolved font replacements require owner audition before final settings closure. They do not justify inventing arbitrary permanent replacements.

---

## Increment C — New persistent data foundation and migration

Add the new character-owned model before full new-tab UI.

### Character-wide state
Add persisted `Lanzador de conjuros` state.

Migration rules:
- existing PC -> ON when meaningful existing spellcasting state exists;
- otherwise OFF;
- new PC -> OFF;
- OFF never deletes spellcasting data.

### Trasfondo
Persist fields for:
- background name;
- summary/description;
- personality traits;
- ideals;
- bonds;
- flaws;
- character story.

The two character-image slots remain UI placeholders only in this build; do not persist fake image records.

### Rasgos
Persist ordered trait/feature entries with:
- stable ID;
- name;
- free-text source;
- organized type/category;
- description;
- optional notes;
- optional usage maximum/spent state;
- optional free-text recovery rule;
- optional activation (`Pasivo`, `Acción`, `Acción adicional`, `Reacción`, `Otro`);
- sort order.

### Conjuros
Persist:
- conceptual spell records;
- stable spellcasting-source entities;
- optional link from source to a PC class row;
- spell-source many-to-many associations;
- source-specific `Preparado` state;
- manual source order;
- manual spell order within level;
- spell fields approved in D-0059.

Do not duplicate existing spell-slot persistence; Quick Magic and `Conjuros` share the same authoritative slot records.

### Notas
Persist:
- one large general-notes field per character;
- ordered titled note cards with stable ID, title, content, sort order.

### Migration/default principles
- preserve all existing run-#180 state unchanged;
- new domains initialize empty;
- do not repurpose combat-entry or inventory-item notes;
- do not infer spellcasting sources from class or Quick Magic data;
- references fail softly rather than causing unrelated deletion.

**Checkpoint C1:** SQLDelight/schema migration files.  
**Checkpoint C2:** shared domain models/repository mapping.  
**Checkpoint C3:** migration and repository tests.

**Gate C:** migration tests prove run-#180 data preservation and new-PC defaults.

---

## Increment D — Top-level navigation + PC Settings shell

Introduce the approved navigation architecture before filling all new tabs.

### Top-level row
Order when spellcasting ON:
1. General
2. Habilidades
3. Combate
4. Equipo
5. Trasfondo
6. Rasgos
7. Conjuros
8. Notas

When spellcasting OFF, hide `Conjuros` and retain relative order of remaining tabs.

Rules:
- horizontally scrollable single-line tab strip;
- selected tab auto-scrolls fully into view;
- no swipe-between-page navigation;
- selection survives recreation/rotation/text-scale change when the tab still exists;
- if spellcasting is disabled while `Conjuros` is selected, fallback to `General`.

### PC Settings
- gear available from PC header regardless of selected tab;
- dedicated full-screen PC Settings page;
- only character-wide controls belong there;
- initially `Lanzador de conjuros` is the only required setting;
- turning it OFF with existing spellcasting data shows a brief non-destructive hide-not-delete confirmation.

**Checkpoint D1:** navigation shell.  
**Checkpoint D2:** PC Settings behavior and persistence.

**Gate D:** navigation/selection tests at all supported text scales.

---

## Increment E — Trasfondo tab

Implement approved detailed layout:
- background name + separate summary/description;
- two character-image placeholder cards, with no fake persistence;
- compact preview/edit cards for personality traits, ideals, bonds, flaws;
- larger story/history area;
- no generic Notes field inside Background;
- wide layouts may use two columns for compact narrative cards;
- Story remains comfortably wide;
- image placeholders side by side when practical and stack only when necessary;
- all text editors IME-safe and explicit about dismissal.

**Checkpoint E:** Trasfondo persistent UI complete.

**Gate E:** create/edit/save/reopen/recreation/rotation tests.

---

## Increment F — Rasgos tab

Implement ordered structured cards.

Each entry supports:
- name;
- free-text source;
- controlled/permissive type;
- description;
- optional notes;
- optional manual usage tracker;
- optional recovery text;
- optional activation/action type.

Interaction:
- compact collapsed card with short preview;
- tap for full details/editor;
- drag-and-drop ordering;
- responsive multi-column presentation on wide layouts;
- IME-safe editor behavior.

No automatic creation from equipment, background, class, or spells.

**Checkpoint F:** Rasgos persistent UI complete.

**Gate F:** ordering/usage persistence + recreation/rotation tests.

---

## Increment G — Conjuros foundation UI and source management

Implement:
- `Todos` + one subtab per spellcasting source;
- subordinate horizontally scrollable single-line source strip;
- source selection by stable ID;
- add/rename/reorder/delete source;
- optional link to a PC class row;
- custom sources always allowed;
- no automatic source creation for every class;
- deleting selected source falls back to `Todos`;
- deleting source with associations requires warning;
- deleting a class merely unlinks a linked spell source rather than deleting source/spells.

**Checkpoint G:** source-management/navigation shell.

**Gate G:** source CRUD, reorder, rename, soft-unlink, and selection tests.

---

## Increment H — Conjuros spell list/details

Implement conceptual spell records grouped by:
- Trucos;
- Nivel 1 through Nivel 9.

Spell fields:
- Nombre;
- Nivel;
- one or more source associations;
- Tiempo de lanzamiento;
- Alcance;
- V/S/M components;
- optional material text;
- Duración;
- Concentración;
- Ritual;
- Descripción;
- optional spell-specific Notas.

Behavior:
- level sections collapsible;
- empty/unconfigured levels de-emphasized/collapsed;
- manual drag-and-drop order within level;
- compact search in the currently selected `Todos`/source view;
- source-specific `Preparado` checkbox;
- `Todos` shows source-specific prepared indicators rather than one ambiguous universal checkbox;
- one conceptual spell may belong to multiple sources and appears once in `Todos`;
- conceptual spell owns level; source associations do not have independent levels.

**Checkpoint H:** spell CRUD/list/search/prepared state complete.

**Gate H:** multi-source spell cases, ordering, filtering, search, and recreation tests.

---

## Increment I — Quick Magic / Conjuros shared-slot integration

Wire `Conjuros` level headers to the existing authoritative slot state.

Rules:
- Quick Magic and `Conjuros` mutate exactly the same slot records;
- no duplicate slot cache/persistence model;
- slot changes in either view appear immediately in the other;
- Quick Magic remains the single primary manual DC / spell attack / casting ability profile for this build;
- `Lanzador de conjuros = OFF` hides Quick Magic and `Conjuros` without data loss.

**Checkpoint I:** shared-slot integration.

**Gate I:** bidirectional slot-state tests + hide/show preservation tests.

---

## Increment J — Notas tab

Implement the approved hybrid model:
- prominent large unrestricted `Notas generales` area;
- optional titled notes below;
- each titled note has only title + content;
- add/edit/delete;
- drag-and-drop order;
- compact card title + preview;
- wide layouts may use multiple columns for titled cards;
- no required dates/categories/tags/session metadata;
- IME-safe editing.

**Checkpoint J:** Notes persistent UI complete.

**Gate J:** large-text editing, titled-note reorder, recreation/rotation tests.

---

## Increment K — Responsive + accessibility integration pass

Perform a dedicated integration pass after all domains exist.

Required checks:
- 80/90/100/115/130% text scale;
- portrait + landscape;
- top-level tab strip never malformed/wrapped;
- selected top-level tab remains visible;
- Conjuros source strip remains usable with multiple/long source names;
- Habilidades `Por atributo` remains two-column at 115/130;
- Background/Rasgos/Conjuros/Notas exploit width appropriately;
- every new editor is IME-safe;
- outside taps do not silently discard active editor state;
- vector icons/content descriptions/touch targets for all icon-only controls;
- no Unicode glyph pseudo-buttons;
- drag-and-drop remains usable at large text scale.

**Checkpoint K:** integrated responsive/accessibility corrections.

**Gate K:** CI green plus focused UI/recreation regression.

---

## Increment L — Final automated regression and owner QA candidate

Before producing the phone-QA APK:
- run all existing repository/unit/migration tests;
- add regression tests for new persistence/migration/ownership behavior where technically practical;
- verify no run-#180 data path regressed;
- generate one clearly identified APK artifact;
- record workflow/run/artifact IDs and hashes in a QA-target checkpoint.

**Checkpoint L:** phone-QA target checkpoint.

---

# 3. Cross-domain ownership constraints during implementation

These rules are non-negotiable implementation constraints:

- classes own class identity/levels;
- spellcasting sources are separate stable entities and may optionally reference classes;
- deleting a class must not cascade-delete spell sources/spells;
- conceptual spells own their level/details;
- spell-source associations own source-specific prepared state;
- Quick Magic and Conjuros share one slot state;
- Trasfondo owns narrative background/personality/history;
- Rasgos owns structured features;
- Equipo owns item identity/equipment state/attunement/weight/location;
- Combate may contain manual quick-play summaries but does not become authoritative for spells/traits/items;
- Notas remains free-form and non-authoritative for structured game state;
- repeated AC/HP/Initiative/Speed displays are views of existing authoritative character data, not duplicated persistence;
- destructive cascading is reserved for true ownership boundaries;
- broken optional links should fail softly and preserve surviving data.

---

# 4. Owner phone-QA sequence for the final candidate

The final owner QA should be performed on one identified APK after all increments and CI gates are green.

## A. Migration preservation
1. upgrade/open a run-#180 character;
2. confirm all old stats/classes/saves/skills/PB/Quick Magic/slots/combat/equipment/currencies remain unchanged;
3. confirm new Background/Traits/Notes/spells start empty;
4. confirm caster toggle migration is correct for caster/non-caster examples.

## B. Navigation and PC Settings
5. verify seven/eight top-level tabs depending on caster setting;
6. verify horizontal tab scrolling and selected-tab visibility at 80/90/100/115/130;
7. verify rotation/recreation preserves selected tab;
8. disable spellcasting while on Conjuros -> return to General;
9. re-enable -> data restored, no forced jump to Conjuros;
10. confirm hide-not-delete message when appropriate.

## C. Run-#180 corrective backlog
11. General adjustment marker + speed dual-unit display;
12. Combat vertical alignment, IME safety, explicit editor dismissal, drag reorder;
13. Equipment IME safety, drag reorder, compact currencies, responsive columns, special-section presentation;
14. Habilidades `Por atributo` two-column behavior at 115/130;
15. Settings font/theme corrections and persistence.

## D. Trasfondo
16. edit/save/reopen all narrative fields;
17. verify two image placeholders participate correctly in responsive layout;
18. verify compact cards + larger Story area;
19. verify keyboard reachability and no silent outside-tap data loss.

## E. Rasgos
20. create multiple feature types/sources;
21. test activation field;
22. test usage max/spent/recovery;
23. drag reorder and reopen;
24. verify wide multi-column layout.

## F. Conjuros
25. create several sources including class-linked and custom;
26. rename/reorder/delete source and test fallback behavior;
27. delete/unlink linked class without losing source/spells;
28. create spells levels 0–9;
29. associate one spell with multiple sources;
30. verify `Todos` displays conceptual spell once;
31. verify prepared state differs by source;
32. verify search within current source view;
33. verify drag order persists;
34. verify Quick Magic and Conjuros slot pips remain synchronized both directions.

## G. Notas
35. use large general-notes area as an unrestricted scratchpad;
36. create/edit/delete/reorder titled notes;
37. test keyboard behavior and rotation/recreation.

## H. Final resilience
38. switch repeatedly among all available tabs without blank/crash/data loss;
39. screen off/on and full app close/reopen;
40. portrait/landscape recreation with unsaved in-progress editor state where supported;
41. verify no hidden spellcasting data is deleted after toggle OFF/ON;
42. verify all icon-only controls have usable touch targets and semantic descriptions.

---

# 5. Merge criterion

The next build is eligible to become the merge candidate only when:
- all planned implementation checkpoints are present;
- all CI gates are green;
- migration/data-preservation tests pass;
- final owner phone QA has no unresolved blocking defect;
- known non-blocking limitations are explicitly recorded;
- the exact tested commit and APK artifact are checkpointed.

---

# 6. Explicitly deferred from this build

Unless separately approved later, this package does **not** include:
- actual persistent character image attachment/storage (UI placeholders only);
- automatic D&D class/spell legality enforcement;
- automatic spell preparation limits;
- automatic rest restoration;
- automatic creation/synchronization of Combat entries from Traits/Spells/Equipment;
- multiple full Quick Magic casting profiles;
- automatic spellcasting-source inference from class rows;
- complex Notes metadata/tagging system;
- swipe-between-tab page navigation.

---

# 7. Approval gate

If the owner approves this package, production coding may begin with Increment A, and every increment must be checkpointed before dependent work advances.

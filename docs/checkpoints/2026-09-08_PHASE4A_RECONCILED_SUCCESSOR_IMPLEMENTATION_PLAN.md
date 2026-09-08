# Phase 4A — reconciled successor implementation plan

**Date:** 2026-09-08  
**Status:** PLAN COMPLETE; ready to create focused product branch from canonical `main`  
**Canonical baseline:** `main`  
**Latest technically verified product:** `0.4.0-preqa.7` / build `40700` / debug  
**Product code changed by this checkpoint:** no

## 1. Purpose

This plan reconciles three controlling inputs into one successor development sequence:

1. the Stage A–F owner phone audition findings from build `40700`;
2. the cross-cutting `Fuente` / provenance audit;
3. D-0067 plus the six owner model decisions resolved on 2026-09-08.

The goal is not to patch screens independently. The successor cycle must repair shared foundations first, then migrate domain state, then rebuild surfaces on those foundations, and only then produce a new owner-audition build.

The plan remains within Phase 4A. It does not begin DM implementation and does not claim owner acceptance.

## 2. Protected product rules

The following are controlling throughout all increments:

- one datum, one canonical state;
- physical phone landscape remains a phone interaction model;
- tablet/wide is a separate design problem and must be redesigned rather than reused as the phone-landscape fallback;
- fixed/sticky controls must earn their permanent footprint;
- at ordinary scale, information/controls that clearly fit on one row should not be split across several rows;
- app-wide excessive padding/margins are repaired through shared spacing/layout primitives where possible;
- reorderable cards favor whole-card long-press/drag where interaction safety permits, with stronger movement feedback and without large dedicated move rows;
- editors must remain usable with the IME visible and through practical orientation changes;
- `Raza`, `Electrum`, Spanish class/subclass names and `Conocimiento Arcano` are controlling visible terminology;
- help uses the coherent `Siempre visible` / circled-`i` / `Oculto` system;
- generic `Fuente` is not exposed merely because a backing field exists;
- official-existing-vs-custom flows remain conditional on an actually available licensed corpus;
- no automatic rules enforcement is introduced merely to support structured data.

## 3. Dependency graph

The critical dependency chain is:

```text
Ability reference abstraction
    ├─> custom attributes
    ├─> custom skills
    ├─> optional custom saves
    ├─> dice targets
    └─> per-source spellcasting

Structured attack damage
    ├─> compact Combat attack cards
    └─> damage rolling

Shared trackable-value mechanics
    ├─> Resources extensions
    ├─> Custom Markers
    ├─> cross-domain recovery
    └─> General/Gestión/Equipo/Rasgos projections

Responsive/layout primitives
    ├─> phone portrait
    ├─> phone landscape
    ├─> tablet portrait
    └─> tablet landscape

App-owned media storage
    ├─> Trasfondo images
    └─> backup/import media survival
```

These dependencies determine the implementation order below.

## 4. Increment A — schema/domain/storage foundation

**Purpose:** establish authoritative data shapes before rebuilding UI around temporary compatibility state.

### A1 — generalized ability references + custom attributes

Introduce a serializable ability reference abstraction capable of addressing either:

- one of the six built-in `CharacterAbility` values; or
- a custom attribute by durable ID.

Add durable custom-attribute records with at least:

- ID;
- name;
- abbreviation;
- score;
- sort order;
- optional saving-throw configuration;
- optional notes only if they provide real utility without bloating normal UI.

The default modifier is the normal D&D ability-score modifier formula.

Migrate consumers that need extensibility away from direct closed-enum ownership, especially:

- `CharacterCustomSkill.ability`;
- spellcasting-source ability;
- Dice target resolution;
- optional custom saving throws.

Do **not** destabilize standard `SkillKey` -> built-in ability associations unnecessarily. Standard D&D skills can remain bound to standard built-in abilities; custom skills use the generalized reference.

### A2 — per-source spellcasting authority

Extend `CharacterSpellcastingSource` so each source owns:

- casting ability reference;
- optional DC adjustment;
- optional spell-attack adjustment;
- existing linked-class identity and manual source ordering.

Derived values:

- `CD salv. conjuro = 8 + ability modifier + proficiency + source adjustment`;
- `Mod. ataque mágico = ability modifier + proficiency + source adjustment`.

Class-linked sources may receive class-appropriate defaults/suggestions but remain editable/permissive.

Migrate the existing global `spellcastingAbility`, `spellSaveDc`, and `spellAttackModifier` compatibility state safely. Do not discard existing user values; define deterministic migration/fallback behavior and test it.

### A3 — structured attack damage components

Replace sole reliance on opaque `damageEffect: String` with a durable ordered component list. At minimum support:

- dice expression + optional damage/effect type;
- flat numeric modifier;
- free effect text only where it cannot be represented numerically.

Preserve compatibility with existing attacks by migrating the old opaque text into a safe legacy/custom component when it cannot be parsed confidently. Do not guess user intent from arbitrary strings.

The same component list becomes the source for:

- Combat card damage summary;
- damage-roll selection/execution.

### A4 — shared trackable-value primitive without collapsing semantics

Custom Markers and Resources remain separate user-facing/domain concepts, but share reusable mechanics where appropriate.

Define shared value/recovery semantics that can represent:

- binary state;
- integer counter;
- current/max counter;
- no recovery;
- short rest;
- long rest;
- short or long rest;
- manual recovery;
- fixed recovery amount or recover-to-max where meaningful.

Resources additionally receive controlled display-placement metadata, allowing the same canonical resource to be projected in relevant tabs such as General, Gestión, Equipo or Rasgos.

Custom Markers are configured from PC Settings and retain their own durable collection/semantic identity.

### A5 — compact valuables + media persistence fields

Add the smallest durable state needed for:

- `Gemas / arte` compact free-form valuables text;
- two persistent Trasfondo image references backed by app-owned storage metadata rather than fragile transient external URI dependence.

The storage design must participate in own-format backup/import. Imported characters must receive independent media ownership/references rather than pointing back to mutable source-character media.

### A6 — migration / backup compatibility gate

Before broad UI work:

- add focused serialization/repository tests for all new fields;
- test loading pre-migration character data;
- test own-format export/import round-trip;
- test repeated import creates independent copies;
- test old attack damage survives safely;
- test existing global spellcasting values map predictably into source-based state;
- test custom/resource state remains intact.

**Checkpoint boundary A:** domain/schema/storage green before using new structures broadly in UI.

## 5. Increment B — shared UX and responsive primitives

**Purpose:** repair recurring interaction/layout families once before rebuilding surfaces.

### B1 — form-factor-aware shell

Replace width-only assumptions with an explicit layout-context decision that distinguishes at least:

- phone portrait;
- phone landscape;
- tablet portrait;
- tablet landscape.

Physical phone landscape must never enter the tablet interaction composition merely because width crosses a dp threshold.

Preserve selected tab and practical scroll/context across rotation where feasible.

### B2 — spacing/density primitives

Centralize the successor spacing scale so the 40% option can be auditioned consistently.

Audit shared paddings for:

- cards;
- fixed bars;
- collection rows;
- buttons;
- dialogs/editors;
- list boundaries.

Do not blindly multiply all minimum touch targets down with visual spacing. Visual density and reliable tap targets are separate concerns.

### B3 — shared compact toolbar pattern

Create one collection-toolbar primitive that supports:

- search;
- active filter count/state;
- sort/order;
- Add;
- optional contextual selector;
- single-row compact default presentation;
- expansion/transient controls when width or accessibility scale requires it.

Use it to prevent each long collection from rebuilding a different multi-row fixed header.

Conjuros is the highest-risk proving surface for this primitive.

### B4 — shared card manipulation

Create consistent reorder behavior:

- whole-card long-press/drag where safe;
- nested buttons/text inputs excluded from drag initiation;
- visible pickup/movement/drop state;
- haptic feedback routed through configurable haptic settings;
- manual order only;
- drag disabled in A–Z/filtered/search contexts when reordering would be ambiguous.

### B5 — editor/IME foundation

Repair the shared editor family so required save/cancel/actions remain reachable with keyboard visible.

Requirements:

- scrollable body when needed;
- actions outside keyboard-obscured content;
- numeric replacement behavior does not trap leading zero;
- active draft survives reasonable orientation change when Compose state can safely do so;
- outside-tap keyboard dismissal where appropriate without accidentally discarding edits.

### B6 — help and provenance primitives

Implement reusable circled-`i`/help presentation honoring global mode:

- Siempre visible;
- tooltip/info;
- Oculto.

Implement a compact provenance control only where provenance has user-facing value:

`Tipo de origen | Origen específico`

Default origin type: `Clase` where applicable. Allow `Otro`/custom path. Do not add this on top of functional spellcasting-source associations in Conjuros.

**Checkpoint boundary B:** shared primitives demonstrably solve representative density/IME/drag/responsive behavior before broad tab conversion.

## 6. Increment C — navigation, PC Settings and General identity/state

### C1 — character-first application entry

Change startup from campaign-first to character-first.

Initial list shows at least:

- character name;
- Raza;
- class(es) + levels;
- campaign.

Campaign administration remains accessible but secondary. List rows project canonical character/campaign data; do not persist a second summary model.

### C2 — PC Settings information architecture

Move `Configuración de la aplicación` entry near the upper portion.

Add/configure:

- tab order;
- custom attributes;
- custom skills;
- Inspiration visibility;
- Custom Markers;
- haptic strength/duration;
- existing conditional-module visibility without hide-delete regression.

### C3 — General compact identity

Rebuild compact class presentation:

- Spanish class/subclass names;
- class level clearly visible;
- maximum hit dice derived from class level rather than repeated as redundant state;
- remaining hit dice still operational.

### C4 — General reference/state projections

Surface from canonical data:

- `Raza`;
- visible `Idiomas`;
- Defensas with current AC and equipped armor/shield references;
- Inspiration when enabled;
- enabled Custom Markers;
- Resources configured for General placement;
- custom attributes;
- per-source compact `Lanzamiento de Conjuros` rows.

Recommended casting row:

`Mago (INT) | CD 15 | Ataque +7`

Use one shared formula help affordance rather than permanent explanatory paragraphs.

### C5 — Habilidades

- custom skills no longer use a separate visual box;
- render inline with normal skills using italic label styling;
- `Por habilidades` sorted alphabetically by displayed Spanish name;
- use `Conocimiento Arcano`, compact abbreviation only when width actually requires it;
- passive references remain sticky because owner says they earn the space, but make `pasiva` explicit and use one horizontal row when possible.

**Checkpoint boundary C:** owner identity/configuration and skill semantics coherent on phone portrait before proceeding to operational surfaces.

## 7. Increment D — Combat + Dice as one structured interaction family

### D1 — compact attack cards

Use hierarchy:

1. `Nombre (+bono)`;
2. damage summary from structured components;
3. action type;
4. short notes preview when present;
5. compact grouped actions.

Use shared card manipulation and density primitives.

### D2 — character-aware Dice selector

Replace the current browse-heavy dice presentation with one compact control flow:

1. normal / ventaja / desventaja;
2. roll category;
3. concrete target from canonical character data;
4. roll action.

Eligible targets include:

- built-in attributes;
- custom attributes;
- standard saving throws;
- enabled custom saving throws;
- standard skills;
- custom skills;
- attacks;
- source-specific spell attacks if/when exposed as a roll target;
- custom roll path.

### D3 — result modes and damage roll

Application setting chooses:

- animated/visible dice;
- compact result box.

Both show decomposition.

Damage roll consumes the same structured attack components from A3. No second damage model.

**Checkpoint boundary D:** attack editing, attack display, d20 selection and damage rolling all agree on the same canonical structured data.

## 8. Increment E — Gestión, Markers, Resources and recovery

### E1 — compact operational state

Reduce fixed `Estado operativo` height materially.

Death saves become a compact horizontal control when applicable. Repair General/Gestión one-state coherence so saved character state cannot disagree merely because two tabs expose separate drafts.

### E2 — Inspiration and Custom Markers

General and Gestión project the same canonical Inspiration/Marker state.

Markers use compact binary/counter controls appropriate to their configured type.

### E3 — Resources across domains

Extend Resource editing with the shared tracker/recovery capabilities and controlled tab placements.

Representative examples:

- ammunition -> Equipo;
- class-feature uses -> Rasgos;
- generic rest resource -> Gestión;
- especially important resource -> General + Gestión.

The same resource ID/value appears everywhere it is projected.

### E4 — rest integration

Gestión rest preview/apply collects eligible recoverable state across participating domains rather than only objects created inside Gestión.

Automatic application is limited to explicit structured recovery rules. Free/manual text remains review-only.

### E5 — conditions and concentration

Build predefined condition catalog infrastructure with source identity + circled-`i` help.

- supported official/SRD condition set only when corresponding approved corpus is actually available;
- architecture supports Sandy Petersen's Cthulhu Mythos for D&D 5e;
- exact proprietary Spanish descriptions remain deferred until owner-provided/project-appropriate text exists;
- custom conditions remain available.

Concentration helper shows/explains the applicable check/DC calculation without hiding the rule source/assumption.

**Checkpoint boundary E:** live state + rest behavior is canonical across tabs and can be regression-tested independently of collection aesthetics.

## 9. Increment F — Conjuros compact source-context redesign

This increment intentionally occurs after per-source casting data and the shared compact toolbar exist.

### F1 — one sticky context bar

Replace the current persistent source-selector row + separate persistent `Conjuros` toolbar card with **one compact sticky source-context bar**.

Selected-source concept:

`Mago (INT) · CD 15 · Ataque +7   ▾   Buscar   Filtros 2   +`

The precise responsive icon/label balance may change during device audition; the protected requirements are:

- source/class identity is clear;
- ability/DC/attack belong visibly to that source;
- source selection remains immediately reachable;
- search/filter/sort/Add remain immediately reachable;
- the default permanent footprint is one compact row at normal phone scale where feasible.

### F2 — `Todos` behavior

When all sources are displayed:

- bar says `Todos los conjuros`;
- do not show all casting-stat rows permanently;
- individual spells may show small source association labels only where useful;
- selecting a source reveals that source's casting context.

### F3 — transient detail/filter surfaces

Expanded filters, full source details and formula explanations open transiently/collapsibly rather than becoming multiple permanent rows.

### F4 — level headers and spell cards

Retain compact sticky level/slot headers because they support navigation/operation. Apply global card/action/reorder compaction.

### F5 — phone landscape acceptance condition

The spell list must remain visibly usable in phone landscape. If the context bar plus current sticky level header can still erase practical content, reduce/reflow the bar rather than falling back to tablet UI.

**Checkpoint boundary F:** Conjuros must receive an early owner phone portrait + landscape targeted audition because it was the strongest Stage F fixed-footprint failure.

## 10. Increment G — Equipo, Rasgos, Notas and Trasfondo collection/content repairs

### G1 — Equipo

- preserve sticky toolbar because owner says it earns its space;
- clarify and compact Consumible/Munición semantics;
- `Electrum` terminology;
- add compact `Gemas / arte` free-text box;
- show Resources configured for Equipo placement;
- surface currently equipped armor/shield state consistently with General/Defensas;
- use shared drag/card/action patterns.

### G2 — Rasgos and conditional modules

- consolidate `Fuente`/`Tipo` ambiguity using the structured provenance model only when both dimensions have real meaning;
- show Resources configured for Rasgos placement;
- preserve conditional hide-not-delete behavior;
- apply shared compact toolbar/card interaction patterns across Artífice, Formas, Técnicas, Metamagia, Pactos and Compañeros.

### G3 — Notas

Add search/filter while preserving manual order unless the user explicitly selects a presentation order that changes view ordering.

### G4 — Trasfondo

Replace placeholders with actual persistent images using app-owned storage. Verify reopen/restart and backup/import independence.

**Checkpoint boundary G:** long collections and content domains use the same compact interaction grammar rather than bespoke fixes.

## 11. Increment H — Application Settings full-screen redesign

Move Application Settings from modal dialog to navigable page/screen.

Implement:

- text-size stepped slider + explicit percentage/value + live preview;
- spacing compactness stepped slider + explicit value + live preview, including 40%;
- visual mini-grid previews for column settings;
- normal font selection with immediate preview, no provider/origin text in normal UI and no special `audición` framing;
- help-mode setting;
- dice result-presentation setting;
- bounded understandable haptic strength/duration choices, with graceful device-capability fallback;
- theme renames;
- six new audition themes: Carmesí, Ámbar, Glaciar, Lavanda, Pizarra, Terracota.

Theme implementation must preserve readable contrast. Exact colors remain owner-audition-adjustable.

**Checkpoint boundary H:** global settings can be exercised without modal-space constraints and preview the exact density/text/theme choices that affect later device QA.

## 12. Increment I — tablet/wide redesign after phone primitives stabilize

Do **not** begin by stretching the current tablet design.

Use the same canonical domain state and shared controls, but design tablet portrait/landscape as first-class compositions.

Principles:

- extra width should increase useful simultaneous context, not create permanent empty editor panes;
- master/detail is used only where both regions remain useful without starving the primary list;
- no persistent secondary pane merely because space exists;
- rotation preserves practical tab/list/search/filter/editor context;
- large text remains operable.

This increment should reuse phone-proven primitives but make independent layout decisions.

**Checkpoint boundary I:** emulator/layout inspection first; physical owner tablet acceptance remains pending until a device is available.

## 13. Automated verification strategy

### Per-increment focused tests

Add/update tests alongside each schema/domain behavior, especially:

- ability-reference resolution and modifier math;
- custom-skill totals;
- optional custom saving throws;
- per-source spellcasting DC/attack calculations and migration;
- structured damage preservation/roll decomposition;
- resource/marker tracker operations;
- rest recovery across domains;
- character-first list projection;
- source/tab ordering;
- search/filter/manual-order invariants;
- backup/import media/value independence.

### Full gate at coherent product boundaries

Use the established full gate:

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

and:

```bash
cd backend
npm install --no-package-lock
npm run check
```

At minimum run the full gate after A, B/C, D/E/F, G/H/I consolidation and before producing the successor owner-audition APK. Smaller intermediate checks may be used but cannot substitute for the final full gate.

## 14. Owner real-device retest strategy

Do not repeat the old 40700 audition screen-by-screen.

### Early targeted phone retest after F

On Redmi Note 11 Pro 5G:

- Conjuros portrait: one compact context bar, practical spell viewport;
- Conjuros landscape: spells visibly usable;
- representative editor with keyboard;
- representative reorderable card;
- Gestión death saves/operational area;
- Habilidades passive row;
- 40% spacing option.

### Consolidated successor audition

After G/H and responsive integration:

- character-first start surface;
- General/PC Settings custom attributes/markers/tab order;
- Combat + Dice structured flow;
- Resources projected in at least two tabs from one state;
- persistent Trasfondo image reopen;
- Notes search;
- Application Settings live previews/themes;
- representative conditional module;
- phone portrait + phone landscape;
- representative larger text scale.

### Tablet

Treat tablet as redesigned-surface acceptance, not confirmation of the old wide layout. Physical owner tablet testing remains required before final Phase 4A acceptance.

## 15. Build / checkpoint boundaries

Recommended successor build sequence:

1. **Foundation build** after A + enough UI wiring to validate migration in development, not owner visual candidate;
2. **Interaction build** after B/C/D/E/F, suitable for targeted owner phone retest of the highest-risk flows;
3. **Successor audition build** after G/H/I integration and full automated gate;
4. only after owner-targeted repairs and phone/tablet baseline acceptance, freeze a **new formal M6 candidate**.

Do not reuse or mutate old frozen candidates.

Exact version/build numbers are assigned during implementation/build work, not guessed in this planning document.

## 16. Deferred / conditional items

Do not silently expand this cycle into:

- complete SRD ingestion;
- proprietary Cthulhu Mythos text transcription/scraping;
- automatic legal character-builder enforcement;
- automatic AC rules engine beyond available structured data;
- broad DM features;
- emulator farm / enterprise CI infrastructure.

Official-existing-vs-custom selectors may be wired only for domains where an approved corpus is actually available.

## 17. Branch and execution rule

This plan is now coherent enough to satisfy the pre-branch reconciliation gate.

Next action:

1. create a focused implementation branch from canonical `main`;
2. begin with **Increment A**, not with visual one-off patches;
3. leave `main` as canonical baseline until a later reviewed/accepted integration point;
4. checkpoint meaningful increments in Git;
5. do not begin DM work.

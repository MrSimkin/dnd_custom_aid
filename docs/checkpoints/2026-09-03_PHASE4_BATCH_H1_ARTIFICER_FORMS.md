# Phase 4 — Batch H1 Artífice + Formas closure checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** PENDING FULL GATE  
**Canonical `main`:** untouched

## Scope

H1 implements the first two approved reusable conditional class/subclass modules over the already-existing durable shared model:

- `Artífice`;
- `Formas`.

No schema or migration is introduced.

The controlling ownership audit is:

- `docs/checkpoints/2026-09-03_PHASE4_BATCH_H1_ARTIFICER_FORMS_AUDIT.md`;
- audit commit `35d90e31f47d6661e94b5fbf79ce97f4a9a9a315`.

## H1a — pure presentation and collection operations

Implementation:

- `52350ecc5107b6603129f250a643bc2e64f88ff4` — Artifice + Forms presentation/filter/order/duplicate operations;
- `2828dfd9fbfc57b1931f7a6170d740191bae1c2c` — focused H1 operation tests.

Delivered semantics:

### Artífice

- projects only `ARTIFICER_PLAN` and `ARTIFICER_DEVICE` from the shared global `classOptions` collection;
- search across name/source/reference/effect/notes;
- filters Plan, Device, Active and Favorite;
- Manual/A–Z presentation where A–Z never rewrites stored manual order;
- manual drag semantics move only Artifice-visible positions while preserving hidden H2-family positions in the shared `classOptions` sequence;
- duplicate receives a fresh UUID and preserves reference state;
- next sort order accounts for all shared class-option families, including hidden later-module data.

### Formas

- search across name/source/CR/movement/senses/actions/notes;
- source and Favorite filters;
- Manual/A–Z presentation without rewriting manual order;
- safe manual movement;
- duplicate with fresh UUID and preserved human-reference fields.

H1a verification:

- workflow `33824278439` — PASS;
- backend type-check — PASS;
- shared/Kotlin tests — PASS;
- Android debug assembly — PASS;
- Desktop build — PASS;
- APK upload — PASS;
- artifact `dnd-custom-aid-debug-apk`, ID `9919431126`;
- digest `sha256:00319542202d599e50725f74c9a47dbfa1221931baba9628e48a3c12ac6c178f`.

## H1b — Android conditional-module surfaces

New files:

- `CharacterConditionalModuleDraftCodecV4.kt` — temporary structural draft for full `classOptions` + `forms`;
- `CharacterArtificeModuleV4.kt` — Artífice list/editor surface;
- `CharacterFormsModuleV4.kt` — Formas list/editor surface.

Primary commits:

- `b6d38028d09771cb2fa8507f06af10bfe46957c8` — H1 draft codec;
- `6028bbdc7db5d92f86a5138bdd406c4b44537a33` — Artífice surface;
- `253e3c2e41265985e9a4d1d6463001e73c31cfbf` — Formas surface;
- `c126c9b69eed9762fe78dfb3f6a50e29ce525587` — conditional Artífice/Formas navigation;
- `aa6fa34d318fde31f433c5aef15a3c322dcf1483` — guarded CharacterEditor integration.

### Artífice surface

- list is a projection over the complete structural `classOptions` draft rather than a separate Artificer store;
- Plan / Device / Active / Favorite filters;
- search and independent Manual/A–Z presentation;
- visible drag feedback + haptics only in unfiltered Manual mode;
- source/provenance and optional linked character-class identity visible;
- compact Plan/Device and Active/Inactive state badges;
- duplicate and exact named delete;
- row tap is the edit interaction; no generic Edit button;
- editor supports name, Plan/Device, optional linked class, source, cost/reference, effect summary, notes and active state;
- phone uses the approved IME-safe modal editor;
- wide/tablet uses persistent master-detail editing;
- generic Resources/Spells/Equipment/Combat/Traits/Companions remain separate owners and are not duplicated into Artífice.

### Formas surface

- search, Favorite and source filters;
- independent Manual/A–Z presentation;
- visible drag feedback + haptics only in unfiltered Manual mode;
- compact CR/CA/PG badges plus source/movement/senses/action preview;
- duplicate and exact named delete;
- row tap edits;
- editor supports name, source, CR/reference, optional non-negative AC/HP, movement, senses, action/reference summary and notes;
- phone uses IME-safe modal editing;
- wide/tablet uses master-detail;
- opening or editing a form does not apply it to the base character sheet and does not act as a creature-rules engine.

## Conditional navigation

`CharacterNavigationV4` now includes H1 destinations only:

- `Artífice` appears iff `CharacterModuleKind.ARTIFICER` is visible;
- `Formas` appears iff `CharacterModuleKind.FORMS` is visible.

Visibility uses the existing `visibleCharacterModules(classes, moduleOverrides)` union/override logic:

- official/current recognized classes/subclasses can suggest modules;
- multiclass union produces one destination per module, never duplicates;
- manual `FORCE_SHOW` exposes a module for custom/homebrew characters;
- `FORCE_HIDE` removes the destination without deleting data;
- if the selected module becomes hidden in PC Settings, returning to the sheet resolves safely to General;
- H2/H3 module destinations remain absent until their actual UI batches are implemented.

## Structural draft / Save integration

H1 joins the existing global unsaved-change model:

- the temporary H1 draft stores **all** `classOptions`, not only Artifice-owned kinds, plus all `forms`;
- therefore H1 cannot silently discard later H2-family class-option data already present on a character;
- H1 mutations participate in `Cambios sin guardar` and Save / Discard / Keep editing behavior;
- global Save merges `classOptions` + `forms` into the authoritative `CharacterSheet`;
- successful Save resets the H1 draft from repository truth.

Quick Access behavior:

- Artífice uses `CharacterQuickAccessKind.CLASS_OPTION`;
- Formas uses `CharacterQuickAccessKind.FORM`;
- Favorite is disabled for newly-created or duplicated IDs until the record exists durably;
- deleting a durable favorite in the structural draft does not immediately mutate Quick Access;
- only successful global Save prunes `CLASS_OPTION` / `FORM` references whose durable targets no longer exist;
- discarding the structural deletion therefore preserves the favorite reference.

## Full gate required

H1 is not GREEN until the controlling workflow on this checkpoint head passes:

- focused H1 operations tests;
- existing class/subclass/module visibility tests;
- existing full repository round-trip including Artificer option + Form + Companion representative state;
- all shared desktop tests and migrations;
- Android debug assembly of Artífice/Formas phone + wide surfaces and conditional navigation;
- Desktop build;
- backend type-check;
- APK artifact upload.

If the gate is green:

1. record exact workflow/artifact/digest here;
2. mark H1 GREEN;
3. update `docs/PROJECT_STATE.md` to **H2 — Técnicas + Metamagia + Pactos active**;
4. do not begin H2 implementation before this gate is known.

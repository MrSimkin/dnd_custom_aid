# Phase 4 — Batch H1 Artífice + Formas state/ownership audit

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** AUDIT COMPLETE — IMPLEMENTATION MAY PROCEED  
**Canonical `main`:** untouched

## Purpose

H1 implements the first two approved reusable conditional character modules: **Artífice** and **Formas**. This checkpoint records the required shared-state audit before any H1 UI implementation.

The audit compares:

- D-0047 approved conditional-module architecture;
- `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md`;
- current `CharacterSheet` durable domain;
- `CharacterRepository` persistence/normalization;
- `CharacterClosureState` / module visibility and PC Settings overrides;
- current Android character navigation/shell wiring;
- existing shared regression tests.

## 1. Audit result — no H1 schema addition required

The durable domain already contains everything required for the approved H1 scope.

### Artífice durable owner

`CharacterSheet.classOptions` already persists `CharacterClassOption` with:

- stable ID;
- optional linked class ID;
- `CharacterClassOptionKind`;
- name;
- source/provenance;
- cost/reference text;
- effect summary;
- notes;
- active state;
- legacy `pinned` state;
- manual sort order.

The existing option-kind enum already contains the two Artifice-specific categories required by H1:

- `ARTIFICER_PLAN`;
- `ARTIFICER_DEVICE`.

It also contains generic option families for later H2 work. H1 therefore must **reuse `classOptions`** rather than creating Artificer-specific tables.

### Formas durable owner

`CharacterSheet.forms` already persists `CharacterForm` with:

- stable ID;
- name;
- source/provenance;
- optional challenge-rating/reference text;
- optional AC;
- optional HP;
- movement;
- senses;
- action summary;
- notes;
- legacy `pinned` state;
- manual sort order.

This is deliberately a human-useful alternate-form reference, not a full creature-stat/rules engine. It matches the approved Forms boundary, so H1 must **reuse `forms`** without schema expansion.

### Existing adjacent owners remain authoritative

H1 must not duplicate data already owned elsewhere:

- Artificer counters/charges -> generic `Resources` / Gestión;
- actual spells or spell slots -> Conjuros;
- ordinary/special created items that are inventory objects -> Equipo;
- attacks/actions -> Combate;
- prose class/subclass features -> Rasgos;
- Steel Defender, Eldritch Cannon as tracked entity, Reanimated Companion, etc. -> `Companions` in H3;
- live combat-participant state -> future DM/combat state, not durable H1 sheet modules.

Artífice therefore represents the persistent **plan/device/invention choice library** and concise Artifice-specific reference state that does not have a better existing owner.

## 2. Persistence already exists and is protected

`CharacterRepository.saveCharacter()` already validates and persists `classOptions` and `forms`.

Relevant behavior already present:

- IDs must be distinct;
- names must be nonblank;
- `CharacterClassOption.linkedClassId` is soft-normalized against currently existing character classes rather than making class removal destructive;
- Form AC/HP cannot be negative;
- both domains preserve explicit sort order through repository round trip.

`CharacterClosureFoundationTest.classSubclassProvenanceAndNewDomainsRoundTrip()` already proves representative Artificer class-option, Form and Companion persistence in one durable character round trip.

No H1 migration is justified.

## 3. Module visibility infrastructure already exists

`CharacterModuleKind` already defines:

- ARTIFICER;
- FORMS;
- TECHNIQUES;
- METAMAGIC;
- PACTS;
- COMPANIONS.

`suggestedCharacterModules()` / `visibleCharacterModules()` already implement:

- class + subclass suggestion union across multiclass;
- localized-name fallback for manual/older class records;
- PC Settings overrides;
- AUTO / FORCE_SHOW / FORCE_HIDE;
- hide-without-delete semantics.

Existing tests already cover:

- multiclass union;
- manual localized-class fallback;
- FORCE_SHOW/FORCE_HIDE precedence;
- returning an override to AUTO;
- visibility changes not mutating unrelated closure state.

PC Settings already exposes all six module families with Spanish labels and automatic/manual visibility controls.

## 4. Current Android gap

The main character sheet does **not** yet expose conditional module destinations.

`CharacterTopTabStripV4` currently contains only the base tabs:

- General;
- Habilidades;
- Combate;
- Gestión;
- Equipo;
- Trasfondo;
- Rasgos;
- conditional Conjuros;
- Notas.

`CharacterDomainShellV4` is only a generic placeholder shell and there is no implemented Artífice/Formas editing surface.

Therefore H1 is primarily:

1. pure list/presentation operations over the existing durable collections;
2. Artífice UI over `classOptions` filtered to Artifice-owned kinds;
3. Formas UI over `forms`;
4. conditional navigation wiring driven by `visibleCharacterModules()`;
5. integration into the existing global unsaved-draft/Save model;
6. Quick Access integration using the existing `CLASS_OPTION` and `FORM` reference kinds.

## 5. H1 ownership decisions

### Artífice module content

H1 Artífice owns entries whose `CharacterClassOptionKind` is:

- `ARTIFICER_PLAN`;
- `ARTIFICER_DEVICE`.

It does **not** absorb generic `SUBCLASS_STATE` merely because an Artificer subclass exists. A subclass-specific datum should remain in Resources, Equipment, Traits, Spells, Combat or Companions when one of those domains is the natural owner. Future truly orphaned Artifice state can be represented using the existing class-option model without schema redesign.

An Artifice entry may link to a specific character class row or remain unlinked for custom/homebrew/manual use.

### Formas module content

H1 Formas owns the character's reusable `CharacterForm` library. It does not infer legality, calculate creature blocks, validate CR eligibility or automatically overwrite the character's normal AC/HP/movement.

A form is durable reference data. Selecting/opening a form in H1 is not equivalent to applying a live transformation to the authoritative base sheet.

## 6. H1 interaction requirements

Both H1 lists should follow already-green closure conventions:

- compact contextual toolbar;
- search;
- Manual / A–Z presentation, where A–Z never rewrites stored manual order;
- visible drag feedback + haptics only in unfiltered Manual view;
- duplicate with fresh UUID;
- named delete confirmation;
- source/provenance visible;
- ★ Quick Access using `CharacterQuickAccessKind.CLASS_OPTION` / `FORM`;
- Favorite disabled for newly-created/duplicated unsaved records so closure state cannot point at non-durable IDs;
- Quick Access references for deleted records pruned only after successful global Save;
- phone uses IME-safe modal editing;
- wide/tablet uses master-detail where the list/editor density benefits;
- D16 list/query context preserved while editing.

Artífice additionally offers a simple Plan / Device filter and compact active-state presentation. It must not create a second resource counter system.

Formas presents its human-reference fields compactly and progressively discloses full action/notes detail.

## 7. Navigation decision for H1

H1 adds **conditional top-level module destinations** for Artífice and Formas because D-0047 explicitly defines reusable module tabs/surfaces and requires no duplicate module tabs.

Visibility is computed from `visibleCharacterModules(settingsSheet.classes, closureState.moduleOverrides)`:

- Artífice destination appears iff ARTIFICER is visible;
- Formas destination appears iff FORMS is visible;
- manual FORCE_SHOW can expose either for custom/homebrew characters;
- FORCE_HIDE removes the destination without deleting its draft or durable data;
- if the currently selected module becomes hidden, navigation resolves safely to General;
- H2/H3 will extend the same navigation pattern for the remaining module families rather than introducing a second mechanism.

This H1 step must not expose empty placeholder destinations for H2/H3 modules before those batches implement their UI.

## 8. Save/draft integration

Artifice/Form changes are structural sheet edits, not live operational counters. They therefore join the existing global unsaved-change model rather than auto-saving every structural mutation.

H1 will maintain temporary editor drafts for:

- Artifice-owned `classOptions` while preserving non-Artifice class options already present in `stored.classOptions`;
- `forms`.

On successful global Save:

- edited H1 collections are merged into the authoritative `CharacterSheet`;
- non-Artifice class options remain untouched;
- `CLASS_OPTION` and `FORM` Quick Access references whose durable targets were deleted are pruned;
- newly saved records become eligible for Favorite actions;
- unsaved-leave guard sees H1 modifications.

## 9. Gate plan

### H1a — pure operations

Add focused shared helpers/tests for:

- Artifice projection;
- search/filter;
- Manual/A–Z presentation;
- duplicate;
- safe manual move;
- Forms search/presentation/duplicate/move;
- manual order preservation.

Run full standard CI gate before Compose wiring.

### H1b — Android + navigation integration

Implement:

- Artífice surface;
- Formas surface;
- conditional H1 navigation;
- global Save/dirty/Quick Access integration;
- phone + wide master-detail behavior.

Then run the full standard gate and record APK identity.

## Audit conclusion

**No schema change is required for Batch H1.**

The existing shared model is already intentionally shaped for these modules. The correct implementation is to reuse it, add tested presentation operations, and expose it through the approved conditional UI architecture without duplicating Resources, Equipment, Traits, Spells or Companion responsibilities.
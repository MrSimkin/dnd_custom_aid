# Phase 4 — Batch H3 Compañeros ownership and field audit

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** IMPLEMENTATION INPUT — D-0047 APPROVED SCOPE  
**Canonical `main`:** untouched

## Purpose

Confirm whether the existing durable companion model is sufficient for the approved reusable `Compañeros` character module and define the boundary between character-owned companion data and future DM live-combat state before implementation.

## Product boundary

`Compañeros` represents recurring entities owned/referenced by a character: beasts, constructs, spirits, familiar-like entities, persistent created entities and custom/homebrew companions whose state deserves more than prose in Rasgos.

It is **not** a DM initiative/combatant store.

A future DM combat tracker may project/copy/reference a companion into live encounter state, but combat must not directly turn the durable character-sheet companion record into the encounter authority.

## Existing durable model

`CharacterCompanion` already persists:

- UUID;
- optional linked character-class ID;
- name;
- freeform kind/type;
- source/provenance;
- optional armor class;
- optional maximum HP;
- optional current HP;
- temporary HP;
- speed text;
- ability/stat summary text;
- senses/proficiencies summary text;
- traits/actions text;
- notes;
- active state;
- manual sort order.

Repository validation already guarantees:

- nonblank name;
- non-negative AC when present;
- non-negative maximum/current/temp HP;
- invalid/deleted linked class IDs are soft-unlinked rather than deleting the companion.

Existing repository round-trip coverage already proves companion persistence at a representative level.

## Field sufficiency conclusion

**The existing model is sufficient for H3. No SQL schema migration is required.**

Reasons:

- CA and HP cover the most commonly consulted durable live-reference values;
- freeform speed, ability summary, senses/proficiencies and traits/actions keep the model permissive across 5e, 5.5e, supplemental and homebrew companions;
- forcing six ability scores, saves, skills, attack objects or a full monster stat block would prematurely turn this module into a creature/rules engine;
- `kind` is intentionally freeform because companion categories vary widely;
- Favorites do not require a `pinned` column because authoritative Favorite/Quick Access state already uses `CharacterQuickAccessKind.COMPANION`;
- encounter initiative, turn state, conditions in a particular fight and temporary encounter-only effects belong to future DM live combat state, not this durable record.

## Official/module triggers

Use the already-approved catalog/module audit and current `CharacterModuleKind.COMPANIONS` suggestions. Representative existing triggers include:

- Artificer Artillerist;
- Artificer Battle Smith;
- Artificer Reanimator;
- Druid Wildfire;
- Ranger Beast Master;
- Ranger Drakewarden;
- Warlock Vestige Patron;
- Wizard Necromancer (Arcana Unleashed);
- custom/homebrew via manual PC Settings override.

The catalog is convenience metadata, not legality enforcement.

## Module-union rules

H3 must explicitly verify the complete conditional-module behavior established across H1/H2:

1. visible modules are the union of all relevant class/subclass suggestions;
2. multiple triggers for `COMPANIONS` still create only one `Compañeros` destination;
3. H1/H2 modules remain visible alongside Companions when also triggered;
4. manual `FORCE_SHOW` exposes Companions for custom/homebrew characters;
5. manual `FORCE_HIDE` removes the destination without deleting any companion records;
6. returning an override to Auto restores catalog-driven behavior;
7. hiding a currently selected module resolves navigation safely to General under the existing resolver.

## Collection behavior

H3 should follow the established Phase 4 collection grammar:

- Manual and A–Z presentation modes;
- A–Z never rewrites stored manual order;
- visible drag/drop feedback and configurable haptics only in clean Manual mode;
- search across name, kind, source, speed, ability summary, senses/proficiencies, traits/actions and notes;
- filters for Active, Favorite, source and kind/type where useful;
- duplicate with fresh UUID and appended manual order;
- named delete confirmation;
- row tap edits; no generic Edit button;
- new/duplicated records cannot be Favorited until durably saved;
- dynamic source/kind filters are presentation state only.

## Android editor design

Phone:

- existing reusable IME-safe editor dialog;
- all structural fields reachable with keyboard visible.

Wide/tablet:

- list on the left, persistent selected/add editor on the right;
- editing must not reset list search/filter/order context.

Editor fields:

- name;
- optional linked class;
- kind/type;
- source/provenance;
- optional non-negative CA;
- optional non-negative PG máximos;
- optional non-negative PG actuales;
- non-negative PG temporales;
- speed;
- abilities/stats summary;
- senses/proficiencies summary;
- traits/actions;
- notes;
- active state.

No automatic derivation from official companion rules is required. The owner enters the values/reference state intentionally.

## Durable draft and Save ownership

H1/H2 currently use one structural draft containing all `classOptions` plus `forms`, technically still named `h1ModuleDraft`.

H3 should extend that same structural draft to include `companions` rather than creating a second authority.

Required consequences:

- Companion add/edit/delete participates in global `Cambios sin guardar`;
- Save persists `companions` together with class options/forms;
- Discard restores repository truth;
- successful Save prunes stale `CharacterQuickAccessKind.COMPANION` targets;
- deleting a companion in an unsaved draft must not immediately remove its Favorite reference, so Discard remains reversible;
- hide/show from PC Settings does not touch the structural draft.

The legacy technical name may remain through H3 if renaming it would add unnecessary risk; its documented conceptual ownership is the complete conditional-module structural draft.

## H3a gate plan

Before Compose UI:

1. add pure companion presentation/search/filter/order helpers;
2. add duplicate/next-order helpers;
3. add focused tests for Manual/A–Z non-mutation, search/filter and duplicate;
4. add tests for companion module union across multiple official triggers;
5. add manual show/hide override regression for Companions;
6. add/strengthen repository round-trip and soft-linked-class removal coverage;
7. run full shared/Kotlin/Android/Desktop/backend gate.

Only after H3a is GREEN may H3 Android UI/navigation/draft integration begin.

## Exact next action

Implement H3a pure companion operations and focused tests. Do not add the Compañeros Compose surface before that gate passes.
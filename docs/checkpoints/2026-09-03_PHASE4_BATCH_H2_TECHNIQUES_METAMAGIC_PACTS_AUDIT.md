# Phase 4 — Batch H2 Técnicas + Metamagia + Pactos ownership audit

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** IMPLEMENTATION INPUT — D-0047 APPROVED SCOPE  
**Canonical `main`:** untouched

## Purpose

Define exact durable-data ownership for Batch H2 before adding operations or UI.

H2 implements the approved reusable conditional modules:

- `Técnicas`;
- `Metamagia`;
- `Pactos`.

The product/design scope is already approved by D-0047 and `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md`. This audit resolves the implementation-level ownership problem created by sharing one durable `CharacterSheet.classOptions` collection among Artífice and H2.

## Existing durable model

`CharacterClassOption` already persists:

- UUID;
- optional linked character-class ID;
- kind;
- name;
- source/provenance text;
- optional cost/reference text;
- effect/reference summary;
- notes;
- active state;
- pinned legacy/reference state;
- manual sort order.

Existing kinds before H2 are:

- `ARTIFICER_PLAN`;
- `ARTIFICER_DEVICE`;
- `SUBCLASS_STATE`;
- `TECHNIQUE`;
- `METAMAGIC`;
- `INVOCATION`;
- `OTHER`.

H1 already proved that one module can safely project and reorder only its owned positions inside the shared collection without moving hidden entries from other module families.

## Approved H2 module intent

From the class/subclass module audit:

### Técnicas

Reusable chosen maneuver/shot/rune/flourish/technique libraries. Strong examples include Battle Master maneuvers, Arcane Archer shots and similar selectable combat-technique sets.

### Metamagia

Known Metamagic options and quick reference. Sorcery Points remain generic `CharacterResource` state; H2 must not create a second point counter.

### Pactos

Persistent Warlock pact-facing choices, Eldritch Invocations and other pact selections that are awkward inside one undifferentiated Traits list. Actual spells and spell slots remain authoritative in Conjuros/Spells.

## Ownership decision

### Técnicas owns

- `CharacterClassOptionKind.TECHNIQUE` only.

It does **not** own generic `SUBCLASS_STATE` merely because a technique may originate in a subclass.

### Metamagia owns

- `CharacterClassOptionKind.METAMAGIC` only.

Sorcery Point current/max/recovery remains in `CharacterResource`.

### Pactos owns

- `CharacterClassOptionKind.INVOCATION`;
- new `CharacterClassOptionKind.PACT_CHOICE`.

`PACT_CHOICE` is an additive domain enum value, not a new SQL table/column. It exists because the current enum has no accurate Pact-owned category for Pact Boons or other persistent pact-facing selections that are not invocations.

Actual Mystic Arcanum spells remain real `CharacterSpell` records. A Pacts record may contain a human reference/note if useful, but H2 must not create a duplicate spell authority.

### Explicitly not owned by H2

- `ARTIFICER_PLAN` and `ARTIFICER_DEVICE` — H1 Artífice;
- `SUBCLASS_STATE` — remains generic/unassigned rather than being silently captured by a module;
- `OTHER` — remains generic/unassigned;
- `forms` — H1 Formas;
- `companions` — H3 Compañeros;
- resources — Gestión/generic Resources;
- spells/slots/prepared state — Conjuros;
- combat actions — Combate;
- ordinary feature prose/use counters — Rasgos where appropriate.

## Why add `PACT_CHOICE`

Three implementation alternatives were considered:

### Alternative A — use `INVOCATION` for every Pacts entry

Rejected. A Pact Boon or other persistent pact selection is not necessarily an Eldritch Invocation. This would encode a semantic lie into durable data and make future filtering/labels misleading.

### Alternative B — let Pactos own `SUBCLASS_STATE` or `OTHER`

Rejected. Those are generic escape-hatch kinds and may later contain non-Warlock state. Claiming them globally would let Pacts search/reorder/delete data it does not own, especially in multiclass/homebrew characters.

### Alternative C — add `PACT_CHOICE`

Selected. It gives Pacts explicit ownership while retaining `INVOCATION` for actual invocation-style entries. This is additive, backward-compatible and does not require an SQL schema migration because `kind` is already persisted as a textual enum value through the existing class-option table.

## H2 shared collection invariants

Every H2 operation must obey these rules:

1. one module edits only its explicitly owned kinds;
2. Manual/A–Z is presentation only; A–Z never rewrites stored manual order;
3. manual movement replaces only positions occupied by that module's visible owned items;
4. hidden Artífice, other H2, generic and future H3 entries retain their relative positions;
5. search/filter never changes stored order;
6. duplicate receives a fresh UUID and an appended global class-option sort order;
7. deleting in a structural draft does not immediately prune Quick Access;
8. successful global Save prunes stale `CLASS_OPTION` Quick Access targets, as established in H1;
9. a newly-created/duplicated structural option cannot become a durable Favorite until after Save;
10. hide/show of a module from PC Settings remains non-destructive.

## Search/filter design

All three surfaces may search:

- name;
- source;
- cost/reference text;
- effect summary;
- notes;
- human-readable option kind.

Recommended filters:

### Técnicas

- Active;
- Favorite;
- source/provenance where present.

### Metamagia

- Active;
- Favorite;
- source/provenance where present.

The `costText` field is reference text only. It does not spend Sorcery Points automatically.

### Pactos

- `Pacto / elección` (`PACT_CHOICE`);
- `Invocación` (`INVOCATION`);
- Active;
- Favorite;
- source/provenance where present.

## Android surface design

H2 should not create three unrelated copies of the same list/editor architecture.

Recommended implementation:

- one reusable `CharacterClassOptionModuleV4` list/editor shell parameterized by module title, owned kinds, labels and filter choices;
- thin `Técnicas`, `Metamagia` and `Pactos` wrappers/configurations;
- the shell consumes and returns the **complete** `classOptions` structural draft, but projects only its owned kinds;
- row tap edits; no generic Edit button;
- phone uses the established IME-safe modal editor;
- wide/tablet uses the established master-detail pattern;
- visible drag/haptics only in unfiltered Manual mode;
- named destructive confirmation;
- Favorite uses `CharacterQuickAccessKind.CLASS_OPTION`;
- optional linked class is available to support multiclass provenance without requiring an official catalog choice.

This generalized H2 shell is implementation reuse only. It does not collapse the three user-facing modules into one tab.

## Conditional navigation

Use the already-approved/current module visibility engine:

- `CharacterModuleKind.TECHNIQUES` -> `Técnicas`;
- `CharacterModuleKind.METAMAGIC` -> `Metamagia`;
- `CharacterModuleKind.PACTS` -> `Pactos`.

Visibility remains the union of recognized class/subclass suggestions plus PC Settings overrides.

Expected representative triggers already present in the catalog include:

- Battle Master / Arcane Archer / selected technique-library subclasses -> Técnicas;
- Sorcerer -> Metamagia;
- Warlock -> Pactos.

Multiclass union must expose each destination at most once.

## Schema conclusion

**No SQL schema migration is required for H2.**

One additive domain enum value, `PACT_CHOICE`, is justified to close the Pacts ownership gap. Existing class-option persistence is otherwise sufficient.

If persistence proves to hard-reject a newly-added textual enum value during focused tests, repair the existing enum mapping; do not create a new H2 table merely to avoid that mapping.

## H2a gate plan

Before Compose UI:

1. add `PACT_CHOICE` to `CharacterClassOptionKind` and its display-label mapping;
2. add pure ownership predicates for Técnicas, Metamagia and Pactos;
3. add search/filter/Manual-A–Z projection helpers;
4. add module-isolated manual reorder helper(s) that preserve hidden positions;
5. reuse existing duplicate/global-next-sort-order helpers;
6. add focused tests proving cross-family isolation, A–Z non-mutation, search/filter and duplicate behavior;
7. add repository round-trip coverage for `PACT_CHOICE`;
8. run the full shared/Kotlin/Android/Desktop/backend gate.

Only after H2a is GREEN may H2 Android UI/navigation wiring begin.

## Exact next action

Implement H2a pure domain/presentation changes and focused tests. Do not add H2 Compose surfaces before the H2a gate passes.
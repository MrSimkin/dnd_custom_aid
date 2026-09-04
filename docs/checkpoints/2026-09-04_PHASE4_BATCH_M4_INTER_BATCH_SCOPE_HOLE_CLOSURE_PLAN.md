# Phase 4 Batch M4 — inter-batch scope-hole closure plan

**Date:** 2026-09-04  
**Status:** PLANNED / REQUIRED by M3  
**Source audit:** `2026-09-04_PHASE4_BATCH_M3_PRIOR_BATCH_COMPLETENESS_AUDIT.md`  
**Canonical `main`:** keep untouched

## Purpose

Close approved D-0047 requirements that slipped between the historical A–L execution batches. This is implementation completion, not owner device-quality QA and not broad refactoring.

The historical Batch L candidate remains immutable historical evidence. Any M4 production change requires a later replacement frozen candidate.

## M4 fixed scope

### M4a — F14 structured proficiencies/languages

Add a reachable Android management surface over the existing durable `CharacterSheet.proficiencies` model.

Required kinds:

- Language;
- Tool;
- Weapon;
- Armor;
- Other.

Requirements:

- add/edit/delete;
- source optional;
- notes optional;
- stable manual order;
- permissive freeform names;
- no legality enforcement;
- participates in the existing structural draft / Save / Discard / unsaved-leave model;
- responsive compact presentation;
- named delete confirmation and IME-safe editing.

Do not add a schema migration unless current persistence proves insufficient; the repository already persists this model.

### M4b — F15 Resource Favorite / Quick Access

Expose ★/☆ for durable generic Resources in Gestión using existing `CharacterQuickAccessKind.RESOURCE`.

Requirements:

- only durable resource IDs may be favorited;
- removing/deleting a resource must not leave an actionable stale favorite after successful persistence;
- Table mode treats Favorite configuration as structural/configuration, while ± operational resource use remains permitted;
- Supercompact continues resolving the same authoritative Resource by ID; no duplicate value storage.

### M4c — I18 character-list summary

Upgrade the campaign character-list card to include:

- class + subclass + level summary;
- freshness / last-updated presentation;
- optional portrait when the character has a usable persisted portrait reference;
- existing lifecycle status/total-level information may remain where useful without overcrowding.

Use current authoritative CharacterSheet + CharacterClosureState. Do not create a separate list-summary database model.

### M4d — I21 real-sheet Application Settings audition

Replace/augment generic font text and bare theme swatches with a miniature representative character-sheet sample.

The sample must visibly exercise enough real UI semantics to judge:

- selected font;
- selected theme;
- text scale;
- compact header/identity;
- representative stat/value cells;
- at least one semantic badge/state treatment.

It is preview-only and must not own/persist character data.

### M4e — D06 rules/source badge closure

Introduce the smallest reusable semantic badge treatment required for class/subclass rules generation and source/provenance.

Requirements:

- explicit text such as `5e`, `5.5e`, `Custom` / equivalent Spanish labels;
- source text represented distinctly where present;
- color may support but never be the only differentiator;
- apply to the class/subclass identity surface and reuse in the settings miniature sample where helpful;
- do not re-theme unrelated screens.

### M4f — D07 state-badge grammar closure

Create/reuse a compact semantic state-badge primitive and apply it to the clearest approved state-rich surfaces so prepared/equipped/attuned/concentrating/favorite state uses one recognizable grammar.

Minimum closure targets:

- Spells: preserve existing V/S/M/Concentration/Ritual/Prepared information while routing state badges through the shared grammar where safe;
- Equipment: Equipped / Attuned / carried-vs-stored state should not remain only undifferentiated joined metadata;
- Concentration: active concentration should use the same semantic state vocabulary;
- Favorite remains text/symbol identifiable (`★` plus accessible description), never color-only.

Do not turn M4f into a global visual redesign.

## Tests / proof required

Add focused shared/UI-testable logic where possible for:

- proficiency draft/persistence round trip and order;
- Resource Quick Access add/remove/prune semantics;
- character-list summary formatting (including subclass and freshness fallback);
- settings preview model/labels if logic is extracted;
- badge label mapping.

Then run the full standard repository gate:

- backend install/type-check;
- all shared/Kotlin tests;
- SQLDelight generation/migration tests;
- Android debug assemble;
- Desktop build;
- APK upload.

No M4 GREEN declaration from compilation alone if a required surface is still unreachable.

## M4 completion gate

M4 is GREEN only when all six M3 holes are represented in reachable current implementation and focused regression evidence is recorded.

After M4, proceed to **M5**:

- re-run D-0047 traceability over the repaired tree;
- reconcile M2 dead/transitional-code cleanup against risk;
- remove only proven obsolete paths/duplication;
- run complete automated gate;
- prepare a new frozen candidate identity for later owner QA.

Do not patch the historical frozen L branch. Do not merge `main`. Do not begin DM implementation.

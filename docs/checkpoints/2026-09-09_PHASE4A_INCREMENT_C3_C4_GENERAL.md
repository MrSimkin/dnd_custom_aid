# Phase 4A successor cycle — Increment C3/C4 General

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** IMPLEMENTED; FINAL AUTOMATED GATE PENDING

## Scope

This checkpoint covers the reconciled successor-plan steps:

- **C3 — General compact identity**;
- **C4 — General reference/state projections**.

Owner/device visual acceptance is **not** claimed by this checkpoint.

## Implemented

### Compact class identity

- `Clases` now routes through the successor compact class identity component.
- Ordinary presentation is version-neutral and Spanish-facing.
- Class level remains explicit.
- Maximum hit-die count is derived from class level; the UI shows remaining/maximum hit dice plus die type instead of requiring a second editable maximum.
- Existing catalog/source/rules-family metadata remains preserved internally for compatibility, but is not ordinary user-facing clutter.
- Manual/custom class and subclass entry remains supported.

### Canonical General projection layer

Focused shared projection helpers and regression coverage were added for:

- `Raza`;
- languages;
- equipped item references;
- Resources placed in General;
- per-source spellcasting calculations.

### General successor cards

General now has successor projection cards for:

- `Raza` and visible `Idiomas`;
- current CA plus equipped-item references;
- custom attributes;
- Inspiration when enabled in per-character settings;
- enabled Custom Markers;
- Resources configured for General placement;
- compact per-source `Lanzamiento de Conjuros` rows.

The inventory domain currently stores `equipped` but does not yet distinguish armor/shields structurally. The General projection therefore does **not** infer armor type from item names. Typed armor/shield narrowing belongs to the later Equipment/domain pass.

### One-datum / one-state wiring

The General projection is assembled from the current editor drafts rather than stale persisted copies:

- Background draft supplies current `Raza`;
- proficiency draft supplies current languages;
- Equipment draft supplies current equipped references;
- spellcasting draft supplies current source associations;
- the current structural character draft supplies core/class calculations.

Operational Inspiration and Resource changes use the editor's existing canonical operational `CharacterSheet` persistence path. This keeps the editor's stored state synchronized so a later normal Save cannot silently overwrite those quick changes.

### Removed ambiguous General casting duplicate

The legacy General call to the single global `QuickMagicCardV4` was removed. General now uses the successor per-source spellcasting projection instead of exposing one ambiguous global casting ability/CD/attack block.

The legacy private helper remains in the source file for now but is no longer part of the General composition; deletion can be handled by later cleanup without changing behavior.

### Existing closure references preserved

The existing closure card for damage defenses (resistance/immunity/vulnerability), senses, movement, portrait, and token is retained for this increment so C3/C4 does not silently delete unrelated persisted functionality. Its information architecture can be refined in a later coherent surface pass.

## Validation evidence before final gate

- Successor General component previously compiled successfully in isolation.
- Shared projection regression tests previously passed.
- Compact class component compile regression (`findClass` lookup mismatch) was corrected by a small Android-layer catalog lookup adapter.
- Head `8ee4784166492166c90d269eaec79bd9f6a67a25` passed full scaffold workflow `34299449158` before the final editor wiring.
- Final editor wiring commit: `03fa63246499ee3cda408cfcea6d70b8b0eefb96`.
- Exact source substitutions and `git diff --check` passed in the temporary maintenance workflow; that workflow removed itself in the same source commit.

## Exit condition

C3/C4 becomes technically complete only after the normal full scaffold gate passes on a descendant containing the final editor wiring.

After that, update this checkpoint to `COMPLETE / GREEN`, update `docs/checkpoints/LATEST.md`, and advance the exact next implementation action to **C5 — Habilidades**.

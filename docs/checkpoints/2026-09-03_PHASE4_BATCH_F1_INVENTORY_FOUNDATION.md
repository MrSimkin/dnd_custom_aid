# Phase 4 — Batch F1 inventory foundation checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** GREEN  
**Canonical `main`:** untouched

## Scope

F1 establishes the pure/persistent inventory foundation required before the Batch-F Equipo + Monedas UI is rewritten.

Implemented in integration commit `b440eadb569d2ded4e169b37efebcab18dec380b`:

- additive migration 8 adding explicit `carry_state` to existing `character_inventory_usage` metadata;
- backward-compatible default `CARRIED` for existing characters;
- `CharacterInventoryCarryState` with `CARRIED` / `STORED`;
- repository round-trip of consumable kind, quick-use amount and carried/stored state;
- pure inventory presentation helper that separates ordinary/special sections and supports independent Manual/A–Z projections;
- search over name/location/notes/description without mutating stored order;
- grouped inventory filter semantics for carried/stored, equipped, special, consumable/ammunition and located items;
- carried-weight calculation that excludes explicitly stored items while treating equipped items as effectively carried;
- bounded consumable/ammunition quantity decrement;
- inventory + usage duplication helpers;
- focused operation tests and an additive migration regression.

## Model clarification

`location` remains the human-readable container/place/body location (for example Backpack, Belt, Quiver, Inn or custom text).

`carry_state` answers a different question: whether the item is currently with the character or stored away. This avoids unreliable string heuristics and makes I09's `Transportado / Guardado` filter meaningful.

An equipped item is treated as effectively carried even if inconsistent metadata says otherwise.

## Persistence boundary

The new state extends the existing character-scoped inventory metadata rather than modifying the proven core inventory row identity. Existing stable inventory UUID soft-reference behavior remains in place.

No rewrite of migration 7 was performed. Migration file `8.sqm` is additive only.

## First gate finding and repair

The first controlling workflow `33813514262` reached and compiled the shared and Android sources, but `:shared:desktopTest` failed in the new migration fixture only.

The fixture declared itself as the database state immediately before migration `8.sqm`, but invoked SQLDelight migration from version `7`. That re-ran `7.sqm` before `8.sqm`, attempting to recreate closure tables already represented by the fixture.

This was a test-fixture version error, not a product-schema or inventory-operation failure.

Repair commit:

- `ce3fc4996cf2090134f783dfd0d6bd9da55cba52` — `test: repair Batch F1 migration fixture version`;
- the fixture now starts at version `8`, so only the intended migration `8.sqm` is applied;
- no F1 production code or migration content changed in the repair.

## Verification

Controlling retest workflow `33813882408`: **PASS** on head `45297640435a182944bf3f949b1f6aaf155551e6`.

Verified:

- SQLDelight generation/migration regression PASS;
- focused inventory operation tests PASS;
- closure repository round-trip/migration tests PASS;
- all shared desktop tests PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- backend type-check PASS;
- APK artifact upload PASS.

Workflow artifact:

- `dnd-custom-aid-debug-apk`;
- artifact ID `9915876586`;
- ZIP digest `sha256:bc41dbede7a4ed994e921862b0721763a8904b7ed918cc077eb705fba155b0f2`.

This artifact is integration evidence only, not the frozen owner-QA closure candidate.

**F1 gate is closed GREEN.**

## Exact continuation

Proceed to F2 UI wiring with no further schema change:

- unify `inventoryUsage` into the same saveable Equipment draft as items/currencies so metadata also works for newly-created unsaved items;
- dense ordinary rows and compact special rows;
- much more compact currencies;
- independent Manual/A–Z controls for ordinary and special sections;
- visible drag feedback/haptics only where manual reorder is unambiguous;
- search/filter without mutating stored manual order;
- location/container editing for ordinary and special items;
- carried/stored and consumable/ammunition metadata;
- quick-use and duplicate actions;
- collapsible sections;
- proportionate phone/tablet layout improvements.

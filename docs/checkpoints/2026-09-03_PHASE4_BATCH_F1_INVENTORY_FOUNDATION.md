# Phase 4 — Batch F1 inventory foundation checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** PENDING INTEGRATION GATE  
**Canonical `main`:** untouched

## Scope

F1 establishes the pure/persistent inventory foundation required before the Batch-F Equipo + Monedas UI is rewritten.

Implemented in integration commit `b440eadb569d2ded4e169b37efebcab18dec380b`:

- additive schema 8 migration adding explicit `carry_state` to existing `character_inventory_usage` metadata;
- backward-compatible default `CARRIED` for existing characters;
- `CharacterInventoryCarryState` with `CARRIED` / `STORED`;
- repository round-trip of consumable kind, quick-use amount and carried/stored state;
- pure inventory presentation helper that separates ordinary/special sections and supports independent Manual/A–Z projections;
- search over name/location/notes/description without mutating stored order;
- grouped inventory filter semantics for carried/stored, equipped, special, consumable/ammunition and located items;
- carried-weight calculation that excludes explicitly stored items while treating equipped items as effectively carried;
- bounded consumable/ammunition quantity decrement;
- inventory + usage duplication helpers;
- focused operation tests and a real schema-7-to-schema-8 migration regression.

## Model clarification

`location` remains the human-readable container/place/body location (for example Backpack, Belt, Quiver, Inn or custom text).

`carry_state` answers a different question: whether the item is currently with the character or stored away. This avoids unreliable string heuristics and makes I09's `Transportado / Guardado` filter meaningful.

An equipped item is treated as effectively carried even if inconsistent metadata says otherwise.

## Persistence boundary

The new state extends the existing character-scoped inventory metadata rather than modifying the proven core inventory row identity. Existing stable inventory UUID soft-reference behavior remains in place.

No rewrite of migration 7 was performed. Migration 8 is additive only.

## Gate required

F1 is not GREEN until the controlling checkpoint workflow passes:

- SQLDelight schema generation/migrations;
- focused inventory operation tests;
- closure repository round-trip/migration tests;
- all shared desktop tests;
- Android debug assembly;
- Desktop build;
- backend type-check;
- APK artifact upload.

If F1 is GREEN, proceed to F2 UI wiring: dense rows, compact currencies, independent Manual/A–Z controls, search/filter, visible Manual drag only, location/container editing for ordinary and special items, quick-use metadata/actions, duplicate/collapse and proportionate tablet improvements.

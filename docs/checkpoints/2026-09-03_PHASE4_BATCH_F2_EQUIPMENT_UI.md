# Phase 4 — Batch F2 Equipo + Monedas UI checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** PENDING INTEGRATION GATE  
**Canonical `main`:** untouched

## Scope

F2 wires the GREEN F1 inventory foundation into a new isolated Equipo + Monedas surface. No schema change is introduced here.

Integration commit:

- `2141c6e93139f04519f6c129d950ec2eb751ebef` — `feat: integrate Batch F2 equipment and currencies UI`.

## Unified Equipment draft

`CharacterEquipmentDraftV4` now contains:

- core inventory items;
- currencies;
- `CharacterInventoryUsage` metadata.

This makes carried/stored state and consumable/ammunition metadata part of the same saveable/rotation-safe unsaved state as the item itself. A newly-created item can therefore receive metadata before it exists durably in SQL.

Global Save deliberately persists in this order:

1. save the core character/inventory, establishing any new item UUIDs;
2. save closure inventory metadata against those now-existing UUIDs;
3. reload/reset the Equipment draft from the durable core + closure state.

The unified draft also participates in the existing dirty-state and unsaved-leave guard.

## UI delivered

- dense ordinary-equipment rows;
- compact special-equipment cards;
- much more compact currency cells (3 columns narrow / 6 wide);
- total currently-carried weight and attunement summary;
- shared search and approved filters: Transportado, Guardado, Equipado, Especial, Consumible, Munición, Con ubicación;
- independent Manual / A–Z presentation modes for ordinary and special sections;
- alphabetical presentation never rewrites stored `sortOrder`;
- drag handle/feedback/haptics only in unfiltered Manual mode, avoiding ambiguous hidden-item reorders;
- independent collapse/show controls for ordinary and special sections;
- container/location editable and visible for both ordinary and special items;
- converting special -> ordinary preserves location and only confirms removal of special-only description/attunement data;
- carried/stored editor state, with equipped items forced effectively carried;
- consumable/ammunition metadata + configurable quick-use decrement;
- quick-use quantity is bounded at zero by the F1 pure helper;
- duplicate copies core item + extension metadata to a new UUID within the same unsaved draft;
- named delete removes both draft item and its extension metadata;
- row/card tap remains the primary edit affordance; no generic Edit button was added;
- narrow layout uses one item column; wide layout uses three ordinary / two special columns.

## First guarded-integration abort

The first one-time integrator run `33814423659` aborted before code commit because its guard assumed three identically-indented `CharacterEquipmentDraftV4` blocks. The editor actually had one initialization block and two baseline/reset blocks with different indentation.

No partial F2 production code was committed by that failed run.

The guard itself was repaired and the second integrator run `33814527124` succeeded, producing `2141c6e...` and removing its one-time scaffolding.

## Gate required

F2 is not GREEN until the controlling workflow on this checkpoint head passes:

- shared/Kotlin regression tests including F1 operations/migration;
- Android debug compile/assembly of the new Compose surface;
- Desktop build;
- backend type-check;
- APK artifact upload.

If F2 compiles GREEN, perform a short Batch-F completeness review before closing F. In particular, confirm whether the current wide multi-column treatment is sufficient for this batch or whether a focused F3 tablet master-detail refinement is still warranted before advancing to G1. Full adaptive-shell master-detail remains additionally audited in later I1.

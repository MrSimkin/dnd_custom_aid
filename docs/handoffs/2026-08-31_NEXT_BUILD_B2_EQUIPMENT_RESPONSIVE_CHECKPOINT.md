# Next-build B2 — Equipment + responsive list UX checkpoint

**Status:** IMPLEMENTED / awaiting CI gate  
**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Scope implemented

This checkpoint implements the approved B2 corrective UX package while preserving the unified inventory and existing authoritative data model.

### Equipment editor safety

- Item editor now ignores generic outside-tap/dialog dismissal.
- `Aplicar` and `Cancelar` remain the explicit dismissal actions.
- Editor content remains scrollable and now includes IME + navigation-bar padding and extra bottom reachability space.
- Add-currency editor receives the same non-destructive outside-tap behavior and IME-safe scrollable body.

### Inventory drag-and-drop

- Removed up/down Unicode pseudo-buttons.
- Reused the vector `StableDragHandle` introduced in B1.
- Long-press vertical drag changes the visible item order and rewrites the same authoritative `sortOrder` sequence.
- Normal and special items are visually separated, but remain records in the same `CharacterInventoryItem` list/model.
- Reordering within either visible section maps back into the unified list and then normalizes exact `sortOrder` values.

### Special-item presentation

- Ordinary objects and `Equipo especial` are rendered in materially separate visual sections.
- No duplicate item storage was introduced.
- Special description/location/attunement remain properties of the same inventory record.
- Existing manual attunement behavior and informational count are preserved.

### Responsive inventory

- Narrow layout: one inventory card per row.
- Wide layout: two inventory cards per row where space permits.
- Card content remains compact and uses the available width instead of forcing a portrait-shaped list in landscape.

### Responsive currencies

- `Monedas` is now a compact grid of individual currency cells.
- Narrow layout uses two columns.
- Wide layout uses three columns.
- Custom currencies remain editable/removable and use the same underlying currency list.

### Responsive Combat follow-through

- Attack/action cards now use one column on narrow layouts and two columns on wide layouts.
- Existing B1 drag ordering continues to mutate the same `sortOrder` list.
- No swipe-page gesture or duplicate Combat storage was introduced.

## Exact implementation commits / blobs

1. Equipment corrective + responsive UX:
   - commit `74c9ee2438cc394e4145dc1187b51c2c8dd5986d`
   - `CharacterEquipmentTabV4.kt` blob `f4bb8877449d965128b79bfe7beb95f189826ef4`

2. Wide Combat entry grid:
   - commit `0b447dc5274e552895cee6d94cc6176f7e15d13b`
   - `CharacterCombatTabV4.kt` blob `c640bf95dc45353986be0af162e58d6a0125dd95`

## Data/ownership impact

- No SQL/schema/shared-domain change in B2.
- Inventory remains one unified persistent list.
- Currency ownership is unchanged.
- `sortOrder` remains the authoritative persisted manual order.
- Wide/narrow presentation is a view concern only.

## Verification status

- B1 gate was already GREEN before B2 started.
- B2 source implementation: COMPLETE.
- Formal combined B2 CI gate: PENDING at this checkpoint.
- Owner phone QA: deferred to the consolidated final candidate.

## Gate rule

Do not advance to B3 until this checkpoint's CI run is green. If compilation/tests fail, diagnose and checkpoint the correction before continuing.

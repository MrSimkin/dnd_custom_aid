# Next-build B1 — General + Combat corrective UX checkpoint

**Status:** IMPLEMENTED / awaiting CI gate  
**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Scope implemented

This checkpoint implements the approved run-#180 corrective UX package for B1 only: General + Combat. It does not close B2/B3 or the broader next build.

### General

- Nonzero `Ajuste adicional` no longer adds a second collapsed text line.
- The collapsed derived-value control now shows the total plus a compact `*` marker when the additional adjustment is nonzero.
- The exact adjustment remains available in the existing tap breakdown/editor.
- `Velocidad` keeps the canonical persisted/editable value in feet.
- The collapsed General speed control now displays imperial first plus approximate metric using the approved Spanish-decimal convention, e.g. `30 ft (9 m)`.
- Tapping the speed control opens an explicit editor for the canonical feet value and shows the formatted preview.
- Relevant General logical rows use vertical centering where the neighboring derived/display control can have different visual height.

### Combat

- Quick-reference logical rows now vertically center neighboring values.
- Combat quick-reference `Velocidad` displays the same imperial-first approximate-metric convention as General.
- Attack/action editor is scrollable with IME/navigation-bar padding and extra bottom reachability space.
- Outside-tap / generic dialog dismissal no longer closes or discards an active attack/action editor; `Aplicar` and `Cancelar` are the explicit editor dismissal actions.
- Up/down pseudo-buttons were removed from attack/action entries.
- Attack/action entries now expose a vector drag handle with a content description.
- Long-press vertical drag reorders entries and rewrites their persisted draft `sortOrder` sequence exactly.

## Shared UI support

Added reusable `StableDragHandle` in `IconControls.kt`:
- vector/Canvas implementation;
- no Unicode pseudo-button;
- stable content description semantics;
- intended for reuse by Equipment B2.

## Exact implementation commits / blobs

1. Reusable drag handle:
   - commit `6c4239b00bba4f7aa2a58a5ef8447574d6921231`
   - `IconControls.kt` blob `1a1bd1a748bfab64006f7ab078631d759f189f49`

2. Combat corrective UX:
   - commit `c0434e82cdd6c78e84fd2690e2fa62397850793c`
   - `CharacterCombatTabV4.kt` blob `f4687376193c773853b0b2a47941aa798cc2adc2`

3. General corrective UX:
   - prepared and validated on safety branch `tmp/general-b1-safe-edit`;
   - commit `f0595a9fdc5aeebe8a6f8eef84ca2564e5789f5c`;
   - `CharacterEditorV4.kt` blob `898d9777f0f8e801bd98cb013e12b9ce247e6c5a`;
   - compare against parent showed exactly one changed file, 99 additions / 12 deletions;
   - actual patch was inspected before the implementation branch was fast-forwarded;
   - file head/tail were verified after the edit, avoiding recurrence of the prior truncation incident.

## Data/ownership impact

- No schema or shared-domain model change in B1.
- Speed remains the same canonical integer-feet character field.
- Combat entry ordering continues to use the existing authoritative `sortOrder`; only the UI mechanism changed to drag-and-drop.
- No automatic rule inference or new cross-domain synchronization was introduced.

## Verification status

- Source-level safety validation: PASS for the large-file General edit.
- Formal combined CI gate: PENDING at this checkpoint.
- Owner phone QA: NOT YET REQUESTED; final owner QA remains scheduled for the consolidated candidate after all increments.

## Gate rule

Do not advance to B2 until the CI run generated from this checkpoint is green. If Kotlin compilation/tests fail, diagnose and checkpoint the correction before continuing.

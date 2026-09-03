# Phase 4 Closure — Batch B1a IME / Action Foundation

Date: 2026-09-03
Branch: `implementation/phase4-character-closure`
Gate head: `f7e6cd0e69a6c8756d87c8cab6d0877b03422120`
Workflow: `33788731823`
Result: **GREEN**

## What B1a established

- Added reusable `CharacterImeSafeEditorDialog` for character-sheet modal editing.
- The dialog owns IME and navigation-bar insets.
- Editable content scrolls while `Guardar` / `Cancelar` remain reachable above the keyboard.
- Keyboard/back focus dismissal does not discard the editor draft; explicit `Cancelar` owns cancellation semantics.
- Added reusable inline validation, named delete confirmation, and useful empty-state primitives.
- Migrated character creation to the shared dialog/empty-state pattern.
- Migrated titled Notes editing to the shared dialog.
- Notes cards use tap-to-edit; the redundant explicit `Editar` action was removed.
- Notes deletion uses the shared named confirmation pattern.

## Gate evidence

Workflow `33788731823` passed:

- backend type-check,
- shared/Kotlin build and tests,
- Android debug assembly,
- Desktop build,
- APK upload.

## Boundary / next step

B1a proves the shared interaction primitive but does **not** close the global keyboard bug by itself.

B1b must migrate all remaining character-sheet editors/dialogs that still own custom action/IME shells, including:

- Combat,
- Equipment,
- Trasfondo narrative editors,
- Rasgos,
- spell-source management/editing,
- spell-entry editing,
- any remaining editor dialogs embedded in `CharacterEditorV4`.

Where a row/card is already, or can safely become, the primary edit target, redundant explicit `Editar` controls should be removed in favor of the approved action grammar.

No merge to `main` is authorized by this checkpoint.

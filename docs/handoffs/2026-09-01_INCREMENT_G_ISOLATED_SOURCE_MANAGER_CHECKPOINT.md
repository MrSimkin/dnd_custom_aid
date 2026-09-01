# Increment G — isolated Conjuros source-manager checkpoint

**Date:** 2026-09-01  
**Branch:** `implementation/character-data-foundation`  
**Component head:** `bbf8fa25fa714b3a0df523851cc6cf052e09508c`

## Implemented isolated scope

- saveable spellcasting draft codec containing authoritative source and conceptual-spell collections;
- `Todos` plus one horizontally scrollable single-line subtab per spellcasting source;
- source selection by stable source ID;
- source add/rename/edit;
- optional source-to-class-row link through ID/name options supplied by the editor;
- custom/unlinked sources supported;
- drag-and-drop source ordering;
- explicit source deletion confirmation;
- stronger deletion warning when the source still has spell associations;
- source deletion removes only that source's associations and preserves conceptual spell records;
- deleting the selected source falls back to `Todos`;
- no spell CRUD/search/prepared toggle yet (Increment H boundary retained).

## Compile defect caught before editor wiring

Initial isolated run #302 / ID `33461787460` failed Android compilation because `CharacterSpellsTabV4.kt` explicitly imported Compose's internal `androidx.compose.foundation.layout.weight` symbol.

Diagnosis: the explicit import was invalid; `Modifier.weight(...)` itself is valid inside `RowScope` and requires no such import.

The one-line import removal was applied and validated on a temporary safety branch. Corrected source blob: `5a4ea0e07985813e736764c427ece681c7d216a0`.

Temporary safety workflow/marker files were not promoted.

## Validation

Corrected temporary-state validation:
- workflow run #306 / ID `33462081195`: PASS

Clean implementation-branch validation:
- workflow run #307 / ID `33462316805`: PASS
- backend: PASS
- shared/Kotlin tests + Android/desktop builds: PASS
- Android debug APK upload: PASS
- artifact: `dnd-custom-aid-debug-apk`
- artifact ID: `9783643715`
- artifact ZIP digest: `sha256:e757138c8a6bcefd76acebbcecc33c03a2c746910fd36b37825599b7f551ed31`

## Next exact step

Wire this already-green component into `CharacterEditorV4.kt` only through the established asserted narrow-patch safety-branch workflow. Do not modify Quick Magic/slot state or the Notes shell in Increment G.

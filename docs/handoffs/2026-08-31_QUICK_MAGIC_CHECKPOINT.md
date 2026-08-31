# Recovery checkpoint — Quick Magic

**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Completed

Commit `1a133b07ac5ba03fa2399da23969b9fd9302e9f1` (`Add Quick Magic reference and spell-slot pips`) implements the approved Quick Magic slice in the Android character editor.

### General / Quick Magic

- Added `Quick Magic` at the bottom of `General`.
- Moved `CD conjuros` out of `Referencia de combate` into Quick Magic.
- Added manual `Ataque mágico`.
- Added manual `Aptitud mágica` selector with FUE / DES / CON / INT / SAB / CAR / Otro / Ninguna.
- No automatic spellcasting builder logic was introduced.

### Spell slots

- `Configurar espacios` exposes spell levels 1–9 and manual maximum slot counts.
- Compact sheet only shows levels whose configured maximum is greater than zero.
- Each active spell level renders tappable circular spent/unspent marks.
- Durable state uses total + spent count; visible pips remain equivalent because same-level slots are fungible.
- Spent state is included in the character draft saver and persists through character save/restart.
- `Restaurar espacios` manually clears spent state.
- No automatic short/long-rest detection or restoration.

### Persistence / recreation

- Draft saver includes spell save DC, spell attack modifier, spellcasting ability and all 1–9 slot draft states.
- Saver remains backward-compatible with earlier V4/follow-up draft JSON that lacks those new fields.
- `toSheetOrNull` maps Quick Magic state into the already-implemented shared `CharacterSheet` persistence model.

## Verification

GitHub Actions `Scaffold checks` run **#160** / ID `33432004393` completed successfully for commit `1a133b07ac5ba03fa2399da23969b9fd9302e9f1`:

- shared desktop tests: PASS;
- Android debug assemble: PASS;
- desktop build: PASS;
- backend checks: PASS;
- APK artifact upload: PASS.

## Next step

Implement the new `Combate` character-sheet tab as the next isolated increment:

- add the tab after `Habilidades`;
- show read-only references to the same authoritative CA / Iniciativa / Velocidad / PG values;
- add persistent reusable attacks/actions with approved manual fields;
- support add/edit/delete and simple phone-safe manual reorder;
- extend the draft saver so unsaved combat entries survive Android recreation;
- verify CI and checkpoint before starting `Equipo`.

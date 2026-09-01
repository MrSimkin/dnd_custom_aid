# Increment D2 checkpoint — PC Settings component

Date: 2026-09-01
Branch: `implementation/character-data-foundation`

## Added

`CharacterPcSettingsV4.kt` at implementation commit `f540e27a2d809e57eae8d512b763f76439721eb8`.

The component is a dedicated full-screen character-settings surface with:
- back control to return to the character sheet;
- character name context;
- the initial approved character-wide control: `Lanzador de conjuros`;
- explicit text that OFF hides Quick Magic and Conjuros without deleting data.

## Existing-data predicate

`CharacterSheet.hasMeaningfulSpellcastingDataV4()` returns true when any of the following exists:
- spell save DC;
- spell attack modifier;
- spellcasting ability other than NONE;
- configured spell slots (`totalSlots > 0`);
- spellcasting sources;
- conceptual spells.

This predicate controls whether ON -> OFF needs the approved hide-not-delete confirmation.

## Not yet wired

This checkpoint does not yet change the character header gear, persistence, Quick Magic visibility, or Conjuros fallback. Those are the next D2 wiring step and will be performed through the same large-file safety procedure used in D1.
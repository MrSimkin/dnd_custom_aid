# Next-build C1 — schema/migration checkpoint

**Status:** C1 complete; C2 domain/repository mapping still required before the branch is expected to compile against the new generated query signatures  
**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Implemented

### Character-wide persistence
`character` now includes:
- `spellcaster_enabled INTEGER NOT NULL DEFAULT 0`;
- `general_notes TEXT NOT NULL DEFAULT ''`.

### New character-owned tables
- `character_background` — one-to-one narrative background fields;
- `character_trait` — ordered structured Rasgos entries;
- `character_note` — ordered titled notes;
- `character_spell_source` — ordered stable spellcasting sources with nullable soft `linked_class_id`;
- `character_spell` — conceptual spell records ordered within spell level;
- `character_spell_source_assoc` — many-to-many spell/source relationship with source-specific `prepared` state.

No image persistence was added.

### Migration `4.sqm`
Existing PCs migrate `spellcaster_enabled` to ON iff at least one approved signal exists:
- non-null `spell_save_dc`;
- non-null `spell_attack_modifier`;
- `spellcasting_ability <> 'NONE'`;
- at least one `character_spell_slot` row with `total_slots > 0`.

Otherwise the default remains OFF.

The migration creates no inferred spellcasting source and no conceptual spell.

## Validation

Diff from persistence-map checkpoint `631157790ec9d091c6ee66d456a73efd24c097cf` through migration commit `ed9cfe24f1f13cf4442a35d59a4851fdeb6ac216` contains exactly:
- added `4.sqm`: 90 lines;
- modified `Character.sq`: 170 additions / 4 deletions.

Migration blob after validation: `ef486794e69c72e4deded37422cfdd1a3cdd6266`.
Character schema blob after C1: `4d0720cabeb4f929c6e14eea1ec5df97a86ba9f7`.

## Expected temporary state

`Character.sq` query signatures now include the new character-wide fields and new query families. `CharacterRepository.kt` still targets the pre-C1 signatures until C2. Therefore C1 is a durable schema checkpoint, not the Gate C compilation checkpoint.

## Next

C2:
- append the approved domain model types/defaults to `CharacterSheet.kt`;
- update `CharacterRepository.kt` validation/save/hydration;
- implement soft class-link normalization;
- preserve one authoritative spell-slot representation.

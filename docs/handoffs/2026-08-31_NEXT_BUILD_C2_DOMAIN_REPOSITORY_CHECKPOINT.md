# Next-build C2 — domain/repository checkpoint

**Status:** C2 implementation complete; awaiting CI before C3 tests  
**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Shared domain model

`CharacterSheet.kt` now includes safe-default character-owned state for:
- `spellcasterEnabled`;
- `CharacterBackground`;
- ordered `CharacterTrait` entries;
- ordered `CharacterSpellcastingSource` entries;
- conceptual `CharacterSpell` entries;
- per-spell source associations with source-specific `prepared`;
- `generalNotes`;
- ordered titled `CharacterNote` cards.

Added enums:
- `CharacterTraitType`: CLASS, SPECIES_RACE, BACKGROUND, FEAT, GIFT_BLESSING, OTHER;
- `CharacterActivationType`: PASSIVE, ACTION, BONUS_ACTION, REACTION, OTHER.

`CharacterSheet.kt` blob after C2 model work: `93b23c8c8a21307aa575b907eabfa4f0a9a0d8b3`.

## Repository mapping

`CharacterRepository.kt` now validates, saves and hydrates every new Increment C domain transactionally.

### Ownership behavior implemented
- spell slots remain the pre-existing single authoritative `character_spell_slot` representation;
- `spellcasterEnabled = false` is only a persisted visibility/configuration state and does not delete spell data;
- spellcasting-source class links are normalized to null when the referenced class ID no longer exists;
- deleting/removing a source removes only its source associations; conceptual spell records survive if still present in the character spell list;
- one conceptual spell may have multiple source associations with independent `prepared` values;
- associations pointing to absent sources are dropped softly rather than causing unrelated data deletion;
- background and general/titled notes remain separate from combat/inventory notes.

### Ordering
- class, trait, titled-note and source list order is persisted from list order;
- conceptual spell order is normalized independently within each spell level;
- source associations do not own spell level or ordering.

### Validation
- stable IDs are unique inside each character-owned collection;
- traits enforce non-negative/manual usage bounds;
- titled notes require nonblank titles;
- sources require nonblank names;
- spells require nonblank names and levels 0–9;
- a spell cannot associate to the same source twice.

`CharacterRepository.kt` blob after C2 mapping: `d94516b168ba71962e1eda0c9b9e7e6ae3206ad0`.

## Structural validation

Commit `986c1a37c40ddf07ba1ed78b883f9e08a4b7a129` is exactly one commit ahead of the C2 domain-model commit and changes only `CharacterRepository.kt` (233 additions, 0 deletions relative to that parent). File head and tail were explicitly refetched and are intact.

## CI purpose

The C2 CI run is the first compile gate after C1 changed SQLDelight query signatures. It must validate:
- SQLDelight schema/query generation;
- generated query argument/result signatures;
- common shared-domain compilation;
- Android/desktop compilation against the appended `CharacterSheet` fields.

If CI exposes a generated-signature mismatch, diagnose/checkpoint/fix before starting C3 tests.

# Next-build Increment C — persistence map checkpoint

**Status:** Implementation map complete; no Increment C production schema changes yet  
**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`  
**Starting HEAD:** `7c765f55b721b86bd568ca40b7c13f358ac25796`

## Purpose

Record the exact persistence surfaces and implementation constraints before adding the approved `Trasfondo`, `Rasgos`, `Conjuros`, `Notas`, and PC-wide spellcaster state.

## Current authoritative persistence surfaces

- `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterSheet.kt`
  - shared domain model;
  - current authoritative character-owned objects end with currencies.
- `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterRepository.kt`
  - creates, validates, saves, and hydrates full character state transactionally.
- `shared/src/commonMain/sqldelight/io/github/mrsimkin/dndcustomaid/shared/db/Character.sq`
  - base SQLDelight schema and character queries.
- migrations currently present: `1.sqm`, `2.sqm`, `3.sqm`.
- `shared/src/desktopTest/.../CharacterFollowupFoundationTest.kt`
  - current follow-up persistence and migration regression coverage.

Current `Character.sq` blob at Increment C start: `5e98d44033169f748e97bc943178c77a659a564d`.
Current `CharacterSheet.kt` blob: `198c5a8ba9ac6a550022a2a125e3724016ab1136`.
Current `CharacterRepository.kt` blob: `f99c5664b02298d83a1c013edc4ae8b8bbe6c63d`.

## C1 schema plan

Add persisted character-wide fields:
- `spellcaster_enabled` default `0`;
- `general_notes` default empty string.

Add character-owned tables:
- one-to-one `character_background`;
- ordered `character_trait`;
- ordered `character_note`;
- ordered stable `character_spell_source`;
- conceptual `character_spell` ordered within spell level;
- many-to-many `character_spell_source_assoc` with source-specific `prepared`.

Image placeholders remain UI-only; no image-path or fake attachment persistence is added.

## Spellcasting-source/class soft-link implementation

`character_spell_source.linked_class_id` is intentionally a nullable soft ID rather than a cascading foreign key.

Reason:
- approved ownership says deleting a class must unlink rather than delete the source/spells;
- current class persistence rewrites class rows during saves;
- a hard foreign key with `ON DELETE SET NULL` would incorrectly sever otherwise stable links during routine class rewrites.

Repository persistence will normalize a source link to null when its referenced class ID is not present in the saved class set.

## Migration 4 plan

A new `4.sqm` will:
1. add `spellcaster_enabled` and `general_notes`;
2. create all new domain tables empty;
3. migrate existing PCs to `spellcaster_enabled = 1` iff any meaningful existing spellcasting state exists:
   - non-null spell save DC;
   - non-null spell attack modifier;
   - spellcasting ability other than `NONE`;
   - at least one configured spell-slot row with total slots > 0;
4. otherwise leave the flag OFF;
5. create no spellcasting sources or conceptual spells automatically.

## C2 shared-domain plan

Add domain types for:
- narrative background;
- trait type/activation/usage state;
- titled notes;
- spellcasting sources;
- conceptual spells and per-source prepared associations.

All new fields will be appended to `CharacterSheet` with safe defaults so existing construction/copy call sites remain source-compatible during this foundation increment.

## C3 tests

Tests must prove:
- new PC defaults are empty and caster OFF;
- all new domains round-trip through save/reopen with exact manual ordering;
- one conceptual spell can associate with multiple sources with distinct prepared state;
- source links to removed classes fail softly;
- migration preserves old run-#180 state and derives caster ON/OFF correctly;
- migration creates no inferred spell sources/spells;
- toggling caster state is persistence-only and non-destructive to spell data.

## Gate

Increment C remains open until C1, C2, and C3 are checkpointed and the shared/Kotlin CI gate is green.

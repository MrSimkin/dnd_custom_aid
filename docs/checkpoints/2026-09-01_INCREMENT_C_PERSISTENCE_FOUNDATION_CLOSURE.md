# Increment C closure — next-build persistence/schema/migration foundation

Date: 2026-09-01
Branch: `implementation/character-data-foundation`

## Status

**CLOSED / PASS**

Gate C completed successfully on checkpoint commit `b1b3c148f1482b85f933b2b6d4265f6c23851670`.

GitHub Actions:
- workflow: `Scaffold checks`
- run number: `254`
- run ID: `33456724375`
- backend job: PASS
- Kotlin/shared tests + Android/desktop build job: PASS
- Android debug APK upload step: PASS

## Increment C delivered

### C1 — schema and migration

Added persistence for:
- character-level `spellcaster_enabled`;
- background/Trasfondo;
- structured Rasgos;
- general notes and titled note cards;
- spellcasting sources with optional soft linkage to a character class;
- conceptual spells;
- spell ↔ source associations with independent `prepared` state;
- existing shared spell-slot state retained as the single authoritative slot state.

Migration derives `spellcaster_enabled = ON` only when existing Quick Magic state is meaningful:
- spell save DC exists; or
- spell attack modifier exists; or
- spellcasting ability is not `NONE`; or
- at least one configured slot level has `total_slots > 0`.

Migration does not invent spellcasting sources or conceptual spells.

### C2 — shared domain/repository mapping

`CharacterSheet` and `CharacterRepository` now round-trip the new domains transactionally.

Important invariants:
- caster OFF persists as visibility/state only and does not delete spell, source, or slot data;
- source ↔ class linkage is a soft ID link;
- deleting a linked class unlinks the source on the next save rather than deleting spellcasting data;
- one conceptual spell may have multiple source associations;
- `prepared` is stored independently per spell-source association;
- spell/source association replacement is explicitly cleaned before replacing spells/sources, so repeated saves do not depend on SQLite FK cascade enforcement.

### C3 — persistence/migration tests

Dedicated next-build foundation coverage includes:
- new-character defaults;
- full narrative/trait/spell/note round-trip;
- multiple spellcasting sources;
- per-source prepared state;
- caster ON → OFF non-destructive persistence;
- class deletion soft-unlink behavior;
- migration caster-state ON/OFF derivation;
- preservation of existing run-#180 regression coverage.

## Corrective history retained

Gate C initially exposed stale spell-source association rows during repeated saves when FK cascades were not explicitly enabled. A trigger-based cleanup attempt was rejected because the current SQLDelight parser rejects SQLite trigger pseudo-row references such as `OLD.id`. The final accepted correction is the explicit character-scoped cleanup query plus the one repository call promoted at `c4871f30bce9d76440253d7e335026c59c236239`.

## Recovery instruction

If work must be resumed after hardware/chat failure, use this closure checkpoint as the authoritative recovery marker for Increment C. Increment D may begin from the commit created by this closure document.
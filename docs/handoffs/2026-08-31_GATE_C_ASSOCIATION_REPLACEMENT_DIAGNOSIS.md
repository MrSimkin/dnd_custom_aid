# Gate C — spell/source association replacement diagnosis

**Status:** Diagnosed; fix required before Gate C can close  
**Date:** 2026-08-31  
**Failing checkpoint:** `2e3873b7fe7fb0760e8a961be21711e31562b5fa`  
**Workflow:** run #239 / ID `33455722828`

## CI result

- backend: PASS;
- Kotlin/common compilation: PASS;
- Android compilation progressed successfully;
- `shared:desktopTest`: FAIL, exactly one new test failure.

Failing test:
`CharacterNextBuildFoundationTest.nextBuildDomainsRoundTripWithMultiSourcePreparedStateAndSoftClassUnlink`

The exception occurs at the second `saveCharacter` call, when the same persisted caster is saved with `spellcasterEnabled = false` after a successful first save/hydration.

This localizes the defect to repeated replacement persistence rather than schema generation, first-save mapping, or migration execution.

## Root cause

Current replacement sequence deletes conceptual spell rows and spellcasting-source rows, then reinserts them and their associations. Cleanup of `character_spell_source_assoc` currently relies only on `ON DELETE CASCADE` foreign-key behavior.

The desktop in-memory repository test setup creates the SQLDelight schema but does not explicitly enable SQLite foreign-key enforcement for the test connection. Therefore relying on cascade cleanup is not robust: old association rows may survive parent deletion and collide when the same `(spell_id, source_id)` associations are reinserted on a subsequent save.

The repository should not depend on connection-specific FK pragma state for its full-replacement save strategy.

## Corrective direction

Add an explicit character-scoped association-delete query and invoke it before replacing spells/sources:

1. delete all spell-source associations whose spell belongs to this character;
2. delete conceptual spells;
3. delete spellcasting sources;
4. reinsert sources, spells and their approved associations.

Foreign-key cascades remain valid defense-in-depth/true ownership behavior, but repeated save correctness no longer depends on them being enabled.

## Scope

This correction does not change product semantics, schema ownership, or migration rules. It hardens the already-approved transactional replacement implementation.

Increment D remains blocked until the corrected Gate C run passes.

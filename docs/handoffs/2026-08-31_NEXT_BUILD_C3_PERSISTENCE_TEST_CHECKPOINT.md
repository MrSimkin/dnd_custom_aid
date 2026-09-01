# Next-build C3 — persistence/migration test checkpoint

**Status:** C3 tests implemented; awaiting Gate C CI  
**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Added test surface

New file:
`shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterNextBuildFoundationTest.kt`

The existing `CharacterFollowupFoundationTest.kt` remains unchanged so run-#180/follow-up regression coverage is preserved independently.

## Test cases

### 1. New-PC defaults
Proves a newly created PC starts with:
- `spellcasterEnabled = false`;
- empty `CharacterBackground`;
- no Rasgos;
- no spellcasting sources;
- no conceptual spells;
- blank general notes;
- no titled note cards;
- no spell slots.

### 2. Full next-build round trip
Creates and saves:
- narrative background;
- two Rasgos with different type/activation/usage shapes;
- a class-linked spellcasting source plus a custom feat source;
- a conceptual spell associated with both sources with different source-specific `prepared` values;
- a cantrip;
- general notes and ordered titled notes;
- shared pre-existing spell-slot state.

Then verifies exact save/reopen hydration.

The same test also proves:
- turning caster visibility OFF preserves sources/spells/slots;
- removing the linked class causes the source to become unlinked rather than deleted;
- conceptual spells and their valid source associations survive the class removal.

### 3. Run-#180-schema migration derivation
Builds an explicit post-migration-3 / schema-version-4 database and migrates it through `4.sqm`.

Five PCs separately cover:
- spell save DC signal;
- spell attack modifier signal;
- spellcasting ability signal;
- configured spell-slot signal;
- no spellcasting signal.

The test verifies the first four migrate caster ON, the mundane PC remains OFF, existing Quick Magic/slot values remain intact, and every new narrative/trait/source/spell/note domain remains empty rather than inferred.

## Expected Gate C result

Gate C must pass:
- existing tests;
- new default/round-trip/soft-link tests;
- migration 4 execution;
- SQLDelight generation;
- Android/desktop compile and APK build.

Any failure blocks Increment D until diagnosed, checkpointed and corrected.

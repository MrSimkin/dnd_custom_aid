# Gate C — association trigger fix checkpoint

**Status:** Corrective persistence hardening implemented; awaiting rerun  
**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Implemented

Fresh database schema now includes two delete triggers:
- `character_spell_delete_source_assoc`;
- `character_spell_source_delete_assoc`.

These remove spell-source association rows whenever their true owner spell or source is deleted, independently of connection-level foreign-key pragma state.

Migration `5.sqm` installs the same triggers for databases upgraded from earlier schema versions. Run-#180 schema version 4 therefore migrates through:
- `4.sqm` — new domains + caster migration;
- `5.sqm` — association cleanup integrity hardening.

## Files/commits

- temporary explicit-query file removed at `31235f1aa05e2a103edf2394d5d52765c7635986`;
- fresh-schema trigger file `ZCharacterAssociationCleanup.sq` added at `4073b30c7223fc2bc064f172e159fc50e0998b0c`;
- migration `5.sqm` added at `302fe7ffb7fc7d9f97c12721c6cc529a45e2c784`.

No repository/domain/API behavior changed.

## Gate

Rerun the full C3 test package. The previously failing second-save path must now pass without enabling foreign keys artificially in the test driver.

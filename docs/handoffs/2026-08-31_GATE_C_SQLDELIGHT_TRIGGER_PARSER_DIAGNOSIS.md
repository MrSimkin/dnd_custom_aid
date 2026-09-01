# Gate C — SQLDelight trigger parser diagnosis

**Status:** Diagnosed; trigger strategy rejected, explicit repository cleanup required  
**Date:** 2026-08-31  
**Failing checkpoint:** `f31f72574d253ff4ecf3d074682faf6d8b905cf9`  
**Workflow:** run #246 / ID `33456186756`

## CI result

- backend: PASS;
- Kotlin job: FAIL during SQLDelight interface generation before tests could run.

SQLDelight errors:
- `ZCharacterAssociationCleanup.sq:5:21 No table found with name OLD`;
- `ZCharacterAssociationCleanup.sq:12:22 No table found with name OLD`.

The current SQLDelight SQLite 3.18 parser does not accept SQLite trigger pseudo-row references such as `OLD.id` in the schema `.sq` source used for generated interfaces.

## Consequence

The trigger-based hardening strategy cannot be used in this project in its current SQLDelight configuration. Increment D remains blocked; Gate C is still open.

## Corrective direction

Return to the previously identified explicit replacement cleanup:

1. remove the failed fresh-schema trigger file;
2. remove migration `5.sqm`, which existed only for the rejected trigger strategy;
3. add a separate SQLDelight query file containing a character-scoped association delete:
   `DELETE FROM character_spell_source_assoc WHERE spell_id IN (SELECT id FROM character_spell WHERE character_id = ?)`;
4. call that query transactionally before deleting/replacing conceptual spells and spellcasting sources;
5. preserve the existing FK cascade declarations as defense in depth, but do not rely on connection-level FK pragma state for repeated-save correctness.

## Scope

No product behavior, ownership rule, migration-4 behavior, or shared-domain model changes. This is an implementation correction only.

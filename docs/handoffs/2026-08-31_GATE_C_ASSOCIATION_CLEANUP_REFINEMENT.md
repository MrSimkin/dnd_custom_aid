# Gate C — association cleanup strategy refinement

**Status:** Corrective implementation refined before code change  
**Date:** 2026-08-31  
**Previous diagnosis:** `b21bf259f744e10b1a7dc7ef25a277ab7d5ab660`

## Additional verification

`AndroidDatabaseFactory` constructs a normal `AndroidSqliteDriver` and does not explicitly enable SQLite foreign-key enforcement in application code.

Therefore, making only the desktop test connection enable `PRAGMA foreign_keys=ON` would hide a robustness issue rather than fix it.

## Refined corrective implementation

Instead of coupling association cleanup only to one repository save sequence, enforce true ownership at the database layer with SQLite delete triggers:

- deleting a `character_spell` deletes all `character_spell_source_assoc` rows for that spell;
- deleting a `character_spell_source` deletes all association rows for that source.

This makes association cleanup deterministic even when FK enforcement is disabled on a connection.

The existing foreign-key `ON DELETE CASCADE` declarations remain valid defense in depth when FK enforcement is enabled.

## Migration consequence

Because the next-build schema/migration checkpoint already added `4.sqm`, add the trigger hardening as migration `5.sqm` rather than rewriting the recorded migration checkpoint in place. Existing run-#180 schema version 4 will migrate through both 4 and 5 to the current schema.

Fresh databases receive the same triggers from the schema source.

## Scope

No product/domain behavior changes. This is persistence integrity hardening for the already-approved ownership rules.

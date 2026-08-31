# D-0062 — Next-build migration and defaults

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

Before implementing the enlarged post-run-#180 character sheet, the owner approved the migration/default behavior for existing characters and new PCs.

The current persisted character model contains no general Background, Traits, or general Notes fields that need to be redistributed. Existing `notes` fields belong specifically to combat entries and inventory items and remain authoritative in those domains.

## Approved migration/default package

1. Existing run-#180 character data remains semantically unchanged through migration.
   - Core stats, classes, saves, skills, PB adjustment, Quick Magic values, spell slots, combat entries, inventory, currencies and their existing ordering/state are preserved.
   - The migration adds the new domains/state rather than reinterpreting unrelated existing data.

2. Existing PCs receive empty new domains by default.
   - `Trasfondo` fields start blank.
   - `Rasgos` starts empty.
   - `Notas generales` starts blank.
   - titled Notes cards start empty.
   - conceptual spells and spellcasting sources start empty unless created after migration.
   - Do not invent content by copying unrelated fields.

3. Character image support in this build is UI-reserved but not yet persistently implemented.
   - `Trasfondo` shows the two approved character-image placeholders/slots.
   - Do not write dummy file paths or fake attachment records.
   - A proper persistent image model will be added only when real image attachment is implemented.

4. `Lanzador de conjuros` migration for existing PCs is derived from meaningful existing spellcasting state.
   - Migrate to ON if any of the following is present:
     - non-null spell save DC;
     - non-null spell attack modifier;
     - spellcasting ability other than `NONE`;
     - at least one configured spell-slot level with slots.
   - Otherwise migrate to OFF.
   - The purpose is to preserve visibility for characters already using Quick Magic.

5. Do not automatically create a spellcasting source during migration.
   - Existing Quick Magic data does not reliably identify whether its source is a class, feat, innate magic, item, homebrew source, etc.
   - A migrated caster may therefore have `Lanzador de conjuros = ON` with `Conjuros -> Todos` initially empty until the owner/player deliberately creates source(s) and spell records.

6. New PCs default to:
   - empty `Trasfondo`;
   - empty `Rasgos`;
   - empty `Notas`;
   - no spellcasting sources;
   - no spell records;
   - `Lanzador de conjuros = OFF`;
   - two empty image placeholders visible in `Trasfondo`.

7. Turning `Lanzador de conjuros` OFF after data exists remains fully non-destructive.
   - It hides Quick Magic and the `Conjuros` tab.
   - It never removes spell sources, spell records, source associations, prepared state, spell slots, or Quick Magic data.
   - Turning it back ON restores access to the same persisted state.

## Migration principle

When the old model lacks enough information to infer a new-domain concept safely, preserve the old data exactly and initialize the new concept empty rather than guessing.

## Consequence

The migration/default design gate is closed. The next design gate is the cross-domain ownership audit, followed by responsive/navigation consolidation and the final checkpointed implementation package.

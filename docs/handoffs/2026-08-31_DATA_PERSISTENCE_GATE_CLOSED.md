# Checkpoint — Data/persistence gate closed

**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

The owner answered and approved all nine persistence/data-shape questions for the follow-up character-sheet build.

Authoritative new decision: `docs/decisions/D-0052_CHARACTER_SHEET_DATA_AND_MIGRATION_RULES.md`.

Key correction versus the assistant's proposal: **custom currencies are character-level, not campaign-level**. Each PC has the five default currencies and may define its own additional currencies.

Other approved points include one inventory model with optional special-item detail, imperial canonical structured measurements with approximate metric display, compact Quick Magic active slot levels, manual spellcasting ability selector, proficiency-bonus migration through `Ajuste adicional`, preservation/move of existing spell-save DC, manual persistent ordering, and independent `Equipado`/`Ubicación` state.

**No production code has been changed.**

Next step: draft and review the consolidated implementation specification + migration plan + targeted QA checklist before coding.
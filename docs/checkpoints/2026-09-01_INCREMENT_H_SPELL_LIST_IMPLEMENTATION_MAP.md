# Increment H — Conjuros spell list/details implementation map

**Date:** 2026-09-01  
**Working branch:** `tmp/increment-h-spell-list`  
**Baseline:** `d71a47822bcbeb0d0aa9ba404ba4fd01af81b510` — Increment G closed after green Gate G.

## Recovery finding

The previous work completed Increment G cleanly. No Increment H production implementation or checkpoint exists after that closure. The current data foundation already persists the complete approved conceptual-spell model, spellcasting sources, many-to-many source associations, source-specific `Preparado`, and spell `sortOrder`.

Therefore Increment H does not require a schema migration.

## Approved H scope

Implement the approved `Conjuros` spell-list/details behavior from the consolidated package and D-0058/D-0059/D-0063/D-0064:

- conceptual spells grouped as `Trucos`, then `Nivel 1` through `Nivel 9`;
- collapsible level sections, with empty levels de-emphasized/collapsed;
- compact search constrained to the selected `Todos` or source view;
- add/edit/delete conceptual spell records;
- one or more source associations per spell;
- source-specific `Preparado` state;
- `Todos` displays source-specific prepared indicators rather than a misleading universal checkbox;
- manual drag ordering within each spell level;
- multi-source spells appear once in `Todos`;
- conceptual spell owns one level; associations do not own level;
- all approved spell fields remain permissive/manual;
- editor remains IME-safe and dismissal must not silently discard draft state.

Quick Magic/shared spell-slot UI remains Increment I and is not part of H.

## Technical approach

1. Keep `CharacterSpellsTabV4.kt` focused on source navigation/management and make only a narrow integration edit there.
2. Add a separate `CharacterSpellListV4.kt` containing the H list, grouping/filtering/search, spell editor, prepared controls and drag ordering. This avoids risky large-file replacement and preserves the Increment G source manager.
3. Reuse the existing `CharacterSpellcastingDraftV4` transaction already wired into the central character Save action; do not introduce duplicate persistence state.
4. Add focused shared persistence regression tests for multi-source spell details, prepared state, manual order, update and deletion behavior.
5. Run the standard branch CI. A failure blocks promotion.
6. After a green Gate H, write the Increment H closure checkpoint and fast-forward `implementation/character-data-foundation` only to the verified H head.

## Exact next step

Implement the isolated H spell-list component and focused persistence test on this branch, integrate it with the existing source-management tab, then run CI.
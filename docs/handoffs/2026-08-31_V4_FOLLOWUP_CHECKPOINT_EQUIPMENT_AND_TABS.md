# V4 follow-up checkpoint — equipment and tabs

**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`  
**Purpose:** short recovery checkpoint required by the project operating rule that every meaningful step must leave a durable Git checkpoint.

## Just approved

### Special / magic equipment

Owner approved all five proposed interaction/data directions:

1. `Equipo especial` is a richer mode/presentation of the same underlying inventory concept, not a disconnected ownership system.
2. Phone presentation uses a compact card/row showing primarily item name, optional location and Sintonización indicator; tap/expand opens full detail/editor.
3. Show an informational Sintonización count such as `Sintonizados: 2`, but do not present/enforce a restrictive `2 / 3` limit and do not block additional attuned items.
4. `Ubicación` is optional; common paper-sheet locations are available plus `Otro` / custom location.
5. Special items retain ordinary inventory fields where relevant (`Cantidad`, optional `Peso`, equipment state where nonredundant, `Notas`) plus optional `Ubicación`, manual `Sintonizado` and long `Descripción`; entered weight contributes to the common carried-weight total.

Durable decision updated in:

- `docs/decisions/D-0050_COMBAT_EQUIPMENT_AND_MEASUREMENT_DIRECTION.md`

Relevant commit:

- `10a659c10804f033246ecda2256bb504ffef8d23` — `Finalize special equipment interaction decisions`

### Character-sheet tab order

Owner approved exact next-build order:

1. `General`
2. `Habilidades`
3. `Combate`
4. `Equipo`

`General` replaces/renames the current `Resumen` tab rather than adding a fifth tab.

Quick Magic remains at the bottom of `General` as previously approved.

Durable decision:

- `docs/decisions/D-0051_CHARACTER_SHEET_TAB_ORDER.md`

Relevant commit:

- `5fa9e28a0d82c697796645292f4d901e87d5f151` — `Approve four-tab character sheet navigation`

## Current implementation authorization

No production follow-up coding has started or is authorized yet.

## Next discussion gate

Resolve persistence/data-shape decisions that would otherwise be implicit during coding, including at least:

- ordinary vs special inventory representation and conversion;
- custom currency definition scope;
- canonical unit storage and derived imperial/metric presentation;
- Quick Magic slot-row visibility and exact manual spellcasting-reference fields;
- migration behavior for newly derived proficiency bonus and new domains;
- final Combat entry persistence shape where consequential.

After those answers are checkpointed, produce the implementation plan and targeted QA specification before coding.
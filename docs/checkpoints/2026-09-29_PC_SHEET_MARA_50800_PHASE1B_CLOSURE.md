# Mara 50800 — Phase 1B closure / handoff to Phase 2A

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Validated renderer head:** `a5ac8d5edbcc1184ecc8dc9496db8933b029a73f`  
**Workflow:** 4200 / `36509536738`  
**Proof artifact:** `pc-sheet-populated-template-proofs` / `11009076550`  
**Status:** PHASE 1B PROGRESSION PASS / NOT FINAL OWNER ACCEPTANCE

## Authority

This closure is subordinate to:

- `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md`;
- `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`;
- `docs/checkpoints/2026-09-29_PC_SHEET_MARA_50800_REPAIR_EXECUTION_LOG.md`.

It is not permission to mark the complete 50800 repair fixed.

## What was demonstrated on exact real-Mara PDFs

Custom v1 generates.

Across the four real-Mara outputs:

- ordinary Equipment does not show weight;
- ordinary Equipment does not show `Consumible`;
- ordinary Equipment does not show prose descriptions;
- compact equipment identity remains present;
- Custom v1/v2 use native Equipment / Equipo Especial source grammar rather than the rejected generic Inventory page;
- Custom-v2 continuation uses source page 3 (Equipment/Trasfondo), not either main-sheet variant;
- custom location `Bolsa lateral` is visibly placed in the native location column;
- the location cleanup no longer damages the native checkbox;
- the final Special Equipment item is preserved.

## Matrix disposition for progression

Phase-pass candidates:

- M50800-02;
- M50800-15;
- M50800-16;
- M50800-18;
- M50800-19.

These must still be reconfirmed on the final exact candidate before owner handoff.

Still OPEN:

- M50800-17 — long Equipment/Equipo Especial semantic atomicity/readability;
- M50800-20 — adaptive reclaim / redundant scaffold elimination.

All M50800-03..14 and M50800-21..29 not otherwise listed remain OPEN according to the acceptance matrix.

## Why Phase 1B stops here

Further work on row/page reclaim, semantic atomicity and empty sibling scaffolds belongs to later layout phases. Continuing to modify those now would enlarge the phase and violate the requested discrete-fix workflow.

## Phase 2A handoff

Phase 2A is deliberately narrow and must use the **real 50800 Custom-v2 pages 1, 5 and 6** as its visual reference.

Scope:

- M50800-03 source underlay;
- M50800-04 native Custom Statistics grammar;
- M50800-05 four Mara custom attributes together using page-1 capacity;
- M50800-06 no phantom attribute shells;
- M50800-07 clean `Éter`;
- M50800-25 name ribbon centering / two-line fallback.

Before Phase 2A code:

1. reopen the 50800 Atributo/Habilidad page-1 and Custom Statistics evidence;
2. compare current Extended implementation with the page-1 native attribute implementation;
3. preserve per-Attribute vs per-Ability semantic differences while reusing the same native attribute visual grammar;
4. do not touch Traits, Notes or general adaptive packing in Phase 2A.

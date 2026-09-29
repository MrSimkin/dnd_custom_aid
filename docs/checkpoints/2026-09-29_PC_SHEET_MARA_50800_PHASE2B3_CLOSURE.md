# Mara 50800 — Phase 2B.3 residual Equipment progression closure

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Validated renderer/code head:** `49afb6062de99a380b2b64673c28498f6cb4e147`  
**Workflow:** 4252 / `36597017691` — SUCCESS  
**Exact proof artifact:** `pc-sheet-populated-template-proofs` / artifact ID `11046448289`  
**Artifact digest:** `sha256:25229a0a606ab5fd733870018945051a6f7185df37c96bcb85be7c976e0b99f4`  
**Status:** PHASE 2B.3 INTERNAL PROGRESSION PASS / MASTER MATRIX REMAINS OPEN  
**Next:** Phase 3 — Custom-v2 Combat/Actions + Resources/Options + broader adaptive reclaim

## Authority retained

This closure is subordinate to the owner QA from `0.5.0-preqa.8 / 50800` and the 32-item acceptance matrix.

Phase 2B.3 was restricted to:

- M50800-17 — ordinary Equipment compact identity remains visually atomic/readable;
- M50800-20 — exhausted Equipment/Equipo Especial streams do not leave redundant continuation scaffolds.

M50800-15/16/18/19 were treated as regression guards only.

## Original 50800 defect rechecked

Owner 50800 showed two residual Equipment problems after the earlier native-reuse repair:

1. ordinary Equipment identities could be fragmented across physical rows/columns as a flat line stream;
2. a later continuation page could contain only useful Equipo Especial content while still reproducing the full Equipment/Trasfondo source page around it, leaving most of the page as dead scaffold.

The Phase 2B.3 pre-code map re-opened these observations before implementation.

## Implementation result

### M50800-17 — item-aware native Equipment packing

Ordinary Equipment compact labels are now packed as item groups rather than an anonymous list of wrapped lines.

- all wrapped rows belonging to one item remain together;
- a multi-row item moves to the next native column/page when it cannot fit intact in the remaining rows;
- source sort order is preserved;
- compact Equipment semantics remain unchanged: no weight, no `Consumible`, no ordinary prose descriptions.

### M50800-20 — surviving native-module continuation

The continuation renderer now chooses the native component surface according to the streams that actually survive.

For the real Mara case in run 4252, only Equipo Especial survives beyond the base page. The generated continuation therefore reuses the native Equipo Especial component at the top of a clean page rather than reproducing an empty Equipment/Trasfondo scaffold.

This is source-component reuse, not a generic Inventory redesign.

Desktop and generated Android renderers remain synchronized.

## Exact artifact review — run 4252

Artifact downloaded and visually inspected from the exact successful workflow.

Real Mara Custom-v2 Atributo and Habilidad each render 19 pages. Page count is observation only, never an acceptance target.

### Base Equipment page

Rendered page 2 shows:

- native two-column Equipment grammar retained;
- long compact identities such as `Frasco de tinta que recuerda la última palabra escrita 2` occupy consecutive native rows as one readable item group;
- item groups do not jump across the column boundary;
- no weight, `Consumible` or ordinary prose description is visible;
- Equipment/Trasfondo/Equipo Especial source grammar remains intact.

### Special-only continuation

Rendered page 18 shows:

- only the native `EQUIPO ESPECIAL` module is carried forward;
- the previous empty upper Equipment/Trasfondo scaffold is gone;
- `Llave sin cerradura de latón ennegrecido 22` and `Cuaderno de fórmulas personales y mapas plegables 29` remain present;
- `Bolsa lateral` is visibly rendered in the native location column;
- native checkboxes remain intact;
- no generic `INVENTARIO / EQUIPO` replacement is introduced.

PDF text extraction does not reliably expose the custom-location cell; rendered output remains the authority for M50800-19, consistent with earlier artifact QA.

## Regression scan

Exact PDFs preserve the Phase 1B semantics:

- no `Consumible`;
- no equipment `lb` values;
- no ordinary prose description `Descripción suficientemente larga del objeto 2.`;
- final Special Equipment semantic tail `objeto 29` remains present;
- Equipo Especial continues to use the native component.

## Phase disposition

Internal progression candidates with positive exact-artifact evidence:

- M50800-17;
- M50800-20.

These remain formally OPEN in the master matrix until the final exact promoted candidate is generated and the complete 50800 acceptance ledger is re-run.

No owner APK is authorized yet. Do not merge.

## Next bounded work

Resume at **Phase 3**.

Primary 50800 items:

- M50800-12 — Custom-v2 Combat/Actions table readability and packing;
- M50800-14 — Resources/Options reclaim after one stream is exhausted;
- M50800-27 — broader adaptive continuation architecture across surviving streams.

Traits, Notes and residual Equipment should not be reopened unless a new exact-artifact regression is observed.

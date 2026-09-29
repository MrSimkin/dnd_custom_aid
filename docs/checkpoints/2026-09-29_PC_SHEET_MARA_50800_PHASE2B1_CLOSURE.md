# Mara 50800 — Phase 2B.1 native Traits + narrative progression closure

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Validated renderer/code head:** `adb01dd851286419409e7f8dcc349e329f903c51`  
**Workflow:** 4230 / `36582233207` — SUCCESS  
**Exact proof artifact:** `pc-sheet-populated-template-proofs` / artifact ID `11041040671`  
**Artifact digest:** `sha256:64b29126119ca6e93aa2bd351cf2a2d0cdcdcc189791291bd106b6acb4d54240`  
**Status:** PHASE 2B.1 INTERNAL PROGRESSION PASS / MASTER MATRIX REMAINS OPEN  
**Next:** Phase 2B.2 — Notes native reuse / M50800-21..24

## Authority retained

This closure is subordinate to the real owner QA from `0.5.0-preqa.8 / 50800` and the 32-item acceptance matrix.

Phase 2B.1 was restricted to:

- M50800-08 Traits/Rasgos native/source reuse;
- M50800-09 intentional category grouping + coherent order inside each category;
- M50800-10 adaptive Traits space reclaim / no repeated irrelevant scaffold;
- M50800-11 Trasfondo/Historia native/source narrative reuse.

No Notes, residual Equipment, Combat, Resources, Fantasy or owner-candidate work is implicitly accepted here.

## Original 50800 defect rechecked

Owner 50800 showed:

- Atributo Traits pages 7–18 and Habilidad pages 6–17 repeatedly reproduced the same multi-panel scaffold;
- large category panels became empty while detail text remained trapped in a small right-side area;
- category grouping was explicitly accepted; the defect was layout, native-reuse and space reclaim;
- owner clarification required Traits/Rasgos and Trasfondo/Historia to reuse existing sheet grammar rather than generic Extended reconstruction.

Run 4212 still reproduced the fixed-scaffold failure.

## Exact 4230 Traits artifact result

Real Mara Custom-v2 Atributo and Habilidad both now use five Traits continuation pages (pages 6–10). Page count is evidence only, not an acceptance target.

Rendered inspection demonstrates:

- native two-column / 17 pt ruled-row grammar is used instead of the rejected six-panel continuation;
- both physical columns are actively consumed by trait records;
- the old repeated empty `OTROS RASGOS`, `DETALLES / NOTAS` and equivalent fixed panels are absent;
- category grouping is explicit and readable;
- source `sortOrder` remains coherent inside each accepted category;
- semantic record boundaries are visible: trait name, metadata, body and note content remain associated;
- all 26 Mara trait identities remain preserved across the complete PDF;
- the final Traits page consumes remaining Traits/proficiency/reference streams without creating another empty Traits page.

A 4228 artifact pass first exposed an imported-source-font subset issue: slash/dot separators existed in the text layer but were visually missing. The generated headings were therefore moved to complete embedded Fira typography. 4230 visibly preserves `CLASE / DOTES`, `COMPETENCIAS / IDIOMAS` and the `· CONTINUACIÓN` title separators.

## Exact 4230 Trasfondo/Historia evidence

The real Mara payload does not require extra narrative overflow beyond the base page, so Phase 2B.1 additionally emits an exact workflow proof using Mara with deliberately stressed narrative lengths:

`mara-50800-phase2b1-background-overflow.pdf`

The base module authority remains the Custom-v2 Equipment/Narrative page with native `TRASFONDO`, `VÍNCULOS`, `IDEALES` and `HISTORIA` sections.

Run 4229 exposed a real artifact-QA packing defect: long Historia continuation was treated as one large block, leaving free rows in the previous column and producing a second continuation page with only three lines. That result was rejected.

Run 4230 repairs the issue by splitting long narrative sections only at native row boundaries and repeating section identity on continuation. The exact rendered proof now shows:

- one narrative continuation page, not two;
- left column: remaining Trasfondo, Vínculos, Ideales and the first Historia continuation fragment;
- right column: `HISTORIA · CONTINUACIÓN 2`;
- Historia tail through `Historia adicional 60` remains present;
- both columns are consumed before another page could be allocated;
- no generic Traits `DETALLES / NOTAS` panel is used for narrative overflow;
- heading hierarchy, section identity, native ruled rhythm and readable body typography are preserved.

## Phase disposition

Internal progression candidates with positive exact-artifact evidence:

- M50800-08;
- M50800-09;
- M50800-10;
- M50800-11.

These are **not final FIXED statuses**. They remain formally OPEN in the master matrix until the final exact promoted candidate is generated and the entire acceptance ledger is re-run.

No owner APK is authorized yet. Do not merge.

## Next bounded work

Resume at **Phase 2B.2 — Notes native reuse**, primary items:

- M50800-21;
- M50800-22;
- M50800-23;
- M50800-24.

Before code:

1. reopen the real 50800 Notes observations;
2. inspect the current exact artifact Notes pages;
3. map each Notes item to the existing native Notes grammar, implementation surface, regression and visual evidence;
4. only then implement.

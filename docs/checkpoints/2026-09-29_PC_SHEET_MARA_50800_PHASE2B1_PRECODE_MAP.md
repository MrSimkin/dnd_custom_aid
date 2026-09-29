# Mara 50800 — Phase 2B.1 pre-code QA/source map

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Status:** ACTIVE / BLOCKING PRE-CODE MAP FOR PHASE 2B.1  
**Acceptance authority:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`  
**Safe-pause authority:** `docs/checkpoints/2026-09-29_PC_SHEET_MARA_50800_END_OF_DAY_HANDOFF.md`

## 1. Scope

Phase 2B.1 is restricted to:

- M50800-08 — Traits/Rasgos native/source reuse;
- M50800-09 — intentional category grouping + coherent order inside each category;
- M50800-10 — Traits continuation space reclaim / no repeated irrelevant scaffold;
- M50800-11 — Trasfondo/Historia native/source narrative reuse.

This phase does **not** include Notes M50800-21..24, Equipment residual M50800-17/20, Combat M50800-12, Resources M50800-14, broad cross-stream adaptive packing M50800-27, Fantasy-specific work, candidate versioning or owner handoff.

## 2. Real 50800 observations reopened

The owner-facing `0.5.0-preqa.8 / 50800` runtime remains the acceptance source.

For Custom-v2 Traits/Rasgos:

- owner Atributo pages 7–18 and Habilidad pages 6–17 repeatedly emitted the same fixed multi-panel `RASGOS Y ATRIBUTOS` scaffold;
- once category/list areas were exhausted, large regions remained empty while trait detail/continuation text stayed constrained to a small right-side area;
- category grouping itself is intentional and accepted;
- the remaining defect is presentation/layout: preserve coherent order inside each category, preserve record boundaries, reuse the existing trait/rasgo grammar, and stop wasting pages through repeated fixed scaffolds.

The exact current run-4212 artifact reproduces the same failure mode. In Mara Custom-v2 Atributo, page 9 has essentially empty `CLASE / DOTES`, `RAZA / TRASFONDO / OTROS`, `OTROS RASGOS` and `COMPETENCIAS / IDIOMAS` regions while ongoing trait text is still restricted to `DETALLES / NOTAS` and `CONTINUACIÓN`.

For Trasfondo/Historia:

- owner clarification explicitly rejects rebuilding a generic Extended narrative component where the sheet already has native `TRASFONDO`, `VÍNCULOS`, `IDEALES` and `HISTORIA` modules;
- continuation must preserve the existing heading hierarchy, font roles, ruled-writing rhythm and section identity, then add capacity adaptively.

## 3. Existing native/source references

### Traits/Rasgos

The approved Custom-v2 main sheet already provides the native grammar:

- page-1 `RASGOS Y ATRIBUTOS` block;
- two native columns;
- 17 pt ruled-row cadence;
- compact semantic trait names;
- source-family typography/gray-band rhythm.

Authority in code:

- `DesktopPcSheetTemplateProofRenderer.drawCustomV2Common`;
- `V2_TRAIT_RULE_Y`;
- corresponding generated Android renderer.

The current Extended six-panel page is **not** the source reference. It is the rejected reconstruction.

### Trasfondo/Historia

The approved Custom-v2 Equipment/Narrative base page provides:

- `TRASFONDO`;
- `VÍNCULOS`;
- `IDEALES`;
- `HISTORIA`;
- measured 17 pt ruled rows and family typography.

Authority in code:

- `DesktopCustomV2SharedBaseRenderer.drawBackground`;
- `BACKGROUND_RULES`, `BONDS_RULES`, `IDEALS_RULES`, `STORY_RULES`;
- corresponding generated Android renderer.

## 4. Current implementation mismatch

`DesktopCustomV2ExtendedRenderer.appendTraitsExtendedPages/renderTraitsPage` currently constructs a new six-panel page:

- `CLASE / DOTES`;
- `RAZA / TRASFONDO / OTROS`;
- `OTROS RASGOS`;
- `DETALLES / NOTAS`;
- `COMPETENCIAS / IDIOMAS`;
- `CONTINUACIÓN`.

Every continuation page recreates those regions even when most streams are empty. This is the exact fixed-scaffold defect recorded by M50800-10.

`traitSupplementLines` also routes Trasfondo/Vínculos/Ideales/Historia overflow into the generic trait-detail stream, losing the native narrative-module identity required by M50800-11.

## 5. Intended repair

### 5.1 Traits/Rasgos

Replace the fixed six-panel continuation with an adaptive two-column native-row stream derived from the page-1 trait grammar.

Rules:

1. preserve category grouping;
2. preserve `sortOrder` inside each category;
3. each trait is an atomic semantic record whenever it fits one native column;
4. use the native 17 pt row cadence and two-column physical grammar;
5. retain complete trait identity, source/activation/use metadata, description and notes;
6. after one category ends, the next category consumes the next available rows/column — no reserved empty category panel;
7. once one column fills, continue in the other column; once both fill, create another native continuation page;
8. do not recreate the rejected six-panel scaffold;
9. proficiencies/reference lines that must remain in the Traits-family output are appended as adaptive native-row sections, not reserved fixed panels.

### 5.2 Trasfondo/Historia

Remove background narrative overflow from the generic Traits detail stream.

Render only real overflow from the base page's measured capacities:

- Trasfondo: base capacity 3 rows;
- Vínculos: base capacity 3 rows;
- Ideales: base capacity 3 rows;
- Historia: base capacity 11 rows.

Continuation uses native narrative modules with the same section identities, heading hierarchy, font roles and 17 pt ruled rhythm. Only sections with actual overflow are emitted. Surviving sections reclaim available column/page space; unrelated Equipment/Special Equipment scaffolds are not copied merely to obtain the narrative component.

Personality/flaw/religion and Notes-specific record behavior remain outside this subphase and stay routed for Phase 2B.2 unless direct artifact evidence proves they are part of the same native background module.

## 6. Planned regressions

Focused real-Mara assertions for both Custom-v2 families:

- all 26 trait identities remain present;
- category grouping is deterministic and preserves source `sortOrder` inside each category;
- Extended output no longer contains the rejected six-panel labels on later continuation pages;
- a late Traits continuation page uses both available native columns before another page is added;
- no page is emitted solely to preserve an exhausted category panel;
- background continuation preserves explicit `TRASFONDO`, `VÍNCULOS`, `IDEALES`, `HISTORIA` identities when those fields overflow;
- background overflow no longer appears as generic trait-detail text;
- Desktop/Android generated renderer sync remains exact.

These checks support, but do not replace, artifact inspection.

## 7. Artifact gate

After implementation:

1. CI must be green;
2. inspect the exact `pc-sheet-populated-template-proofs` artifact from that run;
3. inspect real Mara Custom-v2 Atributo and Habilidad pages;
4. compare the rendered result directly with the 50800 observations above;
5. mark M50800-08/09/10/11 only as **phase progression candidates** if the exact artifact demonstrates the intended behavior;
6. keep all four master-matrix items formally OPEN until final exact-candidate acceptance.

Any new clipping, record splitting, lost trait, broken category order, background identity loss, excessive compression or new empty scaffold blocks progression to 2B.2.

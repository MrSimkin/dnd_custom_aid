# Checkpoint — Custom v1 Extended Run 2 — PASS FOR OWNER REVIEW

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Candidate implementation commit:** `b244159b467d162c8637db5532dcfe6f2f831953`  
**Frozen Custom-v1 base commit:** `7448693e36ee1b26243bd4091615dba725e95027`  
**Scaffold push run:** `35484718817` / run #2817 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10597183316`  
**Proof PDF:** `custom-v1-complete-family-extended-run2.pdf`

## Why Run 2 exists

Custom-v1 Extended Run 1 was owner-rejected because it copied superficial visual motifs without preserving the actual Custom-v1 design system.

The owner specifically called out:

- spaces/capacity;
- page composition / layout (`maquetación`);
- fonts;
- box construction;
- overall family resemblance.

Run 2 therefore abandons the Run-1 extension layout and rebuilds from the owner-authored source pages themselves.

## Source-faithful design method

The owner PDF is treated as the extension design kit rather than merely a style reference.

The six extension roles are mapped to authentic source-page composition:

1. **Custom Statistics** — source page 1 Attribute grammar, six-column rhythm and ornamental score/modifier boxes;
2. **Traits & Features** — source page 3 narrative/ruled composition;
3. **Resources & Options** — source page 2 equipment/table architecture;
4. **Inventory / Equipment** — direct continuation of source page 2;
5. **Spells** — direct continuation of source page 4;
6. **Notes** — direct continuation of source page 5 two-column notes + grid architecture.

EnchantedLand headings are loaded from the font embedded in the owner PDF. Generated arbitrary character text uses the already-approved Custom-v1 overlay fonts where the source PDF's subset font cannot safely typeset new glyphs.

## Complete owner-review proof

The proof contains 11 Letter pages:

1. frozen Main;
2. frozen Equipment;
3. frozen Narrative / Background;
4. frozen Spells;
5. frozen Notes;
6. source-faithful Custom Statistics;
7. source-faithful Traits & Features;
8. source-faithful Resources & Options;
9. source-faithful Inventory / Equipment;
10. source-faithful Spells;
11. source-faithful Notes.

## Frozen-base preservation

Pages 1–5 remain the owner-approved Run-7 base.

The Run-2 test:

- regenerates the frozen Run-7 proof;
- rasterizes its five pages;
- appends the six extensions;
- rasterizes final pages 1–5 again;
- requires pixel-identical output.

That guard passed in final CI.

The frozen base is not reopened by this candidate.

## Final correction/audit sequence

Before final owner review, the source-faithful candidate went through diagnostic rendering and a strict correction pass.

Corrected before final proof:

- Custom Statistics no longer clips the right edge of the original D&D logo;
- source `Puntos de Experiencia` remnant was removed from the custom-stat page;
- arbitrary new labels no longer rely on unsafe owner-source subset glyph coverage;
- intrusive continuation labels were removed from Spells and Notes;
- Inventory special-equipment sample content now respects the source page's preprinted body-location rows;
- two QA-only custom-skill names that exceeded authentic one-line skill capacity were replaced with realistic shorter sample names instead of widening the source columns.

## Technical audit

Final result:

- backend — SUCCESS;
- hosted database — SUCCESS;
- Kotlin/build/tests — SUCCESS;
- 11 pages, all US Letter;
- static/non-encrypted PDF;
- strict text-overflow guard — PASS;
- final artifact contains no overflow diagnostics;
- frozen base pages 1–5 — pixel-identical regression guard PASS;
- fonts embedded;
- exact final PDF independently re-rendered and inspected;
- no observed clipping, broken glyphs, text/rule collisions, or missing extension content.

## Owner review result — corrections requested / superseded

The owner reviewed Run 2 and said the overall direction was very close, but identified three remaining page-6 defects:

- weird white-space/cut artifacts;
- poorly selected Ability/skill blocks;
- Ability/skill typography fully wrong.

Run 2 is therefore not approved. It is superseded as the active candidate by source-faithful Run 3:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN3_OWNER_REVIEW.md`

PR #85 remains **DRAFT / DO NOT MERGE**.

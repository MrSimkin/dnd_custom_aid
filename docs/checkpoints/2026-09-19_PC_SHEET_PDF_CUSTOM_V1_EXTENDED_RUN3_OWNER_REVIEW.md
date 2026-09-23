# Checkpoint — Custom v1 Extended Run 3 — PASS FOR OWNER REVIEW

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Final implementation commit:** `3203234e0820566298b4a49eb87da14c9ee75de8`  
**Scaffold push run:** `35486748557` / run #2831 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10598081443`  
**Proof PDF:** `custom-v1-complete-family-extended-run3.pdf`  
**Proof SHA-256:** `6ebd1b0b7f75221a4767378d18c13d2ff05d821ac10c3887ce040cb7d942e8f5`

## Owner feedback being corrected

Run 2 was close, but the owner identified three page-6 defects:

- weird white-space/cut artifacts;
- poorly selected Ability/skill blocks;
- Ability/skill typography fully wrong.

Run 3 is a bounded correction of those findings. It does not reopen the frozen Custom-v1 base pages.

## Final Run-3 construction

### 1. No broad white masking

Custom Statistics is now composed on a clean Letter page from authentic clipped source fragments.

The source logo and Attribute/Ability structures are imported directly. Run-2 broad white-cover rectangles are not used as the page-construction mechanism.

This removes the white-cut artifact mechanism at its source.

### 2. Correct source blocks

The original Custom-v1 columns have different skill-row capacities. In particular, STR/DEX/CON are not interchangeable with custom statistics that may require several skill rows.

Run 3 therefore deliberately reuses authentic **five-row** source blocks:

- target white columns use source WIS geometry;
- target gray columns use source INT geometry.

This keeps the original Custom-v1 square/rule construction while providing five real writable rows in every extension column.

The renderer preserves the source-printed square and numeric rule. It overlays only the proficiency/expertise symbol when required; it does not draw a duplicate generic square/rule.

### 3. Source-matched Ability/skill typography with full glyph coverage

The embedded owner `GillSansMT` is a source-PDF subset. Directly reusing it for arbitrary new skill names revealed missing glyph outlines for characters that were not present in the original subset.

The final solution uses the already-shipped full `FiraSans-Regular` font but reproduces the measured source text operator:

- 10 pt;
- 60% horizontal scale;
- source-aligned X/baseline.

Visual comparison against the embedded Gill subset showed this treatment is near-identical in width/shape while supporting complete Spanish glyph coverage.

Full QA names now render correctly, including:

- Etiqueta cortesana;
- Lectura de fortuna;
- Análisis de runas;
- Cartografía;
- Orientación astral;
- Acrobacia aérea;
- Cerrajería fina.

## Implementation sequence

- `385eac76ea9d49a37688c7c2d54751261e36f22a` — artifact-free source-fragment Run-3 construction;
- `63e83faa6f750a32f631371460d6735e87f281e8` — measured compressed source typography experiment;
- `2e5379e162995f85865710227ea3d0182eb33812` — restored full QA skill names;
- `a9c60d0c48d181d0d68af52fd9465e8d981ee603` — WIP safety checkpoint requested by owner;
- `89e0766c26a49ba29f0f4e9cfa4a86aad41869be` — PDFBox 3 clipping API correction;
- `3203234e0820566298b4a49eb87da14c9ee75de8` — final five-row source-block selection + full-glyph compressed skill typography.

## Technical and visual audit

Final CI:

- backend — SUCCESS;
- hosted database — SUCCESS;
- Kotlin/build/tests — SUCCESS;
- strict text-overflow guard — PASS;
- frozen pages 1–5 pixel-identical guard — PASS.

PDF preflight:

- 11 pages;
- Letter;
- encrypted — false;
- PyMuPDF openable — true;
- likely scanned — false;
- XFA — false.

Run 2 -> Run 3 differential audit at 120 dpi:

- pages 1–5: 0% change;
- page 6: changed as intended;
- pages 7–11: 0% change;
- total changed pages: 1.

Corrected renderer-parity audit was split into pages 1–9 and 10–11 to avoid the helper's lexical filename ordering issue above nine pages.

Representative new-page renderer differences remain raster-antialiasing scale, including page 6 at approximately 0.078%.

Page 6 was inspected from both the successful CI render and an independent PDFium render. No missing glyphs, broad white masking cuts, duplicate proficiency blocks, clipping, or text/rule collisions were observed.

## Current gate

**Mandatory owner visual review of the complete 11-page Custom-v1 Extended Run 3 proof.**

Status is **PASS FOR OWNER REVIEW**, not approved/frozen.

Do not change the stable Custom-v1 baseline to claim extension approval until explicit owner approval.

PR #85 remains **DRAFT / DO NOT MERGE**.

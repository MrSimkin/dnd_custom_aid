# Checkpoint — Custom v1 Extended Run 1 — PASS FOR OWNER REVIEW

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Candidate implementation commit:** `b86b4539386cca8c567469bd97202c454df411ad`  
**Frozen Custom-v1 base commit:** `7448693e36ee1b26243bd4091615dba725e95027`  
**Scaffold push run:** `35481567673` / run #2797 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10595249955`

## Scope

This is the first complete Custom-v1 family proof that keeps the owner-approved Run-7 five-page base frozen and appends all six D-0074 extension roles.

The owner-review PDF contains 11 Letter pages:

1. frozen Main;
2. frozen Equipment;
3. frozen Narrative / Background;
4. frozen Spells;
5. frozen Notes;
6. Extension — Custom Statistics;
7. Extension — Traits & Features;
8. Extension — Resources & Options;
9. Extension — Inventory / Equipment;
10. Extension — Spells;
11. Extension — Notes.

## Frozen-base preservation

The new proof does not redraw or reinterpret the five approved base pages.

The candidate test calls the existing approved Run-7 generator and then appends extension pages.

Additional guards:

- the Run-7 test file has the same Git blob SHA at the owner-approved base commit and at this candidate: `b09517d7e54b819702a179256c683d08ae25229e`;
- before appending extensions, the test rasterizes the five approved pages;
- after producing the 11-page PDF, it rasterizes pages 1–5 again and requires pixel-identical output;
- this frozen-base regression guard passed in CI.

## Extension design

The six extension pages intentionally follow Custom-v1 paper grammar rather than Classic or dashboard grammar:

- the exact owner-source D&D branding/logo crop is reused from the Custom-v1 template;
- centered narrow headings;
- grayscale alternating bands;
- thin writing rules;
- table/column structures related to the corresponding base-page sections;
- substantial blank capacity for handwriting.

Custom Statistics explicitly preserves governing-Attribute relationships:

- custom Abilities are grouped under HONOR / RESOLUCIÓN / SUERTE;
- additional custom Abilities governed by standard Attributes are grouped under INTELIGENCIA / SABIDURÍA / DESTREZA;
- proficiency/expertise markers use the frozen v8 symbol grammar.

Spell continuation keeps `ESPACIOS GASTADOS` empty and retains writable spell rows.

## Technical and visual audit

- backend CI — SUCCESS;
- hosted database CI — SUCCESS;
- Kotlin/build/tests — SUCCESS;
- 11 Letter pages;
- static/non-encrypted PDF;
- strict extension text-overflow guard — PASS;
- extension fonts — embedded;
- PDF preflight — PASS / openable / no XFA;
- all six extension pages inspected individually from the successful CI artifact;
- no observed clipping, text/rule collision or missing generated content;
- pdftoppm/PDFium variation on new extension pages is approximately 0.05%–0.08% and appears limited to normal raster antialiasing differences.

## Current gate

**Mandatory owner review of the complete 11-page Custom-v1 family.**

Do not change `docs/PC_SHEET_CUSTOM_V1_APPROVED_BASELINE.md` from its current “base approved / extensions pending” state until the owner explicitly approves this extension family.

PR #85 remains **DRAFT / DO NOT MERGE**.

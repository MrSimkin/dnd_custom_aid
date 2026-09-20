# Checkpoint — Custom v1 Extended Run 4 — PASS FOR OWNER REVIEW

**Date:** 2026-09-20 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Final proof implementation commit:** `7d23fbc12a491b40f214f7f395b7cf5ffce4e585`  
**Scaffold push run:** `35523383272` / run #2856 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10608119762`  
**Artifact archive SHA-256:** `7a8b8795c0f04338820d35c2dde1de12a5e4d6baddf87310e663477fdd93ae1e`  
**Proof PDF:** `custom-v1-complete-family-extended-run4.pdf`  
**Proof PDF SHA-256:** `5d82f0f156b7676c09ff5bdf318bf74176dd18facdc1301c56e9028b13aac11a`  
**Layer diagnostics PDF:** `custom-v1-extended-run4-layer-diagnostics.pdf`  
**Diagnostics SHA-256:** `530a1d39aac4b90450bb51ad060058642cb35b0f10f2a4ac89b1877e78d9c135`

## Owner result that started Run 4

Custom-v1 Extended Run 3 was owner-reviewed and **NOT APPROVED**.

The owner confirmed that the typography direction had improved, but identified recurring clipping/white-cut artifact behavior and explicitly required a layered composition strategy rather than further one-pass coordinate/clip adjustments.

The owner then clarified that the correction had to rebuild **all six added pages 6–11**, because artifacts/issues were not limited to page 6.

Run 3 remains historical rejected evidence and is superseded by this Run 4 candidate.

## Run-4 architecture

Run 4 rebuilds every Custom-v1 Extended page (6–11) from the frozen five-page Custom-v1 base.

Each extension page uses explicit independent PDF layers:

1. authentic source structure;
2. bounded cleanup/occlusion;
3. labels;
4. values/modifiers;
5. approved Para Hoja de PJ Symbols v8 markers.

The renderer also emits a 30-page diagnostic proof: six extension roles × five layer stages.

This allows an artifact to be traced to the layer that introduced it rather than diagnosing only the flattened final PDF.

## Corrections completed during Run 4

- reusable Fira/symbol fonts are fully embedded to prevent form-layer subset/encoding corruption;
- source structural geometry is kept on the immutable source layer;
- cleanup masks are separated from labels/values/symbols;
- Attribute score-box top geometry is protected by source-geometry regression guards;
- page-6 source title/skill cleanup uses background-color invariants, including faint source-font anti-aliasing;
- page-8 Equipment-location cleanup now fills complete text-cell interiors while staying left of the authentic checkbox/rule geometry;
- cleanup-stage regression checks assert the intended white/gray background instead of merely checking for obvious black glyphs.

## Final technical/audit evidence

Final CI:

- backend — SUCCESS;
- hosted database — SUCCESS;
- Kotlin/build/tests — SUCCESS;
- Android debug APK upload — SUCCESS;
- PC sheet source renders upload — SUCCESS;
- populated proof artifact upload — SUCCESS.

Independent final-artifact audit:

- final family: 11 pages;
- diagnostics: 30 pages;
- frozen Custom-v1 pages 1–5 independently rendered at 200 dpi and pixel-identical to the Run-7 frozen base;
- page 6 Layer-2 cleanup: source title/skill interiors clean; Attribute ornamental score-box tops remain intact;
- page 8 Layer-2 cleanup: former Equipment-location source-glyph fragments removed without erasing checkbox/rule geometry;
- pages 7, 9, 10 and 11: no new clipping/white-cut/residual-text defect found in the final whole-family scan;
- page 6 and page 8 independently inspected under both Poppler/pdftoppm and PDFium with no renderer-specific missing-glyph or cleanup regression observed;
- GillSans/source-matched visual direction and approved Symbols v8 usage are preserved.

## Current gate

**PASS FOR OWNER REVIEW.**

This is **not** owner approval and does not freeze the Custom-v1 Extended family.

The next owner gate is the complete 11-page Run-4 proof. If the owner approves it, then and only then may the Custom-v1 Extended family be recorded as OWNER APPROVED / FROZEN.

PR #85 remains **DRAFT / DO NOT MERGE**.

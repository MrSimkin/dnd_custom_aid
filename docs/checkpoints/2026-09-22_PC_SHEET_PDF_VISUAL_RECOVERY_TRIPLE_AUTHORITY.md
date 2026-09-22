# Checkpoint — PC Sheet PDF visual recovery — triple authority contract

**Date:** 2026-09-22 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Rejected candidate:** `bf7d4d8f5324af7aabb5bf4d3d0038936af2b53d` / artifact `10713481125`  
**Recovery status:** ACTIVE

## Why this recovery exists

The App Modified / continuation-cue owner review exposed visual regressions that had already been solved during the earlier owner-approved proof runs.

The recovery must not be treated as either:

- a blind rollback to old approved dummy proofs; or
- an incremental patch of the rejected production candidate.

It is a **three-authority reconciliation**.

## Authority order

### A. Frozen owner-approved visual proofs

These are the measured visual goldens and source renderer code:

- Classic Run 2 — commit `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`, artifact `10595468434`;
- Custom v1 base Run 7 — commit `7448693e36ee1b26243bd4091615dba725e95027`, artifact `10591616012`;
- Custom v1 Extended Run 6 — commit `69b308f3d5d493d06bd0107ac66c7524935aa9fa`, artifact `10609869599`;
- Custom v2 per Attribute Run 4 — commit `03c3155301196eaab1229afb4827c46f8acf04cc`, artifact `10592791186`;
- Custom v2 per Ability corrected Run 2 — commit `be689bb6628a46e562428159fc72b2ee51b95d2c`, artifact `10592459998`;
- Custom v2 Extended Run 7 — commit `f464522ad232f4ed6193e1c28d118e45faad988c`, artifact `10617190236`.

These define the previously approved geometry, font roles, row cadence, check/marker placement and family-native page grammar.

### B. Production semantic authority

Production promotion added real `PcSheetPdfRenderPlan` data, current-state semantics, no-silent-loss rules and data-driven pagination.

Those semantics must be preserved. Recovery must not reintroduce QA fixture values as defaults merely to make pages look like the golden sample.

### C. Owner corrections from the 2026-09-22 rejected review

These corrections are authoritative even where they refine or supersede an older approved visual proof.

#### Classic

- do not skip alternating writing rules when printing generated text;
- do not remove later writable rules merely because preceding text is long;
- generated content and ruled-paper rhythm are independent: writing rules remain available;
- spell continuation must preserve level-specific blocks; do not combine all level 6+ spells into one generic block;
- if content does not fit, add another family-native page rather than compressing unrelated spell levels together;
- Equipment continuation must use the available equipment area; do not waste rows or fill the page with repeated `(cont)` labels.

#### Custom v1

- continuation indicators must not be disruptive boxes;
- restore approved X geometry for Equipment;
- restore approved X geometry for Gemas / Arte / Joyas;
- restore approved X geometry and two-column grammar for Otros Rasgos y Atributos;
- Notes must use the approved two-column structure;
- restore the agreed font roles consistently across fields;
- Equipment continuation must use every physical row in the approved cadence, not line-skipping;
- continuation wording must not be malformed or over-condensed;
- check boxes/marks must use the approved geometry and optical centering.

#### Custom v2 — per Attribute and per Ability

- check marks must use the approved source-measured geometry and optical centering;
- Ability/skill modifiers and numeric values must be centered in the approved target boxes/rules;
- continuation indicators must not be disruptive boxes;
- additional pages must retain approved X geometry;
- remove artificial/excessive line breaks introduced by character-count wrapping;
- Equipment continuation should reuse the source-led v2 Equipment page grammar and extend capacity naturally (including an additional column/page where needed), not replace it with a generic continuation report;
- restore approved Corbel/Fira font-role contract;
- restore approved portrait-name placement for both v2 first-page modes.

## Cross-analysis result

The owner observations were reproduced in code-level differences rather than dismissed as PDF-viewer noise.

Examples already verified:

- Custom-v1 approved Equipment columns resolve to approximately `55 / 340 / 623 px` in the production reference coordinate system; production used `55 / 305 / 555 px`.
- Custom-v1 approved right Notes column begins at approximately `623 px`; production used `555 px`.
- production collapsed the approved two-column Custom-v1 Otros Rasgos structure into one broad region.
- current v2 base rendering uses generic marker/text primitives rather than the approved optical marker geometry from the frozen Run-4/Run-2 proofs.
- normalized same-renderer PDF comparisons show material visual drift on supposedly frozen pages, well beyond antialiasing noise.

## Recovery rules

1. Never compare a future production render only against a prior production render.
2. Golden regression comparisons must originate from the exact owner-approved artifacts above.
3. If the golden proof conflicts with a 2026-09-22 owner correction, the 2026-09-22 correction wins and the new corrected proof becomes the replacement golden only after owner approval.
4. Preserve production semantics while restoring family-native geometry.
5. Prefer extra pages over compression, skipped rows or silent loss.
6. No App Modified or continuation-cue visual work until baseline recovery is visually coherent.
7. Save/Share remains blocked until the recovered visual package passes owner QA.

## Current task

Repair the production renderers from the measured approved implementations, add non-circular regression guards, generate a compact recovery proof set, and return it for owner review.


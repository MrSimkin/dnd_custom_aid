# Latest project checkpoint — global resume map

**Updated:** 2026-09-20 (Chile local time)  
**Normal integrated trunk:** `main`  
**Last verified integrated main:** `2dc74e2d9c7d853a068e9052ec4928bf5178eb9f`  
**Wave 7:** ACTIVE  
**Current active branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**Approved Classic renderer baseline:** `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`  
**Draft PR:** #85 — OPEN / DRAFT / **DO NOT MERGE**

## Read first on resume

1. `AGENTS.md`;
2. this file;
3. `docs/PC_SHEET_PDF_STRATEGY_RUN_PROTOCOL.md`;
4. `docs/PC_SHEET_CUSTOM_V1_APPROVED_BASELINE.md`;
5. `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN4_OWNER_REVIEW.md`;
6. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN3_OWNER_REVIEW.md`;
7. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN3_WIP_SAFETY.md`;
8. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN2_OWNER_REVIEW.md`;
9. `docs/PC_SHEET_CUSTOM_V2_APPROVED_BASELINES.md`;
10. `docs/PC_SHEET_CLASSIC_APPROVED_BASELINE.md`;
11. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CLASSIC_RUN2_OWNER_APPROVED.md`;
12. `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`;
13. `docs/PROJECT_STATE.md`;
14. `docs/BRANCH_STATUS.md`.

## Current PDF visual truth

Strategy 1 / Hybrid remains the proven template-overlay strategy for owner-authored Custom sheets.

Para Hoja de PJ Symbols v8 is **OWNER APPROVED / FROZEN**.

### Approved/frozen base sheets

- **Custom v1** — Run 7 final calibrated base sheet approved/frozen.
- **Custom v2 — per Attribute** — Run 4 base sheet approved/frozen.
- **Custom v2 — per Ability** — corrected Run 2 base sheet approved/frozen.
- Shared Custom-v2 pages are frozen as the approved common baseline.

Do not recalibrate these base sheets without a new owner-observed issue.

### Classic D&D-style

Classic is application-designed, not an official-sheet facsimile.

**Classic Run 2 corrected complete family is OWNER APPROVED / FROZEN.**

Approved baseline:

- renderer/proof commit `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`;
- Scaffold push run `35480871986` / run #2793 — SUCCESS;
- nine-page approval set: three normal pages plus all six D-0074 extension roles;
- strict text-overflow and Spanish-only regression guards pass;
- final Y-axis rule: writing lines remain present under prefilled text and text must align within, not collide with, ruled-paper rhythm.

Stable pointer:

`docs/PC_SHEET_CLASSIC_APPROVED_BASELINE.md`

Run 1 is **OWNER REJECTED / historical evidence only** and must not be used as a continuation baseline.

## Extended pages — required for every design

Owner clarification on 2026-09-19 reaffirmed D-0074:

> Extended pages are required for all visual families.

This means:

- Classic must have Classic-styled Extended pages;
- Custom v1 must have Custom-v1-styled Extended pages;
- Custom v2 per Attribute / per Ability must use Custom-v2-coherent Extended pages;
- there is no generic one-style-fits-all Extended appendix.

Applicable extension roles are data-driven and include:

- Custom Statistics;
- Traits & Features;
- Resources & Options;
- Inventory / Equipment;
- Spells;
- Notes.

Base-sheet approval does not by itself close a whole visual family if its required Extended-page design/QA is still pending.

## Custom v1 Extended — current owner gate

Custom v1 Extended Run 1 is **OWNER REJECTED / historical evidence only**.

Custom v1 Extended Run 2 was reviewed and is **NOT APPROVED / SUPERSEDED** after owner feedback on page-6 white-cut artifacts, block selection and Ability/skill typography.

Custom v1 Extended Run 3 was owner-reviewed and is **NOT APPROVED / SUPERSEDED**. The owner required a layered renderer because the same clipping/white-cut artifact class continued in different locations, and then clarified that all added pages 6–11 had to be rebuilt rather than page 6 alone.

Custom v1 Extended Run 4 is now **PASS FOR OWNER REVIEW**.

Final evidence:

- proof implementation commit `7d23fbc12a491b40f214f7f395b7cf5ffce4e585`;
- Scaffold push run `35523383272` / #2856 — SUCCESS;
- proof artifact `10608119762`;
- final proof PDF SHA-256 `5d82f0f156b7676c09ff5bdf318bf74176dd18facdc1301c56e9028b13aac11a`;
- all six extension roles/pages 6–11 rebuilt with explicit source / cleanup / labels / values / symbols layers;
- 30-page layer-diagnostic proof;
- frozen pages 1–5 independently verified pixel-identical to Custom-v1 Run 7 at 200 dpi;
- cleanup-background regression guards cover the page-6 source-title/skill interiors and page-8 Equipment-location cells;
- source-geometry guards preserve Attribute score-box top boundaries;
- full font embedding removes the earlier form-layer glyph corruption;
- independent final-artifact inspection passes on pages 6–11, including Poppler/PDFium inspection of the repaired pages 6 and 8.

Checkpoint:

`docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN4_OWNER_REVIEW.md`

Do not mark the Custom-v1 extension family approved/frozen until explicit owner approval.

## Current continuation

1. owner reviews the complete 11-page Custom-v1 Extended Run-4 proof;
2. if approved, freeze the Custom-v1 extension family; otherwise apply bounded Run-4 extension-only corrections while preserving frozen pages 1–5 and the layered architecture;
3. design/QA family-matched Extended pages for Custom v2 per Attribute / per Ability without disturbing their frozen bases;
4. promote approved visual mechanics into production renderer paths;
5. complete family/end-to-end export QA and remaining D-0074 behaviors, including data-driven overflow and the separate optional Spellbook;
6. keep PR #85 DRAFT / DO NOT MERGE until the remaining visual/functional gates are satisfied.

No external provider action is required for this PDF-renderer stage.

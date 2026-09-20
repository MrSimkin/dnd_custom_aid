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
5. `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN5_OWNER_REVIEW.md`;
6. `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN4_OWNER_REVIEW.md`;
7. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN3_OWNER_REVIEW.md`;
8. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN3_WIP_SAFETY.md`;
9. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN2_OWNER_REVIEW.md`;
10. `docs/PC_SHEET_CUSTOM_V2_APPROVED_BASELINES.md`;
11. `docs/PC_SHEET_CLASSIC_APPROVED_BASELINE.md`;
12. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CLASSIC_RUN2_OWNER_APPROVED.md`;
13. `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`;
14. `docs/PROJECT_STATE.md`;
15. `docs/BRANCH_STATUS.md`.

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

Custom v1 Extended Run 2 was reviewed and is **NOT APPROVED / SUPERSEDED**.

Custom v1 Extended Run 3 was owner-reviewed and is **NOT APPROVED / SUPERSEDED**.

Custom v1 Extended Run 4 was owner-reviewed and is **NOT APPROVED / SUPERSEDED** after feedback on remaining page-6 artifacts/layout/typography, the `Raza` terminology contract, and page-8 resource design.

Custom v1 Extended Run 5 is now **PASS FOR OWNER REVIEW**.

Final evidence:

- proof implementation commit `f197f9382d751d4342fb648d73b3520d72cdd911`;
- Scaffold push run `35527343150` / #2874 — SUCCESS;
- proof artifact `10610600180`;
- final proof PDF SHA-256 `f41a3394f2cdd9c7d2305f59b9c2c89e8b35da4418d8da8f3d9e5a8eb535436a`;
- page 6 redesigned around top-first Attribute/Ability modules, authentic ornamental geometry only, non-condensed headings, and symmetric Definiciones/Notas sections;
- page 7 uses `Rasgos de Raza` and regression-guards against `Rasgos de Especie`;
- page 8 replaces separate Recuperación/Estados extensions with one v8-glyph resource tracker and a coherent Opciones section;
- inherited page-9 valuables and page-10 spell-slot numeric omissions were repaired by removing the incomplete Gill subset from new Run-5 content;
- frozen pages 1–5 independently verified pixel-identical to Custom-v1 Run 7 at 120 dpi;
- independent Poppler/PDFium render inspection passes.

Checkpoint:

`docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN5_OWNER_REVIEW.md`

Do not mark the Custom-v1 extension family approved/frozen until explicit owner approval.

## Current continuation

1. owner reviews the complete 11-page Custom-v1 Extended Run-5 proof;
2. if approved, freeze the Custom-v1 extension family; otherwise apply bounded Run-5 extension-only corrections while preserving frozen pages 1–5 and the redesigned page-6/7/8 architecture;
3. design/QA family-matched Extended pages for Custom v2 per Attribute / per Ability without disturbing their frozen bases;
4. promote approved visual mechanics into production renderer paths;
5. complete family/end-to-end export QA and remaining D-0074 behaviors, including data-driven overflow and the separate optional Spellbook;
6. keep PR #85 DRAFT / DO NOT MERGE until the remaining visual/functional gates are satisfied.

No external provider action is required for this PDF-renderer stage.

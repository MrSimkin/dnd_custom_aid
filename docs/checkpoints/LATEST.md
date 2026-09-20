# Latest project checkpoint — global resume map

**Updated:** 2026-09-19 (Chile local time)  
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
5. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN2_OWNER_REVIEW.md`;
6. `docs/PC_SHEET_CUSTOM_V2_APPROVED_BASELINES.md`;
7. `docs/PC_SHEET_CLASSIC_APPROVED_BASELINE.md`;
8. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CLASSIC_RUN2_OWNER_APPROVED.md`;
9. `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`;
10. `docs/PROJECT_STATE.md`;
11. `docs/BRANCH_STATUS.md`.

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

Custom v1 Extended Run 2 is **PASS FOR OWNER REVIEW**.

Evidence:

- candidate commit `b244159b467d162c8637db5532dcfe6f2f831953`;
- Scaffold push run `35484718817` / run #2817 — SUCCESS;
- proof artifact `10597183316`;
- 11-page proof = five frozen Run-7 base pages + six source-faithful extension roles;
- frozen pages 1–5 are guarded pixel-identical;
- strict text-overflow guard passes;
- extensions are derived from the actual Custom-v1 source-page composition rather than the rejected generic Run-1 extension grammar.

Checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN2_OWNER_REVIEW.md`

Do not mark the Custom-v1 extension family approved/frozen until explicit owner approval.

## Current continuation

1. owner reviews the complete source-faithful Custom-v1 Extended Run-2 proof;
2. if approved, freeze the Custom-v1 extension family; otherwise apply bounded Run-2 extension-only corrections while preserving pages 1–5;
3. design/QA family-matched Extended pages for Custom v2 per Attribute / per Ability without disturbing their frozen bases;
4. promote approved visual mechanics into production renderer paths;
5. complete family/end-to-end export QA and remaining D-0074 behaviors, including data-driven overflow and the separate optional Spellbook;
6. keep PR #85 DRAFT / DO NOT MERGE until the remaining visual/functional gates are satisfied.

No external provider action is required for this PDF-renderer stage.

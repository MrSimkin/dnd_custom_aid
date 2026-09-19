# Latest project checkpoint — global resume map

**Updated:** 2026-09-19 (Chile local time)  
**Normal integrated trunk:** `main`  
**Last verified integrated main:** `2dc74e2d9c7d853a068e9052ec4928bf5178eb9f`  
**Wave 7:** ACTIVE  
**Current active branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**Current implementation head:** `14b5c2561060fc319ef63cf7a9f2ffcbb7b9c996`  
**Draft PR:** #85 — OPEN / DRAFT / **DO NOT MERGE**

## Read first on resume

1. `AGENTS.md`;
2. this file;
3. `docs/PC_SHEET_PDF_STRATEGY_RUN_PROTOCOL.md`;
4. `docs/PC_SHEET_CUSTOM_V1_APPROVED_BASELINE.md`;
5. `docs/PC_SHEET_CUSTOM_V2_APPROVED_BASELINES.md`;
6. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CLASSIC_RUN1_OWNER_REVIEW.md`;
7. `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`;
8. `docs/PROJECT_STATE.md`;
9. `docs/BRANCH_STATUS.md`.

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

Classic Run 1 now has a technically green populated six-page candidate:

- implementation/final calibration commit `14b5c2561060fc319ef63cf7a9f2ffcbb7b9c996`;
- Scaffold `35473827949` / run #2779 — SUCCESS;
- five normal base pages plus one family-matched Extended — Custom Statistics page;
- strict text-overflow guard passes.

**Current mandatory owner gate:** visual review of Classic Run 1.

Classic is **not yet approved/frozen**.

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

## Current continuation

1. obtain owner visual feedback on Classic Run 1;
2. calibrate/freeze Classic base design if approved;
3. continue family-matched Extended-page design/QA for **all** visual families, preserving frozen base sheets;
4. migrate approved visual mechanics into production renderer paths;
5. complete family/end-to-end export QA and remaining D-0074 behaviors;
6. keep PR #85 DRAFT / DO NOT MERGE until the remaining visual/functional gates are satisfied.

No external provider action is required for this PDF-renderer stage.

# Latest project checkpoint — global resume map

**Updated:** 2026-09-21 (Chile local time)  
**Normal integrated trunk:** `main`  
**Last verified integrated main:** `2dc74e2d9c7d853a068e9052ec4928bf5178eb9f`  
**Wave 7:** ACTIVE  
**Current active branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**Approved Classic renderer baseline:** `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`  
**Draft PR:** #85 — OPEN / DRAFT / **DO NOT MERGE**

## Read first on resume

1. `AGENTS.md`;
2. this file;
3. `docs/checkpoints/2026-09-21_PC_SHEET_PDF_CUSTOM_V1_INTEGRATED_PRODUCTION_AUDIT_PASS.md`;
4. `docs/PC_SHEET_PDF_STRATEGY_RUN_PROTOCOL.md`;
4. `docs/PC_SHEET_CUSTOM_EXTENDED_STRATEGY.md`;
5. `docs/PC_SHEET_CUSTOM_V1_APPROVED_BASELINE.md`;
6. `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN6_OWNER_APPROVED.md`;
7. `docs/PC_SHEET_CUSTOM_V2_APPROVED_BASELINES.md`;
8. `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN6_OWNER_REVIEW.md`;
9. `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN5_OWNER_REVIEW.md`;
10. `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN4_OWNER_REVIEW.md`;
11. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN3_OWNER_REVIEW.md`;
12. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN3_WIP_SAFETY.md`;
13. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN2_OWNER_REVIEW.md`;
14. `docs/PC_SHEET_CLASSIC_APPROVED_BASELINE.md`;
15. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CLASSIC_RUN2_OWNER_APPROVED.md`;
16. `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`;
17. `docs/PROJECT_STATE.md`;
18. `docs/BRANCH_STATUS.md`.

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

## Custom v1 Extended — OWNER APPROVED / FROZEN

Custom v1 Extended Runs 1–5 are historical rejected/superseded evidence.

Custom v1 Extended Run 6 is **OWNER APPROVED / FROZEN**.

Approved evidence:

- implementation commit `69b308f3d5d493d06bd0107ac66c7524935aa9fa`;
- Scaffold `35529317947` / #2885 — SUCCESS;
- artifact `10609869599`;
- proof PDF SHA-256 `03212b642ba9b8414e18344dbe90b6d68d623d14a0cd1ff712544063548eafe5`.

Approval checkpoint:

`docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN6_OWNER_APPROVED.md`

Canonical Custom Extended methodology:

`docs/PC_SHEET_CUSTOM_EXTENDED_STRATEGY.md`

Custom v1 visual-family design/QA is closed. Production promotion and integrated semantic audit are also **PASS** at `5d09271231dd395e44f0c4c2a33cdb39509cc6b5`; see `docs/checkpoints/2026-09-21_PC_SHEET_PDF_CUSTOM_V1_INTEGRATED_PRODUCTION_AUDIT_PASS.md`. Do not recalibrate without new owner-observed defect or product requirement.

## Current production truth

- Custom-v2 Extended design is owner-approved/frozen and its integrated production audit is **PASS** at `0295f30ca77214284902b0e13a563dcdaf58b501`.
- Custom-v1 base + Extended design is owner-approved/frozen and its integrated production audit is **PASS** at `5d09271231dd395e44f0c4c2a33cdb39509cc6b5`.
- Classic Run 2 complete nine-page family is owner-approved/frozen but remains in the review/dummy harness rather than the real plan-driven production path.

## Current continuation

1. promote the frozen Classic Run-2 complete family into a real `PcSheetPdfRenderPlan`-driven production renderer;
2. audit Classic pagination/current-state/canonical semantics without reopening the approved visual grammar;
3. implement the application-owned optional Spellbook;
4. complete portrait-byte handoff plus Crop/Fit behavior;
5. implement the remaining owner-facing `APP_MODIFIED_SHEET` and originating-section continuation-cue gates;
6. wire local Save/Share, beginning with the Desktop DM workflow before cross-surface parity;
7. keep PR #85 DRAFT / DO NOT MERGE until the remaining visual/functional gates are satisfied.

No external provider action is required for this PDF-renderer stage.

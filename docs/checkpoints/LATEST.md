# Latest project checkpoint — global resume map

**Updated:** 2026-09-23 (Chile local time)  
**Normal integrated trunk:** `main`  
**Last verified integrated main:** `2dc74e2d9c7d853a068e9052ec4928bf5178eb9f`  
**Wave 7:** ACTIVE  
**Current active branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**Approved Classic renderer baseline:** `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`  
**Draft PR:** #85 — OPEN / DRAFT / **DO NOT MERGE**

## Read first on resume

1. `AGENTS.md`;
2. this file;
3. `docs/checkpoints/2026-09-23_PC_SHEET_PDF_OWNER_MARKED_REPAIR_FINAL_VISUAL_GATE.md`;
4. `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`;
5. `docs/PC_SHEET_PDF_ITERATION_LEDGER.md`;
6. `docs/PC_SHEET_PDF_GEOMETRY_GATES.md`;
7. `docs/checkpoints/2026-09-22_PC_SHEET_PDF_RECOVERY_PROCESS_HARDENING.md`;
8. `docs/checkpoints/2026-09-22_PC_SHEET_PDF_VISUAL_RECOVERY_OWNER_REVIEW_2_REJECTED.md`;
9. `docs/checkpoints/2026-09-22_PC_SHEET_PDF_VISUAL_RECOVERY_TRIPLE_AUTHORITY.md`;
10. `docs/checkpoints/2026-09-22_PC_SHEET_PDF_VISUAL_RECOVERY_OWNER_QA_READY.md`;
11. `docs/checkpoints/2026-09-22_PC_SHEET_PDF_APP_MODIFIED_CONTINUATION_OWNER_QA_READY.md`;
12. `docs/checkpoints/2026-09-21_PC_SHEET_PDF_PORTRAIT_HANDOFF_PRODUCTION_PASS.md`;
13. `docs/checkpoints/2026-09-21_PC_SHEET_PDF_SPELLBOOK_PRODUCTION_PASS.md`;
14. `docs/checkpoints/2026-09-21_PC_SHEET_PDF_CLASSIC_INTEGRATED_PRODUCTION_AUDIT_PASS.md`;
15. `docs/checkpoints/2026-09-21_PC_SHEET_PDF_CUSTOM_V1_INTEGRATED_PRODUCTION_AUDIT_PASS.md`;
16. `docs/PC_SHEET_PDF_STRATEGY_RUN_PROTOCOL.md`;
17. `docs/PC_SHEET_CUSTOM_EXTENDED_STRATEGY.md`;
18. `docs/PC_SHEET_CUSTOM_V1_APPROVED_BASELINE.md`;
19. `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN6_OWNER_APPROVED.md`;
20. `docs/PC_SHEET_CUSTOM_V2_APPROVED_BASELINES.md`;
21. `docs/PC_SHEET_CLASSIC_APPROVED_BASELINE.md`;
22. `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CLASSIC_RUN2_OWNER_APPROVED.md`;
23. `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`;
24. `docs/PROJECT_STATE.md`;
25. `docs/BRANCH_STATUS.md`.

## Current PDF visual truth

Strategy 1 / Hybrid remains the proven template-overlay strategy for owner-authored Custom sheets.

Para Hoja de PJ Symbols v8 is **OWNER APPROVED / FROZEN**.

### Approved/frozen base sheets

- **Custom v1** — Run 7 final calibrated base sheet approved/frozen.
- **Custom v2 — per Attribute** — Run 4 base sheet approved/frozen.
- **Custom v2 — per Ability** — corrected Run 2 base sheet approved/frozen.
- Shared Custom-v2 pages are frozen as the approved common baseline.

Do not recalibrate these base sheets without a new owner-observed issue.

### Fantasy Sheet (legacy internal id `CLASSIC_DND_STYLE`)

Fantasy Sheet is application-designed and is explicitly **not** an official/official-like D&D sheet. Historical files may still use the old “Classic” name as provenance.

**The historical Run-2 Fantasy-sheet visual baseline remains the frozen geometry source, subject to the active owner regression corrections.**

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
- Classic Run 2 complete base + six continuation roles are owner-approved/frozen and its integrated production audit is **PASS** at `30ac4d073e9fab47a498f4e6dc3d9266c633110e`; final Scaffold #3151 / `35677870304` — SUCCESS; proof artifact `10674062891`. Printed spent-slot markers remain intentionally blank for paper tracking.
- The application-owned optional Spellbook is production-complete and **PASS** at `ac0cbb2b26202bf934ac4926b56899014390613e`; final Scaffold #3161 / `35679451757` — SUCCESS; proof artifact `10674370966`. It is appended after the selected family/Extended output and leaves all preceding pages unchanged.
- Local portrait-byte handoff plus Crop/Fit is production-complete and **PASS** at `93490d2247da5ea46ef50563fd17da78840e659e`; final Scaffold #3177 / `35683430947` — SUCCESS; proof artifact `10676160917`. Classic, Custom v1 and both Custom-v2 first-page variants preserve their portrait frames while supporting Crop-to-fill and Fit-entire-image.
- `APP_MODIFIED_SHEET` plus originating-section continuation-cue candidate at `bf7d4d8f5324af7aabb5bf4d3d0038936af2b53d` / artifact `10713481125` is **OWNER REJECTED / DO NOT USE**. Green CI did not preserve the exact frozen visual baselines.
- Visual recovery artifact `10720833172` remains OWNER REJECTED. VR-3 (`a5cb2311a0beb8454291d8b9985cb1b13f37a3dd`, artifact `10723153227`) passed guarded preflight but owner visual review exposed additional already-existing visual-contract regressions. The bounded owner-marked repair is now at implementation head `f7e4417c05a2981415ef3648ead740e20469fe33`; push #3279 / `35873556136` and PR #3280 / `35873560390` are both SUCCESS; final populated-proof artifact is `10756937024`. Worker visual preflight of that exact artifact is PASS; **owner final visual QA is pending**.

## Current continuation

1. Resume first from `docs/checkpoints/2026-09-23_PC_SHEET_PDF_OWNER_MARKED_REPAIR_FINAL_VISUAL_GATE.md` and the appended VR-4 ledger entry.
2. Exact implementation candidate awaiting owner approval: `f7e4417c05a2981415ef3648ead740e20469fe33`.
3. Exact final CI evidence: push #3279 / `35873556136` SUCCESS and PR #3280 / `35873560390` SUCCESS.
4. Exact populated-proof artifact awaiting owner final visual QA: `10756937024` (`pc-sheet-populated-template-proofs`).
5. Owner-marked repairs now encoded:
   - Fantasy traits continuation uses one continuous ruled grid with no fixed feature-card gaps or unexplained white holes;
   - Custom-v1 Equipment uses compact/condensed item identity with state/notes subordinate beneath it;
   - Custom-v2 section headings behave as headings, not gray data rows;
   - Custom-v2 Equipment continuation reuses the normal-family module grammar;
   - Custom-v2 Equipo Especial uses native location rows and does not overprint recognized location labels.
6. Worker visual preflight on the final artifact is PASS, but **owner approval has not yet been given**.
7. Keep PR #85 DRAFT / DO NOT MERGE and keep Save/Share blocked until explicit owner approval.
8. If owner approves this exact candidate, record OWNER APPROVED, freeze the renderer/proof state, then proceed to Desktop Save/Share/export invocation using the same `DesktopPcSheetWholeDraftRenderer` + `PcSheetPdfRenderPlan` path and perform only implementation-parity regression verification.
9. If owner reports another defect, treat it as a bounded regression and append the next ledger iteration; do not restart PDF design.

No external provider action is required for this PDF-renderer stage.

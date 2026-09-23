# Latest project checkpoint — global resume map

**Updated:** 2026-09-23 (Chile local time)  
**Normal integrated trunk:** `main`  
**Last verified integrated main:** `6ce3ac35798a1ce915a3dac4227e983932d5ab9f`  
**Wave 7:** ACTIVE  
**Current active branch:** `qa/pc-sheet-runtime-character-fixtures`  
**Frozen approved renderer implementation:** `f7e4417c05a2981415ef3648ead740e20469fe33`  
**PR #91:** PC Sheet runtime QA character fixture pack - pending documentation-head CI/merge

## Read first on resume

1. `AGENTS.md`;
2. this file;
3. `docs/checkpoints/2026-09-23_PC_SHEET_RUNTIME_QA_CHARACTER_PACK_READY.md`;
4. `docs/checkpoints/2026-09-23_PC_SHEET_PDF_ANDROID_SAVE_SHARE_PASS.md`;
5. `docs/checkpoints/2026-09-23_PC_SHEET_PDF_ANDROID_RENDERER_BRIDGE_PASS.md`;
6. `docs/checkpoints/2026-09-23_PC_SHEET_PDF_DESKTOP_SAVE_SHARE_PASS.md`;
7. `docs/checkpoints/2026-09-23_PC_SHEET_PDF_FINAL_VISUAL_OWNER_APPROVED.md`;
8. `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`;
9. `docs/PC_SHEET_PDF_ITERATION_LEDGER.md`;
10. `docs/PC_SHEET_PDF_GEOMETRY_GATES.md`;
11. `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`;
12. `docs/PROJECT_STATE.md`;
13. `docs/BRANCH_STATUS.md`.

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
- Visual recovery artifact `10720833172` remains OWNER REJECTED. VR-3 (`a5cb2311a0beb8454291d8b9985cb1b13f37a3dd`, artifact `10723153227`) passed guarded preflight but owner review found additional regressions. VR-4 renderer `f7e4417c05a2981415ef3648ead740e20469fe33`, artifact `10756937024`, passed push #3279 / `35873556136` and PR #3280 / `35873560390`, then received **OWNER APPROVAL on 2026-09-23**. Custom-v2 Equipment/Equipo Especial continuation is approved with an explicitly accepted deviation: it is not a literal copy of the original normal-page modules. The current rendered output is frozen; do not claim literal reuse was achieved.

## Current continuation

1. PC-sheet visual/layout/text-filling gate remains **OWNER APPROVED / CLOSED**.
2. Frozen renderer authority remains `f7e4417c05a2981415ef3648ead740e20469fe33`; approved proof artifact remains `10756937024`.
3. Preserve the accepted Custom-v2 Equipment / Equipo Especial deviation exactly; no current continuation authorizes redesign.
4. Historical App Modified candidate `bf7d4d8f5324af7aabb5bf4d3d0038936af2b53d` remains rejected; integrated export invokes the later owner-approved frozen renderer, not that candidate.
5. PR #86 Desktop Save/Share is integrated as `c28ad548113b368413e479de544c85aa8c924ef4`.
6. PR #88 Android generated renderer bridge is integrated as `c5963881bdff2597770d3f6a26992b8567b2a35b`.
7. PR #89 Android Player/authorized-DM Save/Share is integrated as `e6e153a53bba8aa532b5c371dcc16849a901a541`.
8. PR #90 lifecycle/documentation closure is integrated as `6ce3ac35798a1ce915a3dac4227e983932d5ab9f`; post-merge #3344 PASS.
9. **Runtime QA fixture pack is prepared on PR #91**: Aldren Vale (strict SRD 5.1 Fighter 5/Champion), Ilyra Quill (strict SRD 5.2.1 Wizard 5/Evoker), and Mara de los Siete Umbrales (CUSTOM high-volume Extended stress).
10. All three fixtures are app-owned Character Backup v2 JSONs suitable for normal Android **Importar**; tests prove decode/round-trip/restore-as-copy.
11. Guarded DEV SQL seed is prepared at `database/qa/seed_pc_sheet_runtime_characters.sql`; it stores those exact fixture documents in hosted `pc.snapshot`. It has **not** been executed against DEV in this work.
12. Fixture implementation/test head `a06b2fe5712426e6b42e5ea88f0fe0b2f9529fd1` passed push Scaffold #3348 / `35914303487`.
13. Android export still exposes D-0074 family/state/custom-stat/portrait/Spellbook choices, remains available in Table Mode, and uses the shared planner + generated frozen renderer.
14. Unsaved structural edits still require explicit **Exportar sin guardar** confirmation; PDF projection does not call repository Save.
15. Current Snapshot still lacks a separate local aggregate; shared planner fallback + user-visible notice remains the truthful behavior.
16. **Current boundary remains unchanged:** real Android device/runtime smoke of Save, Share, unsaved-export non-persistence and Current Snapshot fallback notice, now with reproducible QA characters ready.
17. Do not start the next Wave 7 package before recording that manual smoke result.
18. When QA resumes, follow `docs/checkpoints/2026-09-23_PC_SHEET_RUNTIME_QA_CHARACTER_PACK_READY.md`.

No external provider action is required for this PDF-renderer stage.

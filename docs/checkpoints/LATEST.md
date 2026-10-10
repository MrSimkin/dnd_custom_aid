# Latest project checkpoint — global resume map

**Updated:** 2026-10-09 (Chile local time; V11 handoff BRANCH ONLY, not merged)  
**Normal integrated trunk:** `main`  
**Last verified functional main before the unmerged adaptive branch:** `15f86ec8285e69969054d40defaa2c16259b8dce` (PR #107)  
**Wave 7:** ACTIVE  
**Canonical active checkpoint:** `docs/checkpoints/2026-10-09_PC_SHEET_V11_APPROVED_IMPLEMENTATION_HANDOFF.md`  
**Current active route:** **V11 OWNER VISUAL APPROVED / IMPLEMENTATION AUTHORIZED / G0 DOCUMENTARY PREFLIGHT PARTIAL — NO KOTLIN IMPLEMENTATION YET.** Work only under `docs/pc-sheet-v11/` contract + risk ledger, preserving D-0076 history. One bounded branch/PR, anti-loop limits, no merge. V11 Python prototype is **not** App renderer; golden ZIP is **not yet stored in Git**.
**Completed audit branch:** `audit/mara-phase2-existing-repair`. `repair/pc-sheet-adaptive-continuations-cross-family` remains historical implementation evidence at `ce695c...`; it is repository/CI/Worker-preflight green but **not owner-runtime verified** and is not acceptance authority.  
**Current implementation PR:** documentation-only V11 handoff branch `docs/pc-sheet-v11-approved-implementation-handoff` (PR URL recorded after creation). No V11 production commits/builds or owner APK candidate. Any future runtime candidate must pass all QA/unique-build gates.
**Frozen previously owner-approved renderer baseline:** `f7e4417c05a2981415ef3648ead740e20469fe33`.

## Read first on resume

1. `AGENTS.md`;
2. `RESUME.md`;
3. this file;
4. `docs/checkpoints/2026-10-09_PC_SHEET_V11_APPROVED_IMPLEMENTATION_HANDOFF.md`;  
5. `docs/pc-sheet-v11/CONTRATO_VISUAL_APROBADO.md` + `PREFLIGHT_TECNICO_Y_RIESGOS.md` + `ARTEFACTOS_Y_PROCEDENCIA.md`;  
6. `docs/pc-sheet-v11/PROTOCOLO_RIESGOS_Y_RECUPERACION.md` — prevención, detector, recuperación, prueba y STOP de R-01..R-12 (**no es un PASS técnico**);
8. `docs/pc-sheet-v11/PLAN_TECNICO_IMPLEMENTACION_V11.md` + `PROMPT_WORKER_IMPLEMENTAR_V11.md`;  
9. `docs/checkpoints/2026-10-08_PC_SHEET_PDF_REDESIGN_DISCUSSION.md` — HISTÓRICO de cierre conceptual;
10. `docs/decisions/D-0076_PC_SHEET_PDF_REDESIGN_DECISION_LEDGER.md` — §§3.1–3.48 + grouped closeout §7;
11. `docs/decisions/D-0076_PC_SHEET_PDF_REDESIGN_VISUAL_REVIEW_BRIEF.md` — brief histórico de revisión previa, NO maqueta aprobada.

For historical renderer/acceptance context only (not the active design route):

- `docs/checkpoints/2026-09-30_MARA_PHASE2_EXISTING_REPAIR_AUDIT.md`;
5. `docs/checkpoints/2026-09-30_MARA_PHASE1_PROVENANCE_AUDIT.md`;
6. `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md`;
7. `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md` — consolidated owner clarification / mandatory pre-fix checklist;
8. `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md` — original defect/acceptance ledger;
9. `docs/checkpoints/2026-09-27_PC_SHEET_MARA_PREQA8_ADAPTIVE_REPAIR_CLOSURE.md` — superseded claimed closure / evidence to audit, **not** runtime acceptance;
10. `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`;
11. `docs/PC_SHEET_PDF_GEOMETRY_GATES.md`;
12. `docs/PROJECT_STATE.md`;
13. `docs/BRANCH_STATUS.md`.

**Branch rule:** this V11 handoff branch is the active continuation **only if/when this change becomes the current-main route**. Until merged, `main` still has the older design-only checkpoint. Existing Phase-2/Mara evidence remains historical, not a PASS for V11 nor authorization to revive the prior repair instead of the approved V11 redesign.

## Active owner re-QA failure

Observed owner runtime results:

- Fantasy: **45 pages**;
- Custom v1: **generation FAIL** on `Lectura de presagios` excessive compression;
- Custom v2 · Atributo: **28 pages**;
- Custom v2 · Habilidad: **27 pages**.

The prior closure claimed **29 / 18 / 16 / 15**. This mismatch is decisive: runtime does not demonstrate the claimed repair.

The new acceptance gate is:

1. establish exact installed APK commit/artifact provenance;
2. reopen the 2026-09-26 source defect checkpoint, the 2026-09-28 runtime-fail checkpoint and the 50800 pre-fix acceptance matrix;
3. before code changes, map every OPEN matrix item to implementation surface + existing native/source reference + automated regression + actual-candidate evidence;
4. preserve the owner reuse-first rule: attributes, Traits/Rasgos, Trasfondo/Historia-style modules, ordinary Equipment, Equipo Especial, Notes and analogous sections reuse/extend their existing native grammar instead of being rebuilt generically;
5. ordinary Equipment shows compact identity only — **no weight, no `Consumible`, no prose descriptions**;
6. include the Fantasy regressions and Custom-v1 export blocker;
7. require all-four-family real-Mara generation smoke before owner handoff;
8. inspect the actual generated candidate outputs against the matrix; CI/synthetic proof/page-count changes alone are insufficient;
9. issue a **new unique versionName/versionCode**; do not reuse `0.5.0-preqa.8 / 50800`;
10. only then resume owner visual QA.

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
- Visual recovery artifact `10720833172` remains OWNER REJECTED. VR-3 (`a5cb2311a0beb8454291d8b9985cb1b13f37a3dd`, artifact `10723153227`) passed guarded preflight but owner review found additional regressions. VR-4 renderer `f7e4417c05a2981415ef3648ead740e20469fe33`, artifact `10756937024`, passed push #3279 / `35873556136` and PR #3280 / `35873560390`, then received **OWNER APPROVAL on 2026-09-23**. Historical fact: that review accepted a non-literal Equipment/Equipo Especial continuation. **Active owner clarification on 2026-09-28 supersedes that deviation for the current repair:** the next repair must reuse/copy the existing native Equipment and Equipo Especial elements and extend their capacity rather than preserve the later generic reconstruction.

## Current continuation

1. Historical 2026-09-23 visual approval remains provenance, but the PC-sheet visual/layout gate is **REOPENED by the Mara 50800 owner runtime FAIL** for the active defect scope.
2. Renderer `f7e4417c05a2981415ef3648ead740e20469fe33` / artifact `10756937024` remain historical approved evidence; they are not sufficient acceptance for the active Mara defects.
3. The historical non-literal Custom-v2 Equipment / Equipo Especial deviation is superseded for this repair by the 2026-09-28 owner reuse-first clarification and the 50800 acceptance matrix.
4. Historical App Modified candidate `bf7d4d8f5324af7aabb5bf4d3d0038936af2b53d` remains rejected; do not use it as active authority.
5. PR #86 Desktop Save/Share is integrated as `c28ad548113b368413e479de544c85aa8c924ef4`.
6. PR #88 Android generated renderer bridge is integrated as `c5963881bdff2597770d3f6a26992b8567b2a35b`.
7. PR #89 Android Player/authorized-DM Save/Share is integrated as `e6e153a53bba8aa532b5c371dcc16849a901a541`.
8. PR #90 lifecycle/documentation closure is integrated as `6ce3ac35798a1ce915a3dac4227e983932d5ab9f`; post-merge #3344 PASS.
9. **Runtime QA fixture pack is integrated on main through PR #91**: Aldren Vale (strict SRD 5.1 Fighter 5/Champion), Ilyra Quill (strict SRD 5.2.1 Wizard 5/Evoker), and Mara de los Siete Umbrales (CUSTOM high-volume Extended stress).
10. All three fixtures are app-owned Character Backup v2 JSONs suitable for normal Android **Importar**; tests prove decode/round-trip/restore-as-copy.
11. Hosted DEV QA data is now **READY / VERIFIED**: the exact three integrated fixture payloads are present in deterministic campaign `QA - PC Sheet PDF Runtime`; Outlook remains `DM / ACTIVE`; Gmail is deliberately `PLAYER / ACTIVE` for this QA and owns/controls all three PCs. See `docs/checkpoints/2026-09-24_PC_SHEET_RUNTIME_QA_HOSTED_PLAYER_SETUP_READY.md`.
12. Fixture/test head `a06b2fe5712426e6b42e5ea88f0fe0b2f9529fd1` passed #3348/#3349; final docs head passed #3358/#3359; fixture merge `5e778ced3b85fc66d4ca449727a3451e91c50d7e` passed post-merge #3360 / `35915153718`; closure PR #92 merged as `026dc8ca00967e5ca3d932562e2a49d97e6eaaad` and post-merge #3367 / `35915730801` passed.
13. Android export still exposes D-0074 family/state/custom-stat/portrait/Spellbook choices, remains available in Table Mode, and uses the shared planner + generated frozen renderer.
14. Unsaved structural edits still require explicit **Exportar sin guardar** confirmation; PDF projection does not call repository Save.
15. Current Snapshot still lacks a separate local aggregate; shared planner fallback + user-visible notice remains the truthful behavior.
16. Hosted Player-path sync is manually verified and converged. The first Aldren Fantasy Sheet runtime attempt exposed compact combat/resource bounded-routing failures; PR #96 repaired that first set and is integrated as `eece864e5867f477b8bdd57596a1bee1638b4ceb`.
17. Owner Stage 1 then exposed an in-app DEV-tools navigation crash in `0.5.0-preqa.1`; PR #98 repaired it, advanced the QA build to `0.5.0-preqa.2`, and merged as `289eafda731c4b1c61e76a947a86ab5d948c537c`. Runtime owner QA confirms the DEV screen opens, Gmail Player auth works, hosted sync remains converged, 3 PCs are unchanged, conflicts are zero and outbox is empty.
18. Owner Stage 2 reached Aldren Fantasy Sheet / Permanente successfully, but the actual Save/generation attempt exposed a **new bounded-routing failure in long equipment/weapon descriptive content** before a usable PDF save.
19. The repeated pattern is treated as a defect class, not another string-specific patch. The generalized preview/full-detail routing repair is implemented on `fix/fantasy-sheet-generalized-bounded-text-routing` at `29138e7322feae111b6207acb45da896a207d749`; branch Scaffold `36065636679` is SUCCESS. Aldren and Mara real fixtures pass while approved pagination baselines remain intact.
20. PR #100 merged the generalized repair as `fd781262abfeb47003298562e540721a8515071a`; merged-main Scaffold `36067012766` is SUCCESS. Owner runtime on `0.5.0-preqa.3` confirms Save/open across Fantasy, Custom v1 and both Custom-v2 variants. The cross-family Aldren survey is now COMPLETE and proves shared defects in Unicode, semantic continuation routing, inventory/currency routing, unnecessary spell/notes pages and page packing, plus family-specific resource and Custom-v2 typography defects. **Current route:** stop later manual QA and implement the repair package specified by the Canonical active checkpoint. Do not proceed to Share, Ilyra, Mara, Current Snapshot or the final physical-device gate until the repaired Aldren cross-family rerun passes.

22. `0.5.0-preqa.4` generated/opened all four Aldren PDFs but **FAILED owner manual QA**. The authoritative defect list is `docs/checkpoints/2026-09-25_PC_SHEET_ALDREN_PREQA4_CROSS_FAMILY_REVIEW.md`.
23. The repair is repository-green at implementation head `cfaa98f92cecb846babcf83f180783839ccd56c6` (push `36196067865` / #3817 SUCCESS; PR `36196073964` / #3818 SUCCESS). The distinguishable Android candidate is `0.5.0-preqa.5` / `50500` at `62eb4b474b1b55d3e3875ca45a41b30c4f7a4523`; candidate push `36196696061` / #3819 and PR `36196702100` / #3820 are both SUCCESS. Preferred APK artifact: `10890483613`. Owner rerun across Aldren/Permanente Fantasy + Custom v1 + both Custom-v2 modes is **PASS**. Accepted residual: Custom v1/v2 continuation pages may show header/first-row overlap plus excessive vertical row height; owner explicitly classifies it as minor/non-blocking.
24. PR #104 integrated the passed repair into `main` as `8b1618d56d5483524559f9598bc8862acfe91c9a`; post-merge Scaffold #3826 / `36203501345` is SUCCESS. **Current route:** staged Android runtime smoke at Aldren Share, then Ilyra Custom v2 + Spellbook/unsaved-export persistence check, Mara both Custom-v2 Extended variants, and one Current Snapshot fallback-notice check. Media/Handouts remains blocked until that runtime sequence is recorded.
25. **End-of-day pause, 2026-09-25:** owner stopped for the day before executing Aldren Share. At that historical pause, no result had yet been recorded for Share, Ilyra, Mara or Current Snapshot in the resumed sequence.
26. **Runtime-smoke resume, 2026-09-26:** owner reports **Aldren Share PASS**; the shared PDF opened correctly on the second PC and no corruption/readability failure was reported. Step 1 is closed. **Current action:** Step 2 — Ilyra Custom v2 + Spellbook after one harmless unsaved edit, choose `Exportar sin guardar`, verify the PDF reflects the edit, then verify persisted character data remains unchanged.
27. **Ilyra Fantasy repair verification PASS, 2026-09-26:** `0.5.0-preqa.6` / build `50600` removed the Fantasy/DOTES bounded-routing failure. Owner confirmed the generated PDF opens/readable and preserves full Ability Score Improvement semantics including INT 18 and DES 14.
28. **Ilyra staged Step 2 post-confirmation repair, 2026-09-26:** unsaved changes were detected; `Exportar sin guardar` was presented and owner explicitly selected it. Rendering then failed on Potent Cantrip combat compression. The real-Ilyra regression exposed a second latent special-Spellbook one-row overflow after combat was repaired. PR #106 generalized both routes, stamped `0.5.0-preqa.7` / `50700`, and integrated as `fb1e831bdb3f9e53374cffafc24a9f34a2174454`; branch #3887, versioned #3890, PR-head #3894 and merged-main #3895 are SUCCESS. Owner rerun then completed successfully: PDF contained unsaved `Ilyra Quill - QA TEST` and FUE 25, and after leaving without saving/reopening Ilyra the stored baseline `Ilyra Quill` / FUE 8 returned. **Ilyra Step 2 = OWNER PASS / CLOSED.**
29. **Mara active, 2026-09-26:** next staged runtime target is Mara de los Siete Umbrales. Execute 3A **Custom v2 · Atributo + Permanente** first and inspect Extended/custom-stat/overflow continuation behavior; after PASS, execute 3B **Custom v2 · Habilidad + Permanente**. Do not start Current Snapshot until both Mara variants are recorded.
30. **Mara repair integrated / owner-ready, 2026-09-26:** preqa.7 Mara 3A exposed three linked Extended-path defects (source-label micro-fit, featured-trait continuation re-wrap, and long ordinary-equipment identity continuation). The real-Mara regression burned down the chain through #3915/#3918/#3921 and reached green combined validation at #3924. `0.5.0-preqa.8` / `50800` passed exact candidate #3926, consolidated head #3927, PR-head #3928, and merged-main #3929. PR #107 merged as `15f86ec8285e69969054d40defaa2c16259b8dce`. **Current owner action:** rerun Mara 3A only on preqa.8.

No external provider action is required for this PDF-renderer stage.

# Mara 50800 — end-of-day repair handoff

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Validated renderer/code head:** `6e53492dcd9f13945fbb320334f621aad070e61f`  
**Latest validating workflow:** 4212 / `36517524807` — SUCCESS  
**Exact proof artifact:** `pc-sheet-populated-template-proofs` / artifact ID `11011771728`  
**Artifact digest:** `sha256:140b71f0d53c1ace3e0bd53385827d0d234d4d0706b0bff5d476cc595cc3017e`  
**Status:** SAFE PAUSE / RESUME AT PHASE 2B / DO NOT MERGE OR OWNER-HANDOFF YET

## 1. Authority — read this before any code tomorrow

The repair is governed by the real owner QA from build `0.5.0-preqa.8 / 50800`, not by historical green CI, the old adaptive branch, synthetic proofs, page-count reductions or tests that encode rejected behavior.

Read in this order:

1. this handoff;
2. `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md` — 32-item acceptance authority;
3. `docs/checkpoints/2026-09-29_PC_SHEET_MARA_50800_REPAIR_EXECUTION_LOG.md` — run-by-run evidence and decisions;
4. `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_FIX_PHASE_MAP.md` — discrete repair phases;
5. `docs/checkpoints/2026-09-29_PC_SHEET_MARA_50800_PHASE1B_CLOSURE.md`;
6. `docs/checkpoints/PC_SHEET_MARA_50800_PHASE2A_PRECODE_MAP.md`;
7. `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md`;
8. `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`;
9. `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`;
10. `docs/PC_SHEET_PDF_GEOMETRY_GATES.md`.

The old branch `repair/pc-sheet-adaptive-continuations-cross-family` remains **evidence/reference only**. Do not merge or cherry-pick it wholesale.

## 2. Original owner QA evidence that remains authoritative

Real 50800 owner runtime evidence:

- Fantasy — 45 pages — SHA-256 `a4a5864dcb4a1d39570609e671ea860d2de440077bc8d6b62e5be22668909bf7`;
- Custom v1 — generation FAIL:
  `Custom-v1 source-matched label requires excessive compression: 'Lectura de presagios' (49.175125%)`;
- Custom v2 · Atributo — 28 pages — SHA-256 `74ccaf54f29ef03f1d95724cc63728c05cd5f09ef3c22aaacd739dceb9ae6b96`;
- Custom v2 · Habilidad — 27 pages — SHA-256 `bd0cc116031372526f0b160133598f8b69992d8936b1c3b383e35b300d2897a6`;
- Custom-v1 failure screenshot — SHA-256 `a39cd801b6b30c09462fe1f119e760f141215d47e92c7f5faa158fe0c5d3da34`.

Owner clarifications that must never be lost:

- **reuse before reconstruction** for every semantic family with a usable native/source component;
- this includes attributes, Traits/Rasgos, Trasfondo/Historia-style modules, ordinary Equipment, Equipo Especial, Notes and analogous sections;
- ordinary Equipment shows compact identity only — no weight, no `Consumible`, no prose descriptions;
- Equipo Especial must reuse its native component;
- custom locations such as `Bolsa lateral` must be legitimate rows/cells and must not overprint source labels or damage checkboxes;
- Notes overflow must reuse the existing Notes grammar;
- character name is centered horizontally and vertically in its ribbon, with a centered two-line fallback if one line does not fit cleanly;
- meaningful semantic names wrap; do not silently ellipsize;
- there is no fixed minimum/maximum page-count target;
- **no fix is accepted without inspecting the actual generated result of the exact candidate**.

## 3. Phase 1 / 1B — current state

Phase 1B has an internal artifact-reviewed progression pass at:

- renderer head `a5ac8d5edbcc1184ecc8dc9496db8933b029a73f`;
- workflow 4200 / `36509536738`;
- proof artifact `11009076550`.

Demonstrated on real Mara output:

- Custom v1 generates;
- ordinary Equipment does not show weight;
- ordinary Equipment does not show `Consumible`;
- ordinary Equipment does not show prose descriptions;
- compact Equipment identity is preserved;
- V1/V2 use native Equipment / Equipo Especial source grammar rather than the rejected generic Inventory page;
- V2 Equipment continuation uses source page 3, not either main-sheet page;
- `Bolsa lateral` is visibly placed in the native location column;
- V2 custom-location cleanup preserves the native checkbox;
- final Special Equipment item remains present.

Phase-pass candidates from this work:

- M50800-02;
- M50800-15;
- M50800-16;
- M50800-18;
- M50800-19.

Still explicitly OPEN:

- **M50800-17** — long Equipment / Equipo Especial atomicity/readability;
- **M50800-20** — adaptive reclaim / redundant scaffold elimination.

These phase-pass candidates are **not final FIXED statuses**. They must be reconfirmed on the final exact candidate before owner handoff.

## 4. Phase 2A — QA source and work completed today

Phase 2A was created directly from the real 50800 Custom-v2 evidence:

- Atributo: owner pages 1, 5, 6;
- Habilidad: owner pages 1, 5.

Original 50800 observations driving this phase:

- page 1 already proves six attributes fit at native scale;
- Atributo page 5 used three oversized generic cards and page 6 existed only for Renombre;
- Habilidad page 5 painted two fake/empty attribute shells;
- both used `ETE · Éter`;
- Extended text extraction contained unrelated source-page strings from a clipped whole-page underlay;
- Mara's long name was forced into a one-line ribbon presentation.

Phase 2A scope is **only**:

- M50800-03;
- M50800-04;
- M50800-05;
- M50800-06;
- M50800-07;
- M50800-25.

No Traits, Notes, Fantasy or general adaptive packing work belongs to Phase 2A.

### 4.1 Changes implemented

`bd2a48521aafd743ddd93648310018fe0d17fb7f`
- M50800-03: Extended header logo changed from a clipped whole-source-page form to an actual raster crop of logo pixels, preventing hidden source-page text from remaining extractable.
- M50800-25: Custom-v2 name ribbon changed to geometric horizontal/vertical centering with WORD_WRAP, max 2 lines and no optical-Y hack.

`9bc585541c3686a4b361240114715ae8d6e28d54`
- M50800-04/05/06: Custom Statistics rebuilt around the compact native V2 attribute grammar rather than the rejected 3-column generic cards.
- per-Attribute packs native-scale attribute blocks; Mara's four attributes fit on one page.
- per-Ability renders only actual attribute modules; fake fifth/sixth attribute shells are not painted.
- standard-bound skills and definitions remain separate semantic groups.

`f5ce07fcbb8268c57feb19d71bb094b24804bba8`
- M50800-07 artifact-derived glyph repair: dynamic custom-stat labels switched away from imported Corbel subset fonts to complete embedded Fira fonts after artifact QA showed that PDF text extraction could say `Éter` while the visible PDF lost the `É` glyph.

`7461ae2b5e399b542feeb0ef131b646242a32e59`
- compact dynamic skill labels receive bounded font-size fitting rather than reverting to incomplete source-font subsets.

`6e53492dcd9f13945fbb320334f621aad070e61f`
- native compact-row horizontal scale (78%) is preserved for full-font dynamic skill labels, then bounded size reduction is used only if necessary.
- this was required after 4211 showed `Contratos arcanos` did not fit with size reduction alone.

Desktop and generated Android renderers remain synchronized.

## 5. Phase 2A run history that matters

- 4204 / `36512470386` / `5da93721...` — PASS, documentation/pre-code map only.
- 4205 / `36512622238` / `bd2a4852...` — FAIL during first header/ribbon implementation validation.
- 4206 / `36512925922` / `9bc58554...` — FAIL during first native-stat implementation validation.
- 4207 / `36513175606` / `aedee9b...` — one focused test failure after Android image-generation generalization.
- 4208 / `36513783110` / `4debf9a9...` — two focused semantic-label test failures.
- 4209 / `36515547745` / `4fa7efb5...` — **CI PASS / ARTIFACT QA FAIL**: statistics structure was correct, but visible `Éter` lost the `É` glyph because imported Corbel was a source PDF subset.
- 4210 / `36516524777` / `f5ce07fc...` — FAIL: full-font `Manipulación de éter` did not fit native compact row.
- 4211 / `36517014608` / `7461ae2b...` — FAIL: size-only fit still could not place `Contratos arcanos`; proved native row also depends on horizontal scaling.
- **4212 / `36517524807` / `6e53492d...` — SUCCESS.**

## 6. Exact artifact QA — run 4212

Exact artifact:

- name: `pc-sheet-populated-template-proofs`;
- artifact ID: `11011771728`;
- digest: `sha256:140b71f0d53c1ace3e0bd53385827d0d234d4d0706b0bff5d476cc595cc3017e`;
- workflow: 4212 / `36517524807`;
- renderer head: `6e53492dcd9f13945fbb320334f621aad070e61f`.

Real Mara PDF observations:

- Fantasy: 42 pages;
- Custom v1: 39 pages;
- Custom v2 Atributo: 26 pages;
- Custom v2 Habilidad: 26 pages.

These counts are observations only, never acceptance ceilings.

### 6.1 Regression checks preserved from Phase 1B

Across the exact artifact:

- no `Consumible`;
- no ordinary Equipment prose description `Descripción suficientemente larga del objeto 2.`;
- final Special Equipment semantic content `objeto 29` remains present;
- V2 Atributo p.25 visibly retains `Bolsa lateral`, the item 29 identity/description and an intact checkbox.

PDFTextStripper is not treated as the visual oracle for the custom-location cell; rendered output is authoritative.

### 6.2 Custom-v2 Atributo — 4212

- total pages: 26;
- `ESTADÍSTICAS PERSONALIZADAS` occurs only on page 5;
- page 5 contains Fortuna, Cordura, Éter and Renombre;
- all four use compact native-style score/modifier modules;
- no fifth/sixth fake attribute shell is visible;
- no second statistics page exists for Renombre;
- visible `Éter` glyph is correct;
- no `ETE · Éter` label;
- page-5 extracted text contains no `UBICACIÓN`, `EQUIPO ESPECIAL` or `CLASE Y NIVEL` underlay leakage.

The page still contains substantial Definitions/Notes capacity. That is not claimed as an adaptive-packing fix; general reclaim belongs to later phases.

### 6.3 Custom-v2 Habilidad — 4212

- total pages: 26;
- `ESTADÍSTICAS PERSONALIZADAS` occurs only on page 5;
- Fortuna, Cordura, Éter and Renombre are all on page 5;
- only four real attribute modules are visible;
- visible `Éter` glyph is correct;
- no `ETE · Éter` label;
- no source-underlay leakage on the statistics page.

The saves/skills columns still contain unused physical rows. Those are **not phantom attribute shells**; any later page-space reclaim belongs to M50800-27 / adaptive packing and must not be silently folded into Phase 2A.

### 6.4 Character-name ribbon — 4212

Mara's page-1 name is visibly centered inside the native ribbon and fits cleanly on one line in this artifact.

The renderer now supports a centered two-line fallback for longer names.

Internal artifact QA is positive for M50800-25, but this remains a final-candidate/owner reconfirmation item; do not claim owner approval from this internal artifact alone.

## 7. Phase 2A disposition at safe pause

**Internal progression candidates / positive exact-artifact evidence:**

- M50800-03 — no stale source-underlay text on statistics page;
- M50800-04 — compact native attribute grammar reused;
- M50800-05 — all four Mara attributes fit together on one page;
- M50800-06 — no phantom fifth/sixth attribute shell;
- M50800-07 — visible clean `Éter`;
- M50800-25 — centered ribbon + two-line fallback available.

These six items remain formally OPEN in the master matrix until final exact-candidate acceptance. Do not rewrite the matrix to FIXED yet.

**Phase 2A internal progression gate: PASS at 4212.**

## 8. Exact resume point for tomorrow

Resume at **Phase 2B**, not Phase 1B and not Phase 2A, unless a new concrete regression is found in artifact 4212.

Recommended discrete order:

### Phase 2B.1 — Traits/Rasgos + Trasfondo/Historia native reuse
Primary matrix items:
- M50800-08;
- M50800-09;
- M50800-10;
- M50800-11.

Rules:
- category grouping for Traits is intentional;
- reuse existing/native Traits/Rasgos examples;
- reuse existing Trasfondo/Historia/personality-style modules;
- do not reconstruct generic prose/panel systems.

### Phase 2B.2 — Notes native reuse
Primary matrix items:
- M50800-21;
- M50800-22;
- M50800-23;
- M50800-24.

Rules:
- reuse existing Notes sheet/module;
- `Nota N` visually distinguishable;
- visible separation between note records;
- continuation retains note identity;
- consume native Notes rows/columns before another page.

### Phase 2B.3 — residual Equipment readability/reclaim
Primary matrix items:
- M50800-17;
- M50800-20.

Do not reopen M50800-15/16/18/19 unless new artifact evidence shows regression.

## 9. Later phases still untouched

Phase 3:
- M50800-12 Custom-v2 Combat/Actions;
- M50800-14 Resources/Options reclaim;
- broader M50800-27 adaptive continuation;
- residual Notes/Traits adaptive packing.

Phase 4:
- Fantasy-specific M50800-13 / M50800-26 and related packing.

Phase 5:
- M50800-01 unique owner-candidate identity;
- M50800-29 final data preservation;
- M50800-31 complete matrix against exact candidate;
- M50800-32 all-four-family smoke;
- new versionName/versionCode — **never reuse 50800**;
- only then owner visual QA.

## 10. Operating rules for the next session

1. Do not merge this branch yet.
2. Do not create an owner APK yet.
3. Do not declare the whole 50800 repair fixed.
4. Read this handoff + acceptance matrix before code.
5. Reopen the exact owner QA evidence for the semantic family being repaired.
6. Before each subphase, write/update the QA/source map if the implementation surface is not already explicit.
7. Keep Desktop authoritative and Android generated/synchronized.
8. After every visually meaningful subphase, inspect the exact generated artifact; CI alone is insufficient.
9. A historical test that encodes rejected 50800 behavior may be corrected, but document why it contradicts the QA.
10. Never solve a missing-glyph problem by returning arbitrary dynamic text to a source PDF subset font.
11. Never solve a fit problem by excessive compression merely to turn CI green.
12. Preserve the owner's reuse-first rule: extend native components before inventing new ones.

## 11. Safe-stop statement

At this pause:

- Phase 1B progression gate is complete with M50800-17/20 intentionally OPEN;
- Phase 2A internal artifact-QA progression gate is complete at run 4212;
- no Phase 2B renderer work has intentionally begun;
- the branch is green at renderer/code head `6e53492dcd9f13945fbb320334f621aad070e61f`;
- the exact artifact and digest required to reconstruct today's visual state are recorded above.

Tomorrow should begin from this checkpoint and Phase 2B.1.

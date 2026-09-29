# Mara 50800 — repair execution log

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Status:** ACTIVE / NOT OWNER-ACCEPTED / DO NOT MERGE YET  
**Acceptance authority:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`  
**Phase map:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_FIX_PHASE_MAP.md`

## Governing rule

This repair is driven by the 50800 owner QA matrix, not by historical renderer tests or by the old adaptive continuation branch.

A test that encodes a rejected 50800 behavior may be updated when the matrix explicitly contradicts it. A matrix item is **not** closed merely because CI turns green. Items that require rendered-PDF evidence remain pending until the exact generated artifacts are inspected.

The old branch `repair/pc-sheet-adaptive-continuations-cross-family` is reference/evidence only. It is not merged or cherry-picked wholesale.

## Current execution phase

Active phase: **Phase 1B — native Equipment / Equipo Especial semantics and Custom-v1 generation safety**.

Phase 2A (Custom-v2 Custom Statistics/native attribute reuse) has been analyzed but intentionally not modified yet. It remains gated on Phase 1B CI plus real-artifact inspection.

## 50800 items addressed by the active code path

| Matrix item | Current implementation state | Closure state |
| --- | --- | --- |
| M50800-02 Custom v1 generation | Source-fit blocker rebuilt away from the failing generic continuation path. Real Mara fixture now reaches the focused runtime assertions instead of failing on `Lectura de presagios`. | **CANDIDATE / artifact still required** |
| M50800-15 ordinary Equipment design | Custom v1/v2 continuation now uses native Equipment-page grammar instead of the rejected `INVENTARIO / EQUIPO` generic presentation. | **CANDIDATE / visual comparison still required** |
| M50800-16 ordinary Equipment content | Focused runtime QA rejects `Consumible`, weight values and ordinary prose descriptions, while requiring compact recognizable item identity. | **CANDIDATE / artifact still required** |
| M50800-17 ordinary Equipment readability | Ordinary identity is kept in native Equipment rows. Final visual atomicity still requires PDF inspection. | **OPEN pending visual evidence** |
| M50800-18 Equipo Especial design | Continuation is rendered in the native/source Equipo Especial table rather than a separate generic Extended table. | **CANDIDATE / visual comparison still required** |
| M50800-19 custom locations | Used native location cells are cleared before custom values are written; focused QA requires `Bolsa lateral`. | **CANDIDATE / rendered-page inspection still required** |
| M50800-20 redundant Equipment scaffolds | Continuation allocation is data-driven; historical tests that required an Equipment page without Equipment overflow were corrected to stop demanding empty scaffolds. | **CANDIDATE / page-level artifact review required** |
| M50800-30 pre-fix comprehension gate | Acceptance matrix + phase/source map existed before renderer repair work on this branch. | **SATISFIED as process gate** |
| M50800-32 cross-family generation | Focused real-Mara fixture executes Fantasy, Custom v1, Custom v2 Atributo and Custom v2 Habilidad. | **OPEN until one exact CI artifact set passes and is inspected** |

No other matrix item is implicitly closed by this phase.

## Special Equipment packing rule introduced

A Special Equipment item is now a semantic unit for pagination.

- Its location, name and dedicated description/notes are wrapped into native rows.
- The rows belonging to one item are kept together.
- If the complete item does not fit in the remaining native rows, the item moves as a whole to the next native Equipment copy.
- The base page and continuation renderer use the same page-capacity rule.
- This prevents cutting an item's identity/context at the base/Extended boundary.

The focused Mara QA requires three independent anchors for the final special item rather than relying on PDFTextStripper column ordering:

- leading identity: `Cuaderno de fórmulas`;
- custom location: `Bolsa lateral`;
- unique final-item semantic content: `objeto 29`.

The exact rendered name remains subject to visual artifact inspection; this assertion change is not treated as visual closure.

`Bolsa lateral` is also **not** waived: run 4195 showed that PDFTextStripper is not a reliable acceptance oracle for that Custom-v1 location cell. M50800-19 therefore remains OPEN and must be decided from the rendered native Equipo Especial cell in the exact green-run artifact.

## CI / commit ledger

| Run | Commit | Result | Relevant diagnosis |
| --- | --- | --- | --- |
| 4188 / `36501811667` | `7354081ec8fefafde2408a7f67c716f9260bebfd` | FAIL | Earlier V1 refactor still had structural compile fallout. |
| 4189 / `36502124625` | `bfc1360db83c8e6c02e22fb7e03c2dfe954bd9bd` | FAIL | V1 clean reconstruction compiled far enough to expose only V2 `opticalX/opticalY` helper mismatch. |
| 4190 / `36502322050` | `2432d668fc76a957e6a86a127532f13b6eecab67` | FAIL | Build/tests reached semantic QA; exposed real V1 loss of final Special Equipment item plus historical generic-layer expectations. |
| 4191 / `36503127638` | `87db894a3b826c4521f06ce7c030886eb7296ad9` | FAIL | Whole-item Special Equipment packing added; compile stopped on one stale `specialRows` identifier. |
| 4192 / `36503258869` | `7e591b9764a391411e750254c1e1b6539f446dbe` | FAIL | Same stale identifier; V2 treasure standard semantic layers also restored without visual change. |
| 4193 / `36503433050` | `6d63d295bdda3c6ede6278214ebfe20b74436e2a` | FAIL | 84 tests executed; only two assertions remained: PDF text-order assumption for final Mara special item and a fixture incorrectly requiring Equipment continuation without Equipment overflow. |
| 4194 / `36504198065` | `ae44bc5dc3195525b838d7791e020dd7113021b6` | FAIL | 84 tests executed; **one** assertion remained, the PDFTextStripper tail-substring assumption for the final V1 special item. No compile or other test failure. |
| 4195 / `36505758967` | `e7a7f31c29707dc8035fa9ded8bc5dab95db7b2b` | FAIL | 84 tests executed; final Special Equipment identity/content passed. Only `Bolsa lateral` text extraction failed in Custom v1. M50800-19 remains a rendered-cell gate rather than being waived or declared fixed. |
| 4196 / `36506437746` | `1e03d22a95fe3f8a93bc11ed27b6d3701ece450c` | **CI PASS / ARTIFACT QA FAIL** | Kotlin job green and proofs uploaded. Artifact `11007511945` was inspected. Custom-v1 p.38 correctly shows `Bolsa lateral` and the complete item 29 in native Equipo Especial. Custom-v2 Atributo p.26 / Habilidad p.25 incorrectly copy a main-sheet source page and write Special Equipment over its footer. M50800-18/19/20 remain OPEN. |
| 4197 / `36507345237` | `12db45fab5592de15f271a506eb73cf7d35fdfa5` | FAIL before tests | Renderer/source-page correction compiled; focused test failed to compile only because `assertFalse` was not imported. No renderer failure observed. |

## Historical-test corrections made under matrix authority

Tests were changed only where the 50800 matrix directly invalidates their old expectation:

1. old generic layer names such as `V1X INVENTORY` / `V2X INVENTORY` were replaced with native Equipment continuation semantics;
2. a spell/note-overflow fixture no longer requires an Equipment continuation page when it did not add Equipment overflow, matching M50800-20;
3. the final Mara Special Equipment assertion no longer depends on contiguous text extraction across PDF columns/rows; it uses unique semantic anchors and remains subject to rendered-PDF review.

These changes do **not** relax the prohibitions on weight, `Consumible`, ordinary descriptions, generic Inventory presentation, or loss of custom Special Equipment locations/content.

## Gate before Phase 2A

Do not start Custom Statistics renderer changes until all of the following are true:

1. current branch CI Kotlin job is green;
2. the generated real-Mara PDFs for all four families are available from that exact run;
3. Custom v1 actually generates;
4. text scan confirms ordinary Equipment does not contain weight, `Consumible` or ordinary prose descriptions;
5. rendered Equipment / Equipo Especial pages are visually inspected for native reuse, readable item identity, custom locations and redundant scaffolds;
6. Ilyra/control behavior is checked where the focused suite/artifact provides it.

If any visual acceptance item is ambiguous, keep it OPEN and continue the corresponding repair phase. Do not infer closure from CI alone.

## Phase 2A remains OPEN

The following matrix items are intentionally not claimed by the Phase 1B work:

- M50800-03 source-underlay leakage;
- M50800-04 native Custom Statistics grammar;
- M50800-05 four real Mara attributes together at native scale;
- M50800-06 no phantom attribute shells;
- M50800-07 clean/unambiguous Éter presentation;
- M50800-25 name-ribbon centering/wrap.

The real 50800 Atributo/Habilidad pages remain the visual baseline for those changes.


## Artifact review - run 4196

Exact proof artifact: `pc-sheet-populated-template-proofs`, artifact ID `11007511945`, workflow run `36506437746`, head `1e03d22a95fe3f8a93bc11ed27b6d3701ece450c`.

Observed real-Mara outputs:

- Custom v1: 39 pages. Page 38 uses the native Equipment/Monedas/Equipo Especial source page. The final special item appears with custom location `Bolsa lateral`, name `Cuaderno de fórmulas personales y mapas plegables 29`, and its dedicated description/QA note. This is positive evidence for M50800-18/19 in Custom v1, but M50800-20 remains open because broader adaptive reclaim is not yet demonstrated.
- Custom v2 Atributo: 27 pages. Page 26 contains the final special item but the underlay is the **wrong source page** (main character page), producing severe overlap at the bottom.
- Custom v2 Habilidad: 26 pages. Page 25 has the same wrong-source-page defect.

Root cause confirmed in code: `renderNativeEquipmentContinuation` used `resources.forms[1]`. The planner defines Custom-v2 Equipment/Trasfondo as source page **3**, therefore the imported zero-based form must be `resources.forms[2]`. Source pages 1 and 2 are the alternative main-sheet variants.

A focused regression is added so the page containing Mara's final Special Equipment item must contain `EQUIPO ESPECIAL` and must **not** contain `CLASE Y NIVEL`.


## Runs 4197-4198 — source-page correction derived from 4196 artifact QA

Run 4196 was green in CI but **failed artifact QA**: the real Custom-v2 Atributo/Habilidad continuation pages copied a main-sheet source underlay. That result, not CI, drove the next fix.

- 4197 / `36507345237` / `12db45fab5592de15f271a506eb73cf7d35fdfa5`: renderer changed Custom-v2 native Equipment continuation from `resources.forms[1]` to `resources.forms[2]`, matching the planner's source page 3 authority. CI stopped only because the newly added test used an unimported `assertFalse`.
- 4198 / `36507646803` / `71c759df166ba121531e5cc085744b69af9581d1`: test compiled, but its page locator incorrectly assumed the final item text `objeto 29` must be extractable as one page-local regex.

The focused regression is now aligned directly to the 50800 defect:

1. both V2 families must contain the native `EQUIPO ESPECIAL` page;
2. each V2 PDF must contain **exactly one** page with `CLASE Y NIVEL`;
3. therefore a continuation cannot silently copy either main-sheet source page.

The final Special Equipment item remains protected separately by the global semantic assertions (`Cuaderno de fórmulas` + unique `objeto 29` content). M50800-18/19/20 remain OPEN until the exact green-run PDFs are inspected visually.

This change does not weaken M50800-19 and does not treat text extraction as a substitute for rendered-cell QA.


## Artifact review - run 4199

Exact workflow run: `36508734878`, head `861dc6a220ba3f30be030c9c414e8ffba7f13a1d`.  
Exact proof artifact: `pc-sheet-populated-template-proofs`, artifact ID `11008661146`.

CI result: **PASS**.

Real-Mara outputs inspected from that artifact:

- Fantasy: **42 pages**;
- Custom v1: **39 pages**;
- Custom v2 Atributo: **27 pages**;
- Custom v2 Habilidad: **26 pages**.

Text/content scan on the exact PDFs:

- no `Consumible` in any family;
- no equipment `lb` values in any family;
- no ordinary-item prose description `Descripción suficientemente larga del objeto 2.`;
- final Special Equipment semantic content `objeto 29` remains present;
- Custom-v2 PDFs now contain `CLASE Y NIVEL` only on page 1, confirming the Equipment continuation no longer copies a main-sheet source page.

Rendered-page inspection:

### Custom v1

Page 38 uses the native Equipment / Monedas / Equipo Especial source page.

Positive evidence:
- `Bolsa lateral` is visibly present as the custom location;
- the final item identity and description remain present across native rows;
- no generic `INVENTARIO / EQUIPO` replacement page is used.

Open observation:
- the long Special Equipment identity spans several physical native rows. M50800-17 remains OPEN pending final atomic/readability judgment; CI/text preservation is not enough.

### Custom v2 Atributo / Habilidad

Atributo p.26 and Habilidad p.25 now use the correct source page containing `EQUIPO`, `TRASFONDO` and `EQUIPO ESPECIAL`. The prior 4196 main-sheet-underlay overlap is gone.

Positive evidence:
- `Bolsa lateral` is visibly written into the Special Equipment location column;
- the final item remains present;
- no `CLASE Y NIVEL` underlay is visible on the continuation.

New artifact-QA defect:
- the cleanup rectangle used to remove the source's canonical location label overlaps the native checkbox column;
- the checkbox is partially erased and renders as a bracket-like `]` on used rows;
- this is a direct M50800-19 failure because a legitimate custom location must not damage the existing native row element.

Repair derived directly from this artifact:
- reduce the location cleanup mask from 78 pt to 70 pt in both the Custom-v2 base and Extended native Equipment renderers;
- preserve the native checkbox geometry while still clearing the canonical location text.

Disposition after run 4199:

- M50800-02: **CANDIDATE PASSED generation; keep closure tied to final candidate artifact**;
- M50800-15: **CANDIDATE positive native-reuse evidence; final visual closure still pending**;
- M50800-16: **CANDIDATE PASS on exact artifact text/content scan**;
- M50800-17: **OPEN**;
- M50800-18: **CANDIDATE positive source-page evidence; final visual closure still pending**;
- M50800-19: **OPEN - artifact defect found and repair applied after 4199**;
- M50800-20: **OPEN** because broader adaptive space reclaim is not demonstrated by this phase.

Phase 2A remains gated until the post-mask-fix artifact is rendered and inspected.


## Artifact review - run 4200 / Phase 1B progression gate

Exact workflow run: `36509536738`  
Head: `a5ac8d5edbcc1184ecc8dc9496db8933b029a73f`  
Proof artifact: `pc-sheet-populated-template-proofs`, artifact ID `11009076550`  
CI: **PASS**

Real-Mara outputs remain:

- Fantasy: 42 pages;
- Custom v1: 39 pages;
- Custom v2 Atributo: 27 pages;
- Custom v2 Habilidad: 26 pages.

The page counts are observations only, not acceptance targets.

Exact artifact checks:

- no `Consumible`;
- no equipment weight in `lb`;
- no ordinary Equipment prose description for object 2;
- Custom v1 generates successfully;
- V2 `CLASE Y NIVEL` occurs only on the real main page;
- V2 continuation uses the source-native Equipment/Trasfondo page;
- `Bolsa lateral` is visibly present in the Special Equipment location column;
- the post-4199 cleanup-mask fix preserves complete native checkboxes in both base and continuation pages;
- the prior bracket-like partial checkbox artifact is gone.

Phase 1B progression disposition:

- M50800-02: **PHASE PASS / final-candidate reconfirmation required**;
- M50800-15: **PHASE PASS for native Equipment source reuse / final-candidate reconfirmation required**;
- M50800-16: **PHASE PASS on exact artifact**;
- M50800-18: **PHASE PASS for native Equipo Especial source reuse / final-candidate reconfirmation required**;
- M50800-19: **PHASE PASS on exact rendered artifact**;
- M50800-17: **OPEN** — final semantic atomicity/readability of long identities still requires later layout review;
- M50800-20: **OPEN** — broader adaptive reclaim / redundant scaffold elimination is not solved by this phase.

Phase 1B may now hand off to Phase 2A. No other matrix item is implied fixed.


## Phase 2A — run 4207 diagnostic

Run 4207 / `36513175606` / head `aedee9b0232a8e4fd113ec3a25ec8c71773e4638` compiled Desktop and Android successfully and passed the Android renderer sync guard.

It reached 84 Desktop tests with **one** failure:

- `promotesOwnerApprovedCustomV2ExtendedCustomStatisticsFromRealPlanData` still required historical keyed spellings `HONor`, `VOLuntad`, and `SUErte`.

That expectation conflicts with the Phase 2A acceptance rule derived from M50800-07: custom-attribute identity is rendered as its clean semantic name, not by injecting the abbreviation into the name. The test is therefore updated to `Honor`, `Voluntad`, and `Suerte`.

This is a test-authority correction, not a renderer relaxation. The test still requires the Custom Statistics semantic layers and all representative custom-skill content.


## Phase 2A — run 4208 test-authority correction

Run 4208 / `36513783110` / head `4debf9a9a77bf5b42ebb19840eb9ecedbfcb2628` compiled the Phase 2A renderer and executed 84 Desktop tests. Two failures remained, both caused by the previous test edit crossing family scope:

- the **Custom v1** test was accidentally changed to clean semantic names even though Phase 2A does not change Custom v1;
- the **Custom v2** test still required historical keyed names `HONor`, `VOLuntad`, `SUErte`, contradicting M50800-07.

Correction under the 50800 matrix:

- restore the V1 test to its existing keyed-name expectation;
- require clean `Honor`, `Voluntad`, `Suerte` only for Custom v2.

No renderer behavior is relaxed by this commit. M50800-07 remains an artifact gate: Mara must render clean `Éter` and must not emit `ETE · Éter` on the exact generated statistics page.


## Phase 2A — run 4209 exact artifact review

Run 4209 / `36515547745` / head `4fa7efb51fbd5ea958f02d515085d4ab5a13ab0d`  
Proof artifact ID: `11010473535`  
CI: **PASS** (84 Desktop tests + Android/Desktop sync).

Exact real-Mara Custom-v2 artifact inspection:

- Atributo: 26 pages observed; Custom Statistics occurs only on page 5.
- Habilidad: 26 pages observed; Custom Statistics occurs only on page 5.
- page counts are observations only, not acceptance targets.

Text/page evidence:

- both statistics pages contain Fortuna, Cordura, Éter and Renombre;
- neither emits `ETE · Éter`;
- neither statistics page contains stale `UBICACIÓN`, `EQUIPO ESPECIAL` or `CLASE Y NIVEL` source text;
- only real attribute modules are emitted; Atributo no longer needs page 6 and Habilidad no longer paints fifth/sixth attribute shells.

Rendered evidence:

- M50800-03: positive candidate evidence; hidden source-underlay text is gone.
- M50800-04/05/06: positive candidate evidence; native-scale compact modules are reused, all four real Mara attributes fit on one page, and phantom attribute shells are gone.
- M50800-25: positive candidate evidence; `Mara de los Siete Umbrales` is horizontally and vertically centered in the ribbon and fits cleanly on one line. The renderer now has a two-line centered fallback for longer names.
- **M50800-07 remains OPEN / artifact FAIL:** text extraction reports `Éter`, but the rendered PDF visibly shows `ter` in custom attribute/save/skill contexts.

Root cause established from the artifact and renderer:

- imported Corbel fonts are PDF subsets from the source template;
- `font.encode(text)` can succeed even when a subset lacks the visible glyph;
- therefore PDF text extraction can preserve `É` while rendering a blank glyph.

Repair derived from this artifact:

- keep imported source fonts for fixed/native labels;
- render user-defined/dynamic Custom Statistics names and skill labels with complete embedded Fira fonts, retaining the source-matched size/horizontal scale;
- re-run and visually inspect `Éter` before Phase 2A closure.

4209 is therefore **CI PASS / PHASE 2A ARTIFACT QA FAIL**, not an accepted Phase 2A result.

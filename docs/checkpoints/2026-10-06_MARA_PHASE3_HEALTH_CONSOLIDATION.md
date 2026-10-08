# Mara Phase 3 — compositor health consolidation

**Local date:** 2026-10-07 (Chile)  
**Phase:** 3 — renderer repair implementation  
**Branch:** `repair/mara-phase3-semantic-flow-compositor`  
**Main base:** `c4e65955a5f58f88e0b03807b4616a3f8a9323a8`  
**Validated implementation head at capture:** `e6e07a13721d3d56364d6373472a0b5b0074acb6`  
**Current functional implementation HEAD:** `1566bd3c04bd64ec136fbf9c5df510ac60641a66` — M50800-03 isolated Custom-v1 source fragments + role-based continuation-cue routing; fast run `37568418440` SUCCESS; artifact `11459124665` / `sha256:1c9675a4afc5d504f131f15849d81783ba207bd9c304780c30b3fa6f14527d2c`; **CI_GREEN / VISUAL_VALIDATED**  
**Current infrastructure/proof HEAD before this documentation consolidation:** `ab48e71097ef7ef49628507b1f06423a6c8ed916` — focused fast-gate validation run `37566302657` SUCCESS; artifact `11457979404` / `sha256:36dcc8222eabb275747344cf26909c3ea4dc8da65afcc1c704b0c97dd1cd7b5f`  
**Current status:** ACTIVE / NARRATIVE + TRAITS + COMBAT/ACTIONS + NOTES + SEMANTIC SURVIVAL + GENERAL BIDIRECTIONAL CONTINUITY + M50800-03 SOURCE-GEOMETRY / STALE-UNDERLAY VALIDATED; NEXT = EXACT MARA FOUR-FAMILY PROOF GATE  
**Anti-loop result:** `CONTINUE_WITH_GUARDRAIL`  
**Open implementation PR:** none at capture  
**Owner visual handoff:** NOT AUTHORIZED

## Authority / resume route

Resume in this order:

1. `AGENTS.md`
2. `RESUME.md`
3. `docs/checkpoints/LATEST.md`
4. this checkpoint
5. `docs/checkpoints/2026-09-30_MARA_PHASE3_IMPLEMENTATION.md`
6. final burn-down in `docs/checkpoints/2026-09-30_MARA_PHASE2_EXISTING_REPAIR_AUDIT.md`
7. `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`
8. `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`
9. `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md`
10. `docs/PC_SHEET_PDF_GEOMETRY_GATES.md`

The Phase-2 final burn-down and latest final owner decisions remain binding. Do not resurrect provisional questions already resolved there.

## Why this consolidation exists

After the post-Inventory checkpoint, Phase 3 advanced through several compositor/semantic packages in rapid succession. The owner explicitly requested a bounded convergence check before more implementation so that a long CI/fix sequence cannot silently turn into an infinite repair loop.

This checkpoint is that bounded control point. It is not another audit phase and must not be repeated after every commit.

## Post-Inventory package closures

From `a026678b1caa4e49837cdea3da3fffe96268b20c` through `e6e07a13721d3d56364d6373472a0b5b0074acb6`:

1. **Custom-v2 Resources / Options**
   - production compositor integration: `6d0e77ea...`;
   - exhausted option-marker visual cleanup: `29102383...`;
   - final run `37519249101`: SUCCESS;
   - exhausted sibling scaffold disappears and surviving native module reclaims valid space.

2. **Custom-v1 Resources / Options**
   - production compositor integration: `da623415...`;
   - run `37521034176`: SUCCESS;
   - native Run-6 geometry retained and exhausted sibling scaffold disappears.

3. **Fantasy Resources / Options**
   - production compositor integration: `41be34d9...`;
   - semantic identity wrap/no-ellipsis follow-up: `f4dd69da...`;
   - final run `37524780962`: SUCCESS.

4. **Custom-v2 Traits**
   - native adaptive columns: `f600d4fc...`;
   - native header-band restoration: `9d8f9ad8...`;
   - native left/right slot preference correction: `d8a4e4a8...`;
   - final run `37528580904`: SUCCESS;
   - category grammar retained, within-category order preserved, exhausted scaffold removed.

5. **Fantasy Traits**
   - native-frame compositor: `c7788db6...`;
   - stale test/fixture alignment: `d7b94596...`;
   - wrapped identity extraction correction: `7af8addb...`;
   - stored-order correction: `fdad9f87...`;
   - final run `37533912056`: SUCCESS;
   - this package consumed four CI cycles and is the current churn hotspot, but it closed rather than oscillating.

6. **Custom-v1 Narrative association**
   - narrative overflow moved out of generic Traits and into native BACKGROUND_STORY modules: `1854a099...`;
   - over-broad regression gate narrowed to the canonical semantic association: `e6e07a13...`;
   - run `37537059037`: backend / hosted-database / kotlin all SUCCESS;
   - proof artifact `11447835297`, digest `sha256:059eb8a5214b32b1e7a346f748e0cadea81b552c393395a35ebd82e2dc6cd0c0`;
   - visually inspected proof `custom-v1-narrative-native-module-association.pdf`: narrative occupies native `Rasgos de Personalidad` / `Historia del Personaje` surfaces, late tokens survive, and the following Traits surface does not contain the narrative payload.

## Bounded anti-loop result

Observed post-Inventory CI cycles across the six packages above:

- total coherent CI cycles: **14**;
- SUCCESS cycles: **8**;
- FAILURE cycles: **6**;
- material packages closed: **6**;
- packages reopened after closure under the current regression suite: **0**;
- largest single-package chain: **Fantasy Traits = 4 cycles**, then closed;
- audited capture head `e6e07a13...` regression suite: GREEN;
- physical branch later advanced to `92e460f0...`; its first Custom-v1 Traits cycle is RED and is recorded below rather than being folded backward into the already-completed health sample.

Interpretation: **not an infinite loop**. The sequence shows real convergence, but Traits is a churn-prone implementation surface. Continue only with an explicit guardrail.

## Guardrail for remaining Phase 3

For each new implementation front, track:

`criterion -> observed failure -> root cause -> change -> result`

Stop that front for structural diagnosis before another corrective commit when any of these occurs:

1. the same acceptance criterion survives two corrections for the same root cause;
2. the same front reaches **4 CI cycles without material closure**;
3. a previously closed criterion begins oscillating A -> B -> A;
4. tests are repeatedly relaxed without a canonical Phase-2/visual-contract reason;
5. the number of material OPEN items for that front does not decrease.

A test change is valid only when the old test contradicts the canonical contract; green-by-relaxation is not acceptance.

Do not repeat this health consolidation unless one of the guardrail triggers fires or the owner explicitly requests another one.

## Post-consolidation physical-state correction

The branch advanced after this consolidation was first captured. This does **not** invalidate the bounded audit and must not trigger rollback or replay.

Physical state initially captured on 2026-10-06; superseded for the active route by the later Traits closure recorded below:

- initial functional implementation head for this bounded front: `92e460f005cb1f7f3cb76e3a4d6ebfa34afb04a4` — `feat: compose v1 Traits from native modules`;
- parent documentation commit: `12172e330b38f26edb6dc0b99e6a0b426e935bdc` — this health consolidation;
- Narrative validation remains anchored at `e6e07a13721d3d56364d6373472a0b5b0074acb6` / Scaffold #4514 / run `37537059037` — backend, hosted-database and kotlin SUCCESS;
- Narrative proof artifact: `11447835297`, digest `sha256:059eb8a5214b32b1e7a346f748e0cadea81b552c393395a35ebd82e2dc6cd0c0`;
- direct proof inspection confirmed native `Rasgos de Personalidad` / `Historia del Personaje` association, late tokens `PersonalidadNarrativaV128` and `HistoriaNarrativaV142`, and no narrative payload in the following Traits surface;
- current Traits run: Scaffold #4516 / `37543504407` — backend SUCCESS, hosted-database SUCCESS, kotlin FAILURE, no proof artifact;
- open implementation PR: none.

Historical Custom-v1 Traits failure packet at #4516 (now resolved by the closure section below):

1. `promotesOwnerApprovedCustomV1TraitsContinuationWithoutCustomStatistics` fails because source-only metadata on a trait already represented in the base sheet is emitted again as Extended trait detail. The current implementation treats `source` alone as sufficient continuation detail.
2. `keepsCustomV1NarrativeOverflowOutOfTraitsAndInNativeStoryModules` also fails. The new Traits-page selector matches extracted `Otros Rasgos y Atributos` text, which can exist in source/native text on narrative proof pages; therefore this failure is a **regression signal but not proof of a visual Narrative regression**. Do not weaken the semantic contract merely to green the test. Re-establish page/module ownership precisely and require a new proof before closing the Traits package.

Historical anti-loop state at #4516: no trigger. The front later closed in three cycles with OPEN 2 -> 1 -> 0; see the closure section below.

## Custom-v1 Traits closure after consolidation

The bounded Custom-v1 Traits front is now **CLOSED / VALIDATED**.

Functional sequence:

1. `92e460f0...` — native `Otros Rasgos y Atributos` compositor; Scaffold #4516 / `37543504407` failed two criteria.
2. `10fa1fdb...` — precise semantic Traits sentinel for Narrative ownership plus first source-metadata correction; Scaffold #4517 / `37553188998` reduced OPEN from two criteria to one.
3. `c2367e11bef9e92990f0f9efc6e89b13cf07795f` — source-only metadata is suppressed for already represented traits while true overflow and meaningful continuation metadata retain source context; Scaffold #4518 / `37553725881` is backend / hosted-database / kotlin **SUCCESS**.

Proof artifact:

- artifact: `11454232332` — `pc-sheet-populated-template-proofs`;
- digest: `sha256:37645e52aab6949580ca0c87cd5567b655aebeb73f12a810a1a2d65d552f4107`;
- inspected actual PDFs, not only extracted text:
  - `custom-v1-traits-native-module-reclaim.pdf`;
  - `custom-v1-production-extended-traits-pass2.pdf`;
  - `custom-v1-narrative-native-module-association.pdf`.

Observed acceptance evidence:

- Custom-v1 Traits reuses the native `Otros Rasgos y Atributos` module geometry;
- true overflow records 31–34 survive in coherent stored order inside their semantic categories;
- one small name-only overflow uses one native module rather than reserving a redundant second Traits page;
- the represented source-only trait no longer creates duplicate Extended metadata;
- a represented trait with real continuation semantics still preserves source/tracker/recovery context;
- Narrative late tokens `PersonalidadNarrativaV128` and `HistoriaNarrativaV142` remain on native Narrative/Story surfaces;
- the actual Traits-owned sentinel page contains no Narrative payload;
- Desktop/Android renderer parity guard passed.

Anti-loop result for this front:

- CI cycles: **3** (#4516, #4517, #4518);
- material OPEN progression: **2 -> 1 -> 0**;
- reopened criteria: **0**;
- oscillation: **none**;
- test-relaxation-only closure: **no**;
- `LOOP_SUSPECTED`: **NO**.

Visible residual observed during proof inspection:

- the historical/source-derived footer text `EXTENSIÓN: NOTAS` is still visible on some copied native continuation surfaces.
- This was already present outside the Traits-specific correction and is **not hidden or declared fixed here**.
- Carry it forward under the remaining M50800-03 source-geometry / stale-underlay audit. It does not reopen the now-closed Traits semantic/packing front.

## Combat / Actions closure after Traits

The M50800-12 / M50800-13 Combat/Actions front is now **CLOSED / VALIDATED** without renderer reimplementation.

Read-only reconciliation found the current production behavior already present and parity-synced:

- Custom-v2 variable-height logical rows: `c9014c52...` + Android sync `75b88d45...`;
- Fantasy table-grammar continuation: `66277458...` + test `a53409c8...` + geometry `e7dfe811...` + Android sync `f1ed4022...`;
- current branch retained those implementations unchanged.

The only material gap was durable visual acceptance evidence for Custom-v2. It was closed by test/proof commit:

`fc6fca3776a88dd53b6e636deddfd9f46edac184` — `test: prove Custom-v2 combat logical-row geometry`

Validation:

- Scaffold #4519 / run `37555121975`: backend / hosted-database / kotlin **SUCCESS**;
- proof artifact `11454378341`;
- digest `sha256:6496fd013f3cc627e635c5ba37fb77040b637eb04c5360a9868e041533f1c225`.

Actual PDF inspection:

1. `custom-v2-combat-logical-rows-attribute.pdf`
   - page 6 visually inspected;
   - one short row and one wrapped long row occupy different physical heights;
   - the long action remains one semantic/logical record across Name / Range / Bonus / Effect / Notes;
   - late token `NotaCombateV2Final` survives;
   - no clipping, overlap or semantic ellipsis.

2. `custom-v2-combat-logical-rows-ability.pdf`
   - page 6 visually inspected;
   - same variable-height logical-row behavior and table grammar;
   - no clipping, overlap or semantic ellipsis.

3. `fantasy-canonical-overflow-audit.pdf`
   - page 5 visually inspected;
   - continuation remains a native-style `ARMAS Y ACCIONES — CONTINUACIÓN` table;
   - action identity/bonus/detail remain in table columns;
   - prose flattening does not return.

Acceptance result:

- **M50800-12: demonstrated for current Custom-v2 implementation.**
- **M50800-13: demonstrated for current Fantasy implementation.**
- Desktop/Android parity remains preserved by the mirrored production implementation and CI parity guard.
- This front required **1 CI cycle**, OPEN `1 evidence gap -> 0`, `LOOP_SUSPECTED = NO`.
- Sparse whitespace on low-volume continuation pages is not treated as a defect here because page count/utilization is not an acceptance target. Global cross-module sharing remains governed separately by M50800-27.

## Notes closure after Combat/Actions

The M50800-21…24 Notes front is now **CLOSED / VALIDATED** by read-only reconciliation against the current implementation and the latest actual proof artifact. No production rewrite was necessary.

Current implementation evidence:

- shared `PcSheetPdfNotesFlow.kt` owns semantic Note records only and excludes Background/Narrative;
- `packPcSheetNoteColumns(...)` moves a whole note to a fresh native column when it fits, splits only an oversized note, preserves separator rows, and emits `PcSheetBidirectionalContinuation` markers that consume physical rows;
- Custom-v1 reuses the complete source Notes page for each overflow page and keeps two native Notes columns;
- Custom-v2 uses the same record-aware/native-column flow with the complete native Notes page;
- Fantasy uses the complete native Notes page geometry, including `NOTAS DE CAMPAÑA`, `CROQUIS / MAPA` and `REFERENCIAS Y RECORDATORIOS`; only the native Notes writing column receives Notes semantics;
- Android counterparts were previously parity-synced and #4519 passed the Android renderer-sync guard.

Acceptance artifact:

- run: Scaffold #4519 / `37555121975` — backend / hosted-database / kotlin **SUCCESS**;
- artifact: `11454378341`;
- digest: `sha256:6496fd013f3cc627e635c5ba37fb77040b637eb04c5360a9868e041533f1c225`.

Actual PDF visual inspection:

1. **Custom-v1**
   - `owner-review-continuation-cues-custom-v1.pdf`, pages 5 / 11 / 12;
   - complete native Notes page retained;
   - base page consumes both Notes columns before Extended page 01;
   - Extended page 01 consumes both columns before Extended page 02;
   - final page uses only the remaining left-column capacity because content is exhausted afterward, which is valid;
   - explicit forward/back markers are visible, including normal -> column 2 -> Extended 01 -> Extended 02.
   - `custom-v1-production-extended-notes-pass6.pdf`, page 8: bold/native-compatible `Nota N — Título` headings and visible record spacing.

2. **Custom-v2 Attribute / Ability**
   - `owner-review-continuation-cues-custom-v2-attribute.pdf`, pages 4 / 10;
   - `owner-review-continuation-cues-custom-v2-ability.pdf`, page 10;
   - complete native Notes geometry is retained;
   - both base columns are consumed before the Extended Notes page;
   - bidirectional markers are visible;
   - `custom-v2-per-attribute-whole-draft.pdf`, page 4: `Notas generales` plus `Nota 1…4` headings are visibly emphasized and separated.

3. **Fantasy**
   - `owner-review-continuation-cues-fantasy.pdf`, pages 10 / 11;
   - the full native Notes page, map panel and reference panel remain intact;
   - each native Notes writing column/page is filled before another full Notes page is opened;
   - visible bidirectional markers identify source/destination Notes pages.
   - `fantasy-production-notes-pass7.pdf`, page 4: `Notas generales` and `Nota N — Título` headings are visually emphasized and records remain separated.

Acceptance result:

- **M50800-21: demonstrated** — overflow Notes pages are complete native Notes pages and remain whole-page-exclusive.
- **M50800-22: demonstrated** — Note identities are emphasized and records have visible boundaries.
- **M50800-23: demonstrated** — split Notes carry explicit forward/back navigation.
- **M50800-24: demonstrated** — native Notes columns are consumed before another Notes page; whole records move rather than being split merely to fill residual rows.
- No new Notes CI cycle was required because the current #4519 artifact already exercises the current production renderer and contains the necessary actual PDFs.
- `LOOP_SUSPECTED = NO`.

## Semantic ellipsis / silent-drop closure after Notes

The bounded M50800-26 + M50800-29 semantic-survival front is now **CLOSED / VALIDATED**.

Root observation reopened from actual #4519 proofs:

- Fantasy still rendered semantic `...` in generated character identity / combat preview content;
- production still used `classicSingleLineExcerpt(...)`, which manufactured semantic ellipsis;
- Custom-v1/v2 already had stronger late-token and association gates in their Extended modules.

Corrective sequence:

1. `8823b089ad328de2b4687b453164b9bf3c7126b0` — remove the Fantasy ellipsis helper and route full semantic text through fit/wrap-aware primitives.
   - Scaffold #4520 / `37556848852`: **RED**;
   - the failure was useful and localized: full text exposed real bounded-base overflow rather than hiding it.
2. `d4e42818223100d96de32e0fe4a278dc0356153e` — preserve full long identity with readable two-line compression and route overlong base Combat preview detail through an explicit `[continúa en COMBATE / ACCIONES]` cue while the complete record remains in the native Extended table.
   - Scaffold #4521 / `37557543660`: backend / hosted-database / kotlin **SUCCESS**;
   - proof artifact `11455588060`;
   - digest `sha256:913c60404bdfb52f0eac5343f593d22388992f35178b73cda9a52228b1d23ceb`.
3. `59b039bdccc8d33a79252536c1d5d7bd10470e0e` — focused proof hardening only; no renderer change.
   - verifies the base Combat cue and the complete referenced Extended record;
   - Scaffold #4522 / `37558318363`: backend / hosted-database / kotlin **SUCCESS**;
   - proof artifact `11455609450`;
   - digest `sha256:72cb4aba21442a76aa887605cdcc2a05a117c50475f159a8d5471f268529ec97`.

Actual-PDF acceptance:

- `fantasy-canonical-overflow-audit.pdf`, page 1:
  - long character name `Iria Noctis Cartógrafa Mayor De La Frontera Septentrional` is complete in two centered lines;
  - no semantic ellipsis;
  - base Combat row shows the explicit continuation cue instead of truncated detail.
- same PDF, page 5:
  - `ARMAS Y ACCIONES — CONTINUACIÓN` retains native table grammar;
  - `Ataque base con referencia explícita` and `DetalleBaseCombatContinuacionQA` are complete in the same logical record;
  - no clipping or semantic ellipsis.
- text scan of the current four key outputs found no literal `...` or `…` in:
  - `fantasy-canonical-overflow-audit.pdf`;
  - `custom-v1-whole-draft.pdf`;
  - `custom-v2-per-attribute-whole-draft.pdf`;
  - `custom-v2-per-ability-whole-draft.pdf`.

Association / silent-drop evidence remains semantic, not whole-document-only:

- shared record refs preserve module ownership for Traits, Combat, ordinary/special Equipment, Notes and Spells;
- Custom-v1 Narrative gates keep narrative payload out of Traits and retain late personality/story tokens in native narrative modules;
- Custom-v2 Narrative owns a dedicated BACKGROUND_STORY module;
- Custom-v2 Combat gates require complete semantic/table content in the Combat page;
- ordinary vs special Equipment gates require the correct native module;
- Fantasy long-tail gates retain late Narrative/Trait/Combat content.

Acceptance result:

- **M50800-26: demonstrated** — semantic ellipsis is not used; fit/compress/wrap/explicit continuation are the allowed outcomes.
- **M50800-29: demonstrated** — preservation is checked together with semantic module/record association.
- front CI count: **3** (#4520 RED -> #4521 GREEN -> #4522 GREEN focused proof);
- material OPEN decreased to zero; no oscillation or test relaxation;
- `LOOP_SUSPECTED = NO`.

## General bidirectional continuity closure

The bounded continuity front is now **CLOSED / VALIDATED**.

Functional renderer:

`516fa0bfac06e9efb70a8dbe4f4d4b3f4d62c1f7`

Automated evidence:

- full Scaffold #4523 / `37560523288`: backend / hosted-database / kotlin SUCCESS;
- focused `PC Sheet fast gate` run `37566302657`: SUCCESS;
- focused artifact `11457979404`, digest `sha256:36dcc8222eabb275747344cf26909c3ea4dc8da65afcc1c704b0c97dd1cd7b5f`.

Direct actual-PDF visual inspection completed on:

1. `fantasy-canonical-overflow-audit.pdf`
   - page 1: base Combat forward cue is visible and readable;
   - page 2: HISTORIA / PERSONALIDAD / IDEALES / VÍNCULOS / DEFECTOS forward markers are visible;
   - pages 4-5: native `HISTORIA Y PERSONALIDAD` continuation modules show matching `proviene de sección normal ...` markers;
   - page 7: Extended Combat shows `proviene de sección normal COMBATE / ACCIONES` inside the native table row;
   - no clipping or overlap was observed in the continuity markers.

2. `custom-v1-narrative-native-module-association.pdf`
   - page 3: base PERSONALIDAD and HISTORIA forward markers are visible;
   - pages 5-7: native PERSONALIDAD / HISTORIA modules show reverse markers and further Extended forward markers where the same record continues;
   - late payload remains visible and associated with the narrative modules;
   - no clipping or overlap was observed in the continuity markers.

Continuity therefore closes without another renderer commit.

The same visual review reconfirmed the separate pending defect class M50800-03: stale source/footer residue is visibly retained on copied/overlaid pages, including examples such as `EXTENSIÓN: RASGOS`, `EXTENSIÓN: INVENTARIO` and `EXTENSIÓN: NOTAS`. This is not hidden as fixed and does not reopen continuity.

## M50800-03 source-geometry / stale-underlay closure

M50800-03 is now **CLOSED / VALIDATED** at functional head `1566bd3c04bd64ec136fbf9c5df510ac60641a66`.

Criterion -> observed failure -> root cause -> change -> result:

- criterion: unrelated source/template objects must not remain searchable/copyable/visible in the wrong semantic context, while preserving layered/native construction;
- observed failure: copied Custom-v1 source fragments were visually clipped but still carried the full source page text layer, and fixed page-index cue routing could place `EXTENSIÓN: NOTAS` onto an Extended page when the Notes base page was omitted;
- root cause: cropped `PDForm` reuse retained non-visible source objects, while cue targeting assumed fixed base-page positions instead of present page roles;
- change: Custom-v1 PERSONALIDAD/HISTORIA source fragments are isolated raster fragments at 288 dpi rather than whole-page forms, and continuation cues resolve from `PcSheetBasePageRole` values actually present in `plan.basePages`; the complete page is not flattened and STRUCTURE / CLEANUP / LABELS / VALUES / MARKERS architecture remains intact;
- result: fast run `37568418440` SUCCESS; artifact `11459124665` / `sha256:1c9675a4afc5d504f131f15849d81783ba207bd9c304780c30b3fa6f14527d2c`; Desktop/Android renderer-sync and delivery guards PASS.

Actual artifact inspection:

1. `custom-v1-narrative-native-module-association.pdf`
   - Extended PERSONALIDAD/HISTORIA pages do not contain searchable `Ideales`, `Vínculos`, `Defectos`, `Notas`, `Otros Rasgos y Atributos`, `EXTENSIÓN: RASGOS`, `EXTENSIÓN: INVENTARIO` or `EXTENSIÓN: NOTAS`;
   - `EXTENSIÓN: NOTAS` is absent when the Custom-v1 Notes base page is omitted;
   - rendered pages preserve the native narrative-fragment appearance and geometry, with generated text/continuation markers unobstructed.
2. `fantasy-canonical-overflow-audit.pdf`
   - the previously validated Fantasy continuity/table surfaces remain visually clean; no new source-underlay regression was observed.

The visible `Trasfondo` artwork on the isolated Custom-v1 narrative fragment remains part of the narrative/source-native visual context; it is raster content rather than an unrelated searchable source text object. No whole-page flattening was introduced.

Anti-loop result for M50800-03: one corrective cycle after read-only diagnosis; OPEN `1 -> 0`; no oscillation; `LOOP_SUSPECTED = NO`.

## Exact next authorized action

Begin the **exact Mara four-family proof gate** from the current source head:

1. generate the exact integrated Mara fixture in Fantasy, Custom v1, Custom v2 · Atributo and Custom v2 · Habilidad from one source head;
2. retain each real PDF as a proof artifact;
3. inspect text layer and rendered pages, not only generation/test success;
4. confirm Android/Desktop renderer parity remains green;
5. run the M50800-01…32 ledger against those exact outputs as `FIXED` / `OPEN` / `CHANGED-NEW`;
6. if any blocking defect appears, repair only that observed defect through the fast gate;
7. if all hard gates pass, then and only then create a unique new versionName/versionCode candidate and run aggregate/full validation.

Do not begin Current Snapshot finalization, Media/Handouts or owner handoff before the exact four-family gate and acceptance matrix pass.

## Delivery objective / autonomous burn-down

The owner has explicitly reaffirmed the desired outcome: Phase 3 should **converge to a new owner-facing QA candidate version**, not stop indefinitely at intermediate repair packages.

Operational rule from this checkpoint forward:

- close one bounded front at a time;
- after a front is green **and its required real proof is inspected**, update operative memory and continue to the next authorized material front without asking routine permission;
- stop only for the anti-loop guardrail, a genuinely new owner decision, a manual/provider/security/cost boundary, or the exact owner visual-QA handoff;
- do not create the new candidate prematurely: the exact four-family Mara gate, M50800-01…32 exact-candidate review, Android/Desktop parity and full provenance chain remain mandatory;
- once those gates pass, issue a **new versionName/versionCode/build identity** and never reuse `0.5.0-preqa.8 / 50800`.

Canonical interruption/continuation prompt:

`docs/checkpoints/2026-10-06_MARA_PHASE3_CONTINUATION_RECOVERY_PROMPT.md`

That prompt is part of the active recovery contract. After timeout, connection interruption, polling loss or server error, verify physical GitHub state before replaying any operation.

## Still pending before owner candidate

At minimum:

- exact Mara generation in Fantasy, Custom v1, Custom v2 Atributo and Custom v2 Habilidad from one current source head, with retained real-PDF artifacts;
- direct text-layer + rendered-page inspection of those four PDFs;
- exact-candidate M50800-01…32 FIXED / OPEN / CHANGED-NEW inspection;
- unique versionName/versionCode only after the matrix passes;
- aggregate/full validation plus complete commit/run/artifact/APK/PDF hash chain for the promoted candidate.

Current Snapshot and Media/Handouts remain blocked. Phase 3 is not closed.

## Interruption safety

After timeout/poll/connection failure, do not replay an operation merely because the response was lost.

First verify:

- physical branch HEAD;
- recent commits;
- relevant check-runs/workflow state;
- PR state;
- artifact existence.

GitHub state is authoritative. If the intended write already exists, continue from it. If state is ambiguous, remain read-only until resolved. Never reset automatically to an older green commit.

Maintain the canonical recovery/continuation prompt throughout Phase 3 at `docs/checkpoints/2026-10-06_MARA_PHASE3_CONTINUATION_RECOVERY_PROMPT.md`. At Phase-3 closure, replace or advance it with the next-phase recovery prompt without losing the verified source/run/artifact/candidate chain.


## 2026-10-07 — Exact Mara gate reconciliation after current repairs

**Latest validated functional head:** `6886a052da394b84d549f9769161f772f2869947`

Latest exact four-family proof:
- Fast Gate run: `37673665059` — **SUCCESS**;
- artifact: `11505896442`;
- digest: `sha256:25e8dc5080065a9309e013e58d0df4689b7224a8b4df19de5c59711ad5bb14f5`;
- exact PDFs:
  - Fantasy: 40 pages;
  - Custom v1: 31 pages;
  - Custom v2 · Atributo: 20 pages;
  - Custom v2 · Habilidad: 20 pages.

Page counts are diagnostic only.

Recent exact-output corrections closed and visually inspected:
- writable Resource trackers preserve paper-editable marks plus compact runtime snapshot in all four families;
- Custom-v1 sparse HISTORIA continuation now composes with a native Traits module when geometry permits;
- Custom-v2 Atributo keeps the extra native HABILIDADES ADICIONALES row on the same statistics page without exceeding six actual attribute rows;
- Custom-v2 Atributo/Habilidad compose the right-side native Narrative module with a compatible left native Traits column;
- Fantasy keeps Mara's four custom attributes together using two rows of existing native panels, without resizing;
- Fantasy custom-attribute headings now reuse shared integrated identity semantics: `FORtuna / CORdura / ETEr / RENombre`.

Exact proof audit also reconfirmed:
- no semantic `...` / `…` in the four Mara PDFs;
- required late Trait / Resource / Inventory / Note identities survive;
- writable tracker snapshots `____(1)/3` and `____(2)/4` survive;
- Custom-v1 long skill `Lectura de presagios` survives using uniform wrapped-label scale;
- Custom-v2 Traits are ordered from stored `sortOrder`, with no historical `featurePriority` promotion;
- sparse final Resource/Trait pages are not automatically defects when the preceding native module is full and no family-approved compatible layout exists.

### Current M50800 ledger

- **M50800-01…26:** `FIXED` for the current exact Mara proof / current validated implementation.
- **M50800-27:** `OPEN / PARTIAL ARCHITECTURE`.
  - The shared `PcSheetExtendedPageComposer` is constraint-aware and already drives several native compositions.
  - Family renderers still invoke composition in sequential role groups (for example Narrative/Traits, then Combat, then Resources, then Inventory) rather than exposing every active Extended stream to one family-global planning pass.
  - This does not justify page-count-driven rewrites; the remaining task is architectural global coordination while preserving every already-validated native layout.
- **M50800-28…30:** `FIXED`.
- **M50800-31:** `PENDING HARD GATE`; candidate identity/provenance must not be created until M50800-27 closes.
- **M50800-32:** `PASS PRE-CANDIDATE / PENDING CANDIDATE REPEAT`; all four exact Mara families currently generate and were inspected, but the same gate must be repeated from the eventual uniquely-versioned candidate.

No blocking `CHANGED-NEW` defect was observed in the latest exact PDFs.

### Exact next authorized front

**M50800-27 — FAMILY-GLOBAL CONSTRAINT-AWARE EXTENDED PLANNING**

Implement the smallest architecture change that makes each family reason about all active Extended semantic streams before page emission, while:
- retaining the existing shared compositor;
- retaining already-approved family-native layouts and fixed geometry;
- preserving Notes as full-page-exclusive;
- preserving Combat/full-width and other incompatible modules rather than forcing artificial sharing;
- combining modules only where an explicit family-approved layout exists;
- allowing exhausted streams to disappear;
- avoiding page-count optimization as an objective;
- keeping Desktop/Android parity.

Do not reopen closed visual criteria merely to make the architecture look more generic.

After M50800-27:
1. regenerate exact four-family Mara proofs;
2. reconcile M50800-01…32 once more;
3. if no blocking OPEN/CHANGED-NEW remains, create a **new unique** versionName/versionCode candidate;
4. run aggregate/full validation and preserve commit/run/artifact/APK/PDF hash provenance;
5. only then hand off to owner visual QA.

`LOOP_SUSPECTED = NO`.


## 2026-10-07 — M50800-27 family-global coordination closure

**Status:** CLOSED / VALIDATED / LOOP_SUSPECTED=NO

M50800-27 now has a family-global semantic coordination layer before Extended page emission while retaining the already-validated native renderers and local layout compositor.

Shared architecture:
- `PcSheetExtendedGlobalCoordinator` requires every active semantic module to belong to exactly one family-approved compatibility front before page emission;
- full-page-exclusive modules remain isolated by `PcSheetNativeModuleContracts`;
- physical geometry is still owned by family-native layouts and `PcSheetExtendedPageComposer`;
- no page-count target or arbitrary resizing was introduced.

Validated subfronts:

1. **Custom-v2**
   - functional commit: `0528e88cca824581d1cd02c347f5746398124203`;
   - Fast Gate: `37675327187` — SUCCESS;
   - artifact: `11506322715`;
   - digest: `sha256:8ab4a8004e99df71fa74c9cd99e79ee1b559a64ea2b5867d84aab922dfa734e8`;
   - exact Atributo / Habilidad outputs retained the same page counts and identical extracted text as `6886a052...`;
   - representative Narrative+Traits, Resources and Inventory pages were pixel-compared against `6886a052...`: **0 differing pixels**.

2. **Custom-v1**
   - functional commit: `c87df71e8d1c6591460566057ccfad9ad1341e18`;
   - Fast Gate: `37675920818` — SUCCESS;
   - artifact: `11506913326`;
   - digest: `sha256:aafcdea4454343f94cf7521b69a1a9ef48cedd8f38662d912d900208b5c3335e`;
   - exact output retained 31 pages and identical extracted text vs `6886a052...`;
   - representative Narrative/Traits, Resources and Inventory pages were pixel-compared: **0 differing pixels**.

3. **Fantasy**
   - functional commit: `80f29afb0faf61cfe186d7e7bcff517796fcab12`;
   - Fast Gate: `37676792397` — SUCCESS;
   - artifact: `11506284490`;
   - digest: `sha256:87ba6b294d95501770384fb968dab17dd121d0d8ae39bc29600e4eff0b89dde3`;
   - exact Mara remained 40 pages with identical complete extracted text vs `6886a052...`;
   - representative Statistics, Narrative, Traits, Combat, Resources, Inventory and Notes pages were pixel-compared: **0 differing pixels**.

Architectural audit at `80f29afb...` confirms all three production families invoke `PcSheetExtendedGlobalCoordinator.plan(...)` before emitting Extended semantic pages. Fantasy legacy `REFERENCIAS` remains outside the Phase-3 semantic planner because it is not a `PcSheetSemanticModule`; no new semantic module was invented.

### Ledger after M50800-27

- **M50800-01…30:** FIXED / demonstrated on the current pre-candidate line.
- **M50800-31:** PENDING exact-candidate acceptance.
- **M50800-32:** PASS on the pre-candidate line, but MUST be repeated from the exact uniquely-versioned candidate.
- blocking CHANGED-NEW: none observed.

### Exact next authorized action — candidate gate

1. advance monotonically from `0.5.0-preqa.8 / 50800` to **`0.5.0-preqa.9 / 50900`**;
2. retain that exact candidate commit identity;
3. generate Mara in Fantasy, Custom v1, Custom v2 Atributo and Custom v2 Habilidad from that exact commit;
4. inspect actual candidate PDFs, not only tests;
5. mark M50800-01…32 FIXED / OPEN / CHANGED-NEW against the exact candidate;
6. any blocking OPEN/NEW regression blocks handoff;
7. run authoritative aggregate/full `Scaffold checks` for the exact candidate and preserve commit/run/artifact/APK/PDF provenance;
8. only then hand off `preqa.9 / 50900` for owner visual QA.

Current Snapshot and Media/Handouts remain blocked until owner disposition.


## 2026-10-07 — preqa.9 internal rejection and Inventory reclaim repair

**Status:** PREQA.9 REJECTED INTERNALLY / REPAIR VALIDATED / NEXT CANDIDATE REQUIRED

The first uniquely-versioned Phase-3 candidate was:
- version: `0.5.0-preqa.9 / 50900`;
- exact commit: `6d77ac5cbeea95b2d4c0f7e81adfaf2d322921b3`;
- Fast Gate: `37677309258` — SUCCESS;
- proof artifact: `11506934322`;
- artifact digest: `sha256:7dea2b6b04415d1ea850de6765bc783d77177ea1c2c6f671ea9423d5075f3231`.

Actual all-page PDF inspection rejected that candidate before owner handoff:
- Custom-v2 special-only Inventory pages retained a large exhausted ordinary-Equipment area;
- Fantasy special-only Inventory pages likewise failed to reclaim all compatible native module area;
- therefore M50800-27 remained OPEN despite green CI;
- `preqa.9 / 50900` MUST NOT be reused.

Repair line:
- product repair: `cfb446aa4e8d79595d58a2a1359d2f2c5054255a` — repeat/reposition complete fixed Special Equipment modules instead of stretching them;
- regression-scope correction: `b529c613d82e129b39160898957d925e820ffed3`;
- semantic-token regression correction: `e2e2847318d238196e7ee42112b79e571b9bbe1a`;
- final Fast Gate: `37679563332` — SUCCESS;
- proof artifact: `11507633293`;
- artifact digest: `sha256:7aa1c8e2a52c652432ccc20d923b417c7ac9f3cace9deb800e16c675d692c753`.

Actual repair-proof inspection:
- Fantasy: **34 pages**, Inventory on pages 25–28; compatible fixed special modules reclaim native sibling/page area without overlap or deformation;
- Custom-v2 Atributo: **19 pages**, Inventory on pages 16–17; the special-only page carries two complete fixed `EQUIPO ESPECIAL` modules;
- Custom-v2 Habilidad: **19 pages**, same validated reclaim;
- Custom-v1 remains **31 pages** and unaffected by this repair;
- page counts are diagnostic only, never acceptance ceilings.

Semantic/content audit of the exact repair proof:
- all 26 Traits present;
- all 10 Resources present;
- all 8 class Options present;
- all 34 Inventory identities present;
- all 9 Notes present;
- all 7 custom Markers present;
- zero semantic `...` occurrences;
- stream identities remain associated with their intended family modules.

M50800-27 is now re-closed on the repaired pre-candidate line, based on actual rendered output rather than source/CI alone.

### Next exact candidate

Advance monotonically to **`0.5.0-preqa.10 / 51000`**.
Do not reuse `preqa.9 / 50900`.

For that exact candidate:
1. run the focused four-family Mara gate;
2. inspect the actual four PDFs again;
3. reconcile M50800-01…32 as FIXED / OPEN / CHANGED-NEW;
4. if clean, run authoritative full `Scaffold checks` through the normal PR trigger;
5. preserve exact candidate SHA, Fast Gate run/artifact, full Scaffold run, APK artifact/hash and proof hashes;
6. only then prepare owner visual-QA handoff.


## 2026-10-07 — owner warning audit: preqa.10 rejected / adaptive pagination core reopened

**Status:** CANDIDATE REJECTED / PROCESS GATE CORRECTED / ADAPTIVE PAGINATION CORE OPEN

Rejected candidate:
- version: `0.5.0-preqa.10 / 51000`;
- exact SHA: `208c570be5d7ac07a613c28c580347b0d91fb613`;
- focused Fast Gate `37685159940`: SUCCESS;
- focused proof artifact `11510478752`;
- full Scaffold `37685874144`: **FAILURE**;
- validation PR #117: **CLOSED WITHOUT MERGE**.

Owner-warning audit result:
- page count itself remains diagnostic only;
- however, the previous internal closure of M50800-27 relied too heavily on source architecture, semantic preservation and selected-page inspection;
- the presence of `PcSheetExtendedGlobalCoordinator` proved global semantic ownership, but did **not** prove globally efficient physical pagination;
- actual candidate inspection still exposes residual/fixed-scaffold patterns that cannot be accepted without demonstrating why each new page is physically necessary;
- therefore a green focused gate or the existence of a global coordinator is insufficient evidence for adaptive-pagination closure.

Reopened criteria:
- **M50800-10** Traits packing — REOPENED pending physical-page necessity proof;
- **M50800-14** Resources/Options packing — REOPENED pending physical-page necessity proof;
- **M50800-24** Notes packing — REOPENED pending physical-page necessity proof;
- **M50800-27** adaptive continuation architecture — REOPENED / PRIMARY;
- **M50800-31** exact candidate acceptance — REOPENED because preqa.10 is rejected;
- M50800-12/13 Combat remain functionally corrected for table grammar, but residual-space sharing stays under M50800-27 until the physical scheduler proves/repairs it.

Full Scaffold `37685874144` additionally failed five Kotlin tests:
- `aldrenCustomFamiliesDoNotReplayAmmunitionMetadataAsEquipmentOverflow`;
- `promotesOwnerApprovedCustomV1ResourcesAndOptionsFromRealPlanData`;
- `generatesWholeCustomFamilyFirstDraftsForOwnerReview`;
- `composesCustomV2TraitsAsNativeColumnsWithoutFixedScaffold`;
- `promotesOwnerApprovedCustomV2TraitsAndResourcesFromRealPlanData`.

These failures reinforce that no owner handoff is authorized.

### Corrected acceptance model

M50800-10/14/20/24/27 may no longer close from architecture/source inspection or page-count reduction alone.

Required evidence for each adaptive front:
1. **physical pagination trace** from the exact renderer path;
2. **invariant tests** proving no new page is emitted while an approved compatible placement remains available;
3. **actual PDF inspection** confirming readability, native grammar and no semantic loss.

A visually sparse last page is not automatically defective. It is valid only if the trace proves the previous page had no compatible legal placement for the next atomic record/module.

### Active front — Adaptive Pagination Core / physical scheduler

First deliverable:
- durable per-page trace emitted by the exact Mara proof path;
- trace records: family/front/page ordinal, demand before, legal candidate layouts, chosen layout/slots, units consumed, demand after, and next-page rationale;
- exact Mara Fast Gate must retain this trace as an artifact alongside the PDFs.

Second deliverable:
- use that trace to classify every suspicious page as either:
  - `JUSTIFIED_RESIDUAL`, or
  - `RECLAIM_OPPORTUNITY`;
- only `RECLAIM_OPPORTUNITY` pages authorize renderer/layout changes.

Third deliverable:
- repair the smallest family-approved physical layout/scheduler gaps revealed by the trace;
- never add page-number- or Mara-specific branches.

Do not create another versionName/versionCode candidate until this front is closed by trace + invariants + real PDFs.

## 2026-10-07 — Bounded physical scheduler recovery: Custom-v2 native Traits column reclaim

**Repository branch:** `repair/mara-phase3-semantic-flow-compositor`. This is a later recovery record and supersedes earlier provisional M50800-27 closure claims. It is not a candidate release.

### Physical recovery and causal chain

1. **Starting source truth:** `2e49c39028430d127a3821cbbbfc565ff9f355ba`; Fast Gate `37704485453` SUCCESS; exact Mara artifact `11518783088`, ZIP SHA-256 `3c0e87b9bbd9f245b897c8652f80508cfe6a42b7fa6ba65a8bfebfaec35b6c88`. `3b19cface257f5bbd4ffa03d688da31c5c675bd2` was already published/green; four subsequent commits had landed. PR #117 verified CLOSED WITHOUT MERGE.
2. **OPEN physical trace gap before:** 77 pagination decisions, only 26 with `physical`, 51 without. The current family-global coordinator proves semantic ownership, **not** legal page necessity.
3. **Observed `RECLAIM_OPPORTUNITY`:** real Mara Custom-v2 Attribute and Ability page 9 has an exhausted primary (left Competencias) native column retaining enough legal rows to absorb the same-page secondary (right Rasgos) *whole prepacked native column*, plus one group heading. The secondary stream continues on later pages. The original independent packing prevented this placement. No fixture/page-index branch authorized.
4. **Invariant/proof commit:** `7cffd79957cd44e5f4449cf57438b6ea4e8a8186` — record measured native-column row usage, probe full secondary-column atomic relocation only when primary is exhausted, heading fits, and no continuity marker is relocated. New exact-Mara invariant fails if `hasPhysicallyAvoidableNextPage()`. Fast Gate `37706661199` **FAILURE, expected and causal**, in both Custom-v2 families at `v2-global-narrative-traits#4`: `exhausted-primary-column-can-reclaim-complete-secondary-column`. This was not a compilation problem or arbitrary rerun.
5. **Bounded generic fix:** `a8efb04ac137e9360afaad29bf845e28b79de81c` — reclaim only the last exhausted primary native column by appending the complete secondary native column with a one-row category heading; preserve secondary stream order; skip continuity-bearing columns and placements that do not fit. Desktop and generated Android mirrors updated together; no Mara/page-specific conditions.
6. **New Fast Gate:** `37706968931` **SUCCESS**; artifact `11520520529`, ZIP SHA-256 `833e9b59021fa8caffd07ce6278e99adf9c6fb8c66975043735352c95a09c623`. Exact proof zip independently downloaded and verified.
7. **Exact real four-PDF inspection:** Fantasy 32 pages and Custom-v1 30 pages unchanged in extracted text; both Custom-v2 variants 18 pages and their 23 Extended trait identifiers preserve the original extraction order. Across **all four PDFs**, the extracted *multiset of nonempty text lines is identical* before/after (zero lines added/removed), so no evidence of text loss. Custom-v2 rendered affected pages 9–11 were directly inspected; Attribute/Ability raster outputs for those pages match byte-for-byte in pixel samples. The previously empty primary column now carries native Rasgos rows under an explicit category heading, with no collision observed.
8. **After-fix trace:** 77 decisions, 36 populated `physical`, 41 still `physical:null`. The observed full-column transfer is no longer eligible at the tested decision; the exact invariant passes. Trace on remaining Custom-v2 columns is **not** complete atomic-block evidence for every later advance.

### Acceptance ledger — do not over-close

- **Bounded observed whole-column reclaim:** `FIXED` (invariant RED -> GREEN, actual PDF inspected, exact artifact recorded).
- **M50800-10 Traits packing:** **OPEN** globally. Later sparse Details/Notes-only Traits page in Custom-v2 may be legitimate but atomic-block/other-layout eligibility has not been fully demonstrated. Do not judge by page count.
- **M50800-14 Resources/Options packing:** **OPEN**, all 15 observed resource decisions still lacked physical trace at prior source; candidate layouts and per-stream atomic capacities need proof. Fantasy uses only the split layout when both streams remain active; dedicated native alternatives are not yet audited for legality while both are active.
- **M50800-24 Notes packing:** **OPEN** for exact candidate acceptance; earlier Notes row advances have measured justified remainders, but whole global/four-family acceptance remains pending.
- **M50800-27 Adaptive Pagination Core:** **OPEN / PRIMARY**. Global semantic ownership is not an accepted physical scheduler.
- **M50800-31 exact candidate acceptance:** **BLOCKED**; do not create `0.5.0-preqa.11` / new code until A/B/C closure.
- **M50800-12/13:** functional Combat fixes retained; residual cross-front behavior still lives under 27.

**Exact next action:** finish physical/atomic traces and no-avoidable-advance invariants for Custom-v2 residual Details/Traits, Fantasy Traits, Custom-v1 Narrative/Traits, and all Resources/Options. Test the chosen native slots **and approved eligible alternatives** before any renderer fix. For every observed reclaim: root cause -> generic invariant -> narrow fix -> new exact artifact/trace -> rendered PDF. Retain no reset, no force push, no replay, no new candidate, no routine owner confirmation.

## 2026-10-07 — Adaptive pagination: complete native trace coverage, global scheduler still OPEN

**Authoritative verified functional source HEAD:** `e49f298c9bddacba2678778e0435b995a4cd3d5e`; **Fast Gate:** `37714224487` SUCCESS; **exact four-family proof artifact:** `11523426740`; **artifact SHA-256:** `6004c6ff733d0d95fef9d8803cf3bd75b5e700ae8be23cd0a7248b4259d8a700`. Latest proof: Fantasy 31 pages, Custom-v1 30 pages, Custom-v2 Attribute 18 pages, Custom-v2 Ability 18 pages. All four retained identical text to preceding validated artifact after the instrumentation-only commits. Exact trace has **76 decisions; 76 non-null `physical`; 0 missing**. Trace coverage must **not** be interpreted as global adaptive-pagination acceptance.

### Published causal evidence, not a presumed commit history

1. **Resources/Options occupancy:** `c53b57ca44082720fe0a38857c88d41c48796dae` tested physical coverage; run `37710144050` RED specifically for 15 unmeasured Resources/Options decisions. `080ec5af54631f40cb09c2307a77b98ebc285319` measured chosen native slots across Fantasy, Custom-v1 and both Custom-v2 variants; run `37710475333` GREEN, artifact `11521074761`. No same-slot atomic unit was left legally placeable in the chosen native layout.
2. **Resources/Options eligible alternatives:** `4a4e590f2a5804f2bd34992e4830b7854adcdc70` introduced the eligibility invariant; first run `37710884727` had a test import error; `abe866956729591282aeeb6fe1ebca07a388831b` fixed the import and run `37711039909` RED as intended for omitted split/full candidates in nine decisions. `8d9c01cc36d2afead1fc955495944c7f52da9b74` exposes source-native split/full alternatives; run `37711280696` GREEN, artifact `11521648726`. PDFs unchanged.
3. **Area scoring + record atomicity:** `a7a1357642a12f60544ff9d55f965624da87c889` added measured area scores and failed the exact invariant at Fantasy Resources/Options decisions #11–14; run `37711574056` RED as expected. `a9b77f696f11ada8a73ecc9a73770d490390e000` changed measured native selection and carries whole logical Resource/Option record row sizes, not arbitrary row fragments; run `37712208913` GREEN, artifact `11521314764`. Fantasy now renders two Options-full and three Resources-full pages, preserving all exact Mara semantic identities; page count is diagnostic only, not acceptance.
4. **Base-origin cue regression found in actual PDF:** `4452db2257e615ba3a4ec3b80b83dcb765a74a19` added exact cue invariant, run `37712754881` RED. Native full pages had caused visual completion to miss the originating `RECURSOS` cue; `3969393b6b4fceb863a820379fe8ddd0dad9577f` fixes Desktop+Android native-page detection, run `37712901340` GREEN, artifact `11522850770` / ZIP SHA-256 `df86665ab83ee0b174ddfe538ee96b312b7425440e350e12a20888d73f6f3b90`. Actual Fantasy page 1 visually restored `EXTENSIÓN: ESTADÍSTICAS / RASGOS / RECURSOS`; all other PDF page text remained unchanged.
5. **Fantasy Traits:** `0da5d068e12550f253d79d0ed2b50a4e299d58f6` added per-frame source-native rows and whole next same-category Trait slice eligibility; run `37713454784` failed only a test cross-module Kotlin smart cast. `1f066cfcb5236ee7169ea8d811c57997b95d30e9` corrected the test; run `37713647438` GREEN, artifact `11522751788` / ZIP SHA-256 `6364369dc752084f6724a1f796895604e5e3482be8477b4f3b0936fb5434b51e`. Nine measured page decisions: all eight nonterminal next slices exceeded their compatible same-category remainder; terminal stream exhausted. No PDF text changed.
6. **Custom-v2 mixed Narrative/Traits:** `a96cfd3c51ed30a3619deb4b069acf5cd571eeb4` measured the two approved native modules and retained separate Trait overflow ownership; run `37713867873` GREEN, artifact `11522653706` / ZIP SHA-256 `3a13799ef7c277833b62b37806ada663132e2bf8b3413867dfcfecbe6b1ee8a4`. Both variants: 42 of 56 mixed native rows used. Actual PDF page visually inspected, no semantic regression. Trace at this point 61/76 physically populated.
7. **Custom-v1 original Traits blocks / atomic padding:** `e49f298c9bddacba2678778e0435b995a4cd3d5e` persists original block ordinals across packed rows and distinguishes printed content, protective blank padding, legal trailing remainder and next complete original block; run `37714224487` GREEN, artifact `11523426740`. All fifteen Custom-v1 Narrative/Traits page decisions have exact native physical trace. Example: oversized 14–15-row original blocks cannot enter a trailing 9–10-row remainder while honoring native column/module grammar. The last decision uses 2 rows with no remaining Traits stream. The four PDF families retained all text unchanged. **Physical trace coverage becomes 76/76.**

### New observed global cross-front physical opportunity — NOT YET A FIX

The exact Custom-v1 PDF and trace expose:
- `v1-global-narrative-traits#16`: final separate Traits page consumes **2 of 24 native rows**, with no remaining Traits demand.
- `v1-global-resources-options#20`: final separate Resources-full page consumes **9 of 30 native rows**, with no remaining Resources demand.
- The original approved **Resources split upper module** has 10 native 20-pt rows from the first rule y=128.5 through y=308.5, with heading y=66 and header y=96.
- The original approved **Traits lower native module** has source crop x=121.748..490.252, y=390..668 (368.504 x 278 pt).
- These occupied native regions do not overlap. A diagnostic one-page **vector PDF** was assembled read-only from exact source PDF pages 26 (upper Resources, clipped to y=330) and 22 (native Traits module remapped to y=390..668). It preserves the visible source-derived typography/ruled geometry and the labels `Reserva 9`, `Reserva 10` and the trailing whole Traits block. Diagnostic PDF SHA-256 `85a815646355466900b1b305554f0c10b43304e3d4dc098cb35885f03e16075a`. This was **NOT produced by the application renderer** and is **NOT an accepted candidate PDF**.
- **Critical missing proof:** whether the global scheduler can legally combine these modules while preserving each stream's order, explicit bidirectional continuation, overall role sequence and all four-family invariants. The existing `PcSheetExtendedGlobalCoordinator` only selects/serializes compatibility fronts; it does **not** evaluate cross-front physical mixed layouts. Its existence and 76/76 physical trace do not prove global physical page necessity.

### Acceptance / next action (strict)

- The bounded fixes above are proved individually as described, but **M50800-10, M50800-14, M50800-24, M50800-27 and M50800-31 remain OPEN** pending whole/global final acceptance, particularly M50800-27 cross-front scheduling. Prior rejected `preqa.9 / 50900`, `preqa.10 / 51000`, closed-unmerged PR #117 remain REJECTED.
- **NEXT:** derive a generic source-native mixed layout compatibility contract for Custom-v1 Resources-upper + Traits-lower; test rectangle non-overlap, native module fidelity, record atomicity and ordering/continuity **before any production scheduler change**. Only once globally legal, write a failing invariant for avoidable dedicated page, implement generic cross-front scheduling with no Mara/page-specific branch, validate exact Fast Gate plus four actual PDFs and source order. If sequencing cannot be proven acceptable, keep the criterion OPEN rather than forcing a page-count improvement.
- Do not issue `preqa.11`, do not merge, do not reset/replay. `CI_GREEN` is not `M50800-27_FIXED`.
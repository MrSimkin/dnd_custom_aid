# Mara Phase 3 — compositor health consolidation

**Local date:** 2026-10-06 (Chile)  
**Phase:** 3 — renderer repair implementation  
**Branch:** `repair/mara-phase3-semantic-flow-compositor`  
**Main base:** `c4e65955a5f58f88e0b03807b4616a3f8a9323a8`  
**Validated implementation head at capture:** `e6e07a13721d3d56364d6373472a0b5b0074acb6`  
**Current validated functional implementation HEAD:** `d4e42818223100d96de32e0fe4a278dc0356153e` — semantic-survival production correction validated at Scaffold #4521 (`37557543660`) SUCCESS  
**Current proof HEAD:** `59b039bdccc8d33a79252536c1d5d7bd10470e0e` — focused Fantasy explicit-continuation proof / Scaffold #4522 (`37558318363`) SUCCESS  
**Current status:** ACTIVE / NARRATIVE + TRAITS + COMBAT/ACTIONS + NOTES + SEMANTIC SURVIVAL VALIDATED; NEXT FRONT GENERAL BIDIRECTIONAL CONTINUITY AUDIT  
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

## Exact next authorized action

Semantic ellipsis / silent drop is closed. Do **not** reopen M50800-26/29 without new defect evidence.

The next authorized front is the bounded **general bidirectional semantic-continuity audit**, beginning read-only from the owner-confirmed general rule:

1. enumerate every current semantic flow that can split one logical record/module across non-contiguous normal/Extended sections or pages;
2. classify each as:
   - already bidirectional and visually demonstrated;
   - atomic/no split possible, therefore no marker required;
   - forward-only / backward-only / ambiguous, therefore materially OPEN;
3. inspect shared `PcSheetBidirectionalContinuation` use and current Desktop/Android renderers;
4. inspect actual #4522 PDFs for visible source/target markers in Narrative, Notes and any other split flow;
5. treat the new Fantasy base Combat cue as a continuity candidate: determine whether the Extended record also needs an explicit `proviene de...` marker under the general owner rule rather than assuming the forward cue alone is sufficient;
6. do not add markers to atomic rows that are merely repeated or wholly moved between pages;
7. only if a real bidirectional gap remains, implement the smallest coherent continuity package;
8. first functional CI for that package counts as cycle **1/4**;
9. close only with green CI and actual-PDF visual evidence.

Do not begin source-geometry/stale-underlay, final candidate/versioning, Current Snapshot finalization or Media/Handouts until this continuity front is closed or the guardrail forces a stop/rethink.

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

- general bidirectional continuity proof in actual output;
- remaining source-geometry / stale-underlay audit;
- Android/Desktop parity;
- exact Mara generation in Fantasy, Custom v1, Custom v2 Atributo and Custom v2 Habilidad from one candidate;
- exact-candidate M50800-01…32 FIXED / OPEN / CHANGED-NEW inspection;
- unique versionName/versionCode and complete commit/run/artifact/APK/PDF hash chain.

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

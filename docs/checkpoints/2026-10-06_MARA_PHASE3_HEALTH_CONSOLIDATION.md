# Mara Phase 3 — compositor health consolidation

**Local date:** 2026-10-06 (Chile)  
**Phase:** 3 — renderer repair implementation  
**Branch:** `repair/mara-phase3-semantic-flow-compositor`  
**Main base:** `c4e65955a5f58f88e0b03807b4616a3f8a9323a8`  
**Validated implementation head at capture:** `e6e07a13721d3d56364d6373472a0b5b0074acb6`  
**Last functional implementation HEAD before documentation consolidation:** `92e460f005cb1f7f3cb76e3a4d6ebfa34afb04a4` — Custom-v1 Traits package OPEN / CI RED at Scaffold #4516 (`37543504407`)  
**Current status:** ACTIVE / CUSTOM-v1 NARRATIVE VALIDATED; CUSTOM-v1 TRAITS PACKAGE OPEN  
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

Physical state verified on 2026-10-06:

- last functional implementation HEAD before documentation-only consolidation: `92e460f005cb1f7f3cb76e3a4d6ebfa34afb04a4` — `feat: compose v1 Traits from native modules`;
- parent documentation commit: `12172e330b38f26edb6dc0b99e6a0b426e935bdc` — this health consolidation;
- Narrative validation remains anchored at `e6e07a13721d3d56364d6373472a0b5b0074acb6` / Scaffold #4514 / run `37537059037` — backend, hosted-database and kotlin SUCCESS;
- Narrative proof artifact: `11447835297`, digest `sha256:059eb8a5214b32b1e7a346f748e0cadea81b552c393395a35ebd82e2dc6cd0c0`;
- direct proof inspection confirmed native `Rasgos de Personalidad` / `Historia del Personaje` association, late tokens `PersonalidadNarrativaV128` and `HistoriaNarrativaV142`, and no narrative payload in the following Traits surface;
- current Traits run: Scaffold #4516 / `37543504407` — backend SUCCESS, hosted-database SUCCESS, kotlin FAILURE, no proof artifact;
- open implementation PR: none.

Current Custom-v1 Traits failure packet:

1. `promotesOwnerApprovedCustomV1TraitsContinuationWithoutCustomStatistics` fails because source-only metadata on a trait already represented in the base sheet is emitted again as Extended trait detail. The current implementation treats `source` alone as sufficient continuation detail.
2. `keepsCustomV1NarrativeOverflowOutOfTraitsAndInNativeStoryModules` also fails. The new Traits-page selector matches extracted `Otros Rasgos y Atributos` text, which can exist in source/native text on narrative proof pages; therefore this failure is a **regression signal but not proof of a visual Narrative regression**. Do not weaken the semantic contract merely to green the test. Re-establish page/module ownership precisely and require a new proof before closing the Traits package.

Anti-loop consequence: **no LOOP_SUSPECTED trigger yet** for Custom-v1 Traits. This is cycle 1 of the bounded front. It must close or materially reduce its open criteria within the existing four-cycle guardrail; otherwise stop for structural diagnosis.

## Exact next authorized action

The read-only Custom-v1 Traits semantic-ownership preflight has already been overtaken by a physical implementation commit on the branch. Do **not** restart it and do **not** open another Phase-3 front.

After this consolidation is presented to the owner, the only authorized implementation work is the **existing Custom-v1 Traits native-module package at `92e460f0...`**:

1. preserve the native `Otros Rasgos y Atributos` geometry and Android/Desktop parity;
2. eliminate duplicate source-only detail for already represented traits without dropping meaningful trait content;
3. restore a precise semantic-association gate so Narrative remains owned by `BACKGROUND_STORY`, without relaxing the canonical contract;
4. require green CI and inspect the resulting real proof PDF/PNG before declaring M50800-08 / 09 / 10 / 27 progress closed for this package;
5. count the next CI attempt as cycle 2 of a maximum four-cycle front.

No Combat/Actions, Notes, ellipsis/silent-drop, candidate, Current Snapshot or Media/Handouts work is authorized until this current package is closed or the guardrail forces a stop/rethink.

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

- Custom-v1 Traits native/compositor reclaim and remaining semantic-ownership correction;
- remaining Combat/Actions continuation work, including Fantasy table grammar and Custom-v2 logical-row model;
- Notes full-native-page behavior and bidirectional continuity;
- remaining semantic ellipsis / silent-drop audit;
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

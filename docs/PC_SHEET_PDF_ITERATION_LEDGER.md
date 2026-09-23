# PC Sheet PDF - append-only iteration ledger

**Status:** ACTIVE / AUTHORITATIVE PROCESS RECORD  
**Established:** 2026-09-22  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 - OPEN / DRAFT / DO NOT MERGE

## Why this exists

The owner reported that already-solved PDF defects and contracts had reappeared across later runs, and that some prior decisions were no longer reliably recoverable from the normal resume documents.

This ledger is the durable defense against that failure.

**Every visual iteration must append an entry here. Existing entries are not rewritten to make later work look cleaner.**

A run is not complete merely because CI is green. Each entry must record:

- exact implementation commit;
- exact CI run(s);
- exact proof artifact, if one was produced;
- owner findings carried into the run;
- Worker findings from reading the run/log/artifact;
- issue-by-issue disposition: OPEN / FIXED IN CODE / VERIFIED BY TEST / VERIFIED VISUALLY / OWNER APPROVED;
- X/Y audit result;
- layer-contract result;
- terminology result;
- next bounded action.

## Permanent issue registry

| ID | Contract | Permanent rule |
| --- | --- | --- |
| TERM-001 | Raza terminology | User-facing PC-sheet output uses **Raza**, never **Especie**. A recurrence is a regression, not a new request. |
| PROC-001 | Iteration memory | Every render/recovery iteration is appended to this ledger with commit/run/artifact/result evidence. |
| ARCH-001 | Layer strategy | Template-derived Custom pages use semantic layers in order: `STRUCTURE -> CLEANUP -> LABELS -> VALUES -> MARKERS`. Do not flatten layers to hide defects. |
| XY-001 | Pre-print geometry | Owner-facing proof promotion is blocked until X/Y placement is audited against frozen/measured geometry. Green build alone is insufficient. |
| NAME-001 | Family naming | The current application-designed family whose legacy technical enum is `CLASSIC_DND_STYLE` is owner-facing **Fantasy Sheet**. Do **not** call it Classic, official, or official-like. Historical files may retain their old names as provenance only. |
| OFFICIAL-001 | Official-like family | A future D&D-official-like family, if built, is a separate visual product gate and must actually resemble the official D&D sheet grammar. Fantasy Sheet does not satisfy or claim this. |
| RUN-001 | Read every run | After every CI run, read the actual result/logs. Record what failed/passed and how each issue was addressed before another owner proof is promoted. |
| GOLDEN-001 | Frozen evidence | Owner-approved proofs/renderers are the visual goldens. A later production render does not replace a golden merely by passing CI. |

## Iteration history

### VR-0 - rejected App Modified / continuation candidate

- Candidate: `bf7d4d8f5324af7aabb5bf4d3d0038936af2b53d`
- Artifact: `10713481125`
- Result: **OWNER REJECTED**
- Durable lesson: production-to-production comparison was circular; visual drift could preserve itself.
- Status: historical only / never use as visual authority.

### VR-1 - first triple-authority recovery

- Final implementation: `6ea493c9ee53cf727464a985bc6ef0b558a73e99`
- Push Scaffold: #3239 / `35786839063` - SUCCESS
- PR Scaffold: #3240 / `35786844300` - SUCCESS
- Artifact: `10720833172`
- Result: **OWNER REJECTED**
- Regressions reported:
  - Fantasy-sheet ruled text not aligned with rules;
  - unnecessary vertical gaps;
  - lost reference/writing lines;
  - `Especie` recurrence despite the existing Raza contract;
  - Custom-v1 special checks off-center;
  - Custom-v2 blank vertical bands;
  - Custom-v2 wrong Extended title/subtitle typography;
  - Custom-v2 logo artifacts.
- Status: do not freeze / do not resend.

### VR-2 - rule-coupled recovery WIP

- Implementation: `4e7ea99997de46ef8a9a8d1066b08fa362854cdd`
- Push Scaffold: #3245 / `35791076544` - FAILED
- PR Scaffold: #3246 / `35791080855` - FAILED
- Worker log reading:
  - 74 Desktop tests executed; 7 failed;
  - all seven failures originated from the stricter Fantasy-sheet ruled-area overflow diagnostic;
  - the diagnostic exposed text that still arrived with embedded line breaks / too much base-page content;
  - no owner-facing proof artifact was promoted.
- Resolution direction:
  - keep physical-rule coupling;
  - normalize line input;
  - bound base-page content to physical capacity;
  - route complete text into family-native continuation pages instead of compressing or removing rules.

### VR-2A - restore durable authority

- Commit: `afbc1aa3ef738c3ead9e54fadb17e4e3a9970353`
- Change: restored `docs/PC_SHEET_PDF_VISUAL_CONTRACT.md` as an explicit durable rule ledger and corrected resume documents.
- Status: documentation hardening.

### VR-2B - physical rows + layered v2 header

- Commit: `d2cedea9e0f102ee6fa1fd2e1e76fdb6540369f5`
- Change: preserved physical Fantasy-sheet row cadence and removed the rejected raster-header shortcut from v2.
- Status: code repair.

### VR-2C - bound Fantasy-sheet class traits

- Commit: `25cb0454d90e20a79768ac634897af25dd770071`
- Change: class-trait base projection now respects physical ruled capacity; clipped content is routed to continuation.
- Status: code repair.

### VR-2D - source-preserving v2 logo

- Commit: `2669f6d48d8e0aa01585e502f310c17043ff1af6`
- Change: v2 logo remains vector/source-derived and bounded inside STRUCTURE rather than flattened.
- Status: code repair.

### VR-2E - five-layer regression guard

- Commit: `921989455f79f9ce55051b555faa34103c8297f5`
- Push Scaffold: #3255 / `35793266628` - FAILED
- PR Scaffold: #3256 / `35793270699` - run created from same head.
- Worker log reading for #3255:
  - backend PASS;
  - hosted-database PASS;
  - Kotlin failed in one Desktop test;
  - failure: `rendersOwnerApprovedClassicBaseFromRealPlanData` expected 3 pages but correctly received 4 after the new physical-capacity continuation logic;
  - this is a stale test expectation, not permission to remove the continuation page.
- Layer result: tests now require presence of all five semantic layers for v1/v2 continuation roles.
- Remaining process gap at this point: ordered-layer assertion + dynamic X/Y pre-print report still needed.

## Mandatory next entry

The next generated proof iteration must append a new `VR-3` entry **before owner delivery** and include:

- final head;
- push + PR run IDs and conclusions;
- proof artifact ID/digest;
- ordered-layer audit;
- `TERM-001` result;
- dynamic X/Y audit report;
- Worker visual inspection findings;
- exact issue-by-issue resolution mapping;
- owner status left PENDING until explicit review.


### VR-3A - executable pre-print gates, first attempt

- Implementation: `d0bf95d0aa7f81d9a5d99ba324c5b96dab788361`
- Push Scaffold: #3259 / `35794207437` - FAILED
- PR Scaffold: #3260 / `35794211161` - FAILED
- Backend: PASS.
- Hosted database: PASS.
- Kotlin: FAILED in 2 tests.
- Worker log reading:
  1. `XY-001` selected the first same-named v2 `RASGOS Y ATRIBUTOS` label on the base sheet (Y ~= 411) rather than the intended Extended heading (Y ~= 38). This was an audit-selector defect, not permission to move the approved Extended heading.
  2. Fantasy inventory test assumed exactly one continuation page. Correct no-loss routing now required two continuation pages for that fixture. The fixed page-count expectation was stale.
- Resolution:
  - X/Y selector now chooses the matching occurrence nearest the independently expected Y band;
  - inventory validation aggregates all continuation pages instead of constraining page count;
  - neither visual geometry nor overflow capacity was weakened.

### VR-3B - guarded recovery candidate

- Implementation: `a5cb2311a0beb8454291d8b9985cb1b13f37a3dd`
- Push Scaffold: #3261 / `35794876389` - **SUCCESS**; Kotlin build/test: `BUILD SUCCESSFUL in 6m 37s`.
- PR Scaffold: #3262 / `35794879895` - **SUCCESS**; Kotlin build/test: `BUILD SUCCESSFUL in 5m 3s`.
- Exact proof artifact: `10723153227`
- Artifact digest: `sha256:89e458a34a0f79992720c0b85116f66ce8007a3da5026248189eac30b9c4c421`
- Pre-print audit report: `pc-sheet-preprint-xy-audit.tsv`.
- X/Y audit: **15/15 PASS**.
- TERM-001: **PASS** - generated audited PDFs contain `Raza`; no user-facing `Especie`.
- ARCH-001: **PASS** - Custom semantic layer groups are required and ordered `STRUCTURE -> CLEANUP -> LABELS -> VALUES -> MARKERS`.
- Worker rendered-page preflight:
  - Fantasy page 2: Historia/Personalidad now flows on consecutive physical rules; Rasgos adicionales aligns to rules; Idiomas and Aliados/Tesoro retain visible writing/reference rules.
  - Fantasy traits continuation: text aligns to visible source-paper rules; user-facing heading is `RASGOS DE RAZA / TRASFONDO / OTROS`.
  - Fantasy inventory continuation: one native row per item; no alternating blank-row cadence; Valor/Ubicación/Notas uses consecutive ruled rows.
  - Fantasy notes: Notas de campaña and Referencias/Recordatorios align to their physical rules.
  - Custom v1: Special Equipment check marks are visually centered inside the source boxes. Independent raster measurement on the first checked white-row box placed the check-ink weighted centroid about 0.6 pt left of geometric center and about 0.14 pt vertically from center, inside the +/-1.5 pt gate.
  - Custom v2 both variants: Extended title/subtitle appearance matches the frozen Run-7 source-font treatment again; Clase/Dotes and Raza/Trasfondo/Otros entries are packed on consecutive source rows rather than fixed 102-pt blocks; logo appears clean in rendered preflight.
  - Current v2 header-vs-frozen-Run7 raster comparison shows only a very small bounded header-region difference; no visible opaque/crop artifact was found in the inspected render.
- Family naming:
  - current application-designed family is owner-facing **Fantasy Sheet**;
  - `CLASSIC_DND_STYLE` remains legacy internal compatibility only;
  - no claim is made that Fantasy Sheet resembles the official D&D sheet.
- Status: **IMPLEMENTATION + AUTOMATED PRE-PRINT GATES PASS / WORKER VISUAL PREFLIGHT PASS / OWNER VISUAL QA PENDING**.
- PR #85 remains DRAFT / DO NOT MERGE.
- Save/Share remains blocked until owner visual approval.


### VR-4 - owner-marked bounded repair / final visual gate candidate

- Owner review source: annotated screenshots against VR-3 proof; findings are clarifications of existing visual-contract rules, not new design requirements.
- Final implementation head: `f7e4417c05a2981415ef3648ead740e20469fe33`.
- Push Scaffold: #3279 / `35873556136` - **SUCCESS**.
- PR Scaffold: #3280 / `35873560390` - **SUCCESS**.
- Final populated-proof artifact: `10756937024` (`pc-sheet-populated-template-proofs`).
- Backend: PASS.
- Hosted database: PASS.
- Kotlin build/test: PASS.
- Source-render upload: PASS.
- Populated-proof upload: PASS.

Owner findings mapped to implementation:

1. **Custom v1 Equipment**
   - owner status before repair: PASS WITH OBSERVATION;
   - compact/condensed Equipment treatment added;
   - logical item identity (item + location + weight) is kept together when feasible;
   - operational state and notes remain subordinate labeled lines rather than item-like rows;
   - no redesign of the approved v1 family.

2. **Custom v2 headings**
   - section labels no longer behave like gray data rows;
   - headings use the family heading grammar with white breathing space and established Corbel-bold treatment.

3. **Custom v2 Equipment / Equipo Especial**
   - Extended Equipment now reuses the normal-family two-column module grammar instead of the rejected three-column invention;
   - Equipment metadata stays in the same column beneath its item;
   - Equipo Especial reuses native location-row semantics;
   - recognized source rows (Cabeza/Rostro/Cuello/etc.) select the row and are not printed a second time over the source label.

4. **Fantasy Sheet ruled-space model**
   - fixed-height semantic feature-card allocation was removed from trait continuation;
   - one continuous physical ruled grid now governs the writable region;
   - content consumes consecutive rows only as needed;
   - unused capacity remains visible ruled paper;
   - unexplained blank white holes are not part of the accepted grammar.

Intermediate verification:

- first bounded repair exposed a long v2 treasure-row fit failure plus a stale v1 text-run assertion; both were repaired without weakening the visual rules;
- `87ac73e4da63e257b5349aa8cd97d971b9264a68` then passed push #3276 / `35871179109` and PR #3277 / `35871183759`;
- Worker visual inspection found two remaining filling artifacts: orphaned v1 weight text and v2 metadata spilling into a neighboring Equipment cell;
- final cleanup produced `f7e4417c05a2981415ef3648ead740e20469fe33`, and both final workflows are green.

Worker visual preflight of the final artifact:

- Fantasy trait continuation: continuous visible ruled field, consecutive content rows, no unexplained blank white hole;
- Custom v1 Equipment: logical item identity stays together; state/note lines remain visibly subordinate;
- Custom v2 per Attribute and per Ability: headings read as headings; Equipment uses duplicated native module grammar; item metadata remains vertically associated; special-equipment recognized locations no longer overprint source labels.

Contract status:

- TERM-001: PASS.
- ARCH-001: PASS.
- XY-001: no regression detected from the bounded repair; final CI and rendered-page preflight pass.
- NAME-001: PASS - owner-facing family remains **Fantasy Sheet**; legacy `CLASSIC_DND_STYLE` is internal compatibility only.
- RUN-001: PASS - failed/intermediate runs were read and classified before promotion.
- GOLDEN-001: unchanged - prior owner-approved proofs remain historical visual authority until the owner explicitly approves this candidate.

Status: **IMPLEMENTATION + CI + WORKER VISUAL PREFLIGHT PASS / OWNER FINAL VISUAL QA PENDING**.

Gate:

- PR #85 remains DRAFT / DO NOT MERGE;
- Save/Share remains blocked;
- if owner approves this exact candidate, freeze it and move to Desktop Save/Share integration using the same production renderer/render plan, followed only by implementation-parity verification;
- if owner reports another defect, append a bounded next iteration rather than restarting PDF design.

Detailed continuity checkpoint:

`docs/checkpoints/2026-09-23_PC_SHEET_PDF_OWNER_MARKED_REPAIR_FINAL_VISUAL_GATE.md`

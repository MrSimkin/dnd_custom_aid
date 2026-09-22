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

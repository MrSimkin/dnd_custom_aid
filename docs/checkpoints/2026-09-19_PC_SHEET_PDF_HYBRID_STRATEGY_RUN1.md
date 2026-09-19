# Checkpoint — PC Sheet PDF Hybrid Strategy 1 / Run 1

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Strategy:** 1 — Hybrid authoritative-template + native-geometry + separate vector Form/OCG overlay  
**Iteration:** Run 1 of minimum 3 before considering a strategy switch  
**Final Run-1 code head:** `135201dfe4cecee969df7e6834dbb6e568d0a64f`  
**Scaffold:** `35451118750` / run #2641 — SUCCESS

## Iteration policy agreed with owner

For each rendering strategy:

1. agree the strategy before broad implementation;
2. execute at least three meaningful visual runs before abandoning it, unless a true safety/destructive blocker appears;
3. each run must preserve both owner observations and independent technical/visual audit observations;
4. lessons from every run carry into the next run and into any later strategy;
5. after every generated draft, perform a sincere audit before making the next iteration;
6. no visual family is approved merely because CI is green;
7. PR #85 stays draft until an owner-approved rendering strategy and visual result exist.

Strategy 1 (Hybrid) is the first strategy under this protocol.

## Run 1 purpose

Run 1 intentionally did **not** rewrite the whole renderer. It tested the hybrid mechanism on representative failure classes:

- authoritative Custom v1 source PDF remains untouched;
- generated content is placed in a separate PDF Form XObject / Optional Content Group layer;
- geometry is specified in native PDF points rather than the existing renderer-wide pseudo-pixel convention;
- ruled text uses actual rule anchors/start positions;
- ruled text uses a larger Fira Sans Regular candidate instead of tiny Barlow Condensed Bold;
- `ESPACIOS` uses box-driven larger sizing;
- frozen v8 CHECK and OVAL_FILLED glyphs are fitted to measured printed-container rectangles, including independent x/y fitting.

Representative pages retained for the proof:

- source page 1 — attack rule + spell-slot oval;
- source page 3 — story ruled lines;
- source page 4 — `ESPACIOS` box + checked spell row.

The test also generates an overlay-only PDF so template and generated layer can be inspected separately.

## Technical execution history

Initial spike commit:

`8ad3fc0ebec9be177e74cb4f17567f0206ccccbd`

Scaffold `35450575147` failed at compile because the Kotlin binding required `setBBox(...)` rather than property syntax. This was a harness-only API correction, not a visual iteration.

Correction:

`a3b4e014af554d2dab9e8c368584ae55d2bb505c`

Scaffold `35450822144` then generated the PDF but the test failed on a `PDFTextStripper` assertion for text inside the optional-content Form. Since extractor behavior is not a visual acceptance criterion, that irrelevant assertion was removed without changing rendering.

Final Run-1 head:

`135201dfe4cecee969df7e6834dbb6e568d0a64f`

Scaffold `35451118750` / run #2641 passed backend, hosted database, Kotlin/build/tests and artifact upload.

## Independent post-generation audit

### Result

**RUN 1 VISUAL RESULT: FAIL — USEFUL STRATEGY EVIDENCE, NOT A STRATEGY REJECTION**

The hybrid PDF structure itself works:

- authoritative template pages remain intact;
- separate Form XObject content is composited at the expected page regions;
- real Optional Content Groups are present;
- the overlay-only artifact is generated independently;
- 300-DPI proof PNGs are produced.

However, generated fonts inside the Form layer are invalid for reliable visual QA in this run.

### Critical defect — overlay fonts are not embedded

Independent PDF preflight with `pdffonts` reports:

- `FiraSans-Regular` — CID TrueType / Identity-H — **not embedded**;
- `FiraSans-SemiBold` — CID TrueType / Identity-H — **not embedded**;
- `ParaHojadePJSymbolsV8-Regular` — CID TrueType / Identity-H — **not embedded**.

The source-template fonts remain correctly embedded.

A second renderer also reports:

- `non-embedded font using identity encoding: FiraSans-Regular`;
- `non-embedded font using identity encoding: ParaHojadePJSymbolsV8-Regular`;
- `non-embedded font using identity encoding: FiraSans-SemiBold`.

Consequences visible in the proof:

- normal text renders as corrupted/gibberish glyphs;
- the `ESPACIOS` value cannot be judged;
- v8 check/oval rendering cannot be trusted;
- therefore print readability and fine optical alignment cannot receive PASS/CHANGE judgments from this run.

This defect exists in the overlay-only PDF too, proving it is in the Form/font path itself rather than caused by compositing against the owner template.

### What can still be learned visually

Even with corrupt glyph rendering:

- content appears in the intended broad sections;
- native-point rule anchors place the story block at the real right-side rule origin rather than the prior ~23 pt left displacement;
- the separate-layer architecture makes it substantially easier to isolate template vs generated content;
- no evidence from Run 1 suggests that the authoritative template itself must be redrawn.

These are provisional positives only; they do not constitute visual approval.

## Primary technical interpretation

The strongest current hypothesis is that subset font loading is not being finalized correctly when those fonts are referenced only from the Form XObject / optional-content layer.

Run 1 used:

`PDType0Font.load(document, input, true)`

for Fira and v8.

PDFBox 3.0.8 documents the three-argument loader as embedding the TTF and allows `embedSubset=false`; it explicitly recommends non-subset embedding for form-style content such as AcroForm appearances. Run 2 should test full embedding in the Form layer before changing geometry.

## Run 2 proposed changes

Run 2 should preserve the same representative geometry/data and change only what is required to make the hybrid proof valid:

1. load overlay fonts with full embedding (`embedSubset=false`) and verify the saved PDF reports them embedded;
2. add a post-save structural/preflight assertion for overlay font embedding so this defect cannot silently recur;
3. render the same three representative pages and the overlay-only artifact;
4. inspect with at least two renderers;
5. only after glyph correctness is restored, judge:
   - print-readable ruled-text size;
   - exact rule-start alignment;
   - `ESPACIOS` size/centering;
   - check optical fit;
   - oval fill ratio;
6. do not broaden to the whole export during Run 2.

If full embedding does not solve the Form-font path, Run 2 should investigate explicit Form resource/font registration or an alternate Form construction path. This still remains Strategy 1; it is not grounds to switch strategies before the agreed minimum iterations.

## Current gate

- PR #85 remains **DRAFT / DO NOT MERGE**.
- No Custom family is approved.
- Hybrid Strategy 1 remains active.
- Run 1 is closed as a failed-but-informative visual iteration.
- Next action, after owner review of this audit, is Hybrid Run 2.

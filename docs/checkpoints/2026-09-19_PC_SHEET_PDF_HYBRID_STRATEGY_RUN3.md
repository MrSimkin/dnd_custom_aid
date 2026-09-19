# Checkpoint — PC Sheet PDF Hybrid Strategy 1 / Run 3

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Strategy:** 1 — Hybrid authoritative-template + native-geometry + separate vector Form/OCG overlay  
**Iteration:** Run 3 of the agreed minimum three  
**Final Run-3 code head:** `5909ba3780beb666183cad2169f3d296684f82de`  
**Scaffold:** `35453799228` / run #2651 — SUCCESS

## Run 3 purpose

Run 3 preserved all successful Run-2 behavior:

- authoritative source PDF untouched;
- separate Form XObject / Optional Content Group overlay;
- fully embedded Fira and v8 fonts;
- native PDF-point rule anchors;
- 9.0–9.25 pt Fira Regular ruled text;
- 15 pt `ESPACIOS` value.

The only renderer-behavior change was marker fitting.

Instead of mapping hard-coded TrueType design bounds directly into a PDF text matrix, Run 3 fits the actual PDF text span using:

- `font.getStringWidth(glyph) / 1000` for normalized horizontal extent;
- font descriptor ascent/descent for normalized vertical extent;
- target rectangles taken from the authoritative source glyph bboxes;
- small context-specific optical insets.

## Technical result

**PASS.**

Scaffold #2651 is fully green.

Post-save embedding remains correct:

- FiraSans-Regular — embedded=yes, subset=no;
- FiraSans-SemiBold — embedded=yes, subset=no;
- ParaHojadePJSymbolsV8-Regular — embedded=yes, subset=no.

No regression of the separate-layer mechanism or source-template fidelity was observed.

## Independent visual / geometric audit

### Ruled text size and line use

**PASS AT REPRESENTATIVE-SPIKE LEVEL.**

The Run-2 improvement remains stable:

- 9.0–9.25 pt regular text uses the space between rules much better than the former ~6.5–7.5 pt condensed bold;
- the narrative sample is clearly readable on the 300-DPI render;
- baseline placement leaves a small gap above the printed rule;
- no collision with adjacent rules is visible.

Caveat: final whole-export approval still requires a real 100% print test and stress cases in narrower fields.

### Rule-start alignment

**PASS AT REPRESENTATIVE-SPIKE LEVEL.**

The narrative region begins inside the actual right-column rule origin with a small deliberate inset rather than being displaced left of the writing region.

The attack-row sample also starts within the intended writing columns rather than before the rule.

The lesson for whole-renderer migration is to store native rule start/end/baseline geometry plus explicit padding, not generic rectangles.

### `ESPACIOS` numeric size

**PASS AT REPRESENTATIVE-SPIKE LEVEL.**

The level-1 value `4` at 15 pt SemiBold remains materially clearer and visually centered in the printed box.

### Square check marker

**PASS AT REPRESENTATIVE-SPIKE LEVEL.**

Authoritative source square bbox:

`x 28.205..37.874, top-y 327.187..339.474`

Run-3 v8 check bbox:

`x 28.805..37.274, top-y 327.787..338.874`

This is exactly the intended 0.6 pt optical inset on all sides.

The check is now inside and centered within the source square rather than tiny/below-left.

Independent note: the check could later be made slightly more visually assertive by reducing the inset if the owner prefers, but it is now structurally correct.

### Filled spell-slot oval

**PASS AT REPRESENTATIVE-SPIKE LEVEL.**

Authoritative source oval bbox:

`x 80.646..94.710, top-y 442.688..460.560`

Run-3 v8 filled oval bbox:

`x 81.546..93.810, top-y 443.688..459.560`

This matches the intended near-fill with 0.9 pt horizontal and 1.0 pt vertical white margin.

The marker is centered and leaves only a controlled narrow gap rather than the Run-2 left/low displacement.

## Strategy-level conclusion after three runs

### Run history

- **Run 1:** FAIL / useful — separate Form/OCG worked, but Form-only subset fonts were not embedded reliably.
- **Run 2:** technical PASS / partial visual PASS — full embedding fixed; text and `ESPACIOS` improved; marker transform failed.
- **Run 3:** technical PASS / representative visual PASS — metric-based marker fitting corrected check and oval while preserving prior improvements.

### Independent recommendation

**HYBRID STRATEGY 1 IS NOW A VIABLE CANDIDATE TO APPROVE FOR CONTINUED DEVELOPMENT.**

This is not a claim that the complete Custom PDF family is visually approved. It means the three-run representative experiment has successfully demonstrated solutions for the owner's five triggering defect classes:

1. larger/readable ruled text;
2. correct rule-start anchoring;
3. square-check fitting;
4. larger `ESPACIOS` values;
5. near-fill spell-slot ovals.

The experiment also demonstrated:

- reliable embedded fonts inside the Form layer;
- preserved vector/template quality;
- isolated overlay-only debugging;
- native PDF-point geometry;
- marker fitting based on real PDF font metrics.

No evidence from the three runs currently justifies abandoning this strategy for AcroForm, SVG or full-redraw alternatives.

## If owner approves Strategy 1

Do **not** immediately merge PR #85.

The next phase should be a controlled renderer migration:

1. extract the Hybrid proof mechanics into reusable production primitives;
2. create per-template native-point geometry manifests;
3. map ruled regions as explicit start/end/baseline anchors;
4. map marker containers from source glyph/path geometry;
5. keep family/field-specific typography roles and print-size floors;
6. migrate Custom v1 page-by-page/section-by-section;
7. audit every generated draft independently before owner review;
8. then migrate Custom v2 using lessons from v1;
9. retain overlay-only/debug artifacts during migration;
10. only after complete family QA consider removing superseded legacy proof code and merging PR #85.

## Current gate

- Three-run strategy trial is complete.
- Strategy 1 is **recommended for owner approval**, not self-approved on the owner's behalf.
- PR #85 remains **DRAFT / DO NOT MERGE**.
- No Custom visual family is yet owner-approved.

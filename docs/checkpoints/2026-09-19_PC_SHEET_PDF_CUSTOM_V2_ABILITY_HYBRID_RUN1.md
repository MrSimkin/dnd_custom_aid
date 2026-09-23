# Checkpoint — Custom v2 per-Ability Hybrid Run 1

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Visual family:** Custom v2 — per Ability  
**Strategy:** Strategy 1 — Hybrid  
**Implementation commit:** `75c61966312dc745f1d3763631060cdb96db6dae`  
**Scaffold:** `35470243545` / run #2754 — SUCCESS

## Preconditions

Custom v2 — per Attribute is OWNER APPROVED / FROZEN.

Its shared source pages 3–5 are also frozen as the approved v2 common-page baseline.

Authoritative pointers:

- `docs/PC_SHEET_CUSTOM_V2_APPROVED_BASELINES.md`
- `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V2_ATTRIBUTE_RUN4_OWNER_APPROVED.md`

The per-Ability run must therefore change only the alternative first page unless a new shared-page defect is explicitly identified.

## Source structure

Per-Ability four-page export:

1. source page 2 — per Ability;
2. source page 3 — approved common Equipment/Narrative;
3. source page 4 — approved common Spells;
4. source page 5 — approved common Notes.

Source page 1 / per Attribute is excluded.

## Per-Ability first-page geometry

The alternative page preserves the same right-side combat / attack / trait / bottom architecture but reorganizes the left side.

Page-2-specific elements:

- taller portrait;
- proficiency and Inspiration move to the upper-left band;
- six score/modifier blocks remain;
- saving throws become one centralized section;
- skills become one centralized section.

### Ability score / modifier treatment

The per-Ability page reuses the approved per-Attribute ellipse treatment:

- score values: 17 pt;
- modifier values: 15.5 pt;
- modifier rectangles fitted to the actual ellipse interior rather than generic small boxes.

This preserves the owner's approved interpretation of “better use of space” inside the ellipses.

### Saving throws

Measured source checkboxes:

- x `98.0 .. 106.5` pt;
- size `8.5 × 9.0` pt.

Top Y values:

`171.5, 186.5, 201.0, 216.0, 230.5, 245.5`

Numeric rule:

- x `161.5 .. 178.0` pt.

Rule Y values:

`183.0, 198.0, 212.5, 227.5, 242.0, 257.0`

### Skills

Measured source checkboxes:

- x `98.0 .. 106.5` pt;
- size `8.5 × 9.0` pt.

First top Y:

`302.0` pt

then approximately 15-pt row rhythm through `556.5` pt.

Numeric rule:

- x `161.5 .. 178.0` pt.

First Y:

`313.5` pt

then source-measured row values through `568.0` pt.

The same approved v8 optical offsets are reused:

- single check: optical X +1.65 pt, optical Y -0.6 pt;
- expertise/double check: optical X +2.0 pt, optical Y -0.6 pt.

## Technical result

**PASS.**

Scaffold #2754 passed all jobs.

Generated review artifact:

`hybrid-custom-v2-ability-run1-composite.pdf`

Diagnostic overlay:

`hybrid-custom-v2-ability-run1-overlay-only.pdf`

Preflight:

- 4 pages;
- Letter 612 × 792 pt;
- openable;
- encrypted: false;
- likely scanned: false;
- XFA: false.

Font-embedding and section-isolation assertions passed.

## Shared-page preservation proof

The per-Ability artifact's pages 2–4 were compared against the owner-approved per-Attribute Run-4 artifact's pages 2–4 at the CI 300-DPI render resolution.

Results:

- shared page 1 / Equipment-Narrative: **0 changed pixels / 0.000%**
- shared page 2 / Spells: **0 changed pixels / 0.000%**
- shared page 3 / Notes: **0 changed pixels / 0.000%**

This proves the approved shared-page baseline was preserved exactly.

## Renderer parity

PDFium vs pdftoppm at 200 DPI:

- Page 1: ~0.1318% changed pixels
- Page 2: ~0.0860%
- Page 3: ~0.0812%
- Page 4: ~0.0791%

Visual inspection indicates ordinary antialiasing/render-engine differences only; no structural divergence.

## Independent visual audit

### Page 1 — per Ability

**PASS FOR OWNER REVIEW**

Successful:

- Identification values align cleanly with source rules.
- `Raza` retains the approved lower optical placement.
- tall portrait uses the available upper field without touching the frame;
- handwritten character name stays in the lower decorative band and clears the crown;
- upper-left proficiency value is readable and centered;
- Inspiration marker is contained in its source container;
- all six ability scores fit their printed boxes;
- modifier values make effective use of the ellipse interiors using the approved 15.5-pt treatment;
- saving-throw checks and values follow exact source rows;
- centralized skill checks and values follow exact source rows;
- expertise double-check remains optically centered;
- right-side AC / initiative / speed / hit dice / HP remain stable;
- attacks use the established three-column Hybrid geometry;
- Traits remain stable;
- spell-slot totals remain clear;
- `ESPACIOS GASTADOS` remains blank;
- spellcasting summary / treasure / objects / other fields remain stable.

No independent first-page defect was found that warrants another calibration run before owner review.

### Pages 2–4 — approved common pages

**PASS / UNCHANGED**

They are pixel-identical to the approved per-Attribute shared-page baseline.

## Conclusion

**CUSTOM V2 — PER ABILITY — HYBRID RUN 1: PASS FOR OWNER REVIEW**

No strategy change is indicated.

The next gate is the separate owner visual approval required by D-0074.

If approved:

1. freeze the per-Ability first-page baseline;
2. mark both Custom-v2 presentations approved;
3. keep the shared v2 pages frozen;
4. move to the next required visual family: Classic D&D-style.

PR #85 remains **DRAFT / DO NOT MERGE**.

# Checkpoint — Custom v2 per-Attribute Hybrid Runs 3–4 Final Calibration

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Visual family:** Custom v2 — per Attribute  
**Strategy:** Strategy 1 — Hybrid

## Owner observations after Run 2

The owner reported only a small calibration set:

- review Y axis for `Raza`;
- review Y axis for the portrait name;
- make better use of the available space inside the Attribute-modifier ellipses/ovals;
- audit check-mark geometry across the complete sheet.

The owner later clarified that “better use of space on attributes modifiers” specifically meant:

> use the available space inside the modifier ellipses/ovals better,

not merely increase font size in isolation.

## Run 3

Commit:

`b80cac0af3d63e97bf1f922fa1afda592c86b933`

Scaffold:

`35468897425` / run #2737 — SUCCESS

### Run-3 changes

- `Raza` lowered optically toward its source rule;
- portrait name lowered within the lower decorative banner while preserving crown clearance;
- modifier target rectangles expanded to the measured ellipse interiors;
- modifier values increased from 11.5 pt to 15.5 pt so they occupy the ellipse more effectively;
- v8 check marks received family-specific optical offsets:
  - page-1 saving/skill checks;
  - page-2 Special Equipment checks;
  - page-3 spell preparation checks.

### Independent Run-3 audit

Successful:

- `Raza` visually matches the surrounding handwritten Identification fields better;
- portrait name is balanced inside the banner and clears the crown;
- modifier values now make materially better use of their ellipses while preserving clear border space;
- all checks remain inside their printed source boxes.

The audit then measured actual dark-stroke margins from the **overlay-only PDF** rather than relying solely on font metrics.

This exposed a final small residual:

- marks were still slightly left/top biased in some checkbox families.

No other defect or strategy issue was found.

## Run 4 — marker-only final calibration

Commit:

`03c3155301196eaab1229afb4827c46f8acf04cc`

Scaffold:

`35469291933` / run #2739 — SUCCESS

Run 4 changed **only check-mark optical offsets**.

No text, modifier, Identification, portrait, spell-name, Equipment, Notes, or other geometry was changed.

### Final measured dark-stroke margins

Measurements use the 200-DPI overlay-only render and the exact source checkbox rectangles.

#### Page 1 — normal check

Representative margins in points:

- left: 2.30
- top: 3.08
- right: 2.24
- bottom: 3.04

Result: effectively centered on both axes.

#### Page 1 — double-check / expertise

Representative margins:

- left: 2.66
- top: 2.74
- right: 2.96
- bottom: 2.66

Result: balanced for the asymmetric double-check glyph.

#### Page 2 — Special Equipment

Representative margins:

- left: 2.50
- top: 3.02
- right: 2.04
- bottom: 3.10

Result: contained and optically balanced inside the source square.

#### Page 3 — spell preparation checks

Representative left-column margins:

- left: 2.20
- top: 2.70
- right: 2.34
- bottom: 2.92

Representative middle/right checks remain within comparable sub-point balance.

Result: visually centered across all spell columns.

## Final Run-4 technical verification

Approved review artifact candidate:

`hybrid-custom-v2-attribute-run4-composite.pdf`

Diagnostic:

`hybrid-custom-v2-attribute-run4-overlay-only.pdf`

Preflight:

- 4 pages;
- Letter 612 × 792 pt;
- openable;
- not encrypted;
- not scanned;
- no XFA.

Renderer parity:

PDFium vs pdftoppm at 200 DPI:

- Page 1: ~0.1444% changed pixels
- Page 2: ~0.0860%
- Page 3: ~0.0812%
- Page 4: ~0.0791%

Visual inspection shows normal antialiasing/render-engine differences only.

## Differential audit

### Run 3 → Run 4

Only checkbox families changed:

- Page 1: ~0.000164% changed pixels
- Page 2: ~0.000152%
- Page 3: ~0.000312%
- Page 4: **0% change**

This verifies the final marker calibration stayed isolated.

### Run 2 → final Run 4

Intended owner-feedback regions changed:

- Page 1: Identification/portrait/modifier/check regions
- Page 2: Special Equipment checks
- Page 3: spell preparation checks
- Page 4: **0% change**

No unrelated page-wide redesign occurred.

## Independent final visual audit

### Page 1 — PASS FOR OWNER REVIEW

- `Raza` Y placement is visually consistent with the neighboring handwritten fields.
- Portrait name sits cleanly inside the lower banner and clears the crown.
- Modifier values use the ellipse interiors substantially better and remain comfortably inside the borders.
- normal and double v8 checks are optically centered.
- all previously passing Run-2 regions remain stable.

### Page 2 — PASS FOR OWNER REVIEW

- Special Equipment checks are aligned and contained.
- Equipment, Background, Bonds, Ideals, Historia and descriptions remain unchanged and stable.

### Page 3 — PASS FOR OWNER REVIEW

- spell preparation checks are now optically balanced across left, middle and right columns;
- cantrips remain without preparation marks;
- `ESPACIOS GASTADOS` remains blank;
- all spell-name/slot geometry remains unchanged.

### Page 4 — PASS FOR OWNER REVIEW

- pixel-identical to the prior approved-for-review state;
- Notes geometry and typography remain stable.

## Conclusion

**CUSTOM V2 — PER ATTRIBUTE — FINAL CALIBRATED HYBRID CANDIDATE: PASS FOR OWNER REVIEW**

No strategy change was required.

The owner’s Run-2 observations have been addressed and independently audited.

## Next gate

Owner visual review of Run 4.

If approved:

1. freeze the Custom-v2 per-Attribute first-page baseline;
2. freeze/reuse source pages 3–5 as the shared Custom-v2 baseline;
3. proceed directly to the alternative per-Ability first page using the already-persisted source geometry analysis;
4. independently audit the complete per-Ability four-page draft;
5. request the separate owner approval required by D-0074.

PR #85 remains **DRAFT / DO NOT MERGE**.

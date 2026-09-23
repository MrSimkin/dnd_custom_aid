# Checkpoint — Custom v2 per-Attribute Runs 3–4 final calibration

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Visual family:** Custom v2 — per Attribute  
**Strategy:** Strategy 1 — Hybrid

## Owner observations after Run 2

The owner reported only a small number of remaining calibration items:

- review Y axis on `Raza`;
- review Y axis on the portrait name;
- make better use of the available space inside the Attribute modifier ellipses;
- audit check-mark geometry across the complete sheet.

Owner clarification:

> “better use of space on attributes modifiers” means the numeric modifier should occupy the available ellipse interior more effectively, not merely move vertically.

This clarification is binding for the calibration interpretation.

## Run 3 — first calibration pass

Commit:

`b80cac0af3d63e97bf1f922fa1afda592c86b933`

Scaffold:

`35468897425` / run #2737 — SUCCESS

Run 3 changed only the requested calibration families.

### Raza

The source rule remained unchanged.

The handwritten value clearance was reduced from 2.2 pt to 0.7 pt, moving the value optically downward toward the physical rule without changing font role or X geometry.

### Portrait name

The name container moved from top-y 132 pt to 134.5 pt.

The font and horizontal center remained unchanged.

This moves `Aster Vale` lower inside the portrait-name banner while preserving clearance from the central crown ornament.

### Attribute modifiers — ellipse use

This was treated as an **ellipse-space utilization** issue, not merely a vertical-position issue.

Previous modifier placement:

- approximate generic rectangle: 36.5 × 24 pt;
- font size: 11.5 pt.

Run-3 placement:

- rectangle widened to 44 pt;
- vertical geometry tightened to the actual ellipse interior, 22.5 pt;
- X origin shifted to match the ellipse body;
- font size increased to 15.5 pt.

Result:

- modifier values such as `+0`, `+3`, `+4`, `-1` now occupy the ellipse visibly and proportionally;
- values remain clear of the printed ellipse boundary;
- no ellipse artwork is altered.

### Initial check-mark calibration

Run 3 applied family-specific optical offsets rather than one generic global nudge:

- page-1 Attribute/saving-throw/skill checks;
- page-2 Special Equipment checks;
- page-3 spell preparation checks.

## Run 4 — measured optical check centering

Commit:

`03c3155301196eaab1229afb4827c46f8acf04cc`

Scaffold:

`35469291933` / run #2739 — SUCCESS

Run 4 changed only the optical check offsets from Run 3.

The target source rectangles did not change.

### Page 1 — Attribute / saving-throw / skill checks

Final offsets:

- single check: optical X +1.65 pt, optical Y -0.6 pt;
- expertise/double check: optical X +2.0 pt, optical Y -0.6 pt.

Representative target rectangle:

- x `98.5 .. 107.0`
- top-y `125.0 .. 134.0`
- target center: `(102.75, 129.50)`

Measured dark-stroke center in the 200-DPI overlay-only render:

- approximately `(102.78, 129.60)`

Center error:

- X: ~0.03 pt
- Y: ~0.10 pt

Representative expertise/double-check target center:

- `(102.75, 221.00)`

Measured dark-stroke center:

- approximately `(102.60, 221.04)`

Center error:

- X: ~0.15 pt
- Y: ~0.04 pt

### Page 2 — Special Equipment checks

Final offsets:

- optical X +1.75 pt
- optical Y -0.7 pt

Representative target center:

- `(91.75, 535.00)`

Measured dark-stroke center:

- approximately `(91.80, 534.78)`

Center error:

- X: ~0.05 pt
- Y: ~0.22 pt

Second sampled row:

- target center `(91.75, 552.00)`
- measured center ~`(91.80, 552.06)`

### Page 3 — spell preparation checks

Final offsets:

- optical X +1.7 pt
- optical Y -0.6 pt

Representative left-column target center:

- `(18.25, 350.75)`

Measured dark-stroke center:

- approximately `(18.18, 350.64)`

Representative middle-column target center:

- `(213.75, 350.75)`
- measured center ~`(213.66, 350.64)`

Representative right-column target center:

- `(409.25, 483.75)`
- measured center ~`(409.14, 483.48)`

These measurements confirm that the final checks are optically centered inside the printed source rectangles across all checkbox families.

## Technical verification

Final Run-4 PDF:

`hybrid-custom-v2-attribute-run4-composite.pdf`

Diagnostic overlay:

`hybrid-custom-v2-attribute-run4-overlay-only.pdf`

Preflight:

- 4 pages
- Letter 612 × 792 pt
- openable
- encrypted: false
- likely scanned: false
- XFA: false

Renderer parity at 200 DPI:

- Page 1: ~0.1444% changed pixels
- Page 2: ~0.0860%
- Page 3: ~0.0812%
- Page 4: ~0.0791%

Visual inspection confirms these differences are normal renderer/antialiasing differences, not structural divergence.

## Regression diff vs Run 2

Run 2 → final Run 4:

- Page 1 changed: requested Raza, portrait-name, modifier ellipse use, and page-1 check calibration;
- Page 2 changed: Special Equipment check calibration only;
- Page 3 changed: spell check calibration only;
- Page 4: **0% pixel change**.

Changed-pixel percentages:

- Page 1: ~0.002515%
- Page 2: ~0.000237%
- Page 3: ~0.000431%
- Page 4: 0%

## Run 3 → Run 4 isolation

Run 4 was only the final measured check-centering refinement.

Changed-pixel percentages:

- Page 1: ~0.000164%
- Page 2: ~0.000152%
- Page 3: ~0.000312%
- Page 4: 0%

This confirms no unrelated Run-3 calibration was disturbed.

## Independent visual audit — final Run 4

### Page 1

**PASS FOR OWNER REVIEW**

- `Raza` is optically lower and better aligned to its physical rule.
- portrait name is better centered vertically within the lower banner and remains clear of the crown ornament.
- Attribute modifier values now make visibly better use of the ellipse interior rather than floating as undersized labels.
- score values remain stable.
- page-1 single checks and expertise checks are centered in their printed squares.
- all previously accepted attacks, traits, slots, spellcasting summary, treasure and other-item geometry remains stable.

### Page 2

**PASS FOR OWNER REVIEW**

- Special Equipment checks are centered in their printed squares.
- Equipment, narrative and special-equipment text geometry remains unchanged from the previously accepted draft.

### Page 3

**PASS FOR OWNER REVIEW**

- all prepared spell checks are centered consistently across left/middle/right columns;
- cantrips still have no preparation checks;
- `ESPACIOS GASTADOS` remains blank;
- spell names / slot totals remain unchanged.

### Page 4

**PASS FOR OWNER REVIEW**

- unchanged from Run 2;
- Notes geometry remains stable.

## Conclusion

**CUSTOM V2 — PER ATTRIBUTE — RUN 4: PASS FOR OWNER REVIEW**

No strategy change is indicated.

The remaining owner observations from Run 2 have been addressed with bounded calibration.

The per-Attribute variant remains under the mandatory owner visual-approval gate.

PR #85 remains **DRAFT / DO NOT MERGE**.

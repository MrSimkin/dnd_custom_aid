# Checkpoint — Custom v1 Run 7 Final Coordinate Calibration

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Strategy:** Strategy 1 — Hybrid  
**Prior complete all-Hybrid baseline:** Run 7 / Scaffold #2702  
**Calibration commit:** `7448693e36ee1b26243bd4091615dba725e95027`  
**Final Scaffold:** `35465731044` / run #2708 — SUCCESS

## Owner corrections

The owner reported only local coordinate issues remaining:

### Page 2
- Equipment columns 2 and 3: X axis;
- Gemas / Joyas / Arte: X axis;
- Equipo Especial description: X axis;
- Equipo Especial checks: X and Y axes.

### Page 3
- Otros Rasgos y Atributos: X axis;
- Notas: X axis.

### Page 5
- Notes second column: X axis.

No strategy change was requested or required.

## Calibration method

This pass did **not** eyeball offsets.

The authoritative source PDF path/line geometry was measured directly and used as the alignment authority.

No typography sizes, wording, spell semantics, section structure, or unrelated page geometry changed.

## Exact source geometry applied

### Page 2 — Equipment

Column 1 remained unchanged.

Columns 2 and 3 now use the exact source-rule extents:

- column 2: x `169.937 .. 300.331` pt;
- column 3: x `311.669 .. 442.063` pt.

Generated text keeps the existing deliberate 1.5 pt writing inset:

- column-2 generated text begins at x `171.437`;
- column-3 generated text begins at x `313.169`.

### Page 2 — Gemas / Joyas / Arte

Object rule:

- x `453.402 .. 546.945` pt.

Value rule:

- x `549.779 .. 583.795` pt.

Generated object text therefore begins at x `454.902` after the established 1.5 pt inset.

### Page 2 — Equipo Especial description

Description rule now uses the true source span:

- x `240.803 .. 583.795` pt.

Generated description text begins at x `242.303` after the 1.5 pt inset.

### Page 2 — Equipo Especial checks

The original printed square glyph rectangles were measured directly.

All checks now target:

- x `113.244 .. 122.913` pt;
- each row's exact source Y rectangle.

The generated v8 check is fitted with 0.6 pt inset on each side.

Representative first row:

Printed square:
- x `113.244 .. 122.913`;
- top-y `508.770 .. 521.057`.

Generated check:
- x `113.844 .. 122.313`;
- top-y `509.370 .. 520.457`.

Result:

- left margin: 0.6 pt;
- right margin: 0.6 pt;
- top margin: 0.6 pt;
- bottom margin: 0.6 pt.

This resolves both the X and Y optical displacement without changing v8 artwork.

### Page 3 — Otros Rasgos y Atributos

Left divided rule:

- x `215.291 .. 396.708` pt.

Right divided rule:

- x `402.378 .. 583.795` pt.

Generated text retains the deliberate 2 pt inset.

Representative first value begins at x `217.291`.

### Page 3 — Notas

Full physical rule:

- x `215.291 .. 583.795` pt.

Generated paragraph text begins at x `217.291`.

### Page 5 — Notes second column

Second-column physical rules:

- x `311.669 .. 583.795` pt.

Generated text begins at x `313.669` with the established 2 pt inset.

## Technical result

**PASS.**

Scaffold #2708 is fully green.

PDF preflight:

- 5 pages;
- Letter 612 × 792 pt;
- openable;
- not encrypted;
- not scanned;
- no XFA.

Renderer parity:

- PDFium and pdftoppm both render all five pages;
- observed differences remain normal antialiasing/render-engine differences;
- no structural divergence was observed.

## Visual regression diff

Compared the final calibration PDF against the previous final Run-7 PDF (#2702).

Changed pages:

- Page 2 — YES
- Page 3 — YES
- Page 5 — YES

Unchanged pages:

- Page 1 — **0% pixel change**
- Page 4 — **0% pixel change**

Diff scopes correspond to the owner-requested calibration regions.

No unrelated page moved.

## Independent visual audit

### Page 2

**PASS.**

- Equipment columns 2/3 now start on their own physical rules with the same white-space relationship as column 1.
- Gemas/Joyas/Arte object/value fields align to their printed columns.
- Equipo Especial descriptions align with the DESCRIPCIÓN rule.
- checks are centered inside the source checkbox squares on both axes.

### Page 3

**PASS.**

- both Otros Rasgos columns now respect their printed divided-rule starts;
- Notas now uses the same full-width source-rule origin as Historia.

### Page 5

**PASS.**

- second Notes column now begins on the actual second-column line origin;
- left Notes column and grid remain untouched.

## Conclusion

**RUN 7 FINAL COORDINATE CALIBRATION: PASS FOR OWNER REVIEW**

No strategy change occurred.

The remaining owner-reported coordinate defects were resolved using the authoritative source-PDF geometry.

PR #85 remains **DRAFT / DO NOT MERGE** pending owner review.

# Checkpoint — Custom v2 per-Ability Hybrid Run 2 — owner feedback calibration

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Visual family:** Custom v2 — per Ability  
**Strategy:** Strategy 1 — Hybrid  
**Correction commit:** `be689bb6628a46e562428159fc72b2ee51b95d2c`  
**Scaffold:** `35471866920` / run #2760 — SUCCESS

## Owner feedback from Run 1

The owner reported that page 1 was good except for:

- Bono por competencia;
- Inspiración;
- Clase de armadura;
- portrait name.

The owner explicitly reported that the Ability treatment was perfect.

## Calibration method

The correction deliberately preserved the approved per-Ability Ability / Saving Throw / Skill geometry.

For Bono por competencia and Inspiración, placement was calibrated against the already owner-approved Custom-v2 per-Attribute value-to-label optical relationships rather than nudged by eye.

For the portrait name, the approved per-Attribute name position was transferred using the actual decorative-banner source anchor. The page-2 banner begins approximately 42.5 pt lower than the page-1 banner.

Armor Class inspection exposed a concrete implementation defect: Run 1 rendered `16` twice in the same container (20 pt plus a second 16 pt overprint), producing the visibly heavy value.

## Applied corrections

### Bono por competencia

Run 1:

`TopRect(14f, 86f, 78f, 31f)`

Run 2:

`TopRect(9.5f, 96f, 78f, 31f)`

The resulting value-to-label center offset now matches the approved per-Attribute relationship to within rounding tolerance.

### Inspiración

Run 1:

`TopRect(132f, 97f, 18f, 18f)`

Run 2:

`TopRect(111f, 102.5f, 18f, 18f)`

The resulting marker-to-label optical offset now matches the approved per-Attribute relationship.

### Portrait name

Run 1:

`TopRect(205f, 158f, 124f, 22f)`

Run 2:

`TopRect(205f, 177f, 124f, 22f)`

This moves the handwritten name into the page-2 decorative banner while preserving the approved per-Attribute banner-relative placement.

### Clase de armadura

The second smaller `16` overprint was removed.

Run 2 renders Armor Class once using the intended 20 pt placement:

`TopRect(359f, 99f, 53f, 39f)`

PDF text extraction now reports a single `16` rather than the Run-1 merged `1166`.

## Verification

Scaffold #2760 passed all jobs:

- backend — SUCCESS;
- hosted database — SUCCESS;
- Kotlin/build/tests — SUCCESS;
- PDF artifact generation — SUCCESS.

Run-1 -> Run-2 visual regression:

- page 1 changed only for the requested calibration;
- page 2: 0 changed pixels;
- page 3: 0 changed pixels;
- page 4: 0 changed pixels.

Additional page-1 regression check:

- the full left Ability / Saving Throw / Skill region below the upper proficiency/inspiration band: **0 changed pixels**.

Therefore the owner-approved ability treatment was preserved exactly.

## Independent visual audit

**PASS FOR OWNER REVIEW.**

The four requested defects are corrected without reopening the successful Ability treatment or the frozen shared v2 pages.

## Current gate

Mandatory owner visual review of Custom v2 — per Ability Run 2.

If approved:

1. freeze the per-Ability first-page baseline;
2. mark both Custom-v2 presentations approved;
3. retain the shared v2 pages frozen;
4. proceed to the next required visual family: Classic D&D-style.

PR #85 remains **DRAFT / DO NOT MERGE**.

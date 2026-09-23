# PC Sheet PDF - pre-print geometry gates

**Status:** ACTIVE  
**Purpose:** durable independent X/Y expectations used before an owner-facing PDF proof is promoted.

The renderer is not trusted to self-certify its own coordinates. These expectations are intentionally stored separately from implementation logic.

## Gate

A proof may be uploaded by CI as a diagnostic artifact, but it must not be presented as owner-review-ready unless:

1. dynamic PDF text/marker positions are measured from the generated file;
2. measured positions fall inside the allowed frozen/source-derived regions below;
3. Custom semantic layers exist in the required order;
4. the Worker renders and visually inspects the affected pages;
5. the result is appended to `docs/PC_SHEET_PDF_ITERATION_LEDGER.md`.

## Fantasy Sheet

Legacy internal enum: `CLASSIC_DND_STYLE`.

Owner-facing name: **Fantasy Sheet**.

Historical checkpoints used the name “Classic D&D-style”. That historical name does not change the current product naming decision.

Key page geometry that must not drift without an explicit owner-approved redesign:

| Region | X | Y/top | W | H |
| --- | ---: | ---: | ---: | ---: |
| Historia y personalidad frame | 24 | 436 | 226 | 282 |
| Historia ruled content | 34 | 468 | 206 | 238 |
| Idiomas frame | 264 | 580 | 156 | 138 |
| Aliados y tesoro frame | 432 | 580 | 156 | 138 |
| Traits continuation left frame | 24 | 112 | 276 | 606 |
| Raza/Trasfondo/Otros continuation frame | 312 | 112 | 276 | 606 |
| Inventory continuation frame | 24 | 112 | 564 | 402 |
| Campaign notes frame | 24 | 112 | 360 | 606 |
| References/reminders frame | 398 | 418 | 190 | 300 |

Ruled prose contract:

- each generated physical line consumes exactly one physical writing row;
- text baseline and rule Y are derived from the same row;
- no alternating skipped rows;
- a base page may promote overflow to a continuation page rather than compressing line height.

## Custom v1

Frozen family authorities remain Run 7 base + Extended Run 6.

Special Equipment source checkbox rectangle:

- X = 113.244 pt
- W = 9.669 pt
- H = 12.287 pt
- row tops = 508.770, 528.612, 548.455, 568.297, 588.140, 607.982, 627.825, 647.667, 667.510, 687.352, 707.195, 727.037, 746.880 pt.

Generated checked-state marker must use the **geometric/optical center of the exact source checkbox rectangle**, not a guessed center.

## Custom v2

Frozen Extended authority: Run 7.

Key regions:

| Region | X | Y/top | W | H |
| --- | ---: | ---: | ---: | ---: |
| Page title | 126 | 28 | 472 | 34 |
| Traits: Clase / Dotes | 14 | 98 | 277 | 20 |
| Traits: Raza / Trasfondo / Otros | 307 | 98 | 291 | 20 |
| Traits left rows start | 14 | 137 | 277 | 17 cadence |
| Traits right rows start | 307 | 137 | 291 | 17 cadence |
| Inventory Equipment continuation heading | 14 | 97 | 411 | 20 |
| Inventory Treasure/Others heading | 431 | 97 | 167 | 20 |
| Inventory Special Equipment heading | 14 | 489 | 584 | 20 |

Typography:

- frozen title/subtitle equivalents use source Corbel-Bold/Corbel with the Run-7 measured horizontal transforms;
- body/value roles use the approved Fira Sans roles;
- no silent title/subtitle fallback to a visually different font.

Header/logo:

- logo remains source-derived/vector in STRUCTURE;
- artifact correction must not flatten the complete header;
- hidden/off-crop source content must be bounded so viewers cannot reveal logo artifacts.

## Tolerances

Dynamic text-anchor audit uses:

- centered-section X center: +/- 4 pt unless a narrower source measurement exists;
- Y/top band: the text must remain inside the intended heading band with no cross-band drift;
- source checkbox center: +/- 1.5 pt on each axis for the generated marker;
- ruled text: baseline must remain above its own physical rule and within that row.

Any tolerance change is a contract change and must be recorded in the iteration ledger.

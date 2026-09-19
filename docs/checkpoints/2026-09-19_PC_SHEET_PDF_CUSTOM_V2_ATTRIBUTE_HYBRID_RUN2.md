# Checkpoint — Custom v2 per-Attribute Hybrid Run 2

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Visual family:** Custom v2 — per Attribute  
**Strategy:** Strategy 1 — Hybrid  
**Run-2 correction commit:** `6ff6dfe7cb337e0edea5097448f8e5826ff616a4`  
**Scaffold:** `35467797642` / run #2731 — SUCCESS

## Purpose

Apply only the local defects found during the independent Run-1 audit while preserving every successful region unchanged.

Run-1 findings:

1. the QA stick figure extended into the lower portrait/name banner;
2. the handwritten character name overlapped the central crown ornament;
3. the first Historia line overlapped the printed `HISTORIA` heading.

No strategy change was indicated.

## Run-2 changes

### Page 1

Portrait geometry was tightened vertically:

- stick figure remains in the upper portrait field;
- legs no longer enter the lower decorative/name banner.

Name placement was recalibrated:

- `Aster Vale` now sits inside the lower banner;
- text clears the central crown ornament.

No other Page-1 geometry was intentionally changed.

### Page 2

Historia now begins one physical source rule lower:

- the source rule adjacent to the printed `HISTORIA` heading remains empty;
- generated story content starts on the next physical rule.

No other Page-2 section was intentionally changed.

## Technical result

**PASS.**

Scaffold #2731 passed all jobs.

Generated review artifact:

`hybrid-custom-v2-attribute-run1-composite.pdf`

Note: the experimental test harness still retains the original Run-1 artifact filename even though the content is the Run-2 corrected draft. The commit/run identifiers above are the authoritative Run-2 identity.

Preflight:

- 4 pages;
- Letter 612 × 792 pt;
- openable;
- encrypted: false;
- likely scanned: false;
- XFA: false.

Font embedding remains protected by the existing Hybrid test assertion.

## Differential audit vs Run 1

Pixel diff at 200 DPI:

### Page 1
- changed: YES
- changed pixels: ~0.002359%
- diff bounding box confined to portrait/name region

### Page 2
- changed: YES
- changed pixels: ~0.010881%
- diff bounding box confined to Historia region

### Page 3
- changed: NO
- **0% pixel change**

### Page 4
- changed: NO
- **0% pixel change**

This confirms Run 2 preserved all unrelated successful regions.

## Renderer parity

PDFium vs pdftoppm at 200 DPI:

- Page 1: ~0.1442% changed pixels
- Page 2: ~0.0860%
- Page 3: ~0.0812%
- Page 4: ~0.0791%

Visual inspection shows these as ordinary antialiasing/render-engine differences, not structural placement divergence.

## Independent visual audit

### Page 1 — PASS FOR OWNER REVIEW

Successful:

- portrait figure stays entirely in the upper portrait field;
- handwritten name sits cleanly inside the lower banner and clears the crown ornament;
- Identification remains readable/aligned;
- Attribute scores/modifiers remain stable;
- saving-throw / skill checks remain inside their source boxes;
- skill numeric values remain aligned to physical rules;
- core stats remain readable;
- attacks remain aligned across the three columns;
- Traits remain stable;
- spell-slot totals remain clear;
- `ESPACIOS GASTADOS` remains empty;
- spellcasting summary remains readable;
- Treasure / Objects / Other remain aligned.

### Page 2 — PASS FOR OWNER REVIEW

Successful:

- Equipment columns remain aligned;
- Background / Bonds / Ideals remain stable;
- Historia now begins below the printed heading with clean rule clearance;
- Special Equipment names/descriptions remain aligned;
- v8 state checks remain inside source squares.

### Page 3 — PASS FOR OWNER REVIEW

Unchanged from Run 1.

- cantrips have no preparation checks;
- spell levels 1–9 use the same Hybrid strategy;
- preparation checks align;
- names clear their rules;
- slot totals remain readable;
- `ESPACIOS GASTADOS` remains blank.

### Page 4 — PASS FOR OWNER REVIEW

Unchanged from Run 1.

- both Notes columns use source-rule geometry;
- typography remains readable;
- text clears physical rules;
- QA doodles remain independent vector content.

## Run-2 conclusion

**CUSTOM V2 — PER ATTRIBUTE — HYBRID RUN 2: PASS FOR OWNER REVIEW**

No strategy switch is needed.

The complete four-page per-Attribute variant is now ready for the mandatory owner visual gate defined by D-0074.

Shared pages 3–5 of the source template are also in a strong state and should be reused unchanged for the per-Ability variant unless owner review identifies a shared-page issue.

## Next gate

Owner review of the complete populated per-Attribute v2 draft.

If approved:

1. freeze the per-Attribute first-page baseline;
2. preserve the shared pages as the v2 common baseline;
3. implement the per-Ability alternative first page using the already measured source-page-2 geometry;
4. audit the resulting four-page per-Ability draft;
5. request the separate owner approval required by D-0074.

PR #85 remains **DRAFT / DO NOT MERGE**.

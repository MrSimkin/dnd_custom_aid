# Checkpoint — Custom v2 per-Attribute Hybrid Run 1

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Visual family:** Custom v2 — per Attribute  
**Strategy:** Strategy 1 — Hybrid  
**Implementation commit:** `09108bbbd84210d753ef580516c9db4616025b5d`  
**Scaffold:** `35467412619` / run #2725 — SUCCESS

## Scope

First complete populated Custom-v2 per-Attribute Hybrid draft.

Normal four-page v2 export structure:

1. source page 1 — per Attribute;
2. source page 3 — shared Equipment/Narrative;
3. source page 4 — shared Spells;
4. source page 5 — shared Notes.

Source page 2 is intentionally excluded because it is the alternative per-Ability first page.

## Method

Run 1 reuses the approved Custom-v1 Hybrid mechanics:

- authoritative owner source PDF;
- native PDF-point geometry;
- source geometry measured from the exact 2 px = 1 pt source render;
- independent semantic Form XObject / OCG layers;
- Fira Sans / Kalam / frozen v8 symbol roles;
- metric-based ruled text;
- metric-based marker fitting;
- overlay-only diagnostic artifact;
- no legacy-rendered regions in the review draft.

## Technical result

**PASS.**

Scaffold #2725 passed.

Generated artifact:

`hybrid-custom-v2-attribute-run1-composite.pdf`

Diagnostic:

`hybrid-custom-v2-attribute-run1-overlay-only.pdf`

Preflight:

- 4 pages;
- Letter 612 × 792 pt;
- openable;
- not encrypted;
- not scanned;
- no XFA.

Section isolation:

- at least 25 semantic OCG/Form layers asserted.

Fonts:

- generated Hybrid fonts pass the existing embedding guard.

Renderer parity:

- verified with PDFium and pdftoppm;
- per-page changed-pixel percentages are approximately 0.09–0.17%, consistent with antialiasing/render-engine differences;
- no structural placement divergence observed.

## Independent visual audit

### Page 1 — per Attribute

**STRONG PARTIAL PASS.**

Successful:

- Identification handwriting is readable and aligned.
- Attribute scores/modifiers fit the source containers.
- saving-throw / skill checks use the v8 marker and remain inside source boxes.
- skill numeric values fit the physical right-side rules.
- core stat values are readable.
- attacks align cleanly across source columns.
- traits use the two-column ruled geometry cleanly.
- main-page spell-slot totals are clear.
- `ESPACIOS GASTADOS` remains empty.
- spellcasting summary is readable.
- Treasure / Objects / Other rows align well.
- QA stick figure stays inside the portrait frame.

Independent defect:

1. **Portrait name Y placement**
   - `Aster Vale` currently sits over the central lower portrait ornament/crown.
   - This is a local Y-position defect.
   - The handwriting role/font itself is acceptable.
   - Run 2 should move the name upward into the clear lower portrait/name area without changing portrait geometry.

### Page 2 — shared Equipment/Narrative

**STRONG PARTIAL PASS.**

Successful:

- both Equipment columns align with source rules;
- Background, Bonds and Ideals use the physical ruled-paragraph strategy successfully;
- Special Equipment name/description columns align;
- special-equipment v8 checks fit their source squares;
- typography and readability are consistent with the approved Hybrid direction.

Independent defect:

2. **Historia first line**
   - the first generated story line uses the rule directly adjacent to the printed `HISTORIA` heading;
   - its baseline overlaps the heading.
   - Run 2 should leave that first source rule empty and begin generated Historia content on the next physical rule.

### Page 3 — shared Spells

**PASS at Run-1 level.**

- cantrips use no preparation marks;
- levels 1–9 use one consistent Hybrid strategy;
- preparation checks remain inside source squares;
- spell names sit cleanly above physical rules;
- slot totals are readable;
- `ESPACIOS GASTADOS` is blank at every level;
- no visible cross-column drift.

### Page 4 — shared Notes

**PASS at Run-1 level.**

- both note columns use the source rule origins;
- 9.25-pt Fira direction remains readable;
- text clears the physical lines;
- QA doodles remain independent vector content inside the grid.

## Run-1 conclusion

**TECHNICAL PASS / STRONG VISUAL PARTIAL PASS.**

No strategy change is indicated.

Only two local defects were found in the independent audit:

1. portrait-name Y position;
2. Historia first-line Y position.

## Run 2 scope

Preserve every successful Run-1 region unchanged.

Change only:

- portrait-name Y placement on first page;
- Historia content start rule on shared page 3.

After generation:

- independently re-audit all four pages;
- perform a visual diff against Run 1;
- require changes to stay localized to those two regions.

PR #85 remains **DRAFT / DO NOT MERGE**.

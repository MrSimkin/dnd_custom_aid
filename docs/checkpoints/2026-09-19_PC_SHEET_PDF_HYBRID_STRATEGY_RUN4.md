# Checkpoint — PC Sheet PDF Hybrid Strategy 1 / Run 4

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Strategy:** 1 — Hybrid authoritative-template + native-geometry + separate vector Form/OCG overlay  
**Iteration:** Run 4 — generalization pass after the three-run strategy trial  
**Run-4 code head:** `8ce19863a533dbec80c5288f8269c0f80ced4ac9`  
**Scaffold:** `35454577498` / run #2655 — SUCCESS

## Why Run 4 exists

The owner reported no new visual corrections after Run 3 and asked to continue.

Therefore Run 4 was driven by the independent Run-3 audit rather than by a new owner defect list.

The goal was **generalization**, not new typography or marker tuning:

- preserve successful Run-3 fonts, sizes, embedding and marker mathematics;
- replace isolated coordinate calls with reusable native-PDF-point geometry structures;
- exercise repeated rows/markers and width-based wrapping;
- determine whether the Hybrid approach remains stable when content density increases.

## New experimental abstractions

Run 4 introduced test-only geometry concepts:

- `RuleAnchor(startX, endX, ruleY)`;
- `RuleSeries(startX, endX, ruleY[], leftPadding)`;
- `TopRect` marker/box targets;
- width-based `fitOnRule`;
- font-metric `wrapByWidth`.

The measured source PDF provides the geometry.

Representative measured examples include:

### Main attack rules

Five source rules:

- 444.689 pt;
- 464.531 pt;
- 484.374 pt;
- 504.217 pt;
- 524.059 pt.

Source column spans:

- attack: 215.291..354.189;
- range: 357.024..413.717;
- bonus: 416.551..461.905;
- damage: 460.968..583.803.

### Narrative rules

Right-column story rules use:

- x 215.291..583.795;
- y 387.996, 407.839, 427.681, 447.524, 467.366, 487.209, 507.051, 526.894, 546.736.

### Main spell-slot ovals

The first row uses the authoritative four source oval rectangles:

- x 80.646..94.710;
- x 95.318..109.382;
- x 109.990..124.054;
- x 124.662..138.726;

all at y 442.688..460.560.

### Spell-list prepared squares

Five level-1 source squares use the measured rectangles beginning at:

- y 327.187;
- 347.030;
- 366.872;
- 386.715;
- 406.557.

## Technical result

**PASS.**

Scaffold #2655 is fully green.

PDF preflight:

- opens correctly;
- 3 pages;
- not encrypted;
- not scanned;
- no XFA;
- no warnings.

Font preflight remains correct:

- source EnchantedLand / GillSansMT / Para-hj-de-pj — embedded;
- FiraSans-Regular — embedded=yes, subset=no;
- FiraSans-SemiBold — embedded=yes, subset=no;
- ParaHojadePJSymbolsV8-Regular — embedded=yes, subset=no.

Two-renderer parity was checked with PDFium and pdftoppm. Both render the same structures and the same visual defects described below. Pixel differences are small and dominated by renderer/antialiasing differences rather than structural disagreement.

## Independent visual audit

### 1. Five-row attack-table generalization

**PASS AT RUN-4 LEVEL.**

The five rows remain consistently aligned across all four columns.

Observed positives:

- no cumulative vertical drift;
- left starts remain inside the measured printed rules;
- 9.0–9.25 pt regular text remains readable;
- values with different widths (`5 ft`, `80/320 ft`, `1d4+3 perforante`) remain inside their measured columns;
- width-driven fitting did not force any tested row below the readability floor.

This validates the geometry-manifest idea for repeated ruled table rows.

### 2. Repeated main-page spell-slot ovals

**PASS.**

Two spent-slot markers were rendered into the first two of four authoritative oval targets.

They remain centered and consistently near-fill their printed containers.

The unspent third/fourth source ovals remain untouched.

This validates repeated use of the Run-3 metric-based marker primitive.

### 3. Narrative width-based wrapping

**HORIZONTAL / WRAPPING PASS; VERTICAL CHANGE REQUIRED.**

The story is wrapped from actual font width rather than character count.

Positives:

- six generated lines fit within the nine available source rules;
- line starts are stable at the measured right-column origin plus deliberate padding;
- long lines remain inside the source right edge;
- no horizontal overflow or artificial character-count wrapping is visible.

Independent visual concern:

- the generated text sits too close to the printed rule;
- at high-resolution inspection the rule nearly touches/intersects the lower glyph area;
- this is especially risky for descenders and for physical print.

This was less obvious in the sparse Run-3 proof and becomes clear when several adjacent lines are filled.

### 4. Repeated prepared/unprepared spell checks

**MARKER PASS.**

Prepared rows show v8 checks centered within the measured source squares.

Unprepared rows correctly leave the source square blank.

No row-to-row marker drift is visible.

### 5. Spell-name vertical placement

**FAIL.**

The spell names are visibly crossed by / too low against the printed writing rules.

The problem is not marker geometry and not font embedding.

Cause identified:

- Run 4 generalized the old `onRule(... H - (ruleTopY - 3))` placement;
- the spell fixture derived its text Y values indirectly from neighboring square coordinates;
- it did **not** use the actual horizontal rule Y as a semantic baseline target.

The dense spell sample exposes that `ruleY` in the current helper does not have one rigorous meaning.

### 6. Spell-list spent-slot markers

**FAIL — WRONG SEMANTIC GEOMETRY REUSE.**

Two black ovals appear inside the level-1 spell-list rows.

They do not belong there.

Cause:

- Run 4 reused `FIRST_SLOT_OVALS`, which are authoritative **MAIN-page Lanzamiento de Conjuros** oval rectangles;
- the same rectangles were incorrectly used while drawing the SPELL_LIST page.

This is an important generalization lesson: geometry must not only be accurate; it must be **semantically scoped to page/field identity** so correct geometry from one page cannot accidentally be applied to another.

### 7. `ESPACIOS` value

**PASS.**

The 15 pt level-1 value remains clear and centered with no Run-3 regression.

## Run-4 result

**PARTIAL PASS / GENERALIZATION SUCCESS WITH TWO IMPORTANT DEFECT CLASSES EXPOSED.**

Run 4 strengthens confidence in the Hybrid strategy because:

- repeated table geometry is stable;
- width-based wrapping works;
- marker fitting repeats correctly;
- font embedding remains robust;
- denser data did not produce coordinate drift.

But Run 4 also demonstrates that production migration must add two safeguards before broad expansion:

1. rigorous metric-based ruled-text baseline semantics;
2. semantically typed/scoped geometry so page-A targets cannot be reused on page-B accidentally.

## Proposed Run 5

Do not broaden coverage further yet.

Run 5 should preserve the same Run-4 dense fixtures and change only these abstractions:

### A. Replace ambiguous `onRule` with a metric-based printed-rule primitive

Input should mean exactly:

- actual source horizontal rule Y;
- source start/end X;
- desired physical clearance above the rule.

Baseline should be derived from the font's real descent:

`baseline = ruleBottom + clearance - scaledDescent`

so the visible glyph bottom remains a controlled distance above the printed rule.

Use the actual source path Y values, including the spell-list lines, rather than square-derived pseudo-rule values.

### B. Semantically scope geometry

Replace generic shared lists such as `FIRST_SLOT_OVALS` with page/field-specific geometry identities, for example:

- `MainSpellcasting.level1SpentSlotTargets`;
- `SpellList.level1PreparedTargets`;
- `SpellList.level1NameRules`;
- `SpellList.level1HeaderSlotsBox`.

This should make the Run-4 cross-page oval mistake difficult or impossible to express.

### C. Re-run the same dense sample

Audit:

- attack-table regression;
- narrative rule clearance;
- spell-name rule clearance;
- prepared/unprepared checks;
- removal of the incorrect spell-list ovals;
- main-page oval regression;
- `ESPACIOS` regression;
- font embedding / two-renderer parity.

Only after this baseline/semantic-geometry gate passes should the Hybrid renderer expand to broader Custom v1 production migration.

## Current gate

- Hybrid Strategy 1 remains approved as the active candidate strategy.
- Run 4 exposed fixable abstraction defects, not a reason to abandon the strategy.
- PR #85 remains **DRAFT / DO NOT MERGE**.
- No complete Custom visual family is owner-approved.

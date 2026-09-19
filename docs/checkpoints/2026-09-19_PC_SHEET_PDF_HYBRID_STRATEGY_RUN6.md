# Checkpoint — PC Sheet PDF Hybrid Strategy 1 / Run 6

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Canonical protocol:** `docs/PC_SHEET_PDF_STRATEGY_RUN_PROTOCOL.md`  
**Run-6 code head:** `c1a92264adc3fe97888205118fd1e3175018415f`  
**Scaffold:** `35457560007` / run #2665 — SUCCESS

## Run 6 purpose

Run 6 follows the consolidated Strategy/Run MO and responds to the owner decision that subsequent runs should both:

1. repair known local defects; and
2. continue expanding field coverage.

Run 6 therefore:

- preserves the Run-5 section/OCG architecture;
- repairs skill checkbox/value geometry from measured source PDF coordinates;
- repairs Identification `Puntos de Experiencia` / `Siguiente Nivel` using actual source rules;
- adds more fields/sections;
- subdivides Skills into independent ability-column layers.

## Section isolation

Run 6 uses at least sixteen independent semantic OCG/Form layers:

Main page:
- Identification
- Defense
- Core Stats
- Abilities
- Skills STR
- Skills DEX
- Skills INT
- Skills WIS
- Skills CHA
- Spellcasting Summary
- Attacks
- Traits rows 1-2

Page 2:
- Background fields
- Story

Spell page:
- Cantrips
- Level 1

This further validates the owner-requested InDesign-like workflow.

## Source-geometry calibration performed

### Skill printed squares

Run 5 used estimated checkbox centers.

Run 6 instead measures the source `Para-hj-de-pj` square glyph bboxes directly from the authoritative PDF.

Representative measured rectangles:

- Athletics: x 23.035..32.704, y 324.386..336.673
- Acrobatics: x 119.413..129.082, y 324.353..336.639
- Arcana: x 312.169..321.838, y 324.353..336.639
- Medicine: x 408.547..418.216, y 324.353..336.639
- Deception: x 504.925..514.594, y 324.353..336.639

Each subsequent row has its own measured source rectangle.

### Skill numeric lines

Numeric values now use actual source line segments rather than generic value centers.

Representative segments:

- STR value line: x 85.398..108.075
- DEX value line: x 181.776..204.453
- INT value line: x 374.531..397.208
- WIS value line: x 470.909..493.586
- CHA value line: x 567.287..589.964

This resolves the Run-5 `Trato con Animales` / `+1` crowding.

### Identification rules

Measured source lines:

- Puntos de Experiencia: x 134.504..233.717 at y 113.035
- Siguiente Nivel: x 341.433..440.646 at y 113.035

Run 6 populates:

- `23.000 PX`
- `34.000 PX`

respectively.

## Added coverage

### Core Stats

Added:

- Initiative: +4
- Proficiency Bonus: +3
- Maximum HP: 34
- Current HP: 27
- Speed: 30
- Hit Dice: 4d6 / 1d8

**Visual result: PASS at Run-6 level.**

All six tested values are legible and convincingly centered inside their independent printed boxes.

**Coverage caveat:** `Inspiración` remains unpopulated in this run. The section is therefore not yet complete coverage of every printed Core Stats field.

### Spellcasting Summary

Added:

- Spell Save DC: 15
- Spell Attack Modifier: +7
- Spellcasting Ability: INT

**PASS at Run-6 level.**

All three values fit their ornamental boxes cleanly.

### Rasgos rows 1-2

Run 5 tested one row / three columns.

Run 6 adds a second row:

First row:
- Visión en la oscuridad
- Recuperación Arcana
- Ataque furtivo 1d6

Second row:
- Trance
- Acción astuta
- Erudito arcano

**PASS at Run-6 level.**

### Page-2 Historia

Adds a short `Historia del Personaje` sample on the right side using width-based wrapping and measured rule geometry.

**PASS at Run-6 sample level.**

The three populated lines remain inside the section width and maintain controlled clearance above the printed rules.

## Repair audit

### Habilidades — checks

**PASS.**

Prepared/proficient/expertise markers now sit inside the actual source squares.

The Run-5 left displacement is gone.

DEX and INT repeated trained rows show no visible row-to-row drift.

### Habilidades — values

**PASS.**

Values are centered on the real printed numeric line segments.

The previous long-label collision is resolved, including `Trato con Animales +1`.

### Identification — Siguiente Nivel

**PASS.**

`34.000 PX` now occupies the real `Siguiente Nivel` rule rather than overlapping the printed label.

### Identification — Puntos de Experiencia

**PASS / NEW COVERAGE.**

`23.000 PX` is placed on the measured source XP rule.

## Regression audit

### Defense

PASS.

### Attribute scores and modifiers

PASS.

### Handwritten portrait name

PASS.

### Attack table

PASS; five populated rows remain aligned.

### Spell-page semantics

PASS:

- cantrips have no preparation marks;
- level-1 prepared checks remain optically aligned;
- spell names remain above their printed rules;
- `ESPACIOS GASTADOS` remains empty;
- level-1 `ESPACIOS` total remains populated.

### Page-2 background fields

PASS:
- Trasfondo
- Ideales
- Vínculos
- Defectos

## Technical / renderer verification

Scaffold #2665: **SUCCESS**

Preflight:

- 3 pages
- 612 x 792 pt
- openable
- not encrypted
- not scanned
- no XFA

Fonts:

- source EnchantedLand — embedded
- source GillSansMT — embedded
- source Para-hj-de-pj — embedded
- Kalam-Bold — embedded, non-subset
- FiraSans-SemiBold — embedded, non-subset
- FiraSans-Regular — embedded, non-subset
- ParaHojadePJSymbolsV8-Regular — embedded, non-subset

Renderer parity:

- verified with PDFium and pdftoppm;
- parity differences are small and visually attributable to antialiasing/rendering differences;
- no structural placement divergence was observed.

## Run-6 conclusion

**STRONG PASS AT CURRENT EXPERIMENTAL COVERAGE.**

Run 6 materially strengthens the Hybrid strategy:

- exact source geometry repairs the remaining Run-5 skill/Identification defects;
- finer per-column section isolation is practical;
- expansion and repair can coexist without losing diagnostic control;
- newly added Core Stats, spellcasting summary, additional traits and story fields behave well;
- previously successful spell semantics and layout remain stable.

The remaining known coverage gap from this run is:

- Inspiration has not yet been populated/tested in the new Core Stats layer.

This is not a strategy failure.

## Recommended next run

Continue Strategy 1.

Run 7 may safely expand coverage again while preserving the repair discipline.

Suggested next targets:

- Inspiration marker in Core Stats;
- saving-throw checks + values using the same exact-source geometry method now proven for Skills;
- additional spell levels, maintaining:
  - no generated spent-slot marks;
  - cantrip no-preparation semantics;
- more page-2 `Historia` lines / `Otros Rasgos y Atributos`;
- selected page-2 Notes content;
- possibly more Rasgos rows.

Any new region should remain independently layered or subdivided when its geometry benefits from separate calibration.

PR #85 remains **DRAFT / DO NOT MERGE**.

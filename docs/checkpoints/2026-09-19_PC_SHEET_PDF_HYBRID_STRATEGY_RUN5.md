# Checkpoint — PC Sheet PDF Hybrid Strategy 1 / Run 5

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Strategy:** Hybrid authoritative-template + native geometry + independent Form/OCG section layers  
**Run-5 code head:** `77bf4ca2040c26018e3cce29f722ba17049b9b3a`  
**Scaffold:** `35456131470` / run #2659 — SUCCESS

## Owner inputs for Run 5

Spell-page corrections:

- prepared checks were slightly misaligned on X;
- `ESPACIOS GASTADOS` must always remain empty in generated sheets;
- spell names were strongly misaligned on Y;
- cantrips (`TRUCOS`) are always prepared and therefore must never show a preparation mark.

Owner also authorized adding any subset up to all of:

- Habilidades checks + values;
- Atributos scores + modifiers;
- handwritten character name below portrait;
- Defensa values;
- handwritten identification entries;
- Rasgos first row / three columns;
- page-2 Trasfondo / Ideales / Vínculos / Defectos.

Architecture gate from owner: continue only if page regions can be isolated like InDesign boxes/sections or through multiple independent layers.

## Architecture result

**PASS — STRONG.**

Run 5 explicitly renders nine independent PDF Form XObject / Optional Content Group layers:

Main page:
1. Identification
2. Defense
3. Abilities
4. Skills
5. Attacks regression
6. Traits row 1

Page 2:
7. Background fields

Spell page:
8. Cantrips
9. Level-1 spells

The saved PDF asserts the OCG count and keeps each section independently composited over the untouched authoritative template.

This confirms the Hybrid renderer can support an InDesign-like workflow where irregular sections have separate coordinate/alignment domains. There is no architectural reason to abandon Strategy 1.

## Technical result

**PASS.**

Scaffold #2659 is fully green.

PDF preflight:

- 3 pages;
- Letter 612x792 pt;
- no encryption;
- no AcroForm/XFA;
- source template preserved.

Font preflight:

- source EnchantedLand / GillSansMT / Para-hj-de-pj embedded;
- Kalam-Bold embedded=yes, subset=no;
- FiraSans-SemiBold embedded=yes, subset=no;
- FiraSans-Regular embedded=yes, subset=no;
- ParaHojadePJSymbolsV8-Regular embedded=yes, subset=no.

## Independent visual audit

### Spell page — owner corrections

**PASS AT RUN-5 LEVEL.**

- preparation checks are now optically corrected on X using a section-specific optical offset;
- spell-name Y placement now derives from the actual bottom edge of each authoritative source checkbox / printed rule instead of the old pseudo-rule Y;
- names sit clearly above the printed rule rather than being crossed by it;
- `ESPACIOS GASTADOS` is completely untouched / blank;
- cantrips are populated without any preparation check;
- level-1 `ESPACIOS` value remains clear at 15 pt.

This should become the export semantic contract:

- never populate spent-slot state in the PDF;
- never render preparation marks for cantrips.

### Handwritten name below portrait

**PASS.**

`Aster Vale` in Kalam is centered cleanly in the portrait banner and is visually distinct from ordinary generated text.

### Defense

**PASS AT RUN-5 LEVEL.**

CA, Mod. Destreza, Armadura, Escudo and Modificador are readable and align convincingly to their independent section geometry.

### Attributes and modifiers

**PASS AT RUN-5 LEVEL.**

All six scores and modifiers are legible and remain visually centered in their ornamental boxes.

### Habilidades — values

**MOSTLY PASS / ONE LOCAL ISSUE.**

Values are readable and generally align to their right-side value lines.

Detected issue:

- the Wisdom `Trato con Animales` row is long enough that the `+1` value visually crowds/overlaps the label.

Lesson: skill value anchors must use measured per-column/right-edge line geometry rather than one generic center assumption.

### Habilidades — checks

**FAIL / LOCAL GEOMETRY CALIBRATION NEEDED.**

Skill proficiency/expertise marks sit too far left and partly escape their printed squares.

Cause:

- unlike spell-page checks, Run 5 used approximate checkbox rectangles reconstructed from the old pseudo-coordinate centers;
- the v8 metric-fitting primitive itself is working;
- the **target rectangles are wrong**.

Next run should extract/measure the actual source skill-square rectangles and fit checks to those exact containers, just as the spell-page squares are handled.

This is a section-local calibration problem, not an architecture problem.

### Identification handwriting

**PARTIAL PASS.**

`Clase y Nivel`, `Raza`, and `Alineamiento` entries look convincing in the handwritten layer.

`Siguiente Nivel` **FAILS**: `24.000 PX` overlaps the printed label badly.

Cause: the fourth rule Y was estimated rather than measured from the authoritative PDF.

Because Identification is its own layer, the fix is isolated and does not affect the rest of the page.

### Rasgos first row / three columns

**PASS AT RUN-5 LEVEL.**

All three names fit cleanly over the first-row source rules with the larger regular text treatment.

### Page-2 Trasfondo-side fields

**PASS AT RUN-5 LEVEL.**

The populated first lines for:

- Trasfondo;
- Ideales;
- Vínculos;
- Defectos;

are readable, consistently placed and do not collide with the printed section headings/rules.

### Attack regression

**PASS.**

Run-4 attack alignment remains stable.

### Main-page spent-slot regression

**PASS.**

No generated spent-slot marks are present. The `ESPACIOS GASTADOS` source ovals remain empty.

## Run-5 conclusion

**STRONG PARTIAL VISUAL PASS + ARCHITECTURE PASS.**

The most important result is architectural:

> independent page-section layers work and should become a first-class design principle for Custom-sheet rendering.

The remaining failures are narrow and measurable:

1. skill checkbox rectangles need authoritative source measurement;
2. skill value lines need per-column geometry for long labels;
3. Identification `Siguiente Nivel` needs its real source rule Y.

No evidence from Run 5 supports changing renderer strategy.

## Proposed Run 6

Keep the same section architecture and same data.

Do not add another large set of sections yet.

Focus on precise calibration of the remaining local failures:

1. measure authoritative source rectangles for every skill checkbox used by the fixture;
2. measure each skill value line end/start and right-align or center values inside the actual available segment;
3. measure the true `Siguiente Nivel` rule and place the handwritten value there;
4. retain all Run-5 successful sections unchanged;
5. re-audit spell semantics, Defense, attributes, portrait name, traits, background fields and attacks for regression.

Potential refinement: split Skills further into one layer per ability column if that improves calibration/debugging. The section-layer architecture supports this without affecting other page regions.

## Current gate

- Hybrid Strategy 1 remains active and strengthened.
- Section isolation is approved at the experimental-architecture level.
- Run 5 is not a complete Custom-family approval.
- PR #85 remains **DRAFT / DO NOT MERGE**.

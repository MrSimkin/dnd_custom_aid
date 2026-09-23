# Checkpoint — PC Sheet PDF Hybrid Strategy 1 / Run 7 — Complete Five-Page All-Hybrid Custom v1 Draft

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Strategy:** Strategy 1 — Hybrid  
**Canonical protocol:** `docs/PC_SHEET_PDF_STRATEGY_RUN_PROTOCOL.md`  
**Final Run-7 code head before this checkpoint:** `69c605bc1ff27ece5cf67b6a7a1dbf8696479b9b`  
**Final Scaffold:** `35464998378` / run #2702 — SUCCESS

## Why Run 7 exists

Owner review of the first production-promotion draft correctly identified that the review artifact had mixed:

- proven Run-6 Hybrid sections; and
- legacy renderer sections / intentional production blanks.

That was a process error.

Run 7 restores the correct visual-development rule:

> A visual-approval draft must render every visible region under review with the active Hybrid section/layer strategy. Dummy QA data is allowed when product-domain values are not yet available. Production data mapping is a separate gate.

The Run-7 review artifact therefore contains **no legacy-rendered visual regions**.

## Owner corrections addressed

### Page 1

Added / restored in the Hybrid visual proof:

- Armadura
- Escudo
- Modificador
- Inspiración
- Alineamiento
- Siguiente Nivel
- stick-figure portrait

The portrait is drawn as its own independent OCG/Form layer.

Final portrait correction keeps the figure fully within the actual portrait interior and clear of the frame/banner boundaries.

### Page 2 — Equipo

All review content is rendered through Hybrid section layers:

- ordinary Equipo
- Monedas
- Gemas / Joyas / Arte
- Equipo Especial

These sections use:

- Fira / semibold typography roles consistent with the approved Hybrid direction;
- section-specific native-point geometry;
- actual ruled-row placement;
- v8 checks for special-equipment state.

No legacy Barlow/pseudo-pixel renderer is used in the Run-7 visual artifact.

### Page 3

Hybrid sections now include:

- Trasfondo
- Rasgos de Personalidad
- Ideales
- Vínculos
- Defectos
- Otros Rasgos y Atributos
- Historia del Personaje
- Notas

Rasgos de Personalidad uses the same multi-rule paragraph strategy as the other left-side sections.

Notas uses the same ruled-paragraph strategy as Historia.

Otros Rasgos y Atributos uses its own divided-row geometry with independent left/right column rules.

### Page 4

All spell levels use the Hybrid strategy:

- Trucos
- Levels 1–9

Every level uses:

- level/slot-total header geometry;
- prepared checks fitted with the frozen v8 font;
- the larger approved spell-name typography/baseline method;
- section-specific geometry.

Durable spell semantics remain:

- cantrips have no preparation marks;
- `ESPACIOS GASTADOS` is always empty.

### Page 5

Notes text now uses the same ruled-text / physical-line strategy established for page-1 Rasgos and other successful Hybrid ruled regions, adapted to page-5 left/right rule geometry.

The notes-page grid keeps simple QA doodles as separate vector content.

## Run history inside Run 7

### Initial Run-7 commit

`02dc40ff0793058b92203d8f133a99afd075e0b9`

Scaffold #2692 failed in the Run-7 test at the explicit minimum-readable-width guard.

This was **not** a strategy or architecture failure.

Cause:

- several new Equipment/Valuables QA labels were too verbose for a single physical rule at the approved minimum font size.

Correction:

- shorten dummy QA labels;
- preserve the typography floor;
- do not shrink text below the readable size merely to satisfy the fixture.

### First successful complete render

Commit:

`8540b57f610e046c2fc2013ca02994f5ec7a23bd`

Scaffold:

`35464595193` / run #2700 — SUCCESS

Independent render-first audit found one remaining defect:

- the QA stick figure crossed the left portrait frame and came too close to the banner boundary.

### Final portrait correction

Commit:

`69c605bc1ff27ece5cf67b6a7a1dbf8696479b9b`

Scaffold:

`35464998378` / run #2702 — SUCCESS

The figure was re-centered and reduced using the actual portrait interior rather than the earlier estimated portrait box.

## Technical verification

Final artifact:

`hybrid-strategy1-run7-composite.pdf`

Preflight:

- 5 pages
- Letter 612 × 792 pt
- openable
- not encrypted
- not scanned
- no XFA

CI assertions:

- five pages;
- at least 35 independent semantic OCG/Form layers;
- generated font embedding guard passes.

Renderer parity:

- verified with PDFium and pdftoppm;
- differences are small antialiasing/render-engine differences;
- no structural divergence observed.

## Targeted final regression diff

Compared final #2702 against successful #2700:

- changed pages: **1**
- page 1 only
- page-1 changed area: portrait region only
- page 1 changed pixels: ~0.0020%
- pages 2–5: **0% change**

This confirms the final correction did not regress the already-audited pages.

## Independent visual audit

### Page 1 — PASS at Run-7 visual-proof level

- Defense breakdown populated.
- Inspiration populated.
- Alignment populated.
- Next Level populated.
- stick figure fully inside portrait bounds.
- approved Run-6 attributes/skills/attacks/spellcasting/traits remain stable.

### Page 2 — PASS at Run-7 visual-proof level

- ordinary equipment uses readable Hybrid typography on physical rules;
- coins use compact prominent numeric typography;
- valuables use independent object/value geometry;
- special equipment uses clear checks + name + description row geometry.

No legacy review typography remains.

### Page 3 — PASS at Run-7 visual-proof level

- Personality follows the same multi-rule approach as Background/Ideals/Bonds/Flaws;
- Other Traits uses divided-row geometry;
- Story remains stable;
- Notes uses the same ruled paragraph strategy as Story.

### Page 4 — PASS at Run-7 visual-proof level

- all levels 0–9 use the same Hybrid visual approach;
- checks are consistent;
- names use the approved readable typography;
- spent-slot areas remain blank;
- cantrips remain unmarked.

### Page 5 — PASS at Run-7 visual-proof level

- all notes use the Hybrid ruled-text strategy;
- page is readable and visually consistent with the rest of the Custom-v1 draft;
- doodles remain independent vector QA content.

## Run-7 conclusion

**COMPLETE FIVE-PAGE ALL-HYBRID CUSTOM V1 VISUAL DRAFT: PASS FOR OWNER REVIEW**

This is the first review artifact in the current sequence where all five visible Custom-v1 pages use the active Hybrid visual strategy rather than a mixed Hybrid/legacy draft.

This does not itself close Custom-v1 production mapping or merge PR #85.

## Next gate

Owner review of the final Run-7 five-page all-Hybrid Custom-v1 draft.

If owner approves:

1. treat Run 7 as the complete Custom-v1 visual baseline;
2. migrate the full all-Hybrid visual geometry into production data mapping;
3. map unsupported QA-only values only when/if the product domain provides them;
4. run production regression against the approved Run-7 visual baseline;
5. only then consider Custom-v1 production closure / PR #85 next scope.

PR #85 remains **DRAFT / DO NOT MERGE**.

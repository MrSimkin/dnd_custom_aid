# Research — PC Sheet PDF Renderer Foundation

**Date:** 2026-09-17 (Chile local time)  
**Status:** RESEARCH CHECKPOINT — no visual approval implied  
**Related strategy:** `docs/checkpoints/2026-09-17_PC_SHEET_PDF_WHOLE_EXPORT_RENDERER_QA_STRATEGY.md`  
**Related QA rejection:** `docs/checkpoints/2026-09-17_WAVE7_PC_SHEET_PDF_MAIN_PAGE_VISUAL_QA_ROUND1_REJECTED.md`

## Scope

Focused research after owner rejection of the first populated MAIN-page proofs.

This checkpoint does **not** implement the renderer. It records findings that should shape the next renderer-foundation design before more visual mapping is attempted.

## 1. Whole-export survey confirms MAIN-page-only design is insufficient

The authoritative template renders show additional primitive classes that MAIN page 1 alone cannot validate.

### Custom v1

- page 1: identity, combat summary, attributes, saves/skills, spell slots, attacks, traits;
- page 2: equipment table, currency/wealth, repeated equipment rows, special-equipment checkbox rows;
- page 3: background/narrative sections, ideals/bonds/flaws, notes;
- page 4: spell list columns, spell-level grouping, slot markers/check rows, repeated dense rows;
- page 5: lined notes plus graph-paper area.

### Custom v2

- page 1: MAIN — per Attribute;
- page 2: MAIN — per Ability;
- page 3: combined equipment + narrative + special-equipment structures;
- page 4: spell list columns and repeated slot/check rows;
- page 5: lined notes plus graph-paper area.

### Consequence

The shared renderer must be designed for more than isolated text labels and numeric fields. At minimum it needs reusable primitives for:

- bounded single-line text;
- bounded multi-line/wrapped text;
- centered/right/left numeric values;
- repeated rows;
- tables/columns;
- check/proficiency/expertise markers;
- slot/counter markers;
- portrait frames;
- lined-note regions;
- dynamic/extended-page content;
- page/family-specific visual geometry.

The Classic D&D-style family should reuse those primitives rather than implement a second rendering engine.

## 2. Source-template typography was inspected

The rejected proof PDFs preserve each authoritative source page as the background, so their embedded source fonts can be inspected separately from the added Helvetica proof text.

### Custom v1 source fonts visible on MAIN page

- `EnchantedLand`
- `GillSansMT`
- `Para-hj-de-pj` — appears to be used as a symbol/marker font in parts of the template.

The first proof added:

- Helvetica
- Helvetica-Bold

### Custom v2 source fonts visible on both MAIN alternatives

- `Corbel`
- `Corbel-Bold`
- `Bahnschrift`

The first proof again added:

- Helvetica
- Helvetica-Bold

### Consequence

The owner's font criticism is supported by the source PDFs: Round 1 overlaid a generic Helvetica treatment onto templates that already have distinct family-specific typography.

The source fonts in the PDFs are **embedded subsets**, not a safe assumption for arbitrary generated user text. A subset can contain only the glyphs needed by the original template labels. The renderer therefore needs its own explicit font resources rather than trying to treat the template's embedded subsets as a complete runtime font library.

## 3. Existing repository font resources

Desktop currently bundles:

- `geist_vf.ttf` — Geist, SIL Open Font License 1.1;
- `mona_sans_condensed_vf.ttf` — Mona Sans Condensed, SIL Open Font License 1.1.

The repository deliberately does not bundle all named system fonts; exact system-installed families are exposed only when present locally.

### Consequence for PDF export

A deterministic offline PDF export should not depend on Corbel, Bahnschrift, Gill Sans or other OS-installed/proprietary fonts being present on the machine.

The renderer should use bundled, redistributable font resources for generated content.

The two existing bundled files are variable fonts. Before adopting them for PDF generation, explicitly validate PDFBox/FontBox behavior with these exact binaries. If variable-font behavior or weight selection is unreliable, prefer static OFL font instances for the PDF renderer.

Do not silently substitute a different font while calling it by another family name.

## 4. PDFBox version decision should happen before visual baselines

Current proof code uses PDFBox `2.0.37`.

Apache currently publishes:

- PDFBox `2.0.37` as the current 2.0.x feature/bugfix release;
- PDFBox `3.0.8` as the current 3.0.x feature release.

PDFBox 3 changes PDF loading APIs and has other rendering/font behavior changes. Apache's migration guide explicitly notes that PDF generation behavior can differ and that 3.x added/changed font shaping behavior.

### Consequence

Do **not** upgrade casually after owner-approved visual baselines exist.

Before building the shared renderer foundation, make an explicit technical choice:

- stay on 2.0.37 for this export package; or
- migrate the proof/foundation to 3.0.8 first and establish all subsequent visual baselines there.

Either option is viable; the important point is to settle the major version **before** structured visual QA creates golden expectations.

References:
- Apache PDFBox download page: https://pdfbox.apache.org/download
- Apache PDFBox 3 migration guide: https://pdfbox.apache.org/3.0/migration.html

## 5. Text measurement should be geometry-driven

PDFBox exposes font measurements that are more appropriate than the Round 1 hand-tuned offsets:

- `PDFont.getStringWidth(...)` for text advance width;
- `PDFontDescriptor.getAscent()`;
- `PDFontDescriptor.getDescent()`;
- `PDFontDescriptor.getCapHeight()`;
- font bounding boxes;
- vector-font glyph paths when actual glyph outline bounds are needed.

References:
- https://pdfbox.apache.org/docs/2.0.12/javadocs/org/apache/pdfbox/pdmodel/font/PDType0Font.html
- https://pdfbox.apache.org/docs/2.0.13/javadocs/org/apache/pdfbox/pdmodel/font/PDFontDescriptor.html

### Recommended renderer primitive

Introduce a reusable `TextBox` / `TextMetrics` abstraction rather than calls such as:

`textPx(x, y, text, fontSize, maxWidth)`

A text box should know:

- rectangle `x/y/width/height`;
- horizontal alignment: LEFT / CENTER / RIGHT;
- vertical alignment: BASELINE / VISUAL_CENTER / TOP / BOTTOM as needed;
- font role;
- preferred size;
- minimum readable size;
- wrap policy;
- overflow policy;
- padding;
- maximum lines;
- whether the field is numeric/sign-aware.

The renderer should calculate the baseline from font metrics and the box geometry.

For compact numeric fields, visual centering may need glyph/cap-height-aware metrics rather than treating the baseline itself as the vertical center.

## 6. Font roles should be semantic and family-aware

One global font is insufficient.

The renderer should expose semantic roles such as:

- CHARACTER_NAME;
- PRIMARY_VALUE;
- SECONDARY_VALUE;
- BODY;
- COMPACT_TABLE;
- NUMERIC_COMPACT;
- NOTE_TEXT;
- SPELL_NAME;
- OPTIONAL_DECORATIVE.

Each visual family/theme maps those roles to concrete font resources, weights and sizes.

This lets Custom v1, Custom v2 and Classic share rendering mechanics while preserving distinct visual character.

The final font-family selections remain subject to owner visual QA. This research does not approve Geist, Mona Sans, or any other candidate for a particular role.

## 7. Font embedding

For arbitrary generated text, PDFBox supports embedding TrueType fonts as `PDType0Font`, including subsetting.

Reference:
- https://pdfbox.apache.org/docs/2.0.9/javadocs/org/apache/pdfbox/pdmodel/font/PDType0Font.html

### Recommended direction

- bundle explicitly licensed font files required by the PDF renderer;
- load/embed them into each output document;
- subset when supported and appropriate;
- include accented Spanish and representative Unicode in tests;
- verify no missing-glyph substitutions or black boxes;
- keep font loading deterministic and offline.

## 8. Vector markers should not be text glyphs

PDFBox page content streams support native vector paths:

- `moveTo`;
- `lineTo`;
- `curveTo`;
- fill/stroke operations;
- forms/XObjects.

References:
- https://pdfbox.apache.org/docs/2.0.10/javadocs/org/apache/pdfbox/pdmodel/PDPageContentStream.html
- https://pdfbox.apache.org/docs/2.0.3/javadocs/org/apache/pdfbox/multipdf/LayerUtility.html

### Recommended marker approach

For simple semantic markers, prefer direct PDF vector geometry:

- empty circle;
- filled circle;
- ring/double ring;
- check;
- cross if a specific design actually calls for it;
- square/filled square;
- small pip/counter marker.

Repeated marker artwork can be cached as reusable form/XObject geometry where useful.

This avoids encoding semantics as arbitrary letters and makes marker appearance independent of the selected text font.

## 9. SVG should be optional, not the default marker mechanism

Apache Batik can parse/transcode SVG, and Apache FOP provides SVG-to-PDF support. FOP's PDF renderer converts SVG paths into PDF drawing operations.

References:
- https://xmlgraphics.apache.org/batik/using/transcoder.html
- https://xmlgraphics.apache.org/fop/dev/svg.html

### Consequence

Batik/FOP is technically viable for complex SVG art, but it introduces a much larger dependency path than needed for circles/checks/proficiency symbols.

Initial recommendation:

- direct PDF vector paths for simple UI/semantic markers;
- consider SVG/Batik/FOP only if future iconography is sufficiently complex to justify it.

Do not rasterize simple markers into PNGs.

## 10. Wrapping and fitting must follow D-0074

The renderer should use measured text width and line layout, not character-count guesses.

Recommended order for bounded text:

1. preferred font size;
2. wrap within the defined box;
3. moderate size reduction/condensation when allowed;
4. stop at the readability floor;
5. continue to the matching Extended Page / overflow structure.

No silent truncation, content deletion or cryptic abbreviation.

## 11. Dense dummy data is part of QA infrastructure

Round 1's sparse fixture was not enough to evaluate the renderer.

Create at least:

### A. Normal representative PC

Realistic values and moderate content.

### B. Dense/stress PC

Populate every canonical field that maps to the reviewed page and deliberately include:

- multiclass text;
- long race/background/class names;
- positive and negative modifiers;
- all save/skill states;
- proficiency and expertise;
- long attacks/damage text;
- all spell-slot levels relevant to the fixture;
- many traits;
- equipment/currency;
- narrative blocks;
- many spells;
- long names and descriptions;
- custom attributes/abilities where the selected mode exposes them;
- portrait.

### C. Edge-state fixture(s)

As needed for:

- missing portrait;
- empty writable regions;
- current vs permanent snapshot;
- no spellcasting;
- overflow into Extended pages.

The fixture should be deterministic so identical renderer code produces stable visual artifacts.

## 12. Visual regression should supplement, not replace, owner QA

Automated rendering can produce page PNGs through PDFBox and compare them against accepted baseline renders.

Recommended after owner approval begins:

- render each accepted QA artifact at a fixed DPI;
- retain accepted baseline images;
- run pixel/structural diffs with a small documented tolerance;
- fail CI on unexpected large visual changes;
- preserve owner visual QA as the authority for aesthetics.

A pixel diff proves change/no-change, not that the design is good.

## 13. Proposed renderer architecture to investigate next

A plausible shared structure is:

```text
PcSheetPdfRenderer
  ├─ PdfDocumentContext
  │    ├─ FontRegistry
  │    ├─ VectorMarkerRegistry
  │    └─ ResourceCache
  ├─ Layout primitives
  │    ├─ TextBox
  │    ├─ NumberBox
  │    ├─ Marker
  │    ├─ RepeatedRow
  │    ├─ Table
  │    ├─ WrappedBlock
  │    └─ PortraitFrame
  ├─ Family theme
  │    ├─ CustomV1Theme
  │    ├─ CustomV2Theme
  │    └─ ClassicTheme
  └─ Page mappings
       ├─ Custom v1 pages
       ├─ Custom v2 pages
       ├─ Classic pages
       └─ Extended / Spellbook pages
```

This is a research sketch, not an approved class hierarchy. Keep the actual implementation smaller if the same behavior can be expressed more simply.

## 14. Next technical research/implementation gates

Before new owner visual review:

1. settle PDFBox 2.0.37 vs 3.0.8;
2. validate candidate bundled font binaries with the selected PDFBox version;
3. choose initial family-specific font-role candidates;
4. implement and test `TextMetrics/TextBox`;
5. implement vector marker primitives;
6. implement wrapping/fitting with readability floors;
7. create normal + dense deterministic fixtures;
8. generate a **primitive QA sheet**, not another pseudo-final character page;
9. review those primitives with the owner;
10. then implement all pages/families using the accepted foundation.

## Research conclusion

The owner's proposed slower approach is technically justified.

The next best step is not to perfect page 1 by coordinate nudging. It is to stabilize the common rendering foundation against the requirements exposed by **all pages**, then perform structured QA incrementally.

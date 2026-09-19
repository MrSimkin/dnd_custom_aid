# PC Sheet PDF — owner visual filling contract

Date: 2026-09-18

Status: **OWNER-DIRECTED VISUAL CONTRACT / ACTIVE AUTHORITY FOR NEXT CUSTOM DRAFT PASS**

Branch at decision capture:

`wave7/pc-sheet-pdf-renderer-template-proof`

Renderer head immediately before this checkpoint:

`3b43d5be46dd21b5dedd358583a3dbc9ad88d4d2`

PR #85 remains **OPEN / DRAFT / DO NOT MERGE**.

## Why this checkpoint exists

The whole-sheet Custom v1 / Custom v2 development drafts are technically generating and Scaffold #2609 is green, but the owner rejected the drafts as a faithful visual filling of the sheets.

The remaining problem is not a font-v8 design problem and must not reopen the frozen symbol-font gate. The problem is that too much renderer behavior still treats the source PDF as a background with generic text boxes placed over it instead of treating the source PDF as the actual visual design that generated content must deliberately fill.

This checkpoint preserves the owner's feedback and the clarified rules before further renderer work.

## Confirmed defects in the current drafts

Across Custom v1, Custom v2 per Attribute and Custom v2 per Ability, defects are broadly similar:

- numbers/text inside printed boxes are often not visually centered horizontally and/or vertically;
- marker choice/shape/size does not always correspond closely enough to the printed container or intended marked element;
- some important numeric values are too small relative to the box that contains them;
- attacks, traits and similar ruled regions do not respect the actual printed writing lines: text may start too low, skip lines or treat separated line groups as one generic block;
- handwritten typography is missing where it is visually/logically appropriate;
- portrait areas are not represented in the development drafts;
- generated text sizing does not consistently fit the visual importance and physical size of the field;
- ordinary filled-in text currently reads too much like a generic Helvetica/Arial-adjacent computer overlay;
- QA sample coverage is insufficient in several areas, especially Custom v1 notes/grid content, money, ammunition/resources, other objects and alignment-sensitive fields.

These drafts remain development evidence only. **No Custom visual family is owner-approved.**

## Owner-approved visual rules

### 1. Optical alignment, not blind mathematical centering

For values inside printed boxes/shapes, prioritize what looks centered to the human eye.

A mathematically centered string may still look vertically or horizontally wrong because glyph shapes are asymmetric. Small manual per-field offsets are allowed and expected when needed.

### 2. The box guides value size

The physical box and the importance of the value determine its text size.

- large/important field -> use the available space confidently;
- compact field -> use a correspondingly smaller size;
- do not use one conservative shared size merely for implementation convenience;
- values must remain comfortably clear of borders/ornaments.

### 3. Filled content must be visibly distinct from the preprinted sheet

Generated/fill-in text should complement the source sheet but remain noticeably separate from the original printed artwork.

Avoid a generic Helvetica/Arial-adjacent appearance for normal filled fields. Choose typography that fits the sheet while clearly reading as entered/generated character data.

This is a renderer typography choice. It does **not** authorize modification of Para Hoja de PJ Symbols v8.

### 4. Handwritten typography has a narrow role

Use handwritten/script typography only for:

- character name;
- player name;
- the text/name below the portrait.

Do **not** spread handwriting to equipment, attacks, traits, spells, narrative prose or ordinary table fields.

### 5. Printed marker/container geometry is the visual authority

Marker rendering must follow the actual printed field.

- choose the most visually corresponding approved v8 glyph for the context;
- adjust glyph size and optical position to fit the printed container;
- where necessary, the renderer may visually cover/replace the preprinted marker/container while preserving its intended location and overall size;
- do not replace v8 with literal `"X"` or temporary vector-marker substitutes;
- proficiency remains single check;
- expertise/pericia remains stacked double-check;
- v8 remains the only symbol-marker font.

### 6. Ruled regions are individual writing positions, not generic text boxes

Attacks, traits, equipment-description rows and similar lined areas must explicitly map to the source sheet's real writing lines.

The renderer must:

- align the baseline to each actual printed writing line;
- use the template's actual line spacing;
- treat visible larger gaps/breaks as separate groups/elements;
- not vertically center one paragraph over an entire ruled region;
- not skip every second line simply because a generic textbox line-height differs from the printed sheet.

The source's line/group structure is authoritative.

### 7. Portraits in this draft are development placeholders

Add a clearly visible development placeholder portrait to exercise:

- placement;
- available frame/area;
- scaling;
- crop/fit behavior;
- interaction with surrounding artwork.

Do not prematurely design the final portrait workflow. Portrait work will receive deeper treatment later.

### 8. Notes/grid QA should exercise non-text content lightly

Because this is a development visual stress test, include one or two simple doodles in the square-notebook/grid-like notes region.

The purpose is to prove placement and coexistence with text, not to design a full drawing feature yet.

### 9. Dense QA coverage is intentional

The development proof should intentionally populate nearly every meaningful field the current model/renderer can represent, even where a realistic character would leave fields blank.

Exercise, where supported:

- all money denominations;
- ammunition/resources/counters;
- ordinary and special equipment;
- valuables;
- attacks;
- traits/features;
- narrative regions;
- spell slots;
- prepared/unprepared spells;
- marker variants;
- portrait placeholder;
- notes/grid content;
- short, medium and long strings;
- alignment-sensitive numeric boxes.

Blank space should remain blank only when blank behavior itself is being intentionally tested.

## Core mental model

The source PDF is not merely a background.

The desired behavior is:

> **This PDF is the design. Generated content must look as though it was made specifically to fill this exact sheet.**

Therefore the renderer must interpret the sheet's visual grammar:

- box size and importance;
- individual ruled lines;
- line-group breaks;
- printed marker/container shapes;
- decorative headings and ornaments;
- portrait frames;
- field-specific typography roles;
- visual hierarchy.

The goal is not merely to avoid overlap. The filled PDF should look like the blank template was designed for exactly this generated content.

## Frozen font rule

Para Hoja de PJ Symbols v8 remains:

**OWNER APPROVED / FROZEN / FONT GATE CLOSED**

TTF SHA-256:

`f8f7eeed331be34ea08da23bb165f200c8313e1ba670667466ad8e1aebf77d8b`

Do not modify v8. Do not create v9 unless the owner explicitly asks to change the font artwork itself.

Renderer-level glyph selection, size and placement corrections are allowed.

## Next renderer pass

The next pass should be a systematic visual-mapping pass over the existing Custom drafts, not another isolated checkbox/spell-coordinate tweak and not a large architecture rewrite.

It should:

1. inventory visible fields/ruled regions on the authoritative Custom templates;
2. map important boxed values using optical alignment and box-driven sizing;
3. map ruled regions line-by-line/group-by-group;
4. choose/size/position v8 markers against the actual printed geometry;
5. replace generic ordinary fill typography with a more appropriate non-generic fill treatment while keeping handwriting narrow;
6. add development portrait placeholders;
7. expand dense proof data, including money/resources/objects;
8. add one or two simple notes-grid doodles;
9. regenerate all three Custom whole-sheet families;
10. inspect fresh renders section-by-section and page-by-page;
11. keep PR #85 DRAFT / DO NOT MERGE.

Green CI remains necessary but does not constitute owner visual approval.

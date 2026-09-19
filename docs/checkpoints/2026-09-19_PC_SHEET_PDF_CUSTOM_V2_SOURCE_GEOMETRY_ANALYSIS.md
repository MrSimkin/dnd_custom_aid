# Checkpoint — Custom v2 Source Geometry Analysis

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Template:** `assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf`  
**Active strategy:** Strategy 1 — Hybrid

## Purpose

Persist the source-geometry findings used to start the Custom-v2 Hybrid implementation so later runs and the per-Ability variant do not depend on chat memory.

## Render authority

The source-render artifact was revalidated before implementation.

Rendered v2 pages are exactly:

- 1224 × 1584 px
- source PDF page size: 612 × 792 pt
- therefore: **2 rendered pixels = 1 PDF point**

This gives a direct, deterministic conversion from measured source-render geometry to native PDF-point geometry.

The rendered source remains the visual authority and exact source rules / printed boxes are preferred over estimated centers.

## Page-family structure

The source PDF has five physical pages.

Normal Custom-v2 exports use four:

### v2 — per Attribute
- source page 1
- source page 3
- source page 4
- source page 5

### v2 — per Ability
- source page 2
- source page 3
- source page 4
- source page 5

Source pages 1 and 2 are alternatives, not cumulative pages.

## Shared-page finding

Pages 3–5 are genuinely shared by both v2 variants.

They should be calibrated once and reused unchanged unless a later owner observation requires a shared-page correction.

This is a key efficiency rule for the v2 phase.

## Source page 1 — per Attribute

The left column groups each official Attribute with:

- score box;
- modifier oval;
- saving throw;
- governed skills.

The right / center regions include:

- identification;
- portrait;
- proficiency / inspiration;
- AC / initiative / speed;
- hit dice / max HP / current HP;
- attacks;
- traits and attributes;
- spell-slot totals;
- spellcasting summary;
- treasure / objects;
- ammunition;
- other items.

Measured examples:

### Identification rules
- Clase y Nivel: x `442.5 .. 598.0`, y `46.5`
- Raza: x `388.5 .. 598.0`, y `67.5`
- Alineamiento: x `445.5 .. 598.0`, y `89.0`

### Attack table
Eight physical rows:

`264.5, 281.5, 298.5, 315.5, 332.5, 349.5, 366.5, 383.5`

Columns:

- Arma / Conjuro: x `184.5 .. 408.0`
- Bonificador: x `411.5 .. 453.5`
- Daño / Tipo de Daño: x `456.5 .. 598.0`

### Rasgos y Atributos
Nine physical rows:

`437.5 .. 573.5` in 17-pt increments.

Columns:

- left: x `184.5 .. 388.0`
- right: x `394.5 .. 598.0`

### Bottom rows
Spell-slot total rules:

- x `28.5 .. 56.5`
- levels 1–9 at y `627.5 .. 763.5`, 17-pt rhythm.

Treasure / object / other areas also use the same stable 17-pt row rhythm.

## Source page 2 — per Ability

Most center/right geometry is shared with source page 1.

The substantive alternative is the left-side statistics layout:

- six official Attribute score/modifier boxes remain;
- saving throws become one independent section;
- skills become one centralized independent section;
- skill labels include their governing Attribute abbreviation.

This means the per-Ability implementation should reuse:

- identification;
- portrait;
- core stats;
- attacks;
- traits;
- bottom spell/treasure/other geometry;
- all shared pages 3–5.

Only the left statistics area should require major new calibration.

## Source page 3 — shared Equipment / Narrative

### Equipment

Two columns:

- left: x `14.0 .. 149.5`
- right: x `156.0 .. 291.5`

Rows begin at y `114.5` and repeat every 17 pt through the section.

### Narrative right side

All right-side ruled fields use:

- x `297.5 .. 597.5`

Background:
- y `114.5, 131.5, 148.5`

Bonds:
- y `182.5, 199.5, 216.5`

Ideals:
- y `250.5, 267.5, 284.5`

Story:
- y `301.5 .. 488.5` in 17-pt increments.

### Special Equipment

Rows:
- first text rule y `542.5`
- repeat every 17 pt.

Name:
- x `99.0 .. 297.0`

Description:
- x `303.0 .. 596.0`

Printed state squares:
- x `87.5 .. 96.0`
- 8.5 × 9 pt source rectangle
- first top y `530.5`
- repeat every 17 pt.

## Source page 4 — shared spell page

Three physical columns:

- left spell text: approximately x `14/25.5 .. 203.5`
- middle: x `221.0 .. 399.0`
- right: x `416.5 .. 594.5`

Prepared-state source squares:

- left levels: x `14.0`
- middle levels: x `209.5`
- right levels: x `405.0`
- source square: 8.5 × 8.5 pt.

All spell-name rows use a 17-pt vertical rhythm.

Durable semantics carried from Custom v1:

- cantrips / Trucos receive no preparation checks;
- `ESPACIOS GASTADOS` is never populated;
- slot totals may be populated.

## Source page 5 — shared Notes

The v2 Notes page uses the same core physical strategy already proven for Custom v1:

Left rules:
- x `14.0 .. 302.5`

Right rules:
- x `309.0 .. 597.5`

Rows:
- begin y `104.0`
- repeat every 17 pt.

This page should therefore be a straightforward reuse of the approved Hybrid ruled-text mechanics with v2 source coordinates.

## Implementation consequence

Custom v2 does **not** require new strategy research.

The expected work is:

1. source-specific geometry map;
2. same approved Hybrid mechanics;
3. populated render;
4. independent audit;
5. bounded local calibration;
6. explicit owner approval.

## Current implementation

First full v2 / per-Attribute Hybrid draft:

`desktopApp/src/test/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopPcSheetCustomV2AttributeHybridRun1Test.kt`

Initial implementation commit:

`659e6e5737a273ea7eb450f8c279175f9c76f2ac`

The run is subject to the mandatory post-generation independent visual audit before any owner-facing conclusion.

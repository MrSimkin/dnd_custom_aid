# Checkpoint — Custom v1 Extended Run 6 → Custom v2 Extended visual mapping

**Date:** 2026-09-20 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Status:** DESIGN MAPPING COMPLETE — implementation may proceed

## Purpose

This checkpoint records the mandatory page-by-page translation from the OWNER APPROVED Custom-v1 Extended Run 6 precedent into the already-frozen Custom-v2 visual language.

The rule is not to invent a new appendix style. Custom-v1 Run 6 supplies the proven Extended-page construction logic; the frozen Custom-v2 sheets supply the geometry, typography, cadence, fills and marker language.

## Evidence inspected before implementation

- `docs/PC_SHEET_CUSTOM_EXTENDED_STRATEGY.md`;
- `docs/PC_SHEET_CUSTOM_V1_APPROVED_BASELINE.md`;
- Custom-v1 Extended Run-6 OWNER APPROVED checkpoint and implementation;
- the exact approved Run-6 PDF/renders;
- `docs/PC_SHEET_CUSTOM_V2_APPROVED_BASELINES.md`;
- per-Attribute Run-4 and per-Ability Run-2 OWNER APPROVED checkpoints;
- both frozen v2 rendered baselines;
- `2026-09-19_PC_SHEET_PDF_CUSTOM_V2_SOURCE_GEOMETRY_ANALYSIS.md`;
- Para Hoja de PJ Symbols v8 and its GUIDE.

## Global translation

| Proven Run-6 construction | Custom-v2 equivalent |
| --- | --- |
| Dense source-family rhythm rather than filling empty height | v2's native ~17 pt ruled cadence |
| Alternating structural fills | v2 gray/white bands measured from the source |
| Authentic score/modifier donor fragments | v2 combined Attribute score-frame + modifier-oval fragment |
| Compact source-matched labels | full-glyph Fira Sans calibrated to v2 dimensions; source subset fonts only for safe exact source text |
| Family-specific proficiency/expertise vocabulary | Symbols v8 v3-derived square grammar: outline / boxed check / boxed stacked double-check |
| Compact resources/options instead of oversized panels | 17 pt rows; square counters for maxima <10, X/Y when a counter no longer fits reasonably |
| Equipment / special-equipment continuation | v2 Equipment + Equipo Especial row grammar, one state square per special item |
| Spells/Notes reuse instead of redesign | near-literal reuse of v2 source pages 4/5 |

Every evaluation page keeps five real OCG layers: STRUCTURE → CLEANUP → LABELS → VALUES → MARKERS. Cleanup must remain empty unless a bounded source interior genuinely needs occlusion.

## Page mapping

### 1. Custom Statistics — per Attribute

Keep the Run-6 relationship model **Attribute → Tirada de Salvación → linked Abilities**, but draw it with authentic v2 score/modifier ornaments, v2 17 pt rows, gray/white cadence and v2 square training markers.

Custom Attributes use integrated three-letter keys, e.g. `HONor`, `VOLuntad`, `SUErte`.

Standard Attributes referenced by custom Abilities appear only as compact anchors. They must not become a second set of six full Attribute modules.

### 2. Custom Statistics — per Ability

Translate directly from the frozen v2 per-Ability first-page grammar:

**ATRIBUTOS | TIRADAS DE SALVACIÓN | HABILIDADES**

These remain three independent vertical regions. The Attribute region uses compact authentic v2 ornaments; saves and abilities use dense 17 pt rows and v2 square training states.

### 3. Traits & Features

Carry forward Run-6's useful density and category/detail separation, but express it through v2's Rasgos y Atributos / narrative ruled-row grammar and alternating bands.

Trait-local uses/max/recovery remain inside the trait when no independent CharacterResource exists. They must not be duplicated on Resources.

### 4. Resources & Options

Carry forward Run-6's compact table solution, but use the tighter v2 17 pt cadence and v2 square vocabulary.

The evaluation must visibly contain both:
- a small maximum represented by squares, e.g. 2/4;
- a larger maximum represented as X/Y, e.g. 7/12.

Options borrow the single-square Equipo Especial grammar.

### 5. Inventory / Equipment

Carry forward Run-6's dense Equipment / valuables / special-equipment continuation instead of a generic lined page.

Translate it to v2's gray/white 17 pt Equipment and Equipo Especial grammar. Each special-equipment row has only one square. Any `Sintonizado` state is textual, not a second icon.

### 6. Spells

Reuse the v2 spell page essentially intact. Preserve levels, rows and preparation squares. The continuation carries overflow spells only; `ESPACIOS GASTADOS` is visually occluded and is not turned into a second slot tracker.

### 7. Notes

Reuse the v2 Notes page essentially literally. Only continuation content is added.

## Run-5 defects this mapping supersedes

The previous evaluation established useful mechanics but still drifted visually:
- standard-Attribute anchors on per-Attribute were too large;
- the per-Ability Attribute region was under-dense;
- Traits was mostly generic black rules on white;
- Resources used oversized 34 pt rows;
- Inventory lacked the source-family gray/banded Equipment + Equipo Especial grammar.

Run 6 must correct those defects without changing the already-approved Custom-v1 or Custom-v2 base/shared baselines.

## Gate

After implementation: CI → exact artifact → PDF/render inspection → comparison against v1 Run 6 and frozen v2 → missing-glyph / clipping / overlap / terminology checks → two-renderer comparison where practical.

The maximum status before explicit owner approval is **PASS FOR OWNER REVIEW**.

Never mark this work OWNER APPROVED automatically. PR #85 remains **DRAFT / DO NOT MERGE**.

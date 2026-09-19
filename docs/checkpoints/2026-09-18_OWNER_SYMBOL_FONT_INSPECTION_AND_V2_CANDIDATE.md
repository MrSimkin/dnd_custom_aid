# Research — Owner Symbol Font Inspection and v2 Candidate

**Date:** 2026-09-18 (Chile local time)  
**Status:** LOCAL FONT INSPECTION COMPLETE / V2 CANDIDATE GENERATED / OWNER QA PENDING  
**Input asset:** owner-provided `Para Hj De Pj.ttf`  
**Repository policy:** font binary is **not committed** while repo is public.

## Ownership/provenance

The owner confirmed that he created the original `Para-hoja-de-pj` font.

Original SHA-256:

`d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`

## Inspection findings

The original TTF:

- reports family name `Para hj de pj`;
- reports version `Version 1.00 January 13, 2007, initial release`;
- uses 2048 units per em;
- contains 653 glyph slots, but almost all inherited slots are empty;
- visibly drawn owner symbols are concentrated in:
  - `A/a` — round outline;
  - `B/b` — square outline;
  - `C/c` — narrow oval outline;
  - `D/d` — narrow oval filled;
  - `E/e` — square filled;
- also contains the normal `.notdef` / underscore outlines;
- includes several inherited PUA/cmap entries such as `F001/F002/F004/F005`, but those glyphs are empty and should not be relied upon as the new renderer namespace.

This confirms the original sheet already used a purpose-built symbol-font strategy.

## v2 candidate

A local experimental expansion was generated without overwriting the original.

Candidate family:

`Para Hoja de PJ Symbols v2`

Candidate version:

`Version 2.00 2026-09-18; expanded renderer symbol set`

Candidate SHA-256:

`fb539e276861a164614cf1666482bd302a5e8efcbca20fff73051d42614e6295`

The candidate preserves all legacy `A-E/a-e` mappings.

### Generic PUA mapping

- `U+E000` round outline
- `U+E001` round filled
- `U+E002` double round outline
- `U+E003` exact legacy square outline
- `U+E004` exact legacy square filled
- `U+E005` check
- `U+E006` cross
- `U+E007` checked-state mark / check
- `U+E008` diamond outline
- `U+E009` diamond filled
- `U+E00A` exact legacy narrow oval outline
- `U+E00B` exact legacy narrow oval filled

### Semantic renderer aliases

- `U+E100` `PROFICIENT` → round filled
- `U+E101` `EXPERTISE` → double round outline
- `U+E102` `CHECKBOX_EMPTY` → legacy square outline
- `U+E103` `CHECKBOX_CHECKED` mark → check (compose with box in renderer)
- `U+E104` `SLOT_AVAILABLE` → round outline
- `U+E105` `SLOT_SPENT` → round filled
- `U+E106` `COUNTER_EMPTY` → round outline
- `U+E107` `COUNTER_FILLED` → round filled

Semantic PUA code points are rendering-layer conventions only. They must never become game/domain data.

## Design decision for checked boxes

The candidate intentionally keeps the square and check as independently composable glyphs instead of baking one fixed checked-square artwork.

Reason:

- preserves the owner's exact legacy square;
- lets QA tune check size/offset independently;
- supports multiple visual families with the same semantic marker;
- avoids locking one checkbox geometry too early.

## QA status

The following are **not yet approved**:

- stroke thickness;
- relative scale;
- check design;
- cross design;
- diamond design;
- double-circle expertise treatment;
- semantic choice of symbol for proficiency/expertise/slots.

These should be included in Primitive QA alongside direct-PDF-vector alternatives.

## Public-repo handling

No owner font binary or expanded TTF was committed.

The candidate can be regenerated from the owner's original with a deterministic local FontTools script. The generator/mapping can be kept outside Git until owner review, or committed later because it contains no proprietary third-party font binary.

## Continuation

During renderer-foundation work:

1. test the v2 candidate with the chosen PDFBox major version;
2. compare font-glyph markers against direct PDF vector markers;
3. include both options in Primitive QA where useful;
4. adjust/expand the font only from concrete renderer/QA requirements;
5. preserve backward compatibility with legacy A-E mappings.

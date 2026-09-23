# Checkpoint — Application-Owned Spellbook Production — PASS

**Date:** 2026-09-21 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Implementation head:** `ac0cbb2b26202bf934ac4926b56899014390613e`  
**Final Scaffold:** `35679451757` / run #3161 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10674370966`  
**Artifact digest:** `sha256:0713bd537f2abac361be4b37bc46e70041ed13bd4a03c334fb3cacfc8e0ced0c`

## Purpose

Close the D-0074 optional application-owned Spellbook production gate without reopening any frozen Classic, Custom-v1 or Custom-v2 visual family.

The Spellbook is implemented as one family-neutral printable appendix. The selected family renders first, including any matching Extended pages, and the Spellbook is appended afterward only when the export request asks for spell descriptions and attached spells exist.

## Product contract implemented

The existing shared `PcSheetPdfRenderPlan` remains the semantic authority.

Production now:

- includes only spells actually attached to the selected PC snapshot;
- groups index entries by spell level and alphabetizes within each level;
- provides an index with spell name, level and absolute PDF page number;
- preserves every recorded source relationship for a spell;
- prints source origin, prepared/not-prepared state, casting ability, save DC and spell-attack modifier where the model knows them;
- prints casting time, range, duration, V/S/M components, material text, ritual/concentration flags, complete stored description and notes;
- paginates long entries with explicit `(continuación)` headings rather than truncating them;
- remains application-owned and visually independent from Classic/v1/v2, as D-0074 permits;
- leaves exports unchanged when `includeSpellDescriptions = false`;
- preserves the existing planner notice instead of appending an empty Spellbook when descriptions are requested but the PC has no attached spells.

The current `CharacterSpell` model has no separate school/category or higher-level/scaling field. The renderer therefore prints all normal spell information the application actually stores and does not invent absent metadata.

## Append-only family preservation

The whole-sheet renderer first produces the already-approved family PDF with the Spellbook plan removed, reopens that completed PDF, and appends the common Spellbook.

This intentionally isolates the appendix from frozen family layout code.

Final proof comparison:

- baseline without Spellbook: 18 pages;
- same export with Spellbook: 21 pages;
- all first 18 pages were rendered at 120 DPI and compared pixel-for-pixel;
- **all 18 pre-Spellbook pages are identical**.

Therefore the Spellbook append does not alter the previously approved base/Extended output.

## Pagination / no-silent-loss proof

The integrated regression exercises three attached spells across two levels, including:

- a cantrip;
- a level-1 spell with two casting sources and distinct calculated source values;
- a deliberately very long level-1 spell that must continue onto a second Spellbook content page.

Final artifact behavior:

- page 19: Spellbook index;
- page 20: all three spell starts;
- page 21: `Zancada interminable (continuación)`;
- terminal description marker `MARCADOR TERMINAL DEL SPELLBOOK` is present;
- terminal authored note is present;
- index page numbers resolve to the actual first page of each spell.

The long description is preserved rather than clipped or silently discarded.

## Validation history

The first integrated run, Scaffold #3159 / `35679167146`, failed only on a narrow Kotlin collection-mutation compile issue in the new renderer.

Repair:

- `ac0cbb2b26202bf934ac4926b56899014390613e` — replace ambiguous `+=` list mutation with explicit `add(...)`.

Final Scaffold #3161 / `35679451757`:

- Kotlin — SUCCESS;
- backend — SUCCESS;
- hosted database — SUCCESS.

## Representative proof hashes

From artifact `10674370966`:

- `spellbook-append-baseline.pdf`  
  SHA-256: `6e3c134a5a65624d66633ad7c642a160f49462920b890a575e698aa29faa7997`
- `spellbook-application-owned-proof.pdf`  
  SHA-256: `fffdbf973db2fa1c3de63edc1da1805b42ea0b7efb657798a46aec034c1fdf68`

## PDF preflight / visual audit

The final Spellbook proof was rendered and visually inspected after the green Scaffold.

- 21 Letter pages, 612 × 792 pt;
- not encrypted;
- no XFA;
- 0 AcroForm fields;
- 0 attachments;
- 0 annotations;
- text remains extractable;
- index, normal spell entries and long continuation page inspected at 180 DPI;
- no material clipping, overlap, broken glyph, footer collision or reading-order defect observed.

The footer-rule concern raised during code review was checked against the rendered artifact and is not a defect; no speculative geometry change was made.

## Remaining D-0074 gates

This checkpoint closes the optional Spellbook renderer, not the complete PDF-export product.

Remaining known gates are:

1. platform portrait-byte handoff plus Crop-to-fill / Fit-entire-image rendering;
2. owner-facing `APP_MODIFIED_SHEET` visual implementation;
3. originating-section continuation cues on frozen base sheets;
4. Save/Share integration, beginning with the Desktop DM workflow and then approved surface parity.

## Conclusion

**APPLICATION-OWNED SPELLBOOK PRODUCTION: PASS**

The common Spellbook is now real-plan-driven, appended after the selected family/Extended output, preserves attached-spell/source semantics, paginates complete descriptions, and does not modify any pre-existing sheet page.

Next implementation gate:

> Complete the local portrait-byte handoff and render Crop-to-fill / Fit-entire-image behavior without adding a network dependency to PDF generation.

PR #85 remains **OPEN / DRAFT / DO NOT MERGE**.

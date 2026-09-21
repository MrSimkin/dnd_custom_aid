# Checkpoint — Custom v2 Extended Run 7 — OWNER APPROVED / FROZEN

**Date:** 2026-09-20/21 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Owner decision:** APPROVED  
**Family state:** Custom v2 base + Extended pages OWNER APPROVED / FROZEN

## Approved Extended baseline

- implementation commit: `f464522ad232f4ed6193e1c28d118e45faad988c`;
- Scaffold push run: `35546791262` / #2946 — SUCCESS;
- populated-template-proofs artifact: `10617190236`;
- artifact archive SHA-256: `5a31ab9ab7907f2a97a81fe5b86ceedddbe461e3a80c9e4360df3139ba0e4bc4`;
- proof PDF: `custom-v2-extended-evaluation-run7.pdf`;
- proof PDF size: 656,209 bytes;
- proof PDF SHA-256: `b765884d62cc69a5451fb02dade16db756f1b58469ba4cdb5f5c16ec56e79440`.

## Owner approval

The owner explicitly approved the final Run-7 result after the complete CI-artifact and two-renderer visual audit.

This approval closes the Custom-v2 Extended visual-design/QA phase and freezes the complete seven-page evaluation family:

1. Custom Statistics — per Attribute;
2. Custom Statistics — per Ability;
3. Traits & Features;
4. Resources & Options;
5. Inventory / Equipment;
6. Spells;
7. Notes.

The already-owner-approved Custom-v2 base sheets remain unchanged and authoritative:

- Custom v2 — per Attribute;
- Custom v2 — per Ability;
- shared Custom-v2 source pages.

## Frozen design mechanics

The following are part of the approved Custom-v2 Extended baseline:

- five independent semantic OCG layers per page:
  `STRUCTURE -> CLEANUP -> LABELS -> VALUES -> MARKERS`;
- transparent source-derived score/modifier ornament rather than opaque source crops;
- source-measured v2 gray band tones;
- source-measured v2 checkbox/marker geometry;
- Corbel-Bold / Corbel roles where direct frozen-v2 equivalents exist;
- Fira Sans roles for generated values/body content;
- Para Hoja de PJ Symbols v8 for applicable state markers;
- source-measured horizontal Corbel transforms;
- per-Attribute relationship grammar for custom Attribute + save + linked Abilities;
- separate `ATRIBUTOS | TIRADAS DE SALVACIÓN | HABILIDADES` columns in per-Ability mode;
- trait-local uses/recovery remaining local to the trait;
- compact symbol counters for small maxima and textual X/Y fallback for larger maxima;
- one Special Equipment state square plus textual `Sintonizado`;
- Extended spells preserving the v2 spell grammar and blank `ESPACIOS GASTADOS` semantics;
- source-led Notes page;
- `Raza` terminology contract.

## Final correction history

Run 6 was rejected as the final owner candidate and remains historical evidence only.

Run 7 corrected:

- opaque crop/background artifacts through transparency/layer separation;
- incorrect direct-equivalent font roles;
- v2 gray tones and marker geometry;
- alignment from measured source geometry;
- note overflow through row-cadence wrapping rather than arbitrary font shrinking;
- Corbel subset rendering by reusing the font objects already cloned from the approved v2 source forms instead of reconstructing the embedded subset from font-file bytes.

The final Corbel correction was necessary because reconstructing the subset changed glyph mappings even when PDF text extraction appeared correct.

## Final verification

The exact CI artifact from `f464522ad232f4ed6193e1c28d118e45faad988c` was independently rendered with:

- PDFBox/PDFium-side artifact renders;
- Poppler / `pdftoppm`.

Final visual inspection found:

- no missing Corbel glyphs;
- no replacement characters;
- no clipping;
- no problematic overlaps;
- no opaque source-crop rectangles;
- no cross-renderer structural divergence.

PDF checks:

- exactly 7 pages;
- Letter / 612 x 792 pt;
- not encrypted;
- 35 semantic OCG groups minimum contract satisfied.

Semantic guards passed:

- `Dados de portento` absent;
- `Especie` absent;
- `Atributo personalizado` absent;
- `7/12` present;
- `Sintonizado` present.

## Preservation rule

Do not recalibrate or redesign the approved Custom-v2 Extended pages unless:

- the owner reports a new visible defect;
- production-data integration exposes a real bounded defect;
- or a product requirement explicitly reopens the baseline.

A production-data defect must be fixed as narrowly as possible and must not silently redefine this visual baseline.

Green CI alone is never authority to alter the frozen appearance.

## Next gate

The Custom-v2 Extended visual-design/QA phase is closed.

Next development phase:

**Custom-v2 Extended production promotion + real-data integration**

The approved Run-7 mechanics must be promoted from QA/evaluation test code into the real renderer driven by `PcSheetPdfRenderPlan`, preserving both frozen Custom-v2 first-page modes.

After production promotion:

1. generate complete populated PDFs from real plan/model data;
2. exercise long/empty/dense data cases and overflow routing;
3. compare production output against the frozen Run-7 visual baseline;
4. perform two-renderer QA and an independent production audit;
5. obtain owner review for the production-integrated result.

PR #85 remains **DRAFT / DO NOT MERGE** until the remaining production/end-to-end renderer gates are complete.

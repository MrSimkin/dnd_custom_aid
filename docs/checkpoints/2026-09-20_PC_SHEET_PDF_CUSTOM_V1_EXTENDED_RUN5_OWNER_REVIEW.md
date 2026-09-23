# Checkpoint — Custom v1 Extended Run 5 — PASS FOR OWNER REVIEW

**Date:** 2026-09-20 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Final proof implementation commit:** `f197f9382d751d4342fb648d73b3520d72cdd911`  
**Scaffold push run:** `35527343150` / run #2874 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10610600180`  
**Artifact archive SHA-256:** `8093d3dac9a828828f03d659cda05613b54d9f7a14065c7ab97876340096b342`  
**Proof PDF:** `custom-v1-complete-family-extended-run5.pdf`  
**Proof PDF SHA-256:** `f41a3394f2cdd9c7d2305f59b9c2c89e8b35da4418d8da8f3d9e5a8eb535436a`  
**Layer diagnostics PDF:** `custom-v1-extended-run5-layer-diagnostics.pdf`  
**Diagnostics SHA-256:** `9c19bc1e675d5fadb7c49b71d531cbbecf0f13e534837a8cc0af43d7c09820ef`

## Owner feedback that superseded Run 4

Run 4 was owner-reviewed and **NOT APPROVED / SUPERSEDED**.

The owner identified:

- page 6 still had colored artifacts in/around Attribute/Ability boxes;
- `Notas de Estadísticas Personalizadas` lacked the construction symmetry of `Definiciones`;
- page 6 had the wrong information hierarchy: Attributes/Abilities should be at the top;
- Attribute headings still used condensed typography when they should not;
- page 7 violated the project terminology contract with `Rasgos de Especie`; the product term is always `Raza`;
- page 8's Resources design should reuse the sheet's ammunition/resource-counter logic and/or Para Hoja de PJ glyph vocabulary;
- page 8's standalone `Recuperación` and `Estados` sections were unnecessary and the former still showed color artifacts.

The owner explicitly requested design strategy first, then implementation.

## Run-5 design strategy

Run 5 does not continue the Run-4 masking/coordinate patch cycle.

### Page 6 — Custom Statistics

- Attributes/Abilities are moved to the top, immediately below the page title.
- Whole copied source columns are no longer imported.
- Only the authentic ornamental score/modifier geometry is reused from the owner source.
- New Ability/save rows are constructed natively.
- Attribute headings use full-width Fira Sans SemiBold with no horizontal scaling/compression.
- Proficiency/expertise boxes use approved Para Hoja de PJ Symbols v8.
- `Definiciones` and `Notas de Estadísticas Personalizadas` share the same three-column widths, row count, spacing and alternating-band rhythm.
- The final score crop begins exactly at the authentic ornamental top edge, eliminating the last inherited source-text remnants without reintroducing masks.
- New numeric scores/modifiers/save/skill bonuses use the full-glyph Fira family rather than the incomplete source Gill subset.
- The narrow save-row caption is `Salvación`, preventing label/bonus collision.

### Page 7 — Traits & Features

- `Rasgos de Especie` is replaced by `Rasgos de Raza`.
- Final PDF regression guard rejects `Rasgos de Especie`.

### Page 8 — Resources & Options

- Standalone `Recuperación` panel removed.
- Standalone `Estados` panel removed.
- `Recursos` becomes one full-width tracker:
  - resource name;
  - visible Para Hoja de PJ Symbols v8 counter/slot glyphs;
  - recovery cadence on the same row.
- `Opciones` remains the lower coherent section.
- Page 8 is now native structure and deliberately has no cleanup-mask dependency.

### Whole-family regression discovered during audit

Run 5's whole-family scan also exposed inherited source-font-subset omissions that were not part of the owner's page-6/7/8 feedback:

- page 9 valuables values `120 / 75 / 45` were missing;
- page 10 spell-slot counts were missing.

Both were caused by the incomplete embedded Gill subset. Run 5 removes Gill from newly rendered extension content and uses full Fira Sans SemiBold for those values.

## Final validation

CI #2874:

- backend — SUCCESS;
- hosted database — SUCCESS;
- Kotlin/build/tests — SUCCESS;
- Android debug APK upload — SUCCESS;
- PC sheet source renders upload — SUCCESS;
- populated PC proof artifact upload — SUCCESS.

Final PDF audit:

- 11 pages;
- unencrypted;
- PyMuPDF-openable;
- not scanned;
- no XFA;
- frozen pages 1–5 independently rendered at 120 dpi and pixel-identical to the approved Custom-v1 Run-7 base;
- page 6: no colored source-mask artifacts observed; Attributes/Abilities are top-first; headings are non-condensed; Definiciones/Notas construction is symmetric; score/modifier/save/skill numbers render;
- page 7: `Rasgos de Raza` present; invalid `Rasgos de Especie` guarded against;
- page 8: resource counters render with v8 glyphs; no standalone Recuperación/Estados extension panels; no cleanup mask;
- page 9: `120 / 75 / 45` render correctly;
- page 10: spell-slot counts render correctly;
- page 11: no regression found;
- independent PDFium and Poppler rendering completed successfully; targeted comparison of pages 6, 8, 9 and 10 showed no renderer-specific missing content or layout failure;
- pages 6–11 were re-scanned as a complete extension family.

## Current gate

**PASS FOR OWNER REVIEW.**

This is not owner approval and does not freeze the Custom-v1 Extended family.

The owner must review the complete 11-page Run-5 proof. Only explicit owner approval may mark the Custom-v1 Extended family OWNER APPROVED / FROZEN.

PR #85 remains **DRAFT / DO NOT MERGE**.

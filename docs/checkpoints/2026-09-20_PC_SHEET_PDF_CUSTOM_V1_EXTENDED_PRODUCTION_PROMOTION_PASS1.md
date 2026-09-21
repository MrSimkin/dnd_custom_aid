# Checkpoint — Custom v1 Extended Production Promotion Pass 1 — Custom Statistics

**Date:** 2026-09-20 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Owner-approved visual authority:** Custom-v1 Extended Run 6  
**Production head:** `da4bc41a32b903d210b6380604502874dd362f1f`  
**Final push Scaffold:** `35555908248` / run #3060 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10620106290`  
**Artifact digest:** `sha256:c41b77002143ef9b76c64b6bd57c7c8390525ce475bad483bde0e0b88e90d1c8`

## Scope

Promote one frozen Custom-v1 Extended role from owner-review/test-only code into real production rendering driven by `PcSheetPdfRenderPlan`:

- **Extended — Custom Statistics**

This pass intentionally does not promote Traits, Resources, Inventory, Spells or Notes.

## Production implementation

New production renderer:

`desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopCustomV1ExtendedRenderer.kt`

Whole-export wiring:

`DesktopPcSheetWholeDraftRenderer`

The renderer is instantiated only when a Custom Statistics Extended page is actually mandatory and real custom statistics exist. Therefore a normal Custom-v1 export without custom stats does not import unused source forms or otherwise touch the frozen five-page base.

## Fixture/model separation

The production renderer contains no owner-review fixture names or values.

A source scan of the production renderer found no hardcoded references such as:

- Honor;
- Voluntad;
- Suerte;
- Etiqueta;
- Aster;
- Liria;
- or other Run-6/dummy-character values.

Those remain test/calibration data only.

Production values come exclusively from `PcSheetPdfRenderPlan`.

## Frozen Run-6 grammar preserved

The production page retains the owner-approved Run-6 mechanics:

- six Attribute/Ability modules per page;
- authentic owner-source score/modifier ornament;
- full `Tirada de Salvación` naming;
- five linked-skill rows per module;
- owner decorative Attribute-heading grammar where glyph-safe;
- three ruled columns for Definitions;
- three ruled columns for Custom Statistics Notes;
- independent OCG layers:
  1. STRUCTURE;
  2. CLEANUP;
  3. LABELS;
  4. VALUES;
  5. MARKERS;
- approved v8 training/expertise symbols;
- owner source proportions and row cadence.

Custom Attributes own skills explicitly linked to them. Custom skills governed by a built-in Ability are rendered in the matching standard-Ability module, preserving the conceptual relationship used by the approved Run-6 example.

No named custom Attribute (for example Honor) is presumed to exist.

## Data-driven pagination

The production renderer paginates instead of imposing the review fixture's density:

- modules: six per page;
- linked custom skills: five per module slice;
- Definitions: fifteen physical ruled lines per page;
- Notes: fifteen physical ruled lines per page.

A Custom Attribute with more than five linked skills receives additional module slices/pages rather than losing skills.

Definitions/notes can also require additional matching pages.

## Font safety

The owner-approved EnchantedLand decorative source font is an embedded subset.

Generated Attribute headings:

- use the exact imported owner font object when it can encode the user-authored heading;
- fall back to full-glyph Fira Sans SemiBold only when the subset cannot safely encode that text.

This follows the established Custom-v2 font-safety lesson and avoids rebuilding embedded subset fonts.

## Exact proof hashes

From artifact `10620106290`:

- `custom-v1-whole-draft.pdf`  
  SHA-256: `bcbd5f4c0bf078a535455a44a3647be56de82f4d3dcd2b6b148c39faa7c741a9`
- `custom-v1-production-extended-stats-pass1.pdf`  
  SHA-256: `f506a48047a058d606f5f9c49172afd2e510a3d535b017cf68536890c20ba0ae`
- `custom-v1-custom-stats-overflow.pdf`  
  SHA-256: `5acf9b1fecd214dbfae311d8e0af8b4b867127b4f7e60be22c1f5edc86c857ea`

## Frozen-base regression

The five-page `custom-v1-whole-draft.pdf` from this pass was rendered and compared against the pre-promotion `0295f30...` artifact.

Result:

- page 1: 0.0% pixel change;
- page 2: 0.0% pixel change;
- page 3: 0.0% pixel change;
- page 4: 0.0% pixel change;
- page 5: 0.0% pixel change.

**Frozen base preservation: PASS.**

## Production proof

Normal real-plan proof:

- 6 Letter pages;
- page 6 is the promoted Extended — Custom Statistics page;
- openable and unencrypted;
- no AcroForm fields, attachments or annotations;
- source/text/symbol fonts present;
- expected Custom-v1 Extended OCG layers present by automated regression test;
- representative real-plan custom Attribute/skill relationships preserved.

Stress proof:

- 9 Letter pages;
- four matching Custom Statistics pages after the five-page base;
- multi-slice Attributes remain readable;
- final stress skill `Vínculo 7-7` survives;
- no observed clipping, overlap, broken glyphs or black squares.

The stress fixture uses synthetic custom-stat names solely to prove pagination; production code does not depend on those names.

## Dual-renderer audit

The normal production proof and stress pages were rendered independently through PDFium and Poppler.

Normal page-6 raster difference at 180 DPI was approximately 0.062%, consistent with ordinary rasterization/antialiasing differences. No semantic or structural divergence was observed.

Visual inspection found the promoted page coherent with the frozen Run-6 grammar.

## Conclusion

**CUSTOM V1 EXTENDED PRODUCTION PROMOTION PASS 1: PASS**

Only Extended — Custom Statistics is production-promoted by this checkpoint.

Next isolated promotion role:

> Extended — Traits & Features

The remaining Custom-v1 Extended roles remain unpromoted until their own plan-driven implementation, CI artifact and visual audit pass.

PR #85 remains **OPEN / DRAFT / DO NOT MERGE**.

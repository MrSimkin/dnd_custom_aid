# Checkpoint — Wave 7 PC Sheet PDF Renderer Research Pause

**Date:** 2026-09-18 (Chile local time)  
**Status:** PAUSED BY OWNER — SAFE RESUME POINT  
**Integrated trunk:** `main` at `2dc74e2d9c7d853a068e9052ec4928bf5178eb9f`  
**Active branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**Draft PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Owner visual gate:** ROUND 1 REJECTED

## What was completed in this work session

The branch first established that the Desktop application can:

- load the owner's authoritative Custom v1/v2 PDF templates;
- consume the integrated `PcSheetPdfRenderPlan`;
- overlay canonical representative PC data;
- generate deterministic PDF + PNG proof artifacts in CI.

The exact proof head reviewed by the owner was:

`e3ab7fd850bd72b1782109c7d6d2361ab7203624`

Scaffold run:

`35299306838` — SUCCESS

That green run is valid **technical generation evidence only**.

## Owner visual QA result

Round 1 was rejected.

The owner identified:

- unacceptable horizontal text alignment;
- unacceptable vertical text alignment;
- inappropriate use of font letters such as `X` as check/proficiency markers;
- inadequate sparse dummy data for visual review;
- incorrect font selection;
- inappropriate one-font treatment;
- need for structured QA rather than whole-page eyeballing.

Therefore:

- Custom v1 MAIN: NOT APPROVED;
- Custom v2 per Attribute MAIN: NOT APPROVED;
- Custom v2 per Ability MAIN: NOT APPROVED;
- no visual family is approved;
- PR #85 must not be merged on the basis of the existing proof.

## Approved strategic change

The owner explicitly approved a **slower but better whole-export-first approach**.

Do not perfect page 1 in isolation and then discover new renderer requirements on pages 2–5 or Classic.

Approved sequence:

1. survey the full export surface;
2. research/design the shared renderer;
3. implement reusable rendering primitives;
4. perform primitive QA;
5. implement all required export pages/families;
6. run structured section-by-section / parameter-by-parameter QA;
7. run page/family QA;
8. run complete end-to-end export QA.

Implementation breadth and QA granularity are intentionally different: design for the whole export, but validate incrementally.

## Full-template survey finding

The non-MAIN pages introduce primitives page 1 cannot validate:

- equipment tables;
- repeated compact rows;
- wealth/currency regions;
- narrative/wrapped text blocks;
- spell grids and spell-level rows;
- repeated slot/check structures;
- lined notes;
- graph-paper/freeform regions.

This justifies building common primitives before attempting another broad page mapping.

## Renderer research findings

Research checkpoint:

`docs/checkpoints/2026-09-17_PC_SHEET_PDF_RENDERER_FOUNDATION_RESEARCH.md`

Important findings:

- Custom v1 source typography includes EnchantedLand, GillSansMT and the owner's `Para-hj-de-pj` symbol font;
- Custom v2 source typography includes Corbel, Corbel Bold and Bahnschrift;
- Helvetica in Round 1 was objectively disconnected from both family type systems;
- alignment should derive from font metrics and defined rectangles, not approximate baseline offsets;
- renderer typography should use semantic, family-aware font roles;
- simple markers can use native PDF vector geometry;
- a purpose-built symbol font is also a strong candidate;
- wrapping/fitting must honor D-0074 readability/overflow rules;
- dense deterministic fixtures are required;
- PDFBox major version should be settled before owner-approved visual baselines.

## Repository visibility / font policy

The app is private/personal-use.

The repository stays public during development because the owner's current GitHub usage model provides more useful Actions capacity while public. The owner intends to make the repository private when the project is complete.

While public:

- do not commit third-party proprietary font binaries without redistribution rights;
- private/local proprietary fonts may still be considered for the final private app;
- making the repo private later does not undo prior public Git history.

Policy checkpoint:

`docs/checkpoints/2026-09-18_REPOSITORY_VISIBILITY_AND_PDF_FONT_ASSET_POLICY.md`

## Owner symbol font

The owner provided the original `Para Hj De Pj.ttf` and confirmed that he created it.

Inspection found the visible legacy symbol system primarily at:

- `A/a` round outline;
- `B/b` square outline;
- `C/c` narrow oval outline;
- `D/d` narrow oval filled;
- `E/e` square filled.

Original SHA-256:

`d0c0d50ad8ed33064e3af89b0976933b9eba8ead2d26234011c1a3233c246658`

A local v2 candidate was generated while preserving legacy mappings.

Candidate SHA-256:

`fb539e276861a164614cf1666482bd302a5e8efcbca20fff73051d42614e6295`

The candidate is **not visually approved**. Its scale, stroke thickness, double-circle expertise symbol, check, cross, diamonds and semantic assignments remain Primitive-QA material.

The owner explicitly approved continued evolution of this font as renderer needs become concrete.

Public-repo reproducibility is now preserved through:

- `scripts/fonts/expand_para_hoja_de_pj_v2.py`;
- `docs/reference/Para_Hoja_de_PJ_Symbols_v2_MAPPING.md`.

Neither the original nor generated TTF binary is committed to the public repository.

## Structured QA requirement

Future visual review must be section-by-section, objective-by-objective and parameter-by-parameter.

At minimum validate:

- canonical field/data mapping;
- horizontal placement;
- vertical placement;
- font family;
- font weight/style;
- font size;
- fit/wrapping;
- marker/icon;
- spacing;
- overflow/collision;
- empty-state behavior;
- cross-field visual consistency.

Use explicit **PASS / CHANGE / N/A** results.

Do not ask for whole-page/family approval until component sections have passed.

## Resume sequence

When work resumes:

1. reverify `main`, active branch and PR #85;
2. read this checkpoint plus the rejected-QA, strategy and research checkpoints;
3. settle PDFBox 2.0.37 vs current 3.x before visual baselines;
4. validate actual private/public font candidates against that version;
5. design/implement metric-based text/layout primitives;
6. compare native vector markers vs the owner symbol-font candidate;
7. implement text wrapping/fitting/readability-floor behavior;
8. build normal + dense deterministic fixtures;
9. generate a **primitive QA artifact**;
10. conduct structured owner primitive QA;
11. only then expand to full export implementation.

## Explicit non-actions at pause

- no merge;
- no claim of visual approval;
- no new provider deployment;
- no backend/database change;
- no proprietary font binary committed;
- no restart from page-one coordinate nudging;
- no assumption that the current Round 1 renderer geometry is authoritative.

This checkpoint is the safe continuation point.

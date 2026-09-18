# Latest project checkpoint — global resume map

**Updated:** 2026-09-18 (Chile local time)  
**Normal integrated trunk:** `main`  
**Last verified integrated `main`:** `2dc74e2d9c7d853a068e9052ec4928bf5178eb9f`  
**Wave 5:** COMPLETE / OWNER-QA ACCEPTED / INTEGRATED  
**Wave 6 core reusable/persistent content architecture:** COMPLETE / INTEGRATED  
**Wave 7:** ACTIVE  
**Current active branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**Draft PR:** #85 — OPEN / DRAFT / **DO NOT MERGE**  
**PC Sheet PDF visual QA:** ROUND 1 **REJECTED**  
**Current checkpoint:** `docs/checkpoints/2026-09-18_WAVE7_PC_SHEET_PDF_RENDERER_RESEARCH_PAUSE.md`

## Read first on resume

1. `AGENTS.md`;
2. `docs/checkpoints/2026-09-18_WAVE7_PC_SHEET_PDF_RENDERER_RESEARCH_PAUSE.md`;
3. `docs/checkpoints/2026-09-17_WAVE7_PC_SHEET_PDF_MAIN_PAGE_VISUAL_QA_ROUND1_REJECTED.md`;
4. `docs/checkpoints/2026-09-17_PC_SHEET_PDF_WHOLE_EXPORT_RENDERER_QA_STRATEGY.md`;
5. `docs/checkpoints/2026-09-17_PC_SHEET_PDF_RENDERER_FOUNDATION_RESEARCH.md`;
6. `docs/checkpoints/2026-09-18_REPOSITORY_VISIBILITY_AND_PDF_FONT_ASSET_POLICY.md`;
7. `docs/checkpoints/2026-09-18_OWNER_SYMBOL_FONT_INSPECTION_AND_V2_CANDIDATE.md`;
8. `docs/PROJECT_STATE.md`;
9. `docs/BRANCH_STATUS.md`;
10. D-0071 through D-0075 as relevant.

## Current truth

The PC Sheet PDF shared semantic/render-plan foundation is integrated.

A first local PDFBox/template-overlay proof was implemented on PR #85 and generated reviewable Custom v1/v2 MAIN-page PDFs successfully. CI generation evidence was green, but **owner visual QA rejected the rendering**.

The rejected proof must not be treated as a nearly approved mapping. Problems include:

- horizontal and vertical alignment;
- incorrect typography choices;
- inappropriate one-font treatment;
- text glyphs used as semantic markers;
- insufficient full-page dummy data;
- inadequate whole-page review method.

The owner approved a **slower, whole-export-first renderer strategy**:

```text
full export survey
    -> shared renderer research/design
    -> reusable text/layout/font/marker primitives
    -> primitive QA
    -> all required pages/families
    -> section QA
    -> page QA
    -> family QA
    -> end-to-end export QA
```

Do not resume with isolated coordinate nudging on page 1.

## Symbol-font continuity

The owner created the original `Para-hoja-de-pj` symbol font and provided it for inspection.

A v2 candidate has been investigated locally and may continue to evolve as real renderer requirements appear.

Public-repo reproducibility:

- `scripts/fonts/expand_para_hoja_de_pj_v2.py`;
- `docs/reference/Para_Hoja_de_PJ_Symbols_v2_MAPPING.md`.

No original or generated TTF binary is committed while the repository remains public.

## Next substantive work

1. settle PDFBox 2.0.37 vs current 3.x **before visual baselines**;
2. validate font resources and family-aware typography roles;
3. implement metric-based text/layout primitives;
4. compare native PDF vector markers with the owner symbol-font candidate;
5. implement wrapping/fitting/readability-floor behavior;
6. create normal + dense deterministic fixtures;
7. generate a primitive-QA artifact;
8. conduct structured owner QA using PASS / CHANGE / N/A;
9. expand to the complete export only from the accepted foundation.

## Mandatory owner gate

No PC Sheet PDF visual family is approved.

PR #85 remains draft and must not merge until the appropriate visual/functional gates are satisfied.

## Permanent safety / operating constraints

- current repo intentionally public during development; owner intends private repo at project completion;
- final application is private/personal-use;
- do not publish proprietary third-party font binaries merely because they can be used locally;
- external-service budget remains USD $0 unless owner changes it;
- never commit secrets/credentials/tokens;
- preserve PC ownership/controller/campaign authority distinctions;
- preserve D-0074 PC export semantics; renderer work must consume, not redefine, the shared plan;
- green automated tests are not visual approval.

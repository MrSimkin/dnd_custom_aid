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
**Current checkpoint:** `docs/checkpoints/2026-09-18_PDF_PRIMITIVE_QA_OWNER_FEEDBACK_APPLIED.md`

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

The renderer foundation has now moved past the original research-only pause:

1. PDFBox 3.0.8 is settled for this package;
2. metric-based text/layout, wrapping/overflow, vector markers, repeated rows and portrait Fit/Crop primitives exist;
3. Fira Sans / Barlow Condensed Bold / Kalam Bold are bundled as deterministic **QA candidates**, not approved final typography;
4. a dedicated handwritten/script role exists;
5. adaptive vs fixed font-size behavior is explicit;
6. the app-derived single-check / double-check training grammar exists in the vector marker set;
7. `Para-hoja-de-pj` now has versioned repository architecture and a v3 generator/guide; original v1 binary publication is authorized but awaits re-attachment/hash verification.

The immediate gate is **structured owner Primitive QA** focused on typography, per-element scale, condensed-font readability, handwritten treatment and marker variants.

Do not resume broad full-page mapping until that primitive typography/symbol gate is accepted.

## Owner typography feedback applied

The owner accepted the direction so far with changes recorded in `2026-09-18_PDF_TYPOGRAPHY_AND_OWNER_SYMBOL_FONT_FEEDBACK.md`.

Latest fully green implementation Scaffold before this documentation/text correction: `35359508972` at `4d4c2fae3d6b483c5e508886783fda37076f2d81`.

## Mandatory owner gate

No PC Sheet PDF visual family is approved.

PR #85 remains draft and must not merge until the appropriate visual/functional gates are satisfied.

## Owner orchestration alert — DO NOT TRIGGER YET

This alert exists so the owner does not have to remember when parallel work becomes safe.

Trigger it only when **all** of the following are true:

- the owner has accepted the current Primitive QA;
- any changes requested during that QA have been applied;
- the resulting shared renderer/primitives foundation is green and stable;
- that shared foundation is no longer expected to keep changing underneath the next pieces of work;
- preferably, that stable foundation has been integrated into `main`, so every future worker can start from the same agreed version.

When those conditions are satisfied, **do not automatically continue into the next broad implementation package**.

Tell the owner clearly:

> **SAFE ORCHESTRATION POINT REACHED — introduce the orchestrator before continuing downstream work.**

Then explain, in plain language:

> The common groundwork is now stable enough that the next stage can safely be divided among several Work chats. This is the moment to create a fresh orchestrator chat. The orchestrator should decide which pieces can be worked on independently, give each worker a separate bounded task, and later bring those finished pieces back together before the next round of parallel work.

Do **not** trigger this alert merely because automated tests are green, because Primitive QA has started, or because one visual example looks promising. If the shared foundation is still being reviewed, corrected or rejected, continue the current work normally and keep the alert pending.

## Permanent safety / operating constraints

- current repo intentionally public during development; owner intends private repo at project completion;
- final application is private/personal-use;
- do not publish proprietary third-party font binaries merely because they can be used locally;
- external-service budget remains USD $0 unless owner changes it;
- never commit secrets/credentials/tokens;
- preserve PC ownership/controller/campaign authority distinctions;
- preserve D-0074 PC export semantics; renderer work must consume, not redefine, the shared plan;
- green automated tests are not visual approval.

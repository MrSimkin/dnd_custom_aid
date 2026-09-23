# Checkpoint — Custom v1 Hybrid Production Promotion Pass 1

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Owner-approved visual baseline:** Hybrid Run 6  
**Final Pass-1 code head before this checkpoint:** `f7d7e0fc7e88a3a87f2f7c029df87f0aa3d3e85e`  
**Final Scaffold:** `35462132265` / run #2686 — SUCCESS

## Purpose

Promote the owner-approved Run-6 Custom v1 Hybrid mechanics from QA-only test code into real renderer code driven by `PcSheetPdfRenderPlan`.

This is a production-promotion pass, not a new rendering strategy.

## Approval clarification preserved

The owner clarified that approval applies specifically to the latest draft presented for review:

**Hybrid Run 6 / Custom v1**

Durable approval checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_RUN6_OWNER_APPROVED.md`

## Production code added

New renderer:

`desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopCustomV1HybridRenderer.kt`

The renderer promotes:

- native PDF-point geometry;
- independent Form XObject / OCG sections;
- full font embedding;
- Fira / Kalam / frozen v8 symbol roles;
- metric-based ruled text placement;
- measured skill checkbox/value geometry;
- measured Identification geometry;
- Run-6 spell semantics.

## Whole-draft wiring

`DesktopPcSheetWholeDraftRenderer` now has a dedicated Custom-v1 path.

### Hybrid production sections

Page 1 / MAIN:
- Identification
- Defense
- Core Stats
- Abilities
- Skills by ability column
- Spellcasting Summary
- Attacks
- first two rows of Traits

Page 3 / NARRATIVE:
- Background
- Ideals
- Bonds
- Flaws
- Story

Page 4 / SPELL_LIST:
- Cantrips
- Level 1

### Temporary legacy residual sections retained

To preserve existing whole-sheet data coverage while migration continues:

Page 2 / EQUIPMENT:
- still legacy draft renderer

Page 3 / NARRATIVE residual:
- Personality
- Other Traits/Attributes long-form text
- Notes

Page 4 / SPELL_LIST residual:
- levels 2–9

Page 5 / NOTES:
- still legacy draft renderer

These residual regions are explicitly **not** claimed as Run-6 Hybrid-approved production rendering.

## Real-model semantics

The Run-6 QA fixture contained values used only for calibration.

Production mapping does not invent unavailable domain data.

Current model does not provide:

- Alignment
- armor/shield/misc AC decomposition
- next-level XP threshold

Those fields remain blank.

Current XP is rendered when progress mode is EXPERIENCE.

Inspiration remains deferred because it was not part of the approved Run-6 Core Stats baseline.

## Durable spell semantics

Preserved in production:

- cantrips never render preparation marks;
- `ESPACIOS GASTADOS` remains empty;
- level-1 slot total may be rendered;
- level-1 preparation checks use measured Run-6 geometry.

For legacy levels 2–9, spent-slot markers were also removed so the durable `ESPACIOS GASTADOS` rule applies across the whole temporary spell page.

## Regression-test changes

Custom v1 no longer treats `PDFTextStripper` output from Form/OCG content as a visual correctness oracle.

The whole-draft test now verifies:

- five Custom-v1 pages;
- required Custom-v1 OCG layer names;
- retained direct residual data coverage;
- page renderability.

Custom v2 keeps its previous assertions.

## Audit history

### First production promotion build

Commit:

`cad69fa2be64b3dbef548152fe7436f3d6efdc61`

Scaffold:

`35461749003` / run #2684 — SUCCESS

Render-first audit found one real model-density issue:

- long real `Ideales`, `Vínculos`, and `Defectos` disappeared because the approved Run-6 QA fixture used short single-line examples;
- the production helper correctly refused to force overlong text into a single rule, but the section needed multi-rule paragraph semantics.

### Correction

Commit:

`f7d7e0fc7e88a3a87f2f7c029df87f0aa3d3e85e`

Background / ideals / bonds / flaws now use their actual source multi-rule geometry and font-width wrapping.

Final Scaffold:

`35462132265` / run #2686 — SUCCESS

## Final PDF verification

Artifact:

`custom-v1-whole-draft.pdf`

Preflight:

- 5 pages
- 612 × 792 pt
- openable
- not encrypted
- not scanned
- no XFA

Renderer parity:

- PDFium and pdftoppm both render the complete 5-page document;
- no structural divergence was observed during visual inspection.

Targeted visual regression diff between #2684 and #2686:

- changed pages: **1**
- page 3 only
- pages 1, 2, 4, 5: **0% pixel change**
- page-3 change is localized to the left-side Background/Ideals/Bonds/Flaws region.

This proves the correction stayed inside the intended section.

## Independent final audit

### Page 1

**PASS for promoted Run-6 sections.**

Production data mapping preserves the approved geometry and typography.

Intentional blanks:
- Alignment
- unsupported AC decomposition
- next-level XP threshold
- Inspiration (deferred)

### Page 3

**PASS for promoted Hybrid fields after correction.**

Real long Ideals/Bonds/Flaws now wrap across physical rules and remain readable.

Story remains stable.

Legacy residual content is still visibly older/smaller typography and remains migration work.

### Page 4

**PASS for promoted Cantrip + Level-1 slice.**

- cantrip preparation marks absent;
- level-1 checks aligned;
- level-1 names use approved larger text/baseline;
- spent-slot areas remain blank.

Levels 2–9 remain legacy typography/geometry and are explicitly pending migration.

### Pages 2 and 5

No promotion regression detected.

They remain legacy-rendered and pending Hybrid migration.

## Pass-1 conclusion

**CUSTOM V1 HYBRID PRODUCTION PROMOTION PASS 1: PASS**

The Run-6 visual mechanics are no longer confined to QA tests; they now exist in real renderer code using actual character-plan data.

This is not complete Custom-v1 renderer closure because some pages/sections remain on the legacy path.

## Next development step

Continue Custom v1 production migration using the same section-isolated architecture.

Recommended next promotion pass:

1. saving-throw checks/values + Inspiration on MAIN using measured source geometry;
2. EQUIPMENT page sections;
3. NARRATIVE residual Personality / Other Traits / Notes;
4. spell levels 2–9 using exact Hybrid geometry and approved semantics;
5. NOTES page using Hybrid ruled-text sections;
6. produce a complete five-page all-Hybrid Custom-v1 draft;
7. perform independent audit and owner review before Custom-v1 production closure.

PR #85 remains **DRAFT / DO NOT MERGE**.

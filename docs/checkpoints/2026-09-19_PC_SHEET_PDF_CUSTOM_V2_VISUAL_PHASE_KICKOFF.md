# Checkpoint — Custom v2 PDF Visual Phase Kickoff

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Active strategy:** Strategy 1 — Hybrid  
**Previous visual family:** Custom v1 — OWNER APPROVED / FROZEN

## Purpose

Begin the Custom v2 visual-family phase without repeating the strategy-discovery work already completed for Custom v1.

The owner explicitly requested:

- reuse all lessons from Custom v1;
- ensure the previous work and Method of Operation are durably preserved in the repository;
- perform independent audits after every generated draft.

## Durable authorities verified before kickoff

The following repository records are present and authoritative:

- `docs/PC_SHEET_PDF_STRATEGY_RUN_PROTOCOL.md`
- `docs/PC_SHEET_CUSTOM_V1_APPROVED_BASELINE.md`
- `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_RUN7_FINAL_OWNER_APPROVED.md`
- `docs/checkpoints/2026-09-19_PC_SHEET_PDF_RUN7_FINAL_COORDINATE_CALIBRATION.md`
- `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`

Custom v1 remains frozen and must not be silently modified.

## Custom v2 product scope

D-0074 requires two owner-reviewable Custom-v2 presentations using:

`assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf`

The two first pages are alternatives:

1. **Custom v2 — per Attribute**
2. **Custom v2 — per Ability**

Pages shared by both variants should be calibrated once and reused rather than independently reinvented.

## Strategy rule

Start with **Strategy 1 — Hybrid**.

Do not reopen broad rendering-strategy research merely because this is a new visual family.

Reuse:

- authoritative source PDF as visual authority;
- native PDF-point geometry;
- independent semantic Form XObject / OCG layers;
- measured source rules / glyph rectangles instead of guessed centers;
- fully embedded generated fonts;
- Fira Sans / Kalam / frozen v8 symbol direction;
- metric-based marker fitting;
- metric-based ruled-text baselines;
- section-local calibration analogous to InDesign frames;
- overlay-only debug artifacts;
- renderer parity and visual-diff verification.

A strategy change is considered only if Custom v2 reveals a real structural blocker that Strategy 1 cannot reasonably solve.

## Run / audit MO — mandatory

Every Custom-v2 draft must follow:

1. render the source / current draft;
2. inspect the actual rendered pages;
3. change only the bounded hypothesis or coverage planned for that run;
4. render again;
5. independently audit the result even if the owner reports no observations;
6. record:
   - technical result;
   - visual result;
   - regressions;
   - owner observations;
   - assistant independent observations;
   - exact next corrections;
7. preserve lessons in the repository;
8. never equate green CI with visual approval.

For tricky/final drafts, verify with at least two renderers.

## Visual-draft completeness rule

The Custom-v1 lesson remains binding:

> A visual-approval draft must not mix the active Hybrid strategy with legacy visual rendering.

Dummy/calibration values are allowed when needed to expose geometry and typography.

Production-domain mapping is a separate gate.

## Planned sequence

### Phase A — source analysis

- render all v2 source pages;
- identify which pages are shared by both variants;
- measure page/section geometry;
- map reusable v1 primitives vs v2-specific geometry.

### Phase B — first v2 Hybrid draft

Target:

- shared pages first;
- Custom v2 — per Attribute first-page variant;
- enough realistic dummy data to expose alignment, density, markers and overflow risks.

### Phase C — iterative audited runs

- independently audit every draft;
- calibrate local sections;
- preserve successful sections unchanged;
- use actual source geometry for defects.

### Phase D — per Ability variant

Reuse the already-approved shared pages.

Only the alternative first page should require substantial new calibration unless evidence proves otherwise.

## Owner gates

Custom v2 — per Attribute:
- populated example required;
- independent audit required;
- explicit owner approval required.

Custom v2 — per Ability:
- populated example required;
- independent audit required;
- explicit owner approval required.

Neither variant is approved merely because CI passes.

## Current state

**CUSTOM V2 VISUAL PHASE: ACTIVE**

Next action:

- inspect/render the authoritative v2 source PDF and begin the first Hybrid run.

PR #85 remains **DRAFT / DO NOT MERGE**.

# Checkpoint — Custom v1 Run 7 Final Calibrated Draft — OWNER APPROVED

**Owner approval date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Renderer strategy:** Strategy 1 — Hybrid  
**Approved rendering commit:** `7448693e36ee1b26243bd4091615dba725e95027`  
**Approved Scaffold:** `35465731044` / run #2708 — SUCCESS  
**Approved workflow artifact:** `pc-sheet-populated-template-proofs` / artifact id `10591616012`  
**Approved PDF:** `hybrid-strategy1-run7-composite.pdf`  
**Approved overlay diagnostic:** `hybrid-strategy1-run7-overlay-only.pdf`

## Owner decision

The owner explicitly approved the final calibrated five-page Custom v1 Run-7 draft and instructed that the result be saved durably in the repository so it is not lost before moving on.

**CUSTOM V1 FINAL RUN-7 CALIBRATED VISUAL BASELINE: OWNER APPROVED**

This approval supersedes earlier Custom-v1 visual baselines as the current visual authority.

Run 6 remains important historical evidence and the source of the successful section/layer architecture, but Run 7 final calibration is now the approved Custom-v1 visual reference.

## What is approved

The approved baseline is the complete five-page all-Hybrid Custom v1 visual draft after final coordinate calibration.

### Page 1
- Identification values
- Alignment
- XP / Next Level
- portrait stick-figure QA placement
- Defense / AC breakdown
- Inspiration
- Core Stats
- Attributes
- Skills
- Spellcasting Summary
- Attacks
- Traits

### Page 2
- Equipment, including calibrated columns 2 and 3
- Coins
- Gems / Jewelry / Art
- Special Equipment
- Special Equipment description geometry
- Special Equipment checks, calibrated on both X and Y

### Page 3
- Background
- Personality
- Ideals
- Bonds
- Flaws
- Other Traits / Attributes with calibrated divided-column X geometry
- Story
- Notes with calibrated X geometry

### Page 4
- Cantrips
- Spell levels 1–9
- preparation checks
- spell names / baselines
- slot totals

Durable spell semantics:
- cantrips do not receive preparation marks;
- `ESPACIOS GASTADOS` always remains empty.

### Page 5
- Notes using the Hybrid ruled-text strategy
- calibrated second-column X geometry
- QA doodles as independent vector content

## Architecture approved with this baseline

The approved Custom-v1 visual system uses:

- authoritative source PDF as design authority;
- native PDF-point geometry;
- independent semantic Form XObject / OCG section layers;
- source-measured rule / checkbox geometry;
- Fira Sans / Kalam / frozen v8 symbol roles established during the Hybrid runs;
- metric-based ruled-text baselines;
- metric-based marker fitting;
- section-level calibration analogous to the owner's InDesign workflow.

## Final coordinate proof

The final calibration pass used measured source-PDF geometry, not estimated offsets.

Important final coordinates include:

- Equipment col 2: x `169.937 .. 300.331`
- Equipment col 3: x `311.669 .. 442.063`
- Valuables object: x `453.402 .. 546.945`
- Valuables value: x `549.779 .. 583.795`
- Special Equipment description: x `240.803 .. 583.795`
- Other Traits left: x `215.291 .. 396.708`
- Other Traits right: x `402.378 .. 583.795`
- Page-3 Notes: x `215.291 .. 583.795`
- Page-5 Notes col 2: x `311.669 .. 583.795`

Special Equipment source checks were fitted with exactly 0.6 pt inset on every side of their measured printed squares.

## Regression verification

Compared against the immediately previous complete Run-7 draft:

- Page 1: 0% pixel change
- Page 2: changed only in requested calibration regions
- Page 3: changed only in requested calibration regions
- Page 4: 0% pixel change
- Page 5: changed only in requested calibration region

PDF preflight remained clean.

PDFium and pdftoppm showed no structural divergence.

## Preservation rule

Do not replace, reinterpret, or silently recalibrate this approved Custom-v1 visual baseline in future work.

Any future Custom-v1 visual modification must be treated as an explicit new change relative to this approved baseline.

When production mapping is completed, visual regression should be checked against this approved Run-7 result.

## Current gate

**CUSTOM V1 VISUAL DESIGN: OWNER APPROVED / FROZEN BASELINE**

This does not automatically merge PR #85 or approve Custom v2 / Classic.

The team may now move on to the next project step without further Custom-v1 visual experimentation unless the owner explicitly reopens it.

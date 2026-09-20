# Checkpoint — Custom v1 Extended Run 3 — WIP SAFETY / OWNER FEEDBACK CORRECTION

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Current WIP head before this checkpoint:** `2e5379e162995f85865710227ea3d0182eb33812`

## Why this checkpoint exists

The owner reviewed the source-faithful Custom-v1 Extended Run 2 and said it is **very close**, but identified three remaining defects on the Custom Statistics / Abilities extension page:

1. weird visual artifacts / white-space cuts;
2. some poorly selected / duplicated-looking blocks around Abilities;
3. the font used for Abilities is fully wrong.

The owner then explicitly asked to consolidate the current correction state into the repository for safety before continuing.

This checkpoint is therefore a **WIP safety checkpoint**, not an approval checkpoint.

## Durable status before Run 3

- Custom v1 frozen base pages 1–5 remain OWNER APPROVED / FROZEN at Run 7.
- Custom v1 Extended Run 1 remains OWNER REJECTED / historical evidence.
- Custom v1 Extended Run 2 remains the last complete reviewed candidate, but it is **NOT approved** because of the owner feedback above.
- Run 3 is the active correction line.
- PR #85 remains DRAFT / DO NOT MERGE.

## Run-3 correction scope

Only the Custom Statistics extension page is being materially corrected unless new evidence appears.

The frozen five-page Custom-v1 base must remain untouched.

The rest of the source-faithful extension composition from Run 2 remains the current direction unless the owner identifies a new defect.

### A. Remove white-mask artifacts

Run 2 constructed the Custom Statistics page by drawing the full source page and covering unwanted regions with white rectangles.

Run 3 changes that construction model:

- start from a clean Letter page;
- import only authentic source-PDF fragments needed for the page;
- source logo is clipped/imported directly;
- source Attribute/Ability strip is clipped/imported directly;
- new sections are drawn only in intentionally blank areas.

This removes the white-mask/cut artifact mechanism at its source.

Implementation commit:

`385eac76ea9d49a37688c7c2d54751261e36f22a`

### B. Preserve authentic source blocks

Run 2 could draw proficiency-square/rule geometry on top of geometry already present in the imported source strip.

Run 3 preserves the source's printed square and numeric rule and overlays only the proficiency symbol when needed.

No second generic square/rule should be drawn over the authentic source block.

### C. Match the real Ability/skill typography

The owner correctly identified that Run 2's Ability/skill text did not match the source.

Inspection of the source operator showed the source skill labels use Gill Sans with horizontal compression.

The active Run-3 implementation now uses the embedded owner-source `GillSansMT` and matches the source operator with:

- font size: 10 pt;
- horizontal scale: 60%;
- source-aligned baseline / X origin.

Typography refinement commit:

`63e83faa6f750a32f631371460d6735e87f281e8`

### D. Preserve realistic full custom-skill names

A first Run-3 draft shortened some sample names merely to guarantee fit. That was not desirable if the authentic source typography could accommodate the full names.

The current head restores the fuller custom-skill names while keeping the compressed Gill Sans treatment.

Current WIP head:

`2e5379e162995f85865710227ea3d0182eb33812`

Commit message:

`test: restore full compressed Custom v1 skill names`

## Current verification state at checkpoint time

Push workflow:

- run id: `35486040353`;
- run number: #2825.

At checkpoint time:

- backend — SUCCESS;
- hosted database — SUCCESS;
- Kotlin/build/tests — still IN PROGRESS.

Therefore **do not infer PASS from this checkpoint**.

The next continuation step is to wait for / inspect the completed Kotlin result and then download/render the exact successful artifact if the strict test passes.

## Mandatory continuation rules

1. Do not restart Run 3 from Run 2.
2. Resume from current WIP head `2e5379e162995f85865710227ea3d0182eb33812` or the immediately following safety-doc commit.
3. Keep pages 1–5 frozen and pixel-identical.
4. Keep the source-fragment construction for page 6; do not reintroduce broad white masking.
5. Keep authentic source block geometry; do not draw duplicate proficiency squares/rules.
6. Keep source-matched compressed Gill Sans for custom Ability/skill labels unless direct visual evidence shows the match is still wrong.
7. After CI passes, download the exact artifact, render the complete 11-page PDF, inspect page 6 at high resolution, and compare against the original Custom-v1 page 1.
8. If page 6 is visually clean, present a complete Run-3 proof to the owner.
9. Do not mark Custom-v1 extensions APPROVED/FROZEN until explicit owner approval.
10. PR #85 remains DRAFT / DO NOT MERGE.

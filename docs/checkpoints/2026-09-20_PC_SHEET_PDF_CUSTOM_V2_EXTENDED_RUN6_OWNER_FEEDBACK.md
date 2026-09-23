# Checkpoint — Custom v2 Extended Run 6 — owner visual feedback

**Date:** 2026-09-20 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Reviewed candidate:** `custom-v2-extended-evaluation-run6.pdf`  
**Disposition:** REJECTED AS FINAL OWNER CANDIDATE — preserve as historical evidence

## Owner feedback

The owner identified three classes of correctable defects:

1. visible artifacts that should be solved through proper layers/transparency rather than accepted as part of the design;
2. incorrect font usage in locations that have direct equivalents in the already-approved Custom-v2 sheets;
3. misalignment that should be corrected from frozen v2 geometry and layer placement rather than by visual approximation.

## Root-cause confirmation

Inspection of the approved Custom-v2 baselines confirms:

- Attribute names use the source **Corbel-Bold** treatment at approximately 12.12 pt;
- compact saving-throw / Ability labels use source **Corbel** at approximately 7.8 pt;
- generated values/body content use the established Fira Sans roles;
- v2 ruled bands use two gray source tones (approximately RGB 200/199/199 and 227/227/227), rather than generic white/gray zebra striping;
- source score/modifier ornaments must not carry an opaque source-page background into a differently colored target layer;
- v2 checkbox geometry is approximately 8.5 × 9 pt in the equivalent regions.

## Run 7 correction contract

Run 7 must:

- preserve Run 6 unchanged as historical evidence;
- replace opaque score/modifier fragment transplantation with a transparent source-derived ornament layer;
- derive placement from approved v2 geometry;
- use Corbel-Bold / Corbel in regions with direct v2 typographic equivalents;
- retain Fira Sans for generated values/body text and Symbols v8 for state markers;
- use the two measured v2 gray tones where banding is required;
- tighten checkbox size/alignment to v2 source geometry;
- keep Spells and Notes source-led unless a visible defect requires a bounded correction;
- keep five semantic OCG layers per evaluation page;
- remain at most **PASS FOR OWNER REVIEW** until explicit owner approval.

PR #85 remains **DRAFT / DO NOT MERGE**.

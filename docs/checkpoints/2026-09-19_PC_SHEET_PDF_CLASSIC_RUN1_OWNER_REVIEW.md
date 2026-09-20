# Checkpoint — Classic D&D-style Run 1 — OWNER REJECTED / HISTORICAL EVIDENCE

**Date:** 2026-09-19 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Visual family:** Classic D&D-style  
**Final Run-1 implementation/calibration commit:** `14b5c2561060fc319ef63cf7a9f2ffcbb7b9c996`  
**Scaffold:** `35473827949` / run #2779 — SUCCESS

## Product boundary

Classic follows D-0074:

- independently designed by the application;
- familiar D&D information organization;
- not a pixel-for-pixel copy or recreation of an official published sheet;
- multipage;
- intentional portrait area;
- populated dummy data for owner visual review.

No owner/custom source PDF is used as the Classic page background.

## Run-1 pages

1. Main — portrait, official attributes, saving throws, skills, combat, attacks, quick resources;
2. Equipment & Resources;
3. Features & Story;
4. Spell List;
5. Notes & Reference;
6. Extended — Custom Statistics.

The sixth page is intentional. It demonstrates the family-matched extension language using the same typography, border rhythm, spacing and print treatment as the Classic base pages.

## Extended-page scope clarification

The owner reaffirmed during continuation that Extended pages are needed for **all designs**.

This is treated as confirmation of D-0074 section 5, not as a Classic-only rule.

Therefore:

- Classic requires Classic-styled extensions;
- Custom v1 requires extensions coherent with Custom v1;
- Custom v2 requires extensions coherent with Custom v2;
- base-sheet approvals do not close extension design/QA.

Run 1 demonstrates only **Extended — Custom Statistics** for Classic. Other extension roles remain future work as applicable.

## Technical convergence

Initial Run 1 implementation exposed text-layout overflow in two compact geometry classes:

- repeated character-name header box;
- spellcasting / spell-slot compact mini-stat boxes.

A diagnostic proof isolated the issue. The final calibration enlarged only those boxes and restored the strict no-overflow assertion.

Final result:

- all six pages are Letter size;
- PDF is static/non-encrypted;
- strict text-overflow guard passes;
- backend job — SUCCESS;
- hosted database job — SUCCESS;
- Kotlin/build/tests — SUCCESS;
- proof artifact generation — SUCCESS.

## Independent visual audit

All six final PNG renders were inspected.

Observed state:

- consistent application-owned Classic visual grammar;
- portrait area is explicit;
- information grouping is familiar without copying an official sheet;
- typography hierarchy is legible at page scale;
- skill/save markers remain clear;
- spell page preserves generated-sheet semantics;
- Notes page preserves writable paper space;
- Extended — Custom Statistics clearly belongs to the same family;
- no clipping or missing fields observed after final compact-field correction.

**Technical/visual pre-owner result at the time: PASS FOR OWNER REVIEW.**

## Owner decision — REJECTED

The owner rejected Run 1 after visual review. This candidate is historical evidence only and must not be used as a Classic baseline.

Owner findings that invalidated the candidate:

- English owner-facing text appeared despite the Spanish export requirement;
- the visual grammar did not read as meaningfully inspired by the official D&D paper-sheet lineage;
- boxes/checks lacked a sufficiently coherent semantic grammar;
- writable/manual-entry space was too limited;
- the Extended page did not yet satisfy the agreed family-native extension contract;
- custom Abilities did not make their governing Attribute relationship sufficiently visible.

The rejection is design-level, not a request to continue coordinate-patching the Run-1 dashboard composition.

## Superseding baseline

Classic Run 2 replaced this candidate after targeted official/community-sheet research, a complete nine-page family proof, owner feedback, Y-axis/ruled-line correction and final owner approval.

See:

- `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CLASSIC_RUN2_OWNER_APPROVED.md`;
- `docs/PC_SHEET_CLASSIC_APPROVED_BASELINE.md`.

PR #85 remains **DRAFT / DO NOT MERGE** pending the remaining renderer/product integration work.

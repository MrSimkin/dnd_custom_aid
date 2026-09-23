# Checkpoint - PC Sheet PDF recovery VR-3 pre-print pass

**Date:** 2026-09-22  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 - OPEN / DRAFT / DO NOT MERGE  
**Implementation head:** `a5cb2311a0beb8454291d8b9985cb1b13f37a3dd`  
**Status:** AUTOMATED PRE-PRINT PASS / WORKER VISUAL PREFLIGHT PASS / OWNER QA PENDING

## Evidence

- push Scaffold #3261 / `35794876389`: SUCCESS;
- PR Scaffold #3262 / `35794879895`: SUCCESS;
- proof artifact: `10723153227`;
- artifact digest: `sha256:89e458a34a0f79992720c0b85116f66ce8007a3da5026248189eac30b9c4c421`;
- measured audit: `pc-sheet-preprint-xy-audit.tsv` - 15/15 PASS.

## Contracts enforced before proof promotion

- TERM-001: Raza, never user-facing Especie - PASS.
- ARCH-001: ordered semantic Custom layers - PASS.
- XY-001: measured owner-facing X/Y anchors - PASS.
- NAME-001: application-designed legacy `CLASSIC_DND_STYLE` family is owner-facing **Fantasy Sheet**.
- RUN-001: failed #3259/#3260 results were read and classified before the successful rerun.

## Worker rendered-page preflight

### Fantasy Sheet

- Historia/Personalidad uses consecutive physical writing rows without the prior fixed vertical semantic gaps.
- Rasgos adicionales, Idiomas and Aliados/Tesoro preserve visible reference rules.
- Traits continuation uses visible ruled-paper rhythm and `Raza` terminology.
- Inventory continuation uses one native row per item; no every-other-row skip; lower Valor/Ubicación/Notas prose sits on consecutive rules.
- Campaign Notes and References/Reminders align to physical rules.
- This family is not called Classic or official-like. It is an application-designed Fantasy Sheet.

### Custom v1

- Special Equipment check glyphs are single overlays inside the native source checkbox.
- Visual inspection plus raster centroid measurement confirms the checked-state mark is within the +/-1.5 pt optical-center gate.

### Custom v2 - per Attribute and per Ability

- Extended title/subtitle typography is restored to the frozen Run-7 source-font mechanics.
- Clase/Dotes and Raza/Trasfondo/Otros entries consume consecutive measured source rows instead of fixed blank 102-pt blocks.
- Logo/header remains vector/source-derived in STRUCTURE and appears clean in the rendered preflight.
- Five-layer semantic OCG contract is enforced by tests.

## Gate

Owner visual QA remains mandatory.

Do not merge PR #85 and do not begin Save/Share/export invocation until the owner explicitly approves this exact recovery candidate or supplies another bounded defect report.

All further iterations must append to `docs/PC_SHEET_PDF_ITERATION_LEDGER.md`.

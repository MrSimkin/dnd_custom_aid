# Checkpoint — Custom v1 Extended Run 6 — OWNER APPROVED / FROZEN

**Date:** 2026-09-20 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Owner decision:** APPROVED  
**Family state:** Custom v1 base + Extended pages OWNER APPROVED / FROZEN

## Approved Extended baseline

- implementation commit: `69b308f3d5d493d06bd0107ac66c7524935aa9fa`;
- Scaffold: `35529317947` / run #2885 — SUCCESS;
- artifact: `10609869599`;
- artifact archive SHA-256: `10d492f071db27b4bfc29c323d5841022e685164361fece4144e8ba6ba2c336a`;
- proof PDF: `custom-v1-complete-family-extended-run6.pdf`;
- proof PDF SHA-256: `03212b642ba9b8414e18344dbe90b6d68d623d14a0cd1ff712544063548eafe5`;
- diagnostics PDF: `custom-v1-extended-run6-layer-diagnostics.pdf`;
- diagnostics SHA-256: `db6e5d8f1d37f74fb9847fc82ceb4820f1f33cb29303ee3f81f2b7fceb5e5a6b`.

## Owner approval

The owner explicitly approved the Run-6 Extended-page result and requested that it be consolidated as the Custom-v1 Extended baseline.

The approval includes the complete extension family:

1. Extended — Custom Statistics;
2. Extended — Traits & Features;
3. Extended — Resources & Options;
4. Extended — Inventory / Equipment;
5. Extended — Spells;
6. Extended — Notes.

The previously frozen five-page Custom-v1 base remains unchanged and authoritative.

## Frozen design mechanics

The following are part of the approved Custom-v1 Extended baseline:

- independent multi-layer composition;
- source geometry / native measured structure separated from cleanup, labels, values and symbols;
- layer diagnostics;
- source-family proportions and row cadence;
- Custom-v1 decorative Attribute headings and three-letter stat-key naming convention;
- source-matched compact Ability/skill typography;
- full `Tirada de Salvación` naming;
- `Raza` terminology contract;
- compact resource/options design using v8 symbols where appropriate;
- authentic Equipment/Gemas continuation;
- regular body typography for Gemas values and spell-slot totals;
- frozen base-page pixel-identical regression protection.

## Preservation rule

Do not recalibrate or redesign the Custom-v1 base or Extended pages unless:

- the owner reports a new visible defect;
- a production-data integration exposes a real bounded defect;
- or a product requirement explicitly reopens the baseline.

Green CI alone is not authority to change the visual baseline.

## Strategy carry-forward

The layered strategy, observations, failure lessons and Custom-v2 carry-forward rules are consolidated in:

`docs/PC_SHEET_CUSTOM_EXTENDED_STRATEGY.md`

Custom-v2 Extended work must read that document before implementation.

## Next gate

Custom v1 visual-family design/QA is closed.

Next visual-family work:

- Custom v2 family-matched Extended pages;
- preserve both frozen v2 first-page modes and shared v2 pages;
- apply the approved layered methodology without blindly copying v1 geometry.

PR #85 remains **DRAFT / DO NOT MERGE** until Custom-v2 Extended and remaining production/end-to-end renderer gates are complete.

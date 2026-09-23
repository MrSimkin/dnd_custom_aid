# Checkpoint — Custom v1 Extended Run 6 — PASS FOR OWNER REVIEW

**Date:** 2026-09-20 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Final proof implementation commit:** `69b308f3d5d493d06bd0107ac66c7524935aa9fa`  
**Scaffold push run:** `35529317947` / run #2885 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10609869599`  
**Artifact archive SHA-256:** `10d492f071db27b4bfc29c323d5841022e685164361fece4144e8ba6ba2c336a`  
**Proof PDF:** `custom-v1-complete-family-extended-run6.pdf`  
**Proof PDF SHA-256:** `03212b642ba9b8414e18344dbe90b6d68d623d14a0cd1ff712544063548eafe5`  
**Layer diagnostics PDF:** `custom-v1-extended-run6-layer-diagnostics.pdf`  
**Diagnostics SHA-256:** `db6e5d8f1d37f74fb9847fc82ceb4820f1f33cb29303ee3f81f2b7fceb5e5a6b`

## Owner feedback that superseded Run 5

Run 5 was owner-reviewed and **NOT APPROVED / SUPERSEDED**.

The owner identified three bounded regressions:

- page 8 ignored the proportions/density already established by the Custom-v1 sheet and used oversized rows that consumed too much space;
- page 9 disturbed an Equipment/Gemas continuation that had already been working correctly in earlier runs;
- page 6 still failed the sheet's Ability/Attribute typography, vertical spacing and naming conventions.

## Run-6 correction strategy

Run 6 keeps the artifact-free layered approach from Run 5 but restores the frozen Custom-v1 sheet grammar rather than inventing new proportions.

### Page 6 — Custom Statistics

- Attribute headings again use the owner/source `EnchantedLand` heading font.
- Attribute naming follows the frozen page-1 convention: the three-letter stat key is uppercase inside the name:
  - `HONor`;
  - `RESolución`;
  - `SUErte`;
  - `INTeligencia`;
  - `SABiduría`;
  - `DEStreza`.
- `Tirada de Salvación` is restored in full.
- Ability/skill labels return to the measured source-matched treatment: Fira Sans Regular, 10 pt, 60% horizontal scale.
- Save/skill rows return to the source cadence of approximately 14.173 pt rather than Run-5's loose 22 pt rhythm.
- Run-5's clean ornamental score/modifier crop and artifact-free construction are preserved.
- `Definiciones` and `Notas de Estadísticas Personalizadas` retain their symmetric construction.

### Page 8 — Resources & Options

- Resource and option rows return to the Custom-v1 sheet's compact approximately 20 pt cadence.
- The giant Run-5 40 pt resource rows are removed.
- Resource tracking keeps the approved Para Hoja de PJ Symbols v8 counter grammar.
- `Opciones` returns to the proportions and checkbox grammar of `Equipo Especial`.
- Standalone `Recuperación` and `Estados` extension panels remain removed.
- Page 8 remains native structure with no cleanup-mask dependency.

### Pages 9–10 — restore proven typography

- Equipment/Gemas geometry remains the authentic source Equipment continuation.
- Gemas values return to regular body typography rather than the Run-5 semibold deviation.
- Spell-slot counts remain visible but likewise return to the proven regular body typography.
- No redesign of the Equipment/Gemas page is introduced.

## Final verification

CI #2885:

- backend — SUCCESS;
- hosted database — SUCCESS;
- Kotlin/build/tests — SUCCESS;
- Android debug APK upload — SUCCESS;
- PC sheet source renders upload — SUCCESS;
- populated PC proof artifact upload — SUCCESS.

Exact final PDF preflight:

- 11 pages;
- unencrypted;
- PyMuPDF-openable;
- not scanned;
- no XFA.

Visual/renderer checks:

- frozen pages 1–5 remain protected by the pixel-identical regression guard;
- pages 7 and 11 are pixel-identical to Run 5;
- page 6 renders the restored decorative headings, three-letter naming convention, full `Tirada de Salvación` labels and compact source-like vertical rhythm;
- page 8 uses compact sheet-density rows and no oversized Run-5 bands;
- page 9 returns Equipment/Gemas to the prior body-font treatment;
- page 10 keeps visible spell-slot counts with regular body typography;
- exact final PDF was rendered independently with both PDFium and Poppler; targeted pages show no renderer-specific missing glyphs, clipping or layout failures.

## Current gate

**PASS FOR OWNER REVIEW.**

This is **not** owner approval and does not freeze the Custom-v1 Extended family.

The owner must review the complete 11-page Run-6 proof. Only explicit owner approval may mark the Custom-v1 Extended family OWNER APPROVED / FROZEN.

PR #85 remains **DRAFT / DO NOT MERGE**.

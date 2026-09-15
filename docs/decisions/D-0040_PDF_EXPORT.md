# D-0040 — Character-sheet PDF export is local on Android and DM desktop

**Status:** Approved historical foundation; expanded/superseded where conflicting by D-0074  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Current controlling PDF decision:** `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`

Character-sheet PDF export is required on both the Android application and the native DM desktop/laptop application.

D-0074 later makes the surface requirement explicit as Player Android + DM Android/tablet + DM Desktop, and defines the complete product behavior.

## Architecture

- Export is generated locally and must not require Neon, Cloudflare, Descope, or Internet connectivity.
- The owner's existing non-fillable Adobe InDesign-generated PDFs remain presentation/output templates rather than the authoritative character data model.
- The original technical direction was Android **PdfBox-Android** and desktop **Apache PDFBox** for local generation.
- Android and desktop may share template/layout metadata and field mapping where practical without creating a generalized cross-platform PDF subsystem merely for symmetry.
- PDF output may use saved state or deliberately unsaved edited values according to D-0027 without saving/committing them.

Under D-0073/D-0074, the exact PDF library is now a delegated technical choice rather than an owner approval gate. The original PDFBox direction may be retained if it cleanly supports the approved behavior, or changed technically if necessary while preserving local/offline static export.

## Scope correction

Earlier MVP summaries listed PDF regeneration/export explicitly under the Player Android surface and did not list it under DM desktop administration. The owner explicitly required desktop PDF export as well.

D-0074 later expands this further: the same Player-facing PC Sheet export is available from Player Android, authorized DM Android/tablet and authorized DM Desktop/PC Manager.

## Template handling

- Template PDFs belong under `assets/character-sheets/templates/`.
- Exact field coordinates, font sizing, overflow behavior and template version mappings are implementation details established from the owner-provided templates.
- Static base-page overlay remains useful where fidelity is desired.

The original D-0040 rule required an InDesign-side change request whenever PDF geometry itself needed to change. D-0074 supersedes that restriction for explicitly approved **App Modified Sheet** and design-specific **Extended Page** generation: the application may generate/adapt those pages without requiring a new owner InDesign-edit cycle merely because geometry changes.

Changes to the owner's canonical source PDF outside D-0074's generated/adapted behavior should still be recorded deliberately rather than silently replacing the source artifact.

## Proportionality

No server-side PDF service, HTML-to-PDF subsystem, fillable-PDF form engine, or enterprise document pipeline is required for the current personal-scale project.

D-0074 additionally requires the Classic family, design-specific Extended pages and an optional application-designed Spellbook, all still within the local/offline static-PDF model.

> Detail-record note: this file preserves the earlier foundation. For current product behavior read D-0074 first.

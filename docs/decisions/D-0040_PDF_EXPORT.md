# D-0040 — Character-sheet PDF export is local on Android and DM desktop

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

Character-sheet PDF export is required on both the Android application and the native DM desktop/laptop application.

## Architecture

- Export is generated locally and must not require Neon, Cloudflare, Descope, or Internet connectivity.
- The owner's existing non-fillable Adobe InDesign-generated PDFs remain presentation/output templates rather than the authoritative character data model.
- Android uses **PdfBox-Android** to load the approved template and overlay structured character values at defined layout coordinates.
- Desktop uses standard **Apache PDFBox** for the equivalent local generation workflow.
- Android and desktop may share template/layout metadata and field mapping where practical, but the project does not introduce a cross-platform PDF abstraction merely for symmetry.
- PDF output may use saved state or deliberately unsaved edited values according to D-0027; exporting unsaved values does not save/commit them.

## Scope correction

Earlier MVP summaries listed PDF regeneration/export explicitly under the player Android surface and did not list it under DM desktop administration. The owner has now explicitly required desktop PDF export as well. This decision therefore amends that surface-level omission without requiring Android/desktop feature parity generally.

## Template handling

- Template PDFs belong under `assets/character-sheets/templates/`.
- Exact field coordinates, font sizing, overflow behavior and template version mappings are implementation details to be established from the actual owner-provided templates.
- If implementation reveals that the PDF layout itself must change, the required InDesign-side change must be recorded in `assets/character-sheets/CHANGE_REQUESTS.md` rather than silently redesigning the owner's sheet.

## Proportionality

No server-side PDF service, HTML-to-PDF subsystem, fillable-PDF form engine, or enterprise document pipeline is required for the current personal-scale project.

This resolves the PDF generation/rendering portion of D-0009. D-0009 remains Pending until the remaining consequential architecture choices are approved.

> Safety checkpoint note: this decision is stored as a dedicated decision file on the active architecture branch and should be consolidated into the chronological `docs/DECISIONS.md` log before merge.
# Character Sheet PDF Templates

This directory is reserved for the owner's existing custom character-sheet PDF templates.

## Purpose

The preferred table workflow uses physical printed character sheets. The application is intended to keep a sufficiently current digital representation of each character so a player can:

- consult the character from phone/tablet when the physical sheet is unavailable; and
- regenerate/reprint the latest digitally recorded copy using an approved PDF template.

The PDFs stored here are presentation/output templates, not the authoritative character data model.

## Owner upload route

Upload blank/custom character-sheet PDFs to:

`assets/character-sheets/templates/`

Use meaningful filenames where practical. Do not overwrite an existing template merely to revise it; when a template materially changes, preserve enough version/history to understand which layout was used for generated sheets.

## Source files

The owner creates these sheets in Adobe InDesign. The current known PDFs are not fillable/editable PDFs.

The implementation may later render structured character data onto/into a PDF layout, but the exact PDF generation method has not been selected yet.

If implementation discovers that a PDF layout itself needs to change, the agent must:

1. record the required visual/layout change in Git;
2. explain exactly what must be changed and why;
3. identify the affected template/version;
4. tell the owner that an InDesign-side edit is required;
5. not silently redesign or replace the owner's sheet format.

## Do not infer requirements from a template

A field appearing on one PDF does not automatically mean it is required in every game system, campaign, or app screen. Templates are design/reference inputs and must be reconciled with approved product/data decisions.

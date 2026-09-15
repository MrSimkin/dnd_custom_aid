# Character Sheet PDF Templates

This directory contains the owner's custom character-sheet PDF templates used by the PC Sheet PDF-export capability.

## Purpose

The preferred table workflow uses physical printed character sheets. The application keeps a sufficiently current digital representation of each character so a Player or authorized DM can:

- consult the character from phone/tablet/desktop when the physical sheet is unavailable; and
- regenerate/share the latest digitally recorded copy using an approved PDF design.

The PDFs stored here are presentation/output templates, not the authoritative character data model.

## Controlling decision

Read:

- `docs/decisions/D-0074_PC_SHEET_PDF_EXPORT_PRODUCT_DEFINITION.md`;
- [`REFERENCE.md`](REFERENCE.md).

D-0074 is the current authority for export behavior and expands/supersedes narrower D-0040 assumptions where they conflict.

## Current source PDFs

- `Hoja de PJ - 5.0 - Simkin.pdf` — Custom v1;
- `Hoja de PJ v2 - 5.0 - Simkin.pdf` — Custom v2 source containing two alternative first-page organizations.

The PDFs themselves remain the authoritative visual references for their base designs.

The v2 first pages are alternatives selected at export time:

1. per-Attribute organization;
2. separate saving-throw/abilities organization.

They are not normally exported together as two mandatory first pages.

## Rendering direction

The current product design requires more than fixed text overlay alone.

Implementation may use:

- the original static PDF pages as faithful backgrounds/templates and draw character data at measured coordinates;
- generated/adapted geometry for D-0074 **App Modified Sheet** modes;
- new design-specific Extended pages that visually belong to the selected base family;
- the independently designed Classic D&D-style family;
- the separate application-designed Spellbook appendix.

Exact PDF libraries, coordinate systems, font handling and pagination mechanics are delegated technical details.

## Visual fidelity rule

For Custom v1 and Custom v2, preserve the source design as closely as practical where the selected export mode calls for faithful base pages: typography, sizing, colors, borders, spacing, image areas and overall visual language.

Extended and App Modified pages may alter geometry as explicitly approved by D-0074, but should remain recognizably part of the selected family.

Under the project's current personal-use/non-publishing scope, preserve the existing D&D branding and visual identity in the owner templates. If distribution scope changes later, review that separately rather than preemptively redesigning the personal-use sheets now.

## Portraits

Both custom template families contain intentional PC-image/portrait areas. PDF export must map the PC portrait there when available and honor the D-0074 Crop-to-fill vs Fit-entire-image choice.

## Blank writable space

Do not compact away unused sheet areas merely because the digital character has no value for them. Blank rows/boxes/notes areas remain useful for handwriting on printed sheets.

## Durable visual/terminology companion

Read [`REFERENCE.md`](REFERENCE.md) together with the PDFs. It records terminology, page/grouping structure and known app/PDF wording mismatches so future work does not require repeated binary re-upload merely to recover already-captured context.

When visual fidelity matters and rendered/binary access is available, the PDFs themselves remain the authoritative visual references.

## Source files

The owner creates these sheets in Adobe InDesign. The current PDFs are not fillable/editable form PDFs.

An InDesign-side owner edit is **not** automatically required merely because implementation needs an App Modified or Extended page: D-0074 explicitly authorizes the application to generate/adapt those pages.

If implementation instead discovers a proposed change to the owner's canonical source PDF itself that is outside the approved generated/adapted behavior, record it in `assets/character-sheets/CHANGE_REQUESTS.md` and explain the affected template/version and reason.

## Do not infer new product requirements from a template

A field appearing on one PDF does not automatically mean it is required in every app screen or data model. Templates are design/reference inputs and must be reconciled with approved product/data decisions.

Likewise, do not invent companions, forms, mounts or other character subsystems merely to fill an Extended page. Extensions represent data the PC model actually contains.

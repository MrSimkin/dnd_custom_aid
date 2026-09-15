# Checkpoint — PC Sheet PDF export product closure

**Date:** 2026-09-14  
**Branch for this documentation pass:** `docs/pdf-export-product-closure-2026-09-14`  
**Base `main` verified before edits:** `9ce536d2dee146311a32b23993279ca2942dbcca`  
**Product decision:** D-0074  
**Scope:** documentation/product/technical alignment only; no product-code or branch-convergence implementation

## Why this checkpoint exists

After the integrated-MVP technical-readiness review had reached the implementation-authorization gate, the owner remembered a cross-surface PC Sheet PDF-export requirement that had not been fully product-defined.

The implementation gate was deliberately reopened for that feature rather than beginning code with an unresolved paper-first capability.

The owner then supplied/reconfirmed the two existing custom PDF templates already present in the repository and closed the product behavior through sequential discussion.

## Source templates revalidated

Repository templates:

- `assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf`;
- `assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf`.

Both are static/non-fillable owner-created 5-page PDFs suitable for template/background use plus generated content.

The v2 source contains two alternative first-page layouts:

1. skills/abilities grouped by governing Attribute;
2. separate saving-throw and abilities/skills blocks.

The owner confirmed these are alternatives selected according to comfort rather than two mandatory pages in one export.

## Product closure

D-0074 now defines the complete capability.

Key closed behavior:

- export exists on Player Android, DM Android/tablet and DM Desktop/PC Manager;
- DM generates the same Player-facing PC Sheet rather than a special DM/audit document;
- export-time visual choices are Classic D&D-style, Custom v1, Custom v2-per-Attribute and Custom v2-per-Ability;
- custom v1/v2 preserve current branding/visual identity under the current personal-use scope;
- output is static PDF, local/offline, Save/Share only and carries no app/export metadata footer;
- user chooses Permanent/Character Sheet state or Current Snapshot;
- portrait behavior is selectable Crop-to-fill or Fit-entire-image; missing uncached portrait never blocks offline export;
- blank writable areas are preserved;
- moderate font reduction/condensation is allowed down to a hard readability floor;
- overflow gets a visual continuation cue and matching purpose-specific Extended pages;
- no data is silently truncated or discarded;
- custom Attributes/Abilities are automatic and non-optional in a complete sheet;
- custom-stat presentation choices are Extended Page, App Modified Sheet, or Modified Sheet plus the complete Extended Page;
- each visual family receives its own native-looking Extended-page family rather than one generic layout;
- only custom-stat mode 3 deliberately duplicates the complete custom-stat extension; normal overflow merely continues;
- optional Include Spell Descriptions appends an application-designed Spellbook organized by level then alphabetically, with index and full usable spell information including PC-specific casting values/source where known;
- actual PC resources use normal or matching Extended-page space as required;
- unrelated speculative PC subsystems are not invented by the exporter.

## Technical alignment

The feature should be one semantic export capability across all clients, not three unrelated implementations.

Recommended shape:

```text
canonical PC/export snapshot
-> shared export semantics / render plan
-> selected visual family
-> platform renderer
   -> template overlay for faithful pages
   -> generated/adapted pages for Modified/Extended output
   -> shared Spellbook semantics/design
```

The exact PDF library, coordinate system, font handling, pagination heuristics and render implementation remain delegated engineering decisions under D-0073.

D-0040's local/offline/template principles remain sound. D-0074 explicitly relaxes its earlier overlay-only/layout-change assumptions where the newly approved App Modified/Extended behavior requires generated geometry.

No external service/account activation is required for PDF generation.

## MVP boundary impact

PC Sheet PDF Export is now explicitly protected from later trimming as part of the integrated MVP.

It is not a cosmetic post-MVP report. It supports the core paper-first recovery scenario: the DM may hold current digital PCs and regenerate/share a Player's sheet when the physical paper copy is unavailable or lost.

## Implementation placement

Exact wave/package placement is delegated, but implementation should occur after the shared PC/domain state is coherent enough to produce one canonical export snapshot. Surface integration may then be delivered through Player Android, DM Android and Desktop without semantic duplication.

Integrated QA should cover at least representative combinations of:

- each visual family;
- custom-stat modes;
- portrait fit/crop;
- Permanent vs Current Snapshot;
- overflow/continuation pages;
- custom Attributes/Abilities;
- resources;
- offline export;
- Save/Share;
- optional Spellbook.

## Current gate after closure

The PDF product-definition gate is now **CLOSED**.

Technical readiness remains sufficient; no newly discovered PDF implementation detail requires owner rubber-stamp approval.

The next owner-level decision returns to:

> **Authorize beginning the integrated-MVP implementation, starting with the protected `main` + Player-successor convergence.**

Until that authorization is explicit, this pass remains documentation-only and must not execute product-code convergence or hosted/DM implementation.

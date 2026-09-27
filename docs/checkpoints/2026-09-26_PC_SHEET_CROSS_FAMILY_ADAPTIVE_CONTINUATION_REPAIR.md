# Checkpoint — Cross-family adaptive continuation repair

**Date:** 2026-09-26 (Chile local time)  
**Base main:** `a468c7e81e352657f29f3f084ad8511d7ca82845`  
**Active branch:** `repair/pc-sheet-adaptive-continuations-cross-family`  
**Status:** CROSS-FAMILY VISUAL/PAGINATION ARCHITECTURE REPAIR ACTIVE

## Why this package exists

Mara preqa.8 proved that the remaining PDF problem is not a sequence of isolated Mara/Custom-v2 bugs.

The central defect is architectural:

- continuation pages are too template/scaffold-centric;
- a single overflowing content family can generate many mostly-empty pages;
- surviving content cannot reclaim space from exhausted sibling panels;
- page count and readability are therefore both worse than necessary.

This requirement applies to **all PDF families**:

- Fantasy Sheet;
- Custom v1;
- Custom v2 · Atributo;
- Custom v2 · Habilidad.

Mara is the canonical high-volume stress fixture; the owner is not required to manually reproduce the same architecture failure in every family.

## Owner-loop policy

Do **not** use the owner as the primary PDF defect detector for this package.

Before any new owner APK is requested:

1. generate Mara PDFs in CI for all four families;
2. download and inspect the generated artifacts internally;
3. run automated structural/text/layout invariants;
4. require semantic completeness from the fixture;
5. require adaptive packing / no empty-scaffold regressions;
6. require known Mara visual defects to be closed in automated or inspectable artifacts;
7. only then ask the owner for one final real-app acceptance pass.

Owner QA after this package should be confirmation, not iterative discovery.

## Cross-family continuation invariants

All export families must obey:

1. continuation space is allocated according to **remaining content**, not fixed page templates;
2. exhausted sibling sections surrender their physical space to surviving sections;
3. available columns/rows are consumed before a new page is created;
4. no empty repeated scaffold is rendered solely because another section still overflows;
5. semantic record order and boundaries remain explicit;
6. cross-page records identify continuation instead of starting with an anonymous tail;
7. readable typography floors remain fail-closed;
8. page count is driven by actual content height/rows;
9. no semantic content may disappear during repacking.

Family-specific visual grammar remains allowed; the packing invariants are shared.

## Mara visual defects that must close before owner rerun

The canonical source remains:

`docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`

Required closure includes:

- custom-attribute **card/box graphical artifacts**, not only Éter text;
- phantom custom-stat cards;
- Traits continuation page waste;
- Combat header/first-row collision and row cadence;
- Resources/Options empty-panel repetition;
- Inventory grouping/association and custom-location artifacts;
- redundant empty Equipment/Special pages;
- Notes record separation and continuation ordering;
- stale source-template/underlay text where applicable.

## Validation strategy

### Automated semantic gates

Using the real Mara fixture, require every family to preserve all applicable source semantics, including counts/identities for:

- custom attributes/skills;
- traits;
- resources;
- custom markers;
- class options;
- combat entries;
- inventory;
- note cards.

### Automated structural/packing gates

Add family-appropriate tests for:

- no phantom empty custom-stat panels;
- no pages whose dominant continuation panel is empty while another panel overflows;
- no repeated empty sibling scaffold after that sibling stream is exhausted;
- no header/data-row overlap;
- note-card boundary markers preserved;
- continuation page count bounded by actual row capacity rather than fixed sibling templates.

### Artifact review

CI must publish the generated Mara PDFs/renders for all four families. Inspect those artifacts internally before owner handoff.

## Current implementation direction

Do not attempt one renderer implementation shared verbatim across all families.

Current code confirms:

- Fantasy Sheet is programmatic and does not depend on imported template forms for continuation pages;
- Custom v1 continuation pages still reuse/import source-template forms and mask/relabel them;
- Custom v2 mixes programmatic Extended structure with limited imported source resources/forms.

Therefore:

- share **packing policy/invariants** where practical;
- repair renderer implementation per family;
- do not preserve a fixed scaffold merely to match historical continuation pages when it produces unusable PDFs;
- frozen base-page visual authorities remain frozen unless a concrete base-page defect is part of this package.

## Owner boundary

No owner PDF generation or manual comparison is requested while this package is active.

Mara 3B, Current Snapshot and Media / Handouts remain blocked.

The next owner request should occur only after a repository-green candidate has passed cross-family Mara artifact review.

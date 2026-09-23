# Checkpoint — Custom v2 Extended Production Promotion — Pass 1

**Date:** 2026-09-20/21 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Frozen visual authority:** Custom v2 Extended Run 7  
**Final Pass-1 head:** `19f4e117fe10884e0d330c600892a3c1c37d865f`  
**PR Scaffold:** `35549031512` / #2963 — SUCCESS  
**Artifact:** `10617622322`

## Scope

Promote the OWNER APPROVED / FROZEN Run-7 Custom Statistics Extended page from QA/evaluation code into the real renderer driven by `PcSheetPdfRenderPlan`.

This pass covers both frozen Custom-v2 modes:

- Custom v2 — per Attribute;
- Custom v2 — per Ability.

The five shared Extended roles remain for subsequent promotion passes.

## Production implementation

New renderer:

`desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopCustomV2ExtendedRenderer.kt`

Whole-draft wiring:

`DesktopPcSheetWholeDraftRenderer` now appends mandatory Custom-v2 Extended pages from `plan.mandatoryExtendedPages`.

The renderer consumes canonical planner/model data:

- `PcSheetCustomAttributeProjection`;
- `PcSheetCustomSkillProjection`;
- calculated custom saving-throw totals;
- calculated custom-skill totals;
- custom Attribute notes;
- real proficiency/expertise state.

No Run-7 calibration values are production defaults.

## Preserved frozen mechanics

Production Pass 1 preserves:

- five semantic OCG layers;
- transparent source-derived v2 score/modifier ornament;
- source v2 gray tones;
- Corbel/Fira/Symbols-v8 roles;
- source-measured Corbel transforms;
- per-Attribute relationship grammar;
- separate per-Ability columns;
- empty proficiency boxes as part of the visual grammar;
- Custom-v2 Attribute three-letter key naming;
- abbreviation-prefixed Definitions/Notes;
- source-family row cadence and geometry.

## Capacity policy

Pass 1 refuses silent truncation beyond the currently promoted physical capacities.

It fails explicitly when overflow pagination is still required, rather than dropping canonical data.

These bounded capacity guards are temporary until continuation pagination is promoted.

## QA correction history

Initial production build compiled after two narrow Kotlin fixes.

First exact-artifact visual review found two visual drifts:

- unused marker slots were omitted;
- built-in anchor ordering had changed from the frozen Run-7 presentation.

Final correction commit:

`19f4e117fe10884e0d330c600892a3c1c37d865f`

This restored the full empty-marker grammar, preserved first-seen canonical skill-group ordering, and restored the Attribute abbreviation prefix in Definitions/Notes.

## Final exact-artifact verification

Per-Attribute production PDF:

- `custom-v2-per-attribute-production-pass1.pdf`;
- 5 pages;
- Letter / 612 x 792 pt;
- not encrypted;
- SHA-256: `9bb58644589465f09e3c7c44e4d782e2dca224a5645d1ab1c5ba5a32ce73b78e`.

Per-Ability production PDF:

- `custom-v2-per-ability-production-pass1.pdf`;
- 5 pages;
- Letter / 612 x 792 pt;
- not encrypted;
- SHA-256: `4c4ee2c2fbe2f4788828844d59e47c25653eb115cc3c780250e3583effb6ae05`.

Both production Extended pages were independently inspected in PDFium and Poppler against frozen Run 7.

No missing glyphs, clipping, overlap, opaque crop artifacts or renderer divergence were observed.

## Pass-1 conclusion

**CUSTOM V2 EXTENDED PRODUCTION PROMOTION PASS 1: PASS**

The approved Custom Statistics mechanics are now real-plan-driven production renderer code for both v2 modes.

## Next development step

Promote the five shared Run-7 Extended roles:

1. Traits & Features;
2. Resources & Options;
3. Inventory / Equipment;
4. Spells;
5. Notes.

Use canonical CharacterSheet / Closure / Successor data only.

After all shared roles are promoted, generate complete production-integrated Custom-v2 families, exercise dense/empty/long data and continuation routing, and run the final production audit before owner review.

PR #85 remains **DRAFT / DO NOT MERGE**.

# Checkpoint — Custom v2 Extended Production Promotion — Pass 2

**Date:** 2026-09-20/21 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Frozen visual authority:** Custom v2 Extended Run 7  
**Final Pass-2 head:** `698ca4f958abd7504ebba14d7ad9ada34ff84097`  
**Scaffold push:** `35549649541` / #2972 — SUCCESS  
**Artifact:** `10617617960`  
**Artifact digest:** `sha256:389f70c1e83d7830f0e1668a5b9f9e75dc61c9e04be284da340afdb254822b80`

## Scope

Promote two shared OWNER APPROVED Run-7 visual roles into real-plan-driven Custom-v2 production rendering:

1. Traits & Features;
2. Resources & Options.

This pass does not make any Run-7 fixture name, resource, trait, option, value or description part of the product model.

Run 7 is the rendering/layout exemplar. Canonical output content comes only from the selected `PcSheetPdfRenderPlan` / character aggregate.

## Warrant rules

Traits & Features is appended only when real character data needs representation beyond the base-v2 trait-name surface, including:

- trait count beyond base capacity;
- trait-local uses/recovery/notes requiring the detailed role;
- character proficiencies/languages requiring the continuation role.

Resources & Options is appended only when the character actually contains canonical `CharacterResource` and/or `CharacterClassOption` data.

A character with none of those needs receives neither page.

## Production data mapping

Traits & Features consumes real:

- `CharacterTrait`;
- trait-local `maxUses`, `spentUses`, `recovery`, activation, source, description and notes;
- `CharacterProficiency`.

Resources & Options consumes real:

- `CharacterResource`;
- `CharacterClassOption`;
- closure resource-recovery cadence where applicable.

The renderer preserves the approved semantic rule that trait-local use/recovery state stays with the trait rather than being duplicated as an independent CharacterResource.

## Frozen visual mechanics preserved

- five semantic OCG layers per appended page;
- Run-7 page geometry and row cadence;
- source-v2 gray tones;
- Corbel/Fira typography roles;
- source-measured heading transforms;
- Symbols-v8 small resource counters;
- textual X/Y fallback for larger maxima;
- one active-state marker per option row;
- `Raza` terminology contract.

## Capacity behavior

No silent truncation is accepted.

The current promoted physical capacities are guarded explicitly; later continuation pagination may extend them, but exceeding a promoted capacity must fail rather than discard canonical data.

## Exact-artifact verification

Per-Attribute Pass-2 PDF:

- `custom-v2-per-attribute-production-pass2.pdf`;
- 7 pages;
- Letter / 612 x 792 pt;
- not encrypted;
- SHA-256: `efeaf3ad950575a51e53b09237d1cf8fdfd23d932fb4220dacdb872e388f078a`.

Per-Ability Pass-2 PDF:

- `custom-v2-per-ability-production-pass2.pdf`;
- 7 pages;
- Letter / 612 x 792 pt;
- not encrypted;
- SHA-256: `34712028eed7afdb1dda4de0571a565416f84ac553ff57efe24494ca4f384cb6`.

The exact artifact Traits and Resources pages were independently inspected in PDFium and Poppler.

Observed:

- no missing glyphs;
- no clipping;
- no opaque crop artifacts;
- no cross-renderer structural divergence;
- dynamic resource values correctly use symbols for small maxima and textual `7/12` for a large maximum;
- no forbidden `Especie` terminology;
- no `Dados de portento` fixture leakage.

## Pass-2 conclusion

**CUSTOM V2 EXTENDED PRODUCTION PROMOTION PASS 2: PASS**

## Next development step

Promote the remaining shared roles from the frozen Run-7 visual grammar:

1. Inventory / Equipment;
2. Spells;
3. Notes.

Each page must be appended only when real model data/overflow warrants it. Run-7 sample content remains QA fixture data only.

PR #85 remains **DRAFT / DO NOT MERGE**.

# Checkpoint — Custom v1 Integrated Production Audit — PASS

**Date:** 2026-09-21 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Integrated audit head:** `5d09271231dd395e44f0c4c2a33cdb39509cc6b5`  
**Final push Scaffold:** `35647130040` / run #3097 — SUCCESS  
**Final PR Scaffold:** `35647134492` / run #3098 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10660478619`  
**Artifact digest:** `sha256:79b446d97c9153c1fb87a49a33ece60c14a6c8db1634c0fb354565c1d33e340a`

## Purpose

Close the production promotion and integrated semantic audit for the owner-approved Custom-v1 base + Extended Run-6 family.

This checkpoint does **not** reopen or redesign the frozen visual family. It records that the approved mechanics are now driven by real `PcSheetPdfRenderPlan` data, paginate when canonical content requires continuation, preserve Current Snapshot semantics, and retain the five frozen base pages unchanged.

## Owner-approved visual authority preserved

The visual authority remains:

- Custom-v1 base Run 7 — OWNER APPROVED / FROZEN;
- Custom-v1 Extended Run 6 — OWNER APPROVED / FROZEN;
- six data-driven extension roles:
  1. Custom Statistics;
  2. Traits & Features;
  3. Resources & Options;
  4. Inventory / Equipment;
  5. Spells;
  6. Notes.

Production code reuses those approved mechanics rather than recalibrating them.

## Production promotion sequence

The bounded role-by-role production work closed as follows:

- Custom Statistics — production promotion began at `da4bc41a32b903d210b6380604502874dd362f1f`;
- Traits & Features — final continuation proof at `3b246a0ccb900b1c60988598703245e75c417119`;
- Resources & Options — final role proof at `3ef2005d383d9538ee54195ba776bdc6d7e469e9`;
- Inventory / Equipment — reading-order correction at `d3366f49b4e8de974160c0cff9408fd703f75106`;
- Spells — final two-page overflow regression at `4a05db1135194ed528a78c8e3a13216bdd4ccc64`;
- Notes — production promotion at `bcb5d41a985d88961730622d4d9dc26d24665fa8`;
- canonical/current-state semantic completeness repair — `b735cb76e28c1fa5d84caccf275b97d9dc61120b`;
- final nonstandard-currency unit repair — `5d09271231dd395e44f0c4c2a33cdb39509cc6b5`.

## Integrated semantic audit

The final Custom-v1 production renderer preserves canonical data that the frozen base pages cannot fully express, while avoiding redundant Extended pages when the base already carries the information.

Audited continuation semantics include, where present:

- background summary/religion and genuine personality/ideal/bond/flaw/story overflow;
- canonical race/subrace/background identity when it differs from the compatibility projection;
- subclass identity and long class identity;
- combat/actions beyond native capacity or entries whose type/notes are not expressible by the native table;
- milestone progress;
- inspiration;
- temporary HP, death saves and adjusted passive perception;
- weapon masteries;
- exhaustion and concentration;
- conditions and defenses;
- alternate movements and senses;
- active temporary effects;
- structured combat damage;
- additional spellcasting-source reference values;
- forms and companions;
- inventory usage/current-state semantics;
- custom currency amounts without falsely labelling their unit as `po`.

Current Snapshot regression coverage verifies representative temporary/current data in a real Custom-v1 export.

## Data-driven pagination / no-silent-loss evidence

The production tests exercise actual overflow rather than only checking that an extra page exists:

- Custom Statistics — multi-page custom Attribute/custom-skill stress;
- Traits & Features — multi-page trait/proficiency/reference continuation;
- Resources & Options — wrapped resource/option rows and custom markers;
- Inventory / Equipment — ordinary metadata, special equipment, treasure overflow and nonstandard currency;
- Spells — 30 level-1 spells: native base capacity 10, then #11–20 and #21–30 on two matching continuation pages;
- Notes — native 34-line capacity, with line 35 onward continued on the matching Notes page.

No tested canonical terminal row is silently dropped.

## Frozen-base preservation

The final artifact's `custom-v1-whole-draft-page-1.png` through page 5 were byte-compared against the Pass-1 production artifact `10620106290`.

All five pairs are exact matches:

- page 1 SHA-256: `4c3b2d7a3b287739f8fe4e3c0e6f20ad0538a3338f082d5b0078ebe36c45f805`;
- page 2 SHA-256: `e35b8f642d95bf2b8bfcc0c64213830d20a71edb01ee7a73b2ea899077fa9ecc`;
- page 3 SHA-256: `3e6b0df734faa7c57c24c75724026fcd4cf2ad54afd79f1d68824e4bd02821fc`;
- page 4 SHA-256: `76f46d2c4e3b0a04894cf896b870d49734be32eca600e9ec95b5b1dbb0dde993`;
- page 5 SHA-256: `6b9b5262134e4d1072079a15d686149edb92734ddfe4bc971d9412176cc6d90a`.

**Frozen Custom-v1 base preservation: PASS.**

## Representative final proof hashes

From artifact `10660478619`:

- `custom-v1-whole-draft.pdf`  
  SHA-256: `c3e2ac5ca47c551f8c567900a5ea188e3b9d055122e1f5759660ec8ebf793f3f`
- `custom-v1-current-snapshot-semantics.pdf`  
  SHA-256: `f186b3c3cdca6fa02877b14ca0ab8701019b4045a6549d8dbfc6973c68c9e21a`
- `custom-v1-production-extended-stats-pass1.pdf`  
  SHA-256: `f491681a58996a28feeb0e444d441553c129fa87fce3b2833c6cc6a3d5257b97`
- `custom-v1-production-extended-traits-pass2.pdf`  
  SHA-256: `ce5317abd41cfa4c5b6e4b325448303f77cb6bf31038513d796bdca4937b972f`
- `custom-v1-production-extended-resources-pass3.pdf`  
  SHA-256: `f804b27a63269cd7c915ae02c13855c2c1be8949017903af9ace0c0e67835b0a`
- `custom-v1-production-extended-inventory-pass4.pdf`  
  SHA-256: `8dffb8017adbb43506e246977356222f4d3764c700fe4a1a668c4e50e9d34253`
- `custom-v1-production-extended-spells-pass5.pdf`  
  SHA-256: `5b7ce972e049ac77b48a4e846bec646970ed5e3c11d2e0c9738b5f18f658edae`
- `custom-v1-production-extended-notes-pass6.pdf`  
  SHA-256: `99a7ed86de30ce4f79abcc7d12c9840da5f0238329d8ebb11d316bcdb0ae3f11`
- `custom-v1-custom-stats-overflow.pdf`  
  SHA-256: `98c8c4ecff2a599c3879267af5d833ab601e36b025285dc056ab36a600c0bb44`

## PDF preflight / visual audit

Representative Current Snapshot proof:

- 11 Letter pages, 612 × 792 pt;
- not encrypted;
- 0 AcroForm fields;
- 0 attachments;
- 0 annotations;
- owner-source and production text/symbol fonts present.

Role artifacts were visually inspected during promotion. The final semantic Current Snapshot continuation and final Inventory/currency proof were also visually inspected after the last repairs.

No material clipping, overlap, broken glyph, black-square artifact or reading-order defect remains in the inspected v1 production proofs.

## Deliberately unresolved D-0074 gates

This checkpoint closes **Custom-v1 production**, not the complete PDF-export product.

Remaining known product gates are:

1. Classic Run-2 production promotion from owner-review fixture code into a real plan-driven renderer;
2. application-owned optional appended Spellbook;
3. platform portrait-byte handoff plus Crop-to-fill / Fit-entire-image rendering;
4. Save/Share integration on approved app surfaces;
5. `APP_MODIFIED_SHEET` visual implementation, which remains an owner-facing visual gate;
6. originating-section continuation cues on frozen base sheets, which require an explicit bounded visual decision/review.

Custom-v2 production was already closed by:

`docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V2_INTEGRATED_PRODUCTION_AUDIT_PASS.md`

## Conclusion

**CUSTOM V1 INTEGRATED PRODUCTION AUDIT: PASS**

Custom-v1 base + matching Extended pages are now production-driven for the audited renderer scope, with no known silent canonical-data loss and with the five frozen base pages preserved exactly.

Next implementation gate:

> Promote the owner-approved Classic Run-2 complete family into a real `PcSheetPdfRenderPlan`-driven production renderer without reopening its frozen visual design.

PR #85 remains **OPEN / DRAFT / DO NOT MERGE**.

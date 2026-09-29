# Mara 50800 — Phase 2B.3 residual Equipment pre-code QA/source map

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Status:** ACTIVE / BLOCKING PRE-CODE MAP FOR PHASE 2B.3  
**Acceptance authority:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`  
**Previous progression gate:** `docs/checkpoints/2026-09-29_PC_SHEET_MARA_50800_PHASE2B2_CLOSURE.md`

## 1. Scope

Phase 2B.3 is restricted to:

- M50800-17 — ordinary Equipment identity remains visually atomic/readable;
- M50800-20 — exhausted Equipment/Equipo Especial streams do not leave redundant continuation scaffolds.

M50800-15/16/18/19 remain regression guards and are not reopened unless exact artifact evidence shows regression.

No Combat, Resources/Options, Fantasy or final-candidate work belongs to this phase.

## 2. Real 50800 acceptance wording

M50800-17:
- defect: item identity and metadata/descriptions were fragmented across physical streams/columns;
- expected: each compact equipment identity remains visually atomic and readable within the reused native Equipment grammar.

M50800-20:
- defect: later Inventory pages repeated empty Equipment/Equipo Especial structures after useful content was exhausted;
- expected: do not emit empty continuation scaffolds; surviving content reclaims the area; no page exists solely to preserve an exhausted template.

The owner clarification remains **reuse before reconstruction**.

## 3. Exact current artifact recheck

Exact artifact before this subphase:

- renderer/code head `5c5759a9699316bad27d4d032f31f082215045fe`;
- workflow 4244 / `36594894685` — SUCCESS;
- artifact `11045852152`.

Real Mara Custom-v2 Atributo page 2 shows ordinary Equipment using the native two-column module, but long compact identities wrap onto a following ruled row, e.g. the item ending in `última palabra escrita 2` is visually split across rows. The semantic content is preserved, but row allocation is currently a flat line stream rather than an item-aware stream.

Real Mara continuation page 18 shows the stronger M50800-20 failure:

- useful overflow is only Equipo Especial;
- the full source Equipment/Trasfondo page is copied;
- the upper Equipment module is completely empty;
- Trasfondo/Vínculos/Ideales/Historia are also empty;
- Equipo Especial remains at the bottom of the page;
- therefore most of the physical page is dead scaffold.

This is the exact 50800 defect class and blocks Phase 2B.3 progression.

## 4. Current implementation mismatch

### Ordinary Equipment

Base renderer:
`DesktopCustomV2SharedBaseRenderer.drawEquipment`

Extended renderer:
`DesktopCustomV2ExtendedRenderer.appendInventoryExtendedPages`

Both flatten wrapped item identities into one list of strings and then slice by raw row count. This loses the item boundary as a physical pagination unit.

### Continuation scaffold

`renderNativeEquipmentContinuation` always draws `resources.forms[2]`, the complete native Equipment/Trasfondo page, regardless of which streams actually survive.

When ordinary Equipment is exhausted and only special Equipment remains, the renderer therefore preserves large empty sibling modules instead of reclaiming the page.

## 5. Intended repair

### M50800-17 — item-aware native row packing

- preserve ordinary Equipment as compact identity only;
- wrap each item using the native Equipment typography/width;
- treat all wrapped rows for one item as one semantic group;
- if the complete group does not fit in the remaining native column/page rows, move the item as a whole to the next native column/page;
- never split one compact identity merely to fill the last row of a column;
- preserve source `sortOrder` and native two-column grammar.

A single item that intrinsically exceeds one full native column remains an explicit exceptional case and must be handled without silent data loss.

### M50800-20 — surviving native-module continuation

Continuation layout is selected by surviving streams:

1. ordinary Equipment + Equipo Especial both survive:
   - full native source page reuse remains valid because both source modules are active.
2. ordinary Equipment survives alone:
   - reuse/copy the native Equipment module without reserving empty Equipo Especial/narrative scaffold.
3. Equipo Especial survives alone:
   - reuse/copy the native Equipo Especial module and place it in reclaimed page space rather than leaving the entire upper page empty.
4. neither survives:
   - emit no native Equipment continuation page.

This is source-component reuse, not a new generic Inventory design.

## 6. Planned regressions/evidence

Automated:

- ordinary Equipment compact labels remain complete and ordered;
- no weight, `Consumible` or ordinary prose descriptions return;
- item-aware allocator never splits a multi-row identity across a column/page boundary when the group can fit;
- no continuation page is emitted for an exhausted stream alone;
- final Special Equipment semantics including `Bolsa lateral` and item 29 remain present;
- native checkbox preservation remains intact;
- Desktop/Android generated renderer sync remains exact.

Exact rendered artifact:

- inspect real Mara base Equipment page for atomic/readable identity grouping;
- inspect every continuation page in both Custom-v2 families;
- verify a special-only continuation no longer carries an empty full-page Equipment/Trasfondo scaffold;
- verify Equipo Especial remains recognizably the reused native component;
- verify no new clipping/overprint/checkmark regression.

## 7. Artifact gate

Phase 2B.3 may receive an **internal progression PASS** only after:

1. CI passes;
2. exact generated `pc-sheet-populated-template-proofs` is inspected;
3. M50800-17 and M50800-20 are checked against the rendered real-Mara pages;
4. M50800-15/16/18/19 regressions remain absent.

M50800-17/20 remain formally OPEN in the master matrix until final exact-candidate acceptance.

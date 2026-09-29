# Mara 50800 — Phase 3 Custom-v2 adaptive continuation pre-code QA/source map

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Status:** ACTIVE / BLOCKING PRE-CODE MAP FOR PHASE 3  
**Acceptance authority:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`  
**Previous progression gate:** `docs/checkpoints/2026-09-29_PC_SHEET_MARA_50800_PHASE2B3_CLOSURE.md`

## 1. Scope

Phase 3 is restricted to the remaining Custom-v2 adaptive continuation defects:

- M50800-12 — Combat/Actions table readability and packing;
- M50800-14 — Resources/Options reclaim after one sibling stream is exhausted;
- M50800-27 — broader adaptive continuation architecture as exercised by these surviving streams.

Traits, narrative, Notes and Equipment are regression guards only. Fantasy-specific M50800-13/26 remains Phase 4.

## 2. Real 50800 acceptance wording

### M50800-12

Owner defect:
- header labels collide with the first row;
- row heights are excessive;
- fields are difficult to associate;
- continuation page is mostly empty.

Required result:
- preserve a clear table grammar;
- keep headers separate from data;
- preserve readable association of identity/range/bonus/damage/effect/notes;
- use content-driven row height/packing.

### M50800-14

Owner defect:
- after Options are exhausted, a large empty Options scaffold is repeated while Resources continue.

Required result:
- exhausted streams stop reserving space;
- Resources/Markers reclaim available area before another page is created.

### M50800-27

Owner defect:
- fixed multi-panel/page scaffolds remain allocated after sibling streams end.

Required result:
- surviving content reclaims space before page advance while preserving semantic boundaries.

## 3. Exact current artifact recheck

Exact pre-Phase-3 artifact:

- renderer/code head `49afb6062de99a380b2b64673c28498f6cb4e147`;
- workflow 4252 / `36597017691` — SUCCESS;
- artifact `11046448289`;
- digest `sha256:25229a0a606ab5fd733870018945051a6f7185df37c96bcb85be7c976e0b99f4`.

### Combat

Real Mara Custom-v2 Atributo/Habilidad:

- page 11 contains the first seven continuation combat entries;
- the column header labels visibly overlap the first record;
- each wrapped text line consumes a 42 pt physical band, creating excessive vertical whitespace;
- page 12 contains only the final combat entry at the top and the remainder of the page is empty.

This is a direct reproduction of M50800-12.

### Resources / Options

Real Mara:

- page 13 contains Resources plus all eight Options;
- pages 14–17 continue Resources/Markers;
- the complete lower Options scaffold is still painted on pages 14–17 even though Options are exhausted;
- pages 14–17 therefore reserve approximately half of each page for an empty sibling stream.

This is a direct reproduction of M50800-14 / M50800-27.

## 4. Current implementation mismatch

### Combat

`appendCombatExtendedPages` flattens each logical Combat entry to one or more physical line records and then allocates a fixed `COMBAT_ROW_STEP = 42f` for every physical line.

Consequences:

- logical records lose explicit group boundaries;
- every wrapped line pays a full 42 pt band;
- `COMBAT_ROWS_PER_PAGE = 14` forces Mara's final entry onto a mostly-empty second page;
- table labels are positioned inside the same vertical zone as the first data band.

### Resources / Options

`appendResourcesExtendedPages` computes the page count as the maximum of independent fixed capacities:

- 10 resource lines/page;
- 18 option lines/page.

`renderResources` always paints both the upper Resources table and lower Options table.

Consequences:

- once Options end, their lower half remains reserved on every later page;
- surviving Resources cannot reclaim the lower half;
- M50800-14 remains structurally impossible to satisfy.

## 5. Intended repair

### Phase 3A — Combat/Actions

Preserve the same five-column table semantics, but pack by logical entry:

- wrap each logical entry into 17 pt native-style physical rows;
- keep all wrapped rows belonging to one logical entry together when they fit on the current page;
- move the whole entry to the next page if it cannot fit intact;
- reserve a dedicated header band above body rows;
- first data row begins below the labels;
- alternate/shade by logical entry, not by anonymous wrapped line;
- page allocation is driven by consumed physical rows, not fixed 42 pt bands.

No field may be flattened into generic prose.

### Phase 3B — Resources / Options

Use the existing Resources and Options table grammars, selected by streams that survive:

1. both survive:
   - retain the split Resources + Options page;
2. Resources survive alone:
   - expand/reuse the Resources table through the reclaimed lower page;
3. Options survive alone:
   - expand/reuse the Options table through the reclaimed page;
4. neither survives:
   - emit no continuation page.

First combined page may carry both streams. Later pages must not reserve exhausted sibling geometry.

### Phase 3C — adaptive regression

Verify that this allocator does not reopen:

- Traits adaptive reclaim;
- Notes two-column reclaim;
- Equipment special-only reclaim.

## 6. Planned regressions

Combat:
- all combat entry identities remain present and ordered;
- headers do not overlap data geometry;
- the final Mara combat entry is not stranded on a mostly-empty page when physical capacity exists;
- identity/range/bonus/effect/notes remain in their columns.

Resources:
- all 10 resource identities, markers and 8 option identities remain present;
- later resource-only pages contain no empty Options scaffold;
- no additional page is created while surviving content fits reclaimed rows;
- active markers/checks remain associated with the correct options/resources.

Cross-family:
- both Custom-v2 Atributo and Habilidad pass;
- Desktop/Android generated renderer sync remains exact.

## 7. Artifact gate

Phase 3 may receive an internal progression PASS only after:

1. CI passes;
2. exact generated `pc-sheet-populated-template-proofs` is inspected;
3. real Mara Combat continuation is checked for header separation, record association and packing;
4. real Mara Resources continuation is checked for sibling reclaim;
5. prior 2A/2B artifact-positive behaviors show no new regression.

M50800-12/14/27 remain formally OPEN in the master matrix until final exact-candidate acceptance.

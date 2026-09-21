# Checkpoint — Custom v2 Extended Production Promotion — Pass 3

**Date:** 2026-09-20/21 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Frozen visual authority:** Custom v2 Extended Run 7  
**Final Pass-3 head:** `a78b847320a50f85fd7ab30b0047e904e1758ca5`  
**Scaffold push:** `35550650243` / #2986 — SUCCESS  
**Artifact:** `10617479234`  
**Artifact digest:** `sha256:fd27a9056738c3ddd0b84da2be22ae0abdbc31e66340151a3de90cca808aee16`

## Scope

Promote the remaining shared OWNER APPROVED Run-7 visual roles into real-model-driven Custom-v2 continuation rendering:

1. Inventory / Equipment;
2. Spells;
3. Notes.

Run 7 remains the visual/behavioral exemplar only. None of its literal sample character values are product defaults.

## Warrant rules

### Inventory / Equipment

The Extended Inventory role is appended only when real character data requires representation beyond the base-v2 surface, including:

- ordinary non-special inventory beyond the base 23-row capacity;
- Special Equipment beyond the base 14-row capacity;
- real attunement state that the base sheet cannot preserve explicitly;
- real valuables text from successor preferences.

The page consumes canonical `CharacterInventoryItem` and real successor preference data.

Attunement is represented by textual `Sintonizado`; the one state square reflects actual equipped state. No second attunement icon is invented.

### Spells

Spell continuation is appended only when a real spell level exceeds the existing base-v2 row capacity.

Base capacities used by both the base renderer and continuation routing are:

- level 0: 8;
- level 1: 10;
- level 2: 9;
- level 3: 10;
- level 4: 10;
- level 5: 8;
- level 6: 8;
- level 7: 6;
- level 8: 6;
- level 9: 5.

Continuation consumes canonical `CharacterSpell` data and uses real prepared state from spell source associations.

It preserves the approved v2 continuation grammar and does not redraw or duplicate spent-slot state.

### Notes

Notes continuation is appended only when the real notes payload exceeds the base Notes capacity.

The content source is:

- `generalNotes`;
- ordered `noteCards`.

The continuation uses the same base wrapping contract and starts only after the first 40 base-note lines.

## Pagination

Inventory, Spells and Notes now support repeated continuation pages rather than silent truncation.

Current physical continuation capacities:

- Inventory ordinary rows: 57/page;
- valuables: 19/page;
- Special Equipment: 12/page;
- Notes: 40 lines/page;
- Spells: one approved v2 level-block capacity per continuation page, repeated as needed.

## Test fixture authority

The Pass-3 test fixture deliberately creates inventory state, spell overflow and notes overflow in order to exercise routing.

Names such as `Conjuro adicional 9` and `Nota de continuación 45` are QA fixture values only. They are not canonical content and are never inserted by production code unless they exist in the selected character model.

## Exact-artifact verification

Per-Attribute Pass-3 PDF:

- `custom-v2-per-attribute-production-pass3.pdf`;
- 7 pages;
- Letter / 612 x 792 pt;
- not encrypted;
- SHA-256: `a31922e30455aaacb049137092d033e95957ff84d61bd35f5f9d2053827d75db`.

Per-Ability Pass-3 PDF:

- `custom-v2-per-ability-production-pass3.pdf`;
- 7 pages;
- Letter / 612 x 792 pt;
- not encrypted;
- SHA-256: `1955ba44cff7b2153d97a037c87254d35ce8049336e08a185422e49955db913f`.

Each exact PDF contains 15 Pass-3 semantic OCG groups:

- five Inventory layers;
- five Spells layers;
- five Notes layers.

Both PDFs were inspected in PDFium and Poppler.

Observed:

- no missing generated glyphs;
- no replacement characters;
- no clipping or problematic overlap;
- no opaque source-crop artifacts;
- no cross-renderer structural divergence;
- shared Extended pages render identically across per-Attribute and per-Ability families;
- real `Sintonizado` state appears where warranted;
- real overflow spell entries appear in the correct source-led level block;
- real note overflow begins after the base Notes capacity;
- `Especie` absent;
- `Dados de portento` absent.

## Pass-3 conclusion

**CUSTOM V2 EXTENDED PRODUCTION PROMOTION PASS 3: PASS**

All six Extended semantic roles now have a production rendering path based on the OWNER APPROVED Run-7 visual grammar:

- Custom Statistics;
- Traits & Features;
- Resources & Options;
- Inventory / Equipment;
- Spells;
- Notes.

Their existence and content remain dynamic and character-model-driven.

## Next gate

Do not treat Pass 3 as final product approval.

Next phase:

**Integrated Custom-v2 production audit / end-to-end renderer closure**

The audit must verify:

1. minimal characters do not receive unwarranted Extended pages;
2. each role appears only for real data/presentation/overflow need;
3. combined dense data routes all roles correctly;
4. boundary and multi-page continuation cases preserve all canonical data;
5. both per-Attribute and per-Ability base families remain frozen;
6. the production renderer is actually connected to the product export path rather than existing only as a development/draft renderer;
7. no successor-model content with an applicable PDF semantic role is silently dropped;
8. exact CI artifacts pass two-renderer QA;
9. PR #85 remains DRAFT / DO NOT MERGE until final owner review.

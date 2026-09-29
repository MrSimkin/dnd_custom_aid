# Mara 50800 — fix phase/source mapping

**Date:** 2026-09-28  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Status:** ACTIVE / REQUIRED PRE-CODE MAP  
**Acceptance authority:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`

## Provenance decision

The owner-facing `0.5.0-preqa.8 / 50800` identity was used by the integrated PR #107 candidate at main commit `15f86ec8285e69969054d40defaa2c16259b8dce`.

The later branch `repair/pc-sheet-adaptive-continuations-cross-family` is **158 commits ahead** of that integrated candidate and ends at `ce695c40782e1847ed70a2256885b5b07113feae`. It was never integrated as runtime authority and reused the same 50800 identity. Therefore exact installed-binary provenance cannot be recovered from version/build identity alone.

Repair policy:

- current `main` is the new repair base;
- the old adaptive branch is evidence/reference only and is not merged/cherry-picked wholesale;
- any useful idea from it must be re-evaluated against the real 50800 QA and the current acceptance matrix;
- the next owner candidate receives a new unique version/build identity.

## Discrete implementation phases

### Phase 1 — semantic baseline + generation safety

Close the highest-confidence semantic contradictions before layout reconstruction:

- ordinary Equipment is compact native identity only;
- no ordinary Equipment weight;
- no `Consumible`;
- no ordinary Equipment prose-description continuation;
- prevent ordinary metadata from allocating continuation pages;
- fix the Custom-v1 real-Mara generation blocker without aggressive compression;
- add/adjust focused regressions and keep Desktop/Android parity.

### Phase 1B — native Equipment / Equipo Especial continuation

Status after run 4200: **progression gate passed**, with M50800-17 and M50800-20 intentionally still OPEN.

- reuse native Equipment/Equipo Especial pages and rows;
- ordinary Equipment = compact identity only;
- no weight, no `Consumible`, no ordinary descriptions;
- preserve custom locations without damaging native checkboxes;
- keep Special Equipment items whole across pagination boundaries;
- inspect exact generated PDFs before progression.

### Phase 2A — Custom-v2 Custom Statistics + source underlay + name ribbon

Status after run 4212: **internal exact-artifact progression PASS** at `6e53492dcd9f13945fbb320334f621aad070e61f` / artifact `11011771728`. M50800-03/04/05/06/07/25 remain formally OPEN until final-candidate reconfirmation.

Acceptance scope: M50800-03, M50800-04, M50800-05, M50800-06, M50800-07, M50800-25.

- reuse the page-1 attribute grammar/capacity rather than the 3-column generic cards;
- Mara's Fortuna, Cordura, Éter and Renombre must fit together at native scale;
- render only real attributes; no phantom shells;
- preserve clean semantic names, including `Éter`;
- remove stale source-underlay text contamination from Extended headers;
- center the character name horizontally and vertically in the native ribbon;
- use two centered lines when one line does not fit cleanly.

### Phase 2B — remaining Custom-v2 native component reuse

**Next resume point.** Execute as discrete subphases:

- **2B.1:** Traits/Rasgos + Trasfondo/Historia native reuse — M50800-08/09/10/11;
- **2B.2:** Notes native reuse/boundaries/continuation — M50800-21/22/23/24;
- **2B.3:** residual Equipment readability/reclaim — M50800-17/20.


- Traits/Rasgos reuse native/source grammar;
- Trasfondo/Historia and similar narrative modules reuse native/source grammar;
- refine Equipment/Equipo Especial readability issues left OPEN by Phase 1B;
- Notes reuse the existing native Notes grammar;
- preserve semantic boundaries and source identity.

### Phase 3 — adaptive continuation packing

- Custom-v2 Combat/Actions;
- Resources/Options reclaim;
- Traits continuation reclaim;
- Notes continuation packing/identity;
- redundant scaffolds;
- cross-stream page allocation.

### Phase 4 — Fantasy-specific repair

- semantic-name wrap instead of ellipsis;
- Combat table semantics;
- Traits/Resources/Inventory/Notes packing;
- preserve semantics while eliminating empty-page inflation.

### Phase 5 — cross-family acceptance/candidate

- all-four-family real-Mara generation;
- actual generated PDF inspection;
- text/data preservation checks;
- 32-item matrix disposition;
- unique next candidate identity;
- only then owner QA.

## Acceptance item → source map

| ID | Primary implementation surface | Existing/native reference | Planned evidence |
| --- | --- | --- | --- |
| M50800-01 | version/build config + release workflow | prior unique preqa candidates | unique candidate commit/run/artifact/digest |
| M50800-02 | `DesktopCustomV1ExtendedRenderer.kt` + generated Android counterpart; source-fit helpers | Custom-v1 approved Run 6/7 grammar | real Mara Custom-v1 generation + PDF |
| M50800-03 | Custom-v2 Extended layer/template construction | five-layer Custom-v2 contract | PDF text-layer scan + render |
| M50800-04 | `DesktopCustomV2ExtendedRenderer.appendPerAttributePages/appendPerAbilityPages` | Custom-v2 page-1 native attribute ornaments/slots | base-vs-Extended render |
| M50800-05 | same Custom Statistics layout/capacity | six native page-1 attributes | all four Mara custom attributes on native-scale row |
| M50800-06 | Custom Statistics iteration/slicing | page-1 real-slot behavior | no empty/fake card shells |
| M50800-07 | Custom Statistics label composition | native label/abbreviation grammar | clean Éter text/render |
| M50800-08 | `appendTraitsExtendedPages` | existing base/approved Traits/Rasgos sections | base-vs-continuation render |
| M50800-09 | trait category collection/order | accepted category-grouped grammar | deterministic category-order regression |
| M50800-10 | trait pagination/continuation allocation | native trait rows/continuation grammar | no repeated empty panels |
| M50800-11 | Custom-v2 base/shared narrative rendering + continuation | base Trasfondo/Historia/personality modules | base-vs-continuation render |
| M50800-12 | `appendCombatExtendedPages` / combat page renderer | existing base combat table | header/data separation + row packing |
| M50800-13 | `DesktopClassicRenderer` combat continuation | Fantasy base combat table | rendered Fantasy combat table |
| M50800-14 | `appendResourcesExtendedPages` | existing Resources/Options grammar | surviving stream reclaims exhausted area |
| M50800-15 | shared `pdfCompactEquipmentLabel`; Custom v1/v2 base + Extended Equipment methods | existing base Equipment elements | base-vs-continuation render |
| M50800-16 | `PcSheetPdfInventorySemantics.kt` + ordinary continuation helpers | compact native Equipment identity | no weight / no Consumible / no prose detail |
| M50800-17 | ordinary Equipment row/column pagination | native Equipment rows/columns | atomic item identity readability |
| M50800-18 | Custom v1/v2 special Equipment renderers | existing native Equipo Especial | base-vs-continuation render |
| M50800-19 | `positionedSpecialItems` / custom-location rendering | native location rows + added legitimate rows | no canonical-label overprint |
| M50800-20 | inventory page-count/scaffold logic | native modules | no empty Equipment/Special scaffold page |
| M50800-21 | `appendNotesExtendedPages` + base Notes renderer | existing Notes sheet/module | base-vs-overflow grammar |
| M50800-22 | note record model/render loop | native Notes typography | bold/emphasized Note N + visible separation |
| M50800-23 | note pagination state | native Notes continuation | continuation retains note identity |
| M50800-24 | Notes column/page allocator | two-column native Notes grammar | columns filled before page advance |
| M50800-25 | Custom-v2 shared/base character-name renderer | existing portrait ribbon geometry | horizontal+vertical centering; 2-line fallback |
| M50800-26 | `DesktopClassicRenderer` single-line excerpt/truncation helpers | existing semantic row grammar | no unintended semantic ellipsis |
| M50800-27 | continuation allocators in Classic/Custom v1/Custom v2 | family-native modules | actual PDFs show cross-stream reclaim |
| M50800-28 | tests/checkpoints only; remove numeric-target assumptions | acceptance matrix | no acceptance based solely on page ceiling |
| M50800-29 | real Mara fixture regressions | fixture source semantics | traits/resources/options/inventory/note identities preserved |
| M50800-30 | `AGENTS.md` + this map + acceptance matrix | real 50800 artifacts | map exists before first renderer code change |
| M50800-31 | runtime QA fixture + artifact inspection workflow | exact candidate outputs | matrix FIXED/OPEN/CHANGED-NEW disposition |
| M50800-32 | runtime QA fixture + whole-draft renderer across 4 families | real Mara fixture | Fantasy + Custom v1 + v2 Atributo + v2 Habilidad generation |

## Phase 1 concrete code targets

1. `shared/.../PcSheetPdfInventorySemantics.kt`
   - compact ordinary label becomes quantity + name only;
   - ordinary Equipment detail metadata is not routed into PDF Equipment/Notes semantics.

2. `DesktopCustomV1HybridRenderer.kt`, `DesktopCustomV2SharedBaseRenderer.kt`
   - inherit the compact helper; native base Equipment keeps its existing grammar with cleaner content.

3. `DesktopCustomV1ExtendedRenderer.kt`, `DesktopCustomV2ExtendedRenderer.kt`
   - ordinary continuation carries compact identity only;
   - ordinary usage/state metadata does not allocate or add continuation rows;
   - special Equipment remains separate for later native-module work.

4. `DesktopClassicRenderer.kt`
   - ordinary inventory weight/state output is suppressed for generated Equipment identity while preserving table structure.

5. Android generated counterparts
   - remain mechanically equivalent to Desktop behavior; sync guard must pass.

6. Focused tests
   - shared inventory semantics;
   - real runtime fixture assertions for ordinary Equipment;
   - Custom-v1 Mara generation regression.

No Phase-2 geometry redesign is allowed inside Phase 1.

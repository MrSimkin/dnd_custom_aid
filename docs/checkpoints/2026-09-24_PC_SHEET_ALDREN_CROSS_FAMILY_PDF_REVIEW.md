# Checkpoint — Aldren cross-family Android PDF review complete

**Date:** 2026-09-24 (Chile local time)  
**QA build:** `0.5.0-preqa.3` / build `50300`  
**Functional main under test:** `fd781262abfeb47003298562e540721a8515071a` (PR #100 generalized Fantasy bounded-text repair)  
**Current repository main while recording this evidence:** `cfb2dfda6e68ea337f5480b325e26a68fcca9ee6`  
**Status:** CROSS-FAMILY ALDREN SURVEY COMPLETE / SAVE+OPEN PASS / CONTENT-LAYOUT REPAIR PACKAGE REQUIRED

## 1. Scope and owner evidence

After Fantasy Sheet Save/open passed on Android, the owner exported Aldren Vale / Permanente in all four visual options available in the current PC Sheet PDF surface:

1. Custom v1 — **13 pages**;
2. Custom v2 — per Attribute — **8 pages**;
3. Custom v2 — per Ability — **8 pages**;
4. Fantasy Sheet — **18 pages**.

All four PDFs were opened and visually inspected page by page.

The two Custom-v2 exports are pixel-identical from **pages 2 through 8**. Their defect surface is therefore shared for equipment/history, spell page, notes, traits continuation, resources/options and inventory; only page 1 differs by the Attribute-vs-Ability base-sheet composition.

Owner-provided PDFs and screenshots are runtime QA evidence from the Android build. They are not committed as repository binaries; this checkpoint is the durable factual record.

## 2. Mechanism result

Across all four formats:

- Save/generation succeeds;
- the Android document-save path completes;
- the PDFs open;
- Aldren is present;
- the documents are populated/readable enough for inspection;
- the prior Fantasy bounded-routing production failure does not recur.

Therefore the Save/open runtime mechanism and PR #100 bounded-routing repair remain **PASS**.

The remaining problems are content correctness, semantic routing, visual semantics and pagination/packing.

## 3. Cross-family defect matrix

| Defect class | Custom v1 | Custom v2 / Attribute | Custom v2 / Ability | Fantasy |
| --- | --- | --- | --- | --- |
| Mojibake / corrupted accented text | FAIL | FAIL | FAIL | FAIL |
| Combat/action material routed through Traits/reference overflow | FAIL | FAIL | FAIL | FAIL |
| Ordinary equipment/inventory duplicated into extension output | FAIL | FAIL | FAIL | FAIL |
| Currency/money routed to the wrong semantic surface | FAIL | FAIL | FAIL | FAIL |
| Empty/unnecessary spell page for non-spellcaster Aldren | FAIL | FAIL | FAIL | FAIL |
| Sparse/unnecessary standalone Notes page | FAIL | FAIL | FAIL | FAIL |
| Content-driven packing / extension-page efficiency | FAIL — severe | FAIL | FAIL | FAIL — extreme |
| One-use resource presentation | PASS/acceptable circle | FAIL — unexplained filled square | FAIL — unexplained filled square | FAIL — numeric 1/1 + continuation rows |
| Equipment-special Location typography | no new owner defect recorded | FAIL | FAIL | n/a to this template-specific issue |

## 4. Shared defect classes

### 4.1 Text encoding / mojibake — all families

Spanish and punctuation arrive visibly corrupted in all four Android-generated PDFs.

Examples include:

- `ComÃºn` instead of `Común`;
- `Ã‰lfico` instead of `Élfico`;
- `AcÃ³lito` instead of `Acólito`;
- `acciÃ³n` instead of `acción`;
- `versÃ¡til` instead of `versátil`;
- `2â€“5` instead of `2–5`;
- source separators rendered as `â€”`.

This is a **shared** defect and must not be treated as a Fantasy-only font/layout problem. Repair investigation must determine where otherwise-correct fixture Unicode becomes mojibake before/during Android PDF generation.

### 4.2 Combat/actions and structured damage are routed into Traits/reference overflow — all families

The continuation/reference architecture uses a generic overflow channel for content that is not semantically a trait.

Observed examples include:

- Espada larga attack reference;
- Ballesta ligera attack reference;
- Second Wind action reference;
- Action Surge action reference;
- structured damage rows for the same entries.

Manifestation differs by family:

- **Fantasy:** explicit `Acción / ataque` and `Daño estructurado` entries appear on pages titled `RASGOS Y CARACTERÍSTICAS`;
- **Custom v1:** the same material is placed inside the repeated `Notas` area of trait-extension pages;
- **Custom v2:** the same material enters `DETALLES / NOTAS` and `CONTINUACIÓN` on `RASGOS Y ATRIBUTOS` extension pages.

Data preservation is not sufficient if the semantic destination is wrong. Combat/action detail needs a combat/action continuation/reference destination, not Traits.

### 4.3 Inventory/equipment is duplicated instead of overflow-only — all families

Ordinary and/or special equipment is represented on base sheets and then repeated again on extension pages rather than emitting only genuine overflow/additional detail.

Examples observed repeatedly:

- Ballesta ligera;
- Virotes;
- Mochila de explorador;
- Libro de oraciones;
- Incienso;
- Vestiduras;
- Ropa común;
- Cota de malla;
- Escudo;
- Espada larga;
- Símbolo sagrado.

Family manifestations:

- **Custom v1:** page 2 already contains Equipo and Equipo Especial; page 13 emits the equipment again with notes/details.
- **Custom v2:** page 1 already lists objects, page 2 has the detailed Equipo/Equipo Especial surface, and page 8 repeats the inventory again.
- **Fantasy:** page 2 has the base equipment list and pages 14–17 spread ordinary/special/details/currency over four extension pages.

The continuation planner must distinguish **already represented**, **needs extra detail**, and **true overflow** instead of replaying the whole inventory set.

### 4.4 Currency/money is semantically misplaced — all families

Fixture currency includes gold = 15 and zero-value default denominations.

Observed misrouting:

- **Custom v1:** currency reappears on page 13 inside `Gemas / Joyas / Arte` entries (e.g. Cobre, Plata, Electrum, Oro) instead of being correctly populated/retained in the `Monedas` surface.
- **Custom v2:** page 8 places `Cobre: 0`, `Plata: 0`, `Electrum: 0`, `Oro: 15` inside the extension `EQUIPO` columns.
- **Fantasy:** currency is spread through `VALOR / UBICACIÓN / NOTAS` on inventory extension pages 16–17.

Currency must remain in currency/treasure semantics and must not create inventory extension pages.

### 4.5 Non-spellcaster Aldren gets a full blank spell page — all families

Aldren has `spellcasterEnabled = false` and no real spellcasting content, yet every export includes a full spell page:

- Custom v1 page 4;
- Custom v2 page 3 in both variants;
- Fantasy page 3.

This is unnecessary page generation for the tested state. The renderer/planner must suppress spell pages when there is no spellcasting content requiring them, unless a documented product rule explicitly requires a blank writable spell page.

### 4.6 Sparse standalone Notes pages — all families

Aldren's general QA note generates a dedicated largely-empty Notes page:

- Custom v1 page 5;
- Custom v2 page 4 in both variants;
- Fantasy page 18.

This combines badly with other continuation pages that also expose Notes/detail areas. The architecture is generating new semantic pages without first using available compatible capacity.

### 4.7 Page amplification / poor packing — shared architectural defect

The exports are much larger than their content requires:

- Custom v1: 13 pages;
- Custom v2: 8 pages;
- Fantasy: 18 pages.

The issue is not simply “blank space looks bad.” Fixed per-section capacities and fixed left/right continuation columns create new pages while compatible capacity remains unused elsewhere.

Repair should move from page-count-by-independent-section to content-aware packing within each family’s approved visual grammar.

Do **not** hard-code a target page count merely to make the fixtures shorter. The acceptance rule is: no new extension page should exist when its content can be placed, without clipping or semantic misrouting, in already-existing compatible capacity.

## 5. Family-specific findings

### 5.1 Fantasy Sheet — 18 pages / highest severity

Verified layout pattern:

- page 3: empty spell page for a non-spellcaster;
- pages 4–11: **eight** Traits/Characteristics continuation pages;
- pages 6–11 frequently have an entire left column empty while only one or two entries occupy the right column;
- pages 8–11 are combat/action and structured-damage references incorrectly living under Traits;
- page 12: resources table;
- page 13: a second resources page exists essentially to carry one trailing `Action Surge (cont.) / máximo` continuation fragment;
- pages 14–17: **four** inventory/equipment extension pages;
- pages 15–17 are highly sparse, with pages 16–17 largely carrying notes/currency;
- page 18: sparse standalone Notes page.

Resource-specific defect:

- Second Wind and Action Surge use numeric `1 / 1` in `Actual / máx.`;
- long recovery text creates named continuation rows whose other columns are mostly empty;
- page 13 is produced by the tail of that continuation.

The owner specifically rejects this as an appropriate representation for a one-use/binary resource.

### 5.2 Custom v1 — 13 pages

Verified layout pattern:

- page 4: empty spell page;
- page 5: nearly empty standalone Notes page;
- pages 6–11: **six** `Otros Rasgos y Atributos` extension pages;
- the extension template repeatedly uses its `Notas` area as a generic overflow sink for traits, class details, combat/action references and structured damage;
- large portions of the fixed trait/race/feat/proficiency/language areas are empty while new pages are still created;
- page 12: resource page; the owner considers the one-use resource circle presentation acceptable here;
- page 13: equipment/currency extension that largely duplicates page-2 equipment.

The key Custom-v1 defect is not just page count: the renderer has compatible space but continues allocating pages because semantic buckets are statically partitioned.

### 5.3 Custom v2 — both variants / 8 pages

Pages 2–8 are pixel-identical for per-Attribute and per-Ability exports, so the following findings apply to both.

Verified layout pattern:

- page 3: empty spell page;
- page 4: sparse standalone Notes page;
- pages 5–6: Rasgos y Atributos continuation;
- page 6 is substantially underfilled while carrying continued detail;
- page 7: resources/options;
- page 8: inventory/equipment continuation.

#### One-use resource marker is semantically unclear

On page 7, Second Wind and Action Surge show a **solid filled square** in the `ACTUAL / MÁX.` column.

A filled square with no legend/label does not communicate “1 available out of 1” or “used/unused” clearly. This must be replaced by a consistent, explicit binary/one-use representation.

#### Equipo Especial / Ubicación typography mismatch

On the base Custom-v2 equipment page, Location values use the template’s large decorative `Corbel-Bold` at approximately **12.12 pt**, while item names use the app fill font around **9.25 pt** and the extension-page Location values use approximately **7.4 pt Corbel**.

The owner reports the `Ubicación` column as visibly broken in font family/size, and the PDF span metrics confirm the mismatch. The base equipment-fill path must use the intended fill typography rather than leaving/using the oversized template label styling for location values.

#### Inventory continuation wastes half-page table capacity

Page 8 renders two side-by-side `EQUIPO` columns. The left column contains the repeated ordinary inventory and currency; the right column is empty. The packing logic must fill available compatible rows/columns before expanding or leaving a mirrored table unused.

## 6. Additional observations found during full PDF review

These were not part of the owner's initial five-item list but are directly visible in the supplied PDFs:

1. **Unconditional spell-page generation** for Aldren is common to all four exports.
2. **Standalone Notes-page generation** is common to all four exports and is sparse for this fixture.
3. **Custom-v2 Attribute and Ability share exactly the same pages 2–8**, so later repair/test work should avoid duplicating identical coverage.
4. **Fantasy pagination amplification is extreme:** 18 pages for this straightforward level-5 non-spellcasting fighter, including multiple pages whose only useful content is one or two rows.
5. **Custom-v1 Traits overflow is semantically fragmented:** a dedicated Notes page exists, yet trait-extension `Notas` panels are also used to carry unrelated overflow; this creates both duplication of surfaces and unnecessary pagination.
6. **Custom-v2 inventory extension mixes semantic domains:** ordinary equipment and currencies occupy the same continuation table, while a second compatible equipment column remains empty.
7. **Fantasy inventory continuation also mixes semantic domains:** equipment notes and currencies are routed through `VALOR / UBICACIÓN / NOTAS`, which is not an appropriate currency destination.
8. **The encoding problem includes punctuation, not only accented letters:** en/em dashes are also mojibake, which is useful diagnostic evidence against treating this as merely a missing glyph/font issue.

## 7. Repair-package classification

The next implementation package should be designed in this order:

### A. Shared encoding/data-path correctness

Find the first point where valid fixture Unicode becomes mojibake in the Android runtime path. Fix once at the earliest correct boundary and add regression coverage containing accented Spanish plus en/em dashes.

Do not “repair” mojibake inside each renderer by string replacement.

### B. Shared semantic continuation routing

Split the current generic reference/overflow channel into semantic destinations at minimum for:

- traits/features;
- combat/actions/damage;
- inventory/equipment;
- currency/treasure;
- notes/background.

Content must not be preserved by putting it in the wrong section.

### C. Shared content-aware page creation and packing

Before creating any extension page:

1. determine whether the semantic family actually has content;
2. use compatible existing capacity first;
3. pack across available columns/areas for that family;
4. create another page only for genuine remaining overflow.

This should remove the blank spell pages, sparse Notes pages and most continuation amplification without introducing clipping.

### D. Family-specific resource visualization

Keep one-use/binary semantics consistent but family-appropriate:

- Custom v1 current circle presentation is acceptable owner evidence;
- Custom v2 filled-square presentation is not acceptable;
- Fantasy numeric `1 / 1` plus continuation rows is not acceptable.

Do not assume the same graphic must be used in all templates, but the meaning must be obvious and consistent.

### E. Custom-v2 equipment typography repair

Correct the base Equipo Especial `Ubicación` value typography/size so data fill is visually consistent with the approved template and other filled columns.

## 8. Regression/acceptance requirements for the repair

At minimum, automated/runtime regression must prove:

- exact Unicode survives: `Común`, `Élfico`, `Acólito`, `acción`, `versátil`, and representative en/em dash text;
- Aldren non-spellcaster does not receive an empty spell page unless an explicit product decision says otherwise;
- combat/actions/damage do not appear under Traits;
- currency is not routed through inventory/equipment or generic value/location notes;
- already-represented equipment is not blindly replayed on extension pages;
- one-use resources have a semantically legible representation in each family;
- Custom-v2 base equipment Location fill uses the intended typography;
- no page is created solely because a fixed semantic bucket overflowed while compatible capacity remained available;
- complete semantic detail is still preserved somewhere correct;
- the existing bounded-overflow safety guard remains active;
- Android Save/open behavior remains PASS.

Do not declare success from page count alone.

## 9. Current manual/engineering boundary

The comparative Aldren survey is **complete**.

Do not continue to Share, Ilyra, Mara, Current Snapshot or the physical-device final gate yet. Those tests would be contaminated by known shared PDF defects.

The next route is engineering:

1. inspect the shared planner/renderer/template-fill architecture against sections 7–8 above;
2. implement the smallest coherent repair package that addresses shared causes before family-specific symptoms;
3. add cross-family Aldren regressions;
4. produce a new distinguishable QA APK;
5. rerun Aldren across the four formats before resuming later fixtures.

Fresh sessions resume through:

`RESUME.md -> docs/checkpoints/LATEST.md -> this checkpoint`.

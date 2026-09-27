# Checkpoint — Mara preqa.8 PDF visual QA defects

**Date:** 2026-09-26 (Chile local time)  
**Base main:** `85303c0cfcf0da1d13d5d0f57bdec2c25a38ecf0`  
**Runtime build under review:** `0.5.0-preqa.8` / build `50800`  
**Artifact reviewed:** 28-page Mara de los Siete Umbrales / Custom v2 · Atributo / Permanente / Extended PDF  
**Status:** EXPORT-GENERATION PASS / VISUAL-QA FAIL / REPAIR REQUIRED

## Owner observations confirmed

The uploaded owner PDF confirms the following runtime/layout defects:

1. ordinary **EQUIPO** continuation is visually hard to parse;
2. **NOTAS** records run directly into one another without useful separation;
3. **custom-stat Extended blocks** are corrupted/visually wrong;
4. **COMBATE / ACCIONES** still has the known header/first-row overlap and excessive row spacing;
5. the Extended pages before page 18 require full review;
6. page 26 ordinary Equipment text is badly formatted;
7. page 26 **EQUIPO ESPECIAL** contains visible overlap/artifacts;
8. note continuation/order on page 28 is poor and visually detached from the base Notes page.

## Independent visual-QA findings

### A. Systemic template-underlay contamination

Extended pages are being drawn over a reused source-template page without removing the original text objects.

Evidence from PDF text extraction shows hidden/stale template labels such as:

- `EQUIPO`;
- `TRASFONDO`;
- `VÍNCULOS`;
- `IDEALES`;
- `HISTORIA`;
- `EQUIPO ESPECIAL`;
- static special-equipment location labels.

These stale text objects remain searchable/copyable even when visually covered. In some places, especially Inventory/Special Equipment, the underlay leaks visibly and overlaps current content.

This is a **systemic PDF structure/accessibility defect**, not just cosmetic noise.

### B. Custom Statistics page 5 — Éter heading corruption

The third custom-attribute card visually renders as approximately:

`ETE ter`

instead of preserving the intended `ETE · Éter` / unambiguous Éter label.

The source data is correct. This is a rendering/stylization defect around the abbreviation/name composition and accented initial character.

### C. Custom Statistics page 6 — phantom empty attribute cards

Mara has exactly four custom attributes:

1. Fortuna;
2. Cordura;
3. Éter;
4. Renombre.

The second Custom Statistics page correctly needs only **Renombre**.

However, the renderer also paints two additional empty attribute-card shells with:

- black score/modifier blocks;
- empty white number boxes;
- empty skill rows.

These are visually indistinguishable from malformed/missing character statistics and must not render as fake attribute cards.

### D. Traits/Features pages 7–18 — severe continuation packing waste

All 26 trait cases are present, but after the category/list areas empty, the renderer keeps repeating the entire four-panel **RASGOS Y ATRIBUTOS** scaffold while using mostly only the right-side Detail/Continuation area.

Pages roughly 9–18 are therefore dominated by empty panels while dense continuation text remains at a small font.

This is a **pagination/layout efficiency defect**:

- too many mostly-empty pages;
- reduced readability despite abundant unused area;
- repeated irrelevant section scaffolds.

A dedicated full-width/full-page continuation layout should be considered once the base category panels are exhausted.

### E. Traits/Features ordering inconsistency

The source traits are globally sorted 1,2,3,4,...

The visible first detailed continuation sequence is effectively grouped by panel/category (for example 1,4,2,3 before the later sequential run).

This may be intentional category reading order, but it is inconsistent with fixture sort order. It should either:

- preserve the global source order; or
- be explicitly treated as category-grouped order in the visual contract.

Until clarified, record as an ordering inconsistency rather than confirmed data loss.

### F. Combat pages 19–20 — header collision, not only spacing

The known row-spacing residual is worse than simple whitespace.

On page 19, column labels such as:

- `TIPO / NOMBRE`;
- `RANGO`;
- `BONIF.`;
- `DAÑO / EFECTO`;
- `NOTAS`

occupy the same vertical band as the first combat entry and visibly overlap the row text.

The same structural problem carries into the continuation page.

The accepted historical “spacing residual” is therefore no longer sufficient as the full description: **header/first-row collision is a real visual defect**.

### G. Resources/Options pages 21–25 — values mostly correct, pagination inefficient

Cross-check against the Mara fixture:

- 10 resources are present;
- 7 custom markers are present;
- 8 class options/protocols are present.

**Marcador custom 2 = 1 is correct.**

Fixture semantics:

- `Marcador custom 2`;
- kind = COUNTER;
- currentValue = 1;
- no maxValue.

So the plain `1` shown in the PDF is data-correct.

However, after all 8 options are exhausted on page 21, pages 22–25 repeat a large empty **OPCIONES** table while Resources/Markers continue above. This wastes a major portion of each page and contributes to unnecessary pagination.

### H. Inventory pages 26–27 — context fragmentation

All 34 inventory item identities are present in extracted text, but the layout makes the data hard to understand:

- identity lines, status lines and long descriptive-detail lines are split into separate physical streams;
- it is difficult to associate a detail continuation with its item;
- weight tokens such as `lb` can become isolated on their own row;
- page 27 uses only a small portion of the left Equipment area while the other equipment column is empty.

This is not currently evidence of missing item identities, but it is a major readability/association failure.

### I. Special Equipment page 26 — custom-location overprint

The special-equipment table has fixed canonical location labels.

Items using custom/noncanonical locations such as:

- `Espalda`;
- `Bolsa lateral`

are being placed into fallback rows while their custom location text is drawn over the existing fixed row labels.

The result is visible overprint such as stacked/garbled location names.

This confirms the owner's “weird artifacts” observation and identifies the likely mechanism.

### J. Page 27 repeats an essentially empty Special Equipment scaffold

After page 26 already contains the special items, page 27 repeats a full **EQUIPO ESPECIAL** table with no useful special-item content.

This is unnecessary pagination/template repetition.

### K. Notes pages 4 and 28 — record boundaries and continuation order are poor

Source data contains:

- one general Notes paragraph;
- 9 note cards in sort order 1…9;
- background personality/flaw/religion/subclass material.

All 9 note-card titles are present in PDF extraction.

However:

- page 4 concatenates Note 1 → Note 2 → etc. with no blank row, separator, or card boundary;
- page 28 begins with the tail of Note 8 without an explicit `Nota 8 (continuación)` identity;
- Note 9 follows immediately;
- background-derived notes then follow;
- the entire page-28 overflow occupies only the left notes column while the right column remains blank.

This is a **continuation packing + semantic-boundary defect**.

Preferred behavior:

- preserve a visible boundary between note cards;
- label cross-page continuation;
- fill available Notes columns/rows before adding another page;
- strongly consider placing Notes continuation immediately after the base Notes page rather than after all unrelated Extended sections.

## Independent data-preservation scan

Text-level scan of the owner PDF found:

- all **26** trait `Caso N` identifiers;
- all **10** resource identifiers;
- all **7** custom markers;
- all **8** `Protocolo de paradoja` options;
- all **34** inventory item identities;
- all **9** note-card titles.

Therefore the current Mara 3A failure is primarily **presentation, layout, packing, reading-order and PDF-underlay structure**, not broad semantic data disappearance.

This does **not** prove every descriptive sentence is perfectly associated with the right visual row; Inventory and Notes specifically remain structurally suspect.

## Core architectural defect — fixed scaffolds instead of adaptive continuation packing

The owner explicitly clarified the central product problem:

**A character having many Notes, many Traits, many Resources, many Inventory items, or any other one-sided content load must not cause a long sequence of mostly-empty PDF pages.**

The Mara artifact demonstrates that the current Extended renderer is too template-centric:

- it allocates continuation pages by repeated fixed multi-panel scaffolds;
- when only one section still has overflow, unrelated empty panels are still reproduced;
- the overflowing section is constrained to its original small region instead of gaining the freed page area;
- this inflates page count and simultaneously reduces readability;
- the same failure mode can occur for any content family, not only Traits or Notes.

Examples already visible in Mara:

- Traits continuation keeps repeating the full four-panel Traits scaffold while mostly only Detail/Continuation is used;
- Resources/Options keeps repeating a large empty Options region after options are exhausted;
- Inventory repeats empty Equipment/Special Equipment structures after one stream is exhausted;
- Notes continuation uses only part of the available Notes page while the other column remains empty.

This must be treated as a **general pagination/layout architecture defect**, not as four isolated page bugs.

### Required design principle

Extended pages must become **content-adaptive**:

1. preserve the approved base-sheet visual language and section identity;
2. on continuation pages, allocate space according to the content streams that actually remain;
3. when one sibling section is exhausted, allow the surviving section to reclaim that physical space;
4. pack content vertically and across available columns before creating another page;
5. preserve semantic record boundaries and ordering while repacking;
6. avoid rendering empty section scaffolds unless they carry useful orientation/context;
7. use dedicated full-width/full-page continuation layouts when a single content family dominates;
8. page count should be driven by actual remaining content height/rows, not by fixed template repetition.

The intended result is a **good character sheet first**, with efficient, readable continuation pages even for extreme custom characters. Mara is the stress fixture proving whether this adaptive behavior works.

## Runtime verdict

Mara 3A:

- export-generation: PASS;
- visual/readability/structure QA: **FAIL**;
- overall Mara 3A: **NOT PASS**.

Do not run Mara 3B or Current Snapshot yet.

## Repair package direction

The next repair should be treated as one **visual-layout/continuation package**, not a string-specific patch.

Required scopes:

1. eliminate/flatten stale template-underlay text from Extended pages;
2. fix Custom Statistics partial-page rendering and Éter heading;
3. redesign/compact exhausted Traits continuation pages;
4. separate combat headers from first data row and rationalize row height;
5. improve Resource/Options page packing;
6. redesign Inventory continuation grouping and custom-location handling;
7. remove redundant empty Inventory/Special scaffolds;
8. give Notes explicit record boundaries and sane multi-column/page continuation order.

A new owner APK should not be requested until automated visual/text regression covers these layout behaviors on the real Mara fixture.

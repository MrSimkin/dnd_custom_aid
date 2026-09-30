# Phase 2 audit — existing adaptive repair against owner QA

**Date:** 2026-09-30 (Chile local time)  
**Phase:** 2 — audit/recovery of existing repair work  
**Branch:** `audit/mara-phase2-existing-repair`  
**Base main:** `bf4ed5b7017e63f2e0f1cfb43f60250d1c5ab988`  
**Repair evidence head under audit:** `ce695c40782e1847ed70a2256885b5b07113feae`  
**Status:** COMPLETE PENDING PUBLICATION / AUDIT ONLY / NO RENDERER CHANGES  
**Owner review mode:** batches of at most 5 decision points; unresolved owner questions are accumulated rather than interrupting technical audit.

## Governing authority

Phase 1 proved that the owner's failed 50800 QA used the pre-adaptive `15f86ec...` APK, not the later `ce695c...` repair APK.

Therefore Phase 2 audits the later repair against the full owner QA. It does not infer success from CI or page counts.

Binding acceptance authority remains:

- `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`;
- `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md`;
- `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`.

Later owner clarifications remain binding, especially:

- reuse existing native/source component grammar before reconstruction;
- ordinary Equipment: compact identity only, no weight, no `Consumible`, no prose description;
- meaningful names wrap rather than ellipsize;
- no fixed page-count target;
- actual output inspection is required.

## Audit classification

Each repair item is classified provisionally as:

- **KEEP** — implementation directly addresses the owner observation and is suitable to retain;
- **MODIFY** — useful implementation exists but does not fully satisfy the owner contract;
- **REPLACE** — implementation approach conflicts with the clarified owner contract;
- **MISSING/GAP** — no sufficient repair exists;
- **PENDING OWNER** — technical evidence is available but owner confirmation is required before final classification.

No renderer/product code is changed during Phase 2.

## Batch 1 — pending owner review

### B1-01 — M50800-02 Custom v1 generation / “Lectura de presagios”

**Original observation:** owner runtime on `15f86ec...` failed before producing Custom v1 PDF with:

`Custom-v1 source-matched label requires excessive compression: 'Lectura de presagios' (49.175125%)`.

**Repair attempted:**

- Desktop commit `ab9ea202710866621f1fc9003a5c79652ee9566c`;
- Android commit `ab7356320450caa2dd2410490beb8eb0339c5d2f`.

Long Custom-v1 skill labels are converted to multiple physical lines with `skillLines` before module packing. Total/training marker remains only on the first physical line.

**Evidence from exact final repair proof:** `mara-custom-v1-stress-baseline.pdf` from artifact `10942589698` generates successfully with 18 pages. Page 6 visibly contains “Lectura de presagios” as wrapped content and the cross-family failures file is empty.

**Provisional classification:** **KEEP** for the specific generation blocker. This does not approve the whole Custom-v1 Statistics layout.

**Owner question:** confirm whether the intended behavior for long custom skill names is wrapping to additional physical rows at normal/readable scale rather than further compression.

### B1-02 — M50800-04 Custom Statistics visual grammar

**Original observation / later clarification:** Extended custom attributes must reuse the already-correct Custom-v2 page-1/native attribute grammar. The QA rejected generic large black/white cards and later clarified that a generic Extended reconstruction is not acceptable merely because it includes source-like ornament fragments.

**Repair attempted:** several iterations replaced broken programmatic score/modifier boxes with source-derived ornament crops:

- `d62bc99...` restored source attribute ornament;
- `2419e557...` / `d98958e...` preserved light/dark source variants;
- `ea3e4e79...` / `db0c030...` corrected measured dark crop.

**Actual final proof:** source ornament fragments are visibly improved, but the Extended layout remains a bespoke reconstruction:
- Per Attribute uses three 184-pt custom-stat columns across the page;
- Per Ability uses a custom three-column “ATRIBUTOS / TIRADAS / HABILIDADES” table;
- neither reproduces the page-1 native attribute arrangement as a whole.

**Provisional classification:** **MODIFY/REPLACE presentation layer**. KEEP the recovered source ornament assets/crop measurements, but the surrounding Extended Custom Statistics layout does not satisfy the later reuse-first requirement.

**Owner question:** confirm that source ornament reuse alone is insufficient and that the Extended layout should be rebuilt around the native page-1 attribute module/grammar rather than retaining these generic containers.

### B1-03 — M50800-05 Custom Statistics capacity

**Original clarification:** Mara has four custom attributes — Fortuna, Cordura, Éter, Renombre — and the native-scale grammar should allow all four together; the previous three-card page limit is not a product requirement.

**Repair state in `ce695c...`:**
- Per Attribute still has `ATTRIBUTE_COLUMNS_PER_PAGE = 3`;
- final proof page 5 renders Fortuna/Cordura/Éter;
- page 6 contains Renombre alone.
- Per Ability has `ABILITY_ATTRIBUTES_PER_PAGE = 6`, so all four Mara custom attributes fit on page 5.

**Provisional classification:** **PARTIAL / MODIFY**. Per Ability removes the capacity problem for this fixture; Per Attribute explicitly preserves the rejected three-per-page limit.

**Owner question:** confirm that Per Attribute should also place all four Mara custom attributes together using the native-scale grammar, rather than preserving a three-column capacity.

### B1-04 — M50800-06 phantom custom attributes

**Original observation:** the old output painted unused custom-stat capacity as fake attribute cards/shells, visually indistinguishable from missing statistics.

**Repair attempted:**
- Per Attribute Desktop/Android: `5bd9cc18...` / `9254b262...` draw structures/markers only for `attributes.size`;
- Per Ability Desktop/Android: `bf55ab86...` / `fe5d4449...` change fixed repetition to `repeat(attributes.size)`.

**Actual final proof:**
- Per Attribute page 6 shows only the real Renombre card; the two old fake sibling cards are gone.
- Per Ability page 5 shows the four real Mara attributes and no additional fake attribute cards.

Empty table rows reserved for saves/skills remain, but they are not painted as additional attribute identities.

**Provisional classification:** **KEEP / specific defect appears corrected**. This does not approve the general visual grammar or capacity.

**Owner question:** confirm that this exact phantom-card defect can be considered correctly addressed, while leaving the surrounding Custom Statistics design open.

### B1-05 — M50800-07 Éter identity/presentation

**Original observation:** the old card appeared approximately as `ETE ter`, losing an unambiguous representation of Éter.

**Repair attempted:**
- Desktop/Android identity commits `43aff39a...` / `bc8cc476...` force explicit `<KEY> · <full name>` construction instead of merging abbreviation/name heuristically.

**Actual final proof:** both Custom-v2 presentations visibly render `ETE · Éter`; extracted PDF text also contains `ETE · Éter`.

**Provisional classification:** **KEEP / specific identity defect appears corrected**. The label may later move if the whole Custom Statistics component is replaced, but the identity rule itself is sound.

**Owner question:** confirm that `ETE · Éter` is the intended unambiguous presentation and that this identity rule should be retained even if the surrounding layout is rebuilt.

## Batch 1 evidence

Final repair proof artifact: `10942589698` from Scaffold #4088 / `36354213574`, repair head `ce695c...`.

Relevant proof files inspected directly:

- `mara-custom-v1-stress-baseline.pdf`;
- `mara-custom-v2-attribute-stress-baseline.pdf`;
- `mara-custom-v2-ability-stress-baseline.pdf`;
- `mara-cross-family-failures.txt`.

No page-count result is treated as acceptance by itself.

## Gate

Batch 1 remains **PENDING OWNER REVIEW**. No item above becomes final KEEP/MODIFY/REPLACE until the owner responds.

Phase 2 may continue technical investigation independently, but no renderer changes are permitted.


## Owner response — Batch 1

Recorded immediately after owner review. These responses supersede any narrower provisional interpretation above.

### B1-01 — Custom v1 long skill names

**Owner clarification:**

- Compression is acceptable up to a **reasonable, readable limit**.
- If the available vertical space permits wrapping without breaking the design or overlapping other elements, wrapping to two lines is acceptable/preferred.
- Compression may still be used where appropriate.
- **Uniformity across wrapped lines is mandatory:** do not render the first line compressed and the second line uncompressed, because that produces visibly inconsistent typography.

**Audit consequence:** provisional KEEP is narrowed to **KEEP/MODIFY typography behavior**. The multi-line routing is useful, but final acceptance requires a consistent scale decision across all physical lines belonging to the same semantic label and a readable compression floor.

**Owner status:** clarified.

### B1-02 — Custom-v2 native attribute score/modifier boxes

**Owner clarification:**

The prior audit question mixed more than one design issue. For this item, isolate only the score/modifier attribute ornament.

The required construction is explicitly layered:

1. **Layer 1:** background colors / base page color fields.
2. **Layer 2:** the **exact native score/modifier boxes from page 1**, reused as the visual component.
3. Additional text/value/marker layers may then be applied as appropriate.

Earlier approved/example PDFs had already demonstrated use of those exact native boxes. The owner requires the native page-1 ornament to be the source of truth, not a generic/programmatic approximation.

**Audit consequence:** source-crop/ornament recovery work is potentially reusable, but only if it reproduces the exact native page-1 boxes as their own layer. Any broader question about the surrounding Custom Statistics page layout must be audited as a **separate item** and must not be conflated with this ornament requirement.

**Owner status:** this ornament requirement is clear. A separate clarification item is required below for the broader layout question.

### B1-03 — Custom Statistics capacity

**Owner clarification:**

The target capacity is **up to six attributes per page at native scale**.

This is supported by the existing visual designs:
- Custom v2 already demonstrates six attributes can fit;
- Custom v1 page 1 also demonstrates six attributes in-line at native scale.

Therefore the repair must not preserve an arbitrary three-attribute-per-page limit when six can fit using the native-scale grammar.

**Audit consequence:** the existing `ATTRIBUTE_COLUMNS_PER_PAGE = 3` approach is not acceptable as a product rule. Classification: **MODIFY/REPLACE capacity/layout rule**.

**Owner status:** clarified.

### B1-04 — Phantom custom attributes

**Owner clarification:**

Phantom/empty attribute slots are not inherently objectionable provided that:

- they do **not** create phantom skills beneath them;
- the physical area associated with unused attributes can be reclaimed by other content **to the reasonable extent that the design permits**.

The current repair, which removes the fake attribute cards, is acceptable and may be treated as resolved. The above nuance remains a design consideration but is not a blocker if it is not applied.

**Audit consequence:** classification remains **KEEP / defect resolved**, with the non-blocking reclaim/phantom-skill consideration recorded.

**Owner status:** resolved.

### B1-05 — Éter identity / mode-specific presentation

**Owner clarification — RESOLVED:**

The display rule is now explicit:

- when the **attribute title/name itself** is rendered, use the integrated native-style identity **`ETEr`**;
- everywhere the attribute is referenced in abbreviated form, use **`ETE`** only;
- in per-Ability / por Habilidad presentation, a linked skill therefore appears in the form **`<skill name> (ETE)`**.

Examples:

- attribute heading: `ETEr`;
- linked skill reference: `Lectura de presagios (ETE)`;
- any other compact/keyed reference: `ETE`, not `ETE · Éter`.

The final repair already has the correct compact-reference helper for linked skills (`abilityKey(...)` returns the three-letter abbreviation, so `(ETE)` is produced). However, its main custom-attribute title helper currently produces `ETE · Éter`, which does **not** satisfy the owner's title rule.

**Audit consequence:** classify the compact-reference behavior as **KEEP**, but classify the main custom-attribute title formatting as **MODIFY**. M50800-07 is therefore **PARTIALLY RESOLVED / MODIFY** rather than KEEP.

**Owner status:** resolved.

## Pending clarification after Batch 1

Two points remain intentionally open:

- **Clarification Point 2:** broader Custom Statistics layout/grammar, separated from the native score/modifier-box requirement.
- **Clarification Point 5:** RESOLVED — attribute title = `ETEr`; abbreviated references = `ETE` (e.g. `Lectura de presagios (ETE)`).

No other Batch-1 owner answer remains ambiguous enough to require another question.


## Clarification pending from Batch 1 — Point 2

### Clarification Point 2 — exact native score/modifier ornament reuse

The owner separated this from the broader Custom Statistics layout question.

Current understanding to confirm:

For each Custom-v2 attribute module in Extended output, construction should preserve the multi-layer model:

1. **STRUCTURE/background layer:** native/source-matched background color/band;
2. **native ornament layer:** copy/reuse the **exact page-1 native score + modifier ornament/boxes at native geometry/scale**, repeated for each real attribute as needed;
3. later semantic layers place title/score/modifier/skills/markers without redrawing a generic replacement for those boxes.

The recovered source crops/measurements in the repair are useful only insofar as they reproduce that exact native ornament. The question is intentionally limited to the ornament/box component; broader page composition is audited separately.

**Owner question:** Is the understanding above exact, including preserving the native score/modifier box geometry rather than merely using a visually similar crop inside a newly invented score/modifier component?

**Status:** PENDING OWNER.

## Batch 2 — pending owner review

### B2-06 — M50800-03 stale template/source underlay

**Original observation:** the 50800 Extended output retained hidden/searchable template text and sometimes visibly leaked unrelated source content into current sections.

**Repair attempted:** commit `6dc8e69c...` removed use of whole clipped source-page forms for repeated Extended headers/components where hidden text could survive. In Custom v2 the header changed from a clipped source form to the exact imported source logo image; Custom v1 similarly replaced broad source crops with isolated source imagery/programmatic structure. The existing semantic OCG/layer model remains.

**Evidence:** the final repair test explicitly rejects stale labels such as `EQUIPO ESPECIAL`, `VÍNCULOS`, `IDEALES`, `HISTORIA`, and `PUNTOS DE VIDA` on Custom Statistics pages. Direct text-layer inspection of the final Mara Custom-v2 Attribute proof found no such unrelated stale labels on Extended pages; occurrences of `TRASFONDO` on the Traits page and `EQUIPO ESPECIAL` on Inventory are legitimate current-page semantics.

**Provisional classification:** **KEEP with regression-strengthening**. The technique appears to fix the observed hidden-text contamination while preserving layered construction, but future regression should scan all relevant Extended roles/families rather than only a first Custom Statistics page.

**Owner question:** Is it acceptable to use an isolated raster/source image for a source fragment such as the logo or native ornament specifically to prevent hidden source-template text, provided that the PDF page itself still preserves the intended independent semantic layers and the whole page is not flattened?

### B2-07 — M50800-08 Traits/Rasgos source reuse

**Original observation/clarification:** Extended Traits/Rasgos must reuse the established sheet visual grammar instead of becoming an unrelated generic multi-panel appendix.

**Repair attempted:** the final Custom-v2 repair keeps a structured overview page with `RASGOS Y ATRIBUTOS`, `CLASE / DOTES`, `RAZA / TRASFONDO / OTROS`, `OTROS RASGOS`, `DETALLES / NOTAS`, and `COMPETENCIAS / IDIOMAS`, using the v2 gray/ruled cadence. Once overview capacity is exhausted, continuation pages switch to two full-width native-like ruled columns titled `DETALLES / NOTAS`.

**Evidence:** final Mara pages 7–10 show one structured overview followed by three dense two-column continuation pages rather than repeating the full six-panel scaffold.

**Provisional classification:** **MODIFY/KEEP candidate**, depending on owner intent. It clearly moves toward the native v2 visual language, but it is still programmatically reconstructed rather than literally copying one complete source component.

**Owner question:** Is the desired rule that the overview may keep these semantically native v2 sections and continuation pages may collapse to the native ruled-row language, or do you require the Traits/Rasgos blocks themselves to be copied more literally from an existing source component before any adaptive continuation occurs?

### B2-08 — M50800-09 Traits ordering

**Original owner clarification:** grouping by semantic category is intentional; a single global numeric 1…26 order across categories is not required. Order should remain coherent **within** each category.

**Repair state:** the code begins from `sortOrder`, splits traits into semantic left/right groups, then applies a `featurePriority` so traits carrying uses/recovery/notes are selected as featured before other traits, with `sortOrder` used inside that priority. The Mara proof therefore begins details in the visible order 01, 04, 02, 03, then 05 onward rather than strict global numeric order.

**Provisional classification:** **PENDING OWNER**. Category grouping is consistent with the clarified requirement; the remaining ambiguity is whether `featurePriority` is allowed to reorder records inside a category.

**Owner question:** Within a category, should original `sortOrder` remain strict, or is it acceptable for “featured” traits with uses/recovery/notes to be promoted ahead of otherwise earlier traits in the same category?

### B2-09 — M50800-10 Traits continuation packing

**Original observation:** old pages repeatedly reserved the complete four/six-panel Traits scaffold even after those sections were exhausted, producing many mostly-empty pages and constraining useful text to a small area.

**Repair attempted:** `d5a113db...` introduced dedicated two-column continuation pages after the overview; `17289a41...` then made packing record-aware so a trait record is kept together when it can fit in the next physical column rather than being split merely to fill the last lines.

**Evidence:** Mara Custom-v2 Attribute moved from the old pathological pages 7–18 pattern to pages 7–10: page 7 is the structured overview; pages 8–10 use nearly the complete two-column writing area for remaining trait records. The old empty category panels are not repeated.

**Provisional classification:** **KEEP / likely real correction** for the packing defect. This does not automatically approve every visual detail of the Traits design.

**Owner question:** Is this the intended adaptive pattern: one structured Traits/Rasgos overview using the family grammar, followed—only when needed—by dense continuation pages that reclaim essentially the full usable writing area while preserving record boundaries?

### B2-10 — M50800-12 Custom-v2 Combat/Actions

**Original observation:** Combat/Actions had header/first-row collision, excessive vertical row height, poor field association, and mostly-empty continuation space.

**Repair attempted:** `0cc63991...` moved the first content rule down from 137 to 154 pt, reduced fixed physical row cadence from 42 to 26 pt, and increased physical capacity from 14 to 22 rows. The renderer preserves explicit columns `TIPO/NOMBRE | RANGO | BONIF. | DAÑO/EFECTO | NOTAS` and wraps overflow onto subsequent physical rows.

**Evidence:** final Mara page 11 has a cleanly separated header and recognizable columns. Packing is materially denser. However logical records often occupy two physical bands and the Notes column uses very small text; the algorithm still uses a fixed 26-pt physical-row cadence rather than a genuinely content-sized logical row.

**Provisional classification:** **PARTIAL / MODIFY**. Header collision and gross row waste are improved; record-level layout/readability is not convincingly closed.

**Owner question:** Should one logical combat/action record behave as one visual row that grows vertically only as much as its wrapped cells need (with all its columns kept associated), rather than being represented as a fixed sequence of 26-pt physical rows as in the current repair?

## Batch 2 evidence

Directly inspected final repair proof:

- `mara-custom-v2-attribute-stress-baseline.pdf` pages 7–12;
- final source/text layer from repair head `ce695c...`.

Relevant repair commits include:

- underlay: `6dc8e69c...`;
- Traits adaptive continuation: `d5a113db...`;
- Traits record boundaries: `17289a41...`;
- Combat compact/header separation: `0cc63991...`.

Batch 2 is **PENDING OWNER REVIEW**. No renderer/product code changes are permitted.


## Owner follow-up — Batch 1/2 clarifications

### B1-05 — Éter / abbreviated ability-reference rule — EXPANDED

The owner further clarified that the `(ETE)` suffix is **mode/context dependent**, not universally required.

Required behavior:

- **Per Attribute / por Atributo:** when a skill is listed directly under its owning attribute, do **not** redundantly append `(ETE)` because the parent attribute is already visually explicit above it.
- **Per Ability / por Habilidad:** when skills are listed together outside an owning attribute block, append the compact ability key, e.g. `Lectura de presagios (ETE)`.
- The same compact-key rule applies when **overflow/extra skills associated with existing standard abilities** (e.g. Strength/Wisdom) must be listed outside the page-1 native block because they no longer fit there:
  - `<skill> (FUE)`;
  - `<skill> (SAB)`;
  - etc.
- Attribute title/name remains integrated native-style, e.g. `ETEr`.
- Compact references use only the three-letter key, e.g. `ETE`.

**Audit consequence:** any unconditional suffixing strategy is wrong. The formatter must know whether the owning attribute is already structurally explicit. Classification remains **PARTIALLY RESOLVED / MODIFY**, with existing compact-key helper potentially reusable.

### Clarification Point 2 — native boxes / 7 mm native row grammar — RESOLVED

The owner confirmed the earlier understanding exactly.

Required layered construction:

1. background/fill color layer;
2. exact native page-1 score/modifier box ornament as the next visual layer;
3. later semantic text/value/marker layers.

Do not replace the native ornament with a merely similar programmatic box when the original component can be reused.

Additional source-authoring fact supplied by the owner:

- the bicolour ruled/table rows used across Custom v1/v2 (e.g. Equipment, Background and analogous tables) were designed in Adobe InDesign at **7 mm row height**;
- 7 mm is the intended physical handwriting-friendly row cadence;
- pixel/raster measurements can obscure this design fact, so the implementation should treat **7 mm (~19.84 pt)** as an authoritative physical-design reference when reproducing such source-native rows.

Fallback rule when a new box must genuinely be created because no reusable source object/component exists:

- it must be visually almost indistinguishable from the owner's established family grammar;
- e.g. Custom v1 boxes use the characteristic border construction (owner recalls thin / thick / thin line treatment);
- family-specific stroke/fill/spacing grammar must be measured/reused rather than replaced by a generic rectangle.

**Audit consequence:** Clarification Point 2 is closed. Exact source-component reuse is the default; source-faithful reconstruction is a fallback only when copying/reusing is impossible.

### B2-06 — isolated source objects / placement — RESOLVED

The owner accepts isolated source-derived raster/image objects to avoid hidden template contamination.

Placement rule:

- copy/reuse the source object itself;
- **do not assume the original source-page coordinates must be preserved**;
- reposition the object to the location required by the new Extended layout and the semantic role it serves;
- never place it over another important element merely to preserve source coordinates or visual resemblance.

**Audit consequence:** classification becomes **KEEP** for the isolation technique, with placement governed by collision-free adaptive composition.

### B2-07 — Traits/Rasgos and adaptive Extended page composition — MAJOR CLARIFICATION

The owner clarified that the key requirement is broader than merely changing the Traits continuation page.

Extended pages must **not** be treated as a fixed multi-zone page template whose unused sections remain reserved.

Example supplied by owner:

If overflow exists only for:
- Rasgos; and
- Equipo;

then a new page should contain **only those two active modules**, with their available page area expanded/reallocated appropriately.

Rejected pattern:

- create a fixed page containing six semantic slots;
- populate only Rasgos and Equipo;
- leave the other four slots empty.

Accepted conceptual pattern:

- compose the page dynamically from the content streams that actually remain;
- include only active/warranted modules;
- allocate page width/height among those modules according to their remaining content and family-native visual grammar;
- allow modules to reclaim space from absent/exhausted modules;
- generate another page only when remaining content genuinely requires one.

The page remains family-matched:
- reuse/copy native modules where possible;
- preserve source-family visual grammar;
- source-derived objects may be repositioned;
- new/reconstructed boxes must be source-faithful;
- no important visual/data element may be overprinted.

**Audit consequence:** the current repair's pattern of having separate role-specific Extended pages (Traits page, Resources page, Inventory page, Notes page, etc.) is **not sufficient as the final architecture** if multiple remaining roles could share one page efficiently. The adaptive repair made useful local packing improvements, but a higher-level **cross-role page composition layer** is still required.

Therefore B2-07 is reclassified from KEEP/MODIFY candidate to **PARTIAL / ARCHITECTURAL GAP**:
- KEEP family-native module/continuation primitives where valid;
- MODIFY/REPLACE fixed role-page allocation;
- add a dynamic page composer that decides which active modules share each Extended page.

This clarification also materially affects later review of Resources, Equipment, Notes and other continuation roles; those items must be audited under this cross-role composition rule.


## Owner follow-up — Batch 2 responses

### B1-05 — mode/context rule for attribute suffixes — FINAL CLARIFICATION

The owner reaffirmed and sharpened the context rule:

- In **per Attribute / por Atributo**, do not append a compact key such as `(ETE)` to a skill when the owning custom attribute is already visually explicit above the skill list.
- However, if **extra/overflow skills linked to existing built-in attributes** no longer fit the native page-1 block and must appear elsewhere, they **must** carry the compact owning key:
  - `<skill> (FUE)`;
  - `<skill> (SAB)`;
  - etc.
- The general product rule is therefore structural: show the compact attribute key only when the skill's owning attribute is **not already unambiguously established by the surrounding block**.

**Audit consequence:** formatting must be context-aware; no unconditional suffix rule.

### Clarification Point 2 — CONFIRMED

The owner confirmed the previously recorded native ornament/layer interpretation exactly.

Additional authoritative family-design facts:

- Custom v1/v2 bicolour table rows (e.g. Equipment, Background and analogous tables) were authored in Adobe InDesign at **7 mm physical row height**, chosen as the handwriting-friendly target.
- When source components can be copied/reused, they should be.
- If a component genuinely cannot be copied and must be reconstructed, the reconstruction must be visually almost indistinguishable from the owner's source-family design.
- Example family cue: Custom v1 box borders use a characteristic multi-stroke treatment remembered by the owner as **thin / thick / thin** rather than a generic single rectangle.

### B2-06 — source-object reuse and relocation — CONFIRMED

The owner confirms:

- copy/reuse the source object itself;
- original source coordinates are not mandatory;
- place the object where the new semantic/layout purpose requires it;
- **never overprint or cover another important element** merely to preserve a source position.

### B2-07 — cross-role adaptive Extended composition — CONFIRMED AND STRENGTHENED

Owner example:

If only **Rasgos** and **Equipo** remain to overflow, a new Extended page should not preserve unrelated empty zones. It should compose only the active modules.

Rejected conceptual page:

```
| cosa 1 | cosa 2 |
| RASGOS | cosa 3 |
| cosa 4 | EQUIPO |
```

Desired conceptual page:

```
| RASGOS | EQUIPO |
| ...    | ...    |
| ...    | ...    |
```

The exact split need not be fixed by this sketch; the key rule is that only currently active content roles consume page area.

**Architectural consequence:** the repair needs a higher-level Extended-page layout selector/composer with multiple valid family-matched page arrangements. It should choose an appropriate arrangement according to which roles remain and how much content each still has, rather than assigning every role its own fixed page or reserving empty slots.

### B2-08 — owner requests clarification by example/wireframe

The owner did not yet resolve whether feature-priority may reorder traits inside a category. Re-present with a concrete wireframe/example.

### B2-09 — multiple page layouts / choose correct layout based on remaining content

The owner clarified the desired architecture further:

- there should be **multiple possible Extended page formats/layouts**;
- the renderer should choose the correct format depending on **how much information remains to be shown across the entire character sheet**;
- this is broader than merely giving Rasgos its own adaptive continuation page.

**Audit consequence:** KEEP local packing primitives; current role-specific page allocation remains an architectural gap. The eventual solution needs a page-layout decision layer that selects among several family-native compositions based on remaining active roles/content volume.

The owner requests a follow-up only if this interpretation is still unclear.

### B2-10 — owner requests clarification by example/wireframe

The owner did not yet resolve the logical-row vs fixed-physical-band Combat/Actions behavior. Re-present with concrete wireframes.


## Clarification wireframes + Batch 3 staging

This section records the next owner-review set. It contains unresolved clarifications B2-08/B2-09/B2-10 plus two new audit points, for a maximum of five owner decisions.

### Clarification B2-08 — order inside a category

Current repair behavior can promote a later trait ahead of an earlier trait inside the same semantic category when the later trait has uses/recovery/notes.

Illustrative case:

Source order inside category CLASE:
- Rasgo A — sortOrder 1
- Rasgo B — sortOrder 2
- Rasgo C — sortOrder 3, has uses/recovery

Strict sortOrder would render:
```
CLASE
Rasgo A
Rasgo B
Rasgo C [3/3]
```

Current feature-priority logic may render:
```
CLASE
Rasgo C [3/3]
Rasgo A
Rasgo B
```

**Owner question:** inside one category, is the first or second behavior desired?

### Clarification B2-09 — page-layout selection architecture

Owner intent currently understood as:

- maintain multiple valid family-native Extended page layouts;
- inspect all remaining overflow roles and their content volume;
- choose the most appropriate composition for that page;
- do not reserve absent-role slots;
- do not force every role onto its own page;
- when one role ends, remaining roles may reclaim its area on later pages.

Illustrative selection:

```
Remaining:
Rasgos = medium
Equipo = medium
Notas = none
Recursos = none

Choose:
+-------------------------------+
| RASGOS        | EQUIPO        |
| ...           | ...           |
| ...           | ...           |
+-------------------------------+
```

Another state:

```
Remaining:
Rasgos = very large
Equipo = small

Possible chosen layout:
+-------------------------------+
| RASGOS                        |
|                               |
|                               |
+-------------------------------+
| EQUIPO                        |
+-------------------------------+
```

The exact variants are not yet designed; Phase 2 only establishes the product rule.

**Owner question:** confirm that this multiple-layout selector interpretation matches the desired architecture.

### Clarification B2-10 — logical Combat/Actions row

Current Custom-v2 repair uses fixed physical bands of ~26 pt. A long logical record may consume multiple bands.

Current-style conceptual result:
```
+---------+------+-------+----------+-------------+
| Espada  | 5 ft | +7    | 1d8+4    | primera     |
+---------+------+-------+----------+-------------+
|         |      |       |          | parte nota  |
+---------+------+-------+----------+-------------+
|         |      |       |          | segunda     |
+---------+------+-------+----------+-------------+
```

Proposed logical-row behavior:
```
+---------+------+-------+----------+-------------+
| Espada  | 5 ft | +7    | 1d8+4    | primera     |
|         |      |       |          | parte nota  |
|         |      |       |          | segunda     |
+---------+------+-------+----------+-------------+
```

In the second form, the action is one semantic row whose height is determined by the tallest wrapped cell; all columns share the same top/bottom boundary.

**Owner question:** which behavior is desired?

### B3-11 — M50800-11 Trasfondo/Historia and narrative modules

**Original requirement:** when source/native modules already exist for Trasfondo/Historia and analogous narrative sections, Extended overflow should preserve that module identity, hierarchy, font roles and writing rhythm rather than flattening the content into generic text elsewhere.

**Repair state at `ce695c...`:**

Custom-v2 does not create a dedicated native-like narrative overflow module. Instead:
- `Trasfondo`, `Vínculos`, `Ideales`, and `Historia` overflow is converted to generic wrapped text by `traitSupplementLines(...)` and injected into the Traits/Rasgos detail flow;
- personality traits/flaws/religion may also be replayed as generic Note paragraphs.

Fantasy similarly converts long background/story overflow to generic labelled reference/continuation text.

**Provisional classification:** **DESVÍO / GAP** under the owner's reuse-first rule and newly clarified cross-role composer architecture. The data routing exists, but the native narrative module is lost.

**Owner question:** should Trasfondo/Historia overflow become its own composable native-style module (copying/reusing the family source section as closely as possible), which the global Extended-page composer may place beside/above/below other active modules depending on remaining content?

### B3-12 — M50800-13 Fantasy Combat/Actions semantic table

**Original requirement:** Fantasy continuation must preserve the semantic scan pattern of the base combat/action table rather than flattening name/bonus/range/damage/notes into prose.

**Repair state at `ce695c...`:**

The Fantasy base page still has a clear table-like `ARMAS Y ACCIONES` region with columns such as:
- Nombre;
- Bonif.;
- Daño / notas.

But overflow continuation `appendCombatPages(...)` still:
- builds one prose-like string per combat entry;
- concatenates fields with separators (`Ataque — Nombre · Ataque +X · Efecto/daño... · Alcance... · Notas...`);
- wraps the resulting text into a ruled text area headed `REFERENCIA DE COMBATE / ACCIÓN / DAÑO`.

Thus the repair did not restore table semantics for Fantasy overflow.

**Provisional classification:** **MISSING / REPLACE continuation presentation**. Data preservation exists; the visual/semantic grammar does not satisfy the QA.

**Owner question:** should Fantasy overflow reuse/extend the existing base `ARMAS Y ACCIONES` table grammar (adding rows/pages and allowing content-driven row height as needed) rather than use the current prose/reference continuation block?

## Gate

The five owner decisions in this review set are:
1. clarification B2-08;
2. clarification B2-09;
3. clarification B2-10;
4. B3-11;
5. B3-12.

No renderer/product changes are made during this review.


## Owner responses — clarification set 8–12

### B2-08 — trait order inside categories — OWNER DIRECTION

The owner rejects both a strict synthetic priority order and the current feature-priority reordering as a universal rule.

Practical character-sheet reality:

- players typically enter race/species traits first;
- then class traits;
- then background traits;
- later additions arrive in irregular order: feats, gifts/blessings, weapon-derived traits, later-level class traits, etc.;
- therefore there is no semantically correct global system order independent of player entry/order.

Owner direction:

- the strongest candidate authority is the **order supplied by the player / stored sheet order**;
- do not promote a later trait merely because it has uses/recovery/notes;
- category grouping may still be used where the visual module requires it, but ordering inside a category should preserve the player-defined/stored order rather than invent a priority heuristic.

**Audit consequence:** current `featurePriority` logic is **MODIFY/REMOVE** unless later evidence proves that stored/player order already encodes the intended visual order.

**Owner status:** substantially resolved; implementation should treat player/stored order as the default authority.

### New cross-cutting requirement — editable use/ammunition trackers

The owner added an important handwriting/editability rule applying at least to:

- trait/use trackers;
- ammunition trackers.

A generated PDF must not pre-consume/fill the marks representing already-used capacity in a way that prevents the player from editing the printed sheet naturally.

The writable tracker should represent **available capacity / blank state**, e.g. conceptually:

- `○ ○ ○ ○ ○` / empty boxes rather than pre-filled spent marks;
- or a writable counter shape such as `____ / 3`.

The PDF is a writable character sheet, not merely a snapshot dashboard. Existing state may need separate semantic treatment if it must be conveyed, but the handwriting control itself must remain usable.

**Status:** OWNER REQUIREMENT RECORDED; one clarification remains below about whether current/spent state should be printed elsewhere or omitted entirely.

### B2-09 — adaptive page-layout selector — OWNER DIRECTION WITH MODULE CONSTRAINTS

The owner confirms the multiple-layout selector architecture, with important per-module geometry constraints.

Different modules have different legitimate flexibility:

- **Rasgos:** horizontally occupies approximately half-page width as a native module.
- **Trasfondo / narrative modules:** likewise normally occupy half-page width.
- **Equipo especial:** owner states its occupied space is effectively **fixed**; exact meaning/axis requires one clarification below.
- **Equipo normal:** highly flexible; may expand/contract substantially to fit available page area.
- **Notas:** exceptional case; when Notes overflow warrants another Notes page, copy/reuse the **entire native Notes page**, rather than sharing it with unrelated modules.
- **Acciones/Ataques:** highly flexible and may use almost any practical width.

Therefore the page composer must not treat all modules as arbitrary rectangles. Each module exposes family-native placement/size constraints, and the selector chooses among valid page layouts that satisfy those constraints while minimizing wasted space and preserving handwriting usability.

**Audit consequence:** cross-role compositor requirement is CONFIRMED. Layout selection must be constraint-aware, not generic bin-packing.

### B2-10 — Combat/Actions logical row — OWNER CONFIRMED WITH STRONG DESIGN DIRECTION

The owner favors the single logical-row model.

For an Extended page dedicated substantially to actions/attacks:

- use the **full page width** where practical;
- give generous width to the action's special/effect/notes section to avoid unnecessary wrapping;
- one action should remain a coherent visual record;
- row height may grow according to the content that actually needs more lines;
- avoid splitting one action into several visually independent boxes/bands, because that becomes confusing and difficult to scan.

The base first page may intentionally force concise writing because space is constrained, but an Extended action page should use its additional space to improve readability rather than reproduce that constraint unnecessarily.

**Audit consequence:** current fixed 26-pt physical-band implementation is **PARTIAL / MODIFY**. Keep header/table semantics; move to content-driven logical row height and wider full-page composition when the layout selector chooses an action-heavy page.

### B3-11 — Trasfondo/Historia module identity — OWNER CONFIRMED + CONTINUITY CONTRACT

The owner confirms the original question: Trasfondo/Historia overflow must preserve its own native/module identity and participate in the Extended-page compositor rather than being flattened into generic Traits/Notes prose.

The owner additionally defines an explicit **continuity navigation contract** for text that flows across modules/pages.

Example for Historia:

Base/native section ends with a visible continuation marker:

`...[continua en sección extendida HISTORIA 01]...`

The first Extended Historia segment begins with:

`...[proviene de sección normal HISTORIA]...`

If that Extended segment itself overflows, it ends with:

`...[continua en sección extendida HISTORIA 02]...`

The next Extended segment begins with:

`...[proviene de sección extendida HISTORIA 01]...`

and so on.

Required properties:

- continuation targets are explicitly named;
- sequence numbers make multiple Extended segments unambiguous;
- each continuation segment identifies its immediate source;
- the reader can navigate both forward and backward without guessing;
- markers must be visible and must not overwrite meaningful content.

**Audit consequence:** existing generic overflow routing does not satisfy this. The future flow model needs source/target-aware continuation metadata and rendering support, not merely text chunks.

**Owner status:** clear and confirmed.

### B3-12 — Fantasy Combat/Actions semantic table — OWNER CONFIRMED

The owner confirms the simple rule:

- Fantasy overflow must preserve/extend the base Combat/Actions table grammar;
- the current prose/reference continuation is not acceptable.

**Audit consequence:** current Fantasy continuation presentation is **REPLACE**; data extraction can be retained.

## Remaining clarifications from this response

### Clarification 8A — writable tracker state

The owner requires blank/editable use/ammunition controls such as empty marks or `____ / 3`.

**Question:** if the character currently has some uses/ammunition already spent at export time, should the PDF:
A. omit that spent/current state entirely and show only the blank writable capacity; or
B. preserve the current/spent state somewhere as separate text while leaving the handwriting tracker itself blank/editable?

### Clarification 9A — Equipo Especial fixed geometry

The owner states that Equipo Especial has effectively **100% fixed occupied space**.

**Question:** does “fixed” mean the native module's full geometry (both width and height) should be copied as-is whenever it appears, or specifically that its **width** is fixed while its height may extend by adding/repeating native rows?


### Clarification 11A — scope of continuity navigation contract

The Historia example is understood exactly.

One remaining scope question is recorded for owner confirmation:

Should the same bidirectional continuity-marker contract (`continua en...` / `proviene de...`) become a **general rule for any semantic text/module that is split across non-contiguous sections/pages**, or should it apply only to narrative modules such as Historia/Trasfondo?

No implementation assumption is made until owner confirms scope.


## Safety checkpoint — owner temporarily away

**State:** SAFE / PERSISTED / NO IMPLEMENTATION WORK

Everything discussed through the owner's latest message has been persisted on branch `audit/mara-phase2-existing-repair`.

Persisted decisions/clarifications include:

- B1-01 long-skill behavior: readable compression is allowed; multiline wrap is allowed when useful; all wrapped lines belonging to one semantic label must use consistent scale.
- B1-02 / Clarification Point 2: exact native Custom-v2 page-1 score/modifier ornament reuse as a separate layer; source-faithful reconstruction only when copying/reuse is genuinely impossible.
- source-family physical row reference: bicolour Custom-v1/v2 table rows were authored at 7 mm height for handwriting usability.
- B1-03: Custom Statistics capacity target supports up to six native-scale attributes per page; three-per-page is not a product rule.
- B1-04: phantom attribute-card defect may be treated as resolved; phantom skills are not allowed and unused space may be reclaimed where reasonable.
- B1-05: custom attribute title uses integrated native-style form (e.g. `ETEr`); compact references use the three-letter key (e.g. `ETE`). In per-Attribute mode, do not redundantly suffix skills when parent attribute is already explicit; overflow skills detached from an owning block use keys such as `(FUE)`, `(SAB)`, `(ETE)`.
- B2-06: isolated source objects may be raster/source-derived to prevent hidden underlay text; source coordinates are not binding; placement must serve the new layout and never cover important content.
- B2-07/B2-09: Extended pages require a cross-role, constraint-aware layout selector with multiple valid family-native formats chosen according to all remaining content. Do not reserve absent-role slots and do not force every role onto its own page.
- module constraints already recorded: Rasgos and Trasfondo are roughly half-page-width native modules; Equipo normal is highly flexible; Notes overflow uses the full native Notes page; Actions/Attacks are highly flexible; Equipo Especial fixed-geometry meaning still requires clarification 9A.
- B2-08: no synthetic feature-priority ordering; default authority is player/stored order, with category grouping preserved where applicable.
- new tracker requirement: trait-use/ammunition controls must remain handwriting-editable and must not pre-consume the writable marks; exact treatment of stored current/spent state remains clarification 8A.
- B2-10: Combat/Actions should preserve one logical visual record per action, use content-driven height, and use broad/full-page width on action-heavy Extended layouts to keep special/effect/notes readable.
- B3-11: Trasfondo/Historia retains its own native/module identity in Extended composition; explicit bidirectional continuation navigation is required between base and extended segments.
- B3-12: Fantasy Combat/Actions prose continuation is rejected; overflow must preserve/extend the base table grammar.

Open owner clarifications at this safety point:

1. **8A — writable tracker state:** whether stored current/spent state is omitted from the PDF entirely or shown separately while the handwriting tracker remains blank/editable.
2. **9A — Equipo Especial fixed geometry:** whether both width and height are fixed, or width is fixed while height may extend through repeated native 7 mm rows.
3. **11A — continuity-marker scope:** whether `continúa en...` / `proviene de...` is a general rule for any split semantic module or specifically narrative modules such as Historia/Trasfondo.

No further Phase 2 audit conclusions should be advanced past these questions while the owner is away.
No renderer/product code changes are authorized.


## Batch 4 — Resources/Options and Equipment — pending owner review

This batch continues after B3-12. Earlier open clarifications 8A and 11A remain open. Clarification 9A is folded into B4-17 because it directly governs Equipo Especial geometry.

### B4-13 — M50800-14 Resources/Options packing

**Original observation:** once Options were exhausted, the old renderer continued reserving/repeating a large empty Options scaffold while Resources still needed continuation space.

**Repair attempted:** `535070ed...` / `dc5d5caa...` changed Resources/Options pagination from a fixed paired-page model to three local modes:
- combined Resources + Options while both remain;
- Resources-only when Options are exhausted;
- Options-only when Resources are exhausted.

**Actual final proof:**
- Mara page 12 contains both Resources and Options;
- page 13 contains only Resources and reclaims almost the full page instead of repeating an empty Options block.

**What is genuinely useful:** the exhausted-stream detection and the ability for Resources to reclaim space are real improvements.

**What is no longer sufficient under the owner's later architecture:** the implementation still creates a dedicated Resources page. Under the confirmed cross-role compositor rule, Resources should become a composable module that may share a page with other remaining roles when a valid family-native layout permits it.

**Additional cross-cutting issue:** current resource counters render already-consumed/current state with filled/empty squares; this intersects unresolved clarification 8A about writable tracker state.

**Provisional classification:** **PARTIAL / KEEP local reclaim primitive + MODIFY page-allocation architecture**.

**Owner question:** confirm that the local rule “when Options ends, Resources may reclaim its space” is correct and reusable, but Resources/Options must ultimately participate in the global constraint-aware Extended-page composer rather than automatically own a continuation page.

### B4-14 — M50800-15 Ordinary Equipment design

**Original requirement:** do not invent a separate descriptive Inventory presentation. Reuse/copy the existing native ordinary `EQUIPO` element and increase capacity by repeating/adding its native rows/columns/pages as needed.

**Repair state at `ce695c...`:**
- the final repair replaced the earlier dedicated Equipment structures with a generic two-column `INVENTARIO / EQUIPO · CONTINUACIÓN` flow;
- each column is a long ruled text stream;
- section labels such as `EQUIPO` are inserted as text records into that stream.

**Actual final proof:** Mara page 14 is visibly a generic two-column continuation rather than a copy/extension of the native Equipment element from base page 2.

**Relevant owner design facts now known:**
- ordinary Equipment is highly flexible in page allocation;
- source-family bicolour table rows were authored at 7 mm physical height;
- source components should be copied/reused when available.

**Provisional classification:** **REPLACE presentation architecture**. Record-packing helpers may be reusable, but the generic two-column Inventory page is not the desired visual component.

**Owner question:** confirm that the generic two-column `INVENTARIO / EQUIPO` continuation should be discarded as a visual design, and ordinary Equipment should instead be represented by reusable native Equipment modules/rows that the global composer can resize/repeat within valid layouts.

### B4-15 — M50800-16 Ordinary Equipment content

**Original owner rule:** visible ordinary Equipment content is compact identity, e.g. `3 x Frasco de tinta`.
Do **not** display:
- weight;
- `Consumible`;
- prose descriptions.

**Repair state:**
- shared `pdfCompactEquipmentLabel()` explicitly appends weight (`· 2 lb`, etc.);
- Custom-v2 continuation adds operational status lines such as `Estado: Equipado`, `Estado: Consumible`, etc.;
- ordinary descriptions were removed from the Equipment row path, which is a useful partial correction.

**Actual final proof:** Mara page 14 visibly contains item weight and status/Consumible lines.

**Provisional classification:** **PARTIAL / MODIFY**:
- KEEP quantity + full item identity and the removal of prose descriptions from ordinary Equipment;
- REMOVE weight from the visible ordinary Equipment label;
- REMOVE `Consumible` and similar operational metadata from visible ordinary Equipment;
- preserve ammunition/use semantics only through the separately defined writable-tracker treatment, not by adding prose metadata to the item identity.

**Owner question:** no new product decision is required unless the owner disagrees; this follows an already explicit acceptance rule. Confirm only whether `Equipado` should also be absent from ordinary Equipment rows, consistent with the compact-identity-only rule.

### B4-16 — M50800-17 Ordinary Equipment readability / atomic identity

**Original observation:** item identity and metadata/descriptions were fragmented across physical streams/columns, making Equipment difficult to scan.

**Repair attempted:** record-aware packing keeps an item's wrapped lines together when possible and adds a continuation title if a record must cross a physical column.

**Actual final proof:** this improves record contiguity compared with the old failure, but the item presentation is still polluted by weight/status and uses a non-native generic flow. Long identities are also rendered in a relatively narrow half-page column even though ordinary Equipment is one of the most flexible modules.

**Owner's already-established typography rule:** readable compression is allowed; wrapping is allowed; all wrapped lines of one semantic label must use a consistent scale.

**Provisional classification:** **KEEP record-boundary concept / MODIFY visual implementation**.

The reusable principle should be:
- compact identity stays one semantic record;
- choose a reasonable native Equipment column width/layout first;
- apply bounded uniform compression if useful;
- then wrap consistently if needed;
- do not interleave another item until the record is complete;
- do not waste another page merely because a rigid Equipment slot ended.

**Owner question:** confirm this as the intended readability rule for ordinary Equipment.

### B4-17 — M50800-18 Equipo Especial native design + clarification 9A

**Original requirement:** the renderer invented a separate Extended table although a correct native/source `EQUIPO ESPECIAL` element already exists. Reuse/copy that native component and extend only capacity/rows as needed.

**Repair state at `ce695c...`:**
- the final repair abandoned the native Equipo Especial table in continuation;
- special items are converted into generic title/body records inside the same two-column Inventory flow;
- location, quantity, weight, Equipped/Attuned/Consumible state, description and notes are concatenated into wrapped prose-like lines.

**Actual final proof:** Mara page 15 is not the native Equipo Especial module; it is a generic two-column text continuation headed by `EQUIPO ESPECIAL`.

**Provisional classification:** **REPLACE continuation presentation**. Data extraction and record-boundary logic may survive, but the visual module must return to the native/source Equipo Especial grammar.

**Clarification 9A folded here:** the owner previously said Equipo Especial occupies effectively “100% fixed” space. Two interpretations remain:

A. **Full module geometry fixed:** copy the complete native module at its exact width and height. If more capacity is needed, place another complete module.

B. **Width/grammar fixed, vertical capacity extensible:** preserve native width, columns, header/strokes/row grammar, but extend height by adding/repeating native 7 mm rows when the selected page layout has room.

**Owner question:** choose A or B (or correct both) so the Phase-3 composer knows the true geometry contract for Equipo Especial.

## Batch 4 evidence

Direct final-repair evidence inspected:
- Mara Custom-v2 Attribute page 12 — combined Resources/Options;
- page 13 — Resources-only continuation;
- page 14 — ordinary Equipment generic two-column continuation;
- page 15 — Equipo Especial generic text-flow continuation.

Code evidence inspected at repair head `ce695c...`:
- Resources adaptive local modes in `appendResourcesExtendedPages`;
- generic Inventory flow in `appendInventoryExtendedPages` / `renderInventory`;
- shared `pdfCompactEquipmentLabel()` includes weight;
- continuation status path emits `Equipado`, `Consumible`, `Munición`, etc.

No renderer/product code changes are made in Phase 2.


## Owner responses — Batch 4 partial (14–17)

### B4-14 — Ordinary Equipment native geometry — CONFIRMED

The owner confirms that the generic two-column continuation design should be discarded.

Required rule:

- start from the **actual native Equipment element**;
- reuse its existing width/height and visual grammar as the baseline;
- the Extended-page composer may place/repeat that native module, but must not invent arbitrary new Equipment box dimensions or new families of height/width merely for packing convenience;
- flexibility comes from composing/repeating valid native modules/rows/layouts, not from redefining the module into an unrelated generic rectangle.

**Audit consequence:** generic `INVENTARIO / EQUIPO` continuation is **REPLACE**. Native Equipment module geometry becomes the authority.

### B4-15 — Ordinary Equipment visible content — CONFIRMED

The owner confirms that `Equipado` should also be absent from ordinary Equipment rows.

Visible ordinary Equipment row content is therefore limited to compact item identity:

- quantity when relevant;
- full item name/identity.

Do not show in the visible ordinary Equipment row:

- weight;
- `Consumible`;
- `Equipado`;
- operational state prose;
- prose description.

Ammunition/use semantics are governed separately by the writable-tracker rule.

**Audit consequence:** current `pdfCompactEquipmentLabel()` and continuation status rendering require modification.

### B4-16 — Ordinary Equipment atomic identity / wrapped-line indentation — CONFIRMED

The owner confirms the semantic-record rule:

- one item remains visually atomic;
- choose the native Equipment module width first;
- bounded readable compression is allowed;
- if wrapping is still required, wrapped lines use uniform scale;
- no next item begins before the current item finishes.

Additional owner requirement:

- from the **second wrapped line onward**, use visible indentation (or an equivalent clear continuation cue) so it is obvious that the line belongs to the item above rather than starting a new item.

Conceptual example:

```
3 x Cuaderno de fórmulas personales
    y mapas plegables
Antorcha
```

**Audit consequence:** record-boundary packing is reusable, but wrapped-line rendering must include a continuation indentation/cue within native Equipment grammar.

### B4-17 — Equipo Especial geometry — RESOLVED AS FIXED NATIVE MODULE

The owner clarifies that the native Equipo Especial module already includes capacity/space for **non-common/custom locations**.

Therefore:

- do not extend/redefine its geometry;
- do not invent a taller variant;
- copy/reuse the native Equipo Especial element as-is;
- when more capacity is required, **repeat the native element**;
- the compositor may position the repeated module where a valid layout permits, but the module itself remains native.

This corresponds to prior option **A: full native module geometry fixed and repeatable**.

**Audit consequence:** current generic two-column Equipo Especial continuation is **REPLACE**. Clarification 9A is CLOSED.

### B4-13 — Resources / “Options” terminology — PENDING NOMENCLATURE CLARIFICATION

The owner does not recognize the abbreviated label “Opciones”.

Before asking for a product decision, identify the actual domain/UI name represented by `CharacterClassOption` / the `OPCIONES` section and explain it using concrete examples from the supported kinds. Do not assume owner intent until terminology is clear.


### B4-13 terminology clarification — “Options” means class-choice modules

Repository inspection shows that the PDF label `OPCIONES` is an umbrella over `CharacterClassOption` records. In the current app these are surfaced through concrete modules such as:

- **Técnicas** — e.g. manoeuvres, shots, runes, flourishes and similar chosen techniques;
- **Metamagia** — known Metamagic options;
- **Pactos** — pact choices and **Invocaciones**.

The underlying model also supports kinds such as Artificer plans/devices, subclass-state records and a generic OTHER kind.

Therefore “Opciones” is not a good owner-facing shorthand for the audit. Future review should call these **Técnicas / Metamagia / Pactos / Invocaciones / otras elecciones de clase**, or “elecciones de clase” as the umbrella term.

B4-13 remains pending owner review under this clarified terminology.


## Batch 5 — clarification 13 + Equipo Especial/Notes 19–22

### Clarification B4-13 — what “Options” means

The PDF heading `OPCIONES` is an umbrella over **class-choice records** (`CharacterClassOption`), not one familiar single sheet concept.

In the app these appear through concrete modules such as:
- **Técnicas** — manoeuvres, shots, runes, flourishes and analogous techniques;
- **Metamagia**;
- **Pactos / elecciones de Pacto**;
- **Invocaciones**.

The underlying model can also represent artificer plans/devices, subclass-state records and other class-choice records.

For owner-facing review, use **elecciones de clase** as the umbrella term, and concrete names such as Técnicas / Metamagia / Pactos / Invocaciones when possible.

**Restated B4-13 question:** when one class-choice stream is exhausted but Resources remain, is the local reclaim rule correct, while both Resources and class-choice modules ultimately participate in the global Extended-page composer instead of automatically owning a dedicated page?

### B5-19 — M50800-19 Equipo Especial custom locations

**Original defect:** custom locations such as `Espalda` or `Bolsa lateral` were painted over fixed canonical location labels.

**Repair state:** the final generic Inventory flow avoids direct overprint by flattening location + item name into text, but that solution is superseded because the owner rejected the generic continuation and requires reuse of the native Equipo Especial module.

**Owner clarification already supplied:** the native Equipo Especial module already contains space for non-common/custom locations.

**Provisional classification:** **REPLACE old repair / requirement essentially clarified**:
- canonical location rows keep their printed native labels;
- non-common locations use the native module's dedicated non-common/custom-location capacity;
- never overprint a canonical label;
- if more capacity is needed, repeat the full native module.

**Owner question:** confirm that this is the exact rule for custom locations.

### B5-20 — M50800-20 redundant Equipment scaffolds

**Original defect:** later Inventory pages repeated empty Equipment / Equipo Especial structures after their useful content was exhausted.

**Repair attempted:** generic record-flow packing stopped emitting some empty fixed scaffolds and packed surviving records into remaining columns.

**Assessment under current owner architecture:** the local “do not render exhausted content” principle is correct, but the generic flow visual design is rejected. The same principle must move into the global page composer:
- absent/exhausted modules consume no page slot;
- surviving modules may use a different valid layout;
- no page exists solely to preserve an empty Equipment/Equipo Especial template.

**Provisional classification:** **KEEP principle / REPLACE implementation**.

**Owner question:** no new product decision appears necessary; confirm that an exhausted native module should simply disappear from subsequent Extended-page layout selection rather than remain as an empty placeholder.

### B5-21 — M50800-21 Notes design

**Original defect:** a separate generic Extended Notes design was invented even though the sheet already has a native Notes page/module.

**Owner clarification already supplied:** Notes is a special case: overflow Notes should use/copy the **entire native Notes page**, not share a page with unrelated modules.

**Repair state at `ce695c...`:**
- continuation Notes does **not** copy the native Notes page;
- it programmatically creates a new `NOTAS · CONTINUACIÓN` page with two gray-banded columns.

**Actual Mara proof:** final page 16 visibly shows this reconstructed two-column gray table.

**Provisional classification:** **REPLACE**. The flow/pagination logic may be reusable, but the visual page must be the native Notes page copied/reused as a whole.

**Owner question:** no new design decision should be needed; confirm that every additional Notes page uses the full native Notes page geometry/visual design as-is, with only the content/layer overlays changing.

### B5-22 — M50800-22 Notes record boundaries

**Original defect:** `Nota 1`, `Nota 2`, etc. ran directly into one another and were difficult to scan.

**Repair attempted:** `noteFlowLines` now treats each paragraph/note as a record and inserts a blank ruled row between records where possible. It also avoids starting a note in the last few lines of a column when the whole note can fit in the next column.

**Actual Mara proof:** page 16 shows visible blank-row separation between `Nota 6`, `Nota 7`, `Nota 8`, `Nota 9` and subsequent narrative records. However the `Nota N — título` line uses essentially the same body rendering; there is no strong explicit heading emphasis.

**Provisional classification:** **PARTIAL / MODIFY**:
- KEEP record-aware separation;
- add explicit native-compatible emphasis to the note identity/header.

**Owner question:** should the first line/header of each note be visibly emphasized (for example bold `Nota N — Título`) while the following wrapped body lines remain normal, plus at least one native ruled-row of separation between note records when space permits?

## Batch 5 evidence

Direct final-repair proof inspected:
- Mara Custom-v2 Attribute page 16.

Code evidence:
- `noteFlowLines` provides record-aware blank-row separation and continuation labels;
- `renderNotesContinuationPage` reconstructs rather than copies the native Notes page.

No renderer/product code changes are made in Phase 2.


## Owner responses — Batch 5 (13, 19–22)

### B4-13 — Resources + class-choice streams — CONFIRMED WITH GLOBAL RECLAIM

The owner confirms:

- if the class-choice area (e.g. Técnicas / Metamagia / Pactos / Invocaciones) is exhausted, it should stop reserving space;
- if there are enough remaining Resources, Resources may expand to use the whole page;
- if Resources do not need the entire reclaimed area, another compatible Extended module may occupy that space instead, e.g. Inventory/Equipment, Traits or another active role;
- therefore reclaim is **global**, not merely “Resources gets the former Options half”.

**Audit consequence:** local exhausted-stream detection is KEEP-worthy, but the final behavior belongs to the cross-role page composer. The composer chooses a valid layout based on all remaining active modules/content volume.

### B5-19 — Equipo Especial custom locations — CONFIRMED WITH SOURCE-FAITHFUL FALLBACK

The owner clarifies a two-tier rule:

1. **Preferred:** use the native Equipo Especial module's existing capacity for custom/non-common locations.
2. **Fallback when that capacity is insufficient/inapplicable:** construct an equivalent location-capable variant **from pieces of the native module**, leaving the location-name field/label blank so the custom location can be written there.

Fallback constraints are strict:
- use native/source components as building blocks;
- use the corresponding native font;
- use the corresponding native font size;
- preserve native spacing, strokes, fills, row rhythm and visual grammar;
- never overprint a custom location on top of an existing canonical label.

This fallback is not permission to invent a generic new Equipo Especial design.

**Audit consequence:** current generic text-flow continuation remains REPLACE. Future implementation may use either the exact native module or a source-composed blank-location variant that is visually indistinguishable from the family.

### B5-20 — exhausted Equipment scaffolds — CONFIRMED VIA B4-13

The owner explicitly refers B5-20 to the same rule as B4-13:

- an exhausted module stops reserving space;
- surviving modules may expand into the reclaimed area if their native geometry permits;
- otherwise another compatible active Extended module may occupy that space;
- no empty placeholder exists merely to preserve an earlier template arrangement.

**Audit consequence:** KEEP exhausted-stream detection principle; REPLACE fixed-role page/scaffold architecture with global constraint-aware layout selection.

### B5-21 — Notes design — CONFIRMED

Every additional Notes page uses the complete native Notes page design/geometry as the visual authority.

Only semantic/content layers change.

Notes is not to be recomposed as a generic shared Extended page with unrelated modules.

**Audit consequence:** current reconstructed two-column Notes page is REPLACE; full native Notes-page reuse is required.

### B5-22 — Notes record boundaries — CONFIRMED

The owner confirms:
- clearly emphasize each note identity/header, e.g. `Nota N — Título`, using native-compatible typography such as bold;
- preserve visible separation between notes, ideally at least one native ruled-row/line when space permits;
- the body remains visually subordinate to the note identity.

**Audit consequence:** KEEP record-aware separation primitive; MODIFY rendering so note headers are clearly distinguishable within the native Notes-page grammar.


## Batch 6 — Notes continuation, name ribbon, semantic ellipsis, adaptive architecture (23–27)

### B6-23 — M50800-23 Notes continuation identity

**Original defect:** an overflow Notes page/column could begin with the tail of a note without identifying which note was continuing.

**Repair state at `ce695c...`:**
- `noteFlowLines` keeps a semantic label derived from the note paragraph;
- when a note must continue after a physical segment boundary, it can emit `<label> (continuación)` before the remaining lines;
- this is a useful record-identity primitive.

**Limitations:**
- Mara final proof does not provide a sufficiently clear multi-page Notes stress case to prove the full cross-page behavior visually;
- the current rendering is on the rejected reconstructed Notes page rather than the native Notes page;
- the owner's later bidirectional continuity contract for Historia raises a scope question: whether Notes should also carry explicit forward/back navigation, not only a destination-side `(continuación)` label.

**Provisional classification:** **KEEP continuation-identity primitive / MODIFY visual placement; pending owner scope clarification**.

**Owner question:** for Notes specifically, is `Nota N (continuación)` at the destination sufficient, or should a split note also use the full bidirectional contract:
- source: `[continúa en NOTAS 02 / Nota N]`;
- destination: `[proviene de NOTAS 01 / Nota N]`?
This also helps close earlier clarification 11A about whether the bidirectional contract is general or narrative-only.

### B6-24 — M50800-24 Notes packing

**Original defect:** Notes continuation used only part of available writing space and created another page while native Notes rows/columns remained unused.

**Repair state:**
- current flow uses both continuation columns before adding a page;
- it is record-aware: if a complete note will not fit in the remaining tail of the current column but will fit in a fresh column, it advances the whole note rather than splitting it;
- only records larger than a full physical segment must split.

**Owner rule now established:** additional Notes pages must be complete copies of the native Notes page.

**Provisional classification:** **KEEP packing concept / transplant to native Notes geometry**.

**Owner question:** confirm the packing priority:
1. preserve a whole note in the next native Notes column if it fits there, even if that leaves some unused lines at the end of the previous column;
2. split a note only when it is too large to fit as a whole in a fresh native column;
3. when split, preserve note identity/continuity markers;
4. consume all usable native Notes columns before creating another full Notes page.

### B6-25 — M50800-25 character-name ribbon

**Original defect:** the character name was shifted/misaligned inside the portrait ribbon.

**Acceptance rule:** center horizontally and vertically inside the existing ribbon; if one line does not fit cleanly, use two centered lines.

**Repair evidence:**
- final Mara Custom-v1 and Custom-v2 proofs show the full one-line name `Mara de los Siete Umbrales` in the ribbon without truncation;
- Custom-v1 implementation still calls a one-line `centered(... PORTRAIT_NAME_RECT ...)` helper; there is no two-line fallback in that path;
- therefore the Mara fixture looks acceptable for its current length, but the general long-name rule is not fully implemented/proven.

**Provisional classification:** **PARTIAL / MODIFY**.

**Owner question:** confirm the desired long-name strategy:
- start at native/preferred type size;
- allow only reasonable uniform reduction if necessary;
- if one line still does not fit cleanly, wrap to two centered lines;
- center the resulting one- or two-line block both horizontally and vertically in the native ribbon;
- never ellipsize the character name.

### B6-26 — M50800-26 semantic ellipsis

**Original defect:** Fantasy silently truncated meaningful semantic identities with `...` in names across metadata, attacks, traits, resources/class choices and equipment.

**Repair attempted:** semantic-identity tests were strengthened (e.g. commit `3ba83b49...`) to assert that complete identities exist somewhere in the generated PDF.

**Critical limitation:** those tests prove data preservation somewhere, not correct rendering at the location where the identity is shown.

**Direct final-proof evidence:** `mara-fantasy-stress-baseline.pdf` still contains many visible/extracted ellipses, including examples such as:
- `Mara de los Siete...`;
- `Cronomante del Umbr...`;
- `Cartógrafa de Parad...`;
- attack/effect names ending in `...`;
- trait names ending in `...`;
- equipment identities ending in `...`.

The Fantasy renderer still contains `classicSingleLineExcerpt(...)`, which explicitly returns a shortened string plus `...`.

**Provisional classification:** **NOT RESOLVED / REPLACE truncation behavior**.

**Owner question:** confirm that meaningful identity fields must never use semantic ellipsis even on constrained base pages: use bounded reasonable compression and/or wrap/grow the logical row/module; if the native base surface truly cannot hold the complete semantic record, route the excess through an explicit continuation mechanism rather than silently shortening the name.

### B6-27 — M50800-27 adaptive continuation architecture

**Original defect:** fixed per-role/multi-panel scaffolds remained allocated after a stream ended, producing mostly-empty pages.

**Repair state:** many useful local adaptive primitives were added:
- Traits can reclaim its own continuation area;
- Resources can drop exhausted class-choice space;
- Inventory record packing avoids some empty local scaffolds;
- Notes can use both local columns;
- cross-family tests constrain excessive page growth.

**Owner clarification during Phase 2 is broader:** the final architecture must be a **global, constraint-aware Extended-page composer**:
- inspect every remaining active content stream;
- respect each native module's geometry/flexibility constraints;
- choose among multiple valid family-native page layouts;
- absent/exhausted modules reserve no space;
- compatible surviving modules share/reclaim page area;
- Notes remains a whole-page native exception;
- fixed modules such as Equipo Especial are repeated rather than arbitrarily resized;
- semantic record boundaries and explicit continuity are preserved.

**Provisional classification:** **PARTIAL / ARCHITECTURAL MODIFY-REPLACE**:
KEEP local packing/reclaim primitives that remain valid;
REPLACE fixed role-page ownership with global layout selection.

**Owner question:** confirm that this global composer is the umbrella rule for all Extended continuation work, and that page count is only an outcome/evidence signal - not a target to optimize at the expense of native grammar, handwriting space, readability, or semantic continuity.

## Batch 6 evidence

Directly inspected:
- final repair Mara Custom-v1 page 1;
- final repair Mara Custom-v2 Attribute page 1;
- extracted final Fantasy proof text for literal `...` occurrences.

Code inspected:
- Custom-v1 ribbon remains a one-line centered call;
- Fantasy `classicSingleLineExcerpt` explicitly creates ellipsis;
- Notes record-aware continuation/packing logic;
- semantic-identity test strengthening in `3ba83b49...`.

No renderer/product code changes are made in Phase 2.


## Owner responses — Batch 6 partial (23–26)

### B6-23 — Notes continuation identity — BIDIRECTIONAL CONTRACT CONFIRMED

The owner confirms that Notes must use the same explicit bidirectional continuity-navigation pattern already defined for Historia.

Therefore a split Note must identify both:
- where the source continues to; and
- where the destination came from.

The previously supplied owner pattern is authoritative in form and intent.

Example adaptation for Notes:

Source segment ends with a visible marker such as:
`...[continua en sección extendida NOTAS 02 / Nota 8]...`

Destination segment begins with a visible marker such as:
`...[proviene de sección extendida NOTAS 01 / Nota 8]...`

The precise section label/sequence must identify the actual source and target unambiguously.

**Audit consequence:** destination-only `Nota N (continuación)` is insufficient by itself. The future flow model needs source/target-aware bidirectional continuation metadata.

This also resolves earlier clarification 11A in the direction of a **general continuity principle for semantic content that is split across non-contiguous sections/pages**, not Historia-only.

### B6-24 — Notes packing — CONFIRMED

The owner confirms the packing priority:

1. if the whole next Note fits in a fresh native Notes column, move the whole Note there rather than split it merely to consume the tail of the current column;
2. split only when the Note itself is too large to fit within a fresh native column;
3. when split, preserve bidirectional continuation navigation and note identity;
4. consume all usable native Notes columns before allocating another full native Notes page.

**Audit consequence:** current record-aware packing concept is KEEP-worthy, but must be transplanted onto the full native Notes-page geometry and upgraded to bidirectional continuity markers.

### B6-25 — character-name ribbon — CONFIRMED

The owner confirms the fallback order:

1. render at native/preferred size;
2. allow reasonable uniform reduction if needed;
3. if one line still does not fit cleanly, wrap to two centered lines;
4. center the resulting one- or two-line text block horizontally and vertically within the native ribbon;
5. never use semantic ellipsis for the character name.

**Audit consequence:** current one-line-only Custom-v1 path is PARTIAL / MODIFY.

### B6-26 — semantic ellipsis — CONFIRMED AS PRODUCT RULE

The owner confirms:
- meaningful semantic names/identities must never be silently truncated with `...`;
- first use reasonable uniform compression;
- then wrap/grow the logical row/module as needed;
- if a constrained native base surface genuinely cannot contain the complete semantic record, use explicit continuation rather than ellipsis.

The owner asks whether this is already the exact Custom-v1/v2 rule. Repository verification is required before answering; do not conflate intended family grammar with current implementation behavior.

### B6-27 — adaptive continuation architecture — OWNER REQUESTS LAYMAN/WIREFRAME REPHRASE

The prior wording was too architectural.

Re-present B6-27 using concrete page-composition examples/wireframes and plain language. Do not treat it as resolved.


### B6-26 verification — Custom-v1/v2 family rule vs current implementation

Repository inspection confirms the owner's recollection at the **design-rule level**:

- Custom-v1/v2 identity rendering generally favors full semantic text, bounded size reduction, width-aware wrapping, or explicit overflow routing rather than a `...` helper;
- unlike Fantasy, these renderers do not center their identity strategy around a `classicSingleLineExcerpt(...)` function that deliberately appends ellipsis.

However, this must not be overstated as “fully implemented everywhere”:
- several base-page paths still use bounded `.take(...)` over wrapped lines because the native base module has finite capacity;
- correctness therefore depends on the remaining semantic content being routed explicitly to continuation surfaces;
- Phase 3 must audit each meaningful identity path to ensure no data is silently dropped even when no literal ellipsis is printed.

**Conclusion:** the owner's remembered rule is correct for Custom-v1/v2 family grammar; the current implementation is not yet proven uniformly compliant end-to-end.

### B6-27 layman rephrase staged

Plain-language question to owner:

When content overflows, should the renderer think like a person arranging native sheet blocks on blank continuation pages?

Example state:
- Rasgos still has content;
- Equipo still has content;
- Recursos still has a little content;
- Historia is finished;
- Notes is a special full-page case.

Bad/fixed approach:
```
PAGE A = always Rasgos template
PAGE B = always Recursos template
PAGE C = always Equipo template
```
This can leave large blank areas.

Proposed approach:
```
What still needs space?
  Rasgos = much
  Equipo = medium
  Recursos = little

Try a valid native-layout arrangement:
+-------------------------------+
| RASGOS        | EQUIPO        |
| ...           | ...           |
| ...           | ...           |
+---------------+---------------+
| RECURSOS                      |
| ...                           |
+-------------------------------+
```

If that arrangement is invalid because module geometry does not allow it, choose another valid layout. If a module is finished, it disappears. If Notes overflows, it gets its own full native Notes page. If Equipo Especial is needed, use/repeat its fixed native module.

The goal is not “fewest pages at any cost”; the goal is “use the available page intelligently while keeping every module looking/behaving like the native sheet”.

**B6-27 remains PENDING OWNER.**


### B6-27 — adaptive continuation architecture — CONFIRMED

The owner confirms the layman rephrasing exactly.

The renderer should behave like an **automatic page layout compositor**:

1. inspect what semantic content still remains to be rendered;
2. select the corresponding native/family-matched modules;
3. choose among multiple valid page layouts;
4. combine compatible active modules on the same page when their native geometry/rules permit;
5. stop reserving space for modules whose content is exhausted;
6. allocate another page whenever remaining content cannot fit without distorting native design, handwriting space, readability or semantic continuity.

This is **not** free-form geometric packing:
- native module geometry and family grammar remain authoritative;
- fixed modules stay fixed/repeatable;
- flexible modules may reclaim compatible space only within valid layouts;
- Notes retains its full-native-page exception;
- page-count reduction is an outcome, never the optimization target.

**Audit consequence:** M50800-27 is conceptually CLOSED for owner intent. Phase 3 must implement the global constraint-aware compositor while preserving the valid local packing primitives identified during Phase 2.


## Batch 7 — acceptance/process rules (28–32)

### B7-28 — M50800-28 page-count interpretation

**Matrix rule:** there is no fixed product minimum/maximum page count. A high page count is evidence of a packing problem only when pages waste usable native space; a low count is not proof of correctness if achieved by compression, deformation, data loss or loss of handwriting usability.

**Evidence:** the final adaptive proof reported 29 / 18 / 16 / 15 pages and its historical closure called them fixture regression ceilings. Phase 2 has since established that several of those layouts still violate owner design rules despite the lower counts.

**Audit consequence:** numeric ceilings must not be used as product acceptance. Page count remains diagnostic evidence only.

**Owner question:** confirm that 29 / 18 / 16 / 15 must not be preserved as target numbers; the next candidate may legitimately have more or fewer pages if the visual/native/semantic rules are satisfied.

### B7-29 — M50800-29 data preservation

**Matrix rule:** all required semantic identities must survive layout repair. Readability cannot be purchased by silently dropping records.

**Existing repair evidence:** `ce695c...` strengthened the Mara cross-family fixture so generated PDFs contain full semantic identities for:
- traits;
- resources;
- class-choice records;
- inventory items;
- note-card titles;
- custom attributes;
- custom markers.

This is useful regression protection, but text-layer presence somewhere is not sufficient visual acceptance.

**Phase-3 requirement:** preserve both:
1. **semantic completeness** — every required record is present;
2. **correct association/presentation** — the identity appears in the correct native module/continuation and is not merely duplicated elsewhere to satisfy a text scan.

**Owner question:** confirm that this two-part rule is correct: no data loss, and “present somewhere in the PDF” is not enough if the data is presented in the wrong module or detached from its semantic record.

### B7-30 — M50800-30 pre-fix comprehension gate

**Matrix requirement:** no renderer code changes until the actual failing artifacts/owner observations are understood, native references are identified and each defect is mapped to evidence/intended behavior.

**Current Phase-2 status:** this gate is being executed now:
- Phase 1 resolved exact 50800 provenance;
- Phase 2 has reviewed the later repair item-by-item against owner QA;
- owner clarifications are being persisted before implementation;
- no renderer/product code has been changed on the Phase-2 audit branch.

**Audit consequence:** the gate is not a design question; it becomes satisfied only when this audit closes with a complete owner-observation → existing code/evidence → KEEP/MODIFY/REPLACE/MISSING → Phase-3 action mapping.

**Owner question:** confirm the process rule that Phase 3 implementation starts only after this Phase-2 mapping is complete enough that no known owner QA item is left without a planned disposition.

### B7-31 — M50800-31 exact-candidate acceptance gate

**Matrix requirement:** CI/tests/synthetic proofs/page counts cannot authorize owner handoff by themselves.

Before owner handoff of the next candidate:
- build must have a unique owner-facing version/build identity greater/different from 50800;
- generate the real Mara PDFs from that exact candidate;
- inspect the actual candidate PDFs, not a previous proof artifact;
- walk the acceptance matrix and mark each item FIXED / OPEN / CHANGED-NEW;
- any material blocking OPEN or new regression blocks handoff;
- record source commit → build/run → APK hash/artifact → exact proof hashes so provenance cannot repeat the 50800 mistake.

**Owner question:** confirm this as the handoff gate.

### B7-32 — M50800-32 all-four-family generation

**Historical evidence:** `ce695c...` successfully generated all four internal Mara proof families:
- Fantasy;
- Custom v1;
- Custom v2 · Atributo;
- Custom v2 · Habilidad.

That proves the old repair head could generate all four internally, but does not waive the requirement for the next candidate.

**Next-candidate rule:** before owner handoff, the exact candidate must generate real Mara output successfully in all four families, and those exact four outputs must be part of candidate inspection.

A failure in any one family blocks handoff even if the other three look correct.

**Owner question:** confirm that all four families are mandatory smoke/visual evidence before the next owner QA handoff.

## Batch 7 status

No renderer/product code changes are made. These are acceptance/process rules and remain pending owner confirmation where a question is listed.


## Owner responses — Batch 7 (28–32)

### B7-28 — page-count interpretation — CONFIRMED WITH PRACTICAL EXPECTATION

The owner confirms that the historical `29 / 18 / 16 / 15` counts are **not product targets** and must not be preserved mechanically.

Page count is an outcome of:
- native visual grammar;
- readable text;
- handwriting usability;
- semantic continuity;
- reasonable packing efficiency.

A candidate may legitimately have more or fewer pages than the historical proof counts.

Additional owner expectation:
- a **normal/standard character sheet should not ordinarily require an excessive number of pages**;
- however, the Mara QA fixture is intentionally extreme/high-volume, so a larger page count may be an artifact of the stress dataset rather than a product defect by itself.

**Audit consequence:** use page count as a diagnostic signal:
- unusually large counts on ordinary characters may indicate a packing regression;
- stress-fixture counts must be interpreted in context;
- no numeric ceiling substitutes for visual/layout acceptance.

### B7-29 — semantic data preservation — CONFIRMED

The owner confirms the two-part rule:

1. preserve every required semantic record;
2. preserve correct semantic placement/association.

“Present somewhere in the PDF” is insufficient.

Examples explicitly consistent with owner intent:
- a Trait must not be routed into Notes merely to preserve its text;
- an Attack must not appear in Equipment;
- each record belongs in its correct native/extended semantic module.

**Audit consequence:** Phase-3 regressions must test both semantic completeness and module/record association, not only whole-document text presence.

### B7-30 — pre-fix comprehension gate — CONFIRMED / THIS PHASE IS THE GATE

The owner confirms that the current Phase-2 exercise is exactly the intended comprehension gate.

Current purpose:
- reconstruct every known owner QA observation;
- understand what it means visually/semantically;
- map it to existing code/evidence;
- classify existing repair work as KEEP / MODIFY / REPLACE / MISSING;
- record the expected future behavior;
- only then enter Phase 3 implementation.

The owner explicitly confirms this is what the current audit should accomplish.

**Audit consequence:** B7-30 is conceptually satisfied only at Phase-2 closure, once no known owner observation lacks a planned disposition. No renderer/product code changes are authorized before that closure.

### B7-31 — exact candidate/version/build acceptance identity — CONFIRMED

The owner strongly confirms the exact-candidate provenance gate.

Reason:
- the owner needs to know exactly which APK is being tested;
- version and build identity must uniquely identify the candidate;
- the delivered APK must have an explicit, unique owner-facing filename — effectively the APK's “nombre y apellido”.

Before owner handoff, record and preserve the exact chain:

- source commit;
- unique app version;
- unique build number, never reusing 50800;
- workflow/build run;
- APK artifact identity;
- APK SHA-256;
- exact Mara proof PDFs generated from that same candidate;
- proof hashes;
- completed acceptance matrix against those exact outputs.

No materially different APKs may share the same owner-facing version/build identity.

**Audit consequence:** provenance is a hard acceptance gate, not bookkeeping.

### B7-32 — all-four-family generation — CONFIRMED

Before the next owner QA handoff, the exact candidate must successfully generate and pass candidate inspection for all four Mara families:

- Fantasy;
- Custom v1;
- Custom v2 · Atributo;
- Custom v2 · Habilidad.

A material failure in any one family blocks handoff even if the other three succeed.

Generation success alone is insufficient; the exact outputs also participate in the candidate visual/semantic acceptance review.

## Batch 7 disposition

Owner intent for M50800-28 through M50800-32 is now clear.

No renderer/product code changes have been made during Phase 2.


## Phase-2 pending-owner reconciliation after Batch 7

A scan of historical `PENDING OWNER` / clarification markers shows that most are stale narrative from earlier batches and have been superseded by later owner decisions in this same checkpoint.

Resolved later in the checkpoint and **not to be re-asked**:
- Clarification Point 2 — native attribute ornament/layering;
- B2-08 ordering — player/stored order authority, no synthetic feature-priority;
- B2-09 adaptive page-layout architecture — resolved through the global automatic compositor rule;
- B4-13 terminology and reclaim behavior — resolved as class-choice modules + global reclaim;
- Clarification 9A — Equipo Especial fixed native module, repeat as needed;
- Clarification 11A — bidirectional continuity applies as a general semantic continuity principle;
- B6-27 — global automatic native-module page compositor confirmed;
- all Batch-7 acceptance/process questions 28–32 confirmed.

### Only substantive owner clarification still open: 8A — writable tracker state

Already confirmed:
- trait-use/ammunition controls must remain handwriting-editable;
- do not pre-fill/consume the writable marks in a way that prevents normal manual use.

Still unresolved:
if persistent character state says some uses/ammunition are already spent at export time, should the printable PDF:

**A. Paper-sheet authority:** ignore the runtime spent/current state for the writable tracker and print only blank capacity, e.g. `____ / 3` or empty marks; or

**B. Preserve runtime snapshot separately:** keep the handwriting tracker blank/editable but also print the current/spent runtime state elsewhere as non-interfering text.

No implementation assumption is authorized until the owner resolves 8A.


## Owner clarification 8A — writable tracker runtime state — RESOLVED

The owner chooses **B**, but with an important compact-presentation constraint.

Required behavior:

- the tracker remains writable/editable by hand;
- the exported runtime/current availability may be preserved;
- do **not** add a verbose secondary status line such as `Estado al exportar: 2 disponibles`;
- instead, integrate the runtime value very compactly into the writable control.

Owner example:

`____(2)/3`

Interpretation:
- `3` = maximum capacity;
- `2` = current available value at export time;
- the blank/writable portion remains usable for manual table play;
- the runtime snapshot is informational and visually subordinate.

Equivalent native-compatible compact forms may be used for ammunition/uses if they preserve the same semantics and handwriting usability.

**Audit consequence:** current pre-filled square/counter implementations that consume the writable marks are **MODIFY**. Phase 3 must provide a compact current/max annotation without sacrificing the blank handwritten control.

## Phase-2 owner clarification status

All owner clarification questions raised through M50800-32 are now resolved.

No known owner QA item remains pending solely for lack of owner intent.

The remaining Phase-2 work is technical consolidation only:
- burn down the acceptance matrix;
- reconcile stale provisional text against later owner decisions;
- produce final KEEP / MODIFY / REPLACE / MISSING classifications;
- map each item to exact Phase-3 action/evidence;
- close the comprehension gate before renderer implementation.


# Phase 2 final burn-down — authoritative disposition for M50800-01…32

> **Authority note:** This section supersedes every earlier provisional `PENDING OWNER`, provisional KEEP/MODIFY/REPLACE label, and interim owner question in this checkpoint. Earlier sections remain as audit history/evidence only.

All owner-intent questions raised during Phase 2 are resolved. No renderer/product code was changed in Phase 2.

Legend:
- **KEEP** — existing repair work is valid and should survive.
- **MODIFY** — core idea is useful but implementation must change.
- **REPLACE** — current presentation/architecture conflicts with the clarified owner contract.
- **MISSING** — required behavior is not sufficiently implemented.
- Mixed dispositions identify reusable primitives explicitly.

| ID | Final disposition of existing repair | Phase-3 action / acceptance intent |
| --- | --- | --- |
| **M50800-01** | **FIXED / KEEP provenance finding** | Preserve Phase-1 finding: owner-tested 50800 = `15f86ec...` APK SHA `ff367e...`. Every future owner candidate gets unique versionName/versionCode/build identity and explicit APK filename/hash chain. |
| **M50800-02** | **KEEP + MODIFY** | Keep the Custom-v1 multi-line skill routing that fixes `Lectura de presagios`; change typography so all wrapped lines of one semantic label use one consistent readable scale. Real Mara Custom-v1 must generate from the exact candidate. |
| **M50800-03** | **KEEP, strengthen regression** | Keep isolated source-object technique that prevents stale hidden template text. Source objects may be repositioned but must never cover important content. Scan/render all relevant Extended roles for stale underlay text. |
| **M50800-04** | **KEEP source assets / REPLACE surrounding presentation** | Custom Statistics must start from exact native page-1 attribute score/modifier ornament as layered source truth. No generic replacement boxes. If reconstruction is unavoidable, it must be visually almost indistinguishable from source grammar. |
| **M50800-05** | **MODIFY / REPLACE capacity rule** | Remove arbitrary 3-attribute limit. Native grammar supports up to six attributes per page at native scale; Mara's four custom attributes belong together when space allows. |
| **M50800-06** | **KEEP / resolved defect** | No phantom skills. Empty attribute visual capacity is non-blocking if it does not create fake semantic records; reclaim unused area where reasonably possible. |
| **M50800-07** | **KEEP compact key logic + MODIFY title logic** | Attribute title uses integrated native-style form, e.g. `ETEr`; compact references use `ETE`. Per-Attribute skills under explicit parent do not need `(ETE)`; detached/per-Ability/overflow skills use `(FUE)`, `(SAB)`, `(ETE)`, etc. |
| **M50800-08** | **KEEP useful native-like primitives / REPLACE fixed role-page design** | Traits/Rasgos must use family-native grammar. Treat it as a composable native module rather than a generic six-zone page or dedicated role page. |
| **M50800-09** | **MODIFY ordering logic** | Remove synthetic `featurePriority` promotion. Category grouping is allowed/expected; within a category preserve player/stored order. |
| **M50800-10** | **KEEP record-aware packing + MODIFY page ownership** | Preserve trait record boundaries and local column reclaim. Traits becomes a module available to the global compositor instead of automatically owning continuation pages. |
| **M50800-11** | **REPLACE generic narrative routing / MISSING continuity metadata** | Trasfondo/Historia retains its own native module identity. Add explicit bidirectional `continúa en...` / `proviene de...` navigation across split semantic sections, with numbered Extended destinations/sources. |
| **M50800-12** | **KEEP table/header separation + MODIFY row model** | Custom-v2 Combat/Actions stays tabular; one action = one logical row whose height is driven by tallest wrapped cell. Use generous/full-page width for action-heavy Extended layouts; avoid confusing fixed 26-pt band fragments. |
| **M50800-13** | **REPLACE presentation** | Fantasy Combat/Actions overflow must extend the base table grammar; discard prose/reference flattening. |
| **M50800-14** | **KEEP exhausted-stream detection + MODIFY architecture** | Resources and class-choice modules (Técnicas/Metamagia/Pactos/Invocaciones/etc.) release unused space globally. Remaining Resources may expand or another compatible active module may occupy the reclaimed area. |
| **M50800-15** | **REPLACE presentation** | Discard generic two-column Inventory continuation. Reuse native ordinary `EQUIPO` element and its existing geometry as baseline; compose/repeat valid native modules/rows instead of inventing arbitrary dimensions. |
| **M50800-16** | **MODIFY content formatter** | Ordinary Equipment visible row = quantity + full identity only. Remove weight, `Consumible`, `Equipado`, operational-state prose and long descriptions. |
| **M50800-17** | **KEEP atomic record packing + MODIFY rendering** | Preserve item boundaries. Use native Equipment geometry; bounded uniform compression, then wrap. Indent second/subsequent wrapped lines (or equivalent native cue) so continuation is visibly tied to the previous line. |
| **M50800-18** | **REPLACE presentation** | Equipo Especial uses the complete native module at fixed native geometry. If more capacity is needed, repeat the native module; do not invent a taller/reshaped variant. |
| **M50800-19** | **REPLACE old workaround / source-faithful fallback permitted** | Prefer the native module's custom-location capacity. If insufficient, build a blank-location variant from native pieces using matching source font, size, strokes, fills, spacing and row rhythm. Never overprint a canonical location label. |
| **M50800-20** | **KEEP exhausted-module principle / REPLACE fixed scaffold implementation** | Once Equipment/Equipo Especial (or any module) is exhausted, it disappears from subsequent layout selection; surviving compatible modules reclaim/use the area. |
| **M50800-21** | **REPLACE presentation** | Every additional Notes page is the complete native Notes page design/geometry. Notes is a whole-page exception and does not share its overflow page with unrelated modules. |
| **M50800-22** | **KEEP record boundaries + MODIFY heading treatment** | Preserve clear spacing between notes; emphasize `Nota N — Título` with native-compatible heading treatment, body subordinate. |
| **M50800-23** | **KEEP identity primitive + MODIFY to bidirectional navigation** | Split Notes use explicit forward/back continuity markers, not destination-only `(continuación)`. This is part of the general semantic-continuity contract. |
| **M50800-24** | **KEEP packing concept, transplant to native Notes geometry** | Move a whole Note to a fresh native column when it fits; split only if too large for a full fresh column; consume all native Notes columns before another full Notes page. |
| **M50800-25** | **MODIFY** | Name ribbon: preferred native size → reasonable uniform reduction → two centered lines if needed. Center block horizontally and vertically. Never ellipsize the character name. |
| **M50800-26** | **REPLACE Fantasy ellipsis behavior; audit Custom-v1/v2 routing** | Meaningful semantic identities never become `...`. Use reasonable uniform compression, then wrap/grow; if base module cannot hold full semantic record, route with explicit continuation. Verify Custom-v1/v2 do not silently drop wrapped overflow even when no ellipsis is printed. |
| **M50800-27** | **KEEP valid local packing primitives / REPLACE architecture** | Implement global constraint-aware **automatic page compositor**: inspect remaining content, choose native modules and valid page layouts, combine compatible active modules, remove exhausted ones, and add a page rather than deforming design. Page count is not the objective. |
| **M50800-28** | **PROCESS RULE CONFIRMED** | Do not preserve historical `29/18/16/15` as targets. Page count is diagnostic only. Excessive pages on ordinary characters can indicate a regression; Mara is a stress fixture and may legitimately be large. |
| **M50800-29** | **KEEP semantic-presence regression + EXTEND association checks** | Preserve every required record **and** verify it appears in the correct semantic module/record. Whole-document text presence alone is insufficient. |
| **M50800-30** | **PHASE-2 GATE SATISFIED BY THIS AUDIT ON CLOSURE** | Phase 3 may start only after this final mapping is committed as the authoritative comprehension plan. No known owner observation may enter implementation without disposition/action. |
| **M50800-31** | **FUTURE HARD GATE** | Exact next candidate: unique version/build + commit/run/artifact/APK SHA + exact four proof hashes; inspect actual candidate PDFs and mark matrix FIXED / OPEN / CHANGED-NEW. Any material blocker stops owner handoff. |
| **M50800-32** | **KEEP four-family harness + repeat on exact candidate** | Before owner handoff, real Mara must generate and be inspected in Fantasy, Custom v1, Custom v2 Atributo and Custom v2 Habilidad. Any material family failure blocks handoff. |

## Cross-cutting owner rules that Phase 3 must treat as normative

### Native-first construction
- Copy/reuse an existing approved source/native component whenever it represents the same semantic family.
- Source coordinates are not sacred; source **geometry/visual grammar** is.
- Reposition a copied object as needed for the new layout, but never overprint important content.
- If a component truly cannot be copied, reconstruct it from source-family pieces/measurements so it is almost visually indistinguishable from the original.
- Custom v1/v2 bicolour table-row physical reference is **7 mm** (about 19.84 pt), chosen for handwriting usability.

### Automatic Extended-page compositor
- Look at **all remaining content**, not one role at a time.
- Choose among valid family-native page arrangements.
- Only active modules consume space.
- Exhausted modules disappear.
- Compatible modules may share/reclaim area only if their native geometry permits it.
- Fixed native modules are repeated, not arbitrarily resized.
- Notes overflow is a full-native-page exception.
- When content cannot fit without harming design/readability/handwriting space, create another page.

### Semantic continuity
For semantic content split across non-contiguous sections/pages:
- source segment visibly identifies the target, e.g. `[continúa en sección extendida HISTORIA 02]`;
- destination visibly identifies its immediate source, e.g. `[proviene de sección extendida HISTORIA 01]`;
- numbered identities must be unambiguous;
- the markers must not overwrite meaningful content.

### Writable trackers
Trait uses/ammunition/etc. remain handwriting-editable while preserving runtime snapshot compactly.
Owner example:
`____(2)/3`
where `2` is current available at export and `3` is maximum.
Do not consume/fill the writable marks or add verbose runtime-status prose.

### Meaningful text
- No semantic ellipsis for names/identities.
- Reasonable readable compression is allowed.
- Wrapped lines belonging to one semantic label use uniform typography/scale.
- Grow/wrap/continue rather than silently truncate.

## Phase-3 implementation order implied by dependencies

This is a dependency order, **not** permission to begin implementation before Phase-2 closure is committed/merged.

1. **Shared semantic-flow contracts**
   - semantic record identity/association;
   - bidirectional continuation metadata;
   - writable tracker semantics;
   - meaningful-text no-ellipsis policy.
2. **Native-module inventory/geometry contracts**
   - source-native attribute module;
   - Traits/Rasgos;
   - Trasfondo/Historia;
   - Combat/Actions tables;
   - Equipment;
   - Equipo Especial;
   - Notes;
   - Resources/class-choice modules.
3. **Global Extended-page compositor**
   - active-stream inventory;
   - module constraints;
   - valid layout choices;
   - reclaim/exhaustion behavior;
   - no arbitrary deformation.
4. **Family renderers**
   - Custom v1;
   - Custom v2 Atributo/Habilidad;
   - Fantasy;
   - Android/Desktop parity.
5. **Regression + exact-candidate proof**
   - semantic completeness + correct association;
   - no stale underlay;
   - no semantic ellipsis/silent drop;
   - all-four-family real Mara generation;
   - actual PDF render inspection;
   - unique candidate provenance.

## Phase-2 comprehension-gate result

**PASS — OWNER INTENT MAPPED / EXISTING REPAIR BURNED DOWN / NO RENDERER CHANGES**

All known M50800-01…32 items now have:
- owner intent;
- disposition of existing repair work;
- explicit Phase-3 action/acceptance direction.

Phase 2 can close after this documentation-only audit state is published through the normal repository review path.

# Phase 2 audit — existing adaptive repair against owner QA

**Date:** 2026-09-30 (Chile local time)  
**Phase:** 2 — audit/recovery of existing repair work  
**Branch:** `audit/mara-phase2-existing-repair`  
**Base main:** `bf4ed5b7017e63f2e0f1cfb43f60250d1c5ab988`  
**Repair evidence head under audit:** `ce695c40782e1847ed70a2256885b5b07113feae`  
**Status:** ACTIVE / AUDIT ONLY / NO RENDERER CHANGES  
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

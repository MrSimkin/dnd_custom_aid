# Checkpoint — Mara owner re-QA runtime FAIL and repair-traceability reset

**Date:** 2026-09-28 (Chile local time)  
**Owner-facing candidate label used:** `dnd-custom-aid-0.5.0-preqa.8-build-50800.apk`  
**Prior repair branch:** `repair/pc-sheet-adaptive-continuations-cross-family`  
**Prior validated head claimed by closure:** `ce695c40782e1847ed70a2256885b5b07113feae`  
**Status:** OWNER RE-QA FAIL / PRIOR REPAIR CLOSURE NOT VALID AS RUNTIME ACCEPTANCE / CANDIDATE PROVENANCE AMBIGUOUS / DO NOT MERGE

## Owner evidence reviewed

Runtime outputs supplied by the owner:

- Fantasy PDF: **45 pages**; SHA-256 `a4a5864dcb4a1d39570609e671ea860d2de440077bc8d6b62e5be22668909bf7`;
- Custom v2 · Atributo PDF: **28 pages**; SHA-256 `74ccaf54f29ef03f1d95724cc63728c05cd5f09ef3c22aaacd739dceb9ae6b96`;
- Custom v2 · Habilidad PDF: **27 pages**; SHA-256 `bd0cc116031372526f0b160133598f8b69992d8936b1c3b383e35b300d2897a6`;
- Custom v1 export failure screenshot: SHA-256 `a39cd801b6b30c09462fe1f119e760f141215d47e92c7f5faa158fe0c5d3da34`.

Custom v1 fails before producing a PDF with:

`Custom-v1 source-matched label requires excessive compression: 'Lectura de presagios' (49.175125%)`

This is a new blocking regression: Custom v1 was previously exportable in the staged runtime sequence and is now **NOT EXPORTABLE** for this Mara path.

## Decisive runtime/proof mismatch

The superseded repair-closure checkpoint claimed these real-Mara page ceilings/results:

- Fantasy: **29**;
- Custom v1: **18**;
- Custom v2 · Atributo: **16**;
- Custom v2 · Habilidad: **15**.

The owner runtime candidate instead produced:

- Fantasy: **45**;
- Custom v1: **export failure**;
- Custom v2 · Atributo: **28**;
- Custom v2 · Habilidad: **27**.

Therefore the prior closure cannot be treated as proof that the owner runtime path contains or exercises the claimed repair.

At least one of these must be true and must be resolved before another layout fix is attempted:

1. the owner installed a stale/older APK that shares the same `0.5.0-preqa.8 / 50800` identity;
2. the owner-facing APK artifact was built from a different source state than the repair proof;
3. Android runtime does not actually exercise the renderer behavior validated by the prior proof/tests.

The repository must not choose among these without evidence. The immediate technical gate is **binary/provenance parity**, not speculative visual coding.

## Previous QA source re-opened

The required source of truth for this repair is:

`docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`

The owner re-QA confirms that the same defect classes recorded there remain visible in runtime. The prior closure's statements that those items had bounded repair evidence are **not accepted as runtime closure**.

The consolidated pre-fix acceptance authority is:

`docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`

That matrix must be used before the next renderer code change and again against the exact next candidate output before owner handoff.

## Custom v2 — previous defect classes still present

### A. Template/source-underlay contamination — STILL PRESENT

The generated Custom-v2 text layer still contains unrelated template labels outside their visible section context, and visible underlay/custom-location artifacts remain in Inventory / Equipo Especial.

This remains a PDF-structure plus visual-layering defect.

### B. Custom Statistics card corruption — STILL PRESENT

Custom v2 · Atributo page 5 still renders generic black/white score-modifier blocks rather than the intended page-1/source-derived attribute grammar.

The Éter heading still appears approximately as `ETE ter`, not a clean unambiguous Éter presentation.

Custom v2 · Habilidad page 5 shows the same generic/corrupted attribute-card family.

### C. Phantom empty attribute cards — STILL PRESENT

- Atributo page 6 renders Renombre plus **two empty fake card shells**.
- Habilidad page 5 renders the four real custom attributes plus **two empty fake card shells**.

Empty siblings must not be painted as missing statistics.

### D. Traits/Features fixed-scaffold waste — STILL PRESENT

- Atributo pages **7–18**;
- Habilidad pages **6–17**.

The renderer repeatedly emits the same multi-panel scaffold while only one or two streams remain populated. Large empty panels persist while continuation text is constrained into small regions.

The intended content-adaptive continuation redesign is not present in owner runtime.

### E. Traits ordering/record presentation — ORDER CONTRACT CLARIFIED / LAYOUT DEFECT STILL OPEN

Category grouping is **intentional and accepted**. Global numeric fixture order across categories is not required.

The remaining defect is presentation/layout: the renderer must reuse the existing trait/rasgo grammar, keep coherent order inside each category, preserve record boundaries and stop wasting pages through repeated fixed scaffolds.

### F. Combat / Actions — STILL PRESENT

- Atributo pages **19–20**;
- Habilidad pages **18–19**.

Confirmed defects:

- header labels collide with first-row content;
- row height is excessive;
- content uses alternating/empty physical rows inefficiently;
- page 2 of the continuation is largely empty;
- available width is badly allocated;
- fields are hard to associate visually.

### G. Resources / Options packing — STILL PRESENT

- Atributo pages **21–25**;
- Habilidad pages **20–24**.

After option content is exhausted, a large empty Options table continues to be reproduced while Resources/Markers remain. The cross-stream reclaim rule is not working in runtime.

### H. Ordinary Equipment reconstruction/readability — STILL PRESENT

- Atributo pages **26–27**;
- Habilidad pages **25–26**.

The current Extended Inventory treatment is not merely hard to parse; it is the wrong presentation model. The base **EQUIPO** element already provides the intended grammar and should be reused/copied with additional native columns/rows/pages as capacity requires.

The desired visible content is compact item identity (for example `3 x Frasco de tinta`).

**Weight must not be shown. `Consumible` must not be shown. Prose descriptions must not be shown.**

### I. Special Equipment custom-location overprint — STILL PRESENT

On Atributo page 26 and the corresponding Habilidad inventory page, noncanonical/custom locations visibly overprint fixed location labels. Examples include custom location text such as `Espalda` and `Bolsa lateral` colliding with canonical rows.

### J. Redundant empty Special Equipment scaffold — STILL PRESENT

The second Inventory page repeats a large essentially empty Equipo Especial table after useful special-item content has already been emitted.

### K. Notes boundaries and continuation packing — STILL PRESENT

- Base Notes page **4** in both Custom-v2 variants concatenates note records with no useful visual record boundary.
- Overflow Notes page **28** in Atributo / **27** in Habilidad resumes text without a strong continuation identity, uses only part of the available writing area and leaves the other side largely unused.

The earlier note-boundary and adaptive packing requirement remains unmet.

## Additional owner-confirmed Custom-v2 defects

1. **Portrait/name ribbon alignment** — page 1 character-name text is visibly shifted/misaligned inside the portrait ribbon.
2. **Custom Statistics visual source mismatch** — the Extended custom-stat cards should reuse the correct source/page-1 visual grammar instead of the generic black/white blocks.
3. **Inventory Extended is effectively illegible** in the current stress output.
4. **Equipo Especial should reuse the correct native/source module grammar** rather than the current overprinted table behavior.

## Owner clarifications after detailed 50800 evidence review — 2026-09-28

These clarifications are **authoritative refinements of the acceptance ledger**. They do not replace the original defects; they define the intended result more precisely.

### General source-component reuse clarification

The owner further clarified that this is **not limited to Custom Statistics, Equipment, Equipo Especial or Notes**.

The existing sheets already contain usable visual examples for **Traits/Rasgos, Trasfondo/Historia and other similar semantic sections**. Those examples should have been treated as the implementation reference instead of reconstructing new Extended components.

Therefore, for any section that already has an approved/native representation:

1. reuse/copy the existing component grammar first;
2. preserve its fonts, scale, spacing, hierarchy and visual identity;
3. extend capacity by adding/copying rows, columns or pages as needed;
4. switch to adaptive continuation only to reclaim space or continue overflow;
5. do **not** invent a generic replacement unless no adequate source/native component exists or the owner explicitly approves a redesign.

This is a cross-section product rule, not a one-off fix for Equipment.

### Custom Statistics — reuse the page-1 attribute design and capacity

The problem is not merely that the Extended page paints phantom cards or that three cards look wrong.

The Custom-v2 page-1 source grammar already demonstrates **six standard attributes across the available line/region using the approved font, size, ornament and visual construction**. Extended Custom Statistics must reuse that same grammar, scale and font behavior rather than inventing a different three-card generic layout.

For Mara's four custom attributes, the expected result is therefore conceptually one native-source row containing all four real attributes:

- Fortuna;
- Cordura;
- Éter;
- Renombre.

There is no design justification for forcing only three real attributes onto the first Extended statistics page when the same family already proves six can fit in the equivalent native grammar. Empty/fake attribute shells must never be painted.

This is primarily a **reuse/source-parity requirement**, not a request to compress a separate generic component.

### Traits ordering — category order is intentional

Traits/features may be grouped **by category** rather than preserving one global numeric source sequence. The layout should preserve coherent category grouping and readable record order inside each category.

The defect is not category grouping itself. The defect remains the fixed-scaffold continuation waste, poor space reclaim and record presentation.

### Ordinary Equipment — reuse the existing element; do not invent descriptive Inventory pages

The existing base **EQUIPO** element is already the intended visual/semantic solution for ordinary equipment. The renderer should **copy/reuse that element and extend it by adding/repeating native columns as required**, rather than inventing a separate Extended Inventory design.

Ordinary equipment does **not** require long prose descriptions or dedicated descriptive Inventory continuation pages. The stress output's long detail paragraphs are not a desired product presentation.

The intended visible content is the compact item identity, for example:

- `3 x Frasco de tinta`

**Weight must not be shown. `Consumible` must not be shown. Prose descriptions must not be shown.**

Those fields may exist in the stress fixture/data model, but they are not part of the desired ordinary Equipment PDF presentation.

The repair must therefore reuse the already-clear base Equipment grammar — same visual construction, font/size/rhythm — and increase capacity by copying/adding its existing columns/rows/pages as needed.

### Equipo Especial — reuse the existing native element

The renderer must not create a bespoke Extended Equipo Especial table when the source/base sheet already contains a visually correct Equipo Especial component.

Extended handling should **reuse/copy the native-source grammar and extend it only as needed for additional/custom locations**. Noncanonical locations such as `Espalda` and `Bolsa lateral` must become legitimate rows/entries rather than overprinting fixed canonical labels.

This is not unique to Equipo Especial: **ordinary Equipment also already has a correctly designed native/source element**. Both modules must be reused directly; Extended rendering should add/copy capacity from the existing components rather than create replacement designs.

### Notes — reuse the existing Notes sheet, not a bespoke Extended Notes design

The project already has an existing Notes sheet/module. The owner explicitly expects overflow Notes to **replicate/reuse that existing Notes visual grammar** rather than inventing a separate Extended Notes page.

Record readability requirements:

- each note must preserve a visible semantic boundary;
- at minimum, `Nota N` should be visually emphasized (for example bold) and there should be a line/row separation between notes;
- if a note crosses a page boundary, the continuation must retain the note identity;
- available Notes space/columns must be consumed before adding another page.

The architectural question is therefore not “how should a new Extended Notes sheet look?” but “how should the existing Notes grammar be reused and paginated adaptively?”

### Portrait/name ribbon alignment

The character name must be centered **horizontally and vertically inside the existing portrait ribbon**. If the full name does not fit cleanly on one line, use two centered lines rather than shifting, clipping or compressing the text.

### Fantasy semantic names — wrap, do not ellipsize

Generated semantic names/identities must use **multi-line wrapping** when required instead of `...` truncation.

The owner clarification applies to meaningful generated content such as character metadata, attacks, traits, resources/options and equipment identities. The renderer should preserve the complete semantic identity and allow the record/row to grow as needed.

### Page count is evidence, not a numeric target

There is **no fixed minimum or maximum page-count requirement** and no requirement that Fantasy specifically equal the previously claimed 29 pages.

The 45-page runtime output is evidence of bad space allocation, pagination and page design because many pages are mostly empty or retain exhausted scaffolds. The acceptance rule is efficient, readable use of physical space; page count should fall out of correct layout rather than become an optimization target.

### Repair-process requirement — no code before understanding the real QA

The owner requires a strict process rule:

**No fix may begin before the real QA result has been reviewed in detail.**

This 50800 re-QA reproduced essentially the same failures as the prior QA, plus a worse Custom-v1 generation regression. Therefore the prior “repair” did not demonstrate that it addressed the owner's actual problems.

For future repairs:

1. inspect the actual failing artifact/output first;
2. restate every owner observation and intended result;
3. use existing approved/native elements as the primary implementation reference wherever they already solve the design problem — including attributes, rasgos/features, trasfondo/history, Equipment, Equipo Especial, Notes and analogous modules;
4. only then design and implement the fix;
5. after implementation, inspect the actual candidate output against the same original observations before calling the repair complete or asking the owner to re-QA.

Coding, green CI, changed page counts or synthetic proofs are not substitutes for this source-observation review.

## Fantasy — owner observations confirmed and expanded

### Underused physical space / packing failure

The renderer often advances content while substantial usable space remains.

Clear examples include:

- page 1: the class-traits area does not use its available capacity efficiently while related content overflows later;
- pages 6–16: Traits continuation repeatedly leaves significant unused lines/regions; page **16** is an extreme case with almost the entire two-column page empty except the tail of one record;
- pages 23–27: Resources/Options retains a large empty lower Options scaffold after useful option content is exhausted;
- pages 38–45: Notes preserves the map/reference scaffold even while campaign-note overflow continues; page **45** is overwhelmingly empty.

This is the same core adaptive-packing failure seen in Custom v2, expressed in the Fantasy family.

### Widespread semantic-name truncation / ellipsis

Excluding the repeated page header `Mara de los Siete...`, visible generated semantic names are ellipsized on Fantasy pages:

**1, 2, 6–16, 18–25 and 28–37.**

Examples include:

- base metadata and attack/damage labels on page 1;
- Equipment names on page 2;
- repeated trait names on pages 6–16;
- resource/option names on pages 18–25;
- inventory and special-item names on pages 28–37.

The owner specifically requires multi-line wrapping when needed rather than silently shortening semantic identities with `...`.

### Combat / additional actions loses table semantics

Fantasy page **17** flattens additional combat/actions into prose-like ruled lines. It does not preserve a clear per-action column structure for fields such as attack/action identity, modifier, damage/effect/range and notes comparable to the base combat grammar.

### Fantasy page-count evidence

Fantasy reaches **45 pages**, not the prior repaired-proof ceiling of 29. The fixed-scaffold/adaptive continuation repair is therefore not demonstrated in runtime.

## New blocking regression — Custom v1

Mara Custom v1 / Permanente / Extended cannot currently generate a PDF.

Exact runtime diagnostic:

`Custom-v1 source-matched label requires excessive compression: 'Lectura de presagios' (49.175125%)`

This must be treated as a regression introduced or exposed in the candidate path, not as a minor visual residual.

No future owner candidate may be promoted until all four visual families at least complete generation on the real Mara fixture.

## Process finding — repair source was not kept as the acceptance ledger

The prior repair closure stated that the source observations were repaired, but owner runtime reproduces the same observations.

A repair may not close by validating a transformed proof while losing the exact original QA complaints that caused the work.

The source defect checkpoint must remain the acceptance ledger until every source observation has explicit candidate evidence.

## Process finding — owner candidate identity collision

`0.5.0-preqa.8 / 50800` had already identified the earlier pre-repair owner candidate. A materially different repaired binary was then described using the same owner-facing version/build identity.

That makes stale and repaired APKs indistinguishable to the owner and prevents reliable runtime provenance.

This is now a process defect.

## Required next gate

Do **not** merge/promote the adaptive repair branch and do **not** start Current Snapshot or Media/Handouts.

Before new layout implementation:

1. establish exactly which commit/artifact produced the APK actually installed by the owner;
2. compare its Android runtime renderer source against the prior repair branch;
3. reopen the original 2026-09-26 defect checkpoint, this clarified 2026-09-28 checkpoint **and** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md` as the acceptance ledger;
4. inspect the real 50800 runtime outputs and walk the pre-fix matrix before changing repair code;
5. map every observation plus the Fantasy/Custom-v1 regressions to:
   - responsible implementation surface;
   - the existing approved/native visual component that should be reused when one exists;
   - automated regression;
   - exact runtime candidate evidence;
6. do not start implementation while any expected result remains materially ambiguous;
7. generate a **new uniquely versioned owner candidate**; never reuse `50800`;
8. run generation smoke on all four families with real Mara before owner handoff;
9. inspect the actual generated candidate PDFs/output against the full acceptance ledger, not merely CI/proof summaries;
10. verify every original observation as FIXED/OPEN/CHANGED before asking the owner for another manual visual pass.

The next owner QA APK must have a unique versionName/versionCode and exact commit/artifact provenance.

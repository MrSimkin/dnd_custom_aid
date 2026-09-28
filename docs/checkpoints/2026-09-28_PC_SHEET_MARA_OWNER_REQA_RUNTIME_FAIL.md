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

### E. Traits ordering/record presentation — STILL UNRESOLVED

Category/panel grouping still determines visible order and continuation placement. The earlier ordering-contract issue is not resolved by this re-QA.

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

### H. Inventory association/readability — STILL PRESENT

- Atributo pages **26–27**;
- Habilidad pages **25–26**.

Ordinary Equipment is visually difficult to parse. Item identity, weight/status and descriptive continuation are fragmented across rows/columns. Long item records are not kept in a readable logical unit.

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
3. reopen the original 2026-09-26 defect checkpoint as the acceptance ledger;
4. map every original observation plus the new Fantasy/Custom-v1 regressions to:
   - responsible implementation surface;
   - automated regression;
   - exact runtime candidate evidence;
5. generate a **new uniquely versioned owner candidate**; never reuse `50800`;
6. run generation smoke on all four families with real Mara before owner handoff;
7. verify the owner-facing runtime outputs match the repaired expectations before asking for another manual visual pass.

The next owner QA APK must have a unique versionName/versionCode and exact commit/artifact provenance.

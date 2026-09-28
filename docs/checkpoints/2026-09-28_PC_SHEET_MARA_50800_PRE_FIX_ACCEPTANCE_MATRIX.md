# Mara 50800 — pre-fix acceptance matrix

**Date:** 2026-09-28 (Chile local time)  
**Status:** ACTIVE / PRE-FIX ACCEPTANCE AUTHORITY  
**Parent checkpoint:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_OWNER_REQA_RUNTIME_FAIL.md`  
**Original defect ledger:** `docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`

## Purpose

This matrix consolidates the owner's detailed review of the real Mara runtime outputs from the owner-facing `0.5.0-preqa.8 / 50800` candidate.

It exists so the next repair cannot begin from a vague bug list, a synthetic proof, a prior agent summary or an assumed design. Every repair item below remains part of the acceptance ledger until the **actual generated output of the exact promoted candidate** is reviewed against it.

No renderer/product code should be changed until this matrix, the parent checkpoint and the real 50800 evidence have been reviewed.

## Real owner evidence

Owner runtime evidence already supplied:

- Fantasy PDF — 45 pages — SHA-256 `a4a5864dcb4a1d39570609e671ea860d2de440077bc8d6b62e5be22668909bf7`;
- Custom v2 · Atributo PDF — 28 pages — SHA-256 `74ccaf54f29ef03f1d95724cc63728c05cd5f09ef3c22aaacd739dceb9ae6b96`;
- Custom v2 · Habilidad PDF — 27 pages — SHA-256 `bd0cc116031372526f0b160133598f8b69992d8936b1c3b383e35b300d2897a6`;
- Custom v1 failure screenshot — SHA-256 `a39cd801b6b30c09462fe1f119e760f141215d47e92c7f5faa158fe0c5d3da34`.

Custom v1 runtime failure:

`Custom-v1 source-matched label requires excessive compression: 'Lectura de presagios' (49.175125%)`

The PDFs plus the Custom-v1 screenshot are sufficient primary evidence for the currently recorded defects. Historical photos are not a prerequisite for the repair.

## Governing design rule — reuse before reconstruction

When the sheet already contains an approved/native component that correctly represents the same semantic family, **reuse/copy that component and extend its capacity**.

Do not reconstruct a generic replacement simply because content has moved to an Extended page.

This applies to, at minimum:

- Custom Statistics / attributes;
- Traits / Rasgos / Features;
- Trasfondo / Historia / personality-style narrative modules;
- Combat / Actions where an existing tabular grammar exists;
- ordinary Equipment;
- Equipo Especial;
- Notes;
- analogous repeated modules with an already usable source/native example.

The repair question is:

> What existing approved/source component already represents this content, and how do we extend its capacity without losing its visual grammar?

It is not:

> What new generic Extended component should we design?

## Acceptance matrix

| ID | Area | 50800 failure / owner clarification | Required result | Required candidate evidence | Status |
| --- | --- | --- | --- | --- | --- |
| M50800-01 | Candidate provenance | The same `0.5.0-preqa.8 / 50800` identity was reused for materially different binaries, so the runtime/proof mismatch cannot be resolved from the label. | Establish exact source commit/artifact for the runtime APK before speculative repair; every later owner candidate gets a new unique version/build identity. | Commit, CI/run, artifact ID and digest for the next candidate; no reuse of 50800. | OPEN |
| M50800-02 | Custom v1 generation | Custom v1 does not produce a PDF and fails on `Lectura de presagios`. | Real Mara Custom v1 must complete generation before owner handoff. | Real-Mara generation smoke + actual generated PDF from exact candidate. | OPEN |
| M50800-03 | Template/source underlay | Custom-v2 Extended pages retain stale source-template text objects and some leak visibly into current content. | Unrelated source/template objects must not remain searchable/copyable/visible in the wrong semantic page context. Preserve the approved layered source construction rather than flattening the whole page. | Text-layer inspection plus rendered affected pages. | OPEN |
| M50800-04 | Custom Statistics visual grammar | Extended custom attributes were rebuilt as large generic black/white cards instead of using the already-correct page-1 attribute design. | Reuse the page-1/native Custom-v2 attribute grammar: same visual construction, scale, font roles and spacing logic. | Side-by-side source/page-1 vs Extended render inspection. | OPEN |
| M50800-05 | Custom Statistics capacity | Page 1 proves six attributes fit in the native grammar, yet Extended fit only three and forced Renombre to another page. | Mara's four custom attributes — Fortuna, Cordura, Éter, Renombre — fit together using the native-scale grammar. Do not invent a 3-card capacity limit. | Actual Custom-v2 Atributo/Habilidad candidate pages. | OPEN |
| M50800-06 | Phantom custom attributes | Empty/fake attribute shells are painted for unused capacity. | Render only real attributes. No fake/empty score/modifier cards. | Rendered Custom Statistics pages. | OPEN |
| M50800-07 | Éter presentation | The custom-stat label appears approximately as `ETE ter` instead of an unambiguous Éter presentation. | Preserve complete, unambiguous semantic name/abbreviation using the native attribute grammar. | Rendered Custom Statistics page. | OPEN |
| M50800-08 | Traits/Rasgos source reuse | Extended traits were reconstructed as a generic multi-panel system despite usable existing trait/rasgo examples. | Reuse the established trait/rasgo visual grammar and extend its capacity rather than replacing it with an unrelated generic design. | Rendered base/continuation comparison. | OPEN |
| M50800-09 | Traits ordering | Earlier ledger treated grouping order as ambiguous. Owner clarified that category grouping is intentional. | Preserve category-grouped presentation; maintain coherent order inside each category. Global numeric 1…26 order across categories is not required. | Text/order scan + rendered continuation. | OPEN |
| M50800-10 | Traits packing | Pages repeatedly reproduce mostly-empty panels while continuation text is constrained to a small region. | Once sibling sections are exhausted, surviving trait content reclaims the free page space. No repeated irrelevant scaffold. | Actual continuation pages showing adaptive reclaim. | OPEN |
| M50800-11 | Trasfondo/Historia and similar modules | Generic Extended reconstruction was used even though usable narrative modules already exist in the sheet. | Reuse the source/native heading hierarchy, font roles, writing rhythm and section identity; extend capacity adaptively. | Base vs continuation visual comparison. | OPEN |
| M50800-12 | Custom-v2 Combat/Actions | Header labels collide with the first row; row heights are excessive; fields are hard to associate; continuation page is mostly empty. | Preserve a clear table grammar with headers separated from data, readable association of identity/range/bonus/damage/effect/notes, and content-driven row height/packing. | Rendered affected pages. | OPEN |
| M50800-13 | Fantasy Combat/Actions | Fantasy continuation flattened combat entries into prose and lost the semantic table scan pattern. | Reuse/preserve the existing combat/action semantic grammar rather than flattening all fields into prose. | Rendered Fantasy combat continuation. | OPEN |
| M50800-14 | Resources/Options packing | After Options are exhausted, a large empty Options scaffold is repeated while Resources continue. | Exhausted streams stop reserving space; Resources/Markers reclaim the available area before another page is created. | Rendered resource continuation pages. | OPEN |
| M50800-15 | Ordinary Equipment design | A separate descriptive Inventory presentation was invented although the base Equipment element was already clear and usable. | Reuse/copy the existing ordinary Equipment element. Increase capacity by adding/repeating its native columns/rows/pages as needed. | Base Equipment vs continuation render comparison. | OPEN |
| M50800-16 | Ordinary Equipment content | Long prose descriptions, weight and state metadata made Inventory effectively illegible. | Visible ordinary Equipment content is compact item identity, e.g. `3 x Frasco de tinta`. **Do not display weight. Do not display `Consumible`. Do not display prose descriptions.** | Text scan and rendered Equipment pages. | OPEN |
| M50800-17 | Ordinary Equipment readability | Item identity and metadata/descriptions were fragmented across physical streams and columns. | Each compact equipment identity remains visually atomic and readable within the reused native Equipment grammar. | Rendered Equipment pages. | OPEN |
| M50800-18 | Equipo Especial design | Renderer invented a separate Extended table despite an already-correct native Equipo Especial element. | Reuse/copy the existing native/source Equipo Especial component; extend only capacity/rows as needed. | Base vs continuation render comparison. | OPEN |
| M50800-19 | Equipo Especial custom locations | Custom locations such as `Espalda` and `Bolsa lateral` overprint fixed canonical labels. | Custom/noncanonical locations become legitimate entries/rows without drawing over canonical source labels. | Rendered Equipo Especial page. | OPEN |
| M50800-20 | Redundant Equipment scaffolds | Later Inventory page repeats empty Equipment/Equipo Especial structures after useful content is exhausted. | Do not emit empty continuation scaffolds. Surviving content reclaims the area; no page exists solely to preserve an exhausted template. | Page-level render review. | OPEN |
| M50800-21 | Notes design | A separate generic Extended Notes sheet was invented despite an existing Notes sheet/module. | Reuse/replicate the existing Notes visual grammar for overflow. Extended is additional capacity, not a new Notes design. | Base Notes vs continuation render comparison. | OPEN |
| M50800-22 | Notes record boundaries | Note 1, Note 2, etc. run directly into one another. | At minimum, emphasize `Nota N` (e.g. bold) and insert visible line/row separation between note records. | Rendered Notes pages. | OPEN |
| M50800-23 | Notes continuation identity | Overflow page can begin with the tail of a note without clearly saying which note continues. | A cross-page continuation retains the note identity, e.g. `Nota 8 (continuación)` or equivalent native treatment. | Rendered cross-page Notes continuation. | OPEN |
| M50800-24 | Notes packing | Continuation uses only part of the available writing area/column while other Notes capacity remains empty. | Consume the available native Notes rows/columns before creating another page. | Rendered Notes pages + page count/context inspection. | OPEN |
| M50800-25 | Character-name ribbon | Name is shifted/misaligned inside the portrait ribbon. | Center horizontally **and vertically** inside the existing ribbon; if one line does not fit cleanly, use two centered lines. | First-page render in affected Custom families. | OPEN |
| M50800-26 | Semantic ellipsis | Fantasy truncates meaningful generated names with `...` across metadata, attacks, traits, resources/options and equipment. | Meaningful semantic names **wrap** to additional lines; do not silently ellipsize them. Record/row may grow in height. | Text scan for unintended ellipsis + rendered affected pages. | OPEN |
| M50800-27 | Adaptive continuation architecture | Fixed multi-panel/page scaffolds remain allocated even after a content stream ends, producing many mostly-empty pages. | Content streams reclaim space from exhausted siblings; pack vertically/across available native columns before adding pages; preserve semantic boundaries. | Multi-family actual candidate PDFs. | OPEN |
| M50800-28 | Page count interpretation | 45 pages is bad evidence because of wasted space, not because 45 exceeds a fixed allowed number. | No fixed minimum/maximum page count target. Page count is a consequence of readable, efficient layout. Do not optimize to the old claimed 29/18/16/15 numbers. | Visual/packing review, not a numeric ceiling assertion. | OPEN |
| M50800-29 | Data preservation | Previous scan found all 26 traits, 10 resources, 7 markers, 8 options, 34 inventory identities and 9 note titles, but presentation/association is poor. | Preserve all required semantic identities while repairing layout. Do not trade readability for data loss or silent omission. | Text/content regression against real Mara. | OPEN |
| M50800-30 | Pre-fix comprehension gate | Previous repair was declared closed without demonstrating the owner's actual defects were gone; 50800 reproduces essentially the same problems and adds Custom-v1 failure. | Before code changes, inspect real failing artifacts, restate each owner observation and intended result, identify native references and map evidence. | Repair-plan/ledger mapping exists before first renderer code change. | REQUIRED BEFORE CODE |
| M50800-31 | Candidate acceptance gate | CI, synthetic proofs, smaller page counts or rewritten tests can falsely appear green. | Before owner handoff, inspect the **actual PDFs/output from the exact candidate** and mark each matrix item FIXED / OPEN / CHANGED-NEW. Any blocking OPEN/NEW regression blocks handoff. | Completed matrix against exact candidate artifacts. | REQUIRED BEFORE HANDOFF |
| M50800-32 | Cross-family generation | A cross-family repair introduced/exposed Custom-v1 failure while other outputs still had old defects. | Real Mara generation smoke covers Fantasy, Custom v1, Custom v2 Atributo and Custom v2 Habilidad before owner handoff. | All-four-family smoke from exact candidate. | OPEN |

## Definition of FIXED

For this matrix, **FIXED** means the original owner-observed defect is demonstrably absent from the actual output of the exact candidate being promoted.

The following are insufficient by themselves:

- green CI;
- a passing unit/snapshot test;
- a synthetic proof generated through a different path;
- a lower page count;
- a rewritten test expectation;
- a source-code diff that appears plausible;
- a prior agent's claim that the issue was addressed.

## Required pre-fix sequence

Before the next renderer repair begins:

1. establish exact provenance/parity for the 50800 runtime APK as far as repository evidence allows;
2. reopen the real 50800 PDFs/screenshot plus the 2026-09-26 and 2026-09-28 ledgers;
3. use this matrix to map each OPEN item to:
   - responsible implementation surface;
   - existing native/source component reference when available;
   - planned automated regression;
   - planned actual-candidate inspection;
4. only then change renderer/product code.

## Required pre-owner-handoff sequence

For the next candidate:

1. unique versionName/versionCode — never reuse 50800;
2. real Mara generation succeeds in all four families;
3. actual generated outputs are inspected, not merely synthetic proofs;
4. every applicable matrix item is marked FIXED / OPEN / CHANGED-NEW;
5. any blocking OPEN or new regression prevents owner handoff;
6. only after this internal acceptance pass is the owner asked to perform the next visual QA.


# Checkpoint - PC Sheet PDF owner-marked bounded repair / final visual gate

**Date:** 2026-09-23  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 - OPEN / DRAFT / DO NOT MERGE  
**Implementation head:** `f7e4417c05a2981415ef3648ead740e20469fe33`  
**Status:** IMPLEMENTATION + CI + WORKER VISUAL PREFLIGHT PASS / OWNER FINAL VISUAL QA PENDING

## Why this checkpoint exists

The owner reviewed the VR-3 proof visually and supplied annotated screenshots that made three already-existing visual-contract regressions unambiguous. These were not new product requirements.

The bounded repair addressed:

1. Custom-v1 Equipment compact typography;
2. Custom-v2 section-heading grammar plus reuse of the native Equipment / Equipo Especial visual modules;
3. Fantasy Sheet continuation ruled-space continuity.

## Owner findings carried into this repair

### Custom v1

Owner disposition before repair: **PASS WITH OBSERVATION**.

Required bounded improvement:

- Equipment should use a condensed/readable treatment when useful so a logical item such as `Mochila de expedición · Espalda · 5 lb` remains one physical row when reasonable;
- longer descriptive/status content may continue below;
- no redesign of the approved Custom-v1 family.

Once this bounded treatment is correct, Custom v1 is an automatic visual pass subject only to the final whole-proof review.

### Custom v2

Owner findings:

- headings such as `CLASE / DOTES`, `RAZA / TRASFONDO / OTROS`, `OTROS RASGOS`, `DETALLES / NOTAS`, `COMPETENCIAS / IDIOMAS`, and `CONTINUACIÓN` must behave as headings, not as gray data rows;
- heading font/size/spacing must follow the same Custom-v2 visual grammar already used elsewhere on the page;
- normal Equipment is an existing reusable module and continuation must not invent an unrelated three-column table;
- `Equipo Especial` continuation must reuse the normal-page module grammar instead of introducing a different table;
- source-defined special-equipment location rows such as Cabeza/Rostro/Cuello/etc. must not receive duplicate overprinted location labels;
- Equipment may use a condensed/readable font so logical item identity remains together when reasonable;
- item status/notes must remain visually associated with the item, not appear as an unrelated neighboring-cell item.

### Fantasy Sheet

Owner findings:

- continuation pages were mixing two incompatible geometries: fixed-height semantic feature cards plus unrelated continuous ruled paper;
- the entire writable region must use one continuous ruled-paper grammar;
- feature content consumes consecutive physical rows only as needed;
- unused capacity remains visible writable ruled paper;
- unexplained blank white holes with missing rules are regressions and must not exist.

## Implementation result

The repair changed renderer mechanics rather than papering over coordinates:

- Fantasy trait continuations now use one continuous ruled grid; fixed 90-point feature-card allocation was removed;
- Custom-v2 Extended headings use white title breathing bands and the established Corbel-bold heading grammar instead of looking like data rows;
- Custom-v2 inventory continuation reuses the normal two-column Equipment module twice instead of the rejected three-column invention;
- Custom-v2 Equipo Especial uses native location-row semantics; recognized locations choose the row instead of being printed over the source label;
- Custom-v1 and Custom-v2 Equipment use the bundled condensed typography for compact item identity;
- final text-flow cleanup keeps item identity on one ruled line when feasible and keeps `Estado:` / `Nota:` lines underneath the same item/column.

## CI history for the bounded repair

The first repair head exposed two bounded test/render issues:

- a long Custom-v2 treasure label did not fit the reused Equipment column;
- one old Custom-v1 assertion assumed `Uso rápido 2` could not cross a PDF text-run/line boundary.

These were repaired without weakening the new visual rules.

A later green candidate `87ac73e4da63e257b5349aa8cd97d971b9264a68` passed push #3276 / `35871179109` and PR #3277 / `35871183759`.

Worker inspection of that artifact found two remaining filling artifacts:

- Custom-v1 could orphan `5 lb` onto a separate ruled line;
- Custom-v2 could place an item's descriptive continuation in the neighboring Equipment cell.

Those were fixed in the final text-flow cleanup.

### Final head

Implementation: `f7e4417c05a2981415ef3648ead740e20469fe33`

- push Scaffold #3279 / `35873556136` - **SUCCESS**;
- PR Scaffold #3280 / `35873560390` - **SUCCESS**;
- backend - PASS;
- hosted database - PASS;
- Kotlin build/test - PASS;
- Android debug APK upload - PASS;
- PC sheet source renders upload - PASS;
- populated PC sheet proof upload - PASS.

Final populated-proof artifact:

- artifact ID: `10756937024`;
- artifact name: `pc-sheet-populated-template-proofs`.

## Worker visual preflight on final artifact

### Fantasy Sheet

- trait continuation now presents one coherent continuous ruled field;
- content consumes consecutive rules;
- unused area remains visibly writable;
- the prior mixed red/green rhythm and unexplained white-hole behavior are no longer present in the inspected proof.

### Custom v1

- logical Equipment identity remains together, e.g. item + location + weight;
- operational state and notes are explicitly labeled below the item instead of resembling separate equipment entries;
- no family redesign was introduced.

### Custom v2 - per Attribute / per Ability

- Extended section headings read as headings rather than gray data rows;
- Equipment continuation uses the normal-family two-column module grammar;
- wrapped/status/note lines remain vertically attached to their item instead of spilling into the neighboring cell;
- Equipo Especial uses native location-row semantics and no duplicate recognized location text is overprinted;
- both v2 variants use the same repaired continuation implementation.

## Gate meaning

This is the **final visual/layout/text-filling gate** for the currently implemented PDF families.

Owner final visual QA is still mandatory. Until explicit approval:

- PR #85 remains DRAFT / DO NOT MERGE;
- do not freeze this candidate as owner-approved;
- do not begin Desktop Save/Share/export invocation.

If the owner approves this exact candidate:

1. append explicit OWNER APPROVED evidence to the iteration ledger;
2. freeze this renderer/proof state;
3. proceed to Desktop Save/Share integration by invoking the existing `DesktopPcSheetWholeDraftRenderer` / `PcSheetPdfRenderPlan` path;
4. perform an implementation-parity smoke/regression check to prove the UI-exported bytes preserve the frozen renderer output;
5. do **not** redesign layouts or reimplement text filling during Save/Share integration.

If the owner reports a defect, treat it as a bounded visual regression against this checkpoint and append the next iteration; do not restart the PDF design process.

No external provider action is required at this gate.

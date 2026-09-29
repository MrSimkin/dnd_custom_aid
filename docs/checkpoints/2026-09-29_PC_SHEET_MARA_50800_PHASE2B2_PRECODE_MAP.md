# Mara 50800 — Phase 2B.2 Notes pre-code QA/source map

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Status:** ACTIVE / BLOCKING PRE-CODE MAP FOR PHASE 2B.2  
**Acceptance authority:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`  
**Previous progression gate:** `docs/checkpoints/2026-09-29_PC_SHEET_MARA_50800_PHASE2B1_CLOSURE.md`

## 1. Scope

Phase 2B.2 is restricted to:

- M50800-21 — Notes native/source design reuse;
- M50800-22 — visible note-record boundaries;
- M50800-23 — continuation retains note identity;
- M50800-24 — consume native Notes rows/columns before another page.

M50800-29 data preservation is guarded as a regression, not claimed closed by this subphase.

This phase does **not** include residual Equipment M50800-17/20, Combat, Resources, Fantasy, final candidate versioning or owner handoff.

## 2. Real 50800 observations reopened

Owner `0.5.0-preqa.8 / 50800` showed:

- base Notes page 4 concatenates Note 1, Note 2, etc. without useful record boundaries;
- overflow Notes begins with the tail of a prior note without an explicit continuation identity;
- Note 9 follows directly afterward;
- background-derived note material then follows;
- continuation uses only the left Notes column while the right native column remains empty.

Owner clarification requires:

- reuse the existing Notes sheet/module rather than inventing a generic Extended Notes design;
- visually emphasize `Nota N` (bold or equivalent);
- leave visible line/row separation between note records;
- retain note identity across continuation;
- consume available Notes rows/columns before another page.

## 3. Exact current artifact recheck

Exact current progression artifact before this fix:

- workflow 4230 / renderer head `adb01dd851286419409e7f8dcc349e329f903c51`;
- artifact `11041040671`.

Rendered Mara Custom-v2 Atributo confirms the 50800 defect still exists:

### Base Notes page 4

- existing native Notes source page is present;
- two ruled text columns are available;
- content is rendered as one continuous text stream;
- `Nota 1`, `Nota 2`, etc. are inline regular text rather than strong record identities;
- there is no blank ruled row between note cards.

### Current overflow Notes page 19

- existing native Notes source page is reused visually;
- the page begins with the anonymous tail of the previous note;
- `Nota 9` appears later with no strong record boundary;
- personality/flaw/faith/subclass records follow;
- only the left text column is used while the entire right Notes column is empty.

Therefore green CI and use of the source Notes page are insufficient: M50800-22/23/24 remain visibly failed.

## 4. Existing native/source reference

The approved Custom-v2 Notes page already provides the required physical grammar:

- source page 5 / imported form index 4;
- heading `NOTAS`;
- two ruled writing columns;
- 20 native rows in the left column;
- 20 native rows in the right column;
- 17 pt row cadence;
- source-native page/grid structure.

Authority in code:

- `DesktopCustomV2SharedBaseRenderer.NOTES_LEFT`;
- `DesktopCustomV2SharedBaseRenderer.NOTES_RIGHT`;
- current Extended `resources.forms[4]`.

The repair must preserve this sheet/module and change semantic packing, not invent a new Notes page.

## 5. Current implementation mismatch

### Base renderer

`DesktopCustomV2SharedBaseRenderer.renderNotes`:

1. flattens all note semantics into `notesText(plan)`;
2. splits the result into words;
3. fills the left column;
4. consumes a word count;
5. continues into the right column.

Once flattened, note identity and record boundaries no longer exist.

### Extended renderer

`DesktopCustomV2ExtendedRenderer.appendNotesExtendedPages`:

1. flattens all Notes content again;
2. wraps by approximate character count;
3. drops the first 40 lines;
4. renders the remainder as anonymous lines.

This cannot reliably identify which note crosses the base/continuation boundary and therefore cannot satisfy M50800-23.

## 6. Intended repair

Create one shared Custom-v2 native Notes semantic/packing path used by both base and Extended renderers.

### Semantic records

Preserve independent records for:

- general Notes prose;
- each `CharacterNote`, in `sortOrder`;
- background personality;
- flaws;
- faith/religion;
- subclass-derived note material.

For note cards, preserve the existing title exactly (for Mara, `Nota N — <title>`) as the record identity.

### Native row packing

- wrap against the actual native Notes column width;
- render record identity in semibold/bold native-compatible typography;
- render body on following native ruled rows;
- insert at least one blank ruled row between records when physical capacity allows;
- fill left column first, then right column, then a new Notes source page;
- if a record crosses a native column/page boundary, repeat its identity as `<identity> (continuación)` (or equivalent) before its continued body;
- do not create a new page while unused native Notes capacity remains.

Base and Extended must derive from the **same physical row sequence**:

- base renders rows 1–40 into its native two columns;
- Extended renders only rows after the base 40, again 40 rows per copied native Notes page.

This removes approximate-character drift between base and continuation.

## 7. Planned regressions

For both Mara Custom-v2 families:

- all 9 note-card titles remain present in the full PDF;
- note cards remain ordered by `sortOrder`;
- base/continuation output uses native `NOTAS` source pages;
- at least one real Mara cross-boundary note continuation retains `Nota N` identity with an explicit continuation marker;
- no anonymous tail may begin an Extended Notes stream;
- the continuation consumes native column capacity in left-to-right order before another Notes page;
- background-derived note records remain present;
- Desktop/Android generated renderer sync remains exact.

Rendered artifact inspection remains mandatory for M50800-21..24; PDF text assertions are supporting evidence only.

## 8. Artifact gate

After implementation:

1. CI green;
2. inspect the exact `pc-sheet-populated-template-proofs` artifact;
3. inspect real Mara base Notes page and every Notes continuation page in both Custom-v2 families;
4. verify title emphasis, row separation, continuation identity and two-column packing against the original 50800 observations;
5. record M50800-21/22/23/24 only as internal progression candidates if the exact artifact passes;
6. keep master-matrix statuses formally OPEN until final exact-candidate acceptance.

Any lost note title/body, anonymous continuation, record collision, unused earlier column with a later page, or generic non-native Notes reconstruction blocks progression.

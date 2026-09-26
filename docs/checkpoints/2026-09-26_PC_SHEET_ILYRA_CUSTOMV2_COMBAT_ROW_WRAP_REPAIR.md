# Checkpoint — Ilyra Custom-v2 combat continuation row-wrap repair

**Date:** 2026-09-26 (Chile local time)  
**Base main:** `9fa65392c8e24ec0fa842569d376e9b0ca46b0c1`  
**Active branch:** `fix/pc-sheet-ilyra-customv2-combat-row-wrap`  
**Status:** OWNER RUNTIME DEFECT REPRODUCED / GENERALIZED MULTI-ROW COMBAT CONTINUATION REPAIR ACTIVE

## Prior completed evidence

- Aldren Share: OWNER PASS.
- Ilyra Fantasy/DOTES repair: OWNER PASS on `0.5.0-preqa.6` / build `50600`.
- The prior Fantasy overflow is gone; generated PDF opens/readable and preserves full Ability Score Improvement semantics.

## New owner runtime defect

During the original staged Ilyra Step 2, with unsaved character changes present and **Custom v2 · Atributo** selected, the app **did reach the unsaved-change confirmation**. The owner explicitly chose **`Exportar sin guardar`**. Rendering then failed immediately afterward, before a PDF could be produced and before the draft-vs-persisted-data assertions could be completed.

Observed diagnostic after choosing `Exportar sin guardar`:

`Custom-v2 combat cell requires excessive compression: 'Cantrip; Potent Cantrip de Evoker puede producir daño parcial incluso al fallar según SRD 5.2.1.' (49.974995%)`

The screenshot showed Spellbook OFF on the export screen. The owner clarification supersedes the earlier sequence interpretation: the **`Exportar sin guardar` confirmation path itself was successfully reached and selected**; the blocker occurs after that choice while the shared Custom-v2 Extended combat continuation is rendered.

Therefore the staged Step-2 evidence is now:
- unsaved changes detected — PASS;
- `Exportar sin guardar` option presented — PASS;
- owner selected `Exportar sin guardar` — PASS;
- PDF generation after that selection — BLOCKED by combat-cell compression;
- PDF reflects unsaved draft — NOT YET VERIFIED;
- persisted Ilyra data remains unchanged — NOT YET VERIFIED.

Spellbook still needs to be ON for the final rerun because that remains part of the original staged Step-2 acceptance scope.

## Root cause

Custom-v2 Extended combat continuation currently allocates one physical table row per logical combat reference entry. Each cell is forced onto one line, with font-size reduction down to 6 pt and horizontal scaling allowed only to the 72% readability floor.

Ilyra's Fire Bolt note is semantically legitimate but requires about 49.97% horizontal compression if kept in one physical notes cell. The fail-closed guard correctly rejects that unreadable output.

## Generalized repair rule

Do **not** special-case Ilyra, Potent Cantrip, or the failing string. Do **not** lower the 72% readability floor.

Instead:

1. preserve the approved Custom-v2 Extended combat table geometry;
2. preserve the existing 6 pt minimum body size and 72% horizontal-scale floor;
3. wrap each logical combat row by actual font width at the readable floor;
4. allow one logical combat entry to consume multiple physical table rows;
5. distribute name/range/bonus/effect/notes cell lines across those physical rows;
6. page-count after expansion, so overflow becomes additional table rows/pages rather than unreadable compression;
7. retain the existing final fail-closed compression guard;
8. apply identically to Desktop and Android;
9. regression-test the real Ilyra fixture for **both** Custom-v2 per Attribute and per Ability, with Spellbook descriptions enabled.

## Current implementation

The active branch now:

- expands `combatReferenceRows(plan)` into physically wrapped rows before pagination;
- uses the existing font metrics and `wrapByWidth` helper at the 6 pt / 72% readability boundary;
- leaves `combatCellText` and its fail-closed guard intact;
- keeps each Special Equipment row limited to compact operational facts;
- routes full special-item description/notes into the existing wrapped equipment-continuation line area rather than crushing them into one special row;
- includes a real Ilyra regression for **both** Custom-v2 variants, with Spellbook descriptions enabled, requiring Fire Bolt / Potent Cantrip semantics, SRD 5.2.1 text, Memorize Spell, and the full special Spellbook description/notes to survive rendering.

Corrected branch validation is now green: Scaffold **#3887** / run `36268568144` — **SUCCESS**. Backend, hosted database, Android renderer-sync/delivery guards, full Kotlin tests, and the strengthened real-Ilyra Custom-v2 regression all passed.

## CI progression — second latent blocker exposed

Scaffold **#3880** reached the full Kotlin/rendering test gate after all fast guards passed.

The new real-Ilyra Custom-v2 regression no longer failed on the Potent Cantrip combat-cell compression guard. That confirms the multi-row combat expansion moved rendering past the owner-observed blocker.

The same end-to-end regression then failed later in the Custom-v2 inventory continuation with:

`Text does not fit: Peso 3 lb · Libro de 100 páginas; contiene una selección legal de conjuros de Ilyra hasta nivel 5, incluidos los añadidos por Evocation Savant. · Contiene la selección legal de conjuros de Ilyra hasta nivel 5.`

This is Ilyra's **special Spellbook inventory item**. The current Special Equipment continuation gives each special item only one physical `DESCRIPCIÓN / ESTADO` row and attempts to render weight + full description + notes into that row.

Therefore the staged blocker chain is now:

1. owner runtime: `Exportar sin guardar` selected successfully;
2. owner runtime: Potent Cantrip combat note blocked rendering;
3. branch repair: combat reference now expands across readable physical rows;
4. CI: rendering advances beyond combat;
5. CI: special Spellbook detail then exposes a second single-row overflow;
6. PDF-draft projection and persisted-data-unchanged assertions remain pending until the whole Ilyra export completes.

### Inventory repair direction

Do not shrink or horizontally crush the full Spellbook description into one Special Equipment row.

Keep the Special Equipment row for compact operational facts (for example weight / attunement / usage state), and route the full descriptive metadata into the existing inventory continuation line area, wrapped at its native column width. Preserve all description/notes text; do not special-case the word `Spellbook`.

The existing fail-closed one-line guards remain active for their bounded cells.


## Green branch acceptance — #3887

Scaffold **#3887** / run `36268568144` completed **SUCCESS** after both bounded-data repairs were present together.

The real Ilyra regression passed for both Custom-v2 variants with Spellbook descriptions enabled and requires preservation of:

- Fire Bolt;
- Potent Cantrip semantics;
- SRD 5.2.1 text;
- Memorize Spell;
- `Libro de 100 páginas`;
- `incluidos los añadidos por Evocation Savant`;
- Ilyra's full Spellbook notes.

No readability floor was lowered. The combat fail-closed compression guard remains active, and Special Equipment descriptive metadata remains preserved through the existing inventory continuation semantic destination.

A distinguishable owner candidate is now stamped as **`0.5.0-preqa.7` / build `50700`** and requires its own green Scaffold run before PR/integration.

## Owner boundary

Do not retry Ilyra on the current `preqa.6` APK. The next candidate is `0.5.0-preqa.7` / build `50700`, but it is not owner-ready until versioned branch, PR-head, and merged-main validation are green. Keep the harmless unsaved edit discarded/not persisted.

No owner action is required until a new distinguishable QA candidate passes branch, PR-head, and merged-main Scaffold validation.

After that, resume the exact Ilyra staged Step 2:
- Custom v2 · Atributo;
- Spellbook ON;
- harmless unsaved edit;
- export;
- choose `Exportar sin guardar` again;
- require PDF generation to complete without the combat compression diagnostic;
- verify the generated PDF reflects the unsaved draft;
- reload Ilyra and verify persisted data did not change.

Do not repeat the already-established fact that the confirmation path exists merely as a separate test objective; the rerun is required only because successful post-confirmation rendering and persistence semantics are still unproven.

Mara, Current Snapshot and Media / Handouts remain pending.

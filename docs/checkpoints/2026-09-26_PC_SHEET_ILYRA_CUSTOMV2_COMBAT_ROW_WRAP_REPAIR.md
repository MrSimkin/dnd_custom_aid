# Checkpoint — Ilyra Custom-v2 combat continuation row-wrap repair

**Date:** 2026-09-26 (Chile local time)  
**Base main:** `9fa65392c8e24ec0fa842569d376e9b0ca46b0c1`  
**Active branch:** none — PR #106 merged; `main` is authoritative  
**Status:** REPAIR MERGED / MERGED-MAIN GREEN / OWNER PREQA.7 STEP-2 COMPLETION READY

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

## Green branch validation

Corrected end-to-end repair validation:

- Scaffold **#3887** / run `36268568144` — **SUCCESS**;
- canonical resume-route guard — PASS;
- Android renderer-sync guard — PASS;
- Android PDF-delivery guard — PASS;
- full Kotlin build/tests — PASS;
- real Ilyra Custom-v2 regression — PASS for **per Attribute** and **per Ability**;
- Spellbook descriptions enabled in regression;
- Fire Bolt / Potent Cantrip / SRD 5.2.1 semantics preserved;
- Memorize Spell preserved;
- full special Spellbook description/notes preserved through inventory continuation routing;
- Android debug APK artifact upload — PASS;
- PC-sheet render/proof artifact uploads — PASS;
- backend — PASS;
- hosted database — PASS.

The distinguishable owner candidate **`0.5.0-preqa.7` / build `50700`** passed versioned Scaffold **#3890**. PR **#106** — `fix: preserve Ilyra Custom-v2 continuation content` — is now the active implementation PR. Require PR-head and merged-main Scaffold success before owner installation.

## Integration closure — PR #106

Validation chain:

- combined corrected repair Scaffold **#3887** / run `36268568144` — SUCCESS;
- exact versioned **`0.5.0-preqa.7` / build `50700`** Scaffold **#3890** — SUCCESS;
- PR **#106** — `fix: preserve Ilyra Custom-v2 continuation content`;
- PR-head Scaffold **#3894** / run `36269391791` — SUCCESS;
- squash-merged to `main` as `fb1e831bdb3f9e53374cffafc24a9f34a2174454`;
- merged-main Scaffold **#3895** / run `36269693233` — **SUCCESS**;
- merged-main Android artifact: `dnd-custom-aid-debug-apk`, artifact id `10915645814`.

Repository/CI acceptance for both discovered blockers is closed:

1. long Custom-v2 combat reference content is expanded across readable physical rows without lowering the 6 pt / 72% readability floor;
2. long special-item description/notes are preserved through wrapped equipment-continuation lines instead of being forced into one Special Equipment row.

The real Ilyra regression passes in both Custom-v2 variants with Spellbook descriptions enabled and preserves Fire Bolt / Potent Cantrip / SRD 5.2.1 semantics, Memorize Spell, and the full special Spellbook description/notes.

The remaining boundary is owner runtime evidence for successful **post-`Exportar sin guardar`** rendering, draft projection into the PDF, and non-persistence back into Ilyra.

## Owner boundary

Exact next owner action on **`0.5.0-preqa.7` / build `50700`**:

1. install the merged-main preqa.7 APK **as an update**; do not uninstall the current app;
2. open **Ilyra Quill**;
3. make one harmless, unmistakable edit and **do not save the character**;
4. open **Hoja de personaje PDF**;
5. choose **Custom v2 · Atributo**;
6. turn **Spellbook ON**;
7. start the export;
8. when the unsaved-change confirmation appears, choose **`Exportar sin guardar`**;
9. require PDF generation to complete without the former combat or Spellbook inventory diagnostics;
10. open the generated PDF and verify the harmless unsaved edit is present;
11. return to/reload Ilyra and verify the harmless edit is **not** persisted in the character;
12. report PASS or the exact observed defect.

Already-proven evidence that must not be forgotten or unnecessarily retested as a separate objective:

- unsaved changes are detected — PASS;
- `Exportar sin guardar` is presented — PASS;
- owner can select `Exportar sin guardar` — PASS;
- the prior failure occurred **after** that selection.

The rerun is necessary because successful rendering after the choice, draft-in-PDF projection, and non-persistence were previously blocked and remain the acceptance boundary.

Mara, Current Snapshot and Media / Handouts remain pending.

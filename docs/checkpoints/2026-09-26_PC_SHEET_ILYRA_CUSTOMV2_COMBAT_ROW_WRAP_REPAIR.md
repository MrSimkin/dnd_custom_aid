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

During the original staged Ilyra Step 2, with unsaved character changes present and **Custom v2 · Atributo** selected, PDF export failed before reaching the intended `Exportar sin guardar` prompt.

Observed diagnostic:

`Custom-v2 combat cell requires excessive compression: 'Cantrip; Potent Cantrip de Evoker puede producir daño parcial incluso al fallar según SRD 5.2.1.' (49.974995%)`

The screenshot also showed Spellbook OFF at the moment of failure. This does not invalidate the defect: the renderer already fails in the shared Custom-v2 Extended combat continuation before the intended unsaved-export persistence behavior can be tested.

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
- includes a real Ilyra regression requiring Fire Bolt, Potent Cantrip semantics, SRD 5.2.1 text, and Memorize Spell to survive rendering in both Custom-v2 variants.

Scaffold validation is pending.

## Owner boundary

Do not retry Ilyra on the current `preqa.6` APK. Keep the harmless unsaved edit discarded/not persisted.

No owner action is required until a new distinguishable QA candidate passes branch, PR-head, and merged-main Scaffold validation.

After that, resume at the exact Ilyra staged Step 2:
- Custom v2 · Atributo;
- Spellbook ON;
- harmless unsaved edit;
- export and choose `Exportar sin guardar`;
- verify PDF sees the draft;
- reload Ilyra and verify persisted data did not change.

Mara, Current Snapshot and Media / Handouts remain pending.

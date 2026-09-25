# Checkpoint — PC Sheet cross-family runtime correctness repair WIP

**Date:** 2026-09-24 (Chile local time)  
**Base main:** `3453b2dac88644fd26ccb8b1c6ce47c634d99cef`  
**Active branch:** `fix/pc-sheet-cross-family-runtime-correctness`  
**Branch HEAD at consolidation:** `f4ffe283ecf382f57461ac47d2c09206dad1d82a`  
**Status:** IMPLEMENTATION IN PROGRESS / CONNECTION-INTERRUPTION RECOVERY CHECKPOINT / DO NOT RESUME MANUAL QA YET

## 1. Why this checkpoint exists

The owner explicitly requested durable consolidation because the chat/session is close to its limit and the connection interrupted active implementation.

This checkpoint supersedes any assumption that the repair package is still only at design stage. Work has started and several shared causes are already patched on the active branch.

Fresh sessions must resume:

`RESUME.md -> docs/checkpoints/LATEST.md -> this checkpoint`

Then continue from **Section 6 — Exact next implementation sequence**.

## 2. Authority / QA evidence

The complete owner/runtime defect evidence remains:

`docs/checkpoints/2026-09-24_PC_SHEET_ALDREN_CROSS_FAMILY_PDF_REVIEW.md`

That checkpoint established:

- Save/open PASS across all four visual families on Android;
- shared mojibake / Unicode corruption;
- combat/action/structured damage incorrectly routed through Traits/reference overflow;
- equipment/inventory replay and currency misrouting;
- unnecessary spell and Notes pages;
- excessive page amplification / poor packing;
- one-use resource presentation defects in Fantasy and Custom-v2;
- Custom-v2 Equipo Especial / Ubicación typography defect.

Do not discard or narrow that evidence.

## 3. Findings established during implementation investigation

### 3.1 Fixture Unicode is correct

The canonical Aldren fixture contains correct Unicode, including:

- `Común`;
- `Élfico`;
- `Acólito`;
- `acción`;
- `versátil`;
- en/em dash characters.

Static accented labels drawn by the Android PDF renderer are also known to render correctly.

Therefore the visible `ComÃºn` / `Ã‰lfico` / `â€“` corruption is **not sourced by the fixture and is not simply a missing PDF glyph/font problem**.

Working conclusion: the runtime character strings are already corrupted before PDF drawing, most likely on the hosted QA snapshot / sync / persistence path. This still requires an explicit data-integrity guard/fix before the package closes.

### 3.2 Blank spell/Notes pages are planner-level

`PcSheetPdfExportPlanner.basePages(...)` historically returned spell and Notes base pages unconditionally.

The active branch now computes content-aware active base pages:

- suppress Spell List when there is no spellcasting content;
- suppress dedicated Notes page when short notes can be packed into an existing compatible narrative page.

### 3.3 Currency misrouting included a concrete key mismatch

The character model/fixture uses canonical currency keys:

- `cp`;
- `sp`;
- `ep`;
- `gp`;
- `pp`.

Extended renderers were filtering against Spanish-style keys such as `pc`, `po`, `pe`, so normal currencies were falsely treated as non-base/custom treasure and pushed into extension inventory surfaces.

### 3.4 Combat/damage contamination of Traits is explicit code

Both Custom extended renderers injected:

- combat entries;
- action/attack references;
- structured damage profiles

inside `traitSupplementLines(...)`.

The active branch removes those blocks and introduces a dedicated semantic `COMBAT_AND_ACTIONS` continuation route plus family-specific combat/action continuation pages.

## 4. Landed repair commits on active branch

The following changes are already remote and must not be reimplemented from scratch:

1. `204559ba48ba78d7df421b21467f5bac1b03584c`
   - content-aware PDF base-page selection;
   - Spell page suppressed when character has no spell content;
   - dedicated Notes page suppressed when notes fit an existing compatible narrative surface.

2. `d49f7aac4df9373a2760bb44c4dc05ea61480765`
   - Custom-v2 packs short notes into the existing narrative/story area.

3. `1ed3b4905ad0359f9f9a95d37d0b704b3cbc3386`
   - Fantasy renderer honors the planner and does not draw a spell page when `SPELL_LIST` is absent.

4. `b39500538b5b41755b4e33e7eac5c7c196dbd348`
   - Custom-v1 standard currency key set corrected to `cp/sp/ep/gp/pp`.

5. `016f401965cd409445da5123b05f9763d9dd3e88`
   - Custom-v2 currency-key correction was initially committed here, **but later Custom-v2 editing overwrote it**. Reapply it; do not assume it remains present.

6. `0ba30866e16bf1cb4a8cadab9750e9e538ccec2d`
   - explicit semantic `COMBAT_AND_ACTIONS` content/extension route added to shared PDF foundation.

7. `931932bd32e9f5bd3631118b519d227b4e6dc49c`
   - Custom-v2 removes combat/action + structured damage from Traits supplements;
   - adds dedicated `COMBATE / ACCIONES` continuation rendering preserving full detail.

8. `f4ffe283ecf382f57461ac47d2c09206dad1d82a`
   - Custom-v1 removes combat/action + structured damage from Traits supplements;
   - adds dedicated `Combate / Acciones` continuation rendering preserving full detail.

## 5. Important incomplete/unsafe state

The branch is **not yet QA-ready**.

Known incomplete items:

1. Reapply Custom-v2 currency key set to `setOf("cp", "sp", "ep", "gp", "pp")`; current HEAD lost that earlier edit during subsequent file replacement.
2. Fantasy still routes combat/action references through its Traits continuation implementation. It needs the same semantic split.
3. Inventory continuation policy still promotes too much detail merely because items have notes/descriptions; must distinguish:
   - already represented on base;
   - extra detail that genuinely needs continuation;
   - true overflow.
4. One-use resource semantics remain:
   - Custom-v1 acceptable;
   - Custom-v2 filled-square semantics unacceptable;
   - Fantasy numeric `1 / 1` + continuation-row behavior unacceptable.
5. Custom-v2 base Equipo Especial `Ubicación` typography mismatch remains.
6. Shared pagination/packing remains only partially addressed.
7. Unicode/hosted data-integrity root cause remains unresolved.
8. Desktop-authority -> Android generated renderer sync has **not yet been run** for this branch.
9. Tests/CI have **not yet validated** the current branch.
10. Version has not yet been bumped and no replacement APK has been built.
11. Do not resume Share/Ilyra/Mara/Current Snapshot or physical-device QA.

## 6. Exact next implementation sequence

Resume in this order:

1. **Repair the branch inconsistency first**
   - reapply Custom-v2 canonical currency keys;
   - compile/check shared enum/route edits for exhaustive `when` fallout.

2. **Fantasy combat semantic split**
   - remove combat/action/structured-damage references from Traits;
   - add dedicated Fantasy `COMBATE / ACCIONES` continuation page;
   - preserve full detail and bounded overflow guard.

3. **Resource semantics**
   - preserve Custom-v1 circle behavior;
   - Custom-v2: replace unexplained filled-square-only representation with explicit available/spent one-use semantics;
   - Fantasy: replace bare numeric `1 / 1` for one-use resources with legible binary markers and prevent recovery-detail wrapping from creating absurd continuation rows/pages.

4. **Inventory/currency**
   - base rows must consume ordinary item identity/details already representable there;
   - extension pages only for true overflow/additional detail;
   - no standard currency in inventory extension;
   - avoid replaying whole inventory sets.

5. **Custom-v2 typography**
   - fix base Equipo Especial Location value font/size using application fill typography, not the oversized source-label styling.

6. **Content-aware packing**
   - fill compatible capacity before allocating additional pages;
   - do not hard-code a desired page count;
   - retain complete semantic detail.

7. **Unicode runtime data integrity**
   - trace hosted fixture -> hosted JSON/snapshot -> local apply -> PDF plan;
   - assert representative exact strings before rendering;
   - fix the earliest corruption boundary;
   - no mojibake string-replacement hacks in renderer code.

8. **Regression and generated Android parity**
   - add/update cross-family Aldren tests;
   - ensure exact Unicode survives;
   - ensure no combat under Traits;
   - ensure standard currencies do not enter inventory continuation;
   - ensure non-spellcaster has no blank spell page;
   - ensure one-use resources are semantically legible;
   - ensure bounded-overflow safety still runs;
   - regenerate Android renderer from Desktop authority and run renderer-sync guard.

9. **Lifecycle**
   - run tests/Scaffold;
   - inspect generated PDFs programmatically and visually where possible;
   - update version to next QA build only after repair is coherent;
   - merge via PR only after green CI;
   - produce clearly named APK;
   - resume Aldren cross-family owner QA before later fixtures.

## 7. Manual QA boundary

Do not ask the owner for more PDF inspection yet.

The next owner action should occur only after:

- implementation is coherent;
- tests/CI pass;
- Android generated renderer is synchronized;
- a new distinguishable QA APK is available.

Then rerun Aldren across all four formats first.

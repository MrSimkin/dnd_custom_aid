# Checkpoint — Ilyra Fantasy Sheet base-feat bounded overflow repair

**Date:** 2026-09-26 (Chile local time)  
**Base main:** `63a56a5de91b7d77c301918331a26d32f79c5f60`  
**Active branch:** `fix/pc-sheet-ilyra-fantasy-trait-pagination`  
**Status:** OWNER RUNTIME DEFECT REPRODUCED / BASE-FEAT BOUNDED PREVIEW ROUTING REPAIR ACTIVE

## Owner runtime evidence

The staged Android runtime smoke had completed **Aldren Share = PASS**. On the next fixture, **Ilyra Quill**, the owner reached the PDF export surface with an unsaved draft change present. Before the intended Custom-v2 + Spellbook persistence check was completed, Fantasy Sheet generation exposed a fail-closed bounded-routing diagnostic:

`Fantasy Sheet production encountered content outside its bounded base/continuation routing: ruled-area value='puntuaciones finales INT 18 y DES 14.' overflow='14'.`

The text comes from the real integrated Ilyra fixture's **Ability Score Improvement** trait:

`+1 Inteligencia y +1 Destreza; puntuaciones finales INT 18 y DES 14.`

This is a genuine renderer defect, not a user-data or fixture reconstruction error.

## Root cause

The real-fixture regression established that the failing text is rendered in Fantasy Sheet's **base-page DOTES** ruled box. The first two repair attempts targeted continuation/reference pagination and therefore did not address the failing surface.

Ilyra's `Ability Score Improvement` feat is projected through `classicBaseTraitSummary` into a bounded 136 pt-wide / 70 pt-high ruled area. The existing character-count preview heuristic can still produce a logical preview that needs one more physical row at the frozen 8.2 pt PDF font. The final `14.` therefore reaches the fail-closed overflow guard.

This is the same generalized bounded-field class already established by earlier runtime QA: a bounded base field must use a physically safe compact preview, while the complete semantic text survives in an appropriate continuation/detail destination.

## Repair direction

Do **not** special-case Ilyra or truncate away canonical data.

The next implementation must:

1. constrain the Fantasy base **DOTES** preview by actual physical rendered-row capacity;
2. ensure a feat clipped for the base preview is promoted to the existing Traits/Features continuation path so its complete description survives;
3. preserve the frozen base-page geometry and typography;
4. retain the final fail-closed overflow guard;
5. cover Desktop and Android consistently;
6. keep the real Ilyra fixture regression requiring the full `Ability Score Improvement` semantics and `Memorize Spell` to survive the complete PDF;
7. prove no regression in existing Fantasy pagination/content tests.

Two branch Scaffold attempts are historical failed diagnostics:
- #3840: Ilyra regression still reproduced the exact overflow;
- #3845: Ilyra still overflowed and two unrelated renderer tests regressed, proving the reference-panel approach was wrong/too invasive.

No APK from those failed runs is valid for owner QA.

## Branch validation evidence

Corrected minimal repair validation:

- Scaffold **#3854** / run `36265067594` — **SUCCESS**;
- canonical resume-route guard — PASS;
- Android renderer-sync guard — PASS;
- Android PDF-delivery guard — PASS;
- full Kotlin build/tests — PASS;
- real Ilyra fixture regression — PASS;
- Android debug APK artifact upload — PASS;
- PC-sheet render/proof artifact uploads — PASS;
- backend — PASS;
- hosted database — PASS.

The green repair keeps the change narrow: the Fantasy base-page **DOTES** preview consumes at most its physically available ruled rows, and any clipped feat is explicitly promoted to the existing full-detail traits continuation. The failed reference/measurement experiments were removed before this successful run.

The distinguishable owner candidate **`0.5.0-preqa.6` / build `50600`** passed Scaffold #3856. PR **#105** is now the active implementation PR; require PR-head and merged-main Scaffold success before owner installation.

## Manual boundary

Do not continue the Ilyra Custom-v2 + Spellbook unsaved-export persistence smoke on the currently installed preqa.5 APK. The minimal repair is repository-green; next require the stamped preqa.6 / build 50600 candidate to pass Scaffold, integrate through PR, then install that APK as an update without uninstalling local state. Resume at Ilyra only after that.

Mara, Current Snapshot and Media/Handouts remain pending.

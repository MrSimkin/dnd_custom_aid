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

## Manual boundary

Do not continue the Ilyra Custom-v2 + Spellbook unsaved-export persistence smoke on the current APK. First require green repository validation, publish a distinguishable Android QA candidate, and install it as an update without uninstalling local state. Then resume at Ilyra.

Mara, Current Snapshot and Media/Handouts remain pending.

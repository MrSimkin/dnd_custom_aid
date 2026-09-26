# Checkpoint — Ilyra Fantasy Sheet trait continuation overflow repair

**Date:** 2026-09-26 (Chile local time)  
**Base main:** `63a56a5de91b7d77c301918331a26d32f79c5f60`  
**Active branch:** `fix/pc-sheet-ilyra-fantasy-trait-pagination`  
**Status:** OWNER RUNTIME DEFECT REPRODUCED / GENERALIZED PHYSICAL-ROW PAGINATION REPAIR ACTIVE

## Owner runtime evidence

The staged Android runtime smoke had completed **Aldren Share = PASS**. On the next fixture, **Ilyra Quill**, the owner reached the PDF export surface with an unsaved draft change present. Before the intended Custom-v2 + Spellbook persistence check was completed, Fantasy Sheet generation exposed a fail-closed bounded-routing diagnostic:

`Fantasy Sheet production encountered content outside its bounded base/continuation routing: ruled-area value='puntuaciones finales INT 18 y DES 14.' overflow='14'.`

The text comes from the real integrated Ilyra fixture's **Ability Score Improvement** trait:

`+1 Inteligencia y +1 Destreza; puntuaciones finales INT 18 y DES 14.`

This is a genuine renderer defect, not a user-data or fixture reconstruction error.

## Root cause

Fantasy trait/reference continuation pagination allocated pages primarily by record count. Individual description slices can still require more than one physical rendered row at the frozen font metrics and continuation width. Ilyra hits that edge case at the end of a continuation column, leaving the final `14` without physical-row capacity.

The final overflow guard correctly stopped production rather than silently clipping data.

## Generalized repair

Do **not** special-case Ilyra or the failing string and do **not** truncate semantic content.

The repair on the active branch:

1. preserves the frozen Fantasy typography and existing trait text slicing;
2. adds non-drawing font-metric text measurement to the Desktop and Android PDF primitives;
3. paginates Fantasy trait/reference records by measured physical-row requirement while preserving the established maximum of five records per continuation column;
4. keeps the fail-closed overflow guard and additionally prevents a whole record from being silently skipped when no physical row remains;
5. applies the same behavior to Desktop and Android;
6. adds the real Ilyra fixture as a Fantasy renderer regression and requires the full Ability Score Improvement text plus Memorize Spell to survive PDF extraction.

## Manual boundary

Do not continue the Ilyra Custom-v2 + Spellbook unsaved-export persistence smoke on the current APK. First require green repository validation, publish a distinguishable Android QA candidate, and install it as an update without uninstalling local state. Then resume at Ilyra.

Mara, Current Snapshot and Media/Handouts remain pending.

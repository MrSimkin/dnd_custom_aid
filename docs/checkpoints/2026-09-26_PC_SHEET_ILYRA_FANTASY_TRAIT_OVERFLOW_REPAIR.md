# Checkpoint — Ilyra Fantasy Sheet reference-row overflow repair

**Date:** 2026-09-26 (Chile local time)  
**Base main:** `63a56a5de91b7d77c301918331a26d32f79c5f60`  
**Active branch:** `fix/pc-sheet-ilyra-fantasy-trait-pagination`  
**Status:** OWNER RUNTIME DEFECT REPRODUCED / FONT-METRIC REFERENCE-ROW WRAPPING REPAIR ACTIVE

## Owner runtime evidence

The staged Android runtime smoke had completed **Aldren Share = PASS**. On the next fixture, **Ilyra Quill**, the owner reached the PDF export surface with an unsaved draft change present. Before the intended Custom-v2 + Spellbook persistence check was completed, Fantasy Sheet generation exposed a fail-closed bounded-routing diagnostic:

`Fantasy Sheet production encountered content outside its bounded base/continuation routing: ruled-area value='puntuaciones finales INT 18 y DES 14.' overflow='14'.`

The text comes from the real integrated Ilyra fixture's **Ability Score Improvement** trait:

`+1 Inteligencia y +1 Destreza; puntuaciones finales INT 18 y DES 14.`

This is a genuine renderer defect, not a user-data or fixture reconstruction error.

## Root cause

The failing content is duplicated into Fantasy Sheet's **REFERENCIAS Y RECORDATORIOS** area. That surface pre-wrapped reference text with an approximate character-count heuristic before passing each logical line to a fixed-width ruled row. At the frozen 8.1 pt font and 166 pt reference width, the logical line ending in `INT 18 y DES 14.` physically requires an extra rendered row. The approximate wrapper therefore under-counted the page's physical row demand and the final `14.` reached the fail-closed overflow guard.

The guard correctly stopped production rather than silently clipping data.

## Generalized repair

Do **not** special-case Ilyra or the failing string and do **not** truncate semantic content.

The repair on the active branch:

1. preserves the frozen Fantasy typography and reference-panel geometry;
2. adds a non-drawing font-metric measurement operation to Desktop and Android PDF primitives;
3. wraps each reference value into **actual physical ruled rows** using the same font, width, padding, line height and one-row constraint used by final rendering;
4. performs the existing record-aware page padding only after those exact physical rows are known;
5. keeps the final fail-closed overflow guard;
6. applies the same behavior to Desktop and Android;
7. keeps the real Ilyra fixture regression and requires the full Ability Score Improvement text plus Memorize Spell to survive PDF extraction.

The earlier experimental trait-column pagination change was removed after the regression proved that it targeted the wrong surface.

## Manual boundary

Do not continue the Ilyra Custom-v2 + Spellbook unsaved-export persistence smoke on the current APK. First require green repository validation, publish a distinguishable Android QA candidate, and install it as an update without uninstalling local state. Then resume at Ilyra.

Mara, Current Snapshot and Media/Handouts remain pending.

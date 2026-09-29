# Mara 50800 — Phase 2B.2 native Notes progression closure

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Validated renderer/code head:** `5c5759a9699316bad27d4d032f31f082215045fe`  
**Workflow:** 4244 / `36594894685` — SUCCESS  
**Exact proof artifact:** `pc-sheet-populated-template-proofs` / artifact ID `11045852152`  
**Artifact digest:** `sha256:217fec8c85b36e240fd3a7358c9294c93b4f0d9f8df00a3bb41b5eab2597f084`  
**Status:** PHASE 2B.2 INTERNAL PROGRESSION PASS / MASTER MATRIX REMAINS OPEN  
**Next:** Phase 2B.3 — residual Equipment readability/reclaim / M50800-17, M50800-20

## Authority retained

This closure is subordinate to the real owner QA from `0.5.0-preqa.8 / 50800` and the 32-item acceptance matrix.

Phase 2B.2 was restricted to:

- M50800-21 — native/source Notes module reuse;
- M50800-22 — visible note-record boundaries;
- M50800-23 — continuation retains owning note identity;
- M50800-24 — native Notes rows/columns are consumed before another page.

No residual Equipment, Combat, Resources, Fantasy or owner-candidate work is implicitly accepted here.

## Original 50800 defect rechecked

The owner 50800 output showed Notes as a flattened stream:

- note records ran into one another without useful boundaries;
- continuation could begin with an anonymous tail;
- note identity was not repeated when a note continued;
- a continuation could leave the second native Notes column unused;
- the owner clarification required reuse of the existing Notes sheet/module rather than a generic reconstruction.

The Phase 2B.2 pre-code map re-opened those observations before implementation.

## Implementation result

Base and Extended Custom-v2 Notes now share one semantic native-row stream.

- records retain independent identity before pagination;
- note-card identity uses the source title in `sortOrder`;
- identity rows use semibold native-compatible typography;
- completed records receive a visible ruled-row separation where capacity remains;
- if one record crosses a native column/page boundary, the next fragment repeats `<identity> (continuación)`;
- the physical allocator uses the source Notes grammar: 20 rows left + 20 rows right;
- Extended output reuses the existing Notes source form and consumes the row stream after the first 40 base rows.

Desktop and generated Android implementations remain synchronized.

## Run history

Initial implementation run 4233 failed two focused assertions. Artifact/logic review showed the assertions encoded accidental packing/text-extraction behavior rather than the owner requirement:

- real Mara no longer necessarily splits a note card exactly at the base/Extended boundary because improved packing can keep complete note records together;
- a historical general-Notes test assumed literal PDFTextStripper whitespace.

The repair did **not** relax M50800-23. A deterministic stressed real-Mara proof was added that lengthens Note 1 until it must cross native columns/pages and requires repeated continuation identity plus the final semantic tail.

Run 4244 then completed successfully on the exact renderer/code head.

## Exact artifact review — run 4244

Real Mara Custom-v2 Atributo and Habilidad each render 19 pages. Page count is observation only, never an acceptance target.

### Base Notes

Rendered page 4 demonstrates:

- the existing native `NOTAS` source page is preserved;
- both native writing columns are used;
- `Notas generales`, `Nota 1 — Hipótesis` through `Nota 5 — Ritual` are visibly distinguished in semibold;
- blank ruled-row separation is visible between records;
- body text remains associated with the correct identity.

### Real Mara overflow Notes

Rendered page 19 demonstrates:

- the same native `NOTAS` source grammar is reused;
- left column contains `Nota 6`, `Nota 7`, `Nota 8`;
- right column contains `Nota 9`, `Rasgos de personalidad`, `Defectos`, `Fe / religión`, `Subclase`;
- both columns are consumed before another page;
- no anonymous note tail begins the page;
- all nine note-card identities remain present across the complete PDF.

### Forced cross-boundary proof

Exact artifact proof:

`mara-50800-phase2b2-cross-page-note.pdf`

Rendered pages 19–21 demonstrate:

- `Nota 1 — Hipótesis (continuación)` is repeated at each physical continuation boundary;
- both columns are consumed before page advance;
- the final stressed semantic tail through `Fragmento prolongado de nota 90` remains present;
- subsequent note records retain visible identity and separation;
- later note fragments such as `Nota 3 — Lugar (continuación)` and `Nota 6 — Hipótesis (continuación)` retain ownership when they cross a boundary.

## Phase disposition

Internal progression candidates with positive exact-artifact evidence:

- M50800-21;
- M50800-22;
- M50800-23;
- M50800-24.

These are **not final FIXED statuses**. They remain formally OPEN in the master matrix until the final exact promoted candidate is generated and the full acceptance ledger is re-run.

No owner APK is authorized yet. Do not merge.

## Next bounded work

Resume at **Phase 2B.3 — residual Equipment readability/reclaim**, primary items:

- M50800-17 — ordinary Equipment atomicity/readability;
- M50800-20 — eliminate redundant Equipment/Equipo Especial continuation scaffolds and reclaim capacity.

Do not reopen M50800-15/16/18/19 unless new exact-artifact evidence demonstrates a regression.

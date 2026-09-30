# Mara 50800 — Phase 5 pre-code cross-family acceptance map

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Status:** ACTIVE / BLOCKING PRE-CODE MAP FOR FINAL CROSS-FAMILY ACCEPTANCE  
**Acceptance authority:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`

## Exact source reviewed before this repair

Phase 5 begins from the exact successful branch head and its generated artifact, not from page-count expectations or prior phase claims.

- head: `22bb175d8bd9bbd12c067420d310a3b0a4fbb94d`
- Scaffold run: 4309 / `36623266391` — SUCCESS
- proof artifact: `pc-sheet-populated-template-proofs` / `11059618225`
- artifact digest: `sha256:1e63f3c9bdbcb1f6c97774d157da0e105809e35fc25df14cbee09bb70154cec7`
- exact real-Mara outputs:
  - Fantasy: 37 pages
  - Custom v1: 40 pages
  - Custom v2 per Attribute: 16 pages
  - Custom v2 per Ability: 16 pages

Counts are observations only. They are not acceptance targets.

## QA finding that blocks candidate promotion

The exact Custom-v1 artifact still violates the owner-approved adaptive continuation requirement (M50800-27).

Pages 9–31 repeatedly reproduce the complete native Traits/Historia/Notas/Trasfondo scaffold while the surviving stream is predominantly trait-detail text. By page 12 the left-side class/race/feat/proficiency/language streams are empty while the page still reserves those regions. The page also begins with an anonymous tail (`espacio. · Reacción`) from the preceding trait record, so the current flat-line pagination can break semantic record boundaries.

This is the same defect class prohibited by the 50800 owner QA:

- exhausted sibling streams must stop reserving physical space;
- surviving content must reclaim available native page space;
- semantic records must retain owning identity across continuation;
- no final candidate is accepted merely because CI is green.

Therefore run 4309 is **CI PASS / PHASE-5 ARTIFACT QA BLOCKED**, not an owner candidate.

## Positive regression evidence retained

The post-Phase-4 Custom-v1 Notes repair at head `22bb175...` is positive and must remain protected:

- Custom v1 generates successfully;
- page 39 visibly uses both native Notes columns;
- `Nota 5 — Ritual (continuación)` and `Nota 7 — Contacto (continuación)` retain owning identity;
- `Nota 8 — Lugar` and `Nota 9 — Deuda` remain visible;
- page 40 begins with `Nota 9 — Deuda (continuación)` and preserves background/religion/subclass records;
- no Notes fix is reopened unless a later exact artifact regresses it.

## Bounded repair scope

Primary matrix item:

- **M50800-27** — Custom-v1 adaptive continuation reclaim + semantic boundary preservation.

Regression items:

- M50800-09/10 — trait ordering and packing;
- M50800-11 — narrative/source identity must remain intact;
- M50800-29 — all Mara semantic identities preserved;
- M50800-31 — exact-candidate matrix acceptance;
- M50800-32 — all-four-family generation.

## Implementation surface

Primary:

- `desktopApp/.../DesktopCustomV1ExtendedRenderer.kt`
  - `appendTraitsExtendedPages`
  - trait-detail continuation allocation
  - source-native `Detalles de Rasgos` continuation surface
- generated Android counterpart must remain byte-equivalent under the renderer generation contract.
- `DesktopPcSheetRuntimeQaFixtureTest.kt` gains final-Mara regression coverage for Custom-v1 reclaim and semantic boundaries.

## Intended behavior

1. While native left-side streams still contain content, keep the existing owner-approved source page grammar.
2. Once those sibling streams are exhausted, stop reproducing the empty class/race/feat/proficiency/language scaffold.
3. Continue surviving trait-detail records on a source-derived `Detalles de Rasgos` continuation that reuses the native heading/font/ruled-writing grammar and uses the reclaimed page area.
4. Pack complete logical trait records. A new physical page/column must not begin with an anonymous tail from the previous trait.
5. Preserve the original category/name ordering and all 26 Mara trait identities.
6. Do not change Fantasy or Custom-v2 layouts unless exact Phase-5 evidence shows a regression there.
7. Do not assign the next owner version/build until the repaired exact artifact passes the 32-item acceptance ledger.

## Evidence required before progression

- Scaffold SUCCESS at the exact repair head;
- all four real-Mara PDFs generated;
- Custom v1 no longer repeats the exhausted full Traits scaffold across detail-only pages;
- no Custom-v1 trait continuation page starts with an anonymous semantic tail;
- all 26 trait identities and downstream supplement records remain present;
- post-Phase-4 Notes evidence remains intact;
- Fantasy and both Custom-v2 outputs retain their prior internal PASS evidence;
- only then proceed to unique owner-candidate identity and final matrix disposition.


## Additional exact-artifact findings discovered during Phase 5

The same 4309 Custom-v1 PDF was inspected beyond Traits before any additional renderer change.

### Resources / Options

Rendered page 33 legitimately uses the split Resources + Options grammar while both streams contain content.

Rendered pages 34–37 retain an empty Options table after Options are exhausted while Resources continue. Page 36 begins with the anonymous tail `recuperación.`, and page 37 again begins with `recuperación.` before the next owning resource identity.

Disposition:

- **M50800-14 / M50800-27 remain blocking for Custom v1**;
- Resources must reclaim the exhausted Options area;
- logical Resource records must remain whole across page boundaries;
- a continuation page may not begin with an anonymous detail tail.

### Equipment / Equipo Especial

Rendered page 38 still contains useful ordinary Equipment identities (`Cuerda...` / `Llave...`) together with the final Equipo Especial record. Therefore the native combined Equipment page is still semantically justified in that exact artifact. No new Custom-v1 M50800-20 blocker is claimed from page 38.

### Sequencing rule

Do not implement the Resources/Options repair until the current Custom-v1 Traits reclaim iteration passes its own CI + exact-artifact gate. Phase 5 repairs remain discrete so a failure can be attributed to one bounded change.

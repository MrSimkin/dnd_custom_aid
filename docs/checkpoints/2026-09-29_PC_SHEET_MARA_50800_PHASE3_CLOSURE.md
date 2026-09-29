# Mara 50800 — Phase 3 Custom-v2 adaptive continuation progression closure

**Date:** 2026-09-29 (Chile local time)  
**Branch:** fix/pc-sheet-50800-phase1-native-semantics  
**Validated renderer/code head:** b9e77ff90799b2e4ec90e87952c565f1380f3d8e  
**Regression head:** 4a512497cec3fb63df9a03f8227af726de1c016c  
**Renderer workflow:** 4261 / 36602606829 — SUCCESS  
**Regression workflow:** 4262 / 36602640910 — SUCCESS  
**Exact proof artifact (final head):** pc-sheet-populated-template-proofs / artifact ID 11050167492  
**Artifact digest:** sha256:ccf00bd9414ba2a06aad9f5d17accb7172cf361b88560e48c899da44941f0187  
**Status:** PHASE 3 INTERNAL PROGRESSION PASS / MASTER MATRIX REMAINS OPEN  
**Next:** Phase 4 — Fantasy-specific continuation repair

## Authority retained

This closure remains subordinate to the owner QA from 0.5.0-preqa.8 / 50800 and the 32-item acceptance matrix.

Phase 3 was restricted to:

- M50800-12 — Custom-v2 Combat/Actions readability and packing;
- M50800-14 — Resources/Options reclaim after one stream is exhausted;
- M50800-27 — broader adaptive continuation behavior exercised by Combat and Resources/Options.

No Fantasy-specific item is implied closed here.

## Original 50800 defects rechecked

### Combat

The pre-Phase-3 artifact still showed table labels colliding with the first record, 42 pt bands allocated to every wrapped physical line, poor field association, and Mara's final combat entry stranded alone on a mostly-empty second page.

### Resources / Options

The pre-Phase-3 artifact still showed all Options exhausted after the first page while later pages continued Resources/Markers and reproduced a completely empty Options table.

## Implementation result

### M50800-12 — Combat/Actions

- the five-column semantic grammar remains intact;
- headers occupy a dedicated region above the body;
- body uses 17 pt physical rows rather than fixed 42 pt bands;
- wrapped rows belonging to one logical combat entry stay together;
- shading follows the logical entry;
- Mara's eight continuation combat records fit on one readable page;
- no combat field was flattened into prose.

### M50800-14 / M50800-27 — Resources/Options

- while both streams survive, the existing split Resources + Options grammar remains;
- once Options are exhausted, Resources reclaim the full page using the same Resources table grammar;
- Options-only continuation is supported without reserving Resources;
- no page is emitted for an exhausted sibling stream.

Artifact review of the first reclaim implementation found one semantic-boundary defect: a resource detail tail could begin the final Resources-only page before its owning Resource identity. That artifact was not accepted.

The follow-up repair pages Resources/Options by complete logical record groups. A record that cannot fit intact in the remaining rows moves to the next continuation page.

Desktop and generated Android renderers remain synchronized.

## Exact artifact review

### Combat — real Mara

Rendered Custom-v2 page 11 demonstrates:

- TIPO / NOMBRE, RANGO, BONIF., DAÑO / EFECTO and NOTAS headers are clearly separated from the first record;
- all eight Mara continuation combat entries appear on the same page;
- wrapped notes occupy adjacent 17 pt rows within their owning entry;
- field association remains columnar and readable;
- there is no second mostly-empty Combat page.

### Resources / Options — real Mara

Rendered page 12 retains the shared Resources + Options grammar while both streams survive.

Rendered pages 13–14 use Resources-only continuation:

- no OPCIONES scaffold is present;
- the full reclaimed area is available to Resources;
- Resource identities and wrapped metadata stay together;
- the final page begins with Reserva 9: Eco temporal, not an anonymous tail;
- Reserva 10: Sello remains complete and associated with its own recovery/origin rows.

The first reclaim artifact that began the final page with the anonymous tail 'nombre, valor y recuperación.' was rejected before closure; run 4261/4262 contains the corrected grouping.

## Regression evidence

The focused Mara regression requires:

- all combat identities preserved;
- exactly one Mara Custom-v2 continuation Combat page for this fixture because the content fits within the available physical rows;
- all 10 Resources and all 8 Options preserved;
- Resources-only pages after Options end;
- Resources-only pages contain no OPCIONES scaffold;
- a Resources-only page cannot begin with an anonymous semantic tail.

Both Custom-v2 Atributo and Habilidad pass.

## Phase disposition

Internal progression candidates with positive exact-artifact evidence:

- M50800-12;
- M50800-14;
- M50800-27 for the Custom-v2 streams exercised by this phase.

These remain formally OPEN in the master matrix until the final exact promoted candidate is generated and the complete acceptance ledger is re-run.

No owner APK is authorized yet. Do not merge.

## Next bounded work

Resume at Phase 4 — Fantasy-specific repair, primarily:

- M50800-13 — preserve Fantasy Combat/Actions semantic table grammar;
- M50800-26 — semantic names wrap instead of ellipsis;
- Fantasy-specific adaptive packing relevant to M50800-27.

Do not reopen completed Custom-v2 phases unless exact artifact evidence shows a regression.

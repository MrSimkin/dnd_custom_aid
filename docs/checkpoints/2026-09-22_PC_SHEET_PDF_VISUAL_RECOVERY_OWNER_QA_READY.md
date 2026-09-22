# Checkpoint — PC Sheet PDF visual recovery — OWNER QA READY

**Date:** 2026-09-22 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / **DO NOT MERGE**  
**Rejected predecessor:** `bf7d4d8f5324af7aabb5bf4d3d0038936af2b53d` / artifact `10713481125`  
**Final recovery implementation head:** `6ea493c9ee53cf727464a985bc6ef0b558a73e99`  
**Push Scaffold:** #3239 / `35786839063` — SUCCESS  
**PR Scaffold:** #3240 / `35786844300` — SUCCESS  
**Owner-review proof artifact:** `10720833172`  
**Artifact digest:** `sha256:6a466a5fbe3d0874a1609dde79227bb1d92fda704fd345087cc83657d4c98098`  
**Gate:** implementation + automated QA PASS; Worker visual preflight PASS; **OWNER VISUAL QA PENDING**

## Purpose

This checkpoint closes the bounded recovery implementation requested after the owner rejected the App Modified / continuation-cue candidate at `bf7d4d8f...`.

The recovery followed the durable three-authority contract in:

`docs/checkpoints/2026-09-22_PC_SHEET_PDF_VISUAL_RECOVERY_TRIPLE_AUTHORITY.md`

Authority order remains:

1. exact owner-approved/frozen family proofs and their renderer code;
2. current production semantics, completeness and data-driven pagination;
3. the owner's 2026-09-22 corrections, which supersede an older approved detail when they conflict.

The rejected `bf7d4d8f...` artifact remains historical evidence only and must not be used as a visual baseline.

## Repairs completed

### Classic

- preserved every physical ruled line while rendering generated text;
- long content no longer consumes/removes later writable rules;
- continuation spells preserve level-specific blocks instead of collapsing levels 6+ into one generic continuation block;
- overflow may add further family-native pages instead of compressing unrelated levels;
- inventory continuation uses available rows and no longer repeats `(cont)` labels as filler.

### Custom v1

- recovered the frozen Run-7 / Extended-Run-6 X geometry for Equipment, Gemas/Joyas/Arte, Otros Rasgos y Atributos and Notes;
- Notes continuation uses the approved two-column grammar;
- approved font roles are reused instead of mixed ad-hoc families;
- inventory continuation uses every physical source-template rule in natural top-to-bottom column order;
- continuation indicators are footer-only rather than disruptive in-section boxes;
- removed the extra `CONTINUACIÓN` subtitle that collided with the native `Equipo` heading;
- Special Equipment keeps the template's native empty checkbox and overlays only the approved v8 check glyph for checked rows, eliminating the double-box effect and restoring optical centering.

### Custom v2 — per Attribute and per Ability

- recovered approved source-measured check/marker placement and numeric/modifier centering;
- recovered approved portrait-name placement;
- retained the Corbel/Fira role contract;
- continuation indicators are footer-only;
- additional pages preserve family-native X geometry;
- inventory continuation reuses the source-led Equipment grammar and expands ordinary equipment to three columns;
- structured item status/details are naturally wrapped rather than character-count fragmented;
- multi-page v2 inventory overflow uses page-scoped optional-layer names, so legitimate extra continuation pages no longer collide in PDFBox.

## Regression/diagnostic hardening

- continuation cues have a positional guard that keeps them in the bottom margin;
- obsolete generic `5+` continuation behavior was removed;
- v1 inventory proof guards against reintroducing the overlapping continuation subtitle;
- Desktop test logging now exposes full failed-test stack traces;
- the recovered multi-page v2 inventory path is exercised by the full proof suite.

## Final visual preflight

The exact artifact `10720833172` was downloaded and rendered locally with the repository PDF workflow.

Worker visual preflight verified:

- Classic overflow pages preserve ruled-paper cadence, useful equipment space and level-specific spell blocks;
- Custom-v1 base/extended geometry is back on the approved source-template axes;
- Custom-v1 Notes continuation uses two columns;
- Custom-v1 Equipment continuation no longer skips alternate physical rules;
- Custom-v1 Special Equipment checks render as a single centered tick inside the source box;
- Custom-v1 `CONTINUACIÓN` no longer overlaps the native `Equipo` heading;
- both Custom-v2 variants preserve the corrected base centering and portrait-name placement;
- v2 inventory continuation uses three ordinary-equipment columns plus the native treasure/special-equipment grammar;
- footer continuation cues are visually unobtrusive and remain outside writable sections.

This preflight is not owner approval.

## Compact owner-review set

Review from artifact `10720833172` in this order.

Faithful + real overflow/continuation:

1. `owner-review-continuation-cues-classic.pdf`
2. `owner-review-continuation-cues-custom-v1.pdf`
3. `owner-review-continuation-cues-custom-v2-attribute.pdf`
4. `owner-review-continuation-cues-custom-v2-ability.pdf`

App Modified presentation:

5. `owner-review-app-modified-classic-modified.pdf`
6. `owner-review-app-modified-custom-v1-modified.pdf`
7. `owner-review-app-modified-custom-v2-attribute-modified.pdf`
8. `owner-review-app-modified-custom-v2-ability-modified.pdf`

Regression sanity:

9. `owner-review-app-modified-classic-with-portrait.pdf`

## Current gate / next work

**Stop here for owner visual QA.**

If the owner approves this package:

1. record owner approval and freeze the recovered/corrected components as the replacement goldens where the 2026-09-22 corrections supersede older details;
2. keep the exact rejected `bf7d4d8f...` candidate historical/non-authoritative;
3. proceed to Desktop Save/Share/export invocation around the completed renderer;
4. then continue cross-surface parity;
5. keep PR #85 DRAFT / DO NOT MERGE until the remaining functional gates are complete.

If the owner reports a remaining defect, treat it as bounded feedback against this exact artifact and do not reopen unrelated frozen geometry.

No Cloudflare, Neon, Descope or other provider action is required for this gate.

# Checkpoint — App Modified Sheet + originating-section continuation cues — OWNER QA READY

**Date:** 2026-09-22 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Implementation head:** `bf7d4d8f5324af7aabb5bf4d3d0038936af2b53d`  
**Final Scaffold:** `35767630932` / run #3195 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10713481125`  
**Artifact digest:** `sha256:0e38c573c5f115179c022bf4f48ae62114325b1b7f4b75ecde9ef9b03462129a`

## Purpose

Close the remaining implementation side of the D-0074 owner-facing `APP_MODIFIED_SHEET` and originating-section continuation-cue gates without reopening the frozen Classic, Custom v1 or Custom v2 visual families.

This checkpoint records **implementation/QA readiness only**. It does not claim owner approval of the new App Modified or continuation-cue treatments.

## Product behavior implemented

### App Modified Sheet

The physical renderer now accepts semantic plans whose base layout mode is `APP_MODIFIED`.

The implementation deliberately reuses the already approved family-native Custom Statistics visual grammar instead of inventing a fifth sheet family:

- the approved family renders first;
- a Custom Statistics page is rendered using the selected family's approved Extended visual grammar;
- for `APP_MODIFIED_SHEET`, that page is promoted into the base-sheet sequence immediately after MAIN;
- for `MODIFIED_SHEET_AND_COMPLETE_EXTENDED_PAGE`, the promoted page is inserted after MAIN while the complete Extended Custom Statistics copy is preserved later in the export;
- promoted pages are explicitly labeled `HOJA MODIFICADA - ESTADÍSTICAS PERSONALIZADAS`;
- Classic's former `EXTENSIÓN` / historical page-number labels are covered and replaced with `MODIFICADA` while keeping the frozen geometry intact.

No shared export semantics were redefined.

### Originating-section continuation cues

When a matching Extended continuation actually exists, the frozen base sheet now receives a bounded cue of the form:

`<SECCIÓN> - CONTINÚA EN EXTENSIÓN`

The cue is omitted when no matching continuation exists.

Covered continuation kinds:

- Custom Statistics;
- Traits & Features;
- Resources & Options;
- Inventory / Equipment;
- Spells;
- Notes.

Family-specific placement is isolated in the completion layer and does not rewrite the underlying frozen renderers.

The final v2 trait cue is anchored at the lower boundary of `RASGOS Y ATRIBUTOS`, avoiding the `OTROS` area.

## Frozen-family preservation

The implementation is isolated in:

`DesktopPcSheetVisualCompletionRenderer`

plus dispatcher/regression wiring.

The frozen family renderers themselves were not recalibrated.

App Modified is applied after the approved family-native output exists, and continuation cues are painted onto the canonical base-page objects before any modified-page insertion changes document page indexes.

## Portrait compatibility

Classic originally assumed its portrait target was static page index 1. App Modified inserts a page after MAIN, which can shift the Classic `ASPECTO` page.

Production now resolves the Classic portrait page from rendered `ASPECTO` content rather than assuming a fixed index.

Regression coverage verifies that App Modified insertion does not move portrait ink onto the promoted custom-statistics page.

Custom v1 and both Custom-v2 variants retain their existing page-1 portrait targets.

## Automated coverage

The integrated regression suite covers:

- all four visual families;
- `APP_MODIFIED_SHEET`;
- `MODIFIED_SHEET_AND_COMPLETE_EXTENDED_PAGE`;
- custom Attributes;
- custom Abilities/skills;
- family-native Custom Statistics completeness;
- no-false-cue behavior when no continuation exists;
- real continuation cues for overflowing content;
- App Modified page ordering;
- Classic portrait target preservation after page insertion;
- existing Spellbook/portrait/Extended regression suite through the normal Desktop build.

## Validation history

Relevant successful runs during implementation:

- #3187 / `35765815167` — SUCCESS;
- #3189 / `35766119696` — SUCCESS;
- #3191 / `35766547517` — SUCCESS;
- #3193 / `35767324592` — SUCCESS;
- **final #3195 / `35767630932` — SUCCESS**.

Final #3195:

- Kotlin build/test — SUCCESS;
- backend — SUCCESS;
- hosted database — SUCCESS;
- populated proof artifact upload — SUCCESS.

## Final visual preflight

The final #3195 proof artifact was inspected after CI.

Confirmed:

- Classic promoted Custom Statistics page is labeled `MODIFICADA` and no longer carries stale continuation/page-number labeling;
- Custom v1 modified page retains its approved Custom-v1 visual grammar;
- Custom v2 per Attribute modified page retains the approved per-Attribute organization;
- Custom v2 per Ability modified page retains the approved separated Attribute / Saving Throw / Ability organization;
- v2 `RASGOS` continuation cue sits at the trait-section boundary rather than inside `OTROS`;
- Classic portrait remains on the `ASPECTO` page after App Modified insertion.

## Owner review set

Primary proof files in artifact `10713481125`:

- `owner-review-app-modified-classic-modified.pdf`;
- `owner-review-app-modified-custom-v1-modified.pdf`;
- `owner-review-app-modified-custom-v2-attribute-modified.pdf`;
- `owner-review-app-modified-custom-v2-ability-modified.pdf`;
- corresponding `*-modified-plus-extended.pdf` proofs;
- `owner-review-continuation-cues-classic.pdf`;
- `owner-review-continuation-cues-custom-v1.pdf`;
- `owner-review-continuation-cues-custom-v2-attribute.pdf`;
- `owner-review-continuation-cues-custom-v2-ability.pdf`;
- `owner-review-app-modified-classic-with-portrait.pdf`.

PNG snapshots with matching prefixes are included for quick review.

## Current gate

**IMPLEMENTATION + AUTOMATED QA: PASS**

**OWNER VISUAL QA: PENDING**

Do not merge PR #85 and do not declare these two new visual treatments frozen/approved until the owner reviews the generated proof set.

## Next package after owner approval

After the owner accepts this visual gate:

> wire local Desktop DM Save/Share/export invocation around the completed renderer, preserving the same shared semantic plan and local portrait resolver boundary.

No Cloudflare, Neon, Descope or provider action is required for the current gate.

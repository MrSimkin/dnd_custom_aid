# Checkpoint — App Modified Sheet + originating-section continuation cues — OWNER REJECTED / DO NOT USE

**Date:** 2026-09-22 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Implementation head:** `bf7d4d8f5324af7aabb5bf4d3d0038936af2b53d`  
**Final Scaffold:** `35767630932` / run #3195 — SUCCESS  
**Rejected proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10713481125`  
**Artifact digest:** `sha256:0e38c573c5f115179c022bf4f48ae62114325b1b7f4b75ecde9ef9b03462129a`

## Purpose

Close the remaining implementation side of the D-0074 owner-facing `APP_MODIFIED_SHEET` and originating-section continuation-cue gates without reopening the frozen Classic, Custom v1 or Custom v2 visual families.

This checkpoint is retained as **rejected historical evidence only**. The owner reviewed the generated proofs on 2026-09-22 and rejected the candidate because it reintroduced multiple visual defects that had already been resolved in the frozen approved baselines.

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


## Owner rejection — 2026-09-22

The owner explicitly rejected artifact `10713481125`.

Observed regression classes include:

### Classic

- ruled writing rhythm regressed: generated text skips usable rules and some long-text regions no longer preserve the expected subsequent writing lines;
- spell continuation improperly compresses multiple high spell levels into one block instead of extending to additional family-native pages as necessary;
- Equipment continuation wastes available writing/table capacity and overuses `(cont)` labels instead of extending the approved equipment grammar naturally.

### Custom v1

- continuation cues are visually disruptive;
- Equipment, Gemas/Arte/Joyas, Otros Rasgos y Atributos and Notes drift on the X axis from the owner-approved placement;
- Notes no longer use the approved two-column treatment;
- font roles are inconsistent with the approved/frozen family;
- Equipment continuation skips writing lines, contains poor condensation/wording, and no longer follows the approved row cadence;
- check markers are incorrect and miscentered.

### Custom v2 — per Attribute and per Ability

- check markers and Ability modifiers are miscentered;
- several numeric values are not centered in their intended boxes;
- continuation cues are visually disruptive;
- additional-page text drifts on the X axis from the approved layouts;
- unnecessary line breaks reappear;
- additional Equipment treatment ignores the already-approved source-led page grammar that could be extended with another column;
- font use diverges from the approved roles;
- portrait/name treatment no longer matches the frozen approved version.

The owner reviewed these proofs in the ChatGPT PDF viewer rather than the owner's normal PDF reader. Viewer-specific font rendering may affect minor raster appearance, but the structural regressions above must be treated as real until the exact approved baselines prove otherwise. Do not dismiss them as a viewer artifact.

## Root-cause correction

The rejected candidate was built from the later production renderers as if their previous semantic/audit PASS status guaranteed exact visual equivalence to the frozen owner-approved proof implementations.

That assumption is invalid.

The durable visual authority is the exact owner-approved proof commit/artifact for each family:

- Classic Run 2: commit `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`, artifact `10595468434`;
- Custom v1 base Run 7: commit `7448693e36ee1b26243bd4091615dba725e95027`, artifact `10591616012`;
- Custom v1 Extended Run 6: commit `69b308f3d5d493d06bd0107ac66c7524935aa9fa`, artifact `10609869599`;
- Custom v2 per Attribute base Run 4: commit `03c3155301196eaab1229afb4827c46f8acf04cc`;
- Custom v2 per Ability corrected Run 2: commit `be689bb6628a46e562428159fc72b2ee51b95d2c`;
- Custom v2 Extended Run 7: commit `f464522ad232f4ed6193e1c28d118e45faad988c`, artifact `10617190236`.

Production promotion must now be re-audited **against those exact frozen artifacts**, not against later production artifacts or prior production-only screenshots.

## Required recovery method

Do not patch the rejected candidate page by page.

Instead:

1. download/materialize the exact frozen approved artifacts;
2. reconstruct which exact test/proof renderer code produced each approved page;
3. compare current production output against the approved page at raster and layout-rule level;
4. extract/reuse the approved renderer components directly where production diverged;
5. add golden visual regression guards so future production changes cannot silently reintroduce X/Y/font/marker/row-cadence regressions;
6. only after the faithful baseline is recovered, reintroduce App Modified and continuation cues as bounded additions;
7. obtain owner visual review again.

## Current gate

**ARTIFACT 10713481125: OWNER REJECTED / DO NOT USE**

**APP MODIFIED + CONTINUATION CUES: REOPENED**

PR #85 remains **OPEN / DRAFT / DO NOT MERGE**.

The next action is recovery of exact owner-approved visual baselines, not Save/Share.

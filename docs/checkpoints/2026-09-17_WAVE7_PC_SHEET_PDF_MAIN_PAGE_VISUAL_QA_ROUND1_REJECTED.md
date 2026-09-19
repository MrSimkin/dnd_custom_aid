# Checkpoint — PC Sheet PDF Main-Page Visual QA Round 1 Rejected

**Date:** 2026-09-17 (Chile local time)  
**Status:** OWNER VISUAL QA — REJECTED / REMAPPING REQUIRED  
**Draft PR:** #85 — `pdf: add Custom sheet template rendering proof`  
**Reviewed head:** `e3ab7fd850bd72b1782109c7d6d2361ab7203624`  
**Scaffold:** `35299306838` — SUCCESS

## Purpose

This checkpoint records the owner's first visual review of the populated Custom v1 and Custom v2 MAIN-page PDF proofs.

Automated verification remains green, but the visual mapping is **not approved**. Green CI proves generation/build behavior only; it does not satisfy the required owner visual gate.

## Owner observations — Round 1

The owner rejected the current mapping with the following findings:

1. **Text alignment is not acceptable.**
   - Text is not reliably aligned horizontally inside its intended boxes/lines.
   - Text is not reliably aligned vertically inside its intended boxes/lines.
   - Placement must be based on deliberate field geometry/text metrics rather than approximate visual coordinates.

2. **Checkbox/proficiency markers are not acceptable.**
   - The current proof uses font glyphs such as `X` as check/proficiency marks.
   - This is not the desired rendering approach.
   - The next implementation should investigate and prefer actual vector markers/icons/drawn shapes (for example SVG/vector geometry or equivalent PDF vector primitives) instead of pretending a text glyph is a UI symbol.
   - Marker semantics and shapes must be intentionally mapped; do not assume one generic glyph is correct for every state.

3. **The dummy-data proof is too sparse for visual QA.**
   - If a page is being presented for typography/mapping review, the **entire reviewable page must be populated with representative dummy data wherever the canonical PC model provides a corresponding value**.
   - Empty fields should remain empty only when intentionally testing blank/writable behavior or when no canonical source data exists.
   - Sparse examples are insufficient to validate spacing, collisions, wrapping, overflow, font sizing and visual hierarchy.

4. **Font selection is not acceptable.**
   - The font choice used for overlaid data does not visually fit the source templates.
   - Using essentially one font treatment across all overlaid content is not acceptable.
   - The next attempt must investigate font families/weights/styles by semantic role and by template family rather than applying one generic typeface everywhere.

5. **Round 1 mapping is not approved.**
   - Custom v1 MAIN page: NOT APPROVED.
   - Custom v2 — per Attribute MAIN page: NOT APPROVED.
   - Custom v2 — per Ability MAIN page: NOT APPROVED.
   - No visual family may be marked complete or merged on the basis of these proofs.

## Required research before Round 2

Before making another broad visual-mapping attempt, investigate and document the rendering approach for at least:

- PDF coordinate and box measurement;
- horizontal alignment rules: left, centered, right, decimal/sign-aware where relevant;
- vertical alignment using real font ascent/descent/baseline metrics rather than approximate offsets;
- font discovery/selection/embedding and multiple font roles per family;
- font weight/style selection for names, values, labels/secondary text and compact numeric fields;
- text fitting, wrapping and readability floors;
- vector marker/icon rendering for proficiency/check states, including whether SVG import or direct PDF vector primitives are the better fit;
- reusable field/box geometry so alignment is parameterized rather than hand-tuned independently at every call site;
- deterministic full-page representative dummy data for visual stress testing.

This research should remain an implementation investigation. It must not redefine the already-approved PC export semantics in D-0074 or the shared `PcSheetPdfRenderPlan`.

## Required review method for Round 2

The owner does **not** want another all-at-once visual approval request.

Future review must be structured like a QA test and proceed **section by section, objective by objective, parameter by parameter**.

For each reviewed section, present a compact QA matrix that identifies at minimum:

| Parameter | What is being checked |
| --- | --- |
| Field/data mapping | Correct canonical PC value goes to the intended template field |
| Horizontal placement | Left/center/right alignment and X positioning |
| Vertical placement | Baseline/box centering and Y positioning |
| Font family | Typeface is appropriate to the template/semantic role |
| Font weight/style | Regular/bold/other treatment is appropriate |
| Font size | Readable and visually proportional |
| Fit/wrapping | Long representative values behave correctly |
| Marker/icon | Correct vector symbol/state, not a text-glyph substitute |
| Spacing | Padding and separation from template labels/borders |
| Overflow/collision | No overlap; defined behavior for long content |
| Empty-state behavior | Intentionally blank/writable when appropriate |
| Visual consistency | Comparable fields use consistent rules |

Each parameter should receive an explicit owner result such as **PASS**, **CHANGE**, or **NOT APPLICABLE**. A section is not approved until its required parameters pass. A page/family is not approved until all of its required sections pass.

## Suggested section order for MAIN-page QA

Use a stable sequence so review is manageable:

1. identity / class / race / progression;
2. core combat summary boxes;
3. abilities and modifiers;
4. saving throws and proficiency markers;
5. skills and training/expertise markers;
6. spellcasting summary and spell-slot markers;
7. attacks/actions;
8. traits/features;
9. portrait area;
10. remaining wealth/ammunition/other fields where present;
11. whole-page consistency and stress case.

Do not jump to final whole-page approval before the component sections have passed.

## Continuation boundary

The next task is **research + renderer/mapping redesign**, not more blind coordinate nudging.

Do not merge PR #85.

Do not treat the current proof renderer as visually accepted.

After the rendering approach has been researched and revised, produce fully populated representative MAIN-page proof(s) and begin the structured QA sequence above. Preserve owner feedback and previous rejected proofs as evidence rather than overwriting the historical result.

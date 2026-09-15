# Phase 4A — preqa.8 — P12 closed

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** P12 CLOSED / READY FOR REPAIR SPEC  
**Implementation authorization:** NOT YET  
**Audit scope:** `FULL APP AUDIT REQUIRED`

## P12 — Contextual help info control / real rich tooltip behavior

### QA problem

The owner liked the contextual-help system conceptually but found the visible `ⓘ` controls poorly positioned/aligned. Inspection also showed that the shared `CharacterHelpV4` primitive renders the information control as a literal text glyph and currently opens explanatory content through a dropdown/menu-like surface rather than a true tooltip.

### Owner-approved control presentation

- Replace the literal text `ⓘ` used as an icon/control with a proper graphical information icon from the shared icon/control system.
- The visible control should be visually compact/discreet rather than looking like a chunky text button or boxed glyph.
- The actual touch target must remain comfortably tappable even when the visible icon itself is small.
- Place the info control adjacent to the title, label, field or section it explains so the relationship is immediately clear.
- Prefer same-row/trailing alignment where space permits; narrow layouts may wrap only when needed, while preserving an obvious association with the explained element.
- Do not leave contextual-help controls floating in detached rows when they semantically belong to a specific nearby label/field/section.

### `Siempre visible` mode

- The owner explicitly approves the current `Siempre visible` presentation.
- Preserve its inline explanatory-text behavior and overall presentation grammar.
- P12 must not redesign `Siempre visible` merely to make it resemble tooltip mode.

### `ⓘ / tooltip` mode

The owner had **not** previously approved the existing opened-state presentation. The repair must therefore replace the current menu-like behavior with a genuine contextual rich tooltip:

- tapping the info icon opens a floating explanatory bubble visually anchored to that icon/control;
- the bubble may contain multiline explanatory text;
- it must not insert/push explanatory text into the normal page layout as though `Siempre visible` had merely been toggled temporarily;
- it should provide a visual anchor/caret or equivalent positional relationship to the triggering info icon where the platform/component permits;
- placement adapts automatically to available room (above/below/side as appropriate) rather than clipping off-screen;
- the tooltip has a sensible maximum width; on narrow phones it may use most of the available width with margins, while tablets must not stretch it unnecessarily across the display;
- tapping outside, pressing Back, or reactivating the triggering info control closes the tooltip;
- unusually long help content remains bounded/usable rather than becoming an unbounded floating wall of text;
- the exact shared implementation may use the platform/Material rich-tooltip primitive where suitable, but the accepted product behavior above is controlling.

### Rejected alternatives / non-goals

- Do not treat inline expansion after tapping as the tooltip design; that is semantically too close to `Siempre visible` and was explicitly distinguished from the desired tooltip behavior.
- Do not open a full dialog/bottom sheet for ordinary contextual help unless a future exceptional content case explicitly requires it.
- Do not redesign the wording/content model of contextual help under P12; the repair concerns the control, positioning and opened-state presentation.

### FULL APP AUDIT REQUIRED

This point exposes a shared UI primitive/pattern rather than an isolated screen defect. Implementation must audit all Player surfaces that render contextual help and ensure they use the repaired shared graphical info control, appropriate adjacency/alignment and real rich-tooltip behavior. This also overlaps P10's app-wide pseudo-icon audit; the overlap must be resolved through shared primitives rather than duplicate local fixes.

### Regression boundary

Coverage must prove representative contextual-help placements across different field/section layouts; proper graphical info icon use rather than literal glyph-as-control; safe touch target; `Siempre visible` unchanged; `ⓘ / tooltip` opens a floating anchored multiline tooltip without reflowing the normal page; dismissal by outside tap/Back/retrigger; bounded behavior on phone/tablet widths; and no screen-local legacy implementation bypassing the shared repaired primitive.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

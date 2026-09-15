# Phase 4A — preqa.8 — P12 Round 1 consolidation

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** P12 OPEN — ROUND 1 CONSOLIDATED  
**Audit scope:** `FULL APP AUDIT REQUIRED`  
**Implementation authorization:** NOT YET

## P12 — Contextual-help info control presentation / tooltip behavior

This file is the durable round-level consolidation required before any further P12 questions.

### Owner-approved decisions

1. Replace literal text-character `ⓘ` controls with a proper graphical info icon. The visual centering/alignment must not depend on font glyph metrics.

2. The visible info affordance should be visually discreet and icon-like rather than a chunky text-button-style rectangle, while retaining a safe/easy touch target around it.

3. Position each info icon in a way that makes its relationship to the explained title/label/field/control immediately clear, normally in the same visual row or directly adjacent to that element. Avoid detached/floating help icons in their own arbitrary row unless space genuinely requires wrapping.

### Clarification that supersedes the earlier assumption about item 4

- The owner explicitly approves the existing **`Siempre visible` presentation** and does not want that mode redesigned merely because the info/tooltip mode is being repaired.
- The owner has **not previously approved the opened-state presentation of `ⓘ / tooltip` mode**.
- Showing the help text merely in the same inline format as `Siempre visible` after tapping the info icon is **not considered a tooltip** by the owner.
- Therefore the opened-state interaction/presentation for `ⓘ / tooltip` remains OPEN and requires a deliberate design decision.
- The current implementation uses a menu-like popup anchored from the shared `CharacterHelpV4` control; this must not be treated as owner-approved simply because it is functional.

### Full-app audit requirement

P12 is `FULL APP AUDIT REQUIRED` because contextual help is provided by a shared primitive and appears across multiple screens. Repair must audit all current usages for icon type, placement/alignment, touch target, narrow/wide responsive behavior, and consistency of the opened help presentation.

P12 also intersects P10's full-app pseudo-icon audit, but the P12 audit additionally owns contextual-help placement and opened-state presentation.

### Still open before P12 closure

- the desired opened-state UI/interaction for `ⓘ / tooltip` mode;
- dismissal behavior, anchoring, width/height constraints and handling of longer contextual-help text, as necessary to define that presentation without changing the approved `Siempre visible` mode.

No implementation is authorized by this consolidation.
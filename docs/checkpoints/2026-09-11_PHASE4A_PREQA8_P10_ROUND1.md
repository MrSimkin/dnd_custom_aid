# Phase 4A — preqa.8 — P10 Round 1 consolidation

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** P10 OPEN — ROUND 1 CONSOLIDATED  
**Implementation authorization:** NOT YET

## P10 — Application Settings compactness / density proportionality

This file is the durable round-level consolidation required before any further P10 questions. The parent decision log remains the controlling point-by-point record; P10 is not closed yet.

### Owner-approved Round 1 decisions

1. **Compactness is an app-wide density/whitespace control, not merely an inter-card-gap control.** It must coherently influence page/section margins, inter-card spacing, internal card padding, spacing between related fields/rows, editor/panel whitespace, and analogous spacing throughout the app.

2. **The 100% baseline must be rebalanced first.** The current 40800 baseline is internally inconsistent: some card-to-card spacing is already extremely constrained while other elements are extremely far apart. Repair must first create a coherent, comfortable, visually balanced 100% density system. Lower-than-100% and higher-than-100% settings are then derived from that corrected baseline; the slider must not merely scale the currently imbalanced values.

3. **100% remains the normal intended baseline.** It is neither an intentionally loose accessibility mode nor a workaround for oversized components. Independent layout defects such as the Combate fixed HUD are repaired at their own accepted baseline rather than requiring the owner to lower compactness.

4. **The density system applies across the whole application, including windows/dialogs/editors.** It is not limited to normal sheet/card surfaces. Modal outer margins, internal padding/rhythm, panels and other app-controlled whitespace participate where appropriate, subject to the P9 adaptive editor rules and usability floors.

5. **Text size, icon size and usable touch targets do not shrink merely because compactness is lowered.** Font size has its own setting. Surrounding whitespace may contract, but controls must remain safely tappable/readable.

6. **Scaling is perceptually proportional rather than naïvely mathematical.** A 70% setting need not multiply every individual distance by exactly 0.70. Different spacing roles may have sensible minimum floors so already-small gaps do not collapse while oversized whitespace is reduced more visibly.

7. **Internal card/component padding definitely participates.** A denser setting must make the components themselves feel denser where safe, not simply push unchanged bulky cards closer together.

8. **The Settings preview must honestly demonstrate the real density effect.** It should visibly demonstrate at least outer margin, spacing between elements and internal element/card padding so the selected percentage previews the same kinds of changes the app actually applies.

### New owner requirements surfaced in this round

- The compactness control must support **values above 100%** as well as below 100%. The exact upper range/steps remain to be decided in the next P10 round.
- Audit remaining **text-based pseudo-icons / text icon buttons**. The owner reports still seeing controls represented by text characters in some parts of the app. This requires inspection before deciding the final P10/UI-control boundary; it must not be silently ignored.

### Still open before P10 closure

- exact above-100% range/step policy;
- result of the text-icon audit and whether any remaining occurrences are legitimate text actions versus pseudo-icons that should use the shared icon-control system.

No implementation is authorized by this consolidation.

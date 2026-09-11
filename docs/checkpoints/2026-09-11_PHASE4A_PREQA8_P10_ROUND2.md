# Phase 4A — preqa.8 — P10 Round 2 consolidation

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** P10 OPEN — ROUND 2 CONSOLIDATED  
**Implementation authorization:** NOT YET

## Owner-approved Round 2 decisions

1. **Density range is symmetric around the corrected 100% baseline:** use `50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150%`.
2. The UI must make the direction unmistakable: values below 100% mean **denser / less whitespace**, 100% means the rebalanced normal baseline, and values above 100% mean **more spacious / more whitespace**. Do not rely on the raw percentage alone to communicate this.
3. The app-wide pseudo-icon rule is approved: when characters such as `+`, `✓`, `×`, `★`, arrows, etc. are being used as icons or icon-controls, replace them with proper graphical/shared icon controls. Legitimate word-based actions such as `Guardar`, `Cancelar`, `Añadir`, `Cerrar`, `Manual`, etc. may remain text buttons when text is semantically appropriate.
4. This pseudo-icon requirement is explicitly **transversal** and requires a full Player-app audit during the repair, including normal sheet surfaces, dialogs/windows, menus, toolbars, settings and other shared controls. Known examples already confirmed in the active branch include:
   - the collection-toolbar add control rendered as literal `+`;
   - filter selected-state rendering using textual `✓`;
   - PC Settings `Orden de pestañas` move controls rendered as literal `↑` / `↓` text buttons.
5. **Transversal audit trigger rule:** when a QA point reveals that the defect is caused by or represents a shared primitive, repeated interaction grammar, shared presentation rule or other app-wide pattern, that point must be explicitly marked as requiring a full-app audit of analogous surfaces before the repair can be considered complete. The implementation must not repair only the originally reported instance and leave equivalent occurrences elsewhere.

## New interaction issue surfaced by the audit

`Orden de pestañas` currently uses per-row `↑` / `↓` move buttons. The owner suggests that tab ordering may itself be better represented by direct drag-and-drop rather than merely replacing those arrows with graphical arrow icons.

This is not silently decided by P10. The pseudo-icon defect is confirmed, but the underlying tab-order interaction must be explicitly resolved before P10/P13 implementation planning. It should be reconciled with the already-approved P6 direct drag-and-drop ordering grammar rather than treated as an isolated cosmetic icon swap.

## Still open before P10 closure

- Decide whether `Orden de pestañas` should use direct drag-and-drop ordering instead of move-up/move-down controls.
- Fold P10 Round 1 + Round 2 into the controlling parent decision log and mark P10 closed only after that interaction question is resolved.

No implementation is authorized by this consolidation.
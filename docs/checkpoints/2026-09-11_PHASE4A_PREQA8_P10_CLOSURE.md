# Phase 4A — preqa.8 — P10 closure

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Audit matrix:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_FULL_APP_AUDIT_MATRIX.md`  
**Status:** `CLOSED / READY FOR REPAIR SPEC`  
**Implementation authorization:** NOT YET

## P10 — Application Settings compactness / density proportionality

### Baseline and scope

- `Compactación de espacios = 100%` must first be rebalanced into a coherent normal-density baseline. The current 40800 baseline is internally inconsistent: card-to-card gaps can be extremely constrained while other whitespace is excessive.
- The density system applies throughout the Player application, including page/section margins, inter-card gaps, internal card/component padding, related-row/field rhythm, dialogs/windows/editors and analogous app-controlled whitespace.
- Independent layout defects such as the Combate fixed HUD remain repaired at their own proper 100% baseline; compactness is not a workaround for bad default layout.
- Text size, icon size and minimum usable touch targets do not shrink merely because density is lowered. Font size remains a separate preference.
- Scaling is perceptually proportional with sensible minimum/maximum floors, not a blind multiplication of every distance by the selected percentage.

### Accepted range and meaning

The accepted density range is symmetrical around the corrected baseline:

`50 · 60 · 70 · 80 · 90 · 100 · 110 · 120 · 130 · 140 · 150%`

- `< 100%` = progressively **denser / more compact**;
- `100%` = corrected normal/balanced baseline;
- `> 100%` = progressively **more spacious / more breathing room**.

The Settings UI must make this direction unmistakable on screen. The user must not have to infer whether `50%` means smaller spacing or larger spacing. Endpoint/directional wording or equivalent visual labeling must clearly communicate `Más compacto / más denso` versus `Más espacio / más aire`, with 100% identifiable as the normal/balanced point. Exact Spanish wording may be polished during implementation, but semantic ambiguity is not acceptable.

### Preview

The Settings preview must honestly demonstrate the real density effect. It should show enough structure to make visible changes to:

- outer margin;
- spacing between elements/cards;
- internal padding/rhythm inside an element/card.

It must preview the same categories of whitespace actually controlled across the app.

### Pseudo-icon rule

A full Player-app audit is required for characters/text being used as iconography or icon controls.

- Literal symbols such as `+`, `✓`, `×`, stars, arrows and similar characters must not be used merely as cheap substitutes for proper graphical/shared icons where the semantic role is an icon/control.
- Use the existing shared icon-control system where an appropriate control already exists; extend that system coherently where a missing icon is genuinely required.
- Legitimate textual actions remain text when the words themselves are the intended control, e.g. `Guardar`, `Cancelar`, `Cerrar`, `Manual`, etc.
- The audit must cover normal tabs, settings, dialogs/windows, menus, toolbars, conditional modules and shared primitives rather than patching only observed call sites.

Confirmed current occurrences include the shared collection literal `+`, textual checkmark-style selection, and `Orden de pestañas` literal `↑` / `↓` controls.

### Tab-order interaction discovered during the pseudo-icon audit

The owner explicitly rejects solving `Orden de pestañas` merely by replacing `↑` / `↓` text with prettier arrow icons.

Tab ordering is a genuine reorder operation and must follow the direct manipulation grammar approved in P6:

- long-press the tab-order row;
- drag it directly up/down through the visible list;
- neighboring rows reflow to show the insertion destination;
- release to commit/persist immediately;
- no up/down arrow buttons;
- no separate reorder mode;
- no `Done/Listo` step solely for the reorder.

This occurrence is therefore covered by both P6's shared reorder audit and P10's pseudo-icon audit.

### Audit scope

**Marker:** `FULL APP AUDIT REQUIRED`.

P10 is not acceptance-complete after changing only the Application Settings slider or known pseudo-icons. Implementation must systematically inspect Player whitespace/density usage and symbol-as-icon controls, updating analogous cases to the approved shared rules or documenting a legitimate exception.

### Regression boundary

Validation must prove at minimum:

- a corrected visually balanced 100% baseline;
- clearly denser representative screens at values below 100%;
- clearly more spacious representative screens above 100%;
- full accepted `50–150%` range and persistence;
- unambiguous dense↔spacious direction in Settings;
- internal component padding as well as inter-component gaps participating where safe;
- dialogs/editors participating consistently with P9 constraints;
- text/icon/touch-target sizes not being inappropriately scaled by density;
- preview accurately demonstrating outer/inter/internal spacing behavior;
- app-wide pseudo-icon audit with known literal symbol controls removed where they are acting as icons;
- direct drag-and-drop tab ordering with immediate persistence and no arrow-button workflow.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.
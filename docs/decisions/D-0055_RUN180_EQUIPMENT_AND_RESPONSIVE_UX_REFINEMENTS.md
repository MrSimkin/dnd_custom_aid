# D-0055 — Run #180 Equipment and responsive UX refinements

**Status:** Approved / exploratory where explicitly noted  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

Owner phone QA of Scaffold checks run #180 confirmed the functional `Equipo` domain while identifying several corrective UX requirements that also reinforce broader presentation principles already used elsewhere in the character sheet.

These refinements must preserve the existing durable character/inventory model and the functional behavior already verified.

## 1. Equipo editor inherits the Combate IME requirements

The add/edit equipment editor has the same unacceptable keyboard behavior found in Combate.

Required correction:
- software keyboard must not cover required lower content/actions;
- editor content should remain reachable through scrolling/insets/appropriate constraints;
- tapping outside to dismiss the keyboard must not close the entire item editor or discard its draft;
- explicit Apply/Cancel or an equivalent intentional close control governs editor dismissal.

This requirement should be implemented consistently across comparable character-sheet editors, not as a one-off Combat-only workaround.

## 2. Drag-and-drop inventory ordering

Replace up/down text/glyph reorder controls with direct drag-and-drop ordering for inventory items.

Requirements:
- preserve the resulting explicit `sortOrder`;
- Save/reopen restores exactly that order;
- accidental reorder should be reasonably avoidable through a drag handle or suitable long-press interaction;
- do not alphabetize automatically.

## 3. Compact responsive currency presentation

Current `Monedas` presentation consumes too much vertical space and conflicts with the project's compact-view principle.

Approved direction:
- substantially reduce vertical footprint;
- use a responsive grid/cell presentation rather than one tall row per currency;
- landscape should show at least three currency cells/columns where width permits;
- two columns in portrait is a candidate to test, **not yet a hard fixed requirement**;
- keep labels/amounts readable and editable without sacrificing compactness;
- custom currencies participate in the same responsive layout.

## 4. Responsive multi-column attacks and inventory

The owner observes that attacks/actions and item cards could benefit from multiple columns when space allows.

Approved responsive direction:
- portrait phone may remain single-column where that is genuinely clearer;
- landscape/wide layouts should use multiple columns when it improves density without harming readability or edit/reorder affordances;
- exact breakpoints/counts are implementation choices to validate in phone/tablet QA rather than fixed product rules.

This is a layout optimization only; it does not change ordering semantics or data ownership.

## 5. Vector icons, not text glyph buttons

Reinforce the existing icon-control principle:
- icon-only controls must use stable vector/icon resources;
- do not use Unicode/text glyphs such as arrows, crosses or symbolic characters as pseudo-icons when a real vector icon is appropriate;
- icon geometry should remain stable across text scale/font changes;
- provide appropriate content descriptions for accessibility.

This applies to reorder, edit/delete/close and comparable icon-only controls throughout the app.

## 6. Special items visually separated from common equipment

The owner wants special equipment visibly apart from ordinary inventory because it is special.

Interpretation approved here:
- retain the previously approved **unified underlying inventory data model** and stable item identity;
- present ordinary equipment and special/magic equipment as distinct visual sections/groups in `Equipo`;
- special items retain their richer location/Sintonización/description presentation;
- moving an item between ordinary and special state changes which visual group it appears in, without creating a disconnected ownership record.

This supersedes any implementation presentation that simply intermixes ordinary and special cards in one undifferentiated visible list.

## 7. Font selector must preview each font

The Settings typography audition must allow meaningful visual comparison.

Required behavior:
- each font option in the selector/dropdown should render its label/preview using that font family itself;
- selecting a font should not require memorizing its name to know how it looks;
- keep the eight approved audition candidates and existing saved-preference migration behavior;
- exact preview copy may be the font name itself or another short sample, provided the selected font is visibly demonstrated.

## 8. QA disposition

Run #180 `Equipo` remains functionally successful for checks 1–11 and 13. Check 12 remains partial because of the shared IME/editor-dismissal defect.

The next corrective build should address this decision together with D-0053 and D-0054 before final merge acceptance.
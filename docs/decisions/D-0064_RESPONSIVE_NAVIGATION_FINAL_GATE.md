# D-0064 — Responsive navigation final gate

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

This decision closes the final product-design gate before implementation planning for the enlarged post-run-#180 character sheet.

The owner approved the full responsive/navigation package covering the eight top-level tabs, nested spell-source navigation, editor/IME behavior, large text scales, and icon/touch-target requirements.

## Approved responsive/navigation rules

1. **Top-level tabs use one horizontally scrollable row.**
   - Applies at every supported text scale.
   - If all tabs naturally fit on a wide screen, scrolling need not be forced.
   - The selected tab must always be automatically brought fully into view.

2. **Selected-tab state is preserved when possible.**
   - Preserve selected top-level tab through rotation, recreation, and text-scale changes whenever that tab still exists.
   - If `Lanzador de conjuros` is turned OFF while `Conjuros` was selected, returning from PC Settings falls back deterministically to `General` because the prior tab no longer exists.
   - Turning spellcasting back ON makes `Conjuros` available again but does not force navigation there.

3. **`Conjuros` source navigation uses a second subordinate horizontal strip.**
   - Conceptually: `Todos | Mago | Clérigo | ...`.
   - The active source must also be automatically kept visible.
   - Very long custom source names may use sensible maximum width / ellipsis rather than shrinking all labels.

4. **Spell-source selection follows stable identity.**
   - Deleting the currently selected source falls back to `Todos`.
   - Renaming or reordering a selected source keeps that source selected because selection follows stable source ID rather than display name or list index.

5. **Do not add swipe-between-page navigation.**
   - Character-tab navigation is performed through the tab controls.
   - Avoid horizontal-page swipe because it would conflict with scrollable tab strips, source strips, drag-and-drop interactions, and editor gestures.

6. **Responsive layout is content-specific, not a blanket one-column phone rule.**
   - Portrait may use one or two columns depending on the component.
   - Landscape/wide layouts should exploit available width.
   - `Habilidades -> Por atributo` must retain its approved compact two-column concept, including at 115% and 130% text scale; collapsing it to one column is not an acceptable responsiveness fix.

7. **New-domain responsive behavior.**
   - `Trasfondo`: narrative cards may use two columns when width allows; Story remains comfortably wide.
   - `Rasgos`: cards may become multi-column on wide layouts.
   - `Conjuros`: compact spell rows/cards may use available width without permanently expanding descriptions.
   - `Notas`: the general note area remains generously sized; titled cards may become multi-column.
   - Character image placeholders in `Trasfondo` should appear side by side when practical and may stack only when width genuinely requires it.

8. **IME/keyboard safety is app-wide for all new editors.**
   - Applies to Background text, traits, spells, spell-source management, titled Notes, and other new editors.
   - Required controls/actions must remain reachable while the keyboard is visible.
   - Tapping outside must not silently discard editor state.

9. **All supported text scales remain valid.**
   - 80%, 90%, 100%, 115%, and 130%.
   - Navigation labels may scroll or truncate appropriately.
   - Labels must not wrap into malformed multi-line tabs, overlap controls, or be made illegibly small to solve width pressure.

10. **All new icon-only controls follow the vector-icon rule.**
    - Use real vector icons, adequate touch targets, and content descriptions.
    - This includes PC Settings gear, spell-source management, add/reorder/edit actions, and future interactive image controls.
    - Do not use Unicode/text glyphs as pseudo-buttons.

## Consequence

The product-design phase for the next build is considered closed, subject only to contradictions discovered during consolidation.

The next step is a consolidated implementation package that defines:
- exact coding order;
- schema/data migration work;
- run-#180 corrective UX work;
- implementation of `Trasfondo`, `Rasgos`, `Conjuros`, `Notas`, and PC Settings/navigation;
- durable checkpoint sequence;
- CI gates;
- final owner phone-QA sequence.

No production code should begin until that implementation package has been produced and checkpointed.

# D-0056 — Run #180 Settings, theme/font audition and `Por atributo` refinements

**Status:** Approved direction; replacement candidates/control details still to be selected where noted  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

Owner phone QA of run #180 validated the mechanics of font/theme/text-scale selection and persistence, but the audition identified redundant font candidates, theme identities that need redesign, and a regression in `Habilidades -> Por atributo` caused by collapsing the earlier two-column layout to one column.

## 1. Font audition pruning

The eight-font mechanism works, but the final candidate set should not retain visually redundant choices merely to preserve a count.

Owner observations:
- `Lexend` and `Sora` look too similar. **Remove or replace one of them.** The owner has no preference yet on which one survives.
- `Oswald` is disliked. **Remove or replace it.** Replacement is permitted but not yet selected.
- `Barlow Condensed` and `Roboto Condensed` look too similar. **Remove or replace one of them.** The owner has no preference yet on which one survives.

Do not invent replacement fonts without a later audition/approval. The next corrective build may either present a smaller non-redundant set or introduce clearly differentiated replacement candidates for owner testing.

The previously recorded requirement remains: the font-selection UI should show each candidate **rendered in its own typeface**, so choosing a font does not require selecting it blindly and returning to the sheet to identify it.

## 2. Theme redesign and expansion

### Gris

Current `Gris` is not acceptable as the intended middle ground between light and dark modes. It should be redesigned toward an actual intermediate neutral theme rather than simply being another gray-looking light theme.

### Azul noche

Current `Azul noche` reads as blue, but not strongly enough. Increase the blue identity while preserving the intended dark/night character and readability.

### Light variants

Add adapted light-theme counterparts for:
- `Cian oscuro`;
- `Azul noche`;
- `Verde bosque`.

These should be purpose-designed light variants, not naive color inversion. Naming may be refined consistently during implementation, but the relationship to the three existing color identities should remain obvious.

### Matrix naming

Rename user-facing `Matriz` to **`Matrix`**.

### Pergamino

Current `Pergamino` does not visually evoke parchment/scroll strongly enough. Redesign its palette/material impression so the identity is recognizable while preserving text contrast and usability.

## 3. Theme-selection control

With the expanded theme set, a simple text dropdown is no longer an appropriate selection experience.

Replace it with a more visual/compact selection control suited to browsing many themes. The exact control is still an implementation choice, but it should let the user compare identities more directly than a long text-only dropdown. Small theme preview swatches/cards are an acceptable direction for later implementation/testing.

## 4. `Habilidades -> Por atributo`: preserve two columns

The run #180 change to a single-column `Por atributo` layout is rejected as a solution to narrow-width/large-text pressure.

Approved correction:
- return to/preserve the useful **two-column** presentation on phone where previously demonstrated;
- do not solve long labels by collapsing the entire view to one column;
- allocate widths responsively inside each logical row;
- keep attribute/modifier cells aligned coherently;
- when a label wraps, use shared/logical row height so neighboring cells remain vertically aligned;
- `Juego de manos`, `Investigación` and similarly long labels must remain readable at 115% and 130%;
- landscape may use the available width more aggressively, but must retain clear grouping and alignment.

This supplements the earlier large-text/layout direction rather than replacing it.

## 5. QA consequences

Run #180 Settings/text-scale behavior is functionally good enough to continue QA:
- all font candidates applied: PASS;
- all current theme candidates applied: PASS as audition infrastructure;
- all five text scales usable: PASS;
- top-level tab labels did not wrap at 115/130: PASS;
- settings persistence through app restart: PASS;
- `Por atributo` presentation: FAIL/UX regression until two-column layout and attribute/modifier alignment are corrected.

The font/theme visual changes above and the `Por atributo` correction are required before final acceptance/merge, but they do not invalidate unrelated functional QA passes.
# D-0053 — Run #180 General UX refinements

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

During owner phone QA of the follow-up character-sheet build (Scaffold checks run #180), the complete focused `General` + derived-values + Quick Magic functional batch passed. The owner identified two presentation refinements for the next correction build.

These are UX refinements only and do not invalidate the functional PASS of the tested behavior.

## 1. Compact indication of additional adjustments

For derived values that contain a non-zero `Ajuste adicional`, the compact sheet must not consume a second line with explanatory text such as:

`ajuste +2`

The derived total itself already opens the calculation breakdown/editor where the exact adjustment is visible.

Approved direction:

- keep the compact field at normal one-line height;
- indicate the presence of a non-zero additional adjustment with a small compact marker, such as `*`;
- do not redundantly spell out the adjustment amount in the collapsed field;
- zero/blank adjustment should need no marker.

The exact marker may be refined for legibility/accessibility, but it must remain compact and must not add a second text row.

## 2. Velocidad follows the distance measurement convention

`Velocidad` is structured distance and therefore falls under the already-approved imperial-first approximate-metric presentation convention from D-0050.

User-facing speed should display imperial first and approximate metric in parentheses, for example:

`30 ft (9 m)`

Use the same game-friendly approximation convention already approved for other structured measurements. The authoritative structured/source value remains imperial; metric is derived for display.

This applies wherever the application itself presents the structured Speed value, including `General` and the read-only quick reference in `Combate`.

## Consequence

Both refinements are required for the post-run-#180 correction build before final acceptance/merge, but they do not block continuing owner QA of the other current build domains.
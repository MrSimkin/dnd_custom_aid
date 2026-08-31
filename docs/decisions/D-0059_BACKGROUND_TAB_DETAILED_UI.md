# D-0059 — Trasfondo tab detailed UI

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Scope

This decision closes the detailed `Trasfondo` presentation direction for the next character-sheet build.

## Approved fields and presentation

1. `Trasfondo` itself is split digitally into:
   - `Nombre del trasfondo`;
   - optional `Descripción / resumen`.

2. The tab also includes these narrative sections:
   - `Rasgos de personalidad`;
   - `Ideales`;
   - `Vínculos`;
   - `Defectos`;
   - `Historia del personaje`.

3. `Rasgos de personalidad`, `Ideales`, `Vínculos`, and `Defectos` should use compact cards/sections in the collapsed phone view:
   - title;
   - short one/two-line preview;
   - tap to open/expand for editing.

4. `Historia del personaje` is intentionally a larger narrative block because it is expected to contain substantially more text.

5. Responsive layout may place the four shorter narrative sections in two columns where width genuinely supports it, while preserving a phone-safe compact layout in narrow portrait mode.

## Character images

The `Trasfondo` UI must reserve space for up to two character images:

- primary image;
- secondary optional image.

The application does not need to implement full image persistence in the first slice if that is not yet technically appropriate, but the UI/layout must account for these image slots now so later image support does not require redesigning the tab.

If image attachment is not yet implemented, the controls must present an honest placeholder state rather than pretending that image storage works. Use vector iconography rather than text-character pseudo-icons.

The application must not impose semantic meaning such as portrait/body/alternate form on the two images; those are user choices.

## Notes boundary

`Notas` is a separate character-sheet section/domain and is **not** part of the `Trasfondo` tab.

Do not add a duplicate generic Notes field inside `Trasfondo` merely because one of the paper layouts contains a local notes area. The dedicated Notes concept will be designed separately.

## Paper-sheet references

Both supplied custom PDFs were reviewed as distinct design references rather than interchangeable copies.

The older sheet provides the richer narrative grouping (`Trasfondo`, personality traits, ideals, bonds, flaws, character story, notes and other traits), while V2 uses a more compact background grouping and moves feature/equipment responsibilities differently.

The digital UI intentionally combines the useful narrative semantics from those references without copying their paper geometry literally.

## Consequence

The detailed `Trasfondo` design gate is closed for these fields and presentation rules. Remaining expansion design work should continue with `Rasgos`, then `Conjuros`, plus the separate `Notas` domain and exact `Lanzador de conjuros` control placement/migration rules.

# D-0058 — Trasfondo structure, character images and Notes boundary

**Status:** Approved direction; detailed presentation still under discussion  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

After approving the next-build expansion with `Trasfondo`, `Rasgos` and `Conjuros`, the owner selected the proposed structured `Trasfondo` option and added two important requirements from the supplied custom-sheet references:

- the UI must reserve a place for one or two character images;
- `Notas` must be a separate section/domain rather than being embedded inside `Trasfondo`.

Both supplied PDFs were reviewed as distinct design references, including their rendered page layouts and differences in grouping.

## 1. Trasfondo top-level structure

Approved option A.

The top `Trasfondo` concept is split digitally into:

- `Nombre del trasfondo`;
- optional `Descripción / resumen`.

This is an intentional digital improvement over one undifferentiated paper text area.

## 2. Narrative fields

The `Trasfondo` domain should also support:

- `Rasgos de personalidad`;
- `Ideales`;
- `Vínculos`;
- `Defectos`;
- `Historia del personaje`.

These are narrative/free-text fields. `Historia del personaje` should be allowed substantially more text than the shorter personality/background sections.

`Otros Rasgos y Atributos` does not belong in `Trasfondo` in the digital model because the next build has a dedicated `Rasgos` tab.

## 3. Character images

The `Trasfondo` UI must reserve a place for **one or two images of the character**.

Current implementation may initially be only a UI placeholder if durable image attachment/storage is not ready in this build, but the layout must be designed with the future real feature in mind rather than added later as an afterthought.

Approved design intent:

- support a primary character image;
- support an optional second image;
- likely future uses include portrait + full-body/alternate appearance/reference image;
- do not pretend the placeholder is a working attachment control if image persistence is not implemented yet;
- a placeholder should be visibly identifiable as not-yet-functional if that is the actual state.

The exact storage mechanism, image size policy, crop behavior, and attachment lifecycle are not approved yet.

## 4. Notes boundary

`Notas` is **not part of `Trasfondo`**.

The supplied sheets include a dedicated final Notes page, so the digital design should preserve Notes as an apart/separate section or domain.

This decision does not yet determine whether `Notas` becomes an eighth top-level tab in the immediately next build or another separate navigation surface; that exact placement remains to be discussed.

Do not duplicate a generic Notes field inside `Trasfondo` merely for convenience.

## 5. PDF-reference distinction

The two supplied PDFs must be treated as different layout/grouping references rather than as duplicates.

Important differences observed:

- the older sheet keeps `Equipo`/`Monedas`/`Gemas-Joyas-Arte` on a dedicated page and gives narrative background material its own detailed page with `Trasfondo`, personality traits, ideals, bonds, flaws, character history, notes and `Otros Rasgos y Atributos`;
- the V2 sheet integrates more first-page material (including treasure/ammunition/other equipment areas) and uses page 3 to combine `Equipo`, a simplified `Trasfondo` block (`Trasfondo`, `Vínculos`, `Ideales`, `Historia`) and `Equipo Especial`;
- the V2 file contains two different character-main-sheet arrangements on pages 1 and 2, including different skills/saving-throw grouping, making it especially useful as a presentation-variation reference rather than a single canonical paper layout;
- both versions retain a dedicated spell page and a dedicated final Notes page.

The Android design may synthesize the strongest ideas from both references while following the established compact/responsive digital principles.

## Pending Trasfondo presentation details

Still to resolve before coding this new domain:

1. exact compact/expanded presentation for the narrative sections;
2. exact position/aspect ratio of the one/two image placeholders;
3. portrait vs landscape arrangement;
4. whether `Nombre del trasfondo` is always visible while other sections use progressive disclosure;
5. detailed persistence schema for the narrative fields;
6. eventual real image-attachment storage/permissions behavior (may remain future scope).

# D-0057 — Next-build character tabs and spellcaster visibility

**Status:** Partially approved / design details pending  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Approved next-build scope

Add three new real character-sheet tabs:

- `Trasfondo`
- `Rasgos`
- `Conjuros`

These are not placeholders; each must contain useful persistent character data.

Current four-tab sheet therefore expands from:

`General / Habilidades / Combate / Equipo`

to seven top-level character domains, with exact ordering still to be approved.

## Paper-sheet references supplied by owner

The owner re-uploaded both custom five-page paper sheets as design references for this pass.

### Trasfondo

The original sheet page 3 contains:
- Trasfondo
- Rasgos de Personalidad
- Ideales
- Vínculos
- Defectos
- Historia del Personaje
- Notas
- Otros Rasgos y Atributos

The V2 sheet page 3 contains:
- Trasfondo
- Vínculos
- Ideales
- Historia

and keeps Equipo / Equipo Especial adjacent on the same paper page.

Digital proposal: `Trasfondo` should consolidate the narrative/background fields rather than reproduce the paper geometry literally.

### Rasgos

Both paper designs contain a large `Rasgos y Atributos` / `Otros Rasgos y Atributos` area. This supports a separate digital `Rasgos` domain.

Digital proposal: represent reusable features/traits as a persistent ordered list rather than one giant undifferentiated text area. Possible minimal fields:
- Nombre
- Fuente/categoría optional (class, species/race, background, feat, item, homebrew, other)
- Descripción
- Notas optional
- manual order

This structure is a digital-product proposal, not literally specified by the paper sheet, and requires owner approval before coding.

### Conjuros

Both paper sheets page 4 provide a dedicated spell list grouped by levels 0 through 9, with spell-slot/spent-slot reference for leveled spells.

The current app already has Quick Magic on General with manual spell save DC, spell attack modifier, spellcasting ability and slot totals/spent state.

Digital direction:
- `Conjuros` becomes the detailed persistent spell-list domain;
- Quick Magic remains the fast summary on `General`;
- both must share the same authoritative spellcasting reference/slot state rather than duplicate it;
- spell list is grouped by level 0–9;
- the exact meaning of the checkbox shown beside spell lines on the paper sheet must be clarified before modeling it; do not assume prepared/known/favorite/etc.

## Proposed manual `Lanzador de conjuros` visibility switch — pending approval

Owner proposed a character-level checkbox/switch named `Lanzador de conjuros` that controls whether spellcasting-specific UI is shown.

Recommended behavior:
- manual boolean; never infer it from class;
- when ON: show Quick Magic on General and show `Conjuros` tab;
- when OFF: hide Quick Magic and hide `Conjuros` tab;
- turning it OFF must **not delete spellcasting data**;
- turning it back ON restores the same saved spellcasting data;
- if spell data already exists and the owner turns the switch OFF, the UI may explain that spellcasting sections are being hidden, not erased;
- this remains permissive for multiclass, innate casting, feats, homebrew and unusual campaign rules.

This visibility switch is not yet approved; it is a proposal for owner review.

## Navigation consequence

Seven top-level tabs are too many for a fixed equal-width phone tab row without crowding. Recommended next-build navigation is a horizontally scrollable single-line tab strip (or an equivalent compact top-level control) rather than shrinking labels or allowing wrapping.

Exact tab order is pending owner approval. Initial proposal:

`General / Habilidades / Combate / Equipo / Trasfondo / Rasgos / Conjuros`

For a non-spellcaster with the proposed visibility switch OFF, `Conjuros` disappears while the remaining tabs keep their relative order.

## Additional paper-sheet completion observations

The supplied sheets also retain a large final `Notas` page, and the original sheet includes a grid area beneath notes. These are useful future completion clues but are **not added to next-build scope by this decision**.

Likewise, paper-sheet fields such as Inspiration, alignment, race/species, XP/next level, treasure/ammunition/other categories should only be added if not already represented and after explicit owner review; the PDFs are references, not automatic requirements.

## Coding gate for these new domains

Do not code the three new tabs until the owner approves:
1. exact tab order/navigation behavior;
2. `Lanzador de conjuros` show/hide semantics;
3. minimum durable fields/interaction for `Trasfondo`;
4. minimum durable fields/interaction for `Rasgos`;
5. minimum durable fields/interaction for detailed `Conjuros`, including the paper checkbox meaning.

The already-accepted run #180 corrective UX backlog remains in scope for the same next build unless the owner changes that plan.
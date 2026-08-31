# D-0057 — Next-build character-sheet expansion direction

**Status:** Approved high-level direction; detailed schemas still under discussion  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

After closing owner phone QA of run #180, the owner proposed three additional character-sheet tabs for the next build: `Trasfondo`, `Rasgos`, and `Conjuros`, using the two supplied custom character-sheet PDFs as design references.

The owner explicitly agreed with the assistant's high-level expansion proposal and requested a detailed design discussion before coding.

## Approved high-level direction

1. Add three real character-sheet tabs:
   - `Trasfondo`
   - `Rasgos`
   - `Conjuros`

2. Target tab order:
   - `General`
   - `Habilidades`
   - `Combate`
   - `Equipo`
   - `Trasfondo`
   - `Rasgos`
   - `Conjuros`

3. With seven tabs, use a horizontally scrollable, single-line tab strip rather than shrinking labels excessively or allowing multi-line top-level tabs.

4. Add a manual character-level `Lanzador de conjuros` state/switch.

5. The `Lanzador de conjuros` state controls visibility of:
   - Quick Magic on `General`;
   - the `Conjuros` tab.

6. Turning `Lanzador de conjuros` OFF hides spellcasting UI but must **not delete or reset** existing spellcasting data. Turning it back ON restores access to the same persisted data.

7. Do not infer `Lanzador de conjuros` automatically from class. The project remains permissive for multiclass, ancestry/species magic, feats, homebrew, gifts and campaign exceptions.

8. Quick Magic remains the compact at-a-glance spellcasting reference. `Conjuros` becomes the detailed spell-list domain. They must share authoritative spellcasting/slot data rather than duplicate it.

9. The supplied paper PDFs are design/grouping references, not literal Android geometry specifications.

## PDF-derived design map

The supplied sheets support the following conceptual areas:

- narrative/background material: `Trasfondo`, `Rasgos de Personalidad`, `Ideales`, `Vínculos`, `Defectos`, `Historia del Personaje`, `Notas`;
- feature/trait material: `Rasgos y Atributos` / `Otros Rasgos y Atributos`;
- a dedicated spell-list page grouped by cantrips and spell levels 1–9;
- a separate final notes page.

The exact digital representation of these areas is still to be resolved through owner discussion.

## Pending detailed design gates

Before coding the three new tabs, resolve at minimum:

1. `Trasfondo`: exact fields, whether each is single text or list-like, and compact phone presentation.
2. `Rasgos`: exact entry fields, categories/source semantics, ordering and editor interaction.
3. `Conjuros`: exact spell-entry fields, meaning of the paper-sheet checkboxes, level grouping, prepared/known semantics if any, and interaction with Quick Magic slot state.
4. `Lanzador de conjuros`: exact control placement and default/migration behavior for existing characters.
5. Whether a dedicated `Notas` tab belongs in this build or remains future scope.

No production implementation of these three new domains should begin until the relevant detailed decisions are approved and checkpointed.
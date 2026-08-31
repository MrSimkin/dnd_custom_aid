# D-0059 — Spell entry and PC Settings behavior

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

During detailed design of the new `Conjuros` domain, the owner approved the remaining proposed spell-entry, source-management, slot-sharing, search/order, and `Lanzador de conjuros` migration/default behavior.

This decision builds on D-0058, which established stable spellcasting-source entities, `Todos` + per-source subtabs, multi-source spell associations, source-specific `Preparado`, one primary Quick Magic profile, and the character-level `PC Settings` location.

## Approved spell-entry model

Each conceptual spell supports:
- `Nombre`;
- `Nivel` (`0` for cantrip through `9`);
- one or more spellcasting-source associations;
- `Tiempo de lanzamiento`;
- `Alcance`;
- components `V`, `S`, `M`;
- optional material-component text;
- `Duración`;
- `Concentración`;
- `Ritual`;
- `Descripción`;
- optional `Notas` specific to that spell.

Casting time, range, duration and descriptive fields remain permissive/manual rather than being constrained to an exhaustive D&D rules enum.

## Prepared-state presentation

- In a specific source subtab, the checkbox means `Preparado` for that source association.
- In `Todos`, a multi-source spell must not present one misleading universal prepared checkbox.
- The combined view should instead expose compact source-specific prepared state, for example `Mago ✓ · Clérigo ○`, while source subtabs remain the simplest place to toggle preparation.

## Level grouping and shared slots

- Spells remain grouped as `Trucos`, then `Nivel 1` through `Nivel 9`.
- Level sections are collapsible.
- Empty/unconfigured levels may remain visually de-emphasized or collapsed.
- For levels 1–9, the `Conjuros` view may show the same slot total/spent pips used by Quick Magic.
- Quick Magic and `Conjuros` must mutate the same authoritative spell-slot state; never duplicate slot storage.

## Search and ordering

- Provide a compact spell search field at the top of `Conjuros`.
- Search operates inside the currently selected `Todos` or source view.
- Preserve manual drag-and-drop ordering within each spell level.
- Source filtering preserves the user-defined conceptual order rather than alphabetizing automatically.

## Spellcasting-source management

Provide a compact vector-icon source-management control within `Conjuros`.

Supported operations:
- add source;
- rename source;
- optionally associate source with a PC class row;
- reorder sources;
- delete source.

Rules:
- never automatically create a spellcasting source for every class row;
- class names may be offered as unobtrusive suggestions when creating/linking a source;
- custom/non-class sources remain allowed;
- deleting a source that still has spell associations requires an explicit warning;
- deleting one source must not delete a conceptual spell that is still associated with another source.

## `Lanzador de conjuros` defaults and migration

`Lanzador de conjuros` is a character-wide setting located in `PC Settings`.

Behavior:
- new PCs default to OFF;
- migration from run #180 sets it ON when existing Quick Magic data or configured spell-slot data exists;
- migrated characters without spellcasting data default to OFF;
- OFF hides Quick Magic and the `Conjuros` top-level tab;
- OFF never deletes/resets spell sources, spell records, source associations, prepared states, Quick Magic data or slot state;
- turning it ON restores access to the same durable data.

## Consequence

The detailed `Conjuros` product design is now sufficiently closed for implementation planning, subject to the broader next-build consolidation and the remaining `PC Settings` / separate `Notas` design gates.

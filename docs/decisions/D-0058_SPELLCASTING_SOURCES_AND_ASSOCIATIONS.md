# D-0058 — Spellcasting sources and spell-source associations

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

During detailed design of the new `Conjuros` tab, the owner approved a multiclass-friendly model that avoids duplicating spell records while still allowing source-specific state.

The paper character sheets establish spell grouping by cantrips and spell levels 1–9, but class/source subtabs are a digital enhancement for usability.

## Approved decisions

### 1. Stable spellcasting-source entities

Spellcasting sources are durable named entities associated with the character.

Examples:
- `Mago`
- `Clérigo`
- `Dote: Iniciado en la Magia`
- `Innato`
- a custom/homebrew source

Rules:
- a source may optionally link to one of the PC's class rows;
- a source is not required to be a class;
- custom sources are always allowed;
- source identity is stable even if its display name changes;
- do not infer legality or allowed spell lists from the source.

### 2. Conjuros source subtabs

The `Conjuros` tab provides an always-present `Todos` subtab plus one subtab for each spellcasting source.

Conceptual navigation:
- `Todos`
- `Mago`
- `Clérigo`
- other custom sources as present

Within the selected source view, spells remain grouped by:
- `Trucos`
- `Nivel 1` through `Nivel 9`

The source subtabs are filtered views of one spell domain, not separate spell databases.

### 3. One conceptual spell may belong to multiple sources

A conceptual spell record may have one or more source associations.

Example:
- `Detectar magia` may be associated with both `Mago` and `Clérigo`.

Behavior:
- `Todos` shows the conceptual spell once;
- each relevant source subtab shows the spell under that source;
- the app does not create duplicate conceptual spell records merely because a spell comes from multiple sources.

### 4. `Preparado` is source-specific

The owner approved the paper-sheet checkbox meaning as `Preparado`.

Because the same conceptual spell may be prepared through one source and not another, `Preparado` belongs to the spell-source association rather than universally to the spell record.

This state remains manual and permissive:
- no class-rule enforcement;
- no automatic preparation limits;
- no automatic un/preparation;
- the app assists rather than enforces character-building legality.

### 5. Quick Magic remains one primary profile for now

The existing Quick Magic concept remains a single primary at-a-glance spellcasting profile for the character.

It continues to expose the character's primary manual references such as:
- spell save DC;
- spell attack modifier;
- spellcasting ability;
- shared spell-slot state.

Multiple full Quick Magic profiles are not introduced in this build. The richer per-source spell organization lives in `Conjuros`.

### 6. PC Settings location

Character-wide switches belong in a dedicated `PC Settings` area opened from a gear/settings control for the current PC.

`Lanzador de conjuros` belongs there rather than inside `General` or application-wide Settings.

Current approved behavior for `Lanzador de conjuros`:
- ON: show Quick Magic and the `Conjuros` tab;
- OFF: hide both;
- turning it OFF must never delete/reset spell or slot data;
- turning it back ON restores the same data.

The PC Settings area is intentionally extensible for future character-wide options.

## Consequence

The remaining `Conjuros` design work can now define the spell-entry fields, compact presentation, source-management interaction, slot presentation, migration/default behavior, and PC Settings UX without ambiguity about multiclass/source ownership.
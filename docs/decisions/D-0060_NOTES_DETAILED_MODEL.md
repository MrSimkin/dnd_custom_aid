# D-0060 — Detailed Notes model

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

The supplied paper character sheets intentionally use `Notas` as an open, friendly, literal note-taking page rather than a rules subsystem. The owner previously selected a hybrid digital model: one large general note area plus optional titled note cards.

This decision closes the detailed `Notas` interaction model for the next build.

## Approved model

### 1. General notes

Provide one prominent unrestricted multiline `Notas generales` field.

Purpose:
- preserve the freeform paper-sheet experience;
- remain immediately usable without requiring the player to create structured notes;
- support arbitrary character-related scratchpad content.

No required categories, dates, tags, session numbers, colors, or other metadata are imposed.

### 2. Optional titled notes

Below the general notes area, provide an optional list of titled note cards.

Each titled note contains only:
- `Título`;
- `Contenido`.

The intentionally minimal schema keeps this domain lightweight rather than turning it into a general knowledge-management system.

### 3. Card interaction

Titled notes support:
- add;
- edit;
- delete with intentional confirmation where appropriate;
- manual drag-and-drop reorder;
- durable persistence of user-defined order.

Collapsed presentation shows:
- title;
- a short content preview.

Tap/open exposes the full note for reading/editing.

### 4. Responsive presentation

- `Notas generales` remains visually prominent and comfortably writable in portrait and landscape.
- Titled note cards may use multiple columns on wider/landscape layouts where that improves use of space.
- Responsive layout must not reduce the general freeform note area to an awkwardly narrow column.

### 5. Domain boundary

`Notas` remains:
- a separate character domain;
- a separate top-level tab;
- independent from `Trasfondo`, spell-specific notes, equipment notes, and trait notes.

Domain-specific note fields remain attached to their own records; they do not automatically duplicate into `Notas generales` or titled notes.

## Consequence

The `Notas` product design is closed for implementation planning. The next design gate is migration/default behavior for new and existing PCs, followed by the cross-domain ownership audit and final next-build implementation package.
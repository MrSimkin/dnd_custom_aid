# D-0051 — Character sheet tab order

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Decision

The next character-sheet build will use four real tabs in this exact order:

1. `General`
2. `Habilidades`
3. `Combate`
4. `Equipo`

`General` supersedes the current `Resumen` label. This is a rename of the existing overview tab, not a fifth tab.

## Intended domain boundaries

### `General`

Primary character overview and high-value persistent references, including the content currently living in `Resumen` after the approved V4 follow-up corrections.

Quick Magic remains a compact quick-reference block at the bottom of `General` even if a more complete magic/spell domain is added in a future phase.

### `Habilidades`

Abilities, saving throws and skills, retaining the approved `Por habilidades` / `Por atributo` presentation preference.

### `Combate`

Persistent reusable combat references and attacks/actions. It is not the future live encounter/turn tracker.

### `Equipo`

Persistent inventory, money, carried weight and richer special/magic-item details, including optional location and manual Sintonización state.

## Navigation principles

- No empty placeholder tabs.
- All four tabs contain real useful character data in the next build.
- Tab selection and unsaved draft state must preserve the already-passing Android recreation behavior across rotation, screen-off/on and normal navigation.
- Portrait and landscape may use different tab-strip geometry, but ordering and semantics remain stable.
- The new tabs must not introduce duplicate authoritative storage for values that already exist elsewhere.

## Out of scope

This decision does not create a dedicated `Magia`, `Rasgos`, `Notas` or other future tab. Those remain future possibilities only if a later content/domain need justifies them.
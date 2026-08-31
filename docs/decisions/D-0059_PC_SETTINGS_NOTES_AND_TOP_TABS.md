# D-0059 — PC Settings, Notes, and top-level tab navigation

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

After closing the detailed `Conjuros` design, the owner approved the remaining high-level navigation and character-wide configuration direction, and clarified the intended nature of `Notas` from the supplied paper sheets.

## Approved PC Settings decisions

1. A PC-level gear/settings control is accessible from the current PC header regardless of selected character-sheet tab.
2. The gear opens a dedicated `PC Settings` screen/page rather than a small dropdown/popover.
3. Only genuinely character-wide controls belong in PC Settings. Application-wide typography/theme/text scale remain in application Settings; spell-source management remains in `Conjuros`; image attachment belongs to `Trasfondo`.
4. Initially, `Lanzador de conjuros` is the only required PC-wide control.
5. `Lanzador de conjuros` ON shows Quick Magic and the `Conjuros` tab; OFF hides them without deleting or resetting spell, source, preparation, or slot data.
6. If spellcasting data already exists when the user turns the switch OFF, show a brief non-destructive confirmation explaining that the spellcasting UI will be hidden while data is retained.
7. The PC Settings area is intentionally extensible for future character-wide controls.

## Approved top-level navigation

Use a horizontally scrollable, single-line top-level tab strip.

Full conceptual order when spellcasting is enabled:

1. `General`
2. `Habilidades`
3. `Combate`
4. `Equipo`
5. `Trasfondo`
6. `Rasgos`
7. `Conjuros`
8. `Notas`

When `Lanzador de conjuros` is OFF, `Conjuros` is hidden and the remaining seven tabs keep their relative order.

Do not solve width pressure by shrinking labels excessively, wrapping top-level labels to multiple lines, or collapsing approved content geometry.

## Approved Notes direction

The paper-sheet `Notas` section was deliberately intended as a literal, friendly, open PC note-taking area rather than a rules subsystem.

The owner chose the hybrid digital option:

- a large, open `Notas generales` free-text area that preserves the paper-sheet spirit;
- plus an optional list of individually titled note cards for organization when desired.

The titled cards are an enhancement, not a requirement for basic use. A player can ignore them and use the large open note area exactly like the paper sheet.

`Notas` is a separate character domain and top-level tab. It does not belong inside `Trasfondo`.

## Consequence

The remaining work before production coding is no longer broad product discovery. The next gates are:

1. finish the exact `Notas` entry/card interaction and persistence semantics;
2. resolve migration/default behavior for the new domains and PC Settings state;
3. audit cross-domain ownership so values are not duplicated (especially Quick Magic / Conjuros, classes / spellcasting sources, equipment / traits, and background / notes);
4. define responsive behavior for eight top-level tabs plus nested Conjuros source tabs;
5. consolidate all run #180 corrective UX items together with the approved new-domain scope into one implementation package and checkpointed sequence;
6. only then begin production coding in small durable increments.

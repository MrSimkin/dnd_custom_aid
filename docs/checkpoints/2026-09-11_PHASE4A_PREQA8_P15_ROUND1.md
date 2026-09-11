# Phase 4A — preqa.8 — P15 Supercompact redesign Round 1

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** P15 ROUND 1 CLOSED / DESIGN RECONCILIATION IN PROGRESS  
**Implementation authorization:** NOT YET

## P15 — Supercompact conceptual redesign

The current Supercompact implementation is rejected as a tile/dashboard grammar. The owner wants a dense, coherent PC stat-block presentation inspired by the information hierarchy of modern D&D 5.5e Monster Manual/SRD stat blocks, without reproducing protected artwork or literal copyrighted page layout.

### Round 1 — accepted decisions

1. **One coherent stat block, not a dashboard of independent cards.**
   - Supercompact should read as one dense character reference surface with a stable hierarchy.
   - Accepted conceptual order begins with identity, then combat highlights, abilities, compact reference data, actions/traits/resources and other relevant sections.
   - Visual design, colors, typography and spacing remain original to this app; only the useful information hierarchy is borrowed.

2. **PC identity header.**
   - Header describes the actual PC rather than imitating monster metadata.
   - Show character name prominently.
   - Show species/race and Subraza when available, using the owner-facing terminology already accepted elsewhere.
   - Show class/subclass composition and levels, including multiclass in a compact human-readable form.
   - Background may appear as a quieter secondary identity detail when useful/space permits.
   - Lifecycle status appears only when meaningful (e.g. Retirada/Muerta), not as permanent noise such as `Activa`.

3. **High-frequency combat line follows P5/P2 shared rules.**
   - Compact top reference includes CA, current/max HP, temporary HP, initiative, speed and proficiency bonus.
   - Speed uses the global imperial-first plus metric-parentheses formatter from P5, e.g. `30 ft (9 m)`.
   - Supercompact must not keep its own special `−1 / +1` HP interaction grammar.
   - HP operations reuse the shared P2 compact `Daño | cantidad | Curar` behavior and canonical P1 state.
   - This reinforces one operational HP grammar across the app.

4. **Abilities use a compact stat-block table rather than six separate tiles.**
   - Present the six standard attributes in a compact comparative grammar with score, modifier and saving-throw information.
   - The exact visual compression may differ from a literal text table but should preserve fast horizontal comparison.
   - Custom Atributos, when present, participate below/in the same native grammar without pretending to be one of the canonical six.

5. **Compact reference lines only for data that exists.**
   - After abilities, show useful reference data such as passive perception, senses, languages, proficiencies, resistances or analogous character data when available.
   - Do not reserve empty rows merely because a monster stat block would conventionally have a field.
   - Supercompact remains a PC-driven projection of actual character data.

6. **Favorites accelerate Supercompact but no longer define it.**
   - The stat block has a useful stable structure even with zero Favorites.
   - Favorites may provide a small quick-access area near the beginning and/or visually elevate relevant entries inside their natural sections.
   - Favorites must not remain the primary structural section that determines what Supercompact contains.
   - Remove verbose empty-state copy such as the current `Aún no hay accesos rápidos...` from this deliberately dense view.

## Preserved underlying capabilities

The redesign may reuse existing state/plumbing for live HP, spell-slot, resource and Favorite projections where semantically correct. Acceptance of the new visual/IA model does not require discarding working persistence/state infrastructure.

## Explicit non-goals in Round 1

- Exact contents/order of Actions, Traits, spellcasting and resource sections are not yet fully frozen.
- Exact Favorite presentation is not yet fully frozen beyond its subordinate/accelerator role.
- Exact live-control density and which operational controls remain directly visible are not yet fully frozen.
- P16 landscape/available-height policy remains a separate point and must not be silently resolved only inside Supercompact.
- No implementation is authorized yet.

**Round 1 status:** `CLOSED / CONSOLIDATED`; continue P15 point-by-point design before closing P15.
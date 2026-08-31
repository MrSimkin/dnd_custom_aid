# Step checkpoint 01 — V4 follow-up gate answers

**Date:** 2026-08-31  
**Working branch:** `implementation/character-data-foundation`  
**Status:** SAFE RECOVERY POINT — NO FOLLOW-UP PRODUCTION CODING STARTED

## Just completed

The owner answered all six pre-implementation questions from `2026-08-31_V4_FOLLOWUP_PREIMPLEMENTATION.md`.

Authoritative record: `docs/decisions/D-0049_V4_FOLLOWUP_PREIMPLEMENTATION_APPROVALS.md`.

Approved at this checkpoint:

- eight-font audition: Manrope, Sora, Source Sans 3, Lexend, Barlow Condensed, Roboto Condensed, Archivo Narrow, Oswald;
- add real `Combate` and `Equipo` tabs in the next build;
- Quick Magic stays at bottom of `Resumen` even if a future detailed `Magia` tab exists;
- spell slots use manual totals + tappable spent/unspent marks;
- required numeric fields may be temporarily blank; Save warns that blanks will become zero, Cancel returns to editing, Confirm persists zero;
- calculated proficiency bonus uses the same interactive breakdown + `Ajuste adicional` pattern as other derived values.

## Operating-rule change

`AGENTS.md` now requires a durable Git checkpoint after every meaningful project step, even when short, before advancing to the next meaningful step.

## Remaining blocker / exact next step

Production coding is still blocked.

Next step: discuss and approve the actual minimum useful contents/interactions/data for the newly approved `Combate` and `Equipo` tabs. Do not infer those contents from the names alone.

# D-0049 — V4 follow-up pre-implementation approvals

**Status:** Partially approved; implementation still blocked on tab-content definition  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

After V4 run #107 manual QA was closed, the follow-up build was consolidated in `docs/handoffs/2026-08-31_V4_FOLLOWUP_PREIMPLEMENTATION.md` with six explicit owner questions before production coding.

The owner answered all six questions on 2026-08-31. Those answers are authoritative here.

## Approved answers

### Q1 — Expanded font audition

**Approved.** The next phone-QA build should expose this reversible eight-font audition set:

Normal-width sans:

1. Manrope;
2. Sora;
3. Source Sans 3;
4. Lexend.

Condensed / narrow:

5. Barlow Condensed;
6. Roboto Condensed;
7. Archivo Narrow;
8. Oswald.

These are audition candidates, not final branding. IBM Plex Sans Condensed remains rejected. Existing saved IBM Plex Sans Condensed preference should migrate intentionally to Roboto Condensed rather than falling back to an unrelated font.

### Q2 — New character-sheet tabs

The owner approved adding **two real tabs in the next build**:

- `Combate`;
- `Equipo`.

These must not be empty placeholder tabs. Their useful content/data must be discussed and approved before implementation begins. Therefore the tab names/domains are approved, but their exact MVP content remains a coding blocker.

### Q3 — Quick Magic placement

**Approved:** Quick Magic remains at the bottom of `Resumen` as an at-a-glance spellcasting reference even if a later detailed `Magia` tab is introduced.

A future `Magia` tab, if ever approved, would contain deeper spell-management content rather than replacing the quick summary in `Resumen`.

### Q4 — Spell-slot interaction

**Approved:** manual spell-slot tracking uses **tappable slot marks/pips**.

The character stores the manually entered total slots for each spell level. The UI renders that total as individual tappable marks suitable for toggling spent/unspent state during play.

This remains manual and permissive. The app does not infer slot totals from class/level or enforce spellcasting progression.

The detailed persistence semantics for spent-slot state should remain simple and must be resolved during the Quick Magic data-shape discussion before coding if not already obvious from the approved character-persistence model.

### Q5 — Required numeric field blank at Save

The owner **revised the assistant recommendation**.

Approved behavior:

- while editing, required numeric fields may be temporarily blank;
- if Save is pressed while one or more such fields remain blank, show a warning/confirmation rather than blocking save outright;
- the warning must make clear that blank required numeric values will be stored as `0`;
- if the user cancels, remain in the editor so values can be corrected;
- if the user confirms, normalize those blank required numeric values to `0` and persist them.

Do not silently coerce blank to zero without the warning/confirmation.

### Q6 — Proficiency bonus interaction

**Approved:** calculated proficiency bonus uses the same progressive-disclosure interaction as the other derived values.

Compact sheet:

- show final proficiency bonus.

On activation, show a calculation breakdown/editor including at least:

- total character level;
- standard proficiency bonus derived from level;
- `Ajuste adicional`;
- final total.

This preserves both consistency and the project's permissive homebrew/exception path.

## Remaining pre-coding gate

The original Q1–Q6 gate is answered, but production coding is **still blocked** because Q2 deliberately adds two new data domains whose actual contents have not yet been specified.

Before coding, discuss and approve:

1. the minimum useful durable content and interactions for `Combate`;
2. the minimum useful durable content and interactions for `Equipo`;
3. which existing `Resumen` fields, if any, are merely referenced from those tabs versus moved or duplicated;
4. whether any per-session/ephemeral state belongs in those tabs or must remain separate from persistent character-sheet state;
5. the resulting persistence/data-shape and targeted QA criteria.

Do not infer a full attacks/actions system or inventory system from the tab names alone.

## Relationship to existing decisions

This decision supplements:

- D-0045 character-sheet presentation;
- D-0046 derived values and adjustments;
- D-0047 Quick Magic;
- D-0048 Settings QA candidates.

Where this record is more specific and later, it controls the V4 follow-up build.

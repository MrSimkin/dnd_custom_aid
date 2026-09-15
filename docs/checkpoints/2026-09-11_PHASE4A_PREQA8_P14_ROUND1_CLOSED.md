# Phase 4A — preqa.8 — P14 Table Mode activation/state contract — Round 1

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** P14 ROUND 1 CLOSED / DESIGN RECONCILIATION CONTINUES  
**Implementation authorization:** NOT YET

## P14 — Table Mode activation/state contract

The current shared Table Mode interaction policy is conceptually correct: presentation and operational/session interactions remain available while structural character/configuration writes are blocked. The observed `40800` failure is in activation/state handling and UI affordances, not in the basic policy intent.

Current implementation inspection also identified a concrete mismatch: Table Mode activation is gated by the broad aggregate `hasUnsavedChanges`, while the intended contract only requires protection from unsaved **structural** edits. Operational/live state changes must not be misclassified as a structural draft that prevents activation.

**FULL APP AUDIT REQUIRED.** The repair must audit Player surfaces that participate in Table Mode so structural affordances, operational interactions, presentation controls and state persistence follow one consistent policy rather than ad-hoc per-screen behavior.

## Round 1 — owner-approved decisions

### 1. Only unsaved structural edits may block activation

- A clean persisted character must always be able to enter Table Mode.
- Unsaved structural edits may block direct activation until the user resolves them.
- Operational/session actions must **not** make Table Mode appear unavailable merely because they changed durable live state.
- Representative operational actions that must not block activation include HP damage/healing, temporary/session HP operations, spell-slot spend/recover, resource/ammunition use, conditions, Inspiration and comparable live table-state operations.
- The implementation must therefore distinguish structural-draft dirtiness from live operational persistence instead of using one undifferentiated `hasUnsavedChanges` aggregate as the Table Mode gate.

### 2. Dirty structural activation opens a warning/resolution window

If the owner attempts to enable Table Mode while structural edits are pending, do not leave the switch mysteriously disabled and require a manual navigation/save loop.

Open a focused warning/resolution window that:

- clearly states that structural changes are still unsaved;
- **shows which changes are unsaved**, rather than only presenting a generic warning;
- allows `Guardar y activar`;
- allows `Descartar y activar`;
- allows `Cancelar`.

`Guardar y activar` activates Table Mode only after the normal save succeeds. Existing validation/confirmation requirements remain authoritative; activation must not bypass required validation.

`Descartar y activar` intentionally restores the last persisted structural state, discards the pending structural draft, and then enables Table Mode.

`Cancelar` leaves both the pending draft and current Table Mode state unchanged.

The exact presentation/detail level of the unsaved-change summary remains to be refined in the next P14 round; the requirement to identify the pending changes is already accepted.

### 3. Structural editing affordances should normally disappear in Table Mode

- While Table Mode is active, structural actions should generally be removed from the working UI rather than leaving the app full of disabled edit/delete/duplicate/reorder controls.
- Useful presentation and operational controls remain visible and usable.
- If removing a particular control would make state or behavior confusing, it may remain visibly disabled/read-only instead; this is the exception, not the default pattern.
- The character UI keeps one concise visible indication that Table Mode is active and structural editing is protected, rather than adding repeated lock badges everywhere.

### 4. PC Settings remains accessible under the same structural/operational distinction

While Table Mode is active, PC Settings remains available because the user must be able to leave Table Mode and use legitimate non-structural functions.

Remain available:

- disable/leave Table Mode;
- open Application Settings;
- export backup where its independent safety conditions are satisfied;
- open Supercompact;
- interaction/presentation preferences such as character haptic feedback.

Become read-only/unavailable while Table Mode is active:

- structural spellcasting visibility/configuration;
- Inspiration sheet-visibility configuration;
- tab ordering;
- custom attributes;
- custom skills;
- custom markers;
- special-module structural configuration;
- character lifecycle/status structural changes;
- other character-structure configuration.

Turning Table Mode off immediately restores normal structural editing affordances/state rules.

### 5. Table Mode is durable until explicitly turned off

- Table Mode persists across leaving/reopening the character and across app restart.
- The app must not silently disable the protection merely because the process/session ended.
- Exiting Table Mode is an explicit owner action.

## Existing policy retained

Table Mode remains a selective interaction policy, not a blanket input lock:

- presentation-only interactions: allowed;
- operational/session interactions: allowed;
- structural character/configuration writes: blocked.

The repair must preserve this distinction and must not regress to a whole-screen pointer/input blocker.

## Round-1 regression boundary

Implementation/QA will need to prove at minimum:

- clean persisted character can always enable Table Mode;
- live operational changes do not create a false structural-dirty activation block;
- pending structural edits invoke the resolution window;
- the warning identifies the pending structural changes;
- `Guardar y activar`, `Descartar y activar`, and `Cancelar` have distinct correct state transitions;
- failed/blocked save does not activate Table Mode;
- structural affordances are removed/read-only consistently across representative Player surfaces;
- operational and presentation controls remain usable;
- PC Settings remains reachable and follows the same policy distinction;
- Table Mode survives character reopen and app restart until explicitly disabled.

## Explicit non-goals for this round

- No code implementation is authorized yet.
- P15 Supercompact content/redesign remains separate.
- P16 landscape/fixed-height policy remains separate.
- This round does not yet freeze the exact visual/detail grammar of the unsaved-change summary; that is the next P14 refinement.

**Round 1 status:** `CLOSED / CONSOLIDATED`.

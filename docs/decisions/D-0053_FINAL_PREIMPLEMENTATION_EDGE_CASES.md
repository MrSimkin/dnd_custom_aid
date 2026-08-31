# D-0053 — Final pre-implementation edge cases

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

After review of the consolidated follow-up implementation package, three residual edge cases remained before production coding could begin. The owner approved all three on 2026-08-31.

This decision supplements D-0046 through D-0052 and closes the final design questions for the follow-up character-sheet build.

## 1. Proficiency bonus at total character level 0

Approved.

A character with total class level `0` uses a standard proficiency-bonus base of **+2** for application calculation purposes.

This is an incomplete-character/default behavior, not a claim that official D&D defines a level-0 player-character progression row.

The purpose is to keep newly created or partially entered characters usable and to preserve the existing default behavior while the character has no positive class levels.

The normal approved progression remains:

- total level 0–4: `+2` for application purposes;
- 5–8: `+3`;
- 9–12: `+4`;
- 13–16: `+5`;
- 17–20: `+6`.

`Ajuste adicional` remains available for exceptions/homebrew.

## 2. Class level may persist as 0 after blank-save confirmation

Approved.

The global required-numeric editing rule applies consistently to class level:

- while editing, the class-level field may be temporarily blank;
- if Save is pressed while it remains blank, include it in the warning that blank required numeric values will be stored as `0`;
- Cancel returns to editing;
- Confirm stores level `0`.

A class row at level `0` is treated as incomplete/permissive character data. The application must not reject the save merely because the row is not yet a legal/complete D&D character-build state.

This supersedes the current repository validation that requires every class level to be strictly positive.

No additional character-builder legality enforcement is introduced.

## 3. Inventory weight is per-unit weight

Approved.

For structured inventory weight:

- `Peso` means **weight per unit**;
- carried contribution for an item is `quantity × unit weight`;
- the displayed carried-weight total sums those contributions for items whose structured weight is present;
- missing weight contributes nothing and never blocks saving.

Example:

- quantity `20`;
- weight per unit `0.05 lb`;
- contribution to carried total `1 lb (0,5 kg)` using the approved game-friendly approximate metric presentation.

The UI should label or otherwise present the field clearly enough that users understand it is unit weight rather than whole-stack weight.

## Design-gate consequence

The final residual pre-coding questions are now answered.

The detailed target remains `docs/handoffs/2026-08-31_FOLLOWUP_IMPLEMENTATION_REVIEW_PACKAGE.md`, amended by this decision where necessary.

The follow-up character-sheet design gate is **CLOSED / APPROVED FOR IMPLEMENTATION**.

Implementation must continue using the mandatory checkpoint rule in `AGENTS.md`: every meaningful implementation increment is checkpointed in Git before proceeding to the next increment.

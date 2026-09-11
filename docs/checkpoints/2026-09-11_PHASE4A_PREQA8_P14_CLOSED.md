# Phase 4A — preqa.8 — P14 Table Mode closure

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent decision log:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`  
**Status:** P14 CLOSED / READY FOR REPAIR SPEC  
**Implementation authorization:** NOT YET

## P14 — Table Mode activation/state contract

The owner accepts the core Table Mode policy: presentation and operational/session interactions remain available while structural character/configuration writes are blocked. The observed `preqa.8 / 40800` defect is primarily the activation/state gate and its UI communication, plus the need to prove that the structural/operational distinction is respected across all Player surfaces.

## Confirmed implementation mismatch

The current implementation derives Table Mode enablement from a broad `hasUnsavedChanges` aggregate. That aggregate includes multiple draft/state domains and is too coarse for the intended contract. Table Mode activation must be blocked only by genuinely pending **structural** edits, not by already-persisted operational/session activity.

## Round 1 — accepted activation/state behavior

1. **Only unsaved structural edits may block activation.**
   - Pending edits such as character identity/stat/configuration changes, edited traits, edited attacks, custom-structure changes, etc. may block immediate activation until resolved.
   - Operational/session actions such as HP changes, spell-slot spend/recovery, ammunition/consumable use, conditions, Inspiration, resource spend/recovery and similar live play state must not be treated as an unsaved structural draft merely because they changed recently.

2. **Do not leave the Table Mode control mysteriously disabled.**
   - If structural edits are pending and the owner attempts to enable Table Mode, open a warning/review window.
   - The window must show which structural changes are currently unsaved so the owner can make an informed decision.
   - Actions are:
     - `Guardar y activar`;
     - `Descartar y activar`;
     - `Cancelar`.

3. **While Table Mode is active, suppress structural editing affordances where practical rather than filling the UI with disabled controls.**
   - Presentation/browsing controls remain available.
   - Operational/live controls remain available.
   - Structural add/edit/delete/duplicate/reorder/configuration affordances disappear where that is clearer and safer.
   - A disabled/read-only presentation may remain where removing the control would make the state unclear.
   - Keep one concise visible Table Mode/read-only indication rather than adding padlocks everywhere.

4. **PC Settings remains accessible in Table Mode.**
   - Available while active: disabling Table Mode, Application Settings, backup/export where otherwise valid, Supercompact, presentation/interaction preferences such as haptics.
   - Character-structural settings remain read-only/unavailable: tab ordering, custom attributes/skills/markers, special-module configuration, lifecycle/status mutation and other structural character configuration.
   - Turning Table Mode off restores those controls immediately.

5. **Table Mode persists until explicitly turned off.**
   - Closing/reopening the character or restarting the app must not silently disable it.

## Round 2 — accepted unsaved-changes window and transition semantics

1. **Show meaningful structural diffs.**
   - The warning should summarize actual pending structural changes, grouped sensibly by area/domain.
   - Simple scalar changes should show useful old → new values where practical, e.g. `Velocidad: 30 → 35 ft`.
   - Collection/complex edits should use concise human-readable descriptions, e.g. `Ataque “Daga” agregado`, `Rasgo “Visión en la oscuridad” modificado`, `1 rasgo eliminado`.
   - Never expose raw JSON, internal IDs or noisy low-level field dumps merely to prove that a change exists.

2. **The change list scrolls when necessary while actions remain readily reachable.**
   - The review window follows the adaptive-dialog principles already accepted under P9.
   - Large change sets must not force an absurdly tall dialog or hide the transition actions.

3. **`Descartar y activar` discards all pending structural edits as one explicit operation.**
   - Do not turn this safety transition into a selective merge UI with per-change checkboxes.
   - The displayed list exists to make the discard decision informed.

4. **`Guardar y activar` uses normal validation and activates only after a successful structural save.**
   - Existing validation/confirmation semantics remain authoritative.
   - If save fails or requires correction, Table Mode remains OFF.
   - Only after a genuine successful save does activation occur.

5. **Turning Table Mode OFF is immediate and requires no confirmation.**
   - Disabling the protection does not itself lose data and therefore should not trigger a pointless confirmation dialog.

## FULL APP AUDIT REQUIRED

P14 is explicitly marked **FULL APP AUDIT REQUIRED**.

The repair must audit the complete Player UI for correct classification and behavior of interactions under Table Mode. It is not sufficient to fix the activation switch in PC Settings.

The audit must verify, for every relevant surface/control, whether the interaction is:

- `PRESENTATION` — allowed in Table Mode;
- `OPERATIONAL` — allowed in Table Mode;
- `STRUCTURAL` — blocked/suppressed in Table Mode.

Representative domains include, but are not limited to:

- General / Overview;
- Skills / custom skills / custom attributes;
- Combat;
- Dice;
- Management;
- Equipment/currencies/usage;
- Background;
- Traits/features;
- Spells/spellcasting sources;
- Notes;
- conditional/special modules;
- PC Settings and their focused subpages;
- Supercompact;
- backup/export and navigation/presentation controls.

The audit must also catch callbacks that bypass the intended structural-write barrier even if the visual affordance was hidden correctly.

## Regression boundary

Implementation/QA must prove at minimum:

- a clean persisted character can always enable Table Mode;
- persisted live/session operations do not falsely block activation;
- genuine pending structural edits invoke the review window;
- the review window accurately summarizes representative scalar and collection changes;
- `Guardar y activar` does not activate before a successful validated save;
- `Descartar y activar` restores the last persisted structural state and then activates;
- `Cancelar` leaves state and mode unchanged;
- Table Mode persists across close/reopen and app restart;
- disabling Table Mode is immediate and restores structural controls;
- presentation and operational interactions remain usable across representative surfaces;
- structural writes remain impossible across representative surfaces, including via hidden callback paths;
- PC Settings remains accessible but respects the same interaction classification;
- no blanket input-blocking overlay is introduced.

## Explicit non-goals

- P14 does not redesign Supercompact content/layout; that remains P15.
- P14 does not redefine what game mechanics count as an operational action beyond the established shared interaction policy; ambiguous individual controls should be classified during the required full-app audit.
- P14 does not authorize implementation yet.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

# D-0061 — Spellcasting navigation and PC Settings direction

**Status:** Approved direction; detailed spell schema still under discussion  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

During detailed design of the new `Conjuros` tab, the owner approved:

- the paper-sheet checkbox meaning `Preparado`;
- retaining one primary Quick Magic profile for now rather than introducing multiple independent arithmetic profiles;
- moving the character-level `Lanzador de conjuros` control into a dedicated PC Settings area rather than placing it directly in General.

The owner also added a requirement for multiclass spellcasters: the detailed spell list must support an `Todos` view plus per-class/source sub-tabs so spells from different casting classes can be distinguished easily.

The supplied paper sheets establish spell grouping by cantrips and spell levels 1–9, but they do not define digital multiclass source tabs or PC Settings. Those are digital product decisions derived from the application's needs.

## Approved spellcasting navigation direction

1. `Conjuros` has a top-level internal sub-navigation for spellcasting source.
2. There is always an `Todos` sub-tab showing the complete character spell list.
3. Additional sub-tabs separate spell lists by spellcasting class/source, for example:
   - `Mago`
   - `Clérigo`
   - another spellcasting class/source as applicable.
4. The same spell data remains one authoritative collection; these are filtered views, not duplicate spell lists.
5. Inside each source view, spells remain grouped by `Trucos` and levels 1–9.
6. This source grouping is organizational only. It does not create separate automatic DC/attack/progression engines.
7. Quick Magic remains one primary compact spellcasting profile in this build.
8. A spell retains an explicit source association so the app can place it in the correct source sub-tab and in `Todos`.
9. Non-class spell origins (feat, ancestry/species, item, homebrew, etc.) must remain representable. The exact UI for such custom sources is still to be finalized, but the data model must not assume every source is a class.

## Approved paper checkbox meaning

The unlabeled checkbox beside spell rows is interpreted digitally as manual `Preparado` state.

- It is a manual marker.
- The app does not infer or enforce whether a class should prepare spells.
- It does not reject prepared/unprepared states based on class rules.

## PC Settings

A dedicated character-level `PC Settings` menu/screen is approved, accessed through a gear/settings control associated with the character sheet.

Purpose:

- hold options that affect the whole PC or multiple tabs;
- avoid putting global character switches inside one content tab;
- provide a home for future cross-character-sheet options of the same nature.

`Lanzador de conjuros` belongs in PC Settings.

Behavior remains:

- ON: Quick Magic and the `Conjuros` tab are visible;
- OFF: both are hidden;
- OFF never deletes or resets spellcasting data;
- turning it back ON restores the same persisted data.

Migration direction remains:

- existing characters with any persisted Quick Magic/spellcasting data should migrate with `Lanzador de conjuros = ON`;
- characters without spellcasting data may default OFF.

## Still pending

Before coding `Conjuros`, still resolve:

1. exact spell-source representation and how per-class/custom source subtabs are created/renamed;
2. whether one spell may belong to more than one source for the same character;
3. exact compact spell-card fields and editor fields;
4. prepared-filter interaction, if any, beyond the per-entry marker;
5. spell ordering rules within a level/source view;
6. exact PC Settings navigation/presentation and any additional next-build settings that belong there.

No production code for this new spellcasting domain should begin until these remaining choices are approved and checkpointed.
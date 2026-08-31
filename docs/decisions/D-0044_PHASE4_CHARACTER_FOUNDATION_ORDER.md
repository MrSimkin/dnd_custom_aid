# D-0044 — Phase 4 begins with character data foundation, then combat tracker

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

## Decision

Phase 4 implementation order is:

1. establish the Android/local character data foundation and usable character workflow;
2. build the DM combat tracker on top of stable character data needed for PC reference.

The character work is not required to finish every eventual character-sheet, PDF, audit, synchronization or account feature before combat begins. It must establish enough stable character data that later DM quick views and combat can consume real character records rather than temporary duplicate participant data.

## First character-slice boundary

The first character slice includes:

- stable character UUID and explicit campaign association;
- character name and lifecycle status;
- last-saved/updated freshness data;
- multiclass-aware class/level entries;
- hit-die size and remaining hit dice per class entry;
- six ability scores;
- Armor Class;
- maximum/current/temporary HP;
- initiative modifier;
- speed;
- proficiency bonus;
- six final saving-throw modifiers;
- passive Perception;
- optional spell save DC;
- the complete standard D&D skill set, storing each skill's final modifier plus descriptive proficiency/expertise state;
- local SQLDelight persistence and Android create/list/open/edit/save behavior.

## Rules philosophy for stored values

The application does not automatically enforce character-building legality or recalculate final mechanical values in this slice.

Final saved values are authoritative character-sheet data. Descriptive facts such as proficiency/expertise may coexist with final modifiers that differ from ordinary formula results. This intentionally supports campaign gifts, homebrew, house rules and other owner-approved exceptions.

Do not impose ordinary D&D caps or legality validation merely because a value is unusual. Structural validation such as nonblank names, positive class levels and positive hit-die sizes is still appropriate.

A future character-check/validation feature may be useful, but it is deliberately deferred until late in development after character-sheet behavior, exceptions and supported rules have been clarified substantially. It must not be treated as a prerequisite for character storage or editing.

## Deferred from this first slice

- spell lists and spell slots;
- inventory/equipment and currencies;
- attacks/actions;
- features/traits;
- broader proficiencies/languages;
- biography/personality content;
- PDF generation;
- grouped audit-history implementation;
- account ownership/control UI;
- hosted synchronization;
- automatic rules enforcement or character legality checks.

These remain subject to the approved MVP/product roadmap and later focused slices.

## Combat relationship

Persistent character-sheet state and live combat working state remain separate under D-0025/D-0026. A later combat tracker should consume the relevant character data/projection when a PC is added, then maintain independent live encounter/combat state where appropriate rather than mutating the durable character sheet automatically.

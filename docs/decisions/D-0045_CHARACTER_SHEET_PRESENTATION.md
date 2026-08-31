# D-0045 — Character-sheet presentation preferences

**Status:** Approved where explicitly stated below; unresolved items remain pending  
**Date:** 2026-08-30  
**Decision owner:** Project owner

## Context

The first two Android phone character-sheet QA passes established that the durable character data/persistence model is functionally sound, while the editor presentation still needs another focused UX iteration. The owner supplied two custom five-page paper character sheets as grouping/design references and clarified how their alternative layouts should inform the digital UI.

Repository references:

- `assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf`
- `assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf`
- `docs/CHARACTER_SHEET_UX.md`

The PDFs are design inspiration and presentation references; they are not instructions to reproduce a paper page literally on Android.

## Approved decisions

### 1. The two paper main-page layouts are alternatives

The relevant first-page variants in the supplied paper sheets represent **two alternative designs for the same main character-sheet page**. A player chooses the organization they prefer; the alternatives are not intended to be used simultaneously as separate required pages.

### 2. Digital ability/skill presentation has two views

The Android character sheet should support two presentation modes for the relationship between abilities, saving throws and skills:

1. **By skills** — the default. Abilities remain visible, while skills are presented as a skills-oriented list and each skill visibly identifies its associated ability.
2. **By attribute** — abilities act as the organizing groups, with the related saving throw and skills presented with the relevant ability.

The default is **By skills**.

This is a presentation preference, not a difference in the underlying character data. It should therefore not alter or duplicate the durable character model.

A small character-sheet gear/settings control is the intended home for this sheet-specific presentation preference unless later usability testing demonstrates a better placement.

### 3. Class selector source and custom escape path

Class entry should use a known-class selector built from the **SRD 5.2.1 class list**, plus:

- **Artífice**;
- **Otro**.

Selecting **Otro** exposes an open field for a nonstandard/homebrew class name.

The permissive product rule remains controlling: the selector accelerates common entry but does not reject custom/homebrew character data.

End-user labels are Spanish under C-0006. When implemented, the SRD-derived list should be sourced from the exact SRD 5.2.1 reference rather than reconstructed from memory.

### 4. Global Settings belongs outside the sheet data

The next character-sheet build should introduce the first small global application Settings surface for presentation preferences.

Initial global Settings scope:

- font size;
- font family/style;
- theme.

Theme choices must include at least:

1. System;
2. Light;
3. Dark;
4. Light Gray;
5. Dark Purple.

Dark Purple is an explicit owner preference.

These settings affect application presentation and must not be stored as character mechanics/data.

### 5. Saving throws are no longer waiting on an additional owner explanation

The earlier QA note that saving-throw presentation was "pinned" pending a later explanation is resolved. The owner only wanted the supplied paper PDFs reviewed because they already demonstrate the intended presentation alternatives.

The PDFs therefore provide the design reference for the next saving-throw layout discussion. No additional hidden/unsupplied saving-throw concept should be assumed.

## Pending owner decision — tab structure

The owner wants tabbed organization in the digital character sheet and wants to review the agent's **first proposed tab structure** before it is implemented as a durable layout convention.

No exact tab names, count or section assignment are approved by this decision.

## Still unresolved

The following remain intentionally open unless separately approved:

- exact font families/styles offered by the first Settings menu;
- exact font-size steps/control style;
- final tab structure;
- whether changing skill proficiency/training should ever recalculate the stored final skill modifier;
- exact visual styling beyond the grouping/density directions already recorded by QA.

## Consequence for the next Android QA build

Before producing the next owner-test APK, implementation should:

- preserve editor/navigation state across rotation and screen-off/on recreation;
- fully solve keyboard/IME obstruction of lower content;
- further compact the editor, including six abilities in one row where readability permits;
- compact each class entry toward one-row tabletop order such as `3d10`;
- use the SRD 5.2.1 class selector + Artífice + Otro/custom path;
- make skill rows materially more compact;
- show each skill's associated ability;
- implement both ability/skill presentation modes with **By skills** as default;
- introduce the small global Settings surface described above;
- preserve stable grouping in landscape as dynamic class rows are added;
- obtain owner approval for the proposed tab structure before treating it as final.

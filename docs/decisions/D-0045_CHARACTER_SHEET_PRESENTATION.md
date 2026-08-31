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

This preference is a **user/device presentation preference**, not a per-character property. Changing it should affect how characters are presented on that device/user context rather than writing layout state into character mechanics/data.

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

Approved font-size scale options are:

1. **80%**;
2. **90%**;
3. **100%**;
4. **115%**;
5. **130%**.

The default font-size step is **not yet approved**.

Font-family/style policy for the first Settings menu:

- **no serif option**;
- offer **two distinct sans-serif families** plus **one condensed sans-serif family**;
- avoid the overused/default-feeling families such as Roboto, Arial and Helvetica;
- exact family names remain to be selected with owner approval before implementation becomes final.

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

### 6. Initial tab structure

For the current implemented character-data scope, the first tab structure is approved as:

1. **Resumen** — fast-reference character overview containing identity/status, compact class/level/hit-dice entries, all six ability scores, AC, HP, initiative, speed, proficiency bonus, passive Perception and optional spell save DC.
2. **Habilidades** — skills/saving-throw/ability relationship area, supporting the approved By skills and By attribute presentation modes.

Tabs should be added **when their feature domains become relevant and implemented**, rather than creating empty placeholders for future features. Likely later domains such as combat/actions, spells or equipment may receive tabs when those slices actually exist, but their names/order are not approved by this decision.

A compact persistent editor header should keep navigation/character identity/save/settings reachable while moving between the current tabs; exact styling remains implementation-level unless QA identifies a consequential UX issue.

## Still unresolved

The following remain intentionally open unless separately approved:

- exact three font families offered by the first Settings menu;
- default font-size step;
- whether changing skill proficiency/training should ever recalculate the stored final skill modifier;
- exact visual styling beyond the grouping/density directions already recorded by QA.

## Consequence for the next Android QA build

Before producing the next owner-test APK, implementation should:

- preserve editor/navigation state across rotation and screen-off/on recreation;
- fully solve keyboard/IME obstruction of lower content;
- implement the approved **Resumen** and **Habilidades** tabs without speculative empty future tabs;
- further compact the editor, including six abilities in one row where readability permits;
- compact each class entry toward one-row tabletop order such as `3d10`;
- use the SRD 5.2.1 class selector + Artífice + Otro/custom path;
- make skill rows materially more compact;
- show each skill's associated ability;
- implement both ability/skill presentation modes with **By skills** as default and persist that choice as a user/device presentation preference rather than character data;
- introduce the small global Settings surface with font sizes **80/90/100/115/130%**, two non-generic sans options, one condensed sans option, and the five approved themes;
- preserve stable grouping in landscape as dynamic class rows are added.

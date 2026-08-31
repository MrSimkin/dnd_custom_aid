# D-0045 — Character-sheet presentation preferences

**Status:** Approved where explicitly stated below; unresolved items remain pending  
**Date:** 2026-08-30  
**Decision owner:** Project owner

## Context

The Android phone character-sheet QA passes established that the durable character data/persistence model is functionally sound while presentation is being iterated. The owner supplied two custom five-page paper character sheets as grouping/design references and clarified how their alternative layouts should inform the digital UI.

Repository references:

- `assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf`
- `assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf`
- `docs/CHARACTER_SHEET_UX.md`

The PDFs are design inspiration and presentation references; they are not instructions to reproduce a paper page literally on Android.

## Approved decisions

### 1. The two paper main-page layouts are alternatives

The relevant first-page variants in the supplied paper sheets represent **two alternative designs for the same main character-sheet page**. A player chooses the organization they prefer; the alternatives are not intended to be used simultaneously as separate required pages.

### 2. Digital ability/skill presentation has two views

The Android character sheet supports two presentation modes for the relationship between abilities, saving throws and skills:

1. **Por habilidades / By skills** — the default. Abilities remain visible, while skills are presented as a skills-oriented list and each skill visibly identifies its associated ability.
2. **Por atributo / By attribute** — abilities act as the organizing groups, with the related saving throw and skills presented with the relevant ability.

The default is **Por habilidades / By skills**.

This is a presentation preference, not a difference in the underlying character data. It must not alter or duplicate the durable character model.

This preference is a **user/device presentation preference**, not a per-character property. Changing it affects how characters are presented on that device/user context rather than writing layout state into character mechanics/data.

V3 QA confirmed that both organizations are understandable and structurally useful. The owner prefers the selector itself to become a **compact two-state segmented/slider-like control with a clear active indicator**, rather than the current dropdown-style control.

### 3. Class selector source and custom escape path

Class entry uses a known-class selector built from the **SRD 5.2.1 class list**, plus:

- **Artífice**;
- **Otro**.

Selecting **Otro** exposes an open field for a nonstandard/homebrew class name.

The permissive product rule remains controlling: the selector accelerates common entry but does not reject custom/homebrew character data.

End-user labels are Spanish under C-0006. The SRD-derived list must be sourced from the exact SRD 5.2.1 reference rather than reconstructed from memory.

V3 QA confirmed the class choices and custom path work. **Artífice must participate in the alphabetic class ordering rather than being appended after the SRD list. `Otro` remains the final escape-path item rather than part of the alphabetic class names.**

### 4. Global Settings belongs outside the sheet data

The character sheet includes a small global application Settings surface for presentation preferences.

Initial global Settings scope:

- font size;
- font family/style;
- theme.

Approved font-size scale options remain:

1. **80%**;
2. **90%**;
3. **100%**;
4. **115%**;
5. **130%**.

The approved default remains **100%**.

V3 QA found that UI menus/layout begin to look wrong above 100%. This is an implementation/responsiveness defect to correct before changing the approved scale itself; if the larger steps cannot be made visually sound, the owner should review revised steps rather than silently changing them.

Initial V3 font candidates were:

1. **Manrope** — sans-serif;
2. **Atkinson Hyperlegible Next** — sans-serif;
3. **Barlow Condensed** — condensed sans-serif.

V3 QA supersedes the initial Atkinson choice: **Atkinson Hyperlegible Next is not accepted in its current result** (it looked wrong and may not have loaded/rendered as intended). Keep Manrope and Barlow Condensed for now and add/try **another condensed family** as the replacement candidate. Exact replacement family remains unresolved and requires owner QA.

There is still **no serif option** unless the owner later requests one.

Theme choices remain:

1. System;
2. Light;
3. Dark;
4. Light Gray;
5. Dark Purple.

V3 QA found the current palettes insufficiently differentiated:

- **Dark Purple** is too close to ordinary Dark and reads too wine/burgundy rather than clearly purple;
- **Light Gray** is almost indistinguishable from Light.

The theme identities remain approved, but the actual palettes must be revised so Light Gray is visibly gray and Dark Purple is visibly purple and meaningfully distinct from Dark.

These settings affect application presentation and must not be stored as character mechanics/data.

### 5. Saving throws are no longer waiting on an additional owner explanation

The earlier QA note that saving-throw presentation was "pinned" pending a later explanation is resolved. The owner only wanted the supplied paper PDFs reviewed because they already demonstrate the intended presentation alternatives.

The PDFs therefore provide the design reference for saving-throw layout. No additional hidden/unsupplied saving-throw concept should be assumed.

### 6. Initial tab structure

For the current implemented character-data scope, the tab structure is approved as:

1. **Resumen** — fast-reference character overview containing identity/status, compact class/level/hit-dice entries, all six ability scores, AC, HP, initiative, speed, proficiency bonus, passive Perception and optional spell save DC.
2. **Habilidades** — skills/saving-throw/ability relationship area, supporting the approved By skills and By attribute presentation modes.

V3 intended-device QA explicitly confirmed that this tab structure is understandable and works better than the previous continuous editor.

Tabs should be added **when their feature domains become relevant and implemented**, rather than creating empty placeholders for future features. Likely later domains such as combat/actions, spells or equipment may receive tabs when those slices actually exist, but their names/order are not approved by this decision.

A compact persistent editor header keeps navigation/character identity/save/settings reachable while moving between the current tabs; exact styling remains implementation-level unless QA identifies a consequential UX issue.

### 7. Skill training control

V3 QA confirmed that the compact skill training/proficiency control **type is liked and should be retained**. Its current abbreviated letters are not sufficiently clear, so the next iteration should improve the visible state labels/indicator without reverting to the older oversized control.

Changing `Competente` / `Pericia` continues to leave the stored final numeric modifier untouched unless a later explicit decision changes that behavior.

### 8. Combat-reference grouping

`Referencia de combate` is accepted as a useful semantic group, but **its internal ordering is not yet accepted**. The next layout pass should compare the order against the two stored custom paper-sheet references and favor coherent subgroups (core combat reference, HP, secondary reference) rather than treating all values as one arbitrary sequence.

Exact revised order remains pending owner review after that comparison.

## Still unresolved

The following remain intentionally open unless separately approved:

- whether changing skill proficiency/training should ever recalculate the stored final skill modifier;
- exact replacement for Atkinson Hyperlegible Next;
- exact revised internal ordering of `Referencia de combate`;
- exact visual styling beyond the grouping/density directions already recorded by QA;
- one owner QA note written as "no modifiers on sheet, some over abreviations" needs clarification before it is translated into a concrete implementation change.

## Consequence for the next Android QA build

The next focused presentation pass should preserve the V3 functional successes while:

- reducing remaining unnecessary class/box internal padding;
- fixing the narrow `dX` hit-die control so `d8`, `d10`, etc. never stack vertically;
- alphabetizing Artífice with the class names while keeping `Otro` last;
- correcting menu/layout behavior at font scales above 100%;
- replacing/retesting Atkinson with another condensed family candidate;
- increasing visual distinction between Light and Light Gray;
- making Dark Purple clearly purple and distinct from Dark;
- replacing the By skills / By attribute dropdown with a compact two-state control with an active indicator;
- retaining the liked compact skill-training control while making its states/letters clearer;
- reviewing and revising `Referencia de combate` ordering against the custom paper-sheet references;
- preserving the now-passing rotation/screen-off navigation state, IME/keyboard accessibility, landscape behavior and persistence.

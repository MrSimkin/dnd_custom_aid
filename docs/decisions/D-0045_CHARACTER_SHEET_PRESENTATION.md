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

V3 QA confirmed that both organizations are understandable and structurally useful. The owner wants the selector itself to become a **compact two-state segmented/slider-like control with a clear active indicator**, rather than the current dropdown-style control.

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

Typography direction after V3 clarification:

- **Manrope** — retained normal-width sans candidate;
- **Barlow Condensed** — retained condensed candidate;
- **IBM Plex Sans Condensed** — approved as an additional condensed candidate for the next QA pass;
- **Atkinson Hyperlegible Next** — removed from the candidate set;
- a second normal-width sans must replace Atkinson. **Sora** is the current proposed V4 candidate and should be tested before being treated as final.

The intended V4 test set therefore has two normal-width sans choices and two condensed choices. There is still **no serif option** unless the owner later requests one.

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

### 5. Saving throws and SRD-derived context

The earlier QA note that saving-throw presentation was "pinned" pending a later explanation is resolved. The owner only wanted the supplied paper PDFs reviewed because they already demonstrate the intended presentation alternatives.

A subsequent owner clarification requested a quick but thorough character-creation review against the SRD. The official Spanish SRD 5.2.1 confirms:

- an ability score has a corresponding **modificador por característica** written beside the score;
- a proficient saving throw is the relevant ability modifier plus the **bonificador por competencia**;
- a non-proficient saving throw normally uses the relevant ability modifier;
- a proficient skill is its associated ability modifier plus the proficiency bonus;
- Percepción pasiva is based on the Wisdom (Perception) check modifier.

Consequences already approved at presentation level:

- ability **modifiers must be visible and automatically derived from their ability scores** rather than entered as independent fields;
- saving throws need a proficiency control as well as their displayed total;
- the saving-throw proficiency control **must not simply reuse the skill training control**, because skills have a three-state none/proficient/expertise concept while ordinary saving-throw proficiency is a distinct binary concept.

A broader storage/calculation decision for saving throws, skill totals and other derived values is consequential to the durable model and remains pending explicit owner approval before implementation.

### 6. Initial tab structure

For the current implemented character-data scope, the tab structure is approved as:

1. **Resumen** — fast-reference character overview containing identity/status, compact class/level/hit-dice entries, all six ability scores, AC, HP, initiative, speed, proficiency bonus, passive Perception and optional spell save DC.
2. **Habilidades** — skills/saving-throw/ability relationship area, supporting the approved By skills and By attribute presentation modes.

V3 intended-device QA explicitly confirmed that this tab structure is understandable and works better than the previous continuous editor.

Tabs should be added **when their feature domains become relevant and implemented**, rather than creating empty placeholders for future features. Likely later domains such as combat/actions, spells or equipment may receive tabs when those slices actually exist, but their names/order are not approved by this decision.

A compact persistent editor header keeps navigation/character identity/save/settings reachable while moving between the current tabs; exact styling remains implementation-level unless QA identifies a consequential UX issue.

### 7. Skill training control

V3 QA confirmed that the compact skill training/proficiency control **type is liked and should be retained**. Its current abbreviated letters are not sufficiently intuitive.

Approved next visual treatment:

- no proficiency: **empty box**;
- proficiency / `Competente`: **checked box**;
- expertise / `Pericia`: **double-check style box/indicator**.

The expanded selector/menu may still spell out the full Spanish states. The compact state should communicate through the indicator rather than cryptic letters.

Whether the stored final numeric skill modifier should remain independent or become a derived standard value plus an exception mechanism is now part of the pending SRD-derived data-model decision; do not silently choose either behavior.

### 8. Icon controls must not scale as text glyphs

V3 exposed that some controls such as Back and Settings are currently text glyphs (`←`, `⚙`) and therefore change awkwardly with font scaling.

Approved direction:

- navigation/action affordances such as Back and Settings should use proper icon-button controls with stable touch-target/icon sizing;
- they should not be implemented as ordinary font text whose geometry changes with the application text-size preference.

The same principle applies to similar icon-only controls added later.

### 9. Combat-reference grouping and order

`Referencia de combate` is accepted as a useful semantic group.

The owner **approved** the following digital subgroup order after comparison with the stored custom paper sheets:

1. **Core reference:** `CA` · `Iniciativa` · `Velocidad`;
2. **Health:** `PG actuales` · `PG máximos` · `PG temporales`;
3. **Secondary reference:** `Bonificador por competencia` · `Percepción pasiva` · `CD de salvación de conjuros`.

Portrait and landscape may use different geometry, but these semantic subgroups and their internal order should remain stable rather than flattening fields into an arbitrary wide row.

The wording should avoid opaque abbreviations. Conventional, highly recognizable D&D abbreviations such as `CA` and `PG` may remain where useful, but labels such as `Comp.` and `Perc. pas.` should be replaced with clearer/full Spanish wording when space permits. Use SRD terminology as the naming reference.

## Still unresolved

The following remain intentionally open unless separately approved:

- the durable calculation/storage model for saving throws, skill totals and related standard-derived values after the SRD review;
- the final normal-width sans replacement for Atkinson (Sora is the proposed V4 test candidate);
- exact visual styling beyond the grouping/density directions already recorded by QA.

## Consequence for the next Android QA build

Before V4 implementation, resolve the consequential derived-value model question. Once approved, the next focused presentation pass should preserve the V3 functional successes while:

- reducing remaining unnecessary class/box internal padding;
- fixing the narrow `dX` hit-die control so `d8`, `d10`, etc. never stack vertically;
- alphabetizing Artífice with the class names while keeping `Otro` last;
- correcting menu/layout behavior at font scales above 100%;
- removing Atkinson, adding IBM Plex Sans Condensed, and testing Sora as the replacement normal-width sans alongside Manrope and Barlow Condensed;
- increasing visual distinction between Light and Light Gray;
- making Dark Purple clearly purple and distinct from Dark;
- replacing the By skills / By attribute dropdown with a compact two-state control with an active indicator;
- retaining the liked compact skill-training interaction while using empty/check/double-check visual states;
- replacing text-glyph Back/Settings affordances with stable icon buttons unaffected by font-scale changes;
- displaying automatically derived ability modifiers alongside ability scores;
- adding a distinct saving-throw proficiency control consistent with the approved derived-value model;
- applying the approved `Referencia de combate` subgroup ordering and clearer SRD-based labels;
- preserving the now-passing rotation/screen-off navigation state, IME/keyboard accessibility, landscape behavior and persistence.

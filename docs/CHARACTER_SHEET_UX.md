# Character-Sheet UX Working Record

Status: active working record for Phase 4 character-sheet UX on `implementation/character-data-foundation`.

This document records owner QA, accepted directions, open questions, and design-reference observations. It is not a substitute for the durable character data model, and it must not be interpreted as permission to copy the paper layouts literally into the application.

## 1. Second intended-device QA — revised phone editor

Test build: branch head `113fe27c42e15ff0950d53e854796f26de6671b4` (`Route character editing through revised phone UX`).
Primary device: Android phone.
Overall result: **NEEDS CHANGES**.

### What improved / passed

- APK updated successfully and the PC editor opened.
- The revised editor clearly looks different from the first functional version.
- It is more compact and more organized.
- Nothing in the new presentation was judged worse than the previous version.
- Numeric-field behavior passed the owner check: numeric keyboard/filtering behavior is acceptable in this build.
- Hit-die selector is better than the previous free-text control.
- Skill proficiency/expertise selector works and is understandable.
- Landscape mode is generally liked.
- Readability is good.
- Character save/reopen and full persistence regression checks passed.

### Remaining layout/density issues

- The sheet is still not compact enough.
- All six ability scores should be able to fit in one row, rather than consuming multiple rows.
- Existing internal spacing should be reduced further where practical.
- The editor is more organized but all content still lives in one continuous surface; the previously discussed tabbed organization has not yet been implemented.
- Skill training controls work but consume too much space. Their final compactness may depend partly on the later global font-size setting.

### Class and hit-die UX

Current revision is better but not accepted.

Required direction:

- One class entry should be compact enough to fit on one row when practical.
- Hit-dice notation/order should follow the natural tabletop convention: quantity before die type, e.g. `3d10`.
- The current visual order of die type and number of dice is therefore logically inverted.
- Class selection should use a known-class selector.
- The known list must include Artificer / Artífice (Eberron).
- It must also include `Otro` / `Otra` as an escape path that exposes an open custom/homebrew class field.
- The permissive product rule remains controlling: known choices improve entry speed but must not prohibit nonstandard/homebrew classes.

Exact final class list beyond the above remains to be confirmed with the owner before it becomes a durable convention.

### Skills

- Skill training selection is understandable and functionally works, but the selector is too large.
- Whether changing training/proficiency should affect the stored final numeric modifier is **not yet decided**. Do not implement automatic recalculation without owner approval.
- Each skill should visibly indicate its associated ability.
- The owner wants a presentation choice between two ways of seeing ability/skill relationships. Exact default and labels are still to be confirmed, but the two intended concepts are:
  1. ability-centered view, where an ability is shown with the skills associated with that ability;
  2. separate abilities plus a skills list where each skill explicitly shows its associated ability.
- A small sheet-local gear/basic-settings control is a plausible place for this presentation preference.

### Saving throws

Owner explicitly placed this topic on hold during QA because they have a clearer explanation to provide later.

Do not redesign or finalize saving-throw presentation until that follow-up explanation is obtained.

### Keyboard / IME

**Still failing.**

- The software keyboard still hides the bottom portion of the sheet/content.
- The previous correction did not fully solve lower-content accessibility while the IME is visible.
- This remains a blocking UX defect for the next phone build.

### Landscape / responsive behavior

- Landscape presentation is generally liked.
- However, adding classes disrupts the grouping/layout: dynamic class content causes groups to reflow in a way that breaks the intended visual organization.
- The next responsive layout must preserve stable visual grouping as repeated/dynamic class rows are added.

Recommended English wording for the recorded issue: **“Adding classes disrupts the grouping/layout.”** A more specific formulation is: **“Adding classes causes the groups to reflow in a way that breaks the visual grouping.”**

### Navigation/state defect

A new functional UX defect was found:

- rotating portrait ↔ landscape returns the app to the start/campaign page;
- turning the phone screen off and back on also returns the app to the start/campaign page.

The current character/editor navigation state therefore is not surviving ordinary Android Activity/configuration recreation and must be preserved across rotation and screen-off/on restoration.

### Settings direction — next build

The next build should include the first small application Settings surface rather than hard-coding typography/theme choices inside the PC editor.

Initial settings scope requested by the owner:

- font size;
- font family/style;
- theme.

Theme choices must include at least:

1. System
2. Light
3. Dark
4. Light Gray
5. Dark Purple — explicit owner personal preference

Exact font families and exact font-size steps remain open and require owner review before finalization.

A separate small character-sheet gear/basic-settings control may later contain sheet-specific presentation preferences (for example ability/skill grouping mode). Global font/theme settings and sheet-specific layout preferences should not be conflated unless there is a concrete usability reason.

## 2. Owner paper-sheet PDFs supplied as design inspiration

The owner supplied two five-page custom paper character-sheet PDFs for detailed visual review:

- `Hoja de PJ - 5.0 - Simkin.pdf`
- `Hoja de PJ v2 - 5.0 - Simkin.pdf`

These are **design/grouping references**, not requirements to reproduce paper layout literally in Android.

The binary PDFs are not yet stored on the Git branch. For durable repository continuity, the owner should upload them under:

`assets/character-sheets/templates/`

Suggested filenames may remain the original filenames unless the owner prefers a versioned naming convention. Once uploaded, this document should be updated to point to the repository paths.

### 2.1 `Hoja de PJ - 5.0 - Simkin.pdf`

#### Page 1 — main character sheet

Strong grouping ideas:

- top identity block: class/level, race, alignment and experience/next level;
- compact combat/reference cluster for Armor Class, initiative, proficiency, HP, speed, inspiration and hit dice;
- all six abilities presented in a single horizontal band;
- each ability visually owns its saving throw and associated skills directly below it;
- spellcasting summary and attacks are separated into clear lower-page blocks;
- traits/attributes form a distinct final band.

Important digital lesson: the page does not treat abilities, saves and skills as unrelated forms. It uses visual ownership and proximity to explain their relationship.

#### Page 2 — equipment

- general equipment is separated from money/valuables;
- special equipment has explicit body-slot/location structure;
- information density is high without each item becoming a large independent card.

#### Page 3 — character/background information

- background, personality traits, ideals, bonds and flaws are separate from broader history and notes;
- the grouping is semantic rather than simply a sequence of generic text fields.

#### Page 4 — full spell sheet

- spells are grouped by spell level;
- slot counts and spent slots are adjacent to the relevant level;
- the layout uses columns to fit a large amount of structured information on one page.

#### Page 5 — notes

- two-column free notes area plus a grid area for sketches/maps/other visual notes.

### 2.2 `Hoja de PJ v2 - 5.0 - Simkin.pdf`

This version is especially relevant because it already explores alternative grouping strategies.

#### Page 1 — ability-centered main sheet

- abilities form a vertical left rail;
- each ability is directly associated with its saving throw and relevant skills;
- identity, AC, hit dice, max/current HP, initiative and speed form a compact upper-right reference cluster;
- attacks and traits occupy strong horizontal blocks;
- spellcasting, treasure, ammunition and other items are compact bottom sections.

Digital lesson: strong groups can remain visually stable even when the overall screen is dense. A phone version should not use the same exact geometry, but the hierarchy is useful.

#### Page 2 — alternate skills/saves organization

This page provides an especially useful direct precedent for the owner’s current QA request:

- abilities remain visually separate;
- all saving throws are collected into their own block;
- all skills are collected into their own block;
- each skill includes its associated ability abbreviation, e.g. Acrobacias (DES), Atletismo (FUE), Conoc. Arcano (INT), etc.

This means the owner’s paper designs already contain both presentation concepts now being discussed for the app:

1. ability-centered grouping with related saves/skills;
2. separate skills/saves lists with explicit ability association.

The digital app can therefore treat this as a **view preference**, not a disagreement about the underlying data model.

#### Page 3 — equipment/background

- general equipment and background/history content share the upper area;
- special equipment remains a distinct body-slot table beneath them.

#### Page 4 — spell sheet

- same core spell-level grouping idea as the earlier sheet, with dense multi-column use.

#### Page 5 — notes

- two-column notes plus large grid area, consistent with the first version.

## 3. Design principles derived from the paper references

These are working principles for the next digital iteration, not instructions to copy paper literally:

1. **Relationship by proximity.** If fields are mechanically/conceptually related, show that relationship spatially rather than relying only on labels.
2. **Dense does not mean unstructured.** The paper sheets fit much more information because fields are compact and grouped, not because all margins are simply removed.
3. **Stable groups matter.** Dynamic elements such as multiclass rows should not cause unrelated groups to jump/reflow unpredictably.
4. **Alternative views are legitimate.** The owner’s own v2 paper sheet demonstrates more than one valid way to organize abilities, saves and skills.
5. **Use digital affordances.** Tabs, compact dropdowns, settings, conditional/custom fields and responsive layout can reduce scrolling more effectively than reproducing a single paper page.
6. **Keep global and sheet-specific preferences distinct.** Theme/font belong naturally in app Settings; grouping/view preferences can live in a small sheet-specific gear if needed.

## 4. Current next-build target

Before another owner QA APK, implement a focused third character-editor pass that includes:

- preserve current screen/editor across rotation and screen-off/on restoration;
- fully solve keyboard/IME obstruction of lower content;
- compact six abilities into one row where the phone width/readability permits it;
- further reduce spacing without harming readability;
- redesign one class entry toward a single-row `quantity dX` logic such as `3d10`;
- known-class selector including Artificer / Artífice and `Otro` custom entry;
- materially more compact skill rows/controls;
- show associated ability for each skill;
- introduce the first small Settings surface for font size, font family/style and the five requested themes;
- preserve good landscape behavior while preventing dynamic class rows from breaking group layout;
- do **not** finalize saving-throw layout yet;
- do **not** decide automatic skill-modifier recalculation from training yet;
- decide tabs/section navigation with the owner rather than silently imposing a final structure.

The second revised phone editor is therefore **not accepted for PR/merge yet**.
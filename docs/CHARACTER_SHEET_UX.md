# Character-Sheet UX Working Record

Status: active working record for Phase 4 character-sheet UX on `implementation/character-data-foundation`.

This document records owner QA, accepted directions, open questions, and design-reference observations. It is not a substitute for the durable character data model, and it must not be interpreted as permission to copy the paper layouts literally into the application.

## 1. Third intended-device QA — V3 phone editor

Tested implementation head: `f728acd7ec10f4fae2df093ec8b16db4c8d2ba90` (`Wire downloadable fonts through GMS provider`).  
CI verification: GitHub Actions run #84 / `33352541814` passed shared tests, Android debug build/APK upload, Desktop build and backend checks.  
Primary device: Android phone.  
Overall result: **FUNCTIONAL PASS; PRESENTATION NEEDS A SMALLER FOLLOW-UP PASS BEFORE PR/MERGE.**

### Functional/regression result

- V3 installed over the previous stable-signed QA build successfully.
- Existing campaign data remained present.
- Existing character data remained present.
- Character save/reopen and full application persistence passed.
- Portrait/landscape rotation no longer returns to the campaign/start screen.
- Screen off/on no longer returns to the campaign/start screen.
- Software keyboard/IME no longer hides lower editable content.
- Landscape behavior is acceptable and repeated class rows did not produce the previous grouping/reflow problem.

### Tabs and overall organization

Owner accepted the current two-tab structure:

- `Resumen`;
- `Habilidades`.

Both labels are understandable and the tabbed organization is preferred over the previous continuous form. Overall sheet density and grouping are now acceptable. All six ability scores fit compactly in one row and the combat/reference area is recognized as a useful group.

Remaining density note: class/row boxes can use **slightly less internal margin/padding**.

### Classes and hit dice

What passed:

- one class entry is now effectively compact enough;
- class choices are correct;
- `Artífice` exists;
- `Otro` correctly exposes a custom/homebrew class field;
- adding multiple classes does not destabilize the layout.

Required changes:

- the hit-die `dX` selector is too narrow in practice and can render vertically stacked, e.g. `d` above `8`; `d8`, `d10`, etc. must stay on one line;
- `Artífice` is currently appended at the end of the list; it should participate in normal alphabetical ordering;
- `Otro` should remain the final escape-path entry rather than being alphabetized with real class names.

The quantity-before-die logic is otherwise accepted.

### Global Settings

#### Font size

- `100%` remains the approved default/baseline.
- Above 100%, menus/layout begin to look visually wrong.
- Treat this first as a responsive-layout defect rather than silently removing the approved larger scale steps.

#### Fonts

- Manrope remains acceptable.
- Barlow Condensed remains a useful candidate.
- Atkinson Hyperlegible Next looked wrong in V3; it may have rendered/loaded poorly and is **not accepted in its current result**.
- Owner requested another **condensed** family candidate as replacement/alternative.

#### Themes

- `Morado oscuro` is too close to ordinary Dark and currently reads more wine/burgundy than purple.
- `Gris claro` is almost indistinguishable from ordinary Light.
- Keep the theme identities but revise the palettes so the alternatives are visibly distinct.

### Habilidades — Por habilidades

Confirmed good:

- all six abilities are visible compactly;
- saving throws have their own group;
- every skill shows its associated ability abbreviation;
- skill rows are substantially smaller than V2;
- the compact proficiency/training control type is liked;
- changing `Competente` / `Pericia` does not alter the stored numeric skill modifier.

Required refinement:

- keep the current compact training-control **interaction type**, but its abbreviated letters/state indication are not clear enough and should be made more legible/obvious.

### Habilidades — Por atributo

Owner confirmed the intended grouping works:

- FUE with its saving throw and Atletismo;
- DES with its saving throw and relevant skills;
- INT, SAB and CAR with their saving throws and relevant skills;
- CON naturally contains its saving throw with no standard associated skill.

The presentation concept is therefore accepted.

### Habilidades view selector

The current dropdown/gear mechanism works, but owner requested a more direct **two-state slider/segmented-control style selector with a clear active indicator** for `Por habilidades` / `Por atributo`.

This remains a user/device presentation preference, not character mechanics data.

### Keyboard / IME

**PASS in V3.**

The keyboard no longer obstructs lower content during editing. Preserve this behavior.

### Navigation/state recreation

**PASS in V3.**

The previous rotation and screen-off/on state-loss defect is fixed. Preserve current character/tab/draft state across normal Android recreation.

### Landscape

**PASS / acceptable in V3.**

The owner reported no issue with the current landscape behavior and no recurrence of the earlier dynamic-class grouping disruption.

### Persistence

**PASS in V3.**

Saved character values and presentation preference persistence behaved correctly in the tested workflow.

### Owner note requiring clarification

Owner additionally wrote:

> `no modifiers on sheet, some over abreviations`

The exact visual meaning is not yet sufficiently clear to convert safely into an implementation requirement. Preserve this wording in the QA record and clarify it before changing code specifically for that point.

### Referencia de combate

The owner considers `Referencia de combate` a **good group**, but finds its internal order strange and asked for comparison against the custom paper sheets.

Current V3 portrait ordering is:

1. CA, Inic., Vel.
2. PG máx., PG act., PG temp.
3. Comp., Perc. pas., CD conj.

Current wide ordering begins:

1. CA, Inic., Vel., PG máx., PG act.
2. PG temp., Comp., Perc. pas., CD conj.

The stored paper references use stronger semantic clustering than this wide-row sequence. See section 3 for the paper-derived comparison and next recommendation.

## 2. Second intended-device QA — revised phone editor (historical)

Test build: branch head `113fe27c42e15ff0950d53e854796f26de6671b4` (`Route character editing through revised phone UX`).  
Primary device: Android phone.  
Overall result: **NEEDS CHANGES**.

This pass established the requirements that produced V3: tighter density, six abilities in one row, compact class/hit-die entry, exact class selector, skill→ability labels, two skills/attribute presentations, global Settings, keyboard/IME correction, stable rotation/screen-off navigation state and more stable landscape grouping.

V3 QA above supersedes those items where it explicitly records a pass or revised requirement.

## 3. Owner paper-sheet PDFs supplied as design inspiration

The two five-page custom paper character sheets are stored durably in the repository:

- `assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf`
- `assets/character-sheets/templates/Hoja de PJ v2 - 5.0 - Simkin.pdf`

These are **design/grouping references**, not requirements to reproduce paper layout literally in Android.

### 3.1 `Hoja de PJ - 5.0 - Simkin.pdf`

#### Page 1 — main character sheet

Strong grouping ideas:

- top identity block: class/level, race, alignment and experience/next level;
- compact combat/reference cluster for Armor Class, initiative, proficiency, HP, speed, inspiration and hit dice;
- all six abilities presented in a single horizontal band;
- each ability visually owns its saving throw and associated skills directly below it;
- spellcasting summary and attacks are separated into clear lower-page blocks;
- traits/attributes form a distinct final band.

Important digital lesson: the page does not treat abilities, saves and skills as unrelated forms. It uses visual ownership and proximity to explain their relationship.

### 3.2 `Hoja de PJ v2 - 5.0 - Simkin.pdf`

This version is especially relevant because it explores alternative grouping strategies.

#### Page 1 — ability-centered main sheet

- abilities form a vertical left rail;
- each ability is directly associated with its saving throw and relevant skills;
- identity, AC, hit dice, max/current HP, initiative and speed form a compact upper-right reference cluster;
- attacks and traits occupy strong horizontal blocks;
- spellcasting, treasure, ammunition and other items are compact bottom sections.

#### Page 2 — alternate skills/saves organization

- abilities remain visually separate;
- all saving throws are collected into their own block;
- all skills are collected into their own block;
- each skill includes its associated ability abbreviation.

The paper designs therefore already contain both digital presentation concepts now implemented:

1. ability-centered grouping with related saves/skills;
2. separate skills/saves lists with explicit ability association.

### 3.3 Combat-reference order comparison

The custom sheets suggest **semantic subgroups**, not a single left-to-right numeric sequence:

- immediate combat/reference values such as AC, initiative and speed are visually close;
- HP values are a distinct health cluster;
- proficiency belongs with fast character reference but does not need to interrupt the HP group;
- hit dice are visually associated with the combat/resource cluster in the paper sheets, but in the digital app they already have a strong home in each class row, so duplicating them is unnecessary;
- passive Perception and spell save DC are useful quick-reference values but are conceptually secondary to AC/initiative/speed/HP.

Recommended digital ordering for the next pass:

**Portrait**

1. `CA` · `Inic.` · `Vel.`
2. `PG act.` · `PG máx.` · `PG temp.`
3. `Comp.` · `Perc. pas.` · `CD conj.`

This keeps the current useful three-row logic but changes HP to **current → maximum → temporary**, which is more natural for quick digital reading while keeping all HP together.

**Wide / landscape**

Do not flatten the sequence into an arbitrary five-field first row. Preserve the same semantic blocks horizontally, for example:

- core reference: `CA` · `Inic.` · `Vel.`;
- health: `PG act.` · `PG máx.` · `PG temp.`;
- secondary reference: `Comp.` · `Perc. pas.` · `CD conj.`.

The exact visual geometry can adapt to width, but subgroup identity should remain stable.

This recommendation is derived from the owner's paper-sheet grouping and the V3 QA comment; it is **not yet owner-approved as the final order**.

## 4. Design principles derived from the paper references

1. **Relationship by proximity.** Related fields should be spatially grouped.
2. **Dense does not mean unstructured.** Compact fields still need semantic hierarchy.
3. **Stable groups matter.** Dynamic elements should not cause unrelated groups to jump/reflow unpredictably.
4. **Alternative views are legitimate.** The paper sheets demonstrate more than one valid ability/save/skill organization.
5. **Use digital affordances.** Tabs, compact selectors, segmented controls, conditional fields and responsive layout can outperform literal paper reproduction.
6. **Keep global and sheet-specific preferences distinct.** Theme/font belong in app Settings; skills/attribute organization is a sheet-presentation preference.

## 5. Current next-build target

Do not open/merge the character-foundation PR yet. The next focused presentation pass should preserve all V3 functional successes and address only the remaining QA items:

- slightly reduce class/box internal padding;
- keep `d8`, `d10`, etc. on one line in the hit-die selector;
- alphabetize `Artífice` with the class list while keeping `Otro` last;
- make >100% font scaling visually sound in menus/layout;
- replace/retest Atkinson with another condensed font candidate;
- make `Gris claro` visibly distinct from `Claro`;
- make `Morado oscuro` clearly purple and distinct from ordinary dark;
- replace the skills/attribute dropdown with a compact two-state segmented/slider-like selector;
- retain the compact skill-training control while making its state abbreviation/indicator clearer;
- revise `Referencia de combate` ordering/grouping using section 3.3 as the proposed direction;
- clarify the owner note `no modifiers on sheet, some over abreviations` before implementing a specific change for it;
- preserve passing keyboard/IME, recreation/navigation, landscape and persistence behavior.

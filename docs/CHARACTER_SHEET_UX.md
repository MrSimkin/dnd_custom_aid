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
- `Artífice` should participate in normal alphabetical ordering;
- `Otro` remains the final escape-path entry rather than being alphabetized with real class names.

The quantity-before-die logic is otherwise accepted.

### Global Settings

#### Font size

- `100%` remains the approved default/baseline.
- Above 100%, menus/layout begin to look visually wrong.
- Treat this first as a responsive-layout defect rather than silently removing the approved larger scale steps.

#### Fonts

V3 clarification supersedes the earlier three-font interpretation.

- Manrope remains an accepted normal-width sans candidate.
- Barlow Condensed remains an accepted condensed candidate.
- IBM Plex Sans Condensed is accepted as an **additional condensed candidate** for the next QA pass.
- Atkinson Hyperlegible Next can be removed.
- The owner wants Atkinson replaced by another **normal-width sans**, not another condensed font.
- **Sora** is the current proposed V4 normal-width sans candidate and should be tested before final acceptance.

The intended V4 comparison is therefore:

1. Manrope — sans;
2. Sora — sans candidate replacing Atkinson;
3. Barlow Condensed — condensed;
4. IBM Plex Sans Condensed — condensed.

No serif option is wanted.

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
- changing `Competente` / `Pericia` did not alter the stored numeric skill modifier in V3.

Approved next presentation refinement for the compact training control:

- no proficiency: **empty box**;
- `Competente`: **checked box**;
- `Pericia`: **double-check style box/indicator**.

The interaction type should remain; the problem was the non-intuitive abbreviated letters, not the selector itself. The expanded menu may continue to spell out the full Spanish states.

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

### Icon-only controls

V3 currently renders some actions such as Back and Settings through text glyphs. This makes their geometry change with user font scaling.

Approved correction:

- Back, Settings/gear and similar icon-only actions should use proper icon-button controls;
- their icon/touch-target geometry should remain stable when application text size changes;
- they should not be ordinary text glyphs controlled by the typography scale.

### Keyboard / IME

**PASS in V3.** Preserve this behavior.

### Navigation/state recreation

**PASS in V3.** Preserve current character/tab/draft state across normal Android recreation.

### Landscape

**PASS / acceptable in V3.** No recurrence of the earlier dynamic-class grouping disruption.

### Persistence

**PASS in V3.** Saved character values and presentation preference persistence behaved correctly in the tested workflow.

### Clarification of the earlier `no modifiers / abbreviations` note

The owner clarified the earlier shorthand note as follows:

1. The current digital sheet is missing the **ability modifier** beside each ability score. These modifiers are standard derived values and should be automatic rather than separately entered.
2. Saving throws also have proficiency. Their presentation therefore needs a proficiency control, but the owner does **not** want the exact same three-state control used for skills.
3. Several compact labels are over-abbreviated and hard to recognize. Examples called out were `Perc. pas.` for `Percepción pasiva` and `Comp.` for `Bonificador por competencia`.
4. The owner explicitly requested that the character-creation section of the SRD be checked to ensure the digital sheet reflects the rules context rather than merely reproducing arbitrary fields.

The official Spanish SRD 5.2.1 character-creation section confirms:

- each ability score has a corresponding `modificador por característica` and that modifier is written beside the score;
- a proficient saving throw adds the character's `bonificador por competencia` to the relevant ability modifier;
- a non-proficient saving throw normally uses the relevant ability modifier;
- a proficient skill adds the proficiency bonus to the associated ability modifier;
- `Percepción pasiva = 10 + modificador para pruebas de Sabiduría (Percepción)`;
- `Iniciativa` uses the Dexterity modifier in the ordinary character-creation calculation;
- `CD de salvación de conjuros` follows a standard formula once the spellcasting ability is known.

Presentation consequence already approved: show automatic ability modifiers and add a distinct saving-throw proficiency control. A broader derived-value/storage decision remains pending because it changes the durable character model rather than only the UI.

### Referencia de combate

The owner accepts `Referencia de combate` as a good semantic group and has now **approved** the proposed internal order after comparison against the paper sheets:

1. **Core reference:** `CA` · `Iniciativa` · `Velocidad`;
2. **Health:** `PG actuales` · `PG máximos` · `PG temporales`;
3. **Secondary reference:** `Bonificador por competencia` · `Percepción pasiva` · `CD de salvación de conjuros`.

Portrait and landscape may use different geometry, but subgroup identity and internal order should remain stable.

Avoid unnecessary hard-to-recognize abbreviations. `CA` and `PG` are conventional enough to remain where useful; `Comp.` and `Perc. pas.` should be replaced with clearer/full labels where practical. Spanish SRD terminology is the naming reference.

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

The custom sheets support semantic subgroups rather than a single flattened numeric sequence:

- immediate combat/reference values such as CA, initiative and speed are visually close;
- HP values form a distinct health cluster;
- proficiency belongs with fast reference but does not need to interrupt the HP group;
- hit dice already have a strong digital home in the class rows and need not be duplicated;
- passive Perception and spell save DC are useful secondary quick-reference values.

The approved next-pass digital organization is therefore:

**Core reference:** `CA` · `Iniciativa` · `Velocidad`  
**Health:** `PG actuales` · `PG máximos` · `PG temporales`  
**Secondary reference:** `Bonificador por competencia` · `Percepción pasiva` · `CD de salvación de conjuros`

This order is now owner-approved.

## 4. Design principles derived from the paper references and SRD review

1. **Relationship by proximity.** Related fields should be spatially grouped.
2. **Dense does not mean unstructured.** Compact fields still need semantic hierarchy.
3. **Stable groups matter.** Dynamic elements should not cause unrelated groups to jump/reflow unpredictably.
4. **Alternative views are legitimate.** The paper sheets demonstrate more than one valid ability/save/skill organization.
5. **Use digital affordances.** Tabs, compact selectors, segmented controls, conditional fields and responsive layout can outperform literal paper reproduction.
6. **Keep global and sheet-specific preferences distinct.** Theme/font belong in app Settings; skills/attribute organization is a sheet-presentation preference.
7. **Do not make users re-enter deterministic rules values without a reason.** Standard derived character-sheet numbers should be calculated from their source data when the product has enough information, while the personal/homebrew requirement still needs an explicit exception strategy.
8. **Use recognizable terminology.** Prefer official Spanish SRD terms or conventionally understood D&D abbreviations over space-saving abbreviations that obscure meaning.

## 5. Current next-build target

Do not open/merge the character-foundation PR yet.

Before coding the SRD-derived mechanical changes, obtain owner approval for the durable calculation/exception model described in the current conversation and project state.

After that, V4 should preserve all V3 functional successes and address the remaining presentation items:

- slightly reduce class/box internal padding;
- keep `d8`, `d10`, etc. on one line in the hit-die selector;
- alphabetize `Artífice` with the class list while keeping `Otro` last;
- make >100% font scaling visually sound in menus/layout;
- remove Atkinson;
- add IBM Plex Sans Condensed as the additional condensed option;
- test Sora as the replacement normal-width sans alongside Manrope;
- make `Gris claro` visibly distinct from `Claro`;
- make `Morado oscuro` clearly purple and distinct from ordinary dark;
- replace the skills/attribute dropdown with a compact two-state segmented/slider-like selector;
- retain the compact skill-training interaction while using empty/check/double-check states;
- replace text-glyph Back/Settings actions with proper icon buttons unaffected by font scaling;
- display automatic ability modifiers next to ability scores;
- add a distinct saving-throw proficiency control consistent with the approved durable calculation model;
- apply the approved combat-reference subgroup ordering and clearer terminology;
- preserve passing keyboard/IME, recreation/navigation, landscape and persistence behavior.

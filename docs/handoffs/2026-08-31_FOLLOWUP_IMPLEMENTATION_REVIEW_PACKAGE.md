# Follow-up character-sheet implementation review package

**Status:** PROPOSED — OWNER REVIEW REQUIRED BEFORE CODING  
**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`  
**Production code changed by this package:** **NO**

This package consolidates the accepted V4 run #107 QA findings and decisions D-0046 through D-0052 into one proposed implementation target.

It is deliberately a pre-coding gate. The owner must review this package and answer the residual questions at the end before production implementation begins.

---

## 1. Baseline that must not regress

V4 run #107 already established a passing baseline for:

- in-place migration/preservation of campaigns and PCs;
- ability modifiers and derived arithmetic when numeric adjustment is populated;
- saving throws;
- skills;
- Passive Perception using base 10;
- classes and hit dice functionally;
- `Habilidades` presentation-state persistence;
- keyboard/IME reachability;
- portrait/landscape recreation;
- screen off/on recreation;
- save/reopen persistence;
- full app restart persistence.

The follow-up build is an incremental refinement/expansion, not a rewrite.

---

## 2. Final top-level character-sheet navigation

Exact order:

1. `General`
2. `Habilidades`
3. `Combate`
4. `Equipo`

`General` replaces the current `Resumen` label; there is no fifth overview tab.

Navigation requirements:

- preserve selected tab across ordinary Android recreation;
- preserve unsaved draft state across rotation and screen off/on;
- tab labels remain single-line and understandable;
- phone layout must remain usable at all approved text scales;
- the four tabs are real content domains, never empty placeholders.

---

## 3. `General` specification

### 3.1 Existing overview content

Preserve existing character identity, classes/hit dice, ability scores and general reference information, with the V4 QA corrections below.

### 3.2 Ability scores

- six scores remain compact;
- automatic ability modifiers remain derived and non-editable;
- make the displayed modifier slightly larger/more visually prominent so it does not get lost;
- required numeric fields may be temporarily blank while typing.

### 3.3 Numeric editing behavior

Current defect: required numeric inputs reject the temporary empty state, making replacements such as `20 -> 8` awkward.

Follow-up behavior:

- allow temporary blank editor state;
- if Save is pressed with one or more required numeric fields still blank, show a warning/confirmation;
- warning states that blank required numbers will be stored as `0`;
- Cancel returns to editing without mutation;
- Confirm normalizes those blank required values to `0` and saves;
- never silently coerce a still-blank required field to zero without warning.

Optional numeric fields may use their own optional semantics; the global blank-adjustment rule below remains `blank = 0` without suppressing calculations.

### 3.4 Derived-value interaction

Apply progressive disclosure to:

- Initiative;
- saving throws;
- skills;
- Passive Perception;
- calculated proficiency bonus.

Compact/default state:

- show the final calculated total as the primary value;
- do not permanently show an unexplained `±0` input;
- when a non-zero adjustment exists, a small secondary indication such as `ajuste +2` may be shown.

Activation opens a compact calculation breakdown/editor with:

- relevant rule inputs/contributions;
- `Ajuste adicional`;
- final total.

Blank optional adjustment is semantically `0` everywhere and must neither blank the total nor make the character unsavable.

### 3.5 Calculated proficiency bonus

Standard proficiency bonus derives from total character level:

- 1–4: +2
- 5–8: +3
- 9–12: +4
- 13–16: +5
- 17–20: +6

Durable exceptional state is `proficiencyBonusAdjustment`, not a manually authoritative final proficiency bonus.

Breakdown should include at least:

- total character level;
- standard proficiency bonus;
- `Ajuste adicional`;
- final proficiency bonus.

Skills and proficient saving throws consume this final proficiency bonus.

### 3.6 `Referencia de combate` on General

Remove spell save DC from this block.

Target semantic groups:

- core: `CA` / `Iniciativa` / `Velocidad`;
- health: `PG actuales` / `PG máximos` / `PG temporales`;
- secondary general reference: calculated `Bonificador por competencia` / `Percepción pasiva`.

Portrait and landscape may differ geometrically but must align coherently and preserve subgroup identity.

### 3.7 Quick Magic at bottom of General

Quick Magic stays at the bottom of `General` even if a future detailed `Magia` tab is ever added.

Manual quick-reference values:

- `CD de salvación de conjuros`;
- `Modificador de ataque mágico`;
- `Aptitud mágica` selector: FUE / DES / CON / INT / SAB / CAR / Otro / Ninguna;
- spell slots levels 1–9.

No automatic spellcasting build logic in this slice:

- no slot progression from class/level;
- no automatic spell save DC;
- no automatic spell attack modifier;
- no spell list/prepared-spell system;
- no multiclass spellcasting calculation.

Spell-slot interaction:

- configuration/editor exposes levels 1–9 and manually entered maximum slots;
- normal compact view displays only levels whose maximum is greater than zero;
- active levels render tappable spent/unspent pips;
- spent state persists through save/restart;
- `Restaurar espacios` manually marks all configured slots unspent;
- no automatic rest detection/restoration.

Implementation simplification is permitted: slots of one level are fungible, so durable state may store `total` + `spent count` rather than independent persistent identities for each pip, provided the visible tap behavior remains intuitive and equivalent.

---

## 4. `Habilidades` specification

Preserve both approved modes:

- `Por habilidades`;
- `Por atributo`.

The direct segmented/two-state selector remains and selected mode persists as a presentation preference.

### 4.1 Saving throws

- binary proficiency control;
- visually distinct from skill training;
- total = ability modifier + final proficiency bonus if proficient + adjustment;
- signed arbitrary adjustments remain allowed;
- total uses progressive-disclosure editor rather than permanent adjustment input.

### 4.2 Skills

- all 18 standard skills remain present;
- associated ability remains visible;
- training remains NONE / Competente / Pericia;
- fixed-footprint empty / one-check / double-check control remains;
- total = ability modifier + 0x/1x/2x final proficiency bonus + adjustment;
- total uses progressive-disclosure editor.

### 4.3 Passive Perception

Formula remains:

`10 + final Perception skill total + passive-specific adjustment`.

Base 10 is confirmed against SRD 5.1 and SRD 5.2.1.

### 4.4 Layout fixes from V4 screenshots

- improve width allocation in `Por atributo` so labels such as `Juego de manos` and `Investigación` do not become cramped/awkward;
- preserve stable two-column/landscape grouping where appropriate;
- at 115%/130%, when one label wraps, all cells in the same logical row must adopt coherent height/alignment rather than only the wrapped element moving.

---

## 5. `Combate` specification

This is persistent character-sheet combat reference, **not** the future live encounter/turn tracker.

### 5.1 Read-only quick reference

Top of tab shows views of the same authoritative values, not duplicates:

- CA;
- Iniciativa;
- Velocidad;
- PG actuales;
- PG máximos;
- PG temporales.

Do not store combat-tab copies of those fields.

### 5.2 Reusable attacks/actions list

Each entry has stable identity and persistent user-defined order.

Minimum fields:

- `Nombre`;
- `Tipo`: Ataque / Acción / Acción adicional / Reacción / Otro;
- optional manual `Modificador de ataque`;
- `Daño / efecto` free text;
- optional `Alcance` free text;
- optional `Notas`.

Semantic rule: this is not weapon-only. An entry can be a condensed spell/effect reference for fast play.

Do not infer weapon legality, FUE/DES use, finesse, proficiency, magic bonuses, spell arithmetic or character-build legality.

### 5.3 List interaction

- add;
- edit;
- delete with normal destructive-action protection;
- simple phone-appropriate reorder;
- persist order exactly rather than alphabetizing.

---

## 6. `Equipo` specification

### 6.1 One inventory model

Ordinary and special equipment share stable item identity.

Common fields:

- `Nombre`;
- `Cantidad`;
- optional structured `Peso`;
- `Equipado`;
- `Notas`;
- user-defined `sortOrder`.

Ammunition can be an ordinary item with quantity.

### 6.2 Special/magic-item mode

An item can expose richer special-item fields without becoming a disconnected second inventory record:

- long `Descripción`;
- optional `Ubicación`;
- manual `Sintonizado`.

Phone presentation:

- compact special-item card/row shows at least name, location when present, and Sintonización state;
- tap/expand/edit exposes full description and ordinary fields.

If a future action converts a special item back to ordinary while special-only data exists, warn before discarding those fields.

### 6.3 Location

Predefined UI choices inspired by the paper sheet:

- Cabeza;
- Rostro;
- Cuello;
- Mano Izquierda;
- Mano Derecha;
- Brazo Izquierdo;
- Brazo Derecho;
- Pecho;
- Piernas;
- Pies;
- Otro/custom.

Location is optional and organizational.

Do not enforce exclusivity, anatomy legality, AC, attacks or magic effects from location.

`Equipado` and `Ubicación` are independent states.

### 6.4 Sintonización

Use official Spanish D&D terminology: `Sintonización`, `Sintonizado`.

- manual boolean state;
- show an informational count such as `Sintonizados: 2`;
- do not show/enforce a hard `2/3` cap;
- do not block a fourth attuned item because class features/homebrew/exceptions must remain representable.

### 6.5 Weight and units

Structured canonical storage is imperial.

Display imperial first with approximate metric in parentheses using game-friendly approximations:

- 1 ft (0,3 m);
- 1 lb (0,5 kg).

Examples:

- 30 ft (9 m);
- 10 lb (5 kg).

Free text is never parsed as structured distance/weight.

Calculate total carried weight from entered structured item weights; items without weight do not contribute and do not block saving.

### 6.6 Currency

Each PC independently owns its currency definitions and balances.

Default character currencies:

- cobre;
- plata;
- electro;
- oro;
- platino.

The character can add custom currencies (for example `Diamante Astral`). A custom currency added to one PC does not automatically appear on other PCs in the campaign.

No automatic currency exchange/conversion.

### 6.7 Inventory ordering

- preserve manual order;
- no forced alphabetical sorting;
- reorder persists across save/restart.

---

## 7. Settings/presentation audition

### 7.1 Text scales

Keep:

- 80%;
- 90%;
- 100% default;
- 115%;
- 130%.

Fix row reflow/alignment rather than deleting larger scales.

### 7.2 Eight-font audition

Normal-width:

1. Manrope
2. Sora
3. Source Sans 3
4. Lexend

Condensed/narrow:

5. Barlow Condensed
6. Roboto Condensed
7. Archivo Narrow
8. Oswald

IBM Plex Sans Condensed is removed from the offered set and any saved IBM preference migrates intentionally to Roboto Condensed.

All eight are QA audition candidates, not permanent branding commitments.

### 7.3 Theme audition

Retain useful existing identities and expose the following next-build set for phone evaluation:

- Sistema;
- Claro;
- Oscuro;
- Morado oscuro;
- Gris — neutral gray, replacing failed `Gris claro`;
- Cian oscuro;
- Azul noche;
- Verde bosque;
- Pergamino;
- Alto contraste;
- Matriz — near-black + vivid green.

`Gris claro` is retired. A saved `Gris claro` preference should migrate to `Gris` so user intent is preserved rather than falling back arbitrarily.

Candidates may be pruned after phone QA.

### 7.4 Icon controls

Back, Settings and other icon-only controls remain stable vector/icon controls unaffected geometrically by text scale.

---

## 8. Proposed durable data model changes

Current architecture is Kotlin Multiplatform shared domain + SQLDelight, with `CharacterSheet`, `CharacterRepository`, `Character.sq` and numbered `.sqm` migrations. The existing `character` row currently stores a manually authoritative `proficiency_bonus`, while V4 already stores adjustment-based initiative/saves/skills/passive state.

The follow-up should extend that existing architecture rather than create an Android-only shadow model.

### 8.1 Character core additions/changes

Add conceptually:

- `proficiencyBonusAdjustment: Int`;
- keep legacy `proficiency_bonus` column only as needed for safe migration, but domain final PB becomes derived;
- `spellAttackModifier: Int?`;
- `spellcastingAbility: enum/string` representing FUE/DES/CON/INT/SAB/CAR/OTHER/NONE;
- existing `spellSaveDc` remains authoritative manual value and simply moves UI location.

### 8.2 Spell-slot table

Proposed child table keyed by character + spell level:

- character_id;
- spell_level 1–9;
- total_slots;
- spent_slots.

Rows with total zero may be omitted or retained consistently; domain behavior must be equivalent.

### 8.3 Combat-entry table

Proposed child table:

- id;
- character_id;
- name;
- type;
- attack_modifier nullable;
- damage_effect text;
- range_text nullable;
- notes nullable;
- sort_order.

### 8.4 Inventory-item table

Proposed child table:

- id;
- character_id;
- name;
- quantity;
- weight_lb nullable;
- equipped boolean/integer;
- notes nullable;
- sort_order;
- special boolean/integer;
- description nullable;
- location nullable text;
- attuned boolean/integer.

Store location as a permissive string in durable data; predefined body locations are UI conveniences, not a closed rules enum.

### 8.5 Character-currency table

Proposed child table:

- id;
- character_id;
- name/key;
- amount;
- sort_order;
- optional default/system marker if useful for preventing accidental duplicate seeding.

Each character receives five default entries independently. Custom entries belong only to that character.

### 8.6 Ordering

Use explicit `sort_order` for combat entries, inventory and currencies where visible order is user-controlled.

### 8.7 Transactions

Character saves should remain transactional: core + classes + saves + skills + Quick Magic + combat entries + inventory + currencies must not leave a partially persisted sheet if one write fails.

---

## 9. Proposed migration plan from current V4 database

Create the next numbered SQLDelight migration after `2.sqm`.

### 9.1 Preserve existing V4 data

Do not recalculate or overwrite:

- ability scores;
- AC;
- HP;
- speed;
- initiative adjustment;
- saving throw state/adjustments;
- skill training/adjustments;
- passive adjustment;
- class data;
- existing spell save DC.

### 9.2 Proficiency bonus

Add `proficiency_bonus_adjustment` default 0.

For each existing character:

1. calculate total level as sum of `character_class.level`;
2. calculate the standard PB for that level according to the approved progression;
3. set:

`new adjustment = old proficiency_bonus - standard PB`.

Domain code then derives final PB as standard + adjustment.

The legacy `proficiency_bonus` column may remain physically present for migration safety but must stop being competing authoritative domain state.

### 9.3 Spell save DC

Keep existing column/value exactly. No numeric migration; only UI relocation to Quick Magic.

### 9.4 New Quick Magic fields

- spell attack modifier: null/default blank;
- spellcasting ability: NONE/Ninguna;
- slot rows: none/zero initially.

### 9.5 New combat/inventory data

- no inferred attacks;
- no inferred equipment;
- no inferred item location or attunement;
- collections start empty.

### 9.6 Currency seeding

For every existing character, create the five default per-character currency definitions with amount 0.

New characters receive the same five defaults at creation.

### 9.7 Presentation preference migration

- IBM Plex Sans Condensed -> Roboto Condensed;
- Gris claro -> Gris;
- current `Resumen` tab identity -> `General` when restoring navigation state;
- preserve Habilidades presentation preference and all other valid Settings preferences.

### 9.8 Migration test requirement

Automated migration tests must include at least:

- ordinary PB preserved;
- deliberately unusual PB preserved via adjustment;
- existing spell save DC preserved;
- old characters receive empty new combat/equipment collections;
- five default currencies are seeded once, with no duplicate seeding;
- old font/theme preferences map as specified;
- existing V4 save/skill/passive/initiative values remain unchanged.

---

## 10. Proposed implementation sequence and mandatory checkpoints

Per `AGENTS.md`, every meaningful step gets a durable Git checkpoint before proceeding.

### Step A — owner approves this package

No code before approval and residual-question closure.

### Step B — shared domain + database migration

- new domain types/fields;
- SQLDelight schema/migration;
- repository hydration/save;
- migration/domain tests.

**Checkpoint before UI work.**

### Step C — core V4 correctness/interaction fixes

- blank optional adjustment = zero globally;
- proficiency bonus derivation + adjustment;
- temporary blank required numeric editing + save confirmation;
- derived-value breakdown interaction.

**Checkpoint.**

### Step D — four-tab navigation shell

- rename Resumen -> General;
- General / Habilidades / Combate / Equipo;
- preserve recreation/draft behavior.

**Checkpoint.**

### Step E — General + Quick Magic

- combat-reference cleanup/alignment;
- ability-modifier prominence;
- Quick Magic fields and slot pips.

**Checkpoint.**

### Step F — Habilidades layout/derived UX

- progressive disclosure;
- `Por atributo` width/reflow corrections;
- larger-text row alignment.

**Checkpoint.**

### Step G — Combate tab

- read-only quick strip;
- attacks/actions CRUD + reorder.

**Checkpoint.**

### Step H — Equipo tab

- inventory CRUD + reorder;
- special-item details/location/Sintonización;
- weight total;
- per-PC currencies.

**Checkpoint.**

### Step I — Settings audition

- 8 fonts;
- theme set;
- saved-preference migration;
- text-scale geometry validation.

**Checkpoint.**

### Step J — CI/build target freeze

- automated tests green;
- build exact APK target;
- record commit, workflow run and artifact identity;
- do not rebuild after owner QA begins and call it the same target.

**Checkpoint.**

### Step K — phone-first owner QA

Run the targeted checklist below and record every observation incrementally.

No Phase 4 PR/merge until owner acceptance.

---

## 11. Proposed targeted owner QA checklist for the follow-up build

This is the proposed build-specific suite. Do not replace the canonical `docs/QA_CHECKLIST.md` until the owner approves this package.

### Migration/core preservation

1. Install over exact accepted run #107 path without uninstalling.
2. Campaigns/PCs remain present.
3. Existing Initiative/save/skill/Passive Perception displayed totals remain unchanged.
4. Existing spell save DC remains unchanged but now appears in Quick Magic.
5. Existing ordinary proficiency bonus remains unchanged after becoming calculated.
6. A deliberately unusual proficiency bonus remains numerically unchanged via `Ajuste adicional`.
7. Existing Settings preference migrations behave intentionally (IBM -> Roboto Condensed; Gris claro -> Gris if present).

### Required-number editing

8. Replace a value such as FUE 20 -> clear -> 8 naturally.
9. Leave a required numeric field blank and press Save; warning appears.
10. Cancel warning; blank draft remains for correction.
11. Confirm warning; blank required value persists as 0 according to final approved scope.

### Derived-value UX

12. Initiative compact view shows final total without permanent adjustment field.
13. Tap Initiative; breakdown shows DEX, Ajuste adicional, total.
14. Repeat representative saving throw.
15. Repeat representative skill.
16. Repeat Passive Perception.
17. Repeat calculated proficiency bonus.
18. Non-zero adjustment receives understandable secondary indication without clutter.
19. Blank adjustment behaves as zero and never suppresses total/save.

### General

20. Ability modifiers are more visible but six abilities remain compact.
21. Combat reference is aligned in portrait.
22. Combat reference is aligned in landscape.
23. Spell save DC is absent from general combat reference.
24. Quick Magic appears at bottom of General.
25. Configure slot totals for multiple levels; only active levels appear in compact view.
26. Toggle slot pips spent/unspent.
27. Save/reopen/restart; spent state persists.
28. `Restaurar espacios` restores configured pips.
29. Spell save DC / spell attack / spellcasting ability are manual and persist.

### Four-tab navigation

30. Tabs appear exactly General / Habilidades / Combate / Equipo.
31. All four contain real content.
32. Tab selection survives rotation and screen off/on with unsaved draft.
33. Tab labels remain usable at 115% and 130%.

### Habilidades

34. Existing saving-throw and skill arithmetic still passes.
35. Both Habilidades modes persist.
36. Previously cramped `Por atributo` labels are readable.
37. Wrapped labels at 115/130% keep logical row alignment.

### Combate

38. Quick reference values match General exactly and are read-only views, not copies.
39. Add a weapon-like attack entry.
40. Add a condensed spell/effect entry.
41. Edit/delete entries.
42. Reorder entries and confirm order persists after restart.
43. Optional attack modifier/range and free-text effect/notes behave permissively.

### Equipo — ordinary inventory

44. Add ordinary item with quantity, weight, equipped state and notes.
45. Add item without weight; it saves normally and contributes nothing to weight total.
46. Weight display uses imperial first + approximate metric.
47. Reorder inventory and confirm persistence.
48. Verify equipped state persists independently.

### Equipo — special items

49. Convert/add special item and enter long description.
50. Choose predefined location.
51. Choose custom location.
52. Mark/unmark Sintonizado.
53. `Sintonizados: N` informational count updates without hard cap.
54. Multiple items can share a location without rules rejection.
55. Equipado and Ubicación can differ independently.
56. Compact card is readable; expansion/editor exposes full detail.

### Currency

57. Five default currencies exist for an existing migrated PC.
58. Add custom currency to PC A.
59. Confirm custom currency does not appear on PC B automatically.
60. Amounts persist through restart.

### Settings audition

61. Test Manrope.
62. Test Sora.
63. Test Source Sans 3.
64. Test Lexend.
65. Test Barlow Condensed.
66. Test Roboto Condensed.
67. Test Archivo Narrow.
68. Test Oswald.
69. Confirm IBM Plex Sans Condensed is no longer offered.
70. Test Sistema / Claro / Oscuro / Morado oscuro.
71. Test neutral Gris; confirm it does not read green/blue.
72. Test Cian oscuro.
73. Test Azul noche.
74. Test Verde bosque.
75. Test Pergamino.
76. Test Alto contraste for readability/state distinction.
77. Test Matriz for black/green identity and usability.
78. Restart and confirm selected font/theme/text scale persist.

### Regression

79. Keyboard near bottom leaves content reachable.
80. Portrait <-> landscape retains same PC/tab/draft.
81. Screen off/on retains same PC/tab/draft.
82. Multiple classes remain stable, including normal dropdown-selected class geometry and `Otro` custom class.
83. d8/d10/d12 remain one line and class control geometry is visually coherent.
84. Save/reopen/full restart preserves all old and new durable data.

---

## 12. Residual questions discovered by architecture review

These are the only identified consequential questions still requiring owner answers before coding.

### R1 — Proficiency bonus when total level is 0

Current application allows a newly created character to exist before any class row is added. D-0046 defines the standard progression from level 1.

**Recommendation:** treat total level 0 as standard proficiency bonus **+2**. This keeps a new/incomplete character stable and matches the existing default without pretending that level 0 is an official D&D progression tier.

Approve or change?

### R2 — Does blank-confirm-to-zero include class level?

The current repository requires every class level to be positive. The approved numeric-editor rule says a required numeric field left blank at Save can be confirmed and stored as 0.

Two coherent choices:

- **A — fully general rule:** class level may temporarily/after confirmation become 0, making the character an explicitly incomplete but savable draft. The repository validation changes to allow 0.
- **B — structural exception:** normal numeric stats use blank -> warning -> confirmed 0, but a class row itself still requires level >=1; the warning cannot turn a present class row into level 0.

**Recommendation:** A, because it follows the owner's explicit permissive save rule and avoids one surprising numeric-field exception. A 0-level row is treated as incomplete character data, not official D&D legality.

Choose A or B.

### R3 — Meaning of inventory `Peso`

For quantity >1, the weight total needs an unambiguous meaning.

**Recommendation:** `Peso` is **per unit**, labeled accordingly where useful (`Peso/u.`), and carried total contributes `Cantidad x Peso/u.`. This is easiest when ammunition, rations, potions, etc. are stored as stacks.

Alternative: `Peso` means the total weight of the entire row/stack and quantity does not multiply it.

Approve per-unit weight or choose whole-stack weight?

---

## 13. Coding gate

Production coding remains **BLOCKED** until:

1. R1–R3 are answered;
2. the owner approves or amends this package;
3. those answers/amendments are checkpointed in Git.

After that approval, implementation proceeds in the checkpointed sequence in section 10.

# PC Sheet runtime QA character fixtures

These fixtures exist specifically for the manual Android PC-sheet Save/Share smoke that follows the integrated repository gate.

They are **app-owned Character Backup v2** documents. Android can import each one through the normal Character Directory **Importar** flow, which restores a fresh copy into the selected local campaign and remaps internal IDs.

The same JSON files can also seed the hosted DEV database through `database/qa/seed_pc_sheet_runtime_characters.sql`.

## Fixture set

### 1. Aldren Vale — strict SRD 5.1 / 5e

File:

`qa/pc-sheet/fixtures/01_aldren_vale_srd5_1_champion_fighter.json`

Purpose:

- simple martial baseline;
- no spellcasting;
- normal equipment / traits / resources;
- useful first Save + Share smoke.

Rules construction:

- Human, SRD 5.1;
- Acolyte background, SRD 5.1;
- Fighter 5 / Champion;
- standard array before racial increase: STR 15, DEX 13, CON 14, INT 10, WIS 12, CHA 8;
- 2014 Human +1 to all six -> 16 / 14 / 15 / 11 / 13 / 9;
- Fighter 4 ASI +2 STR -> final STR 18;
- proficiency bonus +3 at character level 5;
- fixed HP progression: 10 + CON mod at level 1, then 6 + CON mod at levels 2–5 -> 44 HP;
- chain mail + shield -> AC 18;
- Dueling Fighting Style is represented in the longsword damage (1d8 + 6);
- Fighter skills: Athletics, Perception;
- Acolyte skills: Insight, Religion;
- Fighter saving throws: Strength, Constitution;
- Champion Improved Critical is present;
- **Remarkable Athlete is deliberately absent** because SRD 5.1 Champion receives it at level 7, not level 5;
- Extra Attack is present at Fighter 5.

This fixture uses only SRD 5.1 character options/rules for its build.

### 2. Ilyra Quill — strict SRD 5.2.1 / 5.5e

File:

`qa/pc-sheet/fixtures/02_ilyra_quill_srd5_2_1_evoker_wizard.json`

Purpose:

- caster baseline;
- multiple legitimate spell sources;
- prepared versus unprepared spells;
- spell slots and Spellbook;
- optional PDF Spellbook QA.

Rules construction:

- High Elf, SRD 5.2.1;
- Sage background, SRD 5.2.1;
- Wizard 5 / Evoker;
- standard array: INT 15, CON 14, DEX 13, WIS 12, CHA 10, STR 8;
- Sage increases: +2 INT, +1 WIS;
- Wizard 4 Ability Score Improvement: +1 INT, +1 DEX;
- final scores: STR 8, DEX 14, CON 14, INT 18, WIS 13, CHA 10;
- proficiency bonus +3;
- fixed HP progression: 6 + CON mod at level 1, then 4 + CON mod at levels 2–5 -> 32 HP;
- permanent AC is 12; Mage Armor is present but is **not** silently assumed active;
- spell save DC 15 and spell attack +7;
- Wizard 5 has 4 Wizard cantrips, 9 prepared level-1+ Wizard spells, and 4/3/2 level 1/2/3 spell slots;
- Sage provides Magic Initiate (Wizard): Light, Message, Comprehend Languages;
- High Elf provides Prestidigitation plus Detect Magic and Misty Step at the appropriate levels;
- species-granted Detect Magic / Misty Step do **not** consume the nine Wizard prepared-spell selections in the fixture;
- Scholar grants Expertise in Arcana;
- Evocation Savant and Potent Cantrip are present;
- **Sculpt Spells is deliberately absent** because SRD 5.2.1 Evoker receives it at level 6, not level 5;
- Memorize Spell is present at Wizard 5.

This fixture uses only SRD 5.2.1 character options/rules for its build.

### 3. Mara de los Siete Umbrales — custom / Extended stress

File:

`qa/pc-sheet/fixtures/03_mara_siete_umbrales_custom_extended.json`

Purpose:

- deliberately non-SRD custom-rules stress case;
- force **Extended — Custom Statistics**;
- create heavy overflow candidates for Traits & Features, Resources & Options, Inventory / Equipment, Spells and Notes;
- exercise long Spanish text, accents, custom spellcasting ability, custom skills, custom markers and long equipment names.

Current stress payload includes at least:

- 4 custom attributes;
- 10 custom skills;
- 26 long traits;
- 10 resources;
- 8 class/custom options;
- 34 inventory entries;
- 24 custom spells;
- 9 long note cards;
- 7 custom markers.

This fixture is intentionally **not** rules-legal D&D SRD content. Its custom mechanics exist only to stress the app and PDF continuation families.

## Android import

From the Android Character Directory:

1. tap **Importar**;
2. choose the destination campaign;
3. select one fixture JSON;
4. the app restores it as a **new local copy**;
5. open the imported character and use **Ajustes de personaje -> Hoja de personaje PDF**.

The source fixture IDs are never reused by the normal backup-import path.

## Suggested manual PDF smoke

Use the three fixtures to cover the pending runtime boundary:

1. **Aldren Vale**
   - save a normal PDF and open it;
   - share the same family through Android chooser;
   - useful families: Fantasy Sheet and Custom v1.

2. **Ilyra Quill**
   - export a Custom v2 family;
   - enable **Include Spell Descriptions**;
   - verify spell sources/prepared state remain coherent;
   - make one harmless unsaved edit and verify **Exportar sin guardar** changes the PDF but does not save the character.

3. **Mara de los Siete Umbrales**
   - use `Extended Page` custom-stat presentation first;
   - exercise both Custom-v2 first-page variants;
   - verify continuation pages exist and remain readable for custom statistics plus overflow-heavy content;
   - this is the primary regression stress fixture.

4. On any fixture, request **Current Snapshot** once and confirm the current Android fallback notice is shown until a separate local snapshot aggregate exists.

## Hosted DEV SQL seed

Run from repository root with psql:

```text
psql "$DATABASE_URL" \
  -v allow_qa_seed=true \
  -v actor_user_id='<existing DEV app_user UUID>' \
  -f database/qa/seed_pc_sheet_runtime_characters.sql
```

The script:

- refuses to run without `allow_qa_seed=true`;
- does not create or guess an app user;
- creates/updates only the deterministic campaign `QA - PC Sheet PDF Runtime`;
- makes the supplied existing user an active DM of that dedicated QA campaign;
- inserts/updates only the three deterministic QA PC rows;
- stores these exact Character Backup v2 JSON documents in `pc.snapshot`;
- leaves `owner_user_id` / `controller_user_id` null by default rather than manufacturing Player ownership;
- is safe to rerun: unchanged snapshots do not increment PC revision.

If Player ownership/controller behavior is needed later, assign it through normal PC Manager authority controls or a separately reviewed QA action.

## Rules sources / attribution

Rules audits were checked against the official D&D Beyond SRD distribution:

- SRD 5.1: <https://www.dndbeyond.com/srd>
- SRD 5.2.1: <https://www.dndbeyond.com/srd>
- direct SRD 5.2.1 PDF used during audit: <https://media.dndbeyond.com/compendium-images/srd/5.2/SRD_CC_v5.2.1.pdf>

This work includes material from the System Reference Document 5.2.1 (“SRD 5.2.1”) by Wizards of the Coast LLC, available at <https://www.dndbeyond.com/srd>. The SRD 5.2.1 is licensed under the Creative Commons Attribution 4.0 International License, available at <https://creativecommons.org/licenses/by/4.0/legalcode>.

The fixture files paraphrase rules text and are intended as project QA data, not as a replacement rules reference.

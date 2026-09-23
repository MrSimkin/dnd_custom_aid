# Checkpoint - PC Sheet runtime QA character pack ready

**Date:** 2026-09-23  
**Base integrated main:** `6ce3ac35798a1ce915a3dac4227e983932d5ab9f`  
**Branch:** `qa/pc-sheet-runtime-character-fixtures`  
**PR:** #91  
**Fixture implementation:** `149c98ca9426424f52ea17fa770c8347a1435f89`  
**Test repair:** `a06b2fe5712426e6b42e5ea88f0fe0b2f9529fd1`  
**Status:** FIXTURE PACK READY / PUSH #3348 PASS / MANUAL ANDROID SMOKE STILL PENDING

## Why this exists

The Android PC Sheet PDF Save/Share implementation is integrated and repository-green, but the owner cannot run the real-device smoke immediately.

To make the next QA session reproducible, this checkpoint prepares three durable characters that can be loaded either:

1. locally through the app-owned Character Backup v2 import flow; or
2. into the hosted DEV PostgreSQL spine through one guarded SQL seed.

The manual Android smoke remains the current Wave 7 boundary. This fixture work does **not** close or bypass that manual gate and does not authorize Media / Handouts work yet.

## Fixture 1 - Aldren Vale

Path:

`qa/pc-sheet/fixtures/01_aldren_vale_srd5_1_champion_fighter.json`

Purpose:

- strict SRD 5.1 / D&D 5e martial baseline;
- Human;
- Acolyte;
- Fighter 5 / Champion;
- no spellcasting;
- normal traits, equipment and resources;
- recommended first Save/Share smoke.

Rules-lawyer checks encoded by test:

- DND_5E rules family;
- level 5;
- STR 18;
- HP 44;
- AC 18;
- proficiency bonus +3;
- Strength/Constitution save proficiency;
- Athletics/Perception Fighter skills plus Insight/Religion Acolyte skills;
- Improved Critical and Extra Attack present;
- Remarkable Athlete absent because the SRD 5.1 Champion receives it at level 7;
- longsword +7 to hit and 1d8 + 6 damage with Dueling.

## Fixture 2 - Ilyra Quill

Path:

`qa/pc-sheet/fixtures/02_ilyra_quill_srd5_2_1_evoker_wizard.json`

Purpose:

- strict SRD 5.2.1 / D&D 5.5e caster baseline;
- High Elf;
- Sage;
- Wizard 5 / Evoker;
- several legitimate spell sources;
- prepared/unprepared spell coverage;
- optional PDF Spellbook QA.

Rules-lawyer checks encoded by test:

- DND_5_5E rules family;
- level 5;
- final STR 8 / DEX 14 / CON 14 / INT 18 / WIS 13 / CHA 10;
- HP 32;
- permanent AC 12;
- proficiency bonus +3;
- spell save DC 15;
- spell attack +7;
- slots 4/3/2;
- exactly 4 Wizard cantrips;
- exactly 9 level-1+ Wizard-source prepared spells;
- Sage Magic Initiate source carries Light, Message and Comprehend Languages;
- High Elf source carries Detect Magic and Misty Step without consuming the nine Wizard prepared selections;
- Arcana Expertise through Scholar;
- Evocation Savant, Potent Cantrip and Memorize Spell present;
- Sculpt Spells absent because SRD 5.2.1 Evoker receives it at level 6.

The spellbook volume remains legal: normal Wizard progression reaches 14 level-1+ book spells by Wizard 5 before Evocation Savant free additions.

## Fixture 3 - Mara de los Siete Umbrales

Path:

`qa/pc-sheet/fixtures/03_mara_siete_umbrales_custom_extended.json`

Purpose:

- explicitly CUSTOM / non-SRD;
- high-volume stress fixture;
- force Custom Statistics Extended behavior;
- stress family-native continuation pages.

Payload includes at least:

- 4 custom attributes;
- 10 custom skills;
- 26 long traits;
- 10 resources;
- 8 class/custom options;
- 34 inventory entries;
- 24 custom spells;
- 9 long note cards;
- 7 custom markers;
- custom spellcasting keyed to a custom attribute;
- structured combat damage and resource placements.

Automated export-plan QA proves that `EXTENDED_PAGE` with Custom v2 requires `CUSTOM_STATISTICS`, creates a Spellbook plan when requested, and retains overflow routes for Traits, Resources, Inventory, Spells and Notes.

## App-owned import path

All fixtures are normal:

`dnd-custom-aid.character-backup` version 2 documents.

Android Character Directory -> **Importar**:

- user chooses the destination campaign;
- import restores a new copy;
- nested IDs/references are remapped;
- source fixture identity is not reused.

Fixture tests prove each file:

- decodes through `CharacterBackupCodec`;
- round-trips through the same codec;
- can be prepared as an independent restore-as-copy.

## Hosted DEV SQL seed

Prepared:

`database/qa/seed_pc_sheet_runtime_characters.sql`

Run from repository root:

```
psql "$DATABASE_URL" \
  -v allow_qa_seed=true \
  -v actor_user_id='<existing DEV app_user UUID>' \
  -f database/qa/seed_pc_sheet_runtime_characters.sql
```

Safety properties:

- refuses to run without explicit `allow_qa_seed=true`;
- requires an existing DEV `app_user` UUID;
- does not create or guess a real identity;
- creates/updates only deterministic campaign `QA - PC Sheet PDF Runtime`;
- inserts/updates only the three deterministic QA PCs;
- stores the exact fixture JSON in hosted `pc.snapshot`;
- uses snapshot format `dnd-custom-aid.character-backup`, version 2;
- owner/controller remain null by default so the seed does not manufacture Player ownership;
- unchanged reruns do not advance PC revision.

**Important:** this SQL was prepared and repository-validated but was **not executed against the hosted DEV database in this work**. No authenticated Neon/database capability is available in this chat.

## Rules sources

Rules audit source of truth:

- official D&D Beyond SRD distribution: https://www.dndbeyond.com/srd
- SRD 5.2.1 PDF: https://media.dndbeyond.com/compendium-images/srd/5.2/SRD_CC_v5.2.1.pdf

SRD 5.2.1 attribution is recorded in `qa/pc-sheet/README.md`.

## Repository validation

Push Scaffold:

- #3348 / `35914303487` - **SUCCESS**;
- backend - PASS;
- hosted database - PASS;
- Android renderer sync guard - PASS;
- Android PDF delivery guard - PASS;
- Kotlin/shared/Desktop tests - PASS;
- Android APK - PASS;
- existing populated PDF proof uploads - PASS.

The initial #3346/#3347 red runs were caused only by the new test omitting the required `stateSelection` constructor argument. Commit `a06b2fe5712426e6b42e5ea88f0fe0b2f9529fd1` corrected the test to request `PERMANENT`; no fixture data or production behavior was weakened to obtain PASS.

## Resume point

When owner device QA becomes possible:

1. use the three JSON fixtures through Android **Importar**, or seed the dedicated hosted QA campaign with the guarded SQL;
2. Aldren: Save + open PDF, then Share;
3. Ilyra: Custom v2 + Spellbook; then make one unsaved edit and verify **Exportar sin guardar** changes the PDF without saving the character;
4. Mara: Custom-v2 Attribute and Ability variants with `Extended Page`; inspect custom-stat and overflow continuation pages;
5. request Current Snapshot once and verify the known fallback notice;
6. record the real-device results in repo;
7. only after that manual gate closes, continue to the next Wave 7 package.

No PDF renderer/layout/product behavior changed in this fixture package.


## Integration closure

PR #91 was merged into `main` as:

`5e778ced3b85fc66d4ca449727a3451e91c50d7e`

Final exact-head validation before merge:

- push #3358 / `35914808570` — **SUCCESS**;
- PR #3359 / `35914813999` — **SUCCESS**.

Post-merge main validation:

- Scaffold #3360 / `35915153718` — **SUCCESS**;
- backend — PASS;
- hosted database — PASS;
- Kotlin/shared/Desktop tests — PASS;
- Android debug APK — PASS;
- Android renderer + PDF delivery guards — PASS;
- PC-sheet source-render and populated-proof uploads — PASS.

The PC Sheet runtime QA character pack is therefore **INTEGRATED / REPOSITORY-VERIFIED**.

The SQL seed remains prepared but **not executed against hosted DEV** in this work.

The next action remains the real-device Android PDF smoke using these fixtures. No Media / Handouts work is authorized before that manual result is recorded.

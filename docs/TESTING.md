# Testing and Verification

## Current status

Phases 0–3 are complete. Phase 4 Character Foundation Closure implementation is complete through M5 pre-QA consolidation.

Current position:

- M1 scope traceability — COMPLETE;
- M2 code-health/static architecture audit — COMPLETE;
- M3 historical implementation-completeness audit — COMPLETE;
- M4 six approved inter-batch scope holes — COMPLETE;
- M5 bounded cleanup, full regression and replacement candidate freeze — GREEN / FROZEN;
- M6 owner real-device QA — **NEXT / NOT YET STARTED**.

The active QA candidate is not historical Batch L. Use only the M5 frozen replacement candidate recorded below.

Green CI is technical evidence, not owner acceptance.

## 1. Core rule

Never claim a test passed unless it was actually executed successfully against the relevant revision.

Every meaningful implementation or QA batch should state:

- what was tested;
- how;
- what passed/failed;
- what was not tested when material;
- relevant device/environment information when material.

Automated verification and manual real-device acceptance are separate gates.

## 2. Standard automated verification

### Kotlin / Android / Desktop / SQLDelight

```bash
gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace
```

Current CI uses JDK 17, Gradle 9.5 and Android SDK platform 36.

### Backend

```bash
cd backend
npm install --no-package-lock
npm run check
```

The established full gate runs both surfaces even when one area is unchanged, unless a smaller intermediate batch gate is explicitly documented.

## 3. Exact active M6 candidate

Owner QA must use only:

- branch `tmp/phase4-m5-frozen-qa-candidate`;
- exact commit `adc286b3e1305ed706c2ed04d478a43652f6b365`;
- exact tree `fd1f7feffde082b34cce41248e951a25eed7a004`;
- ordinary clean standard workflow `33911956696` — SUCCESS;
- independent exact-SHA validator workflow `33912322920` — SUCCESS;
- validator artifact ID `9951922423`, name `phase4-m5-frozen-qa-apk`;
- artifact ZIP digest `sha256:5fb8d7f281dbf937def89db4377e9b4157c46343f07721912aa759bb52d6f9fa`;
- exact APK size `35,720,588` bytes;
- exact APK SHA-256 `e31ce44a84cd79260ea2c51c65cb6a63675b1f916998e44d583358d72893c8ee`.

Owner-facing filename:

`DND_Custom_Aid_Phase4_M6_QA_Candidate_2026-09-04.apk`

The frozen candidate branch must not be changed.

## 4. Automated evidence already complete

M5 automated evidence includes:

- D-0047 re-traceability for all six M4 repairs;
- full shared/Kotlin tests;
- `CharacterOwnerLineageMigrationTest` covering the prior owner schema lineage;
- Android debug assemble;
- Desktop build;
- backend Worker type-check;
- exact-SHA detached checkout verification;
- exact APK hash and size capture.

No schema or persistence migration was added by M4/M5. The current closure schema remains the tested schema 9 line.

The deprecated Kotlin Multiplatform `androidLibrary` target warning is known and intentionally deferred because migrating build target APIs is a structural maintenance change, not a demonstrated QA blocker.

## 5. Critical first QA rule — migration before clean install

**Do not clear app data before the first owner QA test.**

The first test must exercise the real owner upgrade path:

1. keep the existing prior owner-QA app installation and data;
2. install the exact M6 candidate over it;
3. open the app;
4. verify existing campaigns and characters still exist;
5. verify representative General, Combate, Equipo/Monedas, Conjuros and Notas data survive and reopen;
6. record any migration/data-preservation defect before doing anything destructive;
7. only after this pass may the owner clear data or perform a fresh-install QA pass.

A clean install cannot substitute for this migration test.

## 6. Required device/layout matrix

D-0047 makes this closure explicitly a **phone + tablet acceptance package**.

Owner acceptance therefore requires:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. representative larger application text scale.

Tablet is not secondary. The UI should exploit available width where the implementation provides adaptive/master-detail behavior instead of merely stretching phone components.

## 7. M6 owner QA matrix

The QA pass is a focused closure acceptance matrix rather than a verbatim rerun of every historical implementation test.

### Upgrade and persistence

- install over existing data first;
- campaigns survive;
- characters survive;
- representative durable content survives;
- save/reopen remains correct;
- full app close/reopen remains correct.

### Navigation and context

- all main tabs reachable;
- conditional tabs appear/disappear correctly without deleting data;
- last open tab restores across full restart;
- hidden/stale last-tab destinations resolve safely;
- rotation preserves practical context;
- parent/list/tab/search/filter/sort/selection context behaves sensibly.

### Editing and IME

- keyboard does not hide required action controls;
- Add/Edit/Delete grammar is consistent;
- inline validation is understandable;
- named destructive confirmation works;
- unsaved leave offers Save / Discard / Keep editing where intended;
- saved/unsaved state is visible and correct.

### General / Habilidades / Combate

- class/subclass/level identity and rules/source badges;
- structured proficiencies/languages;
- custom skills and passive values;
- defenses/senses/movement;
- quick HP and death saves;
- combat/action metadata;
- representative Favorite/Quick Access behavior.

### Gestión

- conditions/exhaustion;
- active concentration;
- generic resources and one-tap changes;
- rest assistant preview/apply;
- temporary effects;
- reconciliation checkpoints;
- Inspiration/death-save context.

### Equipo / Monedas

- dense list remains usable;
- Manual/A–Z behavior;
- search/filter;
- drag/reorder in Manual mode;
- carried/stored/location metadata;
- equipped/attuned state badges;
- containers/locations;
- consumables/ammunition;
- currencies;
- phone and tablet editors.

### Rasgos / Conjuros / Notas / Trasfondo

- Traits grouping/filter/reorder/use meter/Favorites;
- spell Manual/A–Z, filters, levels, slots, Prepared/source behavior and badges;
- Notes preview/edit/reorder;
- Background fields including Raza and Religión/Fe;
- long-story collapse/expand behavior.

### Conditional modules

Verify representative visibility, edit, save/reopen and hide-not-delete behavior for:

- Artífice;
- Formas;
- Técnicas;
- Metamagia;
- Pactos;
- Compañeros.

Multiclass union must not duplicate equivalent tabs and manual overrides must remain available for custom/homebrew use.

### Table mode

- intended structural edits are blocked;
- live/session controls remain usable where designed;
- enabling Table mode over dirty structural state remains safely handled;
- browsing/search/filter/presentation behavior remains useful.

### Supercompact / Quick Access

- representative favorites/resources render usefully;
- one-tap operational controls work;
- exact editing remains reachable when needed;
- phone/tablet usefulness is acceptable.

### Backup/import

- export own-format backup through system document picker;
- import as a new independent copy;
- source character remains unchanged;
- repeated import creates independent copies;
- reconciliation marker is visible/useful;
- malformed/wrong input fails safely when practical to exercise.

### Larger text and responsiveness

- no critical actions disappear;
- scrolling remains possible;
- dialogs/editors remain operable;
- adaptive/master-detail layouts remain understandable;
- state and source/rules badges remain readable and not color-only.

## 8. Defect handling during M6

Classify findings before changing code:

- visual/ergonomic observation that does not block acceptance;
- minor defect suitable for post-Phase-4 maintenance;
- blocking Phase 4 defect requiring repair before acceptance.

If a blocking finding requires production code change:

1. do not patch the frozen branch;
2. create a focused repair from the appropriate durable line;
3. add focused regression coverage where practical;
4. run the complete automated gate;
5. freeze a **new** exact candidate with commit/tree/workflow/artifact/hash identity;
6. repeat the affected owner QA evidence;
7. preserve the prior frozen candidate as historical evidence.

## 9. Development APK signing

Development CI APKs use a stable **debug-only** signing identity so successive QA APKs can update one another in place and exercise realistic SQLite migration/persistence behavior on owner devices.

The development identity is not a production/release trust boundary and is never to be reused for a real release. A future real release signing identity must remain private and be designed/handled separately.

CI reconstructs the development debug keystore from tracked development-only material before building. Do not expose/reproduce that material in chat or docs merely because it is non-production.

## 10. CI proportionality

Use the existing simple GitHub Actions workflow as a safety gate, not a deployment platform.

Do not add coverage gates, emulator farms, SonarQube, staging, automated production deployment or giant screenshot suites without a concrete requirement.

## 11. Current exact continuation

Current practical resume checkpoint:

`docs/checkpoints/2026-09-04_PHASE4_M6_QA_PAUSE_HANDOFF.md`

When the owner resumes:

1. verify the frozen candidate identity above;
2. verify the APK SHA-256;
3. begin M6 with the in-place upgrade/data-preservation test;
4. proceed through the required phone/tablet matrix;
5. record findings before changing production code.

No DM-feature implementation begins before successful Phase 4 owner QA, final governance/merge-boundary housekeeping and explicit owner closure/merge approval.

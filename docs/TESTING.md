# Testing and Verification

## Current status

Phases 0–3 are complete. Phase 4A implementation is broadly present, the focused pre-QA repair line is technically green through Pass 07, and D-0048 consolidates the current in-progress state into canonical `main`.

Current testing position:

- latest technically verified product build: `0.4.0-preqa.7` / `40700` / `debug`;
- owner phone visual audition Stages A–F: sufficiently covered for this build, with substantial findings recorded;
- owner phone visual acceptance: **NOT PASSED**;
- owner physical tablet acceptance: **NOT PERFORMED**;
- successor repair build: **NOT YET IMPLEMENTED**;
- formal replacement M6 candidate: **NOT FROZEN**;
- Phase 4A owner acceptance/closure: **NOT COMPLETE**.

Green CI is technical evidence, not owner acceptance. Presence on `main` under D-0048 is canonical-development consolidation, not a test result.

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

The established full gate runs both product surfaces even when one area is unchanged, unless a smaller intermediate gate is explicitly documented.

## 3. Latest technically verified product identity

Latest full-gate product code:

- version `0.4.0-preqa.7`;
- build `40700`;
- type `debug`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- tested product tree `3b2f2ab471097d3b108c9a787fc2342c5aad683a`;
- workflow `34171466714` — SUCCESS;
- artifact ID `10035895186`;
- artifact name `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`;
- APK size `36,161,616` bytes;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

The D-0048 consolidation audit verified that the 39 commits after this tested product commit through pre-consolidation head `0ac2d3d190f989549ced4914d7ce98274118a0d3` were documentation/governance-only. D-0048 consolidation therefore does not introduce a newer untested product-code revision.

## 4. Owner phone audition evidence for build 40700

Primary real device:

**Redmi Note 11 Pro 5G**

Use the recorded Stage A–F checkpoints for concrete evidence. Do not restart the full audition merely because the repository moved to `main`.

Major confirmed families include:

- excessive app-wide padding/margins and row fragmentation;
- insufficiently compact card actions and reorder UI;
- shared editor/IME reachability/orientation problems;
- phone landscape incorrectly entering an inadequate wide/tablet interaction model;
- current tablet/wide UI itself requiring redesign/audit;
- Conjuros fixed controls consuming the full usable phone-landscape viewport;
- rotation scroll/context loss;
- oversized Gestión/death-save presentation;
- unclear Consumible/Munición UX;
- generic `Fuente` provenance over-exposed across many editors;
- terminology/localization corrections;
- 40% spacing and typography follow-up needs.

The owner explicitly generalized repeated findings across equivalent cards/elements/windows. Future QA should not force them to repeat the same observation for every surface.

## 5. Current owner-audition conclusion

Build `40700` has yielded enough phone evidence to design the next repair cycle.

It should **not** be promoted to formal M6 merely because the phone audition is complete as an information-gathering exercise.

The owner is currently compiling additional observations outside the formal QA exercise. Those should be recorded and reconciled with the audition backlog before broad repair implementation begins.

## 6. Required device/layout acceptance boundary

D-0047 still makes this closure explicitly a **phone + tablet acceptance package**.

Final owner acceptance requires, at minimum:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. representative larger application text scale.

No physical owner tablet device has yet been recorded, so tablet acceptance is not complete.

The current wide/tablet design is already known to need work. Repair it before treating a tablet test as a final acceptance attempt.

## 7. Historical frozen candidates

Historical frozen candidates remain immutable evidence and are not active QA targets.

### Batch L frozen candidate

- branch `tmp/phase4-l-frozen-qa-candidate`;
- commit `5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27`.

### Historical M5 frozen candidate

- branch `tmp/phase4-m5-frozen-qa-candidate`;
- commit `adc286b3e1305ed706c2ed04d478a43652f6b365`;
- APK SHA-256 `e31ce44a84cd79260ea2c51c65cb6a63675b1f916998e44d583358d72893c8ee`.

A brief 2026-09-08 M6 detour produced one real in-place-upgrade/data-preservation PASS against the historical M5 frozen candidate. That evidence is preserved at:

`docs/checkpoints/2026-09-08_PHASE4_M6_OWNER_QA_PROGRESS.md`

The file is explicitly historical/superseded and does not reactivate that candidate.

## 8. Critical first rule when a replacement formal M6 candidate is eventually frozen

**Do not clear app data before the first formal-M6 upgrade test.**

The first formal-M6 test must exercise the real owner upgrade path:

1. keep the existing prior QA installation/data;
2. install the exact frozen replacement candidate over it;
3. open the app;
4. verify campaigns/characters survive;
5. verify representative General, Combate, Equipo/Monedas, Conjuros and Notas data survive/reopen;
6. record any migration/data-preservation defect before destructive steps;
7. only after that may the owner clear data or perform a fresh-install comparison.

A clean install cannot substitute for this migration test.

## 9. Future formal M6 owner QA matrix

Do not execute this matrix until an exact repaired build is explicitly frozen as the replacement candidate.

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

- keyboard does not hide required actions;
- Add/Edit/Delete grammar is consistent;
- inline validation is understandable;
- named destructive confirmation works;
- unsaved-leave behavior works where intended;
- saved/unsaved state is visible/correct.

### General / Habilidades / Combate

- class/subclass/level identity;
- structured proficiencies/languages;
- custom skills and passive values;
- defenses/senses/movement;
- quick HP/death saves;
- combat/action metadata;
- representative Favorite/Quick Access behavior.

### Gestión

- conditions/exhaustion;
- concentration;
- resources and quick changes;
- rest preview/apply;
- temporary effects;
- reconciliation checkpoints;
- Inspiration/death-save context.

### Equipo / Monedas

- dense list usable;
- Manual/A–Z behavior;
- search/filter;
- drag/reorder in Manual mode;
- carried/stored/location metadata;
- equipped/attuned state;
- containers/locations;
- consumables/ammunition;
- currencies;
- phone/tablet editors.

### Rasgos / Conjuros / Notas / Trasfondo

- Traits grouping/filter/reorder/use meter/Favorites;
- spell Manual/A–Z, filters, levels, slots, Prepared/source behavior and badges;
- Notes preview/edit/reorder;
- Background fields including Raza and Religión/Fe;
- long-story collapse/expand behavior.

### Conditional modules

Representative visibility/edit/save/reopen/hide-not-delete behavior for:

- Artífice;
- Formas;
- Técnicas;
- Metamagia;
- Pactos;
- Compañeros.

### Table mode

- intended structural edits blocked;
- intended live/session controls remain usable;
- enabling over dirty structural state handled safely;
- browsing/search/filter/presentation remains useful.

### Supercompact / Quick Access

- representative favorites/resources render usefully;
- one-tap operational controls work;
- exact editing remains reachable;
- phone/tablet usefulness is acceptable.

### Backup/import

- own-format export works;
- import creates independent copy;
- source remains unchanged;
- repeated import creates independent copies;
- reconciliation marker works;
- malformed/wrong input fails safely where practical.

### Larger text and responsiveness

- no critical actions disappear;
- scrolling remains possible;
- dialogs/editors remain operable;
- phone landscape remains phone-appropriate;
- tablet/wide layouts are understandable/useful;
- state/source/rules presentation remains readable and not color-only.

## 10. Defect handling during future M6

Classify findings before changing code:

- visual/ergonomic observation that does not block acceptance;
- minor defect suitable for later maintenance;
- blocking Phase 4A defect requiring repair before acceptance.

If a blocking finding changes product code:

1. do not patch the frozen branch;
2. branch from canonical `main` or the approved durable repair line;
3. add focused regression coverage where practical;
4. run the complete automated gate;
5. freeze a **new** exact candidate with commit/tree/workflow/artifact/hash identity;
6. repeat affected owner QA evidence;
7. preserve prior frozen evidence.

## 11. Development APK signing

Development CI APKs use a stable **debug-only** signing identity so successive QA APKs can update one another in place and exercise realistic SQLite migration/persistence behavior.

The development identity is not a production/release trust boundary and must never be reused for a real release.

## 12. CI proportionality

Use the existing simple GitHub Actions workflow as a safety gate, not a deployment platform.

Do not add emulator farms, enterprise test management, coverage gates, staging/production deployment or giant screenshot suites without a concrete requirement.

## 13. Exact continuation

Current resume pointer:

`docs/checkpoints/LATEST.md`

Next sequence:

1. finish D-0048 main consolidation;
2. durably record the owner's additional non-QA observations;
3. reconcile them with the existing owner-audition backlog;
4. design one coherent successor repair batch from `main`;
5. run the complete automated gate and identify the successor debug build;
6. perform targeted real-device retesting;
7. only when the baseline is acceptable, freeze a replacement formal M6 candidate and execute the matrix above.

No DM-feature implementation begins before Phase 4A is later accepted and explicitly closed.

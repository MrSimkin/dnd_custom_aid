# Testing and Verification

## Current status

Phases 0–3 are complete. Phase 4A planned successor implementation A–I and the post-audition Player stabilization package are automated full-gate green on the active continuation branch. D-0066 keeps `main` as the canonical baseline while the continuation branch carries the current acceptance line.

Current testing position:

- latest technically verified Player product build: `0.4.0-preqa.8` / `40800` / `debug`;
- owner phone audition of the earlier `40700` build: sufficiently covered and used to drive repairs;
- successor A–I implementation: **COMPLETE / AUTOMATED GREEN**;
- post-audition Player stabilization: **COMPLETE / AUTOMATED FULL-GATE GREEN**;
- consolidated `preqa.8` owner QA: **NOT YET PERFORMED / CURRENTLY PINNED UNTIL OWNER CAN TEST**;
- owner physical tablet acceptance: **NOT PERFORMED**;
- formal replacement M6 candidate: **NOT FROZEN**;
- Phase 4A owner acceptance/closure: **NOT COMPLETE**;
- DM Combat Desk design: documented under D-0068, **NO IMPLEMENTATION TO TEST**.

Green CI is technical evidence, not owner acceptance. Presence on `main` or the continuation branch is repository state, not a test result.

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

## 3. Latest technically verified Player product identity

Current consolidated owner-QA build:

- version `0.4.0-preqa.8`;
- build `40800`;
- type `debug`;
- product source commit `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree `c612c07345ecdfc91d972118314ee649fe2048c4`;
- authoritative validation/checkpoint head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- workflow `34430548061` — **SUCCESS**;
- artifact ID `10134364621`;
- artifact name `dnd-custom-aid-debug-apk`;
- artifact ZIP size `13,321,947` bytes;
- artifact ZIP SHA-256 `b7ead12a7501bbef96fef861321b5bebfd64c631647423b8eab9faec9580699a`;
- extracted APK size `37,996,660` bytes;
- extracted APK SHA-256 `bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

The normal gate verified backend install/check, shared desktop tests, Android debug assembly, Desktop build, stable CI debug signing and APK upload. The downloaded artifact ZIP was independently hashed and matched the GitHub Actions digest exactly; it contains exactly one APK.

Non-blocking backend dependency/tooling warnings were observed (including npm dependency findings and Node/Wrangler setup notices). They did not fail the gate and are not Player acceptance blockers by themselves.

Later commits after the validation head may be documentation-only. A moved branch head does not change the validated product identity unless a newer checkpoint explicitly identifies new product code and a corresponding gate.

## 4. Historical owner phone audition evidence for build 40700

Primary real device:

**Redmi Note 11 Pro 5G**

Use the recorded Stage A–F checkpoints for concrete evidence. Do not restart that historical audition screen by screen.

Major confirmed families from `40700` included:

- excessive app-wide padding/margins and row fragmentation;
- insufficiently compact card actions and reorder UI;
- shared editor/IME reachability/orientation problems;
- phone landscape incorrectly entering an inadequate wide/tablet interaction model;
- current tablet/wide UI requiring redesign/audit;
- Conjuros fixed controls consuming the full usable phone-landscape viewport;
- rotation scroll/context loss;
- oversized Gestión/death-save presentation;
- unclear Consumible/Munición UX;
- generic `Fuente` provenance over-exposed across many editors;
- terminology/localization corrections;
- 40% spacing and typography follow-up needs.

The owner explicitly generalized repeated findings across equivalent cards/elements/windows. QA must not force repeated identical observations for every equivalent surface.

Those findings drove the successor A–I implementation and later stabilization work. Build `40700` remains historical evidence, not the current QA target.

## 5. Current owner-audition conclusion

The repaired/stabilized `preqa.8 / 40800` build is the current **consolidated Player owner-QA build**.

It has automated full-gate evidence but has **not** yet received physical owner acceptance. The owner currently cannot test, so the physical QA is pinned until testing becomes possible.

Do not create a new planned Player engineering increment or replace physical acceptance with more micro-auditions merely because QA is waiting.

When QA resumes, run one coherent consolidated Player pass. If blockers are found, classify them and repair only the acceptance-blocking issues actually observed.

`preqa.8` must not be promoted to formal M6 solely because automated verification is green.

## 6. Required Player device/layout acceptance boundary

D-0047 still makes Phase 4A closure explicitly a **phone + Player-tablet acceptance package**.

Final owner acceptance requires, at minimum:

1. phone portrait;
2. phone landscape;
3. Player tablet portrait;
4. Player tablet landscape;
5. representative larger application text scale.

No physical owner tablet device has yet been recorded, so tablet acceptance is not complete.

The A–I successor includes a separate Player tablet portrait/landscape redesign, but automated compilation/testing cannot substitute for physical tablet acceptance.

The future **DM Combat Desk** is a different surface. D-0068 defines it as tablet-landscape only, but no DM implementation exists yet and therefore it is outside the current Phase 4A Player QA matrix.

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

## 8. Critical first rule when consolidated preqa.8 physical QA resumes

**Do not uninstall the existing app and do not clear app data before the first `preqa.8` upgrade/data-preservation test.**

The first physical test of the consolidated build must exercise the real owner upgrade path:

1. keep the existing prior QA installation/data;
2. install the exact `0.4.0-preqa.8 / 40800` APK over it;
3. open the app;
4. verify campaigns/characters survive;
5. verify representative General, Combate, Equipo/Monedas, Conjuros and Notas data survive/reopen;
6. fully close/reopen and verify persistence;
7. record any migration/data-preservation defect before destructive steps;
8. only after that may the owner clear data or perform a fresh-install comparison.

A clean install cannot substitute for this migration test.

This first test is part of the consolidated Player QA even though `preqa.8` is not yet the formal frozen M6 candidate.

## 9. Consolidated Player owner-QA coverage before formal freeze

Do not turn this into repeated tiny APK tests. The owner should cover the repaired Player baseline coherently, including:

- phone portrait and phone landscape;
- Player tablet portrait and landscape when a device is available;
- representative larger application text scale;
- app spacing and multiline/open-text-field density;
- editor/IME Save/Cancel/Delete/reachability and practical rotation behavior;
- General, Habilidades, Combate and Gestión live state;
- Equipo/Monedas, Rasgos, Conjuros, Notas and Trasfondo;
- linear one-column reorder mode in Equipo/Rasgos/Notas while preserving multi-column browsing outside reorder mode;
- Application Settings text/spacing preview, curated fonts, themes and haptics;
- conditional module visibility/edit/save/reopen behavior;
- Table mode and Supercompact/Quick Access;
- backup/import;
- persistence across app close/reopen and relevant orientation changes;
- phone landscape remaining a phone interaction model;
- Player tablet layouts providing useful context without permanent empty panes.

If a blocker is found, repair the blocker, run the full automated gate for the repaired product, identify the exact replacement build, and resume only the affected acceptance evidence plus any necessary regression—not a complete restart by default.

## 10. Future formal M6 owner QA matrix

Do not execute this matrix as the **formal frozen-candidate matrix** until an exact owner-audited repaired build is explicitly frozen as the replacement candidate. The consolidated `preqa.8` pass above is the prerequisite acceptance/audition boundary.

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
- Player tablet layouts are understandable/useful;
- state/source/rules presentation remains readable and not color-only.

## 11. Defect handling during consolidated QA and future M6

Classify findings before changing code:

- visual/ergonomic observation that does not block acceptance;
- minor defect suitable for later maintenance;
- blocking Phase 4A defect requiring repair before acceptance.

If a blocking finding changes product code:

1. do not patch a frozen historical branch;
2. continue from the approved durable continuation/repair line;
3. add focused regression coverage where practical;
4. run the complete automated gate;
5. identify the exact repaired build/commit/tree/workflow/artifact/hash;
6. repeat affected owner QA evidence;
7. preserve prior frozen evidence;
8. only freeze a new formal M6 candidate once the owner-audited baseline is acceptable.

## 12. Development APK signing

Development CI APKs use a stable **debug-only** signing identity so successive QA APKs can update one another in place and exercise realistic SQLite migration/persistence behavior.

The development identity is not a production/release trust boundary and must never be reused for a real release.

## 13. CI proportionality

Use the existing simple GitHub Actions workflow as a safety gate, not a deployment platform.

Do not add emulator farms, enterprise test management, coverage gates, staging/production deployment or giant screenshot suites without a concrete requirement.

## 14. Exact continuation

Current resume pointer:

`docs/checkpoints/LATEST.md`

Current sequence is intentionally waiting on physical availability:

1. leave Player product code unchanged while physical QA is unavailable unless a concrete blocker is discovered independently;
2. additional DM **design/discovery documentation** may occur, but no DM implementation begins;
3. when the owner can test, install exact `preqa.8 / 40800` over existing QA data first;
4. execute the consolidated Player owner-QA coverage;
5. classify and repair only observed Phase 4A blockers;
6. when the owner-audited baseline is acceptable, freeze the replacement formal M6 candidate;
7. execute the formal matrix above;
8. explicitly accept/close Phase 4A.

Only after explicit Phase 4A closure may DM implementation begin. D-0068 is the design resume document for that future work.

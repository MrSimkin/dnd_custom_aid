# Testing and Verification

## Current status

Phases 0–3 are complete. Phase 4A planned successor implementation A–I and the post-audition Player stabilization package are automated full-gate green on the active continuation branch. D-0066 keeps `main` as the canonical baseline while the continuation branch carries the current acceptance line.

Current testing position:

- latest technically verified Player product build: `0.4.0-preqa.8` / `40800` / `debug`;
- owner phone audition of the earlier `40700` build: sufficiently covered and used to drive successor repairs;
- successor A–I implementation: **COMPLETE / AUTOMATED GREEN**;
- post-audition Player stabilization: **COMPLETE / AUTOMATED FULL-GATE GREEN**;
- consolidated `preqa.8` owner phone QA: **PERFORMED THROUGH PORTRAIT + LANDSCAPE + REPRESENTATIVE LARGER TEXT**;
- `preqa.8` owner result: **NOT ACCEPTED — BLOCKING REPAIR PASS REQUIRED**;
- owner physical tablet acceptance: **NOT PERFORMED / INTENTIONALLY DEFERRED UNTIL SHARED SYSTEMIC DEFECTS ARE REPAIRED**;
- formal replacement M6 candidate: **NOT FROZEN**;
- Phase 4A owner acceptance/closure: **NOT COMPLETE**;
- DM Combat Desk design: documented under D-0068, **NO IMPLEMENTATION TO TEST**.

Controlling current QA checkpoint:

`docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`

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

A defect first observed on phone may still be a cross-device repair requirement when the root cause is a shared state authority, shared component, shared layout primitive, shared spacing policy, shared interaction primitive or shared product concept. That does **not** convert inference into physical tablet test evidence.

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
- artifact ZIP SHA-256 `b7ead12a7501bbef96f861321b5bebfd64c631647423b8eab9faec9580699a`;
- extracted APK size `37,996,660` bytes;
- extracted APK SHA-256 `bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

The normal gate verified backend install/check, shared desktop tests, Android debug assembly, Desktop build, stable CI debug signing and APK upload. The downloaded artifact ZIP was independently hashed and matched the GitHub Actions digest exactly; it contains exactly one APK.

Non-blocking backend dependency/tooling warnings were observed. They did not fail the gate and are not Player acceptance blockers by themselves.

Later documentation-only commits do not change the validated product identity unless a newer checkpoint explicitly identifies new product code and a corresponding gate.

## 4. Historical owner phone audition evidence for build 40700

Primary real device:

**Redmi Note 11 Pro 5G**

Use the recorded Stage A–F checkpoints for concrete evidence. Do not restart that historical audition screen by screen.

Major confirmed families from `40700` included:

- excessive app-wide padding/margins and row fragmentation;
- insufficiently compact card actions and reorder UI;
- shared editor/IME reachability/orientation problems;
- phone landscape incorrectly entering an inadequate wide/tablet interaction model;
- tablet/wide UI requiring redesign/audit;
- Conjuros fixed controls consuming the full usable phone-landscape viewport;
- rotation scroll/context loss;
- oversized Gestión/death-save presentation;
- unclear Consumible/Munición UX;
- generic `Fuente` provenance over-exposed across many editors;
- terminology/localization corrections;
- 40% spacing and typography follow-up needs.

The owner explicitly generalized repeated findings across equivalent cards/elements/windows. QA must not force repeated identical observations for every equivalent surface.

Those findings drove the successor A–I implementation and later stabilization work. Build `40700` remains historical evidence, not the current QA target.

## 5. Current owner-audition conclusion for preqa.8

The repaired/stabilized `preqa.8 / 40800` build received a consolidated physical owner phone pass.

The pass established real PASS evidence for:

- in-place upgrade over the previous QA installation/data;
- campaigns/characters and representative data preservation;
- full close/reopen persistence;
- phone portrait baseline navigation;
- representative editor IME / Save / Cancel / Delete function;
- currency;
- Conjuros portrait source/context behavior;
- Notas normal browsing/editing/search;
- Application Settings functionality/understandability;
- representative conditional modules;
- representative backup/export;
- phone landscape retaining the phone interaction model;
- representative larger application text scale.

The same pass also found Phase 4A blockers requiring repair before acceptance. The detailed authoritative list is in:

`docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`

Key blocker families include:

- General/Combate HP canonical-state inconsistency;
- cumbersome damage/healing operation;
- oversized fixed/sticky viewport regions;
- rejected special one-column reorder mode;
- Rasgos provenance still requiring redundant typing of known origins;
- unwanted Trasfondo photo-UX redesign;
- nearly always full-height shared editors;
- awkward structured-damage editor interaction;
- PC Settings visual/IA redesign need;
- disproportionate compactness behavior;
- theme selector needing three-color shorthand plus current preview;
- Table mode activation failure;
- Supercompact conceptual redesign requirement;
- short-height phone-landscape sticky/vertical-spacing failures;
- custom-skill geometry mismatch and minor `ⓘ` alignment issues.

`preqa.8` must not be promoted to formal M6.

## 6. Required Player device/layout acceptance boundary

D-0047 still makes Phase 4A closure explicitly a **phone + Player-tablet acceptance package**.

Final owner acceptance requires, at minimum:

1. phone portrait;
2. phone landscape;
3. Player tablet portrait;
4. Player tablet landscape;
5. representative larger application text scale.

Phone portrait, phone landscape and representative larger-text evidence now exist for `preqa.8`, but the build failed acceptance because blockers were found.

Physical tablet portrait/landscape QA is intentionally deferred until a repaired successor build exists. This avoids spending physical QA effort on shared defects already known to contaminate the tablet experience.

Cross-device repair rule:

- if a defect is rooted in shared state, shared components, shared interaction primitives, shared spacing/density, shared special-mode logic or shared product concept, repair it across phone and tablet surfaces by default;
- do not describe tablet as physically PASS or FAIL until the repaired build is actually tested there.

The future **DM Combat Desk** is a different surface. D-0068 defines it as tablet-landscape only, but no DM implementation exists yet and it remains outside the current Phase 4A Player QA matrix.

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

## 8. preqa.8 upgrade/data-preservation rule — completed for this build

The critical first physical upgrade test for `preqa.8 / 40800` has now been executed successfully:

1. existing prior QA installation/data was kept;
2. exact `preqa.8 / 40800` was installed over it;
3. campaigns/characters survived;
4. representative General, Combate, Equipo/Monedas, Conjuros and Notas data survived/reopened;
5. full close/reopen persistence succeeded.

Result: **PASS**.

Do not discard this evidence or repeat it merely because documentation moved forward. A later repaired successor build will need its own appropriate update/regression evidence depending on what persistence boundaries change.

## 9. Acceptance-repair and retest coverage before formal freeze

The next build should not restart the entire `preqa.8` pass from zero by default. Repair the accepted blocker set, run the exact automated gates, then retest the affected boundaries plus necessary regression.

Required repair/retest focus includes:

- canonical HP synchronization across General/Combate/shared surfaces;
- damage/healing high-frequency workflow;
- Combat fixed/sticky viewport footprint;
- direct drag-and-drop reorder in the normal active layout, including multicolumn where applicable;
- Rasgos structured provenance flow with no redundant typing for `Clase`, `Subclase`, `Raza` and `Trasfondo`; `Otro` and `Don` may remain free text;
- Trasfondo old preferred photo UX with new persistence retained;
- adaptive shared editor sizing while preserving IME reachability;
- attack damage component editor interaction/labels;
- PC Settings coherent visual/IA redesign;
- compactness proportionality;
- theme selection as name + three-color shorthand + current useful representative preview;
- Table mode activation from clean persisted state plus correct structural/operational behavior;
- Supercompact redesign as a dense PC stat-block-like at-table reference;
- responsive behavior under short-height phone landscape, including Conjuros `Nivel` and other sticky/fixed regions;
- vertical margin/padding policy that responds to available height;
- custom Habilidad geometry and `ⓘ` alignment.

After targeted phone retest is acceptable, execute physical Player tablet portrait and tablet landscape QA on the repaired build.

## 10. Future formal M6 owner QA matrix

Do not execute this matrix as the **formal frozen-candidate matrix** until an exact owner-audited repaired build is explicitly frozen as the replacement candidate.

### Upgrade and persistence

- install over relevant existing data first where required by the new build boundary;
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
- short editors do not consume unreasonable full-height space merely because the shared IME-safe primitive is used;
- Add/Edit/Delete grammar is consistent;
- inline validation is understandable;
- named destructive confirmation works;
- unsaved-leave behavior works where intended;
- saved/unsaved state is visible/correct.

### General / Habilidades / Combate

- class/subclass/level identity;
- structured proficiencies/languages;
- custom skills and passive values;
- custom skills visually integrate with ordinary skills except approved italic distinction;
- defenses/senses/movement;
- canonical HP state remains synchronized across every surface;
- quick HP/death saves;
- damage/healing workflow is practical for frequent combat use;
- combat/action metadata and structured damage;
- representative Favorite/Quick Access behavior;
- fixed/sticky reference regions leave a viable content viewport at normal spacing and short heights.

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
- direct drag/reorder in the active normal layout rather than a forced special one-column mode;
- carried/stored/location metadata;
- equipped/attuned state;
- containers/locations;
- consumables/ammunition;
- currencies;
- phone/tablet editors.

### Rasgos / Conjuros / Notas / Trasfondo

- Rasgos grouping/filter/direct reorder/use meter/Favorites;
- Rasgos canonical structured provenance with no redundant typing for known character origins;
- spell Manual/A–Z, filters, levels, slots, Prepared/source behavior and badges;
- Conjuros sticky/fixed level/source context remains useful without dominating short-height landscape;
- Notes preview/edit/direct reorder;
- Background fields including Raza and Religión/Fe;
- restored preferred photo interaction with durable storage/persistence/backup;
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

- can be enabled from a clean persisted character;
- dirty structural state blocks activation with understandable explanation;
- intended structural edits are blocked once active;
- intended live/session controls remain usable;
- browsing/search/filter/presentation remains useful;
- exit from Table mode works normally.

### Supercompact / at-table PC reference

- reads as a dense, vertically scannable PC stat block inspired by the modern D&D 5.5e monster/NPC information grammar;
- core identity/defense/HP/movement/initiative/attributes/relevant saves and reference data are quickly readable;
- combat/action/resource information is useful at a glance;
- Quick Access/Favorites may contribute but do not define the entire surface;
- deliberate live controls remain compact and useful;
- no parallel character-state authority is created;
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
- available-height policy prevents sticky/fixed controls and excess vertical spacing from consuming the content viewport;
- Player tablet layouts are understandable/useful after shared repairs;
- state/source/rules presentation remains readable and not color-only.

## 11. Defect handling during acceptance repair and future M6

Classify findings before changing code:

- visual/ergonomic observation that does not block acceptance;
- minor defect suitable for later maintenance;
- blocking Phase 4A defect requiring repair before acceptance.

If a blocking finding changes product code:

1. continue from the approved durable continuation/repair line;
2. add focused regression coverage where practical;
3. run the complete automated gate;
4. identify the exact repaired build/commit/tree/workflow/artifact/hash;
5. repeat affected owner QA evidence plus necessary regression;
6. preserve historical evidence;
7. only freeze a new formal M6 candidate once the owner-audited baseline is acceptable.

Automated-boundary strengthening required by the `preqa.8` findings:

- HP tests must verify cross-surface propagation;
- Rasgos tests must exercise the real structured-origin selector flow;
- Table-mode tests must exercise real activation from a clean persisted state;
- responsive tests must explicitly cover short-height phone landscape sticky/fixed regions;
- reorder tests must reflect direct normal-layout drag interaction rather than only a vertical fallback.

## 12. Development APK signing

Development CI APKs use a stable **debug-only** signing identity so successive QA APKs can update one another in place and exercise realistic SQLite migration/persistence behavior.

The development identity is not a production/release trust boundary and must never be reused for a real release.

## 13. CI proportionality

Use the existing simple GitHub Actions workflow as a safety gate, not a deployment platform.

Do not add emulator farms, enterprise test management, coverage gates, staging/production deployment or giant screenshot suites without a concrete requirement.

## 14. Exact continuation

Current resume pointer:

`docs/checkpoints/LATEST.md`

Current sequence:

1. read `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`;
2. discuss/group the owner findings and resolve only remaining design details required for implementation;
3. define a bounded acceptance-repair plan covering shared phone/tablet causes rather than phone-only patches;
4. implement the accepted repair scope on `implementation/phase4a-successor-cycle`;
5. strengthen the exact automated boundaries exposed by owner QA;
6. run the complete automated gate and identify the next monotonic successor QA build exactly;
7. perform targeted phone retest of repaired blockers;
8. once shared/systemic phone defects are acceptable, perform physical Player tablet portrait and landscape QA on that repaired build;
9. freeze the replacement formal M6 candidate only when the owner-audited baseline is acceptable;
10. execute the formal matrix above;
11. explicitly accept/close Phase 4A.

Only after explicit Phase 4A closure may DM implementation begin. D-0068 remains the design resume document for that future work.

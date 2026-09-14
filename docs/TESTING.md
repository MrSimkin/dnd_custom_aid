# Testing and Verification

**Last synchronized:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`

## Current status

Phases 0–3 are complete. Phase 4A Player physical discovery on the current frozen candidate is complete, and the project is now inside the **consolidated post-P17 repair cycle**.

Current testing position:

- current exact frozen physical-QA candidate: `0.4.0-preqa.12 / 41200`;
- candidate commit: `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- candidate Scaffold run `34776627282`: **SUCCESS**;
- artifact ID `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`;
- phone detailed discovery on `preqa.12`: **COMPLETE WITH OPEN FINDINGS**;
- P17 tablet portrait/landscape discovery on the same exact candidate: **COMPLETE WITH OPEN FINDINGS**;
- Repair Round 1 structured dice: **COMPLETE / AUTOMATION GREEN**;
- Repair Round 2 T1 reorder stability: **COMPLETE / AUTOMATION GREEN**;
- next implementation round: **Round 3 shared compact checkbox + responsive grouping**;
- no new repaired physical-QA candidate has been frozen yet;
- Phase 4A owner closure: **NOT COMPLETE**;
- DM implementation: **BLOCKED UNTIL EXPLICIT PHASE 4A OWNER CLOSURE**.

The current live status authorities are:

1. `docs/PROJECT_STATE.md`;
2. `docs/checkpoints/LATEST.md`;
3. the latest completed bounded repair-round checkpoint;
4. this file for testing policy, current testing position and revalidation route.

Green CI is technical evidence, not owner acceptance. Branch location is repository state, not a test result.

## 1. Core rule

Never claim a test passed unless it was actually executed successfully against the relevant revision.

Every meaningful implementation or QA batch should state:

- what was tested;
- how;
- what passed/failed;
- what was not tested when material;
- relevant device/environment information when material.

Automated verification and manual real-device acceptance are separate gates.

A defect first observed on one device may still require cross-device repair when the root cause is a shared state authority, shared component, shared layout primitive, shared spacing policy, shared interaction primitive or shared product concept. That does **not** convert inference into physical evidence on another device.

Accepted physical evidence must be preserved. Do not replay complete phone/tablet suites merely because a later repair round touches another family.

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

The established normal Scaffold gate covers backend install/type-check, stable CI debug-keystore preparation, Kotlin/shared/Android/Desktop build-and-test surfaces and Android debug APK upload.

Every bounded repair round should run focused tests/guards appropriate to that repair plus the normal Scaffold gate before it is marked automation green.

## 3. Current frozen physical-QA candidate

The exact physical discovery baseline remains:

- versionName `0.4.0-preqa.12`;
- versionCode/build `41200`;
- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — **SUCCESS**;
- artifact ID `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

This candidate is immutable evidence. Repair Rounds 1–2 exist on later commits and are **not yet a frozen physical-QA candidate**.

## 4. Physical discovery already completed on `preqa.12`

### Phone

The later detailed 23-check phone pass supersedes any earlier broad interpretation that the complete phone gate was closed, while preserving valid individual PASS evidence:

- checks 1–6 PASS;
- 7–8 OPEN structured-damage defects; Round 1 implementation now complete, targeted physical revalidation pending;
- 9 PASS + direct sign-toggle refinement; Round 1 implementation complete, targeted physical revalidation pending;
- 10–16 PASS, with 16 only an optional compact-density refinement;
- 17.1–17.3 OPEN systemic checkbox/responsive grouping family;
- 18–20 PASS;
- 21 UNASSESSED;
- 22 PARTIAL/AMBIGUOUS;
- 23 PASS.

### Tablet P17

P17 physical discovery on the exact same `preqa.12` APK is complete:

1. install/update + launch PASS;
2. campaign baseline PASS;
3. portrait navigation/adaptive shell PASS;
4. landscape navigation/adaptive shell PASS;
5. rotation/state sanity PASS;
6. Combat portrait PASS;
7. Combat landscape FAIL / T7;
8. canonical HP synchronization PASS;
9. Conjuros portrait FAIL / T5;
10. Conjuros landscape FAIL / same T5;
11. representative non-spell editor/IME PASS + reproduction of phone 17.1 checkbox family;
12. PC Settings PASS;
13. Application Settings responsiveness PASS;
14. Supercompact PASS;
15. Table Mode FAIL / T8;
16. larger text/density PASS;
17. cold persistence/reopen PASS;
18. Conjuros sticky BLOCKED BY T5, not a separate failure.

There is no further broad phone or tablet discovery pass required on `preqa.12`.

## 5. Current repair automation status

### Round 1 — structured dice / signed modifier foundation

Product/test HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`.

Authoritative Scaffold `34787688776` / run `1508` — **SUCCESS**.

Phone 7–9 implementation basis is repaired and automation-protected. Physical revalidation waits for the consolidated new candidate. T2 remains partial because die-result silhouettes, Custom Throw die/custom-sides/signed-modifier UX and Dice-tab ownership of display mode remain.

### Round 2 — T1 reorder target stability

Product/test HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`.

Authoritative Scaffold `34788409987` / run `1519` — **SUCCESS**.

The repair covers **both active reorder models**: one-dimensional/one-column reorder and spatial/multi-column reorder. It uses stable drag-start target geometry, canonical-order preview generation, hysteresis around boundaries, explicit translation during real viewport scrolling and one-time capture of newly revealed lazy targets.

T1 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**.

The existing owner video is the failure baseline and must not be requested again. Future T1 physical revalidation on the consolidated candidate must cover, at minimum:

- one-column reorder;
- multi-column/spatial reorder on a wide layout;
- no target chasing animated preview reflow;
- auto-scroll while dragging where applicable;
- final order persistence after leave/reopen.

A PASS in only one-column or only multi-column mode is insufficient to close T1 because the original physical evidence showed the failure family across layout scenarios and the repair changes shared behavior used by both engines.

## 6. Current implementation route

Next is **Round 3 — shared compact checkbox + responsive grouping**, covering phone 17.1–17.3 and the tablet reproduction.

After Round 3, continue the remaining repair families in dependency-aware order:

- T5 spell-source/bootstrap/source-context compatibility repair;
- T6 class-editor numeric/die controls;
- T3 adaptive Portrait/Landscape card-distribution semantics + T4 symmetric text scale + T9 explicit haptics `None`;
- T7 wide Combat adaptive composition;
- T8 Table Mode structural-affordance enforcement;
- remaining T2 die-result silhouettes, Custom Throw parity and Dice-tab display-mode ownership;
- optional phone-16 compact-density refinement only if safe and materially beneficial.

After the consolidated material repair is complete:

1. run the aggregate Scaffold gate;
2. create/freeze a new monotonic physical-QA candidate;
3. perform **targeted** phone/tablet revalidation only for failed/touched/affected families, phone 21/affected phone 22, and tablet 18 after T5;
4. preserve unrelated accepted PASS evidence;
5. require explicit owner acceptance before Phase 4A closure.

## 7. Failure handling

If physical QA finds a defect:

1. classify whether it is local, shared/systemic, persistence/domain, responsive/layout, or interaction-specific;
2. audit source before asserting a shared root cause;
3. reopen only the relevant accepted repair boundary;
4. repair on `implementation/phase4a-successor-cycle`;
5. preserve storage/import/export/migration, IDs/associations where applicable, canonical-state authority and accepted evidence unless the approved repair explicitly requires a change;
6. run focused tests/guards plus the normal aggregate gate when required;
7. produce a new monotonic QA identity if the frozen physical candidate changes materially;
8. checkpoint exact evidence before another owner pass.

Do not invent an unrelated Player feature or a new numbered repair item merely because the project is waiting at a manual gate.

## 8. Status synchronization rule

At the end of **every bounded implementation/test round**, before proceeding to the next repair family, update all four durable continuity surfaces:

1. the dedicated round checkpoint under `docs/checkpoints/`;
2. `docs/PROJECT_STATE.md`;
3. `docs/checkpoints/LATEST.md`;
4. this `docs/TESTING.md` file, including its **Current status**, current repair automation status and revalidation route whenever affected.

These four surfaces must agree on the latest completed round, automation evidence, next action and physical-revalidation status. Do not allow `TESTING.md` to remain on an older QA candidate or implementation gate after a round advances.

## 9. Phase 4A closure rule

Phase 4A may be marked accepted/closed only after:

- the consolidated repaired candidate has sufficient targeted phone evidence;
- Player tablet portrait/landscape evidence exists for the relevant repaired/touched families;
- blocking defects have been repaired/revalidated as needed;
- unresolved/blocked evidence has been addressed as required;
- the owner explicitly accepts/closes Phase 4A.

No CI result may substitute for explicit owner closure.

No P18 exists. DM implementation remains blocked until explicit Phase 4A owner closure.

## 10. Authority and resume rule

Current branch roles are controlled by `docs/BRANCH_STATUS.md`.

For Player testing/repair, use `implementation/phase4a-successor-cycle`, `docs/PROJECT_STATE.md`, and `docs/checkpoints/LATEST.md`.

For current global/DM discovery state, use `main` and its `docs/checkpoints/LATEST.md`.

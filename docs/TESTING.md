# Testing and Verification

## Current status

Phases 0–3 are complete. Phase 4A Player repair implementation is complete through P1–P16 and automation-qualified. The project is currently at the **physical owner/device QA gate** for `0.4.0-preqa.9 / 40900`.

Current testing position:

- current Player QA candidate: `0.4.0-preqa.9 / 40900`;
- candidate commit: `cd0c203d337c062fa388010d300e875f2f54ced7`;
- normal Scaffold run `34726572588`: **SUCCESS**;
- artifact ID `10307444450` / `dnd-custom-aid-debug-apk`;
- artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`;
- P1–P16 repair implementation: **COMPLETE / AUTOMATION-QUALIFIED**;
- P17 design decision: **CLOSED AS PHYSICAL TABLET-QA GATE POLICY**;
- physical owner/device acceptance of `preqa.9`: **NOT YET PERFORMED/ACCEPTED**;
- Phase 4A owner closure: **NOT COMPLETE**;
- DM implementation: **BLOCKED UNTIL PHASE 4A EXPLICIT OWNER CLOSURE**.

The current detailed QA candidate checkpoint is:

`docs/checkpoints/2026-09-12_PHASE4A_PREQA9_QA_CANDIDATE.md`

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

The established normal Scaffold gate covers backend install/type-check, stable CI debug keystore preparation, Kotlin/shared/Android/Desktop build-and-test surfaces and Android debug APK upload.

## 3. Current QA candidate identity

Current Player physical-QA candidate:

- version `0.4.0-preqa.9`;
- versionCode/build `40900`;
- candidate commit `cd0c203d337c062fa388010d300e875f2f54ced7`;
- candidate commit message: `build: advance repaired QA candidate to preqa.9`;
- workflow `34726572588` — **SUCCESS**;
- artifact ID `10307444450`;
- artifact name `dnd-custom-aid-debug-apk`;
- artifact size `13,608,921` bytes;
- GitHub Actions artifact digest `sha256:2e8c7e3b2a3b11096eaeed3179b707a61b0d24e241c3fb5c31e9a5d99251ba7e`.

The digest above is the GitHub Actions artifact digest; do not relabel it as an independently computed APK-file SHA-256.

The accepted repaired product behavior was already present at `d630270f2f3d8fab94f3c1290963c2da7afaf06d`; the `preqa.9` identity commit exists to provide a monotonic, unambiguous owner-QA package after the earlier `preqa.8 / 40800` build generated the repair backlog.

## 4. Historical `preqa.8 / 40800` owner evidence

The earlier `preqa.8 / 40800` build received real physical phone QA and generated the accepted repair cycle.

Preserve its evidence; do not replay it mechanically as though no QA had occurred.

Key historical checkpoints include:

- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`;
- the accepted P1–P17 repair decision/authorization records;
- individual P implementation/audit/closure checkpoints.

The repaired `preqa.9` candidate is a new physical-QA boundary. Historical `40800` PASS evidence remains valid only for the exact behavior/build boundary it actually exercised.

## 5. Current physical owner-QA sequence

Use `preqa.9 / 40900`.

### Targeted phone regression / acceptance

Prioritize representative repaired shared boundaries rather than replaying every historical screen:

- canonical HP across General/Combate, including damage/heal/temp HP;
- compact Combat HUD footprint and constrained-height behavior;
- representative P6 reorder interaction/persistence;
- representative P9 editor sizing and keyboard Save/Cancel reachability;
- Application Settings density/theme/help behavior;
- PC Settings information architecture;
- P14 Table Mode;
- P15 Supercompact;
- P16 phone landscape / vertical-space / sticky-region behavior;
- Conjuros sticky/source-context behavior;
- persistence/reopen and at least one cross-surface state sanity check.

### P17 physical Player-tablet QA

Proceed to representative tablet portrait/landscape QA when the phone result does not expose a hard shared/systemic failure that would make tablet evidence meaningless.

Representative tablet coverage includes:

- portrait and landscape navigation/adaptive layout;
- Combate / P5 / P16;
- Conjuros sticky behavior;
- representative P9 editor/IME behavior;
- P6 reorder;
- PC Settings and Application Settings responsiveness;
- P15 Supercompact;
- P14 Table Mode;
- representative larger text/density;
- persistence/reopen;
- at least one canonical shared-state sanity check such as HP.

A bounded/local phone defect does not automatically block tablet QA. Actual tablet PASS/FAIL requires actual tablet evidence.

## 6. Failure handling

If physical QA finds a defect:

1. classify whether it is local, shared/systemic, persistence/domain, responsive/layout, or interaction-specific;
2. reopen only the relevant accepted repair boundary;
3. repair on `implementation/phase4a-successor-cycle`;
4. preserve storage/import/export/migration and canonical-state contracts unless the approved defect resolution explicitly requires a change;
5. run the appropriate focused tests plus the normal aggregate gate when required;
6. produce a new monotonic QA identity if the physical candidate changes materially;
7. checkpoint the exact new evidence before another owner pass.

Do not invent an unrelated Player feature or a new numbered repair item merely because the project is waiting at a manual gate.

## 7. Phase 4A closure rule

Phase 4A may be marked accepted/closed only after:

- the repaired candidate has sufficient physical phone evidence;
- Player tablet portrait/landscape QA has actually been performed under P17;
- blocking defects have been repaired/revalidated as needed;
- the owner explicitly accepts/closes Phase 4A.

No CI result may substitute for that explicit owner closure.

## 8. Historical frozen candidates

Historical frozen QA refs remain immutable evidence and are not active QA targets:

- `tmp/phase4-l-frozen-qa-candidate` at `5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27`;
- `tmp/phase4-m5-frozen-qa-candidate` at `adc286b3e1305ed706c2ed04d478a43652f6b365`.

Use `docs/BRANCH_STATUS.md` for lifecycle interpretation and the archive for deliberately removed historical refs.

## 9. Authority and resume rule

Current branch roles are controlled by `docs/BRANCH_STATUS.md`.

For Player testing/repair, use `implementation/phase4a-successor-cycle` and its `docs/checkpoints/LATEST.md`.

For current global/DM discovery state, use `main` and its `docs/checkpoints/LATEST.md`.

DM feature implementation remains blocked until explicit Phase 4A owner closure.

# Phase 4A — preqa.11 repaired QA candidate

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** EXACT VERSIONED CANDIDATE UNDER AUTOMATED VALIDATION / NOT YET OWNER-TESTED

## Candidate identity

- versionName: `0.4.0-preqa.11`
- versionCode: `41100`
- exact candidate commit: `21dc2b0eed4afc261b89578da424cd28d9894500`
- candidate commit message: `build: advance repaired QA candidate to preqa.11`

This is the monotonic successor to physical-evidence candidate `preqa.10 / 41000`; build `41000` is not reused.

## Repair scope contained by this candidate

The candidate contains the physically reopened and bounded P16/P4 repair chain documented in `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_P16_P4_REPAIR_PROGRESS.md`:

- P16 product repair `fcf62103e4d4f1a4167d31efc05d13964c873d6d` — shallow phone-landscape identity/save header + top tabs share horizontal persistent space; constrained Combat HUD spacing/`Cantidad` padding repaired;
- P4 product repair `b40ed12862f73f8e179254b7e86a819962046cf1` — structured damage draft editing stabilized, clipped exact-48dp controls replaced/relaxed, and component internal spacing reduced without enlarging the outer modal.

The previously passed R1–R3 physical behavior remains outside the reopened scope and is expected to remain preserved.

## Pre-version aggregate proof

First normal Scaffold descendant containing both P16/P4 product commits unchanged:

- commit `5a6cb06cbcd3380622a01c27cac3985d65f51ab9`
- run `34732466227`
- conclusion: **SUCCESS**
- backend typecheck: success
- shared/Kotlin tests/builds: success
- Android assemble: success
- APK upload: success

This qualified the repaired source state for versioning.

## Exact candidate validation

Normal Scaffold run:

- run ID: `34732621381`
- exact head SHA: `21dc2b0eed4afc261b89578da424cd28d9894500`
- current status at checkpoint creation: **IN PROGRESS**

Do not claim this candidate automation-green, freeze artifact evidence, or hand it to the owner until this exact run completes successfully.

## Physical gate after exact candidate success

Owner retest should focus on the reopened P16/P4 findings while preserving the existing R1–R3 phone PASS:

- portrait Combat `Cantidad` internal vertical padding/height;
- phone landscape combined header/tab/HUD footprint and useful attacks/actions area;
- rotate landscape ↔ portrait without overlap/reachability/state regression;
- structured damage editor standard die selection;
- `Otro…` die sides editing without state wipe;
- clearing/retyping quantity/sides/modifier without neighboring controls disappearing;
- numeric values fully visible, not clipped;
- compact internal geometry at 100% spacing;
- Save/Cancel remains usable.

P17 tablet QA remains pending. Phase 4A remains open. No P18 exists. DM implementation remains blocked pending explicit owner closure.

# Phase 4A — preqa.11 repaired QA candidate

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** AUTOMATION-GREEN EXACT PHYSICAL-QA CANDIDATE / OWNER RECHECK PENDING

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

The previously passed R1–R3 physical behavior remains preserved and outside the reopened physical recheck scope.

## Pre-version aggregate proof

First normal Scaffold descendant containing both P16/P4 product commits unchanged:

- commit `5a6cb06cbcd3380622a01c27cac3985d65f51ab9`
- run `34732466227`
- conclusion: **SUCCESS**
- backend typecheck: success
- shared/Kotlin tests/builds: success
- Android assemble: success
- Android debug APK upload: success

This qualified the repaired source state for versioning.

## Exact candidate automated proof

Normal Scaffold run:

- run ID: `34732621381`
- exact head SHA: `21dc2b0eed4afc261b89578da424cd28d9894500`
- conclusion: **SUCCESS**
- backend typecheck: success
- shared/Kotlin tests and builds: success
- Android assemble: success
- Android debug APK upload: success

## Artifact evidence

GitHub Actions artifact:

- artifact ID: `10309348779`
- artifact name: `dnd-custom-aid-debug-apk`
- ZIP/archive size: `13,616,149` bytes
- expired at capture: `false`
- GitHub Actions artifact digest: `sha256:752302327bc4af330f5a1ea6e11b6ad61e4324e8cc230675f6d8d2da48fff391`

Independent archive verification after download:

- ZIP SHA-256: `752302327bc4af330f5a1ea6e11b6ad61e4324e8cc230675f6d8d2da48fff391`
- result: **exact match** with GitHub Actions artifact digest

APK extracted from the archive:

- original artifact filename: `androidApp-debug.apk`
- APK size: `38,881,392` bytes
- independent APK SHA-256: `1eebaffbed5e2f0d4479f288575186a916dde0a124e26e72948144bda032ff3a`

The GitHub artifact digest is the ZIP/archive digest. The APK hash is a separate independent digest; do not conflate them.

## Focused physical recheck

Owner retest should focus only on the physically reopened P16/P4 findings while preserving the existing R1–R3 phone PASS:

1. portrait Combat `Cantidad` internal vertical padding/height is materially reduced and proportional;
2. phone landscape uses width to reduce the combined identity/save header + tabs + HUD footprint and leaves a meaningful attacks/actions content area;
3. rotate landscape ↔ portrait without overlap, reachability or character-state regression;
4. standard die selection (`d4`, `d6`, `d8`, etc.) works reliably in the structured damage editor;
5. `Otro…` allows custom sides such as `d5` without wiping quantity/modifier/other controls;
6. clearing/retyping quantity, sides and signed modifier keeps neighboring controls stable while editing;
7. numeric values are fully visible rather than clipped;
8. component/editor internal geometry is materially more compact at normal `100%` spacing without merely enlarging the modal;
9. Save/Cancel remains usable and a valid edited damage component persists correctly.

Portrait relocation of long-card action buttons remains only a consideration and is not part of this candidate's accepted repair scope.

## Gate effect

- R1–R3: preserved physical phone PASS.
- P16: source repaired + automation-green; focused owner phone recheck pending.
- P4: source repaired + automation-green; focused owner phone recheck pending.
- `preqa.10 / 41000`: prior physical evidence candidate; historical after this handoff.
- `preqa.11 / 41100`: current exact repaired physical-QA candidate.
- P17 tablet QA: pending after meaningful phone evidence.
- Phase 4A: open.
- P18: does not exist.
- DM implementation: blocked until explicit owner acceptance/closure.

# Phase 4A — preqa.11 repaired QA candidate

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** AUTOMATION-GREEN EXACT PHYSICAL-QA CANDIDATE / FOCUSED OWNER RECHECK PARTIAL PASS / TRANSVERSAL GEOMETRY REPAIR REQUIRED

## Candidate identity

- versionName: `0.4.0-preqa.11`
- versionCode: `41100`
- exact candidate commit: `21dc2b0eed4afc261b89578da424cd28d9894500`
- candidate commit message: `build: advance repaired QA candidate to preqa.11`

This is the monotonic successor to physical-evidence candidate `preqa.10 / 41000`; build `41000` is not reused.

## Repair scope contained by this candidate

The candidate contains the bounded P16/P4 repair chain documented in `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_P16_P4_REPAIR_PROGRESS.md`:

- P16 product repair `fcf62103e4d4f1a4167d31efc05d13964c873d6d` — shallow phone-landscape identity/save header + top tabs share horizontal persistent space; constrained Combat HUD spacing/`Cantidad` padding repaired;
- P4 product repair `b40ed12862f73f8e179254b7e86a819962046cf1` — structured damage draft editing stabilized, clipped exact-48dp controls replaced/relaxed, and component internal spacing reduced without enlarging the outer modal.

The earlier R1–R3 physical PASS remains preserved.

## Automated proof

Pre-version aggregate descendant containing both P16/P4 product commits unchanged:

- commit `5a6cb06cbcd3380622a01c27cac3985d65f51ab9`
- run `34732466227` — **SUCCESS**

Exact versioned candidate:

- run `34732621381`
- exact head SHA `21dc2b0eed4afc261b89578da424cd28d9894500`
- conclusion **SUCCESS**
- backend typecheck, shared/Kotlin tests/builds, Android assemble and APK upload all succeeded.

## Artifact evidence

- artifact ID `10309348779`
- artifact name `dnd-custom-aid-debug-apk`
- ZIP/archive size `13,616,149` bytes
- GitHub Actions artifact digest `sha256:752302327bc4af330f5a1ea6e11b6ad61e4324e8cc230675f6d8d2da48fff391`
- independently downloaded ZIP SHA-256: exact same value
- APK filename `androidApp-debug.apk`
- APK size `38,881,392` bytes
- independent APK SHA-256 `1eebaffbed5e2f0d4479f288575186a916dde0a124e26e72948144bda032ff3a`

## Owner physical recheck result

Controlling physical progress checkpoint:

`docs/checkpoints/2026-09-12_PHASE4A_PREQA11_OWNER_PHONE_QA_PROGRESS.md`

The owner reported:

- focused checks **1–7: PASS**;
- focused check **8: FAIL**;
- focused check **9: PASS**.

Therefore the specific `preqa.11` fixes are physically accepted for portrait `Cantidad`, phone-landscape combined footprint, rotation, standard/custom dice editing, incomplete-draft stability, numeric clipping, Save/Cancel and persistence.

Check 8 exposed a broader **full-app equivalent-control vertical padding/spacing failure**. The owner supplied an annotated `Editar ataque o acción` screenshot as one example and explicitly clarified that the required fix is transversal across every equivalent object in portrait and landscape, not a one-dialog patch. The `+ / −` structured-dice selector is also explicitly queued for geometry/consistency inspection.

## Gate effect

- R1–R3: preserved physical phone PASS.
- `preqa.11` focused checks 1–7 and 9: physical PASS.
- `preqa.11` check 8: physical FAIL; transversal presentation audit reopened.
- P4 functional structured-damage interaction failure: repaired for tested scope.
- P16 focused phone-landscape shell/usable-height failure: repaired for tested scope.
- full-app equivalent-control vertical padding/spacing consistency: NOT accepted.
- broad phone QA pauses until the transversal repair is completed and revalidated.
- P17 tablet QA remains pending.
- Phase 4A remains open.
- no P18 exists.
- DM implementation remains blocked pending explicit Phase 4A owner closure.

## Next action

Do not rerun the nine-point `preqa.11` list from scratch. Resume from `2026-09-12_PHASE4A_PREQA11_OWNER_PHONE_QA_PROGRESS.md`: perform a full-app inventory of equivalent/shared Player controls that still have disproportionate vertical padding/margins, inspect the `+ / −` selector, repair the shared geometry rather than only the screenshot dialog, validate, then create a new monotonic candidate if product code changes materially.

# Phase 4A — preqa.10 repaired owner-QA candidate

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** AUTOMATION GREEN / REOPENED R1–R3 PHONE REPAIR BOUNDARY PHYSICALLY PASSED / REMAINING TARGETED PHONE QA IN PROGRESS

## Candidate identity

- versionName: `0.4.0-preqa.10`
- versionCode: `41000`
- exact candidate commit: `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3`
- candidate commit message: `build: advance repaired QA candidate to preqa.10`

This identity is the monotonic successor to failed physical candidate `0.4.0-preqa.9 / 40900`. Build `40900` is not reused.

## Repair boundary contained by this candidate

This candidate contains the bounded physical-QA-driven R1–R3 repair chain recorded in `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md`:

- R1 canonical HP state/persistence repair and regression coverage;
- R2 General HP save/navigation canonical propagation repair;
- R3 accepted P2 affected-state feedback repair, Combat `Daño | cantidad | Curar` geometry correction, shared HP-change-impact classifier and regression coverage.

The exact regression-locked pre-version product boundary is `ddd9d01dab4f0b45470174712a5c115de1112d90`, qualified by Scaffold run `34730363231` — SUCCESS.

Comparison from `ddd9d01d…` to candidate commit `a0d7dbd8…` shows no later functional source change: the only product change is the Android version identity (`preqa.9 / 40900` → `preqa.10 / 41000`); the other changes in that range are live continuity documentation.

## Exact candidate automated proof

Normal Scaffold run:

- run ID: `34730531791`
- exact head SHA: `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3`
- conclusion: **SUCCESS**
- backend typecheck: success
- Kotlin/shared tests and builds: success
- Android assemble: success
- Android debug APK upload: success

## Artifact evidence

GitHub Actions artifact:

- artifact ID: `10308364110`
- artifact name: `dnd-custom-aid-debug-apk`
- archive size: `13,611,496` bytes
- expired at capture: `false`
- GitHub Actions artifact digest: `sha256:ef44559ba61267837a286e996d74d5c00b55d89b003d7f6e69bf0902cff5dd32`

Independent archive verification after download:

- ZIP SHA-256: `ef44559ba61267837a286e996d74d5c00b55d89b003d7f6e69bf0902cff5dd32`
- result: **exact match** with GitHub Actions artifact digest

APK extracted from the archive:

- original artifact filename: `androidApp-debug.apk`
- APK size: `38,865,008` bytes
- independently computed APK SHA-256: `ee5db263883c8b1979b12cc9ca365ba9f190009b2b46e4e9b53681408c128c78`

The GitHub artifact digest is the ZIP/archive digest; the APK SHA-256 above is a separate independent hash of the APK file itself. Do not conflate the two.

## Physical owner QA — repaired R1–R3 boundary

The owner physically tested the exact `preqa.10 / 41000` candidate and reported **`1–7 OK`** for the focused repaired boundary. This is direct owner/device evidence, not inferred from automation.

Physical PASS therefore covers:

1. General HP → Combate canonical propagation without requiring global `Guardar` merely to cross tabs;
2. maximum-HP reduction clamps current HP canonically and no `current > max` state was observed;
3. temp-only absorbed damage gives the accepted brief/subtle Temp-only feedback;
4. spillover damage gives the accepted feedback to both Temp + PV when both actually change;
5. healing gives the accepted PV feedback when current HP changes;
6. Combate `Establecer PV` performs the exact current/max correction and projects canonically;
7. `Daño | cantidad | Curar` control height/proportion/padding is coherent with the surrounding Combat HUD.

This physically clears the specific P1/P2/presentation defects that caused `preqa.9` to fail. These seven points do not need to be repeated again absent new contradictory evidence.

## Current physical-QA boundary

The focused R1–R3 repair recheck has passed. Continue the remaining targeted phone regression on the same exact candidate rather than restarting previously passed checks.

Next phone coverage should proceed through the still-unconfirmed representative boundaries, beginning with the compact/fixed Combat HUD and constrained-height/landscape behavior, then representative P6 reorder persistence, P9 editor/IME reachability, settings, P14 Table Mode, P15 Supercompact, P16 vertical-space behavior, Conjuros sticky/source context, and final persistence/reopen/canonical-state sanity.

Representative P17 tablet QA may proceed after phone testing if no hard shared/systemic failure emerges. A bounded/local phone defect does not automatically invalidate unrelated tablet evidence; a hard shared/systemic defect can pause it.

## Gate effect

- `preqa.9 / 40900`: failed physical candidate; preserved historical evidence only.
- `preqa.10 / 41000`: current exact automation-green candidate; reopened R1–R3 repair boundary physically passed.
- Phase 4A: still open; remaining targeted phone QA, P17 representative tablet QA and explicit owner closure are pending.
- P18: does not exist.
- DM implementation: blocked until explicit Phase 4A owner acceptance/closure.

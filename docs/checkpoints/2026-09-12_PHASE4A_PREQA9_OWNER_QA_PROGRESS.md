# Phase 4A — preqa.9 owner/device QA progress and preqa.10 repair handoff

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Failed physical candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Current repaired physical-QA candidate:** `0.4.0-preqa.10 / 41000` at `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3`  
**Status:** PREQA.9 PHYSICAL FAIL PRESERVED / R1–R3 AUTOMATION GREEN / PREQA.10 QUALIFIED AND READY FOR FOCUSED OWNER PHONE RETEST

## Physical evidence preserved from preqa.9

### Step 1 — update-in-place and persistence sanity — PASS

The owner physically confirmed that `preqa.9 / 40900` installed over the previous QA build, launched normally, preserved campaigns and the normal test character, preserved representative saved data across General/Combate/Equipo-Monedas/Conjuros/Notas, and survived a full close/reopen.

### Step 2 — P1/P2 canonical HP and damage/healing — FAIL / REPAIR BOUNDARY REOPENED

Physical findings:

1. General HP edits were not canonical/visible in Combate without an extra global `Guardar`.
2. Increasing maximum HP without silently healing passed.
3. Lowering maximum HP failed to clamp current HP; the owner observed invalid Combate state `20/10`.
4. The accepted subtle changed-HP feedback was absent; this systemic feedback finding need not be repeated for every damage/heal operation.
5. Damage arithmetic, temp-HP absorption/spill and amount clearing passed apart from missing feedback.
6. Healing cap and amount clearing passed.
7. Combate operation → General canonical projection passed.
8. Combate `Establecer PV` did not change current HP, while exact temp-HP correction worked.

### Step 2A — transversal presentation observation

`Daño — Cantidad — Curar` was visibly out of proportion with surrounding Combate UI. This reopened the previously required transversal size/margin/padding consistency boundary; the owner should not have to enumerate the same class of presentation miss control by control.

The owner and assistant explicitly agreed to stop exhaustive QA on known-bad `preqa.9`, preserve these observations as evidence, repair first, and resume on a new candidate.

## Controlling accepted P2 contract

`docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` requires one compact permanent `Daño | cantidad | Curar` row, immediate apply without Save/confirmation, amount reset, canonical HP semantics, and minimal feedback in which only the HP display box(es) actually changed receive a brief/subtle glow or pulse. Temp-only absorption targets Temp only; spillover may target Temp + PV; healing targets PV. No snackbar/toast and no Undo. Exact `Establecer PV` remains a secondary correction path. Compactness must retain usable touch targets.

## Repair progress

### R1 — canonical HP state — IMPLEMENTED + REGRESSION-LOCKED

- `e0397146445c2cd78e7d017943bca1eb76101939` — canonical exact current/max HP operation and invariants.
- `9f3c888b19c694408a2f81d8eae63359d879a3eb` — operational merge preserves proposed max HP and normalizes current against it.
- `f327b6850933e50ec28cf2419bb1c11ae0cefcc9` — direct canonical HP regression tests.
- `49833bb64857376c4931e91c5af684bd287b2aba` — operational merge tests including `20/10 -> 10/10`, max increase without healing and temp-HP preservation.

### R2 — General HP navigation/save propagation — IMPLEMENTED

- `da57a1c1e2fcb952892c75b3f1819954baaa5ce6` — normal General save uses canonical HP semantics; valid HP drafts flush canonically when leaving General; max/current/temp resynchronize together; transient/unparseable typing remains local.

### R3 — P2 affected-state feedback + Combat control proportion — IMPLEMENTED + REGRESSION-LOCKED + AUTOMATION GREEN

- `cc452b156d43967d9eb794a661162c3f3a05f336` — short/subtle affected-HP feedback and repaired `Daño | cantidad | Curar` geometry.
- `ef051af8d2e36a3b765a80487afd9754d5a67e17` — shared `CharacterHpChangeImpact` classifier.
- `ff06bdf1552268f9805c9ff4a3108a3675a22fe5` — Combat HUD consumes the shared classifier.
- `ddd9d01dab4f0b45470174712a5c115de1112d90` — regression tests lock no-change, PV-only, Temp-only and spillover/both targeting.
- Scaffold `34730363231` on `ddd9d01d…`: **SUCCESS** including backend, Kotlin/shared tests/build, Android assemble and APK upload.

## Current repaired candidate — preqa.10 / 41000

Dedicated candidate checkpoint: `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md`.

Identity/evidence:

- versionName `0.4.0-preqa.10`;
- versionCode `41000`;
- exact candidate commit `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3`;
- exact Scaffold run `34730531791` — **SUCCESS**;
- artifact ID `10308364110` (`dnd-custom-aid-debug-apk`);
- archive size `13,611,496` bytes;
- GitHub artifact digest `sha256:ef44559ba61267837a286e996d74d5c00b55d89b003d7f6e69bf0902cff5dd32`;
- independently downloaded ZIP SHA-256: exact same digest;
- APK size `38,865,008` bytes;
- independently computed APK SHA-256 `ee5db263883c8b1979b12cc9ca365ba9f190009b2b46e4e9b53681408c128c78`.

Comparison from regression-locked boundary `ddd9d01d…` to candidate `a0d7dbd8…` contains no later functional source change: the only product delta is the version identity; the other changes are continuity docs.

## Focused owner phone recheck

Resume physical QA on `preqa.10`, not `preqa.9`, beginning with the repaired failures:

1. General HP → Combate without requiring global `Guardar` merely to cross tabs;
2. lowering max HP clamps current HP and never projects current > max;
3. damage fully absorbed by temp HP highlights only Temp briefly/subtly;
4. spillover damage highlights both Temp and PV according to actual change;
5. healing highlights PV when it changes;
6. `Establecer PV` changes exact current/max HP and projects canonically;
7. `Daño | cantidad | Curar` geometry is coherent with the surrounding Combat HUD.

Previously preserved passes do not need to be rediscovered before this focused boundary. If it passes, continue the remaining targeted phone regression and representative P17 tablet QA according to the existing policy.

## Gate effect

- `preqa.9 / 40900`: failed physical evidence only.
- `preqa.10 / 41000`: current exact repaired physical-QA candidate.
- P17 tablet QA: still paused until the focused phone recheck shows no hard shared/systemic failure.
- Phase 4A: open; explicit owner acceptance pending.
- P18: does not exist.
- DM implementation: blocked until explicit Phase 4A closure.

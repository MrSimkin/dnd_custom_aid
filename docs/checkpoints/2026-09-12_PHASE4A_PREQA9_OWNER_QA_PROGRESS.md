# Phase 4A — preqa.9 owner/device QA progress and repair handoff

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Failed physical candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7`  
**New repaired candidate identity:** `0.4.0-preqa.10 / 41000` at `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3`  
**Status:** PREQA.9 PHYSICAL FAIL PRESERVED / R1–R3 AUTOMATION GREEN / PREQA.10 EXACT CANDIDATE QUALIFICATION IN PROGRESS

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

`docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` requires one compact permanent `Daño | cantidad | Curar` row, immediate apply without Save/confirmation, amount reset, canonical HP semantics, and minimal feedback in which **only the HP display box(es) actually changed receive a brief/subtle glow or pulse**. Temp-only absorption targets Temp only; spillover may target Temp + current HP; healing targets current HP. No snackbar/toast and no Undo. Exact `Establecer PV` remains a secondary correction path. Compactness must retain usable touch targets.

## Repair progress

### R1 — canonical HP state — IMPLEMENTED + REGRESSION-LOCKED

- `e0397146445c2cd78e7d017943bca1eb76101939` — canonical exact current/max HP operation and invariants.
- `9f3c888b19c694408a2f81d8eae63359d879a3eb` — operational merge preserves proposed max HP and normalizes current against it.
- `f327b6850933e50ec28cf2419bb1c11ae0cefcc9` — direct canonical HP regression tests.
- `49833bb64857376c4931e91c5af684bd287b2aba` — operational merge tests including `20/10 -> 10/10`, max increase without healing and temp-HP preservation.

### R2 — General HP navigation/save propagation — IMPLEMENTED

- `da57a1c1e2fcb952892c75b3f1819954baaa5ce6` — normal General save uses canonical HP semantics; valid HP drafts flush canonically when leaving General; max/current/temp resynchronize together; transient/unparseable typing remains local.

### R3 — P2 affected-state feedback + Combat control proportion — IMPLEMENTED + REGRESSION-LOCKED + AUTOMATION GREEN

- `cc452b156d43967d9eb794a661162c3f3a05f336` — restores short/subtle feedback around only affected inline HP metric(s), preserves haptics, and rebalances `Daño | cantidad | Curar` with symmetric action space, a narrower amount field and shared compact control height.
- `ef051af8d2e36a3b765a80487afd9754d5a67e17` — shared deterministic `CharacterHpChangeImpact` classification (`NONE`, `HIT_POINTS`, `TEMPORARY_HP`, `BOTH`).
- `ff06bdf1552268f9805c9ff4a3108a3675a22fe5` — Combat feedback consumes that shared classifier.
- `ddd9d01dab4f0b45470174712a5c115de1112d90` — common regression tests lock no-change, PV-only, Temp-only and spillover/both targeting.

Exact regression-locked R3 product boundary: `ddd9d01dab4f0b45470174712a5c115de1112d90`.

Exact R3 qualification run: Scaffold `34730363231` — **SUCCESS**:

- backend typecheck: success;
- Kotlin/shared tests and builds: success;
- Android assemble: success;
- Android debug APK upload: success.

The earlier direct R3 source run `34730201935` on `cc452b15…` also completed fully green, confirming the Compose implementation itself builds/packages before the later regression hardening.

## New monotonic repaired candidate

After R1–R3 became automation-green, the Android identity was advanced monotonically:

- versionName: `0.4.0-preqa.10`;
- versionCode: `41000`;
- exact candidate commit: `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3` (`build: advance repaired QA candidate to preqa.10`);
- exact candidate Scaffold run: `34730531791`.

At this continuity update:

- backend job: **SUCCESS**;
- Kotlin/shared/Android aggregate job: **IN PROGRESS**;
- therefore `preqa.10 / 41000` is **not yet handed off or declared qualified**;
- artifact identity/digest must be captured only after this exact versioned run completes successfully.

## Gate effect

`preqa.9 / 40900` remains failed physical evidence only. P17 tablet physical QA remains PAUSED until the repaired candidate passes the focused owner phone boundary and no hard shared/systemic failure makes tablet evidence misleading. No P18 is created. DM implementation remains blocked pending explicit Phase 4A owner acceptance/closure.

## Exact next action

1. finish exact candidate run `34730531791` on `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3`;
2. if green, capture the exact workflow artifact ID, GitHub artifact digest and independently computed APK SHA-256; create/freeze the `preqa.10` QA-candidate checkpoint and advance live continuity;
3. hand that exact APK to the owner for focused phone retest of the reopened P1/P2/presentation boundary;
4. if that passes, resume the remaining targeted phone regression and representative P17 tablet QA according to the existing gate policy.

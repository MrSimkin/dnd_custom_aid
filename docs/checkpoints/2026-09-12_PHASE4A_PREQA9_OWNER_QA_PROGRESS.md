# Phase 4A — preqa.9 owner/device QA progress

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Candidate:** `0.4.0-preqa.9 / 40900`  
**Candidate commit:** `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Status:** PHYSICAL OWNER QA FOUND BLOCKING SHARED DEFECTS / R1–R3 SOURCE REPAIR IMPLEMENTED / AUTOMATION IN PROGRESS

## Physical evidence preserved

### Step 1 — update-in-place and persistence sanity — PASS

The owner physically confirmed that `preqa.9 / 40900` installed over the previous QA build, launched normally, preserved campaigns and the normal test character, preserved representative saved data across General/Combate/Equipo-Monedas/Conjuros/Notas, and survived a full close/reopen. This remains valid owner/device evidence.

### Step 2 — P1/P2 canonical HP and damage/healing — FAIL / REPAIR BOUNDARY REOPENED

The owner reported:

1. General HP edits did not become canonical/visible in Combate without an explicit global `Guardar`, contrary to the accepted cross-surface behavior.
2. Increasing maximum HP without silently healing passed.
3. Lowering maximum HP failed to clamp current HP; the physical observation included invalid Combate state `20/10`.
4. The accepted subtle changed-HP feedback was absent. This failure applies across the tested damage paths and need not be repeated operation by operation.
5. Damage arithmetic, temporary-HP absorption/spill semantics and amount clearing passed, apart from missing feedback.
6. Healing capped at maximum HP and amount clearing passed.
7. Combate operation → General canonical projection passed.
8. Combate `Establecer PV` failed to change current HP, while exact temporary-HP correction worked.

### Step 2A — transversal presentation observation

The owner additionally reported that the `Daño — Cantidad — Curar` row was visibly out of proportion with surrounding Combate elements. This reopens the previously required transversal size/margin/padding consistency boundary; the owner should not have to report the same class of inconsistency element by element.

## Controlling accepted P2 contract recovered

`docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` is the controlling design record for P1/P2. Its P2 decision requires:

- one compact permanent `Daño | cantidad | Curar` operation row;
- immediate operation without a Save/confirmation step;
- amount reset after application;
- canonical P1 HP semantics, including temp-HP-first damage and healing capped by max HP;
- minimal/non-intrusive visual feedback: only affected HP display box(es) receive a brief/subtle glow or pulse;
- feedback follows actual state change: temp-only absorption highlights Temp only, spillover may highlight Temp + current HP, healing highlights current HP when it changes;
- no snackbar/toast operation message and no Undo;
- exact `Establecer PV` remains a secondary correction path;
- responsive compactness must preserve usable touch targets.

R3 is implemented against this existing contract, not a new UX invention.

## Repair progress

### R1 — canonical HP state boundary — IMPLEMENTED + REGRESSION-LOCKED

- `e0397146445c2cd78e7d017943bca1eb76101939` — canonical exact current/max HP operation enforces non-negative max, `0 <= current <= max`, no silent healing on max increase, clamp on max decrease.
- `9f3c888b19c694408a2f81d8eae63359d879a3eb` — operational merge now carries proposed max HP and normalizes current against that same maximum instead of discarding max HP.
- `f327b6850933e50ec28cf2419bb1c11ae0cefcc9` — direct canonical HP regression tests.
- `49833bb64857376c4931e91c5af684bd287b2aba` — operational merge regression tests, including `20/10 -> 10/10`, max increase without healing and temp-HP preservation.

### R2 — General HP navigation/save propagation — IMPLEMENTED

- `da57a1c1e2fcb952892c75b3f1819954baaa5ce6` — normal General `Guardar` uses the canonical HP operation; operational sync refreshes max/current/temp together; leaving General for another character tab canonically flushes valid HP draft values without requiring global `Guardar`; transient/unparseable numeric typing remains local.

The R2 product diff changed only the intended `CharacterEditorV4.kt` HP-wiring locations. Temporary guarded patch machinery used for the large source file was removed immediately after the product commit.

### R3 — P2 changed-state feedback + Combat control proportion — IMPLEMENTED / AUTOMATION RUNNING

Product commit: `cc452b156d43967d9eb794a661162c3f3a05f336` (`repair: restore combat HP feedback and control proportion`).

The source repair:

- adds an ephemeral feedback target for current/max HP, temporary HP, or both;
- triggers feedback from actual before/after state differences for damage, healing, exact `Establecer PV`, and exact temporary-HP correction;
- renders a short Material-color highlight only around the affected inline HP metric(s), rather than pulsing the entire Combat card;
- restarts the brief feedback window on repeated operations;
- keeps haptic behavior and canonical operation semantics intact;
- gives `Daño` and `Curar` symmetric weighted action space, narrows the numeric amount field relative to the actions, and uses the shared compact single-line control-height policy for all three controls;
- changes only `CharacterCombatOperationalV4.kt`.

Diff guard from pre-R3 live state `97793d3eade15e5b4fb25c750bf515dba6c5d154` to the product commit reports exactly one commit and one modified product file (`CharacterCombatOperationalV4.kt`), with no unrelated source movement.

Exact product Scaffold run: `34730201935` on `cc452b156d43967d9eb794a661162c3f3a05f336`.

At this checkpoint update:

- backend job: SUCCESS;
- Kotlin/shared/Android job: IN PROGRESS;
- therefore R3 is **not yet declared automation-green** and no new QA candidate is declared yet.

## Gate effect

`preqa.9 / 40900` remains a failed physical candidate and historical evidence only. The owner and assistant explicitly agreed not to continue exhaustive physical testing on this known-bad candidate. Existing observations are preserved as repair evidence.

Reopened boundaries remain:

- P1 canonical HP state / General↔Combate propagation and invariants;
- P2 high-frequency damage/heal/exact correction and changed-state feedback;
- transversal presentation consistency for the observed Combat control geometry.

P17 tablet physical QA remains PAUSED until the bounded repair is automation-green and a new monotonic physical-QA candidate exists. No P18 is created. DM implementation remains blocked until explicit Phase 4A owner acceptance/closure.

## Exact next action

1. finish exact Scaffold run `34730201935` and repair any failure rather than accepting R3 by inspection alone;
2. once R1–R3 are automation-green, assign the next monotonic QA identity (do not reuse `40900`), build/package the exact candidate and record run/artifact/digest evidence;
3. resume focused owner phone QA on that repaired candidate before continuing broader/tablet P17 evidence.

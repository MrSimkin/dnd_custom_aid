# Phase 4A — preqa.9 owner/device QA progress

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Candidate:** `0.4.0-preqa.9 / 40900`  
**Candidate commit:** `cd0c203d337c062fa388010d300e875f2f54ced7`  
**Status:** PHYSICAL OWNER QA FOUND BLOCKING SHARED DEFECTS / BOUNDED REPAIR IN PROGRESS

## Evidence recorded

### Step 1 — update-in-place and persistence sanity

Owner report: **PASS** for steps 1–6 of the initial QA handoff.

This confirms, on the owner's physical test device for the current QA pass:

- `preqa.9 / 40900` installed over the previous QA installation without requiring app-data clearing;
- application launched normally;
- existing campaign data remained present;
- the normal test character remained present;
- representative previously saved data remained present across General, Combate, Equipo/Monedas, Conjuros and Notas sanity inspection;
- full application close/reopen completed successfully with the checked data still present.

Interpretation: the initial upgrade/persistence boundary for `preqa.9 / 40900` is **PASS**. This is real owner/device evidence, not inferred from CI.

### Step 2 — P1/P2 canonical HP and damage/healing physical QA

Owner result: **FAIL / SHARED REPAIR BOUNDARY REOPENED**.

The owner tested the agreed General ↔ Combate canonical-HP and combat operation behavior and reported:

1. **FAIL — General HP edits are not live-propagated.** The owner reiterated the prior agreement that changing HP data in General must not require an explicit `Guardar`/commit before the changed canonical value is visible from Combate. This failure is transversal to equivalent General HP fields and must not be requested/reported repeatedly point by point.
2. **PASS — increasing maximum HP did not silently heal.** The tested max-HP increase preserved current HP as intended.
3. **DOUBLE FAIL — maximum-HP reduction/clamping is broken.** After changing the value and using `Guardar` in General, General did not automatically clamp/update current HP. In the physical observation, Combate displayed `20/10`, proving that an invalid `current > maximum` state was allowed to project. The exact observed `20/10` is preserved here as evidence; do not normalize it into the expected test value.
4. **FAIL — agreed subtle changed-HP feedback is absent.** Damage/HP changes did not produce the agreed subtle glow/pulse. The owner states this also applies to the next damage test and should not need to be repeated for every operation.
5. **PASS for damage arithmetic/state semantics, except the already-recorded missing feedback.** Temporary HP absorption/spill behavior and amount clearing worked in the tested path.
6. **PASS — healing semantics.** Healing capped at maximum HP and the amount field cleared in the tested path.
7. **PASS — Combat operation → General projection.** After the tested combat operation, General reflected the resulting canonical HP state without app reopen.
8. **FAIL — `Establecer PV` current-HP correction is ineffective.** Exact correction of current HP caused no current-HP change on either tested surface. The analogous temporary-HP exact correction does work, narrowing the defect to the current-HP correction path rather than the entire exact-state editor.

### Step 2A — transversal presentation observation

The owner additionally reports that the `Daño — Cantidad — Curar` row is visibly out of proportion with the surrounding Combate elements. This is treated as evidence that the previously required full size/margin/padding consistency audit either did not fully land on this element or regressed.

The owner explicitly states that this kind of presentation inconsistency is likely cross-app/transversal and should not have to be repeated on every individual element. The repair pass must therefore inspect the relevant shared sizing/spacing rules rather than patch only this one row cosmetically. Exact historical P-boundary mapping is to be confirmed from the accepted presentation-audit records before code changes; P15/transversal presentation consistency is the likely existing boundary, not a new P-number.

## Repair progress

### R1 — canonical HP state boundary — IMPLEMENTED + REGRESSION-LOCKED / AUTOMATION PENDING

Two concrete source defects were confirmed and repaired:

1. `CharacterCoreOperations.kt` now exposes canonical exact current/max HP operations. They normalize maximum HP to a non-negative value, clamp current HP into `0..max`, preserve current HP when maximum increases, and clamp current HP when maximum decreases. Commit: `e0397146445c2cd78e7d017943bca1eb76101939` (`fix: canonicalize exact hit-point updates`).
2. `CharacterTableModePolicy.kt::mergeCharacterOperationalState` previously discarded `proposed.maxHp` entirely and clamped proposed current HP against the old persisted maximum. It now canonicalizes both proposed current and proposed maximum HP together through the shared exact-state operation. Commit: `9f3c888b19c694408a2f81d8eae63359d879a3eb` (`fix: preserve canonical max and current HP in operational merge`).

Regression coverage is now committed:

- `f327b6850933e50ec28cf2419bb1c11ae0cefcc9` (`test: lock canonical hit-point normalization`) adds direct tests for exact current/max correction, `current <= max`, no silent healing on max increase, clamp on max decrease, and exact-current normalization against the existing maximum.
- `49833bb64857376c4931e91c5af684bd287b2aba` (`test: lock operational HP merge semantics`) corrects the pre-existing operational-merge test that still encoded the old broken assumption that proposed max HP was structural/rejected. It now locks the physical-failure shape (`20/10` proposal normalizes to `10/10`), verifies proposed max HP is carried through, verifies max increase does not heal, and preserves temp HP.

These commits lock the repaired source semantics but **have not yet been declared automation-green**. CI/focused and aggregate validation remain pending after the bounded UI repair lands.

### R2 — General HP navigation/save propagation — IMPLEMENTED / AUTOMATION PENDING

Commit `da57a1c1e2fcb952892c75b3f1819954baaa5ce6` (`fix: persist General HP across navigation and save`) repairs the General-side wiring without introducing per-keystroke persistence:

- ordinary structural `Guardar` now routes current/max HP through the same canonical `setCharacterHitPoints` boundary and clamps temporary HP non-negative before persistence;
- operational synchronization now refreshes **max HP as well as current/temp HP**, so a max-HP reduction that clamps current HP immediately projects the canonical pair back into General;
- a valid General HP draft is canonically persisted when leaving General for another character tab, including Combate, so no global `Guardar` is required merely to make the HP change canonical/visible cross-tab;
- incomplete/transient numeric tokens are not force-persisted: if current, max or temporary HP cannot yet be parsed as integers, the navigation flush returns without inventing a value;
- the repair uses the same shared canonical HP operation already locked by R1 rather than duplicating clamp logic in the UI.

The source commit was produced through a temporary exact-match guarded patcher because the repository connector has no line-patch write operation for the 143 KB editor file. The guard required each intended old fragment to occur exactly once and refused unrelated product-file changes. The resulting product commit was independently inspected: it changes only `CharacterEditorV4.kt` and only the five intended HP wiring fragments. The temporary workflow/helper were then removed; they are not part of the lasting product surface.

R2 is **implemented but not yet automation-qualified or physically accepted**.

Still open after R2:

- changed-state HP glow/pulse remains to be restored/implemented according to the previously accepted P2 feedback contract;
- `Daño — Cantidad — Curar` and the broader transversal size/margin/padding consistency boundary remain to be audited/repaired;
- focused + aggregate validation must cover R1/R2 and the upcoming presentation repair;
- a new monotonic QA identity/artifact is required before owner retest.

## Reopened boundaries and gate effect

Physical owner evidence now reopens at minimum:

- **P1** — canonical HP state and General/Combate propagation/invariants;
- **P2** — combat damage/healing exact-correction and changed-state feedback;
- **transversal size/margin/padding presentation consistency** — exact existing P-boundary mapping to be confirmed before implementation.

R1 and R2 implement the currently identified HP state/persistence repairs, but neither is yet automation- or device-qualified. P17 tablet acceptance remains paused until the full bounded repair is green and a new candidate is physically rechecked.

`preqa.9 / 40900` remains valuable physical evidence but is **not an acceptable Phase 4A candidate** in its present form.

## Exact next action

1. recover the accepted P2 subtle feedback contract from the existing repair/closure records and repair it without inventing a new interaction style;
2. classify and repair the transversal size/margin/padding inconsistency, including the observed `Daño — Cantidad — Curar` row;
3. run focused and aggregate validation covering R1/R2 and the presentation repair;
4. issue a new monotonic QA identity and physical-QA artifact after material product changes are green;
5. resume physical phone QA at this reopened boundary before continuing to tablet P17 evidence.

Do not invent P18 or unrelated Player work. DM implementation remains blocked until explicit Phase 4A owner acceptance/closure.

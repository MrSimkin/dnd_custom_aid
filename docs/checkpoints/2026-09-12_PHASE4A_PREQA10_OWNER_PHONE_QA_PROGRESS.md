# Phase 4A — preqa.10 owner phone QA progress

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Candidate:** `0.4.0-preqa.10 / 41000`  
**Exact candidate commit:** `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3`  
**Exact candidate Scaffold:** `34730531791` — SUCCESS  
**Status:** FOCUSED R1–R3 PHONE RECHECK PASSED; FURTHER PHONE QA FOUND P16/P4 DEFECTS

## 1. Preserved physical PASS — repaired R1–R3 boundary

The owner reported `1–7 OK` for the focused repaired phone boundary. Preserve these passes and do not repeat them absent contradictory evidence:

1. General HP → Combate canonical propagation without requiring a global `Guardar` merely to cross tabs.
2. Maximum-HP reduction clamps current HP; no invalid `current > max` projection.
3. Temp-only damage highlights only Temp.
4. Spillover damage highlights Temp + PV.
5. Healing highlights PV when it changes.
6. Combate `Establecer PV` exact correction changes/projects current/max HP canonically.
7. `Daño | cantidad | Curar` is materially improved/coherent as a three-part operation row relative to the failed preqa.9 candidate.

This physical PASS closes the specific R1–R3 defects that invalidated `preqa.9`.

## 2. New owner evidence — Combat portrait/landscape vertical-space and padding

The owner then tested the compact/fixed Combat HUD in portrait and phone landscape and supplied annotated screenshots.

### Overall result

The owner describes the behavior **in general terms as OK / usable**, but identifies remaining presentation defects that prevent accepting the P16/full vertical-space boundary as complete.

### Portrait finding — excessive internal vertical padding

In portrait, the `Cantidad` numeric field in the permanent `Daño | cantidad | Curar` row still contains visibly excessive top/bottom internal vertical whitespace/padding relative to its text and surrounding controls. The issue is not that the operation row is functionally wrong; the remaining problem is the proportional use of vertical space inside the control.

Owner emphasis: the preferred correction is to remove/reduce unnecessary vertical margin/padding first. Do **not** solve this merely by making surrounding objects larger.

### Phone landscape finding — persistent top shell wastes scarce vertical space

In phone landscape, the upper character shell still reserves a substantial vertical band for the left identity/back area and the right settings/`Guardar` controls. The owner specifically recalls the prior design discussion that shallow/wide phone layouts should exploit width/relocation/compaction to reclaim useful vertical space, rather than preserving portrait-like vertical occupation.

The annotated landscape screenshot also demonstrates that the combined persistent footprint — app/header shell + tab navigation + Combat HUD — leaves a comparatively small primary content region for attacks/actions despite the wide screen.

This is classified as **P16 reopened by real device evidence**. The controlling P16 contract requires:

- actual usable height rather than orientation alone;
- progressive compaction under reduced/constrained height;
- use of width to save height;
- evaluation of the **combined** persistent footprint;
- lower-priority title/header/context chrome yielding before the primary content area becomes impractically small;
- a full-app audit including shared character header/navigation shells.

The historical P16 implementation being automation-green remains valid as automation evidence, but physical owner acceptance for this boundary now **fails/reopens**.

### Combat HUD / P5 relationship

P5's fixed-HUD principle is also relevant: persistent Combat UI should behave as a compact HUD/status strip, minimize unnecessary vertical stacking/internal padding, and leave attacks/actions as the dominant working content area at 100% spacing.

The new evidence does **not** revoke the R1–R3 functional PASS. It records a deeper proportional-padding / combined-footprint defect that remains after the operation row's earlier gross proportion repair.

### Portrait long-card action placement — consideration only

The owner additionally asks that portrait layouts **consider** moving action buttons on long cards when that could recover useful vertical space. This is recorded as a design consideration/recommendation candidate, **not** as an automatically approved layout change. Any implementation should first inspect the current card grammar and interaction consequences.

## 3. New functional defect — attack damage-dice editor

While testing, the owner accidentally activated a weapon/attack damage editor and found the damage-dice control **functionally broken**:

- dice cannot be selected correctly/reliably;
- numeric values/controls are visibly clipped;
- internal elements do not fit their available vertical area correctly;
- the editor/control remains visually too large at the normal `100%` spacing setting.

This is not merely cosmetic. It **reopens P4 — structured attack damage editor UX** because the accepted direct selector-oriented dice workflow is not physically usable in the observed state.

The clipping/oversizing also provides evidence for the shared editor/presentation sizing boundary (P9 and/or proportional spacing primitives); exact secondary root-cause mapping must be determined from source inspection rather than guessed from the screenshot alone.

### Owner repair priority for the damage editor

The owner explicitly rejects "make the box bigger" as the first-line solution.

Repair priority:

1. first reduce/fix unnecessary **internal vertical margin and padding**;
2. ensure the controls/numbers fit and remain fully visible/selectable inside the existing editor geometry;
3. only after the internal spacing is corrected, consider a modest outer-box size adjustment if still genuinely necessary;
4. retain usable touch targets — compactness must not make the controls difficult to operate.

## 4. Gate effect

- `preqa.10 / 41000` remains the exact current physical-evidence candidate.
- R1–R3 focused phone repair boundary: **PHYSICAL PASS**.
- P16 phone landscape / vertical-space combined-footprint boundary: **PHYSICAL FAIL / REOPENED**.
- P4 structured attack damage editor: **PHYSICAL FAIL / REOPENED**.
- P5/transversal proportional padding remains implicated by the Combat HUD evidence; repair must inspect shared sizing/spacing primitives rather than patch only one screenshot cosmetically.
- P17 tablet QA remains pending. No tablet PASS/FAIL is claimed here.
- Phase 4A remains open. No P18 is created. DM implementation remains blocked pending explicit Phase 4A owner closure.

## 5. Exact next action

Under the established QA rule, do not spend owner time exhaustively testing a candidate with known defects that will be superseded.

Next engineering action:

1. inspect and classify the shared character top/header shell plus Combat HUD against P16's combined-footprint policy;
2. inspect the Combat amount-field and related shared spacing primitives for disproportionate vertical padding at 100%;
3. inspect the attack structured-damage editor against P4 and shared P9 sizing/spacing primitives, reproducing the clipped dice/numeric selector defect;
4. repair only the reopened accepted boundaries;
5. strengthen focused regression coverage where feasible;
6. run aggregate validation and issue a new monotonic QA identity if material product code changes;
7. then resume owner physical QA from these reopened points.

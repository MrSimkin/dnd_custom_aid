# Phase 4A — preqa.11 owner phone QA progress

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Candidate:** `0.4.0-preqa.11 / 41100`  
**Exact candidate commit:** `21dc2b0eed4afc261b89578da424cd28d9894500`  
**Status:** FOCUSED P16/P4 RECHECK PARTIAL PASS; TRANSVERSAL FULL-APP SPACING/PADDING BOUNDARY REOPENED

## Physical owner result

The owner physically tested the focused nine-point `preqa.11` recheck and reported:

- **1–7: PASS**
- **8: FAIL**
- **9: PASS**

This is real owner/device evidence. Do not collapse the result into a full candidate PASS or FAIL; preserve the successful repaired behavior while reopening the remaining transversal presentation boundary.

## Physical PASS preserved from this recheck

The following `preqa.11` repairs are physically accepted for their tested scope:

1. **Portrait Combat `Cantidad`** — the previously excessive internal vertical padding/height is materially corrected and proportional enough for the focused check.
2. **Phone landscape combined footprint** — the identity/save header + top-tab use of width materially improves usable vertical area as intended by the P16 repair.
3. **Portrait ↔ landscape rotation** — no reported overlap, reachability or state-regression failure in the focused check.
4. **Standard die selection** — structured attack damage standard die selection works reliably.
5. **`Otro…` custom die sides** — custom sides editing works without wiping neighboring controls.
6. **Incomplete structured editing** — clearing/retyping quantity, sides and signed modifier no longer destroys neighboring controls while editing.
7. **Numeric visibility/clipping** — the previously clipped structured-damage numeric controls are now physically usable/visible.
9. **Save/Cancel + persistence** — controls remain usable and a valid edited damage component persists correctly.

The earlier `preqa.10` R1–R3 physical PASS also remains preserved. Do not restart those checks absent contradictory evidence or a later repair that directly changes their behavior.

## Check 8 — PHYSICAL FAIL: transversal vertical spacing/padding audit still not satisfied

The owner supplied an annotated portrait screenshot of `Editar ataque o acción`. The screenshot is an **example of a broader recurring problem**, not permission to repair only this one dialog.

Observed issue:

- multiple equivalent form/editor objects still carry visibly excessive vertical internal padding / empty vertical space relative to their actual content;
- the annotated example includes ordinary labelled/editable fields and structured-damage controls where the label/value occupies only a small fraction of the object's height;
- the problem applies in **portrait and landscape**;
- the owner had previously required a **full-app audit** of size, vertical margin and padding, so the continued appearance of the same geometry in equivalent objects means that earlier audit was either incomplete in coverage or did not achieve its intended result;
- fixing only `Editar ataque o acción` would repeat the original mistake. The next repair must inspect **every equivalent/shared object and usage across the Player app**, not only the screenshot surface.

### Repair priority / constraint

The owner reiterates the existing geometry principle:

1. reduce/correct unnecessary **internal vertical padding and margin first**;
2. keep content legible and touch targets usable;
3. adjust outer box/control size only if still necessary after internal geometry is corrected;
4. do **not** solve the problem by merely making dialogs/containers larger;
5. equivalent objects should use coherent proportional geometry across portrait and landscape, not one-off per-screen fixes.

This is a **transversal presentation audit reopening** under the accepted P5/P16 compact/usable-height principles and any relevant shared editor/P9 geometry primitives. Exact source classification must be determined from shared components/usages before implementation; do not assume the defect is isolated to P4.

## `+ / −` selector observation

The owner also explicitly asked to **check the plus/minus selector/icon** visible in the structured dice component.

Treat this as a specific audit item inside the same transversal pass:

- inspect the visual size, padding, alignment, sign glyph/icon choice, and touch-target geometry of the `+ / −` selector;
- compare it to equivalent compact selector/action controls in the app;
- do not guess a redesign from this note alone;
- if the current control is inconsistent with the shared compact grammar, repair it through the shared/equivalent-control policy rather than a one-off screenshot-only tweak.

## Clarification about the earlier annotated screenshots

The owner clarified that the earlier first screenshot showing excessive `Cantidad` vertical padding was **portrait**, while the second screenshot was **landscape**. The `preqa.11` focused check #1 confirms the specific portrait `Cantidad` fix is now acceptable. The remaining check-8 failure is a broader class of equivalent controls elsewhere in the app.

## Gate effect

- `preqa.11` focused checks 1–7 and 9: **PHYSICAL PASS**.
- focused check 8: **PHYSICAL FAIL / TRANSVERSAL AUDIT REOPENED**.
- P4 functional structured-damage interaction defects that motivated `preqa.11`: physically repaired for the tested scope.
- P16 phone-landscape shell/usable-height repair: physically repaired for the focused tested scope.
- full-app equivalent-control vertical padding/spacing consistency: **NOT accepted**.
- broad phone QA should pause rather than continue on a candidate known to require another material presentation repair.
- P17 tablet QA remains pending; no tablet PASS/FAIL is claimed.
- Phase 4A remains open.
- no P18 exists.
- DM implementation remains blocked until explicit Phase 4A owner acceptance/closure.

## Exact continuation point for the next session

Do **not** resume by rerunning checks 1–7 or 9 and do not start tablet QA.

Next engineering work is:

1. inventory the shared/equivalent Player UI controls responsible for the observed excessive vertical padding/margins across the **full app**, including but not limited to `Editar ataque o acción`;
2. identify which shared primitives versus one-off usages escaped or defeated the earlier full-app audit;
3. inspect the `+ / −` structured-dice selector as part of that same compact-control audit;
4. define the smallest shared repair that enforces coherent proportional geometry in portrait and landscape while preserving safe touch targets and existing accepted semantics;
5. implement the transversal repair on `implementation/phase4a-successor-cycle` only, without unrelated feature work;
6. update this checkpoint/live continuity after each material repair step;
7. run focused regression + aggregate Scaffold validation;
8. if product code changes materially, create the next monotonic QA identity after `preqa.11 / 41100`, freeze exact commit/run/artifact/digest evidence, and resume owner QA only on the affected transversal geometry boundary before broader phone/tablet QA.

Portrait relocation of long-card action buttons remains only a prior design consideration, not an approved automatic change.

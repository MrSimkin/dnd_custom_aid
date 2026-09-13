# Phase 4A — preqa.10 P16/P4 physical-QA repair progress

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Source physical evidence:** `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md`  
**Status:** P16 + P4 SOURCE REPAIRS IMPLEMENTED / COMBINED AUTOMATION NEXT

## Preserved evidence

The focused `preqa.10 / 41000` R1–R3 owner-phone recheck remains **PASS** and is not reopened by this repair. The later screenshots/observations reopen P16 and P4 only as recorded in the source physical-evidence checkpoint.

## R4 / reopened P16 — combined phone-landscape footprint

### Confirmed source cause

The earlier automation-green P16 implementation correctly measured usable height, made collection/spell stickiness conditional, and reflowed Combat metrics under vertical pressure. Physical `preqa.10` evidence exposed one remaining shared-shell gap: `CharacterAdaptiveShellV4` still rendered the entire identity/save header as one full-width persistent row **above** the full-width top-tab strip in phone landscape.

The Combat `Cantidad` field also retained standard `OutlinedTextField` internal vertical geometry, leaving visibly disproportionate top/bottom whitespace in the permanent HUD.

### Product repair

`fcf62103e4d4f1a4167d31efc05d13964c873d6d` — `repair: compact phone landscape shell and Combat HUD`

Net diff from pre-repair live HEAD `4029c415232ed9941a238c7bbee0b9c7d0678856` to this product boundary changes exactly:

- `CharacterAdaptiveShellV4.kt`
- `CharacterCombatOperationalV4.kt`

Repair behavior:

- in `PHONE_LANDSCAPE` with top-tab navigation and `REDUCED`/`CONSTRAINED` vertical space, identity/save header + scrollable top tabs share **one horizontal persistent row**;
- phone top-tab semantics are preserved; no tablet side-rail switch;
- portrait/tablet shell behavior is unchanged;
- constrained Combat HUD nonessential vertical padding/row spacing is reduced;
- `Daño` / `Curar` retain safe action height;
- `Cantidad` uses an explicitly compact numeric input surface with controlled internal padding;
- R1–R3 HP/damage semantics are unchanged.

## R5 / reopened P4 — structured damage editor

### Confirmed source causes

Source inspection identified two independent causes matching the physical report:

1. quantity/modifier and related numeric Material `OutlinedTextField`s were forced to exactly `48.dp` (`min = 48.dp, max = 48.dp`), clipping their internal label/text geometry;
2. the dice component was reparsed with a strict complete-expression regex after every edit. Intermediate structured states such as `1d`, `d8`, `1d8-`, or empty sides while selecting `Otro…` failed parsing and collapsed the draft controls to blanks.

### Product repair

`b40ed12862f73f8e179254b7e86a819962046cf1` — `repair: stabilize and compact structured damage editor`

Net diff from the live pre-P4 boundary `703dd1ce166ebc6a8a0e7b37b85998467d739d8a` to this product commit changes exactly:

- `CharacterCombatSuccessorV4.kt`

No temporary patch workflow remains in the resulting tree.

Repair behavior:

- the dice draft parser now deliberately accepts incomplete editing tokens (`1d`, `d8`, `1d8-`, etc.) so recomposition does not erase neighboring structured controls;
- save-time validity remains strict: quantity/sides must still parse to positive integers and signed modifier must be a complete integer before the editor can save;
- quantity, modifier, custom die sides, flat damage and custom damage type now use compact inline field surfaces with explicit small internal padding rather than clipped floating-label Material text fields;
- the compact fields keep the shared safe minimum single-line height and are **not** locked to a smaller fixed maximum;
- sign/die/type selector buttons keep a safe minimum height but are no longer artificially capped to exactly 48 dp;
- damage-component card padding/spacing is reduced before any outer-dialog enlargement, matching the owner's explicit priority;
- the outer `CharacterImeSafeEditorDialog` is unchanged; no larger modal/container was introduced.

### Diff/guard evidence

Both repair commits were produced by exact-match guarded one-shot patchers. The temporary workflow files self-deleted. Net repository comparison proves only the intended production files remain changed for each repair boundary.

## Validation state

P16 and P4 are now **source-repaired but not yet declared automation-green or physically reaccepted**. The first aggregate Scaffold gate must run on a descendant containing both `fcf62103…` and `b40ed128…` unchanged. Any compile/test/build failure must be repaired before candidate versioning.

## Gate effect

- R1–R3 physical phone PASS: preserved.
- P16: source repair implemented; automation + focused physical recheck pending.
- P4: source repair implemented; automation + focused physical recheck pending.
- `preqa.10 / 41000`: historical physical-evidence candidate; do not resume broad owner QA.
- next candidate must use a new monotonic version/build identity after green validation.
- P17 tablet QA: pending.
- Phase 4A: open.
- P18: does not exist.
- DM implementation: blocked until explicit Phase 4A owner closure.

## Exact next action

1. run/observe the normal Scaffold gate on this combined repaired source state;
2. repair any failure rather than accepting by inspection;
3. after green aggregate validation, advance to the next monotonic QA identity, validate that exact candidate, freeze artifact/digest evidence, and hand it to the owner;
4. resume focused phone QA only on the reopened P16/P4 boundaries, preserving R1–R3 PASS.

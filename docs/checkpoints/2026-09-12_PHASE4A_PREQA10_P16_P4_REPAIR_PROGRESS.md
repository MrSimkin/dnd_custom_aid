# Phase 4A — preqa.10 P16/P4 physical-QA repair progress

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Source physical evidence:** `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md`  
**Status:** P16 + P4 SOURCE REPAIRS IMPLEMENTED / AGGREGATE AUTOMATION GREEN / NEW CANDIDATE NEXT

## Preserved evidence

The focused `preqa.10 / 41000` R1–R3 owner-phone recheck remains **PASS** and is not reopened by this repair. The later screenshots/observations reopen P16 and P4 only as recorded in the source physical-evidence checkpoint.

## R4 / reopened P16 — combined phone-landscape footprint

Product commit:

`fcf62103e4d4f1a4167d31efc05d13964c873d6d` — `repair: compact phone landscape shell and Combat HUD`

Confirmed source gap: the shared shell still stacked the full-width identity/save header above the full-width top-tab strip in shallow phone landscape. `Cantidad` also retained disproportionate default Material text-field vertical geometry.

Repair behavior:

- `PHONE_LANDSCAPE` + top tabs + `REDUCED`/`CONSTRAINED` height now places identity/save header + scrollable top tabs in **one horizontal persistent row**;
- phone top-tab semantics are preserved; no tablet side-rail switch;
- portrait/tablet shell behavior is unchanged;
- constrained Combat HUD nonessential vertical padding/row spacing is reduced;
- `Cantidad` uses an explicitly compact numeric input surface with controlled internal padding;
- `Daño` / `Curar` retain safe action height and prior R1–R3 semantics.

Net diff from pre-repair live HEAD `4029c415232ed9941a238c7bbee0b9c7d0678856` to this boundary contains exactly:

- `CharacterAdaptiveShellV4.kt`
- `CharacterCombatOperationalV4.kt`

## R5 / reopened P4 — structured damage editor

Product commit:

`b40ed12862f73f8e179254b7e86a819962046cf1` — `repair: stabilize and compact structured damage editor`

Confirmed physical-failure causes:

1. quantity/modifier and related labelled Material numeric fields were forced to exactly `48.dp`, clipping their internal label/text geometry;
2. a strict whole-expression dice parser ran after every edit, so valid intermediate states such as `1d`, `d8`, `1d8-`, or temporarily empty custom sides collapsed the structured draft.

Repair behavior:

- draft parsing accepts incomplete editing tokens so recomposition does not erase neighboring controls;
- save-time validity remains strict;
- quantity, modifier, custom die sides, flat damage and custom damage type use compact explicitly padded inline fields with the shared safe minimum height;
- sign/die/type selector controls keep a safe minimum height without a rigid exact maximum;
- damage-component internal padding/spacing is reduced;
- the outer editor/dialog was **not enlarged**, following the owner's padding-first repair priority.

Net pre/post P4 diff contains exactly `CharacterCombatSuccessorV4.kt`.

## Guard / repository hygiene

Both repairs were applied through exact-match guarded one-shot patchers. Temporary workflow files self-deleted. Net repository comparisons show only the intended production files remain changed.

## Aggregate automated proof

First normal Scaffold descendant containing **both** P16 and P4 product commits unchanged:

- commit: `5a6cb06cbcd3380622a01c27cac3985d65f51ab9` (documentation-only descendant of both product commits)
- run: `34732466227`
- conclusion: **SUCCESS**
- backend typecheck: success
- shared/Kotlin tests and builds: success
- Android assemble: success
- Android debug APK upload: success

This qualifies the combined repaired source state for candidate versioning. It does **not** constitute physical owner acceptance of P16 or P4.

## Gate effect

- R1–R3 physical phone PASS: preserved.
- P16: source repaired + aggregate automation green; focused physical recheck pending.
- P4: source repaired + aggregate automation green; focused physical recheck pending.
- `preqa.10 / 41000`: historical physical-evidence candidate; do not resume broad owner QA on it.
- next candidate must use a new monotonic version/build identity.
- P17 tablet QA: pending.
- Phase 4A: open.
- P18: does not exist.
- DM implementation: blocked until explicit Phase 4A owner closure.

## Exact next action

Advance Android identity to `0.4.0-preqa.11 / 41100`, validate that exact versioned commit with the normal Scaffold workflow, freeze artifact/digest evidence after success, then hand the exact APK to the owner for a focused P16/P4 phone recheck while preserving R1–R3 PASS.

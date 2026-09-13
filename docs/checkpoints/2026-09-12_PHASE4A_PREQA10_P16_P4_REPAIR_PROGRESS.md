# Phase 4A — preqa.10 P16/P4 physical-QA repair progress

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Source physical evidence:** `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md`  
**Status:** P16 SOURCE REPAIR IMPLEMENTED / AUTOMATION PENDING; P4 REPAIR NEXT

## Preserved evidence

The focused `preqa.10 / 41000` R1–R3 owner-phone recheck remains **PASS** and is not reopened by this repair. The later screenshots/observations reopen P16 and P4 only as recorded in the source physical-evidence checkpoint.

## R4 / reopened P16 — combined phone-landscape footprint

### Confirmed source cause

The earlier automation-green P16 implementation correctly measured usable height, made collection/spell stickiness conditional, and reflowed Combat metrics under vertical pressure. Physical `preqa.10` evidence exposed one remaining shared-shell gap: `CharacterAdaptiveShellV4` still rendered the entire identity/save header as one full-width persistent row **above** the full-width top-tab strip in phone landscape.

That meant two of the largest persistent shell layers remained vertically stacked even in shallow/wide phone viewports, contradicting P16's accepted combined-footprint and use-width-to-save-height rules.

The Combat `Cantidad` field also retained the standard `OutlinedTextField` internal vertical geometry, leaving visibly disproportionate top/bottom whitespace in the permanent HUD despite the earlier gross row-proportion repair.

### Product repair

Commit:

`fcf62103e4d4f1a4167d31efc05d13964c873d6d` — `repair: compact phone landscape shell and Combat HUD`

Net diff from pre-repair live HEAD `4029c415232ed9941a238c7bbee0b9c7d0678856` to this product boundary changes exactly:

- `CharacterAdaptiveShellV4.kt`
- `CharacterCombatOperationalV4.kt`

No temporary patch workflow remains in the resulting tree.

Repair behavior:

- in `PHONE_LANDSCAPE` with top-tab navigation and `REDUCED`/`CONSTRAINED` vertical space, the persistent identity/save header and the scrollable top-tab strip now share **one horizontal row** rather than consuming two vertical rows;
- phone navigation semantics remain top tabs; the repair does **not** switch phone landscape to tablet side-rail navigation;
- portrait and tablet shell behavior remain unchanged;
- the Combat HUD reduces only nonessential outer vertical padding/row spacing under vertical pressure;
- `Daño` / `Curar` retain their normal safe compact action height;
- `Cantidad` now uses an explicitly compact numeric input surface with controlled internal padding rather than relying on the taller default `OutlinedTextField` label/text geometry;
- damage/healing semantics, canonical HP state and the previously passed R1–R3 feedback behavior are unchanged.

### Validation state

The guarded patch process proved every intended source fragment matched exactly once and the final net diff contains only the two intended product files. The normal Scaffold run for `fcf62103…` had not yet appeared in the Actions API at this checkpoint revision, so P16 is **implemented but not yet automation-qualified or physically reaccepted**.

## Reopened P4 — next repair boundary

Source inspection already identified two concrete causes in `CharacterCombatSuccessorV4.kt`:

1. dice quantity/modifier and related numeric `OutlinedTextField`s are forced to exactly `48.dp` (`min = 48.dp, max = 48.dp`), which is smaller than the Material field's normal label/text geometry and explains the physical clipping;
2. dice editing reparses the entire expression with a strict complete-expression regex on every recomposition. Valid temporary editing states such as `1d`, `d8`, or clearing sides for `Otro…` fail that parser and collapse the structured draft to blank fields, explaining unreliable selector/edit behavior.

The P4 repair will therefore follow the owner's requested order:

1. correct internal padding/control geometry rather than enlarging the editor first;
2. preserve partial structured dice drafts while editing so selectors/numeric controls remain stable;
3. keep strict validation for save-time validity;
4. only consider outer size growth if still required after internal geometry is corrected.

## Gate effect

- R1–R3 physical phone PASS: preserved.
- P16: source repair implemented at `fcf62103…`; automation + new physical check pending.
- P4: source causes isolated; repair pending next.
- `preqa.10 / 41000`: historical physical-evidence candidate; do not resume broad owner QA while the material P16/P4 repair is underway.
- P17 tablet QA: pending.
- Phase 4A: open.
- P18: does not exist.
- DM implementation: blocked until explicit Phase 4A owner closure.

## Exact next action

Repair P4 in `CharacterCombatSuccessorV4.kt`, update this checkpoint/live pointers again, then run focused + aggregate automation across the combined P16/P4 repair and issue the next monotonic QA candidate only after green validation.

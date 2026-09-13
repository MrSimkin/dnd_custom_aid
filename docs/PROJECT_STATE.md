# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current physical-evidence candidate:** `0.4.0-preqa.10 / 41000` at `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3` — R1–R3 PHYSICAL PASS; LATER PHONE QA REOPENED P16/P4  
**Current repair boundary:** P16 source repair implemented at `fcf62103e4d4f1a4167d31efc05d13964c873d6d`; P4 repair next; combined validation/new candidate pending  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## Branch authority and authorization

This branch remains the authoritative Player implementation/Phase 4A repair line. `main` is intentionally divergent and carries later global/Phase 5A/DM discovery records; it is not the latest Player runtime. `docs/BRANCH_STATUS.md` and `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md` control cross-branch interpretation.

`docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` authorizes P1–P17 repair/validation, QA packaging, and bounded repairs reopened by real QA evidence. Current P16/P4 repair remains inside that authorization. No P18 exists.

## Preserved physical PASS — R1–R3

Owner `preqa.10` report `1–7 OK` physically clears the earlier canonical-HP / combat-operation defects:

- General HP → Combate propagation without extra global `Guardar` merely to cross tabs;
- max-HP reduction clamp / no invalid `current > max`;
- Temp/PV/both targeted feedback;
- working exact `Establecer PV` correction;
- materially corrected three-part `Daño | cantidad | Curar` gross proportion.

Do not repeat these checks absent contradictory evidence.

## Later preqa.10 physical evidence — P16/P4 reopened

Controlling owner evidence: `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md`.

The owner supplied annotated portrait/landscape screenshots showing that the combined persistent shell still wastes too much vertical space in shallow phone landscape and that the Combat `Cantidad` field retains disproportionate internal vertical padding. The same physical session also exposed a functionally broken structured damage-dice editor: dice selection is unreliable, numeric controls are clipped, and the internal geometry remains too large at normal `100%` spacing.

These findings reopen P16 and P4 while preserving the R1–R3 PASS.

## Current repair progress

Live repair checkpoint: `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_P16_P4_REPAIR_PROGRESS.md`.

### R4 / P16 — SOURCE REPAIR IMPLEMENTED / AUTOMATION + PHYSICAL RECHECK PENDING

Exact product commit:

`fcf62103e4d4f1a4167d31efc05d13964c873d6d` — `repair: compact phone landscape shell and Combat HUD`

Confirmed pre-repair source gap: `CharacterAdaptiveShellV4` still stacked the full-width identity/save header above the full-width top-tab strip in phone landscape even under reduced/constrained vertical space. This violated P16's combined-footprint/use-width-to-save-height rule despite the earlier HUD/sticky-header repairs.

The repair:

- combines the persistent identity/save header and scrollable top tabs into one horizontal row only for `PHONE_LANDSCAPE` + top-tab navigation + `REDUCED`/`CONSTRAINED` vertical space;
- preserves phone top-tab semantics; there is no tablet side-rail switch;
- leaves portrait/tablet shell behavior unchanged;
- reduces nonessential Combat HUD vertical padding/row spacing under vertical pressure;
- replaces the permanent Combat `Cantidad` field's high-padding default `OutlinedTextField` body with a compact numeric input surface while retaining safe `Daño`/`Curar` action heights and existing semantics.

Net diff from live pre-repair HEAD `4029c415…` to `fcf62103…` contains exactly two product files:

- `CharacterAdaptiveShellV4.kt`
- `CharacterCombatOperationalV4.kt`

Temporary guarded patch machinery self-deleted and leaves no net repository file.

Normal Scaffold evidence for `fcf62103…` is still pending; do not call P16 automation-green or physically accepted yet.

### P4 — SOURCE CAUSES ISOLATED / REPAIR NEXT

`CharacterCombatSuccessorV4.kt` contains two concrete physical-failure causes:

1. dice quantity/modifier and related numeric Material `OutlinedTextField`s are forced to exactly `48.dp` (`min = 48.dp, max = 48.dp`), clipping Material label/text geometry;
2. the dice component is reparsed with a strict complete-expression regex after each change. Temporary valid editing states such as `1d`, `d8`, or clearing sides for `Otro…` fail the parser and collapse the structured draft to blanks, making selector/edit behavior unstable.

Repair order follows owner direction: correct internal padding/geometry first; preserve partial structured dice drafts while editing; keep strict save validation; only enlarge outer geometry if still genuinely required afterward.

## Current gate

Do not resume broad owner QA on `preqa.10` while this material P16/P4 repair is underway.

Next actions:

1. implement the bounded P4 repair in `CharacterCombatSuccessorV4.kt`;
2. update durable repair state immediately afterward;
3. run focused + aggregate validation for the combined P16/P4 repair;
4. issue the next monotonic QA identity/artifact only after green automation;
5. resume focused physical phone QA on the reopened P16/P4 boundaries;
6. continue remaining phone/tablet evidence only after those checks are meaningful.

P17 tablet QA remains pending. Phase 4A remains open. DM implementation remains blocked until explicit owner acceptance/closure.

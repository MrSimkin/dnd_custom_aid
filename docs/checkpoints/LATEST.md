# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current physical-evidence candidate:** `0.4.0-preqa.10 / 41000` at `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3` — R1–R3 PHYSICAL PASS; P16/P4 REOPENED BY LATER PHONE QA  
**Current repair boundary:** P16 source repair `fcf62103e4d4f1a4167d31efc05d13964c873d6d` implemented; P4 source repair next  
**Acceptance boundary:** combined P16/P4 repair → green validation → new monotonic candidate → focused phone recheck → remaining phone/tablet evidence  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_P16_P4_REPAIR_PROGRESS.md` — **current engineering repair progress and exact P16 product boundary**.
2. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md` — source physical evidence/screenshots interpretation and owner repair priorities.
3. `docs/PROJECT_STATE.md` — live branch authority and current gate.
4. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md` — exact preqa.10 candidate/run/artifact identity.
5. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P16_LANDSCAPE_VERTICAL_SPACE_CLOSED.md` — controlling P16 contract.
6. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — controlling P4/P5/P9 contracts.
7. `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` — durable repair authorization.

## Preserved PASS — do not repeat

Owner `preqa.10` report `1–7 OK` remains authoritative physical PASS for the repaired R1–R3 boundary: canonical General↔Combate HP behavior, max clamp, targeted HP feedback, exact `Establecer PV`, and the earlier gross three-part Combat operation-row proportion repair.

## New physical defects being repaired

- **P16:** combined phone-landscape persistent header + tabs + HUD footprint still consumed too much vertical space; `Cantidad` retained excessive internal vertical whitespace.
- **P4:** structured damage-dice editor was physically broken: selector/edit state unstable and numeric controls clipped/oversized.

Portrait long-card button relocation remains only a consideration, not an approved change.

## P16 source repair now implemented

Commit `fcf62103e4d4f1a4167d31efc05d13964c873d6d`:

- shallow/reduced phone landscape now places identity/save header + scrollable top tabs in **one horizontal persistent row**, using width to save height while preserving phone top-tab navigation;
- portrait/tablet shell behavior is unchanged;
- constrained Combat HUD outer vertical padding/spacing is tightened;
- `Cantidad` uses a compact explicitly padded numeric surface instead of the default high-padding Material text-field body;
- `Daño`/`Curar` retain safe action heights and existing semantics.

Net diff from pre-repair `4029c415…` contains only `CharacterAdaptiveShellV4.kt` and `CharacterCombatOperationalV4.kt`. Temporary patch machinery leaves no net file.

Automation and physical acceptance for this new P16 repair are still pending.

## P4 source causes already isolated

In `CharacterCombatSuccessorV4.kt`:

1. several numeric `OutlinedTextField`s are forcibly clamped to exactly `48.dp`, causing clipping;
2. the strict whole-expression dice parser discards valid partial editing state such as `1d` / `d8` / temporarily empty sides, which makes selector/edit interactions unreliable.

Owner-directed repair order remains: **fix internal padding/geometry first, preserve fully visible/selectable controls, then adjust outer size only if genuinely necessary.**

## Exact next action

Implement the bounded P4 repair, update continuity immediately, then run focused + aggregate validation across P16/P4. Only after green automation create the next monotonic QA build and resume owner testing from these reopened points.

P17 tablet QA remains pending. No P18 exists. Phase 4A remains open. DM implementation remains blocked until explicit owner closure.

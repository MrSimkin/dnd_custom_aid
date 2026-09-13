# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current physical-evidence candidate:** `0.4.0-preqa.10 / 41000` at `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3` — AUTOMATION GREEN; R1–R3 PHYSICAL PASS; LATER PHONE QA REOPENED P16/P4  
**Exact candidate run:** `34730531791` — SUCCESS  
**Acceptance boundary:** bounded P16 + P4 repair/revalidation → new monotonic candidate → focused owner phone recheck → remaining phone/tablet evidence  
**Release status:** debug/development; NOT owner-accepted and NOT release-ready

## Resume here

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md` — **current physical owner evidence**, including the new annotated portrait/landscape spacing findings and broken damage-dice editor.
2. `docs/PROJECT_STATE.md` — current Player authority, reopened boundaries and repair gate.
3. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md` — exact preqa.10 candidate/run/artifact identity.
4. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P16_LANDSCAPE_VERTICAL_SPACE_CLOSED.md` — controlling P16 full-app usable-height/combined-footprint contract.
5. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — controlling P4/P5/P9 and other accepted repair contracts.
6. `docs/checkpoints/2026-09-12_PHASE4A_PREQA9_OWNER_QA_PROGRESS.md` — historical preqa.9 failures and R1–R3 repair chain.
7. `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` — durable authorization.
8. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P17_TABLET_QA_GATE_CLOSED.md` — tablet gate policy.

## Preserved preqa.10 PASS — do not repeat

Owner report **`1–7 OK`** physically clears the specific R1–R3 defects that invalidated preqa.9:

- General HP → Combate canonical propagation without extra global `Guardar` merely to cross tabs;
- max-HP reduction clamp / no invalid `current > max`;
- targeted Temp/PV/both feedback behavior;
- working Combate `Establecer PV` exact correction;
- materially corrected `Daño | cantidad | Curar` three-part operation-row proportion.

These checks remain PASS unless later evidence directly contradicts them.

## New physical findings — current blocker

### P16 / combined persistent footprint — PHYSICAL FAIL / REOPENED

The owner reports phone portrait/landscape is broadly usable in general terms, but annotated screenshots demonstrate remaining excessive/disproportionate vertical consumption:

- `Cantidad` retains excessive internal top/bottom vertical padding;
- phone-landscape top character shell (identity/back; settings/`Guardar`) occupies substantial height rather than exploiting width/compaction sufficiently;
- combined header + tabs + Combat HUD leave too little primary attacks/actions area;
- this matches P16's full-app combined-footprint audit boundary, not merely a one-control cosmetic issue.

P5's compact-HUD principle remains implicated. Owner priority is to reduce unnecessary vertical margin/padding and use available width before making controls larger.

Portrait relocation of buttons on long cards is recorded only as a **consideration**, not an approved change.

### P4 structured attack damage editor — PHYSICAL FAIL / REOPENED

Accidental physical entry into a weapon damage editor exposed a material defect:

- dice selection does not work correctly/reliably;
- numeric values/controls are clipped;
- internal elements do not fit vertically;
- the control/editor remains too large at normal `100%` spacing.

This reopens P4 functionally. Shared editor/spacing primitives (including P9) may also be involved; exact secondary classification requires source inspection.

Owner-directed repair order: **fix internal vertical margin/padding first, make controls fully visible/selectable, then only if necessary adjust outer box size modestly. Do not fix by simply making the box bigger.**

## Exact next action

Pause broad physical QA under the same efficiency rule used after preqa.9. Inspect and repair the reopened accepted boundaries:

1. shared character top/header shell + Combat HUD under P16 usable-height/combined-footprint policy;
2. Combat amount-field/shared vertical spacing at 100%;
3. attack structured-damage editor under P4 plus relevant shared P9 sizing/spacing primitives;
4. focused regression coverage where feasible;
5. aggregate validation;
6. next monotonic QA identity/artifact after material product changes;
7. focused owner retest of these reopened points before resuming broader phone/tablet QA.

P17 tablet QA remains pending. No P18 exists. Phase 4A remains open. DM implementation remains blocked until explicit owner closure.

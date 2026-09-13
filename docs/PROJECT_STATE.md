# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Previous physical candidate:** `0.4.0-preqa.9 / 40900` at `cd0c203d337c062fa388010d300e875f2f54ced7` — FAILED SHARED HP/UX ACCEPTANCE BOUNDARY  
**Current physical-evidence candidate:** `0.4.0-preqa.10 / 41000` at `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3` — AUTOMATION GREEN; R1–R3 PHYSICAL PASS; P16/P4 REOPENED BY LATER PHONE QA  
**Current phase:** bounded P16 vertical-space/persistent-footprint + P4 attack-damage-editor repair from physical owner evidence  
**Release status:** development/debug; NOT owner-accepted and NOT release-ready

## Branch authority and authorization

This branch is the authoritative Player implementation/Phase 4A repair line. `main` is intentionally divergent and carries later global/Phase 5A/DM discovery records; it is not the latest Player runtime. See `docs/BRANCH_STATUS.md` and `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md`.

`docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md` authorizes P1–P17 repair/validation, QA packaging, and bounded repairs reopened by real QA evidence. Current work remains inside that authorization. No P18 exists.

## Current candidate identity

Authoritative candidate checkpoint: `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md`.

- versionName `0.4.0-preqa.10`;
- versionCode `41000`;
- exact candidate commit `a0d7dbd8f0069c87690c8fa54da780da8ffd15e3`;
- exact candidate Scaffold `34730531791` — **SUCCESS**;
- artifact `10308364110`, `dnd-custom-aid-debug-apk`;
- GitHub artifact ZIP digest `sha256:ef44559ba61267837a286e996d74d5c00b55d89b003d7f6e69bf0902cff5dd32`, independently matched after download;
- APK size `38,865,008` bytes;
- independent APK SHA-256 `ee5db263883c8b1979b12cc9ca365ba9f190009b2b46e4e9b53681408c128c78`.

## Preserved physical PASS — R1–R3

The owner reported **`1–7 OK`** for the exact focused repaired boundary on `preqa.10`.

Physical PASS therefore covers:

- General HP → Combate canonical propagation without an extra global `Guardar` merely to cross tabs;
- max-HP reduction clamp with no invalid `current > max` projection;
- Temp-only, PV-only and spillover affected-state feedback behavior accepted in P2;
- working Combate `Establecer PV` exact correction and canonical projection;
- materially corrected `Daño | cantidad | Curar` three-part operation-row proportion relative to the failed preqa.9 candidate.

These R1–R3 passes remain valid and should not be repeated absent contradictory evidence.

## New physical preqa.10 evidence — P16 / P4 reopened

Current controlling physical progress checkpoint: `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md`.

### P16 / persistent vertical-space boundary — REOPENED

Owner phone portrait/landscape testing is broadly usable in general terms, but annotated physical screenshots show that the accepted combined-footprint/usable-height policy is not fully satisfied:

- the Combat `Cantidad` field retains disproportionate top/bottom internal vertical padding;
- in phone landscape, the top character shell (identity/back on the left; settings/`Guardar` on the right) retains substantial vertical occupation instead of exploiting shallow/wide geometry sufficiently;
- the combined app/header shell + tab navigation + Combat HUD leaves too little primary attacks/actions area relative to the available screen;
- the defect is about the **combined persistent footprint**, not merely one isolated margin.

This reopens P16's full-app vertical-space audit and keeps P5's compact fixed-HUD principle relevant. Historical automation-green P16 evidence remains valid for its tested scope; physical acceptance is not valid for the observed phone boundary.

Owner guidance: reduce unnecessary vertical margins/padding and exploit available width before enlarging surrounding controls. Portrait long-card action-button relocation is a consideration to evaluate, not an approved automatic change.

### P4 attack structured-damage editor — REOPENED

During the same physical session, the owner accidentally opened a weapon/attack damage editor and found a functional defect:

- dice cannot be selected correctly/reliably;
- numeric controls/values are clipped;
- internal controls do not fit the available vertical area correctly;
- the editor/control remains too large at normal `100%` spacing.

This physically reopens P4 because the accepted direct structured dice-selector workflow is not usable as observed. The clipping/oversizing also implicates shared editor/spacing behavior (P9 and/or common spacing primitives); exact secondary root-cause mapping must be established from source inspection rather than guessed.

Explicit owner repair priority: **fix/reduce internal vertical margin/padding first; ensure controls fit and remain selectable; only then consider a modest outer-box size change if still necessary. Do not solve the defect simply by making the box larger.**

## Current gate

Under the established QA-efficiency rule, broad owner testing should pause while the known material defects are repaired rather than collecting exhaustive evidence from a candidate that will be superseded.

Next engineering work is bounded to:

1. inspect shared character top/header shell + Combat HUD against P16's combined persistent-footprint/usable-height contract;
2. inspect the Combat amount field and related shared spacing primitives for disproportionate vertical padding at 100%;
3. inspect/reproduce the attack structured-damage editor defect against P4 and shared P9 sizing/spacing primitives;
4. repair only the reopened accepted boundaries;
5. add focused regression coverage where feasible, run aggregate validation, and issue a new monotonic QA identity after material product changes;
6. resume owner physical QA from these reopened points.

P17 tablet QA remains pending; no tablet PASS/FAIL is claimed. Phase 4A remains open. DM implementation remains blocked until explicit owner acceptance/closure.

## Exact continuation point

Read first:

1. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_OWNER_PHONE_QA_PROGRESS.md`;
2. `docs/checkpoints/2026-09-12_PHASE4A_PREQA10_QA_CANDIDATE.md`;
3. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P16_LANDSCAPE_VERTICAL_SPACE_CLOSED.md`;
4. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` for P4/P5/P9 contracts;
5. `docs/checkpoints/LATEST.md`.

Do not restart R1–R3, do not continue broad QA before addressing the recorded defects, and do not invent P18 or unrelated Player work.

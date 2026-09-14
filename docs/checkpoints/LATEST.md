# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — PHONE + TABLET DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation gate:** consolidated post-P17 repair IN PROGRESS; **Rounds 1–3 + T5 + T6 + T3/T4/T9 COMPLETE / AUTOMATION GREEN**; next = **T7 wide Combat adaptive composition**  
**Release status:** debug/development; Phase 4A OPEN; DM implementation blocked pending explicit owner closure

## Resume here

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_APPLICATION_SETTINGS_T3_T4_T9.md` — **latest completed repair; adaptive layout/text-size/haptics semantics + clean automation evidence**.
2. `docs/PROJECT_STATE.md` — live Player authority/current implementation state and next repair family.
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T6_CLASS_EDITOR_CONTROLS.md`.
4. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T5_SPELL_SOURCE_BOOTSTRAP.md`.
5. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND3_COMPACT_CHECKBOX_RESPONSIVE_GROUPING.md`.
6. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md`.
7. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md`.
8. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — remaining repair contracts + targeted revalidation matrix.
9. `docs/TESTING.md` — synchronized testing policy and route.

## Completed repair status

- **Round 1 structured dice:** HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; Scaffold `34787688776` / #1508 SUCCESS. T2 remains partial.
- **Round 2 T1 reorder stability:** HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; Scaffold `34788409987` / #1519 SUCCESS. One-column + spatial stability implemented; targeted physical revalidation pending.
- **Round 3 checkbox/responsive grouping:** HEAD `1d1c476ddaeb045c8a1b186267f452010cad7681`; Scaffold `34793253151` / #1537 SUCCESS. Nineteen raw checkboxes migrated and responsive packing guarded; targeted physical revalidation pending.
- **T5 spell-source bootstrap:** core `2e7fda2852425594971f7df47b433d642eb2119a`; steady-state `3774c53f5189ebd535cc1b73ec18493e268e5d9f`; Scaffold `34794589758` / #1560 SUCCESS. Canonical base casters reconcile to source/profile overlays without destructive ID/association migration; targeted physical revalidation pending.
- **T6 class-editor controls:** product `c98121e50f347f64898c9e31d077e53cad31685f`; steady-state `6ba73b22b07a4c5a92d69425a7372d2695fbc30a`; Scaffold `34795355116` / #1571 SUCCESS. Numeric keypad + standard/custom hit-die selector implemented; targeted physical revalidation pending.
- **T3/T4/T9 Application Settings:** product `1dcd320417e7e3b45ec02ec6aa7cbb616c84e473`; steady-state `5db7bc3a48f1e640fc80770dd07d68a7ffaa02f7`; Scaffold `34796452617` / #1583 SUCCESS. Vertical/Horizontal adaptive density replaces exact-count UI/runtime, text scale is symmetric 50–150 with nearest migration, and `Ninguna` short-circuits real haptic dispatch. Artifact `10329772684`, digest `sha256:74249a197c21572743165927c9330f38ceafd2db7f78267872f36e2edfd2f1af`. Targeted physical revalidation pending.

None of these automation-green repairs are being declared physical PASS.

## Next bounded family — T7 wide Combat adaptive composition

Use only the active `CharacterCombatSuccessorV4.kt` path. Preserve narrow-phone behavior. On wide/tablet layouts:

- bound the operational HUD rather than stretching the phone composition across the display;
- lay out attacks/actions in an adaptive multi-column composition driven by the repaired T3 density preference;
- use Round 2's stabilized spatial reorder engine for multi-column drag/reorder;
- keep favorite/edit/delete behavior and final persisted order unchanged;
- keep death saves, section header and empty state semantically full-width;
- add focused source/behavior guard(s), pass normal Scaffold, then synchronize all four continuity surfaces.

## Remaining after T7

1. T8 Table Mode structural-affordance enforcement.
2. Remaining T2 die-result silhouettes, Custom Throw die/custom sides/signed modifier and Dice-tab display-mode ownership.
3. Optional phone-16 compact-density refinement only if safe/materially beneficial.

## Frozen physical evidence

The frozen candidate remains `0.4.0-preqa.12 / 41200`, commit `abfc7e4a1519a27117f194721a425d75cb5df68a`, Scaffold `34776627282` SUCCESS, artifact `10323602038`, ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`, APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

Do not replay complete phone/tablet discovery. After consolidated repair completion, freeze one new monotonic candidate and perform targeted revalidation only for failed/touched/affected families plus unresolved phone 21/affected phone 22 and tablet 18.

No P18 exists. DM implementation remains blocked until explicit Phase 4A owner closure.

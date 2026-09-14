# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-14  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/physical-revalidation line  
**Current exact frozen physical candidate:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4` — **AUTOMATION GREEN / TARGETED CROSS-DEVICE PHYSICAL REVALIDATION PENDING**  
**Current implementation gate:** consolidated material post-P17 repair **COMPLETE / AUTOMATION GREEN**; exact candidate Scaffold #1630 SUCCESS; next = **targeted owner/device revalidation on preqa.13**  
**Release status:** debug/development; Phase 4A OPEN; DM implementation blocked pending explicit owner closure

## Resume here

1. `docs/checkpoints/2026-09-14_PHASE4A_PREQA13_CONSOLIDATED_REPAIR_CANDIDATE.md` — **current frozen candidate, exact artifact evidence and targeted test matrix**.
2. `docs/PROJECT_STATE.md` — live Player authority and current manual gate.
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T2_DICE_PRESENTATION_CUSTOM_THROW.md` — latest completed bounded material repair + aggregate automation proof.
4. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T8_TABLE_MODE.md`.
5. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T7_WIDE_COMBAT.md`.
6. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_APPLICATION_SETTINGS_T3_T4_T9.md`.
7. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T6_CLASS_EDITOR_CONTROLS.md`.
8. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T5_SPELL_SOURCE_BOOTSTRAP.md`.
9. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND3_COMPACT_CHECKBOX_RESPONSIVE_GROUPING.md`.
10. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md`.
11. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md`.
12. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — original repair/revalidation contract.
13. `docs/TESTING.md` — synchronized test policy and current targeted route.

## Completed repair status

- **Round 1 structured dice:** HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; Scaffold `34787688776` / #1508 SUCCESS. Shared `NdS±M` parsing/rolling and direct sign foundation implemented; targeted revalidation pending within the dice family.
- **Round 2 T1 reorder stability:** HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; Scaffold `34788409987` / #1519 SUCCESS. One-column + spatial stability implemented; targeted revalidation pending.
- **Round 3 checkbox/responsive grouping:** HEAD `1d1c476ddaeb045c8a1b186267f452010cad7681`; Scaffold `34793253151` / #1537 SUCCESS. Nineteen raw checkboxes migrated and responsive packing guarded; targeted revalidation pending.
- **T5 spell-source bootstrap:** core `2e7fda2852425594971f7df47b433d642eb2119a`; steady-state `3774c53f5189ebd535cc1b73ec18493e268e5d9f`; Scaffold `34794589758` / #1560 SUCCESS. Canonical caster sources reconcile without destructive ID/association migration; tablet 18 is now testable.
- **T6 class-editor controls:** product `c98121e50f347f64898c9e31d077e53cad31685f`; steady-state `6ba73b22b07a4c5a92d69425a7372d2695fbc30a`; Scaffold `34795355116` / #1571 SUCCESS. Numeric keypad + standard/custom hit-die selector implemented.
- **T3/T4/T9 Application Settings:** product `1dcd320417e7e3b45ec02ec6aa7cbb616c84e473`; steady-state `5db7bc3a48f1e640fc80770dd07d68a7ffaa02f7`; Scaffold `34796452617` / #1583 SUCCESS. Adaptive Vertical/Horizontal density, symmetric 50–150 text scale and haptics `Ninguna` implemented.
- **T7 wide Combat:** product `5f00bc006a26600a5d03582fbfbdf2e266259673`; steady-state `e567750a529238a2b45722b6f5ed726dd9123d88`; Scaffold `34797403737` / #1592 SUCCESS. Narrow path preserved; wide/tablet bounded HUD + adaptive grid + stabilized spatial reorder implemented.
- **T8 Table Mode:** product `2734d08a72e183ca213cd9213ff47a4db588cbf6`; steady-state `d6c13819a49e1cb7c71dffcad98b53c250d4d9d4`; Scaffold `34799667822` / #1611 SUCCESS. Structural affordances are gated/read-only while operational HP/inspiration/current-resource state remains live.
- **T2 dice presentation / Custom Throw / display mode:** product `8131dd13f4373148503b854b1448f9d081556847`; steady-state `4d09e9eca648e5ca82af896dab8d16b986faae5b`; Scaffold `34801201294` / #1625 SUCCESS. Die-specific visuals, standard/custom-side Custom Throw, signed modifier UX, shared arbitrary-die resolver and Dice-tab display-mode ownership implemented.

None of these repairs are physically PASS merely because automation is green.

## Consolidated repaired candidate — preqa.13

The optional phone-16 compact-density refinement was **not taken** because check 16 was already PASS and no material benefit justified pre-candidate churn.

The consolidated repaired line was advanced monotonically from `preqa.12 / 41200` to:

- versionName `0.4.0-preqa.13`;
- versionCode `41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- candidate Scaffold `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- archive size `13,693,984` bytes;
- GitHub + independent ZIP SHA-256 `13b72f8a7e797e40009f548bd45d1c538a41cfe52ecf9796110e75498e359893`;
- APK size `39,094,384` bytes;
- independent APK SHA-256 `81fbc20525221d791e6a0f786dec4b05709505e052e0e5ffba4ebfa5eb02391e`.

This exact candidate is **FROZEN / AUTOMATION GREEN / PHYSICAL REVALIDATION PENDING**.

## Current gate — targeted physical revalidation

Do **not** replay complete phone/tablet discovery. Test only repaired/touched/affected families and unresolved evidence on the exact `preqa.13 / 41300` APK.

Targeted scope:

1. Round 1/T2 dice family — phone 7–9 + d20/non-d20/custom-side/signed-modifier/result-visual/display-mode persistence;
2. T1 reorder — one-column + spatial/multi-column + applicable auto-scroll + persisted order;
3. Round 3 checkbox/responsive grouping — representative migrated checkboxes + Conjuros packing/source-prepared grouping across relevant orientations;
4. T5 spell sources — canonical source/bootstrap, associations, save/reopen, manual-source coexistence + tablet 18;
5. T6 class controls — numeric keyboards, standard/custom hit die, persistence;
6. T3/T4/T9 settings — Vertical/Horizontal density, text-size semantics/migration, haptics None/non-none;
7. T7 — tablet-landscape Combat width/packing/reorder/persistence + narrow-phone sanity;
8. T8 — structural controls non-editable/non-opening while HP/inspiration/resources remain operational/persistent + narrow-phone sanity;
9. phone 21;
10. affected phone 22 slice;
11. tablet 18.

Preserve all unrelated accepted PASS evidence from `preqa.12`.

## Failure / closure rule

If targeted physical evidence finds a material defect, reopen only the relevant bounded repair family after source audit, rerun focused + aggregate automation, and advance to another monotonic candidate identity if the frozen candidate changes materially.

Phase 4A remains OPEN until sufficient targeted evidence exists and the owner explicitly accepts/closes it. DM implementation remains blocked until that explicit closure. No P18 exists.
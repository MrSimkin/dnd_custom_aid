# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — PHONE + TABLET DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation gate:** consolidated post-P17 repair IN PROGRESS; **Rounds 1–3 + T5 + T6 COMPLETE / AUTOMATION GREEN**; next = **Application Settings T3/T4/T9**  
**Release status:** debug/development; Phase 4A OPEN; DM implementation blocked pending explicit owner closure

## Resume here

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T6_CLASS_EDITOR_CONTROLS.md` — **latest completed repair; numeric keyboard + standard/custom hit-die selector + exact steady-state automation evidence**.
2. `docs/PROJECT_STATE.md` — live Player authority/current implementation state and next repair family.
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T5_SPELL_SOURCE_BOOTSTRAP.md` — completed spell-source/bootstrap/source-context compatibility repair.
4. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND3_COMPACT_CHECKBOX_RESPONSIVE_GROUPING.md` — completed checkbox/responsive repair.
5. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md` — completed T1 reorder repair.
6. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md` — completed structured-dice repair.
7. `docs/checkpoints/2026-09-13_PHASE4A_OWNER_REPAIR_DECISIONS_IMPLEMENTATION_GO.md` — approved T3/T4/T9 semantics + implementation authorization.
8. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — controlling source audit, remaining family contracts and targeted revalidation matrix.
9. `docs/TESTING.md` — synchronized testing policy and route.

## Completed repair status

- **Round 1 structured dice:** product/test HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; Scaffold `34787688776` / #1508 SUCCESS. Phone 7–9 implementation repaired; T2 remains partial.
- **Round 2 T1 reorder stability:** product/test HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; Scaffold `34788409987` / #1519 SUCCESS. One-column + spatial target stability implemented; targeted physical revalidation pending.
- **Round 3 checkbox/responsive grouping:** final HEAD `1d1c476ddaeb045c8a1b186267f452010cad7681`; Scaffold `34793253151` / #1537 SUCCESS. Nineteen raw checkboxes migrated; responsive packing guarded; targeted physical revalidation pending.
- **T5 spell-source bootstrap:** core integration `2e7fda2852425594971f7df47b433d642eb2119a`; steady-state HEAD `3774c53f5189ebd535cc1b73ec18493e268e5d9f`; Scaffold `34794589758` / #1560 SUCCESS. Canonical base casters reconcile to existing source/profile overlays without destructive ID/association migration; targeted physical revalidation pending.
- **T6 class-editor controls:** product `c98121e50f347f64898c9e31d077e53cad31685f`; steady-state HEAD `6ba73b22b07a4c5a92d69425a7372d2695fbc30a`; Scaffold `34795355116` / #1571 SUCCESS. Numeric keypad behavior plus `d4/d6/d8/d10/d12/d20/ Otro…` selector implemented with custom-value/catalog-preselection preservation. Artifact `10329671667`, digest `sha256:6391210567ff4964b7077e1cc6e9c3c38bd862b62aba19a01e929ef6bfe4db4d`. Targeted physical revalidation pending.

None of these automation-green repairs are being declared physical PASS.

## Next bounded family — Application Settings T3/T4/T9

Implement the already-approved owner contract:

- **T3:** two adaptive card-distribution preferences, **Portrait** and **Landscape**, with levels equivalent to `Comfortable / Balanced / Compact / Dense`; do not promise exact columns. Runtime derives safe effective columns from actual available width, orientation preference, text/UI-density pressure and minimum usable card width. Map legacy four exact-count preferences to the closest adaptive intent without unnecessary reset.
- **T4:** normal text-size scale symmetric around 100: preferred `50,60,70,80,90,100,110,120,130,140,150`; map older persisted values to nearest valid option. Do not redesign spacing density as part of T4.
- **T9:** explicit haptic `None`; `None` disables app-generated haptics. Existing users with a prior non-none setting must remain non-none after upgrade.

Add focused compatibility/semantics guard(s), run the normal read-only Scaffold, then synchronize the four continuity surfaces again.

## Remaining after settings

1. T7 wide Combat adaptive composition.
2. T8 Table Mode structural-affordance enforcement.
3. Remaining T2 die-result silhouettes, Custom Throw die/custom sides/signed modifier, Dice-tab display-mode ownership.
4. Optional phone-16 compact-density refinement only if safe/materially beneficial.

## Frozen physical evidence

The frozen candidate remains `0.4.0-preqa.12 / 41200`, commit `abfc7e4a1519a27117f194721a425d75cb5df68a`, Scaffold `34776627282` SUCCESS, artifact `10323602038`, ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`, APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

Do not replay complete phone/tablet discovery. After the consolidated repair is materially complete, freeze one new monotonic candidate and perform targeted revalidation only for failed/touched/affected families plus unresolved phone 21/affected phone 22 and tablet 18.

No P18 exists. DM implementation remains blocked until explicit Phase 4A owner closure.

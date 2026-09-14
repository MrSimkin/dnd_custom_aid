# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — PHONE + TABLET DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation gate:** consolidated post-P17 repair IN PROGRESS; **Rounds 1–2 COMPLETE / AUTOMATION GREEN**; next = **Round 3 shared compact checkbox + responsive grouping**  
**Release status:** debug/development; Phase 4A OPEN; DM implementation blocked pending explicit owner closure

## Resume here

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md` — **latest completed implementation round; T1 repair + exact green automation evidence**.
2. `docs/PROJECT_STATE.md` — live Player authority/current implementation state and next round.
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md` — completed structured-dice/signed-modifier round.
4. `docs/checkpoints/2026-09-13_PHASE4A_OWNER_REPAIR_DECISIONS_IMPLEMENTATION_GO.md` — owner decisions + explicit implementation authorization.
5. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — controlling source audit + repair-family contracts + targeted revalidation matrix.
6. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_P17_TABLET_QA_PROGRESS.md` — complete P17 tablet discovery evidence.
7. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` — controlling detailed phone findings.
8. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — exact preqa.12 candidate/run/artifact/digest evidence.
9. `docs/TESTING.md` — synchronized current testing position, physical-QA/failure-handling policy and targeted revalidation route.

## Completed repair rounds

### Round 1 — structured dice / signed modifier foundation: COMPLETE / GREEN

Product/test HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; authoritative Scaffold `34787688776` / run `1508` — **SUCCESS**.

Phone 7–9 implementation basis is repaired and protected by tests/guard, but physical revalidation waits for the consolidated new candidate. T2 remains partial because die-result silhouettes, Custom Throw die/modifier UX and Dice-tab display-mode ownership remain.

### Round 2 — T1 reorder target stability: COMPLETE / GREEN

Product/test HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; authoritative Scaffold `34788409987` / run `1519` — **SUCCESS**.

Both active reorder engines now target against stable drag-start geometry and canonical order rather than animated preview bounds, use hysteresis around slot boundaries, explicitly translate stable targets during actual viewport scrolling, and are protected by focused common tests plus a durable CI guard.

T1 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. Reuse the existing owner video as the failure baseline; do not ask for it again. Physical closure of T1 requires targeted evidence for **both one-column and multi-column/spatial reorder**, because the failure family was physically observed across layout scenarios and the repair covers both active reorder models. Also revalidate no target-chasing preview reflow, applicable drag auto-scroll and final order persistence after leave/reopen.

## Next round — shared compact checkbox + responsive grouping

This round addresses phone 17.1–17.3 and the tablet reproduction.

Required approach:

- audit all Android Player `Checkbox`, `TriStateCheckbox`, `Switch` and associated row/wrap/container sites;
- classify legitimate exceptions rather than blindly restyling every toggle;
- establish a shared compact/touch-safe checkbox primitive if the systemic audit confirms it;
- normalize control size, label typography, internal spacing and outer spacing;
- repair responsive grouping so portrait items share a row when they fit and wider/landscape layouts exploit width instead of preserving unnecessary two-row groups;
- specifically cover Equipment `Equipado` / `Equipo especial` and spell-editor V/S/M, Concentración/Ritual, source/prepared groups;
- add focused tests/guards and run normal Scaffold;
- update **all four** durable continuity surfaces before proceeding: the dedicated round checkpoint, `docs/PROJECT_STATE.md`, this file, and `docs/TESTING.md` current testing status/route.

## Owner decisions controlling later rounds

- **T3:** separate Portrait/Landscape adaptive card-distribution preferences (`Comfortable / Balanced / Compact / Dense`; `Balanced` normal default). Runtime computes safe effective columns from width, orientation preference, text/UI density and minimum card width. Legacy exact-count prefs map compatibly.
- **T9:** explicit haptics `None`; existing users retain their prior non-none choice across upgrade.
- **T4:** main text-size scale should be symmetric around 100; preferred normal range `50,60,70,80,90,100,110,120,130,140,150` with compatibility-safe mapping.

## Physical evidence preserved

Phone exact preqa.12: 1–6 PASS; 7–8 OPEN/implemented pending revalidation; 9 functional PASS + implemented direct-toggle refinement pending revalidation; 10–16 PASS; 17.1–17.3 OPEN; 18–20 PASS; 21 UNASSESSED; 22 PARTIAL/AMBIGUOUS; 23 PASS.

Tablet exact preqa.12: 1–6 PASS; 7 FAIL/T7; 8 PASS; 9–10 FAIL/T5; 11 PASS + checkbox family; 12–14 PASS; 15 FAIL/T8; 16–17 PASS; 18 BLOCKED BY T5.

Do not replay unrelated accepted evidence.

## Frozen candidate evidence unchanged until a new consolidated build is versioned

- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact ID `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

## Exact route

1. Round 3 shared compact checkbox + responsive grouping audit/repair + focused automation + synchronized status update.
2. Continue remaining dependency-aware repair rounds, updating the dedicated round checkpoint + `PROJECT_STATE.md` + `LATEST.md` + `TESTING.md` after each bounded round.
3. Run aggregate Scaffold over the completed consolidated repair.
4. Freeze a new monotonic physical-QA candidate.
5. Perform targeted cross-device revalidation only for failed/touched/affected families plus phone 21/affected phone 22 and tablet 18 after T5.
6. Explicit owner Phase 4A closure only after repaired evidence is sufficient.

No P18 exists. DM implementation remains blocked until explicit Phase 4A closure.

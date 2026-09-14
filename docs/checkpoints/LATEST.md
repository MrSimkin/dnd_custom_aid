# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — PHONE + TABLET DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation gate:** consolidated post-P17 repair IN PROGRESS; **Rounds 1–3 COMPLETE / AUTOMATION GREEN**; next = **T5 spell-source/bootstrap/source-context compatibility**  
**Release status:** debug/development; Phase 4A OPEN; DM implementation blocked pending explicit owner closure

## Resume here

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND3_COMPACT_CHECKBOX_RESPONSIVE_GROUPING.md` — **latest completed implementation round; systemic checkbox/responsive repair + exact green steady-state automation evidence**.
2. `docs/PROJECT_STATE.md` — live Player authority/current implementation state and next repair family.
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md` — completed T1 repair + exact green automation evidence.
4. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md` — completed structured-dice/signed-modifier round.
5. `docs/checkpoints/2026-09-13_PHASE4A_OWNER_REPAIR_DECISIONS_IMPLEMENTATION_GO.md` — owner decisions + explicit implementation authorization.
6. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — controlling source audit + repair-family contracts + targeted revalidation matrix.
7. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_P17_TABLET_QA_PROGRESS.md` — complete P17 tablet discovery evidence.
8. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` — controlling detailed phone findings.
9. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — exact preqa.12 candidate/run/artifact/digest evidence.
10. `docs/TESTING.md` — synchronized current testing position, physical-QA/failure-handling policy and targeted revalidation route.

## Completed repair rounds

### Round 1 — structured dice / signed modifier foundation: COMPLETE / GREEN

Product/test HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; authoritative Scaffold `34787688776` / run `1508` — **SUCCESS**.

Phone 7–9 implementation basis is repaired and protected by tests/guard, but physical revalidation waits for the consolidated new candidate. T2 remains partial because die-result silhouettes, Custom Throw die/modifier UX and Dice-tab display-mode ownership remain.

### Round 2 — T1 reorder target stability: COMPLETE / GREEN

Product/test HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; authoritative Scaffold `34788409987` / run `1519` — **SUCCESS**.

Both active reorder engines now target against stable drag-start geometry and canonical order rather than animated preview bounds, use hysteresis around slot boundaries, explicitly translate stable targets during actual viewport scrolling, and are protected by focused common tests plus a durable CI guard.

T1 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. Reuse the existing owner video as the failure baseline; do not ask for it again. Physical closure of T1 requires targeted evidence for **both one-column and multi-column/spatial reorder**, because the failure family was physically observed across layout scenarios and the repair covers both active reorder models. Also revalidate no target-chasing preview reflow, applicable drag auto-scroll and final order persistence after leave/reopen.

### Round 3 — compact checkbox + responsive grouping: COMPLETE / GREEN

Product/test HEAD `8455d8015e0bc6f7b4a6f813e56b03c5f9a2915c`; authoritative steady-state Scaffold `34793215805` / run `1536` — **SUCCESS**.

The repair establishes shared compact/touch-safe Player checkbox primitives and responsive grouping, migrates the source-complete baseline of **19 raw Material Checkbox calls across seven Player files**, keeps source/prepared controls semantically paired, and makes Equipment and Conjuros groups wrap only when width requires it. `Switch` controls remain unchanged because the audit did not establish them as defective.

The permanent source guard now requires zero raw Material Checkbox sites outside the shared primitive plus the expected Equipment/Conjuros responsive contract. The temporary CI migration writer and helper were retired after the verified source conversion, so normal Scaffold is read-only again.

Phone 17.1–17.3 and the tablet checkbox-family reproduction are **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. They are not physically PASS yet.

## Next repair family — T5 spell source/bootstrap/source context

The next bounded round must repair the cross-device T5 source-model mismatch without turning Conjuros into a broad spell-legality engine.

Required contract:

- canonical character origins/classes drive spell-source availability analogously to Rasgos provenance;
- retain the existing spellcasting source/profile layer for source-specific ability / save DC / attack modifier configuration;
- preserve compatible existing source IDs, spell associations and saved characters rather than destructively recreating records;
- preserve persistence/import/export and manual/homebrew `Other` source behavior;
- cover new and existing characters, especially a canonical Mago/Mage that should expose a usable source without manual bootstrap;
- after repair, targeted Conjuros revalidation must include add/save/reopen plus tablet portrait/landscape sticky/source-context behavior that was blocked in P17 check 18;
- add focused tests/guards and run normal Scaffold;
- update all four durable continuity surfaces before proceeding.

## Owner decisions controlling later rounds

- **T3:** separate Portrait/Landscape adaptive card-distribution preferences (`Comfortable / Balanced / Compact / Dense`; `Balanced` normal default). Runtime computes safe effective columns from width, orientation preference, text/UI density and minimum card width. Legacy exact-count prefs map compatibly.
- **T9:** explicit haptics `None`; existing users retain their prior non-none choice across upgrade.
- **T4:** the target is specifically the **text-size** control. Main text size should be symmetric around 100 with preferred normal range `50,60,70,80,90,100,110,120,130,140,150` and compatibility-safe mapping. Spacing density is already symmetric and must not be unnecessarily redesigned as part of T4.

## Physical evidence preserved

Phone exact preqa.12: 1–6 PASS; 7–8 OPEN/implemented Round 1 pending revalidation; 9 functional PASS + implemented direct-toggle refinement pending revalidation; 10–16 PASS; 17.1–17.3 OPEN/implemented Round 3 pending revalidation; 18–20 PASS; 21 UNASSESSED; 22 PARTIAL/AMBIGUOUS; 23 PASS.

Tablet exact preqa.12: 1–6 PASS; 7 FAIL/T7; 8 PASS; 9–10 FAIL/T5; 11 PASS + checkbox-family reproduction now implemented in Round 3 pending revalidation; 12–14 PASS; 15 FAIL/T8; 16–17 PASS; 18 BLOCKED BY T5.

Do not replay unrelated accepted evidence.

## Frozen candidate evidence unchanged until a new consolidated build is versioned

- candidate commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact ID `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

## Exact route

1. T5 spell-source/bootstrap/source-context compatibility audit/repair + focused automation + synchronized status update.
2. Continue remaining dependency-aware repair rounds, updating the dedicated round checkpoint + `PROJECT_STATE.md` + `LATEST.md` + `TESTING.md` after each bounded round.
3. Run aggregate Scaffold over the completed consolidated repair.
4. Freeze a new monotonic physical-QA candidate.
5. Perform targeted cross-device revalidation only for failed/touched/affected families plus phone 21/affected phone 22 and tablet 18 after T5.
6. Explicit owner Phase 4A closure only after repaired evidence is sufficient.

No P18 exists. DM implementation remains blocked until explicit Phase 4A closure.

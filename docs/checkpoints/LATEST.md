# Latest project checkpoint — Player / Phase 4A

**Updated:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative Player implementation/repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — PHONE + TABLET DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation gate:** consolidated post-P17 repair IN PROGRESS; **Rounds 1–3 + T5 COMPLETE / AUTOMATION GREEN**; next = **T6 class-editor numeric keyboard + standard die / `Otro…` selector**  
**Release status:** debug/development; Phase 4A OPEN; DM implementation blocked pending explicit owner closure

## Resume here

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T5_SPELL_SOURCE_BOOTSTRAP.md` — **latest completed repair; canonical spell-source bootstrap/source-context compatibility + exact green steady-state automation evidence**.
2. `docs/PROJECT_STATE.md` — live Player authority/current implementation state and next repair family.
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND3_COMPACT_CHECKBOX_RESPONSIVE_GROUPING.md` — completed checkbox/responsive repair + toggle classification.
4. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md` — completed T1 repair + exact green automation evidence.
5. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md` — completed structured-dice/signed-modifier round.
6. `docs/checkpoints/2026-09-13_PHASE4A_OWNER_REPAIR_DECISIONS_IMPLEMENTATION_GO.md` — owner decisions + explicit implementation authorization.
7. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — controlling source audit + repair-family contracts + targeted revalidation matrix.
8. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_P17_TABLET_QA_PROGRESS.md` — complete P17 tablet discovery evidence.
9. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_PHONE_FINDINGS_P17_ROUTE.md` — controlling detailed phone findings.
10. `docs/checkpoints/2026-09-13_PHASE4A_PREQA12_QA_CANDIDATE.md` — exact preqa.12 candidate/run/artifact/digest evidence.
11. `docs/TESTING.md` — synchronized testing position, policy and targeted revalidation route.

## Completed repair rounds

### Round 1 — structured dice / signed modifier: COMPLETE / GREEN

Product/test HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; authoritative Scaffold `34787688776` / run `1508` — **SUCCESS**.

Phone 7–9 implementation basis is repaired and protected by tests/guard; physical revalidation waits for the consolidated candidate. T2 remains partial.

### Round 2 — T1 reorder stability: COMPLETE / GREEN

Product/test HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; authoritative Scaffold `34788409987` / run `1519` — **SUCCESS**.

T1 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. Physical closure requires both one-column and multi-column/spatial reorder, no target-chasing reflow, applicable auto-scroll and final order persistence. Reuse the existing owner video as failure baseline; do not ask for it again.

### Round 3 — compact checkbox + responsive grouping: COMPLETE / GREEN

Migrated product commit `582a809bafbc4d7d38836283ed0eb5fe33d94624`; final product/test HEAD `1d1c476ddaeb045c8a1b186267f452010cad7681`; authoritative Scaffold `34793253151` / run `1537` — **SUCCESS**.

Source-complete result: 19 raw Material Checkbox calls across seven Player files migrated to the shared compact/touch-safe language; responsive Equipment/Conjuros grouping is protected by a permanent guard; 11 legitimate Material `Switch` sites remain intentional and there are 0 `TriStateCheckbox` sites.

Phone 17.1–17.3 and the tablet checkbox-family reproduction are **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**.

### T5 — spell-source bootstrap / source context: COMPLETE / GREEN

Core editor integration `2e7fda2852425594971f7df47b433d642eb2119a`; final tested steady-state HEAD `3774c53f5189ebd535cc1b73ec18493e268e5d9f`; authoritative Scaffold `34794589758` / run `1560` — **SUCCESS**.

T5 now reconciles canonical base spellcasting classes into the existing source/profile overlay by exact class identity. Existing compatible source IDs, spell associations and configured profiles are preserved; missing canonical profiles receive bounded default casting ability; manual/homebrew `OTHER` sources remain supported. Existing pre-T5 characters get a non-destructive editor projection, class changes reconcile immediately, and the normal Save path persists the resulting source/profile state.

Known bounded base-caster metadata includes Artificer (2025/5e), Bard, Cleric, Druid, Paladin, Ranger, Sorcerer, Warlock and Wizard/Mago with their canonical casting ability. No Fighter/Rogue subclass inference or general spell-legality engine was added.

Run `1560` passed backend, compact geometry guard, reorder guard, checkbox guard, T5 bootstrap guard, focused shared tests, Android/Desktop build and APK upload. Artifact `10329072751`; GitHub Actions artifact digest `sha256:26fe76650eb90a56ac2b5b239a3fd75ccb1b70bc2faf41003fac316ed5a04aae`.

T5 and the previously blocked tablet source-context path are **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. They are not physically PASS yet.

## Next repair family — T6 class editor controls

The next bounded round addresses the class-editor control issues from the post-P17 audit:

- numeric fields that should summon numeric keyboard behavior must use the appropriate keyboard options;
- hit-die selection should use the standard die set plus `Otro…` rather than relying on an unrestricted freeform numeric control where the audited contract calls for a die selector;
- preserve existing/custom values safely when they do not match a standard die;
- do not broaden this into class-rules validation or a class-builder redesign;
- add focused tests/guards and run normal Scaffold;
- update all four durable continuity surfaces before proceeding.

## Owner decisions controlling later rounds

- **T3:** separate Portrait/Landscape adaptive card-distribution preferences (`Comfortable / Balanced / Compact / Dense`; `Balanced` normal default), with runtime-safe effective columns and compatibility mapping from legacy exact counts.
- **T9:** explicit haptics `None`; existing users retain prior non-none selection across upgrade.
- **T4:** specifically the **text-size** control; symmetric around 100 with preferred normal range `50,60,70,80,90,100,110,120,130,140,150`. Spacing density is already symmetric and must not be redesigned as part of T4.

## Physical evidence preserved

Phone preqa.12: 1–6 PASS; 7–8 implemented Round 1 pending revalidation; 9 functional PASS + direct-toggle refinement pending revalidation; 10–16 PASS; 17.1–17.3 implemented Round 3 pending revalidation; 18–20 PASS; 21 UNASSESSED; 22 PARTIAL/AMBIGUOUS; 23 PASS.

Tablet preqa.12: 1–6 PASS; 7 FAIL/T7; 8 PASS; 9–10 FAIL/T5 now implemented/green pending revalidation; 11 PASS + checkbox-family reproduction implemented Round 3 pending revalidation; 12–14 PASS; 15 FAIL/T8; 16–17 PASS; 18 formerly BLOCKED BY T5 and now available for targeted revalidation on the future consolidated candidate.

Do not replay unrelated accepted evidence.

## Frozen candidate unchanged

- version `0.4.0-preqa.12 / 41200`;
- commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact `10323602038` / `dnd-custom-aid-debug-apk`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

## Exact route

1. T6 class-editor numeric keyboard + standard die / `Otro…` selector repair + focused automation + synchronized status update.
2. Continue remaining dependency-aware repairs, updating the dedicated checkpoint + `PROJECT_STATE.md` + `LATEST.md` + `TESTING.md` after each bounded round.
3. Run aggregate Scaffold over completed consolidated repair.
4. Freeze a new monotonic physical-QA candidate.
5. Perform targeted cross-device revalidation only for failed/touched/affected families plus phone 21/affected phone 22 and tablet 18 now that T5 is repaired.
6. Explicit owner Phase 4A closure only after repaired evidence is sufficient.

No P18 exists. DM implementation remains blocked until explicit Phase 4A closure.

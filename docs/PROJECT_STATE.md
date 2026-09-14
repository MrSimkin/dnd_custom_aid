# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; CROSS-DEVICE PHYSICAL DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation state:** consolidated repair IN PROGRESS; **Rounds 1–3 + T5 + T6 COMPLETE / AUTOMATION GREEN**; next = **Application Settings T3/T4/T9**  
**Release status:** development/debug; Phase 4A OPEN; DM implementation blocked pending explicit Phase 4A closure

## Authority / continuity

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase5A/DM discovery and is not the latest Player runtime. No P18 exists.

Resume in this order:

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T6_CLASS_EDITOR_CONTROLS.md` — latest completed bounded repair and exact T6 steady-state automation evidence;
2. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T5_SPELL_SOURCE_BOOTSTRAP.md` — T5 canonical spell-source bootstrap/source-context repair;
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND3_COMPACT_CHECKBOX_RESPONSIVE_GROUPING.md`;
4. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md`;
5. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md`;
6. `docs/checkpoints/2026-09-13_PHASE4A_OWNER_REPAIR_DECISIONS_IMPLEMENTATION_GO.md` — approved T3/T4/T9 product decisions + implementation authorization;
7. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — controlling source audit / repair contracts / revalidation matrix;
8. `docs/TESTING.md` — synchronized testing policy and targeted revalidation route.

Supporting physical evidence remains the preqa.12 phone findings, tablet P17 progress and exact candidate checkpoint. Accepted PASS evidence must not be replayed merely because later repair rounds advance.

## Completed repair families

### Round 1 — structured dice / signed modifier: COMPLETE / GREEN

Product/test HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; Scaffold `34787688776` / run `1508` — **SUCCESS**. Phone 7–9 implementation basis is repaired; physical revalidation waits for the consolidated candidate. T2 remains partial.

### Round 2 — T1 reorder stability: COMPLETE / GREEN

Product/test HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; Scaffold `34788409987` / run `1519` — **SUCCESS**. Stable drag-start geometry, canonical-order previewing, hysteresis, real-scroll translation and one-time lazy-target capture cover one-dimensional and spatial reorder.

T1 remains **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. Reuse the existing owner failure video; do not request it again.

### Round 3 — compact checkbox + responsive grouping: COMPLETE / GREEN

Final product/test HEAD `1d1c476ddaeb045c8a1b186267f452010cad7681`; Scaffold `34793253151` / run `1537` — **SUCCESS**. Nineteen raw Material Checkbox calls across seven Player files were migrated to the shared compact/touch-safe primitive; responsive Equipment/Conjuros grouping is guarded; 11 legitimate Material `Switch` sites remain intentional; 0 `TriStateCheckbox` sites.

Phone 17.1–17.3 and tablet checkbox reproduction remain **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**.

### T5 — spell-source bootstrap / source context: COMPLETE / GREEN

Core editor integration `2e7fda2852425594971f7df47b433d642eb2119a`; steady-state HEAD `3774c53f5189ebd535cc1b73ec18493e268e5d9f`; Scaffold `34794589758` / run `1560` — **SUCCESS**.

Canonical base spellcasting classes now reconcile into the existing source/profile overlay by exact class identity. Compatible source IDs, spell associations and configured profiles are preserved; missing canonical profiles receive bounded default casting ability; manual/homebrew `OTHER` sources remain supported. T5 does not implement Fighter/Rogue subclass casting, spell legality or multiclass progression.

T5 remains **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. Tablet check 18 is no longer blocked and becomes part of future targeted revalidation.

### T6 — class-editor numeric / hit-die controls: COMPLETE / GREEN

Product commit `c98121e50f347f64898c9e31d077e53cad31685f`; steady-state HEAD `6ba73b22b07a4c5a92d69425a7372d2695fbc30a`; Scaffold `34795355116` / run `1571` — **SUCCESS**.

Implemented numeric keyboard behavior for `Nivel`, `DG restantes` and custom hit-die sides; replaced freeform `Dado` with a compact `d4/d6/d8/d10/d12/d20 + Otro…` selector; preserved catalog preselection and existing nonstandard values; added permanent `check_player_class_editor_controls.py`. No schema/storage/class-rules redesign was introduced. The temporary migration writer was removed and normal Scaffold is read-only again.

Artifact `10329671667`, size `13,645,969` bytes, digest `sha256:6391210567ff4964b7077e1cc6e9c3c38bd862b62aba19a01e929ef6bfe4db4d`.

T6 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. It is not physically PASS yet.

## Current next family — Application Settings T3/T4/T9

Owner decisions are already complete and implementation is authorized:

- **T3:** replace exact promised column counts with separate **Portrait** and **Landscape** adaptive card-distribution/density preferences. User-facing levels: `Comfortable / Balanced / Compact / Dense`; `Balanced` is the normal default. Runtime computes safe effective columns from available width, orientation preference, effective text/UI density and minimum usable card width. Legacy exact-count preferences must map to the closest density intent without unnecessary reset.
- **T4:** the **text-size** control only. Use a symmetric normal scale around 100, preferred `50,60,70,80,90,100,110,120,130,140,150`. Existing persisted values map to the nearest valid option. Spacing density is already symmetric and is not a T4 target.
- **T9:** add explicit haptics `None`; selecting it disables app-generated haptic feedback. Existing users retain their current non-none preference after upgrade.

## Remaining after T3/T4/T9

- **T7 wide Combat composition:** adaptive wide/card layout using stabilized reorder/layout primitives.
- **T8 Table Mode:** structural affordances hidden/disabled while operational controls remain enabled.
- **T2 remaining integration:** die-specific result silhouettes, Custom Throw die/custom sides/signed modifier, Dice-tab ownership of display mode.
- **Optional phone-16 compact-field refinement:** only if visual comparison proves a safe material benefit.

## Physical evidence / candidate policy

The frozen physical candidate remains unchanged:

- versionName `0.4.0-preqa.12`;
- versionCode `41200`;
- commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact `10323602038`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

Do not physically revalidate every intermediate repair. Complete the consolidated material repair, run the aggregate Scaffold, freeze one new monotonic physical-QA candidate, then perform targeted phone/tablet revalidation for failed/touched/affected families plus unresolved phone 21/affected phone 22 and tablet 18.

## Exact route

1. Execute **Application Settings T3/T4/T9** as one bounded, compatibility-safe family with focused guard/tests and normal Scaffold.
2. At the end of the family, update the dedicated checkpoint + this `PROJECT_STATE.md` + `docs/checkpoints/LATEST.md` + `docs/TESTING.md`.
3. Continue T7, T8 and remaining T2 in dependency-aware order.
4. Run aggregate Scaffold over the completed consolidated repair.
5. Freeze a new monotonic physical-QA candidate.
6. Perform targeted cross-device revalidation only; preserve unrelated accepted evidence.
7. Phase 4A may close only after sufficient repaired evidence and explicit owner acceptance.

Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change. DM implementation remains blocked until explicit Phase 4A owner closure.

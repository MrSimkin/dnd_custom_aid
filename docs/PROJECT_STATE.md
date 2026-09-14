# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; CROSS-DEVICE PHYSICAL DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation state:** consolidated repair IN PROGRESS; **Rounds 1–3 + T5 + T6 + T3/T4/T9 COMPLETE / AUTOMATION GREEN**; next = **T7 wide Combat adaptive composition**  
**Release status:** development/debug; Phase 4A OPEN; DM implementation blocked pending explicit Phase 4A closure

## Authority / continuity

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase5A/DM discovery and is not the latest Player runtime. No P18 exists.

Resume in this order:

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_APPLICATION_SETTINGS_T3_T4_T9.md` — latest completed bounded repair and exact clean automation evidence;
2. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T6_CLASS_EDITOR_CONTROLS.md`;
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T5_SPELL_SOURCE_BOOTSTRAP.md`;
4. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND3_COMPACT_CHECKBOX_RESPONSIVE_GROUPING.md`;
5. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md`;
6. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md`;
7. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — controlling source audit / remaining repair contracts / revalidation matrix;
8. `docs/TESTING.md` — synchronized testing policy and route.

Supporting physical evidence remains the preqa.12 phone findings, tablet P17 progress and exact candidate checkpoint. Accepted PASS evidence must not be replayed merely because later repair rounds advance.

## Completed repair families

### Round 1 — structured dice / signed modifier: COMPLETE / GREEN

HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; Scaffold `34787688776` / #1508 — **SUCCESS**. Phone 7–9 implementation basis repaired; physical revalidation waits for the consolidated candidate. T2 remains partial.

### Round 2 — T1 reorder stability: COMPLETE / GREEN

HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; Scaffold `34788409987` / #1519 — **SUCCESS**. Stable drag-start geometry, canonical preview, hysteresis, real-scroll translation and lazy-target capture cover one-dimensional and spatial reorder. T1 remains **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. Reuse the existing owner failure video; do not request it again.

### Round 3 — compact checkbox + responsive grouping: COMPLETE / GREEN

HEAD `1d1c476ddaeb045c8a1b186267f452010cad7681`; Scaffold `34793253151` / #1537 — **SUCCESS**. Nineteen raw Material Checkbox calls across seven Player files migrated to shared compact/touch-safe controls; responsive Equipment/Conjuros grouping guarded. Phone 17.1–17.3 and tablet checkbox reproduction remain targeted-physical-revalidation pending.

### T5 — spell-source bootstrap / source context: COMPLETE / GREEN

Core integration `2e7fda2852425594971f7df47b433d642eb2119a`; steady-state HEAD `3774c53f5189ebd535cc1b73ec18493e268e5d9f`; Scaffold `34794589758` / #1560 — **SUCCESS**. Canonical base caster classes reconcile into the existing source/profile overlay while preserving compatible source IDs, associations, configured profiles and manual/homebrew sources. T5 remains targeted-physical-revalidation pending; tablet check 18 is no longer blocked.

### T6 — class-editor numeric / hit-die controls: COMPLETE / GREEN

Product `c98121e50f347f64898c9e31d077e53cad31685f`; steady-state HEAD `6ba73b22b07a4c5a92d69425a7372d2695fbc30a`; Scaffold `34795355116` / #1571 — **SUCCESS**. Numeric keypad behavior plus `d4/d6/d8/d10/d12/d20 + Otro…` implemented with catalog/custom preservation. Artifact `10329671667`, digest `sha256:6391210567ff4964b7077e1cc6e9c3c38bd862b62aba19a01e929ef6bfe4db4d`. T6 remains targeted-physical-revalidation pending.

### T3/T4/T9 — Application Settings semantics: COMPLETE / GREEN

Product commit `1dcd320417e7e3b45ec02ec6aa7cbb616c84e473`; steady-state HEAD `5db7bc3a48f1e640fc80770dd07d68a7ffaa02f7`; authoritative Scaffold `34796452617` / #1583 — **SUCCESS**.

Implemented:

- T3 separate adaptive Vertical/Horizontal card-density preferences (`Cómodo / Equilibrado / Compacto / Denso`) with `Equilibrado` default;
- live runtime derives safe columns from actual available width, orientation density, effective font scale, spacing pressure and minimum usable card width instead of consuming legacy exact-count preferences;
- old exact-count keys remain migration/rollback shadows only and map compatibly to adaptive intent;
- T4 normal text-size scale is symmetric `50..150` step 10 with nearest-value migration; spacing density remains unchanged;
- T9 adds `Ninguna` and short-circuits the shared haptic dispatch before both Android vibrator and fallback platform haptic paths;
- existing users retain MEDIUM as missing/legacy non-none fallback;
- permanent `check_player_application_settings_semantics.py` guards these semantics.

The temporary migration helper and haptic diagnostic were removed. The normal Scaffold is read-only again.

Artifact `10329772684`, size `13,644,492` bytes, digest `sha256:74249a197c21572743165927c9330f38ceafd2db7f78267872f36e2edfd2f1af`.

T3/T4/T9 are **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. They are not physically PASS.

## Current next family — T7 wide Combat adaptive composition

The active path is `CharacterCombatSuccessorV4.kt`. The audited tablet-landscape cause remains current: the operational composition and attack/action list are one-dimensional, and cards use full available width.

T7 contract:

- preserve narrow-phone behavior;
- keep the operational HUD deliberately compact/bounded on wide layouts;
- use adaptive multi-column attack/action cards on wide/tablet layouts and consume the repaired T3 density semantics;
- reuse Round 2's stabilized spatial reorder foundation for wide multi-column reorder rather than inventing another drag engine;
- preserve favorite/edit/delete semantics and persisted ordering;
- add focused guard/tests and run the normal read-only Scaffold.

## Remaining after T7

- **T8 Table Mode:** structural affordances hidden/disabled while operational controls remain enabled.
- **T2 remaining integration:** die-specific result silhouettes, Custom Throw die/custom sides/signed modifier, Dice-tab ownership of display mode.
- **Optional phone-16 compact-field refinement:** only if visual comparison proves safe material benefit.

## Physical evidence / candidate policy

The frozen physical candidate remains unchanged:

- versionName `0.4.0-preqa.12`;
- versionCode `41200`;
- commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact `10323602038`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

Do not physically revalidate intermediate rounds independently. Complete the consolidated material repair, run the aggregate Scaffold, freeze one new monotonic physical-QA candidate, then perform targeted cross-device revalidation for failed/touched/affected families plus unresolved phone 21/affected phone 22 and tablet 18.

## Exact route

1. Execute **T7 wide Combat adaptive composition** on the active successor path.
2. Update the dedicated T7 checkpoint + this file + `LATEST.md` + `TESTING.md` after focused/aggregate automation is green.
3. Continue T8 and remaining T2 in dependency-aware order.
4. Run aggregate Scaffold over the completed consolidated repair.
5. Freeze a new monotonic physical-QA candidate.
6. Perform targeted cross-device revalidation only; preserve unrelated accepted evidence.
7. Phase 4A closes only after sufficient repaired evidence and explicit owner acceptance.

Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change. DM implementation remains blocked until explicit Phase 4A owner closure.

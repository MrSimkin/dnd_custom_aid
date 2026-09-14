# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-14  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair + physical-revalidation line  
**Current exact frozen physical candidate:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4` — **AUTOMATION GREEN / TARGETED CROSS-DEVICE PHYSICAL REVALIDATION PENDING**  
**Current implementation state:** consolidated material post-P17 repair **COMPLETE / AUTOMATION GREEN**; Rounds 1–3 + T5 + T6 + T3/T4/T9 + T7 + T8 + T2 complete; aggregate Scaffold #1625 SUCCESS; exact versioned candidate Scaffold #1630 SUCCESS  
**Release status:** debug/development; Phase 4A OPEN; DM implementation blocked pending explicit Phase 4A owner closure

## Authority / continuity

This branch remains authoritative for current Player runtime and Phase 4A physical revalidation. `main` remains intentionally divergent for global/Phase5A/DM discovery and is not the latest Player runtime. No P18 exists.

Resume in this order:

1. `docs/checkpoints/2026-09-14_PHASE4A_PREQA13_CONSOLIDATED_REPAIR_CANDIDATE.md` — exact frozen candidate, artifact evidence and targeted revalidation matrix;
2. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T2_DICE_PRESENTATION_CUSTOM_THROW.md` — latest completed bounded material repair + aggregate automation proof;
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T8_TABLE_MODE.md`;
4. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T7_WIDE_COMBAT.md`;
5. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_APPLICATION_SETTINGS_T3_T4_T9.md`;
6. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T6_CLASS_EDITOR_CONTROLS.md`;
7. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T5_SPELL_SOURCE_BOOTSTRAP.md`;
8. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND3_COMPACT_CHECKBOX_RESPONSIVE_GROUPING.md`;
9. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md`;
10. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md`;
11. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — original source audit / repair contracts / revalidation matrix;
12. `docs/TESTING.md` — synchronized testing policy and current manual gate.

The prior `preqa.12` phone/tablet discovery remains historical evidence. Accepted PASS evidence from that candidate must not be replayed merely because the repaired candidate advanced to `preqa.13`.

## Completed repair families

### Round 1 — structured dice / signed modifier

HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; Scaffold `34787688776` / #1508 — **SUCCESS**. Shared `NdS±M` parsing/rolling and direct sign foundation are implemented. Physical revalidation is folded into the targeted Round 1/T2 dice-family pass on `preqa.13`.

### Round 2 — T1 reorder stability

HEAD `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`; Scaffold `34788409987` / #1519 — **SUCCESS**. Stable drag-start geometry, canonical preview, hysteresis, real-scroll translation and lazy-target capture cover one-dimensional and spatial reorder. T1 remains **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. Reuse the existing owner failure video as historical baseline; do not request it again.

### Round 3 — compact checkbox + responsive grouping

HEAD `1d1c476ddaeb045c8a1b186267f452010cad7681`; Scaffold `34793253151` / #1537 — **SUCCESS**. Nineteen raw Material Checkbox sites were migrated to shared compact/touch-safe controls; responsive Equipment/Conjuros grouping is guarded. Phone 17.1–17.3 and the tablet checkbox reproduction remain targeted-revalidation pending.

### T5 — spell-source bootstrap / source context

Core `2e7fda2852425594971f7df47b433d642eb2119a`; steady-state `3774c53f5189ebd535cc1b73ec18493e268e5d9f`; Scaffold `34794589758` / #1560 — **SUCCESS**. Canonical caster sources reconcile into the existing source/profile overlay while preserving compatible IDs, associations, configured profiles and manual/homebrew sources. Former tablet check 18 is now testable on `preqa.13`.

### T6 — class-editor numeric / hit-die controls

Product `c98121e50f347f64898c9e31d077e53cad31685f`; steady-state `6ba73b22b07a4c5a92d69425a7372d2695fbc30a`; Scaffold `34795355116` / #1571 — **SUCCESS**. Numeric keypad behavior plus `d4/d6/d8/d10/d12/d20 + Otro…` are implemented with catalog/custom preservation.

### T3/T4/T9 — Application Settings semantics

Product `1dcd320417e7e3b45ec02ec6aa7cbb616c84e473`; steady-state `5db7bc3a48f1e640fc80770dd07d68a7ffaa02f7`; Scaffold `34796452617` / #1583 — **SUCCESS**. Separate adaptive Vertical/Horizontal card-density preferences, symmetric `50..150` text-size options, compatibility migration and real haptics `Ninguna` are implemented and guarded.

### T7 — wide Combat adaptive composition

Product `5f00bc006a26600a5d03582fbfbdf2e266259673`; steady-state `e567750a529238a2b45722b6f5ed726dd9123d88`; Scaffold `34797403737` / #1592 — **SUCCESS**. Narrow one-column Combat is preserved; wide/tablet Combat uses a bounded HUD, T3-driven adaptive attack/action grid and stabilized spatial reorder with one canonical persisted-order path.

### T8 — Table Mode structural-affordance enforcement

Product `2734d08a72e183ca213cd9213ff47a4db588cbf6`; steady-state `d6c13819a49e1cb7c71dffcad98b53c250d4d9d4`; Scaffold `34799667822` / #1611 — **SUCCESS**. Structural name/class/ability/save/skill/general-reference editing is visibly gated in Table Mode while HP, inspiration and current resource state remain operational.

### T2 — dice-result / Custom Throw / display-mode integration

Product `8131dd13f4373148503b854b1448f9d081556847`; final migration-free steady-state `4d09e9eca648e5ca82af896dab8d16b986faae5b`; authoritative Scaffold `34801201294` / #1625 — **SUCCESS**.

T2 adds die-specific d4/d6/d8/d10/d12/d20 result silhouettes plus neutral fallback, standard/custom-side Custom Throw, signed modifier UX, shared arbitrary-die resolution reused by damage, and Dice-tab ownership of the existing `dice_result_mode` preference. Permanent `check_player_dice_t2.py` remains in normal read-only Scaffold.

T2 remains **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING** until owner/device evidence is collected on `preqa.13`.

## Consolidated automation proof

All material post-P17 repair families are implemented and automation green.

Final migration-free aggregate proof before candidate versioning:

- HEAD `4d09e9eca648e5ca82af896dab8d16b986faae5b`;
- Scaffold `34801201294` / #1625 — **SUCCESS**;
- every permanent Player guard PASS;
- backend PASS;
- shared tests PASS;
- Android build PASS;
- Desktop build PASS;
- APK upload PASS.

The optional phone-16 compact-field refinement is **NOT TAKEN**. Phone check 16 was already PASS and no later evidence demonstrated a safe material benefit sufficient to justify visual churn before the physical candidate.

## Frozen physical candidate — preqa.13

Exact immutable candidate for the next owner/device pass:

- versionName `0.4.0-preqa.13`;
- versionCode `41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- candidate Scaffold `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- artifact/archive size `13,693,984` bytes;
- GitHub artifact digest `sha256:13b72f8a7e797e40009f548bd45d1c538a41cfe52ecf9796110e75498e359893`;
- independently downloaded ZIP SHA-256 `13b72f8a7e797e40009f548bd45d1c538a41cfe52ecf9796110e75498e359893` — exact match;
- APK size `39,094,384` bytes;
- independent APK SHA-256 `81fbc20525221d791e6a0f786dec4b05709505e052e0e5ffba4ebfa5eb02391e`.

`preqa.13` is automation green but **not physically PASS**.

## Current gate — targeted physical revalidation

Do **not** replay the full historical phone/tablet suites. The next work is owner/device evidence on this exact `preqa.13 / 41300` candidate for failed/touched/affected families only.

Required coverage:

1. Round 1 + T2 dice family — phone 7–9, ordinary visible d20 sanity, non-d20 Custom Throw, `Otro…` bounds, signed modifiers, damage/flat visuals and Dice-tab display-mode persistence;
2. T1 reorder — one-column + spatial/multi-column + applicable auto-scroll + leave/reopen persisted order;
3. Round 3 checkbox/responsive grouping — representative Equipment/other migrated checkbox plus Conjuros groups/source-prepared packing across relevant orientations;
4. T5 — canonical caster source/bootstrap, associations, save/reopen, manual-source coexistence and tablet 18;
5. T6 — numeric keyboard, standard/custom hit die and persistence;
6. T3/T4/T9 — Vertical/Horizontal density, text-size behavior/migration and haptics `Ninguna`/non-none;
7. T7 — tablet-landscape Combat width/packing/reorder/persistence plus one narrow-phone sanity;
8. T8 — structural controls genuinely non-editable/non-opening while operational HP/inspiration/resources remain usable/persistent, plus one narrow-phone sanity;
9. unresolved phone 21;
10. affected slice of phone 22;
11. tablet 18, formerly blocked by T5.

Preserve all unrelated accepted PASS evidence.

## Failure / closure rules

If targeted physical QA finds a material defect, reopen only the relevant bounded repair family after source audit; rerun focused + aggregate automation; advance to another monotonic candidate identity if the frozen candidate changes materially; checkpoint the new evidence before another owner pass.

Phase 4A closes only after sufficient targeted repaired evidence and **explicit owner acceptance**. CI cannot substitute for physical evidence or owner closure. DM implementation remains blocked until that explicit Phase 4A closure.

Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change. No P18 exists.
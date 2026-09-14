# Project State — Player / Phase 4A successor line

**Last verified:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Role:** authoritative current Player runtime / Phase 4A repair line  
**Current exact frozen physical candidate:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a` — AUTOMATION GREEN; CROSS-DEVICE PHYSICAL DISCOVERY COMPLETE WITH OPEN FINDINGS  
**Current implementation state:** consolidated material post-P17 repair **COMPLETE / AUTOMATION GREEN**; Rounds 1–3 + T5 + T6 + T3/T4/T9 + T7 + T8 + T2 complete; aggregate steady-state Scaffold #1625 SUCCESS; next = **freeze one new monotonic physical-QA candidate**  
**Release status:** development/debug; Phase 4A OPEN; DM implementation blocked pending explicit Phase 4A closure

## Authority / continuity

This branch remains authoritative for current Player runtime and Phase 4A repairs. `main` remains intentionally divergent for global/Phase5A/DM discovery and is not the latest Player runtime. No P18 exists.

Resume in this order:

1. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T2_DICE_PRESENTATION_CUSTOM_THROW.md` — latest completed bounded repair, aggregate automation evidence and exact next gate;
2. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T8_TABLE_MODE.md`;
3. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T7_WIDE_COMBAT.md`;
4. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_APPLICATION_SETTINGS_T3_T4_T9.md`;
5. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T6_CLASS_EDITOR_CONTROLS.md`;
6. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_T5_SPELL_SOURCE_BOOTSTRAP.md`;
7. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND3_COMPACT_CHECKBOX_RESPONSIVE_GROUPING.md`;
8. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND2_REORDER_STABILITY.md`;
9. `docs/checkpoints/2026-09-13_PHASE4A_REPAIR_ROUND1_STRUCTURED_DICE.md`;
10. `docs/checkpoints/2026-09-13_PHASE4A_POST_P17_CROSS_DEVICE_AUDIT_REPAIR_PLAN.md` — controlling source audit / repair contracts / targeted revalidation matrix;
11. `docs/TESTING.md` — synchronized testing policy and route.

Supporting physical evidence remains the preqa.12 phone findings, tablet P17 progress and exact candidate checkpoint. Accepted PASS evidence must not be replayed merely because later repair rounds advance.

## Completed repair families

### Round 1 — structured dice / signed modifier: COMPLETE / GREEN

HEAD `6fa8f7b1611648d49b1e839f0ac9cc7214e651f0`; Scaffold `34787688776` / #1508 — **SUCCESS**. Phone 7–9 implementation basis repaired; physical revalidation waits for the consolidated candidate. The remaining T2 integration is now also complete.

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

Implemented separate adaptive Vertical/Horizontal card-density preferences (`Cómodo / Equilibrado / Compacto / Denso`), safe runtime column derivation from real width/text/spacing pressure, compatibility mapping from legacy exact-count preferences, symmetric `50..150` text-size options with nearest-value migration, and real dispatch-level haptics `Ninguna`. Permanent `check_player_application_settings_semantics.py` guards these semantics.

Artifact `10329772684`, size `13,644,492` bytes, digest `sha256:74249a197c21572743165927c9330f38ceafd2db7f78267872f36e2edfd2f1af`.

T3/T4/T9 are **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. They are not physically PASS.

### T7 — wide Combat adaptive composition: COMPLETE / GREEN

Product commit `5f00bc006a26600a5d03582fbfbdf2e266259673`; steady-state HEAD `e567750a529238a2b45722b6f5ed726dd9123d88`; authoritative Scaffold `34797403737` / #1592 — **SUCCESS**.

T7 preserves the narrow one-column Combat path while changing wide/tablet Combat to a deliberately bounded operational HUD plus T3-driven adaptive attack/action grid. Wide cards reuse the stabilized spatial reorder foundation, including stable target geometry and auto-scroll; both narrow and wide reorder converge on one canonical persisted-order commit path. Favorite/Edit/Delete are suppressed during an active spatial drag. No storage/schema/import/export model changed.

Permanent `check_player_wide_combat.py` guards the active successor path, narrow-path preservation, adaptive columns, bounded HUD, spatial reorder wiring and persisted ordering.

Artifact `10330505672`, size `13,651,587` bytes, digest `sha256:582d455aea9dad0f0898fa43368d6bd69e6ed8994914522008ada85abe46c4c9`.

T7 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. It is not physically PASS.

### T8 — Table Mode structural-affordance enforcement: COMPLETE / GREEN

Product commit `2734d08a72e183ca213cd9213ff47a4db588cbf6`; steady-state HEAD `d6c13819a49e1cb7c71dffcad98b53c250d4d9d4`; authoritative Scaffold `34799667822` / #1611 — **SUCCESS**.

T8 aligns the visible UI with the existing `CharacterTableModePolicy`: structural name/class/ability/save/skill/general-reference editing is hidden, disabled or read-only while Table Mode is active, while genuine operational HP, inspiration and current resource state remain usable. The repair covers Overview, Skills, class identity and general structural references; already-correct specialized collection modules and PC Settings were left unchanged.

Permanent `check_player_table_mode_affordances.py` guards both sides of the contract: structural affordances remain gated and operational current/session values remain routed through operational persistence. No storage/schema/import/export model changed.

Artifact `10330822191`, size `13,662,998` bytes, digest `sha256:bf90762e36177735bd948524c37552b143ec1bbf1ddd7bdb292511b7a7255715`.

T8 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. It is not physically PASS.

### T2 — dice-result / Custom Throw / display-mode integration: COMPLETE / GREEN

Product commit `8131dd13f4373148503b854b1448f9d081556847`; final migration-free steady-state HEAD `4d09e9eca648e5ca82af896dab8d16b986faae5b`; authoritative Scaffold `34801201294` / #1625 — **SUCCESS**.

T2 completes the Round-1 dice foundation with die-specific result silhouettes for d4/d6/d8/d10/d12/d20 plus neutral circular fallback, real Custom Throw standard/custom-side selection and signed modifier UX, shared arbitrary-die resolution reused by damage rolling, and Dice-tab ownership of the existing persisted result-display preference. No preference key, storage/schema/import/export model changed.

Permanent `check_player_dice_t2.py` guards the active successor Dice path, custom throw semantics, shared resolver reuse, die-result visual contract and preference ownership.

Artifact `10331846822`, size `13,693,976` bytes, digest `sha256:4ceeba64f31ab3ee4436f2ee1cc71ce324154d84cb99a551cbb1ef3ba28cb7f0`.

T2 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. It is not physically PASS.

## Consolidated material repair status

All material post-P17 repair families identified for this cycle are now implemented and automation green. Normal read-only Scaffold #1625 ran after T2 temporary migration cleanup and passed every permanent Player guard plus shared tests, Android/Desktop builds, backend type-check and APK upload. It therefore serves as the aggregate automation proof for the completed consolidated repair set.

The optional phone-16 compact-field refinement is **not taken** before the next candidate. Phone check 16 was already PASS, and no new evidence demonstrates a safe material benefit that would justify introducing visual churn into an accepted surface.

This does not convert any automation-green repair into physical PASS.

## Physical evidence / candidate policy

The frozen physical candidate remains unchanged until a new candidate is explicitly versioned/frozen:

- versionName `0.4.0-preqa.12`;
- versionCode `41200`;
- commit `abfc7e4a1519a27117f194721a425d75cb5df68a`;
- Scaffold `34776627282` — SUCCESS;
- artifact `10323602038`;
- ZIP SHA-256 `0c2ee37cac5be74e8a63e2e636147dbf890448ecad0d45f240e03a6c65a1a3c7`;
- APK SHA-256 `5f28785d02cf663a5a3626b2ce328f48eb0cd2de74946b1403cdb5afc0dfbcce`.

Do not physically revalidate intermediate rounds independently. The next step is to freeze one new monotonic physical-QA candidate from the consolidated repaired line, then perform targeted cross-device revalidation for failed/touched/affected families plus unresolved phone 21/affected phone 22 and tablet 18. Preserve unrelated accepted evidence.

## Exact route

1. **Freeze one new monotonic physical-QA candidate** from the consolidated repaired Player line. Do not reuse `preqa.12` identity.
2. Record exact versionName/versionCode, commit, Scaffold run and artifact/digests for that candidate.
3. Perform the already-defined targeted cross-device revalidation only; do not replay complete phone/tablet discovery.
4. Reopen only a relevant bounded repair if targeted physical evidence finds a material defect.
5. Phase 4A closes only after sufficient repaired evidence and explicit owner acceptance.
6. DM implementation remains blocked until that explicit Phase 4A owner closure.

Portrait relocation of long-card action buttons remains only a prior consideration, not an approved automatic change. No P18 exists.
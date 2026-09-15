# Phase 4A — Application Settings T3 / T4 / T9 repair

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Physical baseline:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Product commit:** `1dcd320417e7e3b45ec02ec6aa7cbb616c84e473`  
**Final steady-state HEAD:** `5db7bc3a48f1e640fc80770dd07d68a7ffaa02f7`  
**Status:** T3/T4/T9 IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING

## Scope

This bounded family implements the owner-approved Application Settings semantics without changing the frozen physical candidate:

- **T3:** exact promised column counts are replaced by separate adaptive Portrait/Vertical and Landscape/Horizontal card-distribution density preferences;
- **T4:** the normal text-size scale is symmetric around 100 (`50..150` step 10), with compatible nearest-value migration;
- **T9:** device haptics gain an explicit `None` / `Ninguna` state that actually suppresses app-generated haptic dispatch.

Spacing density is not redesigned by T4. Per-character haptics-enabled behavior remains intact. This repair does not introduce a new physical-QA candidate.

## T3 — adaptive card distribution

The active preference model now includes `CharacterCardDensityV4` with:

- `COMFORTABLE` / `Cómodo`;
- `BALANCED` / `Equilibrado`;
- `COMPACT` / `Compacto`;
- `DENSE` / `Denso`.

Both portrait and landscape default to `BALANCED` for new/unmapped state.

Application Settings now presents only two adaptive selectors, `Vertical` and `Horizontal`, and explicitly states that the app does not promise an exact column count.

The runtime no longer consumes the four legacy exact-count preferences. Effective columns are derived from:

- actual `availableWidthDp`;
- portrait/landscape density choice;
- effective font scale;
- spacing/UI-density pressure;
- density-specific minimum usable card width;
- the consuming screen's safe phone/wide maximum.

The four old exact-count keys remain readable only as compatibility/migration shadows. If no new density key exists, the legacy phone+tablet values map to the closest density intent. The historical defaults map to `BALANCED` in both orientations.

## T4 — symmetric text-size scale

The normal text-size options are now:

`50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150`

100 is the semantic center. Older persisted values are clamped to the supported normal range and mapped to the nearest valid option instead of being reset. Representative guarded mappings include:

- `70 -> 70`;
- `145 -> 140`;
- `160 -> 150`;
- `200 -> 150`;
- values below 50 -> `50`.

Spacing density remains the pre-existing symmetric `50..150` control and is not part of T4.

## T9 — explicit haptics None

`CharacterHapticStrengthV4` now includes `NONE("Ninguna", 0)`.

The shared `rememberCharacterHapticHookV4` dispatch point now enters neither the Android vibrator path nor the fallback `performHapticFeedback` path when strength is `NONE`. This is intentionally a dispatch-level short circuit; a zero amplitude alone would be insufficient because the pre-existing vibration code clamps requested amplitude to at least 1 and can fall back to platform haptic feedback.

Existing users remain compatible because the pre-existing default and missing/legacy fallback remain `MEDIUM`. `None` is therefore opt-in rather than silently disabling existing installations.

## Permanent guard

`scripts/check_player_application_settings_semantics.py` remains in the normal read-only Scaffold and verifies:

- all four adaptive density levels and Balanced defaults;
- new portrait/landscape preference keys;
- legacy exact-count migration mapping;
- absence of the old four exact-count selectors;
- absence of live legacy exact-count consumption in responsive runtime;
- actual-width, orientation, font-scale, spacing-pressure and minimum-card-width inputs;
- symmetric `50..150` text-size options and nearest-value migration;
- unchanged symmetric spacing scale;
- `Ninguna` plus MEDIUM upgrade/default fallback;
- dispatch-level haptic-off short circuit;
- representative migration semantics for both layout density and text scale.

The temporary settings migration helper and the temporary haptic-hook diagnostic were removed. The normal Scaffold is read-only again.

## Automation evidence

Writable bounded validation:

- product commit: `1dcd320417e7e3b45ec02ec6aa7cbb616c84e473`;
- migration/guards/build/commit all succeeded before cleanup.

Authoritative steady-state proof:

- Workflow: `Scaffold checks`
- Run ID: `34796452617`
- Run number: `1583`
- Head: `5db7bc3a48f1e640fc80770dd07d68a7ffaa02f7`
- Conclusion: **SUCCESS**

The clean read-only run passed:

- backend install/type-check;
- compact Player geometry guard;
- reorder stability guard;
- checkbox consistency guard;
- spellcasting-bootstrap guard;
- class-editor-controls guard;
- application-settings semantics guard;
- shared tests;
- Android build;
- Desktop build;
- Android debug APK upload.

Artifact:

- ID `10329772684`;
- name `dnd-custom-aid-debug-apk`;
- size `13,644,492` bytes;
- digest `sha256:74249a197c21572743165927c9330f38ceafd2db7f78267872f36e2edfd2f1af`.

## Physical status

T3/T4/T9 are **not physically PASS**. On the future consolidated candidate, targeted physical revalidation should verify:

1. Vertical and Horizontal adaptive density choices are understandable and persist;
2. representative phone/tablet layouts use width adaptively and do not promise impossible exact counts;
3. larger text pressure safely reduces effective columns rather than clipping cards;
4. text-size choices are symmetric around 100 and an upgraded legacy value maps predictably;
5. `Ninguna` produces no app-generated haptic feedback while a non-none option still does;
6. an existing non-none installation remains non-none after upgrade.

Do not replay unrelated accepted phone/tablet evidence.

## Next bounded family — T7 wide Combat

Proceed to the active `CharacterCombatSuccessorV4.kt` path only. The audited cause remains the one-dimensional full-width phone composition stretching across tablet landscape.

T7 contract:

- preserve narrow-phone behavior;
- keep the operational HUD deliberately bounded/compact on wide layouts;
- use adaptive multi-column/card composition on wide/tablet layouts driven by the repaired T3 preference;
- reuse the stabilized Round 2 spatial reorder foundation for wide multi-column reorder rather than introducing another drag engine;
- preserve structural edit/favorite/delete semantics and persisted ordering;
- add a focused T7 guard/tests and run the normal read-only Scaffold before checkpointing.

Phase 4A remains OPEN. The frozen physical baseline remains `preqa.12`. DM implementation remains blocked until explicit owner closure.

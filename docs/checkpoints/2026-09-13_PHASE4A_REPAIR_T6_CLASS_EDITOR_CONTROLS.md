# Phase 4A — T6 class-editor numeric / hit-die controls

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Physical baseline:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**T6 product commit:** `c98121e50f347f64898c9e31d077e53cad31685f`  
**Final steady-state HEAD:** `6ba73b22b07a4c5a92d69425a7372d2695fbc30a`  
**Status:** T6 IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING

## Scope

This bounded repair addresses only the audited T6 class-editor control family in `CharacterClassIdentitySuccessorV4.kt`:

- `Nivel` uses numeric keyboard behavior;
- `DG restantes` uses numeric keyboard behavior;
- `Dado` is no longer an unrestricted freeform numeric control;
- standard hit dice are selectable as `d4`, `d6`, `d8`, `d10`, `d12`, `d20`;
- `Otro…` exposes a numeric custom-sides field;
- catalog-driven official hit-die preselection is preserved;
- existing nonstandard/custom hit-die values remain representable and are not destructively coerced.

No class schema, class/subclass identity model, storage format, class-rules validation or class-builder redesign was introduced.

## Implementation

The active class editor now reuses the existing compact control language. `CompactLabeledNumberInputSuccessorV4` supplies `KeyboardOptions(keyboardType = KeyboardType.Number)`, so the two audited numeric fields and custom die sides request a numeric keypad.

`CompactClassHitDieSelectorV4` presents the standard die set. Standard values display directly as `dX`; a null/nonstandard numeric value is represented as `Otro…` and retains the existing value in `Caras del dado`. Selecting `Otro…` from a standard value clears only the draft hit-die sides so the custom field can be entered explicitly.

Known catalog class selection continues to use the existing `officialHitDieV4` / catalog-preselection path. The persisted field remains the pre-existing `hitDieSides` string; no migration is required.

## Permanent guard

`scripts/check_player_class_editor_controls.py` remains in the normal Scaffold and verifies:

- numeric keyboard imports/wiring;
- standard die set `4, 6, 8, 10, 12, 20`;
- compact hit-die selector presence;
- `Otro…` custom option;
- `Caras del dado` custom numeric field;
- explicit null/nonstandard preservation logic;
- catalog hit-die preselection preservation;
- absence of the legacy freeform `Dado` field.

The temporary migration helper and CI source-writing step were removed after the verified product commit. The normal Scaffold is read-only again.

## Automation evidence

The bounded migration validation run `34795076069` / run `1569` was green and committed the verified product source.

The authoritative steady-state proof is:

- Workflow: `Scaffold checks`
- Run ID: `34795355116`
- Run number: `1571`
- Head: `6ba73b22b07a4c5a92d69425a7372d2695fbc30a`
- Conclusion: **SUCCESS**

It passed:

- backend install/type-check;
- compact Player geometry guard;
- Player reorder stability guard;
- Player checkbox consistency guard;
- Player spellcasting-bootstrap guard;
- Player class-editor-controls guard;
- shared tests;
- Android build;
- Desktop build;
- Android debug APK upload.

Artifact:

- ID `10329671667`
- name `dnd-custom-aid-debug-apk`
- size `13,645,969` bytes
- GitHub Actions digest `sha256:6391210567ff4964b7077e1cc6e9c3c38bd862b62aba19a01e929ef6bfe4db4d`

## Physical status

T6 is **not physically PASS**. The future consolidated candidate should receive targeted checks for:

1. `Nivel` opens the expected numeric keyboard and saves correctly;
2. `DG restantes` opens the expected numeric keyboard and remains bounded by level behavior;
3. a catalog class preselects its official hit die;
4. each standard die selector choice saves correctly;
5. `Otro…` exposes numeric custom sides and preserves a representative nonstandard value;
6. save / leave / reopen preserves the selected or custom hit die.

No unrelated phone/tablet discovery needs to be replayed.

## Next bounded family

Proceed to the already-approved Application Settings repair:

- **T3:** Portrait/Landscape adaptive card-distribution density semantics (`Comfortable / Balanced / Compact / Dense`) with compatibility mapping from legacy exact-count preferences and runtime-safe effective columns;
- **T4:** text-size options symmetric around 100, preferred normal range `50..150` step 10; spacing density is not part of T4;
- **T9:** explicit haptics `None`, with existing non-none users preserved across upgrade.

Phase 4A remains OPEN. The frozen physical candidate remains `preqa.12`; no new physical-QA candidate is created by this checkpoint. DM implementation remains blocked pending explicit Phase 4A owner closure.

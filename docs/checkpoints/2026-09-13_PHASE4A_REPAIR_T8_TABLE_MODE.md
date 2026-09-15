# Phase 4A — T8 Table Mode structural-affordance repair

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Physical baseline:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Product commit:** `2734d08a72e183ca213cd9213ff47a4db588cbf6`  
**Final steady-state HEAD:** `d6c13819a49e1cb7c71dffcad98b53c250d4d9d4`  
**Status:** T8 IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING

## Scope

T8 repairs the Table Mode affordance defect observed during tablet P17 check 15 without changing the frozen physical candidate.

The shared `CharacterTableModePolicy` already defined the intended product contract before this repair:

- structural character/configuration editing is disabled in Table Mode;
- genuine operational/session state remains usable.

The defect was therefore not missing persistence protection. Structural callbacks were already rejected at the persistence boundary, but several controls could still appear editable, open editors or accept gestures that produced no durable structural change. That created misleading UI affordances. The audit also found the inverse problem in Overview: some genuine operational controls were routed through the structural no-op path.

T8 aligns the visible interaction surface with the existing shared policy.

## Implemented behavior

### Overview

`OverviewTabV4` now receives and propagates `structuralEditingEnabled` explicitly.

In Table Mode:

- character name is presented read-only rather than as an editable text field;
- class Add/Edit/Delete affordances are hidden and class editor/delete dialogs cannot open;
- ability-score controls are read-only;
- structural Combat references are presented read-only;
- structural general-reference editors remain visible as information but cannot open or mutate;
- portrait/token structural actions are hidden;
- defense, sense and special-movement Add/Edit/Delete paths are gated.

The repair deliberately preserves operational play state:

- Overview uses `CharacterCombatOperationalCardV4` for live HP behavior when structural editing is disabled;
- operational HP persists through `onOperationalSheetChange` rather than the structural draft path;
- inspiration remains operational;
- resource current values remain operational.

### Skills

The Skills tab already received `structuralEditingEnabled`, but several child controls did not honor it. T8 propagates the flag through the built-in ability/save/skill surfaces.

In Table Mode:

- the presentation-only Skills layout selector remains usable;
- ability scores are read-only;
- saving-throw adjustment dialogs cannot open;
- saving-throw proficiency toggles are disabled;
- standard skill adjustment dialogs cannot open;
- standard skill training selectors/dropdowns are disabled;
- derived totals remain visible.

Custom projected skills were already read-only and remain so.

### Class identity and general references

`CharacterClassIdentityV4.kt` now forwards the structural flag to the successor implementation. `CharacterClassIdentitySuccessorV4.kt` gates class Add/Edit/Delete and associated dialogs.

`CharacterGeneralClosureV4.kt` propagates the same policy through portrait/token and structural reference cards so defenses, senses and special movement do not expose misleading editing paths in Table Mode.

### Already-correct surfaces

The audit confirmed that the specialized collection modules already derived or received the Table Mode structural policy and suppressed their structural Add/Edit/Delete/reorder affordances. PC Settings was also already substantially correct: structural configuration controls are disabled while legitimate non-structural settings/actions remain available. Those paths were not churned merely for consistency.

## Scope purity

Verified product commit `2734d08a72e183ca213cd9213ff47a4db588cbf6` changed exactly four Android product files:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt`;
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterClassIdentityV4.kt`;
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterClassIdentitySuccessorV4.kt`;
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterGeneralClosureV4.kt`.

No storage/schema/import/export model changed.

Temporary bounded migration writers were used only during auditable CI transformation and were deleted before the authoritative steady-state run. Scaffold permissions were restored from `contents: write` to `contents: read`, and the migration/auto-commit steps were removed.

## Permanent guard

`scripts/check_player_table_mode_affordances.py` remains in normal Scaffold and verifies:

- Overview receives the Table Mode structural flag;
- character identity, abilities and class identity honor the flag;
- Table Mode Overview routes live HP through the operational Combat surface;
- inspiration and current resource values remain operational;
- Skills keeps its presentation selector while gating ability/save/skill structural editors and selectors;
- class Add/Edit/Delete and dialogs are gated;
- portrait/token and defense/sense/movement Add/Edit/Delete/editor paths are gated;
- shared `CharacterTableModePolicyTest` continues proving that operational current values merge while structural name/AC/configuration changes are rejected.

## Automation evidence

### Writable bounded validation

Workflow `Scaffold checks`, run `34799065695` / #1607 — **SUCCESS**.

The corrected full-scope migration passed:

- bounded T8 migration;
- all pre-existing permanent Player guards;
- the new T8 Table Mode affordance guard;
- backend type-check;
- shared tests;
- Android build;
- Desktop build;
- Android debug APK upload;
- verified product commit.

This run produced product commit `2734d08a72e183ca213cd9213ff47a4db588cbf6`.

Earlier failed T8 validation attempts are not counted as evidence; they landed no product code and were used only to correct compile compatibility and make migration anchors unambiguous.

### Authoritative clean steady-state proof

- Workflow: `Scaffold checks`
- Run ID: `34799667822`
- Run number: `1611`
- Head: `d6c13819a49e1cb7c71dffcad98b53c250d4d9d4`
- Conclusion: **SUCCESS**

The final tree had all temporary T8 migration writers removed and normal Scaffold restored to read-only operation. It passed:

- backend install/type-check;
- compact Player geometry guard;
- reorder stability guard;
- checkbox consistency guard;
- spellcasting-bootstrap guard;
- class-editor-controls guard;
- application-settings semantics guard;
- wide-Combat composition guard;
- Table Mode affordance guard;
- shared tests;
- Android build;
- Desktop build;
- Android debug APK upload.

Artifact:

- ID `10330822191`;
- name `dnd-custom-aid-debug-apk`;
- size `13,662,998` bytes;
- digest `sha256:bf90762e36177735bd948524c37552b143ec1bbf1ddd7bdb292511b7a7255715`.

## Physical status

T8 is **not physically PASS**. On the future consolidated candidate, targeted revalidation should verify at minimum:

1. tablet Table Mode no longer exposes active-looking structural name/class/ability/reference editors that silently fail to persist;
2. class Add/Edit/Delete cannot be opened while Table Mode is active;
3. portrait/token, defense, sense and special-movement structural actions are absent/disabled as intended;
4. Skills keeps its layout/presentation selector usable while ability scores, save proficiency/adjustments and standard skill training/adjustments are read-only/non-opening;
5. live HP remains usable and persists;
6. inspiration and current resource values remain usable and persist;
7. leaving/reopening confirms structural values were not changed and operational values were retained;
8. one representative phone sanity check confirms Table Mode behavior is coherent on the narrow layout.

Do not replay the full historical phone/tablet suites.

## Next bounded family — remaining T2 integration

T8 closes the last non-T2 material repair family from the post-P17 plan. The next bounded implementation family is the remaining T2 dice-result / Custom Throw / Dice display-mode integration:

- die-specific result silhouettes;
- Custom Throw die/custom-sides/signed-modifier UX;
- Dice-tab ownership of display mode.

Round 1 already repaired shared structured `NdS±M` parsing/rolling and direct sign handling; T2 work must build on that foundation rather than duplicate it.

The optional phone-16 compact-field refinement remains optional and should be attempted only if a safe material visual benefit is demonstrated.

Phase 4A remains OPEN. The frozen physical baseline remains `preqa.12`. DM implementation remains blocked until explicit Phase 4A owner closure.

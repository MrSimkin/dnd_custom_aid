# Phase 4A — T2 dice-result / Custom Throw / display-mode integration

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Physical baseline:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Product commit:** `8131dd13f4373148503b854b1448f9d081556847`  
**Permanent-guard wiring:** `8a081dd17c75cf800a9c792d7c008ec4a95b9161`  
**Final steady-state HEAD:** `4d09e9eca648e5ca82af896dab8d16b986faae5b`  
**Status:** T2 IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING

## Scope

T2 completes the remaining material dice integration identified by the post-P17 cross-device audit. Round 1 had already established the shared structured `NdS±M` parser/roller/sign foundation; this repair deliberately builds on that foundation rather than introducing a second dice-expression model.

The repair addresses three residual defects:

1. visible dice results used generic presentation rather than die-specific identity;
2. `Personalizada` / Custom Throw was effectively d20-oriented and only customized the modifier;
3. dice-result presentation mode was configured in global Application Settings rather than on the Dice surface that consumes it.

## Implemented behavior

### Die-specific result presentation

`CharacterDiceRollSuccessorV4.kt` now provides a reusable `CharacterDieResultVisualV4` presentation.

For known standard dice it uses distinct silhouettes for:

- d4;
- d6;
- d8;
- d10;
- d12;
- d20.

Unsupported/custom die identities and flat aggregate values fall back to a circle rather than pretending to be one of the standard polyhedra.

The visual language is used for:

- visible ordinary d20 results;
- visible Custom Throw results;
- damage-result components when die identity is known;
- flat numeric damage components, using the neutral circular fallback.

Textual damage breakdown remains present; the visual layer supplements rather than replaces the arithmetic evidence.

### Custom Throw

`Personalizada` now has independent custom-roll state rather than being a modifier-only d20 shortcut.

It supports:

- standard die selection: d4 / d6 / d8 / d10 / d12 / d20;
- `Otro…` with custom side count;
- bounded custom sides `2..1000`, matching the shared structured-dice semantics;
- explicit signed modifier control with direct `+` / `−` toggle;
- actual rolling through the shared arbitrary-die resolver.

Normal skill/save/attack/etc. checks continue to use the existing d20 Normal/Ventaja/Desventaja path. Custom Throw does not silently change ordinary d20 semantics.

### Shared arbitrary-die resolver

`CharacterCombatDiceOperations.kt` now exposes `resolveCharacterDiceExpressionRoll(...)` and `CharacterResolvedDiceExpressionRoll` for a parsed `CharacterDiceExpression`.

The resolver:

- rolls exactly the expression's quantity and sides;
- validates each returned result against `1..sides`;
- exposes dice subtotal and modifier-adjusted total.

Existing damage rolling now reuses this same primitive for DICE components. A focused common test covers an arbitrary signed expression (`1d12-2`) and verifies side dispatch, raw result and final total.

### Dice-tab ownership of result presentation mode

The existing persisted `dice_result_mode` preference was preserved. There is no preference migration and existing user choice is not reset.

What changed is UI ownership:

- the active selector was removed from Application Settings;
- `CharacterEditorV4` passes its existing `UiPreferences` / `onPreferencesChange` seam into the successor Dice tab;
- the Dice tab now exposes the result-mode selector directly where the presentation is consumed.

This is a presentation/ownership repair, not a storage redesign.

## Scope purity

Verified product commit `8131dd13f4373148503b854b1448f9d081556847` changed exactly six intended files:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterDiceRollSuccessorV4.kt`;
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt`;
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt`;
- `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterCombatDiceOperations.kt`;
- `shared/src/commonTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterCombatDiceOperationsTest.kt`;
- `scripts/check_player_dice_t2.py`.

No storage/schema/import/export model changed.

`CharacterDiceRollTabV4.kt` was intentionally not churned: the active successor runtime is wired through `CharacterDiceRollSuccessorV4.kt` from `CharacterEditorV4.kt`.

## Permanent guard

`scripts/check_player_dice_t2.py` remains in normal Scaffold and verifies at minimum:

- the standard d4/d6/d8/d10/d12/d20 set;
- `Otro…` custom-die path and bounded custom-side entry;
- signed-modifier editor;
- shared arbitrary-die resolver usage;
- die-specific result visual / silhouette helper;
- damage-result visual integration;
- Dice-tab result-mode selector and preference mutation;
- removal of the active `Resultados de dados` selector from Application Settings;
- preservation of the existing `dice_result_mode` persistence key;
- editor-to-Dice preference wiring;
- damage rolling reuse of the shared arbitrary-die resolver.

## Automation evidence

### Bounded product transformation

Temporary migration workflow run `34800943893` completed successfully and produced product commit `8131dd13f4373148503b854b1448f9d081556847` after strict source-anchor and `git diff --check` validation.

Earlier failed migration attempts landed no product code. They were used only to correct an ambiguous duplicate replacement and to separate product commits from workflow-file edits, which GitHub correctly rejects without workflow permission.

### Integrated validation before cleanup

Normal read-only `Scaffold checks` run `34800980682` / #1622 on HEAD `8a081dd17c75cf800a9c792d7c008ec4a95b9161` — **SUCCESS**.

It passed:

- backend install/type-check;
- every pre-existing permanent Player guard;
- the new T2 dice guard;
- shared tests;
- Android build;
- Desktop build;
- Android debug APK upload.

Artifact `10331097676`, size `13,693,976` bytes, digest `sha256:3e08566915d543f183c462e2d54a143071fdeee4f3519a7cc997b59193093deb`.

### Authoritative clean steady-state / aggregate proof

Temporary T2 migration machinery was then removed completely:

- `.github/workflows/phase4a-t2-migration.yml`;
- `scripts/migrate_phase4a_t2.py`;
- `scripts/migrate_phase4a_t2_retry.py`.

Normal Scaffold remains `permissions: contents: read`; the permanent T2 guard remains active.

Authoritative run:

- Workflow: `Scaffold checks`
- Run ID: `34801201294`
- Run number: `1625`
- Head: `4d09e9eca648e5ca82af896dab8d16b986faae5b`
- Conclusion: **SUCCESS**

The final migration-free tree passed:

- backend install/type-check;
- compact Player geometry guard;
- reorder stability guard;
- checkbox consistency guard;
- spellcasting-bootstrap guard;
- class-editor-controls guard;
- application-settings semantics guard;
- wide-Combat composition guard;
- Table Mode affordance guard;
- T2 dice presentation / Custom Throw guard;
- shared tests;
- Android build;
- Desktop build;
- Android debug APK upload.

Because this is the normal Scaffold after all material repair families were implemented and cleanup was complete, run #1625 also serves as the aggregate automation proof for the consolidated repair set.

Artifact:

- ID `10331846822`;
- name `dnd-custom-aid-debug-apk`;
- size `13,693,976` bytes;
- digest `sha256:4ceeba64f31ab3ee4436f2ee1cc71ce324154d84cb99a551cbb1ef3ba28cb7f0`.

## Optional phone-16 refinement decision

Phone discovery check 16 was already PASS. The compact-field density idea was explicitly optional and conditional on a demonstrated safe/material visual benefit.

No new evidence in this repair cycle establishes such a benefit. Therefore the optional phone-16 refinement is **not taken** before the next candidate. This avoids introducing unneeded churn into an already accepted surface.

This is not a waiver of a defect; check 16 had no blocking defect to repair.

## Physical status

T2 is **not physically PASS**. On the future consolidated candidate, targeted revalidation should verify at minimum:

1. ordinary d20 visible-result presentation remains coherent;
2. representative standard Custom Throw dice (including a non-d20 die) select and roll correctly;
3. `Otro…` accepts a representative valid custom side count and rejects/out-disables invalid bounds;
4. positive and negative Custom Throw modifiers are directly controllable and included in totals;
5. visible results distinguish standard die identities and use a neutral circular fallback for custom/flat values as intended;
6. representative damage rolls show correct die identities without losing the textual arithmetic breakdown;
7. result display mode is controlled from the Dice tab rather than Application Settings;
8. the pre-existing display preference survives leave/reopen and upgrade/persistence sanity;
9. one representative phone and tablet/wide sanity check covers the touched Dice surface without replaying the complete historical suites.

Round 1 phone checks 7–9 remain part of the same future targeted dice-family revalidation; accepted unrelated phone/tablet evidence remains preserved.

## Consolidated repair status / next gate

With T2 complete, all material post-P17 repair families identified for this cycle are implemented and automation green:

- Round 1 structured dice;
- Round 2 / T1 reorder stability;
- Round 3 checkbox/responsive grouping;
- T5 spell-source/bootstrap;
- T6 class-editor controls;
- T3/T4/T9 Application Settings semantics;
- T7 wide Combat;
- T8 Table Mode;
- T2 dice presentation / Custom Throw / display-mode ownership.

The optional phone-16 refinement is deliberately not taken. Aggregate Scaffold #1625 is green on the migration-free steady-state tree.

**Next gate:** freeze one new monotonic physical-QA candidate from the consolidated repaired line, then perform the already-defined targeted cross-device revalidation only. Do not replay complete phone/tablet discovery.

Phase 4A remains OPEN. The frozen physical baseline remains `preqa.12` until that new candidate is explicitly versioned/frozen. DM implementation remains blocked until explicit Phase 4A owner closure.
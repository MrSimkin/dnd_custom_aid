# Phase 4A — post-P17 cross-device source audit and consolidated repair plan

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Audit baseline HEAD:** `ce1d451b9d06c1c56d0ef90fe6c85857aedd740a`  
**Physical candidate audited:** `0.4.0-preqa.12 / 41200`  
**Exact candidate commit:** `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Status:** P17 DISCOVERY COMPLETE / CROSS-DEVICE SOURCE AUDIT COMPLETE / REPAIR PLAN READY / PRODUCT CODE NOT YET MODIFIED

## 1. Gate status

P17 physical tablet discovery is complete on exact `preqa.12 / 41200`. Existing physical PASS evidence is preserved; the project must not replay the complete phone or tablet suites from zero.

Phase 4A remains **OPEN** because phone/tablet findings require one consolidated repair cycle plus targeted revalidation. DM implementation remains blocked until explicit owner Phase 4A closure. No P18 exists.

This audit is a source/root-cause pass over the latest phone findings, P17 findings T1–T8, and accepted bounded refinements. It did not modify product source, version, APK or candidate identity.

## 2. Executive result — repair families

The physical findings collapse into the following source-level repair families where evidence supports a shared cause:

1. **Structured dice / dice-result family** — phone 7–9 + T2 + a latent shared parser gap.
2. **Reorder interaction family** — T1 across one-column and spatial/multi-column reorder.
3. **Checkbox + responsive toggle grouping family** — phone 17.1–17.3 and tablet reproduction of 17.1.
4. **Spell-source/bootstrap family** — T5 and blocked tablet Conjuros check 18.
5. **Class editor numeric/die-control family** — T6.
6. **Wide Combat composition family** — T7, with implementation dependence on stabilized reorder/adaptive layout primitives.
7. **Table Mode structural-affordance family** — T8.
8. **Application Settings semantics family** — T3/T4.
9. **Optional compact-field density refinement** — phone 16 only; non-blocking and lower priority.

The families above should be implemented in dependency-aware order rather than as unrelated screen patches.

## 3. Structured dice / dice-result family — confirmed root causes

### Phone checks 7–8: serialization defect

In `CharacterCombatSuccessorV4.kt`, the structured damage editor parses quantity, die sides and an optional signed modifier, but `buildDiceComponentExpressionV4` concatenates the modifier draft directly after the sides.

A user-entered positive modifier such as `2` therefore serializes as:

`1d8` + `2` → `1d82`

The next parse legitimately reads `82` as die sides. That directly explains both physical symptoms:

- selecting a standard die and entering a positive modifier can change the selector to `Otro…`;
- under `Otro…`, modifier digits appear to migrate into `Caras del dado`.

This is one defect, not two independent field-routing bugs.

### Additional shared parser gap found by source audit

`shared/.../CharacterCombatDiceOperations.kt` currently parses only bare `NdS` expressions. It does not represent/apply an optional modifier, even though the Combat editor can store expressions such as `1d8+2`.

If only the editor serialization were repaired, damage rolling with a die modifier would remain semantically incomplete. This shared parser/result contract must be repaired in the same family.

### T2 current architecture

`CharacterDiceRollSuccessorV4.kt` currently renders a generic Material result shape rather than a die-specific silhouette. Custom Throw remains effectively d20-oriented and customizes the modifier rather than die type. Dice-result display mode is stored as a global application preference instead of being controlled from the Dice tab.

### Repair contract

- Keep quantity, sides and modifier as independent structured draft fields while editing.
- Serialize a positive modifier with explicit `+`; preserve negative sign and incomplete-draft safety.
- Change the sign control to direct `+`↔`−` tap-toggle while preserving a compact visible surface and >=48dp interaction envelope.
- Extend the shared dice expression/result model to carry an optional signed modifier and include it in totals.
- Add focused tests for at least `1d8+2`, `2d6-1`, bare dice and invalid/incomplete drafts.
- Introduce one reusable die-result visual primitive whose border/silhouette reflects standard die type (d4/d6/d8/d10/d12/d20). `Otro…`, aggregate-only and flat-result presentation use a circle.
- Apply that primitive to normal Dice throws and damage results where die identity is known.
- Custom Throw must allow standard die selection, `Otro…` custom sides and signed modifier entry.
- Move the dice-display-mode control from Application Settings to the Dice tab. Persistence may remain a device preference; only the ownership/presentation of the setting moves.

## 4. T1 reorder family — confirmed live-geometry feedback loop

The audit examined both reorder engines:

- `CharacterReorderSessionV4.kt` for one-dimensional/one-column reorder;
- `CharacterSpatialReorderV4.kt` plus `CharacterReorderInteractionV4.kt` / `CharacterReorderOverlayV4.kt` for spatial/multi-column reorder.

Both engines retarget from current rendered bounds while a drag is active, including from `registerBounds()`. The preview reorder itself moves/animates neighboring items, which changes those same bounds. Those moving preview bounds are then fed back into target selection.

That creates the physical T1 loop: neighbors jump/repack, insertion boundaries move while the user is dragging, and the pointer effectively chases a moving layout.

### Repair contract

- Do not retarget merely because preview animation/recomposition reports new bounds.
- Retarget primarily from pointer motion and explicit auto-scroll events.
- Use stable slot/target geometry for the active drag, with hysteresis/deadband where appropriate, rather than continuously chasing animated item bounds.
- Keep the dragged overlay anchored predictably to the finger.
- Re-evaluate after real viewport scroll, not every animation frame.
- Preserve semantic reorder persistence and existing one-column + spatial behavior.
- Add focused tests for target stability, one-column crossing and spatial/multi-column crossing.

This is one shared interaction-family repair even though the engines have separate implementations.

## 5. Checkbox + responsive toggle grouping — confirmed systemic family

There is no shared compact Player checkbox-row primitive comparable to the compact text-field primitives. Active UI uses raw Material `Checkbox` with locally composed Rows/Text, producing the physical inconsistency in control scale, typography, gap, padding and grouping.

Confirmed active raw Checkbox call-sites were found in at least these six files:

- `CharacterEquipmentClosureV4.kt` — Equipado / Especial / Sintonizado variants;
- `CharacterSpellListClosureV4.kt` — selected/prepared, per-source include/prepared, V/S/M, Concentración/Ritual;
- `CharacterCompanionsModuleV4.kt` — Vinculado;
- `CharacterClassOptionModulesV4.kt` — Humano variante;
- `CharacterArtificeModuleV4.kt` — Atado;
- `CharacterManagementSuccessorV4.kt` — Aplicar descanso largo.

The audit confirmed at least 18 active raw `Checkbox` call-sites across those files. Because authoritative branch-wide code search is not available through the connector and the private repository could not be cloned anonymously, implementation must include a branch-local scan/guard to prove that no active raw Material `Checkbox` sites remain unintentionally.

`Switch` controls used for settings are a related toggle family but are not automatically defective and should not be replaced without evidence.

### Phone 17.2 / 17.3 exact spell layout causes

`CharacterSpellListClosureV4.kt` currently renders every spell source as its own full-width Row containing source/prepared controls. It also hardcodes V/S/M in one Row and Concentración/Ritual in a separate Row. These rigid rows explain the wasted width and unnecessary wrapping observed physically.

### Repair contract

- Create a reusable compact checkbox/toggle-row primitive with:
  - consistent visible control scale;
  - >=48dp interaction envelope;
  - consistent label typography and label gap;
  - consistent inner/outer spacing;
  - explicit enabled/read-only behavior;
  - whole-row interaction where appropriate.
- Migrate all confirmed Player raw Checkbox sites.
- Add a durable Android Player source guard against raw Material `Checkbox`, with only explicit documented exceptions.
- Build a responsive checkbox/toggle group using FlowRow or equivalent packing:
  - items share one row whenever they actually fit;
  - wrap only when required;
  - source/prepared pairs remain semantically grouped;
  - landscape/tablet can place multiple source groups per row;
  - V/S/M and Concentración/Ritual no longer use rigid two-row grouping when one row fits.

## 6. T5 spell-source/bootstrap family — confirmed architecture mismatch

### Current Conjuros model

`CharacterSheet` persists `spellcastingSources`, `spellcastingProfiles` and `spellSourceAssociations`. `CharacterSpellsTabV4` exposes only stored spellcasting sources, and `CharacterSpellSourceEditorV4` requires explicit source management. A newly configured Mago can therefore have a valid canonical class but zero spell sources. Spell save then has no valid source association and the user cannot add the spell through the intended flow.

`CharacterEditorV4` also keeps `spellcasterEnabled` as a separate setting rather than deriving/suggesting Conjuros from known spellcasting class metadata. `CharacterClassCatalog.kt` currently does not contain spellcasting metadata sufficient to bootstrap that relationship.

### Rasgos comparison

Rasgos provenance derives valid source/origin options dynamically from canonical character entities such as class/subclass/race/background and stores the relationship. It does not require the user to manually create a duplicate source record merely to make that canonical owner exist.

That confirms the owner’s T5 requirement as an architectural mismatch: spell source **availability/ownership** should behave analogously to Rasgos provenance, while spellcasting-specific configuration still needs its own profile data.

### Compatibility-safe repair contract

- Canonical character origins become authoritative for source availability, analogous to Rasgos.
- Preserve `CharacterSpellcastingSource`/profile data as the spellcasting configuration overlay because ability/DC/attack settings are richer than simple provenance.
- For a canonical class, automatically ensure/reconcile a linked spellcasting source/profile rather than forcing the user to manually create a duplicate owner.
- Preserve existing source IDs, associations, storage/import/export and non-destructive update/delete semantics.
- Reconcile existing source overlays by canonical link (such as `linkedClassId`) rather than destructive migration.
- Retain manual `Other` sources for homebrew/noncanonical spellcasting.
- Add bounded class-catalog metadata for known spellcasting classes sufficient to suggest/enable Conjuros and preselect the appropriate spellcasting ability where known.
- Do not expand this repair into a new spell-legality or subclass-progression engine.
- When a known spellcasting class such as Mago is added/configured, Conjuros should become available and a valid source should exist without separate manual source creation.

After this repair, P17 check 18 (Conjuros sticky behavior) becomes physically testable rather than blocked by T5.

## 7. T6 class-editor controls — confirmed legacy input path

`CharacterClassIdentitySuccessorV4.kt` uses compact text-field wrappers for `Nivel`, `DG restantes` and `Dado`, but the numeric wrappers do not request a numeric keyboard. `Dado` remains free numeric text with a `d` prefix.

The class catalog already knows official hit dice and preloads known `hitDieSides`; the data foundation for standard-die preselection already exists.

### Repair contract

- `Nivel` and `DG restantes`: numeric keypad / numeric keyboard options.
- `Dado`: standard die selector (d4/d6/d8/d10/d12/d20) plus `Otro…`.
- `Otro…`: numeric custom sides field using numeric keypad.
- Preserve/preselect catalog hit die where known.
- Reuse the newer compact control language; do not introduce another one-off selector style.

## 8. T7 wide Combat composition — confirmed active-screen cause

The active Combat screen is `CharacterCombatSuccessorV4.kt`. Its attack/action cards are rendered one-dimensionally with `fillMaxWidth()`, content/actions spanning the full available width and no adaptive multi-column layout. That directly explains the tablet-landscape evidence: the phone composition stretches across the tablet instead of using width meaningfully.

An older/different Combat implementation contains column logic, but it is not the active successor screen and must not be repaired accidentally instead of the live path.

### Repair contract

- Keep the operational HUD compact and deliberately bounded on wide/tablet layouts.
- On wide/tablet layouts, use adaptive attack/action card composition (multi-column or deliberate max-width/grid strategy) rather than full-width stretched cards.
- Preserve narrow-phone behavior.
- Honor the redesigned responsive-card preference from T3 where relevant.
- If spatial reorder is used for the new wide layout, land/stabilize the T1 interaction repair first.

## 9. T8 Table Mode — intended contract confirmed

Shared `CharacterTableModePolicy` explicitly defines:

- structural character/configuration editing disabled in Table Mode;
- operational/session actions remain enabled.

`CharacterEditorV4` enforces this partly by making structural draft updates no-op when structural editing is disabled. The physical bug occurs because normal structural Edit/Add/Delete/reorder affordances can still be shown/opened, so the user enters apparently editable controls whose changes cannot take effect.

### Repair contract

- Hide or clearly disable structural Edit/Add/Delete/reorder affordances while Table Mode is active.
- Structural values may remain visible but must present as read-only/non-interactive rather than open no-op editors.
- Keep genuine operational/session controls enabled, including HP and mutable current/spent resource state where policy allows it.
- Audit each major tab boundary for whether `structuralEditingEnabled` is correctly propagated and honored.
- Keep/extend shared policy tests and add UI-level focused tests around structural affordance visibility/enabled state.

T8 is therefore an implementation/affordance bug, not an unresolved product-design choice.

## 10. T3/T4 Application Settings semantics

### T3 — Columns control is semantically misleading

`UiPreferences` stores four apparently exact requested column counts (phone portrait/landscape and wide portrait/landscape), each allowing 1–6. `CharacterResponsivePreferencesV4.resolveCharacterColumnsV4()` then clamps those values differently by form factor and text scale (for example, phone maximum 2 and wide maximum 4). Some screens, including the active Combat successor, do not currently consume the preference at all.

Therefore the setting can visibly promise 5/6 columns while the app correctly renders fewer, and different screens interpret/ignore the value differently. T3 is not a label-only problem.

### Recommended product decision for T3

**Recommended:** replace the four exact-count selectors with one adaptive **card distribution / density** preference. The app computes effective columns from available width, text scale and a target minimum card width / density policy. The setting communicates a preference, not a guaranteed exact count.

This better matches the adaptive work already present and avoids contradictory “5 columns selected / 3 rendered” states.

**Lower-risk fallback:** retain context-specific selectors but rename them to **maximum columns**, constrain their selectable ranges to real maxima and clearly explain that text scale/available width can reduce the effective count. This preserves more existing preference structure but remains more complex and less intuitive.

This is the only repair family in this plan that should receive an explicit owner product-choice confirmation before implementation because it intentionally changes the meaning/shape of a user preference rather than simply restoring an already-defined contract.

### T4 — 100% center / symmetry

The current text-size options are asymmetric around 100 (`70…200`), while spacing density is already symmetric (`50…150`). Therefore T4 is specifically the text-size scale.

Recommended repair:

- make the main text-size selector symmetric around 100, preferably `50,60,70,80,90,100,110,120,130,140,150`;
- 100 is the visual/semantic center;
- migrate existing persisted values by clamping/nearest valid option rather than resetting unexpectedly;
- if >150 accessibility support is retained later, expose it through a deliberately separate/advanced path rather than making the normal control asymmetric again.

## 11. Optional phone-16 field-density refinement

`CharacterCompactControlPrimitivesV4.kt` currently uses very small internal vertical padding while preserving the interaction envelope. Physical phone QA rated the geometry good and asked only whether it could be slightly tighter.

This is not a blocking defect. Because the primitive affects roughly the entire compact-field family, implementation should change it only if preview/screenshot comparison shows a material benefit without clipping/readability regression. Do not let this cosmetic refinement increase the risk of the functional repair cycle.

## 12. Dependency-aware implementation order

Recommended order:

1. Shared dice expression/parser + structured editor serialization/sign control.
2. Reorder target-stability foundation (one-dimensional and spatial).
3. Shared compact checkbox/toggle primitive + full migration + guard.
4. Responsive toggle grouping in Conjuros.
5. Spell-source/bootstrap compatibility repair.
6. Class-editor numeric/die controls.
7. T3/T4 settings redesign after owner confirms T3 semantics.
8. Wide Combat adaptive composition using stabilized reorder/layout primitives.
9. Table Mode structural-affordance enforcement.
10. Die-shape visual/custom-throw UX integration if not already completed with step 1.
11. Optional phone-16 compact-field padding refinement only if still desired/safe.

Focused tests should land with each family. After all product repairs, run the normal aggregate Scaffold gate and create a new monotonic QA identity (expected next identity should be chosen from current version history at implementation time, not assumed blindly here).

## 13. Durable automated verification to add/extend

The consolidated repair should add or extend focused coverage for:

- structured dice positive/negative modifiers and result totals;
- incomplete structured-dice drafts;
- direct sign toggle;
- one-column and spatial reorder target stability;
- no unapproved raw Material `Checkbox` remaining in Android Player source;
- responsive toggle packing behavior;
- spell-source bootstrap/reconciliation while preserving legacy IDs/associations;
- numeric keyboard/die selector behavior where testable;
- effective responsive-card calculation under text scaling;
- Table Mode structural affordances vs operational actions.

Existing storage/import/export/migration behavior must remain preserved unless this plan explicitly requires a compatibility-safe extension.

## 14. Targeted physical revalidation after repaired candidate

Do **not** rerun the full historical phone 23-check suite or the full tablet 18-check discovery suite.

### Phone targeted revalidation

- phone 7–8 structured damage die/modifier behavior;
- phone 9 direct sign toggle;
- phone 17.1 checkbox/control family on representative Equipment + another migrated surface;
- phone 17.2/17.3 Conjuros responsive grouping in portrait/landscape;
- phone 21 settings persistence, previously unassessed;
- affected slice of phone 22 only (rotation/landscape behavior touched by repairs), not a generic full sweep;
- T1 representative reorder;
- T2 custom die/modifier + visible die/damage-result presentation;
- T5 new Mago -> Conjuros available -> source present -> add/save/reopen spell;
- T6 class editor numeric keyboard + standard/Other hit die;
- T8 Table Mode structural affordance if shared phone path;
- phone 16 only if compact-field padding is actually changed.

### Tablet targeted revalidation

- T1 reorder including multi-column/wide path where applicable;
- tablet 7 / T7 Combat landscape width use;
- tablet 9–10 / T5 Conjuros source availability/add spell;
- tablet 11 representative migrated checkbox family;
- tablet 15 / T8 Table Mode;
- tablet 18 Conjuros sticky behavior, now expected to be unblocked;
- T2/T3/T4/T6 shared controls/settings where materially touched;
- one persistence/reopen sanity check for changed domains rather than replaying the full tablet baseline.

Unrelated accepted physical PASS evidence remains valid and preserved.

## 15. Exact next gate

Before product implementation, the owner should confirm the recommended T3 semantics:

**A. Recommended:** one adaptive card distribution/density preference, with effective columns computed from available width + text scale + target minimum card width.

**B. Fallback:** keep context-specific selectors but define them explicitly as maximum columns and constrain/explain them accordingly.

All other repair-family directions above are sufficiently grounded in existing contracts, physical evidence and source audit to proceed without inventing a new product rule.

After T3 confirmation: implement the consolidated repair on this branch, run focused tests + aggregate Scaffold, assign a new monotonic candidate, freeze exact artifact evidence, then perform only the targeted cross-device revalidation matrix above.

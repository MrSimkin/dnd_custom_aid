# Phase 4 — Batch I2a Supercompact completion

**Date:** 2026-09-04  
**Status:** GREEN  
**Durable branch:** `implementation/phase4-character-closure`  
**Safety branch:** `tmp/phase4-i2a-supercompact`  
**Canonical `main`:** untouched

## Scope

I2a completes the Supercompact half of Batch I2 without introducing a second character-state model or a schema migration.

The controlling requirements are D-0047 F15/F16 and the Batch I2 execution plan: Supercompact must project authoritative character values plus ordered Quick Access/Favorites, adapt to available width and expose only deliberate live operational controls.

## Implementation result

### Shared Quick Access projection

Added `CharacterQuickAccessProjection.kt` as a pure shared resolver.

It:

- preserves persisted Quick Access sort order;
- resolves references directly against authoritative `CharacterSheet` + `CharacterClosureState` collections;
- covers combat entries, traits, spells, resources, class options, forms, companions, custom skills and temporary effects;
- ignores stale references safely;
- ignores currently unbacked `OTHER` references rather than inventing data;
- carries identity/name/reference information only and does not copy mutable live values.

Focused desktop tests cover ordered resolution across all backed kinds, stale refs and empty Quick Access.

### Supercompact Android surface

`CharacterSupercompactV4` now:

- reads the persisted character sheet and closure state directly;
- retains compact identity, AC, HP, speed, initiative, proficiency, passive perception, Inspiration, contextual death saves and ability values;
- renders ordered Favorites from the shared Quick Access projection;
- uses one Favorite column on narrower layouts and two on sufficiently wide layouts while retaining the existing responsive stat-column logic;
- shows kind-specific compact Favorite detail from the authoritative domain objects;
- provides deliberate live `-1/+1` HP controls through the existing shared damage/healing operations;
- provides one-tap spend/recover for Favorite generic resources;
- provides one-tap use/recover for persisted spell slots;
- states explicitly that Supercompact does not maintain an independent copy of character state.

### Editor/state integration

`CharacterEditorV4` now opens Supercompact with persisted `stored` + `closureState`, not the settings/draft projection.

A narrow `persistSupercompactSheet` bridge saves authoritative live writes and mirrors changed HP/temp-HP and spell-slot values back into the editor draft so a later ordinary Save cannot restore stale operational values.

If structural drafts are already dirty, Supercompact remains fully readable but its live HP/resource/slot controls are disabled until those pending structural changes are saved or discarded. This avoids combining an authoritative live write with an older unsaved structural snapshot.

## Verification history

### Shared seam gate

- shared/test integration commit: `dc8b6c64d57a390c6ba448f5fd86a22b2f3631ac`;
- workflow `33833431463` — PASS across backend, shared/Kotlin tests, Android debug assemble, Desktop build and APK upload.

### Integrated UI gate

The first clean integrated tree (`17b1d57200a21dfe92e81b3eef1eb40b354ebf9c`) correctly exposed one Kotlin cross-module smart-cast failure at the Favorite-resource recover-button enabled condition.

The repair was behavior-preserving and exactly one source-line change:

- replace the cross-module `maxValue == null || currentValue < maxValue` smart-cast expression with a nullable `let` expression;
- repair product commit `9136acbd6733f50ca4aa781b4aeaac2ddc0d7e22`;
- net repair diff: one addition / one deletion in `CharacterSupercompactV4.kt` only.

### Final controlling gate

- exact clean-tree commit: `3d24115bb1fff8aff223a80f4df63a21545539da`;
- tree: `102758bf5dc111d3f0af5aa1faa790d5d0731865`;
- workflow `33835475798` — PASS;
- backend type-check: PASS;
- shared/Kotlin tests: PASS;
- Android debug assemble: PASS;
- Desktop build: PASS;
- APK upload: PASS;
- artifact: `9923180307` (`dnd-custom-aid-debug-apk`);
- artifact ZIP digest: `sha256:6cbfc6f8c1a616d531e289b1316054ed42875bc94d79071cab14a13ab1af61fd`.

Final durable-baseline diff from pre-I2 state `e4989db3151869ef36d85ca40d56ea57906fb751` contains only four intended files:

1. `CharacterEditorV4.kt`;
2. `CharacterSupercompactV4.kt`;
3. `CharacterQuickAccessProjection.kt`;
4. `CharacterQuickAccessProjectionTest.kt`.

Temporary workflow helpers and CI marker files are absent from the final product tree.

## I2b boundary discovered during I2a audit

Table mode must remain a write-policy over the existing character surfaces, not a blanket pointer-blocking overlay.

Existing seams already distinguish many structural and operational actions:

- Combat separates structural entry changes from persisted operational sheet changes;
- Spells separates spell/source edits from slot-spent changes;
- Traits has distinct use-meter operations even though they currently share the trait-list callback;
- Equipment has explicit quick-use operations separate from add/edit/duplicate/delete/reorder intent;
- Gestión is primarily live operational state, while resource definition add/edit/delete is structural;
- Notes, Background and most conditional-module editors are structural.

I2b should therefore suppress structural add/edit/delete/duplicate/reorder/configuration while preserving intended session controls such as HP, resource counters/rest recovery, spell-slot spend/recover, trait use meters, consumable/ammunition quick use, conditions/exhaustion, concentration, temporary effects, Inspiration/death saves and similar explicitly live state.

Search/filter/collapse/presentation-only controls are not character writes and may remain usable.

## Exact continuation

Resume **Batch I2b — Table mode completion** from the durable branch after this checkpoint.

Implement explicit structural-edit permission at existing callback/UI seams; do not create a second read-only character model and do not globally consume pointer input. Preserve operational controls. Run the full Gate I technical checks before declaring Batch I complete. Keep `main` untouched and do not begin Batch J or DM work early.

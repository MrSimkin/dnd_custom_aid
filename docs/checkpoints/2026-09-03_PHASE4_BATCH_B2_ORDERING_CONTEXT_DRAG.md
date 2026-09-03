# Phase 4 Batch B2 — Ordering, Context, Dirty State and Drag Foundation

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** **GREEN / COMPLETE**  
**Purpose:** establish reusable collection presentation, dirty/leave protection, context-preserving state ownership, and real drag-feedback primitives before domain-specific closure batches.

## 1. B1 prerequisite closure

B1 global editor/IME/action foundation was fully closed before B2 proceeded.

Final B1 code head:

- `79092402e7ff0b93579bc785f891e5e95a0333ed`

Final B1 workflow:

- `33791637168`
- backend PASS;
- shared/Kotlin tests PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- APK artifact upload PASS.

B1 established the shared IME-safe editor/action grammar and migrated the existing character keyboard editors away from one-off editable `AlertDialog` implementations.

## 2. Shared presentation/search foundation

Implemented in:

- `4cb0ced0106ef48e8bff969a8d9549380c52fcee` — `CharacterPresentation.kt`;
- `f199a6e5e13b9abc19cf7fc4cb842e0f2b1eef16` — regression tests.

The shared contract now provides:

- `CharacterPresentationOrder.MANUAL` / `ALPHABETICAL`;
- immutable `CharacterCollectionQuery` search/filter state;
- normalized case/accent-insensitive search support;
- generic collection projection for search/filter/order;
- deterministic tie-breaking;
- a strict presentation-only A–Z path that never rewrites stored manual `sortOrder`.

Regression coverage explicitly proves Manual -> A–Z -> Manual restores the original manual order and that search/filter projection does not mutate stored ordering.

## 3. Android collection/drag/haptic primitives

Implemented in:

- `a2ed100dec6a5ad85edf23c423e78b0bfe198fad` — `CharacterCollectionPrimitivesV4.kt`.

Reusable primitives now include:

- count/search/filter/sort toolbar with externally owned state;
- `CharacterDragVisualStateV4`;
- lifted/translated drag-surface modifier;
- animated insertion/drop indicator;
- semantic haptic event hook covering drag pickup/step/drop, resource interaction and destructive interaction.

The haptic hook deliberately accepts an external `enabled` value. Batch C will connect that value to the already-persisted per-character `CharacterClosureState.hapticsEnabled`; B2 does not duplicate that persisted setting.

Controlling foundation workflow:

- `33792391465`
- backend PASS;
- shared/Kotlin tests PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- APK upload PASS.

## 4. Dirty state and unsaved-leave protection

Implemented in:

- `2094d2dc7b3ef23696234b534d7130343e13180c` — reusable three-action leave dialog;
- `7d62aec09539ecc26a5122b4ca24a4beb470e413` — main character editor integration.

Behavior:

- dirty state is derived from actual current draft state versus the last persisted state, not a manually maintained boolean;
- reverting edits back to the persisted state removes the dirty indication automatically;
- the character header shows `Cambios sin guardar` while dirty;
- leaving a dirty character offers `Guardar`, `Descartar`, or `Seguir editando`;
- save-on-leave chains correctly through the existing blank-required-numbers confirmation;
- cancelling the secondary blank-number prompt cancels the pending leave intent;
- PC Settings Back still returns to the same character editor before character-list navigation.

Controlling workflow:

- `33793135304`
- backend PASS;
- shared/Kotlin tests PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- APK upload PASS.

## 5. Real drag-feedback proof consumers

### Notes

Commit:

- `024b1ac0dde49557ab89f7d5aceda6b9e06d50cb`

Behavior:

- dragged note card visibly lifts/scales;
- card follows the finger using the unconsumed drag remainder;
- insertion direction is shown by an animated drop indicator;
- existing persisted manual reorder semantics remain authoritative.

Workflow:

- `33793677310` — PASS across backend, shared/Kotlin, Android debug, Desktop and APK upload.

### Combat entries

Commit:

- `6f10f472c70fe6e54dec6eb9cfb783245e165805`

The same proven visual drag behavior now wraps attack/action cards without changing their stored reorder semantics.

Workflow:

- `33794100599` — PASS across backend, shared/Kotlin, Android debug, Desktop and APK upload.

## 6. D16 context-preservation foundation

B2 intentionally avoids introducing a second navigation/context model.

Existing character context is already held in `rememberSaveable` at the appropriate ownership level, including selected character tab and child-editor state. The new collection toolbar is stateless with externally owned query/order state, so later Equipment/Rasgos/Conjuros implementations can preserve search/filter/order while entering and leaving child editors instead of recreating state internally.

PC Settings remains inside the same character-editor composition path; returning from it preserves the character draft and selected tab.

Further app-reopen persistence such as last-open-tab is still an explicit later Increment I concern and is not falsely claimed by B2.

## 7. B2 gate conclusion

**B2 is GREEN and closed.**

Reusable foundations are now ready for later domain batches:

- Manual/A–Z without manual-order loss;
- shared search/filter state;
- real drag presentation;
- configurable haptic hook;
- actual dirty-state indicator;
- guarded unsaved leave;
- parent-owned context state.

Domain-specific sorting/filtering, haptic preference wiring, and full drag rollout remain deliberately owned by their planned batches rather than duplicated here.

## 8. Exact continuation

Proceed to **Batch C — PC Settings**.

Use the already-persisted schema-7 `CharacterClosureState` through `CharacterClosureRepository`; do not add a migration merely to re-store existing settings.

Batch C should wire:

- lifecycle status into PC Settings;
- haptics setting;
- Table/read-only mode setting;
- progress mode and XP/Milestone values;
- conditional module overrides;
- global application-settings entry that opens the existing app-wide settings surface without changing preference ownership;
- existing spellcasting hide-not-delete control;
- D16 Back/context hierarchy.

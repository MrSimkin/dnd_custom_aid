# Phase 4 — Batch C PC Settings checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** GREEN  
**Controlling tested head:** `d8116353f96f0fc32e99ac3a5e4a1084c4b3b02b`  
**Controlling workflow:** `33796586608`

## 1. Scope closed

Batch C consolidates character-wide behavior/configuration in PC Settings while keeping live character state and application-wide preferences in their correct ownership layers.

Implemented:

- lifecycle status (`Activo`, `Inactivo`, `Retirado`, `Muerto`) moved out of General and into PC Settings;
- existing `Lanzador de conjuros` hide-not-delete behavior retained;
- persisted schema-7 haptic setting wired to real Notes/Combat drag haptics;
- persisted Table/read-only preference exposed in PC Settings;
- XP/Milestone mode plus editable XP/milestone progress values;
- conditional module auto-suggestions plus manual `Automático` / `Mostrar` / `Ocultar` overrides;
- module hiding remains visibility-only and does not delete module-owned data;
- entry from PC Settings to the existing global Application Settings surface; theme/font/text scale remain application-wide preferences;
- responsive PC Settings layout with two-column grouping when useful width is available;
- experimental Supercompact entry and first real read-only operational projection;
- Back hierarchy: Supercompact -> PC Settings -> character editor -> character list;
- PC Settings changes use immediate persistence rather than adding a second ambiguous Save workflow.

## 2. Supercompact C-stage surface

The initial experimental view is intentionally useful but not the final Increment-I implementation.

It currently projects authoritative character data only:

- identity, class/subclass, lifecycle status and total level;
- AC;
- current/max/temp HP;
- speed;
- initiative;
- proficiency bonus;
- passive Perception;
- Inspiration;
- death saves when relevant;
- six ability scores/modifiers;
- adaptive real-width column count.

It owns no duplicate gameplay state.

Deferred intentionally to Batch I2:

- Favorite/Quick Access population;
- direct operational controls from Supercompact;
- holistic Table-mode structural-edit suppression;
- final density/visual polish after phone/tablet QA.

## 3. Class/subclass identity integrity fix

During C audit, the Android draft layer was found to preserve only class name/level/hit-die fields. An ordinary character Save could therefore erase already-persisted schema-6 class/subclass provenance.

C fixes that round trip. `CharacterEditorDraftV4` now preserves through save/recreation:

- class rules family;
- class source;
- class catalog key;
- subclass name;
- subclass source;
- subclass catalog key;
- subclass rules family.

If the user intentionally changes a class name manually, stale catalog/subclass provenance is cleared rather than silently attached to a different class.

This fix is required for reliable conditional module suggestions and is considered part of C's integrity gate, not additional feature scope.

## 4. Module visibility foundation

Added shared resolution helpers:

- catalog-based class/subclass suggestions;
- multiclass union;
- manual overrides that always win;
- `AUTO` normalization so redundant overrides need not remain stored;
- permissive fallback for manually entered localized class names when exact catalog provenance is absent.

Manual/custom/homebrew characters remain unrestricted and may force any reusable module visible or hidden.

## 5. Persistence and ownership

No schema migration was required for C.

`CharacterClosureRepository` remains authoritative for:

- haptics;
- Table/read-only preference;
- XP/Milestone state;
- module overrides.

`CharacterRepository` remains authoritative for lifecycle status and spellcaster visibility.

`UiPreferences` remains authoritative for application theme/font/text settings.

Lifecycle status persistence is synchronized back into the open editor draft so a later ordinary Save cannot accidentally revert a status changed from PC Settings.

## 6. Haptic integration

The B2 semantic haptic hook is now backed by the persisted character preference for existing real-drag consumers.

Notes and Combat emit:

- drag pickup;
- successful reorder step;
- drag drop.

Disabling haptics in PC Settings suppresses those events without changing reorder behavior.

## 7. Implementation checkpoints

Shared module resolution/test foundation:

- helper commit `bff7df5ab04f7651e2cd8702011b812d00df2e29`;
- initial test commit `5648af73b45a2f4bdc05be5a3054e471b4d1961f`;
- helper refinement `bd18b7acd62f4db076d7aa0d5cb1ddc79519b314`;
- final manual-name regression test head `d8116353f96f0fc32e99ac3a5e4a1084c4b3b02b`.

New Android surfaces:

- consolidated PC Settings commit `17946ab475fb2e0c18664164aee3cb7f1b280ddf`;
- experimental Supercompact commit `51c85c9f27b8345ba417ac6741b69732392801f5`.

Integrated Android wiring/integrity change:

- `f387b33b491a9e43c6d5678b0d589c538e69df6a` — `feat: integrate Batch C PC settings`.

The one-time guarded integration scaffolding self-deleted after successful application and is not part of the permanent product tree.

## 8. Verification

Shared module helper foundation:

- workflow `33795174781` — PASS.

Controlling full Batch C gate:

- workflow `33796586608`;
- tested head `d8116353f96f0fc32e99ac3a5e4a1084c4b3b02b`;
- backend type-check PASS;
- shared/Kotlin tests + SQLDelight PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- APK artifact upload PASS.

**Batch C is closed GREEN.**

## 9. Real-device boundary

CI does not replace final owner QA. The eventual closure APK still requires:

- phone portrait;
- phone landscape;
- tablet portrait;
- tablet landscape;
- representative larger text scale;
- Supercompact usefulness/visual-quality assessment;
- Back and global-settings return-path smoke;
- haptic on/off verification on physical hardware.

## 10. Exact continuation

Begin **Batch D — `Gestión` live character maintenance**.

Keep ownership explicit:

- schema-7 closure state: Conditions, Exhaustion, Concentration, recovery metadata, temporary effects, reconciliation checkpoints;
- core CharacterSheet: generic Resources, Inspiration, death saves, HP/hit-dice;
- Gestión projects and edits those authoritative values; it must not create duplicate gameplay state.

Implement Rest behavior as preview + selective apply only. No automatic legality enforcement and no unapproved hard-coded class rules.

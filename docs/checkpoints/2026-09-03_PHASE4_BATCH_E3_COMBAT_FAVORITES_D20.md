# Phase 4 — Batch E3 Combate + Favorites + d20 checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** PENDING INTEGRATION GATE  
**Canonical `main`:** untouched

## Scope

E3 closes the approved Batch-E operational combat improvements without introducing another persistence model or rules engine.

Implemented:

- quick HP operations in Combat: damage, healing and exact temporary HP;
- damage consumes temporary HP first and current HP never drops below 0;
- healing is capped by the currently stored maximum HP;
- contextual death-save success/failure controls appear when stored current HP is 0;
- death-save controls are bounded 0..3 but do not infer stabilization/death/reset behavior;
- combat-entry summary line exposes action type + optional attack modifier + damage/effect at a glance;
- Quick Access Favorite toggle for persisted combat entries;
- newly created but not-yet-structurally-saved combat entries cannot persist a Favorite reference yet;
- bounded simple d20 convenience roller for combat entries with an attack modifier;
- the same d20 surface is available for standard saves, standard skills and custom skills in both existing Habilidades organization modes;
- dice results are ephemeral and are not saved to the character.

## HP state-integrity rule

Current/max/temp HP overlap with the structural editor draft, so quick combat operations cannot update only the stored sheet.

`persistCombatOperationalSheet` saves the live sheet and, only when current HP or temporary HP actually changed, synchronizes those two fields back into the existing draft. All unrelated structural draft fields remain untouched.

This prevents a later structural `Guardar` from accidentally restoring stale HP while preserving unrelated unsaved character edits.

A death-save-only operation does not rewrite draft HP because HP did not change.

## Rules boundary

The d20 helper deliberately stops at `d20 + modifier`.

It does not interpret:

- advantage/disadvantage;
- critical hits/failures;
- damage rolls;
- Sneak Attack or similar features;
- legality;
- automatic effects or rule outcomes.

Death saves likewise remain manual state recording rather than automated D&D resolution.

## Favorites boundary

E3 wires Favorites for the E-relevant combat-entry surface only. The approved execution plan assigns Favorites for Traits, Spells and later companion/module domains to their own G/H batches, so E3 does not pre-empt those domain-specific integrations.

## Relevant integration

- bot integration commit `bd340abdec50a4bbbeec44378e6f83d44b760f3f` — `feat: integrate Batch E3 combat quick operations favorites and d20`;
- `CharacterCombatOperationalV4.kt` owns quick HP and contextual death-save UI;
- `CharacterD20RollUiV4.kt` owns the reusable bounded d20 surface;
- `CharacterCombatTabV4.kt` now projects Quick Access Favorites and compact action/damage summaries;
- `CharacterEditorV4.kt` owns selective HP draft synchronization;
- `CharacterCustomSkillsV4.kt` reuses the d20 surface for calculated custom-skill totals.

## Gate required

E3 is not GREEN until the controlling workflow on this checkpoint head passes:

- backend type-check;
- shared/Kotlin tests, including E1 HP/D-0046 operations;
- Android debug compile/assembly;
- Desktop build;
- APK artifact upload.

If E3 passes, close this checkpoint GREEN, create/close the overall Batch E checkpoint, update `PROJECT_STATE.md` to Batch F, and only then begin Equipo + Monedas closure.

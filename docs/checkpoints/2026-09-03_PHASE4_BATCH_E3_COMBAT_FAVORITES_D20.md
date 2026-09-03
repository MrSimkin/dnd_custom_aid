# Phase 4 — Batch E3 Combate + Favorites + d20 checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** GREEN  
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
- controlling tested head `1ae8fd235b0fa863b3efc62e091a97242f295aea`;
- `CharacterCombatOperationalV4.kt` owns quick HP and contextual death-save UI;
- `CharacterD20RollUiV4.kt` owns the reusable bounded d20 surface;
- `CharacterCombatTabV4.kt` now projects Quick Access Favorites and compact action/damage summaries;
- `CharacterEditorV4.kt` owns selective HP draft synchronization;
- `CharacterCustomSkillsV4.kt` reuses the d20 surface for calculated custom-skill totals.

## Verification

Controlling workflow `33812352925`: **PASS**.

Verified on the checkpoint head:

- backend install + type-check PASS;
- shared/Kotlin regression tests PASS;
- Android debug compile/assembly PASS;
- Desktop build PASS;
- APK artifact upload PASS.

Workflow artifact:

- name: `dnd-custom-aid-debug-apk`;
- artifact ID: `9915350879`;
- ZIP digest reported by GitHub: `sha256:7d57b4bcbb3a11a4e60cf32821a0a00c50ff15e6f777e0a406073f14ab0d6141`.

This is integration evidence only, not the frozen owner-QA closure candidate.

**E3 gate is closed GREEN.**

## Exact continuation

Close overall Batch E in a durable checkpoint, move `PROJECT_STATE.md` to **Batch F — Equipo + Monedas**, and only then begin F implementation.

Real phone/tablet portrait/landscape and larger-text QA remains part of the later frozen closure-candidate owner gate.

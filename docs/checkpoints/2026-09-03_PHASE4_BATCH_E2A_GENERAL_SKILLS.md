# Phase 4 — Batch E2a General + Habilidades checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** PENDING INTEGRATION GATE  
**Canonical `main`:** untouched

## Scope

E2a wires the already-persisted schema-7 character reference state into General and Habilidades without introducing another migration or shadow state.

General now projects/edit:

- optional local portrait reference;
- optional local token reference;
- structured Defenses;
- structured Senses;
- structured special Movement.

The media picker uses Android's document picker and stores only a persistable local URI/reference. No cloud upload or third-party parser is introduced.

Habilidades now includes:

- Passive Perception;
- Passive Insight;
- Passive Investigation;
- custom/homebrew skills linked to a Character Ability;
- custom-skill proficiency/expertise + adjustment calculations;
- the same existing `Por habilidades` / `Por atributo` presentation choice controls how custom skills are shown; no third skill organization mode is created.

## State ownership

- General structural draft remains owned by `CharacterEditorDraftV4` / `CharacterRepository`.
- Defenses/Senses/Movement/portrait/token/custom skills remain owned by `CharacterClosureState` / `CharacterClosureRepository`.
- Closure edits persist immediately and do not consume unrelated unsaved structural draft changes.
- Passive/custom-skill totals use the current calculable draft projection (`settingsSheet`) so D-0046 proficiency/ability semantics remain authoritative.

## Relevant commits

- `08e60bdaa73994d72046ee48961f13ecd341450e` — General closure reference cards;
- `a92e209cd80e34df1a9df3f92849122d0d534fb7` — custom skills Habilidades surface;
- `f25eab82283b00791140ba53b3039ef11ab3af99` — passive skills summary;
- `d19fd6fc2eab2eee15354451a47f6d2cb747378c` — guarded editor wiring + explicit vertical General-card container.

The isolated surface head `a92e209c...` already passed workflow `33810193659`, but that did not test the later editor wiring.

## Gate required

This checkpoint commit exists to trigger the controlling integration run on the wired head. E2a is not GREEN until that run passes:

- backend type-check;
- shared/Kotlin tests including prior E1/D-0046 coverage;
- Android debug compile/assembly;
- Desktop build;
- APK artifact upload.

Real phone/tablet portrait/landscape and larger-text QA remains part of the later frozen closure-candidate owner gate.

# Phase 4 — Batch E2a General + Habilidades checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** GREEN  
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
- `d19fd6fc2eab2eee15354451a47f6d2cb747378c` — guarded editor wiring + explicit vertical General-card container;
- controlling tested head `f3a8fda2e616d88d455d717c17e95a6d2368122b`.

## Verification

Controlling workflow `33810868863`: **PASS**.

Verified on the wired integration head:

- backend type-check PASS;
- shared/Kotlin tests including prior E1/D-0046 coverage PASS;
- Android debug compile/assembly PASS;
- Desktop build PASS;
- APK artifact upload PASS.

The earlier isolated surface head `a92e209c...` also passed workflow `33810193659`, but the controlling acceptance evidence for E2a is `33810868863` because it includes the final editor wiring.

**E2a gate is closed GREEN.**

## Exact continuation

Proceed with **E2b — class/subclass/source identity UI + non-enforcing hit-die suggestion**. Existing schema/draft persistence already preserves catalog keys, source and rules-family identity, so E2b must remain a UI/presentation/editor change rather than a new persistence model.

Real phone/tablet portrait/landscape and larger-text QA remains part of the later frozen closure-candidate owner gate.

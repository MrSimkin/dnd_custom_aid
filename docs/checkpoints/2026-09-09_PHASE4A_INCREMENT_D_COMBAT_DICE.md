# Phase 4A successor cycle — Increment D Combat + Dice

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** COMPLETE / GREEN

## Scope

This checkpoint covers successor-plan Increment **D — Combat + Dice as one structured interaction family**.

Owner/device visual acceptance is **not** claimed by this checkpoint.

## D1 — compact structured Combat

Combat now consumes the successor structured damage model instead of treating legacy free-text `damageEffect` as the primary editing surface.

Attack/action cards use the planned hierarchy:

1. `Nombre (+bono)`;
2. structured damage/effect summary;
3. action type + range;
4. short notes preview;
5. compact grouped actions.

Reordering uses the shared whole-card long-press drag primitive on the non-interactive card body, with existing haptic pickup/step/drop feedback and a visible moving state.

The editor uses the shared IME-safe dialog family and supports ordered damage/effect components:

- dice expression + optional type;
- flat signed integer + optional type;
- free effect text.

The legacy `damageEffect` field is now written only as a derived compatibility summary. Structured components remain authoritative for successor behavior.

## Draft/save transaction and FK safety

Combat entries remain canonical core-sheet objects while structured damage profiles remain successor state keyed by combat-entry ID.

New attacks are not auto-saved merely to create a successor foreign-key parent. Instead, both the attack and its damage profile remain in the existing screen draft until the user invokes the normal global Save operation.

Save order is explicit:

1. persist the integrated core `CharacterSheet`, creating/updating combat-entry parents;
2. filter the damage draft to the now-live combat-entry IDs;
3. persist `CharacterSuccessorState.combatDamage` through the existing successor repository context.

This preserves current draft semantics and avoids hidden persistence side effects.

Structured-damage draft state also participates in the editor's unsaved-change detection, so damage-only edits use the same leave/Back protection as ordinary character edits.

## D2 — compact character-aware Dice flow

The previous browse-heavy grouped list is replaced by one compact flow:

1. Normal / Ventaja / Desventaja;
2. roll category;
3. concrete target;
4. Tirar.

The shared target engine exposes, when available:

- built-in attributes;
- custom attributes;
- standard saving throws;
- enabled custom-attribute saving throws;
- standard skills;
- custom skills;
- attacks;
- per-source spell attacks;
- manual/custom modifier path.

Target labels and calculations reuse existing shared successor projections, including `Conocimiento Arcano` and generalized ability references.

Results always expose the mathematical decomposition. Advantage/disadvantage records both d20 values and the chosen die.

## Damage rolling

When an attack target is selected, Dice reads the same structured damage components used by Combat.

- DICE components roll the configured expression;
- FLAT components contribute their signed value;
- TEXT components remain descriptive and are never guessed into numeric damage;
- the result exposes component breakdown plus numeric total when numeric components exist.

Legacy attacks without a successor profile continue through the already-defined safe TEXT fallback; arbitrary old text is not heuristically parsed.

## D3 — application result presentation mode

Application Settings now persists one device-wide Dice result presentation preference:

- `Resultado compacto`;
- `Dados visibles`.

Both modes consume the same resolved roll object and therefore cannot change roll mathematics.

`Dados visibles` presents the actual d20 result(s) prominently, marks the selected die for advantage/disadvantage, and retains the same decomposition beneath the visual result.

The preference is stored in the existing `UiPreferencesStore`; no character schema migration is required.

## Shared engine

New shared implementation:

- `CharacterCombatDiceOperations.kt`

It owns:

- d20 normal/advantage/disadvantage resolution;
- generalized target categories/projection;
- structured dice-expression parsing;
- structured damage summaries;
- deterministic damage-roll resolution;
- safe successor/legacy damage bridging.

Focused common tests cover mode selection, custom targets, source-specific spell attacks, damage parsing/rolling and legacy fallback.

## Source evidence

- shared engine: `650152cf953a662d86ab3c29f0d82be5f6cee94a`;
- focused tests: `6695a905170aed17cd04ad1615c2c2759cf2d8d1`;
- shared-engine full gate: workflow `34302821857` — SUCCESS;
- damage draft codec: `34685bf398edf2df26d91dd3602905c14d079429`;
- structured Combat surface: `89698c37d9b52727e29bdcd04f226d3813a5ce63`;
- compact Dice surface: `5d1e6d01338caed6efdf41acc29626b19d8f89c3`;
- editor integration: `05ac4796b110031aaf52b69b10f70addfd8dd09b`;
- D3 persisted result mode: `e4b3e58bd60fb1ceb76ea0bfde6f849a5c85645c`;
- all guarded integration patches matched their expected anchors and passed `git diff --check` before commit.

## First integrated gate and correction

Normal full scaffold workflow `34303994398` ran on checkpoint descendant `6bec814345d8a5b342a795ee3b71a05ebef91d0b`.

Results before Android compilation:

- backend/type-check: PASS;
- shared common tests: reached/passed their compile/test path;
- desktop application build: PASS.

Android compilation then failed on exactly two access errors:

- `CharacterCombatSuccessorV4.kt`: attempted to call `sanitizeSignedIntV4`, which is private to `CharacterEditorV4.kt`;
- `CharacterDiceRollSuccessorV4.kt`: same private-helper access error.

No domain/schema/interaction-model defect was indicated by this failure.

Correction:

- shared Android signed-integer draft helper added in `005189df77614ff9f07ae55380f8203823b58049`;
- both new D surfaces switched to that helper through an exact-guarded patch;
- patch workflow `34304259045` — SUCCESS;
- corrected integrated source head: `96c285edbed3b957edff4ba9b52345e786f285a6`;
- temporary patch files removed in the same correction commit.

## Final automated gate

Normal full scaffold workflow `34304392913` ran on descendant:

`7ef476d0248609d0e1caf291d48afc5c5cc5f77d`

Result: **SUCCESS**.

Verified together:

- backend/type-check: PASS;
- shared/Kotlin tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop compilation/build: PASS;
- Android debug APK artifact upload: PASS.

Artifact:

- artifact ID `10086158879`;
- artifact name `dnd-custom-aid-debug-apk`;
- ZIP digest `sha256:70891bc5de59f5391c60bc9823facaaacc81c91319013b88e5950e62f0fccc84`;
- artifact source head `7ef476d0248609d0e1caf291d48afc5c5cc5f77d`.

This closes Increment D technically. It does **not** constitute owner-device visual acceptance.

## Exact continuation

Proceed with Increment **E — Gestión + Markers + Resources + recovery/conditions**.

The first E boundary is the shared trackable/recovery operation layer:

1. reuse one tracker/recovery mechanic for Resources and Custom Markers while preserving their distinct identities;
2. unify rest preview/apply across participating domains;
3. automatic recovery only from explicit structured rules;
4. legacy/free recovery text remains review-only;
5. then wire the compact Gestión operational surface against those canonical operations.

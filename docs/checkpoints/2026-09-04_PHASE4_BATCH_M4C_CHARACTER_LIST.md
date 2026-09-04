# Phase 4 Batch M4c — I18 character-list completeness

**Date:** 2026-09-04  
**Status:** **GREEN / COMPLETE** — next M4 hole may proceed  
**Safety branch:** `tmp/phase4-m4c-character-list`  
**Authoritative M4b base:** `2020f195a4ced15ac1edc7ee18cd6f5d58bcf3ca`  
**Accepted product commit:** `cde97d9b7a4ae09cdd710c6a155bea4a5dc9e4cd`  
**Accepted product tree:** `ced96e176b3822ca10b89a3baef81437ab722241`  
**Clean controlling checkpoint commit:** `4a47b3f22d411ac09eeff87f37fbf3cdf09aca30`  
**Canonical `main`:** untouched  
**Historical L candidate:** untouched

## Purpose

Close M3 hole I18: the campaign character list already exposed character name, class name/level, lifecycle status and total level, but it did not expose the approved subclass, freshness/last-updated signal, or portrait.

M4c enriches the existing list from existing authoritative data only. It does not create or persist a second character-summary model.

## Existing authorities reused

- `CharacterSheet.classes` and `CharacterClassLevel.subclassName` for class/subclass/level;
- `CharacterSheet.updatedAtEpochSeconds` for freshness;
- `CharacterClosureState.portraitRef` for the already-persisted local portrait URI;
- `CharacterClosureRepository` already owned by `MainActivity`.

No schema migration, new database table, backend activation, or duplicate summary authority was added.

## Shared presentation helpers

Added `CharacterListPresentation.kt`.

### `characterListClassSummary(...)`

- preserves saved class order using `sortOrder` with stable identity tie-break;
- includes class level;
- includes nonblank subclass as `Clase nivel (Subclase)`;
- supports multiclass summaries separated by ` / `;
- falls back safely for no class or blank class name.

Commit:

- `ecce54c9e44672d8aa97ed7a0f657af77ddbd8d3`.

### `characterListFreshnessLabel(...)`

Human-scale presentation from the authoritative `updatedAtEpochSeconds`:

- under 60 seconds: `Actualizado ahora`;
- under one hour: minutes;
- under one day: hours;
- otherwise: days;
- future clock skew is safely clamped to `Actualizado ahora`.

## Focused tests

Added `CharacterM4CharacterListPresentationTest.kt`.

Coverage:

- class + subclass + level output;
- saved multiclass ordering;
- no-class fallback;
- blank-subclass behavior;
- freshness boundaries at seconds/minutes/hours/days;
- future timestamp clamp.

Commit:

- `c6c6e715a08fd24303bfa3758fd819aa73f06e40`.

## Android list integration

The existing `CharacterListScreen` now receives `CharacterClosureRepository` and snapshots closure state for the listed characters alongside the authoritative `CharacterSheet` list.

Each card now shows:

- character name;
- class/subclass/level summary;
- lifecycle status;
- total level;
- relative freshness from `updatedAtEpochSeconds`;
- a 64 dp portrait thumbnail when the existing local portrait URI is readable;
- a neutral first-initial placeholder if there is no portrait or the local URI cannot be decoded.

Portrait reading reuses the same local-content pattern already used by the General portrait editor: `contentResolver.openInputStream(Uri.parse(...))`, safe decode, no persistence mutation.

`reload()` refreshes both character sheets and closure-state projections, so returning from a character edit reflects current authoritative data without a new cache authority.

## Guarded integration history

### First attempt — rejected

Workflow `33899458436` correctly passed the exact CharacterUi patch and I18 reachability assertions, but Android compilation failed because the first MainActivity patch matched an overly broad argument sequence:

- one `CharacterListScreen` call missed `closureRepository`;
- the existing `CharacterEditorScreenV4` call received `closureRepository` twice.

This attempt was **not accepted or promoted**.

### Focused repair

Added an exact repair helper that:

- adds the missing repository argument to exactly one CharacterList call;
- removes exactly one duplicate editor argument;
- asserts exactly three intended `closureRepository = characterClosureRepository` injections remain: two list call sites plus the existing editor call.

Repair staging commits:

- `1c693d71780429ed29587e51e44d870562c9610c`;
- `92e262bd8760dc38208c27eda2e1712abf3887fe`.

### Controlling guarded gate

Workflow `33899848934` — **SUCCESS**.

It verified:

- exact source patch + exact call-site repair;
- class-summary helper reachable from list cards;
- freshness helper reachable from list cards;
- portrait reads `closureState.portraitRef`;
- exactly three intended closure-repository injections;
- full shared/Kotlin desktop tests PASS;
- Android debug assemble PASS;
- Desktop build PASS;
- backend dependency install/type-check PASS;
- accepted helper-free product commit created.

The temporary integration workflow/scripts self-deleted in accepted product commit `cde97d9b7a4ae09cdd710c6a155bea4a5dc9e4cd` and are not part of the accepted product tree.

## Clean controlling standard gate

Ordinary repository workflow on the clean helper-free product tree plus the pending checkpoint:

- exact tested commit `4a47b3f22d411ac09eeff87f37fbf3cdf09aca30`;
- workflow `33900280161` — **SUCCESS**;
- backend install/type-check — PASS;
- full shared/Kotlin desktop tests — PASS;
- Android debug assemble — PASS;
- Desktop build — PASS;
- Android debug APK upload — PASS;
- artifact ID `9947440048`, name `dnd-custom-aid-debug-apk`;
- artifact ZIP digest `sha256:12775271da058a991cccbc00577110f64265a71cdd6117abe866d6358cb5b98e`.

This artifact is technical M4c evidence only. It is not the future replacement frozen M5 QA candidate.

## Gate conclusion

**M4c / I18 is GREEN and complete.**

M4c closes I18 implementation completeness only. Real-device visual density, thumbnail behavior and freshness readability remain M6 owner QA. That does not reopen implementation completeness unless QA exposes a real functional defect.

## Exact continuation

Promote the final helper-free M4c tree to `implementation/phase4-preqa-consolidation`, then consult the durable M3/M4 audit checkpoint and `PROJECT_STATE.md` to select the next confirmed M4 implementation hole. Do not infer the next hole from memory and do not advance to owner QA.

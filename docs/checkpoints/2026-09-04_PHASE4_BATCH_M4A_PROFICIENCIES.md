# Phase 4 Batch M4a — F14 structured proficiencies/languages

**Date:** 2026-09-04  
**Status:** **GREEN / COMPLETE** — M4b may proceed  
**Safety branch:** `tmp/phase4-m4-implementation`  
**Authoritative pre-M4 base:** `d028147307732bc81938f2e8a531d6220e03c631`  
**Accepted product commit:** `8c4c5f32d201e0080ee1af2af95d39f38179ac5a`  
**Accepted product tree:** `c1e159fa6c3a8860c10186dcf428f7f8ef2c8b95`  
**Clean controlling checkpoint commit:** `2fce2d1678c193fc11bea8ae2b511c4fbd7e98bc`  
**Canonical `main`:** untouched  
**Historical L candidate:** untouched

## Purpose

Close M3 hole F14: the durable `CharacterSheet.proficiencies` model already existed and round-tripped, but Android had no reachable management UI for structured languages/proficiencies/training.

This batch closes the missing implementation only. Real-device quality remains M6 QA.

## Delivered

### Shared operations

Added `CharacterProficiencyOperations.kt`:

- deterministic normalization by saved manual `sortOrder` with stable identity tie-break;
- dense manual order after normalization;
- identity-preserving manual move by offset;
- safe no-op normalization at list boundaries or unknown IDs.

Commit:

- `b3d23b83e1276525bc9e9d128269849ed453a63e`.

### Focused regression coverage

Added `CharacterM4ProficiencyTest.kt` covering:

- manual normalization and move without changing identities;
- repository round-trip for all approved proficiency kinds:
  - Language;
  - Tool;
  - Weapon;
  - Armor;
  - Other;
- optional source and notes;
- preserved IDs and dense manual order.

Commit:

- `0c63492e8ef6b8afe4c9b24eec50e172497811b1`.

The earlier `CharacterClosureFoundationTest` continues to independently cover representative proficiency persistence alongside the broader character aggregate.

### Android structural draft codec

Added `CharacterProficiencyDraftCodecV4.kt` so proficiency edits participate in the established structural draft model rather than persisting immediately or creating a new database authority.

Properties:

- stable UUIDs;
- type/name/source/notes/manual order preserved;
- permissive fallback for unknown serialized type to `OTHER`;
- no schema migration and no duplicate persistence authority.

Commit:

- `6c76c0176df63ad53c0fcf01e8e245b61f160b41`.

### Android Habilidades surface

Added `CharacterProficienciesV4.kt` with:

- one `Idiomas y competencias` card in Habilidades regardless of skill organization mode;
- add/edit/delete;
- Language/Tool/Armor/Weapon/Other type selection;
- freeform name;
- optional source and notes;
- stable manual up/down order;
- useful empty state;
- named destructive confirmation;
- reusable IME-safe editor;
- inline blank-name validation;
- no class/species/feat legality enforcement;
- structural controls hidden/disabled under Table mode.

Commit:

- `9460d7d52ca829215b6ae91f9087956aa1baf749`.

### Character editor / Save integration

Accepted product commit:

- `8c4c5f32d201e0080ee1af2af95d39f38179ac5a`.

Integration adds a separate saveable proficiency draft to `CharacterEditorScreenV4` and wires it into:

- Habilidades rendering;
- structural editing permission;
- `Cambios sin guardar` detection;
- Save;
- Discard/reopen through stored-draft comparison/reset;
- unsaved-leave protection;
- ordinary `CharacterRepository.saveCharacter()` persistence.

An unsaved proficiency mutation therefore behaves like the other structural character domains: it cannot silently persist, disappear from dirty detection, or bypass Table-mode structural locking.

## Verification

### Guarded integration gate

Temporary exact-match integration workflow:

- run `33895264397` — **SUCCESS**.

Before creating the accepted product commit, that workflow verified:

- every coordinator source seam matched exactly once;
- proficiency draft participates in dirty detection;
- Habilidades reaches `CharacterProficienciesCardV4`;
- Save writes `proficiencies` into the integrated `CharacterSheet`;
- shared/Kotlin desktop tests PASS;
- Android debug assemble PASS;
- Desktop build PASS;
- backend dependency install/type-check PASS.

The temporary integration script/workflow self-deleted in the accepted product commit. They are not part of the accepted product tree.

### Clean controlling standard gate

Ordinary repository workflow on clean helper-free product tree plus this checkpoint:

- exact tested commit `2fce2d1678c193fc11bea8ae2b511c4fbd7e98bc`;
- workflow `33895701076` — **SUCCESS**;
- backend install/type-check — PASS;
- full shared/Kotlin desktop tests — PASS;
- Android debug assemble — PASS;
- Desktop build — PASS;
- Android debug APK upload — PASS;
- artifact ID `9945748159`, name `dnd-custom-aid-debug-apk`;
- artifact ZIP digest `sha256:8c429d8f683340814b9c8c4ffbfc9cedb7fab9f2a37c4481a1144a2ccf331c28`.

This artifact is technical M4a evidence only. It is not the future replacement frozen M5 QA candidate.

## Gate conclusion

**M4a / F14 is GREEN and complete.**

The feature is now implemented and technically gated. Whether its layout/interaction quality is acceptable on real phones/tablets remains M6 owner QA; that does not reopen implementation completeness unless QA exposes a real functional defect.

No schema migration was added. No repository redesign was performed. `main` and the historical Batch L candidate remain untouched.

## Exact continuation

Proceed to **M4b — F15 Resource Favorite / Quick Access** on a safety branch from the accepted M4a durable state.

Use the existing `CharacterQuickAccessKind.RESOURCE` authority. Add a reachable Favorite toggle in Gestión for durably saved Resources, preserve operational `−/+` resource controls, prune stale Resource Quick Access references after successful deletion persistence, and treat Favorite configuration as structural under Table mode. Do not add a new favorite storage model or schema migration.

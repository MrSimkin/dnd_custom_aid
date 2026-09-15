# Phase 4A — Repair T5: spell-source bootstrap / source-context compatibility

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Final product/test HEAD:** `3774c53f5189ebd535cc1b73ec18493e268e5d9f`  
**Core editor integration commit:** `2e7fda2852425594971f7df47b433d642eb2119a`  
**Physical baseline remains:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Status:** T5 COMPLETE / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING

## 1. Failure family / root cause

P17 tablet checks 9–10 reproduced the same cross-device T5 failure: a valid canonical Mago/Mage could reach Conjuros without a usable spell-source context, so the add-spell path could not satisfy its required source association. Tablet check 18 remained blocked by the same source-context defect.

Source audit confirmed that canonical class ownership and spellcasting-source ownership were parallel systems. `CharacterClassLevel` already carried stable class identity/catalog provenance, while Conjuros consumed `CharacterSpellcastingSource` plus source-ID-keyed `CharacterSpellcastingProfile`. Nothing reconciled the two for a newly created canonical caster.

The repair therefore belongs at the class-to-spellcasting projection/reconciliation boundary rather than as a screen-only Mago special case.

## 2. Bounded canonical spellcasting metadata

`CharacterSpellcastingBootstrap.kt` adds intentionally small metadata for canonical base spellcasting classes whose default casting ability is known:

- Artificer 2025 / Artificer 5e → INT;
- Bard 2024 → CHA;
- Cleric 2024 → WIS;
- Druid 2024 → WIS;
- Paladin 2024 → CHA;
- Ranger 2024 → WIS;
- Sorcerer 2024 → CHA;
- Warlock 2024 → CHA;
- Wizard/Mago 2024 → INT.

The metadata only activates for catalog keys that still exist in `CharacterClassCatalog`.

Deliberate non-goals:

- no spell-list legality engine;
- no subclass progression engine;
- no automatic Eldritch Knight / Arcane Trickster inference;
- no multiclass spell-slot legality calculation;
- no name-based guessing for custom/homebrew classes.

## 3. Reconciliation contract

`reconcileCharacterSpellcastingBootstrap(...)` now provides the shared compatibility rule:

- canonical caster ownership is matched by exact `linkedClassId`, never display name;
- an existing class-linked source is reused with the same source ID;
- an existing configured source profile is preserved unchanged;
- a missing profile is created with the bounded canonical casting ability;
- an existing profile whose ability is genuinely unconfigured receives the bounded ability while retaining save-DC/attack adjustments and legacy overrides;
- manual/homebrew and non-class sources are preserved verbatim;
- noncasters/custom classes do not invent sources;
- reconciliation is idempotent.

`needsCharacterSpellcastingBootstrap(...)` separately detects when a canonical class exists but the persisted source layer has not yet acquired its class-linked source.

## 4. Character editor integration

`CharacterEditorV4.kt` now uses the shared reconciliation in four places:

1. **Open/projection:** existing saved characters that predate T5 receive a non-destructive projected source/profile in the editor without an immediate background database write.
2. **Class changes:** reconciliation runs from the projected domain `settingsSheet.classes`, so adding a canonical caster during editing immediately makes source context available.
3. **Navigation:** while a canonical source is missing from persisted state, Conjuros is effectively enabled so a new/existing Mago is not trapped behind the old independent `spellcasterEnabled` flag.
4. **Save:** the normal Save path persists reconciled sources and source-ID-keyed profiles, then filters profiles against the source IDs actually returned by the saved sheet.

The bootstrap comparison baseline uses the same projected sources/profiles, so merely opening an old canonical caster does not create a false unsaved-change state.

Once the canonical source has been persisted, the existing explicit visibility setting is not permanently overridden merely because the class remains; the forced enablement is specifically the missing-bootstrap bridge.

## 5. Compatibility guarantees

The T5 repair intentionally preserves:

- compatible existing `CharacterSpellcastingSource.id` values;
- existing spell associations that reference those source IDs;
- configured casting abilities and per-source save-DC / spell-attack adjustments;
- persistence/import/export schema and storage shape;
- existing saved characters;
- manual/homebrew `OTHER` spell sources;
- the richer source/profile overlay as the owner of casting configuration.

This is projection/reconciliation, not a destructive migration of saved characters.

## 6. Focused automation

Added `CharacterSpellcastingBootstrapTest.kt` covering:

- Mago/Wizard creates a class-linked source and INT profile when missing;
- existing linked source identity and configured profile are preserved;
- an unconfigured Cleric profile receives WIS without losing adjustments/legacy overrides;
- noncaster/custom classes do not invent sources;
- missing canonical source detection;
- multi-source reconciliation remains idempotent and retains manual sources.

Added permanent `scripts/check_player_spellcasting_bootstrap.py`, which guards:

- the bounded canonical class/ability map;
- exact class-ID source matching;
- configured-profile preservation;
- projected-domain class reconciliation in the editor;
- one-time missing-source Conjuros availability;
- save-path source/profile persistence.

A first compile attempt exposed one adapter mismatch (`ClassLevelDraftV4` vs domain `CharacterClassLevel`). The correction deliberately moved class-change reconciliation to the existing `settingsSheet.classes` domain projection. The shared API was not weakened and no duplicate class conversion was introduced.

## 7. Authoritative automation evidence

Temporary CI migration machinery was used only to land the single editor integration after a successful workspace build, then removed. The final normal workflow is read-only again; only the permanent T5 guard remains.

Authoritative steady-state run:

- Workflow: `Scaffold checks`;
- Run ID: `34794589758`;
- Run number: `1560`;
- Head SHA: `3774c53f5189ebd535cc1b73ec18493e268e5d9f`;
- Conclusion: **SUCCESS**;
- backend/type-check: **SUCCESS**;
- compact Player geometry guard: **SUCCESS**;
- Player reorder stability guard: **SUCCESS**;
- Player checkbox consistency guard: **SUCCESS**;
- Player spellcasting bootstrap guard: **SUCCESS**;
- Kotlin/shared tests: **SUCCESS**;
- Android build: **SUCCESS**;
- Desktop build: **SUCCESS**;
- Android debug APK upload: **SUCCESS**;
- artifact ID `10329072751` / `dnd-custom-aid-debug-apk`;
- artifact size `13,639,065` bytes;
- GitHub Actions artifact digest `sha256:26fe76650eb90a56ac2b5b239a3fd75ccb1b70bc2faf41003fac316ed5a04aae`.

The artifact digest is the GitHub Actions artifact digest; it is not relabelled as an independently computed APK-file SHA-256.

## 8. QA status after T5

T5 is **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. It is not physically PASS yet.

On the future consolidated physical-QA candidate, targeted T5 revalidation should include:

### Phone

- canonical Mago/Mage has Conjuros/source context without manually creating a second source;
- source uses the expected default INT casting profile unless an existing configured profile should be preserved;
- add a spell, associate it, save, leave and reopen;
- source identity/association remains stable across reopen;
- representative manual/homebrew source still coexists correctly.

### Tablet

- repeat the Mago source-context/add/save/reopen path in portrait and landscape;
- revalidate the previously blocked P17 check 18 sticky/source-context behavior after source context exists;
- combine source/prepared responsive-group validation from Round 3 where practical rather than replaying unrelated tablet checks.

Do not replay the complete 23-phone / 18-tablet discovery suites.

## 9. Project gate / next repair family

The exact frozen physical baseline remains `0.4.0-preqa.12 / 41200`; T5 does not create a new physical-QA candidate by itself.

Phase 4A remains OPEN. No P18 exists. DM implementation remains blocked until explicit owner Phase 4A closure.

**Next repair family: T6 class-editor numeric keyboard + standard die / `Otro…` selector.**

After that bounded implementation/test round, update its dedicated checkpoint plus `PROJECT_STATE.md`, `LATEST.md`, and `TESTING.md` before proceeding.
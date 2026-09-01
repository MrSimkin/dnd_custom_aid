# Increment G — Conjuros source wiring checkpoint

**Date:** 2026-09-01  
**Branch:** `implementation/character-data-foundation`  
**Promoted wiring commit:** `342ba8bf69945b118bc755f73d806303e2e0e6c1`

## Wired behavior

The already-green Increment G source-management component is now integrated into the character editor.

Editor integration adds:
- recreation-safe spellcasting draft state containing sources + conceptual spells;
- draft decoding in the editor;
- central draft updater;
- spellcasting source/spell collections included in the existing character Save transaction;
- spellcasting draft reset from repository-normalized state after Save;
- current draft class rows mapped to stable ID/name options for optional source linking;
- replacement of only the temporary `Conjuros` shell with `CharacterSpellsTabV4`.

Not changed in Increment G:
- Quick Magic profile;
- spell-slot state or shared-slot integration;
- spell CRUD/search/prepared controls (Increment H);
- Notes shell (Increment J).

## Safety validation

Large-file editing used an asserted temporary safety branch. The patch changed exactly one production file:
- `CharacterEditorV4.kt`
- diff: +30 / -3
- validated editor blob: `3f54bc803d926f3d950f00e565904c7248ee184d`

Safety-branch scaffold validation:
- workflow run #312
- run ID: `33462642460`
- backend: PASS
- shared/Kotlin tests + Android/desktop builds: PASS
- Android debug APK upload: PASS

Only the validated editor blob was promoted; temporary workflow, trigger and validation-marker files were not promoted.

## Next exact step

Add focused shared/repository Gate G coverage for source rename/reorder persistence and association-safe deletion, then run the final clean implementation-branch Gate G. UI touch/visual behavior remains later owner/device QA.

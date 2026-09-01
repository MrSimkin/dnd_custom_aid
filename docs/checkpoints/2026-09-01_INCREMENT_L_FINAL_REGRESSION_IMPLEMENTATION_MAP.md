# Increment L — Final regression / owner QA target implementation map

**Date:** 2026-09-01  
**Branch:** `tmp/increment-l-final-regression-qa-target`  
**Baseline:** promoted Increment K head `a43526a1a0ae9d30a0b53023fa4a8b9ee1836f02`

## Purpose

Increment L is the final automated consolidation increment from the approved next-build package. It adds no new product behavior.

Required outcome:

1. run all existing repository/unit/migration tests;
2. add regression coverage for any remaining technically practical persistence/migration/ownership gap;
3. verify run-#180 data paths still coexist with all new Phase 4 domains;
4. run the standard backend + shared + Android + desktop gate;
5. generate one clearly identified APK artifact;
6. record the exact tested commit, workflow run, artifact ID and digest;
7. publish the exact owner phone-QA checklist without claiming those device checks as automated PASS.

## Existing coverage audit

Current tests already provide substantial migration/domain coverage:

- `CharacterFollowupFoundationTest`: legacy/follow-up persistence and v3 migration preservation;
- `CharacterNextBuildFoundationTest`: new-domain defaults, multi-source spell ownership/soft class unlink, and run-#180/v4 caster migration without inventing new domains;
- `CharacterSpellcastingSourceManagementTest`: source ownership/deletion semantics;
- `CharacterSpellListPersistenceTest`: conceptual spells, source associations/prepared state, order/edit/delete;
- `CharacterSpellSlotIntegrationTest`: Quick Magic/Conjuros shared slots and hide/show preservation;
- `CharacterNotesPersistenceTest`: large notes, titled-note order/edit/delete across disk reopen.

The remaining useful gap is a single holistic, file-backed current-schema round-trip that stores representative run-#180 fields and all new Phase 4 domains on the same character, closes the database, reopens it, and verifies the combined state is preserved together.

## Planned L code change

Add one test file only:

`shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterPhase4FinalRegressionTest.kt`

The test should cover representative values from:

- core abilities/combat stats/status;
- class and hit-die state;
- saving throws and skills;
- proficiency/derived adjustment fields;
- Quick Magic profile and slots;
- combat entries;
- inventory including special/equipped/attuned/location/weight fields;
- currencies;
- caster visibility;
- Background;
- Traits;
- spellcasting sources and multi-source conceptual spell/prepared state;
- general and titled Notes.

Then close/reopen the SQLite file and assert identity, order and representative values across both legacy and Phase 4 domains.

No production source, schema, migration, or UI change is planned for L unless the regression/gate exposes a concrete defect.

## Gate L

The authoritative candidate gate remains `Scaffold checks`:

- backend install/type-check;
- full `:shared:desktopTest`;
- Android debug assembly;
- desktop build;
- APK upload.

## Owner QA boundary

The final owner QA follows the approved 42-step sequence in `docs/handoffs/2026-08-31_NEXT_BUILD_CONSOLIDATED_IMPLEMENTATION_PACKAGE.md`, covering migration preservation, navigation/settings, corrective backlog, Trasfondo, Rasgos, Conjuros, Notas and final resilience.

The project does not become merge-eligible merely because Gate L is green. Final owner phone QA must have no unresolved blocking defect, known non-blocking limitations must be recorded, and the exact tested commit/APK must remain checkpointed.

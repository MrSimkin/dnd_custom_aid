# Next-build Increment A — Baseline verified

**Status:** PASS / Gate A checkpoint  
**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Verified starting point

- Branch HEAD before this checkpoint: `e6a1a5006356501e5f04b52a31a34bdbce47e6c8`.
- Consolidated implementation package: `docs/handoffs/2026-08-31_NEXT_BUILD_CONSOLIDATED_IMPLEMENTATION_PACKAGE.md` at that HEAD.
- Scaffold checks run for that HEAD: run `#209`, workflow-run ID `33448644906`.
- `backend` job: PASS.
- `kotlin` job, including build/tests and Android debug APK upload: PASS.
- Accepted run-#180 implementation target remains `8be69ce94a0ce613cc29e3752e40bcc365c81b47`; run #180 / workflow-run ID `33436382484` is confirmed completed successfully.

## Production-code continuity verification

A compare from run-#180 implementation target `8be69ce94a0ce613cc29e3752e40bcc365c81b47` to pre-checkpoint HEAD `e6a1a5006356501e5f04b52a31a34bdbce47e6c8` shows the branch is ahead only by documentation/decision/handoff additions. No Android/shared production-code file changed in that interval.

Critical recovery anchors remain intact:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt`
  - current blob: `96510441bc8781a5df2f8970d08c372ca03693fa`
  - this matches the exact recovered pre-truncation blob.
- `AGENTS.md`
  - current blob: `77cc9fa2104d656ef106728d901c545f1d48c91c`
  - permanent checkpoint/recovery rules remain active.

## Exact existing implementation surfaces

### Android editor / presentation

Primary existing files expected to be touched by Increment B and later integration:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt`
  - General, Habilidades, Quick Magic and current character-sheet tab composition.
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatTabV4.kt`
  - Combat presentation/editor/reorder behavior.
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatDraftCodecV4.kt`
  - Combat draft conversion where relevant.
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEquipmentTabV4.kt`
  - Equipment presentation/editor/reorder/currency behavior.
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEquipmentDraftCodecV4.kt`
  - Equipment draft conversion where relevant.
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterLayoutV4.kt`
  - shared responsive-layout decisions/helpers.
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/IconControls.kt`
  - vector/icon-only control surface.
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt`
  - app-level navigation/settings shell and future PC Settings integration as required.
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt`
  - app-wide font/theme/text-scale preferences; remains separate from character-level PC Settings.

### Shared character domain and persistence

Primary existing files expected to be extended by Increment C:

- `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterSheet.kt`
  - current shared character model.
- `shared/src/commonMain/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterRepository.kt`
  - current persistence mapping/character repository.
- `shared/src/commonMain/sqldelight/io/github/mrsimkin/dndcustomaid/shared/db/Character.sq`
  - current character/class/save/skill/spell-slot/combat/inventory/currency schema and queries.
- `shared/src/commonMain/sqldelight/io/github/mrsimkin/dndcustomaid/shared/db/1.sqm`
- `shared/src/commonMain/sqldelight/io/github/mrsimkin/dndcustomaid/shared/db/2.sqm`
- `shared/src/commonMain/sqldelight/io/github/mrsimkin/dndcustomaid/shared/db/3.sqm`
  - existing migration chain; next schema work should add a new migration rather than rewrite history.

### Existing tests to extend

- `shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterFollowupFoundationTest.kt`
- `shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterRepositoryTest.kt`

Additional focused tests/files may be added rather than forcing unrelated logic into these files.

## Planned new implementation surfaces

Prefer new focused files for new domains rather than further inflating `CharacterEditorV4.kt`, where architecture permits. Candidate responsibilities include:

- top-level scrollable character tab strip / PC Settings screen;
- `Trasfondo` tab;
- `Rasgos` tab;
- `Conjuros` tab/source management/spell editor;
- `Notas` tab;
- focused draft/editor helpers for those domains.

Exact new filenames are implementation choices, not product requirements, but new domain code should remain separated enough to avoid another monolithic editor file.

## Safety / sequencing result

- Gate A baseline: GREEN.
- No recovery discrepancy found.
- No production-code drift found after run #180.
- Run-#180 corrective UX Increment B may start.
- Before every existing file edit, refetch its current blob/content first.
- Avoid GitHub contents-API whole-file replacement of `CharacterEditorV4.kt` for narrow changes; use a local exact copy + narrow scripted patch and Git blob/tree commit workflow if that large file must change.
- Every B1/B2/B3 implementation increment receives its own durable checkpoint and CI gate.
- The unresolved permanent font replacements remain an owner-audition item for B3; this does not block B1 or B2.

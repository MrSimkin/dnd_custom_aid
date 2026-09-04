# Phase 4 — Batch J3 Android backup file UX checkpoint

**Checkpoint date:** 2026-09-04  
**Phase:** Phase 4 Character Foundation Closure  
**Batch:** J — own-format backup/import  
**Sub-batch:** J3 — Android local-file export/import UX  
**Active product branch:** `tmp/phase4-j-backup-import`  
**J3 product commit:** `b40fe1d2c812ee48b44470468b43dfca6c42de5b`  
**J3 product tree:** `fcdedc3b151e5d15213bc101ef98f4ed78aa5aad`  
**Status:** GREEN for implementation/automated gate; real document-picker behavior remains part of later owner device QA

## 1. Scope delivered

J3 wires the already-proven J1/J2 backup semantics into Android without creating a second storage model.

### Import

Import is exposed at campaign character-list context because v1 semantics always restore a backup as a new character copy inside the currently selected campaign.

Android uses `ActivityResultContracts.OpenDocument` and the system document picker. The selected document is read as UTF-8 and decoded through the shared `CharacterBackupCodec`.

On a valid backup, Android calls the shared `CharacterBackupRepository.importAsCopy(...)` with the selected campaign as destination. Successful import reloads the character list and explicitly tells the user in Spanish that a **new local copy** was created and that no existing character was replaced. The user may immediately open the restored copy.

Malformed/unsupported backup errors reuse the controlled Spanish shared-codec messages. File-read or repository failures are reported through safe Spanish UI messages.

### Export

Export is character-specific and lives in PC Settings under `Respaldo local`.

Android uses `ActivityResultContracts.CreateDocument("application/json")` and writes the encoded shared backup document through the system-selected URI as UTF-8.

Export deliberately uses the latest fully saved authoritative character state. If the editor currently has unsaved draft changes, the export action is disabled and explains that the user must save or discard those edits first. This prevents an ambiguous backup that silently mixes saved authority with unsaved UI draft state.

The suggested filename is derived from the saved character name and uses the suffix `_respaldo_dnd-custom-aid.json`.

## 2. Wiring

`MainActivity` now creates one `CharacterBackupRepository` from the existing SQLDelight database and passes it to both relevant Android surfaces:

- `CharacterListScreen` for restore-as-copy import;
- `CharacterEditorScreenV4` for character-specific export.

No new database/schema, Android storage permission, cloud service or parallel character model is introduced.

## 3. Guard behavior

The first guarded workflow attempt, run `33878739317`, stopped before compilation because the staging helper expected two textually identical `CharacterListScreen` call sites in `MainActivity`, while the second call had different indentation/context.

That was a staging-patch matching defect only. No product code was committed.

The helper was narrowed to the two exact real call shapes and the same guarded process was retried.

## 4. Automated gate — GREEN

Controlling guarded workflow:

- workflow run `33878945969` — PASS;
- staging helper repair — PASS;
- exact Android patch application — PASS;
- stable CI debug-keystore preparation — PASS;
- `gradle :shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace` — PASS;
- backend `npm install` + `npm run check` — PASS;
- self-clean/product commit — PASS.

The workflow committed the four intended Android product files only after all gates passed and removed its temporary helper/workflow files.

## 5. Product files changed

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt`
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterUi.kt`
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt`
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterPcSettingsClosureV4.kt`

Temporary `.github/j3_*` staging helpers are not part of the product tree after the successful gate.

## 6. What automated verification does not prove

CI proves compilation, existing shared regression tests, Android assembly, Desktop compatibility and backend type-checking.

It does **not** prove real Android document-provider behavior, picker UX, provider-specific URI handling, orientation ergonomics or actual export/import interaction on the owner's phone/tablet. Those remain explicit later owner QA items under the Phase 4 acceptance boundary.

## 7. Remaining Batch J work

J1, J2 and J3 are now technically implemented.

Before declaring Batch J fully GREEN:

1. close the small J1 malformed-header type-safety robustness item discovered during review;
2. run a final exact-clean-tree full J gate on the resulting product commit;
3. update current-state continuity to mark Batch J complete and transition to Batch K only after that exact gate is green.

The header robustness item is intentionally narrow: syntactically valid JSON with non-primitive `format` or `version` values must return controlled backup failures rather than allowing JSON accessor exceptions to escape.

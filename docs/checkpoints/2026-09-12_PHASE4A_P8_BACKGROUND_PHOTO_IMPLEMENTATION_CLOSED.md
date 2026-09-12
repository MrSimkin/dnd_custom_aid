# Phase 4A — P8 Trasfondo photo UX implementation CLOSED

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**P7 closure baseline:** `d190c34717f75cc4449ec30da8f427afd741f1b9`  
**Implementation evidence head before this checkpoint:** `ccfe30479671e335a5668f2cd6cea40adfd313ca`  
**Status:** **IMPLEMENTATION CLOSED / READY FOR OWNER QA IN THE REPAIRED BUILD**

## Authority and scope

This checkpoint closes implementation of P8 — **Trasfondo photo UX: restore simple image tiles while preserving durable storage** — under:

- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`;
- `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md`.

P1–P7 remain independently closed and were not reopened by this repair. In particular, the P7 closure baseline is:

`docs/checkpoints/2026-09-11_PHASE4A_P7_PROVENANCE_IMPLEMENTATION_CLOSED.md`

No DM implementation, `main` synchronization, destructive history rewrite, reset, force push, or P9 implementation is authorized or performed by this checkpoint.

## Implementation commits

P8 implementation after the P7 closure baseline consists of two focused commits:

1. `f809aecb1bcd45109df70e260093e826ba9f1de8` — `test: prove P8 image persistence and backup survival`
2. `ccfe30479671e335a5668f2cd6cea40adfd313ca` — `fix: restore P8 background image tile UX`

The aggregate diff from `d190c34717f75cc4449ec30da8f427afd741f1b9` through `ccfe30479671e335a5668f2cd6cea40adfd313ca` is limited to:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterBackgroundTabV4.kt`;
- `shared/src/desktopTest/kotlin/io/github/mrsimkin/dndcustomaid/shared/character/CharacterBackgroundImagePersistenceTest.kt`.

No P1–P7 implementation surface was modified.

## Restored owner-facing tile contract

The normal Trasfondo sheet now presents exactly two side-by-side image slots using the approved labels:

- `Imagen principal`;
- `Imagen secundaria`.

Each slot uses the existing fixed `4:5` tile footprint. Loading an image does not replace that geometry with the image's intrinsic dimensions.

The normal sheet remains intentionally simple:

- there is no persistence-era outer image card/header/control row;
- there is no permanent filename presentation;
- there is no permanent storage/persistence explanation;
- there are no permanent `Añadir`, `Cambiar` or `Eliminar` buttons surrounding a filled tile.

## Empty-slot and filled-slot interaction

The existing direct interaction semantics were preserved because they already matched the P8 contract:

- an empty tile is itself the image-selection target when structural editing is available;
- no separate permanent `Añadir` button is required;
- a filled tile opens the larger viewer on normal tap;
- tapping a filled tile does not immediately replace its image.

Structural-editing restrictions remain authoritative:

- an empty read-only tile does not expose an active picker action;
- a filled tile may still be opened for inspection in read-only state;
- replace/remove actions are shown only when structural editing is available.

## Thumbnail versus viewer presentation

The compact sheet projection keeps the approved split of responsibilities:

- thumbnail: fixed `4:5` footprint with `ContentScale.Crop`, providing a center-cropped compact tile;
- enlarged viewer: `ContentScale.Fit`, showing the complete image without cropping while preserving its source aspect ratio within the available viewer area.

The viewer no longer reserves a separate permanent edit row beneath the image. Instead:

- title / `Cerrar` are overlaid inside the viewer area;
- when editing is allowed, `Cambiar` / `Eliminar` are overlaid inside the viewer area at the lower edge;
- when editing is unavailable, those structural controls are absent.

This restores the requested image-first viewer while retaining explicit management actions where they belong.

## Durable app-owned storage retained

P8 deliberately does **not** revert the storage work that originally motivated the persistence-era redesign.

`CharacterBackgroundImage` remains an app-owned serializable payload containing:

- stable internal image identity;
- slot identity (`PRIMARY` / `SECONDARY`);
- MIME type;
- encoded image payload;
- optional original filename as metadata.

The Android import path continues to decode, resize when necessary, compress/copy, and persist image bytes into the app-owned aggregate. The normal character sheet does not expose those implementation details as permanent explanatory UI.

No external content URI is persisted as the authoritative image reference.

## Reopen / restart persistence evidence

New focused test:

`CharacterBackgroundImagePersistenceTest.appOwnedBackgroundImagesSurviveDatabaseReopen`

The test uses a real file-backed SQLite database rather than an in-memory round trip:

1. creates a character;
2. persists both PRIMARY and SECONDARY app-owned image payloads;
3. closes the database driver;
4. opens a new driver/repository against the same database file;
5. verifies both images survive unchanged and remain attached to the correct slots.

This directly covers the P8 persistence boundary corresponding to save/reopen and application-process restart of repository state.

The pre-existing `CharacterSuccessorRepositoryTest.backgroundImagesRoundTripAsAppOwnedPayloads` remains compatible and continues to cover ordinary successor-state persistence.

## Backup / export / import evidence

New focused test:

`CharacterBackgroundImagePersistenceTest.backupCodecAndRestoreAsCopyCarryBothAppOwnedImages`

The test exercises the complete app-owned backup path:

1. persist both image slots on the source character;
2. `CharacterBackupRepository.exportCharacter`;
3. `CharacterBackupCodec.encode`;
4. `CharacterBackupCodec.decode`;
5. `CharacterBackupRepository.importAsCopy` into another campaign;
6. verify both slots exist after import;
7. verify imported internal image IDs are remapped rather than reused;
8. verify MIME type, encoded payload and original-name metadata are preserved;
9. reload imported successor state and verify the persisted imported images match the import result.

Production backup behavior already included `CharacterSuccessorState.backgroundImages` and remapped image IDs for restore-as-copy. P8 therefore strengthened regression evidence rather than rewriting an already-correct backup layer.

## Automated validation

The final P8 implementation head before this checkpoint, `ccfe30479671e335a5668f2cd6cea40adfd313ca`, was validated by GitHub Actions:

- workflow: `Scaffold checks`;
- workflow run: `34670074975`;
- backend / Worker type-check: **SUCCESS**;
- Kotlin build and shared desktop tests: **SUCCESS**;
- Android debug assembly: **SUCCESS**;
- desktop build: **SUCCESS**;
- Android debug APK upload: **SUCCESS**.

The Kotlin gate therefore executed the repository's standard build/test surfaces with the new P8 persistence tests included.

## P8 contract audit conclusion

Implementation evidence now covers the accepted P8 contract:

- exactly two coherent side-by-side Trasfondo image slots;
- exact labels `Imagen principal` / `Imagen secundaria`;
- fixed `4:5` tile geometry independent of source-image dimensions;
- whole empty tile as the add/select target when editing is allowed;
- filled-tile tap opens the viewer rather than the picker;
- center-cropped thumbnail through `ContentScale.Crop`;
- complete uncropped viewer image through `ContentScale.Fit`;
- replace/delete controls contained as viewer overlays rather than permanent sheet chrome;
- structural editing restrictions retained;
- no permanent filename, storage explanation or image-management action row in the normal sheet;
- app-owned image payload retained rather than reverting to fragile external URI ownership;
- persistence proven across an actual database close/reopen cycle;
- backup encode/decode/import-as-copy survival proven for both image slots;
- internal image IDs remapped on imported copies while image payload metadata is preserved;
- P1–P7 unchanged by the P8 repair.

No additional P8 production gap was found during the closure audit.

## Automated UI boundary

This branch currently has no `androidApp/src/androidTest` / Compose instrumentation test harness. P8 does not introduce a new cross-project instrumentation infrastructure solely to manufacture an automated visual claim.

Accordingly:

- layout/interaction structure is implementation-evidenced by the reviewed Compose code and successful Android compilation;
- persistence/restart/backup behavior is additionally covered by automated shared tests;
- exact visual appearance, touch behavior on physical devices, responsive phone/tablet rendering, image crop perception and overlay usability remain owner/device acceptance items.

The absence of an instrumentation harness must **not** be misrepresented as automated proof of physical rendering.

## Acceptance boundary

**IMPLEMENTATION CLOSED** means the approved P8 behavior is implemented, its persistence contract is regression-tested, the repository builds successfully, and no known P8 implementation gap remains.

It does **not** mean owner acceptance has already occurred.

The repaired build must still be checked by the owner on the intended phone/tablet surfaces for the visual and interaction aspects of P8, including:

- the perceived `4:5` tile footprint;
- the simplicity of the two-tile Trasfondo presentation;
- empty-tile picker behavior;
- filled-tile viewer behavior;
- thumbnail crop versus complete viewer image;
- viewer overlay placement and usability;
- read-only/Table Mode behavior where relevant.

No P9 work has been started by this closure.

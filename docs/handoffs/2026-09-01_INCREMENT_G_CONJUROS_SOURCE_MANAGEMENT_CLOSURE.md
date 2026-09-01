# Increment G — Conjuros source-management closure

**Date:** 2026-09-01  
**Branch:** `implementation/character-data-foundation`  
**Final tested head:** `4addd35022d89775bb81ba583e132dad6a8c866d`

## Implemented scope

Increment G replaces the temporary `Conjuros` shell with the approved source-management/navigation foundation:
- always-present `Todos` view;
- horizontally scrollable single-line source subtabs;
- source selection by stable source ID;
- add, rename/edit, reorder and delete source;
- optional link to a current PC class row;
- custom/unlinked sources always allowed;
- no automatic source creation from classes;
- deleting the selected source falls back to `Todos`;
- explicit stronger warning when deleting a source that still has spell associations;
- source deletion removes only that source's associations, preserving conceptual spells and any other associations;
- source order persisted by list order;
- linked-class deletion soft-unlinks the source through existing repository normalization;
- recreation-safe spellcasting draft state integrated into the central character Save transaction.

Increment G deliberately does not implement spell CRUD/search/level sections/prepared toggling; those belong to Increment H. Quick Magic/slot integration remains Increment I.

## Defect caught during isolated implementation

Initial isolated run #302 / ID `33461787460` caught one Android compile defect before editor wiring: an invalid explicit Compose `layout.weight` import.

It was corrected in isolation and validated before promotion. No large editor file was involved in that defect.

## Validation evidence

Corrected isolated component:
- run #306 / ID `33462081195`: PASS
- clean implementation-branch run #307 / ID `33462316805`: PASS

Editor safety-branch integration:
- run #312 / ID `33462642460`: PASS
- production diff restricted to `CharacterEditorV4.kt` (+30/-3)
- validated editor blob: `3f54bc803d926f3d950f00e565904c7248ee184d`

Focused repository Gate G test:
- `CharacterSpellcastingSourceManagementTest.kt`
- verifies stable IDs through rename/reorder;
- verifies persisted source order;
- verifies deleting one source filters only that association while preserving the conceptual spell and remaining association/prepared state;
- verifies deleting the linked class soft-unlinks the source while preserving source/spell data.

Final Gate G:
- workflow run #315
- run ID: `33462906470`
- tested head: `4addd35022d89775bb81ba583e132dad6a8c866d`
- backend: PASS
- full shared/Kotlin tests: PASS
- Android + desktop builds: PASS
- Android debug APK upload: PASS
- artifact: `dnd-custom-aid-debug-apk`
- artifact ID: `9783859563`
- artifact ZIP digest: `sha256:676efc73f93486ccfdc7104031c219482ae728b37eca9fa577c4929d20e158d9`

## Validation boundary

Automated coverage verifies persistence/ownership behavior and build integrity. The actual touch ergonomics of the subordinate source strip, drag interaction, dialogs, and visual fallback to `Todos` remain part of later owner/device QA and the responsive/accessibility integration pass; they are not represented here as automated interaction PASS.

## Status

**Increment G is closed.**

Next implementation boundary: **Increment H — Conjuros spell list/details**.

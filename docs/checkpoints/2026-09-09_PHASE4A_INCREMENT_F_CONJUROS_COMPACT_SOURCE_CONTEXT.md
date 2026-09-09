# Phase 4A — Increment F: Conjuros compact source context

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** AUTOMATED GATE GREEN / OWNER DEVICE RETEST FAILED / REPAIR REQUIRED  
**Pre-F baseline:** `f7f0389fde4fd3a72ca8f8a547dae38255825266`  
**Active Increment F source commit:** `0e25fb0a84c2be63a16fee709bd7e96d7469373b`  
**Authoritative integrated validation commit:** `4ba248f7749a062ac40e1e0c46c0687f4caccbbf`  
**Authoritative workflow:** `34376169597` — SUCCESS

## Scope implemented

Increment F replaced the vertically expensive Conjuros source-selector/header stack with one compact collection context while preserving the existing source model and per-source spellcasting authority.

### F1 — compact source context

- the permanent horizontal source `LazyRow` and separate divider/settings footprint were removed;
- source selection now lives inside the shared compact collection toolbar;
- the selected source displays its own casting ability abbreviation, spell save DC and spell attack modifier;
- `Todos los conjuros` intentionally shows no fake/global casting statistics;
- source selection and `Gestionar fuentes…` are transient dropdown actions;
- ordinary spell-list content receives more permanent viewport height, including phone landscape.

### F2 — compact search/filter/add behavior

`CharacterCollectionToolbarV4` received backward-compatible opt-in controls:

- `collapsibleSearch = true` opens Conjuros search in the same fixed toolbar row;
- while search is expanded, source context is temporarily replaced by the search field plus `Cerrar`;
- non-empty collapsed search remains indicated by `Buscar •`;
- `showItemCount = false` preserves horizontal room;
- Manual/A–Z, filters and add remain functionally available;
- other toolbar consumers retain previous behavior by default.

### F3 — source authority preserved

- no new shared/domain state was introduced;
- no generic free-text `Fuente` field was added;
- spell sources remain `CharacterSpellcastingSource` identities that may link to a class or remain custom/unlinked;
- source add/edit/delete/reorder semantics remain intact;
- source-specific spellcasting values continue to project from the canonical successor spellcasting profiles through `generalSpellcastingRows(successorState)`;
- no global spellcasting ability/DC/attack authority was recreated.

### F4 — editor numeric normalization

- spell-level input uses shared `normalizeCharacterUnsignedIntegerInput(..., maxDigits = 1)` in both editor paths;
- the old private digit-taking workaround was removed;
- shared tests cover `05 -> 5` and single-digit behavior.

### F5 — contextual help

- source-system explanatory copy uses the existing `CharacterHelpV4` authority;
- the source dropdown explains that casting ability, save DC and spell attack belong to each source.

## Automated evidence

A self-removing one-off maintenance helper was used because the connected GitHub API could write the repository while the execution container had no direct GitHub checkout/network path.

- staging commit: `888f4d22aef22041e9ff50c934fced2fe3c54c46`;
- first helper workflow `34375078206` failed safely during `git apply --check`; no source delta was committed;
- transport repair commit: `52bee78bbef00ec107c06e195ce93377cb34e475`;
- successful helper workflow: `34375529779` — SUCCESS;
- patch `git apply --check`: PASS;
- `:shared:desktopTest`: PASS;
- `:androidApp:compileDebugKotlin`: PASS;
- resulting source commit: `0e25fb0a84c2be63a16fee709bd7e96d7469373b`;
- helper workflow and `.maintenance` files self-removed;
- net product-code diff from pre-F baseline contains exactly four Android source files.

### Final integrated gate

Workflow `34376169597` — **SUCCESS** on validation commit `4ba248f7749a062ac40e1e0c46c0687f4caccbbf`.

Verified together:

- backend/type-check: PASS;
- shared/Kotlin tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop compilation/build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10114037181`;
- name `dnd-custom-aid-debug-apk`;
- size `13235995` bytes;
- ZIP digest `sha256:a0aa9ddfeeaa74bffa17ecb15dd1d3e0880c7238159c9058bfc80beaeec5041d`;
- generated `2026-09-09T16:23:40Z`.

## Owner physical retest — Redmi Note 11 Pro 5G

The first Increment F owner-device retest was performed on 2026-09-09 and **did not accept Increment F**.

Confirmed observations:

- filters behave acceptably;
- spell drag/reorder is severely incorrect and can skip two cards;
- the visible three-line drag handle is contrary to the owner interaction model: reorder must start by long-pressing and dragging the card itself, not by requiring a handle;
- the current star/favorite presentation is visually unacceptable;
- portrait toolbar controls are visually awkward/over-compressed;
- spell cards still waste vertical space and split controls across unnecessary rows;
- the spell editor still fails the app-wide row-efficiency requirement: several short controls that naturally fit together remain on separate full-width rows;
- the keyboard still hides part of editor content, so the shared IME-safe editor is not yet physically safe on the owner phone.

### Technical diagnosis after retest

The drag failure has concrete implementation causes in `SpellRowG2`:

- it still uses its own direct `detectDragGesturesAfterLongPress` path instead of shared `characterLongPressDragV4`;
- its reorder threshold is hard-coded to `66.dp`, unrelated to actual rendered card height;
- a `while` loop can execute multiple logical moves from one pointer update;
- the pointer coroutine can retain stale reorder callbacks after a live move/recomposition, which the shared drag primitive was explicitly designed to prevent.

The drag modifier is currently attached only to `StableDragHandle`, explaining why the rest of the card does not initiate reorder.

The star is currently rendered as raw Unicode `★` / `☆` text inside a `TextButton`, explaining the inconsistent visual style.

The spell card's right-side action column stacks Prepared, favorite/delete, and duplicate across multiple vertical tiers, which drives unnecessary card height.

The editor currently renders `Nivel`, `Tiempo de lanzamiento`, `Alcance`, and `Duración` as separate full-width text-field rows. The next repair must explicitly consolidate naturally compatible short fields rather than waiting for the owner to enumerate obvious pairs. The V/S/M and Concentración/Ritual boolean controls also require a compact row-efficiency audit.

`CharacterImeSafeEditorDialog` currently applies outer `imePadding()` but combines it with a content-sized surface and a scroll body using `weight(..., fill = false)`; physical testing shows that this does not reliably keep the focused editor area above the keyboard.

## Required F repair pass before G

Do **not** begin Increment G. Repair Increment F first:

1. replace the private spell-row drag path with the shared stale-safe drag primitive;
2. make the card itself the long-press drag surface and remove the three-line drag handle from spell cards;
3. use measured rendered card geometry for reorder stepping and prevent one pointer event from producing accidental multi-step skips;
4. compact spell-card action layout into the minimum sensible number of rows while preserving touch targets;
5. replace raw Unicode favorite star text with a proper stable app icon/state control;
6. make the Conjuros portrait toolbar responsive without adding another permanent row;
7. redesign spell-editor short-field layout so naturally compatible fields share rows at phone width;
8. repair the shared IME-safe dialog so focused fields and fixed save/cancel controls remain usable with the keyboard visible;
9. run focused tests/compile, then the full integrated gate and produce a new Redmi retest APK.

## Acceptance boundary

Increment F remains **automated-green only for the previous implementation**. Owner-device acceptance has failed and the increment is reopened for repair. No Phase 4A closure, no Increment G start, and no tablet acceptance should be inferred from the green workflow.

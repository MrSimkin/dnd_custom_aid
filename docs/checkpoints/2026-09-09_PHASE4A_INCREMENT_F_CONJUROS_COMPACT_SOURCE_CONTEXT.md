# Phase 4A — Increment F: Conjuros compact source context

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** COMPLETE / AUTOMATED GATE GREEN / OWNER DEVICE ACCEPTANCE PENDING  
**Pre-F baseline:** `f7f0389fde4fd3a72ca8f8a547dae38255825266`  
**Active Increment F source commit:** `0e25fb0a84c2be63a16fee709bd7e96d7469373b`  
**Authoritative integrated validation commit:** `4ba248f7749a062ac40e1e0c46c0687f4caccbbf`  
**Authoritative workflow:** `34376169597` — SUCCESS

## Scope completed

Increment F replaces the vertically expensive Conjuros source-selector/header stack with one compact collection context while preserving the existing source model and per-source spellcasting authority.

### F1 — compact source context

- the permanent horizontal source `LazyRow` and its separate divider/settings footprint are removed from the top of Conjuros;
- source selection now lives inside the shared compact collection toolbar;
- the selected source displays its own casting ability abbreviation, spell save DC and spell attack modifier;
- `Todos los conjuros` intentionally shows no fake/global casting statistics;
- source selection and `Gestionar fuentes…` are transient dropdown actions rather than permanent stacked rows;
- ordinary spell-list content therefore receives substantially more permanent viewport height, including phone landscape.

### F2 — compact search/filter/add behavior

`CharacterCollectionToolbarV4` receives backward-compatible opt-in controls:

- `collapsibleSearch = true` allows Conjuros search to open in the same fixed toolbar row rather than adding another permanent row;
- while search is expanded, the source context is temporarily replaced by the search field plus `Cerrar`;
- when a non-empty search is collapsed, `Buscar •` preserves visible indication that a query remains active;
- `showItemCount = false` is used by Conjuros to preserve horizontal room;
- existing Manual/A–Z order, filter counting/menu behavior and compact `+` add action remain available;
- other toolbar consumers keep their previous behavior through default parameters.

### F3 — source authority preserved

- no new shared/domain state was introduced;
- no generic free-text `Fuente` field was added;
- spell sources remain `CharacterSpellcastingSource` identities that may link to a class or remain custom/unlinked;
- source add/edit/delete/reorder semantics remain intact;
- source-specific spellcasting values are projected from the existing canonical successor spellcasting profiles via `generalSpellcastingRows(successorState)`;
- no global spellcasting ability/DC/attack authority is recreated.

### F4 — editor numeric normalization and IME

- the existing IME-safe spell editor remains in use;
- spell-level input now uses shared `normalizeCharacterUnsignedIntegerInput(..., maxDigits = 1)` in both editor paths;
- the private digit-taking workaround is removed;
- existing shared tests cover the key normalization case `05 -> 5` as well as single-digit behavior.

### F5 — contextual help and drag boundary

- source-system explanatory copy now uses the existing `CharacterHelpV4` authority rather than creating a second explanation system;
- the source dropdown explains that casting ability, save DC and spell attack belong to each source;
- card/source drag mechanics were not redesigned by Increment F;
- card drag feel remains an explicit owner-device audition item rather than an automated acceptance claim.

## Focused implementation evidence

A self-removing one-off maintenance helper was used only because the connected GitHub API can write the repository while the execution container has no direct GitHub checkout/network path.

- staging commit: `888f4d22aef22041e9ff50c934fced2fe3c54c46`;
- first helper workflow `34375078206` failed safely during `git apply --check` because two transport chunks were not byte-identical; no source delta was committed;
- transport repair commit: `52bee78bbef00ec107c06e195ce93377cb34e475`;
- successful helper workflow: `34375529779` — SUCCESS;
- patch `git apply --check`: PASS;
- `:shared:desktopTest`: PASS;
- `:androidApp:compileDebugKotlin`: PASS;
- resulting source commit: `0e25fb0a84c2be63a16fee709bd7e96d7469373b`;
- helper workflow and `.maintenance` transport files self-removed in the source commit;
- net diff from the pre-F baseline contains exactly four Android source files.

## Final integrated gate

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

## Acceptance boundary

Increment F is technically complete and automated-green, but it is **not yet owner-device accepted** and is not a release boundary. Stop before Increment G and perform the planned early targeted Redmi Note 11 Pro 5G portrait/landscape Conjuros audition.

The owner retest must focus on permanent viewport footprint, ordinary spell-card visibility in landscape, source statistics/selection, transient search/filter behavior, editor/keyboard behavior, numeric level normalization and card drag feel. Tablet/wide redesign remains Increment I.

## Next action

Install the Increment F debug APK on the Redmi Note 11 Pro 5G and execute the targeted Conjuros portrait/landscape owner retest. Do not begin Increment G until that early F audition has been reviewed.

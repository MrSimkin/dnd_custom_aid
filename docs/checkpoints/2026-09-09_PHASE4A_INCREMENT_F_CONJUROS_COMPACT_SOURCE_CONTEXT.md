# Phase 4A — Increment F: Conjuros compact source context

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** IMPLEMENTED / FOCUSED GATE GREEN / FULL INTEGRATED GATE PENDING  
**Pre-F baseline:** `f7f0389fde4fd3a72ca8f8a547dae38255825266`  
**Active Increment F source commit:** `0e25fb0a84c2be63a16fee709bd7e96d7469373b`

## Scope implemented

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

## Full integrated gate

Pending on this checkpoint-triggered `Scaffold checks` run. The authoritative gate must verify together:

- backend/type-check;
- shared/Kotlin tests;
- Android debug assembly;
- desktop build;
- Android debug APK upload.

This checkpoint must be updated with the exact workflow run and Android artifact evidence before Increment F is called automated-green.

## Acceptance boundary

Increment F is implemented but is **not yet owner-device accepted** and is not a release boundary. After the integrated gate is green, stop before Increment G and prepare the early targeted Redmi Note 11 Pro 5G portrait/landscape Conjuros audition.

The owner retest must focus on permanent viewport footprint, ordinary spell-card visibility in landscape, source statistics/selection, transient search/filter behavior, editor/keyboard behavior, numeric level normalization and card drag feel. Tablet/wide redesign remains Increment I.

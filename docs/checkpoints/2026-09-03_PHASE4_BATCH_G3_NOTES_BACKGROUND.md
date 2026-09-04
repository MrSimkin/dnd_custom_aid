# Phase 4 — Batch G3 Notas + Trasfondo closure checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** PENDING INTEGRATION GATE  
**Canonical `main`:** untouched

## Scope

G3 closes the approved Notas + Trasfondo UX scope over the established durable models. No schema change is introduced.

Historical boundaries remain authoritative:

- Notas stays intentionally lightweight: one unrestricted General Notes field plus optional titled cards containing only title + content;
- no tags, dates, categories or knowledge-management metadata are introduced;
- Trasfondo retains background name/summary, Raza, Religión/Fe, narrative profile fields, long Story and the two honest image placeholders;
- no image-persistence expansion is introduced in G3.

## G3a — pure titled-note operations

Implementation:

- `dfaa11abaec5181714041f484e1f54d0c4c8d344` — normalize/move/duplicate helpers;
- `7d4d3e77efd9443f223034fd90f31fcc4c8c5611` — focused operation tests.

Controlling G3a workflow:

- `33823030581` — PASS across backend, shared/Kotlin tests, Android debug, Desktop and APK upload;
- artifact `dnd-custom-aid-debug-apk`, ID `9918996783`, digest `sha256:399c330a1c540a947a74755be0dd75a40fe5c0cc2cb784d329870dea44bfae51`.

Semantics established:

- normalization preserves the visible input sequence and rewrites dense `sortOrder` only;
- manual move changes only the requested position and is safe at boundaries;
- duplicate receives a fresh UUID, keeps content unchanged, appends `(copia)` to the title and receives appended order;
- no content classification, parsing or metadata inference is introduced.

## G3b — Android Notas + Trasfondo closure UI

UI integration commit:

- `1fbd6d0b4260c5a804ee2dae6ab3b71348d67d46` — `feat: integrate Batch G3 notes and background closure UI`.

Delivered Notas behavior:

- existing General Notes long-text field retained;
- existing 3-line titled-note previews retained;
- existing visible drag feedback + haptics retained;
- titled-note move now uses tested shared G3 helper semantics;
- titled-note Duplicate action added as a secondary action below the preview so phone title width remains available;
- Duplicate creates a fresh UUID, preserves content and appends at the end;
- explicit `rememberLazyListState()` is supplied to the Notes list so scroll/list context remains stable while IME-safe editors overlay;
- card tap remains the edit interaction; no generic Edit button was added;
- existing named delete confirmation and long-note IME-safe editor remain intact;
- existing one-column phone / two-column wide presentation remains intact.

Delivered Trasfondo behavior:

- background name, summary, Raza and Religión/Fe are unchanged;
- both honest image placeholders are unchanged;
- personality/ideals/bonds/flaws preview-card interaction is unchanged;
- Story now defaults to a compact 3-line preview;
- `Mostrar` / `Añadir` expands Story in place; `Ocultar` collapses it again;
- expanded Story remains directly editable using the same durable `background.story` value;
- long Story editor has bounded height plus internal scrolling guidance;
- no Story data is duplicated, summarized into storage or otherwise transformed.

## Existing regressions that must remain green

The full G3 gate must include existing persistence/migration protection, especially:

- `CharacterNotesPersistenceTest` — large General Notes, titled-card order/update/delete round trip;
- `CharacterBackgroundIdentityMigrationTest` — migration preserves existing background and Raza/Religión round trip;
- all shared repository/migration tests;
- Android debug assembly and Desktop build.

## Gate required

G3 is not GREEN until the controlling workflow on this checkpoint head passes:

- focused G3 note-operation tests;
- all shared desktop tests including Notes/Background persistence and migration regressions;
- Android debug assembly including the updated Notes/Background Compose surfaces;
- Desktop build;
- backend type-check;
- APK artifact upload.

If green, mark G3 GREEN, advance `docs/PROJECT_STATE.md` to **H1 — Artífice + Formas active**, and audit the existing shared module state before writing H1 UI. Do not begin H1 implementation from momentum before G3 is known green.

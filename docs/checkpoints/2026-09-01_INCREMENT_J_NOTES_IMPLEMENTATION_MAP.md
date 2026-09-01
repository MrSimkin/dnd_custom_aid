# Increment J — Notas implementation map

**Date:** 2026-09-01  
**Working branch:** `tmp/increment-j-notes-tab`  
**Verified baseline:** `4dd1e86b2ad62cea0789baede6bf20af8bae2b15` — Increment I closed/promoted; durable CI run `33465502292` green

## Approved boundary

The consolidated implementation package defines Increment J as the persistent `Notas` tab with a deliberately simple hybrid model:

- one prominent unrestricted `Notas generales` area;
- optional titled note cards below it;
- each titled note owns only title + content;
- add/edit/delete;
- manual drag-and-drop ordering;
- compact card title + content preview;
- no required dates, categories, tags, session metadata, or other structure;
- IME-safe editing;
- proportional wide-layout presentation.

The approved checkpoint is “Notes persistent UI complete.” Gate J covers large-text persistence/editing, titled-note reorder, and recreation/rotation safety.

## Existing persistence foundation

Increment C already established the required data model. No schema or migration is needed:

- `CharacterSheet.generalNotes: String` owns the unrestricted note area;
- `CharacterSheet.noteCards: List<CharacterNote>` owns titled notes;
- `CharacterNote` already contains stable UUID, title, content, and sort order only;
- `CharacterRepository.saveCharacter(...)` validates note identity/title, persists `generalNotes`, rewrites note cards in list order, and hydrates them ordered on read.

Therefore Increment J must not introduce a second notes persistence model.

## Android draft boundary

Follow the already-proven separate recreation-safe domain-draft pattern used by Combate/Equipo/Trasfondo/Rasgos/Conjuros rather than expanding the large core `CharacterEditorDraftV4` unnecessarily:

1. create a small `CharacterNotesDraftV4(generalNotes, cards)` UI draft;
2. serialize it to a `rememberSaveable` JSON string keyed to the character;
3. decode the current draft for the Notes tab;
4. integrate that draft into the existing central character Save transaction;
5. reset the draft from the persisted character after Save.

This keeps unsaved notes safe through ordinary Compose/Android recreation while preserving one central persistence boundary.

## UI approach

- `LazyColumn` with `imePadding()` + `navigationBarsPadding()`.
- First card: large unrestricted multiline `Notas generales` editor, deliberately much taller than ordinary form fields.
- Second area: `Notas con título`, with `+ Añadir` and zero-state text when empty.
- Titled-note cards show drag handle, title, compact content preview, Editar and Eliminar.
- Long-press vertical drag reuses the existing stable trait/spell reorder interaction pattern and normalizes `sortOrder` after each move.
- Add/edit dialog contains only `Título` and unrestricted multiline `Contenido`; nonblank trimmed title is required.
- Delete requires confirmation and becomes persistent only through the normal character Save action.
- Wide mode may increase outer spacing / use available width, but does not force a grid that would complicate ordered drag semantics for a long-text domain.

## Verification plan

Focused shared persistence regression will verify:

- a large multiline `generalNotes` value persists exactly;
- titled-note title/content persist;
- manual list reorder persists and returns normalized order;
- edit and deletion preserve remaining note identity/content;
- database close/reopen recovers both general and titled notes.

The Android code path will use `rememberSaveable` for the Notes draft and compile in the standard Android build. Actual physical rotation/IME ergonomics remain part of intended-device phone QA under C-0010; they must not be falsely reported as automated interaction PASS.

## Source-edit safety

`CharacterEditorV4.kt` remains a high-risk large file. Increment J wiring must be applied as a narrow asserted patch that fails closed if any expected seam differs. The new Notes codec and Notes tab live in separate files.

## Next exact step

Create the isolated Notes draft codec, Notes tab UI, and shared persistence regression; then wire the editor through an asserted patch and run Gate J.
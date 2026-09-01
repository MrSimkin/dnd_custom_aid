# Increment J — Gate candidate

**Date:** 2026-09-01  
**Branch:** `tmp/increment-j-notes-tab`  
**Clean source wiring head:** `47f8e4743499f5fbbe67a483cc5b3c81f41a1506`

## Candidate behavior

The `Notas` top-level tab is now a persistent editor rather than a shell.

It implements the approved hybrid notes model:

- prominent unrestricted `Notas generales` multiline area;
- optional titled note cards below it;
- each titled note contains only title + content;
- add/edit/delete;
- compact title/content preview;
- long-press vertical drag ordering with normalized sort order;
- no required dates, tags, categories, session metadata, or other imposed structure;
- IME-padding on both the tab and titled-note editor dialog;
- larger proportional horizontal spacing in wide mode without changing ordered notes into a drag-hostile grid.

## Recreation-safe draft

`CharacterNotesDraftV4` is encoded as JSON and held through `rememberSaveable(characterId, "notes")`, matching the accepted pattern already used for other character domains.

The draft is integrated into the existing central character Save transaction as `generalNotes` + `noteCards`, then rebuilt from the saved `CharacterSheet` after successful persistence.

No database schema, migration, second persistence model, or new note metadata was added.

## Regression coverage

`CharacterNotesPersistenceTest` uses an actual file-backed SQLite database and verifies:

- a general-notes body well over 10,000 characters persists exactly;
- multiline titled-note content persists;
- closing and reopening the database recovers general/titled notes;
- manual titled-note reorder persists;
- sort orders are normalized;
- title/content edits persist;
- deleting one note preserves the remaining stable note identities/content/order;
- a final second reopen observes the edited/reordered/deleted state.

Physical Android rotation/IME ergonomics remain intended-device QA under C-0010; the automated boundary verifies persistence/reopen plus compilation of the `rememberSaveable` draft path and must not be misreported as an instrumentation interaction PASS.

## Source safety

The large `CharacterEditorV4.kt` change was applied by an exact-match asserted patch. Every expected seam matched exactly once; the patch workflow passed and removed itself plus its temporary script in the source-wiring commit.

Baseline-to-clean-source diff for the editor: 29 additions / 3 deletions only.

## Gate J requirements

This direct checkpoint triggers the standard `Scaffold checks` workflow on the complete candidate. Gate J requires:

- backend check PASS;
- shared desktop tests PASS, including `CharacterNotesPersistenceTest`;
- Android debug build PASS;
- desktop build PASS;
- APK artifact upload PASS.

If green, Increment J may be technically closed and fast-forward promoted to `implementation/character-data-foundation`; `main` remains outside the promotion boundary.
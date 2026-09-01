# Increment J — Notas closure

**Date:** 2026-09-01  
**Working branch:** `tmp/increment-j-notes-tab`  
**Baseline:** `4dd1e86b2ad62cea0789baede6bf20af8bae2b15` — Increment I closed/promoted  
**Gate J tested head:** `3732dbd62414e06ab8c2ef1820d14009dd518173`

## Result

Increment J is technically complete.

The `Notas` tab now implements the approved persistent hybrid model without adding schema or imposing extra metadata.

## Implemented behavior

- one prominent large unrestricted `Notas generales` editor;
- optional titled note cards below it;
- each titled note owns only title + content;
- add/edit/delete titled notes;
- compact card title/content preview;
- long-press vertical drag ordering;
- normalized manual sort order after moves/deletion;
- no required dates, categories, tags, session metadata, or other structure;
- IME padding in the Notes tab and titled-note editor;
- wider outer spacing on wide layouts while preserving a simple ordered vertical note list.

## Draft/recreation architecture

Unsaved Notes state is represented by `CharacterNotesDraftV4(generalNotes, cards)` and encoded into a character-keyed `rememberSaveable` JSON string.

This follows the already-accepted separate domain-draft pattern. On the central character Save action, the Notes draft is integrated into the authoritative `CharacterSheet.generalNotes` and `CharacterSheet.noteCards`, then rebuilt from the repository result after save.

There is no second persistence model. Increment C's existing note fields/table remain authoritative.

## Persistence regression

`CharacterNotesPersistenceTest` uses a file-backed SQLite database and verifies:

- a freeform general note body well over 10,000 characters persists exactly;
- multiline titled-note content persists;
- database close/reopen recovers both general and titled notes;
- titled-note reorder persists;
- repository sort order is normalized;
- title/content edits persist;
- deletion preserves remaining stable note identity/content/order;
- a final second reopen observes the edited/reordered/deleted state.

## Source safety

The 93 KB `CharacterEditorV4.kt` was changed only through an exact-match asserted patch. All expected seams matched exactly once. The patch workflow passed and removed its own temporary workflow/script before the clean Gate J candidate.

Baseline-to-clean-source editor delta: 29 additions / 3 deletions.

## Gate J evidence

GitHub Actions `Scaffold checks` run **`33465839442`** passed on tested head `3732dbd62414e06ab8c2ef1820d14009dd518173`:

- backend install/type check: **PASS**;
- shared desktop tests: **PASS**;
- `CharacterNotesPersistenceTest`: **PASS** as part of the shared suite;
- Android debug build: **PASS**;
- desktop build: **PASS**;
- Android debug APK upload: **PASS**.

APK artifact:

- ID: `9784853364`
- name: `dnd-custom-aid-debug-apk`
- digest: `sha256:d55958ca91f1d56b0f673b003acdc386f94210babfd128b835eda7da4ef646ae`

## Manual acceptance boundary

Automated Gate J is green. Physical Android rotation, keyboard/IME feel, long-press drag ergonomics, text-scale presentation, and wide/landscape presentation remain intended-device owner QA under C-0010. The implementation uses `rememberSaveable` and IME-safe Compose paths, but those manual interactions must not be represented as instrumentation PASS.

## Promotion boundary

This closure is suitable for descendant-only fast-forward promotion to `implementation/character-data-foundation` after ancestry verification.

`main` must remain untouched without explicit owner approval.

## Next implementation boundary

**Increment K — Responsive + accessibility integration pass.**

Audit and correct the integrated V4 character sheet at 80/90/100/115/130% text scale and portrait/landscape assumptions, top-level/source strip behavior, wide-layout use, IME-safe editors, outside-tap safety, semantic icon controls/touch targets, removal of Unicode pseudo-buttons, and drag usability. Automated/static corrections can be completed in-repo; actual physical-device scale/orientation ergonomics remain owner QA.
# Increment G — Conjuros source-management implementation map

**Date:** 2026-09-01  
**Branch:** `implementation/character-data-foundation`

## Authoritative scope

Increment G implements only the `Conjuros` navigation/source-management foundation approved in D-0058, D-0059, D-0061, D-0063 and the consolidated implementation package.

Required behavior:
- always-present internal `Todos` view;
- one horizontally scrollable, single-line subtab per spellcasting source;
- selected source identified by stable source ID, not display name;
- add, rename, reorder and delete spellcasting sources;
- optional link from a source to one existing PC class row;
- custom/non-class sources always allowed;
- never automatically create a source for every class;
- class names may be offered only as unobtrusive linking suggestions;
- deleting the selected source falls back to `Todos`;
- deleting a source with spell associations requires explicit warning;
- deleting a source removes only that source's associations, never the conceptual spell record itself;
- deleting a linked class does not delete the source or spells: repository normalization soft-unlinks the source.

Increment G does **not** implement spell CRUD/search/level sections/prepared toggling; those belong to Increment H. It may show a neutral placeholder/content summary below the source strip while G is active.

## Existing durable foundation

No schema change is required.

Existing shared model already contains:
- `CharacterSpellcastingSource(id, name, linkedClassId, sortOrder)`;
- `CharacterSpell` with source associations.

Existing `CharacterRepository.saveCharacter()` already:
- validates stable distinct source IDs and nonblank names;
- normalizes missing class links to `null` rather than deleting sources;
- filters spell associations to surviving source IDs;
- persists source order by list index;
- preserves conceptual spells when a source association disappears.

## Implementation structure

1. Add a saveable spellcasting-domain draft codec containing current source and spell collections. This keeps source edits and association cleanup recreation-safe and provides the state container Increment H can extend.
2. Add a dedicated `CharacterSpellsTabV4.kt` foundation UI:
   - `Todos` + source strip;
   - stable-ID selection;
   - compact vector-icon source-management entry point;
   - source list with drag-and-drop ordering;
   - add/rename editor with optional class-row link;
   - explicit deletion confirmation, stronger warning when associations exist;
   - source deletion removes only matching associations from spell records;
   - selected-source deletion immediately falls back to `Todos`.
3. Compile the isolated G component before touching `CharacterEditorV4.kt`.
4. Wire the spellcasting draft into the existing character Save transaction and replace only the `SPELLS` shell using the established asserted narrow-patch safety branch.
5. Validate/promotion only after exact one-file editor diff review and green safety-branch scaffold run.

## Gate G

Gate G requires:
- source add/rename/delete/reorder persistence foundations;
- stable-ID selection behavior;
- delete-selected fallback to `Todos`;
- association-safe source deletion;
- class soft-unlink behavior retained;
- full scaffold CI green.

Physical touch/visual QA remains a later owner/device gate and must not be represented as automated PASS.

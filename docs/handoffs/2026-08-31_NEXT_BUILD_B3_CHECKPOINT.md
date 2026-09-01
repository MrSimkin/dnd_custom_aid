# Next build — B3 checkpoint

**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`  
**Status:** Implementation complete; CI gate pending at checkpoint creation

## Scope closed by this checkpoint

### Settings / presentation corrections

Implemented from the approved run #180 owner review and D-0056 direction:

- font audition is visual rather than rendering every option in the currently active font;
- rejected/redundant font candidates are pruned without inventing unapproved replacements;
- saved removed-font selections migrate to surviving choices instead of breaking preferences;
- `Gris` is redesigned as a more genuinely intermediate neutral theme;
- `Azul noche` has a stronger blue identity;
- adapted light variants are added for cyan, night blue, and forest green;
- user-facing `Matriz` is renamed to `Matrix`;
- `Pergamino` is redesigned to read more clearly as parchment;
- theme selection uses visual preview cards rather than a long text-only dropdown;
- text scale options remain 80 / 90 / 100 / 115 / 130.

Settings implementation commit: `cede68f55857eea10486afdd9944608b0746b7b5`  
Authoritative `UiPreferences.kt` blob after B3: `421971f59ea7a6125621464a14a2b2e30cb7b393`

### `Habilidades -> Por atributo` correction

The rejected one-column phone fallback is removed.

Implemented behavior:

- phone/narrow layout returns to **2 columns**;
- wide layout uses **3 columns**;
- long skill labels may use up to three lines where necessary;
- each attribute group uses a compact aligned header for abbreviation, score, and modifier;
- saving-throw total/proficiency control is aligned as a second logical row rather than nesting a full `SaveRowV4` beside the attribute editor;
- no skill/save/proficiency calculation or persistence semantics were changed.

Large-file safety procedure used:

1. authoritative pre-edit blob refetched;
2. edit performed on temporary branch `tmp/skills-b3-safe-edit`;
3. safety commit `90d22e7b2b1cc004b6cab33547fa0a0e9d79668c` validated against parent `cede68f55857eea10486afdd9944608b0746b7b5`;
4. comparison showed one modified file only, with 46 additions and 20 deletions;
5. file head and tail were explicitly checked and intact;
6. implementation branch was fast-forwarded without force.

Authoritative `CharacterEditorV4.kt` blob after B3: `3a6cfbc0cb6c5d470536959fc05a6d8af9d73b07`

## Recovery point

At this checkpoint, B1 and B2 are already CI-green and B3 production changes are fully present on the implementation branch. If interrupted, resume by checking the CI run for this checkpoint commit. Do not repeat the B3 edits unless CI exposes a real defect.

## Next step after green CI

Begin **Increment C — new persistence/schema/migration foundation** for the approved next-build expansion (`Trasfondo`, `Rasgos`, `Conjuros`, `Notas`, character-level spellcaster state / PC Settings), preserving all migration and ownership decisions already checkpointed in the decision documents.

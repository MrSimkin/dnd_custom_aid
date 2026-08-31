# Four-tab character-sheet integration checkpoint — 2026-08-31

Branch: `implementation/character-data-foundation`

## Approved navigation now implemented

The character editor now routes the approved tab order:

1. `General`
2. `Habilidades`
3. `Combate`
4. `Equipo`

Integration source commit:

- `22f3bdfc0e64eaba1f8117cd21cbae22d809f1b6` — wire Combat and Equipment into the main character editor.

## Common authoritative Save flow

`Combate` and `Equipo` are no longer isolated shells. Their saveable draft state is integrated into the same authoritative `CharacterSheet` save operation used by General/Habilidades.

On Save:

- the existing core editor draft produces the candidate sheet;
- combat draft entries are applied to `combatEntries`;
- equipment draft items are applied to `inventoryItems`;
- per-PC currency draft is applied to `currencies`;
- the repository persists the combined sheet transactionally;
- all drafts refresh from the stored authoritative result.

This avoids duplicate independent stores for tab data.

## Recreation behavior

- selected tab remains `rememberSaveable` state;
- core character draft remains saveable;
- combat draft JSON remains saveable;
- equipment/currency draft JSON remains saveable.

This is designed to preserve unsaved work through portrait/landscape recreation and screen off/on until owner phone QA confirms it.

## Combat quick-reference rule

The Combat tab receives CA, Initiative, Speed and HP directly from the same current core draft. They are read-only references there and are not duplicated persistent values.

## Verification

The integrated state, including the corrected Equipment import, passed Scaffold checks **run #169** (`33434414952`):

- shared tests: PASS;
- Android debug assembly: PASS;
- desktop build: PASS;
- backend checks: PASS.

## Continuation rule

This checkpoint is non-blocking by owner instruction. Continue immediately into remaining presentation/settings implementation and later owner QA. Do not open a PR before owner acceptance.

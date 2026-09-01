# Increment D1 checkpoint — navigation components ready for editor wiring

Date: 2026-09-01
Branch: `implementation/character-data-foundation`

## Added components

### `CharacterNavigationV4.kt`

Defines the next-build top-level tab identities in approved order:

1. General
2. Habilidades
3. Combate
4. Equipo
5. Trasfondo
6. Rasgos
7. Conjuros
8. Notas

Behavior encoded:
- `Conjuros` is omitted when `spellcasterEnabled == false`;
- a saved/selected tab that no longer exists resolves deterministically to `General`;
- top-level navigation uses one horizontally scrollable Material tab row;
- tab labels are single-line (`maxLines = 1`, no wrapping);
- the Material scrollable-tab component owns selected-tab visibility/scrolling behavior.

Implementation commit: `54cd61ee883b258989d1eab2c3ba9bf388049e45`.

### `CharacterDomainShellsV4.kt`

Adds an explicit temporary shell composable for the four later-domain tabs. These shells expose navigation only and do not pretend that Background/Traits/Spells/Notes editing is implemented during Increment D.

Implementation commit: `0005384bc2aff48b35f227b24b4cfb76978d4d63`.

## Next action

Wire these components into `CharacterEditorV4.kt` using a temporary safety branch and validate the exact diff before promotion. No PC Settings behavior is included yet; that remains D2.
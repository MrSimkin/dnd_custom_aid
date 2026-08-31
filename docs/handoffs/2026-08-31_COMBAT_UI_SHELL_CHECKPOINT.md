# Recovery checkpoint — Combat UI shell

**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Completed

A standalone Android `Combate` tab UI was added in:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterCombatTabV4.kt`

Commits:

- `449f0a713ce6dd85d34492d5a5cad7846d65533d` — initial isolated Combat tab UI;
- `284ce70ac5f26d162a91900422c88e237258648f` — corrected the Compose `KeyboardOptions` import caught by CI.

### UI behavior implemented in the isolated shell

- Read-only quick-reference card for the same authoritative character values:
  - CA;
  - Iniciativa;
  - Velocidad;
  - PG actuales / máximos / temporales.
- Reusable attacks/actions list supporting:
  - Nombre;
  - Tipo: Ataque / Acción / Acción adicional / Reacción / Otro;
  - optional manual Modificador de ataque;
  - free-text Daño / efecto;
  - optional free-text Alcance;
  - optional Notas.
- Entries are deliberately not weapon-only; the UI text explicitly permits condensed spell/effect references.
- Add/edit dialog.
- Delete confirmation.
- Manual phone-safe ordering through up/down controls instead of drag-and-drop.
- Dialog/editor transient state uses `rememberSaveable` so in-progress modal fields can survive normal Android recreation.

## Verification history

Run #162 / ID `33432474589` failed only because `KeyboardOptions` was imported from the wrong Compose package. The compiler error was inspected and corrected without changing product behavior.

GitHub Actions `Scaffold checks` run **#163** / ID `33432803706` then completed successfully for commit `284ce70ac5f26d162a91900422c88e237258648f`:

- shared desktop tests: PASS;
- Android debug assemble: PASS;
- desktop build: PASS;
- backend checks: PASS;
- APK artifact upload: PASS.

## Important current boundary

The isolated UI file is compiled but **not yet routed from the character sheet**. No `Combate` tab is visible yet, and the character draft saver has not yet been extended with combat entries.

## Next step

Wire `Combate` into `CharacterEditorV4.kt`:

1. add the tab after `Habilidades`;
2. pass the current draft's read-only core combat values;
3. include `combatEntries` in `CharacterEditorDraftV4`;
4. include combat entries in draft JSON saver/restoration so unsaved changes survive recreation;
5. map the draft list into `CharacterSheet.combatEntries` on Save and load it back from persistence;
6. verify CI and checkpoint the completed Combat tab before starting `Equipo`.

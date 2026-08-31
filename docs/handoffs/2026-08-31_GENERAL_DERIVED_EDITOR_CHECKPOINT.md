# Recovery checkpoint — General derived editor integration

**Date:** 2026-08-31  
**Branch:** `implementation/character-data-foundation`

## Completed

Persistence consistency:

- `66b2a32c208443aa769672c4f06bcabcafeda164` allows hit-die size `0` as incomplete persisted numeric data, matching the approved blank-number confirmation behavior.
- `06d0127301484443c18af8cd9694ae281e756174` checkpointed that narrow consistency fix before UI work.

Android editor integration:

- `3ab66d239804f15bf815c22b2224e1dbb7fcae6a` integrates the cross-cutting follow-up behavior into `CharacterEditorV4.kt`.
- `Resumen` presentation label is now `General`.
- Required numeric fields can be temporarily cleared while editing.
- Pressing Save with required numeric blanks shows a warning/confirmation; confirmation persists those blanks as `0`.
- Blank optional adjustments are semantically `0` for Initiative, saving throws, skills, Passive Perception and proficiency bonus.
- `Bonificador por competencia` is now displayed from total character level + `Ajuste adicional` rather than as a permanently manual final field.
- Initiative, saving throws, skills, Passive Perception and proficiency bonus use progressive disclosure: compact final total, tap for calculation breakdown and `Ajuste adicional` editing.
- Non-zero adjustments receive a compact secondary `ajuste ±N` indication.
- Ability modifiers were made slightly more visually prominent.
- The draft saver intentionally understands both the new proficiency-adjustment field and the legacy saved-draft proficiency value.

## Verification

GitHub Actions `Scaffold checks` run **#158** / ID `33431476942` completed successfully for commit `3ab66d239804f15bf815c22b2224e1dbb7fcae6a`:

- shared desktop tests: PASS;
- Android debug assemble: PASS;
- desktop build: PASS;
- backend checks: PASS;
- APK artifact upload: PASS.

## Not yet implemented

- Quick Magic UI on `General`;
- removal of `CD conjuros` from the old combat-reference row (retain it there until Quick Magic is present so no intermediate build loses access);
- new `Combate` tab;
- new `Equipo` tab;
- V4 screenshot layout refinements beyond the ability-modifier prominence change;
- Settings font/theme audition changes.

## Next step

Implement Quick Magic at the bottom of `General`, move the existing spell-save DC there, add manual spell attack/ability fields, configurable spell-slot totals, persistent tappable spent/unspent pips and `Restaurar espacios`. Verify and checkpoint before adding `Combate` / `Equipo`.

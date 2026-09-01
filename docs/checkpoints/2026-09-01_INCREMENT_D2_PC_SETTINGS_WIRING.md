# Increment D2 checkpoint — PC Settings wiring promoted

Date: 2026-09-01
Branch: `implementation/character-data-foundation`

## Production promotion

Validated D2 source blobs were transplanted to the implementation branch at commit:

- `5a4e8db246e0a52ffc68532d3a370efa55128c68`

Promoted blobs only:
- `CharacterEditorV4.kt`: `ccabcc2f84d1fb11fb030c582255e26b6dbcfde4`
- `MainActivity.kt`: `785d6504a68f4b1a2181dce5041403a953fde500`

Temporary workflow, trigger, and validation-marker files were intentionally not promoted.

## Implemented D2 behavior

### Character settings ownership

- `CharacterEditorScreenV4` no longer accepts the application-settings callback.
- `MainActivity` no longer routes the character header gear to application Settings.
- campaign/application Settings routing remains unchanged.
- character header gear opens the dedicated full-screen `CharacterPcSettingsV4` page.

### `Lanzador de conjuros`

The setting is persisted through `CharacterRepository` using the current stored character while preserving all unsaved legacy editor draft state in the parent composable.

ON:
- exposes Quick Magic;
- exposes Conjuros in the top-level tab strip;
- does not force navigation to Conjuros.

OFF:
- hides Quick Magic;
- hides Conjuros;
- preserves Quick Magic data, slots, sources, conceptual spells, source associations, and per-source prepared state;
- if Conjuros was selected, stored top-level selection is changed to General.

### Hide-not-delete confirmation

When switching ON -> OFF and meaningful spellcasting data exists, the UI asks for confirmation with explicit retained-data wording.

Meaningful-data detection includes legacy Quick Magic state, configured slots, spellcasting sources, and conceptual spells.

## Draft preservation design

PC Settings is rendered as an alternate full-screen child of the same `CharacterEditorScreenV4` state owner. The parent-owned General/Combat/Equipment draft state is therefore retained when entering/leaving PC Settings.

Changing only `spellcasterEnabled` saves `stored.copy(spellcasterEnabled = ...)`; it does not reset the in-progress draft, combat draft JSON, or equipment draft JSON. A later normal character save integrates those local drafts while retaining the persisted caster flag.

## Large-file safety validation

Temporary branch: `tmp/increment-d2-pc-settings-wiring`.

- asserted patch workflow run: `33457818874` — PASS;
- patched source commit: `f55fe9b1d69180e3e239aaa47b6bd26f84da20c5`;
- exact patch diff: `CharacterEditorV4.kt` +52/-6; `MainActivity.kt` -1 only;
- editor changed regions and file tail refetched and verified;
- temporary validation commit: `81da860936b0c1f67efbd70c1ec5fa1fc0619de3`;
- full Scaffold checks validation run: `33457938261` / run #274 — backend PASS, tests/builds PASS, APK upload PASS.

## Gate status

D2 is implemented and pre-promotion validated. The next action is a normal working-branch Scaffold checks run on this checkpoint. Increment D closes only after that gate is green.
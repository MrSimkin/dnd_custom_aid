# Increment D implementation map — navigation + PC Settings shell

Date: 2026-09-01
Branch: `implementation/character-data-foundation`
Baseline: Increment C closure commit `7dafac1733098687ef0ab121db0354a8131353c5`

## Authoritative package

`docs/handoffs/2026-08-31_NEXT_BUILD_CONSOLIDATED_IMPLEMENTATION_PACKAGE.md` defines Increment D as:

- D1: top-level navigation shell;
- D2: dedicated full-screen PC Settings behavior + persistence;
- Gate D only after both are implemented and verified.

## Current implementation map

### Character editor

`androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt`

Current pre-D blob: `3a6cfbc0cb6c5d470536959fc05a6d8af9d73b07`.

Current behavior:
- fixed four-tab `TabRow` (`General`, `Habilidades`, `Combate`, `Equipo`);
- tab selection is already `rememberSaveable` by stable enum name;
- Quick Magic always renders at the bottom of General;
- the header gear delegates to the application Settings callback;
- the legacy editor draft does not own `spellcasterEnabled` or the new-domain records.

Important safety property:
- `CharacterEditorDraftV4.toSheetOrNull(original)` returns `original.copy(...)` for only fields the legacy editor owns;
- therefore `spellcasterEnabled`, Background, Traits, spells/sources, and Notes survive ordinary editor saves unchanged.

Because this source is ~88 KB and previously suffered an accidental whole-file truncation, all D edits to it must be narrow, performed/validated on a temporary safety branch before promotion.

### Application routing

`androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt`

Current pre-D blob: `f08eba744e49570cf097f8a89e46c166aad53ada`.

Current behavior:
- campaign-level gear opens application Settings;
- character editor receives the same application-settings callback, so the character header gear currently opens the wrong settings domain.

Application Settings must remain application-wide only.

## D1 plan — navigation shell

Prefer a small new navigation file for:
- top-level tab identity/labels;
- visible-tab list derived from `spellcasterEnabled`;
- horizontally scrollable single-line tab strip;
- selected-tab auto-visibility through the scrollable Material tab component;
- deterministic `General` fallback if a saved selection no longer exists.

Approved order when caster ON:
1. General
2. Habilidades
3. Combate
4. Equipo
5. Trasfondo
6. Rasgos
7. Conjuros
8. Notas

Caster OFF hides only Conjuros.

New-domain tabs are navigation shells in D; their persistent detailed UIs belong to later increments E/F/G-H/J.

No swipe-between-page navigation.

## D2 plan — PC Settings

Prefer a small new PC Settings composable rendered as a dedicated full-screen page *inside the character-editor state owner*. This keeps the parent editor composable alive so unsaved General/Combat/Equipment drafts are not discarded merely by opening PC Settings.

Behavior:
- header gear opens PC Settings, not application Settings;
- initial control: `Lanzador de conjuros` only;
- new persisted value is saved through `CharacterRepository`;
- ON exposes Quick Magic + Conjuros;
- OFF hides Quick Magic + Conjuros but never deletes Quick Magic, slots, sources, spells, prepared state, or associations;
- if meaningful spellcasting data exists, OFF first shows a non-destructive hide-not-delete confirmation;
- if Conjuros was selected when OFF is confirmed, return to General;
- re-enabling makes Conjuros available but does not force navigation there.

## Persistence integration choice

Do not add `spellcasterEnabled` to the legacy `CharacterEditorDraftV4` codec solely for D2. Update `stored` through `repository.saveCharacter(stored.copy(spellcasterEnabled = ...))` while retaining all in-progress editor drafts. Subsequent normal saves preserve the flag because the draft converts through `original.copy(...)`.

## Planned files

New/small files preferred:
- `CharacterNavigationV4.kt` — tab model + scrollable strip/helper logic;
- `CharacterPcSettingsV4.kt` — full-screen character settings UI;
- optional small `CharacterDomainShellsV4.kt` — temporary D navigation shells for the four later domains.

Narrow existing-file edits:
- `CharacterEditorV4.kt` — wire dynamic tabs, shell bodies, PC Settings state, Quick Magic visibility;
- `MainActivity.kt` — stop passing application Settings into the character header path.

## Checkpoints/gates

- D1 checkpoint after navigation shell compiles and CI passes;
- D2 checkpoint after PC Settings/caster behavior compiles and CI passes;
- Gate D closure after navigation selection/fallback/persistence behavior is verified.

Every meaningful D step remains independently recoverable in Git.
# Equipment UI implementation checkpoint — 2026-08-31

Branch: `implementation/character-data-foundation`

## Completed implementation

The approved `Equipo` character-sheet domain is implemented on Android.

Source commits in this increment:

- `dc365a7b480ffd1c892324b60c0277b49910653c` — saveable Equipment draft codec.
- `88b3739d3e6af794ddbc94bba5860316cb54af99` — Equipment tab UI.
- `a4ffd4d4395fb03817e295ac74fe983b9ba7f212` — correct Equipment `KeyboardOptions` import.

The initial Equipment UI CI run #167 failed only because `KeyboardOptions` was imported from the wrong Compose package. The import was corrected before acceptance of this checkpoint.

## Implemented behavior

- one ordered inventory model for ordinary and special/magic equipment;
- add/edit/delete and manual up/down ordering;
- quantity;
- optional per-unit weight;
- carried-weight total uses `quantity × unit weight`;
- imperial-first display with approximate metric in parentheses using the approved rough conversion (`lb × 0.5 = kg`);
- `Equipado` is independent from `Ubicación`;
- special equipment exposes long `Descripción`, optional `Ubicación`, and manual `Sintonizado`;
- predefined body/location choices plus `Otro` custom location;
- changing a special item back to ordinary warns before discarding special-only fields;
- `Sintonizados: N` count without hard RAW `/3` enforcement;
- five repository-provided default currencies remain per-PC;
- per-PC custom currencies can be added and removed;
- currency amounts remain manual.

## Persistence / recreation

Equipment and currencies use a saveable JSON draft codec so unsaved in-editor state can survive normal Android recreation and participate in the common character Save action after integration.

## Verification

The corrected Equipment implementation is included in the integrated branch state verified by Scaffold checks **run #169** (`33434414952`), where Kotlin/Android/shared/desktop and backend jobs all passed.

## Next

Continue without waiting:

1. four-tab integration checkpoint;
2. presentation/settings audition and remaining layout corrections;
3. final follow-up APK owner QA before any PR.

# Follow-up character-sheet phone QA target checkpoint — 2026-08-31

Branch: `implementation/character-data-foundation`

## Verified implementation state

The branch contains the approved follow-up implementation scope through the current phone-QA target:

- `General / Habilidades / Combate / Equipo` four-tab character sheet;
- common Save/recreation flow for all four tabs;
- Quick Magic with persistent manual slot pips;
- calculated proficiency bonus with `Ajuste adicional`;
- progressive-disclosure adjustments for derived values;
- blank optional adjustment = 0;
- temporary blank required numeric editing + Save warning/confirm-to-zero behavior;
- reusable combat attacks/actions with manual ordering;
- unified inventory + special/magic equipment, location, Sintonización and manual ordering;
- per-PC default/custom currencies;
- imperial-first approximate metric weight display;
- eight-font audition;
- full approved theme audition including neutral Gris, Cian oscuro, Azul noche, Verde bosque, Pergamino, Alto contraste and Matriz;
- responsive layout refinements for class/hit-die geometry, large text and `Por atributo`;
- V4 database migration/preservation support.

## Write-recovery verification

A truncated contents-API write was repaired byte-for-byte before this target. `CharacterEditorV4.kt` was restored to the complete pre-error blob `96510441bc8781a5df2f8970d08c372ca03693fa`. No implementation work was lost.

## CI

Scaffold checks run **#180** (`33436382484`) passed completely on branch head `8be69ce94a0ce613cc29e3752e40bcc365c81b47`:

- shared tests: PASS;
- Android debug assembly: PASS;
- desktop build: PASS;
- backend checks: PASS;
- debug APK upload: PASS.

Artifact:

- name: `dnd-custom-aid-debug-apk`;
- artifact ID: `9774615456`;
- ZIP digest: `sha256:0b8d899428eb6ba02e1b350b753a3312d11f76ea83aed115b96f09d7c9f32bf6`.

## One known cosmetic cleanup

Top-level tabs currently use `maxLines = 2` in source. Because the exact labels are single words (`General`, `Habilidades`, `Combate`, `Equipo`), this is expected to be visually single-line at ordinary sizes, but the approved specification explicitly requires single-line labels. A direct one-line source cleanup was attempted with a whole-file contents replacement and was immediately reverted after that replacement truncated the large source file. Do not repeat that unsafe edit method.

Treat tab-label wrapping at 115%/130% as an explicit phone-QA observation. If it actually wraps, apply the cleanup later using a safe patch-capable workflow.

This checkpoint is non-blocking by owner instruction. The next step is owner phone QA of this exact artifact/build.
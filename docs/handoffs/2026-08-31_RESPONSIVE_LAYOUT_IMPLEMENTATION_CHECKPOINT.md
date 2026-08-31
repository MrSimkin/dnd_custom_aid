# Responsive character-sheet layout implementation checkpoint — 2026-08-31

Branch: `implementation/character-data-foundation`

## Source commits

- `6e68989d6412e78bbd306ce382f4acd77cf369ae` — shared compact-field label/selector geometry helpers.
- `c03fc8ea823cc0d0377d2ef4ba00d5d945655fd4` — main character-sheet responsive layout corrections.
- `852ed6a547ce4d0a897f6ac3c50246c8eb4284c8` — align Combat quick-reference labels with the same large-text geometry.

## Owner observations addressed

### Ability modifiers

Ability modifiers under the six ability scores are visually more prominent (`titleMedium` rather than the previous smaller body style) while remaining automatic/non-editable.

### Classes / hit dice

The geometry issue reported in screenshots is addressed for both normal dropdown-selected classes and custom-class entry:

- class selector uses the same compact surface geometry as neighboring compact fields;
- custom class mode remains in the same logical row;
- hit-die selector is wider and uses the same compact surface geometry;
- custom hit-die mode no longer introduces the previous extra vertical `Lista` row;
- delete control is aligned through the same reserved label slot.

### 115% / 130% text alignment

Adjacent compact fields now use a shared `CompactFieldLabelV4` label slot whose minimum height scales with effective font scale. A two-line label therefore does not push only its own box downward while neighboring boxes remain higher.

This helper is used by General compact fields/derived values, Quick Magic and Combat quick-reference fields.

### `Por atributo`

Portrait/non-wide `Por atributo` now uses one ability group per row instead of the cramped two-column layout. Wide layouts retain three columns. This gives long skill labels such as `Juego de Manos` and `Investigación` usable horizontal space.

### Combat quick reference

CA/Iniciativa/Velocidad and HP reference rows use the same reserved label geometry so their value boxes remain aligned when labels wrap at larger text scales.

## Verification

Scaffold checks **run #176** (`33435361845`) passed completely on `852ed6a547ce4d0a897f6ac3c50246c8eb4284c8`:

- shared tests: PASS;
- Android debug assembly: PASS;
- desktop build: PASS;
- backend checks: PASS;
- debug APK upload: PASS.

## Remaining cleanup before final QA target

The approved review package requires the four top-level tab labels to remain single-line. Current routing still permits `maxLines = 2`; this is a small known cleanup and is the immediate next implementation step.

This checkpoint is non-blocking by owner instruction. Continue immediately.

# Increment F checkpoint — Rasgos implementation map

Date: 2026-09-01
Branch: `implementation/character-data-foundation`
Baseline head: `64a5ecd5989e926222deb24e46753a84a45f3dc6`

## Authoritative scope

Increment F is limited to the approved persistent `Rasgos` tab defined by `D-0059_RASGOS_DOMAIN.md` and the consolidated next-build package.

Each ordered Rasgo entry supports:
- `Nombre`;
- free-text `Fuente`;
- controlled/permissive `Tipo`;
- `Descripción`;
- optional `Notas`;
- optional manual usage tracker (`Usos máximos`, `Usos gastados`, optional free-text `Recuperación`);
- optional `Activación` / action type;
- explicit manual order.

Interaction requirements:
- compact collapsed cards with name, `Fuente · Tipo` metadata when present, and one/two-line description preview;
- tap/edit opens the complete editor;
- direct long-press drag-and-drop ordering using the same interaction family as existing Combat/Equipo ordered lists;
- responsive multi-column card presentation on wide layouts;
- IME-safe editor with explicit Apply/Cancel and no outside-tap discard;
- no automatic creation from equipment, background, class, spells, or any other domain.

## Existing durable foundation

Increment C already added:

`CharacterTrait`:
- stable `Uuid` identity;
- `name`;
- `source`;
- `CharacterTraitType`;
- `description`;
- nullable `notes`;
- nullable `maxUses`;
- integer `spentUses`;
- nullable `recovery`;
- nullable `CharacterActivationType`;
- `sortOrder`.

Approved type values already exist in the shared enum:
- CLASS;
- SPECIES_RACE;
- BACKGROUND;
- FEAT;
- GIFT_BLESSING;
- OTHER.

Approved activation values already exist:
- PASSIVE;
- ACTION;
- BONUS_ACTION;
- REACTION;
- OTHER.

`CharacterRepository.saveCharacter()` already replaces/persists `sheet.traits` transactionally, writes list index as authoritative `sort_order`, and hydrates the collection on reopen. Shared foundation tests already cover non-empty trait round-trip including usage/recovery/activation fields.

Therefore Increment F requires no SQLDelight migration or shared model/repository change unless a genuine defect is found.

## Android implementation shape

1. Add a small saveable Rasgos JSON draft codec for `List<CharacterTrait>`.
2. Add a dedicated `CharacterTraitsTabV4.kt` composable in a new source file.
3. Card behavior:
   - stable drag handle;
   - compact metadata/preview;
   - explicit Edit/Delete controls;
   - optional usage summary and manual `Gastar` / `Recuperar` controls when `maxUses != null`;
   - usage changes remain draft changes until the sheet-level `Guardar` action.
4. Editor behavior:
   - add/edit one trait at a time;
   - type dropdown with approved Spanish labels;
   - activation dropdown supporting `Sin especificar` plus approved activation types;
   - optional usage tracker controlled by whether `Usos máximos` is blank/null;
   - `Usos gastados` clamped to `0..maxUses` when a maximum is configured;
   - recovery is free text and only persisted when nonblank;
   - explicit Apply/Cancel; outside tap does not dismiss/discard.
5. Hoist Rasgos JSON draft in `CharacterEditorV4.kt`, integrate it into central Save, refresh after Save, and replace only the TRAITS shell.

## Ordering semantics

- UI reorder mutates the draft list directly.
- Every reorder normalizes `sortOrder = index`.
- Add appends at the end.
- Delete compacts order.
- Edit preserves identity and position.
- No alphabetical or inferred sorting.

## Usage semantics

- `maxUses = null` means no usage tracker.
- `spentUses` is manual state, not inferred from rules.
- card-level `Gastar` increases spent uses by one up to max;
- card-level `Recuperar` decreases spent uses by one down to zero;
- no rest automation and no recovery-rule enforcement;
- `Recuperación` remains arbitrary free text.

This counter presentation is the digital equivalent of the approved manual spent/unspent tracker while remaining usable for unusually large maximum-use values.

## Large-file safety

`CharacterEditorV4.kt` wiring will follow the proven asserted temporary-branch workflow:
- refetch exact current blob;
- exact-match assertions;
- fail closed on mismatch;
- compare and inspect resulting one-file diff;
- refetch changed regions and file tail;
- normal scaffold validation on the temporary patched state;
- promote only the validated editor blob onto the clean working-branch tree;
- checkpoint and gate the exact working-branch state.

## Gate F target

Before closing Increment F at implementation/automated-gate level:
- trait CRUD compiles and is wired to persistent sheet Save;
- list order normalizes and persists through repository round-trip;
- usage/recovery/activation fields round-trip through the existing shared foundation tests;
- Rasgos draft is saveable across tab switching/recreation state;
- wide/narrow rendering paths compile;
- editor is IME-safe and explicit-dismissal;
- working-branch CI is green.

Physical drag ergonomics, keyboard behavior, rotation, text-scale responsiveness, and subjective layout remain later owner/integration QA concerns and must not be overclaimed from CI.

# Increment E checkpoint — Trasfondo implementation map

Date: 2026-09-01
Branch: `implementation/character-data-foundation`
Baseline head: `207b8d5e369dc565247710fc58095fe8ec4d30f7`

## Authoritative scope

Increment E is limited to the approved `Trasfondo` tab from the consolidated next-build implementation package.

Required behavior:
- `Nombre del trasfondo` plus separate `Descripción / resumen`;
- two character-image placeholder cards only; no image persistence is introduced in this increment;
- compact preview/edit cards for `Rasgos de personalidad`, `Ideales`, `Vínculos`, and `Defectos`;
- a materially larger `Historia del personaje` editor;
- no generic Notes field inside Trasfondo;
- responsive use of width, including two-column compact narrative cards on wide layouts where practical;
- image placeholders side by side when practical and stacking only when necessary;
- IME-safe text editing with no silent outside-tap discard.

## Existing durable foundation

Increment C already added `CharacterBackground` with exactly these persisted fields:
- `name`;
- `summary`;
- `personalityTraits`;
- `ideals`;
- `bonds`;
- `flaws`;
- `story`.

`CharacterRepository.saveCharacter()` already writes those seven values transactionally through `upsertCharacterBackground(...)` and hydrates them back into `CharacterSheet.background`.

Therefore Increment E requires no SQLDelight migration and no shared repository/domain modification unless a genuine defect is discovered.

## Implementation shape

1. Add a small Android-only saveable background draft codec rather than expanding the legacy V4 editor draft model.
2. Add a dedicated `CharacterBackgroundTabV4.kt` composable in a new small source file.
3. Hoist Trasfondo draft state in `CharacterEditorV4.kt` so:
   - switching tabs does not lose unsaved edits;
   - recreation/rotation preserves the draft;
   - the existing top-level Save button can persist the domain atomically with the rest of the sheet.
4. Extend the existing `persist(candidate)` integration only by supplying the decoded `background` value and reloading that draft after save.
5. Replace only the temporary `CharacterTabV4.BACKGROUND` shell with the real tab.

## Large-file safety

`CharacterEditorV4.kt` remains a high-risk large file. Wiring will use the same asserted temporary-branch patch workflow proven in Increment D:
- refetch exact current blob first;
- exact-match assertions;
- fail closed on mismatch;
- validate diff/file integrity;
- compile on the temporary branch;
- promote only validated source blobs;
- checkpoint and gate on the working branch.

## Gate E target

Before closing Increment E:
- create/edit/save/reopen all seven fields;
- preserve unsaved Trasfondo draft through tab switching and Android recreation;
- verify portrait/landscape compilation and responsive layout behavior;
- verify narrative editor dialogs are IME-safe and require explicit Apply/Cancel;
- confirm image placeholders are visibly non-functional and create no persistence records;
- CI green on the exact working-branch checkpoint.

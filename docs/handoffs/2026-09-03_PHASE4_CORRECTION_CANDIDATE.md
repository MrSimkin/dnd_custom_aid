# Phase 4 owner-QA correction candidate

Date: 2026-09-03  
Branch: `implementation/character-data-foundation`

## Implementation lineage

- Correction A: `b9fb1347bbf7c46662075e61a6571e8bc5e00cb4`
  - N-01 Android system Back hierarchy.
  - C-01 combat editor IME-safe actions.
  - E-04 equipment editor IME-safe actions.
  - C-02 outside-tap focus/IME dismissal without draft discard.
- Correction B: `81f0147b0e9b026b8bf30b26de51f000a1d3549b`
  - B-01 persisted `Raza` and `Religión / Fe` through domain, SQLDelight schema/query, migration `5.sqm`, repository, Android recreation codec, UI and regression coverage.
- Correction C: `24ef827a302349c213a915ac26c7d73e42e26853`
  - U-01 stable fixed compact-label slots for Combat reference alignment.
  - E-05 compact Monedas editors.
  - L-01/C-03 reduced synthetic phone-first bottom whitespace.
  - D-01/E-01 visible active drag feedback across combat, equipment, traits, spell sources, spell rows and note cards while preserving the accessible handle target.
  - E-02 compact semantic icon actions for equipment rows.
  - E-03 intentionally keeps the 48 dp interactive drag target; no unapproved handle-free interaction was introduced.
  - R-01 clearer Rasgos usage wording.
  - S-01 numeric keypad for `Nivel (0-9)` in the spell editor.
  - T-01 product-facing `Quick Magic` replaced by `Lanzamiento de Conjuros` according to the character-sheet terminology reference.
  - N-02 long Notes editors are bounded and explicitly advertise internal scrolling when content is long.
  - N-03 titled Notes use a deterministic two-column layout on wide/landscape screens.

## Automated validation

Bootstrap workflow run `33710087103` completed successfully after applying Correction C. Its full gate completed successfully before the correction commit was pushed:

- shared Kotlin/SQLDelight desktop tests;
- migration/persistence regressions, including B-01;
- Android debug assembly;
- desktop build;
- backend install/type-check.

The standard repository workflow then independently rebuilt the complete correction candidate from commit `6ae415d8919efb865d7b22092d95b94b3fa7866a`:

- workflow run: `33710347091`;
- Kotlin/shared/Android/desktop job: PASS;
- backend job: PASS;
- APK upload: PASS;
- artifact: `dnd-custom-aid-debug-apk`;
- artifact id: `9876725270`;
- artifact digest: `sha256:ca0bf1a9aa25bb3c679a786ec126662bd429229cebec9aa0ed9d9eb551777bd2`;
- artifact expires: 2026-12-02.

The ZIP was also downloaded from GitHub and the contained `androidApp-debug.apk` independently hashes to `sha256:7eb0543ac70960f50555905acf7a9580e917f9e532f44048260bd2fd445bd5b9`.

Status: **all planned corrections are implemented and automated gates are green; owner focused device retest is the remaining acceptance gate.**

## Owner focused retest scope

Do not repeat all 42 original checks blindly. Test the correction candidate in this order:

1. Migration/data preservation smoke: open the existing campaign/character and confirm prior data survived; verify new Trasfondo fields begin empty on the migrated character.
2. N-01: Android Back from PC Settings -> character editor -> character list -> campaigns; only root exits. Repeat once with IME open.
3. Combat: Check 12A alignment at normal and larger text; Check 12B actions reachable with IME; outside tap dismisses keyboard without losing draft; spacing/drag feedback smoke.
4. Equipo: Check 13B ordinary and special item editors with IME; Check 13C compact Monedas portrait/landscape including custom currency; equipment action compactness and drag feedback.
5. Trasfondo B-01: enter `Raza` and `Religión / Fe`, save, close/reopen, rotate/recreate, confirm exact persistence.
6. Rasgos: readable usage summary; Spend/Recover regression; reorder feedback/persistence; landscape layout smoke.
7. Conjuros: `Lanzamiento de Conjuros` terminology, numeric keypad for spell level, source/spell reorder feedback, shared slot synchronization and quick persistence smoke.
8. Notas: long general/titled text bounded scrolling affordance, portrait/landscape, two-column titled cards when wide, reorder/save/reopen, keyboard/rotation smoke.
9. Final resilience smoke: repeated tab switching, screen off/on, full app close/reopen, rotation with in-progress edits, spellcasting OFF/ON hide-not-delete, icon touch/meaning.

Any failure should be recorded against the exact correction finding rather than restarting Phase 4 implementation.

## Merge boundary

This is a correction **candidate**, not owner acceptance. Do not merge to `main`, clean branches, or declare Phase 4 accepted until the owner finishes the focused device retest and explicitly approves the merge. Continuity/documentation drift and debug-signing governance wording also remain to be reconciled before the eventual merge proposal.

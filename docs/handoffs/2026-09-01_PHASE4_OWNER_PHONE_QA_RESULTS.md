# Phase 4 Owner Phone QA Results

Date: 2026-09-01
Branch: `implementation/character-data-foundation`
Designated owner-QA APK: Gate L artifact `9785676981` (`dnd-custom-aid-debug-apk`), tested commit `089a991c6491627961f1e75f3815959a8a1c8b48`.

This file records owner real-device QA against the authoritative 42-step checklist in `2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`.

Status values: PASS, FAIL/blocking, limitation/non-blocking, NOT TESTED.

## Checks completed

1. **PASS — Migration preservation / pre-Phase-4 character opens.** Existing character created before Phase 4 remains present and opens normally after upgrade.
2. **PASS — Legacy data preserved.** Owner reports prior stats/classes/saves/skills/proficiency/Quick Magic data/spell slots/combat/equipment/currencies appear preserved.
3. **PASS — New Phase 4 domains initialize empty/default.** Background/traits/notes/conceptual spell domains are empty/default as expected for migrated legacy data.
4. **PASS — Spellcaster-toggle migration.** Character with pre-existing meaningful spellcasting data migrated with spellcasting enabled.
5. **PASS — Navigation tab count.** With spellcasting enabled, all 8 expected tabs are present.
6. **PASS — Horizontal tab navigation / selected-tab visibility at all supported scales.** Owner tested 80%, 90%, 100%, 115%, and 130%; all behaved correctly.
7. **PASS — Selected tab survives rotation/recreation.** Owner reports selected tab remains correct through orientation change.
8. **PASS — Conjuros fallback when spellcasting is disabled.** Starting from `Conjuros`, disabling spellcasting removes the tab and returns the character sheet to `General` without crash, blank state, or invalid selection.
9. **PASS — Re-enable spellcasting without forced navigation or data loss.** Re-enabling spellcasting restores the `Conjuros` tab and the prior spellcasting data without forcing navigation to the spell tab or resetting the character's spellcasting state.

## Findings discovered during QA

### T-01 — `Quick Magic` terminology mismatch

Status: **limitation/non-blocking for continued QA; must be reconciled before acceptance/merge**.

The installed app uses `Quick Magic`. Owner explicitly identified that wording as an inadequate Spanish-equivalent label and instructed that the custom character-sheet PDFs be used as the terminology reference.

Durable PDF terminology/visual references now exist under `assets/character-sheets/templates/`, including `REFERENCE.md` and rendered PNG pages. The PDF terminology uses `Lanzamiento de Conjuros` for the corresponding spellcasting area, together with `CD de Salvación de Conjuro`, `Modificador de Ataque Mágico`, `Aptitud Mágica`, `Espacios`, `Espacios Gastados`, and `Trucos`.

Do not silently rename during this QA pass; reconcile in the correction/consolidation pass with owner approval.

### E-01 — Equipment drag/reorder handle does not work

Status: **FAIL/blocking**.

Observed while the owner was on `Equipo` during Check 7. The visible triple-line/reorder affordance does not perform a drag/reorder action, and equipment rows cannot be reordered by drag-and-drop.

This directly conflicts with the Phase 4 acceptance target for Equipo (`drag reorder`). Treat as a real functional defect, not a cosmetic limitation.

Do not silently patch the already-designated QA candidate. Continue QA to collect findings unless the owner chooses to stop; remediation must happen on a focused non-main branch, followed by the appropriate automated gate and a newly identified QA target.

### E-02 — Equipment row actions are too bulky

Status: **limitation/non-blocking**.

Owner reports the `Editar` and `Eliminar` buttons consume too much space for compact equipment rows. This is a density/layout issue and should be reviewed against the intended compact equipment presentation and the owner's character-sheet visual references.

### U-01 — Wrapped two-line text is top-aligned instead of vertically centered

Status: **limitation/non-blocking**.

Owner reports that labels/content wrapping to two lines remain aligned toward the top of their available control/row area instead of being vertically centered. Record as a general visual-alignment issue and tie it to exact affected controls/screens as they are encountered during the remaining QA checks.

## Current QA disposition

- Checks 1–9: PASS.
- Blocking defects found: E-01.
- Non-blocking/reconciliation findings: T-01, E-02, U-01.
- QA should continue step-by-step to discover the full defect set before deciding the correction batch, unless the owner explicitly requests an immediate stop-and-fix cycle.

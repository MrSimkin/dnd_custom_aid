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
10. **PASS — Hide-not-delete warning for meaningful spellcasting data.** Disabling spellcasting presents a clear warning/confirmation, and after re-enabling spellcasting the prior data returns intact.
11. **PASS — General adjustment marker and speed formatting.** Owner confirms the compact non-zero adjustment indicator behaves as intended without the redundant second-line adjustment label, and `Velocidad` follows the approved imperial-first format with approximate metric in parentheses.

### Check 12 — Combate corrective backlog

- **12A: FAIL/blocking — vertical centering.** In the Combate quick-reference row, when one label/value wraps to two lines, neighboring one-line text remains top-aligned instead of being vertically centered within the taller shared row. This reproduces and localizes the previously noted U-01 visual-alignment issue on the exact corrective surface targeted by Check 12.
- **12B: FAIL/blocking — IME/keyboard reachability incomplete.** The combat editor remains generally usable with the keyboard open, but the owner cannot scroll far enough to bring the bottom action buttons into view. Because Check 12 explicitly requires keyboard-safe access to the full editor and actions, this cannot pass acceptance as a partial/semi-pass. The editor must provide enough inset/scroll range to reach the bottom controls while the IME is visible.
- **12C: PASS for editor-retention / explicit-dismissal requirement, with non-blocking keyboard UX limitation.** Tapping elsewhere does not close the combat editor or discard the unsaved draft. The keyboard itself also remains open on outside tap and currently closes only through Android system Back. The core acceptance requirement here is satisfied because outside taps do not silently dismiss the editor/draft; see C-02 for the keyboard-dismissal usability limitation.
- **12D: PASS for functional drag reorder, with significant discoverability/feedback limitation.** Dragging a combat block can reorder it, but the owner reports very little or no visible movement feedback while dragging, making it difficult to tell that the gesture is actually working.

Check 12 is complete at the functional-subcheck level, but overall **FAIL/blocking** because 12A and 12B fail explicit acceptance requirements.

### Check 13 — Equipo corrective backlog

- **13A: PASS — functional drag reorder.** Retesting while watching the final order confirms that the three-line drag affordance successfully reorders equipment entries. The earlier apparent functional failure was caused by insufficient drag feedback/discoverability rather than a broken reorder operation.
- **13B: FAIL/blocking — keyboard safety / lower controls inaccessible.** With the Android keyboard open in Equipo, the owner cannot reach all lower controls; specifically `Editar` and `Eliminar` remain hidden/inaccessible. Because Check 13 explicitly requires keyboard-safe Equipo interaction, this fails acceptance even though the rest of the editor remains usable.

Remaining Check 13 subchecks are pending.

## Findings discovered during QA

### T-01 — `Quick Magic` terminology mismatch

Status: **limitation/non-blocking for continued QA; must be reconciled before acceptance/merge**.

The installed app uses `Quick Magic`. Owner explicitly identified that wording as an inadequate Spanish-equivalent label and instructed that the custom character-sheet PDFs be used as the terminology reference.

Durable PDF terminology/visual references now exist under `assets/character-sheets/templates/`, including `REFERENCE.md` and rendered PNG pages. The PDF terminology uses `Lanzamiento de Conjuros` for the corresponding spellcasting area, together with `CD de Salvación de Conjuro`, `Modificador de Ataque Mágico`, `Aptitud Mágica`, `Espacios`, `Espacios Gastados`, and `Trucos`.

Do not silently rename during this QA pass; reconcile in the correction/consolidation pass with owner approval.

### E-01 — Equipment drag/reorder works but lacks clear feedback

Status: **limitation/non-blocking; functional failure resolved by Check 13A**.

During Check 7, the owner initially reported that the visible triple-line/reorder affordance in Equipo appeared not to work. Check 13A confirms that the final equipment order does change successfully when the gesture is performed deliberately. The issue is therefore weak drag feedback/discoverability, consistent with D-01 in Combate, not broken reorder behavior.

### E-02 — Equipment row actions are too bulky

Status: **limitation/non-blocking**.

Owner reports the `Editar` and `Eliminar` buttons consume too much space for compact equipment rows. This is a density/layout issue and should be reviewed against the intended compact equipment presentation and the owner's character-sheet visual references.

### E-03 — Equipment drag handle is visually too large; owner proposes handle-free long-press drag

Status: **limitation/non-blocking / design proposal for correction pass**.

During Check 13A the owner reports that the three-line drag icon is too large for a compact equipment row. The owner further suggests that a dedicated icon may not be necessary at all: press-and-hold the row/block itself, then move it to reorder. Treat this as a proposed interaction for the correction pass rather than an already-approved implementation detail. Any handle-free design must retain clear drag-start/movement feedback and avoid gesture ambiguity, while preserving adequate touch/accessibility behavior.

### E-04 — Equipo controls remain hidden with keyboard open

Status: **FAIL/blocking**.

During Check 13B, the owner reports that lower controls — specifically `Editar` and `Eliminar` — remain hidden/inaccessible while the Android keyboard is open. Equipo needs sufficient IME inset/scroll range so every relevant control can be reached without dismissing the keyboard.

### U-01 — Wrapped two-line text is top-aligned instead of vertically centered

Status: **FAIL/blocking on Combate acceptance surface; broader scope still to be mapped**.

Owner first reported generally that labels/content wrapping to two lines remain aligned toward the top of their available control/row area instead of being vertically centered. Check 12A confirms the defect specifically in the Combate quick-reference row, where vertical centering was an explicit corrective requirement from the prior phone QA.

### C-01 — Combat editor bottom actions remain unreachable with keyboard open

Status: **FAIL/blocking**.

During Check 12B, the owner can use most of the combat editor with the Android keyboard visible, but cannot scroll far enough to see/reach the action buttons at the bottom of the window. This is the remaining IME-safety defect from the prior phone QA and directly fails the explicit Check 12 requirement.

### C-02 — Outside tap does not dismiss the Android keyboard in combat editor

Status: **limitation/non-blocking**.

During Check 12C, tapping outside the active field does not hide the keyboard; Android system Back is required to dismiss the IME. The important editor-retention behavior is correct: the editor remains open and the draft is preserved. Treat this as a usability refinement unless later testing shows it contributes to data loss or navigation failure.

### C-03 — Combat blocks are too vertically spacious

Status: **limitation/non-blocking**.

Owner reports that each combat block consumes substantially more space than desirable. The combat list should be made somewhat more compact while preserving readability, touch targets, and the information hierarchy. This is consistent with the project's phone-first character-sheet workflow and should be considered together with drag affordance feedback rather than solved by shrinking interactive targets below usability requirements.

### D-01 — Drag-and-drop lacks clear visual movement feedback

Status: **limitation/non-blocking, but high-priority usability correction**.

Checks 12D and 13A confirm functional drag reorder in both Combate and Equipo, but the owner had difficulty noticing that an item was moving because the gesture gives insufficient visible feedback. The drag interaction should provide clear affordance and in-progress feedback (for example visible lift/offset/reordering response) so users can tell that the item is being dragged. Apply the correction consistently anywhere this drag pattern is used.

### N-01 — Android system Back exits the app instead of navigating within the app

Status: **FAIL/blocking pending scope confirmation**.

Owner reports that pressing the Android system Back action from an internal app screen takes them out of the app. Expected Android behavior for this product is that system Back should behave like the app's own back/navigation action through internal screens and should only exit when already at the app's main/root screen. Scope should be mapped during the remaining QA (character editor, PC Settings, campaign/character selection, modal editors). Note from Check 12C: when the IME is open, Android Back can first be used to dismiss the keyboard; the separate internal-navigation failure still needs its own scoped retest with the keyboard closed.

## Current QA disposition

- Checks 1–11: PASS.
- Check 12: complete; overall FAIL/blocking because 12A and 12B fail. 12C and functional 12D pass with usability limitations.
- Check 13: in progress; 13A PASS functionally, 13B FAIL/blocking.
- Confirmed blocking defects: U-01/12A, C-01/12B, E-04/13B, N-01 pending scope confirmation.
- Equipment reorder is confirmed functional; E-01 is reclassified as a drag-feedback/discoverability limitation.
- Non-blocking/reconciliation findings: T-01, E-01, E-02, E-03, C-02, C-03, D-01.
- QA should continue step-by-step to discover the full defect set before deciding the correction batch, unless the owner explicitly requests an immediate stop-and-fix cycle.

# Phase 4 Owner Phone QA Results

Date: 2026-09-01
Branch: `implementation/character-data-foundation`
Designated owner-QA APK: Gate L artifact `9785676981` (`dnd-custom-aid-debug-apk`), tested commit `089a991c6491627961f1e75f3815959a8a1c8b48`.

This file is the consolidated owner real-device QA record against the authoritative 42-step checklist in `2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`.

Status values: PASS, FAIL/blocking, limitation/non-blocking, NOT TESTED / partially verified.

## Checks 1–11

1. **PASS — Migration preservation / pre-Phase-4 character opens.** Existing character created before Phase 4 remains present and opens normally after upgrade.
2. **PASS — Legacy data preserved.** Prior stats/classes/saves/skills/proficiency/Quick Magic data/spell slots/combat/equipment/currencies appear preserved.
3. **PASS — New Phase 4 domains initialize empty/default.** Background/traits/notes/conceptual spell domains are empty/default as expected for migrated legacy data.
4. **PASS — Spellcaster-toggle migration.** Character with pre-existing meaningful spellcasting data migrated with spellcasting enabled.
5. **PARTIALLY VERIFIED — navigation tab count.** With spellcasting enabled, all 8 expected tabs were directly confirmed. The 7-tab spellcasting-OFF count has not yet been directly counted and must not be inferred from Check 8; capture this naturally during a later OFF-state test, preferably Check 41.
6. **PASS — Horizontal tab navigation / selected-tab visibility at all supported scales.** 80%, 90%, 100%, 115%, and 130% all behaved correctly.
7. **PASS — Selected tab survives rotation/recreation.** Selected tab remains correct through orientation change.
8. **PASS — Conjuros fallback when spellcasting is disabled.** Starting from `Conjuros`, disabling spellcasting removes the tab and returns to `General` without crash, blank state, or invalid selection.
9. **PASS — Re-enable spellcasting without forced navigation or data loss.** Re-enabling restores `Conjuros` and prior spellcasting data without forcing navigation or resetting state.
10. **PASS — Hide-not-delete warning for meaningful spellcasting data.** Warning/confirmation is clear and prior spellcasting data returns intact after re-enable.
11. **PASS — General adjustment marker and speed formatting.** Compact non-zero adjustment indicator behaves as intended, and `Velocidad` uses the approved imperial-first format with approximate metric in parentheses.

## Check 12 — Combate corrective backlog

- **12A: FAIL/blocking — vertical centering.** In the Combate quick-reference row, when one label/value wraps to two lines, neighboring one-line text remains top-aligned instead of vertically centered.
- **12B: FAIL/blocking — IME/keyboard reachability incomplete.** The combat editor cannot scroll far enough to bring bottom action buttons into view while the keyboard is open.
- **12C: PASS core requirement, with non-blocking keyboard UX limitation.** Outside tap does not close the editor or discard the draft. The keyboard itself does not dismiss on outside tap and currently requires Android Back.
- **12D: PASS functional drag reorder, with weak feedback.** Reorder works, but drag movement gives very little visible feedback.

Check 12 overall: **FAIL/blocking** because 12A and 12B fail explicit acceptance requirements.

## Check 13 — Equipo corrective backlog

- **13A: PASS — functional drag reorder.** Equipment entries reorder successfully; prior apparent failure was caused by weak feedback/discoverability.
- **13B: FAIL/blocking — keyboard safety / lower controls inaccessible.** With the keyboard open, `Editar` and `Eliminar` remain hidden/inaccessible.
- **13C: FAIL/blocking — currencies are not compact enough.** `Monedas` consumes too much vertical space.
- **13D: PASS — responsive columns/orientation.** Equipo responds acceptably between portrait and landscape.
- **13E: PASS — special-equipment presentation/functionality.** Long description, location, attunement, and special-item surface are usable. Known global spacing and IME-hidden-control findings remain active.

Check 13 overall: **FAIL/blocking** because 13B and 13C fail explicit acceptance requirements.

## Check 14 — Habilidades responsive two-column layout

- **14A: PASS — 115% text scale.** `Habilidades -> Por atributo` remains two-column without problematic overlap/clipping.
- **14B: PASS — 130% text scale.** Same result at 130%.

Check 14 overall: **PASS**.

## Check 15 — PC Settings font/theme persistence

- **15A: PASS — text-scale persistence.** Scale remains selected and visibly active after leaving and reopening PC Settings.
- **15B: PASS — theme persistence.** Chosen theme remains active and selected after leaving and reopening PC Settings, with no mixed/reset state.

Check 15 overall: **PASS**.

## Check 16 — Trasfondo narrative persistence

- **16A: PASS — all currently implemented narrative fields editable and saveable.**
- **16B: PASS — saved narrative values persist after leaving and reopening the same character.**

Check 16 overall: **PASS** for currently implemented fields. B-01 remains an additive correction request.

## Check 17 — Trasfondo image placeholders

- **17: PASS.** Both placeholders respond acceptably to available width in portrait and landscape, without clipping, overlap, awkward fixed narrowness, or disproportionate scaling.

## Check 18 — Trasfondo narrative sizing

- **18: PASS.** Compact narrative cards remain appropriately smaller than the larger `Historia del Personaje` writing area. Known cross-cutting spacing/padding finding L-01 remains active and is not waived by this PASS.

## Check 19 — Trasfondo keyboard / outside-tap behavior

- **19A: PASS — keyboard reachability.** Lower narrative content remains reachable with the Android keyboard open.
- **19B: PASS — active edit retention.** Tapping outside an actively edited field does not silently discard the unsaved text.

Check 19 overall: **PASS**.

## Check 20 — Rasgos types and sources

- **20A: PASS — multiple `Tipo` values.** Several entries can use different type values independently.
- **20B: PASS — multiple `Fuente` values.** Several entries can use different sources independently.

Check 20 overall: **PASS**.

## Check 21 — Rasgos activation/action type

- **21A: PASS.** Different activation/action-type values can be selected and retained independently across entries without silent resets or cross-entry mutation.

Check 21 overall: **PASS**.

## Check 22 — Rasgos uses and recovery

- **22A: PASS — maximum/spent uses.** Maximum uses and spent uses can be changed independently and remain mathematically consistent for the tested entry.
- **22B: PASS — recovery text.** Recovery/recharge text can be edited independently, remains attached to the correct Rasgo, and does not alter usage values or another Rasgo.

Check 22 overall: **PASS**. R-01 remains an owner-requested presentation correction for the usage summary.

## Check 23 — Rasgos drag reorder and persistence

- **23: PASS.** The owner reordered multiple Rasgos entries, saved the character, left/reopened the same character, and confirmed the exact reordered sequence persisted.

Check 23 overall: **PASS**. Existing D-01 weak drag-feedback/discoverability finding remains separate and does not invalidate persistence behavior.

## Check 24 — Rasgos wide multi-column presentation

- **24: PASS.** On the owner's phone in landscape/wide presentation, Rasgos made acceptable use of the available width with no reported overlap, clipping, unusable controls, ordering confusion, or excessive horizontal dead space.

Check 24 overall: **PASS**. Existing L-01 spacing/padding direction remains separate.

# Findings / correction backlog discovered during QA

## T-01 — `Quick Magic` terminology mismatch

Status: **limitation/non-blocking for continued QA; must be reconciled before acceptance/merge**.

The app uses `Quick Magic`. Owner directed that the custom character-sheet PDFs under `assets/character-sheets/templates/` be the terminology reference. They use `Lanzamiento de Conjuros` and related Spanish terminology. Do not silently rename during active QA; reconcile in the correction/consolidation pass with owner approval.

## E-01 — Equipment drag/reorder works but lacks clear feedback

Status: **limitation/non-blocking**. Functional failure resolved by Check 13A; issue is discoverability/feedback.

## E-02 — Equipment row actions are too bulky

Status: **limitation/non-blocking**. `Editar` and `Eliminar` consume too much space for compact phone-first rows.

## E-03 — Equipment drag handle visually too large / handle-free long-press proposal

Status: **limitation/non-blocking / design proposal**. Owner suggested press-and-hold on the row itself may be preferable, but this is not yet a final approved interaction. Any change must retain clear feedback and accessibility.

## E-04 — Equipo controls hidden with keyboard open

Status: **FAIL/blocking**. `Editar` and `Eliminar` remain inaccessible with the IME open.

## E-05 — Currency section too tall

Status: **FAIL/blocking** on the compact-currency acceptance target.

## B-01 — Add `Raza` and `Religion / Fe` to Trasfondo

Status: **owner-requested additive product correction**.

Add two persisted one-line fields to `Trasfondo`: `Raza` and `Religion / Fe`. Because these are durable character data, implementation requires data shape/migration/default handling, not UI-only fields.

## U-01 — Wrapped two-line text top-aligned instead of vertically centered

Status: **FAIL/blocking on Combate acceptance surface; broader scope still to be mapped**.

## C-01 — Combat editor bottom actions unreachable with keyboard open

Status: **FAIL/blocking**.

## C-02 — Outside tap does not dismiss Android keyboard in combat editor

Status: **limitation/non-blocking**. Editor/draft retention itself is correct.

## C-03 — Combat blocks too vertically spacious

Status: **limitation/non-blocking**.

## D-01 — Drag-and-drop lacks clear visual movement feedback

Status: **limitation/non-blocking, high-priority usability correction**. Apply consistently to drag-reorder surfaces.

## L-01 — Owner direction: cap unnecessary padding and margins

Status: **owner-approved cross-cutting layout direction**.

Reduce/cap unnecessary padding and margins across phone-first character UI, especially repeated cards/rows/lists, while preserving touch/accessibility minimums. Exact numeric spacing tokens are not yet selected and should be chosen during correction implementation/review. Owner does not need to repeat this known finding on every subsequent screen.

## N-01 — Android system Back exits app instead of navigating internally

Status: **FAIL/blocking pending scope confirmation**.

Expected behavior is internal back/navigation until the app root; only then should Back exit. Scope still needs mapping across character editor, PC Settings, campaign/character selection, and modal editors. With the IME open, Android Back may first dismiss the keyboard.

## R-01 — Rasgos usage summary is unclear

Status: **owner-requested usability correction / non-blocking for continued QA**.

Current presentation similar to `Usos X/Y · Gastados Y-X` feels awkward/unclear. Replace it during the correction pass with a cleaner, immediately understandable Spanish presentation while preserving max/spent/remaining semantics. Exact final wording/layout is not yet approved.

# Current QA disposition / resume point

- Checks 1–4: PASS.
- Check 5: **partially verified** — 8 tabs ON directly confirmed; exact 7-tab OFF count still pending direct verification.
- Checks 6–11: PASS.
- Check 12: **FAIL/blocking** due 12A and 12B.
- Check 13: **FAIL/blocking** due 13B and 13C.
- Checks 14–24: PASS for their tested acceptance criteria.
- Confirmed blocking defects: **U-01/12A, C-01/12B, E-04/13B, E-05/13C, N-01 pending scope confirmation**.
- Non-blocking/reconciliation findings: **T-01, E-01, E-02, E-03, C-02, C-03, D-01, R-01**.
- Owner-requested additive correction: **B-01** (`Raza`, `Religion / Fe`).
- Owner-approved cross-cutting layout direction: **L-01**.
- The Gate L APK is not acceptable for merge; a correction APK/retest cycle will be required after full defect collection.
- QA resumed on 2026-09-02; **Checks 23 and 24 passed**.
- **Exact next QA step: Check 25 — create multiple spellcasting sources, including one class-linked source and one custom source.**
- Do not merge Phase 4 and do not restart earlier implementation/QA steps when resuming.

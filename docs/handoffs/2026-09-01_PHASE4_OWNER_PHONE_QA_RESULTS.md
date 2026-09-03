# Phase 4 Owner Phone QA Results — FINAL CONSOLIDATED RECORD

Date started: 2026-09-01  
QA completed: 2026-09-02  
Branch: `implementation/character-data-foundation`  
Designated owner-QA APK: Gate L artifact `9785676981` (`dnd-custom-aid-debug-apk`)  
Workflow run: `33468310534`  
Executable/tested commit: `089a991c6491627961f1e75f3815959a8a1c8b48`

This file is the **authoritative consolidated owner real-device QA record** for the 42-step checklist in `docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`.

The 42-step owner phone QA is now complete. Completion of the checklist is **not** owner acceptance and is **not** permission to merge Phase 4. The tested Gate L APK has unresolved blocking defects and requires a focused correction build followed by retest/regression before it can become a merge candidate.

## Authority / checkpoint reconciliation

During QA, individual checkpoint files were created to preserve progress, especially after Check 22 and for Checks 25–42. Those files remain useful historical evidence, but they are **subordinate to this final consolidated record**.

In particular:

- any older pause/resume file that says QA is incomplete is superseded;
- any individual checkpoint that contains an old `Exact next QA step` / `Resume point` is historical and must not be treated as the current project resume point;
- later specific owner observations captured in the individual checkpoint files are incorporated here as findings `S-01`, `N-02`, and `N-03`;
- `docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md` remains the authoritative definition of what Checks 1–42 mean;
- this file is the authoritative record of the owner results and correction backlog derived from those checks.

Do not restart Checks 1–42 unless a focused correction retest or regression explicitly requires it.

# Final checklist disposition

- **40 of 42 top-level checks: PASS** for their tested acceptance behavior.
- **Check 12: FAIL/blocking** because 12A and 12B fail.
- **Check 13: FAIL/blocking** because 13B and 13C fail.
- **Check 5 is fully closed:** exactly 8 tabs with spellcasting ON and exactly 7 tabs with spellcasting OFF were directly verified by completion of Check 41.
- Check 25 is a functional **PASS** with non-blocking finding `S-01`.
- Checks 35–36 are **PASS** with Notes UX findings `N-02` and `N-03`.
- A separate blocking navigation defect, `N-01`, was discovered during QA and remains active even though it is not itself one of the two failed top-level checklist items.
- Gate L is **not acceptable for merge** in its current form.

# Checks 1–11

1. **PASS — Migration preservation / pre-Phase-4 character opens.** Existing character created before Phase 4 remains present and opens normally after upgrade.
2. **PASS — Legacy data preserved.** Prior stats/classes/saves/skills/proficiency/Quick Magic data/spell slots/combat/equipment/currencies remain present.
3. **PASS — New Phase 4 domains initialize empty/default.** Background/traits/notes/conceptual-spell domains are empty/default as expected for migrated legacy data.
4. **PASS — Spellcaster-toggle migration.** Representative pre-existing meaningful spellcasting data migrated with spellcasting enabled as intended.
5. **PASS — Navigation tab count.** Eight top-level character tabs were directly verified with spellcasting ON; seven were directly counted with spellcasting OFF during Check 41.
6. **PASS — Horizontal tab navigation / selected-tab visibility at supported scales.** 80%, 90%, 100%, 115%, and 130% behaved correctly.
7. **PASS — Selected tab survives rotation/recreation.** Selected tab remains correct through orientation change when the tab still exists.
8. **PASS — Conjuros fallback when spellcasting is disabled.** Starting from `Conjuros`, disabling spellcasting removes the tab and returns deterministically to `General` without crash, blank state, or invalid selection.
9. **PASS — Re-enable spellcasting without forced navigation or data loss.** Re-enabling restores `Conjuros` and prior spellcasting data without forcing navigation to that tab or resetting state.
10. **PASS — Hide-not-delete warning for meaningful spellcasting data.** Warning/confirmation is clear and prior spellcasting data returns intact after re-enable.
11. **PASS — General adjustment marker and speed formatting.** Compact non-zero adjustment indicator behaves as intended, and `Velocidad` uses the approved imperial-first format with approximate metric in parentheses.

# Check 12 — Combate corrective backlog

- **12A: FAIL/blocking — vertical centering.** In the Combate quick-reference row, when one label/value wraps to two lines, neighboring one-line content remains top-aligned instead of vertically centered.
- **12B: FAIL/blocking — IME/keyboard reachability incomplete.** The combat editor cannot scroll far enough to bring bottom action buttons into view while the keyboard is open.
- **12C: PASS core requirement, with non-blocking keyboard UX limitation.** Outside tap does not close the editor or discard the draft. The keyboard itself does not dismiss on outside tap and currently requires Android Back.
- **12D: PASS functional drag reorder, with weak feedback.** Reorder works, but drag movement gives very little visible feedback.

Check 12 overall: **FAIL/blocking** because 12A and 12B fail explicit acceptance requirements.

# Check 13 — Equipo corrective backlog

- **13A: PASS — functional drag reorder.** Equipment entries reorder successfully; prior apparent failure was caused by weak feedback/discoverability.
- **13B: FAIL/blocking — keyboard safety / lower controls inaccessible.** With the keyboard open, `Editar` and `Eliminar` remain hidden/inaccessible.
- **13C: FAIL/blocking — currencies are not compact enough.** `Monedas` consumes too much vertical space.
- **13D: PASS — responsive columns/orientation.** Equipo responds acceptably between portrait and landscape.
- **13E: PASS — special-equipment presentation/functionality.** Long description, location, attunement, and special-item surface are usable. Known global spacing and IME-hidden-control findings remain active.

Check 13 overall: **FAIL/blocking** because 13B and 13C fail explicit acceptance requirements.

# Checks 14–24

14. **PASS — Habilidades responsive two-column layout.** `Habilidades -> Por atributo` remains two-column without problematic overlap/clipping at both 115% and 130% text scale.
15. **PASS — PC Settings font/theme persistence.** Text scale and selected theme remain active and selected after leaving/reopening Settings, with no mixed/reset state.
16. **PASS — Trasfondo narrative persistence.** All currently implemented narrative fields are editable/saveable and saved values persist after leaving and reopening. `B-01` remains an additive owner correction.
17. **PASS — Trasfondo image placeholders.** Both placeholders respond acceptably to available width in portrait and landscape without clipping, overlap, awkward fixed narrowness, or disproportionate scaling.
18. **PASS — Trasfondo narrative sizing.** Compact narrative cards remain appropriately smaller than the larger `Historia del Personaje` writing area. `L-01` remains active and is not waived by this PASS.
19. **PASS — Trasfondo keyboard / outside-tap behavior.** Lower narrative content remains reachable with the keyboard open, and tapping outside an actively edited field does not silently discard unsaved text.
20. **PASS — Rasgos types and sources.** Several entries can use different `Tipo` and `Fuente` values independently.
21. **PASS — Rasgos activation/action type.** Different activation/action-type values can be selected and retained independently across entries without silent resets or cross-entry mutation.
22. **PASS — Rasgos uses and recovery.** Maximum uses, spent uses, and recovery/recharge text behave independently and remain attached to the correct Rasgo. `R-01` remains an owner-requested presentation correction.
23. **PASS — Rasgos drag reorder and persistence.** Multiple Rasgos were reordered, saved, left/reopened, and the exact sequence persisted. `D-01` remains separate.
24. **PASS — Rasgos wide multi-column presentation.** Landscape/wide presentation used available width acceptably with no reported overlap, clipping, unusable controls, ordering confusion, or excessive horizontal dead space.

# Checks 25–34 — Conjuros

25. **PASS — Multiple spellcasting sources, with non-blocking input UX finding.** A class-linked source and a custom source can be created/selected independently without disappearance, corruption, or crash. `S-01` recorded separately below.
26. **PASS — Source rename/reorder/delete and selected-source fallback.** Sources can be renamed, reordered, and deleted; deleting the selected source falls back to a valid surviving source without crash/data loss, and the resulting state persists.
27. **PASS — Linked-class removal preserves surviving source/spells.** Deleting/unlinking the linked class does not cascade-delete the surviving spell source or conceptual spells; state survives reopen.
28. **PASS — Conceptual spells across cantrips and levels 1–9.** At least one spell was exercised at cantrip/0 and each spell level 1 through 9 with correct grouping and persistence.
29. **PASS — One conceptual spell can belong to multiple sources.** The same conceptual spell can be associated with multiple sources without becoming independent duplicate spell records; associations remain coherent and persist.
30. **PASS — `Todos` deduplicates multi-source conceptual spells.** The multi-source spell appears exactly once in `Todos`, remains appropriately visible in each individual source, and does not duplicate after view switching/reopen.
31. **PASS — `Preparado` is source-specific.** Prepared state can differ independently between sources for the same conceptual spell and survives view switching/reopen.
32. **PASS — Search is scoped to the active `Todos`/source view.** Search filters within the currently selected view and does not leak unrelated source-only spells into the result.
33. **PASS — Spell drag reorder within a level persists.** Spells can be reordered within the same level and the exact order persists after save/reopen. `D-01` remains separate.
34. **PASS — Quick Magic / Conjuros spell-slot synchronization.** Spell-slot pip state is shared and remains synchronized in both directions and after reopen.

Conjuros Checks 25–34 are functionally complete. `S-01` and terminology finding `T-01` remain correction items.

# Checks 35–37 — Notas

35. **PASS — `Notas generales` unrestricted scratchpad.** Long free-form text, paragraphs/line breaks, middle editing, scrolling, and persistence after reopen all worked. `N-02` recorded separately.
36. **PASS — Titled note cards.** Create, edit, delete, drag reorder, and persistence all worked; unrelated notes remained intact. `N-02` extension and `N-03` recorded separately.
37. **PASS — Notes keyboard behavior + rotation/recreation.** Notes remained usable with the keyboard and through portrait/landscape recreation for the tested path; no saved-note loss, duplication, or ordering corruption was observed.

# Checks 38–42 — Final resilience

38. **PASS — Repeated navigation resilience.** Repeated switching among all available character tabs produced no blank screen, freeze, crash, or observed data loss.
39. **PASS — Screen off/on and full app close/reopen.** Resume and full relaunch both preserved character/campaign data without obvious corruption/reset.
40. **PASS — Unsaved editor state across orientation recreation.** The tested in-progress unsaved edit survived portrait/landscape recreation and the final saved value persisted.
41. **PASS — Spellcasting OFF/ON is hide-not-delete.** OFF hides Conjuros and produces exactly seven tabs; ON restores exactly eight tabs and preserves sources, spells, prepared states, and slot state across reopen. This also closes the previously pending OFF-state portion of Check 5.
42. **PASS — Icon-only control sampling.** Sampled icon-only controls were acceptable for touch usability and contextual meaning/accessibility; no systemic failure was identified.

# Consolidated correction backlog

## Blocking defects — must be corrected before a new merge candidate

### U-01 / Check 12A — Combate wrapped-row vertical centering

**Status:** FAIL/blocking.

When one quick-reference label/value wraps to two lines, neighboring single-line content top-aligns instead of vertically centering. Correct the row/layout behavior and retest the affected Combate quick-reference surface.

### C-01 / Check 12B — Combat editor bottom actions unreachable with IME

**Status:** FAIL/blocking.

The combat editor cannot scroll/pan enough to expose bottom action buttons while the Android keyboard is open. Correct keyboard-safe reachability and retest with the IME open.

### E-04 / Check 13B — Equipo actions inaccessible with IME

**Status:** FAIL/blocking.

`Editar` and `Eliminar` remain hidden/inaccessible with the keyboard open. Correct keyboard-safe reachability and retest the relevant equipment editor/actions.

### E-05 / Check 13C — Monedas section too tall

**Status:** FAIL/blocking.

The currency presentation consumes too much vertical space for the approved compact phone-first target. Redesign/compact the surface while keeping it usable and readable.

### N-01 — Android system Back exits the app instead of navigating internally

**Status:** FAIL/blocking; exact scope still needs mapping before implementation/retest.

Expected behavior is internal back/navigation until the app root; only at the root should Back exit. Scope must be mapped across character editor, PC Settings, campaign/character selection, and modal/editor surfaces. With the IME open, Android Back may appropriately dismiss the keyboard first.

## Non-blocking / reconciliation findings to carry into correction pass

### T-01 — `Quick Magic` terminology mismatch

**Status:** non-blocking for QA; must be reconciled before acceptance/merge.

The app uses `Quick Magic`, while the owner-designated terminology reference is the custom character-sheet PDF material under `assets/character-sheets/templates/`, which uses `Lanzamiento de Conjuros` and related Spanish terminology. Do not invent a new vocabulary; reconcile the UI terminology against those owner references during correction review.

### E-01 — Equipo drag/reorder feedback is weak

**Status:** non-blocking usability correction.

Functional reorder passes. Improve visible drag movement/discoverability. This is the Equipo-specific instance of the broader `D-01` finding.

### E-02 — Equipo row actions are too bulky

**Status:** non-blocking usability correction.

`Editar` and `Eliminar` consume excessive horizontal/vertical space for compact phone-first rows.

### E-03 — Equipment drag handle is visually too large

**Status:** non-blocking / design proposal not final.

The owner suggested that press-and-hold on the row itself might be preferable to a large dedicated handle. This interaction is **not yet an approved final design**. Any implementation change must preserve discoverability, drag feedback, touch targets, and accessibility.

### C-02 — Outside tap does not dismiss Android keyboard in combat editor

**Status:** non-blocking usability correction.

Outside tap correctly does not discard/close the draft, but it also does not dismiss the keyboard. Improve keyboard dismissal without violating draft-retention behavior.

### C-03 — Combate blocks are too vertically spacious

**Status:** non-blocking usability correction.

Reduce unnecessary vertical space as part of the broader `L-01` compactness pass.

### D-01 — Drag-and-drop lacks clear visible movement feedback

**Status:** non-blocking, high-priority usability correction.

Functional reorder passed across tested surfaces, but the moving item/target position is often insufficiently obvious. Apply a consistent, accessible drag-feedback treatment across drag-reorder surfaces.

### R-01 — Rasgos usage summary is unclear

**Status:** owner-requested usability correction / non-blocking.

Current presentation similar to `Usos X/Y · Gastados ...` is awkward/unclear. Replace it with a cleaner immediately understandable Spanish presentation while preserving max/spent/remaining semantics. Exact final wording/layout still requires review.

### S-01 — Numeric spellcasting-source field opens normal text keyboard

**Status:** non-blocking input-UX defect; include before acceptance/merge.

A numeric field in the spellcasting-source workflow opens the normal text keyboard instead of an appropriate numeric keypad. The exact field label was not captured during owner QA; map the affected numeric input(s) during correction implementation and verify the appropriate numeric keyboard/input configuration.

### N-02 — Long Notes need visible scroll affordance

**Status:** owner-requested non-blocking usability improvement.

Applies to both `Notas generales` and long titled-note bodies. Preferred direction: allow the editor to grow to a sensible bounded height, then use internal vertical scrolling with a subtle visible scrollbar/scroll indicator when content exceeds that cap. Avoid allowing one long note to consume the entire screen and avoid awkward nested scrolling. Exact dimensions/tokens are not yet approved.

### N-03 — Titled note cards should use two columns in landscape/wide layouts

**Status:** owner-requested non-blocking usability improvement.

When available phone-landscape/wide width permits, titled note cards should use a responsive two-column presentation, with fallback to one column when width is insufficient. Preserve readable order, touch-target minimums, and accessibility.

# Owner-requested additive / cross-cutting corrections

## B-01 — Add `Raza` and `Religion / Fe` to Trasfondo

**Status:** owner-requested additive product correction.

Add two persisted one-line fields to `Trasfondo`: `Raza` and `Religion / Fe`. Because these are durable character data, implementation requires data shape/schema, migration/default handling, persistence tests, and UI—not UI-only placeholders.

## L-01 — Cap unnecessary padding and margins

**Status:** owner-approved cross-cutting layout direction.

Reduce/cap unnecessary padding and margins across the phone-first character UI, especially repeated cards/rows/lists, while preserving touch/accessibility minimums and readability. Exact spacing tokens are not yet selected and should be chosen during correction implementation/review. The owner does not need to repeat this finding screen by screen.

# Correction-pass implementation boundary

The owner QA discovery phase is complete. Do **not** add further product expansion as part of this correction pass except for the already recorded owner-approved/additive items above.

Recommended correction sequence:

1. map exact scope for `N-01` and affected numeric field(s) for `S-01`;
2. implement blocking corrections `U-01`, `C-01`, `E-04`, `E-05`, `N-01`;
3. implement owner-requested/additive corrections `B-01`, `L-01` and merge-compatible non-blocking fixes/reconciliations (`T-01`, `C-02`, `C-03`, `D-01`/`E-01`, `E-02`, `R-01`, `S-01`, `N-02`, `N-03`), while treating `E-03` as a proposal requiring interaction review rather than an automatic change;
4. run automated regression including migration/data-preservation coverage;
5. produce a clearly identified correction APK;
6. perform focused owner retest of every blocker and materially changed UX surface, plus targeted regression for neighboring behavior;
7. only after those results are acceptable, repair remaining continuity/documentation drift and perform Phase 4 merge review;
8. do not merge to `main` without explicit owner approval.

# Current project resume point

**QA discovery is complete. The exact next phase is correction planning / implementation on a non-main branch.**

Before editing production code, convert the consolidated backlog above into a focused correction plan that maps each finding to the affected code/data/test surfaces and defines the minimum retest needed. Do not restart implementation increments A–L and do not merge Phase 4 yet.

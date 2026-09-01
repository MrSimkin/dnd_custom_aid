# Phase 4 Owner QA pause / resume checkpoint — after Check 22B

Date: 2026-09-01
Branch: `implementation/character-data-foundation`
Consolidated QA results commit immediately before this checkpoint: `6bcd6e86f5881b4b3ee287cd93a07e34f99f180a`
Designated owner-QA APK: Gate L artifact `9785676981` (`dnd-custom-aid-debug-apk`), tested commit `089a991c6491627961f1e75f3815959a8a1c8b48`.

## Purpose

The owner explicitly paused real-device Phase 4 QA after completing Check 22B. Resume from this checkpoint; do not restart implementation A–L, do not repeat already completed QA checks unless required for a focused defect retest, and do not merge Phase 4.

Authoritative consolidated QA record:
`docs/handoffs/2026-09-01_PHASE4_OWNER_PHONE_QA_RESULTS.md`

Authoritative checklist:
`docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`

## QA completed through pause

- Checks 1–4: PASS.
- Check 5: partially verified — 8 tabs with spellcasting ON directly confirmed; exact 7-tab OFF count still needs a direct count later, preferably during Check 41.
- Checks 6–11: PASS.
- Check 12: overall FAIL/blocking due vertical-centering defect and combat-editor IME reachability.
- Check 13: overall FAIL/blocking due Equipo IME-hidden controls and overly tall Monedas section.
- Checks 14–22: PASS for the tested acceptance criteria.
- Check 22A: maximum/spent uses behavior PASS.
- Check 22B: recovery text behavior PASS.

## Active blocking findings

- U-01 / 12A — wrapped/two-line Combate row content not vertically centered.
- C-01 / 12B — combat-editor bottom actions unreachable while IME is open.
- E-04 / 13B — Equipo `Editar`/`Eliminar` inaccessible while IME is open.
- E-05 / 13C — Monedas presentation too tall / not compact enough.
- N-01 — Android system Back exits the app instead of internal navigation; exact scope still needs mapping.

## Active non-blocking / correction findings

- T-01 — replace/reconcile `Quick Magic` terminology using the owner PDFs as reference.
- E-01 / D-01 — drag works but feedback/discoverability is weak.
- E-02 — Equipo row actions too bulky.
- E-03 — oversized drag handle; handle-free long-press is an owner proposal, not yet a final decision.
- C-02 — outside tap does not dismiss keyboard in combat editor.
- C-03 — combat blocks too vertically spacious.
- R-01 — Rasgos summary like `Usos X/Y · Gastados Y-X` is unclear and needs a cleaner Spanish presentation.

## Owner-approved/additive directions

- B-01 — add persisted one-line `Raza` and `Religion / Fe` fields to Trasfondo.
- L-01 — cap/reduce unnecessary padding and margins across phone-first character UI while preserving accessibility/touch targets. The owner will not repeat this known cross-cutting observation on every screen.

## Exact resume step

Resume with **Check 23 — Rasgos drag reorder + persistence**.

One subcheck at a time. First ask the owner to reorder several Rasgos entries, save, leave/reopen the same character, and verify that the exact order persists. Do not skip to Check 24 until Check 23 is classified.

After Check 23, continue sequentially with Check 24 and then Conjuros checks 25–34, Notas 35–37, and resilience 38–42.

Do not infer the missing 7-tab OFF count from earlier tests; capture it explicitly when a natural spellcasting-OFF state occurs, preferably Check 41.

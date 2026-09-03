# SUPERSEDED — Phase 4 Owner QA pause / resume checkpoint — after Check 22B

> **Superseded on 2026-09-02. Do not resume QA from this file.** The full 42-step owner phone QA has been completed. The authoritative current record and correction backlog is `docs/handoffs/2026-09-01_PHASE4_OWNER_PHONE_QA_RESULTS.md`. Any `Exact resume step` or incomplete-QA status below is preserved only as historical context.

Date: 2026-09-01
Branch: `implementation/character-data-foundation`
Consolidated QA results commit immediately before this checkpoint: `6bcd6e86f5881b4b3ee287cd93a07e34f99f180a`
Designated owner-QA APK: Gate L artifact `9785676981` (`dnd-custom-aid-debug-apk`), tested commit `089a991c6491627961f1e75f3815959a8a1c8b48`.

## Purpose — historical

The owner explicitly paused real-device Phase 4 QA after completing Check 22B. This was the correct resume point at the time. It is no longer the current project resume point.

Authoritative final consolidated QA record:
`docs/handoffs/2026-09-01_PHASE4_OWNER_PHONE_QA_RESULTS.md`

Authoritative checklist:
`docs/handoffs/2026-09-01_INCREMENT_L_PHONE_QA_TARGET.md`

## QA completed through this historical pause

- Checks 1–4: PASS.
- Check 5: was partially verified at this pause — later fully closed by Check 41.
- Checks 6–11: PASS.
- Check 12: overall FAIL/blocking due vertical-centering defect and combat-editor IME reachability.
- Check 13: overall FAIL/blocking due Equipo IME-hidden controls and overly tall Monedas section.
- Checks 14–22: PASS for the tested acceptance criteria.
- Check 22A: maximum/spent uses behavior PASS.
- Check 22B: recovery text behavior PASS.

## Blocking findings known at this historical pause

- U-01 / 12A — wrapped/two-line Combate row content not vertically centered.
- C-01 / 12B — combat-editor bottom actions unreachable while IME is open.
- E-04 / 13B — Equipo `Editar`/`Eliminar` inaccessible while IME is open.
- E-05 / 13C — Monedas presentation too tall / not compact enough.
- N-01 — Android system Back exits the app instead of internal navigation; exact scope still needs mapping.

## Non-blocking / correction findings known at this historical pause

- T-01 — replace/reconcile `Quick Magic` terminology using the owner PDFs as reference.
- E-01 / D-01 — drag works but feedback/discoverability is weak.
- E-02 — Equipo row actions too bulky.
- E-03 — oversized drag handle; handle-free long-press is an owner proposal, not yet a final decision.
- C-02 — outside tap does not dismiss keyboard in combat editor.
- C-03 — combat blocks too vertically spacious.
- R-01 — Rasgos summary like `Usos X/Y · Gastados Y-X` is unclear and needs a cleaner Spanish presentation.

Later QA added `S-01`, `N-02`, and `N-03`; see the authoritative final consolidated record rather than extending this historical file.

## Owner-approved/additive directions known at this pause

- B-01 — add persisted one-line `Raza` and `Religion / Fe` fields to Trasfondo.
- L-01 — cap/reduce unnecessary padding and margins across phone-first character UI while preserving accessibility/touch targets.

## Historical resume step — no longer active

The old resume point was **Check 23 — Rasgos drag reorder + persistence**. Checks 23–42 were subsequently completed.

**Current resume point:** correction planning / implementation on a non-main branch, as defined by `2026-09-01_PHASE4_OWNER_PHONE_QA_RESULTS.md`.

Do not restart the 42-step QA sequence unless a focused correction retest/regression explicitly requires individual checks to be repeated. Do not merge Phase 4 without explicit owner approval.

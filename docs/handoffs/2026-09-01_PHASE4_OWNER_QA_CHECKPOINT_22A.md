# Phase 4 Owner Phone QA checkpoint — through Check 22A

Date: 2026-09-01
Branch: `implementation/character-data-foundation`
Designated owner-QA APK: Gate L artifact `9785676981` (`dnd-custom-aid-debug-apk`), tested commit `089a991c6491627961f1e75f3815959a8a1c8b48`.

This checkpoint supplements `2026-09-01_PHASE4_OWNER_PHONE_QA_RESULTS.md` and records the owner QA results gathered after Check 17.

## Newly completed checks

- **18: PASS — Trasfondo narrative sizing.** Compact narrative cards remain appropriately smaller than the larger `Historia del Personaje` writing area. Known cross-cutting spacing/padding findings remain active and are not waived by this PASS.
- **19A: PASS — Trasfondo keyboard reachability.** Lower narrative content remains reachable with the Android keyboard open.
- **19B: PASS — Trasfondo outside-tap edit retention.** Tapping outside an actively edited narrative field does not silently discard the unsaved text.
- **Check 19 overall: PASS.**
- **20A: PASS — Rasgos multiple types.** Multiple feature/trait entries can use different `Tipo` values independently.
- **20B: PASS — Rasgos multiple sources.** Multiple entries can use different `Fuente` values independently.
- **Check 20 overall: PASS.**
- **21A: PASS — Rasgos activation/action-type values.** Different activation/action-type values can be selected and retained independently across entries.
- **Check 21 overall: PASS.**
- **22A: PASS — Rasgos maximum/spent uses behavior.** Maximum uses and spent uses can be changed independently and remain mathematically consistent for the tested entry.

## New finding

### R-01 — Rasgos uses summary is unclear

Status: **owner-requested usability correction / non-blocking for continued QA**.

The current summary presentation similar to `Usos X/Y · Gastados Y-X` feels awkward and unclear to the owner. The correction pass should replace it with a cleaner, immediately understandable Spanish presentation while preserving the same underlying max/spent/remaining-use semantics. Exact final wording/layout is not yet approved and should be reviewed during the correction pass rather than improvised during active QA.

## Existing owner directions still active

- B-01: add persisted one-line `Raza` and `Religion / Fe` fields to Trasfondo.
- L-01: reduce/cap unnecessary padding and margins across phone-first character UI while preserving touch/accessibility minimums.
- Known IME-hidden-control, drag-feedback, terminology, vertical-centering, and Android Back findings remain active as previously recorded.

## Exact next QA step

Continue with **Check 22B — Rasgos recovery text behavior**. Do not skip ahead to Check 23.

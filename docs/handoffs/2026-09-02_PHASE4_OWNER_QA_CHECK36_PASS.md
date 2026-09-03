# Phase 4 Owner Phone QA — Check 36

Date: 2026-09-02
Branch: `implementation/character-data-foundation`

## Check 36 — titled note cards

**PASS.** The owner created, edited, deleted, reordered, and reopened titled note cards successfully. Edited content persisted, the deleted card stayed deleted, reordered sequence persisted, and unrelated notes remained intact.

## Notes UX observations

### N-03 — titled note cards should use two columns in landscape

Status: **limitation/non-blocking / owner-requested UX improvement**.

When available width permits on phone landscape/wide layouts, titled note cards should use a two-column presentation rather than remaining a single narrow column. Preserve readable ordering, touch-target minimums, and responsive fallback to one column when width is insufficient.

### N-02 extension — long titled-note bodies need visible scroll affordance

Status: **limitation/non-blocking / owner-requested UX improvement**.

The previously recorded long-text scroll observation for `Notas generales` should also apply to long titled-note bodies. Preferred direction: allow the editor to grow to a sensible bounded height, then scroll internally with a subtle visible vertical scrollbar/scroll indicator so users can tell more text exists below without allowing one note to consume the entire screen.

## Resume point

**Exact next QA step: Check 37 — keyboard behavior and rotation/recreation with Notes state.**

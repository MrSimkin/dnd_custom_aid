# Phase 4 Owner Phone QA — Check 35

Date: 2026-09-02
Branch: `implementation/character-data-foundation`

## Check 35 — Notas generales unrestricted scratchpad

Result: **PASS**.

Owner confirmed that `Notas generales` accepts a long free-form block of text, preserves line breaks and edits, remains usable with scrolling, and persists unchanged after leaving and reopening the character.

## New UX finding — N-02

Status: **limitation/non-blocking / owner-requested usability improvement**.

For very long `Notas generales` content, add a clear scroll affordance so users can tell that additional content exists beyond the currently visible portion. Prefer a phone-friendly design that avoids awkward nested scrolling: allow the editor to grow to a sensible height cap, then provide internal vertical scrolling with a subtle visible scroll indicator/scrollbar when content exceeds that cap. Exact dimensions/tokens are not yet approved and should be selected during the correction pass together with L-01 spacing work.

Check 35 remains PASS; N-02 does not block continued QA.

Exact next QA step: **Check 36 — create, edit, delete and drag-reorder titled note cards.**

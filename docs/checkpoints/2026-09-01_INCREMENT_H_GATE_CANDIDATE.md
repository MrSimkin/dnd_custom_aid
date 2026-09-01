# Increment H — Gate H candidate

**Date:** 2026-09-01  
**Branch:** `tmp/increment-h-spell-list`  
**Clean implementation parent:** `d2891aff5de9dc4b1d20081ea16aae6fb7b2f62d`

## Recovery baseline

Repository/branch/checkpoint audit established `d71a47822bcbeb0d0aa9ba404ba4fd01af81b510` as the clean Increment G closure boundary. Increment H was resumed from that exact commit.

## Candidate scope

The clean Gate H candidate contains:

- conceptual spell list grouped by `Trucos` and levels 1–9;
- collapsible populated level sections and de-emphasized empty levels;
- source-aware list filtering for `Todos` and individual source views;
- compact search constrained to the selected view;
- conceptual spell add/edit/delete;
- one-or-more source associations;
- source-specific `Preparado` state;
- source-specific prepared indicators in `Todos` rather than a misleading universal checkbox;
- manual within-level long-press drag ordering;
- all approved permissive spell detail fields;
- reuse of the existing recreation-safe spellcasting draft and central character Save transaction;
- focused shared persistence regression for multi-source details, prepared state, ordering, update and deletion.

No schema migration is required because Increment C already established the complete spell/source persistence model.

## Safety history

Two isolated defects were caught before this gate:

1. the first asserted source-tab wiring matcher was too whitespace-specific and aborted with no production edit;
2. the first Android compile exposed a trailing comma inside the editor validation `when` expression; the exact syntax defect was corrected before this clean candidate.

Temporary self-edit workflow/trigger machinery used for narrow safe patches has been removed from the candidate.

## Gate H

This checkpoint exists to trigger the standard repository gate from the clean candidate. Promotion/closure requires:

- backend checks PASS;
- full shared/Kotlin tests PASS;
- Android debug build PASS;
- desktop build PASS;
- Android debug APK artifact upload PASS.

Manual touch ergonomics, drag feel, keyboard/IME behavior and visual responsive acceptance remain an explicit owner/device QA boundary and are not represented as automated interaction PASS.

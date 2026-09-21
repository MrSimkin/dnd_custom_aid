# Checkpoint — Custom v2 Integrated Production Audit — PASS

**Date:** 2026-09-20 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Integrated audit head:** `0295f30ca77214284902b0e13a563dcdaf58b501`  
**Final push Scaffold:** `35555004560` / run #3048 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10620120137`  
**Artifact digest:** `sha256:3ea3bf7b4eb007864e2f596fe712c08c7317676ced6427d290e92477f69afc1a`

## Purpose

Close the bounded production-integration audit for the owner-approved Custom-v2 Extended Run-7 family before moving to the remaining D-0074 production-promotion work.

This pass did **not** redesign the frozen Custom-v2 family. It audited real `PcSheetPdfRenderPlan` semantics, removed QA-fixture leakage, added data-driven continuation/pagination where the production model can exceed the frozen base geometry, and repaired only defects exposed by production data.

## Owner-approved visual authority preserved

The visual authority remains:

- Custom-v2 Extended Run 7 — OWNER APPROVED / FROZEN;
- both first-page modes remain supported:
  - per Attribute;
  - per Ability;
- the approved layered page grammar remains:
  1. STRUCTURE;
  2. CLEANUP;
  3. LABELS;
  4. VALUES;
  5. MARKERS.

No owner approval is inferred from this automated/integration audit. This checkpoint records production correctness against the already approved visual baseline.

## Production defects closed

The audit closed the following concrete defects without inventing new character subsystems:

- Custom Statistics now paginates instead of rejecting legitimate dense custom-Attribute/custom-skill sets.
- Traits/Features pagination preserves full trait names/details, proficiencies, background/reference continuation and current-state semantics.
- Resources/Options pagination is based on physical wrapped rows rather than only logical records.
- Inventory continuation preserves record fields omitted by the frozen base rows, including quantity/location/weight/equipped/attuned state, descriptions/notes and meaningful current inventory-usage semantics.
- Standard v2 currencies remain in the native Treasure region; only non-native currencies/details continue.
- Current Snapshot export preserves non-default temporary/current sheet data known to the app.
- Canonical/reference sheet semantics now preserved in the approved Traits/Other continuation include, where present:
  - background/personality/flaws/religion overflow;
  - canonical race/subrace/background identity;
  - subclass identity;
  - XP/milestone progress;
  - weapon masteries;
  - concentration, exhaustion, conditions and defenses;
  - alternate movements and senses;
  - active temporary effects;
  - structured combat damage;
  - spellcasting-source reference values;
  - forms and companions.
- Combat/actions beyond the native base capacity, or entries whose type/notes cannot be expressed by the native table, receive continuation rather than silent omission.
- Long character/class identity receives continuation rather than relying solely on a single narrow base field.

## QA-fixture leakage removed

Two production leaks from earlier visual-proof fixtures were found and removed:

1. the v2 `MUNICIONES` grid no longer receives hardcoded `3 / 5 / 7 / 9` fills;
2. `ESPACIOS GASTADOS` remains blank, matching the frozen owner paper-tracking contract.

The Custom portrait region also no longer renders the QA-only crossed `RETRATO QA` placeholder. When no real portrait bytes are supplied, the owner template portrait area remains blank as required by D-0074.

Actual portrait rendering remains a platform handoff task because Android currently stores persisted `content://` references while the shared PDF plan carries reference/availability semantics, not resolved image bytes.

## Final ruled-row repair

High-resolution dual-renderer inspection found one bounded visual production defect after the first green audit candidate:

- a wrapped Current Snapshot line (`Activo`) could occupy the final continuation row below the previously generated rule cadence.

Repair commit:

`0295f30ca77214284902b0e13a563dcdaf58b501`

The Traits extension structure now provides one additional final rule through the actual lower continuation capacity.

Differential proof versus the prior green candidate:

- pages 1–4: pixel-identical;
- pages 5–6: changed only at the final bottom ruled row, about 0.02% of pixels;
- page 7: pixel-identical.

The corrected pages were inspected independently in PDFium and Poppler. The final wrapped text now sits inside the ruled-paper rhythm and no clipping/overlap/broken glyph was observed.

## Representative exact-proof hashes

From artifact `10620120137`:

- `custom-v2-current-snapshot-semantics.pdf`  
  SHA-256: `1f75b4b1dfb2de9e641e6862dd05c3aa8f799cde9ec1807960667fd3c82eb098`
- `custom-v2-per-attribute-whole-draft.pdf`  
  SHA-256: `62eef772bb50ef063ae941272bc7ef096e2d084748a5078e584a14051b531a39`
- `custom-v2-per-ability-whole-draft.pdf`  
  SHA-256: `6666cb6b2ab3b694c29b10425592f5b496179a9cbcc29c2f50d95e9e020828e9`
- `custom-v2-custom-stats-overflow-attribute.pdf`  
  SHA-256: `7122d448e6066b2e3f039a9f513b1da9c37363b36bb60d8ea5c2770a6aa01dcf`
- `custom-v2-custom-stats-overflow-ability.pdf`  
  SHA-256: `f4bbc88a57a56a40be0822eb3fd94b9df99ca84e0cc3c96feb4f5ac8bf833a98`
- `custom-v2-resources-pagination-audit.pdf`  
  SHA-256: `0e35236c52e1652cea07e433ec96aad83055c3af81a27d7a223a81975e4de071`

## PDF preflight / renderer audit

Representative Current Snapshot proof:

- 7 Letter pages, 612 × 792 pt;
- not encrypted;
- no AcroForm fields;
- no attachments;
- no annotations;
- required source/fallback/text/symbol fonts present;
- exact artifact ZIP digest matched the GitHub Actions artifact digest;
- PDFium and Poppler both rendered the production/stress proofs successfully;
- cross-renderer differences were limited to expected antialiasing/rasterization noise.

Stress proofs covered:

- both Custom-v2 custom-stat modes with multi-page overflow;
- multi-page Resources/Options;
- dense production Pass-3 continuation;
- Current Snapshot operational/reference semantics.

No silent data truncation, broken glyphs, black squares, material clipping or layout collision was observed in the inspected proofs.

## Deliberately unresolved gates

This checkpoint does **not** claim complete D-0074 product closure.

Remaining known work includes:

1. Custom-v1 Extended production promotion from frozen Run 6;
2. Classic Run-2 production promotion from owner-review/dummy harness into a real plan-driven renderer;
3. application-owned optional appended Spellbook renderer;
4. platform portrait-byte handoff and crop/fit rendering;
5. Save/Share integration on Player Android, DM Android/tablet and DM Desktop;
6. `APP_MODIFIED_SHEET` visual implementation, which requires an owner-facing visual review rather than being invented silently;
7. D-0074 continuation cues on originating base sections, because those alter frozen base pages and therefore require an explicit bounded visual decision/review.

## Conclusion

**CUSTOM V2 INTEGRATED PRODUCTION AUDIT: PASS**

The owner-approved Custom-v2 Extended design is now production-driven by real character-plan semantics for the audited renderer scope, with bounded pagination/current-state fixes and no known QA-fixture leakage.

Next implementation gate:

> Promote the owner-approved Custom-v1 Extended Run-6 mechanics from test-only review code into a real `PcSheetPdfRenderPlan`-driven production renderer, one extension role at a time, without reopening the frozen visual design.

PR #85 remains **OPEN / DRAFT / DO NOT MERGE**.

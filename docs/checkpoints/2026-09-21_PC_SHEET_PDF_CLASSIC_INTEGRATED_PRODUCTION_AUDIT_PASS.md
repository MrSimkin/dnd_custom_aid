# Checkpoint — Classic Integrated Production Audit — PASS

**Date:** 2026-09-21 (Chile local time)  
**Branch:** `wave7/pc-sheet-pdf-renderer-template-proof`  
**PR:** #85 — OPEN / DRAFT / DO NOT MERGE  
**Owner-approved visual baseline:** `3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`  
**Audited production head:** `30ac4d073e9fab47a498f4e6dc3d9266c633110e`  
**Final Scaffold:** `35677870304` / run #3151 — SUCCESS  
**Proof artifact:** `pc-sheet-populated-template-proofs` / artifact `10674062891`  
**Artifact digest:** `sha256:bdd261acc89033519b38922cb2cbc843b4fd938aae25c9b74a07c70beead004c`

## Purpose

Close production promotion and the integrated semantic/current-state/pagination audit for the owner-approved Classic Run-2 family.

Classic is now driven by the real `PcSheetPdfRenderPlan`: three normal pages plus all six D-0074 continuation roles. This checkpoint does not reopen or redesign the frozen Classic visual grammar.

## Owner-approved visual authority preserved

The visual authority remains Classic corrected Run 2 — OWNER APPROVED / FROZEN:

- three normal Classic pages;
- Extended — Custom Statistics;
- Extended — Traits & Features;
- Extended — Resources & Options;
- Extended — Inventory / Equipment;
- Extended — Spells;
- Extended — Notes.

Production code reuses those approved mechanics. Semantic repairs made during productionization only bind canonical/current data into existing base controls or existing continuation channels.

## Production and audit closure

The final production path:

- dispatches Classic from the whole-sheet renderer into `DesktopClassicRenderer`;
- renders the three application-designed normal pages from the selected aggregate;
- appends each matching continuation role only when the selected canonical data requires it;
- retains the global overflow diagnostic as a fail-closed guard rather than silently dropping unhandled content.

The final semantic repair sequence also closed observed production gaps for:

- combat overflow and non-attack/reference semantics;
- long identity/background/trait/base text through bounded excerpts plus continuation;
- inventory names/state and deterministic continuation pagination;
- additional minimal companions beyond the single normal-page companion capacity;
- spell-slot totals while deliberately leaving spent-slot markers empty for writable paper tracking, per the active renderer protocol;
- carried-but-not-equipped inventory state (`Llevado`) through Inventory continuation.

## Integrated canonical / Current Snapshot audit

The audited Classic renderer preserves, where present and applicable:

- canonical character/background/species/subrace/class/subclass identity and long identity overflow;
- background summary/religion and narrative/personality/ideal/bond/flaw overflow;
- combat/actions beyond native capacity, non-attack action types and notes;
- milestone progress;
- inspiration, current/max HP, temporary HP and death saves;
- adjusted passive perception, initiative and movement speed;
- weapon masteries;
- exhaustion and concentration;
- conditions and defenses;
- alternate movements and senses;
- active temporary effects;
- structured combat damage;
- additional spellcasting-source reference values;
- spell-slot totals, with spent-slot marks intentionally omitted from generated ink so the player can track them on paper;
- forms and companions, including additional minimal companions;
- resources, custom markers and class options;
- inventory usage/current-state semantics, including stored/carried state;
- nonstandard currency/valuables;
- spells and prepared state;
- general notes and note cards.

Application/UI-only state such as quick-access ordering, haptics, table-mode preferences and reconciliation-history metadata is not treated as printable character-sheet content.

## Data-driven pagination / no-silent-loss evidence

Production regressions exercise real terminal content and page boundaries:

- Custom Statistics — multi-page custom Attribute/custom-skill overflow, including terminal linked skills;
- Traits & Features — overflow traits, languages/proficiencies and reference semantics;
- Resources & Options — terminal resources, custom markers and class options;
- Inventory / Equipment — 19-item pagination with explicit page-4/page-5 boundary assertions, usage state and nonstandard currency;
- Spells — 31 level-1 spells with explicit #14/#22 on page 4 and #23/#31 on page 5;
- Notes — general notes plus 30 note cards with the first and terminal card verified across pages.

The long-canonical-content stress proof also verifies terminal markers for long identity/background/traits and spell-slot counts beyond the four-marker normal-page capacity.

No tested canonical terminal row is silently dropped.

## Frozen-base preservation / writable spent-slot semantics

The final production base artifact preserves the established Classic production geometry and the active paper-tracking semantic.

Against the immediately preceding green production artifact before the erroneous spent-slot fill experiment, all three normal-page PNGs are byte-identical:

- page 1 SHA-256: `a0e211b11b00fe4ad0779278d1f3558804e06aaad925d018b1606d53cd2def82`;
- page 2 SHA-256: `b3c9ad4897753daabe1f20aa700f856fb37f0641cfc695f5a5da203057994b59`;
- page 3 SHA-256: `200896f133a94be517a37739075391cb60bf3c3b680acfc3a43e48f0724593eb`.

The active run protocol explicitly requires `ESPACIOS GASTADOS` to remain empty in generated sheets so those controls stay writable on paper. A matched Custom-v2 Permanent-vs-Current artifact comparison with different `spentSlots` produced 0 changed pixels on the spell page, and the Classic regression enforces the same semantic. Slot totals remain printable; spent-slot marks remain player-maintained paper state.

## Representative final proof hashes

From artifact `10674062891`:

- `classic-production-base-pass1.pdf`  
  SHA-256: `67b2156fe08583f44e4cef9654691eeb3b2d73f1c0dda231e8fd76ebae320d2f`
- `classic-current-snapshot-semantics.pdf`  
  SHA-256: `69a006bfcc8e0bc2f1f4ee6d8aae3d48359031d22e307e7e7104eb9f8afa59ee`
- `classic-canonical-overflow-audit.pdf`  
  SHA-256: `e1adc4c5bfb3ad374556c59e67c9e70f863646cd691406fc0b64709134f0d561`
- `classic-production-custom-stats-pass2.pdf`  
  SHA-256: `4600320e9c69b367c052f57a83a5c1794b3b15589e7097fbf6e38c44b66166f6`
- `classic-production-traits-pass3.pdf`  
  SHA-256: `8a3b2322624e85cf49eba552aff7682b00d45f46e483bcb8996e510e5b1e8d86`
- `classic-production-resources-pass4.pdf`  
  SHA-256: `85d29d6c9cea24ffb9e5e6a74d48e2c90579f62bc3db68ee7398b8eedacda637`
- `classic-production-inventory-pass5.pdf`  
  SHA-256: `061a12666b39b73e39ba33396e868e1972f6963887808b14220acb3d502f3ad6`
- `classic-production-spells-pass6.pdf`  
  SHA-256: `9a1ad40509f3ca4e2a7bef993d781589b789214c29c79e7a4585f80c3b94cd62`
- `classic-production-notes-pass7.pdf`  
  SHA-256: `d570225bd876b053271334791a8fdcf5a3e6efc872cc546b42e7083b82008c1c`

## PDF preflight / visual audit

Representative proofs were rendered and inspected after the final semantic repairs.

- base proof: 3 Letter pages, 612 × 792 pt;
- Current Snapshot proof: 11 Letter pages, 612 × 792 pt;
- canonical-overflow proof: 11 Letter pages, 612 × 792 pt;
- not encrypted;
- no AcroForm/XFA;
- zero annotations in the inspected representatives;
- text remains extractable; proofs are not scan-only PDFs.

The affected Current Snapshot continuation pages were visually inspected after the final carried-state repair. The second companion and carried inventory state fit the approved layouts without material clipping, overlap, broken glyphs or reading-order failure. The final spent-slot correction restores outline/blank writable markers and does not alter the approved page geometry.

## Deliberately unresolved D-0074 gates

This checkpoint closes Classic production, not the complete PDF-export product.

Remaining known product gates are:

1. application-owned optional appended Spellbook;
2. platform portrait-byte handoff plus Crop-to-fill / Fit-entire-image rendering;
3. owner-facing `APP_MODIFIED_SHEET` visual implementation;
4. originating-section continuation cues on frozen base sheets;
5. Save/Share integration, beginning with the Desktop DM workflow and then approved surface parity.

## Conclusion

**CLASSIC INTEGRATED PRODUCTION AUDIT: PASS**

Classic Run-2 base + all six matching continuation roles are now real-plan-driven production output for the audited renderer scope, with Current Snapshot semantics preserved and no known silent canonical-data loss in the tested domain.

Next implementation gate:

> Implement the separate application-owned optional Spellbook without reopening any frozen base/Extended visual family.

PR #85 remains **OPEN / DRAFT / DO NOT MERGE**.

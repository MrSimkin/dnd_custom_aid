# Checkpoint — Cross-family PC Sheet runtime repair ACTIVE

**Date:** 2026-09-25 (Chile local time)  
**Branch:** `fix/pc-sheet-cross-family-runtime-repair`  
**Branch base:** main `3453b2dac88644fd26ccb8b1c6ce47c634d99cef` (PR #102 cross-family QA handoff)  
**Status:** IMPLEMENTATION ACTIVE / LATER MANUAL QA PAUSED

## Why this branch exists

The Aldren/Permanente Android survey on `0.5.0-preqa.3` proved Save/open across all four PDF families but exposed shared defects in Unicode ingress, semantic continuation routing, inventory/currency handling and page packing, plus family-specific resource and Custom-v2 equipment typography defects.

The complete evidence and acceptance requirements remain in:

`docs/checkpoints/2026-09-24_PC_SHEET_ALDREN_CROSS_FAMILY_PDF_REVIEW.md`

This checkpoint records implementation progress only; it does not supersede that evidence.

## Implemented so far

1. **QA UTF-8 ingress hardening**
   - `database/qa/seed_pc_sheet_runtime_characters.sql` now pins psql client encoding to UTF-8 before any `\\copy`;
   - the transaction verifies exact Unicode sentinels (`Común`, `Élfico`, `versátil`, `2–5`, `—`) before commit and rolls back if corrupted;
   - hosted wire JSON has an accented-Spanish/punctuation round-trip regression.
   - commits: `b7a56a9cb3287973b624e6afbd6f28c76b289291`, `ddfc55a9b8b1dfd4212f69f96e792b040638cfd7`.

2. **Content-aware spell base-page planning**
   - non-spellcasters no longer plan an empty spell base page merely because the visual family contains a spell template;
   - spellcaster/spell/slot/source content still retains the spell page;
   - Fantasy now honors the planner decision instead of drawing its spell page unconditionally.
   - commits: `1a6f2b21cbd5fc9015980c3a18c362649e883a79`, `cd0f905fe8290e2da39e7b7c3cf80ad47fccf105`, `38d4d5d60b6a3dc40de8cb5e289061d8adb9a7ed`, `0ded9e440a420dd4f2c69c1a444c40ab7c7460ee`.

## Confirmed root defects still being repaired

- Custom v1 and Custom v2 explicitly inject combat/action entries and structured damage into their Traits supplement channel;
- Fantasy similarly mixes combat/reference detail into Traits continuation;
- base-represented inventory items are re-emitted on continuation pages merely because they carry detail/status metadata;
- currency/treasure continuation must remain semantically distinct from equipment;
- Custom-v2 one-use resource squares and Fantasy numeric one-use rows remain incorrect;
- Custom-v2 base special-equipment Location fill still uses the wrong visual path;
- Notes/content packing still needs repair after semantic destinations are separated.

## Current engineering order

1. complete semantic routing for combat/actions/damage;
2. make inventory continuation true-overflow/detail-only and separate currency/treasure;
3. repair one-use resource semantics;
4. repair Custom-v2 Location typography;
5. finish content-aware packing/Notes behavior without clipping or losing detail;
6. expand cross-family Aldren regressions;
7. synchronize generated Android renderer sources and require Scaffold PASS;
8. produce a new distinguishable QA APK;
9. rerun Aldren in all four families.

Do not resume Share, Ilyra, Mara, Current Snapshot or final physical-device QA before the repaired Aldren cross-family rerun passes.

Fresh sessions resume through:

`RESUME.md -> docs/checkpoints/LATEST.md -> this checkpoint`.

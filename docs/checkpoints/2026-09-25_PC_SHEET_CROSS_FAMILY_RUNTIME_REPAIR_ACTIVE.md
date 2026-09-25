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


## Owner clarifications consolidated 2026-09-25

These rules are authoritative for the active repair and supersede any temporary branch implementation that conflicts with them.

1. **Currency stays a currency domain**
   - standard/default coins and custom currencies belong together in the sheet's currency/treasure area;
   - custom currency must not be routed as ordinary equipment;
   - use the model's `isDefault` plus semantic standard-currency mapping rather than localized key guesses;
   - a format should use its native currency/custom-currency capacity first and create continuation only for true overflow that the native currency area cannot represent.

2. **Unicode repair has two layers**
   - keep UTF-8 ingress hardening so newly imported QA data is not corrupted;
   - PDF export must also conservatively repair recognizable UTF-8-as-legacy-codepage mojibake already present in stored text (for example `ComÃºn -> Común`, `2â€“5 -> 2–5`);
   - repair is export-time/read-only and must not mutate stored character data or reinterpret clean text.

3. **Normal Equipment is intentionally compact**
   - the normal Equipment line represents inventory identity, quantity and weight;
   - normal-item location, description and notes do not belong on the ordinary Equipment line merely because they exist;
   - those details may be preserved in a semantically appropriate detail/notes continuation when warranted, without replaying the item as a duplicate inventory entry;
   - expected normal case is one line per ordinary item, with a second physical line only for exceptional name/quantity/weight overflow;
   - special equipment keeps its dedicated location/state semantics.

Repository precedent supports these rules:
- historical Custom-v2 repair `1a17fa56e41d07ca1bdd62bdd0f06f41ea541b66` explicitly extended only currencies absent from native v2 treasure;
- historical Custom-v1 repair `5d09271231dd395e44f0c4c2a33cdb39509cc6b5` preserved custom-currency units rather than mislabelling them as GP value;
- the original promoted Custom-v1 base renderer rendered ordinary Equipment as quantity + name before later regression-repair work added location/weight metadata.


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


3. **Read-only export-time Unicode recovery**
   - the export planner now normalizes only recognizable reversible UTF-8-as-Windows-1252/Latin-1 mojibake in the selected PDF projection;
   - clean Unicode remains unchanged and persisted character data is never rewritten;
   - regressions cover accented Spanish and en/em-dash corruption.

4. **Native currency placement restored from verified source geometry**
   - the frozen Custom-v1 page-2 source render was inspected directly: after the five standard coin rows, `Monedas` contains **two blank native custom-currency rows**; these now receive the first two true custom currencies;
   - the frozen Custom-v2 page-1 source render was inspected directly: four standard `TESORO` rows sit beside four `OTROS` rows; Electrum/non-native/custom currencies use `OTROS` before any continuation;
   - `Gemas / Joyas / Arte` is no longer treated as Custom-v1 custom-currency capacity;
   - semantic standard-currency identity wins over imperfect legacy `isDefault` flags, preventing standard coins from being duplicated as custom overflow.

5. **Ordinary Equipment semantics centralized**
   - normal inventory identity is quantity + name + weight only across Fantasy, Custom v1 and Custom v2;
   - location/description/notes no longer inflate the ordinary Equipment row;
   - a bare location by itself does not allocate a detail/Notes page;
   - when richer ordinary-item detail is worth preserving, location may accompany it on a detail/Notes surface;
   - represented special equipment keeps its dedicated semantics instead of being replayed solely for quantity/weight metadata.

6. **Notes/page packing repaired in the first compatible surfaces**
   - Fantasy campaign notes reuse an already-needed inventory/detail continuation before a standalone Notes page is allocated;
   - Custom-v1 page 3 owns normal campaign notes;
   - Custom-v1 page 5 is now conditional and contains only campaign overflow plus warranted ordinary-equipment detail;
   - further Custom-v1 Notes pages are true overflow only;
   - Custom-v2 standalone Notes packing remains to be decided from actual spare `DETALLES / NOTAS` capacity rather than suppressed heuristically.

7. **Regression contract migration in progress**
   - tests are being changed from obsolete exact physical page counts to semantic layer/content/data-preservation assertions where pagination is intentionally content-aware;
   - Android generated renderers are synchronized after Desktop authority changes;
   - latest Scaffold validation is still pending; no repaired QA APK has been promoted yet.

## Remaining repair/validation focus

- prove the repaired semantic routing and one-use resource presentation with a green Scaffold run and fresh Aldren runtime PDFs;
- finish content-aware Custom-v2 Notes packing only where existing compatible `DETALLES / NOTAS` capacity is demonstrably available;
- revalidate Custom-v2 special-equipment Location typography in the fresh runtime output;
- audit residual page amplification after the spell, inventory, currency and Notes repairs rather than targeting an arbitrary page count;
- keep bounded-overflow/data-preservation guards active while obsolete fixed-page-count tests are migrated.

## Current engineering order

1. complete semantic routing for combat/actions/damage;
2. restore native standard/custom currency placement and remove the temporary Custom-v2 currency-as-equipment/treasure-continuation experiment;
3. normalize ordinary Equipment to quantity + name + weight, with detail-only continuation for location/description/notes;
4. add conservative export-time mojibake repair while retaining UTF-8 ingress hardening;
5. repair/retain one-use resource semantics and Custom-v2 Location typography;
6. finish content-aware packing/Notes behavior without clipping or losing detail;
7. expand cross-family Aldren regressions and update assertions whose page count changed intentionally;
8. synchronize generated Android renderer sources and require Scaffold PASS;
9. produce a new distinguishable QA APK;
10. rerun Aldren in all four families.

Do not resume Share, Ilyra, Mara, Current Snapshot or final physical-device QA before the repaired Aldren cross-family rerun passes.

Fresh sessions resume through:

`RESUME.md -> docs/checkpoints/LATEST.md -> this checkpoint`.

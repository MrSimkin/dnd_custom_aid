# Checkpoint — Cross-family PC Sheet runtime repair ACTIVE

**Date:** 2026-09-25 (Chile local time)  
**Branch:** `fix/pc-sheet-cross-family-runtime-repair`  
**Branch base:** main `3453b2dac88644fd26ccb8b1c6ce47c634d99cef` (PR #102 cross-family QA handoff)  
**Status:** PREQA.4 OWNER QA FAILED / POST-QA REPAIR IMPLEMENTED / CURRENT-HEAD CI PENDING

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
   - ordinary location/description/notes are not auto-promoted into Equipment or Notes merely for preservation; if a family has a genuine dedicated detail surface they may appear there only when semantically warranted and without replaying the item as a second inventory entry;
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
   - Custom-v2 keeps the standalone Notes page when it is the guaranteed destination for genuine campaign/ordinary-equipment detail; the existing Traits `DETALLES / NOTAS` area is not treated as free capacity unless its actual occupancy is known.

7. **Preqa.4 became the owner-observed failure baseline**
   - Scaffold `36163990848` was mechanically green at `17d93d32561a6cc46d228b2bd6b0aa8dca466ea8`;
   - Android `0.5.0-preqa.4` / `50400` generated/opened all four Aldren PDFs, but owner manual QA **FAILED**;
   - the authoritative defect list and acceptance gate are now `docs/checkpoints/2026-09-25_PC_SHEET_ALDREN_PREQA4_CROSS_FAMILY_REVIEW.md`;
   - therefore the old “manual QA ready” state is historical evidence only.

8. **Post-preqa.4 semantic/packing repair**
   - Fantasy:
     - race identity is no longer treated as a racial-trait entry;
     - proficiencies/background/reference metadata are removed from Traits continuation and routed to References/Notes;
     - action/resource-backed traits do not replay their detailed semantics in Traits;
     - Traits continuation uses measured physical capacity instead of the old 3-left/2-right artificial page limits;
     - special-equipment continuation no longer repeats quantity/weight already visible on base Equipment and packs four Aldren special items in the native block;
     - one-use resources use row-aligned paper markers: outline = available, filled = spent;
     - Equipment detail is no longer labelled as treasure.
   - Custom v1:
     - body-location-aware special-equipment slot mapping replaces sequential wrong-row placement;
     - structured Combat/Actions continuation uses name/type, range, bonus, effect/damage and notes columns;
     - structured recovery suppresses duplicate legacy recovery and redundant `A máximo` for one-use resources;
     - raw Dexterity is no longer presented as an AC decomposition when the model does not store armor/shield decomposition;
     - resource/action-backed traits do not replay full details in Traits;
     - Details + Notes are paginated as one right-hand continuation stream rather than independent sparse streams;
     - background/faith/subclass references use page-3 narrative Notes/overflow, not Traits;
     - ammunition/status metadata cannot create an Equipment continuation;
     - ordinary item metadata is not promoted into dedicated Notes.
   - Custom v2:
     - normal Equipment remains on the native Equipment/Narrative page; page-1 `OBJETOS` is no longer populated from ordinary inventory;
     - ordinary item metadata is excluded from Notes;
     - one-use resources render explicit `current / 1` rather than `Disponible/Gastado`;
     - structured Combat/Actions continuation replaces prose-only rows;
     - special-equipment location placement/typography and extended location spellings are corrected;
     - species identity and action/resource-backed traits are excluded from detailed Traits replay;
     - personality/flaws/faith/subclass reference facts use the existing Notes page rather than Traits.
   - Cross-family fixture regression now requires `Virotes` to appear once in each Custom family and forbids `Estado: Munición` as a continuation-only artifact.
   - Android generated renderers are synchronized after every Desktop authority change.

9. **Known bounded residual**
   - preqa.4 showed the v2 source sheet's dedicated `MUNICIONES` tracker unused;
   - this repair removes the harmful consequence (Virotes/ammunition metadata no longer allocates a continuation page), but does **not** invent unverified native ammunition coordinates/semantics;
   - source-template PDF coordinate inspection was not available through the required PDF screenshot path in this environment, so direct `MUNICIONES` population remains a bounded visual follow-up if the next owner rerun still requires it.

## Remaining repair/validation focus

- obtain a full green Scaffold on the current post-preqa.4 implementation head;
- only after green, advance the distinguishable Android QA build to `0.5.0-preqa.5` / versionCode `50500`;
- rerun Aldren/Permanente across Fantasy, Custom v1, Custom v2 per Attribute and Custom v2 per Ability;
- judge packing by semantic usefulness and absence of sparse/duplicate pages rather than an arbitrary page-count target;
- do not resume Share, Ilyra, Mara or Current Snapshot until that four-family Aldren rerun passes.

## Current engineering order

**Current gate:** repository implementation/test validation of the post-preqa.4 repair. The owner is not being asked to rerun Android yet.

**After a green current-head Scaffold:** publish the distinguishable preqa.5 candidate, record its exact commit/run/artifact, then perform the bounded Aldren four-family rerun.

Do not resume Share, Ilyra, Mara, Current Snapshot or final physical-device QA before the repaired Aldren cross-family rerun passes.

Fresh sessions resume through:

`RESUME.md -> docs/checkpoints/LATEST.md -> this checkpoint`.

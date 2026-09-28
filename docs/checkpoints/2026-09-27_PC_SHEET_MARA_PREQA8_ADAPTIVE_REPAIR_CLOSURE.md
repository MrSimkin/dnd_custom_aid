# Checkpoint — Mara preqa.8 adaptive continuation repair closure

**Date:** 2026-09-27 (Chile local time)  
**Fixture:** `qa/pc-sheet/fixtures/03_mara_siete_umbrales_custom_extended.json`  
**Repair branch:** `repair/pc-sheet-adaptive-continuations-cross-family`  
**Validated implementation head:** `ce695c40782e1847ed70a2256885b5b07113feae`  
**Final regression run:** Push Scaffold #4088 / `36354213574` — **SUCCESS**  
**Populated-proof artifact:** `10942589698` (`pc-sheet-populated-template-proofs`)  
**Status:** IMPLEMENTATION + CI + WORKER VISUAL PREFLIGHT PASS / OWNER RE-QA PENDING

## Repair invariant

Continuation pages are content-adaptive across every PDF family:

- surviving streams reclaim physical space from exhausted sibling sections;
- empty scaffolds are not repeated merely because they existed on the first continuation page;
- semantic records stay together when the complete record fits in the next physical region;
- columns/rows are consumed before a new page is created;
- page count is driven by actual remaining content rather than fixed template repetition.

## Current Mara stress counts

The final cross-family fixture reports:

- Fantasy Sheet: **29 pages**;
- Custom v1: **18 pages**;
- Custom v2 · Atributo: **16 pages**;
- Custom v2 · Habilidad: **15 pages**.

These are regression ceilings for this fixture, not arbitrary product page-count targets.

## Owner-observation closure

The eight defects recorded in
`2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`
now have bounded repair evidence:

1. **ordinary Equipment continuation** — grouped/readable continuation flow; item identity is not separated from its own continuation merely by a raw row cut when the record fits the next physical region;
2. **Notes records run together** — explicit spacing/boundaries and record-aware pagination are present; available columns are consumed before another page;
3. **Custom Statistics blocks corrupted** — source-derived card/ornament chrome is restored; no generic replacement statistic shell is used; Éter remains unambiguous;
4. **Combat / Actions header collision** — heading band and first row are separated and the row cadence is stable;
5. **Extended pages before the old page 18** — affected Stats, Traits, Combat, Resources, Inventory and Notes continuations were re-rendered and visually inspected during this repair package;
6. **ordinary Equipment text badly formatted** — continuation uses grouped flow and record-aware packing rather than fragmented identity/status/detail streams;
7. **Special Equipment overlap/artifacts** — custom-location handling and redundant scaffold behavior were repaired;
8. **Notes continuation/order** — record-aware multi-column/full-page continuation removes the detached one-column tail behavior.

## Independent-finding closure

### Source-template underlay

Custom Statistics regression now checks that stale source-template labels such as
`EQUIPO ESPECIAL`, `VÍNCULOS`, `IDEALES`, `HISTORIA`, and
`PUNTOS DE VIDA` are absent from the generated Custom Statistics page text layer.

### Custom Statistics card corruption and phantom cards

Only real attribute slices render. Empty sibling card shells are not painted as missing/phantom statistics.

The Custom-v2 source ornament is copied from the original sheet. Per-Ability rows use distinct source-derived light/dark variants rather than one white plate for every row.

### Traits fixed-scaffold waste

After the overview sections are exhausted, continuation pages use the surviving Details/Notes flow rather than repeating the full multi-panel scaffold.

Trait text is record-aware across overview/continuation segments. A trait that fits in the next continuation column is moved there as a complete record instead of being split merely because the previous column reached a raw line limit.

### Resources / Options

Once a sibling stream is exhausted, the surviving flow can use the reclaimed continuation area rather than repeating an empty table.

### Inventory

Continuation flow preserves record association and exhausted sibling structures do not force redundant pages. Fantasy additionally verifies that each full inventory identity appears on one physical PDF page when that identity fits a page.

### Notes

Notes/References packing is record-aware. In Fantasy, overflowing References can replace the unused map scaffold and can reclaim lower-left space only when at least one complete reference record fits.

## Custom-v2 source-ornament correction

The original Custom-v2 source sheet contains distinct light and dark attribute-ornament surroundings.

The final renderer uses measured source crops:

- light-row source tone around the ornament: RGB **227 / 227 / 227**;
- dark-row source tone around the ornament: RGB **200 / 199 / 199**.

An intermediate attempt incorrectly reused the Extended page's 96-pt row cadence as the source-template crop cadence. That crop copied the source label `Constitución` into dark ornaments. The intermediate result passed CI but was rejected during Worker visual preflight.

The final dark source crop uses the measured original-sheet Y position instead. The rendered Mara V2-Habilidad page was visually re-inspected and no source label leaks into the ornament.

The runtime QA fixture now samples the four Mara per-Ability ornament rows at 72 DPI and locks the alternating source tones, so the white-on-gray regression fails automatically.

## Automated regression

The real Mara cross-family fixture now guards:

- semantic identities for Traits, Resources, class Options, Inventory, Notes, custom attributes, and custom markers;
- stale source-template text leakage on Custom Statistics;
- page ceilings **29 / 18 / 16 / 15**;
- Fantasy inventory identity on a single physical page;
- Custom-v2 per-Ability source-derived light/dark ornament tones.

Final run #4088:

- backend: PASS;
- hosted database: PASS;
- Kotlin build/test: PASS;
- Android renderer sync guard: PASS;
- Android PDF delivery guard: PASS;
- populated-proof upload: PASS.

## Ordering contract note

The preqa.8 QA item about Traits ordering was recorded as an **ordering inconsistency / contract question**, not confirmed semantic loss.

This repair package preserves content and record boundaries but does not silently redefine the visual reading contract from category-grouped ordering to global fixture ordering.

Therefore this one item remains an owner-facing contract note unless/until a specific ordering rule is chosen. It is not being misreported as a repaired data-loss defect.

## Owner re-QA execution procedure

The next manual boundary is a bounded owner re-QA of the repaired Mara path. Do not restart defect discovery, extend the repair or promote/merge the branch before this disposition.

### Candidate identity and installation

- application/version identity: `dnd-custom-aid-0.5.0-preqa.8-build-50800.apk`;
- repaired implementation authority: `repair/pc-sheet-adaptive-continuations-cross-family` at validated head `ce695c40782e1847ed70a2256885b5b07113feae`;
- install through the owner's normal emulator/device interaction (for example drag/drop), **not** through `adb install`;
- if an earlier QA build is already installed, install over it; do not uninstall merely for this re-QA because local QA state should be preserved when possible.

### Step 1 — Mara 3A

1. Open **Mara de los Siete Umbrales**.
2. Export **Custom v2 · Atributo**.
3. Use **Permanente**.
4. Use **Extended** custom-stat presentation.
5. Keep Spellbook **OFF/as-is**.
6. Choose **Guardar PDF** and open/read the generated PDF.
7. Retrieve the complete Android `Download` directory to the owner review folder; do not select or filter files:

```powershell
New-Item -ItemType Directory -Force -Path "D:\DnD_Aid\PDF Check" | Out-Null
adb pull "/sdcard/Download/." "D:\DnD_Aid\PDF Check\"
```

8. Inspect the generated Mara PDF, with emphasis on the repaired failure surface:
   - ordinary Equipment continuation remains grouped/readable and item identity is not detached from its continuation when it can stay together;
   - Notes records have visible boundaries, do not run together, use available columns/space before creating another page, and do not leave a detached tail;
   - Custom Statistics cards/ornaments are intact, only real attribute slices render, Éter remains unambiguous, and stale source-template labels do not bleed through;
   - Combat / Actions heading does not collide with the first row;
   - Traits continuation packing becomes adaptive after sibling/overview sections are exhausted instead of repeating empty scaffolds;
   - Resources / Options reclaim unused sibling space rather than forcing empty repeated structures;
   - ordinary and Special Equipment do not overlap or show stale/custom-location artifacts.
9. Record **PASS** or the exact first observed defect with page/section evidence. Do not advance on a material defect.

### Step 2 — Mara 3B, only after 3A PASS

1. Reopen/use **Mara de los Siete Umbrales**.
2. Export **Custom v2 · Habilidad**.
3. Use **Permanente** and **Extended**.
4. Keep Spellbook **OFF/as-is**.
5. Choose **Guardar PDF**, open/read it, then repeat the same bulk `adb pull` of the complete Android `Download` directory.
6. Repeat the 3A continuation/layout inspection and additionally verify that the Custom-v2 per-Ability score/modifier ornaments retain the intended alternating source-derived light/dark surroundings and that no source label such as `Constitución` leaks into an ornament.
7. Record **PASS** or the exact observed defect.

Only after both Mara 3A and 3B owner re-QA are accepted may the staged runtime sequence move forward. Current Snapshot and later blocked work remain out of scope until that disposition is recorded.

## Gate

This checkpoint does **not** claim owner approval.

Current state:

**REPAIR IMPLEMENTED + FINAL REGRESSION GREEN + WORKER VISUAL PREFLIGHT PASS / OWNER RE-QA PENDING**

Any future owner approval must refer to the exact candidate head/artifact reviewed.

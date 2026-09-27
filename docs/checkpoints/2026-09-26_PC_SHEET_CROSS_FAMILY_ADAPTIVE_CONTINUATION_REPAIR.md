# Checkpoint — Cross-family adaptive continuation repair

**Date:** 2026-09-26 (Chile local time)  
**Base main:** `a468c7e81e352657f29f3f084ad8511d7ca82845`  
**Active branch:** `repair/pc-sheet-adaptive-continuations-cross-family`  
**Status:** CROSS-FAMILY VISUAL/PAGINATION ARCHITECTURE REPAIR ACTIVE

## Why this package exists

Mara preqa.8 proved that the remaining PDF problem is not a sequence of isolated Mara/Custom-v2 bugs.

The central defect is architectural:

- continuation pages are too template/scaffold-centric;
- a single overflowing content family can generate many mostly-empty pages;
- surviving content cannot reclaim space from exhausted sibling panels;
- page count and readability are therefore both worse than necessary.

This requirement applies to **all PDF families**:

- Fantasy Sheet;
- Custom v1;
- Custom v2 · Atributo;
- Custom v2 · Habilidad.

Mara is the canonical high-volume stress fixture; the owner is not required to manually reproduce the same architecture failure in every family.

## Owner-loop policy

Do **not** use the owner as the primary PDF defect detector for this package.

Before any new owner APK is requested:

1. generate Mara PDFs in CI for all four families;
2. download and inspect the generated artifacts internally;
3. run automated structural/text/layout invariants;
4. require semantic completeness from the fixture;
5. require adaptive packing / no empty-scaffold regressions;
6. require known Mara visual defects to be closed in automated or inspectable artifacts;
7. only then ask the owner for one final real-app acceptance pass.

Owner QA after this package should be confirmation, not iterative discovery.

## Cross-family continuation invariants

All export families must obey:

1. continuation space is allocated according to **remaining content**, not fixed page templates;
2. exhausted sibling sections surrender their physical space to surviving sections;
3. available columns/rows are consumed before a new page is created;
4. no empty repeated scaffold is rendered solely because another section still overflows;
5. semantic record order and boundaries remain explicit;
6. cross-page records identify continuation instead of starting with an anonymous tail;
7. readable typography floors remain fail-closed;
8. page count is driven by actual content height/rows;
9. no semantic content may disappear during repacking.

Family-specific visual grammar remains allowed; the packing invariants are shared.

## Mara visual defects that must close before owner rerun

The canonical source remains:

`docs/checkpoints/2026-09-26_PC_SHEET_MARA_PREQA8_PDF_VISUAL_QA_DEFECTS.md`

Required closure includes:

- custom-attribute **card/box graphical artifacts**, not only Éter text;
- phantom custom-stat cards;
- Traits continuation page waste;
- Combat header/first-row collision and row cadence;
- Resources/Options empty-panel repetition;
- Inventory grouping/association and custom-location artifacts;
- redundant empty Equipment/Special pages;
- Notes record separation and continuation ordering;
- stale source-template/underlay text where applicable.

## Validation strategy

### Automated semantic gates

Using the real Mara fixture, require every family to preserve all applicable source semantics, including counts/identities for:

- custom attributes/skills;
- traits;
- resources;
- custom markers;
- class options;
- combat entries;
- inventory;
- note cards.

### Automated structural/packing gates

Add family-appropriate tests for:

- no phantom empty custom-stat panels;
- no pages whose dominant continuation panel is empty while another panel overflows;
- no repeated empty sibling scaffold after that sibling stream is exhausted;
- no header/data-row overlap;
- note-card boundary markers preserved;
- continuation page count bounded by actual row capacity rather than fixed sibling templates.

### Artifact review

CI must publish the generated Mara PDFs/renders for all four families. Inspect those artifacts internally before owner handoff.

## Current implementation direction

Do not attempt one renderer implementation shared verbatim across all families.

Current code confirms:

- Fantasy Sheet is programmatic and does not depend on imported template forms for continuation pages;
- Custom v1 continuation pages still reuse/import source-template forms and mask/relabel them;
- Custom v2 mixes programmatic Extended structure with limited imported source resources/forms.

Therefore:

- share **packing policy/invariants** where practical;
- repair renderer implementation per family;
- do not preserve a fixed scaffold merely to match historical continuation pages when it produces unusable PDFs;
- frozen base-page visual authorities remain frozen unless a concrete base-page defect is part of this package.

## Implementation progress — cross-family automated repair

### CI stress-proof harness

`DesktopPcSheetRuntimeQaFixtureTest.maraCrossFamilyStressProofsAreEmittedForInternalReview` now attempts to render Mara through all four families and writes the resulting PDFs plus a page-count report into the existing `pc-sheet-populated-template-proofs` artifact channel.

The first diagnostic run, Scaffold **#3942**, failed before artifact upload on **Custom v1**, with:

`Custom-v1 source-matched label requires excessive compression: 'Lectura de presagios' (49.175125%)`

This independently confirmed the owner's cross-family deduction without requiring another manual export.

Custom v1 now wraps long skill names into multiple physical rows at the approved **60% source-matched scale** instead of allowing ~49% compression. Only the first physical row retains the logical skill's total/proficiency marker. Partially filled Custom-v1 custom-stat pages also stop painting unused module shells.

### Custom v2 repairs already implemented in Desktop + Android

The active branch now includes:

1. **Custom Statistics**
   - suppress phantom attribute/module shells on partially filled pages;
   - suppress unused standard-skill and note structures;
   - preserve real card/marker structure only for actual projected content.

2. **Combat**
   - separate header band from first data row;
   - tighten physical row cadence from the wasteful historical layout while preserving multi-row readable wrapping;
   - retain fail-closed readability floors.

3. **Traits / Features**
   - preserve one familiar overview/orientation page;
   - subsequent overflow uses dedicated two-column continuation-only pages;
   - exhausted category/index panels no longer repeat for every detail page;
   - remaining proficiency overflow is routed into continuation text instead of forcing empty sibling panels.

4. **Resources / Options**
   - while both streams remain, use the mixed overview page;
   - once one stream is exhausted, the surviving stream gets a full-page continuation table;
   - no repeated empty Options or Resources half-page solely because the sibling still overflows.

5. **Notes**
   - base Notes flow is record-aware rather than a single flattened string;
   - note records receive explicit visual separation;
   - records move intact to the next column when possible;
   - genuine cross-column records receive a `(continuación)` identity;
   - continuation Notes pages are programmatic rather than imported source-form underlays, removing stale searchable/visible template content from that surface.

6. **Inventory**
   - Extended continuation is now record-based and two-column adaptive;
   - ordinary equipment, treasure and special equipment share available page space dynamically;
   - logical item identity/status/detail remains grouped;
   - long records move intact to the next column when possible;
   - cross-column records identify continuation;
   - custom/noncanonical locations render as item data rather than overprinting fixed body-location labels;
   - empty Special Equipment scaffolds are no longer repeated simply because ordinary Equipment continues.

### Semantic regression correction

An intermediate experiment routed every ordinary-equipment description into Notes. Existing tests correctly rejected that because compact ordinary equipment metadata—especially ammunition metadata—must not be replayed elsewhere merely because it exists.

That broad routing was reverted. The adaptive repair keeps the existing semantic contract: ordinary Equipment identity remains compact and metadata does not create duplicated PDF representations by itself.

### Current validation boundary

The latest combined code includes the Custom-v2 adaptive work above plus the Custom-v1 readable skill-row repair. Scaffold validation is active on the exact combined branch head. No owner artifact should be requested until:

- the full Kotlin/rendering suite is green;
- the four-family Mara stress PDFs are successfully uploaded;
- those artifacts are internally inspected;
- any remaining family-specific packing defects are repaired;
- a versioned candidate passes PR-head and merged-main validation.

## Validation discoveries after Scaffold #3974

Scaffold **#3974** reached the cross-family Mara semantic gate and exposed two real regressions:

1. **Fantasy long inventory identity truncation**
   - Full Mara identity `Frasco de tinta que recuerda la última palabra escrita 2` was not recoverable from the PDF.
   - The item existed, but both base and continuation surfaces truncated the same long name.
   - The Fantasy continuation now wraps long ordinary-item names across physical rows, keeping quantity/weight only on the first row and preserving the complete semantic identity.

2. **Custom-v2 Notes base/continuation handoff mismatch**
   - Existing regression lost `Nota de continuación 45`.
   - Root cause: the base Notes page and Extended Notes continuation used slightly different physical wrap widths, then Extended dropped a fixed first 40 flow rows.
   - Because wrapping differed, the logical handoff could skip content at the boundary.
   - Base and continuation now use the same 280.5-pt flow width so the 40-row handoff is exact.

These failures are retained as regression evidence. The tests are not weakened.

## Cross-family implementation expansion — Fantasy + Custom v1

### Fantasy Sheet

Desktop and generated Android Fantasy renderers now also obey adaptive continuation rules:

1. **Traits**
   - left/right category streams share their original two-column layout while both remain;
   - once one stream is exhausted, the surviving stream reclaims **both columns** before another page is created;
   - long ordinary inventory identities are wrapped across continuation rows rather than being truncated on both base and continuation surfaces.

2. **Resources / Options**
   - mixed layout is retained only while both streams remain;
   - Resources-only continuation pages use the full page;
   - Options-only continuation pages use the full page.

3. **Inventory**
   - mixed Inventory/Special/Treasure layout is used only while multiple streams remain;
   - ordinary Equipment-only overflow receives a dense full-page table;
   - Special-only overflow receives a dense full-page list;
   - Treasure-only overflow receives a dense full-page ruled area;
   - when ordinary Equipment is exhausted but Special + Treasure both remain, those two streams reclaim tall left/right columns.

4. **Notes / References**
   - mixed Notes + Map/References layout is retained while both content streams remain;
   - Notes-only overflow reclaims the full page instead of repeating an empty map/reference side;
   - References-only overflow reclaims the full page.

The Android Fantasy renderer is regenerated from the Desktop authority through the canonical generator contract, rather than maintained manually.

### Custom v1

Desktop Custom-v1 authority and generated Android counterpart now include:

1. **Custom Statistics stress repair**
   - long source-matched skill labels wrap at the approved source scale rather than requiring ~49% compression;
   - partially populated statistics pages do not paint unused module shells.

2. **Traits**
   - one native source-led overview page remains;
   - later overflow is converted to dense two-column programmatic continuation pages;
   - exhausted category panels no longer force repeated source-template scaffolds.

3. **Resources / Options**
   - mixed source-led layout remains while both streams have data;
   - Resources-only and Options-only overflow each reclaim a full programmatic page.

4. **Inventory**
   - a native mixed page is used only when at least two continuation streams are active;
   - remaining ordinary Equipment, Special Equipment, and Treasure each move to their own dense programmatic continuation page type;
   - custom special-item locations are rendered as explicit data on those continuation pages rather than relying on fixed body-location rows.

### Current validation gate

The exact combined branch head is under Scaffold **#3994**. The next decision is test-driven:

- if semantic/renderer gates fail, repair the failing invariant without weakening coverage;
- if green, download the four Mara stress PDFs from CI and perform internal artifact review before any owner APK is prepared.

## Validation discovery — Scaffold #4000

Scaffold **#4000** reached Mara's Custom-v1 Inventory continuation and failed on:

`Custom-v1 Extended text does not fit: 2 x Frasco de tinta que recuerda la última palabra escrita 2 · 0.5 lb`

Root cause:

- Custom-v1 adaptive pagination was already active;
- however, `inventoryContinuationLines` still emitted the full compact equipment identity as one physical row;
- the row therefore remained a fail-closed width blocker even though the page architecture itself had been repaired.

Correction:

- the compact logical equipment identity now wraps at the actual Custom-v1 continuation width;
- wrapping stays at the existing readable 8.4 pt continuation typography;
- operational status follows on subsequent rows;
- Desktop and Android are synchronized;
- the semantic-completeness gate remains unchanged.

The next exact-head gate is Scaffold **#4002**.

## Owner boundary

No owner PDF generation or manual comparison is requested while this package is active.

Mara 3B, Current Snapshot and Media / Handouts remain blocked.

The next owner request should occur only after a repository-green candidate has passed cross-family Mara artifact review.

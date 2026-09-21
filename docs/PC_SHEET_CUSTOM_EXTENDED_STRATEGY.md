# PC Sheet PDF — Custom Extended-Page Rendering Strategy

**Status:** CANONICAL / OWNER-APPROVED DIRECTION  
**Established:** 2026-09-20  
**Applies to:** Custom v1 Extended (frozen), Custom v2 Extended Run 7 (frozen), and future production/integration work for both families.  
**Related protocol:** `docs/PC_SHEET_PDF_STRATEGY_RUN_PROTOCOL.md`

## 1. Purpose

This document records the renderer/design method that converged successfully for Custom-v1 Extended pages and the constraints that must carry forward into Custom-v2 Extended work.

The central rule is:

> Extended pages are not generic appendices. Each Custom family must extend the visual grammar already frozen in that family's owner-approved base sheet.

Do not reconstruct these rules from chat memory. This file is the durable authority for Custom Extended-page work.

## 2. Design before implementation

Before changing renderer code for a new Extended page or correction:

1. identify the semantic role of the page;
2. identify which frozen source page(s) contain the closest native grammar;
3. measure the relevant source geometry, row cadence, column proportions, typography and marker language;
4. decide which geometry should be reused authentically and which content should be generated natively;
5. define the layer plan;
6. define what existing pages/regions must remain pixel-identical;
7. only then implement.

Do not start with coordinate nudges or broad cleanup masks.

## 3. Layered composition architecture

The approved architecture is independent semantic layers rather than one flattened drawing pass.

The normal layer order is:

1. **Structure**
   - authentic source geometry and/or native geometry derived from measured source proportions;
   - source fragments should contain only the structural material actually needed;
   - source geometry is authoritative where reused.

2. **Cleanup / Occlusion**
   - optional;
   - only for bounded safe interiors;
   - must not erase borders, rules, ornaments, checkboxes or other structural primitives;
   - if a page can be constructed natively without cleanup, prefer an empty cleanup layer.

3. **Labels**
   - headings, field names and captions;
   - typography follows the frozen family grammar.

4. **Values**
   - scores, modifiers, body text, table content and other variable data;
   - use full-glyph embedded fonts for arbitrary generated text.

5. **Markers / Symbols**
   - checks, double-checks, resource counters, proficiency/expertise marks and similar glyphs;
   - use owner-approved Para Hoja de PJ Symbols v8 when its vocabulary fits the semantic role.

Each layer should be independently renderable for diagnostics.

### Required diagnostics

For a six-role Extended family, keep per-role staged diagnostics whenever practical:

- structure;
- cleanup;
- labels;
- values;
- markers.

This was decisive for distinguishing structural problems from cleanup artifacts, font problems and marker problems during Custom-v1 Runs 3–6.

## 4. Source geometry and proportions

### 4.1 Source family grammar is authoritative

Do not infer new spacing merely because blank page space exists.

Measure and reuse:

- row cadence;
- column widths;
- rule lengths;
- checkbox/marker placement;
- heading footprints;
- vertical density;
- whitespace rhythm;
- relationship between lookup/calculated values and writable player information.

A page must not "hog all available space" when the frozen source family uses a denser paper-sheet rhythm.

### 4.2 Reuse proven structures

If a source family already has a good analogue, extend from it:

- Equipment / Equipo Especial grammar for inventory/options-style rows;
- spell-list grammar for Extended spells;
- notes grammar for Extended notes;
- source Attribute/Ability grammar for Custom Statistics;
- ammunition/slot/resource-counter grammar when representing limited-use resources.

Do not redesign a previously-good structure during an unrelated correction.

### 4.3 Preserve working pages

When owner feedback is bounded:

- keep unaffected pages pixel-identical when possible;
- do not restyle a page that already passed owner/assistant review;
- use differential renders to prove the intended scope of change.

## 5. Typography rules learned from Custom v1

### 5.1 Embedded source subsets are not general-purpose fonts

The embedded owner `GillSansMT` source font is subsetted and unsafe for arbitrary generated text. Missing glyphs produced silent numeric/text omissions.

Therefore:

- use source subset fonts only when the exact required glyphs are known to exist;
- use fully embedded full-glyph fonts for arbitrary generated values/text;
- verify generated fonts are fully embedded in Form/OCG use.

### 5.2 Source-matched generated body/skill labels

For Custom-v1 Ability/skill labels, the measured successful source-match is:

- Fira Sans Regular;
- 10 pt;
- approximately 60% horizontal scale;
- source-aligned X/baseline;
- readability floor enforced before further compression.

This reproduces the compact source label treatment while retaining full Spanish glyph coverage.

### 5.3 Custom-v1 Attribute headings

Custom-v1 Attribute names use the owner/source decorative heading font and the three-letter stat-key naming convention embedded in the name.

Frozen examples include:

- `FUErza`;
- `DEStreza`;
- `CONstitución`;
- `INTeligencia`;
- `SABiduría`;
- `CARisma`.

Approved Custom-v1 custom-stat examples:

- `HONor`;
- `RESolución`;
- `SUErte`.

Do not replace these headings with a generic sans font.

### 5.4 Custom v2 warning

These Custom-v1 typography values are **not permission to copy v1 typography blindly into v2**.

For Custom v2:

- inspect the frozen per-Attribute and per-Ability v2 baselines first;
- derive v2 heading/body/compact-label treatment from v2 source/frozen geometry;
- reuse the *method* (measure -> reproduce -> verify), not necessarily the exact v1 font sizes/scales.

## 6. Cleanup-mask rules

Runs 2–4 demonstrated that broad cleanup masks can create:

- white cuts;
- colored artifacts;
- clipped ornament borders;
- residual antialiasing;
- visually shifted artifacts after coordinate changes.

Therefore:

- never use a large mask as a substitute for understanding the source fragment;
- keep cleanup masks inside safe field interiors;
- preserve a measurable safety gap from authentic borders;
- if residual source text repeatedly survives, redesign the structure/source crop instead of endlessly expanding the mask;
- where possible, crop exact source geometry and rebuild generated text natively.

For native page sections, an empty cleanup layer is preferable to unnecessary masking.

## 7. Symbol and resource grammar

Para Hoja de PJ Symbols v8 is OWNER APPROVED / FROZEN.

Use it when the symbol vocabulary naturally fits the data:

- proficiency / expertise;
- checks and double-checks;
- slot/resource availability;
- similar discrete state markers.

For limited-use resources, favor the family’s existing ammunition/slot/resource-counter grammar rather than inventing large standalone panels.

Recovery cadence belongs to the resource row when that information is semantically part of the resource. Do not create a separate "Recuperación" extension unless the data model genuinely needs a dedicated recovery section.

Likewise, do not create redundant generic panels such as "Estados" merely to fill page space.

## 8. Semantic and terminology contracts

Owner terminology wins over generic contemporary terminology.

Known contract:

- use **Raza**, never `Especie`, in this product's character-sheet UI/PDF language.

Add machine guards for terminology that must not regress.

Where a frozen sheet has an established field label, preserve it unless the owner explicitly changes the product vocabulary. Example:

- `Tirada de Salvación` must not be shortened merely to make a poor layout fit; fix the layout instead.

## 9. Validation gate for every Custom Extended candidate

Green CI is necessary but never sufficient.

Before owner review:

1. generate the exact CI artifact;
2. preflight the PDF:
   - page count;
   - openability;
   - encryption;
   - XFA;
   - expected page dimensions;
3. independently render with at least two PDF renderers when practical;
4. inspect actual PNG renders, not only PDF text extraction;
5. verify frozen base pages pixel-identical;
6. verify unaffected extension pages pixel-identical or explain intended changes;
7. inspect layer diagnostics for changed pages;
8. verify full font embedding / no missing generated glyphs;
9. verify terminology contracts;
10. verify no clipping, colored/white artifacts, overlaps or broken glyphs;
11. only then mark **PASS FOR OWNER REVIEW**;
12. only explicit owner approval may mark **OWNER APPROVED / FROZEN**.

## 10. Custom-v1 Extended frozen baseline

Owner approved on 2026-09-20:

- implementation commit: `69b308f3d5d493d06bd0107ac66c7524935aa9fa`;
- Scaffold: `35529317947` / #2885 — SUCCESS;
- artifact: `10609869599`;
- proof PDF: `custom-v1-complete-family-extended-run6.pdf`;
- proof PDF SHA-256: `03212b642ba9b8414e18344dbe90b6d68d623d14a0cd1ff712544063548eafe5`;
- diagnostics SHA-256: `db6e5d8f1d37f74fb9847fc82ceb4820f1f33cb29303ee3f81f2b7fceb5e5a6b`.

Run 6 is the Custom-v1 Extended visual authority.

Do not recalibrate it without a new owner-observed defect or product requirement.

## 11. Custom-v2 Extended frozen baseline

Owner approved on 2026-09-20/21:

- implementation commit: `f464522ad232f4ed6193e1c28d118e45faad988c`;
- Scaffold push run: `35546791262` / #2946 — SUCCESS;
- artifact: `10617190236`;
- proof PDF: `custom-v2-extended-evaluation-run7.pdf`;
- proof PDF SHA-256: `b765884d62cc69a5451fb02dade16db756f1b58469ba4cdb5f5c16ec56e79440`;
- durable checkpoint: `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V2_EXTENDED_RUN7_OWNER_APPROVED.md`.

Run 7 is the Custom-v2 Extended visual authority.

Do not recalibrate it without a new owner-observed defect, a bounded defect exposed by real production data, or an explicit product requirement.

The next phase is production promotion: move the approved mechanics out of QA/evaluation-only code and into the real `PcSheetPdfRenderPlan` renderer while preserving both Custom-v2 first-page modes and the shared Extended pages.

## 12. Custom-v2 production-promotion carry-forward plan

Custom-v2 base sheets are already OWNER APPROVED / FROZEN:

- per Attribute;
- per Ability;
- shared v2 pages.

Custom-v2 Extended work must now use the same disciplined process while remaining visually native to v2.

### Before first v2 Extended run

For each extension role:

1. inspect both frozen first-page variants and the shared v2 pages;
2. decide whether the extension geometry is common to both variants or whether Custom Statistics needs a variant-specific page;
3. identify the closest v2 source grammar for:
   - Custom Statistics;
   - Traits & Features;
   - Resources & Options;
   - Inventory / Equipment;
   - Spells;
   - Notes;
4. measure v2-specific row cadence, columns, typography, boxes and symbols;
5. define source/cleanup/labels/values/markers layers;
6. define exact frozen-page regression guards.

### Do not blindly inherit from v1

Carry forward:

- layered architecture;
- diagnostic staging;
- source-measurement method;
- preservation rules;
- full-glyph font safety;
- symbol-font usage;
- owner terminology;
- two-renderer visual QA;
- artifact/clip avoidance;
- proportion discipline.

Re-derive for v2:

- exact geometry;
- row cadence;
- heading sizes;
- compact label scaling;
- column widths;
- whether alternating fills are appropriate;
- which source page is the correct structural donor;
- how per-Attribute vs per-Ability presentation affects Custom Statistics.

### First v2 Extended design question

Do not start coding until deciding whether the six Extended roles can share one v2 extension set across both first-page modes or whether at least Custom Statistics requires two variant-specific layouts.

That decision must come from the frozen v2 visual grammar and semantic relationship model, not convenience.

## 13. Historical failure lessons to preserve

- **Run 1:** superficial family motifs are not enough; spacing/capacity/maquetación must match the source family.
- **Run 2:** source-faithful direction was correct, but broad masks and block selection still created artifacts.
- **Run 3:** source-matched typography and authentic fragments improved fidelity, but one flattened composition remained fragile.
- **Run 4:** multiple layers made defects diagnosable, but masks still produced residual artifacts and owner feedback showed terminology/design issues.
- **Run 5:** artifact-free native reconstruction solved mask problems, but over-redesigning proportions and previously-working pages drifted from the sheet grammar.
- **Run 6:** successful convergence came from combining layered architecture with strict restoration of the frozen sheet's proportions, typography, naming and proven page structures.

The durable principle is:

> Use layers to isolate responsibilities, but use the frozen source family—not empty page space—as the design system.

# PC Sheet PDF — Strategy / Run Operating Protocol and Consolidated State

**Canonical status:** ACTIVE  
**Date established:** 2026-09-19  
**Applies to:** PR #85 and subsequent PC-sheet PDF renderer work until superseded by an explicit owner decision.

---

## 1. Purpose

This document is the durable operating method for developing and validating PC-sheet PDF rendering.

The goal is not merely to make a PDF that compiles or looks acceptable once. The goal is to converge on a rendering strategy that:

- preserves the owner's authoritative sheet design;
- prints clearly at real size;
- aligns text/markers reliably despite irregular sheet geometry;
- remains maintainable in Kotlin/JVM/PDFBox;
- works offline and without external-service cost;
- can be debugged section-by-section;
- incorporates every learned result into later runs and, if needed, later strategies.

Chat memory is not the authority for this process. This repository document and the dated run checkpoints are.

---

## 2. General Strategy / Run MO

### 2.1 Strategy first, work second

Before broad implementation, agree on a rendering strategy.

A strategy is an architectural approach, not a single coordinate tweak.

### 2.2 Minimum iteration rule

A strategy receives **at least three meaningful runs** before considering a switch, unless a hard blocker proves it cannot satisfy the requirements.

A failed run does not automatically mean a failed strategy.

### 2.3 Every run must be audited

After every generated draft:

1. inspect the actual rendered PDF/artifacts;
2. compare against owner observations;
3. perform an independent technical/visual audit;
4. record both owner findings and assistant findings;
5. state what should change in the next run;
6. do not silently repair defects and erase the evidence;
7. preserve lessons for later runs and later strategies.

Green CI is never visual approval.

### 2.4 Learning carries forward

Every run must retain:

- successful mechanics;
- successful geometry;
- successful typography;
- semantic decisions;
- discovered failure modes;
- rejected assumptions.

If Strategy N is later abandoned, Strategy N+1 starts with all relevant lessons from Strategy N rather than restarting from zero.

### 2.5 Owner vs assistant roles

Owner:

- defines desired visual/product semantics;
- may add observations after inspecting drafts;
- approves or rejects strategy-level and visual-family gates.

Assistant:

- performs independent post-generation audit;
- must be candid about defects even when the owner reports none;
- proposes bounded next-run changes;
- may recommend a strategy, but does not self-approve owner visual results.

### 2.6 Scope control

Runs should isolate hypotheses.

When possible:

- preserve working sections unchanged;
- change one abstraction/failure class at a time;
- add new coverage only when it does not destroy diagnostic clarity.

After a strategy has demonstrated stability, runs may combine:
- repair of known local defects; and
- expansion to new fields/sections.

### 2.7 Stop / strategy-switch rule

Do not brute-force a strategy known to be structurally unsuitable.

Stop and discuss a new strategy if evidence shows the active approach cannot reasonably provide:

- independent regional alignment;
- acceptable print/vector fidelity;
- reliable font/marker rendering;
- maintainable calibration;
- source-template preservation where required.

---

## 3. Strategy ladder

### Strategy 1 — HYBRID — ACTIVE

Authoritative owner PDF remains the visual source of truth.

Generated content uses:

- native PDF-point geometry;
- PDFBox Form XObjects;
- Optional Content Groups (layers);
- fully embedded generated fonts;
- owner-approved v8 symbol font;
- real font metrics for text/marker fitting;
- explicit optical calibration where appropriate;
- section-specific geometry.

**Current status:** viable and active. No evidence currently justifies abandoning it.

### Strategy 2 — Direct PDFBox page overlay with redesigned layout model — RESERVE

Keep direct page-content-stream rendering but replace pseudo-pixel/generic-box abstractions with rigorous native-point geometry.

Potential advantage: lower implementation complexity.

Reason not currently preferred: Strategy 1 provides superior isolation/debugging through independent Form/OCG layers.

### Strategy 3 — Derived AcroForm / fillable template + flatten — RESERVE

Potentially useful for simple text fields.

Known concern: irregular ruled sections, custom markers, spell grids and optical placement would still require custom appearance streams and geometry work.

### Strategy 4 — SVG / Graphics2D intermediate — RESERVE

Potentially useful for easier visual authoring.

Known costs: another rendering layer, dependency/metric/font-parity risks.

### Strategy 5 — Full template reconstruction / redraw — LAST RESORT

Maximum control but maximum maintenance effort and highest risk of drifting from the authoritative owner PDFs.

Only consider if source-template overlay strategies prove structurally unable to meet fidelity requirements.

---

## 4. Strategy 1 architectural principles learned so far

### 4.1 Authoritative template

The source owner PDF is design authority.

Do not redraw or reinterpret printed geometry merely because a generic layout primitive is convenient.

### 4.2 Native PDF points

New geometry should be stored in native PDF points.

The old whole-draft pseudo-pixel convention is not authoritative for the Hybrid renderer.

### 4.3 Semantic geometry

Geometry must be scoped by semantic identity, not just coordinate shape.

Examples:

- `Main.Defense`
- `Main.Abilities`
- `Main.Skills.Dexterity`
- `Main.Spellcasting.SpentSlotTargets`
- `SpellList.Level1.PreparedChecks`
- `SpellList.Level1.NameRules`

Correct coordinates from one semantic region must not be reusable accidentally in another.

### 4.4 Section / box isolation

**Approved experimental architecture principle after Run 5:**

A page should be treated as a collection of independently calibrated semantic sections rather than one monolithic alignment domain.

Independent Form XObject / OCG layers may be used liberally when they improve calibration and debugging.

Sections may be subdivided further, including one layer per ability/skill column if useful.

This is intentionally analogous to the owner's successful InDesign workflow.

### 4.5 Ruled text

Ruled regions are not generic text boxes.

A ruled-text mapping should define:

- actual source rule Y;
- actual start X;
- actual end X;
- physical clearance above the rule;
- role-specific preferred/minimum font size;
- optional optical padding.

Baseline should derive from real font metrics, especially descent.

### 4.6 Markers

Markers should be fitted to authoritative target rectangles using actual PDF font metrics.

Do not place checks/ovals from guessed centers when the source container can be measured.

The approved symbol font remains:

`assets/fonts/owner/para-hoja-de-pj/v8/Para Hoja de PJ Symbols v8.ttf`

Do not modify the frozen v8 artwork unless the owner explicitly requests artwork changes.

### 4.7 Font embedding

Fonts used inside Form/OCG overlays must be fully embedded.

Run 1 demonstrated that subset-only Form usage can fail.

Current proof convention uses `PDType0Font.load(..., embedSubset=false)` and post-save embedding verification.

### 4.8 Typography direction

Current representative success:

- Fira Sans Regular ~9.0–9.25 pt for ruled/body/table text;
- Fira Sans SemiBold for compact prominent numeric fields;
- Kalam Bold for handwritten name/identification content;
- v8 symbol font for owner markers.

These are successful QA directions, not yet final visual-family owner approval.

---

## 5. Durable export semantics established during runs

### Spell preparation

- Cantrips / `TRUCOS` are always prepared for the owner's sheet semantics.
- Therefore generated cantrip rows do **not** receive preparation marks.

### Spent spell slots

- `ESPACIOS GASTADOS` must remain **empty** in generated sheets.
- Do not export current spent-slot state into those printed ovals.
- Slot-total / `ESPACIOS` values may be populated.

These are semantic requirements, not temporary visual tweaks.

---

## 6. Consolidated run history

### Run 1 — Hybrid mechanism / native geometry / separate layer

**Result:** FAIL, informative.

Succeeded:
- authoritative template preserved;
- separate Form XObject / OCG mechanism worked;
- overlay-only debugging artifact worked;
- native-point positioning concept proved viable.

Failed:
- Fira and v8 Form-only fonts were saved unembedded / Identity-H;
- visual text/markers were corrupted.

Lesson:
- Form-layer font lifecycle/resource handling requires full embedding and explicit verification.

Checkpoint:
`docs/checkpoints/2026-09-19_PC_SHEET_PDF_HYBRID_STRATEGY_RUN1.md`

### Run 2 — Full font embedding

**Result:** technical PASS / partial visual PASS.

Succeeded:
- full embedding fixed corrupted rendering;
- larger Fira Regular ruled text materially improved readability;
- 15 pt `ESPACIOS` value improved clarity.

Failed:
- v8 check and filled oval were misfit despite correct target rectangles.

Lesson:
- target geometry was correct; glyph transform needed real PDF font metrics rather than hard-coded design-space bounds.

Checkpoint:
`docs/checkpoints/2026-09-19_PC_SHEET_PDF_HYBRID_STRATEGY_RUN2.md`

### Run 3 — Metric marker fitting

**Result:** technical PASS / representative visual PASS.

Succeeded:
- check fitted inside authoritative source square;
- filled spell-slot oval near-filled authoritative source oval;
- text and `ESPACIOS` improvements remained stable.

Strategy conclusion:
- Strategy 1 demonstrated viable solutions for the original defect classes and became the recommended active strategy.

Checkpoint:
`docs/checkpoints/2026-09-19_PC_SHEET_PDF_HYBRID_STRATEGY_RUN3.md`

### Run 4 — Generalization / repeated geometry

**Result:** technical PASS / generalization partial PASS.

Succeeded:
- five attack rows generalized without cumulative drift;
- width-based narrative wrapping worked;
- repeated marker placement worked;
- embedding remained robust.

Exposed:
- ambiguous ruled-text baseline semantics caused text too close to/crossed by printed rules;
- correct MAIN-page spell-slot geometry was accidentally reused on SPELL_LIST page.

Lessons:
- ruled text needs explicit metric-based clearance semantics;
- geometry must be semantically scoped by page/field identity.

Checkpoint:
`docs/checkpoints/2026-09-19_PC_SHEET_PDF_HYBRID_STRATEGY_RUN4.md`

### Run 5 — Section-isolated / InDesign-style proof

**Result:** architecture PASS / technical PASS / strong visual partial PASS.

Architecture:
- nine independent Form/OCG section layers across three pages;
- proved independent regional alignment is feasible and maintainable.

Succeeded:
- spell-name Y placement corrected;
- prepared-check X improved;
- `ESPACIOS GASTADOS` kept empty;
- cantrips have no preparation checks;
- handwritten portrait name;
- Defense values;
- all six attribute scores/modifiers;
- first Rasgos row in three columns;
- page-2 Trasfondo/Ideales/Vínculos/Defectos;
- attack regression.

Remaining local defects:
- skill checks too far left because target boxes were estimated rather than measured;
- long skill label/value interaction (`Trato con Animales`) shows the need for per-column actual value-line geometry;
- Identification `Siguiente Nivel` rule was estimated and value overlaps printed label.

Primary conclusion:
- section/layer isolation should become a first-class production design principle.

Checkpoint:
`docs/checkpoints/2026-09-19_PC_SHEET_PDF_HYBRID_STRATEGY_RUN5.md`

---

## 7. Current state before Run 6

Active strategy: **Strategy 1 — Hybrid**

Known-good mechanics to preserve:

- authoritative source template;
- independent section layers;
- full font embedding;
- Fira/Kalam/v8 roles;
- native-point geometry;
- metric-based marker fit;
- metric-based ruled-text baseline;
- 15 pt `ESPACIOS`;
- no spent-slot generation;
- no cantrip preparation marks;
- Run-5 Defense/Attributes/Portrait/Rasgos/Background/Attack results.

Known defects to repair:

1. skill checkbox target geometry;
2. skill value-line geometry for long labels;
3. Identification `Siguiente Nivel` real rule geometry.

Run 6 policy:

- repair the three known defects;
- **also expand to additional fields/sections**;
- keep new additions independently layered;
- audit every new section and all known-good regressions;
- do not merge PR #85 merely because Run 6 passes.

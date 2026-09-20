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

### Run 6 — Measured skill calibration + continued expansion

**Result:** technical PASS / strong experimental visual PASS.

Repairs:
- skill checkboxes now use measured authoritative source glyph rectangles;
- skill numeric values use measured source line segments;
- `Trato con Animales` no longer collides with its value;
- `Siguiente Nivel` uses the true source rule;
- `Puntos de Experiencia` was added on its true source rule.

Expanded coverage:
- Core Stats: Initiative, Proficiency Bonus, Max HP, Current HP, Speed, Hit Dice;
- Spellcasting Summary: Save DC, attack modifier, spellcasting ability;
- Rasgos second row / three columns;
- page-2 Historia sample.

Architecture:
- at least sixteen independent semantic OCG/Form layers;
- Skills subdivided by ability column.

Regression:
- spell semantics remain stable;
- Defense, abilities, portrait name, attacks and background fields remain stable.

Coverage caveat:
- Inspiration is not yet populated/tested in the new Core Stats layer.

Checkpoint:
`docs/checkpoints/2026-09-19_PC_SHEET_PDF_HYBRID_STRATEGY_RUN6.md`

---

## 7. Current state after Run 6

Active strategy: **Strategy 1 — Hybrid**

Current confidence:
- Strategy architecture: strong;
- section/box isolation: proven;
- exact-source geometry workflow: proven for spell checks, skill checks, numeric skill lines and Identification rules;
- font embedding: stable;
- cross-renderer behavior: stable;
- complete Custom-family approval: **not yet reached**.

Known-good mechanics/semantics to preserve:

- authoritative source template;
- independent semantic section layers;
- subdivide difficult sections further when useful;
- full font embedding;
- Fira/Kalam/v8 role direction;
- native PDF-point geometry;
- metric-based marker fit;
- metric-based ruled-text baselines;
- measured source target rectangles/lines instead of guessed centers;
- 15 pt `ESPACIOS` direction;
- `ESPACIOS GASTADOS` remains empty;
- cantrips receive no preparation marks;
- Defense / attributes / portrait name / attacks / page-2 background fields;
- measured skill columns;
- XP / Next Level Identification placement;
- Core Stats tested values;
- spellcasting summary;
- two Rasgos rows;
- short Historia sample.

Current known coverage gap:

1. Inspiration has not yet been populated/tested in the new Core Stats layer.

Recommended future expansion candidates:

- Inspiration marker;
- saving-throw checks + values using exact-source geometry;
- additional spell levels while preserving spell semantics;
- more Historia / Otros Rasgos y Atributos;
- selected Notes content;
- additional Rasgos rows;
- later, migration of the proven primitives into production renderer code.

Operating rule remains:
- continue to expand and repair in the same run when diagnostic clarity is preserved;
- independently audit every generated draft;
- PR #85 remains **DRAFT / DO NOT MERGE** until a complete owner-reviewed visual family is ready.


---

## 8. Owner approval gate — Custom v1 Run-6 draft

**Date:** 2026-09-19  
**Status:** OWNER APPROVED

After Run 6, the owner explicitly approved the **latest Custom v1 draft presented for review: Run 6**, and confirmed that no strategy change was needed.

This establishes:

- Strategy 1 / Hybrid remains the selected renderer strategy;
- the Run-6 Custom v1 result is the owner-approved visual baseline;
- Runs 1–6 are considered a successful convergence sequence for Custom v1;
- the section-isolated Form/OCG architecture remains the approved direction for future renderer work.

This approval does not automatically approve Custom v2 or Classic, does not merge PR #85, and does not remove future regression/audit requirements.

Durable approval checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_RUN6_OWNER_APPROVED.md`


---

## 9. Custom v1 production promotion status

**Pass 1:** PASS

Owner-approved Run-6 Hybrid mechanics have been promoted into real renderer code:

`DesktopCustomV1HybridRenderer.kt`

The whole-draft Custom-v1 path now uses production Hybrid sections for:

- MAIN approved Run-6 regions;
- NARRATIVE Background/Ideals/Bonds/Flaws/Story;
- SPELL_LIST Cantrips and Level 1.

Temporary legacy residual rendering remains for:

- EQUIPMENT page;
- NARRATIVE Personality / long-form Other Traits / Notes;
- spell levels 2–9;
- NOTES page.

Real-model mapping does not invent unsupported values. Alignment, AC breakdown and next-level XP threshold remain blank until the domain provides them. Inspiration remains deferred from the Run-6 baseline.

Production promotion uncovered and corrected one density issue: real long Ideals/Bonds/Flaws require multi-rule paragraph geometry rather than the short single-line QA fixture.

Final production-promotion Pass-1 checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_PRODUCTION_PROMOTION_PASS1.md`

Next objective:

- migrate the remaining Custom-v1 legacy regions into independent Hybrid layers;
- generate a complete five-page all-Hybrid Custom-v1 candidate;
- independently audit and return it for owner review before Custom-v1 production closure.

PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 10. Run 7 — Complete five-page all-Hybrid Custom v1 visual draft

**Result:** PASS FOR OWNER REVIEW

Run 7 corrects the process error discovered after Production Promotion Pass 1.

Durable rule added:

> Visual-approval drafts must not mix the active Hybrid renderer with legacy visual rendering. Every visible region under review must use the active strategy. QA/dummy values may be used for visual calibration when production-domain values are unavailable; production mapping is a separate gate.

Run 7 renders all five Custom-v1 pages through independent Hybrid section/layer geometry:

### Page 1
- Identification
- portrait QA figure
- Defense
- Core Stats
- Attributes
- Skills
- Spellcasting Summary
- Attacks
- Traits

### Page 2
- Equipment
- Coins
- Valuables
- Special Equipment

### Page 3
- Background
- Personality
- Ideals
- Bonds
- Flaws
- Other Traits/Attributes
- Story
- Notes

### Page 4
- Cantrips
- spell levels 1–9

### Page 5
- Notes
- grid/doodle QA layer

Spell semantics preserved:
- `ESPACIOS GASTADOS` always empty;
- cantrips have no preparation checks.

Final successful Run-7 scaffold:

`35464998378` / run #2702

Final visual regression:
- final portrait correction changed only page 1 portrait region;
- pages 2–5 remained pixel-identical to the prior successful all-Hybrid render.

Durable checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_HYBRID_STRATEGY_RUN7_ALL_HYBRID_CUSTOM_V1.md`

Current next gate:

- owner review of the complete five-page all-Hybrid Custom-v1 visual draft;
- PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 11. Run 7 final coordinate calibration

**Result:** PASS FOR OWNER REVIEW

After review of the complete five-page all-Hybrid Run-7 draft, the owner identified only local X/Y calibration issues on pages 2, 3 and 5.

The correction pass used measured authoritative source-PDF line/glyph geometry rather than visual guesswork.

Corrected:

- Page 2 Equipment columns 2/3;
- Page 2 Gemas/Joyas/Arte object/value geometry;
- Page 2 Equipo Especial description X origin;
- Page 2 Equipo Especial check X/Y placement using exact printed-square rectangles;
- Page 3 Otros Rasgos divided-column X origins;
- Page 3 Notas full-width X origin;
- Page 5 Notes second-column X origin.

Final Scaffold:

`35465731044` / run #2708 — SUCCESS

Regression diff vs the prior final Run-7 draft:

- page 1: 0% change;
- page 2: changed only for requested calibration;
- page 3: changed only for requested calibration;
- page 4: 0% change;
- page 5: changed only for requested calibration.

Durable checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_RUN7_FINAL_COORDINATE_CALIBRATION.md`

No rendering-strategy change occurred.

Current gate:

- owner review of the calibrated complete five-page all-Hybrid Custom-v1 draft;
- PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 12. Custom v1 final visual approval — frozen baseline

**Owner status:** APPROVED / FROZEN  
**Date:** 2026-09-19

The owner approved the final calibrated Run-7 five-page all-Hybrid Custom-v1 draft and explicitly requested that the result be preserved in the repository before moving on.

Current visual authority:

- rendering commit: `7448693e36ee1b26243bd4091615dba725e95027`;
- Scaffold: `35465731044` / run #2708 — SUCCESS;
- workflow artifact id: `10591616012`;
- approved PDF: `hybrid-strategy1-run7-composite.pdf`;
- overlay diagnostic: `hybrid-strategy1-run7-overlay-only.pdf`.

Stable approved-baseline pointer:

`docs/PC_SHEET_CUSTOM_V1_APPROVED_BASELINE.md`

Owner approval checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_RUN7_FINAL_OWNER_APPROVED.md`

### Preservation rule

Run 7 final calibration supersedes earlier Custom-v1 drafts as the current visual authority.

Future work must not silently alter this baseline. Any Custom-v1 visual modification requires an explicit new change relative to the approved baseline and a new owner approval gate.

### Current state

**CUSTOM V1 VISUAL DESIGN: APPROVED / FROZEN**

No further Custom-v1 visual experimentation is required unless the owner explicitly reopens it.

The project may now proceed to the next development step.

PR #85 remains **DRAFT / DO NOT MERGE** until its broader scope/closure gate is resolved.


---

## 13. Custom v2 visual phase — per-Attribute Runs 1–2

Custom v2 reuses Strategy 1 / Hybrid without reopening rendering-strategy research.

Source structure:

- page 1 = per Attribute first-page variant;
- page 2 = per Ability first-page variant;
- pages 3–5 = shared v2 pages.

Durable source geometry analysis:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V2_SOURCE_GEOMETRY_ANALYSIS.md`

### Per-Attribute Run 1

Result:

**technical PASS / strong visual partial PASS**

Independent audit found only local Y-placement defects:

- portrait figure entered the lower banner;
- handwritten portrait name overlapped the crown ornament;
- first Historia line overlapped the printed heading.

Checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V2_ATTRIBUTE_HYBRID_RUN1.md`

### Per-Attribute Run 2

Result:

**PASS FOR OWNER REVIEW**

Corrections were limited to:

- portrait figure vertical extent;
- portrait-name placement;
- Historia first content rule.

Differential audit vs Run 1:

- page 1 changed only in portrait/name region;
- page 2 changed only in Historia;
- pages 3 and 4: 0% pixel change.

Final Run-2 scaffold:

`35467797642` / run #2731 — SUCCESS

Checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V2_ATTRIBUTE_HYBRID_RUN2.md`

Current gate:

- mandatory owner review of Custom v2 — per Attribute;
- shared pages should remain unchanged for the per-Ability variant unless owner review identifies a shared-page issue;
- PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 14. Custom v2 per-Attribute — final calibration Runs 3–4

After Run 2, the owner requested only local calibration:

- `Raza` Y;
- portrait-name Y;
- fuller use of modifier ellipse/oval space;
- whole-sheet check-mark geometry audit.

Run 3:

- calibrated Identification/portrait Y;
- expanded modifier values into measured ellipse interiors;
- applied family-specific check optical offsets.

Independent overlay-only audit then measured the actual dark-stroke margins and found a small residual checkbox bias.

Run 4 changed **only marker offsets** and completed the calibration.

Final result:

**PASS FOR OWNER REVIEW**

Final code commit:

`03c3155301196eaab1229afb4827c46f8acf04cc`

Final Scaffold:

`35469291933` / run #2739 — SUCCESS

Final checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V2_ATTRIBUTE_HYBRID_RUNS3_4_FINAL_CALIBRATION.md`

Key lesson carried forward:

> For frozen v8 symbols, fitting the font metrics to the source rectangle is necessary but not always sufficient. Final calibration should measure the rendered dark-stroke margins from the overlay-only PDF and apply glyph-family-specific optical offsets when needed.

Current gate:

- owner review of final Custom-v2 per-Attribute Run-4 candidate;
- per-Ability work should reuse the shared v2 pages unchanged unless owner review identifies a shared-page issue;
- PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 14. Custom v2 per-Attribute Runs 3–4 — final calibration candidate

Owner observations after Run 2 were limited to:

- `Raza` Y;
- portrait-name Y;
- better use of Attribute-modifier ellipse space;
- complete check-mark geometry audit.

Owner clarified that modifier feedback referred to **using the available ellipse interior more effectively**, not merely moving the value vertically.

### Run 3

Commit:

`b80cac0af3d63e97bf1f922fa1afda592c86b933`

Scaffold #2737 — SUCCESS.

Calibrated:

- Raza value lower toward its source rule;
- portrait name lower inside its banner;
- modifier values enlarged from 11.5 pt to 15.5 pt and fitted to wider/tighter ellipse-interior rectangles;
- initial family-specific check offsets.

### Run 4

Commit:

`03c3155301196eaab1229afb4827c46f8acf04cc`

Scaffold #2739 — SUCCESS.

Run 4 refined only v8 check optical centering using measured raster margins.

Representative final check-center errors are sub-quarter-point across:

- page-1 single / expertise checks;
- page-2 Special Equipment checks;
- page-3 spell checks.

Run 2 → Run 4 regression:

- only pages 1–3 changed in requested calibration families;
- page 4: 0% change.

Run 3 → Run 4 regression:

- only checkbox regions changed;
- page 4: 0% change.

Final result:

**CUSTOM V2 — PER ATTRIBUTE — RUN 4: PASS FOR OWNER REVIEW**

Checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V2_ATTRIBUTE_RUN3_RUN4_CALIBRATION.md`

PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 15. Custom v2 — per Attribute owner approval / shared-page freeze

**Owner status:** APPROVED / FROZEN  
**Date:** 2026-09-19

Approved rendering state:

- commit: `03c3155301196eaab1229afb4827c46f8acf04cc`;
- Scaffold: `35469291933` / run #2739 — SUCCESS;
- PDF: `hybrid-custom-v2-attribute-run4-composite.pdf`;
- overlay: `hybrid-custom-v2-attribute-run4-overlay-only.pdf`.

Owner approval checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V2_ATTRIBUTE_RUN4_OWNER_APPROVED.md`

Stable v2 pointer:

`docs/PC_SHEET_CUSTOM_V2_APPROVED_BASELINES.md`

### Shared-page freeze

The owner-approved per-Attribute artifact also freezes the current v2 common pages:

- source page 3 — Equipment / Narrative;
- source page 4 — Spells;
- source page 5 — Notes.

These shared pages are the approved common baseline for the per-Ability variant.

Per-Ability work should reuse them unchanged and focus on source page 2.

Any shared-page modification requires a new explicit delta, independent audit, and owner review.

### Current next gate

Implement and audit **Custom v2 — per Ability** using:

- source page 2 as the alternative first page;
- approved shared pages unchanged.

PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 16. Custom v2 — per Ability Run 1

**Result:** PASS FOR OWNER REVIEW

Implementation:

- commit `75c61966312dc745f1d3763631060cdb96db6dae`;
- Scaffold `35470243545` / run #2754 — SUCCESS.

The per-Ability draft uses:

- source page 2 as its unique first page;
- the owner-approved v2 shared pages unchanged.

First-page-specific geometry includes:

- tall portrait;
- upper-left proficiency / Inspiration;
- six score/modifier blocks using the approved ellipse treatment;
- centralized saving throws;
- centralized skills;
- approved family-specific v8 check optical offsets.

Shared-page regression proof:

- Equipment/Narrative: 0% pixel change;
- Spells: 0%;
- Notes: 0%.

Independent audit found no local first-page defect requiring another run before owner review.

Checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V2_ABILITY_HYBRID_RUN1.md`

Current gate:

- mandatory owner review of Custom v2 — per Ability;
- PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 17. Custom v2 — per Ability Run 2 — owner feedback calibration

**Result:** PASS FOR OWNER REVIEW

Owner Run-1 feedback identified four page-1 issues only:

- Bono por competencia;
- Inspiración;
- Clase de armadura;
- portrait name.

The owner explicitly confirmed the Ability treatment was perfect.

Run 2 corrected only those concerns:

- Bono por competencia now reproduces the approved per-Attribute value-to-label optical offset;
- Inspiración now reproduces the approved per-Attribute marker-to-label optical offset;
- the portrait name follows the actual page-2 decorative-banner anchor and sits inside the banner;
- Armor Class no longer overprints two `16` values; only the intended 20 pt value is rendered.

Correction commit:

`be689bb6628a46e562428159fc72b2ee51b95d2c`

Scaffold:

`35471866920` / run #2760 — SUCCESS

Regression:

- shared pages 2–4: 0 changed pixels;
- page-1 Ability / Saving Throw / Skill region: 0 changed pixels.

Checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V2_ABILITY_HYBRID_RUN2_OWNER_FEEDBACK.md`

Current gate:

- mandatory owner review of Custom v2 — per Ability Run 2;
- if approved, freeze the per-Ability baseline and close Custom v2 visual-family approval;
- then proceed to Classic D&D-style;
- PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 18. Custom v2 — per Ability Run 2 owner approval

**Status:** OWNER APPROVED / FROZEN

The owner approved the corrected Run-2 per-Ability result after the bounded Run-1 feedback calibration.

Frozen rendering baseline:

- commit: `be689bb6628a46e562428159fc72b2ee51b95d2c`;
- Scaffold: `35471866920` / run #2760 — SUCCESS;
- approval checkpoint: `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V2_ABILITY_RUN2_OWNER_APPROVED.md`.

Both Custom-v2 first-page alternatives and their shared base pages are now approved/frozen.

---

## 19. Extended-page scope clarification

**Owner clarification:** 2026-09-19

Extended pages are required for **every** visual family:

- Classic D&D-style;
- Custom v1;
- Custom v2 — per Attribute;
- Custom v2 — per Ability.

This is consistent with D-0074 section 5: extensions are design-specific. There is no universal generic Extended-page skin.

Important state distinction:

- Custom v1 base sheet: approved/frozen;
- Custom v2 per-Attribute base sheet: approved/frozen;
- Custom v2 per-Ability base sheet: approved/frozen;
- family-matched Extended-page sets for the Custom families: still pending visual design/QA.

Do not use a base-sheet approval to claim that the complete family, including Extended pages, is closed.

---

## 20. Classic D&D-style Run 1 — populated visual candidate

**Result:** OWNER REJECTED / historical evidence only

Implementation/final calibration commit:

`14b5c2561060fc319ef63cf7a9f2ffcbb7b9c996`

Scaffold:

`35473827949` / run #2779 — SUCCESS

The first application-designed Classic candidate is generated from blank Letter pages; it does not overlay or recreate an official published character sheet.

It contains:

1. Main — portrait, attributes, saves, skills, combat, attacks, quick resources;
2. Equipment & Resources;
3. Features & Story;
4. Spell List;
5. Notes & Reference;
6. Extended — Custom Statistics.

The sixth page deliberately demonstrates that the Classic extension pages use the same typography, border rhythm, spacing and print language as the base family.

Run-1 text layout has a strict no-overflow guard. The first implementation run exposed undersized name-header and spell-summary mini-stat boxes; those were corrected without changing the overall composition. The final run contains no recorded overflow.

Owner decision:

- Run 1 was rejected at the design-grammar level;
- English leakage, insufficient official-sheet lineage, inconsistent marker/box semantics, insufficient writable capacity, incomplete extension semantics and missing/weak Attribute-to-Ability relationship presentation were material owner findings;
- do not resume from the Run-1 dashboard composition;
- Run 2 supersedes it.

PR #85 remains DRAFT / DO NOT MERGE.


---

## 21. Classic D&D-style Run 2 — complete family owner approval

**Status:** OWNER APPROVED / FROZEN

Approved renderer/proof commit:

`3dbcff8f5f9a2413f6deb8e400daeb6288b4f6b1`

Final Scaffold push run:

`35480871986` / run #2793 — SUCCESS

Stable baseline:

`docs/PC_SHEET_CLASSIC_APPROVED_BASELINE.md`

Approval checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CLASSIC_RUN2_OWNER_APPROVED.md`

Run 2 was rebuilt from a targeted design investigation rather than incrementally patching the rejected Run-1 dashboard grammar.

Approved set:

1. Main;
2. Character / History / Equipment;
3. Spell List;
4. Extended — Custom Statistics;
5. Extended — Traits & Features;
6. Extended — Resources & Options;
7. Extended — Inventory / Equipment;
8. Extended — Spells;
9. Extended — Notes.

Durable Classic lessons:

- the family should feel descended from recognizable official D&D paper-sheet grammar without tracing or reproducing official artwork;
- writable whitespace is functional;
- compact framed treatment belongs to lookup/calculated values, while mutable player information needs practical writing surfaces;
- Attribute -> Ability/skill relationships must remain explicit, including custom statistics;
- extension pages must be genuine family-native paper pages rather than report/dashboard appendices;
- writing rules remain visible under populated text;
- text/marker/rule Y placement must preserve a clear paper-writing rhythm.

Final owner feedback required a fresh proof correcting Y-axis and ruled-line behavior. The corrected proof passed strict overflow/language guards and independent nine-page render inspection before owner approval.

Classic is now frozen. Do not recalibrate it without a new owner-observed defect or product requirement.

Remaining visual-family work is the family-matched Extended-page design/QA still pending for frozen Custom v2, followed by production renderer integration and complete D-0074 end-to-end QA. Custom v1 base + Extended is owner-approved/frozen.

PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 22. Custom v1 Extended Run 1 — rejected family candidate

**Status:** OWNER REJECTED / historical evidence only

Candidate implementation:

`b86b4539386cca8c567469bd97202c454df411ad`

Scaffold push run:

`35481567673` / run #2797 — SUCCESS

Checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN1_OWNER_REVIEW.md`

The candidate preserves the five-page owner-approved Run-7 Custom-v1 base and appends all six D-0074 extension roles.

Important regression guard:

- Run-7 generator source blob is unchanged from the approved base commit;
- pages 1–5 are rendered before and after extension append;
- CI requires them to remain pixel-identical.

The extension visual language intentionally follows Custom v1: owner-source branding, grayscale alternating bands, thin writing rules, centered headings and practical writable space.

Owner decision:

- Run 1 did not preserve Custom-v1 spacing/capacity, layout/maquetación, typography or box grammar closely enough;
- superficial motifs were insufficient to make the extension pages part of the same design family;
- do not continue patching this extension direction;
- source-faithful Run 2 supersedes it.

PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 23. Custom v1 Extended Run 2 — source-faithful complete family candidate

**Status:** PASS FOR OWNER REVIEW

Candidate implementation:

`b244159b467d162c8637db5532dcfe6f2f831953`

Final Scaffold push run:

`35484718817` / run #2817 — SUCCESS

Checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN2_OWNER_REVIEW.md`

Run 2 abandons the rejected Run-1 extension composition.

Design method:

- use the owner-authored Custom-v1 PDF as the actual extension design kit;
- Custom Statistics derives from source page 1;
- Traits derives from source page 3;
- Resources and Inventory derive from source page 2;
- Spells directly reuses source page 4 grammar;
- Notes directly reuses source page 5 grammar;
- preserve source EnchantedLand headings where safe;
- use approved overlay fonts only for generated arbitrary text that source subset fonts cannot safely encode.

The five approved Run-7 base pages remain frozen and are guarded pixel-identical.

Final strict audit:

- 11 Letter pages;
- strict text-overflow guard passes;
- no overflow diagnostic file;
- no clipped source logo;
- no unsafe subset-glyph loss;
- no intrusive continuation labels in Spells/Notes;
- body-location rows respected on Inventory continuation;
- all CI jobs green.

Current gate:

- mandatory owner review of source-faithful Custom-v1 Extended Run 2;
- do not mark extensions approved/frozen before explicit owner approval;
- if corrections are requested, preserve the frozen five-page base unless the owner explicitly reopens it.

PR #85 remains **DRAFT / DO NOT MERGE**.


---

## 24. Custom v1 Extended Run 3 — owner-feedback correction

**Status:** PASS FOR OWNER REVIEW

Final implementation:

`3203234e0820566298b4a49eb87da14c9ee75de8`

Scaffold:

`35486748557` / run #2831 — SUCCESS

Artifact:

`10598081443`

Checkpoint:

`docs/checkpoints/2026-09-19_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN3_OWNER_REVIEW.md`

Owner feedback on Run 2 was bounded to the Custom Statistics page:

- white-space/cut artifacts;
- poorly selected Ability/skill blocks;
- wrong Ability/skill typography.

Run 3 corrects those findings without reopening the rest of the family:

- page 6 is composed from clipped authentic source fragments on a clean page;
- all six statistics columns use authentic five-row source blocks selected from WIS/INT geometry to preserve white/gray parity and actual source square/rule construction;
- duplicate generic squares/rules are not overlaid;
- the embedded GillSansMT subset was proven unsafe for arbitrary new glyphs;
- FiraSans-Regular at 10 pt / 60% horizontal scale reproduces the measured source compressed label treatment with complete Spanish glyph coverage.

Run 2 -> Run 3 diff:

- pages 1–5: pixel-identical;
- page 6: intended changes only;
- pages 7–11: pixel-identical.

Current gate:

- mandatory owner review of the complete Run-3 proof;
- do not freeze the extension family before explicit owner approval;
- keep PR #85 DRAFT / DO NOT MERGE.


---

## 25. Custom v1 Extended Run 6 — owner approval and canonical carry-forward strategy

**Status:** OWNER APPROVED / FROZEN  
**Date:** 2026-09-20

Approved implementation:

`69b308f3d5d493d06bd0107ac66c7524935aa9fa`

Scaffold:

`35529317947` / #2885 — SUCCESS

Artifact:

`10609869599`

Approval checkpoint:

`docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN6_OWNER_APPROVED.md`

Stable Custom-v1 baseline:

`docs/PC_SHEET_CUSTOM_V1_APPROVED_BASELINE.md`

Canonical Custom Extended-page methodology:

`docs/PC_SHEET_CUSTOM_EXTENDED_STRATEGY.md`

Run 6 closes the Custom-v1 Extended visual gate.

The key convergence lesson is not merely "use multiple layers." It is:

> Use independent layers to isolate responsibilities, but derive geometry, density, typography and naming from the frozen source family rather than from empty page space.

The canonical layer model is:

1. source/measured structure;
2. optional bounded cleanup;
3. labels;
4. values;
5. markers/symbols.

Layer diagnostics remain mandatory when they materially improve traceability.

Run-history lessons carried forward:

- broad masks can create white/color artifacts and should not substitute for correct source-fragment selection;
- embedded source font subsets are unsafe for arbitrary generated text;
- source-matched generated typography must preserve readability and full glyph coverage;
- do not redesign already-working pages during unrelated corrections;
- source row cadence and page density are part of the visual contract;
- owner terminology/naming conventions are part of the visual contract;
- green CI is not visual approval;
- frozen base pages require regression protection.

Custom-v2 Extended work must read `docs/PC_SHEET_CUSTOM_EXTENDED_STRATEGY.md` before implementation.

Do not copy Custom-v1 geometry blindly into v2. Carry forward the architecture, measurement discipline, diagnostics and QA method, then re-derive exact geometry/typography from the frozen v2 baselines.

PR #85 remains **DRAFT / DO NOT MERGE** while Custom-v2 Extended and remaining renderer/product gates are pending.

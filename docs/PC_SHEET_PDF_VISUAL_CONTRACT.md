# PC Sheet PDF — durable visual contract

**Status:** ACTIVE / PRE-EXISTING RULE LEDGER  
**Purpose:** single operational reference for visual rules that had already been established through owner review and frozen proof runs.

This file does **not** create new design rules. It consolidates rules already present in the owner-approved baseline/checkpoint documents so future recovery work does not lose them between runs.

## Authority

The visual contract is reconstructed from these already-approved authorities:

- `docs/PC_SHEET_CLASSIC_APPROVED_BASELINE.md`;
- `docs/PC_SHEET_CUSTOM_V1_APPROVED_BASELINE.md`;
- `docs/PC_SHEET_CUSTOM_V2_APPROVED_BASELINES.md`;
- `docs/checkpoints/2026-09-19_PC_SHEET_PDF_CLASSIC_RUN2_OWNER_APPROVED.md`;
- `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V1_EXTENDED_RUN6_OWNER_APPROVED.md`;
- `docs/checkpoints/2026-09-20_PC_SHEET_PDF_CUSTOM_V2_EXTENDED_RUN7_OWNER_APPROVED.md`;
- `docs/checkpoints/2026-09-22_PC_SHEET_PDF_VISUAL_RECOVERY_TRIPLE_AUTHORITY.md`.

A later owner defect report may identify a regression against this contract. It is **not automatically a new rule**.

## Global rules

1. **Owner-approved artifacts are the visual goldens.** Do not use a later mutable production render as the sole visual reference.
2. **Preserve family-native construction.** Classic remains Classic; Custom v1 remains v1; Custom v2 remains v2.
3. **Prefer extra family-native pages over compression, skipped rows, artificial blank gaps or silent omission.**
4. **Terminology contract: use `Raza`, never user-facing `Especie`.**
5. **No repair by flattening away the approved construction.** Where the approved Custom family uses source-derived/vector elements and semantic layers, preserve them.
6. **Layered repair is mandatory for Custom template-derived pages.** Keep independent semantic OCG layers:
   `STRUCTURE -> CLEANUP -> LABELS -> VALUES -> MARKERS`.
   - `STRUCTURE`: source-derived paper/layout geometry;
   - `CLEANUP`: bounded masks/removal of unwanted source content or artifacts;
   - `LABELS`: family-native headings/subheadings;
   - `VALUES`: generated character values/text;
   - `MARKERS`: checks, states and symbol-font marks.
7. **Do not merge/rasterize multiple semantic layers as a shortcut to remove artifacts.** Fix the responsible layer while preserving the others. Transparent source-derived images are acceptable only for a component whose approved construction already requires image extraction (for example the v2 attribute ornament); they are not a substitute for flattening a source page/header.
8. Generated text must follow measured source/paper geometry, not an independent generic layout model.

## Classic D&D-style — pre-existing frozen rules

From the approved corrected Run 2:

- recognizable D&D paper-sheet lineage without tracing official artwork;
- writable whitespace is functional capacity;
- writing/reference rules remain visible even when generated text is prefilled;
- generated text, rules and markers share one intentional Y-axis rhythm;
- generated text must sit in the writing row, not collide with or float independently of its rule;
- do not skip alternating writing rules;
- do not create unnecessary empty semantic bands between consecutive prose;
- long content flows to a Classic-native continuation page rather than removing later writable rules;
- player-maintained prose/list information keeps practical ruled/table writing area;
- spell continuations remain level-specific; do not collapse unrelated high levels into a generic `6+` bucket for fit;
- inventory continuations use the available native table space without `(cont)` filler or synthetic half-empty rows;
- user-facing terminology is `Raza`, including `RAZA`, `ATRIBUTOS DE RAZA` and `RASGOS DE RAZA / TRASFONDO / OTROS`.

## Custom v1 — pre-existing frozen rules

Base Run 7 + Extended Run 6 remain the family authority:

- source-measured X/Y geometry and row cadence;
- family-approved font roles, not ad-hoc substitutions;
- Equipment/Gemas/Arte/Joyas and Otros Rasgos use the approved measured columns;
- Notes uses the approved two-column grammar;
- continuation content uses every physical source row in natural reading order;
- source checkboxes remain source checkboxes; generated checked-state marks use the approved v8 marker geometry and optical centering;
- continuation indicators must not replace or collide with native headings;
- layer separation remains the normal construction method for source-faithful Extended pages.

## Custom v2 — pre-existing frozen rules

Extended Run 7 remains the authority:

- five independent semantic OCG layers per page:
  `STRUCTURE -> CLEANUP -> LABELS -> VALUES -> MARKERS`;
- transparent source-derived attribute ornaments instead of opaque crops;
- source-measured gray bands, rule geometry, checkbox geometry and marker placement;
- direct frozen equivalents use source Corbel-Bold / Corbel with the measured horizontal transforms;
- generated body/value roles use the approved Fira Sans roles;
- applicable states use Para Hoja de PJ Symbols v8;
- `Raza` terminology contract; `Especie` must be absent from user-facing output;
- title/subtitle roles must not silently fall back to a different font when the frozen Run-7 source-font construction already proved the text;
- logo/header defects must be repaired through the layered source-preserving construction, not by flattening/rasterizing the whole header;
- Clase/Dotes and Raza/Trasfondo/Otros content uses consecutive measured source rows without gratuitous empty bands;
- Equipment continuation reuses the v2 Equipment grammar and expands by native columns/pages.

## How owner review must be classified

When owner feedback repeats one of the rules above, record it as:

**PRE-EXISTING RULE VIOLATED / REGRESSION**

not:

**NEW RULE**

A review may contain a new observed symptom (for example “logo contains artifacts”), but the governing repair rule can still be an older frozen rule (for example layered source-preserving cleanup).

## Current 2026-09-22 classification

The second recovery review did **not** introduce new design rules.

Its observations identify current regressions against already-established rules:

- Classic rule/text rhythm, visible reference lines, no unnecessary blank gaps and D&D lineage;
- `Raza` terminology;
- Custom-v1 check optical centering;
- Custom-v2 row cadence, exact frozen font roles and artifact-free layered header/logo rendering.

Treat these as defects against this contract.

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
2. **Preserve family-native construction.** Fantasy Sheet (legacy internal id `CLASSIC_DND_STYLE`) remains its own application-designed family; Custom v1 remains v1; Custom v2 remains v2.
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

## Fantasy Sheet - legacy internal id `CLASSIC_DND_STYLE`

**Owner naming correction 2026-09-22:** the existing application-designed family is allowed to remain, but it is **not** to be called Classic, official, or official-like. It does not sufficiently resemble the official D&D sheet to justify that claim.

Historical checkpoints and source filenames may still contain “Classic” as immutable provenance. New owner-facing proof names, status text and documentation use **Fantasy Sheet**.

If an official-like D&D family is implemented later, it is a separate design family and must pass an owner review specifically for resemblance to the official sheet grammar.

### Pre-existing frozen visual rules

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

## Owner-confirmed continuation/reuse rules — 2026-09-28

The following rules were clarified directly by owner review of the real Mara `0.5.0-preqa.8 / 50800` runtime outputs. They are durable product/visual requirements for future PC-sheet repair work.

### Reuse proven native/source components before inventing new Extended components

When the base/approved sheet already contains a component that correctly represents the same semantic content, Extended/continuation rendering must use that component's approved visual grammar as the first reference.

Do not invent a generic substitute merely because the content is on an Extended page.

This applies explicitly to:

- Custom-v2 attributes/custom statistics;
- ordinary Equipment;
- Equipo Especial;
- Notes.

A continuation may extend capacity, add rows/columns or adapt pagination, but should remain recognizably the same approved component unless the owner explicitly approves a redesign.

### Custom-v2 custom attributes

The Custom-v2 page-1 attribute area already demonstrates six attributes using the approved source-native font, size, ornament and layout grammar.

Custom Statistics must reuse that same grammar and capacity logic. Four Mara custom attributes should fit together in the native-scale presentation; a three-card bespoke layout is not an acceptable substitute.

Never paint empty/fake attribute shells for unused capacity.

### Trait ordering

Trait/feature presentation is intentionally **category-grouped**. Global numeric fixture order is not required across categories.

Within each category, preserve coherent record order and semantic boundaries.

### Ordinary Equipment

Ordinary Equipment is a compact identity/list problem, not a prose-description page family.

Reuse the existing base Equipment grammar. Prefer compact item identities such as `3 x Frasco de tinta` plus only genuinely useful compact state/weight information.

Do not create long descriptive Inventory pages merely because stress-fixture metadata contains descriptions. Avoid mechanically exposing metadata such as `Consumible` unless it is useful and belongs in the intended player-facing equipment representation.

### Equipo Especial

Reuse the existing native/source Equipo Especial element rather than constructing an unrelated Extended table.

Additional/custom locations must be supported as legitimate entries/rows without overprinting canonical source labels.

### Notes

Reuse the existing Notes sheet/module grammar for overflow Notes; do not invent a separate generic Extended Notes visual family.

At minimum:

- emphasize the note identity such as `Nota N` (for example bold);
- leave a visible line/row separation between note records;
- preserve note identity across page continuations;
- consume available Notes rows/columns before adding another page.

### Portrait/name ribbon

Center the character name horizontally and vertically inside the existing portrait ribbon.

If the name does not fit cleanly on one line, use two centered lines rather than clipping, shifting or excessive compression.

### Semantic names

Meaningful generated identities must **wrap rather than ellipsize** when they need additional horizontal space.

Rows/records may grow in height to preserve the complete semantic identity.

### Adaptive continuation packing and page count

There is no fixed target page count.

Continuation layout must:

1. allocate physical space according to streams that still contain content;
2. let surviving streams reclaim space from exhausted sibling sections;
3. stop repeating empty scaffolds;
4. preserve semantic record boundaries while repacking;
5. create another page only when remaining content actually requires it.

A high page count is a defect signal only when it is caused by poor layout/packing, not because it exceeds a numeric threshold.

## How owner review must be classified

When owner feedback repeats one of the rules above, record it as:

**PRE-EXISTING RULE VIOLATED / REGRESSION**

not:

**NEW RULE**

A review may contain a new observed symptom (for example “logo contains artifacts”), but the governing repair rule can still be an older frozen rule (for example layered source-preserving cleanup).

## Current 2026-09-22 classification

The second recovery review did **not** introduce new design rules.

Its observations identify current regressions against already-established rules:

- Fantasy-sheet rule/text rhythm, visible reference lines and no unnecessary blank gaps;
- `Raza` terminology;
- Custom-v1 check optical centering;
- Custom-v2 row cadence, exact frozen font roles and artifact-free layered header/logo rendering.

Treat these as defects against this contract.


## Process security

The following files are mandatory continuation authority:

- `docs/PC_SHEET_PDF_ITERATION_LEDGER.md` - append-only run/issue history;
- `docs/PC_SHEET_PDF_GEOMETRY_GATES.md` - independent X/Y expectations.

A rule recorded there is not allowed to disappear merely because a later generated proof or production snapshot differs.

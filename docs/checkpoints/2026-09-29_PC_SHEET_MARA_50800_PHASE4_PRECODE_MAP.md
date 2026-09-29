# Mara 50800 — Phase 4 Fantasy pre-code QA/source map

**Date:** 2026-09-29 (Chile local time)  
**Branch:** `fix/pc-sheet-50800-phase1-native-semantics`  
**Status:** ACTIVE / BLOCKING PRE-CODE MAP FOR PHASE 4  
**Acceptance authority:** `docs/checkpoints/2026-09-28_PC_SHEET_MARA_50800_PRE_FIX_ACCEPTANCE_MATRIX.md`  
**Previous progression gate:** `docs/checkpoints/2026-09-29_PC_SHEET_MARA_50800_PHASE3_CLOSURE.md`

## 1. Scope

Phase 4 is restricted to Fantasy-specific failures exposed by the real 50800 QA:

- M50800-13 — Fantasy Combat/Actions must preserve a structured semantic table instead of flattening entries into prose;
- M50800-26 — meaningful generated names wrap; they must not be silently ellipsized;
- M50800-27 — Fantasy continuation streams must reclaim exhausted sibling space rather than repeat mostly-empty fixed scaffolds.

M50800-28 remains a governing rule: page count is an observation, never a numeric target.

## 2. Exact current artifact recheck

Current exact artifact used for this pre-code review:

- renderer/code family includes Phase 3 fixes through `b9e77ff90799b2e4ec90e87952c565f1380f3d8e`;
- workflow 4261 / `36602606829` — SUCCESS;
- artifact `11049817808`;
- Fantasy output: 42 pages (observation only).

Rendered and text-layer review of the exact Fantasy PDF confirms the following.

### M50800-13 — Fantasy Combat remains flattened

Fantasy page 17 is titled `COMBATE / ACCIONES`, but the continuation body is one ruled prose area headed `REFERENCIA DE COMBATE / ACCIÓN / DAÑO`.

Each logical combat entry is serialized into sentences such as:

- `Ataque — Técnica 1 — Corte de fase · Ataque +8 · Efecto / daño...`;
- `Acción — Técnica 2 — Pulso inverso · Efecto / daño...`;
- `Reacción — Técnica 4 — Descarga prismática...`.

Range, attack bonus, damage/effect and notes are not independently scannable columns. This exactly matches M50800-13: the semantic table pattern is lost.

### M50800-26 — semantic ellipsis remains widespread

Exact PDF text/render contains visible `...` truncation in meaningful generated data, including:

- character identity: `Mara de los Siete...`;
- class/background metadata such as `Cartógrafa de fract...`, `Cronomante del Umbr...`, `Cartógrafa de Parad...`;
- base Combat names/effects such as `Técnica 4 — Descarga prismá...`, `2d6 + 4 daño etér...`;
- Traits names such as `Rasgo extenso 13 — Umbr...`, `Rasgo extenso 16 — Reso...`;
- Resources such as `Cronomant...` and continuation identities like `Reserva 7: Fortuna (c...`;
- Options such as `Protocolo de paradoja 1 (...`;
- Equipment identities such as `Astrolabio de cobre con ani...`, `Frasco de tinta que recuerd...`.

This is not cosmetic. M50800-26 explicitly requires semantic identity to wrap rather than be replaced by ellipsis.

### M50800-27 — Fantasy Resources/Options fixed scaffold remains

Fantasy pages 18–27 repeatedly emit the same split `RECURSOS Y OPCIONES` page.

Observed progression:

- early pages contain both streams;
- later pages continue only Resources/Markers;
- the lower `OPCIONES Y ESTADOS RELEVANTES` frame remains reserved even when exhausted;
- page 23 is an extreme example: only a few Resource continuation rows and one Option tail are present while most of the lower half is empty;
- subsequent pages continue repeating the same fixed sibling geometry.

This is the same architectural defect already repaired in Custom-v2 Phase 3, but Fantasy uses a different visual grammar and therefore needs a family-specific implementation.

### Other Fantasy fixed-scaffold evidence

Current output also includes repeated Inventory pages 28–34 and Notes pages 35–42. These are evidence that M50800-27 is broader than Resources/Options.

Phase 4 will first repair the specific Fantasy surfaces directly tied to M50800-13/26 and apply adaptive reclaim where the current Classic renderer already has independent sibling streams. It must not invent a generic cross-family visual system.

## 3. Current implementation mismatch

Primary implementation surface:

`desktopApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/desktop/DesktopClassicRenderer.kt`

### Combat

`appendCombatPages` consumes `classicCombatReferenceLines(plan)` and renders them through one generic `ruledTextArea`.

`classicCombatReferenceLines` joins structured combat fields into prose and then wraps by character count.

Required change:

- preserve logical entry structure;
- reuse the Fantasy base combat semantic grammar as the authority;
- expose separate identity/type, range, bonus, damage/effect and notes columns;
- paginate by complete logical entry groups;
- no generic prose flattening.

### Ellipsis

Fantasy currently uses single-line excerpt/truncation helpers in several surfaces, including `classicSingleLineExcerpt` and fixed-width base/continuation cells.

Required change:

- meaningful semantic names use wrapped lines;
- row/record height may grow;
- helper calls that intentionally abbreviate non-semantic decoration may remain only where they do not destroy identity;
- no acceptance test may merely move the full value into hidden/reference prose while leaving the primary visible identity ellipsized.

### Resources / Options

`appendResourcesPages` computes page count from fixed per-page capacities and always draws both a Resources frame and an Options frame.

Required change:

- while both streams survive, preserve the existing Fantasy split grammar;
- when one stream is exhausted, the surviving table reclaims the page;
- paginate complete Resource/Option semantic groups rather than anonymous continuation rows;
- never reserve an empty sibling panel.

## 4. Planned subphases

### Phase 4A — Fantasy Combat table

- replace prose continuation with structured table rows;
- group wrapped physical rows by logical combat entry;
- preserve all Mara combat identities and field association;
- inspect exact rendered Fantasy continuation.

### Phase 4B — semantic wrap

- inventory all active `classicSingleLineExcerpt`/ellipsis paths affecting meaningful names;
- replace them with bounded wrapped identity rendering;
- add a text-layer regression rejecting unintended `...` in Mara Fantasy semantic content;
- visually inspect first/base pages and continuation pages for clipping/overlap.

### Phase 4C — Fantasy adaptive reclaim

- repair Resources/Options sibling reclaim using Fantasy visual grammar;
- then inspect Inventory and Notes current artifacts for any remaining fixed-scaffold blockers covered by M50800-27;
- only extend scope where exact artifact evidence demonstrates the same owner-reported defect.

## 5. Regression evidence required

- all 26 trait identities preserved;
- all 10 resources, 7 markers and 8 options preserved;
- all 34 inventory identities preserved;
- all 9 note titles preserved;
- all combat identities preserved;
- no meaningful Fantasy identity contains unintended `...`;
- Combat remains structured, not prose;
- exhausted sibling streams do not reserve empty continuation panels;
- no new clipping, overlap or data loss.

## 6. Artifact gate

Phase 4 may receive an internal progression PASS only after:

1. CI passes;
2. exact generated `pc-sheet-populated-template-proofs` is inspected;
3. Fantasy Combat is visually checked against M50800-13;
4. semantic ellipsis is text-scanned and visually checked against M50800-26;
5. Fantasy adaptive packing is checked against M50800-27;
6. all relevant semantic identities remain present.

M50800-13/26/27 remain formally OPEN in the master matrix until final exact-candidate acceptance.

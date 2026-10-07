# Phase 3 — Mara renderer repair implementation

**Date:** 2026-09-30 (Chile local time)  
**Phase:** 3 — implementation  
**Branch:** `repair/mara-phase3-semantic-flow-compositor`  
**Base main:** `c4e65955a5f58f88e0b03807b4616a3f8a9323a8`  
**Status:** ACTIVE / IMPLEMENTATION AUTHORIZED  
**Owner authority:** Phase-2 final burn-down in `docs/checkpoints/2026-09-30_MARA_PHASE2_EXISTING_REPAIR_AUDIT.md`.

## Non-negotiable authority

Phase 3 must implement the authoritative final disposition for M50800-01…32. Earlier provisional questions/classifications in the Phase-2 checkpoint are historical only.

Do not reopen owner decisions already resolved unless implementation reveals a genuinely new product ambiguity.

## Implementation order

1. **Shared semantic-flow contracts**
   - semantic record identity + correct module association;
   - bidirectional continuation metadata (`continúa en...` / `proviene de...`);
   - writable tracker semantics with compact current/max snapshot (owner example `____(2)/3`);
   - meaningful-text policy: no semantic ellipsis, bounded readable compression, then wrap/grow/continue.

2. **Native-module geometry/contracts**
   - Custom Statistics source-native attribute module;
   - Traits/Rasgos;
   - Trasfondo/Historia;
   - Combat/Actions;
   - ordinary Equipment;
   - Equipo Especial;
   - Notes;
   - Resources / class-choice modules.

3. **Global Extended-page compositor**
   - inspect all remaining active semantic streams;
   - choose valid family-native layouts;
   - remove exhausted modules from later layouts;
   - permit compatible modules to reclaim/share space only within native geometry constraints;
   - fixed modules repeat rather than resize;
   - Notes overflow remains a whole-native-page exception.

4. **Family renderer integration**
   - Custom v1;
   - Custom v2 · Atributo;
   - Custom v2 · Habilidad;
   - Fantasy;
   - Android/Desktop parity.

5. **Regression + proof**
   - semantic completeness and correct module association;
   - no stale underlay;
   - no semantic ellipsis or silent dropped overflow;
   - all-four-family real Mara generation;
   - actual rendered PDF inspection against M50800-01…32.

6. **Exact owner candidate gate**
   - unique versionName/versionCode/build identity; never reuse 50800;
   - explicit owner-facing APK filename;
   - exact source commit → workflow/build → APK artifact → APK SHA-256;
   - exact four Mara proof hashes;
   - acceptance matrix marked FIXED / OPEN / CHANGED-NEW before owner handoff.

7. **Continuous recovery / continuation prompt**
   - maintain a ready-to-copy timeout/connection/server-interruption recovery prompt throughout Phase 3, not only at closure;
   - canonical active prompt: `docs/checkpoints/2026-10-06_MARA_PHASE3_CONTINUATION_RECOVERY_PROMPT.md`;
   - it must force canonical repo re-anchoring, preserve final/latest owner decisions over superseded provisional text, verify partially-completed operations before repeating them, and distinguish validated functional HEADs from red functional heads and documentation-only commits;
   - at Phase-3 closure, advance this prompt into the next-phase recovery route without losing references.

## Cross-cutting owner rules

- Native/source reuse before reconstruction.
- Custom v1/v2 native bicolour row physical reference: 7 mm (~19.84 pt).
- Meaningful semantic identities never use `...`.
- Wrapped lines for one semantic label use uniform typography/scale.
- Ordinary Equipment visible content = quantity + full identity only; no weight, `Consumible`, `Equipado`, status prose or description.
- Equipo Especial uses the native fixed module; repeat it when more capacity is needed. Custom-location fallback may be built from native pieces with matching font/size/strokes/fills/spacing, never overprinting canonical labels.
- Notes overflow uses the full native Notes page.
- Split semantic content uses bidirectional explicit navigation.
- Writable use/ammunition trackers remain hand-editable while compactly preserving runtime current/max state.
- Page count is diagnostic, not a target.

## First implementation package

Start with shared semantic-flow contracts and tests. Do not begin by patching individual Mara pages.

The first package should make renderer-independent concepts explicit and testable before visual integration:
- semantic module identity;
- continuation links;
- compact writable tracker state;
- semantic text fit/overflow decisions.

## Owner boundary

Routine technical decomposition, tests, branch commits, CI and PR mechanics are autonomous while scope remains within this checkpoint.

Stop only for:
- a genuinely new owner/product decision not resolved by Phase 2;
- material regression/contradiction in native source evidence;
- security/cost/provider boundary;
- exact owner visual QA handoff after internal candidate acceptance.

## Continuity and candidate-delivery directive

Owner objective: **converge to a new owner-facing QA candidate version**.

Within already-authorized Phase-3 scope, routine technical progression is autonomous:

- work one coherent material front at a time;
- after green CI plus whatever real proof the criterion requires, record the closure and continue to the next authorized front;
- do not stop after every green package merely to request routine approval;
- anti-loop guardrails remain mandatory and override continued commit churn;
- exact owner candidate creation remains blocked until all blocking Phase-3 acceptance gates pass.

The candidate must receive a new unique `versionName` / `versionCode`; `0.5.0-preqa.8 / 50800` is permanently non-reusable.

## Gate to Phase 4 / next phase

Phase 3 is not closed until the exact candidate has passed the internal all-four-family acceptance gate and the owner-facing handoff state is recorded.

At closure, advance the point-7 recovery prompt into the next phase and preserve the exact source/CI/artifact/proof/candidate provenance chain.


## Progress — Package 1 shared semantic-flow contracts

**Status:** COMPLETE / GREEN

Implemented:
- `PcSheetSemanticModule` + stable semantic record references;
- domain-to-module association for Traits, Combat/Actions, Resources, class choices, ordinary/special Equipment, Notes and Spells;
- bidirectional continuation endpoints/markers with numbered Extended destinations;
- paper-first writable tracker state with compact runtime snapshot, e.g. `____(2)/3`;
- shared semantic-text decision order: native size -> readable uniform compression -> uniform wrap -> explicit continuation; semantic ellipsis is not an available disposition;
- ordinary Equipment compact label corrected to quantity + identity only (weight removed);
- Notes semantic helper no longer reroutes ordinary-Equipment metadata into Notes.

Validation:
- Scaffold #4391 / `36785191325`: backend PASS; hosted-database PASS; kotlin PASS.
- Initial runs #4384–#4390 failed only because `LATEST.md` pointed to the new canonical checkpoint without adding it to the Read-first list; route guard was corrected before functional validation.

Next:
- Package 2 native-module contracts/geometry capabilities.


## Progress — Package 2 native-module contracts

**Status:** COMPLETE / GREEN

Implemented shared capability contracts for every semantic module:
- native components vs native rows/columns vs logical rows vs whole-module repetition;
- page-sharing vs full-native-page exclusivity;
- exhausted-module space release;
- explicit prohibition on arbitrary geometry resizing.

Owner-critical locked behaviors:
- Notes = full native page, exclusive;
- Equipo Especial = repeat whole native module;
- ordinary Equipment = native rows/columns;
- Combat/Actions = content-driven logical rows;
- Traits/Background/Resources/Class choices = shareable only through valid family layouts.

Exact measured coordinates remain family-renderer responsibility; these contracts intentionally do not invent dimensions.

Validation:
- Scaffold #4394 / `36785716176`: backend PASS; hosted-database PASS; kotlin PASS.

Next:
- Package 3 constraint-aware Extended-page composer.


## Progress — Package 3 constraint-aware Extended-page composer

**Status:** COMPLETE / GREEN

Implemented a renderer-independent compositor that:
- accepts only family-approved native layout templates supplied by renderers;
- never invents or resizes geometry;
- inspects only active semantic module demands;
- lets exhausted modules disappear instead of reserving placeholders;
- assigns compatible active modules to available native slots;
- treats full-page-exclusive modules (Notes) as owning the complete page;
- returns remaining demand for subsequent pages;
- fails explicitly when no valid family layout can consume remaining content.

The composer optimizes only among already-valid layouts. Utilization is a selector signal, not permission to deform modules or a page-count target.

Validation:
- Scaffold #4396 / `36786217919`: backend PASS; hosted-database PASS; kotlin PASS.

Next:
- selective family-renderer integration, starting with M50800-02 Custom-v1 long-skill generation/wrapping.


## Progress — Family integration checkpoint A

**Status:** REPOSITORY-GREEN / NOT YET OWNER-ACCEPTED

Validated together at implementation head `ff6bc98bb207e021e3345ebc3f4e3bdb12677303`:
- Custom-v1 long custom-stat skill wrapping with uniform source scale;
- shared Custom-v1/v2 custom-attribute identity semantics, including integrated title form such as `ETEr` and context-dependent compact owner keys;
- Custom-v2 Custom Statistics six-row/native-capacity direction;
- isolated Custom-v2 source logo to prevent stale hidden template text;
- Custom-v2 Combat/Actions logical-record rows with content-driven height;
- Fantasy Combat/Actions table-grammar continuation;
- writable trait-use trackers using the compact paper-first snapshot form `____(current)/max`;
- Special Equipment fixed-native-row flow groundwork;
- one-line -> bounded reduction -> two-line centered character-name ribbon fallback;
- record-aware Notes flow with native-page treatment and bidirectional continuation metadata;
- Fantasy Notes restored to the frozen native Notes frame geometry rather than a widened replacement;
- Custom-v2 narrative overflow moved out of Traits into a dedicated `BACKGROUND_STORY` semantic module, including Personality / Flaws / Religion-Faith that previously had no surviving PDF route.

Regression notes:
- page counts are no longer asserted as acceptance targets;
- narrative text is tested for semantic-module association, not merely whole-document presence;
- long Notes stress remains within the Fantasy semantic wrap contract while still forcing continuation.

Validation:
- Scaffold #4485 / `36804840149`: backend PASS; hosted-database PASS; kotlin PASS;
- Android debug APK and PDF proof artifacts uploaded by the workflow.

This is an implementation milestone only. It does **not** close M50800 acceptance: actual candidate PDF inspection, global compositor integration, remaining Inventory/Resources/Traits/ellipsis work, all-four-family Mara generation and exact candidate provenance are still required.

Next:
- repair Custom-v2 Inventory semantics and replace the generic Inventory continuation with native ordinary-Equipment + fixed native Special-Equipment modules.


## Progress — Custom-v1 Traits native-module closure

**Status:** COMPLETE / GREEN / ACTUAL PROOF INSPECTED

Validated functional head:

`c2367e11bef9e92990f0f9efc6e89b13cf07795f`

Bounded CI sequence:
- #4516 / `37543504407`: two open criteria;
- #4517 / `37553188998`: one open criterion;
- #4518 / `37553725881`: backend PASS; hosted-database PASS; kotlin PASS.

Proof:
- artifact `11454232332`;
- digest `sha256:37645e52aab6949580ca0c87cd5567b655aebeb73f12a810a1a2d65d552f4107`;
- actual Custom-v1 Traits, pass2 Traits and Narrative PDFs directly inspected.

Closed behavior:
- native `Otros Rasgos y Atributos` module reused without arbitrary resize;
- true overflow names retain category grouping and stored order;
- exhausted/redundant Traits page allocation is avoided for the focused overflow case;
- source-only metadata on already represented traits no longer creates duplicate continuation detail;
- real continuation metadata keeps source/tracker/recovery context;
- Narrative remains associated to native BACKGROUND_STORY surfaces, not Traits;
- Android/Desktop parity guard passes.

Anti-loop: three cycles, OPEN `2 -> 1 -> 0`, no reopening/oscillation, `LOOP_SUSPECTED = NO`.

Observed but not hidden: visible source/footer text `EXTENSIÓN: NOTAS` remains pending for the later M50800-03 stale-underlay/source-geometry audit. It is not part of this Traits closure.

Next:
- read-only reconciliation of Custom-v2 + Fantasy Combat/Actions against final M50800-12 / M50800-13, current implementation/tests and actual proofs before any new code.


## Progress — Combat/Actions acceptance closure

**Status:** COMPLETE / GREEN / ACTUAL PROOFS INSPECTED

Validated functional head:

`fc6fca3776a88dd53b6e636deddfd9f46edac184`

Current production logic was already in place:
- Custom-v2 logical rows: `c9014c52...` + Android sync `75b88d45...`;
- Fantasy table continuation: `66277458...`, semantic gate `a53409c8...`, geometry `e7dfe811...`, Android sync `f1ed4022...`.

The remaining gap was a durable focused visual proof for Custom-v2, added without production renderer changes.

Validation:
- Scaffold #4519 / `37555121975`: backend PASS; hosted-database PASS; kotlin PASS;
- proof artifact `11454378341`;
- digest `sha256:6496fd013f3cc627e635c5ba37fb77040b637eb04c5360a9868e041533f1c225`.

Actual proof inspection:
- `custom-v2-combat-logical-rows-attribute.pdf` page 6: short row + taller wrapped row, one logical record, full table columns, late token survives, no ellipsis/clipping;
- `custom-v2-combat-logical-rows-ability.pdf` page 6: same;
- `fantasy-canonical-overflow-audit.pdf` page 5: native-style `ARMAS Y ACCIONES — CONTINUACIÓN` table, no prose flattening.

M50800-12 / M50800-13 are demonstrated for the current implementation. One CI acceptance cycle; evidence OPEN `1 -> 0`; `LOOP_SUSPECTED = NO`.

Next:
- read-only reconciliation of Notes M50800-21…24 and bidirectional continuity against current production/tests/latest proofs before any code.


## Progress — Notes acceptance closure

**Status:** COMPLETE / CURRENT PROOFS INSPECTED / NO NEW PRODUCTION WRITE

Current validated functional/proof head:

`fc6fca3776a88dd53b6e636deddfd9f46edac184`

Validation source:
- Scaffold #4519 / `37555121975`: backend PASS; hosted-database PASS; kotlin PASS;
- artifact `11454378341`;
- digest `sha256:6496fd013f3cc627e635c5ba37fb77040b637eb04c5360a9868e041533f1c225`.

Current shared Notes flow already provides record identity, fresh-column packing, oversized-note-only splitting, separators and physical-row bidirectional markers.

Actual proof inspection demonstrated:
- Custom-v1 full native Notes pages, two-column packing, explicit forward/back continuity and emphasized `Nota N — Título`;
- Custom-v2 Attribute/Ability full native Notes pages, base-column exhaustion before Extended Notes, bidirectional continuity and emphasized note records;
- Fantasy complete native Notes geometry with campaign Notes + map/reference companion panels intact, one native Notes writing column consumed per full page, emphasized record identities and bidirectional continuity.

M50800-21 / 22 / 23 / 24 are demonstrated in the current implementation. No new functional commit or CI cycle was necessary.

Next:
- bounded read-only M50800-26 / M50800-29 semantic ellipsis and silent-drop audit against current renderers/tests/#4519 proofs.


## Progress — semantic ellipsis / silent-drop closure

**Status:** COMPLETE / GREEN / ACTUAL PROOFS INSPECTED

Validated production head:

`d4e42818223100d96de32e0fe4a278dc0356153e`

Focused proof head:

`59b039bdccc8d33a79252536c1d5d7bd10470e0e`

Sequence:
- #4520 / `37556848852`: RED after removing manufactured Fantasy ellipsis; exposed real bounded-base overflow.
- #4521 / `37557543660`: GREEN after long-identity two-line fit and explicit Fantasy Combat continuation routing.
- #4522 / `37558318363`: GREEN focused proof; no production renderer change.

Current proof artifact:
- `11455609450`
- `sha256:72cb4aba21442a76aa887605cdcc2a05a117c50475f159a8d5471f268529ec97`

Actual visual proof:
- `fantasy-canonical-overflow-audit.pdf` page 1: complete long character identity, no ellipsis, explicit Combat continuation cue;
- page 5: complete referenced logical Combat row in native table grammar, no clipping/ellipsis;
- key Fantasy / Custom-v1 / Custom-v2 Attribute / Custom-v2 Ability PDFs contain no literal semantic ellipsis.

M50800-26 and M50800-29 are demonstrated. Front CI count = 3; OPEN -> 0; `LOOP_SUSPECTED = NO`.

Next:
- read-only general bidirectional semantic-continuity audit before any further production change.

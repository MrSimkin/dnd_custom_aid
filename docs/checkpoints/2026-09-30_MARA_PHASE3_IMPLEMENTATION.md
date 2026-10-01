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

7. **Next-phase recovery prompt**
   - at Phase-3 closure, provide the owner a ready-to-copy continuation/recovery prompt equivalent to the established timeout/connection-failure prompt;
   - it must force canonical repo re-anchoring, preserve final/latest owner decisions over superseded provisional text, verify partially-completed operations before repeating them, and route into the next phase without losing references.

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

## Gate to Phase 4 / next phase

Phase 3 is not closed until the exact candidate has passed the internal all-four-family acceptance gate and the owner-facing handoff state is recorded.

At closure, provide the point-7 recovery prompt requested by the owner.


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

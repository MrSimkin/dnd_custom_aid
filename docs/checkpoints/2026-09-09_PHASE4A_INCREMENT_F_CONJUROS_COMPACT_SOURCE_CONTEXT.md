# Phase 4A — Increment F: Conjuros compact source context

**Date:** 2026-09-09  
**Branch:** `implementation/phase4a-successor-cycle`  
**Status:** CLEARED FOR CONTINUATION / FINAL TACTILE GRID-DRAG AUDITION DEFERRED  
**Pre-F baseline:** `f7f0389fde4fd3a72ca8f8a547dae38255825266`  
**Original F source:** `0e25fb0a84c2be63a16fee709bd7e96d7469373b`  
**Latest residual F product commit:** `bc4f8a337fa3b335abe73f31ce57dacaca5d038e`  
**Latest residual F tree:** `ca6e8c95c141d07f7d1466e5d21b0a51587205c7`

## 1. Purpose and acceptance boundary

Increment F rebuilt Conjuros around a compact source context and then absorbed two rounds of owner-device findings that exposed broader interaction/editor defects.

F is now **cleared to stop blocking G/H/I implementation**. This does **not** mean final Phase 4A acceptance, tablet acceptance, or final tactile acceptance of the latest multi-column reorder repair. Those items remain part of the later consolidated successor audition.

The owner explicitly wanted to avoid becoming the implementation bottleneck. Therefore:

- G may begin after this checkpoint;
- H and I may follow their normal automated boundaries without an owner stop after each increment;
- the latest large-text clipping, long multiline-field behavior and multi-column drag repair are re-auditioned in the consolidated successor build;
- only genuinely non-inferable product decisions should interrupt the owner before then.

## 2. Compact Conjuros surface

The permanent source-selector row and separate Conjuros toolbar footprint were replaced by one compact collection context.

The resulting behavior is:

- selected source is visible in the collection toolbar;
- selected source shows its own casting ability abbreviation, spell save DC and spell attack modifier;
- `Todos los conjuros` deliberately shows no fake/global casting statistics;
- source selection and source management are transient actions;
- search expands in the same toolbar footprint instead of adding another permanent row;
- a collapsed non-empty search remains indicated;
- filters, ordering and Add remain immediately reachable;
- phone landscape keeps ordinary spell cards visibly usable rather than falling back to tablet UI;
- the Add `+` control received a small owner-requested width increase and was accepted in the second smoke test.

## 3. Typed spellcasting-origin model

The original source UI was too close to a named bucket with an optional class link. Owner clarification established that a source represents the **origin of spellcasting**.

Canonical visible origin order:

1. `Clase`;
2. `Rasgo`;
3. `Raza`;
4. `Trasfondo`;
5. `Dote`;
6. `Objeto`;
7. `Objeto mágico`;
8. `Don`;
9. `Otro`.

Rules:

- `Clase` is the default origin;
- selecting `Paladín` does not require a second mandatory `Nombre: Paladín` field;
- class/trait/feat/gift/item origins use structured existing records where available;
- `Raza` and `Trasfondo` use the character's canonical background data;
- only `Otro` exposes arbitrary custom origin text;
- legacy linked-class sources migrate to `Clase`;
- legacy unlinked sources migrate safely to `Otro`;
- source IDs and spell associations remain stable.

Persistence foundation was introduced in commit `5473d41e7360d83f17f2fb4c97af82a37db6ebf6` and the typed source editor/profile wiring in `6e677fb544b1e92e47641b318f90e968b21b2c9e`.

## 4. Per-source casting authority

Casting mechanics remain owned by the existing per-source `CharacterSpellcastingProfile` authority. F does not create another global or source-local duplicate of derived values.

Normal formulas remain:

- `CD = 8 + modificador de aptitud + competencia + ajuste CD`;
- `Ataque = modificador de aptitud + competencia + ajuste ataque`.

The origin editor exposes:

- casting ability;
- CD adjustment;
- spell-attack adjustment;
- live derived CD;
- live derived spell-attack modifier.

The owner smoke-tested the `Clase -> Paladín` flow and accepted the corrected origin behavior.

## 5. Card actions and one-column reorder

The first physical F retest exposed a private spell drag implementation with a fixed `66.dp` step, multi-step `while` behavior and stale callback risk. It also exposed handle-only initiation and raw Unicode favorite stars.

Repairs:

- whole-card long-press drag uses the shared measured stale-callback-safe primitive;
- the visible three-line drag handle is not required;
- one pointer update can perform at most one logical reorder;
- spell-specific vertical threshold was tuned to `0.65` of measured card height after owner feedback that the previous threshold felt too eager;
- displaced spell cards use a very short placement animation (~75 ms);
- raw `★/☆` controls were replaced app-wide by the shared favorite icon;
- spell favorite/duplicate/remove actions share a compact row rather than determining card height through a vertical action tower.

The owner reported a **great improvement** to the one-column interaction and accepted the action presentation.

## 6. App-wide compact editor repair

F became the proving ground for editor density rather than a Conjuros-only patch.

High-confidence short fields were paired where readable, including Conjuros:

- `Nivel + Tiempo de lanzamiento`;
- `Alcance + Duración`.

The same conservative rule was applied to other short/reference/numeric editor fields where pairing is natural. Long descriptive fields remain full-width.

Window/editor spacing was also tightened through shared spacing primitives rather than blindly shrinking touch targets.

## 7. IME/window model after owner tests

The first repair attempted to keep Save/Cancel fixed while the keyboard was visible. Physical testing showed that this continued to compete with the IME, so that approach was abandoned.

Current shared editor model:

- title, editor fields and `Cancelar / Guardar` participate in one scrollable dialog flow;
- the dialog is constrained by IME/navigation insets;
- actions are reached by scrolling rather than being permanently pinned above the keyboard;
- multiline fields are bounded to a compact visible line range so long text scrolls inside the field instead of expanding until its cursor/end disappears behind the keyboard.

The owner confirmed that Save/Cancel became reachable with the new scroll model, but a very long lowest multiline field still exposed cursor/end coverage. That residual was addressed in the latest F residual repair by bounding the multiline fields.

## 8. Text-size clipping and window scaling

The owner verified that application text/spacing preferences now affect windows, but large text exposed a new defect: only the upper half of some field glyphs/numbers was visible.

Diagnosis: the compact-field repair had used hard `.height(...)` constraints. Font scale grew the glyphs while the field box remained capped.

Latest residual repair:

- compact single-line fields now use `.heightIn(min = ...)` rather than hard `.height(...)`;
- compact height is a minimum, not a clipping ceiling;
- scaled text is allowed to make the field taller when necessary;
- multiline material/description/notes fields use bounded visible line ranges.

This is a cross-app correction because it fixes the shared compact-window pattern rather than introducing a Conjuros-specific font workaround.

## 9. Multi-column reorder residual repair

The second owner smoke test used Equipo to test multiple columns because Conjuros currently does not use the configurable card-column setting. The owner found that multi-column movement had almost no meaningful visual feedback horizontally, vertically or diagonally.

Technical diagnosis confirmed the shared reorder primitive was one-dimensional:

- it consumed only Y movement;
- visual feedback translated only Y;
- it could request only `-1/+1` logical moves.

That cannot correctly describe row-major grids.

Latest residual repair adds a separate measured 2-D grid primitive while preserving the already-improved one-column primitive:

- drag state can carry X and Y visual offsets;
- horizontal, vertical and diagonal pointer movement is visible;
- normalized movement decides the dominant crossed axis;
- one pointer update still performs at most one logical move;
- horizontal grid movement requests an adjacent column;
- vertical grid movement requests an adjacent row in the same column;
- invalid moves and incomplete final-row targets are rejected safely;
- Equipo uses the 2-D primitive only when the configured layout has more than one column;
- one-column Equipo retains the previously auditioned 1-D behavior.

The latest 2-D feel is **not yet owner-accepted**. It is intentionally deferred to the consolidated successor audition instead of blocking G/H/I.

## 10. App-wide repair evidence

Earlier F repair work also established these repository-wide invariants:

- zero raw Unicode favorite controls in Android character UI;
- zero explicit Android `fontSize = ...` overrides found by the residual audit;
- zero remaining text `Duplicar` controls in the audited character UI;
- shared numeric spell-level normalization remains canonical (`05 -> 5` / one digit);
- source/context explanatory copy uses the global contextual-help system.

A density audit found additional raw padding declarations. They are not mass-rewritten automatically because some belong to interactive touch geometry and must not be reduced merely to make the UI look denser.

## 11. Automated evidence

Important automated boundaries during F:

- original F integrated workflow `34376169597` — SUCCESS, followed by failed owner retest;
- repaired integrated workflow `34392690411` — SUCCESS;
- clean normal full workflow `34401858533` — SUCCESS on the pre-final-residual product tree;
- latest residual fix focused workflow `34408296702` — SUCCESS:
  - residual source invariants: PASS;
  - `:shared:desktopTest`: PASS;
  - `:androidApp:compileDebugKotlin`: PASS;
  - helper cleanup: PASS;
  - resulting product commit: `bc4f8a337fa3b335abe73f31ce57dacaca5d038e`;
  - resulting tree: `ca6e8c95c141d07f7d1466e5d21b0a51587205c7`.

The documentation checkpoint following this residual source commit is used to trigger the normal full repository gate against the same product code. Exact latest full-gate evidence is recorded in `docs/checkpoints/LATEST.md` once available.

## 12. Owner smoke-test result

Second quick Redmi Note 11 Pro 5G smoke test established:

- typed origin / Paladín behavior: OK;
- Save/Cancel scroll reachability with keyboard: OK;
- `+` width: accepted;
- portrait/landscape Conjuros sanity: OK;
- remaining large-text glyph clipping: identified and repaired afterward;
- remaining very-long multiline-field cursor/end visibility issue: identified and repaired afterward;
- multi-column reorder feedback deficiency: identified in Equipo and repaired afterward through the new 2-D grid primitive.

The owner explicitly requested that these residual fixes **not create another immediate blocking retest**.

## 13. Continuation rule

Increment F is cleared for continuation into G.

Do **not** infer:

- final F tactile/grid-drag acceptance;
- final Phase 4A acceptance;
- tablet acceptance;
- release readiness.

Recheck the latest residual editor clipping/long-text behavior and 2-D grid drag during the consolidated successor audition after G/H/I integration. Until then, continue implementation autonomously unless a genuinely new product decision cannot be resolved from the controlling documents and established owner rules.

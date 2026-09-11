# Phase 4A — preqa.8 point-by-point repair decisions

**Opened:** 2026-09-11  
**Status:** IN PROGRESS — OWNER DESIGN RECONCILIATION  
**Branch:** `implementation/phase4a-successor-cycle`  
**QA source:** `0.4.0-preqa.8` / build `40800` / debug  
**Controlling QA checkpoint:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`  
**Implementation authorization:** NOT YET — discussion/consolidation first

## Purpose

This checkpoint is the durable consolidation record for the acceptance-repair decisions produced from the `40800` owner QA.

The owner explicitly requires the findings to be discussed **one point at a time**, not grouped into repair packages during the design discussion.

For each point:

1. inspect the exact QA finding and relevant current implementation/context;
2. discuss the intended behavior in enough detail to remove implementation ambiguity;
3. do not broaden the discussion into unrelated findings;
4. when the owner explicitly closes the point, record the resulting decision here;
5. only then proceed to the next point.

A closed point may identify shared/transversal implementation consequences, but that does not merge its design discussion with other open points.

## Consolidation format for each closed point

Each closed item must record:

- QA point / observed problem;
- owner-approved target behavior;
- important interaction/layout/state rules;
- phone/tablet scope where relevant;
- explicit non-goals or rejected alternatives where useful;
- automated/regression boundary needed to prove the repair;
- status: `CLOSED / READY FOR REPAIR SPEC`.

Do not mark implementation complete in this document merely because the design decision is closed.

## Closed decisions

### P1 — Canonical HP state across General / Combate / shared surfaces

**QA problem:** `preqa.8 / 40800` allowed HP shown/edited in General and HP shown in Combate to diverge. This violated the protected `one datum / one canonical state` rule.

**Owner-approved target behavior:**

- `HP actual`, `HP máximo` and `HP temporal` each have one authoritative character state shared by General, Combate and every other projection/operation.
- A direct `HP actual` edit in General is an exact administrative/set operation. It does **not** simulate receiving damage or healing and therefore does not invoke combat damage/healing semantics.
- Editing `HP máximo` changes only the maximum. Increasing maximum HP does not silently heal the character.
- Invariant: `HP actual` may never exceed `HP máximo`. If maximum HP is reduced below the current value, current HP is clamped down to the new maximum.
- `HP temporal` is likewise one canonical value. Direct editing sets that value exactly; operational damage handling is separate and will apply the appropriate temporary-HP rule.
- Once an HP change is committed, all visible projections update immediately from the same authoritative state. No tab switch, reopen, refresh or duplicate save cycle is required.
- A text field may temporarily contain an uncommitted editing draft while the owner is typing, but after commit there is only one canonical value.

**Explicitly not decided in P1:**

- the visual/interaction design of the frequent-combat `Daño / Curar` operation;
- death-save / unconscious-state presentation and any automatic coupling to HP transitions.

Those are separate discussion points and must not be inferred from this state decision.

**Phone/tablet scope:** shared state rule applies to every phone/tablet surface using HP.

**Regression boundary:** automated coverage must prove cross-surface propagation from one authoritative HP state, maximum-HP clamping, no silent healing when maximum increases, and canonical temporary-HP projection. Independent per-screen persistence tests are insufficient.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

### P2 — `Daño / Curar` high-frequency combat interaction

**QA problem:** `preqa.8 / 40800` routes a frequent in-combat HP operation through a large editor/dialog, amount entry and explicit save. The owner rejected this as too slow and intrusive for normal combat use.

**Owner-approved target behavior:**

- Combate presents one compact operational HP block containing the visible current/max HP, temporary HP and the frequent `Daño / Curar` controls.
- The primary interaction is a compact three-part operation row: `Daño` action, one shared numeric amount field, and `Curar` action.
- The owner enters the amount and taps `Daño` or `Curar`; the operation applies immediately. There is no additional Save step and no confirmation dialog in the normal path.
- After application, the amount field clears and is ready for the next operation.
- Damage/healing uses the canonical P1 HP state and normal HP semantics underneath, including temporary-HP absorption for damage and maximum-HP capping for healing.
- Feedback is intentionally minimal and non-intrusive: affected HP display box(es) receive only a brief/subtle visual glow or pulse. There is no snackbar/toast-style operation message required.
- The visual feedback follows what actually changed: e.g. damage fully absorbed by temporary HP highlights only temporary HP; spillover damage may highlight temporary HP and current HP; healing highlights current HP when it changes.
- There is **no Undo operation** for damage/healing. If the owner enters an incorrect operation, correction is performed explicitly through the ordinary game-facing operations (e.g. heal back damage and/or restore temporary HP) or through an explicit exact-state correction where appropriate.
- Combat also provides a secondary exact-correction path for current HP. Tapping/activating the visible current/max HP display may expose an explicit `Establecer PV` correction action. This is secondary to `Daño / Curar` and must be clearly presented as exact state correction, not as another form of healing.
- `Establecer PV` uses the same canonical state and P1 invariants; it does not invoke damage/healing semantics.

**Explicitly not decided in P2:**

- death-save / unconscious-state presentation or automatic coupling to HP transitions;
- any broader redesign of the full Combate fixed/sticky region beyond keeping this HP operation compact;
- a separate richer workflow for granting/replacing temporary HP, beyond preserving canonical temporary-HP state and normal damage interaction.

**Phone/tablet scope:** the same high-frequency interaction principle applies to phone and tablet wherever this shared HP operation is exposed. Layout may adapt to available width, but it must remain compact and direct.

**Regression boundary:** automated/integration coverage must prove immediate damage/healing application without a save dialog, correct temp-HP-first damage behavior, healing capped by max HP, amount-field reset, exact-correction separation from damage/healing semantics, canonical cross-surface propagation, and affected-state visual-feedback triggering. No Undo control should be exposed.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

### P3 — Custom Habilidades geometry and custom Atributos participation

**QA problem:** `preqa.8 / 40800` renders custom skills with visibly different margin/padding from ordinary skills in both `Por habilidades` and `Por atributo`, despite the intended inline behavior.

**Owner-approved target behavior for custom Habilidades:**

- A custom Habilidad uses the same row geometry, alignment, padding, spacing, controls and interaction model as an ordinary Habilidad.
- This applies in both `Por habilidades` and `Por atributo` grouping.
- Italicized naming is the intended visual distinction for a custom Habilidad; it must not receive a special card, badge, indentation, background, row height or container geometry merely because it is custom.
- A custom Habilidad is therefore a normal participant in the skill system, not a visually separate secondary object.

**Owner clarification discovered during P3 — custom Atributos:**

This was not an observed `40800` QA failure and had not previously been explicitly discussed/tested. The owner confirms it as required Player behavior rather than leaving it as an assumption:

- custom Atributos follow the same native-participant principle as the six basic Atributos: use the same interaction/visual grammar rather than a special custom-only container style;
- custom Atributos are additional to the six basic Atributos and may occupy additional row(s) beyond the standard six rather than displacing or masquerading as one of them;
- custom Habilidades may be assigned to a custom Atributo;
- when the Habilidades view is grouped `Por atributo`, those custom Habilidades must group under their assigned custom Atributo exactly as standard skills group under standard Atributos;
- the data model/UI must therefore not assume that the parent Atributo of a Habilidad can only be one of the six basic attributes.

This clarification is part of the acceptance boundary even though it was surfaced outside the original QA checklist, because leaving it implicit would create an avoidable implementation ambiguity.

**Phone/tablet scope:** identical semantic behavior across phone and tablet; responsive wrapping may differ, but custom versus standard geometry must remain coherent within each layout.

**Regression boundary:** visual/integration coverage must compare ordinary and custom Habilidad geometry in both grouping modes. Additional coverage must prove creation/display of custom Atributos beyond the six basics and assignment/grouping of custom Habilidades under a custom Atributo. This behavior must be tested rather than merely assumed.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

### P4 — Attack structured-damage editor UX

**QA problem:** `preqa.8 / 40800` supports structured attack damage and rolling, but the interaction requires manually typing expressions such as `1d8` and presents fields such as `1d8` / `+3` in a way that reads like examples rather than direct semantic controls. The owner rejected that interaction even though the underlying model works.

**Owner-approved target behavior:**

- Attack damage is edited as a list of structured damage components rather than as one opaque raw formula.
- The ordinary direct component grammar is compact and selector-oriented, conceptually: `[cantidad] d [caras] [modificador plano] [tipo de daño]`, with appropriate omission of parts that are not used.
- Dice quantity and die size are separate direct controls. Normal attacks must not require typing `2d6` or similar expressions.
- Standard die sizes are directly selectable, and the die-size control also includes `Otro`, allowing unusual positive die sizes such as `d3`, `d5` or other homebrew dice.
- Dice quantity is not artificially limited to a small preset ceiling; it is a positive integer when dice are present.
- A component may also contain only flat damage, with no die at all. Flat-only components are valid first-class components rather than a workaround.
- Flat modifiers may be positive, zero or negative.
- Multiple components may use the same damage type and remain separate. Example: `1d8+3 Cortante +3 Fuego +1d4 Fuego` is represented as three structured components, not collapsed into one typed expression.
- Damage type uses a direct selector for standard damage types and includes `Otro` for custom/homebrew types.
- Multiple structured components remain independently represented but the saved attack provides a convenient roll-all-damage action.
- The attack card displays saved damage compactly and can roll configured damage without reopening the editor; editing and rolling are separate interactions.

**Signed component rule / unusual arithmetic:**

- Dice components themselves may be positive **or negative** in the overall damage expression. This supports cases such as a cursed weapon with `1d8 - 1d4`.
- The sign belongs to the component arithmetic; the quantity of dice and number of faces remain positive values.
- Negative dice therefore do not require a raw-expression escape hatch. A component can explicitly contribute `-1d4` just as another contributes `+1d8`.
- Flat contributions likewise support positive or negative values.
- The structured list must therefore be capable of expressing combinations such as `1d8 - 1d4`, `2d6 - 3`, `-1d4`, `+3 Fuego`, or `-2 Radiante` without falling back to free-form formula text.

**Roll-result semantics:**

- The roller reports the arithmetic damage result exactly as produced by the configured signed components.
- A negative total is **not** automatically reinterpreted as healing or relabelled by the application.
- Example: if `1d8 - 1d4` evaluates to `-2`, the result is presented simply as `Daño: -2`.
- The app remains mechanically neutral at this layer; any narrative or game-rule interpretation of a negative damage result is left to the user/table.

**Explicit non-goal / rejected alternative:**

- No general raw `Expresión personalizada` text mode is required merely to support unusual dice arithmetic. The accepted structured-component model is intended to cover those cases directly.
- Do not reduce support back to only `NdX + flat` with positive dice components; signed dice components are part of the accepted grammar.

**Phone/tablet scope:** the same structured model and editing capabilities apply on phone and tablet. Layout may adapt, but the interaction must remain direct and compact rather than reverting to manual expression typing on narrower surfaces.

**Regression boundary:** automated/integration coverage must prove direct quantity/die selection, `Otro` die sizes such as `d3`/`d5`, flat-only components, positive and negative flat values, positive and negative dice components, repeated damage types across separate components, custom damage type through `Otro`, compact saved rendering, roll-all behavior, and exact arithmetic reporting including negative totals such as `Daño: -2`.

**Status:** `CLOSED / READY FOR REPAIR SPEC`.

## Current discussion point

**P5 — Combate fixed quick-reference footprint.**

At normal `Compactación de espacios = 100%`, the fixed Combate reference region consumed roughly 60% of the phone portrait viewport in `40800`, with an even more severe effect in landscape. The owner explicitly rejected treating a lower compactness setting such as 40% as the solution. P5 must determine what should remain persistently visible, what can collapse/scroll/adapt, and how the fixed region should react to available vertical space without yet broadening into the separate global phone-landscape/sticky-policy finding.

## Remaining boundary

After all required QA points are individually closed:

- convert the consolidated decisions into one bounded Phase 4A acceptance-repair implementation plan;
- implement only the accepted scope;
- run strengthened automated validation for the affected boundaries;
- produce the next monotonic successor QA build;
- perform targeted phone regression/acceptance retest;
- then perform physical Player tablet portrait and landscape QA;
- freeze a replacement formal M6 candidate only after the repaired baseline is owner-acceptable;
- explicitly close Phase 4A before any DM implementation begins.

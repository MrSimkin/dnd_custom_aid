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

## Current discussion point

**P2 — `Daño / Curar` high-frequency combat interaction.**

The state semantics underneath the operation are constrained by P1, but the owner has explicitly requested discussion of how `Daño / Curar` should actually look and behave. The current large editor/save workflow is not approved.

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
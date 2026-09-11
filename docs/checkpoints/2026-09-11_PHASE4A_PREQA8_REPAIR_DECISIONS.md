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

_None yet._

## Current discussion point

Not yet recorded at file creation. The next session begins with the first unresolved `40800` QA finding selected for detailed owner discussion.

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

# Phase 4A — preqa.8 P17 tablet QA gate — CLOSED

**Date:** 2026-09-11
**Branch:** `implementation/phase4a-successor-cycle`
**Status:** CLOSED / DESIGN DECISION ONLY / NO IMPLEMENTATION AUTHORIZED

## Context

Physical tablet QA was deferred during the `0.4.0-preqa.8 / 40800` owner-phone pass because the phone exposed multiple shared/systemic failures that were expected to invalidate or merely duplicate tablet evidence. The owner clarified that this must not become a blanket rule requiring a full phone PASS before any tablet QA can begin.

The controlling principle is that tablet QA is both an acceptance activity and, when useful, a diagnostic activity for responsive behavior.

## Final decisions

### P17.1 — Tablet QA gate is based on failure type, not binary phone PASS/FAIL

Physical tablet QA may proceed once the phone build is stable enough to provide meaningful evidence, even if the phone still has known bounded defects.

Defer/stop tablet QA only when an unresolved shared/systemic primitive failure is severe enough that tablet results would be predictably invalid, redundant, or dominated by the same broken foundation.

Examples of hard shared/systemic failures that justify stopping before tablet QA include:

- shared layout primitive fundamentally broken;
- shared reorder interaction fundamentally broken across collections;
- canonical HP/state authority broken;
- shared editor primitive unusable;
- P16 adaptive-height/persistent-footprint policy fundamentally failing;
- app-wide navigation or state corruption.

A phone **soft PASS** or **soft FAIL** is sufficient to proceed to tablet QA when remaining defects are bounded, local, minor, or otherwise do not invalidate the tablet exercise.

Examples that should not automatically block tablet QA include:

- minor visual defects;
- local spacing issues;
- one module-specific UX problem;
- bounded presentation imperfections;
- known local bugs whose shared foundation is otherwise usable;
- phone-specific issues that may not reproduce on tablet.

If it is uncertain whether a defect is phone-specific or responsive/shared, tablet QA should generally proceed because tablet evidence can help classify the defect.

### P17.2 — No tablet PASS or FAIL inferred from phone QA

Phone findings may define responsive/shared repair requirements, but the physical tablet remains formally **untested / deferred** until actual tablet QA occurs.

Do not infer tablet acceptance from phone success, and do not infer tablet failure solely from a phone defect unless the defect is a demonstrated shared/systemic root cause. Even then, the tablet remains physically untested rather than falsely recorded as having been exercised.

### P17.3 — Tablet remains a first-class implementation target before physical QA

Shared repairs must be implemented responsively for phone and tablet rather than as "phone now, tablet later" patches.

This includes, where applicable:

- shared state authority;
- shared interaction primitives;
- drag reorder;
- editor/dialog behavior;
- density/spacing behavior;
- P16 vertical-space/persistent-footprint policy;
- responsive PC Settings and Supercompact layouts;
- other shared Player surfaces affected by P1–P16.

Automated/layout-level coverage should exercise tablet-relevant states where practical. Physical tablet QA then verifies the real-device result.

### P17.4 — Later tablet physical QA is representative, not a mechanical replay of every phone step

Minimum representative tablet coverage should include:

- portrait and landscape;
- navigation and adaptive layout;
- Combate / P5 / P16;
- Conjuros sticky behavior;
- representative editor/IME behavior under P9;
- P6 reorder behavior;
- PC Settings and Application Settings responsiveness;
- P15 Supercompact;
- P14 Table Mode;
- representative larger application text and density behavior;
- persistence/reopen;
- at least one cross-surface sanity check for canonical shared state such as HP.

If a tablet-specific failure appears, expand testing around that failure rather than mechanically repeating unrelated phone tests.

### P17.5 — Tablet evidence may reopen a relevant repair point

A physical tablet failure may reopen the relevant P1–P16 repair decision when new evidence demonstrates that the accepted responsive/shared contract is not actually satisfied.

Do not invent speculative tablet-only redesigns before such evidence exists. Follow the accepted shared responsive contracts first, then adjust only when real tablet evidence warrants it.

## Relationship to existing QA checkpoint

This decision refines, but does not contradict, the original `preqa.8 / 40800` tablet deferral. That deferral was appropriate because the phone had exposed enough hard shared/systemic failures to make immediate tablet QA inefficient.

The durable rule going forward is therefore:

> **Hard shared/systemic primitive failure -> stop/defer tablet QA.**
>
> **Soft PASS / soft FAIL / bounded defects -> proceed to tablet QA.**
>
> **Uncertain phone-specific vs responsive issue -> tablet QA may be useful diagnostic evidence, so proceed when the shared foundation is otherwise usable.**

## Closure state

P17 is CLOSED.

With P17 closed, the owner/assistant discussion of repair/design points **P1–P17 is complete**. This closes the design-decision phase only.

**No repair implementation is authorized by this checkpoint.**

Implementation remains blocked until the owner explicitly authorizes the repair pass.

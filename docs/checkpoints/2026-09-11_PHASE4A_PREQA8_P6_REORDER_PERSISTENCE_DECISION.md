# Phase 4A pre-QA.8 — P6 reorder persistence decision

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Parent audit:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P6_FULL_REORDER_SUBSYSTEM_AUDIT.md`  
**Parent audit commit:** `8d0e92e8692511428ab517d262f2b64de1861b96`

## Status

**DECISION ACCEPTED / CONTROLLING FOR P6 REBUILD DESIGN.**

This checkpoint records the owner decision resolving the persistence-boundary ambiguity identified by the full reorder subsystem audit.

No implementation is authorized by this document.

---

## Decision

Reorder follows the ordinary structural-edit transaction model.

A successful drop:

- immediately updates the current structural **draft** order;
- does **not** write a special reorder-only autosave directly to durable repository state;
- requires no separate reorder `Done` action;
- participates in the normal character-level structural **Save / Cancel** boundary.

Therefore:

- **Save** persists the reordered draft together with the character's other structural edits;
- **Cancel** restores the last persisted structural state, including the previous order;
- reorder is not an exception that can become durable while sibling structural edits remain discardable.

This replaces any earlier wording of “persist immediately on drop” that could be interpreted as bypassing the structural draft. For P6, “commit on drop” means **commit to the current structural draft exactly once on successful drop**, not immediate durable repository persistence.

---

## Required state semantics

1. Drag preview is transient interaction state only.
2. While dragging, canonical structural draft order is not repeatedly rewritten.
3. Successful drop applies one final ordered result to the structural draft.
4. Cancelled/interrupted drag applies no order change to the structural draft.
5. Character Save durably persists the final draft order.
6. Character Cancel discards the reorder together with other pending structural changes.
7. Table Mode structural-dirty detection must treat a dropped-but-unsaved reorder as a structural pending change.
8. Operational actions remain independent and must not accidentally commit or discard structural reorder state.

---

## Architectural consequence

The universal reorder contract should expose a final stable-ID order/subset-order result at the drop boundary. Domain code should apply that result once to the structural draft rather than receiving repeated gesture-derived `+1/-1` mutations.

This supports the disposition already established by the parent audit:

- retire the legacy threshold/step orchestration;
- preserve and harden the spatial/session model;
- preserve good domain normalization/grouping/sorting logic;
- replace offset-based reorder APIs as the primary UI/domain integration contract where necessary.

---

## Audit implication

**FULL APP AUDIT REQUIRED** remains in force for P6.

Every Player surface that permits manual ordering must be checked against this same transaction model, including cancellation, Save/Cancel integration, search/filter eligibility, Table Mode restrictions, persistence after reopen, and any conditional-module reorder path.

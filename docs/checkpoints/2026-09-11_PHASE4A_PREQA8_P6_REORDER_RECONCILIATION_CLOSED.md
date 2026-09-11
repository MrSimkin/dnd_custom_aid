# Phase 4A pre-QA.8 — P6 reorder reconciliation closed

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Full audit:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P6_FULL_REORDER_SUBSYSTEM_AUDIT.md`  
**Audit commit:** `8d0e92e8692511428ab517d262f2b64de1861b96`  
**Persistence decision:** `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P6_REORDER_PERSISTENCE_DECISION.md`  
**Persistence decision commit:** `201520f6551ced37097176b6465c626927763957`

## Status

**P6 DESIGN / AUDIT RECONCILIATION CLOSED.**  
**READY FOR REPAIR / REBUILD SPECIFICATION.**  
**IMPLEMENTATION NOT AUTHORIZED.**  
**FULL APP AUDIT REQUIRED.**

---

## Controlling product contract

P6 manual reorder uses one coherent app-wide interaction and state model:

- direct long-press + drag on the item/card body;
- child controls retain their normal actions and do not initiate drag;
- no dedicated reorder mode, no mandatory drag handle, and no `Reordenar / Listo` workflow;
- movement follows the real rendered layout, including genuine two-dimensional layouts when present;
- reorder is available only for Manual ordering and is disabled while search/filter makes the visible collection non-canonical for manual ordering;
- active item receives subtle lift/follow-pointer feedback; neighboring items reflow naturally; haptics are subtle and semantically correct;
- one drag session owns transient preview state;
- no canonical structural draft mutation occurs repeatedly during pointer movement;
- a successful drop applies exactly one final stable-ID order result to the structural draft;
- drag cancellation/interruption restores the pre-drag preview and applies no structural order change;
- no reorder-specific autosave exception exists;
- normal character Save/Cancel remains the durable persistence boundary;
- a dropped-but-unsaved reorder is a pending structural edit for Table Mode dirty-state purposes;
- accessibility must expose equivalent semantic reorder capability without restoring permanent visible arrow-button chrome.

---

## Architectural disposition

### Rebuild / retire

- legacy threshold/step reorder gesture stack;
- repeated `+1/-1` gesture-to-domain mutation orchestration;
- legacy drop-indicator / feedback semantics tied to that stack;
- per-screen reorder modes and one-column reorder detours where they exist;
- any caller that mutates canonical structural draft order during drag rather than at drop.

### Keep and harden

- spatial/session reorder concept and pure geometry/order policy;
- Equipment and PC Settings as directionally correct migrated consumers;
- stable-ID domain identity;
- existing useful sorting, filtering, grouping, normalization, Favorite and presentation rules.

### Replace/refactor as shared infrastructure

- the current non-lazy `FlowRow` spatial host must not become the universal host for potentially large collections;
- use one shared reorder session/state contract with layout-specific adapters appropriate to bounded flow/grid and scalable lazy collections;
- domain integration should accept final stable-ID order/subset-order results rather than repeated gesture-relative offsets.

---

## Required implementation audit scope

The rebuild specification and later implementation must inventory and reconcile every Player manual-order surface, including at minimum:

- Combat collections;
- Traits;
- Spells;
- Notes where manual ordering exists;
- Equipment;
- PC Settings tab order;
- Artificer-specific collections;
- Forms;
- Companions;
- Techniques;
- Metamagic;
- Pacts;
- conditional/special modules and any other manual-order collection found during implementation inventory.

For every surface, verify:

- same direct-drag grammar;
- correct real-layout target resolution;
- child-control gesture arbitration;
- scroll and edge-auto-scroll behavior where applicable;
- one preview session and one drop commit;
- cancellation safety;
- Save/Cancel integration;
- reopen persistence after Save;
- no persistence after Cancel;
- Manual-vs-sorted eligibility;
- search/filter disabling;
- Table Mode structural lock;
- haptic/visual feedback;
- accessibility semantics;
- lifecycle interruption and stale-state handling;
- no surviving bypass through legacy APIs.

---

## Testing bar

Pure list/geometry unit tests are necessary but insufficient. The repair specification must require tests for the actual interaction/state machine and persistence boundary, including pickup, preview, reflow, scroll, successful drop, cancellation, structural dirty state, Save, Cancel, Table Mode, gesture conflicts, and representative one- and multi-column consumers.

P6 should not return to owner QA until the legacy path is retired from active Player callers and the full-app reorder audit passes against the unified contract.

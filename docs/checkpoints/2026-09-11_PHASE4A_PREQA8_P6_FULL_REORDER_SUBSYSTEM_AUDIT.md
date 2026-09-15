# Phase 4A pre-QA.8 — P6 full reorder subsystem audit

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Audit baseline:** `36adff86c41d279e263c706678cec139320c0860` (`repair: replace Equipment reorder with spatial grid engine`)  
**Scope:** full general audit of Player reorder behavior and its supporting architecture. Vivaldi may remain a useful interaction reference, but it is **not** the audit standard.

## Status

**P6 remains OPEN / NOT QA READY.**

**FULL APP AUDIT REQUIRED.** Reorder is a shared interaction/state primitive used across many Player surfaces. The current branch contains two materially different reorder architectures at the same time, so passing behavior on Equipment or PC Settings cannot certify the rest of the app.

No implementation is authorized by this document. This checkpoint records audit findings, component disposition, and the remaining state/persistence decision that must be reconciled before the rebuild can be considered coherent.

---

## 1. Executive conclusion

The reorder subsystem is currently a **partially completed architectural migration**, not a single finished subsystem with isolated bugs.

Two reorder models coexist:

1. **New spatial/session model**
   - collection-owned drag session;
   - rendered geometry drives target selection;
   - transient preview order while dragging;
   - one commit at successful drop;
   - cancel restores the pre-drag order;
   - active card visually follows the pointer;
   - edge auto-scroll support;
   - currently used by Equipment and PC Settings tab order.

2. **Legacy threshold/step model**
   - gesture accumulation is converted into repeated logical `+1/-1` moves;
   - several callers mutate their structural draft during the gesture rather than at a single drop boundary;
   - old drag-feedback/drop-indicator primitives remain active;
   - some surfaces still expose a separate `Reordenar` / `Listo` mode and/or collapse to one column;
   - cancellation and haptic semantics differ from the new model.

This split is itself a blocker. A shared user interaction must not have two competing state machines and transaction models depending on the screen.

### Primary disposition

- **Legacy reorder UI/orchestration path: FULL REBUILD / RETIRE.** Do not tune thresholds or continue adding exceptions.
- **New spatial state core: KEEP, but HARDEN / REFACTOR before app-wide adoption.** Its conceptual state boundary is substantially better, but it is not yet robust enough to certify as the universal primitive.
- **Current `FlowRow` spatial grid host: DO NOT adopt as the universal host.** Rebuild/replace the hosting layer for scalable lazy/adaptive collections; a bounded-small-collection variant may remain if justified.
- **Shared pure spatial order policy: KEEP and expand tests.**
- **Domain normalization, sorting, filtering, grouping, and stable-ID rules: KEEP.** They are generally useful and independent of gesture mechanics.
- **Offset-based reorder domain APIs: DEPRECATE as the UI contract.** Add canonical final-ID-order / reordered-subset application APIs so the UI can commit one final order instead of synthesizing repeated `+1/-1` moves.
- **Already migrated consumers (Equipment, PC Settings tab order): KEEP their direction, then harden them against the findings below.**

---

## 2. Audit standard

This audit evaluates the subsystem generally against:

- correctness and state integrity;
- one coherent interaction grammar across the Player app;
- ownership of transient versus canonical state;
- persistence semantics;
- cancellation safety;
- gesture arbitration with scrolling and child controls;
- rendered-layout fidelity in one and multiple columns;
- scalability and composition/layout cost;
- accessibility;
- haptic/visual feedback correctness;
- lifecycle and mid-gesture state changes;
- search/filter/manual-order eligibility;
- test coverage;
- obsolete/dead compatibility code;
- maintainability and whether a component should be repaired or rebuilt.

The previously accepted P6 product rules remain controlling unless explicitly reconciled later: direct long-press drag, no dedicated reorder mode, real layout movement, Manual-order eligibility, search/filter disabling, ordinary child actions remaining usable, and a single successful-drop transaction boundary.

---

## 3. Component disposition matrix

| Component / responsibility | Audit result | Disposition |
|---|---|---|
| `CharacterSpatialReorderStateV4` | Correct conceptual direction: collection-owned transient session, preview, cancel rollback, one drop commit, geometry registration, auto-scroll hook | **KEEP + HARDEN / REFACTOR** |
| `characterSpatialReorderBoundsV4` / pickup modifier / visual modifier | Useful separation of geometry, pickup region, and lifted rendering, but API naming and lifecycle/gesture semantics need tightening | **KEEP CONCEPT, REFACTOR API** |
| `CharacterSpatialGridV4` (`FlowRow` + lookahead/animation) | Suitable only for bounded collections; non-lazy composition/layout is risky for large spell/equipment/module collections and should not become universal | **REBUILD / REPLACE AS UNIVERSAL HOST**; optionally retain a bounded-small adapter |
| `CharacterSpatialReorderPolicy` (`nearest…`, preview, subset merge) | Small deterministic pure logic with useful stable-ID behavior | **KEEP** |
| Pure spatial policy tests | Good basic list/nearest-slot coverage, but far from subsystem coverage | **KEEP + EXPAND** |
| `CharacterPcSettingsReorderV4` | Good proof of a simple 1D spatial consumer with scrolling | **KEEP + HARDEN** |
| `CharacterEquipmentSpatialReorderV4` | Correct migration direction and subset merge; needs scalability, coordination, persistence-boundary and lifecycle hardening | **KEEP DIRECTION + HARDEN** |
| `characterLongPressDragV4` | Legacy low-level detector coupled to old haptic semantics | **RETIRE after migration** |
| `characterMeasuredReorderDragV4` | Threshold/step reorder; incompatible with canonical spatial session | **FULL REBUILD / RETIRE** |
| `characterLongPressDrag2DV4` | Legacy callback-driven 2D detector | **RETIRE after migration** |
| `characterMeasuredGridReorderDragV4` | Threshold grid stepping, not actual continuous spatial placement | **FULL REBUILD / RETIRE** |
| `CharacterDragVisualStateV4` + old drag feedback | Supports legacy accumulated-offset/drop-before/drop-after model; springing drag offset contributed to lag/disconnect risk | **RETIRE after migration** |
| `CharacterDropIndicatorV4` | Belongs to legacy before/after step model, unnecessary for direct spatial reflow | **RETIRE after migration** |
| `CharacterLinearReorderModeControlV4` | Explicit compatibility shim for old `Reordenar` / `Listo` workflow; contradicts accepted P6 direct interaction | **RETIRE** |
| Trait/spell/inventory/conditional-module normalization, filtering, grouping, manual sort metadata | Useful domain behavior independent of gesture engine | **KEEP** |
| Domain `move…Manual(..., offset)` APIs | Fine as small pure helpers, but unsuitable as the primary UI transaction contract | **KEEP only if independently useful; DEPRECATE FOR DRAG UI** |
| Final-order application by stable IDs | Needed for one-drop transaction and subset/group preservation | **ADD / STANDARDIZE** |

---

## 4. Confirmed active legacy callers

The legacy path is not dead code. It remains part of active Player UI on the audited branch.

Confirmed legacy consumers include:

- **Rasgos / Traits**
  - old measured drag path;
  - separate reorder mode remains;
  - one-column reorder behavior remains;
  - `Reordenar` / `Listo` compatibility UI remains.

- **Notas / Notes**
  - separate reorder mode;
  - one-column reorder path;
  - legacy measured drag.

- **Conjuros / Spells**
  - legacy measured drag;
  - repeated offset moves during the gesture;
  - old drag/drop visual state.

- **Combate / Combat entries**
  - legacy measured drag;
  - canonical draft list/order is rewritten at threshold crossings during the gesture, rather than at one successful-drop boundary.

- **Artífice module**
  - legacy measured reorder.

- **Forms module**
  - legacy measured reorder.

- **Companions module**
  - legacy measured reorder.

- **Class-option shared modules**
  - Techniques;
  - Metamagic;
  - Pacts;
  - shared legacy reorder primitive.

Equipment and PC Settings tab order have moved to the spatial path, but that does not make P6 globally complete.

### Audit implication

The migration must inventory **every Player manual-order collection**, not only the surfaces already mentioned in earlier P6 implementation notes. No legacy gesture caller may remain silently active when P6 is re-certified.

---

## 5. Why the legacy path requires a rebuild, not a patch

The problem is not merely an unsuitable movement threshold.

The legacy implementation encodes a different architecture:

- pointer travel becomes repeated logical item moves;
- visual feedback is based on accumulated offsets and directional drop indicators;
- several callers mutate draft order as the pointer crosses thresholds;
- cancellation therefore does not share the new model's simple “discard transient preview” boundary;
- the old low-level gesture helper emits a drop haptic on cancel as well as completion;
- some screens require an explicit reorder mode and transformed one-column layout;
- the UI contract is expressed through `offset = +/-1` domain operations rather than one stable final order.

Tuning this path until it resembles the spatial model would duplicate the spatial engine while preserving two code paths. The safer and simpler architecture is to migrate its consumers and delete/quarantine the old stack.

**Finding: FULL REBUILD REQUIRED for the legacy reorder UI/orchestration path.**

---

## 6. New spatial core — what is good and what still needs hardening

`CharacterSpatialReorderStateV4` establishes the right conceptual transaction:

1. capture canonical order at pickup;
2. maintain transient preview order during movement;
3. visually lift/follow the dragged item;
4. commit once on a successful changed drop;
5. restore the captured order on cancel.

That architecture should be preserved.

However, app-wide certification requires the following hardening.

### 6.1 Pointer/card anchor semantics

The engine receives the root pointer position at pickup, but target geometry is principally derived from the source card center plus accumulated drag delta. The implementation should explicitly model the pickup anchor / pointer relationship so behavior remains correct through relayout and scroll, and so the targeting rule is easy to reason about and test.

### 6.2 Relayout and auto-scroll coordinate stability

During auto-scroll or animated reflow, item bounds move independently of raw pointer delta. The engine must guarantee that:

- the lifted card stays visually anchored to the user's finger;
- target selection uses a stable coordinate model;
- scrolling does not create target jumps or drift;
- viewport clipping/sticky headers do not distort edge detection.

### 6.3 Mid-drag canonical changes

Current synchronization intentionally ignores canonical-order updates while a drag is active. That avoids destroying the session, but it creates a stale-commit risk if another structural mutation changes the collection during the drag.

A collection/session coordinator should make the invariant explicit: a structural mutation that invalidates the active reorder session must either be impossible while dragging or must cancel/reconcile the drag safely. The engine must never overwrite a newer canonical state with an old drag snapshot.

### 6.4 Eligibility changes during drag

The session must explicitly cancel safely if reorder ceases to be legal, including examples such as:

- Manual -> A–Z presentation change;
- search text becomes non-empty;
- a filter activates;
- Table Mode / structural-edit permission changes;
- the dragged item is removed;
- the collection is replaced or reloaded;
- navigation destroys the owning surface.

No partial order may leak through those transitions.

### 6.5 Single active drag per interaction scope

Equipment already owns more than one reorder state in the same scrolling viewport. Multi-touch or overlapping detectors must not allow two sessions to become active simultaneously. A collection/viewport-level coordinator or equivalent invariant is required.

### 6.6 Target stability / hysteresis

Nearest-center targeting is simple and deterministic, but closely packed/variable-height cards can oscillate around geometric boundaries. Physical-device QA should determine whether hysteresis or slot-crossing stabilization is necessary. If needed, it belongs in the shared engine rather than per-screen patches.

### 6.7 API naming

The current modifier named `characterSpatialReorderDragHandleV4` is conceptually a **safe pickup region**, not necessarily a visible drag handle. Rename/refactor the API so future callers do not accidentally reintroduce visible handles or attach the gesture to child-control regions.

**Finding: KEEP the new core, but HARDEN / REFACTOR it before declaring it the final shared primitive.**

---

## 7. Layout host / scalability audit

The current multi-column host uses `FlowRow` with lookahead/animated bounds. This gives attractive real-layout reflow and is reasonable for small bounded collections, but it composes and lays out the whole section.

That is not a safe universal strategy for:

- large spell collections;
- large equipment collections;
- potentially large traits/notes/custom modules;
- future datasets whose size is not tightly bounded.

The universal architecture should distinguish:

- **1D lazy-list adapter** for long linear collections;
- **2D lazy/adaptive grid adapter** for scalable multi-column collections;
- optionally a **small bounded FlowRow adapter** where wrapping variable-height cards is productively different and item count is known to remain small.

All adapters should use the same spatial/session transaction contract and final-order commit API.

This avoids the opposite mistake of forcing every surface into one visual layout merely to share the gesture engine.

**Finding: the current `CharacterSpatialGridV4` should not become the app-wide universal host. Rebuild/replace the hosting layer; retain only a bounded-small variant if justified.**

---

## 8. Gesture arbitration audit

The accepted interaction is direct long-press on a safe card-body region. The shared implementation must guarantee:

- ordinary vertical scroll remains responsive before long-press pickup;
- tapping a card's real child controls does not begin reorder;
- buttons, checkboxes, menus, quick-use controls, text inputs, links, favorite toggles and equivalent operational children retain their own gestures;
- long-press pickup does not consume a gesture before it has actually won recognition;
- once pickup succeeds, drag movement belongs to the reorder session until drop/cancel;
- cancellation does not emit a successful-drop signal;
- a no-op drop does not pretend that persistence changed;
- nested scroll/autoscroll cannot create competing motion loops.

Callers should declare safe pickup surfaces structurally, not solve conflicts with local timing hacks.

---

## 9. Haptic and visual feedback audit

The new spatial direction is preferable:

- pickup haptic when pickup genuinely succeeds;
- target/slot feedback only when the preview target meaningfully changes;
- drop haptic only on a genuine successful drop according to the final contract;
- no “drop” haptic on cancellation;
- lifted card elevation/scale may animate;
- active pointer translation itself should remain direct rather than spring-lagged;
- non-dragged cards may animate reflow, provided the animation does not destabilize target geometry.

The legacy path currently has materially different semantics, including a drop haptic on cancellation. This is another reason not to preserve it.

---

## 10. Accessibility audit

Direct drag cannot be the only way an accessibility user can reorder.

The app does **not** need to restore visible up/down buttons or a visible reorder mode. Instead, the shared reorder component should expose semantic/custom accessibility actions appropriate to the layout, for example:

- move before / after in a linear sequence;
- move up / down / left / right or equivalent positional actions in a grid where meaningful;
- announce pickup/result/order changes when needed without excessive chatter.

The semantic action should use the same canonical final-order operation as touch drag, not a separate state model.

Current spatial math tests do not cover accessibility semantics, and current migrated callers do not constitute accessibility certification.

**Finding: accessibility support is a required missing part of the shared rebuild/hardening, not a later cosmetic enhancement.**

---

## 11. Domain-layer audit

The shared domain functions inspected for traits, spells, inventory/collections, and conditional modules generally contain useful logic for:

- stable IDs;
- normalization of manual sort indices;
- A–Z/manual presentation modes;
- grouping;
- filtering/search;
- Favorite/pinned semantics;
- preserving unrelated records while a subset changes order.

These rules should **not** be rebuilt just because the drag UI is being rebuilt.

The problematic coupling is the use of repeated `move…Manual(itemId, offset)` calls as the UI's drag transaction. A spatial drag naturally knows a final ordered list of stable IDs.

The domain/API boundary should therefore standardize operations equivalent to:

- apply exact final manual order for a collection;
- apply exact final order for a reorderable subset while preserving non-participating records;
- validate IDs, duplicates and membership;
- normalize persisted sort indices once after the final order is accepted.

Existing offset helpers may remain for independent non-drag uses or tests, but the drag UI should stop depending on them.

**Finding: KEEP the domain rules; ADD/STANDARDIZE final-order APIs rather than rebuilding the domain model.**

---

## 12. State and persistence boundary — unresolved product/architecture reconciliation

This audit found an important ambiguity between accepted P6 wording and the current editor architecture.

The structural character editor keeps many collections as in-memory/saveable **drafts** and considers them unsaved structural changes until the normal character Save path writes the repository authority.

The new Equipment spatial reorder currently commits the drag result once into the structural draft. It does **not** independently persist the entire character to the repository at drop.

Older P6 wording says order should “persist immediately on drop”. That can mean either:

### Interpretation A — immediate canonical draft commit

- pointer movement is transient preview only;
- successful drop writes the final order once to the current structural draft;
- the character becomes structurally dirty as normal;
- normal character Save/Cancel semantics remain authoritative for durable repository persistence.

### Interpretation B — reorder is a structural autosave exception

- successful drop immediately writes the repository authority even if other structural edits on the character still use global Save/Cancel;
- reorder therefore has different durability semantics from surrounding structural edits.

These interpretations are not equivalent. The subsystem should not silently choose one during migration.

### Audit recommendation

Prefer **Interpretation A** unless the owner explicitly wants reorder to be an autosave exception.

Reasons:

- preserves one consistent structural-draft transaction model;
- avoids a reorder being durably saved while neighboring edits can still be cancelled;
- avoids complicated partial-save interactions with dirty-state detection and Table Mode;
- still satisfies the important P6 interaction rule that the **drag transaction itself commits exactly once at drop**, with no separate `Done` button;
- cancellation of the editor can continue to mean cancellation of structural draft edits.

If A is accepted, the earlier phrase “persist immediately on drop” should be clarified in P6 documentation as **“commit immediately to the canonical structural draft on drop; durable repository persistence follows the normal structural Save contract.”**

This is the first owner decision required after this audit.

---

## 13. Testing audit

The pure spatial policy tests are useful but insufficient. Green compilation/current CI cannot prove the interaction subsystem is correct because the legacy path still compiles and remains active.

The rebuilt/hardened subsystem needs tests at several levels.

### Pure/order tests

- exact final-order application;
- subset merge with hidden/non-participating records;
- invalid/missing/duplicate IDs;
- first/last movement;
- row-major target mapping;
- variable/partial rendered-slot sets.

### Shared session/state-machine tests

- begin -> preview -> successful drop commits once;
- begin -> move -> cancel commits nothing and restores canonical order;
- unchanged drop has no false mutation;
- external canonical mutation during active drag cancels/reconciles safely;
- dragged item removal;
- reorder eligibility becoming false mid-drag;
- only one active session within a coordinated interaction scope;
- callback freshness after recomposition/state changes.

### Auto-scroll / geometry tests

- upper/lower edge activation;
- no scroll outside edge bands;
- scroll limits;
- pointer/card anchor remains stable while scrolling;
- reflow does not cause target drift;
- clipped viewport/sticky-header geometry;
- variable-height cards and multi-column layouts.

### Gesture/instrumentation tests

- normal scroll without pickup;
- long-press then drag;
- child button/control does not initiate reorder;
- cancellation/back/navigation does not commit;
- search/filter/manual-order changes correctly disable/cancel;
- Table Mode structural lock prevents reorder;
- accessible reorder semantic actions use the same final-order state path.

### Persistence/integration tests

Once the persistence decision in §12 is closed, test the exact agreed boundary explicitly, including Save/Cancel/reopen behavior.

### Physical owner-device QA

CI and emulator/instrumentation cannot certify drag feel. Final P6 QA must include owner-device testing for:

- short and long collections;
- portrait and landscape;
- one and multiple columns;
- variable card heights;
- edge auto-scroll;
- large app text scale;
- slow/fast/diagonal movement;
- child controls;
- no accidental reorder during ordinary scrolling;
- no lag between finger and lifted card.

---

## 14. Recommended rebuild sequence

This is the safest dependency order; it is not an authorization to implement.

1. **Resolve the drop/persistence boundary** in §12.
2. **Standardize final-order domain operations** by stable IDs/subsets, preserving existing domain normalization/filter/group rules.
3. **Harden/refactor the spatial session core**:
   - explicit pointer/card anchor model;
   - safe canonical-version/session invalidation;
   - eligibility-loss cancellation;
   - one-active-session coordination;
   - correct haptic lifecycle;
   - accessibility action contract.
4. **Build layout adapters rather than one universal visual host**:
   - lazy linear;
   - scalable lazy/adaptive grid;
   - bounded FlowRow only where justified.
5. **Migrate every active legacy caller** while preserving each collection's existing domain/presentation rules.
6. **Remove the legacy reorder-mode UX** (`Reordenar` / `Listo`, forced one-column transformation) everywhere.
7. **Delete/quarantine legacy gesture/drag-feedback/drop-indicator primitives** once repository-wide caller audit proves none remain.
8. **Add state-machine, geometry, gesture, accessibility and persistence tests.**
9. **Run repository-wide static caller audit + CI/runtime tests.**
10. **Run owner-device P6 physical QA** before closing the finding.

---

## 15. Exit criteria for P6

P6 must not be closed until all of the following are true:

- one coherent reorder state/transaction model is used by every Player manual-order collection;
- no active threshold/step legacy caller remains;
- no visible `Reordenar` / `Listo` mode or forced reorder-only one-column layout remains unless a separately approved product exception exists;
- search/filter/non-Manual/Table-Mode eligibility is enforced consistently;
- safe child-control gesture arbitration is verified;
- one successful drop produces one final-order commit and cancel produces none;
- the agreed draft/repository persistence boundary is tested;
- long collections use a scalable layout host;
- auto-scroll and layout reflow remain stable on device;
- accessibility has a non-drag reorder path using the same canonical state operation;
- obsolete compatibility primitives are removed or proven unreachable and intentionally retained;
- automated tests cover state/session failures, not merely list math;
- CI is green;
- owner-device physical QA passes.

---

## 16. Audit verdict

**P6 is a systemic shared-subsystem repair.**

The repo should **not** continue by individually tuning each legacy reorder caller. The correct boundary is:

- preserve good domain/data rules;
- preserve and harden the promising spatial session concept;
- rebuild the universal hosting/integration layer where scalability requires it;
- migrate all active callers;
- retire the incompatible legacy UI/orchestration stack;
- then certify the subsystem across the full Player app.

**FULL APP AUDIT REQUIRED remains active through implementation and verification.**

**P6 status after this audit: OPEN / ARCHITECTURAL REBUILD REQUIRED FOR LEGACY PATH / NEW CORE HARDENING REQUIRED / NOT QA READY.**

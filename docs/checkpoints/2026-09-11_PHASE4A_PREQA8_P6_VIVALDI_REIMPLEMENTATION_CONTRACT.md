# Phase 4A PREQA8 — P6 reopened: Vivaldi-style reorder reimplementation contract

**Date:** 2026-09-11  
**Branch:** `implementation/phase4a-successor-cycle`  
**Baseline before this checkpoint:** `f8ba275f6ec9646844b36448676962a114a3d179`  
**Status:** **P6 REOPENED / REIMPLEMENTATION REQUIRED**  
**Implementation authorization:** already granted for the Player repair pass.  
**Scope:** Player manual-order collections and PC Settings tab order.  
**Audit classification:** **FULL APP AUDIT REQUIRED**.

## 1. Owner correction / reason for reopening

The owner explicitly rejected the implementation plan that would simply remove the legacy `Reordenar -> one-column -> Listo` gates and re-enable the drag machinery already present underneath.

The owner reports that **all prior versions of the reorder interaction felt wrong**, and asked for a deeper investigation of the Vivaldi Mobile browser interaction that was the original reference. The existing drag implementation must therefore be treated as suspect at the architectural level, not as a nearly-correct primitive needing threshold tuning.

The earlier P6 product intent remains useful, but **the previous implementation is not an approved basis for the repaired interaction**.

## 2. External interaction research

Primary current/reference sources reviewed:

- Vivaldi Browser Help — **Tabs on Android**: Tab Switcher reordering is `long-press -> drag -> drop at the new location`; no separate reorder mode is entered.
  - https://help.vivaldi.com/android/android-browse/android-tabs/
- Vivaldi Browser Help — **Tab Stacks on Android**: the same direct spatial drag grammar is used to reorder tabs inside stacks and to drag a tab over another tab/stack for grouping.
  - https://help.vivaldi.com/android/android-browse/android-tab-stacks/
- Vivaldi Android 7.4 release material: the Tab Switcher is fundamentally a normal grid/list browsing surface rather than a dedicated ordering editor.
  - https://vivaldi.com/blog/vivaldi-on-android-7-4/
- Current Vivaldi 8.2 Android changelog/release line: Vivaldi itself has continued to fix reorder-related UI problems, including `VAB-13335` (reordering/pinning causing UI glitches). This is a useful warning **not to copy an assumed internal implementation**; we copy the direct-manipulation interaction model only.
  - https://vivaldi.com/changelog-vivaldi-browser-8-2-on-android/ (localized mirrors contain the same changelog)
- Android Developers — **Drag and drop in Compose** explicitly distinguishes platform drag-and-drop transfer from the in-app **pick up and move** list pattern.
  - https://developer.android.com/develop/ui/compose/touch-input/user-interactions/drag-and-drop
- Android Developers — **Understand gestures** documents the lower-level pointer primitives needed for a custom long-press/pick-up-and-move interaction.
  - https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures

### Interaction lessons adopted from Vivaldi

1. The collection remains in its normal spatial layout before, during and after reordering.
2. Reorder is transiently activated by a long press on the item itself; there is no `Reordenar` mode and no `Listo` confirmation.
3. The selected object is treated as a spatial object that is picked up and moved, not as a request to increment/decrement an index.
4. The user must be able to understand where the object is, and where it will land, continuously through the gesture.
5. The gesture is compatible with a grid mental model: horizontal and diagonal motion are real, not translated into an artificial one-column ordering screen.
6. A normal tap remains a normal tap; reorder requires deliberate hold-and-move intent.

These are interaction references, **not a claim that this app must reproduce Vivaldi artwork, dimensions, animations or proprietary internals**.

## 3. Audit of the current app primitive

Current files inspected include:

- `CharacterCardInteractionV4.kt`
- `CharacterCollectionPrimitivesV4.kt`
- `CharacterEquipmentClosureV4.kt`
- `CharacterPcSettingsReorderV4.kt`
- historical `2026-09-03_PHASE4_BATCH_B2_ORDERING_CONTEXT_DRAG.md`

The current interaction has architectural properties that explain the repeated poor feel:

### A. Movement is index stepping, not spatial placement

`characterMeasuredReorderDragV4` and `characterMeasuredGridReorderDragV4` accumulate pointer delta until a threshold is crossed, call a logical `onMove(...)`, then reset the accumulated offset.

Consequences:

- the dragged object does not remain continuously anchored to the user's finger;
- every logical move can visually restart the drag from the item's new layout position;
- the gesture feels like repeated index commands hidden behind a drag gesture.

### B. The 2D version uses axis arithmetic rather than rendered-slot hit testing

The grid primitive chooses row/column deltas from normalized X/Y movement and callers convert those deltas back into row-major indices.

Consequences:

- target selection is based on the dragged item's dimensions and threshold fractions, not the actual geometry of destination cards/slots;
- gaps, unequal card heights, responsive column widths and incomplete last rows can produce unintuitive transitions;
- diagonal motion is reduced to whichever normalized axis wins rather than to the real slot under the dragged card/pointer.

### C. The visual object can lag the pointer

`characterDragFeedbackV4` spring-animates active X/Y translation. A spring is useful for neighboring-item settling and final snap, but the object actively held by the user should track the pointer tightly. Applying spring interpolation to the active drag offset can create visible latency.

### D. Model mutation can happen during the gesture

Current callers can rewrite `sortOrder`/state for every threshold crossing. That conflates:

- transient drag preview state;
- canonical persisted order.

It also makes cancellation semantics weak and increases the chance that recomposition/layout changes occur while the finger is still moving.

### E. No shared spatial reorder session exists

There is no central session owning:

- stable dragged item identity;
- pickup pointer offset inside the card;
- measured item rectangles/centers;
- preview order;
- current insertion/slot target;
- edge auto-scroll;
- commit vs cancel semantics.

Instead, each card owns pieces of its own drag state and asks its collection to move one step at a time.

### F. The historical proof was insufficient

The 2026-09-03 B2 checkpoint proved that cards could visibly lift/translate and that CI was green. It did **not** prove Vivaldi-like direct manipulation quality on the owner's device. Subsequent owner QA supersedes that earlier interaction-quality conclusion.

## 4. Replacement architecture — required

P6 must be implemented as a **new shared pick-up-and-move reorder engine**. Existing threshold-step functions may be removed/deprecated after callers migrate; they are not to be merely re-enabled.

### 4.1 Collection-owned reorder session

A reorderable collection owns one session containing at minimum:

- stable `draggedId`;
- source index;
- pickup pointer position and the pointer-to-card anchor offset;
- current pointer position in collection coordinates;
- measured bounds/centers for visible reorderable items;
- a transient `previewOrder` or equivalent insertion model;
- current target slot/index;
- drag-active state;
- scroll viewport information required for edge auto-scroll.

### 4.2 Dragged item remains under the finger

After activation, the picked-up card/row must track pointer movement **directly**, preserving the pickup anchor so the card does not jump its center underneath the finger.

- Do not spring/interpolate active pointer tracking.
- Lift/elevation/scale may animate at pickup.
- Final settle after drop/cancel may animate.

### 4.3 Real rendered geometry determines target placement

Destination changes must be derived from measured rendered item geometry / slot centers, not repeated +/- index thresholds.

For 2D collections:

- preserve the actual responsive grid;
- support horizontal, vertical and diagonal travel naturally;
- row-major order remains the persistence order, but it is **not** the gesture model;
- incomplete final rows and heterogeneous card sizes must have deterministic nearest/insertion behavior.

For 1D collections, the same engine may use vertical slot geometry rather than a separate index-step gesture implementation.

### 4.4 Stable placeholder / neighbor reflow

While the dragged object is visually lifted above the collection, the collection must expose a clear destination by moving/reflowing neighbors around a placeholder/insertion slot.

The user should perceive:

> pick this card up -> move it -> surrounding cards make room -> release it here.

Do not make the picked card repeatedly snap back into the layout after each crossed threshold.

### 4.5 Preview is transient; persistence happens on successful drop

During drag:

- mutate only transient preview/order state where practical;
- do not repeatedly persist canonical sort order for every pointer threshold crossing.

On drop:

- commit the final order once;
- persist immediately;
- clear the reorder session.

On cancellation/interruption:

- restore/retain the pre-drag canonical order;
- clear the transient session;
- no partial reorder should leak through.

### 4.6 Edge auto-scroll

Long collections must support controlled auto-scroll when the active pointer approaches the top or bottom edge of the scroll viewport.

Requirements:

- speed increases sensibly with proximity to the edge;
- dragged-item anchoring remains coherent while content scrolls;
- target geometry is recomputed as new items enter the viewport;
- no uncontrolled fling/jump.

This is required for a reorder system intended for real Player collections, not an optional polish item.

### 4.7 Gesture arbitration / child controls

- Long-press on the reorderable **card body** is the pickup gesture.
- Normal short tap behavior remains unchanged.
- Interactive child controls (buttons, checkboxes, menus, resource controls, text inputs, etc.) retain their own actions and must not unexpectedly begin reorder.
- Scrolling before a successful long-press must continue to scroll normally.
- Once pickup is established, the reorder session owns subsequent movement until drop/cancel.

### 4.8 Haptics

Haptic grammar:

- one clear pickup haptic when the item is successfully lifted;
- restrained target/slot transition feedback, not vibration for raw pointer movement;
- one drop haptic on successful commit;
- cancellation must not falsely communicate a successful drop.

Respect the existing per-character haptic preference.

### 4.9 Visual grammar

The dragged item should receive a restrained direct-manipulation lift:

- elevated above siblings;
- slight scale/tonal/border treatment if helpful;
- original content remains legible;
- direct pointer tracking.

Neighbors should animate into preview positions smoothly. The primary location cue is **real reflow/space creation**, not a pair of generic before/after bars attached to the moving item.

### 4.10 Availability rules retained

Reorder remains available only when:

- structural editing is enabled;
- presentation order is `MANUAL`;
- no active search/filter changes the visible subset/order.

A–Z is presentation-only and must never rewrite manual order.

No separate reorder mode, no drag handle requirement, no `Done` button.

## 5. Required rollout scope

The replacement engine must be audited and applied consistently to every Player surface that exposes manual ordering, including at least:

- Equipo ordinary/special collections;
- Rasgos;
- Notas;
- Combate attacks/actions where manual order exists;
- Conjuros or conditional-module collections wherever manual ordering is exposed;
- PC Settings tab ordering;
- any other Player collection discovered by the full-app P6 audit.

The recently-added `CharacterPcSettingsReorderV4.kt` uses the old threshold-step primitive and is therefore **not accepted merely because its CI is green**. It must migrate to the replacement engine too.

## 6. Implementation strategy

Preferred sequence:

1. Build the new shared reorder session/geometry engine in isolation.
2. Add deterministic pure tests for preview-order / target-slot calculations where they can be separated from Compose.
3. Build one representative 2D grid consumer and one 1D consumer as proof consumers.
4. Validate interaction semantics in code/automated tests; then roll out to all Player manual-order surfaces.
5. Delete or quarantine the old threshold-step reorder path only after no production caller depends on it.
6. Run the full Player reorder audit and CI.
7. Treat owner-device QA as the final interaction-quality authority; CI green alone does not close P6.

## 7. Acceptance criteria

P6 is implementation-complete only when all of the following are true:

- an item can be long-pressed and picked up directly in its normal layout;
- it remains visually anchored under the finger through the entire gesture;
- neighboring items create/reflow around the intended destination;
- 2D grids reorder according to real rendered geometry rather than hidden vertical/index stepping;
- drag across multiple positions is continuous, not repeated snap-reset steps;
- edge auto-scroll works for off-screen destinations;
- normal scroll remains natural when reorder was not activated;
- nested child controls remain usable without accidental reorder;
- cancellation causes no partial canonical reorder;
- successful drop commits/persists exactly once at the final order boundary (or an equivalent single canonical transaction);
- Manual/A–Z/search/filter invariants remain intact;
- no `Reordenar`, `Listo`, arrow buttons or reorder-only one-column transformation remain;
- haptic feedback describes pickup/target/drop rather than raw movement;
- all Player manual-order surfaces use the same interaction grammar;
- owner-device QA confirms the interaction no longer has the prior "off" feel.

## 8. Supersession statement

This checkpoint **supersedes the implementation approach** previously inferred from P6 and from the B2 drag foundation.

It does **not** revoke the useful product-level P6 decisions (direct long-press drag, real normal layout, Manual-only, search/filter protection, immediate persistence after drop, shared rollout). It clarifies that those decisions require a genuine direct-manipulation reimplementation rather than activation/tuning of the existing step-based drag code.

**P6 remains REOPENED until the new engine is implemented, audited, CI-green and owner-device QA has evaluated its feel.**

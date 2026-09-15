# Phase 4A — Repair Round 2: reorder target stability

**Date:** 2026-09-13  
**Branch:** `implementation/phase4a-successor-cycle`  
**Round baseline HEAD:** `de5f2aeb55e6e3f4558d58103f0781b4baa960fa`  
**Round product/test HEAD:** `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`  
**Physical baseline remains:** `0.4.0-preqa.12 / 41200` at `abfc7e4a1519a27117f194721a425d75cb5df68a`  
**Status:** ROUND 2 COMPLETE / AUTOMATION GREEN / T1 TARGETED PHYSICAL REVALIDATION DEFERRED TO CONSOLIDATED CANDIDATE

## 1. Scope

This round repairs T1: the owner-observed card reorder instability where surrounding cards repeatedly reflowed and the apparent insertion target moved while the user dragged. Existing physical video evidence established that the defect was not tablet-specific, orientation-specific or column-count-specific, so the repair covers both active reorder engines rather than one screen layout.

This round does not mark T1 physically PASS. A new consolidated physical-QA candidate has not yet been frozen.

## 2. Root cause repaired

Both active Android reorder paths were feeding live `onGloballyPositioned` preview geometry back into target selection while the preview itself was being animated/repacked. That formed a feedback loop: preview movement changed measured bounds, measured bounds changed the target, and the target changed the preview again.

The repaired contract is:

- capture canonical order and currently rendered target geometry at drag start;
- derive every preview from that canonical order, never from the previous preview order;
- do not let animated/recomposition bounds retarget an already measured slot during the active drag;
- permit pointer motion to retarget against the stable snapshot;
- apply a small geometric hysteresis/deadband around slot boundaries;
- when the viewport really scrolls, translate the stable slot snapshot by the consumed scroll before retargeting;
- lazily revealed targets may be captured once without replacing existing stable slots;
- final order still crosses the existing validated commit/persistence boundary.

## 3. Implementation chain

- `258636c217ba1cfb9009e45008c8842b318ecaff` — `fix: add stable reorder targeting policy`
  - adds `stableCharacterReorderTargetIndex(...)`;
  - adds 12% geometric hysteresis by default;
  - adds stable-slot viewport translation;
  - keeps missing/non-rendered slots from attracting the pointer.
- `ed5fa0a7eded8b0984c8dbb1dd0b3f6632973914` — `test: cover stable reorder targeting and hysteresis`
  - canonical-preview idempotence;
  - boundary hysteresis in both directions;
  - two-dimensional/spatial targeting;
  - explicit viewport-scroll translation;
  - missing/unrendered-slot behavior.
- `164e4ac60f9837547bc3417591b3e4c22f00f8c0` — `fix: stabilize spatial reorder drag targets`
  - spatial/card-grid engine now uses drag-start stable slots and canonical preview order.
- `b98320fcae81f09f0c5440d5a331357095c96bde` — `fix: stabilize shared reorder session targets`
  - one-dimensional/shared session receives the same stable-target contract.
- `af19d1d9aea14cafc4d512c30ddfed4f0dc4090b` — `test: guard stable Player reorder targeting`
  - durable source guard prevents live preview geometry from becoming an active retarget trigger again.
- `bc1dfd207230079506f978eaef0050a1a104e1ae` — `ci: enforce Player reorder stability contract`
  - adds the reorder guard to normal Scaffold Kotlin validation.
- `34999b0712179015c11ead4e25a213ab45d10650` — `fix: share reorder slot conversion across engines`
  - resolves the cross-file helper visibility boundary exposed by compilation.
- `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb` — `test: avoid reorder policy test class collision`
  - resolves the final test-class naming collision without changing the repair contract.

## 4. Automated verification

Authoritative green run for this round:

- Workflow: `Scaffold checks`
- Run ID: `34788409987`
- Run number: `1519`
- Head SHA: `5b06056e9e8ed5cf05a767dd1da3d6f4f48363eb`
- Conclusion: **SUCCESS**
- Backend job: **SUCCESS**
- Compact Player control geometry guard: **SUCCESS**
- Player reorder target-stability guard: **SUCCESS**
- Kotlin/shared build and tests: **SUCCESS**
- Android build: **SUCCESS**
- Debug APK upload: **SUCCESS**

Earlier intermediate CI failures in this round were integration/test-harness issues discovered by the normal gate (shared helper visibility and a duplicate test-class name). They were corrected inside the round; the successful run above supersedes them for Round 2 verification.

## 5. QA status after Round 2

T1 is now **IMPLEMENTED / AUTOMATION GREEN / TARGETED PHYSICAL REVALIDATION PENDING**. Preserve the existing owner video as the failure baseline; do not ask for it to be re-explained or reattached.

Targeted physical revalidation on the future consolidated candidate should verify:

- drag target no longer visibly chases preview reflow;
- one-column reorder;
- multi-column/spatial reorder;
- auto-scroll while dragging;
- final order persists after leave/reopen.

Accepted unrelated phone/tablet evidence remains preserved and must not be replayed.

## 6. Project gate

Phase 4A remains **OPEN**. The exact frozen physical candidate remains preqa.12 until the consolidated repair is complete and receives a new monotonic QA identity. DM implementation remains blocked pending explicit owner Phase 4A closure. No P18 exists.

## 7. Next round

**Round 3: shared compact checkbox + responsive grouping family** for phone 17.1–17.3 and the tablet reproduction.

Audit all Android Player checkbox/tri-state/switch sites and their row/wrap containers, establish legitimate exceptions, introduce or reuse a shared compact/touch-safe primitive where systemic evidence supports it, migrate affected sites, and repair responsive packing so controls share a row when they fit and exploit wider layouts instead of preserving unnecessary stacked rows. Add focused tests/guards, run the aggregate Scaffold gate, then update durable status again before proceeding.

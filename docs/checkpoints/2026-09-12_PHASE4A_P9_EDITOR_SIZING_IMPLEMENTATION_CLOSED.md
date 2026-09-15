# Phase 4A — P9 shared editor sizing / adaptive editor layout implementation CLOSED

**Date:** 2026-09-12  
**Branch:** `implementation/phase4a-successor-cycle`  
**P8 closure baseline:** `10d0cbeda73ce91fde78700be0d81d903567cd0d`  
**Implementation evidence head before this checkpoint:** `590c4184723dd4ffb3d4a33b38089e96e78d0901`  
**Status:** **IMPLEMENTATION CLOSED / READY FOR OWNER QA IN THE REPAIRED BUILD**

## Authority and scope

This checkpoint closes implementation of P9 — **Shared editor sizing / adaptive editor layout** — under:

- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md`;
- `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md`.

P1–P8 remain independently closed and were not reopened by this repair. The immediate baseline is:

`docs/checkpoints/2026-09-12_PHASE4A_P8_BACKGROUND_PHOTO_IMPLEMENTATION_CLOSED.md`

No DM implementation, `main` synchronization, destructive history rewrite, reset, force push, or P10 implementation is authorized or performed by this checkpoint.

## Important implementation finding

P9 is unusual in this repair sequence because inspection of the actual P8-baseline code showed that **most of the owner-approved P9 behavior was already present in the shared editor primitive before this repair point was formally closed**.

At baseline `10d0cbeda73ce91fde78700be0d81d903567cd0d`, `CharacterImeSafeEditorDialog` already implemented:

- natural/content-sized height for ordinary short editors rather than unconditional full-height surfaces;
- maximum height derived from the currently usable `BoxWithConstraints` viewport;
- internal vertical scrolling for content that exceeds the available height;
- `Cancelar / Guardar` outside the scrolling content region;
- `imePadding()` and `navigationBarsPadding()` on the currently usable dialog area;
- a readable maximum width rather than stretching ordinary dialogs across all tablet width;
- focus clearing on outside interaction without changing the editor design.

Those behaviors were verified from primary implementation evidence and preserved. They were **not rewritten merely to manufacture a larger P9 diff**.

The remaining contract gap was the explicit opt-in expanded mode for genuinely complex workflows.

## P9 implementation commit

P9 adds one focused production commit after the P8 closure baseline:

`590c4184723dd4ffb3d4a33b38089e96e78d0901` — `feat: add explicit P9 expanded editor mode`

The aggregate production diff from the P8 closure baseline through the P9 implementation head is limited to:

`androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterInteractionPrimitivesV4.kt`

The diff is eight additions and one deletion. No P1–P8 implementation surface was modified.

## Shared default editor contract

`CharacterImeSafeEditorDialog` remains content-sized by default.

The new API is:

```kotlin
expanded: Boolean = false
```

Therefore every existing caller keeps the compact adaptive behavior unless it deliberately requests the larger surface.

This preserves the owner-approved rule that expanded sizing is an exception, not a new shared default.

## Expanded editor contract

When a genuinely complex workflow elects to use the expanded mode, the shared primitive applies:

```kotlin
Modifier.fillMaxHeight(0.9f)
```

inside the already IME-aware/navigation-aware/padded usable dialog viewport.

Consequences:

- expanded mode reserves most of the **currently usable** height;
- it still leaves outer breathing room;
- it does not request literal device fullscreen;
- it still honors the same `heightIn(max = maxHeight)` bound;
- it still uses the same independently scrolling content region;
- it still keeps the shared action row outside that content scroll;
- when the keyboard reduces usable height, the expanded surface is recomputed against that reduced area rather than using a fixed physical size.

No existing editor was mechanically forced into expanded mode simply to demonstrate the flag. Doing so would contradict the P9 rule that expanded mode is optional and should be used only when it improves a genuinely complex workflow.

## Representative simple-editor evidence

The Trasfondo narrative field editor in `CharacterBackgroundTabV4.kt` remains a representative short editor:

- it invokes `CharacterImeSafeEditorDialog` without expanded mode;
- its body may contain only the current narrative field;
- the shared surface therefore wraps the actual content instead of consuming the available screen height;
- a larger tablet does not make that short editor tall merely because more height exists.

This is representative evidence for the owner complaint that motivated P9: short/simple editors no longer inherit a full-height policy from the shared primitive.

## Medium / large editor behavior

The shared primitive composes the editable content as a bounded scrolling region using:

- `heightIn(max = maxHeight)` on the modal surface;
- `weight(1f, fill = false)` on the editable content container;
- `verticalScroll(scrollState)` on that content container.

Accordingly:

- short content keeps natural height;
- medium content grows naturally;
- content larger than the usable viewport is bounded and scrolls internally;
- portrait/constrained height cannot cause the editor to grow indefinitely beyond the usable screen.

## Save / Cancel and IME behavior

`Cancelar / Guardar` remain outside the scrolling editable-content column in the shared dialog.

The usable editor region is wrapped by:

- `imePadding()`;
- `navigationBarsPadding()`;
- responsive `BoxWithConstraints` bounds.

This preserves the preferred P9 behavior without adding device-specific keyboard hacks.

The owner-approved practical-flexibility clause remains in force: a separate editor surface may use a stable reachable-through-scroll fallback if a fixed action footer is not robust in that context. P9 does not reopen the previously rejected keyboard-layout rabbit hole.

## Representative complex / wide-layout evidence

`CharacterSpellListClosureV4` provides the representative existing complex-editor case used for P9 review.

Its spell workflow includes name, level, casting time, range, source associations, prepared state, V/S/M, optional material text, duration, concentration, ritual, description, notes, and validation.

Its existing layout already demonstrates the accepted semantic grouping policy rather than mechanical alternating columns:

- `Nivel` and `Tiempo de lanzamiento` are paired through `CharacterCompactFieldRowV4`;
- `Alcance` and `Duración` are paired through `CharacterCompactFieldRowV4`;
- long-text `Descripción` and `Notas` span the available editor width;
- on the existing wide tablet-landscape path, the spell collection and a bounded 340 dp editor surface coexist side by side instead of stretching a simple modal pointlessly across the tablet.

This behavior was already present and was deliberately preserved rather than redesigned under P9.

The non-wide spell-editor path continues to use the shared adaptive dialog and therefore receives bounded scrolling and the stable action row. It is **eligible to opt into** the new `expanded` mode if future owner/device QA demonstrates that the larger surface is actually beneficial; P9 does not assume that larger is automatically better.

## Contract-by-contract audit

### 1. Short/simple editor no longer fills available height

**PASS — implementation evidence.**

The shared default has no `fillMaxHeight` request. Short callers use natural content height.

### 2. Medium/large editors grow and then scroll at a bounded height

**PASS — implementation evidence.**

Natural content sizing is combined with `heightIn(max = maxHeight)` and an internal scroll container.

### 3. Portrait / constrained height does not allow unreasonable unbounded growth

**PASS — implementation evidence.**

The modal is bounded by the current `BoxWithConstraints.maxHeight`, after IME/navigation insets and outer padding.

### 4. Complex workflows can explicitly request expanded sizing without true fullscreen

**PASS — implementation capability.**

`expanded = true` is now a first-class opt-in on the shared primitive and reserves 90% of the currently usable height, not the raw full display.

No current caller was forcibly enlarged merely to exercise the option because that would violate the owner-approved opt-in semantics.

### 5. Keyboard-open states preserve reliable editing and action reachability

**PASS — implementation evidence / owner-device acceptance still required.**

The shared modal applies `imePadding()` to the available dialog region and keeps actions outside the content scroll. Physical behavior across the owner's devices remains an acceptance test, not something CI can certify.

### 6. Wide/tablet layouts do not stretch simple editors pointlessly

**PASS — implementation evidence.**

Default dialogs remain natural-height and are width-bounded to 640 dp. Extra tablet space alone does not enable expanded mode.

### 7. Representative complex wide form uses semantic grouping with long text full width

**PASS — existing implementation evidence.**

The spell editor uses semantically paired compact rows for related short fields and full-width long-text fields; its tablet-landscape path uses a bounded side editor.

## Automated/build evidence

Implementation commit `590c4184723dd4ffb3d4a33b38089e96e78d0901` ran Scaffold workflow:

`34671222155`

Result:

- backend / Worker type-check: **SUCCESS**;
- Kotlin build and desktop tests: **SUCCESS**;
- Android debug assembly: **SUCCESS**;
- Android debug APK upload: **SUCCESS**.

The repository does not currently provide a dedicated Android Compose instrumentation harness for asserting physical modal pixels, IME behavior, or owner-device geometry. P9 therefore does not pretend that compilation is device acceptance. The shared layout mechanics are implementation-closed; real-device visual/IME acceptance remains owner QA.

## Non-regression / scope audit

Compare:

`10d0cbeda73ce91fde78700be0d81d903567cd0d...590c4184723dd4ffb3d4a33b38089e96e78d0901`

contains exactly one modified file:

`CharacterInteractionPrimitivesV4.kt`

with eight additions and one deletion.

No P1–P8 production surface was modified. No domain semantics, persistence schema, reorder behavior, provenance behavior, photo persistence, combat state, or collection behavior was changed.

## P9 conclusion

P9 is **implementation-closed**.

The repair does not claim that the P8-baseline primitive was entirely wrong. Instead, primary code inspection showed that the key adaptive behavior had already been implemented but not formally reconciled against the P9 acceptance contract. The repair preserved those correct behaviors and added the one missing policy capability: an explicit, bounded, IME-aware expanded opt-in for genuinely complex workflows.

This conclusion is intentionally narrower than owner acceptance.

### Implementation closure means

- the shared default is content-sized;
- larger content is bounded and internally scrollable;
- shared Save/Cancel actions are outside content scroll;
- IME/navigation insets reduce the usable sizing area;
- simple tablet editors remain compact;
- genuinely complex workflows now have an explicit bounded expanded option;
- existing semantic wide-layout behavior is preserved;
- CI/build evidence is green.

### Implementation closure does not mean

- owner/device QA has accepted physical modal proportions;
- every keyboard/IME implementation on every Android device has been manually exercised;
- every editor must use expanded mode;
- every tablet editor must use multiple columns;
- the separate global phone-landscape/sticky policy has been implemented;
- P10 or later repair points have begun.

## Next boundary

P10 is **not started** by this checkpoint.

Any subsequent repair must begin from the actual remote HEAD after this checkpoint, preserve P1–P9 closure boundaries, and re-read only the next authorized repair decision rather than reopening already closed points without contradictory evidence.

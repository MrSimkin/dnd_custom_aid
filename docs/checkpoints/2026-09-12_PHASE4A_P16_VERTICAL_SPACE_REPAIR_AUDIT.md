# Phase 4A — P16 landscape / vertical-space repair audit

Date: 2026-09-12
Branch: `implementation/phase4a-successor-cycle`
Authority: `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P16_LANDSCAPE_VERTICAL_SPACE_CLOSED.md`

## Status

IMPLEMENTATION REPAIR APPLIED — NORMAL SCAFFOLD VALIDATION PENDING AT THIS CHECKPOINT REVISION.

Primary source commit:

`82fe1fd5f90d1844729ca01bec918a5981a33355` — `repair: implement P16 vertical-space policy`

This checkpoint does not claim owner/device QA and does not make the current APK a Phase 4A QA candidate. P17 remains the later physical phone/tablet acceptance gate after P13–P16 and the aggregate regression sweep are complete.

## Branch-wide audit evidence

A temporary read-only workflow enumerated the active branch before repair. It found:

- centralized `CharacterVerticalSpaceV4` thresholds in `CharacterLayoutContextV4.kt`;
- Combat already consuming vertical-space state for its persistent HUD;
- seven raw `stickyHeader` sites across Player collection/spell surfaces:
  - Forms;
  - class-option modules;
  - Traits;
  - Equipment;
  - spell level headers;
  - Artifice;
  - Companions;
- shared `CharacterCollectionToolbarV4` callsites across those collections plus Notes;
- phone/tablet navigation still governed independently by form factor;
- the editor already wrapped in `BoxWithConstraints`, but `characterLayoutContextV4()` still used static `LocalConfiguration.screenHeightDp` rather than those live constraints.

The Android activity uses `android:windowSoftInputMode="adjustResize"`, so the editor's `BoxWithConstraints` is the appropriate branch-local source for a viewport that can shrink when the IME reduces usable height.

## Demonstrated P16 gaps and repairs

### 1. Actual usable vertical space was not propagated

Before repair, `CharacterEditorV4` entered `BoxWithConstraints` but discarded `maxWidth` / `maxHeight` and recreated layout context from `LocalConfiguration`.

Repair:

- added `characterLayoutContextForAvailableSizeV4(...)`;
- the editor now derives available width/height from `BoxWithConstraints.maxWidth/maxHeight` after Scaffold padding;
- form-factor classification still comes from the normal screen configuration, so temporary height pressure does **not** reclassify a tablet as a phone or change phone navigation semantics;
- `CharacterAdaptiveShellV4` receives that measured context and provides it through `LocalCharacterLayoutContextV4` to nested Player surfaces;
- existing nested calls to `characterLayoutContextV4()` therefore see the same measured vertical-space state.

This keeps the width/form-factor axis separate from the vertical-pressure axis, as required by P16.

### 2. Collection/spell sticky UI ignored vertical pressure

Before repair, seven audited collection/spell headers remained sticky regardless of height.

Repair:

- added shared `LazyListScope.characterAdaptiveStickyHeaderV4(...)`;
- each audited collection computes stickiness from the centralized vertical-space state;
- headers remain sticky only in `COMFORTABLE` space;
- in `REDUCED` or `CONSTRAINED` space the exact same content/actions become ordinary list items and scroll away naturally.

This preserves functionality while yielding persistent height to the active task. It also makes spell-level headers conditional rather than permanently sticky.

### 3. Existing Combat behavior retained

Combat's pre-existing P16 behavior remains intact:

- the persistent Combat HUD consumes `CharacterVerticalSpaceV4`;
- when width permits and vertical space is reduced/constrained, core metrics reflow into a shallow horizontal row;
- death saves remain scrollable content rather than permanent HUD chrome.

### 4. Navigation semantics retained

`characterNavigationPresentationForLayoutV4(...)` remains unchanged:

- phone portrait: top tabs;
- phone landscape: top tabs;
- tablet portrait: top tabs;
- tablet landscape: side rail.

Vertical pressure therefore cannot accidentally turn phone landscape into tablet-style navigation.

## Collection-toolbar policy after audit

The shared toolbar already supports compact controls and collapsible search. The P16 repair does not hide actions or filters. Instead, collection toolbars that were previously inside sticky collection headers now scroll away under reduced/constrained height. The Spells toolbar is already a compact/collapsible single toolbar immediately above the active spell list; its lower-priority spell-level subheaders now lose stickiness under height pressure.

## Scope safety

The repair changes layout-pressure measurement and presentation persistence only. It does not alter character data, structural-edit ownership, Table Mode semantics, canonical HP, reordering persistence, spell-slot persistence, or navigation tab ordering. No change was made to `main`.

Temporary audit/repair workflow and script files self-deleted from the resulting source commit.

## Validation requirement

P16 is not automation-closed by this document alone. The repository's normal Scaffold gate must pass on a descendant containing `82fe1fd5...`, including backend typecheck, the exact Kotlin/shared/Android/Desktop gate, and Android debug APK upload. Any compile or regression failure must be repaired before P16 closure is recorded.

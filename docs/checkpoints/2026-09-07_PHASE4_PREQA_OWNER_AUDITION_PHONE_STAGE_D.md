# Phase 4 pre-QA owner audition — phone Stage D

**Date:** 2026-09-07  
**Status:** OWNER PHONE STAGE D COMPLETE; card-column and rotation findings recorded  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Review identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary phone:** Redmi Note 11 Pro 5G

## Preconditions

- exact review build already verified in `Ajustes -> Acerca de`;
- representative populated character used;
- Stage A/B/C already recorded separately.

## Stage D — phone portrait card-column evidence

Owner observations:

- 1 column: comfortable;
- 2 columns: comfortable;
- 3+ columns: cards become uncomfortably cramped/readability suffers;
- buttons/quick actions remain comfortably tappable at every tested portrait density;
- no overlap, clipping or strange card-layout behavior observed.

Classification: readability/preference limit, not a functional defect. Higher opt-in densities need not be removed solely from this evidence.

## Stage D — phone landscape responsive-mode findings

### D-F01 — phone landscape enters tablet-like interaction model

**Severity:** major

On Redmi Note 11 Pro 5G landscape, the character UI presents like a tablet rather than a widened phone. The owner expects phone interaction patterns to remain phone-like while taking better advantage of horizontal width.

Observed manifestations:

- tabs become vertical instead of remaining horizontal;
- in `Equipo`, a large persistent right-side `Equipo nuevo` pane appears;
- the right-side pane is visually unattractive and consumes scarce landscape-phone space;
- no overlap or clipping occurs, but the physical space remains limited because this is still a phone.

Expected direction for later repair: preserve the phone interaction model in phone landscape; use extra width without switching wholesale to tablet navigation/master-detail behavior.

### D-F02 — Equipo right-side creation pane is inappropriate on phone landscape

**Severity:** major

`Equipo` shows a massive `Equipo nuevo` section on the right in landscape. This pane does not exist in the normal phone presentation and occupies a disproportionate share of the screen.

Treat separately from the general breakpoint issue so the Equipo composition can be re-audited after any responsive-shell repair.

### D-F03 — vertical tabs on phone landscape

**Severity:** major

Character tabs switch from horizontal to vertical in phone landscape. Owner preference/requirement is that phone landscape retain horizontal phone navigation while using width more efficiently.

### D-F04 — multi-column card reordering is effectively one-dimensional

**Severity:** needs later implementation/UX audit

Cards can be moved up/down but not laterally across visible columns. A multi-column grid therefore behaves like a one-dimensional reorder list despite its two-dimensional presentation.

Do not assume this is automatically a defect; later repair analysis should determine whether lateral drag should map to adjacent logical positions and whether current reorder semantics remain understandable after reflow.

### D-F05 — rotation preserves tab but resets scroll position

**Severity:** major usability finding

Tested in `Equipo` with the owner scrolled away from the start before rotation.

- selected tab/surface survived rotation: PASS;
- approximate list/scroll position did **not** survive;
- after rotation, the screen returned to the start/top.

Search/filter retention was not separately re-reported in the final rotation observation, so do not infer PASS/FAIL for that subpoint beyond earlier staged intent.

### D-F06 — Gestión `Estado operativo` fixed block consumes almost all phone-landscape viewport

**Severity:** major

On phone landscape in `Gestión`, the fixed `Estado operativo` box occupies almost the entire visible screen, leaving effectively no room to see the rest of the tab content.

This reinforces the broader landscape-phone issue but must remain a separate finding because it concerns fixed/sticky footprint, not only navigation/master-detail breakpoints. Re-check during later Stage F repair verification as well.

## Stage D outcome

Stage D is sufficiently covered on the primary phone for the current pre-QA audition.

Protected observations:

- portrait card density is comfortable through 2 columns;
- interaction targets remain comfortable;
- layout does not overlap/clip;
- phone landscape currently adopts tablet-like navigation/master-detail behavior and should not;
- rotation keeps the selected tab but loses scroll position;
- Gestión fixed `Estado operativo` consumes an unreasonable share of the landscape-phone viewport.

Per the owner QA workflow rule, these are recorded findings only. Do not implement piecemeal during the ongoing audition unless the owner explicitly requests a batched repair build or a blocking defect prevents meaningful continuation.

## Next action

Proceed to **Stage E — keyboard and editor window behavior** on the Redmi Note 11 Pro 5G using build `40700`.

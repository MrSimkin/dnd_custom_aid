# Phase 4A — P10 density and pseudo-icon repair — implementation closed

Date: 2026-09-12
Branch: `implementation/phase4a-successor-cycle`
Status: **IMPLEMENTATION CLOSED / READY FOR OWNER QA IN THE REPAIRED BUILD**

This checkpoint records implementation closure only. It does **not** claim owner/device acceptance.

## Authority

Primary repair authority:

- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_REPAIR_DECISIONS.md` — P10 only.
- `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md`.

P10 decision/closure evidence used to recover the finalized contract:

- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P10_ROUND1.md`.
- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P10_ROUND2.md`.
- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P10_CLOSURE.md`.
- P10 audit obligation in `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_FULL_APP_AUDIT_MATRIX.md`.

P9 continuity evidence only:

- `docs/checkpoints/2026-09-12_PHASE4A_P9_EDITOR_SIZING_IMPLEMENTATION_CLOSED.md`.

P1–P9 were not reopened.

## Baseline and continuation safety

Baseline remote HEAD before P10 continuation:

`e56ae5d348c9c51c82fb1b1f2fc78cc6742643a3`

The branch was confirmed at that exact SHA before substantive P10 work. Before the P10 production write it was rechecked at the same expected HEAD. Before this closure checkpoint write it was rechecked at:

`6f4c1c28bdba914020e629cef353f33a8ceca56e`

That SHA is exactly one forward commit from the baseline and changes only:

`androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterPcSuccessorSettingsV4.kt`

No merge, rebase, reset, force-push, history rewrite, branch synchronization, or `main` work was performed.

## Production state satisfying P10

P10 is satisfied by the combined current branch state. Two material P10 repairs already existed in the baseline inherited from earlier authorized work and were preserved rather than rewritten:

1. `fd272f1518a67ecca749d8f56936440b94765b57`
   - establishes the rebalanced Player density system;
   - legal density range is 50–150% with 100% as the balanced baseline;
   - compact values reduce whitespace and spacious values increase it;
   - density uses a perceptual whitespace mapping with usability floors rather than blindly scaling every UI dimension;
   - the Spanish settings copy communicates the direction (`Más compacto` ↔ `Más espacio`);
   - the preview demonstrates outer margin, inter-element/card spacing, and internal padding/rhythm using the same spacing system;
   - text size, icon size, and minimum touch-target size are not density-scaled;
   - an existing theme-grid textual check pseudo-icon was replaced by graphical rendering.

2. `69376cd7adf23275c0a5cb6de6e9224ac9f39f8a`
   - replaces shared collection-toolbar/filter pseudo-icons with proper graphical controls;
   - because the affected collection primitives are shared, the repair applies to the analogous Player collection surfaces instead of requiring duplicated per-tab churn.

The remaining P10 gap found during this continuation was repaired by:

3. `6f4c1c28bdba914020e629cef353f33a8ceca56e`
   - commit: `repair: finish P10 pseudo-icon cleanup`;
   - removes the obsolete, unrouted arrow-based tab-order implementation that still contained literal `↑/↓` controls;
   - preserves the actual P6 live route, which already uses `CharacterTabOrderDragSettingsV4` and the approved direct-drag grammar;
   - changes the live `+ Añadir` pseudo-icon/text hybrid to the legitimate textual action `Añadir`;
   - removes only helpers/imports made dead by deletion of that obsolete arrow implementation.

## Exact P10 contract evidenced by current repository state

The current branch now implements the authorized P10 contract as follows:

- density accepts 50%, 60%, …, 150%;
- values below 100% mean denser / less whitespace;
- 100% is the balanced normal baseline;
- values above 100% mean more spacious / more air;
- Spanish settings copy clearly communicates compact-versus-spacious direction;
- the density preview represents both external and internal spacing effects rather than only inter-card gaps;
- the shared spacing system participates in outer margins, inter-card/inter-element gaps, internal padding and content rhythm across the Player surfaces that consume it;
- P9 adaptive editor/dialog behavior remains intact and was not reinterpreted or reopened;
- density does not scale typography, icon dimensions, or minimum interaction targets;
- shared collection add/check controls and the theme-grid check use graphical rendering rather than textual pseudo-icons;
- the remaining live `+ Añadir` hybrid has been removed in favor of the legitimate word action `Añadir`;
- tab ordering follows the already-approved P6 direct-drag interaction; no arrow controls, reorder mode, or separate `Listo`/`Done` step were introduced;
- the stale alternative arrow implementation was removed so it cannot be accidentally routed back later.

The P10 transversal audit was resolved through the shared Player primitives and live routing surfaces rather than by manufacturing per-tab edits. Known P10 pseudo-icon problem families were checked at their shared/current implementation points: collection toolbar/filter controls, app-settings/theme selection indication, PC settings tab-order routing/reorder implementation, and successor-settings add actions. Legitimate textual actions remain textual.

## Regression boundaries

This repair deliberately did **not**:

- reopen or rewrite P6 direct drag;
- reopen P7 provenance;
- reopen P8 background-photo UX;
- reopen or reinterpret P9 adaptive editor sizing;
- force P9 `expanded=true` onto any caller;
- scale text, icons, or minimum touch targets with density;
- redesign PC Settings beyond the P10-authorized control cleanup;
- implement the separate fixed/sticky landscape policy;
- change unrelated Spanish user-facing terminology;
- perform unrelated cleanup;
- begin P11.

## Automated evidence

Production commit Scaffold workflow:

- workflow run: `34700110174`;
- branch: `implementation/phase4a-successor-cycle`;
- head SHA: `6f4c1c28bdba914020e629cef353f33a8ceca56e`;
- backend job: **SUCCESS**;
- Kotlin/shared tests/build job: **SUCCESS**;
- Android debug assembly: **SUCCESS**;
- Android debug APK upload: **SUCCESS**;
- uploaded artifact: `dnd-custom-aid-debug-apk`.

The closure checkpoint commit itself must also pass the final Scaffold workflow before its SHA is used as the next continuation anchor.

## Owner/device QA boundary

Repository state and automated CI establish implementation closure, not physical acceptance.

The following remain for owner/device QA in the repaired build where automation cannot prove perceptual or tactile quality:

- whether 50%, 100%, and 150% feel meaningfully and proportionally distinct in actual Player use;
- whether 100% visually reads as the intended balanced baseline;
- preview fidelity against real Player screens;
- phone/tablet and portrait/landscape rendering details;
- visual rhythm inside real dialogs/editors at the density extremes;
- IME/navigation-bar behavior in device use, subject to the already-closed P9 contract;
- direct-drag tactile behavior, reflow feel, and release persistence on device;
- any animation/rendering nuance that the existing automated harness does not physically exercise.

A problem found there would be new QA evidence; this checkpoint does not pre-accept those observations.

## P11 boundary

**P11 was not started.**

The next repair point must not begin until P10 owner/device QA is handled according to the governing repair workflow or an explicit continuation instruction authorizes the next step.

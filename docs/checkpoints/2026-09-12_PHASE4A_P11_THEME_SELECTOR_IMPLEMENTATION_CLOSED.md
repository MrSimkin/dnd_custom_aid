# Phase 4A — P11 theme selector repair — implementation closed

Date: 2026-09-12
Branch: `implementation/phase4a-successor-cycle`
Status: **IMPLEMENTATION CLOSED / READY FOR OWNER QA IN THE REPAIRED BUILD**

This checkpoint records implementation closure only. It does **not** claim owner/device acceptance.

## Authority

Primary P11 repair authority:

- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P11_THEME_SELECTOR_CLOSED.md`.
- `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md`.

Continuity evidence only:

- `docs/checkpoints/2026-09-12_PHASE4A_P10_DENSITY_PSEUDO_ICON_IMPLEMENTATION_CLOSED.md`.

P1–P10 were not reopened.

## Baseline and continuation safety

Remote branch HEAD before P11 continuation:

`0555ea51e29f146dcfe86e334f0866587a09f94e`

The branch was confirmed at that exact SHA before substantive P11 inspection and again immediately before this closure write.

No merge, rebase, reset, force-push, history rewrite, branch synchronization, or work on `main` was performed.

## Finding: no new P11 production churn was required

Inspection of current primary repository state established that the full authorized P11 implementation already exists on the active branch. The material repair was introduced earlier by:

`fd272f1518a67ecca749d8f56936440b94765b57`

Commit:

`repair: rebalance density scale and restore theme palette shorthand`

That production commit modified `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt` and, in addition to the P10 density work, implemented the remaining P11 theme-selector repair.

The immediate parent state `a34c317f7d007395e215f3ebff788c02a541a038` provides direct before/after evidence of the original P11 defect:

- theme options were hard-coded to two columns;
- cards showed only the theme name;
- selected state used literal `Text("✓")`;
- the separate larger `Vista previa · ficha` already existed.

The current state preserves that larger realistic preview while replacing the defective compact selector behavior with the owner-approved design.

No additional production code was written during this P11 continuation because doing so would have manufactured churn against an already-satisfied contract.

## Exact P11 contract evidenced by current repository state

### Theme identity shorthand

`ThemeChoicePicker` iterates every `AppThemeChoice` entry. Each option card displays:

- the existing Spanish theme name;
- one swatch for the resolved theme `background`;
- one swatch for the resolved theme `surface`;
- one swatch for the resolved theme `primary` color.

The three swatches are compact palette identity shorthand only. No miniature fake UI preview is embedded in each option card.

### One separate realistic/live preview

`SettingsSheetPreview(preferences)` remains a separate larger section labelled `Vista previa · ficha` after the compact theme grid.

It renders real Material components from the currently active `MaterialTheme`, including representative header/surface/stat/text-field content. It was not duplicated into every theme option.

### Immediate selection and preview update

`ThemePreviewCard` invokes `onSelect` directly from the card click. There is no separate Apply/Save step.

`MainActivity` owns `preferences` as Compose state. `updatePreferences` immediately:

1. replaces the in-memory state;
2. persists the updated preferences;
3. causes `DndCustomAidTheme(preferences)` and `AppSettingsScreen(preferences)` to recompose from the new state.

Therefore the app theme and the separate preview update as part of the same selection-state change.

### `Sistema` resolves to the actual current system appearance

Both the active application theme and each theme card use the same composable `resolveColorScheme(choice)` path.

For `AppThemeChoice.SYSTEM`, that function evaluates `isSystemInDarkTheme()` and returns either the actual dark or light Material color scheme accordingly.

Because `ThemePreviewCard` derives its swatches from `resolveColorScheme(choice)`, the `Sistema` shorthand shows the currently resolved appearance rather than a half-light/half-dark abstract palette. The separate live preview is rendered by the same active resolved `MaterialTheme`.

### Graphical selected-state indicator

The former literal `Text("✓")` is absent from the current theme option implementation.

Each theme card now uses a graphical circular selected-state indicator with selected/unselected stroke/fill treatment, plus selected border treatment on the option card. This preserves the P10 pseudo-icon closure instead of reintroducing a text glyph.

### Responsive columns

`ThemeChoicePicker` derives its column count from the current configuration width:

- below 600 dp: 2 columns;
- 600–899 dp: 3 columns;
- 900 dp and above: 4 columns.

Rows are chunked by that derived count and incomplete final rows receive weighted spacers, so wider displays gain columns instead of stretching a fixed pair of giant cards across the available width.

This reuses the accepted responsive-layout direction without introducing a separate device taxonomy.

## Implementation surfaces involved

The P11 repair is bounded to the application theme/settings path:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/UiPreferences.kt`
  - `AppThemeChoice`
  - `resolveColorScheme`
  - `AppSettingsScreen`
  - `SettingsSheetPreview`
  - `ThemeChoicePicker`
  - `ThemePreviewCard`
  - `ThemePaletteSwatchV4`
- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/MainActivity.kt`
  - immediate preference state update/persistence
  - `DndCustomAidTheme(preferences)` recomposition path

No Player domain tabs, P6 drag implementation, P7 provenance, P8 photo UX, P9 editor sizing, or P10 density behavior required modification for P11.

## Automated evidence and harness boundary

The current P11 production state is included in the already-green Scaffold run on continuation baseline `0555ea51e29f146dcfe86e334f0866587a09f94e`:

- workflow run: `34700326335`;
- backend: **SUCCESS**;
- Kotlin/shared tests and builds: **SUCCESS**;
- Android debug assembly: **SUCCESS**;
- Android debug APK upload: **SUCCESS**.

The repository currently has no Android UI/instrumentation test source set. Scaffold runs `:shared:desktopTest`, `:androidApp:assembleDebug`, `:desktopApp:build`, backend type-check, and APK upload. Accordingly, this checkpoint does **not** misrepresent compile/build automation as physical visual verification.

The closure checkpoint commit itself must pass a final Scaffold workflow before its SHA becomes the next continuation anchor.

## Regression boundaries

This closure deliberately does **not**:

- redesign the theme catalog;
- create miniature fake previews inside each theme card;
- add an Apply/Save confirmation step;
- replace `Sistema` with an abstract split light/dark representation;
- reintroduce literal text pseudo-icons;
- alter theme names or unrelated Spanish UI terminology;
- alter P10 density behavior;
- reopen P1–P10;
- begin P12.

## Owner/device QA boundary

Repository inspection plus green build evidence establish implementation closure, not perceptual/device acceptance.

Owner/device QA remains appropriate for:

- whether the three swatches give sufficiently fast visual theme identification on a real device;
- whether the graphical selected state is visually obvious enough without clutter;
- whether 2/3/4-column layouts remain comfortably readable across actual phone/tablet widths and font-scale combinations;
- whether `Sistema` visibly tracks the device's resolved light/dark appearance as expected during real configuration changes;
- whether the larger `Vista previa · ficha` feels representative and updates perceptibly/immediately when tapping theme choices;
- touch behavior, rendering, contrast, and other physical presentation details that the existing automated harness does not exercise.

A problem found there would be new QA evidence; it is not pre-accepted by this implementation checkpoint.

## P12 boundary

**P12 was not started.**

# Phase 4A — P12 contextual help repair — implementation closed

Date: 2026-09-12
Branch: `implementation/phase4a-successor-cycle`
Status: **IMPLEMENTATION CLOSED / AUTOMATED GREEN / READY FOR OWNER-DEVICE QA**

This checkpoint records implementation and automated-verification closure only. It does **not** claim owner/device acceptance.

## Authority

Primary P12 repair authority:

- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P12_CLOSED.md`;
- `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md`.

Required pre-P12 Material 3 review:

- `docs/checkpoints/2026-09-12_PHASE4A_MATERIAL3_AUDIT_BEFORE_P12.md`.

Continuity evidence:

- `docs/checkpoints/2026-09-12_PHASE4A_P11_THEME_SELECTOR_IMPLEMENTATION_CLOSED.md`.

P1–P11 were not reopened except for the bounded P3/P4 Material 3 geometry repairs already recorded by the pre-P12 audit.

## Baseline and continuation safety

Last automated-green pre-P12 checkpoint:

`f1088b7d9355f36912d4b5910572579056a39028`

Initial P12 production implementation:

`97e95d39f415f0d9b9b9130443db5f5fe25686d7`

`feat: implement anchored contextual help for P12`

Final P12 production compatibility fix:

`eebc1b0c8c3b8f20e56abc6c6558d37f9e5e4652`

`fix: align P12 RichTooltip with project Material3 API`

The compatibility fix is exactly one production-line deletion relative to the first closure-checkpoint state: removal of the unsupported `caretSize` named argument from `RichTooltip`. No behavior/model scope was added.

The net P12 production surface remains bounded to:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterHelpProvenanceV4.kt`.

Temporary guarded implementation/audit workflow commits left no net workflow or unrelated production-file residue.

No merge, rebase, reset, force-push, history rewrite, branch synchronization, work on `main`, or DM implementation was performed.

## Full-app contextual-help audit

P12 required a full-app audit because the defect lives in a shared UI primitive.

The audit found exactly **18 production usages** of `CharacterHelpV4` and no production literal `ⓘ` bypass. All audited contextual-help call sites route through the shared primitive rather than through screen-local tooltip implementations.

Representative audited surfaces include General, Conjuros/spell source, class identity/options, Rasgos, Equipo, provenance/origin, character management, Artificio, Forms, companions, PC settings/preferences and Notas.

The existing call sites place help at the relevant field/row/card/section boundary. No local call-site rewrite was justified by the audit; repairing the shared primitive is the smallest coherent app-wide fix.

## P12 implementation

### `Siempre visible` preserved

`CharacterHelpModeV4.ALWAYS_VISIBLE` remains the existing inline `Text` presentation using `bodySmall` and `onSurfaceVariant`.

P12 does not redesign or temporarily emulate that mode.

### Real anchored rich tooltip

`CharacterHelpModeV4.INFO` no longer opens explanatory help through `DropdownMenu`.

It now uses the Material 3 tooltip primitives:

- `TooltipBox` for the anchor relationship;
- `rememberTooltipState(isPersistent = true)` for explicit persistent show/dismiss state;
- `RichTooltip` for multiline tooltip presentation;
- `TooltipDefaults.rememberTooltipPositionProvider()` for anchored popup positioning;
- `onDismissRequest` for popup dismissal handling;
- `focusable = true` so popup/Back behavior is owned by the tooltip popup rather than page layout;
- explicit trigger-toggle logic so reactivating the info control dismisses an already visible tooltip.

This is a floating popup anchored to the triggering control. It does not insert explanatory content into the normal page layout and therefore does not reflow the surrounding Player screen.

### Bounded long-help behavior

The rich tooltip is explicitly bounded to:

- maximum width: `320.dp`;
- maximum height: `280.dp`.

Its text body uses vertical scrolling, so unusually long contextual help remains contained rather than becoming an unbounded floating wall of text.

### Graphical compact info control preserved

The trigger remains `CharacterInfoIconButtonV4`:

- graphical `Canvas` information mark, not a literal text pseudo-icon;
- visible icon geometry `18.dp`;
- compact button geometry `36.dp`;
- semantic content description switches between `Mostrar ayuda` and `Ocultar ayuda` according to tooltip visibility.

P12 therefore preserves the P10 pseudo-icon repair rather than reintroducing `Text("ⓘ")`.

## Material 3 compatibility finding and repair

The first normal closure Scaffold run exposed one concrete API-version mismatch:

- closure-checkpoint commit: `cb4e66df204f65580b7d4ff08057ea35d1a91e5e`;
- Scaffold run: `34715068306`;
- backend: **SUCCESS**;
- Kotlin: **FAILURE** at `:androidApp:compileDebugKotlin`;
- exact compiler error: `No parameter with name 'caretSize' found` in `CharacterHelpProvenanceV4.kt`.

No other Kotlin compile defect was reported before that failure.

The repair commit `eebc1b0c8c3b8f20e56abc6c6558d37f9e5e4652` removed only the unsupported named argument. The project-supported `TooltipBox`/`RichTooltip`, persistent state, bounds, scrolling, focus/dismiss and trigger-toggle behavior were retained.

This validates the pre-P12 Material 3 audit conclusion: use the purpose-built M3 primitive where it matches the product contract, but adapt to the actual dependency API rather than assuming a different library signature.

## Automated verification — GREEN

Normal Scaffold run on final production state:

- commit: `eebc1b0c8c3b8f20e56abc6c6558d37f9e5e4652`;
- workflow run: `34715212719`;
- backend/type-check: **SUCCESS**;
- Kotlin/shared tests and builds: **SUCCESS**;
- exact Gradle gate: `:shared:desktopTest :androidApp:assembleDebug :desktopApp:build --stacktrace`;
- result: `BUILD SUCCESSFUL in 1m 24s`;
- tasks: `59 actionable tasks: 45 executed, 14 from cache`;
- Android debug assembly: **SUCCESS**;
- Android debug APK upload: **SUCCESS**.

Generated QA artifact:

- name: `dnd-custom-aid-debug-apk`;
- artifact ID: `10304886349`;
- ZIP size: `13,584,042` bytes;
- artifact digest: `sha256:e474abfa9b1d0286a3de74738fced140f8998675dbde70d1056fa58bbfccaf33`;
- artifact expiry reported by GitHub Actions: `2026-12-11T19:47:59Z`.

The Kotlin build emits a non-blocking deprecation warning for the no-argument `rememberTooltipPositionProvider` overload; it is accepted by the project's current dependency set and does not fail the gate. Changing popup positioning APIs solely to silence that warning is outside this bounded repair unless owner/device QA demonstrates a behavioral problem.

## Regression boundaries

This closure deliberately does **not**:

- change contextual-help wording/content ownership;
- change `Siempre visible` presentation;
- use inline expansion as a substitute for tooltip mode;
- use an ordinary dropdown/menu as the help surface;
- open a full dialog or bottom sheet for normal contextual help;
- introduce a screen-local help implementation;
- reintroduce a literal `ⓘ` pseudo-icon;
- alter P1–P11 behavior outside the already-recorded P3/P4 Material 3 geometry hardening;
- begin any DM-side implementation;
- merge the continuation branch to `main`.

## Owner/device QA boundary

The repository has no physical-device UI harness capable of proving perceptual popup placement or touch behavior. Automated success is technical evidence, not owner/device acceptance.

Owner/device QA must still establish on the repaired build:

- compact/discreet graphical info control with a comfortably usable real touch target;
- obvious adjacency/association between the control and the title/label/field/section it explains;
- a true floating tooltip with no page-layout reflow;
- safe placement near screen edges and in phone landscape;
- readable bounded multiline content on phone and tablet widths;
- usable scrolling for long help text;
- dismissal by outside tap;
- dismissal by Back;
- dismissal by reactivating the trigger;
- acceptable behavior at larger application/font scale;
- readable contrast under representative themes;
- `Siempre visible` remaining visually/behaviorally unchanged.

Physical phone/tablet PASS/FAIL must not be inferred from compile/build automation.

## Exact continuation point

1. This P12 production state is automated-green and ready for owner/device QA.
2. The present documentation-finalization commit must also pass normal Scaffold before becoming the durable branch continuation anchor; because it is documentation-only, any unexpected failure must be investigated rather than attributed to P12 without evidence.
3. Use artifact `10304886349` (or a later bit-equivalent/source-equivalent artifact from the documentation-only finalization run) for owner QA.
4. Do **not** begin a new repair point or DM implementation merely because historical later-numbered checkpoint files exist; this authorized repair sequence closes at P12.
5. Do not merge to `main` before owner QA and explicit Phase 4A closure.

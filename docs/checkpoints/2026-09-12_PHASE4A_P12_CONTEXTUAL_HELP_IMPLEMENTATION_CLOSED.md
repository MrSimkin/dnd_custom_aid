# Phase 4A — P12 contextual help repair — implementation closed

Date: 2026-09-12
Branch: `implementation/phase4a-successor-cycle`
Status: **IMPLEMENTATION CLOSED / FINAL SCAFFOLD REQUIRED BEFORE OWNER QA ANCHOR**

This checkpoint records implementation closure only. It does **not** claim owner/device acceptance.

## Authority

Primary P12 repair authority:

- `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_P12_CLOSED.md`.
- `docs/checkpoints/2026-09-11_PHASE4A_REPAIR_IMPLEMENTATION_AUTHORIZED.md`.

Required pre-P12 Material 3 review:

- `docs/checkpoints/2026-09-12_PHASE4A_MATERIAL3_AUDIT_BEFORE_P12.md`.

Continuity evidence:

- `docs/checkpoints/2026-09-12_PHASE4A_P11_THEME_SELECTOR_IMPLEMENTATION_CLOSED.md`.

P1–P11 were not reopened except for the bounded P3/P4 Material 3 geometry repairs already recorded by the pre-P12 audit.

## Baseline and continuation safety

The last automated-green pre-P12 checkpoint is:

`f1088b7d9355f36912d4b5910572579056a39028`

The P12 production implementation commit is:

`97e95d39f415f0d9b9b9130443db5f5fe25686d7`

Commit:

`feat: implement anchored contextual help for P12`

The active branch was confirmed at that exact P12 source SHA before this checkpoint was written.

A direct compare from `f1088b7d9355f36912d4b5910572579056a39028` to `97e95d39f415f0d9b9b9130443db5f5fe25686d7` shows one net production file changed:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterHelpProvenanceV4.kt` — 36 additions / 19 deletions.

Temporary guarded implementation/audit workflow commits left no net workflow or other production-file delta in that comparison.

No merge, rebase, reset, force-push, history rewrite, branch synchronization, work on `main`, or DM implementation was performed.

## Full-app contextual-help audit

P12 required a full-app audit because the defect lives in a shared UI primitive.

The audit found exactly **18 production usages** of `CharacterHelpV4`, spread across the Player application surfaces, and no production literal `ⓘ` bypass. All audited contextual-help call sites route through the shared primitive rather than through screen-local tooltip implementations.

Representative audited surfaces include:

- General;
- Conjuros and spell-source editing;
- class identity/options;
- Rasgos;
- Equipo;
- provenance/origin;
- character management;
- Artificio;
- Forms;
- companions;
- PC settings/preferences;
- Notas.

The existing call sites place help at the relevant field/row/card/section boundary. No local call-site rewrite was justified by the audit; repairing the shared primitive is the smallest coherent app-wide fix.

## P12 implementation

### `Siempre visible` preserved

`CharacterHelpModeV4.ALWAYS_VISIBLE` remains the existing inline `Text` presentation using `bodySmall` and `onSurfaceVariant`.

P12 does not redesign or temporarily emulate that mode.

### Real anchored rich tooltip

`CharacterHelpModeV4.INFO` no longer opens help content through `DropdownMenu`.

It now uses the Material 3 tooltip primitives:

- `TooltipBox` for the anchor relationship;
- `rememberTooltipState(isPersistent = true)` for explicit persistent show/dismiss state;
- `RichTooltip` for multiline rich-tooltip presentation;
- `TooltipDefaults.rememberTooltipPositionProvider()` for adaptive anchored positioning;
- `onDismissRequest` for outside-dismiss handling;
- a focusable tooltip surface so platform Back/outside behavior is delegated to the popup primitive rather than page reflow;
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

## Material 3 boundary

The pre-P12 audit established that Material 3 itself is not a general defect source. P12 follows that conclusion: it uses the purpose-built Material 3 tooltip primitive because the product contract actually calls for anchored popup behavior, while continuing to apply explicit product bounds and the existing custom graphical trigger.

P12 does **not** globally disable Material 3 touch-size protections or replace unrelated M3 components.

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

## Automated evidence and final-gate boundary

Before P12, checkpoint `f1088b7d9355f36912d4b5910572579056a39028` passed the normal Scaffold boundary, including:

- backend/type-check: **SUCCESS**;
- `:shared:desktopTest`: **SUCCESS**;
- `:androidApp:assembleDebug`: **SUCCESS**;
- `:desktopApp:build`: **SUCCESS**;
- Android debug APK upload: **SUCCESS**.

That green run establishes the P1–P11/P3–P4 pre-P12 baseline only. It is not evidence that the new tooltip source compiles or behaves correctly.

**This closure checkpoint commit itself must pass the normal Scaffold workflow before its SHA becomes the next continuation/owner-QA anchor.** A failure must be repaired only at the concrete P12 boundary and revalidated; no PASS may be inferred from this document.

The repository has no physical-device UI harness capable of proving perceptual popup placement or touch behavior. Automated success therefore remains technical evidence, not owner/device acceptance.

## Owner/device QA boundary after a green final Scaffold

Owner/device QA must still establish, on the repaired build:

- compact/discreet graphical info control with a comfortably usable real touch target;
- obvious adjacency/association between the control and the title/label/field/section it explains;
- a true floating tooltip with no page-layout reflow;
- safe adaptive placement near screen edges and in phone landscape;
- readable bounded multiline content on phone and tablet widths;
- usable scrolling for long help text;
- dismissal by outside tap;
- dismissal by Back;
- dismissal by reactivating the trigger;
- acceptable behavior at larger application/font scale;
- readable contrast under representative themes;
- `Siempre visible` remaining visually/behaviorally unchanged.

Physical phone/tablet acceptance must not be inferred from compile/build automation.

## Exact continuation point

1. Run the normal Scaffold checks on this checkpoint commit.
2. If any gate fails, repair only the demonstrated P12 defect and rerun the gate.
3. If the gate is green, treat that green commit as the repaired Player build anchor and hand the generated debug APK to owner/device QA.
4. Do **not** begin a new repair point or DM implementation merely because historical later-numbered checkpoint files exist; this authorized repair sequence closes at P12.
5. Do not merge to `main` before owner QA and explicit Phase 4A closure.

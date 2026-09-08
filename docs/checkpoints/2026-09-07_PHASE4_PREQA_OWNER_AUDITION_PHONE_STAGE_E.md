# Phase 4 pre-QA owner audition — phone Stage E

**Date:** 2026-09-07  
**Status:** OWNER PHONE STAGE E IN PROGRESS; first keyboard/editor finding recorded  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Review identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary phone:** Redmi Note 11 Pro 5G

## Preconditions

- exact review build already verified in `Ajustes -> Acerca de`;
- Stage A/B/C/D recorded separately;
- Stage D is sufficiently covered on the primary phone;
- per owner workflow, findings are accumulated and not repaired piecemeal during the audition.

## Stage E1 — Notas long general text editor

Owner tested an extremely long general-notes text with the software keyboard visible.

### E-F01 — long text remains reachable under IME

**Result:** PASS

- owner can reach all text by scrolling, even with extremely long content;
- no content-length threshold was encountered that made the active text unreachable.

### E-F02 — editor actions unreachable while software keyboard is open

**Severity:** major

Although the text itself remains scrollable/reachable, the required editor action buttons are not reachable while the software keyboard remains visible.

This fails the Stage E pass criterion that cancel/save/apply actions remain reachable without requiring keyboard dismissal.

Later repair should preserve intrinsic touch targets while making the action area reachable via IME-aware scrolling, pinned actions, or another phone-appropriate composition.

### E-F03 — editor disappears on rotation to landscape and reappears on return to portrait

**Severity:** major

With the editor open:

- rotating the Redmi Note 11 Pro 5G to landscape makes the editor window disappear;
- rotating back to portrait makes the editor window reappear.

The observed behavior suggests editor visibility/composition is not stable across the phone's responsive mode change. Do not infer that editor state is lost, because the editor returns when portrait is restored.

This finding should be considered together with Stage D's broader phone-landscape/tablet-breakpoint issue, but remains separate because it affects an active editing workflow and can interrupt user action.

## Stage E current outcome

Stage E is **not complete**. The first representative editor already demonstrates two major IME/orientation issues:

1. action buttons are unreachable while the keyboard is visible;
2. the active editor disappears in landscape and returns in portrait.

Continue Stage E across other representative editor families to determine whether this is shared infrastructure behavior or surface-specific.

## Next action

Proceed to **Stage E2 — titled note editor** on the Redmi Note 11 Pro 5G using build `40700`.

Open/create a titled note, keep the software keyboard visible, use enough title/body text to require scrolling, and check whether the focused fields plus cancel/save actions remain reachable. Also rotate once with the editor open and note whether the same disappear/reappear behavior occurs.

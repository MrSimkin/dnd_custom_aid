# Phase 4 pre-QA owner audition — phone Stage E

**Date:** 2026-09-07  
**Status:** OWNER PHONE STAGE E IN PROGRESS; E1 corrected to PASS and E2 findings recorded  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Review identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary phone:** Redmi Note 11 Pro 5G

## Preconditions

- exact review build already verified in `Ajustes -> Acerca de`;
- Stage A/B/C/D recorded separately;
- Stage D is sufficiently covered on the primary phone;
- per owner workflow, findings are accumulated and not repaired piecemeal during the audition.

## Stage E1 — Notas long general text editor

**Result:** PASS

Owner clarified that the prior reported action/rotation failures were not from the general Notes editor. The general long-text editor works correctly for the current phone audition.

Protected result:

- long text remains reachable by scrolling with the software keyboard visible;
- required interaction is usable for the owner's E1 test;
- no E1-specific action-reachability or rotation defect is recorded.

## Stage E2 — titled note editor

Owner tested an extremely long titled-note body with the software keyboard visible.

### E-F01 — titled-note text remains reachable under IME

**Result:** PASS

- owner can reach all text by scrolling, even with extremely long content;
- no content-length threshold was encountered that made the active text unreachable.

### E-F02 — titled-note actions unreachable while software keyboard is open

**Severity:** major

Although the text itself remains scrollable/reachable, the required editor action buttons are not reachable while the software keyboard remains visible.

This fails the Stage E pass criterion that cancel/save/apply actions remain reachable without requiring keyboard dismissal.

Later repair should preserve intrinsic touch targets while making the action area reachable via IME-aware scrolling, pinned actions, or another phone-appropriate composition.

### E-F03 — titled-note editor disappears on rotation to landscape and reappears on return to portrait

**Severity:** major

With the titled-note editor open:

- rotating the Redmi Note 11 Pro 5G to landscape makes the editor window disappear;
- rotating back to portrait makes the editor window reappear.

Do not infer that editor state is lost, because the editor returns when portrait is restored. This should be considered alongside Stage D's phone-landscape/tablet-breakpoint finding, while remaining a separate active-editing workflow defect.

## Stage E current outcome

Stage E is **not complete**.

Current distinction is important:

- E1 general Notes editor: PASS;
- E2 titled-note editor: text scrolling PASS, but action reachability and rotation stability FAIL/major.

Continue Stage E across other representative editor families to determine whether the E2 failure is specific to the titled-note dialog or shared with other modal editors.

## Next action

Proceed to **Stage E3 — Equipo editor** on the Redmi Note 11 Pro 5G using build `40700`.

Open an existing equipment item or create one, keep the software keyboard visible, use enough editable content to require scrolling where possible, confirm focused fields plus cancel/save actions remain reachable, and rotate once with the editor open to observe whether the same disappear/reappear behavior occurs.

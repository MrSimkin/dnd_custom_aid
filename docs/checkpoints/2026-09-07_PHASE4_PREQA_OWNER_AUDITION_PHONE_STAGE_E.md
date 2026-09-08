# Phase 4 pre-QA owner audition — phone Stage E

**Date:** 2026-09-07  
**Status:** OWNER PHONE STAGE E IN PROGRESS; E1 PASS, E2-E5 shared IME/editor failures recorded  
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

### E-F03 — titled-note editor disappears on rotation to landscape and reappears on return to portrait

**Severity:** major

With the titled-note editor open:

- rotating the Redmi Note 11 Pro 5G to landscape makes the editor window disappear;
- rotating back to portrait makes the editor window reappear.

Do not infer that editor state is lost, because the editor returns when portrait is restored. This should be considered alongside Stage D's phone-landscape/tablet-breakpoint finding, while remaining a separate active-editing workflow defect.

## Stage E3 — Equipo editor

**Result:** FAIL / major IME ergonomics issue, same core pattern as E2 with additional field-navigation evidence.

Owner observations with software keyboard open:

- the currently focused field can be brought into view;
- when a field high in the form is focused, lower fields may remain hidden behind the keyboard and cannot be reached/seen sufficiently without dismissing the keyboard;
- concrete example: from `Nombre`, the owner cannot practically jump to `Notas` while keeping the keyboard open because lower form content is covered;
- if the owner focuses `Notas`, the field itself becomes visible, but the editor action buttons then become covered/unreachable;
- therefore field visibility and action visibility trade places rather than the whole editor remaining safely navigable above the IME;
- rotation behavior matches E2: the editor disappears in landscape and returns when rotated back to portrait.

Classification: **major**. The Stage E criterion requires focused fields plus cancel/save/apply to remain reachable with the keyboard visible; Equipo does not satisfy that requirement.

## Stage E4 — Rasgos editor

**Result:** FAIL / major; same behavior as E3.

Owner reports the Rasgos editor reproduces the Equipo pattern:

- lower fields can be obscured while a higher field is focused;
- moving to a lower field can bring that field into view, but leaves required Save/Cancel actions covered/unreachable;
- keyboard dismissal is required to freely traverse the full form/action area;
- rotation behavior is the same disappear-in-landscape / return-in-portrait pattern observed in E2/E3.

This is no longer credible as an isolated surface-specific defect. E2, E3 and E4 all show the same modal-editor IME/orientation family of failures.

## Stage E5 — Conjuro editor

**Result:** FAIL / major for the shared IME/orientation behavior; one additional numeric-entry usability defect recorded.

Owner reports E5 reproduces the same behavior as E3/E4:

- upper-to-lower field traversal is not continuously practical with the keyboard visible;
- Save/Cancel become covered/unreachable;
- rotation exhibits the same active-editor discontinuity already observed in E2-E4.

### E-F04 — spell level leading-zero input is not naturally replaceable

**Severity:** minor usability / input-normalization defect

The spell level field starts at `0`. If the owner directly types `5`, the attempted value behaves as `05` and does not become level `5`; the owner reasonably expects `05` to be semantically normalized to `5`, or equivalently that typing a new level over the default zero should work without first manually erasing the zero.

Read-only active-branch inspection confirms the behavior is explained by the current helper:

`raw.filter(Char::isDigit).take(1)`

Because the helper keeps only the first digit, raw `05` becomes `0`. Later repair should make one-digit 0-9 level entry naturally replace/normalize the default zero while retaining range validation.

## Technical characterization note — shared editor guarantee appears regressed/incomplete

Historical Batch B1a explicitly established `CharacterImeSafeEditorDialog` with the intended guarantee that editable content scrolls while `Guardar` / `Cancelar` remain reachable above the keyboard. B1a also migrated titled Notes into that shared pattern and stated that later work should migrate remaining character-sheet editors, including Equipment and Rasgos.

Read-only inspection of the current primitive confirms the intended IME-aware structure still exists: the full dialog uses `imePadding()` and `navigationBarsPadding()`, with scrollable editor content followed by an action row. Nevertheless, current real-device evidence in build `40700` contradicts the intended runtime guarantee across titled Notes, Equipo, Rasgos and Conjuros.

The repeated behavior should therefore be treated first as a shared infrastructure/regression candidate for later repair analysis rather than four unrelated local fixes. Do not overstate the exact root cause yet: later implementation analysis should determine whether IME inset ownership, the weighted scroll container/action-row composition, focus-driven bring-into-view behavior, or interaction with the phone-landscape responsive switch is responsible.

The Conjuros implementation also confirms a credible connection with the Stage D phone-landscape finding: non-wide mode uses `CharacterImeSafeEditorDialog`, while wide mode uses a persistent right-side editor panel. A phone that incorrectly crosses into `wide` behavior on landscape can therefore change editor composition during rotation. This is a strong architectural lead, not yet a final root-cause determination.

## Stage E current outcome

Stage E is **not complete**.

Current results:

- E1 general Notes editor: PASS;
- E2 titled-note editor: text scrolling PASS, but action reachability and rotation stability FAIL/major;
- E3 Equipo editor: FAIL/major; full form cannot be traversed while retaining both field and action access above IME; same rotation failure;
- E4 Rasgos editor: FAIL/major; same E3 pattern and rotation failure;
- E5 Conjuro editor: FAIL/major for the same shared pattern; additional minor spell-level leading-zero normalization defect.

The repetition across four modal editor families is strong evidence of a shared issue.

## Next action

Proceed to **Stage E6 — one conditional-module editor** on the Redmi Note 11 Pro 5G using build `40700`.

Use any available conditional module (for example Técnicas, Metamagia, Pactos, Artífice, Formas or Compañeros). With the software keyboard visible, move from an upper field toward lower fields without dismissing the keyboard, verify whether all fields plus Save/Cancel remain reachable, and rotate portrait -> landscape -> portrait with the editor open. Record fields/action reachability, rotation behavior and any additional anomaly.

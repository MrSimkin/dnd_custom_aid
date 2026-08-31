# D-0054 — Run #180 Combat UX refinements

**Status:** Approved  
**Date:** 2026-08-31  
**Decision owner:** Project owner

## Context

During owner phone QA of the follow-up character-sheet build from Scaffold checks run #180, the `Combate` tab was functionally successful except for one partially accepted entry-editor interaction and several compact-layout/reordering observations.

The owner explicitly approved the following refinements for the next corrective build.

## 1. Shared-row vertical centering

When a quick-reference label or value in a logical row wraps to two lines, one-line neighboring text in that same row should be vertically centered within the shared row height.

The purpose is visual alignment and balance at narrow widths / larger text scales. Do not leave one-line cells visually top-biased just because another cell grew to two lines.

## 2. Attack/action editor must be IME-safe

The `Combate` add/edit editor must remain fully usable while the software keyboard is visible.

Requirements:

- important lower fields and Apply/Cancel actions must remain reachable;
- use scrolling, IME insets/padding, height constraints or another phone-safe mechanism as appropriate;
- the keyboard must not permanently cover required editor content.

## 3. Outside tap must not close/discard the attack editor

Current run #180 behavior allows a tap outside the keyboard/dialog area to dismiss the entire attack/action editor while the keyboard is visible.

Approved correction:

- an outside tap used to dismiss/escape the keyboard must not close the editor or lose its draft;
- editor dismissal should require an intentional action such as Apply or Cancel, or an equivalently explicit close control;
- preserving the in-progress attack/action draft is more important than scrim-tap convenience.

## 4. Drag-and-drop attack/action ordering

The owner prefers direct drag-and-drop ordering for attacks/actions rather than the current explicit up/down controls.

Approved behavior:

- reorder entries by drag-and-drop on phone;
- preserve the resulting explicit order in the existing durable `sortOrder` model;
- Save/reopen must restore exactly that user-defined order;
- do not alphabetize automatically.

Implementation may choose a phone-appropriate drag handle or whole-row long-press gesture, but accidental reorder should be reasonably avoidable.

## 5. QA disposition

Run #180 `Combate` results remain:

- quick-reference correctness: PASS;
- add attack/action: PARTIAL because of editor UX defects above;
- spell/non-weapon summaries, type selection, edit, existing reorder persistence, deletion, Save/reopen persistence, and rotation: PASS.

These changes are corrective UX work for the next build and do not require redesigning the underlying combat-entry data model.

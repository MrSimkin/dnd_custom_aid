# Phase 4 pre-QA owner audition — phone Stage F

**Date:** 2026-09-07  
**Status:** OWNER PHONE STAGE F IN PROGRESS; Gestión and Habilidades recorded  
**Active branch:** `implementation/phase4-preqa-ux-repair`  
**Review identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary phone:** Redmi Note 11 Pro 5G

## Purpose

Stage F checks whether fixed/sticky UI earns the permanent screen area it consumes. Findings are accumulated for a later repair build rather than implemented piecemeal during audition.

## F1 — Gestión fixed `Estado operativo`

**Result:** FAIL / major footprint and compactness problem.

Owner reports that the sticky/fixed operational block is too large and conflicts directly with the desired compact-app direction.

### F-F01 — fixed operational footprint too large

**Severity:** major UX/layout

- `Estado operativo` occupies excessive permanent vertical space on a phone;
- the problem becomes particularly severe in landscape, where prior Stage D evidence showed that the block can consume almost the entire useful viewport;
- later repair should preserve rapid access to operational state while materially reducing permanent height.

### F-F02 — death saves remain visible when General has been edited above 0 HP

**Severity:** major coherence UX; functional severity depends on persistence state

Owner observed that after changing PG above 0 in General, Gestión can still show `Salvaciones de muerte`, consuming substantial space.

Read-only architecture inspection shows General can be editing a draft while Gestión receives the persisted `stored` sheet. Therefore a plausible current explanation is cross-tab stale presentation before the General draft is saved. Later repair/audit must distinguish:

1. unsaved General edit -> Gestión still reading persisted value: cross-tab coherence UX defect;
2. saved General value > 0 -> Gestión still showing death saves: functional defect.

Regardless of trigger, the current death-save presentation is too tall for the compactness target.

### F-F03 — death-save controls should be compacted to one row

**Severity:** major UX/design backlog

Owner direction:

- do not use two rows when the same information can be made clearer in one;
- redesign death-save state into one compact horizontal row where practical;
- use clearer UX/icons rather than text-heavy vertical controls;
- preserve comprehension and tapability while reducing vertical footprint.

## F2 — Habilidades fixed selector + passive references

**Result:** PASS for justification; minor communication/compactness improvements requested.

Owner says the fixed band **earns the space it occupies**. The sticky concept should therefore be preserved.

### F-F04 — passive values are not explicitly labelled as passive

**Severity:** minor UX/content

The fixed references show Percepción, Perspicacia and Investigación, but the UI does not clearly say that these are **pasivas**. Later repair should make the semantic meaning explicit without adding unnecessary height.

### F-F05 — passive references can save vertical pixels

**Severity:** preference / compactness improvement

Owner proposes placing each label to the **left** of its numeric value instead of above it, with clear separators between the three values. This can reduce a small but worthwhile amount of permanent height while preserving readability.

Suggested direction, not frozen implementation:

`Percepción pasiva 14  |  Perspicacia pasiva 12  |  Investigación pasiva 13`

Exact wording/iconography can be optimized during repair; the protected intent is horizontal use of space rather than vertically stacked label/value pairs.

## Cross-cutting owner UX directions recorded during Stage F

These are not isolated Stage F defects but directly affect compactness and later repair design.

### Help-text presentation preference

Many cards contain useful explanatory text, but permanent visibility consumes space. Owner wants an application-level preference with three presentation modes:

1. **Siempre visible**;
2. **Tooltip / circled-i help**;
3. **Oculto**.

The goal is to keep help available without forcing verbose explanatory text into every normal-use layout.

### General compactness rule

Owner UX principle:

> Do not put in two rows what can be presented more clearly and usefully in a single row.

Apply this as a review heuristic, not as an absolute rule when accessibility, readability or touch targets would suffer.

### App Settings — user-reorderable character tabs

Owner requests that App Settings allow the user to reorder character-sheet tabs.

Current implementation derives visible order directly from `CharacterTabV4.entries`; conditional modules are then filtered for visibility. Later repair/design should insert a persisted user-order layer while preserving conditional visibility and selected-tab fallback semantics.

### Phone landscape must remain phone interaction model

Current navigation switches to side rail based only on available width (`>= 760dp`). This explains the previously recorded tablet-like phone-landscape behavior. Later repair should distinguish physical/form-factor intent rather than promoting a rotated phone to tablet navigation solely because width crosses the breakpoint.

## Stage F current outcome

- F1 Gestión: fixed footprint FAIL/major; death-save compactness and cross-tab coherence findings recorded.
- F2 Habilidades: sticky presence PASS; minor semantic/compactness improvements recorded.
- F3 Conjuros: not yet completed.
- F4 long-collection sticky toolbars: not yet completed.

## Next action

Continue **F3 — Conjuros** on the Redmi Note 11 Pro 5G. Inspect the combined permanent footprint of source selector + spell toolbar + current sticky level header in portrait first, then landscape. Report whether each layer earns its space and whether any can be merged/condensed without losing usability.

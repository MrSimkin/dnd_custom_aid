# Phase 4 pre-QA owner audition — phone Stage F

**Date:** 2026-09-07; continued 2026-09-08  
**Status:** OWNER PHONE STAGE F IN PROGRESS; Gestión, Habilidades, and Conjuros portrait recorded  
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

## F3 — Conjuros fixed selector, filters, and spell-card footprint

### F3 portrait

**Result:** FAIL / major compactness and interaction-efficiency problem, while core list visibility remains usable.

The owner confirms that the **spellcasting source selector** and the **filters** deserve permanent screen space. The problem is not their presence; it is the amount of padding, margin, row fragmentation, and card chrome around them.

### F-F06 — source selector and filters earn permanent space, but consume too much height

**Severity:** major UX/layout

Owner direction:

- keep the source selector permanently available;
- keep filters permanently available;
- materially reduce padding and margins, especially vertical spacing;
- preserve usability at high text zoom, but at ordinary zoom do not spread controls across multiple rows when they can fit clearly in one.

### F-F07 — filter controls are fragmented into too many rows

**Severity:** major compactness UX

At ordinary phone portrait scale, several filter controls that could reasonably share one row are distributed across multiple rows. Later repair should use horizontal space more efficiently and only wrap to additional rows when zoom/available width actually requires it.

### F-F08 — spell list remains visible despite the fixed stack

**Severity:** positive usability evidence

The owner can still see and use the spell list in portrait. Therefore the current fixed layers do not make the surface unusable, but they waste more vertical space than necessary.

### F-F09 — spell cards/windows waste substantial internal space

**Severity:** major UX/layout

Spell cards/windows repeat the broader Stage D/E compactness problem: excessive padding and margins reduce information density without adding equivalent usability.

The same previously recorded compactness observations continue to apply here and should be treated as a cross-cutting family rather than isolated per-screen defects.

### F-F10 — spell card actions are spread inefficiently

**Severity:** major UX/interaction design

Owner reports that spell action buttons should be grouped together rather than consuming multiple lines. The present layout spends approximately three lines largely to support move/reorder controls.

Owner direction for later repair:

- group related spell-card actions together;
- remove unnecessary dedicated vertical footprint for move controls;
- prefer direct card manipulation for reordering where practical.

### F-F11 — whole-card hold-and-drag should be the general card-reorder interaction

**Severity:** owner UX direction / cross-cutting interaction design

Owner prefers being able to **press-and-hold and move a card from any practical part of the card**, instead of requiring a large dedicated drag/move affordance that consumes layout space.

This direction applies not only to spell cards but to equivalent reorderable cards throughout the character UI. Later design must still preserve normal button/text-field interactions, discoverability, accessibility, and reliable gesture disambiguation.

### F-F12 — spell-level leading-zero numeric-entry defect persists

**Severity:** minor functional/input defect

The previously recorded issue remains reproducible: when the spell-level field contains `0`, the owner cannot simply type another digit after it to obtain the intended level naturally. Numeric replacement/normalization should allow ordinary editing instead of making the initial zero obstruct input.

This is the same family already recorded during Stage E5; Stage F3 confirms it remains present in build `40700`.

### F-F13 — prior equivalent spacing/IME/card observations remain applicable

**Severity:** cross-cutting confirmation

Owner explicitly confirms that equivalent observations already recorded for excessive padding/margins, row usage, editor/IME behavior, and card density still apply where the Conjuros surface uses the same patterns. Do not create separate fixes for every occurrence when a shared layout/editor primitive can solve the family safely.

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

### General reorderable-card interaction direction

Where cards are reorderable, prefer direct hold-and-drag on the card over a visually dominant dedicated move control, provided nested controls, accessibility, touch targets, and gesture disambiguation remain safe. This is a cross-cutting owner preference first reconfirmed explicitly during F3 Conjuros.

### App Settings — user-reorderable character tabs

Owner requests that App Settings allow the user to reorder character-sheet tabs.

Current implementation derives visible order directly from `CharacterTabV4.entries`; conditional modules are then filtered for visibility. Later repair/design should insert a persisted user-order layer while preserving conditional visibility and selected-tab fallback semantics.

### Phone landscape must remain phone interaction model

Current navigation switches to side rail based only on available width (`>= 760dp`). This explains the previously recorded tablet-like phone-landscape behavior. Later repair should distinguish physical/form-factor intent rather than promoting a rotated phone to tablet navigation solely because width crosses the breakpoint.

## Stage F current outcome

- F1 Gestión: fixed footprint FAIL/major; death-save compactness and cross-tab coherence findings recorded.
- F2 Habilidades: sticky presence PASS; minor semantic/compactness improvements recorded.
- F3 Conjuros portrait: FAIL/major compactness; source selector and filters earn permanent space; spell list remains visible; filter rows, spell cards, spacing, action layout, direct-reorder interaction, and leading-zero input findings recorded.
- F3 Conjuros landscape: not yet completed.
- F4 long-collection sticky toolbars: not yet completed.

## Next action

Continue **F3 — Conjuros, phone landscape** on the Redmi Note 11 Pro 5G using the same build `0.4.0-preqa.7` / `40700` / `debug`.

Do not re-audit the already recorded portrait findings from scratch. Check how the same source-selector + filter + sticky-level stack behaves after rotation, especially:

- whether landscape gains useful horizontal compaction or instead preserves unnecessary vertical rows;
- whether the fixed stack leaves a practical spell-list viewport;
- whether any phone-landscape tablet/master-detail behavior worsens the Conjuros surface;
- whether spell cards become meaningfully denser or continue wasting space;
- any clipping, overlap, inaccessible controls, or new landscape-only defect.

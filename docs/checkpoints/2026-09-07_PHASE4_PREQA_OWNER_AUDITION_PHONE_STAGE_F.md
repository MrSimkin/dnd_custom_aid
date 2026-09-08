# Phase 4 pre-QA owner audition — phone Stage F

**Date:** 2026-09-07; continued 2026-09-08  
**Status:** OWNER PHONE STAGE F IN PROGRESS; Gestión, Habilidades, Conjuros, Equipo and Rasgos recorded  
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

### F3 landscape

**Result:** FAIL / blocking-for-usability landscape composition on the phone.

The owner cannot see the spell list at all in phone landscape because the source selector and filter/fixed-control stack consumes the full usable content viewport.

### F-F14 — fixed Conjuros controls consume the entire phone-landscape viewport

**Severity:** major / effectively unusable surface in this orientation

On the Redmi Note 11 Pro 5G in landscape:

- the source selector and filters occupy all useful vertical space;
- no spells are visible;
- the problem is therefore no longer only inefficient compactness: the primary content disappears from the usable viewport.

This must be repaired before phone landscape can be considered a first-class supported layout.

### F-F15 — Conjuros landscape confirms the existing phone-to-tablet breakpoint failure

**Severity:** major cross-cutting responsive-design defect

This result reinforces Stage D finding D-F01: rotating the phone causes the character UI to adopt the wide/tablet-like interaction model based on width alone. The owner had already rejected that behavior as unsuitable for a phone in landscape.

The Conjuros failure must therefore not be treated as an isolated spell-screen spacing patch. Later repair should address both:

1. the physical phone must retain a phone-appropriate interaction model in landscape; and
2. fixed/sticky controls must compact or reflow so primary content remains visible.

The broader tablet/wide presentation itself remains subject to separate redesign/audit; do not assume that using the current tablet-style composition is an acceptable solution for phone landscape.

## F4 — long-collection sticky toolbars

### F4.1 Equipo

**Result:** PASS for toolbar justification; major cross-cutting density/interaction findings remain.

The owner confirms the Equipo toolbar is useful and **earns its permanent space**. There is no need to remove the sticky toolbar itself.

### F-F16 — Equipo toolbar earns its space

**Severity:** positive usability evidence

The permanently available Equipo toolbar remains useful while scrolling. Future compaction must preserve its practical availability while reducing unnecessary surrounding whitespace.

### F-F17 — Consumible / Munición UX is unclear and consumes too much space

**Severity:** major UX/content-layout

The current presentation for `Consumible` and `Munición` is not clear enough to the owner and uses disproportionate screen space. Later repair should clarify the relationship/meaning of these controls or states and substantially compact their presentation.

Do not solve this only by shrinking text; the interaction/semantic presentation itself needs review.

### F-F18 — currency terminology correction: Electrum

**Severity:** terminology correction

The currency label must use **`Electrum`**, not `Electro`.

### F4.2 Rasgos

**Result:** same toolbar/compactness family as Equipo; toolbar itself is acceptable, but card/filter semantics need repair.

The owner explicitly says the same global spacing, card, button, row-fragmentation, drag/reorder, and landscape observations apply to Rasgos and should not be re-recorded as separate duplicates.

### F-F19 — Rasgos `Fuente` and `Tipo` are redundant in the current UX

**Severity:** major UX/information-architecture finding

In the current Rasgos card/filter presentation, `Fuente` and `Tipo` appear to the owner to duplicate the same information/function, resulting in two filtering spaces and unnecessary footprint.

Later repair must audit the intended semantics. If they are not meaningfully distinct for the user-facing workflow, merge/remove the duplication. If a real semantic distinction exists internally, the UI must make that distinction clear enough that two separate controls visibly earn their space. The current duplicated presentation is not acceptable.

## App-wide findings promoted by owner during F4

The owner explicitly asked not to repeat these observations on every subsequent surface. They now apply **app-wide unless a later test records a materially different exception**.

### Global card interaction

- all reorderable cards should follow the same direct-manipulation direction already recorded for spell cards;
- prefer press-and-hold/drag from the card body where safe rather than a large dedicated move control;
- the visual/tactile feeling and feedback while moving cards should be improved further and consistently across all card types;
- card action buttons should be grouped efficiently rather than creating unnecessary rows.

### Global margins and padding

Excessive margins/padding apply broadly to **all elements**, including cards, buttons, boxes, dialogs/windows and fixed regions. This is now a global density repair target rather than a per-screen finding.

### Global row-efficiency rule

Across windows and surfaces, controls/information that can clearly fit in one row at the current text scale should not be spread over several rows. Wrapping/additional rows remain appropriate when required by high text zoom, accessibility, or genuinely constrained width.

### Global phone-landscape / wide-layout issue

The previously recorded landscape issue applies across the app. A physical phone in landscape must not simply switch into the current tablet/wide interaction model. The current tablet/wide UI itself has not met the owner's UX expectations and requires its own redesign/audit; it is not an acceptable fallback solution for phone landscape.

## Owner terminology and content-model directions recorded during F4

### Use only `Raza`

**Owner decision:** never present the combined label `Especie/raza`. The app should use **`Raza`** only.

This is a durable owner terminology direction for future UI/content work.

### Class and subclass language/version presentation

**Severity:** major content/UX finding

The owner reports that class and subclass names are currently shown in English rather than Spanish. They should be presented in Spanish in the Spanish application UI.

The current distinction between 5e / 5.5e is also unclear and does not presently earn user-facing complexity because the app is not yet performing SRD/rules/custom-rule matching that would make the distinction operationally meaningful.

Owner direction for the current product stage:

- localize class/subclass names consistently into Spanish in the Spanish UI;
- do not make 5e vs 5.5e a prominent user-facing distinction merely for its own sake while the application does not use that distinction for SRD/rules/custom matching;
- revisit/version the distinction when it has a concrete functional purpose rather than presenting ambiguous metadata now.

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

Where cards are reorderable, prefer direct hold-and-drag on the card over a visually dominant dedicated move control, provided nested controls, accessibility, touch targets, and gesture disambiguation remain safe.

### App Settings — user-reorderable character tabs

Owner requests that App Settings allow the user to reorder character-sheet tabs.

Current implementation derives visible order directly from `CharacterTabV4.entries`; conditional modules are then filtered for visibility. Later repair/design should insert a persisted user-order layer while preserving conditional visibility and selected-tab fallback semantics.

### Phone landscape must remain phone interaction model

Current navigation switches to side rail based only on available width (`>= 760dp`). This explains the previously recorded tablet-like phone-landscape behavior. Later repair should distinguish physical/form-factor intent rather than promoting a rotated phone to tablet navigation solely because width crosses the breakpoint.

## Stage F current outcome

- F1 Gestión: fixed footprint FAIL/major; death-save compactness and cross-tab coherence findings recorded.
- F2 Habilidades: sticky presence PASS; minor semantic/compactness improvements recorded.
- F3 Conjuros portrait: FAIL/major compactness; source selector and filters earn permanent space; spell list remains visible; filter rows, spell cards, spacing, action layout, direct-reorder interaction, and leading-zero input findings recorded.
- F3 Conjuros landscape: FAIL/major; fixed selector/filter stack consumes the full usable viewport and no spells are visible; reinforces the already rejected phone-landscape tablet-like breakpoint behavior.
- F4 Equipo: toolbar PASS/earns space; Consumible/Munición UX unclear/oversized; `Electrum` terminology correction recorded.
- F4 Rasgos: same toolbar/compactness family as Equipo; `Fuente`/`Tipo` duplication requires semantic/UI repair.
- App-wide: card interaction, margins/padding, row efficiency, and landscape/wide-layout findings are now global and should not be re-tested/reported on every surface.
- F4 representative conditional-module toolbar: not yet completed.

## Next action

Finish **F4 with one representative conditional module** on the Redmi Note 11 Pro 5G using the same build `0.4.0-preqa.7` / `40700` / `debug`.

Do not repeat global card, padding/margin, row-fragmentation, IME, or landscape findings. Inspect only whether the conditional module's sticky/permanent toolbar itself earns its space, leaves a practical content viewport, and introduces any genuinely module-specific problem not already covered by the global findings.

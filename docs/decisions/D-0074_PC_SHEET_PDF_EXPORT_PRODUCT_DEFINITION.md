# D-0074 — PC Sheet PDF export product definition

**Status:** Approved  
**Date:** 2026-09-14  
**Decision owner:** Project owner  
**Implementation authorization:** this closes the PDF-export product definition but does not itself authorize product-code implementation

## Purpose

This decision closes the PC Sheet PDF-export product-design gap discovered after the 2026-09-14 technical-readiness review and before integrated-MVP coding authorization.

It expands and, where conflicting, supersedes the narrower PDF-export wording in D-0040. It also extends D-0072/D-0073 by making the complete cross-surface PC Sheet PDF export a protected integrated-MVP capability.

The feature exists because the application is paper-first and may act as the durable digital recovery source for Player sheets. A DM must be able to regenerate a Player's sheet when the Player is using only paper or loses/damages the physical copy.

---

## 1. Surfaces and purpose

The same PC Sheet export capability is available from:

1. **Player Android** for the Player's authorized PC;
2. **DM Android/tablet** when the DM is viewing/managing an authorized campaign PC;
3. **DM Desktop / PC Manager** for authorized campaign PCs.

These surfaces generate the same kind of Player-facing PC Sheet. DM export does not silently add audit history, moderation state, sync metadata, correction history or other DM-only administration data.

There is no separate DM PC-sheet document in this feature.

---

## 2. Visual sheet families selected at export time

The user chooses the desired sheet design each time an export is requested.

Approved visual families are:

1. **Classic D&D-style** — an independently designed multipage character sheet with familiar D&D information organization; it is not a pixel-for-pixel copy of an official published sheet;
2. **Custom v1** — based on `assets/character-sheets/templates/Hoja de PJ - 5.0 - Simkin.pdf`;
3. **Custom v2 — per Attribute** — based on the first-page variant in `Hoja de PJ v2 - 5.0 - Simkin.pdf` that groups skills beside their governing attributes;
4. **Custom v2 — per Ability** — based on the alternative first page in the same v2 PDF that uses separate saving-throw and abilities/skills blocks.

For v2, the two first pages are alternatives: one is selected for the export; they are not both required in one normal sheet.

The custom v1/v2 PDFs are owner-created personal-use templates. Under the current personal-use/non-publishing scope, their existing D&D branding and visual identity are retained rather than sanitized. If the project later becomes publicly distributed, that can be revisited separately.

---

## 3. One canonical PC, multiple presentations

All PDF families render the same canonical PC data. They are presentation choices, not separate character models.

The exporter must never make a character's stored content depend on one PDF template's physical boxes.

---

## 4. Custom Attributes and custom Abilities are mandatory in a complete export

If a PC contains custom Attributes and/or custom Abilities, a normal **Complete PC Sheet** export must include them. They cannot be omitted by an export option, silently discarded because the selected base sheet lacks space, or treated as less important merely because they are custom.

A custom Attribute or custom Ability receives the same informational completeness that its official equivalent would receive in the selected sheet family. The exact visual arrangement is family-specific rather than governed by one universal layout.

When custom statistics exist, the user chooses one of three presentation modes:

### 4.1 Extended Page

Keep the selected base sheet essentially faithful to its normal layout and place the complete custom-statistics representation on the matching **Extended — Custom Statistics** page(s).

### 4.2 App Modified Sheet

Integrate the custom Attributes/Abilities into a generated/adapted version of the selected sheet. The exporter may conservatively resize/reposition sections, redistribute space and generate additional matching pages when needed.

The result must remain recognizably part of the selected sheet family and must not solve impossible geometry by making text unreadably small.

### 4.3 Modified Sheet + Complete Extended Page

Adapt the normal sheet wherever the custom information fits elegantly **and also include the complete Extended — Custom Statistics page(s) exactly as if mode 4.1 had been selected**.

Duplication in this mode is deliberate: the main sheet provides at-a-glance access while the Extended page provides one complete dedicated custom-stat reference.

---

## 5. Extended Page families are design-specific

There is no single generic Extended Page merely restyled with different colors.

Each selected visual family has its own coherent extension family. Layout, typography, sizing, borders, spacing, color and general visual language should be as close as practical to that base sheet.

Examples of purpose-specific extension pages include, only when real PC data requires them:

- **Extended — Custom Statistics**;
- **Extended — Traits & Features**;
- **Extended — Resources & Options**;
- **Extended — Inventory / Equipment**;
- **Extended — Spells**;
- **Extended — Notes**.

The exact set remains data-driven; the exporter must not invent unrelated character subsystems simply because D&D can contain them.

The complete-duplication rule is special to Custom Statistics mode 4.3. Other sections normally continue only their overflow rather than duplicating the full section.

---

## 6. Overflow and readability rules

The exporter preserves character-sheet usability and paper readability.

Normal fitting order is conceptually:

```text
normal layout
-> natural wrapping
-> moderate font-size reduction and/or layout condensation
-> hard readability floor
-> continue overflow on the appropriate matching Extended Page
```

Moderate condensation may include sensible adjustments such as line spacing, character spacing, internal padding or an appropriate condensed font treatment. It must not mean silently deleting information or inventing cryptic abbreviations merely to force content into a box.

When content continues to an Extended Page, the originating section must contain a clear visual cue indicating that more information continues elsewhere. Exact wording/iconography may follow the selected design.

No content may be silently truncated or discarded.

Unused rows/boxes/notes areas remain blank and writable rather than being compacted away. The generated PDF should remain useful as a paper sheet after printing.

---

## 7. Portrait behavior

The PC portrait is part of the sheet export where the selected family provides a portrait area; the Classic family should also intentionally provide one.

At export time the user chooses:

1. **Crop to fill** — preserve aspect ratio while filling the frame, allowing edge cropping;
2. **Fit entire image** — preserve the whole image, allowing blank margins inside the frame.

If the PC has no portrait, the portrait area remains blank.

If export is performed offline and the portrait asset is not available locally because it was never cached, the missing portrait must not block the export. Warn the user and allow generation with the portrait area blank.

---

## 8. Character state selected at export time

The user chooses which digital state the PDF represents:

1. **Permanent / Character Sheet state** — the durable/reconciled PC state;
2. **Current Snapshot** — the current temporary values known to the app where applicable, such as current HP/resources/conditions or other tracked current sheet values.

The exact values differ only where meaningful temporary/current state exists.

This choice does not merge unrelated live-combat working state into the durable PC model; it only exports the applicable current PC data that the application legitimately knows.

---

## 9. Static local PDF; Save/Share only

The exported document is a **static PDF**. It is not a fillable/editable PDF-form product.

The app provides **Save / Share** for the generated file. There is no dedicated in-app Print action; printing remains an operating-system/PDF-viewer responsibility.

No app/export metadata footer, revision number, export date or application branding is added merely for traceability. The PDF should visually remain the selected character sheet.

Generation must work locally/offline whenever the required PC data is available locally. Internet, Neon, Cloudflare and Descope are not required merely to render a sheet.

---

## 10. Optional appended Spellbook

The normal character-sheet spell pages preserve the selected sheet family's usual spell-list/slot representation.

At export time, the user may additionally select:

> **Include Spell Descriptions**

When selected, the exporter appends a separate application-designed **Spellbook** after the PC sheet and any matching Extended Pages.

The Spellbook is intentionally not required to imitate the selected Classic/v1/v2 visual family. It may use one coherent application-owned D&D-appropriate printable design optimized for reference.

### 10.1 Spellbook organization

- group spells by spell level;
- alphabetize within each level;
- include an index at the beginning with at least spell name, level and page number.

### 10.2 Included spells

Include every spell actually attached to the PC in its recorded data, including where applicable known, prepared, written in a wizard spellbook, always prepared, or granted by race/feat/item/feature.

Do **not** automatically include the entire theoretical class spell list merely because the PC could choose those spells.

Where the app knows the state/source distinction, preserve it visually.

### 10.3 Spell entry completeness

Each entry is a practical paper reference containing the normal spell information available to the app, including as applicable:

- spell name, level and school/category;
- casting time;
- range;
- components;
- duration;
- ritual/concentration status;
- complete description;
- higher-level/scaling text;
- custom/homebrew authored spell data;
- PC-specific casting source, casting ability, save DC, spell-attack modifier and analogous calculated values where known.

If the same spell is available to that PC from more than one casting source, the export should preserve the applicable source relationship rather than arbitrarily pretending only one exists.

---

## 11. Character resources

Actual class/custom resources that belong to the PC data model follow the same sheet-extension principles.

Use a genuinely appropriate base-sheet area where one exists; otherwise use the matching **Extended — Resources & Options** page(s).

Preserve the meaningful information actually tracked by the PC model, such as name, maximum/current amount, recharge/reset behavior, die size or associated feature/context where applicable.

Permanent vs Current Snapshot affects these values naturally where the app tracks current use.

Do not expand this rule into speculative companions, mounts, summons, alternate forms or other subsystems that are not part of the current PC sheet/data model.

---

## 12. Relationship to D-0040

D-0040 remains valid for these core principles:

- local/offline generation;
- custom owner PDFs are presentation templates, not canonical data;
- platform generation may share template/layout metadata where useful;
- export is not a server-side PDF service;
- static template overlay remains appropriate for faithful base pages.

D-0074 supersedes or expands D-0040 where necessary:

- export is explicitly required on Player Android, DM Android/tablet and DM Desktop;
- v1/v2 source PDFs are not limited to overlay-only output because App Modified and Extended modes may generate/adapt geometry;
- owner-approved modified/extended pages do not require a new InDesign-side approval cycle merely because geometry changes;
- a new Classic family and application-designed Spellbook are required;
- exact PDF library/rendering architecture is a delegated technical choice and may evolve if D-0040's original library choice becomes unnecessarily restrictive, provided the approved behavior and offline/local contract remain intact.

---

## 13. Technical implementation direction delegated from this product decision

The implementation should treat the feature as one semantic export capability shared across surfaces rather than three unrelated exporters.

Recommended technical shape:

```text
canonical PC/export snapshot
-> shared export semantics / render plan
-> selected visual family
-> platform PDF renderer
   - faithful static-template overlay where appropriate
   - generated/adapted pages for Modified/Extended modes
   - common application-designed Spellbook
```

Exact coordinates, fonts, pagination algorithms, readability thresholds, PDF libraries, drawing primitives and platform packaging remain delegated engineering details unless they create a material owner-facing product/security/cost consequence.

The existing PDFs in `assets/character-sheets/templates/` are the authoritative visual references for v1/v2.

---

## 14. MVP protection and implementation placement

PC Sheet PDF Export is a protected integrated-MVP capability and must not be silently demoted to a stretch goal.

It should be implemented after the shared PC/domain state is coherent enough to produce a canonical export snapshot and then exposed through all three approved surfaces. Internal package/wave placement is an engineering decision; do not create separate semantic implementations for Player Android, DM Android and Desktop.

Integrated QA must cover representative exports across the supported visual families, custom-stat modes, portrait modes, Permanent/Current state, overflow/Extended pages, offline generation, Save/Share and optional Spellbook.

### 14.1 Mandatory owner template-approval gate

This approval does **not** happen during the semantic/render-plan foundation work.

When implementation reaches the visual/template-rendering stage, each supported PC Sheet visual family must be presented to the project owner as a rendered example populated with representative **dummy character data** before that template family is considered visually/functionally approved.

Required approval examples:

1. Classic D&D-style;
2. Custom v1;
3. Custom v2 — per Attribute;
4. Custom v2 — per Ability.

The examples should contain enough realistic dummy content to expose layout behavior rather than showing an empty form. Where relevant, the approval set should also demonstrate overflow/Extended-page behavior and other family-specific presentation choices.

A template family may be implemented iteratively, but it is not owner-approved merely because automated tests pass or an empty/template-only PDF renders successfully. Owner review of a populated example is a mandatory visual/product gate at the appropriate rendering stage.

This gate is for approving presentation and usability. It does not require using a real user's character data.

---

## 15. Current gate

With this decision, the reopened PDF-export product-definition gate is **CLOSED**.

No remaining PDF product question currently blocks integrated-MVP implementation authorization.

The next owner-level gate returns to the D-0073 project-level decision:

> **Authorize beginning the integrated-MVP implementation, starting with the protected `main` + Player-successor convergence.**

This decision record itself makes no product-code changes and does not execute that convergence.

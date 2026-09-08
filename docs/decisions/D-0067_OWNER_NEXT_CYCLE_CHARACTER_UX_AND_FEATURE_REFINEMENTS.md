# D-0067 — Owner next-cycle character UX and feature refinements

**Status:** Approved scope/direction with explicitly identified design/implementation details still to resolve  
**Date:** 2026-09-08  
**Decision owner:** Project owner

## Context

After the build `0.4.0-preqa.7` / `40700` phone audition and the D-0066 repository consolidation, the owner supplied a separate set of observations **outside the formal QA exercise** so they can be considered in the same upcoming development cycle.

This package supplements, and does not replace, the already-recorded owner-audition repair backlog.

No implementation is started by this decision. The next implementation batch must reconcile this package with the existing QA findings and then branch from canonical `main`.

## Cross-cutting rules reaffirmed or added

### One datum, one state

If two surfaces display or edit the same character datum, they must read/write the same canonical draft/persisted value. Do not maintain visually duplicated state that can drift merely because two tabs expose it differently.

This applies especially to General/Gestión state, proficiencies/languages, Inspiration/custom markers, class identity, and any other datum surfaced in multiple places.

### Phone landscape and tablet remain separate design problems

- physical phone landscape must remain a phone-appropriate interaction model;
- phone landscape must not be treated as equivalent to tablet simply because its width crosses a breakpoint;
- the current tablet/wide UX is itself insufficient and requires a complete redesign/optimization pass;
- tablet redesign must therefore be audited as its own first-class interaction surface.

### Help / tooltip direction

The previously recorded help preference remains controlling:

- `Siempre visible`;
- tooltip / circled-`i` help;
- `Oculto`.

Formula/rules explanations requested below should use the same coherent help system rather than ad-hoc explanatory paragraphs that permanently consume space.

## I. General

### I-01 — `Clases` must be substantially more compact

The class identity area should consume less permanent space.

Current code displays class + subclass + `Nv.` plus `DG restantes` and die size. Owner direction `DG = NV` is interpreted for implementation planning as:

- the **maximum hit-die count for a class derives from that class level** rather than requiring a redundant separately modeled/displayed maximum;
- remaining hit dice remain operational state and may be below that maximum;
- class presentation should avoid repeating information that can be derived from level.

If implementation reveals that this interpretation differs from the intended shorthand, surface it before changing the domain model.

### I-02 — Enable real images in `Trasfondo`

Background images must move beyond placeholders and become actually selectable/savable/persistent.

Implementation must preserve them across reopen/restart and account for own-format backup/import rather than creating fragile external-only references.

### I-03 — Redesign the initial PC entry surface

The current PC startup/navigation experience is confusing and does not reflect the owner's desired primary workflow.

The primary PC entry should be a **character list**, not a campaign-first obstacle. Each character row/card should expose at least:

- character name;
- `Raza`;
- classes and class levels;
- campaign.

Campaign administration/selection still exists, but a user with characters in multiple campaigns should be able to understand and open their characters directly from the character-centric start surface.

### I-04 — Same data must stay consistent across the sheet

Reaffirmed as the cross-cutting one-datum/one-state rule above.

### I-05 — User-configurable tab order

`Ajustes del personaje` must allow the owner/user to choose the order of character-sheet tabs.

Conditional visibility/hide-not-delete behavior remains intact. A hidden module does not lose its stored position preference or data.

### I-06 — Tab widths proportional to their names

Character tab width should be based on label/content length rather than forcing all tabs to the same width. Preserve tapability and reasonable minimum/maximum widths.

### I-07 — Attacks use structured damage-component lists

An attack must support multiple damage/effect components instead of treating `Daño` as one opaque value.

Conceptual UI:

```text
+ Añadir
1d6 cortante
1d4 perforante
+4
```

A component may represent a dice expression plus damage/effect type or a flat numeric modifier. This structured model should later feed the damage-roll workflow rather than duplicating damage parsing elsewhere.

### I-08 — `Lanzamiento de Conjuros` wording and formulas

#### I-08a

Rename:

`CD conjuros` -> `CD salv. conjuro`

Provide circled-`i` explanation:

`8 + mod. Aptitud mágica + bono de competencia`

#### I-08b

Rename:

`Ataque mágico` -> `Mod. ataque mágico`

Provide circled-`i` explanation:

`mod. Aptitud mágica + bono de competencia`

#### I-08c

For multiclass characters with more than one spellcasting ability, `Aptitud mágica` should show all applicable abilities in the same order as the registered classes rather than collapsing them into one ambiguous value.

### I-09 — `Defensas` should include armor-related proficiency information

The Defensas/reference area should surface the character's armor/armour proficiencies from the same canonical proficiency data rather than creating another disconnected copy.

### I-10 — Add a visible `Idiomas` area

Languages should be exposed as a normal visible character reference section using the same canonical structured language data already owned by the character model.

### I-11 — Custom attributes from PC Settings

`Ajustes del personaje` must allow adding custom character attributes/abilities.

These custom attributes should subsequently participate wherever attributes are relevant, including their visible character reference and, after the model is designed, skills/saves/dice selection as applicable.

This is a domain-impacting capability. Exact custom-attribute fields and modifier semantics require a small design/model audit before implementation; do not silently assume every custom attribute uses one formula if the owner has not approved that behavior.

## II. Habilidades

### II-01 — Custom skills configured from PC Settings, rendered as ordinary skills

Custom skills are added/managed from `Ajustes del personaje`, not from a visually separate special box inside Habilidades.

In Habilidades they participate as normal skills in the same list/layout. Their visual distinction is **italic text**, not a separate container/card family.

### II-02 — `Por habilidades` must be alphabetical

The `Por habilidades` presentation orders skills alphabetically by their displayed Spanish name.

### II-03 — Correct Arcana terminology

Use the real Spanish concept `Conocimiento Arcano`.

Compact UI may abbreviate it as `C. Arcano` or `Conoc. Arcano` when needed by available width. Prefer the clearest abbreviation that fits the final layout.

## III. Combate

### III-01 — Attack quick-card hierarchy

A compact attack card should prioritize:

1. `Nombre (+bono)`;
2. `Daño`;
3. action type;
4. beginning/preview of notes when present;
5. compact action buttons.

This participates in the global card-density and compact-action redesign already recorded from QA.

## IV. Dados

### IV-01 — Dice interaction should behave almost like one compact control

The primary roll flow should be compact and direct:

1. choose advantage state;
2. choose roll category (`Atributo`, `Salvación`, `Habilidad`, `Ataque`, etc.);
3. choose the concrete eligible value/entity from the character sheet;
4. press the roll button.

The UI should derive bonuses from the same canonical character data rather than requiring manual re-entry.

### IV-02 — Global result-presentation setting

Application Settings should offer two result presentation modes:

- visible/animated dice throw;
- compact result box only.

Both modes must show the decomposition explaining how the final result was obtained.

### IV-03 — Damage roll

Add damage-roll support. It should consume the structured attack damage components from I-07 where applicable rather than inventing a second attack-damage representation.

### IV-04 — Custom roll

Provide a custom roll path for expressions that are not represented by a standard attribute/save/skill/attack choice.

## V. Gestión

### V-01 — Inspiration visibility/configuration

Inspiration can be enabled/disabled from `Ajustes del personaje`.

When enabled, it should be visible/usable in both `General` and `Gestión` using one canonical value/state.

### V-02 — Configurable custom markers

`Ajustes del personaje` should allow adding owner-defined markers/counters such as `Puntos de destino` or `Puntos de estrés`.

A marker may be at least:

- binary/boolean;
- integer/counter.

Enabled markers should be visible in both `General` and `Gestión` from the same canonical state.

The existing generic resource system should be audited for reuse before adding a parallel persistence model.

### V-03 — Condition catalog + explanations

Condition adding should offer known predefined conditions rather than requiring every condition to be typed manually.

Requirements:

- include the supported SRD condition set when the corresponding official content is available;
- include the owner's requested `Mitos de Cthulhu` condition set;
- each predefined condition exposes circled-`i` explanatory help.

`Mitos de Cthulhu` content is not assumed to be open/SRD content. Before bundling explanatory rules text, implementation must use a legally/project-appropriate source (for example owner-provided material or content whose license permits inclusion) rather than silently scraping proprietary rules text.

### V-04 — Concentration check helper

The Concentration area should explain the check required to maintain/break concentration and help compute the relevant DC from an entered/known damage amount (for example damage 20 -> appropriate DC under the applicable rule source).

Do not hard-code edition-specific rules invisibly; when official rule data becomes operational, use the identified applicable rule source.

### V-05 — Cross-domain recovery metadata and rest integration

Any charge/use-based feature across the character sheet that recovers on `Descanso corto` and/or `Descanso largo` should explicitly store/show that recovery policy.

`Gestión` rest assistance should use that metadata across domains so a rest can recover eligible charges/resources consistently instead of only handling objects created inside Gestión.

This is a cross-domain behavior and should be implemented through shared recovery metadata/operations, not tab-specific duplicate logic.

## VI. Equipo

### VI-01 — `Gemas / arte`

Add a dedicated valuables section for gems/art objects (`Gemas / arte`). Exact compact fields should be designed before implementation, but it belongs in the equipment/wealth domain rather than free-form notes.

## VII. Trasfondo

### VII-01 — Persist background images

Same approved requirement as I-02: enable actual save/persistence of background images.

## VIII. Conjuros

### VIII-01 — Existing-official vs custom add flow, conditional on available corpus

When an official SRD spell corpus is actually loaded/available to the product, adding a spell should separate:

- `Buscar existente` / add from official catalog;
- `Añadir conjuro personalizado`.

The existing spell source/association system remains separate and functional.

### Current implementation boundary

The repository currently contains architecture for future SRD retrieval/clarification but **does not currently ship/load an official spell corpus into the character app**. Therefore this requirement is recorded now but does not by itself authorize expanding the next repair cycle into building/importing the entire SRD corpus.

## IX. Rasgos and other catalog-backed domains

### IX-01 — Same official-existing vs custom pattern wherever applicable

When an official corpus is actually available for a domain, provide separate flows for selecting an existing official record and creating a custom/homebrew record.

For Rasgos, that means searchable existing traits/features by applicable class/subclass plus a separate custom-trait action.

The owner explicitly generalizes this pattern to every domain where it makes sense; do not require them to repeat it per module.

As with VIII-01, this is conditional on the relevant official data actually being loaded/available.

## X. Notas

### X-01 — Search/filter Notes

Notas needs search/filter support suitable for larger note collections while preserving manual order unless the selected presentation mode explicitly changes ordering.

## XI. Ajustes del personaje

### XI-01 — Haptic strength and duration

Add user control for how strong and how long haptic feedback feels.

Implementation must account for Android/device capability differences. Prefer understandable bounded choices over implying precision the hardware cannot guarantee.

### XI-02 — Move Application Settings entry upward

The `Configuración de la aplicación` entry should appear near the upper part of PC Settings so global settings are easier to find.

## XII. Application Settings

### XII-01 — Application Settings becomes a full screen/page, not a dialog/window

The global settings experience should no longer be implemented as a modal window/dialog. It should have its own navigable settings surface suitable for examples/previews and future growth.

### XII-02 — Text-size control: stepped slider + live example

Owner asked whether a slider is preferable. Recommended/approved implementation direction:

- use a **discrete stepped slider**, not an unconstrained continuous value;
- show the current percentage/value explicitly;
- show a live representative text/UI sample next to/below it;
- preserve the intentionally broad text-size range unless a concrete usability problem requires changing it.

A slider is better than a long dropdown here because text size has a natural ordered scale and benefits from live visual comparison.

### XII-03 — Space compactness: stepped slider + live example

Use the same interaction pattern for `Compactación de espacios`:

- discrete stepped slider;
- explicit percentage/value;
- live representative card/control sample;
- include the previously requested **40%** option in the successor audition.

A stepped slider is recommended here for the same reason: this is an ordered visual density scale.

### XII-04 — Simplify font presentation

Remove font-origin/provider text from the normal font choice UI.

Remove the special `audición` framing/label. Font choice should be presented as an ordinary setting with an immediate representative preview.

Licensing/source attribution may remain in technical/legal/about material where required; it does not need to occupy the normal font selector.

### XII-05 — Column settings need visual examples

Column-count settings should include an understandable visual preview.

Prefer a **small live mini-grid/card preview** over a static explanatory image, because it can reflect the actual selected number of columns and current spacing/text settings.

### XII-06 — Theme renames

Owner-requested visible naming changes:

- `Morado oscuro` -> `Púrpura`;
- `Cian`/current cyan dark presentation -> `Cyan`;
- `Azul noche` -> `Noche`;
- `Azul noche claro` -> `Noche despejada`;
- `Verde bosque` -> `Bosque`;
- `Verde bosque claro` -> `Oasis`.

Where current implementation has both `Cian oscuro` and `Cian claro`, preserve a clear distinction; do not accidentally give two different themes the same visible label.

### XII-07 — Six additional themes delegated to implementation/design

The owner delegated the selection of six additional themes. Proposed set for the next audition, chosen to add genuinely different visual families rather than near-duplicates:

1. **Carmesí** — deep burgundy/red dark theme;
2. **Ámbar** — warm amber/brown dark theme;
3. **Glaciar** — pale cool blue light theme;
4. **Lavanda** — soft violet/lilac light theme;
5. **Pizarra** — dark slate/blue-neutral theme;
6. **Terracota** — warm earthy light theme.

Exact color tokens must be implemented with readable contrast and auditioned on-device. Theme names/colors remain QA-adjustable if the owner dislikes the actual result.

## Dependencies and implementation grouping

The next cycle should avoid implementing these as dozens of isolated patches. Important natural groups include:

1. **canonical character-data consistency** — one datum/one state across tabs;
2. **General + PC Settings identity/configuration** — compact classes, custom attributes, Inspiration/custom markers, languages/armor references, tab order;
3. **structured attacks + dice** — damage components, compact attack cards, roll selection, damage/custom rolls;
4. **cross-domain recoverable resources** — recovery metadata + Gestión rest integration;
5. **settings redesign** — full-screen settings, sliders/previews, columns, fonts, themes, global help/haptics;
6. **responsive redesign** — separate phone portrait, phone landscape, tablet portrait, tablet landscape interaction design;
7. **collection improvements** — Notes search, existing-vs-custom catalog pattern when real official data exists;
8. **image persistence** — Trasfondo images and backup/import survival.

## Explicitly conditional/not silently expanded

The following are approved product directions but should not silently expand the immediate repair batch beyond its intended scope:

- SRD-backed spell/trait/condition search requires actual licensed/available corpus data;
- custom attributes require a small domain/formula design audit;
- `Mitos de Cthulhu` explanatory content requires an appropriate content source;
- background image persistence requires a storage/backup design that survives app restart/import;
- haptic intensity/duration must respect real Android hardware/API capability.

## Next action

Reconcile D-0067 with the existing owner-audition repair backlog, identify conflicts/dependencies, then present one coherent implementation plan before product code changes begin.

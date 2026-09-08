# Project State

**Last verified:** 2026-09-08  
**Canonical branch:** `main`  
**Repository consolidation:** COMPLETE under D-0066  
**Current owner refinement package:** D-0067 RECORDED  
**Current phase:** Phase 4A — Character Foundation Closure / owner-driven repair and refinement cycle  
**Release status:** debug / pre-QA; known defects remain; not release-ready  
**DM work:** blocked until the separate Phase 4A closure/acceptance gate is later satisfied and explicitly approved

## 1. Canonical repository state

D-0066 consolidated the current Phase 4 development baseline into `main` by normal non-force fast-forward. `main` is now the single current development baseline even though Phase 4A is not accepted or release-ready.

Old implementation/safety/tmp branches are historical evidence, not competing current state. Frozen QA candidate branches remain immutable historical evidence.

Use:

- `docs/checkpoints/LATEST.md` for the exact resume point;
- `docs/BRANCH_STATUS.md` only when branch-history interpretation matters;
- `docs/decisions/D-0066_MAIN_CANONICAL_DEVELOPMENT_CONSOLIDATION.md` for the consolidation rationale.

## 2. Latest technically verified product build

The latest product code that completed the full automated gate remains:

- version `0.4.0-preqa.7`;
- build `40700`;
- type `debug`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- tested product tree `3b2f2ab471097d3b108c9a787fc2342c5aad683a`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186` / `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`;
- APK size `36,161,616` bytes.

All canonical-repository changes after that product revision through D-0066/D-0067 are documentation/governance-only unless a later checkpoint explicitly records product code changes. Build `40700` therefore remains the latest technically verified product identity.

## 3. Owner phone audition status

Primary real device:

**Redmi Note 11 Pro 5G**

Stages A–F were covered sufficiently to expose the major visual/interaction problems of build `40700`. The build did **not** pass owner visual acceptance.

Do not force the owner to repeat findings already generalized across equivalent screens.

Detailed evidence remains in the September 7–8 Stage A–F checkpoints and the `Fuente` redundancy audit.

## 4. Existing repair families from QA/audition

### Density / layout

- excessive margins/padding across cards, buttons, boxes, dialogs/windows and fixed regions;
- controls/information that fit clearly in one row should not be spread across multiple rows unless width/text scale requires it;
- add/audition 40% spacing;
- fixed/sticky areas must justify their permanent footprint.

### Card interaction

- direct press-and-hold/drag from reorderable cards where safe;
- stronger movement feedback;
- compact grouped action controls;
- avoid large dedicated move controls.

### Phone landscape / tablet-wide UX

- physical phone landscape must retain a phone-appropriate interaction model;
- phone landscape is not equivalent to tablet UX;
- current tablet/wide UX itself requires complete redesign/optimization;
- Conjuros fixed controls can consume the entire usable phone-landscape viewport;
- rotation scroll/context preservation needs repair.

### Editors / IME

- shared editor family still has major keyboard/action reachability problems;
- active editors are disrupted by orientation change;
- numeric fields must support natural replacement/editing without leading-zero obstruction.

### Information architecture / terminology

- generic `Fuente` provenance is over-exposed;
- preferred compact provenance model is origin type + specific origin on one row where useful, default origin type `Clase`;
- preserve actual Conjuros source associations because they drive behavior and may themselves be custom/non-class;
- clarify/consolidate Rasgos `Fuente`/`Tipo`;
- use `Raza` only;
- use `Electrum`;
- class/subclass names should be Spanish;
- do not foreground 5e/5.5e provenance where it has no current operational purpose;
- Consumible/Munición UX is unclear and oversized.

### Other QA directions

- Habilidades fixed passive band earns its place but should explicitly label passive values and use horizontal space efficiently;
- Gestión operational/death-save area is too tall;
- help text should support `Siempre visible`, circled-`i`/tooltip, or `Oculto`;
- user-configurable character-tab order is desired;
- PT Sans Narrow remains provisional pending satisfactory Bold presentation.

## 5. D-0067 owner non-QA development package

The owner deliberately supplied additional requirements outside the formal QA exercise so they can be considered in the same development cycle.

Detailed authority:

`docs/decisions/D-0067_OWNER_NEXT_CYCLE_CHARACTER_UX_AND_FEATURE_REFINEMENTS.md`

Recording checkpoint:

`docs/checkpoints/2026-09-08_OWNER_NEXT_CYCLE_INPUT_RECORDED.md`

Major additions:

### General / navigation / identity

- compact `Clases`; maximum hit-die count derived from class level, while remaining hit dice remain operational state;
- real persistent images in Trasfondo;
- character-first startup PC list showing Raza, classes/levels and campaign;
- one datum/one canonical state across tabs;
- configurable tab order and proportional tab widths;
- structured multi-component attack damage;
- spellcasting labels/formula help and multiclass spellcasting abilities;
- armor/language reference visibility;
- custom attributes configurable from PC Settings.

### Habilidades / Combate / Dados

- custom skills managed from PC Settings but rendered as ordinary italic skills;
- alphabetical `Por habilidades`;
- `Conocimiento Arcano` terminology;
- compact attack-card hierarchy;
- character-aware compact roll flow;
- selectable animated-dice vs compact-result presentation, both with decomposition;
- damage roll and custom roll;
- structured attack damage should be the shared source for damage rolling.

### Gestión / Equipo / Notas

- Inspiration enable/disable plus shared visibility in General/Gestión;
- configurable custom binary/integer markers shared across General/Gestión;
- predefined condition catalog/help;
- concentration DC helper;
- shared short/long-rest recovery metadata across all charge/resource domains;
- `Gemas / arte` valuables section;
- Notes search/filter.

### PC Settings / Application Settings

- haptic strength and duration controls;
- Application Settings entry moved upward in PC Settings;
- Application Settings becomes a full screen/page, not a dialog;
- stepped sliders with live preview for text size and spacing;
- visual mini-grid preview for column settings;
- remove font provider/origin and special audition framing from normal font selector;
- owner-requested theme renames;
- six delegated theme families for next audition: Carmesí, Ámbar, Glaciar, Lavanda, Pizarra and Terracota.

## 6. Conditional/dependency boundaries

### SRD-backed existing-content add flows

The owner wants `Buscar existente` vs custom-create flows for spells, traits and equivalent domains **when official corpus data is actually available**.

Current repository/product state does not load an official SRD spell/trait corpus into the character app. The architecture for future SRD retrieval exists, but the data pipeline/corpus is not active.

Therefore this is an approved conditional design direction, not silent authorization to expand the immediate repair batch into implementing SRD ingestion.

### `Mitos de Cthulhu` condition content

Requested as predefined condition/help content, but bundled explanatory text requires an appropriate source/license or owner-provided content before implementation.

### Custom attributes

Approved capability, but modifier/formula/data shape must receive a small design/model audit before schema/UI implementation.

### Images

Background images must survive restart and own-format backup/import; storage must not rely on fragile transient external URIs.

### Haptics

Strength/duration UI must reflect real Android/device capability rather than pretending to offer precision unsupported by hardware.

## 7. Tablet acceptance status

No physical owner tablet device has yet been recorded. Tablet acceptance is not complete.

The current tablet/wide design is already known to require redesign, so repair/redesign should precede treating tablet testing as the final acceptance attempt.

## 8. Historical QA evidence

Historical frozen candidates remain immutable evidence and are not active targets, especially:

- `tmp/phase4-l-frozen-qa-candidate`;
- `tmp/phase4-m5-frozen-qa-candidate`.

The brief 2026-09-08 historical M6 detour is preserved at `docs/checkpoints/2026-09-08_PHASE4_M6_OWNER_QA_PROGRESS.md` and is explicitly superseded as a current path.

## 9. Current execution position

Repository consolidation and D-0067 input capture are complete.

**Do not implement the backlog piecemeal yet.**

Exact next sequence:

1. reconcile D-0067 with all Stage A–F owner-audition findings and the `Fuente` audit;
2. identify shared primitives/cross-cutting fixes so equivalent defects are repaired once;
3. separate schema/domain/storage work from UI-only work;
4. map dependencies, especially structured attack damage -> damage dice, custom attributes -> skills/saves/dice, recovery metadata -> Gestión rest, and images -> backup/import;
5. keep SRD-backed existing-content flows conditional unless official corpus ingestion is separately approved for this cycle;
6. produce one coherent implementation order/build plan;
7. only then create a focused product branch from canonical `main`;
8. run the full automated gate after coherent product increments;
9. use targeted owner retesting of repaired families;
10. later freeze a replacement formal M6 candidate only when phone/tablet baseline is acceptable.

## 10. Phase 4 closure boundary remains open

D-0066 made the development baseline canonical; D-0067 expands/refines next-cycle scope. Neither is Phase 4A acceptance.

No DM feature implementation begins before the separate Phase 4A closure/acceptance gate is later satisfied and explicitly approved.

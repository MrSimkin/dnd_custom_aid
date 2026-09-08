# Owner next-cycle development input recorded

**Date:** 2026-09-08  
**Status:** RECORDED; NO PRODUCT IMPLEMENTATION STARTED  
**Canonical branch:** `main`  
**Detailed owner decision/input:** `docs/decisions/D-0067_OWNER_NEXT_CYCLE_CHARACTER_UX_AND_FEATURE_REFINEMENTS.md`  
**Latest technically verified product build:** `0.4.0-preqa.7` / `40700` / `debug`

## Completed in this checkpoint

The owner's non-QA development observations were reorganized and durably recorded as D-0067.

The package covers:

- General/character entry/class compactness and cross-tab data consistency;
- background image persistence;
- character-first startup list;
- configurable tab order and proportional tab widths;
- structured attack damage components;
- spellcasting terminology/formula help and multiclass spellcasting abilities;
- armor/language visibility and custom attributes;
- Habilidades integration/alphabetical/Arcana terminology;
- compact attack-card hierarchy;
- redesigned character-aware dice flow, damage rolls and custom rolls;
- Inspiration/custom markers/conditions/concentration/rest recovery integration;
- `Gemas / arte`;
- conditional existing-official-vs-custom add flows once real official corpus data exists;
- Notes search/filter;
- haptic strength/duration and PC Settings ordering;
- full-screen Application Settings with live-preview stepped sliders, visual column examples, simplified font presentation, theme renames and six additional delegated theme families;
- reaffirmed help/tooltip, tablet redesign and phone-landscape separation rules.

## Technical observations recorded with the package

- current class UI uses `DG restantes`; implementation planning interprets owner shorthand `DG = NV` as deriving the maximum hit-die pool from class level while keeping remaining dice as live state;
- current product does not yet load an official SRD spell/trait corpus, so catalog-backed add flows are recorded as conditional rather than silently expanding the immediate repair cycle into SRD ingestion;
- `Mitos de Cthulhu` explanatory condition content requires an appropriate source/licensing path before bundling rules text;
- custom attributes, image persistence and cross-domain recovery metadata require small model/storage audits before implementation;
- text-size and spacing controls are recommended as discrete stepped sliders with live previews;
- six delegated theme families recorded for successor audition: Carmesí, Ámbar, Glaciar, Lavanda, Pizarra and Terracota.

## Repository housekeeping also completed

The remaining D-0066 consolidation cleanup was finished:

- `docs/TESTING.md` corrected from stale D-0048 consolidation references to D-0066;
- `docs/BRANCH_STATUS.md` corrected/finalized for completed D-0066 consolidation;
- accidental duplicate `docs/decisions/D-0048_MAIN_CANONICAL_DEVELOPMENT_CONSOLIDATION.md` deleted;
- the historical real D-0048 Settings decision remains intact;
- D-0066 remains the unique canonical-main consolidation decision.

## Exact next action

Do not implement piecemeal yet.

Next, reconcile D-0067 against the existing Stage A–F owner-audition backlog and produce one coherent implementation/dependency plan from canonical `main`. The plan should explicitly identify:

1. shared/cross-cutting primitives to repair once;
2. schema/domain/storage changes, if any;
3. UI-only changes;
4. conditional/deferred SRD-backed features;
5. a sensible implementation order;
6. targeted regression/owner-ret test boundaries.

Only after that reconciliation should a new focused product branch be created from `main`.

# Latest project checkpoint

**Updated:** 2026-09-08  
**Canonical branch:** `main`  
**Repository consolidation:** COMPLETE under D-0066  
**Current owner development input:** D-0067 RECORDED  
**Current product status:** debug / pre-QA; known defects remain  
**Latest technically verified product identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary owner test phone:** Redmi Note 11 Pro 5G  
**Phone audition:** Stages A–F sufficiently covered; findings recorded; visual acceptance NOT passed  
**Tablet acceptance:** not complete; current tablet/wide UX is itself a redesign target  
**DM implementation:** blocked pending later Phase 4A closure acceptance

## Read next

1. `docs/PROJECT_STATE.md` — authoritative current-state snapshot;
2. `docs/decisions/D-0067_OWNER_NEXT_CYCLE_CHARACTER_UX_AND_FEATURE_REFINEMENTS.md` — owner non-QA development package for the upcoming cycle;
3. `docs/checkpoints/2026-09-08_OWNER_NEXT_CYCLE_INPUT_RECORDED.md` — exact recording/checkpoint status;
4. `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_F.md` — detailed latest QA/audition findings;
5. `docs/checkpoints/2026-09-08_PHASE4_PREQA_FUENTE_REDUNDANCY_AUDIT.md` — provenance/source information-architecture follow-up;
6. `docs/decisions/D-0066_MAIN_CANONICAL_DEVELOPMENT_CONSOLIDATION.md` and `docs/BRANCH_STATUS.md` only when branch/repository history matters.

## Canonical baseline

D-0066 consolidated the current Phase 4 development reality into `main` by a normal non-force fast-forward. This made `main` the single current development baseline without claiming release readiness or QA acceptance.

Old implementation/tmp branches are historical evidence, not competing current truth. Frozen QA branches remain immutable historical evidence.

## Latest verified product code

The full automated gate last passed for product commit:

`43ca1f5662123ce4d355d9d618b0bfba66d17697`

Review identity:

- version `0.4.0-preqa.7`;
- build `40700`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

All repository changes since that tested product revision through D-0066/D-0067 are documentation/governance only unless a later checkpoint explicitly says otherwise. Build `40700` therefore remains the latest technically verified product identity.

## Existing QA/audition backlog remains active

Do not repeat the whole phone audition on build `40700`.

Cross-cutting recorded families include:

- excessive app-wide padding/margins and unnecessary multi-row layouts;
- card density, direct hold-and-drag reordering and weak movement feedback;
- shared editor/IME reachability/orientation defects;
- phone landscape incorrectly entering an inadequate tablet/wide presentation;
- complete tablet/wide UX redesign/optimization;
- Conjuros fixed controls exhausting the landscape content viewport;
- scroll/context reset on rotation;
- compactness problems in Gestión/death saves and other permanent regions;
- generic `Fuente` provenance over-exposed;
- unclear Consumible/Munición UX;
- `Electrum`, `Raza`-only and Spanish class/subclass terminology corrections;
- help-text/`ⓘ` presentation preference;
- user-configurable tab order;
- 40% spacing successor audition and PT Sans Narrow Bold follow-up.

## D-0067 owner next-cycle package

The owner supplied a separate non-QA package to take advantage of the same development cycle. Major additions include:

- compact `Clases`, derived hit-die maximum from level and stronger same-data consistency across tabs;
- persistent images in Trasfondo;
- character-first initial PC list showing Raza, classes/levels and campaign;
- proportional tab widths and user-set tab order;
- structured multi-component attack damage;
- spellcasting label/formula help and multiclass spellcasting abilities;
- armor/language visibility and custom attributes;
- custom skills managed in PC Settings but rendered normally/italic in Habilidades; alphabetical skills; `Conocimiento Arcano`;
- compact attack-card hierarchy;
- redesigned character-aware dice workflow, selectable result presentation, damage rolls and custom rolls;
- configurable Inspiration/custom markers, predefined conditions/help, concentration DC assistance and cross-domain rest recovery metadata;
- `Gemas / arte`;
- Notes search/filter;
- haptic strength/duration;
- full-screen Application Settings with stepped live-preview sliders, visual column previews, simpler font UI, theme renames and six new delegated theme families;
- conditional official-existing-vs-custom add flows only when the relevant official corpus is actually available.

Detailed requirements and dependencies are in D-0067; do not implement from this summary alone.

## Important conditional boundary

The current product does **not** load an official SRD spell/trait corpus into the character app. D-0067 therefore records `Buscar existente`/official catalog flows as conditional requirements, not authorization to silently add SRD ingestion to the immediate repair cycle.

`Mitos de Cthulhu` explanatory condition content likewise requires an appropriate content/licensing source before bundled rules text is added.

## Exact next action

**Do not start piecemeal product implementation yet.**

Next step is one reconciliation/design pass that combines:

1. existing Stage A–F QA/audition findings;
2. the `Fuente` audit;
3. D-0067 owner non-QA requirements.

That pass must produce a coherent implementation plan identifying:

- shared/cross-cutting primitives to repair once;
- schema/domain/storage changes versus UI-only work;
- dependencies between attack/dice, custom attributes/skills, recovery/Gestión, images/backup and responsive design;
- conditional/deferred SRD-backed items;
- sensible implementation order and build boundaries;
- targeted automated and owner real-device retest scope.

Only after that plan is coherent should a new focused product branch be created from canonical `main`.

No DM-feature implementation begins before the separate Phase 4A closure gate is later satisfied and explicitly approved.

# Project State

**Last verified:** 2026-09-03  
**Canonical branch:** `main` — untouched by Phase 4 closure work  
**Phase 4 durable historical line:** `implementation/character-data-foundation`  
**Active focused closure branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 Character Foundation Closure  
**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; **Batch F active**
**DM work:** explicitly blocked until Phase 4 closure is fully implemented, phone+tablet QA accepted, and owner approves closure/merge

## 0. Primary resume order

1. `docs/checkpoints/2026-09-03_PHASE4_BATCH_E_GENERAL_SKILLS_COMBAT.md` — completed E gate and exact continuation into F;
2. `docs/checkpoints/2026-09-03_PHASE4_BATCH_E3_COMBAT_FAVORITES_D20.md` — completed E3 operational combat gate;
3. `docs/checkpoints/2026-09-03_PHASE4_BATCH_E2B_CLASS_IDENTITY.md` — completed class/subclass/source identity gate;
4. `docs/checkpoints/2026-09-03_PHASE4_BATCH_E2A_GENERAL_SKILLS.md` — completed General/Habilidades gate;
5. `docs/checkpoints/2026-09-03_PHASE4_BATCH_D_GESTION.md` — completed D gate;
6. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — small recoverable execution batches;
7. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level A–J map and final QA matrix;
8. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved product/design scope;
9. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — class/subclass/module audit;
10. earlier A/B/C checkpoints as historical implementation evidence.

## 1. Closure scope status

D-0047 is owner-approved. The closure includes:

- retained QA bug fixes and owner requirements;
- all F01–F18 features;
- all D01–D18 design directions;
- all I01–I22 improvements;
- official class/subclass identity including Artificer and audited supplemental official sources;
- conditional reusable modules: Artífice, Formas, Técnicas, Metamagia, Pactos, Compañeros;
- new Gestión character-maintenance surface;
- PC Settings consolidation;
- Supercompact and Table/read-only mode;
- global IME/action/order/context UX fixes;
- own-format backup/import;
- first-class phone + tablet behavior.

No DM feature implementation begins before the Phase 4 exit gate.

## 2. Batch 0 housekeeping — COMPLETE

Updated current-truth documentation:

- `README.md`;
- `AGENTS.md`;
- `MANIFEST.md`;
- `docs/ROADMAP.md`;
- `docs/ARCHITECTURE.md`;
- `docs/TESTING.md`.

Known remaining index-maintenance item:

- consolidated `docs/DECISIONS.md` still ends at D-0043 while detailed approved D-0044–D-0047 records exist. This remains visible and non-blocking for implementation, but must be reconciled before the eventual Phase 4 merge proposal without renumbering historical decisions.

## 3. Batch A1 — official catalog reconciliation — GREEN

- code commit `1b2f6a79924b88d71567a277f6752dea6bbbb1c9`;
- test commit `dd6f50afebe862222861ee8ccb39cfe99ee82df1`;
- workflow `33785858196` — PASS.

## 4. Batch A2 — schema 7 durable closure data — GREEN

A2 added additive schema-7/domain definitions and `CharacterClosureRepository` for Conditions/Exhaustion, Defenses, movement/senses, Concentration, recovery metadata, consumable metadata, portrait/token references, reconciliation, XP/Milestone, custom skills, temporary effects, module overrides, Table mode, haptics and Quick Access.

A2b controlling workflow `33787986897` — PASS across backend, shared/Kotlin tests + SQLDelight, Android debug, Desktop and APK upload.

## 5. Batch B1 — global editor/IME/action foundation — GREEN

Reusable IME-safe character editor and consistent action/confirmation/validation/empty-state primitives are integrated across the character keyboard editors.

Final B1 workflow `33791637168` — PASS across backend, shared/Kotlin tests, Android debug, Desktop and APK upload.

## 6. Batch B2 — ordering/search/context/drag foundation — GREEN

Implemented:

- Manual/A–Z presentation helpers preserving stored manual order;
- reusable search/filter/query state;
- visible drag feedback and insertion indicators;
- semantic haptic hook;
- actual dirty/saved state;
- unsaved-leave guard;
- context-preservation primitives;
- real drag proof integrations in Notes and Combat.

Controlling workflows `33792391465`, `33793135304`, `33793677310`, `33794100599` — PASS.

## 7. Batch C — PC Settings consolidation — GREEN

Implemented lifecycle status in PC Settings, spellcaster hide-not-delete, haptic/Table/XP settings, module auto-suggestions + manual overrides, Application Settings entry, responsive settings, Supercompact entry and Back hierarchy.

Controlling full C workflow `33796586608` — PASS.

Full Table-mode edit suppression and final Favorite/direct-action Supercompact behavior remain assigned to I2.

## 8. Batch D — Gestión live maintenance — GREEN

Implemented Conditions/Exhaustion, Concentration, generic Resources, Rest preview/selective apply, temporary effects, Inspiration, contextual death saves, reconciliation checkpoints and responsive Gestión.

Final controlling head `64033be2632012cb6cac19728ebecb1d44ec553b`, workflow `33809045740` — PASS across backend, shared/Kotlin tests, Android debug, Desktop and APK upload.

Hit Dice remain review-only in Rest preview; no automatic recovery rule is imposed across mixed D&D 5e / 5.5e / custom characters.

## 9. Batch E — General + Habilidades + Combate — GREEN

Controlling checkpoint:

- `docs/checkpoints/2026-09-03_PHASE4_BATCH_E_GENERAL_SKILLS_COMBAT.md`.

Delivered:

- class/subclass/source identity with official convenience metadata and unrestricted manual/homebrew values;
- non-enforcing Hit Die suggestions;
- portrait/token local references;
- Defenses, Senses and special Movement;
- Passive Perception/Insight/Investigation;
- custom skills in both existing Habilidades organization modes;
- compact combat type/modifier/damage-at-glance;
- quick damage/heal/temp-HP operations with selective structural-draft HP synchronization;
- contextual manual death saves;
- combat-entry Quick Access Favorites for persisted entries;
- bounded simple `d20 + modifier` roller for attack entries, saves and skills.

Verification:

- E2a workflow `33810868863` — PASS;
- E2b workflow `33811490741` — PASS;
- E3 controlling head `1ae8fd235b0fa863b3efc62e091a97242f295aea`;
- E3 workflow `33812352925` — PASS;
- E3 artifact `dnd-custom-aid-debug-apk`, ID `9915350879`.

**Full Batch E gate is closed GREEN.**

## 10. Current batch — F Equipo + Monedas

Goal: close the approved equipment/currency usability and metadata scope while preserving existing inventory persistence and the invariant that alphabetical display never destroys manual stored order.

Batch F targets:

- much denser ordinary-equipment rows;
- much more compact currencies;
- independent Manual/A–Z presentation for ordinary equipment and special equipment;
- real drag feedback in Manual mode only; drag disabled/hidden in A–Z;
- search/filter without changing stored order;
- total carried weight + attunement summary;
- inventory containers/locations;
- consumable/ammunition metadata and quick-use interaction;
- duplicate/collapse behavior;
- tablet multi-column/master-detail behavior where useful;
- preserve existing IME-safe editor semantics.

Gate F requires sort/order, quantity/weight/attunement, persistence, IME and responsive checks before G1.

## 11. Existing baseline that must not regress

The Phase 4 character implementation already contains persistent:

- General/Habilidades;
- Combate;
- Equipo/currencies;
- Trasfondo including Raza and Religión / Fe;
- Rasgos;
- conditional Conjuros with sources/prepared/shared slots;
- Notas;
- consolidated PC Settings;
- experimental Supercompact projection;
- D-0046 derived values/adjustments;
- SQLDelight migrations/persistence tests;
- schema-6 class/subclass/proficiency/resource/form/companion foundation;
- schema-7 closure persistence under A2.

## 12. Final acceptance boundary

The future closure candidate must be one frozen APK with exact commit/workflow/artifact/hash identity.

Owner acceptance matrix must include:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. representative larger text scale.

Green CI never substitutes for real-device IME/drag/layout/tablet QA.

## 13. Merge boundary

Do not merge Phase 4 to `main` until:

- all D-0047 batches are implemented;
- automated gates are green;
- one exact closure QA APK is recorded;
- owner phone+tablet QA is accepted;
- blocking findings are resolved;
- final continuity/governance housekeeping is complete;
- owner explicitly approves merge/closure.

## 14. Exact continuation

Resume **Batch F — Equipo + Monedas** on `implementation/phase4-character-closure`.

Start by defining the F presentation/query/consumable helpers and tests over the existing inventory + schema-7 `CharacterInventoryUsage` state. Then wire dense ordinary/special sections, independent Manual/A–Z mode, search/filter, compact currencies and quick-use behavior. Preserve manual stored order and existing special-item data. Add tablet layout improvements proportionately and close Gate F before beginning G1.

# Project State

**Last verified:** 2026-09-03  
**Canonical branch:** `main` — untouched by Phase 4 closure work  
**Phase 4 durable historical line:** `implementation/character-data-foundation`  
**Active focused closure branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 Character Foundation Closure  
**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; **Batch C active**  
**DM work:** explicitly blocked until Phase 4 closure is fully implemented, phone+tablet QA accepted, and owner approves closure/merge

## 0. Primary resume order

1. `docs/checkpoints/2026-09-03_PHASE4_BATCH_B2_ORDERING_CONTEXT_DRAG.md` — completed B2 gate and exact continuation into C;
2. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — small recoverable execution batches;
3. `docs/checkpoints/2026-09-03_PHASE4_BATCH_A2B_PERSISTENCE.md` — completed schema-7 persistence gate;
4. `docs/checkpoints/2026-09-03_PHASE4_BATCH_A2A_SCHEMA_DOMAIN.md` — completed schema/domain gate;
5. `docs/checkpoints/2026-09-03_PHASE4_BATCH0_A1_HOUSEKEEPING_AND_CATALOG.md` — completed housekeeping/A1 checkpoint;
6. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level A–J map and final QA matrix;
7. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved product/design scope;
8. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — class/subclass/module audit.

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

Housekeeping results:

- stale Phase 3/empty-scaffold status removed;
- closure-first and DM-blocked sequencing made explicit;
- phone/tablet portrait/landscape closure QA made explicit;
- current implementation areas documented;
- stable CI debug signing contradiction resolved in wording: the tracked/reconstructable identity is development-only for update-in-place QA and never a release identity.

Known remaining index-maintenance item:

- consolidated `docs/DECISIONS.md` still ends at D-0043 while detailed approved D-0044–D-0047 records exist. This is visible, non-blocking for implementation, and must be reconciled before the eventual Phase 4 merge proposal without renumbering historical decisions.

## 3. Batch A1 — official catalog reconciliation — GREEN

Implemented:

- code commit `1b2f6a79924b88d71567a277f6752dea6bbbb1c9`;
- test commit `dd6f50afebe862222861ee8ccb39cfe99ee82df1`.

A1 verification:

- workflow `33785858196`;
- tested head `dd6f50afebe862222861ee8ccb39cfe99ee82df1`;
- conclusion **PASS**.

## 4. Batch A2 — schema 7 durable closure data — GREEN

### A2a

Added additive schema-7/domain definitions for:

- Conditions/Exhaustion;
- Defenses;
- special movement/senses;
- Concentration;
- recovery/rest metadata;
- consumable/ammunition metadata;
- portrait/token references;
- reconciliation checkpoints;
- XP/Milestone progress;
- custom skills;
- temporary effects;
- module overrides;
- Table mode;
- haptic preference;
- generic Quick Access.

Schema 6 was not rewritten. Resource/inventory extension metadata intentionally uses character-scoped soft UUID references because the existing core repository rewrites those child rows during save.

A2a controlling verification:

- workflow `33786927646`;
- tested code head `6f35997be2d11ce96b866a68c426a39671cd20ba`;
- conclusion **PASS**.

### A2b

Added `CharacterClosureRepository` as an additive persistence boundary beside the existing proven core `CharacterRepository`. It provides safe defaults, transactional save/load, validation, stale soft-reference normalization, module override persistence and Quick Access persistence.

Regression suite covers rich closure-state round trip, additive migration, rewrite safety, stale-reference behavior, hide-no-delete separation and custom Quick Access.

An initial SQLDelight mapper typing failure was diagnosed and repaired by mapping query rows to non-null raw rows first and applying `mapNotNull` after execution.

A2b controlling verification:

- workflow `33787986897`;
- tested head `7f63e6be2ccdbb584152057a75887a2f74a658e9`;
- includes test commit `ee0cdeae9341a3818fe0e704eb543832d7c214c4`;
- backend PASS;
- shared Kotlin/tests + SQLDelight PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- APK artifact upload PASS.

**Full A2 gate is closed GREEN.**

## 5. Batch B1 — global editor/IME/action foundation — GREEN

B1 introduced the reusable IME-safe character editor shell and consistent action/confirmation/empty-state primitives, then migrated the existing character keyboard editors to that shared behavior.

The migration covers character creation, Combat, Equipment, Trasfondo, Rasgos, Notes, spell sources, spell entries, Speed, spell-slot configuration and derived-value adjustment editors. Editable dialogs keep actions reachable above the Android keyboard; card/row tap is the primary edit affordance on migrated collection surfaces.

Final B1 code head:

- `79092402e7ff0b93579bc785f891e5e95a0333ed`.

Final B1 workflow:

- `33791637168`;
- backend PASS;
- shared/Kotlin tests PASS;
- Android debug assembly PASS;
- Desktop build PASS;
- APK upload PASS.

**B1 gate is closed GREEN.**

## 6. Batch B2 — ordering/search/context/drag foundation — GREEN

Controlling checkpoint:

- `docs/checkpoints/2026-09-03_PHASE4_BATCH_B2_ORDERING_CONTEXT_DRAG.md`.

Implemented foundations:

- shared Manual/A–Z presentation helpers that preserve stored manual order;
- case/accent-insensitive search and immutable filter/query state;
- reusable count/search/filter/sort toolbar;
- reusable lifted/translated drag surface and insertion indicator;
- configurable semantic haptic hook, ready for the persisted PC setting to be wired in C;
- derived `Cambios sin guardar` state based on actual draft-versus-stored content;
- unsaved-leave guard: `Guardar` / `Descartar` / `Seguir editando`;
- context-preserving parent-owned state contract for later list-heavy domains;
- visible drag-feedback proof integrations in Notes and Combat.

Verification:

- presentation/toolbar/drag foundation workflow `33792391465` — PASS;
- unsaved-leave/dirty-state workflow `33793135304` — PASS;
- Notes real-drag workflow `33793677310` — PASS;
- Combat real-drag workflow `33794100599` — PASS.

All controlling runs include backend, shared/Kotlin tests, Android debug assembly, Desktop build and APK upload.

**B2 gate is closed GREEN.**

## 7. Current batch — C PC Settings

Goal: consolidate durable character-wide settings without moving application-wide preference ownership into the character model.

Already verified before C implementation:

- schema-7 `CharacterClosureState` already persists `progressMode`, `experiencePoints`, `milestoneProgress`, `tableModeEnabled`, `hapticsEnabled` and module overrides;
- `CharacterClosureRepository.state(characterId)` / `saveState(characterId, state)` is the existing persistence boundary;
- therefore those controls do not require a new schema migration;
- application-wide theme/font/text preferences remain owned by the existing `UiPreferences` / global settings surface;
- PC Settings should expose an entry that opens that existing application-settings surface rather than duplicating it.

Batch C implementation targets:

- lifecycle status moved/consolidated into PC Settings;
- haptics setting;
- Table/read-only setting;
- XP/Milestone progress mode and values;
- conditional module overrides;
- existing spellcasting hide-not-delete setting;
- `Configuración de la aplicación` navigation entry to the existing global settings UI;
- correct Back hierarchy and context preservation.

## 8. Existing baseline that must not regress

The Phase 4 character implementation already contains persistent:

- General/Habilidades;
- Combate;
- Equipo/currencies;
- Trasfondo including Raza and Religión / Fe;
- Rasgos;
- conditional Conjuros with sources/prepared/shared slots;
- Notas;
- PC Settings spellcasting hide-not-delete behavior;
- D-0046 derived values/adjustments;
- SQLDelight migrations/persistence tests;
- schema-6 class/subclass/proficiency/resource/form/companion foundation;
- schema-7 closure persistence under A2.

## 9. Final acceptance boundary

The future closure candidate must be one frozen APK with exact commit/workflow/artifact/hash identity.

Owner acceptance matrix must include:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. representative larger text scale.

Green CI never substitutes for real-device IME/drag/layout/tablet QA.

## 10. Merge boundary

Do not merge Phase 4 to `main` until:

- all D-0047 batches are implemented;
- automated gates are green;
- one exact closure QA APK is recorded;
- owner phone+tablet QA is accepted;
- blocking findings are resolved;
- final continuity/governance housekeeping is complete;
- owner explicitly approves merge/closure.

## 11. Exact continuation

Resume **Batch C — PC Settings** on `implementation/phase4-character-closure`.

Wire the existing persisted `CharacterClosureState` through `CharacterClosureRepository`, preserve application-wide preference ownership, verify save/reopen/Back behavior, and checkpoint C before beginning D.

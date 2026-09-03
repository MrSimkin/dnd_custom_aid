# Project State

**Last verified:** 2026-09-03  
**Canonical branch:** `main` — untouched by Phase 4 closure work  
**Phase 4 durable historical line:** `implementation/character-data-foundation`  
**Active focused closure branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 Character Foundation Closure  
**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; **B1 active**  
**DM work:** explicitly blocked until Phase 4 closure is fully implemented, phone+tablet QA accepted, and owner approves closure/merge

## 0. Primary resume order

1. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — small recoverable execution batches;
2. `docs/checkpoints/2026-09-03_PHASE4_BATCH_A2B_PERSISTENCE.md` — completed schema-7 persistence gate;
3. `docs/checkpoints/2026-09-03_PHASE4_BATCH_A2A_SCHEMA_DOMAIN.md` — completed schema/domain gate;
4. `docs/checkpoints/2026-09-03_PHASE4_BATCH0_A1_HOUSEKEEPING_AND_CATALOG.md` — completed housekeeping/A1 checkpoint;
5. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level A–J map and final QA matrix;
6. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved product/design scope;
7. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — class/subclass/module audit.

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

## 5. Current batch — B1 global editor/IME/action foundation

Goal: fix cross-cutting editor behavior once, then migrate the existing character editors to the shared pattern.

B1 scope:

- reusable IME-aware editor/dialog body/action layout so critical actions remain reachable while the Android keyboard is visible;
- consistent primary/secondary action semantics (`Guardar`/`Cancelar` or creation verb where genuinely appropriate);
- stable add/edit/delete/duplicate icon/touch/accessibility grammar;
- inline validation support;
- named destructive confirmations;
- useful empty-state primitive;
- preserve existing Android Back hierarchy and draft behavior.

Initial read-only inventory already confirmed that existing tabs such as Combat, Equipment and Notes each own separate `AlertDialog`/editor implementations; outer-list `.imePadding()` alone is therefore insufficient. B1 should introduce reusable primitives rather than continue one-off padding fixes.

## 6. Existing baseline that must not regress

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

## 7. Final acceptance boundary

The future closure candidate must be one frozen APK with exact commit/workflow/artifact/hash identity.

Owner acceptance matrix must include:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. representative larger text scale.

Green CI never substitutes for real-device IME/drag/layout/tablet QA.

## 8. Merge boundary

Do not merge Phase 4 to `main` until:

- all D-0047 batches are implemented;
- automated gates are green;
- one exact closure QA APK is recorded;
- owner phone+tablet QA is accepted;
- blocking findings are resolved;
- final continuity/governance housekeeping is complete;
- owner explicitly approves merge/closure.

## 9. Exact continuation

Resume **Batch B1 — global IME/editor/action foundation** on `implementation/phase4-character-closure`.

Create the reusable interaction primitives first, migrate representative existing editors, run the B1 Android/shared gate, and checkpoint before beginning B2.

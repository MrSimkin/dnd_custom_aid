# Project State

**Last verified:** 2026-09-03  
**Canonical branch:** `main` — untouched by Phase 4 closure work  
**Phase 4 durable historical line:** `implementation/character-data-foundation`  
**Active focused closure branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 Character Foundation Closure  
**Current execution position:** Batch 0 complete; Batch A1 implemented; Gate A1 awaiting/finalizing CI  
**DM work:** explicitly blocked until Phase 4 closure is fully implemented, phone+tablet QA accepted, and owner approves closure/merge

## 0. Primary resume order

1. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — small recoverable execution batches;
2. `docs/checkpoints/2026-09-03_PHASE4_BATCH0_A1_HOUSEKEEPING_AND_CATALOG.md` — latest completed/active checkpoint;
3. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level A–J map and final QA matrix;
4. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved product/design scope;
5. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — class/subclass/module audit.

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

## 3. Batch A1 — official catalog reconciliation

Implemented:

- code commit `1b2f6a79924b88d71567a277f6752dea6bbbb1c9`;
- test commit `dd6f50afebe862222861ee8ccb39cfe99ee82df1`.

Changes:

- all eight Arcana Unleashed audited subclasses no longer carry stale upcoming/early-access metadata;
- source/version/module information remains intact;
- focused tests now cover all eight keys, representative module triggers, current-vs-legacy Artificer/Battle Smith provenance and the open custom escape path.

The code commit diff was inspected and contained only the intended catalog availability/helper changes.

A1 CI:

- run `33785858196`;
- tested head `dd6f50afebe862222861ee8ccb39cfe99ee82df1`;
- final conclusion must be checked before Gate A1 is declared closed.

## 4. Next batch — A2 schema 7

Do not begin until A1 is green.

A2 adds the remaining approved durable D-0047 domains through an additive schema-7 migration, including:

- Conditions/Exhaustion;
- Defenses;
- special senses/movement;
- Concentration;
- recovery/rest metadata;
- intentional inventory consumable/container metadata;
- portrait/token reference metadata;
- reconciliation checkpoints;
- XP/Milestone progress;
- custom skills;
- temporary effects;
- module overrides/visibility;
- Table mode;
- haptic preference;
- any required Supercompact configuration;
- consistent Favorite/Quick Access state.

A2 Gate requires create-from-scratch schema, migration preservation, holistic repository round trip and shared tests before Android closure UI work proceeds.

## 5. Existing baseline that must not regress

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
- SQLDelight migrations/persistence tests.

The schema-6 closure prototype additionally contains class/subclass provenance, Inspiration/death saves, proficiencies, Weapon Mastery, generic Resources, class options, Forms and Companions.

## 6. Final acceptance boundary

The future closure candidate must be one frozen APK with exact commit/workflow/artifact/hash identity.

Owner acceptance matrix must include:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. representative larger text scale.

Green CI never substitutes for real-device IME/drag/layout/tablet QA.

## 7. Merge boundary

Do not merge Phase 4 to `main` until:

- all D-0047 batches are implemented;
- automated gates are green;
- one exact closure QA APK is recorded;
- owner phone+tablet QA is accepted;
- blocking findings are resolved;
- final continuity/governance housekeeping is complete;
- owner explicitly approves merge/closure.

## 8. Exact continuation

Check GitHub Actions run `33785858196`.

- If PASS: record A1 green and begin **Batch A2 — schema 7**.
- If FAIL: diagnose and repair A1 before any schema-7 work.

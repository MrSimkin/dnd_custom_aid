# Project State

**Last verified:** 2026-09-03  
**Canonical branch:** `main` — untouched by Phase 4 closure work  
**Phase 4 durable historical line:** `implementation/character-data-foundation`  
**Active focused closure branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 Character Foundation Closure  
**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; F GREEN; **Batch G1 active**
**DM work:** explicitly blocked until Phase 4 closure is fully implemented, phone+tablet QA accepted, and owner approves closure/merge

## 0. Primary resume order

1. `docs/checkpoints/2026-09-03_PHASE4_BATCH_F_EQUIPMENT_CURRENCIES.md` — completed full F gate and exact continuation into G1;
2. `docs/checkpoints/2026-09-03_PHASE4_BATCH_F3_TABLET_MASTER_DETAIL.md` — completed wide Equipment master-detail gate;
3. `docs/checkpoints/2026-09-03_PHASE4_BATCH_F2_EQUIPMENT_UI.md` — dense Equipment/Currencies UI integration;
4. `docs/checkpoints/2026-09-03_PHASE4_BATCH_F1_INVENTORY_FOUNDATION.md` — inventory usage/order/filter foundation and schema-8 carry-state migration;
5. `docs/checkpoints/2026-09-03_PHASE4_BATCH_E_GENERAL_SKILLS_COMBAT.md` — completed E gate;
6. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — small recoverable execution batches;
7. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level A–J map and final QA matrix;
8. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved product/design scope;
9. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — class/subclass/module audit;
10. earlier A/B/C/D/E checkpoints as historical implementation evidence.

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

## 4. Batch A2 — durable closure data — GREEN

A2 added additive schema/domain definitions and `CharacterClosureRepository` for Conditions/Exhaustion, Defenses, movement/senses, Concentration, recovery metadata, consumable metadata, portrait/token references, reconciliation, XP/Milestone, custom skills, temporary effects, module overrides, Table mode, haptics and Quick Access.

A2b controlling workflow `33787986897` — PASS across backend, shared/Kotlin tests + SQLDelight, Android debug, Desktop and APK upload.

## 5. Batch B1 — global editor/IME/action foundation — GREEN

Reusable IME-safe character editor and consistent action/confirmation/validation/empty-state primitives are integrated across the character keyboard editors.

Final B1 workflow `33791637168` — PASS across backend, shared/Kotlin tests, Android debug, Desktop and APK upload.

## 6. Batch B2 — ordering/search/context/drag foundation — GREEN

Implemented Manual/A–Z presentation helpers preserving stored manual order, reusable search/filter/query state, visible drag feedback and insertion indicators, semantic haptics, dirty/saved state, unsaved-leave guard and context-preservation primitives.

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

Delivered class/subclass/source identity, non-enforcing Hit Die suggestions, portrait/token references, Defenses/Senses/special Movement, Passive Perception/Insight/Investigation, custom skills in both Habilidades modes, compact combat metadata, quick HP operations, contextual death saves, combat-entry Favorites and a bounded simple d20 roller.

Verification:

- E2a workflow `33810868863` — PASS;
- E2b workflow `33811490741` — PASS;
- E3 controlling head `1ae8fd235b0fa863b3efc62e091a97242f295aea`;
- E3 workflow `33812352925` — PASS;
- E3 artifact `dnd-custom-aid-debug-apk`, ID `9915350879`.

## 10. Batch F — Equipo + Monedas — GREEN

Controlling checkpoint:

- `docs/checkpoints/2026-09-03_PHASE4_BATCH_F_EQUIPMENT_CURRENCIES.md`.

Delivered:

- dense ordinary and special inventory;
- materially compact currencies;
- independent Manual/A–Z presentation preserving stored manual order;
- visible Manual drag feedback with reorder hidden/disabled when projection makes it ambiguous;
- search and carried/stored/equipped/special/consumable/ammunition/location filters;
- total carried weight + attunement summary;
- explicit carried/stored metadata plus human-readable location/container;
- consumable/ammunition quick decrement;
- duplicate and collapsible sections;
- unified unsaved Equipment draft for item + currency + metadata;
- phone IME-safe modal editing;
- tablet/wide multi-column plus persistent master-detail editing with selected-row highlight and preserved list context.

Verification:

- F1 workflow `33813882408` — PASS;
- F2 workflow `33814616186` — PASS;
- F2 artifact ID `9916138915`;
- F3 implementation `599926c94ebf4b2f7b0f09171255e4fa90152c2f`;
- F3 controlling checkpoint head `a0f41acf0ad440f0af43694ee5e46a4c0d7f8c17`;
- F3 workflow `33816879652` — PASS;
- F3 artifact `dnd-custom-aid-debug-apk`, ID `9916904744`, digest `sha256:4ed09ae593f514967c16f256a7542e73467d5aea0bc7673fa25d95207eb01822`.

These are integration artifacts, not the future frozen owner-QA candidate.

## 11. Current batch — G1 Rasgos closure

Goal: complete Rasgos usability and Quick Access integration without changing its established durable trait model.

G1 targets:

- group/filter/search by source and trait type;
- clearer remaining/max use meter while retaining exact manual Spend/Recover controls;
- Favorite / Quick Access using existing `CharacterQuickAccessKind.TRAIT` state;
- duplicate action;
- real shared drag feedback/manual ordering;
- responsive tablet grouping;
- preserve current IME-safe trait editor and persistence behavior.

Implementation discipline:

- begin with pure trait presentation/grouping/duplicate/reorder helpers + tests;
- then wire Android UI and closure-state Favorites;
- keep drag disabled when a filtered/search projection would make persisted reorder ambiguous;
- no new schema is expected for G1.

G1 must have its own green checkpoint before beginning G2 Conjuros.

## 12. Existing baseline that must not regress

The Phase 4 character implementation already contains persistent General/Habilidades, Combate, Equipo/currencies, Trasfondo including Raza and Religión/Fe, Rasgos, conditional Conjuros with sources/prepared/shared slots, Notas, consolidated PC Settings, experimental Supercompact projection, D-0046 derived values/adjustments, SQLDelight migrations/persistence tests, schema-6 class/subclass/proficiency/resource/form/companion foundation and closure-state persistence.

## 13. Final acceptance boundary

The future closure candidate must be one frozen APK with exact commit/workflow/artifact/hash identity.

Owner acceptance matrix must include:

1. phone portrait;
2. phone landscape;
3. tablet portrait;
4. tablet landscape;
5. representative larger text scale.

Green CI never substitutes for real-device IME/drag/layout/tablet QA.

## 14. Merge boundary

Do not merge Phase 4 to `main` until all D-0047 batches are implemented, automated gates are green, one exact closure QA APK is recorded, owner phone+tablet QA is accepted, blocking findings are resolved, final continuity/governance housekeeping is complete and owner explicitly approves merge/closure.

## 15. Exact continuation

Resume **Batch G1 — Rasgos** on `implementation/phase4-character-closure`.

Start with pure Rasgos presentation/group/filter/search/duplicate/manual-reorder helpers and focused tests. After that gate is green, wire Favorites, grouping/search/filter, usage meter, duplicate and shared visible drag feedback into `CharacterTraitsTabV4`, preserving the existing IME-safe editor and durable trait persistence. Close G1 before beginning G2 Conjuros.

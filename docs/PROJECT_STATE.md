# Project State

**Last verified:** 2026-09-03  
**Canonical branch:** `main` — untouched by Phase 4 closure work  
**Phase 4 durable historical line:** `implementation/character-data-foundation`  
**Active focused closure branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 Character Foundation Closure  
**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; F GREEN; G1 GREEN; G2 GREEN; G3 GREEN; H1 GREEN; **Batch H2 active**
**DM work:** explicitly blocked until Phase 4 closure is fully implemented, phone+tablet QA accepted, and owner approves closure/merge

## 0. Primary resume order

1. `docs/checkpoints/2026-09-03_PHASE4_BATCH_H1_ARTIFICER_FORMS.md` — completed H1 gate and exact continuation into H2;
2. `docs/checkpoints/2026-09-03_PHASE4_BATCH_H1_ARTIFICER_FORMS_AUDIT.md` — H1 ownership audit and reusable-module precedent;
3. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — approved mapping for Técnicas, Metamagia, Pactos and later Compañeros;
4. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — small recoverable execution batches;
5. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level map and final QA matrix;
6. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved product/design scope;
7. earlier A/B/C/D/E/F/G checkpoints as historical implementation evidence.

## 1. Closure scope status

D-0047 is owner-approved. The closure includes all retained QA fixes and owner requirements, F01–F18, D01–D18, I01–I22, official class/subclass identity including Artificer, conditional reusable modules, Gestión, PC Settings consolidation, Supercompact/Table mode, global IME/order/context UX, backup/import and first-class phone/tablet behavior.

No DM feature implementation begins before the Phase 4 exit gate.

## 2. Batch 0 housekeeping — COMPLETE

Current-truth documentation was refreshed. Known remaining governance item: consolidated `docs/DECISIONS.md` still ends at D-0043 while detailed later decision files exist. Reconcile before merge proposal without renumbering historical decisions.

## 3. Batch A1 — official catalog reconciliation — GREEN

- code `1b2f6a79924b88d71567a277f6752dea6bbbb1c9`;
- tests `dd6f50afebe862222861ee8ccb39cfe99ee82df1`;
- workflow `33785858196` — PASS.

## 4. Batch A2 — durable closure data — GREEN

Additive closure domain/repository foundation. Controlling workflow `33787986897` — PASS across backend, shared/Kotlin tests + SQLDelight, Android debug, Desktop and APK upload.

## 5. Batch B1 — global editor/IME/action foundation — GREEN

Reusable IME-safe editors and consistent action/confirmation/validation/empty-state primitives. Workflow `33791637168` — PASS.

## 6. Batch B2 — ordering/search/context/drag foundation — GREEN

Manual/A–Z presentation, reusable search/filter state, visible drag/drop feedback, haptics, saved/dirty state and context-preservation primitives. Workflows `33792391465`, `33793135304`, `33793677310`, `33794100599` — PASS.

## 7. Batch C — PC Settings consolidation — GREEN

Lifecycle status, spellcaster hide-not-delete, haptics/Table/XP, module overrides, Application Settings, responsive PC Settings and Supercompact entry. Workflow `33796586608` — PASS.

Full Table-mode suppression and final Supercompact behavior remain assigned to I2.

## 8. Batch D — Gestión live maintenance — GREEN

Conditions/Exhaustion, Concentration, generic Resources, Rest preview/selective apply, temporary effects, Inspiration, contextual death saves, reconciliation and responsive Gestión. Workflow `33809045740` — PASS.

## 9. Batch E — General + Habilidades + Combate — GREEN

Class/subclass/source identity, hit-die suggestions, portrait/token, defenses/senses/movement, passive values, custom skills, combat metadata, quick HP, death saves, combat Favorites and simple d20. E3 workflow `33812352925` — PASS; artifact ID `9915350879`.

## 10. Batch F — Equipo + Monedas — GREEN

Dense inventory, compact currencies, independent Manual/A–Z, search/filters, visible drag, carried/stored + location metadata, weight/attunement summary, consumables/ammunition, duplicate/collapse, unified unsaved Equipment draft and tablet master-detail.

Verification:

- F1 `33813882408` — PASS;
- F2 `33814616186` — PASS;
- F3 `33816879652` — PASS;
- F3 artifact ID `9916904744`, digest `sha256:4ed09ae593f514967c16f256a7542e73467d5aea0bc7673fa25d95207eb01822`.

## 11. Batch G1 — Rasgos — GREEN

Delivered pure search/filter/group/reorder/duplicate/use-meter operations, type/source/Favorite filters, responsive grouping, visible drag+haptics, usage controls, Quick Access ★/☆, duplicate, named delete and IME-safe editing.

Verification:

- G1a workflow `33817330178` — PASS;
- G1 controlling workflow `33817819671` — PASS;
- artifact ID `9917236440`, digest `sha256:a81ef352b6c1a8e19cf6a5be2d582fb56c2bc1276f072d86e1e0817993adefa8`.

## 12. Batch G2 — Conjuros — GREEN

Delivered pure Manual/A–Z, search/filter, duplicate and source-projected manual-order operations; one conceptual spell collection across `Todos` and source views; source-specific Prepared state; compact badges; collapsible sticky spell levels; shared slots; Favorites; visible drag/haptics; phone IME-safe editor; tablet master-detail and narrow-phone action refinement.

Verification:

- G2a workflow `33818362184` — PASS;
- first full G2b workflow `33822274027` — PASS;
- density product refinement `498df76ce092d81c965f4eb36a3c8bbd8486d91c`;
- final controlling checkpoint head `7289c19e7db8db53ca50947b71792aed732bd0fc`;
- final workflow `33822722007` — PASS;
- final artifact ID `9918883401`, digest `sha256:69df9fa57d7401d95c30417afc7e81705d574559b44686b6b845a72dca514a25`.

## 13. Batch G3 — Notas + Trasfondo — GREEN

Delivered titled-note normalize/move/duplicate helpers, Notes context retention, General Notes and 3-line previews, visible drag/haptics, IME-safe editor, unchanged Raza and Religión/Fe, honest image placeholders, and collapsible long Story.

Verification:

- G3a workflow `33823030581` — PASS;
- G3 UI integration `1fbd6d0b4260c5a804ee2dae6ab3b71348d67d46`;
- final controlling checkpoint head `9182d4841de2d568ba001ceeffff1ec718ab0220`;
- final workflow `33823386958` — PASS;
- final artifact ID `9919122343`, digest `sha256:588b1e90f3790acf6d39332cb25548fd8bc2ef06b856da98896a741897bced11`.

## 14. Batch H1 — Artífice + Formas — GREEN

Controlling checkpoint:

- `docs/checkpoints/2026-09-03_PHASE4_BATCH_H1_ARTIFICER_FORMS.md`.

Architecture/result:

- H1 audit confirmed no schema/migration was needed;
- existing durable `classOptions` and `forms` remain authoritative;
- Artífice owns only `ARTIFICER_PLAN` + `ARTIFICER_DEVICE` projections while preserving all hidden H2 class-option entries;
- Formas uses the existing generic durable form records;
- both surfaces support search, Manual/A–Z, visible drag/haptics, Favorite, duplicate, named delete, IME-safe phone editor and wide master-detail;
- conditional navigation uses the existing class/subclass suggestion + PC Settings override union;
- hiding a module is non-destructive;
- H1 joins the global unsaved-change/Save/Discard model;
- successful Save prunes stale `CLASS_OPTION` / `FORM` Quick Access references.

Verification:

- H1a workflow `33824278439` — PASS;
- H1 integration commit `aa6fa34d318fde31f433c5aef15a3c322dcf1483`;
- controlling checkpoint head `1196832fe6c05c70b471fb5c14ba527f8e85ae87`;
- final workflow `33825152159` — PASS;
- backend type-check — PASS;
- full shared/Kotlin tests — PASS;
- Android debug assembly — PASS;
- Desktop build — PASS;
- artifact `dnd-custom-aid-debug-apk`, ID `9919731254`;
- digest `sha256:67811cf83f33ff22a2516e6f1063a897e3323232fb2eee5a9f52095a98ead35e`.

These APKs remain integration evidence, not the future frozen owner-QA candidate.

## 15. Current batch — H2 Técnicas + Metamagia + Pactos

H2 reuses the shared `CharacterClassOption` model. The first required task is an explicit ownership audit before adding UI.

Audit inputs:

- `CharacterClassOptionKind.TECHNIQUE`;
- `CharacterClassOptionKind.METAMAGIC`;
- `CharacterClassOptionKind.INVOCATION`;
- `CharacterClassOptionKind.SUBCLASS_STATE` and `OTHER` only where the approved module audit demonstrates a genuine H2 ownership need;
- existing H1 `classOptions` draft/Save integration;
- `CharacterModuleKind.TECHNIQUES`, `METAMAGIC`, `PACTS` suggestions + PC Settings overrides;
- generic Resources and Spells, which remain authoritative for resource counters and spell records/slots.

Required design invariants:

- each H2 surface owns an explicit subset/projection of the shared `classOptions` collection;
- reordering one H2 surface must not move hidden Artífice or other H2-family entries;
- Manual/A–Z presentation must not destroy saved manual order;
- module hide/show remains non-destructive;
- Favorite uses `CharacterQuickAccessKind.CLASS_OPTION` rather than duplicate state;
- multiclass union exposes each module at most once;
- no rules legality engine and no automatic reconstruction of official feature text;
- arbitrary manual/homebrew entries remain allowed;
- no schema change unless the audit identifies an actual durable-data gap.

Execution order:

1. document H2 ownership audit;
2. add pure presentation/search/filter/manual-order/duplicate helpers and focused tests;
3. H2a shared gate;
4. only after H2a GREEN, add Android Técnicas/Metamagia/Pactos surfaces and conditional navigation;
5. run the controlling H2 gate and checkpoint exact workflow/artifact identity.

## 16. Existing baseline that must not regress

Persistent General/Habilidades, Combate, Gestión, Equipo/currencies, Trasfondo including Raza and Religión/Fe, Rasgos, conditional Conjuros with sources/prepared/shared slots, Notas, Artífice, Formas, PC Settings, Supercompact projection, D-0046 derived values/adjustments and all migrations/repositories must remain compatible.

## 17. Final acceptance boundary

The future closure candidate must be one frozen APK with exact commit/workflow/artifact/hash identity. Owner QA matrix: phone portrait, phone landscape, tablet portrait, tablet landscape and representative larger text scale. Green CI never substitutes for real-device QA.

## 18. Merge boundary

Do not merge Phase 4 to `main` until all D-0047 batches are implemented, automated gates are green, one exact closure QA APK is recorded, owner phone+tablet QA is accepted, blocking findings are resolved, governance housekeeping is complete and owner explicitly approves merge/closure.

## 19. Exact continuation

Resume **Batch H2 — Técnicas + Metamagia + Pactos** on `implementation/phase4-character-closure`.

Start by documenting exact `CharacterClassOptionKind` ownership for the three modules and confirming whether the current shared fields (`linkedClassId`, `name`, `source`, `costText`, `effectSummary`, `notes`, `active`, `pinned`, `sortOrder`) are sufficient. If sufficient, do not add schema. Then implement H2a pure operations + tests and gate them before any Compose UI.
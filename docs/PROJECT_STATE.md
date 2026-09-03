# Project State

**Last verified:** 2026-09-03  
**Canonical branch:** `main` — untouched by Phase 4 closure work  
**Phase 4 durable historical line:** `implementation/character-data-foundation`  
**Active focused closure branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 Character Foundation Closure  
**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; F GREEN; G1 GREEN; **Batch G2 active**
**DM work:** explicitly blocked until Phase 4 closure is fully implemented, phone+tablet QA accepted, and owner approves closure/merge

## 0. Primary resume order

1. `docs/checkpoints/2026-09-03_PHASE4_BATCH_G1_TRAITS.md` — completed Rasgos gate and exact continuation into G2;
2. `docs/checkpoints/2026-09-03_PHASE4_BATCH_F_EQUIPMENT_CURRENCIES.md` — completed full F gate;
3. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — small recoverable execution batches;
4. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level A–J map and final QA matrix;
5. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved product/design scope;
6. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — class/subclass/module audit;
7. earlier A/B/C/D/E/F checkpoints as historical implementation evidence.

## 1. Closure scope status

D-0047 is owner-approved. The closure includes all retained QA fixes and owner requirements, F01–F18, D01–D18, I01–I22, official class/subclass identity including Artificer, conditional reusable modules, Gestión, PC Settings consolidation, Supercompact/Table mode, global IME/order/context UX, backup/import and first-class phone/tablet behavior.

No DM feature implementation begins before the Phase 4 exit gate.

## 2. Batch 0 housekeeping — COMPLETE

Current-truth documentation was refreshed. Known remaining governance item: consolidated `docs/DECISIONS.md` still ends at D-0043 while detailed approved D-0044–D-0047 records exist. Reconcile before merge proposal without renumbering historical decisions.

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

Controlling checkpoint:

- `docs/checkpoints/2026-09-03_PHASE4_BATCH_G1_TRAITS.md`.

Delivered:

- pure search/filter/group/reorder/duplicate/use-meter operations;
- type/source/Favorite filters;
- no/type/source grouping;
- responsive two-column groups;
- real visible grouped drag with haptics;
- usage progress meter + Spend/Recover;
- Quick Access ★/☆ using `CharacterQuickAccessKind.TRAIT`;
- duplicate and named delete;
- existing IME-safe editor retained;
- favorite references to durably deleted traits pruned only on successful global Save.

Verification:

- G1a workflow `33817330178` — PASS;
- G1 controlling head `95c246cafcc7147d6a1d02717456f1266a72e80f`;
- G1 workflow `33817819671` — PASS;
- artifact `dnd-custom-aid-debug-apk`, ID `9917236440`, digest `sha256:a81ef352b6c1a8e19cf6a5be2d582fb56c2bc1276f072d86e1e0817993adefa8`.

## 12. Current batch — G2 Conjuros closure

Goal: improve spell presentation/ordering/filtering and tablet editing while preserving the proven spell source/preparation/shared-slot model.

G2 targets from the approved execution plan:

- independent Manual/A–Z presentation inside level/source rules;
- filters;
- V/S/M, concentration, ritual and prepared badges;
- sticky/collapsible level groups;
- Favorites, duplicate and real reorder;
- shared-slot regression protection;
- tablet master-detail;
- preserve existing numeric spell-level input and unrestricted manual/custom content.

Implementation discipline:

- audit current spell/source/slot UI and codec first;
- define pure presentation/order/filter/duplicate helpers + focused tests before Compose changes;
- alphabetical presentation must never rewrite stored manual order;
- drag hidden/disabled when A–Z or a partial search/filter projection makes reorder ambiguous;
- Quick Access uses existing `CharacterQuickAccessKind.SPELL` and only persisted spell IDs;
- no schema expected unless the audit proves a missing durable concept.

G2 must have its own green checkpoint before G3 Notas + Trasfondo.

## 13. Existing baseline that must not regress

Persistent General/Habilidades, Combate, Equipo/currencies, Trasfondo including Raza and Religión/Fe, Rasgos, conditional Conjuros with sources/prepared/shared slots, Notas, PC Settings, Supercompact projection, D-0046 derived values/adjustments and all migrations/repositories must remain compatible.

## 14. Final acceptance boundary

The future closure candidate must be one frozen APK with exact commit/workflow/artifact/hash identity. Owner QA matrix: phone portrait, phone landscape, tablet portrait, tablet landscape and representative larger text scale. Green CI never substitutes for real-device QA.

## 15. Merge boundary

Do not merge Phase 4 to `main` until all D-0047 batches are implemented, automated gates are green, one exact closure QA APK is recorded, owner phone+tablet QA is accepted, blocking findings are resolved, governance housekeeping is complete and owner explicitly approves merge/closure.

## 16. Exact continuation

Resume **Batch G2 — Conjuros** on `implementation/phase4-character-closure`.

Audit the current spell list/editor/source/shared-slot implementation. Then add pure presentation/filter/order/duplicate helpers and tests, gate them, wire the responsive spell UI, and close G2 before beginning G3.

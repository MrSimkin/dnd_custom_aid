# Project State

**Last verified:** 2026-09-03  
**Canonical branch:** `main` — untouched by Phase 4 closure work  
**Phase 4 durable historical line:** `implementation/character-data-foundation`  
**Active focused closure branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 Character Foundation Closure  
**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; F GREEN; G1 GREEN; G2 GREEN; **Batch G3 active**
**DM work:** explicitly blocked until Phase 4 closure is fully implemented, phone+tablet QA accepted, and owner approves closure/merge

## 0. Primary resume order

1. `docs/checkpoints/2026-09-03_PHASE4_BATCH_G2_SPELLS.md` — completed Conjuros gate and exact continuation into G3;
2. `docs/checkpoints/2026-09-03_PHASE4_BATCH_G1_TRAITS.md` — completed Rasgos gate;
3. `docs/checkpoints/2026-09-03_PHASE4_BATCH_F_EQUIPMENT_CURRENCIES.md` — completed full F gate;
4. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — small recoverable execution batches;
5. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level map and final QA matrix;
6. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved product/design scope;
7. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — approved class/subclass/module audit;
8. earlier A/B/C/D/E/F checkpoints as historical implementation evidence.

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

Controlling checkpoint:

- `docs/checkpoints/2026-09-03_PHASE4_BATCH_G2_SPELLS.md`.

Delivered:

- pure Manual/A–Z, search/filter, duplicate and source-projected manual-order operations;
- one conceptual spell collection preserved across `Todos` and source views;
- source-specific Prepared state preserved;
- Favorite/Prepared/Concentration/Ritual/V/S/M filters;
- compact V/S/M/Concentration/Ritual/prepared badges;
- collapsible sticky spell-level headers;
- shared Quick Magic/Conjuros slot state retained;
- Favorites using existing Quick Access with orphan prevention/pruning on successful Save;
- duplicate and named delete;
- visible drag/haptics with A–Z/search/filter safety;
- phone IME-safe editor;
- tablet master-detail with selected-row highlight and independent editor scrolling;
- narrow-phone action column refinement so secondary actions do not unnecessarily squeeze spell content.

Verification:

- G2a workflow `33818362184` — PASS;
- first full G2b workflow `33822274027` — PASS;
- density product refinement `498df76ce092d81c965f4eb36a3c8bbd8486d91c`;
- final controlling checkpoint head `7289c19e7db8db53ca50947b71792aed732bd0fc`;
- final workflow `33822722007` — PASS;
- final artifact `dnd-custom-aid-debug-apk`, ID `9918883401`, digest `sha256:69df9fa57d7401d95c30417afc7e81705d574559b44686b6b845a72dca514a25`.

These APKs remain integration evidence, not the future frozen owner-QA candidate.

## 13. Current batch — G3 Notas + Trasfondo closure

Audit result: no schema change is required.

Existing Notes baseline already provides:

- one large unrestricted General Notes field;
- titled cards with 3-line preview;
- add/edit/delete;
- real visible drag with haptics;
- one-column phone and two-column wide presentation;
- IME-safe long-note editor with internal scrolling;
- persistence regression for very large General Notes and ordered note cards.

Remaining G3 Notes work is bounded to:

- duplicate titled note with a fresh UUID and deterministic appended order;
- explicit LazyList state/context retention while an editor overlays;
- preserve the deliberately lightweight title+content model; do not add tags/dates/categories.

Existing Background baseline already provides:

- background name + summary;
- Raza and Religión/Fe;
- compact personality/ideals/bonds/flaws preview cards with responsive two-column layout;
- two honest image placeholders;
- full long Story field;
- migration + round-trip regression protecting Raza/Religión.

Remaining G3 Background work is bounded to:

- make the long Story block collapsible with a useful short preview while preserving full editing when expanded;
- retain the approved two-image placeholder layout and all existing identity/narrative fields.

Implementation discipline:

- start with small pure note operations + focused tests for normalize/move/duplicate;
- gate those helpers before UI wiring;
- then update Notes and Background only;
- no schema, no new note metadata, no image-persistence expansion;
- G3 must have its own green checkpoint before beginning H1 Artífice + Formas.

## 14. Existing baseline that must not regress

Persistent General/Habilidades, Combate, Equipo/currencies, Trasfondo including Raza and Religión/Fe, Rasgos, conditional Conjuros with sources/prepared/shared slots, Notas, PC Settings, Supercompact projection, D-0046 derived values/adjustments and all migrations/repositories must remain compatible.

## 15. Final acceptance boundary

The future closure candidate must be one frozen APK with exact commit/workflow/artifact/hash identity. Owner QA matrix: phone portrait, phone landscape, tablet portrait, tablet landscape and representative larger text scale. Green CI never substitutes for real-device QA.

## 16. Merge boundary

Do not merge Phase 4 to `main` until all D-0047 batches are implemented, automated gates are green, one exact closure QA APK is recorded, owner phone+tablet QA is accepted, blocking findings are resolved, governance housekeeping is complete and owner explicitly approves merge/closure.

## 17. Exact continuation

Resume **Batch G3 — Notas + Trasfondo** on `implementation/phase4-character-closure`.

First add pure titled-note normalize/move/duplicate helpers and focused tests. After that gate is green, wire Duplicate + explicit list-context retention into Notes and make Background Story collapsible with a compact preview, retaining Raza/Religión and honest two-image placeholders. Close G3 before H1.

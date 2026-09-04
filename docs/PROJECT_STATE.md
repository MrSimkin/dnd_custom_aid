# Project State

**Last verified:** 2026-09-03  
**Canonical branch:** `main` — untouched by Phase 4 closure work  
**Phase 4 durable historical line:** `implementation/character-data-foundation`  
**Active focused closure branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 Character Foundation Closure  
**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; F GREEN; G1 GREEN; G2 GREEN; G3 GREEN; H1 GREEN; H2 GREEN; **Batch H3 active**
**DM work:** explicitly blocked until Phase 4 closure is fully implemented, phone+tablet QA accepted, and owner approves closure/merge

## 0. Primary resume order

1. `docs/checkpoints/2026-09-03_PHASE4_BATCH_H2_TECHNIQUES_METAMAGIC_PACTS.md` — completed H2 gate and exact continuation into H3;
2. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — approved companion triggers and module-union behavior;
3. `docs/checkpoints/2026-09-03_PHASE4_BATCH_H1_ARTIFICER_FORMS_AUDIT.md` — reusable-module ownership precedent;
4. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — small recoverable execution batches;
5. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level map and final QA matrix;
6. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — approved product/design scope;
7. earlier A/B/C/D/E/F/G/H checkpoints as historical implementation evidence.

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

- ownership audit confirmed no schema migration needed;
- Artífice owns only `ARTIFICER_PLAN` + `ARTIFICER_DEVICE` projections;
- Formas uses existing durable `forms`;
- both use search, Manual/A–Z, visible drag/haptics, Favorites, duplicate, named delete, IME-safe phone editor and wide master-detail;
- conditional navigation uses class/subclass suggestion + PC Settings override union;
- H1 joins global unsaved/Save/Discard;
- successful Save prunes stale `CLASS_OPTION` / `FORM` Quick Access references.

Verification:

- H1a workflow `33824278439` — PASS;
- integration commit `aa6fa34d318fde31f433c5aef15a3c322dcf1483`;
- final workflow `33825152159` — PASS;
- artifact ID `9919731254`, digest `sha256:67811cf83f33ff22a2516e6f1063a897e3323232fb2eee5a9f52095a98ead35e`.

## 15. Batch H2 — Técnicas + Metamagia + Pactos — GREEN

Controlling checkpoint:

- `docs/checkpoints/2026-09-03_PHASE4_BATCH_H2_TECHNIQUES_METAMAGIC_PACTS.md`.

Architecture/result:

- Técnicas owns `TECHNIQUE`;
- Metamagia owns `METAMAGIC`;
- Pactos owns `INVOCATION` + additive `PACT_CHOICE`;
- `SUBCLASS_STATE` and `OTHER` remain generic/unassigned;
- no SQL schema migration was required;
- H2a proves cross-family reorder isolation and real `PACT_CHOICE` repository round-trip;
- one reusable Android class-option module implementation drives three distinct user-facing surfaces;
- all support search, Active/Favorite/source filters, Manual/A–Z, visible drag/haptics, duplicate, named delete, linked-class/source/reference/notes/active editing, phone IME-safe editor and wide master-detail;
- Pactos distinguishes `Pacto / elección` from `Invocación`;
- Sorcery Points remain generic Resources and actual spells/slots remain Conjuros authority;
- all H2 surfaces reuse the full existing structural `classOptions` draft and global Save/Discard/Quick Access pruning;
- module visibility remains multiclass-union + manual PC Settings override and hide-not-delete.

Verification:

- H2a workflow `33826037339` — PASS;
- H2a artifact ID `9920055515`, digest `sha256:14728cf56308db200079d4f83a2c2578b9319e5bf4a26ef542844cd93e214cd2`;
- productive integration commit `24906e6239d96b9ac88fce80689b63a54e8ecca6`;
- final controlling checkpoint head `58089d22d3373f236459b28c19e28b066b8710b4`;
- final workflow `33826729095` — PASS;
- backend type-check — PASS;
- full shared/Kotlin tests — PASS;
- Android debug assembly — PASS;
- Desktop build — PASS;
- final artifact ID `9920277155`, digest `sha256:788406604e85dae2b5ae6f7c782c037e5e1622de527d1ca2f9c2994964ce8e0d`.

The first inline-YAML integration attempt (`33826551760`) failed before creating a job and produced no product change; the corrected H1-style guarded script workflow succeeded. All recorded H1/H2 APKs remain integration evidence, not the future frozen owner-QA candidate.

## 16. Current batch — H3 Compañeros + module-union integration

H3 must finish the approved conditional-module family and verify the union behavior holistically.

Existing durable `CharacterCompanion` fields to audit:

- UUID;
- optional linked class ID;
- name;
- freeform kind/type;
- source/provenance;
- optional armor class;
- optional max/current HP;
- temporary HP;
- speed text;
- ability summary;
- senses/proficiencies summary;
- traits/actions text;
- notes;
- active state;
- manual sort order.

H3 audit must confirm whether this is sufficient for durable character-owned companion reference/state without turning Companions into live combat state.

Required invariants:

- companion data remains durable character-sheet state, distinct from future DM live combat participants;
- official triggers such as Beast Master, Battle Smith, Wildfire Spirit, Reanimator, Vestige and other audited companion-producing options suggest one reusable module;
- multiclass/subclass union exposes `Compañeros` at most once;
- manual PC Settings override supports custom/homebrew characters;
- hiding the module does not delete companions;
- Favorite uses `CharacterQuickAccessKind.COMPANION`;
- search/filter/Manual-A–Z/visible drag follow the established collection rules;
- phone IME-safe editing and wide/tablet master-detail are required;
- current/max/temp HP are durable reference/maintenance values only; H3 does not create a DM initiative/combatant authority;
- no schema change unless the audit identifies a real missing durable field.

Execution order:

1. document H3 durable ownership/field audit;
2. add pure companion presentation/filter/order/duplicate helpers + tests;
3. run H3a shared gate;
4. only after H3a GREEN, add Android Compañeros UI and conditional navigation;
5. verify representative class/subclass triggers, multiclass union, manual override and hide-not-delete;
6. run controlling H3 gate and record exact artifact identity.

## 17. Existing baseline that must not regress

Persistent General/Habilidades, Combate, Gestión, Equipo/currencies, Trasfondo including Raza and Religión/Fe, Rasgos, conditional Conjuros with sources/prepared/shared slots, Notas, Artífice, Formas, Técnicas, Metamagia, Pactos, PC Settings, Supercompact projection, D-0046 derived values/adjustments and all migrations/repositories must remain compatible.

## 18. Final acceptance boundary

The future closure candidate must be one frozen APK with exact commit/workflow/artifact/hash identity. Owner QA matrix: phone portrait, phone landscape, tablet portrait, tablet landscape and representative larger text scale. Green CI never substitutes for real-device QA.

## 19. Merge boundary

Do not merge Phase 4 to `main` until all D-0047 batches are implemented, automated gates are green, one exact closure QA APK is recorded, owner phone+tablet QA is accepted, blocking findings are resolved, governance housekeeping is complete and owner explicitly approves merge/closure.

## 20. Exact continuation

Resume **Batch H3 — Compañeros + module-union integration** on `implementation/phase4-character-closure`.

First audit the current `CharacterCompanion` model and repository round-trip against the approved module requirements. If sufficient, do not add schema. Then implement pure companion operations/tests and gate H3a before adding Compose UI.
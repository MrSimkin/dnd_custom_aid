# Project State

**Last verified:** 2026-09-04 owner local time / 2026-09-04 UTC
**Canonical branch:** `main` — untouched by Phase 4 closure work; exact head `471c5570669a6007bea9796d8a2c25536b10be21`  
**Phase 4 durable historical line:** `implementation/character-data-foundation`  
**Active focused closure branch:** `implementation/phase4-character-closure`  
**Current phase:** Phase 4 Character Foundation Closure  
**Current execution position:** Batch 0 complete; A1 GREEN; A2 GREEN; B1 GREEN; B2 GREEN; C GREEN; D GREEN; E GREEN; F GREEN; G1 GREEN; G2 GREEN; G3 GREEN; H1 GREEN; H2 GREEN; H3 GREEN; I1 GREEN; I2a GREEN; I2b GREEN; **Batch I complete; Batch J GREEN; Batch K active**
**DM work:** explicitly blocked until Phase 4 closure is fully implemented, phone+tablet QA accepted, and owner approves closure/merge

## 0. Primary resume order

1. `docs/checkpoints/2026-09-04_PHASE4_BATCH_J_BACKUP_IMPORT_COMPLETE.md` — completed Batch J gate and exact transition into K;
2. `docs/checkpoints/2026-09-04_PHASE4_BATCH_J_PAUSE_HANDOFF.md` — historical J pause/recovery evidence only; no longer current operational state;
3. `docs/checkpoints/2026-09-04_PHASE4_BATCH_I2B_TABLE_MODE.md` — completed I2b gate and Batch I closure;
4. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — approved execution sequence through owner QA; its historical `Exact next action` is stale and superseded by this Project State;
5. `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_IMPLEMENTATION_MAP.md` — higher-level implementation/gate map and final QA matrix;
6. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` — controlling owner-approved closure scope;
7. `docs/CHARACTER_CLASS_SUBCLASS_MODULE_AUDIT.md` — approved class/subclass/module triggers and boundaries;
8. earlier A–I checkpoints as historical implementation/verification evidence.

## 1. Closure scope and merge boundary

D-0047 is owner-approved. The closure includes retained QA fixes and owner requirements, F01–F18, D01–D18, I01–I22, official class/subclass identity including Artificer, conditional reusable modules, Gestión, PC Settings consolidation, Supercompact/Table mode, global IME/order/context UX, backup/import and first-class phone/tablet behavior.

Do not merge Phase 4 to `main` until all remaining batches are implemented, automated gates are green, one exact closure QA APK is frozen and identified, owner phone+tablet QA is accepted, blocking findings are resolved, continuity/governance housekeeping is complete and the owner explicitly approves merge/closure.

No DM feature implementation begins before that exit gate.

## 2. Completed batch ledger

### Batch 0 — repository housekeeping — COMPLETE

Current-truth documentation was refreshed. Known remaining governance item: consolidated `docs/DECISIONS.md` still ends at D-0043 while detailed D-0044–D-0047 files exist. Reconcile before merge proposal without renumbering historical decisions.

### Batch A1 — official class/subclass catalog — GREEN

- code `1b2f6a79924b88d71567a277f6752dea6bbbb1c9`;
- tests `dd6f50afebe862222861ee8ccb39cfe99ee82df1`;
- workflow `33785858196` — PASS.

### Batch A2 — schema 7 durable closure data — GREEN

Additive schema/domain/repository foundation. Workflow `33787986897` — PASS across backend, shared/Kotlin/SQLDelight, Android debug, Desktop and APK upload.

### Batch B1 — global editor/IME/action foundation — GREEN

Reusable IME-safe editors and consistent action/confirmation/validation/empty-state primitives. Workflow `33791637168` — PASS.

### Batch B2 — ordering/search/context/drag foundation — GREEN

Manual/A–Z presentation, search/filter state, visible drag/reflow, haptics, saved/dirty state, unsaved-leave guard and D16 context primitives. Workflows `33792391465`, `33793135304`, `33793677310`, `33794100599` — PASS.

### Batch C — PC Settings consolidation — GREEN

Lifecycle status, spellcaster hide-not-delete, haptics, Table/XP settings, module overrides, Application Settings entry and Supercompact entry. Workflow `33796586608` — PASS. Full Table-mode suppression and final Supercompact behavior were completed in I2.

### Batch D — Gestión — GREEN

Conditions/Exhaustion, Concentration, Resources, Rest preview/selective apply, temporary effects, Inspiration, contextual death saves, reconciliation and responsive Gestión. Workflow `33809045740` — PASS.

### Batch E — General + Habilidades + Combate — GREEN

Class/subclass/source identity, hit-die suggestions, portrait/token, defenses/senses/movement, passive values, custom skills, combat metadata, quick HP, death saves, Favorites and simple d20. Workflow `33812352925` — PASS; artifact `9915350879`.

### Batch F — Equipo + Monedas — GREEN

Dense inventory, compact currencies, independent Manual/A–Z, search/filters, visible drag, carried/stored + location metadata, weight/attunement summary, consumables/ammunition, duplicate/collapse, unified unsaved Equipment draft and tablet master-detail.

Controlling F3 workflow `33816879652` — PASS; artifact `9916904744`, digest `sha256:4ed09ae593f514967c16f256a7542e73467d5aea0bc7673fa25d95207eb01822`.

### Batch G1 — Rasgos — GREEN

Search/filter/group/reorder/duplicate/use-meter operations, type/source/Favorite filters, responsive grouping, visible drag+haptics, usage controls, Quick Access, named delete and IME-safe editing. Controlling workflow `33817819671` — PASS; artifact `9917236440`.

### Batch G2 — Conjuros — GREEN

Manual/A–Z, search/filter, conceptual shared spell collection across source projections, source-specific Prepared state, compact V/S/M/concentration/ritual/prepared badges, sticky/collapsible levels, shared slots, Favorites, drag/haptics, phone IME-safe editor and tablet master-detail. Final workflow `33822722007` — PASS; artifact `9918883401`.

### Batch G3 — Notas + Trasfondo — GREEN

Titled-note helpers, Notes context retention, previews, drag/haptics, IME-safe editor, Raza and Religión/Fe regression preservation, image placeholders and collapsible long Story. Final workflow `33823386958` — PASS; artifact `9919122343`.

### Batch H1 — Artífice + Formas — GREEN

No schema change. Search, Manual/A–Z, drag/haptics, Favorites, duplicate, named delete, IME-safe phone editing, wide master-detail, conditional navigation, global Save/Discard and Quick Access pruning. Workflow `33825152159` — PASS; artifact `9919731254`.

### Batch H2 — Técnicas + Metamagia + Pactos — GREEN

Técnicas owns `TECHNIQUE`; Metamagia owns `METAMAGIC`; Pactos owns `INVOCATION` + `PACT_CHOICE`; generic Resources/Conjuros retain their own authority. No schema change. One reusable Android class-option implementation supports the three user-facing modules with search/filter/order/drag/Favorites/editing. Workflow `33826729095` — PASS; artifact `9920277155`.

### Batch H3 — Compañeros + module-union integration — GREEN

Controlling checkpoint:

`docs/checkpoints/2026-09-03_PHASE4_BATCH_H3_COMPANIONS.md`

Result:

- existing `CharacterCompanion` model confirmed sufficient; no schema change;
- companion state remains durable character-sheet state, not future DM live-combat authority;
- H3a pure operations/module-union gate passed at `bba16529c1a50317a377f6da9ee8c72d54926522`, workflow `33827147845`;
- Android `Compañeros` supports Manual/A–Z, search, Active/Favorite/source/kind filters, visible drag+haptics, duplicate, named delete, Favorites, source/provenance and the full approved companion reference editor;
- phone uses the reusable IME-safe dialog;
- wide/tablet uses list + persistent master-detail editor while preserving collection context;
- companions now join the existing structural conditional-module draft and global dirty/Save/Discard flow;
- successful Save prunes stale `CharacterQuickAccessKind.COMPANION` targets;
- conditional visibility remains class/subclass suggestion + manual PC Settings override + hide-not-delete;
- exact integrated tested commit `4590ec0e584b8b72fe7b4ce82eb01a00d44de2c8`;
- workflow `33829736046` — PASS across backend, shared/Kotlin tests, Android debug assemble, Desktop build and APK upload;
- integration artifact `9921290105`, digest `sha256:119ffc2376b77ef5ab4dcd1580b03f9deb1fab30a547bf30646bef83efa0199f`.

All six approved reusable conditional module families are implemented: Artífice, Formas, Técnicas, Metamagia, Pactos and Compañeros.

### Batch I1 — adaptive shell — GREEN

Controlling checkpoint:

`docs/checkpoints/2026-09-03_PHASE4_BATCH_I1_ADAPTIVE_SHELL.md`

Result:

- available-width character navigation uses the existing top tab strip on smaller/wide layouts and a Material 3 side rail only at `900dp+`;
- the existing `700dp+` child master-detail threshold remains independent;
- compact character/save identity remains outside scrolling tab content;
- responsive/master-detail work delivered in F–H remains authoritative;
- per-character last-open-tab state persists locally across full application reopen through `CharacterNavigationPreferenceStore`;
- `rememberSaveable` covers recreation/rotation;
- restored tabs are re-resolved against current spell/module visibility and stale hidden destinations safely fall back and are rewritten;
- no schema/domain change was required.

Verification:

- first exact-tree workflow `33832706244` caught one invalid Compose `weight` import on the safety branch;
- repair was exactly one import deletion;
- final tested commit `ebceb1c747ff5649d8b0038ddf38b94b9caafcc6`;
- final workflow `33832927017` — PASS across backend, shared/Kotlin tests, Android debug assemble, Desktop build and APK upload;
- integration artifact `9922360358`, digest `sha256:2dfd189833807ff154647a6f0b28dd25d4d7d886fd899dc53c063ec12cb7f953`.

### Batch I — adaptive shell + Supercompact + Table mode — GREEN

I1 adaptive shell, I2a Supercompact and I2b Table mode are all GREEN.

I2b controlling checkpoint:

`docs/checkpoints/2026-09-04_PHASE4_BATCH_I2B_TABLE_MODE.md`

Final evidence:

- exact clean-tree commit `a36a9b36f56b40088c9cb42b55b347a5ecf4c05b`;
- tree `75278c4a7569722f0d54a141fa257d710c62f35e`;
- workflow `33837303412` — PASS across backend, shared/Kotlin tests, Android debug assemble, Desktop build and APK upload;
- artifact `9923757180`, digest `sha256:d1b577750a14a023d5cca2c5cd7581a37321370ed9b1f68eb501cdbe171f065c`;
- no schema migration;
- Table mode is an explicit structural-write policy, not a blanket pointer blocker or second sheet model;
- structural edits are locked while intended live/session controls and presentation-only browsing remain usable;
- enabling Table mode over an already-dirty structural draft is prevented.

### Batch J — own-format backup/import + reconciliation — GREEN

Controlling checkpoint:

`docs/checkpoints/2026-09-04_PHASE4_BATCH_J_BACKUP_IMPORT_COMPLETE.md`

Result:

- app-owned versioned character backup format and strict decode/validation;
- richly populated character + closure-state round trip;
- controlled malformed/wrong-format/unsupported/invalid input handling, including non-primitive header safety;
- restore always creates a new local copy in the destination campaign;
- nested identifiers and internal references are remapped, preventing collisions;
- repeated imports create independent copies and preserve the source;
- repository restore is atomic and rollback-tested;
- imported copies receive reconciliation checkpoints;
- Android import uses the system document picker from campaign character context;
- Android export uses the system document picker from PC Settings and is disabled while structural drafts are pending;
- no SQLDelight schema migration was added for Batch J.

Final controlling evidence:

- exact product commit `7f93fc5268e9c0a9c26a2642fdbc2348b5f08501`;
- product tree `8cb3941f33f29ee6d2cf8c2d247944ebaaef8efd`;
- workflow `33879662226` — PASS across backend, shared/Kotlin tests, Android debug assemble, Desktop build and APK upload;
- artifact `9939406708`, name `dnd-custom-aid-debug-apk`;
- artifact ZIP digest `sha256:94332b1325fa8cc64a4d21520c119d6cc9f3b3fee40666a8abb2704137cccf41`;
- completed J history promoted into the durable closure branch through PR #6 / merge commit `2e37c1321b489db7677154f887b93f64cb586d49`.

The Batch J APK is technical evidence only; it is not the frozen Batch L owner-QA candidate.

## 3. Current active batch — K closure candidate stabilization

Batch K is stabilization only. No unrelated feature scope is permitted.

Required gate:

- full migration regression from the owner APK lineage;
- all shared tests;
- Android debug assemble;
- Desktop build;
- backend type-check;
- focused cross-tab integration repairs only when the stabilization gate demonstrates a concrete defect.

The migration audit must explicitly prove that the currently shipped/owner-tested database lineage can move through every subsequent SQLDelight migration to the current schema without loss of protected character data. Existing migration/repository tests are evidence but do not substitute for checking the exact lineage chain.

## 4. Remaining approved execution sequence

From the current position:

- **K — ACTIVE — closure candidate stabilization:** prove owner-lineage migration regression, run the complete automated gate, repair only demonstrated integration defects;
- **L — frozen phone+tablet APK candidate:** record exact commit/workflow/artifact name+ID/ZIP hash/extracted APK hash and make no silent code changes after QA begins;
- **M — owner QA:** phone portrait, phone landscape, tablet portrait, tablet landscape and representative larger text; failures create focused repair batches/new candidate identities.

## 5. Existing baseline that must not regress

Persistent General/Habilidades, Combate, Gestión, Equipo/currencies, Trasfondo including Raza and Religión/Fe, Rasgos, conditional Conjuros with sources/prepared/shared slots, Notas, all six conditional modules, PC Settings, I2a authoritative Supercompact/Quick Access/live controls, D-0046 derived values/adjustments, I1 adaptive navigation/last-tab restoration, Batch J backup/import/reconciliation and all migrations/repositories remain protected baseline behavior.

Historical/focused/tmp branches remain intentionally preserved. Do not delete them before the eventual post-merge unique-commit audit.

## 6. Final acceptance boundary

The future closure candidate must be one frozen APK with exact commit/workflow/artifact/hash identity. Green CI is technical evidence only and never substitutes for owner real-device QA.

## 7. Exact continuation

Start Batch K from the durable closure branch only after this state commit:

`implementation/phase4-character-closure`

Then:

1. create a fresh Batch K safety branch;
2. identify the exact database/schema version represented by the owner APK lineage and the current schema version;
3. inspect every intervening SQLDelight migration and the existing migration tests;
4. add or repair only the minimum migration regression needed to prove that lineage end-to-end;
5. run the focused migration gate first;
6. if GREEN, run all shared tests, Android debug assemble, Desktop build and backend type-check on one exact clean tree;
7. if that full K gate is GREEN with no integration defects, checkpoint K and proceed to candidate Batch L;
8. if the gate demonstrates a defect, repair only that defect and repeat the exact-clean K gate.

Keep `main` untouched. Do not freeze the Batch L owner-QA candidate until K is fully GREEN. Do not begin DM work before the Phase 4 exit boundary and explicit owner approval.
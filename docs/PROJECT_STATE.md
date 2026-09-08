# Project State

**Last verified:** 2026-09-07 owner local time / 2026-09-07 UTC
**Canonical branch:** `main` — untouched by Phase 4 closure work; exact head `471c5570669a6007bea9796d8a2c25536b10be21`  
**Phase 4 durable historical line:** `implementation/character-data-foundation`  
**Durable pre-QA branch:** `implementation/phase4-preqa-consolidation`  
**Active owner-requested repair branch:** `implementation/phase4-preqa-ux-repair`
**Current phase:** Phase 4 Character Foundation Closure — owner visual audition pending; formal M6 deferred
**Current execution position:** Owner explicitly reopened pre-QA implementation on 2026-09-07 after reviewing the historical M5/M6 candidate. The focused implementation repair line is technically stable through Pass 07 on `implementation/phase4-preqa-ux-repair`, review identity version `0.4.0-preqa.7` / build `40700`. Current-stage governance prose is reconciled through D-0047/Pass 07. Next action is owner phone/tablet visual audition; formal M6 remains deferred until the owner explicitly freezes an exact replacement candidate.
**DM work:** explicitly blocked until Phase 4 closure is fully implemented, phone+tablet QA accepted, and owner approves closure/merge

## 0. Primary resume order

1. `docs/checkpoints/LATEST.md` — stable owner-required resume pointer; always read this first;
2. the concrete checkpoint referenced by `LATEST.md` — active repair-pass state, exact version/build and next action;
3. `docs/checkpoints/2026-09-04_PHASE4_M6_QA_PAUSE_HANDOFF.md` — historical pause state only; its instruction to begin M6 was superseded when the owner explicitly reopened implementation on 2026-09-07;
4. `docs/checkpoints/2026-09-04_PHASE4_BATCH_M5_PREQA_CONSOLIDATION.md` — historical pre-repair closure evidence;
5. `docs/decisions/D-0047_PHASE4_CHARACTER_CLOSURE_EXPANSION.md` and `docs/checkpoints/2026-09-03_PHASE4_CLOSURE_EXECUTION_BATCH_PLAN.md` — controlling approved scope and historical execution decomposition.

## 1. Closure scope and merge boundary

D-0047 is owner-approved. The closure includes retained QA fixes and owner requirements, F01–F18, D01–D18, I01–I22, official class/subclass identity including Artificer, conditional reusable modules, Gestión, PC Settings consolidation, Supercompact/Table mode, global IME/order/context UX, backup/import and first-class phone/tablet behavior.

Do not merge Phase 4 to `main` until all remaining batches are implemented, automated gates are green, one exact closure QA APK is frozen and identified, owner phone+tablet QA is accepted, blocking findings are resolved, continuity/governance housekeeping is complete and the owner explicitly approves merge/closure.

No DM feature implementation begins before that exit gate.

## 2. Completed batch ledger

### Batch 0 — repository housekeeping — COMPLETE

Current-truth documentation is refreshed through the owner-reopened Pass 07 line. The consolidated `docs/DECISIONS.md` is reconciled through D-0047 without renumbering historical decisions. The remaining later governance boundary is the post-acceptance unique-commit/merge audit and final merge proposal.

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

### Batch K — closure candidate stabilization — GREEN

Controlling checkpoint:

`docs/checkpoints/2026-09-04_PHASE4_BATCH_K_STABILIZATION_COMPLETE.md`

Result:

- prior owner-QA APK lineage verified as SQLDelight schema 5;
- current closure schema verified as schema 9;
- new exact historical-schema migration regression exercises schema 5 -> 9 through migrations 5–8;
- representative Campaign, character, class/save/skill, slots, Combat, Equipment/currency, Trasfondo, Rasgo, Nota and spell/source/Prepared data survive and reopen through current repositories;
- closure-era additions receive safe defaults;
- no production migration repair was required;
- accepted regression commit `5030a0ed03df4ae92e6de312b1951b7f364c40d7`, tree `bcd22883c7a08d4c59394d799336f664137f1961`;
- focused migration workflow `33886853307` — PASS;
- controlling exact-clean workflow `33887059005` — PASS across backend, all shared tests, Android debug assemble, Desktop build and APK upload;
- technical artifact `9942413356`, ZIP digest `sha256:dde2ebdfc5f82ce2d2623c8bbd6fc52a00bdc609d90abeed539c5f8c830212ce`.

No integration defect was demonstrated, so Batch K made no production-code stabilization change.

### Batch L — frozen phone+tablet QA candidate — GREEN / FROZEN

Controlling checkpoint:

`docs/checkpoints/2026-09-04_PHASE4_BATCH_L_FROZEN_QA_CANDIDATE.md`

Frozen candidate identity:

- branch `tmp/phase4-l-frozen-qa-candidate`;
- exact commit `5cc034d3fdf4c25d935bd698aeaf2a3f9e427f27`;
- exact tree `b0e25a194ba0ed1926422230f3c29f70bfcd4e24`;
- controlling workflow `33887576972` — PASS across backend, all shared/migration tests, Android debug assemble, Desktop build and APK upload;
- artifact ID `9942595794`, name `dnd-custom-aid-debug-apk`;
- GitHub artifact ZIP digest `sha256:04fccd1c1078e302ddc621f9b546248f6588afcf46aa5f5050b4173919c2999b`;
- independently downloaded ZIP SHA-256 `04fccd1c1078e302ddc621f9b546248f6588afcf46aa5f5050b4173919c2999b` — exact match;
- ZIP contains exactly one `androidApp-debug.apk`, size `36146572` bytes;
- extracted APK SHA-256 `73282b433c519840e73ef9f8c8e63a311dcdf7bd9352c299469a0f7c290be079`.

The frozen candidate branch must not be changed. Any code repair after owner QA begins invalidates this identity and requires a new candidate.

## 3. Expanded Batch M — historical pre-repair audit line; owner later reopened implementation

Batch M was expanded from the historical single owner-QA step so implementation completeness and code health were proven before asking the owner to perform device QA.

The distinction remains controlling:

- M1/M2/M3 prove approved-scope coverage, structural health and historical batch implementation completeness;
- M4 repairs approved-scope holes;
- M5 re-audits the repaired tree, performs bounded justified cleanup, runs full automated gates and freezes one exact replacement candidate;
- M6 is owner real-device QA and is **not** interchangeable with the implementation audits.

### M1 — scope traceability audit — COMPLETE

D-0047 was traced against implementation and exposed four concrete missing/partial capabilities plus two design-fidelity gaps.

### M2 — code-health/static architecture audit — COMPLETE

No broad rewrite was justified. Bounded superseded/dead Android cleanup was identified and later completed in M5 after the scope-hole repairs.

### M3 — prior-batch implementation completeness audit — COMPLETE

No historical Batch 0/A1–L was found falsely GREEN relative to its own written batch contract. The issue was inter-batch allocation: six D-0047-approved requirements had not been fully closed by the historical decomposition.

### M4 — inter-batch scope-hole closure — COMPLETE

All six M3 holes are now implemented:

1. F14 structured languages/proficiencies/training reachable management UI;
2. F15 generic Resource Favorite/Quick Access;
3. I18 richer character-list class/subclass/freshness/optional portrait summary;
4. I21 real-sheet theme/font audition in Application Settings;
5. D06 semantic rules/source badge grammar;
6. D07 bounded semantic state-badge consistency pass.

No schema migration was required.

### M5 — post-repair consolidation + exact replacement candidate — GREEN / FROZEN

Controlling checkpoint:

`docs/checkpoints/2026-09-04_PHASE4_BATCH_M5_PREQA_CONSOLIDATION.md`

M5 completed:

- re-audit of all six M4 repairs;
- fail-closed removal of four superseded Android surfaces plus the obsolete private class/status chain in `CharacterEditorV4`;
- four bounded compiler-warning repairs, including replacement of deprecated `ScrollableTabRow` with `PrimaryScrollableTabRow`;
- full shared tests including owner-lineage migration regression;
- Android debug assemble;
- Desktop build;
- backend type-check;
- ordinary clean repository gate;
- independent exact-SHA candidate validation.

Historical M5 frozen candidate identity (superseded as the active target when the owner reopened implementation on 2026-09-07):

- branch `tmp/phase4-m5-frozen-qa-candidate`;
- commit `adc286b3e1305ed706c2ed04d478a43652f6b365`;
- tree `fd1f7feffde082b34cce41248e951a25eed7a004`;
- ordinary clean standard workflow `33911956696` — SUCCESS;
- independent exact-candidate validator workflow `33912322920` — SUCCESS;
- validator artifact ID `9951922423`, name `phase4-m5-frozen-qa-apk`;
- artifact ZIP digest `sha256:5fb8d7f281dbf937def89db4377e9b4157c46343f07721912aa759bb52d6f9fa`;
- exact APK size `35,720,588` bytes;
- exact APK SHA-256 `e31ce44a84cd79260ea2c51c65cb6a63675b1f916998e44d583358d72893c8ee`.

The frozen M5 candidate branch must not be changed. The historical L branch remains frozen historical evidence only.

### M6 — owner real-device QA — DEFERRED / NOT ACTIVE

The September 4 M5 freeze remains immutable historical evidence, but the owner explicitly reopened implementation before starting formal M6. Do not resume formal QA against that historical target unless the owner explicitly requests comparison.

### Owner-reopened pre-QA UX repair Pass 03–07 — GREEN / TECHNICALLY STABLE

The reopened repair line addressed owner-observed presentation/ergonomic concerns before formal QA. Across Pass 03–07 it completed the planned fixed/sticky controls, application text/font/spacing options, spacing-scale propagation, IME/window audit, conditional long-collection controls, card-column readability audit and final residual spacing sweep.

Current review identity:

- branch `implementation/phase4-preqa-ux-repair`;
- version `0.4.0-preqa.7` / build `40700`;
- tested product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- tested tree `3b2f2ab471097d3b108c9a787fc2342c5aad683a`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186` / `DND-Custom-Aid-0.4.0-preqa.7-build-40700-debug`;
- APK size `36,161,616` bytes;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`;
- checkpoint/branch head `4c6da4577b57e472819e096ecff55bd6750e026d`.

Pass 07 established a technical stopping rule: further speculative visual code changes are not recommended before owner phone/tablet observation. Build `40700` is therefore the current **visual-audition build**, not a frozen formal M6 candidate.

Use `docs/PREQA_OWNER_VISUAL_AUDITION.md`. Any concrete blocking finding receives a focused repair/new identified build. Formal M6 resumes only after the owner explicitly declares an exact build ready to freeze.

## 4. Remaining approved execution sequence

From the current position:

- historical M1–M5 — **COMPLETE/GREEN** at their recorded levels; historical frozen branches remain immutable evidence;
- pre-QA UX repair Pass 03–07 — **GREEN / TECHNICALLY STABLE**;
- current-state governance reconciliation through D-0047/Pass 07 — **COMPLETE**;
- **NEXT:** owner staged phone/tablet visual audition using build `40700` and `docs/PREQA_OWNER_VISUAL_AUDITION.md`;
- blocking audition finding requiring production change -> focused repair -> complete automated gate -> new identified review build -> repeat affected audition evidence;
- when owner explicitly says the replacement build is ready -> freeze one exact formal M6 candidate with commit/tree/workflow/artifact/hash identity;
- formal M6 begins on that frozen candidate with the in-place upgrade/data-preservation test before any clean install;
- after owner QA acceptance -> complete the unique-commit/merge-boundary audit and prepare the Phase 4 merge proposal;
- merge to `main` only after explicit owner closure/merge approval.

No DM feature implementation begins before that explicit Phase 4 exit decision.

## 5. Existing baseline that must not regress

Persistent General/Habilidades, Combate, Gestión, Equipo/currencies, Trasfondo including Raza and Religión/Fe, Rasgos, conditional Conjuros with sources/prepared/shared slots, Notas, all six conditional modules, PC Settings, Supercompact/Quick Access/live controls, Table mode, D-0046 derived values/adjustments, adaptive navigation/last-tab restoration, Batch J backup/import/reconciliation and the schema-5 -> current migration path remain protected baseline behavior.

Historical/focused/tmp branches remain intentionally preserved. Do not delete them before the eventual post-merge unique-commit audit.

## 6. Final acceptance boundary

Historical Batch L and M5 frozen branches remain evidence of their respective pre-repair trees. Neither is the active QA target after the owner reopened implementation. Build `40700` is the current owner-audition identity and is not yet formal M6.

Phase 4 remains open until:

1. staged owner phone/tablet visual audition is complete with no unresolved blocking visual/IME/layout findings;
2. one exact replacement formal M6 candidate is explicitly frozen after owner readiness;
3. M6 owner phone+tablet QA is completed and accepted on that exact replacement candidate;
4. blocking findings are resolved and affected evidence repeated when necessary;
5. the unique-commit/merge-boundary audit and final continuity housekeeping are complete;
6. the owner explicitly approves merge/closure.

Implementation-completeness audits are not substitutes for QA, and automated green is not owner acceptance.

## 7. Exact continuation

**Next action: owner staged phone/tablet visual audition on build `40700`.**

Resume in this order:

1. `docs/checkpoints/LATEST.md`;
2. `docs/checkpoints/2026-09-07_PHASE4_PREQA_UX_REPAIR_PASS_07.md`;
3. `docs/PREQA_OWNER_VISUAL_AUDITION.md`.

Current audition identity:

- branch `implementation/phase4-preqa-ux-repair`;
- version `0.4.0-preqa.7` / build `40700`;
- product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

Do not begin formal M6 yet. Do not clear app data for a hypothetical M6 migration test until an exact replacement candidate is actually frozen. Preserve the historical M5/L branches unchanged. Keep `main` untouched. Do not begin DM work before successful Phase 4 exit and explicit owner approval.

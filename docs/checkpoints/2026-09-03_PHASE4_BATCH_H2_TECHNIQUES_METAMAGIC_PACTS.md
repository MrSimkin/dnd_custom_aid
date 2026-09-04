# Phase 4 — Batch H2 Técnicas + Metamagia + Pactos checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** GREEN  
**Canonical `main`:** untouched

## Scope

Batch H2 implements the approved reusable conditional modules:

- `Técnicas`;
- `Metamagia`;
- `Pactos`.

The controlling ownership audit is:

- `docs/checkpoints/2026-09-03_PHASE4_BATCH_H2_TECHNIQUES_METAMAGIC_PACTS_AUDIT.md`;
- audit commit `8f1aa5700438845060be783a95bf0227cc7d4535`.

## Ownership

H2 reuses the existing durable global `CharacterSheet.classOptions` collection.

Exact ownership:

- Técnicas -> `TECHNIQUE`;
- Metamagia -> `METAMAGIC`;
- Pactos -> `INVOCATION` + additive `PACT_CHOICE`;
- Artífice keeps `ARTIFICER_PLAN` + `ARTIFICER_DEVICE`;
- `SUBCLASS_STATE` and `OTHER` remain generic/unassigned and are not silently captured by H2.

Actual spells/slots remain authoritative in Conjuros; generic resources remain authoritative in Gestión/Resources.

No SQL schema migration is required.

## H2a — shared operations/persistence — GREEN

Commits:

- `821acbd2816f3c8ee1a035a4479b7050a4a0e454` — extensible class-option labels while preserving H1 compatibility;
- `cfdd95831a273727ffcccdffa4ab57255d76dd83` — additive `PACT_CHOICE` enum value;
- `884dca2de49a0c5d16f6ee74df2b0f3917cb836f` — H2 presentation/filter/order ownership helpers;
- `2720f458ba1afa7eb88b764c260f66b391f9367d` — focused H2 ownership and persistence tests.

Delivered shared behavior:

- pure predicates for Technique, Metamagic and Pact ownership;
- Manual/A–Z projection helpers for all three modules;
- search across name/source/cost-reference/effect/notes/kind label;
- Active, Favorite and source filtering;
- Pacts-specific `Pacto / elección` vs `Invocación` filters;
- generic module-isolated manual reorder that replaces only positions owned by the active module family;
- hidden Artífice/other-H2/generic positions remain fixed during a module reorder;
- existing duplicate/global-next-order behavior reused;
- `PACT_CHOICE` round-trips through existing SQLDelight class-option persistence without schema change.

Focused tests prove:

- H2 projections exclude `SUBCLASS_STATE`, `OTHER` and other module families;
- A–Z/search/filter are presentation-only and do not mutate manual order;
- Technique reorder preserves Metamagic/Pacts positions;
- Pacts reorder preserves Technique/Metamagic/Artífice/generic positions;
- Pacts filters distinguish pact choices from invocations;
- duplicate keeps ownership and receives fresh identity/global appended order;
- `PACT_CHOICE` saves and reopens through `CharacterRepository` with all reference fields intact.

H2a verification:

- workflow `33826037339` — PASS;
- backend type-check — PASS;
- full shared/Kotlin tests — PASS;
- Android debug assembly — PASS;
- Desktop build — PASS;
- APK upload — PASS;
- artifact `dnd-custom-aid-debug-apk`, ID `9920055515`;
- digest `sha256:14728cf56308db200079d4f83a2c2578b9319e5bf4a26ef542844cd93e214cd2`.

## H2b — Android surfaces — GREEN

Primary implementation commits:

- `bd3c81c9567ce651d6f9d2673a883f8dceef5e1b` — reusable H2 class-option module UI plus Técnicas/Metamagia/Pactos wrappers;
- `62ce84292d205da04d3949f35f691574aa4a6e4e` — conditional H2 navigation destinations;
- `24906e6239d96b9ac88fce80689b63a54e8ecca6` — guarded CharacterEditor integration over the existing full `classOptions` draft.

### Reusable UI architecture

One `CharacterClassOptionModuleH2` implementation is parameterized into three distinct user-facing modules rather than copying three screens.

Shared behavior:

- search;
- Active, Favorite and dynamic source/provenance filters;
- independent Manual/A–Z presentation;
- A–Z never rewrites saved manual order;
- visible drag/drop feedback + configurable haptics only in a clean Manual view;
- row tap edits; no generic Edit button;
- duplicate;
- exact named destructive confirmation;
- Favorite through `CharacterQuickAccessKind.CLASS_OPTION`;
- newly-created/duplicated IDs cannot be Favorited before durable Save;
- optional linked character class for multiclass provenance;
- source, cost/reference, summary/reference, notes and active state;
- phone uses the reusable IME-safe modal editor;
- wide/tablet uses persistent master-detail editing.

Module specifics:

- Técnicas fixes new entries to `TECHNIQUE`;
- Metamagia fixes new entries to `METAMAGIC`;
- Pactos lets the owner explicitly choose `Pacto / elección` (`PACT_CHOICE`) or `Invocación` (`INVOCATION`);
- Sorcery Points are not duplicated in Metamagia;
- actual Warlock spells/Mystic Arcanum spell records are not duplicated in Pactos.

### Navigation and Save integration

`CharacterNavigationV4` maps:

- `CharacterModuleKind.TECHNIQUES` -> `Técnicas`;
- `CharacterModuleKind.METAMAGIC` -> `Metamagia`;
- `CharacterModuleKind.PACTS` -> `Pactos`.

The existing visible-module union/override engine remains authoritative, so multiclass produces at most one destination and PC Settings hide/show remains non-destructive.

All three modules receive and return the existing **complete** `h1ModuleDraft.classOptions` structural draft. The legacy technical name `h1ModuleDraft` remains for now, but the state itself already owns the complete shared conditional class-option collection. H2 does not introduce a second draft or repository authority.

Consequences inherited from H1:

- H2 changes participate in the global `Cambios sin guardar` state;
- global Save persists the complete `classOptions` collection;
- Save / Discard / Keep editing behavior covers H2;
- successful Save prunes stale `CLASS_OPTION` Quick Access targets;
- hiding an H2 module never deletes its class-option data.

## Integration orchestration note

The first one-time wiring attempt, workflow `33826551760`, failed before creating any job because the long inline integration script was embedded directly in the workflow YAML. It produced no CharacterEditor change.

The mechanism was corrected to the already-proven H1 pattern: separate guarded Python script + minimal workflow. Workflow `33826657639` then completed all integration steps successfully and created productive commit `24906e6239d96b9ac88fce80689b63a54e8ecca6`, removing its temporary script/workflow afterward.

This was an orchestration/scaffolding failure, not an H2 product/runtime failure.

## Full H2 gate — GREEN

Controlling checkpoint head:

- `58089d22d3373f236459b28c19e28b066b8710b4`.

Verification:

- workflow `33826729095` — PASS;
- backend type-check — PASS;
- full shared/Kotlin tests, including H2 ownership/persistence tests — PASS;
- Android debug assembly with all H2 destinations exhaustive — PASS;
- Desktop build — PASS;
- APK upload — PASS;
- artifact `dnd-custom-aid-debug-apk`, ID `9920277155`;
- digest `sha256:788406604e85dae2b5ae6f7c782c037e5e1622de527d1ca2f9c2994964ce8e0d`.

This APK is integration evidence only; it is not the frozen owner-QA closure candidate.

## H2 closure conclusion

Batch H2 is GREEN. Técnicas, Metamagia and Pactos now use explicit durable ownership over the shared class-option collection, reuse one responsive editor/list architecture, integrate with existing module suggestions/overrides, and preserve the established global Save/Discard/Favorite semantics without duplicating Spells or Resources.

## Exact next action

Proceed to **Batch H3 — Compañeros + module-union integration**.

Start by auditing the existing durable `CharacterCompanion` model and repository behavior against the approved companion module requirements. Confirm which fields are sufficient, what companion operations/search/filter/order are needed, and how companion Favorites and hide-not-delete should integrate. Do not add schema unless the audit identifies a real durable-data gap.
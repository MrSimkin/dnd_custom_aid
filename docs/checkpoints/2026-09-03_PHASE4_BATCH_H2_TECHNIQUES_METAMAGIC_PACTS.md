# Phase 4 — Batch H2 Técnicas + Metamagia + Pactos checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** H2a GREEN — H2b ACTIVE  
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
- Pactos -> `INVOCATION` + new additive `PACT_CHOICE`;
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
- `PACT_CHOICE` round-trips through the existing SQLDelight class-option persistence without schema change.

Focused tests prove:

- H2 projections exclude `SUBCLASS_STATE`, `OTHER` and other module families;
- A–Z/search/filter are presentation-only and do not mutate manual order;
- Technique reorder preserves Metamagic/Pacts positions;
- Pacts reorder preserves Technique/Metamagic/Artífice/generic positions;
- Pacts filters distinguish pact choices from invocations;
- duplicate keeps ownership and receives fresh identity/global appended order;
- `PACT_CHOICE` saves and reopens through `CharacterRepository` with all reference fields intact.

Verification:

- workflow `33826037339` — PASS;
- backend type-check — PASS;
- full shared/Kotlin tests — PASS;
- Android debug assembly — PASS;
- Desktop build — PASS;
- APK upload — PASS;
- artifact `dnd-custom-aid-debug-apk`, ID `9920055515`;
- digest `sha256:14728cf56308db200079d4f83a2c2578b9319e5bf4a26ef542844cd93e214cd2`.

This APK is integration evidence only, not the frozen owner-QA candidate.

## H2b — Android surfaces — ACTIVE

Implementation must reuse the existing structural draft that already contains the full `classOptions` collection. It must not introduce a second H2 draft/authority.

Target architecture:

- one reusable class-option module list/editor implementation parameterized for Técnicas, Metamagia and Pactos;
- three distinct user-facing conditional destinations;
- each surface projects only its owned kind(s) while receiving/returning the complete `classOptions` list;
- row tap edits; no generic Edit button;
- phone IME-safe modal editor;
- wide/tablet master-detail;
- search, filters, Manual/A–Z;
- visible drag/haptics only in clean Manual view;
- Favorite through existing `CharacterQuickAccessKind.CLASS_OPTION`;
- new/duplicated IDs cannot be Favorited until durably saved;
- optional linked class, source, cost/reference, effect summary, notes and active state;
- Pactos permits choosing between `Pacto / elección` and `Invocación`;
- Técnicas and Metamagia keep fixed semantic kind in their editors;
- hide/show remains non-destructive through existing PC Settings module overrides;
- multiclass union creates at most one destination per module;
- H2 joins the existing global unsaved/Save/Discard flow;
- existing successful-Save stale `CLASS_OPTION` Quick Access pruning already covers H2.

## Exact next action

Implement H2b Android reusable module surface + Técnicas/Metamagia/Pactos wrappers and conditional navigation. Then run a controlling full gate before marking H2 GREEN or moving to H3.
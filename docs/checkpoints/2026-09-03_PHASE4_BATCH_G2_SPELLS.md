# Phase 4 — Batch G2 Conjuros closure checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** PENDING INTEGRATION GATE  
**Canonical `main`:** untouched

## Scope

G2 closes the approved Conjuros usability scope without changing the established durable conceptual-spell/source model and without adding schema.

Historical rules remain authoritative:

- `Todos` and each spellcasting source are filtered views of one conceptual spell collection;
- one conceptual spell may belong to several sources;
- `Preparado` belongs to the source association, not universally to the conceptual spell;
- shared spell slots remain authoritative on the character sheet and are not duplicated in the spell draft;
- custom spellcasting sources remain allowed and no spell-list legality is enforced.

## G2a — pure spell presentation operations

Implementation:

- `4279b30e7abf09ddf7d1341cbc2fa08870c93ce7` — spell presentation/filter/order/duplicate/reorder helpers;
- `ef9134e14d20226d0e3ed23ca618466a6127ea11` — focused G2 operation tests.

Controlling G2a workflow:

- `33818362184` — PASS across backend, shared/Kotlin tests, Android debug, Desktop and APK upload;
- artifact `dnd-custom-aid-debug-apk`, ID `9917381960`, digest `sha256:c1b343bf80c44a901b7fa517699979ba3b8cf1b76c9f3dd934f3af65d2150e26`.

Semantics established:

- Manual and A–Z are presentation modes within spell level;
- A–Z never rewrites stored `sortOrder`;
- source projection preserves the one conceptual collection;
- source-specific prepared state remains independent;
- search/filter operate inside the selected `Todos` or source projection;
- filters support Favorite, Prepared, Concentration, Ritual and V/S/M;
- moving inside a source view swaps only positions visible to that source within the same spell level, preserving hidden spell positions;
- duplicate receives a fresh UUID and preserves conceptual/source-specific spell state.

Existing persistence and shared-slot regression tests remain part of the full gate.

## G2b — Android Conjuros closure surface

New isolated surface:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterSpellListClosureV4.kt`.

Primary UI commit:

- `b7c4cede8d2dd5b6f207a1237cc3e72eb2532929`.

Editor/list wiring commit:

- `c12db403e112017afae0418b61c5352d49d81a75` — `feat: integrate Batch G2 spells closure UI`.

Delivered UI behavior:

- existing `Todos` + per-source navigation retained;
- reusable search/filter toolbar;
- independent Manual/A–Z presentation with Manual order preserved;
- Favorite / Quick Access ★/☆ using existing `CharacterQuickAccessKind.SPELL` state;
- Favorite disabled for newly-created or duplicated unsaved spells, preventing orphan references;
- Favorite references for deleted spells are pruned only after successful global Save;
- compact V/S/M, Concentration, Ritual and prepared-state badges;
- in `Todos`, prepared state remains explicitly source-aware instead of implying one universal checkbox;
- in a source view, `Preparado` remains directly editable for that source association;
- collapsible sticky level headers;
- shared slot pips remain the same authoritative slot projection used by Quick Magic;
- duplicate action;
- shared visible drag lift/drop feedback + semantic haptics;
- drag disabled in A–Z or when search/filter projection makes persisted reorder ambiguous;
- source-view Manual drag uses the G2a visible-position semantics;
- exact named delete confirmation;
- phone retains IME-safe modal spell editing;
- tablet/wide uses persistent master-detail editing with selected-row highlight, independently scrollable editor and preserved list/query context;
- deleting a source while a spell editor is open cannot leave a stale source ID satisfying spell-editor validation.

The previous `CharacterSpellListV4` implementation remains present as a non-active reference/rollback surface until G2 is accepted.

## Gate required

G2 is not GREEN until the controlling workflow on this checkpoint head passes:

- focused G2 spell operation tests;
- historical spell persistence / multi-source prepared-state regressions;
- historical shared-slot integration regression;
- all shared desktop tests;
- Android debug assembly including sticky headers and tablet master-detail Compose surface;
- Desktop build;
- backend type-check;
- APK artifact upload.

If green, close G2 and advance to **G3 — Notas + Trasfondo closure**. Do not begin G3 from implementation momentum before this gate is known.

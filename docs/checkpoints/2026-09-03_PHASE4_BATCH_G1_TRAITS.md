# Phase 4 — Batch G1 Rasgos closure checkpoint

**Date:** 2026-09-03  
**Branch:** `implementation/phase4-character-closure`  
**Status:** PENDING INTEGRATION GATE  
**Canonical `main`:** untouched

## Scope

G1 closes the approved Rasgos usability scope without changing the established durable `CharacterTrait` model or adding schema.

## G1a — pure trait operations

Implementation:

- `15cb679b5da54f0b5fc075eb389a9f610c92b16b` — trait presentation/group/filter/reorder/duplicate/use-meter helpers;
- `64302449e1e43c2f78f3371cc7156ed755715aa0` — focused operation tests.

Controlling G1a workflow:

- `33817330178` — PASS across backend, shared/Kotlin tests, Android debug, Desktop and APK upload.

Semantics established:

- search covers name, source, type, description, notes, recovery and activation;
- type/source/Favorite filters are OR within one category and AND across categories;
- grouping supports none/type/source as presentation only;
- grouping never mutates stored `sortOrder`;
- grouped manual move swaps only positions occupied by that group, preserving other groups' positions;
- duplicate receives a fresh UUID and preserves trait state with `(copia)` naming;
- limited-use meter clamps malformed spent counts safely.

## G1b — Android Rasgos closure surface

New isolated surface:

- `androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterTraitsClosureV4.kt`.

Primary UI commit:

- `e3665c7610656087bc5a72c449f8ddec07ebcf45`.

Editor wiring commit:

- `9a0206091e4253c7da37bebbe2844060f539b48f` — `feat: integrate Batch G1 traits closure UI`.

Delivered UI behavior:

- reusable search/filter toolbar;
- type and source filters plus Favorite filter;
- `Sin agrupar / Tipo / Fuente` grouping;
- two-column responsive grouping on wide layouts;
- manual drag using shared visible lift/drop feedback and semantic haptics;
- drag hidden while search/filters make reorder ambiguous;
- grouped drag moves only within the active group;
- remaining/max uses visual progress meter plus exact `Recuperar` / `Gastar` controls;
- Quick Access Favorite ★/☆ using existing `CharacterQuickAccessKind.TRAIT` state;
- Favorite disabled for newly-created unsaved traits, preventing orphan references;
- duplicate action;
- exact named delete confirmation;
- existing IME-safe editor semantics retained;
- persisted favorite references for deleted traits are pruned only after successful global Save, so discarding a draft deletion does not mutate durable Quick Access state.

## Gate required

G1 is not GREEN until the controlling workflow on this checkpoint head passes:

- focused G1 trait operation tests;
- all shared desktop tests;
- Android debug assembly including the new Rasgos Compose surface;
- Desktop build;
- backend type-check;
- APK artifact upload.

If green, close G1 and advance to **G2 — Conjuros closure**. Do not begin G2 from implementation momentum before this gate is known.

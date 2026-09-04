# Phase 4 Batch M4f — D07 state-badge grammar closure

**Date:** 2026-09-04  
**Status:** IMPLEMENTED — clean standard gate pending at checkpoint creation  
**Safety branch:** `tmp/phase4-m4f-state-badges`  
**Authoritative M4e base:** `76301abbbd5fb1da0d6e2c1adb87f35826e2656d`  
**Accepted product commit:** `d7f6107193bde51d4d4d5a7638e79a3c09fa9514`  
**Accepted product tree:** `e53299fc418a890082f9cff3cdf671b66b896183`  
**Canonical `main`:** untouched  
**Historical L candidate:** untouched

## Purpose

Close the final M3 inter-batch hole, D07, with a bounded consistent state-badge grammar on the approved state-rich current surfaces. This is not a global re-theme or redesign.

M4f reuses `CharacterSemanticBadgeV4` and `CharacterSemanticBadgeKindV4.STATE` introduced in M4e; no second badge system, schema, persistence model, or backend behavior is introduced.

## Spells

The active `CharacterSpellListClosureV4` keeps all existing spell information while routing badge semantics through the shared primitive:

- `V`, `S`, `M` remain explicit neutral badges;
- concentration uses textual `Concentración` as a STATE badge;
- ritual uses `Ritual` as a STATE badge;
- selected-source prepared status uses `Preparado` as a STATE badge;
- all-source view keeps prepared counts but makes the label explicit: `Preparado n/total` as a STATE badge.

The existing prepared checkbox/control remains a control and is not duplicated into persistence.

Favorite remains visibly identifiable with `★` / `☆`, while the spell Favorite button now supplies an explicit accessibility description (`Añadir ... a Favoritos` / `Quitar ... de Favoritos`). Existing `★ Favoritos` filter labeling is unchanged. Favorite meaning is therefore never color-only.

## Equipment

The active dense Equipment surface no longer hides inventory state inside one undifferentiated metadata sentence.

State is now projected as a compact horizontally scrollable badge row using the shared STATE grammar:

- `Transportado` or `Guardado`;
- `Equipado` when true;
- `Sintonizado` when true.

Operational/location metadata remains ordinary text beneath it:

- consumable/ammunition quick-use amount;
- item location where present.

No inventory state, item identity, carry rule, or persistence behavior changed.

## Active concentration

The real concentration surface in Gestión now displays `Concentración activa` through the same shared STATE primitive before the concentrated effect/spell name and notes.

Start/change/end concentration behavior is unchanged; the change is semantic presentation only.

## Guarded integration gate

Temporary exact-match workflow:

- workflow `33908625625` — **SUCCESS**.

Before accepting the product commit it verified:

- spell concentration/prepared state badges are reachable;
- spell Favorite has explicit accessibility wording;
- Equipment state labels are routed through the shared STATE primitive;
- active concentration is routed through the same shared STATE primitive;
- D07 reaches at least all three controlling real surfaces;
- all shared/Kotlin desktop tests PASS;
- Android debug assemble PASS;
- Desktop build PASS;
- backend dependency install/type-check PASS;
- accepted helper-free product commit was created;
- temporary patch/workflow helpers self-deleted in `d7f6107193bde51d4d4d5a7638e79a3c09fa9514`.

## Scope boundary

M4f closes implementation completeness for D07 only. Exact visual density, horizontal-scroll comfort, badge readability and screen-reader usability remain M6 real-device QA.

At checkpoint creation, one ordinary clean `scaffold-check.yml` run on the helper-free product tree plus this checkpoint remains required before declaring M4f GREEN and promoting it to `implementation/phase4-preqa-consolidation`.

When that clean gate is GREEN, **all six M4 holes are implementation-closed** and the next phase is M5 (re-audit + bounded cleanup + full regression/migration/build + new frozen QA candidate), not owner QA yet.

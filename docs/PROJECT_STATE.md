# Project State

**Last verified:** 2026-09-09  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Current implementation boundary:** successor Increment G integrated automated-green on continuation branch  
**Current phase:** Phase 4A — Character Foundation Closure successor repair/refinement cycle  
**Release status:** debug / development; owner/device acceptance remains open; not release-ready  
**DM work:** blocked until Phase 4A is repaired, accepted and explicitly closed

## 1. Canonical repository reality

`main` remains the canonical baseline and was **not** advanced by Increment G. The active continuation branch contains the post-main successor work through Increment G. Canonical does not mean released, QA-accepted or owner-accepted.

Use:

- `docs/checkpoints/LATEST.md` for the exact current position;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_G_COLLECTION_CONTENT_REPAIRS.md` for the latest completed implementation boundary;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_F_CONJUROS_COMPACT_SOURCE_CONTEXT.md` for repaired-F evidence and remaining targeted phone retest;
- `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` for the controlling A–I sequence;
- `docs/BRANCH_STATUS.md` for branch history/interpretation.

Frozen QA candidate branches remain immutable historical evidence.

## 2. Successor implementation progress

- A — schema/domain/storage foundation: **COMPLETE / GREEN**;
- B — shared UX/responsive primitives: **COMPLETE / GREEN**;
- C — character-first navigation + PC Settings + General/Habilidades: **COMPLETE / GREEN**;
- D — Combat + Dice: **COMPLETE / GREEN**;
- E — Gestión + Markers + Resources + recovery/conditions: **COMPLETE / GREEN**;
- F — compact Conjuros source-context redesign + repair: **AUTOMATED GREEN / TARGETED OWNER REPAIR RETEST PENDING**;
- G — Equipo/Rasgos/conditional modules/Notas/Trasfondo: **COMPLETE / INTEGRATED AUTOMATED GREEN / OWNER ACCEPTANCE PENDING**;
- H — full-screen Application Settings/live previews/themes: **NEXT ENGINEERING INCREMENT**;
- I — separate tablet portrait/landscape redesign: pending.

Two engineering increments remain in the planned successor sequence: **H and I**. Owner/device acceptance work also remains open.

## 3. Latest technically verified boundary

Increment G integrated validation:

- latest G product commit `3b6118e490edc8d57a0097c60d196d4de112722a`;
- validation head `aa7e57647e4a1b467de028f9d0a0d13e4a1ef0bd`;
- workflow `34413644371` — **SUCCESS**;
- artifact ID `10128271891` / `dnd-custom-aid-debug-apk`;
- artifact ZIP digest `sha256:1a3aab50c9c5122f94ddaa2b7de173cddd046b74b80f8be58b0ed4ec564bca2f`.

Verified in one normal gate:

- backend dependency install/type-check;
- shared/Kotlin desktop tests;
- Android debug assembly;
- desktop application build;
- Android debug APK upload.

This artifact is a development validation artifact. It has **not** received owner visual/device acceptance and is not a formal M6 candidate.

## 4. Implemented successor foundations A–G

### A–E foundations

The continuation line retains:

- generalized built-in/custom ability references;
- custom attributes and optional custom saving throws;
- custom-skill ability mapping;
- per-source spellcasting ability/DC/attack configuration;
- structured combat damage with legacy TEXT fallback;
- Custom Markers separate from Resources;
- binary/counter/current-max Resources with structured recovery and cross-tab placements;
- durable tab order;
- app-owned background-image domain/storage model;
- own-format backup/import carrying successor state;
- explicit phone/tablet layout context, spacing scale, compact toolbar, whole-card drag, IME-safe editors, numeric normalization and global contextual help;
- character-first navigation, PC Settings, General/Habilidades, Combat/Dice and Gestión recovery/live-state work.

### F — Conjuros

Repaired automated implementation provides compact source-context behavior, per-source casting projections, compact search/sort/filter/add behavior, shared measured whole-card spell reorder and IME-safe editor/numeric behavior. Its repaired targeted owner phone retest is still pending; automated green is not owner acceptance.

### G — collection/content domains

Equipo:

- canonical CA/equipped projection;
- Equipo-placed Resources;
- clearer consumable semantics;
- `Electrum`;
- `Gemas / arte`.

Rasgos/conditional modules:

- Rasgos-placed Resources;
- structured provenance;
- measured 2-D drag in multi-column Rasgos;
- compact shared collection grammar across Artífice, Formas, Técnicas, Metamagia, Pactos and Compañeros;
- existing hide-not-delete module visibility retained.

Notas:

- title/content search;
- presentation-only content filters;
- Manual/A–Z without mutating stored order;
- measured multi-column whole-card reorder;
- intentionally minimal title/content data model retained.

Trasfondo:

- functional primary/secondary image pickers;
- sampled resize/transcode into existing app-owned encoded payload;
- persistent render/add/change/remove;
- save/reopen regression;
- backup/import payload independence regression.

## 5. Protected owner directions

These remain controlling:

- one datum / one canonical state;
- compact compatible controls into clear rows rather than unnecessary vertical stacks;
- reduce unnecessary margins/padding without degrading required touch targets;
- whole-card drag where safe; physical feel remains auditionable;
- phone landscape is a phone interaction model, not tablet UI;
- tablet/wide UX needs independent redesign;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- Spanish class/subclass presentation;
- contextual explanation remains useful content through `Siempre visible` / `ⓘ / tooltip` / `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- structured provenance uses `Tipo de origen | Origen específico` when that semantic distinction is real;
- Conjuros source association remains a real behavioral concept and may be custom/non-class.

## 6. Owner/device acceptance state

No automated gate through Increment G is owner visual/device acceptance.

The owner directed work to continue into G without recording a passing repaired-F physical retest. Therefore repaired F's targeted Redmi Note 11 Pro 5G check remains outstanding rather than being silently inferred as accepted.

The later consolidated successor audition should also cover representative G behavior, especially persistent Trasfondo image reopen, Notes search, Resource projection across tabs and representative multi-column drag.

No physical owner tablet acceptance has been completed. Increment I remains a redesign task rather than validation of the old wide layout.

## 7. Conditional/deferred boundaries

- SRD-backed `Buscar existente` selectors are only appropriate where an approved corpus actually exists; this cycle does not silently become full SRD ingestion.
- Exact proprietary Sandy Petersen Cthulhu Mythos condition text remains deferred unless project-appropriate/owner-provided text is available.
- Do not invent an automatic AC rules engine until inventory has sufficient structured armor/shield semantics.
- Do not begin broad DM implementation.

## 8. Exact next engineering position

Resume with **Increment H — full-screen Application Settings redesign** according to the reconciled successor plan.

H must preserve one app-wide authority for text size, spacing, column preferences, help mode, dice-result presentation, haptic preferences, font choice and theme. It must provide practical live previews rather than duplicate per-screen settings.

After H, Increment I redesigns tablet portrait/landscape using the same canonical domain state and shared controls without merely stretching the phone/wide UI.

## 9. Phase 4A closure remains open

Phase 4A still requires H/I integration, outstanding targeted phone acceptance, redesigned tablet acceptance, blocking repair resolution, a new formal M6 freeze when appropriate, formal regression/upgrade QA and explicit owner closure.

**No DM feature implementation begins before that explicit closure.**

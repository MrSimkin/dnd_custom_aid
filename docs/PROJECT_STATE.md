# Project State

**Last verified:** 2026-09-09  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Current implementation boundary:** successor Increment H integrated automated-green on continuation branch  
**Current phase:** Phase 4A — Character Foundation Closure successor repair/refinement cycle  
**Release status:** debug / development; owner/device acceptance remains open; not release-ready  
**DM work:** blocked until Phase 4A is repaired, accepted and explicitly closed

## 1. Canonical repository reality

`main` remains the canonical baseline and has not been advanced by successor Increments F–H. The continuation branch contains the current post-main implementation through Increment H. Canonical does not mean released, QA-accepted or owner-accepted.

Use:

- `docs/checkpoints/LATEST.md` for the exact next action;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_H_APPLICATION_SETTINGS.md` for the latest completed implementation boundary;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_G_COLLECTION_CONTENT_REPAIRS.md` for the previous collection/content boundary;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_F_CONJUROS_COMPACT_SOURCE_CONTEXT.md` for repaired-F evidence and remaining targeted phone retest;
- `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` for the controlling A–I sequence.

Frozen QA candidate branches remain historical evidence.

## 2. Successor implementation progress

- A — schema/domain/storage foundation: **COMPLETE / GREEN**;
- B — shared UX/responsive primitives: **COMPLETE / GREEN**;
- C — character-first navigation + PC Settings + General/Habilidades: **COMPLETE / GREEN**;
- D — Combat + Dice: **COMPLETE / GREEN**;
- E — Gestión + Markers + Resources + recovery/conditions: **COMPLETE / GREEN**;
- F — compact Conjuros source-context redesign + repair: **AUTOMATED GREEN / TARGETED OWNER REPAIR RETEST PENDING**;
- G — Equipo/Rasgos/conditional modules/Notas/Trasfondo: **COMPLETE / INTEGRATED AUTOMATED GREEN / OWNER ACCEPTANCE PENDING**;
- H — full-screen Application Settings/live previews/themes: **COMPLETE / INTEGRATED AUTOMATED GREEN / OWNER ACCEPTANCE PENDING**;
- I — separate tablet portrait/landscape redesign: **NEXT**.

One engineering increment remains in the planned A–I successor implementation sequence: **I**. Owner/device acceptance and formal Phase 4A closure work remain after implementation.

## 3. Latest technically verified boundary

Increment H integrated validation:

- latest H product commit `a64ed207d906ae9aea695da34d01ded0e2ccf32a`;
- validation head `5d287cc42331c46c8df39224348fa7380b4c1aeb`;
- workflow `34416419033` — **SUCCESS**;
- artifact ID `10129307655` / `dnd-custom-aid-debug-apk`;
- artifact ZIP digest `sha256:f103f614141b8cf8ca57280bd75243c79a5f7dc63dbed84b9b2b48a7578c9f83`.

Verified in one normal gate:

- backend dependency install/type-check;
- shared/Kotlin desktop tests;
- Android debug assembly;
- desktop application build;
- Android debug APK upload.

This artifact is a development validation artifact. It is not owner/device acceptance and is not a formal M6 candidate.

## 4. Implemented successor foundations A–H

### A–E foundations

The continuation line retains generalized abilities/custom attributes, per-source spellcasting, structured combat damage, Custom Markers and Resources with structured recovery/placements, durable tab order, app-owned background-image storage/backup, explicit phone/tablet layout context, compact collection/drag/editor/help primitives, character-first navigation, PC Settings, General/Habilidades, Combat/Dice and Gestión recovery/live-state work.

### F — Conjuros

Compact source context, canonical per-source casting projections, compact search/sort/filter/add behavior, shared measured whole-card reorder and IME/numeric repair are automated-green. Its targeted owner phone repair retest remains open.

### G — collection/content domains

- Equipo projects canonical CA/equipped state, Equipment Resources, clearer consumables, `Electrum` and `Gemas / arte`;
- Rasgos projects Trait Resources, structured provenance and measured multi-column drag;
- Artífice/Formas/Técnicas/Metamagia/Pactos/Compañeros use the compact shared collection grammar while retaining hide-not-delete module state;
- Notas supports title/content search, presentation-only content filtering, Manual/A–Z and measured multi-column drag without expanding its intentionally minimal data model;
- Trasfondo primary/secondary images are selectable, sampled/resized, persisted as app-owned payloads, rendered, removable and carried safely through backup/import.

### H — Application Settings

- global settings is a true full-screen surface while the previous app surface remains composed underneath;
- text size and spacing are discrete stepped sliders with live previews; text retains 70–200%, spacing includes 40%;
- one global/device haptic strength-duration authority lives in Application Settings while per-character activation remains in PC Settings;
- font UI presents useful immediate samples without normal-screen provider/audition clutter;
- column preferences have live mini-grid previews;
- requested theme renames are applied;
- six additional theme families exist for audition: Carmesí, Ámbar, Glaciar, Lavanda, Pizarra and Terracota;
- theme cards use representative multi-element previews.

## 5. Protected owner directions

- one datum / one canonical state;
- compact compatible controls into clear rows instead of unnecessary vertical stacks;
- reduce unnecessary margins/padding without degrading required touch targets;
- whole-card drag where safe; physical feel remains auditionable;
- phone landscape is a phone interaction model, not tablet UI;
- tablet portrait and tablet landscape require independent first-class redesign;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- Spanish class/subclass presentation;
- contextual explanations remain through `Siempre visible` / `ⓘ / tooltip` / `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- Conjuros source association remains a real behavioral concept and may be custom/non-class.

## 6. Owner/device acceptance state

No automated gate through Increment H is owner visual/device acceptance.

Repaired F's targeted Redmi Note 11 Pro 5G check remains outstanding because no passing physical evidence has been recorded. A later consolidated phone audition should also cover representative G/H behavior: persistent background image reopen, Notes search/reorder, cross-tab Resources, full-screen settings, slider extremes/new themes and real-device haptic differences.

No physical owner tablet acceptance has been completed. Increment I is therefore a redesign task, not validation of the old wide layout.

## 7. Conditional/deferred boundaries

- SRD-backed `Buscar existente` selectors require an approved corpus; this cycle does not silently become full SRD ingestion.
- Exact proprietary Sandy Petersen Cthulhu Mythos condition text remains deferred without an appropriate content source.
- Do not invent an automatic AC rules engine before inventory has sufficient armor/shield semantics.
- Do not begin broad DM implementation.

## 8. Exact next engineering position

Resume with **Increment I — separate tablet portrait/landscape redesign** according to the reconciled successor plan.

I must reuse the canonical A–H state and shared controls while designing tablet portrait and landscape as first-class interaction surfaces. It must not simply stretch the phone layout or use width alone to classify a landscape phone as tablet.

After I, produce the tablet-targeted owner audition boundary and move into acceptance/closure planning rather than DM feature implementation.

## 9. Phase 4A closure remains open

Phase 4A still requires Increment I integration, outstanding phone acceptance, redesigned tablet acceptance, blocking repair resolution if found, a formal M6 freeze when appropriate, regression/upgrade QA and explicit owner closure.

**No DM feature implementation begins before that explicit closure.**

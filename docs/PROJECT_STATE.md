# Project State

**Last verified:** 2026-09-09  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Current implementation boundary:** planned successor Increments A–I integrated automated-green on continuation branch  
**Current phase:** Phase 4A — successor acceptance / closure preparation  
**Release status:** debug / development; owner/device acceptance remains open; not release-ready  
**DM work:** blocked until Phase 4A is accepted and explicitly closed

## 1. Canonical repository reality

`main` remains the canonical baseline and has not been advanced by the later successor implementation sequence. The active continuation branch contains the current A–I successor implementation. Canonical does not mean released, QA-accepted or owner-accepted.

Use:

- `docs/checkpoints/LATEST.md` for the exact current position and next action;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_I_TABLET_REDESIGN.md` for the final planned engineering increment and A–I integrated gate;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_H_APPLICATION_SETTINGS.md` for Application Settings;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_G_COLLECTION_CONTENT_REPAIRS.md` for collection/content repairs;
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
- I — tablet portrait/landscape redesign: **COMPLETE / INTEGRATED AUTOMATED GREEN / PHYSICAL TABLET ACCEPTANCE PENDING**.

The planned A–I engineering sequence is complete. There is no planned Increment J. Remaining work is owner/device acceptance, blocking repairs if observed, formal candidate/regression work and explicit Phase 4A closure.

## 3. Latest technically verified boundary

Final Increment I / A–I integrated validation:

- latest I product commit `c11ed70260f054a99c7ab5e38f2f772a697db886`;
- validation head `7fd61ee281404e4f62f41e2b1fb126ca45a2a199`;
- workflow `34420873078` — **SUCCESS**;
- artifact ID `10130893215` / `dnd-custom-aid-debug-apk`;
- artifact ZIP size `13,308,575` bytes;
- artifact ZIP digest `sha256:9d65bb1e78b5a875ef5d381d56fa5a8038ce1c0ca9b6f87130d80b21d6660194`.

Verified in one normal gate:

- backend dependency install/type-check;
- shared/Kotlin desktop tests;
- Android debug assembly;
- desktop application build;
- Android debug APK upload.

This artifact is the integrated successor audition build. It has not received owner/device acceptance and is not a formal M6 candidate.

## 4. Implemented successor foundations A–I

### A–E foundations

The continuation line retains generalized built-in/custom ability references, custom attributes and optional custom saves, custom-skill mapping, per-source spellcasting, structured combat damage, Custom Markers, canonical Resources with structured recovery/placements, durable tab order, app-owned background-image state/backup, explicit four-form-factor layout context, compact collection/drag/editor/help primitives, character-first navigation, PC Settings, General/Habilidades, Combat/Dice and Gestión recovery/live-state work.

### F — Conjuros

Compact source context, canonical per-source casting projections, compact search/sort/filter/add behavior, measured whole-card reorder and IME/numeric repair are automated-green. The targeted owner Redmi repair retest remains open.

### G — collection/content domains

- Equipo projects canonical CA/equipped state, Equipment Resources, clearer consumables, `Electrum` and `Gemas / arte`;
- Rasgos projects Trait Resources, structured provenance and measured multi-column drag;
- Artífice/Formas/Técnicas/Metamagia/Pactos/Compañeros use the compact shared collection grammar while retaining module visibility semantics;
- Notas supports title/content search, presentation-only filtering, Manual/A–Z and multi-column reorder without expanding its intentionally minimal data model;
- Trasfondo primary/secondary images are selectable, resized/transcoded, app-owned, persistent, removable and backup/import safe.

### H — Application Settings

- global settings is a full-screen surface while the previous app screen remains composed underneath;
- text size and spacing use stepped sliders with live previews; text retains 70–200% and spacing includes 40%;
- device haptic strength/duration has one app-wide authority while activation remains per character;
- font and column choices have immediate previews;
- requested theme renames are applied;
- Carmesí, Ámbar, Glaciar, Lavanda, Pizarra and Terracota are additional audition themes.

### I — tablet portrait/landscape redesign

- the existing 600dp short-side detector still distinguishes phone/tablet and portrait/landscape;
- tablet portrait uses top tabs and a centered portrait canvas rather than the old generic side rail;
- tablet landscape uses an adaptive-width side rail and wider centered canvas;
- the same saveable tab subtree is retained across tablet compositions;
- large text reduces effective tablet card columns without rewriting saved column preferences;
- idle editor panes were removed from Conjuros, Equipo, Artífice, Formas, Técnicas/Metamagia/Pactos and Compañeros;
- tablet landscape shows side detail only while an editor is actually open;
- tablet portrait uses the modal/IME-safe editor path for those collections;
- existing useful wide behavior such as Notas/Rasgos grids and Trasfondo simultaneous content remains.

## 5. Protected owner directions

These remain controlling:

- one datum / one canonical state;
- compact compatible controls into clear rows instead of unnecessary vertical stacks;
- reduce unnecessary margins/padding without degrading required touch targets;
- whole-card drag where safe; physical feel remains auditionable;
- phone landscape is a phone interaction model, not tablet UI;
- tablet portrait and tablet landscape are first-class compositions rather than stretched phone layouts;
- extra tablet width must increase useful context, not create permanent empty panes;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- Spanish class/subclass presentation;
- contextual explanations remain through `Siempre visible` / `ⓘ / tooltip` / `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- Conjuros source association remains a real behavioral concept and may be custom/non-class.

## 6. Owner/device acceptance state

No automated gate through Increment I is owner visual/device acceptance.

Repaired F's targeted Redmi Note 11 Pro 5G check remains outstanding because no passing physical evidence has been recorded. The consolidated successor phone audition should cover representative F–H behavior and any phone-visible regression risk introduced by shared primitives.

Tablet portrait/landscape now has a dedicated implementation and repository-level layout inspection, but no physical owner tablet acceptance has been completed. The repository does not currently include a dedicated tablet screenshot/emulator regression harness; do not report screenshot/device acceptance from CI.

## 7. Conditional/deferred boundaries

- SRD-backed `Buscar existente` selectors require an approved corpus; this cycle does not silently become full SRD ingestion.
- Exact proprietary Sandy Petersen Cthulhu Mythos condition text remains deferred without an appropriate content source.
- Do not invent an automatic AC rules engine before inventory has sufficient armor/shield semantics.
- Do not begin broad DM implementation.

## 8. Exact next position

Do **not** start another planned engineering increment. Move into the successor audition / acceptance / closure sequence:

1. install/expose the integrated A–I APK for owner testing;
2. complete the outstanding targeted and consolidated phone checks;
3. test redesigned tablet portrait/landscape on a representative physical tablet when available;
4. implement only acceptance-blocking repairs actually found;
5. freeze a new formal M6 candidate once the owner-audited baseline is acceptable;
6. complete required regression/upgrade QA;
7. explicitly close Phase 4A.

## 9. Phase 4A closure remains open

Phase 4A is **not** closed merely because A–I engineering is green. It still requires owner/device acceptance, repair resolution if needed, formal candidate/regression work where appropriate and explicit owner closure.

**No DM feature implementation begins before that explicit closure.**

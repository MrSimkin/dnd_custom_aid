# Project State

**Last verified:** 2026-09-09  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Repository consolidation:** D-0066 baseline + 2026-09-09 successor night-close through Increment E  
**Current phase:** Phase 4A — Character Foundation Closure successor repair/refinement cycle  
**Release status:** debug / development; known owner-acceptance work remains; not release-ready  
**DM work:** blocked until Phase 4A is repaired, accepted and explicitly closed

## 1. Canonical repository reality

`main` is the canonical current development baseline. Canonical does **not** mean released, QA-accepted or owner-accepted.

The 2026-09-09 night-close consolidation advances `main` by normal fast-forward to the same completed-E baseline as `implementation/phase4a-successor-cycle`. The active branch remains the focused continuation line for Increment F tomorrow; it is not a competing source of truth.

Use:

- `docs/checkpoints/LATEST.md` for the exact next action;
- `docs/checkpoints/2026-09-09_NIGHT_CLOSE_AFTER_INCREMENT_E.md` for session continuity;
- `docs/BRANCH_STATUS.md` for branch interpretation/cleanup;
- `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` for the controlling A–I implementation sequence.

Frozen QA candidate branches remain immutable historical evidence.

## 2. Current implementation progress

The successor cycle has nine planned increments.

- A — schema/domain/storage foundation: **COMPLETE / GREEN**;
- B — shared UX/responsive primitives: **COMPLETE / GREEN**;
- C — character-first navigation + PC Settings + General/Habilidades: **COMPLETE / GREEN**;
- D — Combat + Dice: **COMPLETE / GREEN**;
- E — Gestión + Markers + Resources + recovery/conditions: **COMPLETE / GREEN**;
- F — compact Conjuros source-context redesign: **NEXT**;
- G — Equipo/Rasgos/conditional modules/Notas/Trasfondo: pending;
- H — full-screen Application Settings/live previews/themes: pending;
- I — separate tablet portrait/landscape redesign: pending.

Four increments remain: **F, G, H and I**.

## 3. Latest technically verified successor boundary

Increment E final integrated validation:

- active successor Gestión wiring source commit `4b3ab53faada5af7b50f73ce951fe767c13ff63a`;
- validation commit `0587db5e65d89e809f138e83d053903659216886`;
- workflow `34307068166` — **SUCCESS**;
- artifact ID `10087074946` / `dnd-custom-aid-debug-apk`;
- artifact ZIP digest `sha256:55bba08d09918a3f6102e4e2694da50c0ee5bb6e9bf3b1ac4c8849f9203ac50`.

Verified in that gate:

- backend/type-check;
- shared/Kotlin tests;
- Android debug compilation/assembly;
- desktop compilation/build;
- Android debug APK upload.

This artifact is a development validation artifact. It has **not** received owner visual/device acceptance and is **not** a formal M6 candidate.

## 4. Latest owner-auditioned practical build

The last owner-auditioned build remains:

- version `0.4.0-preqa.7`;
- build `40700`;
- type `debug`;
- product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

Primary owner phone: **Redmi Note 11 Pro 5G**.

Stages A–F of that prior build produced the canonical repair backlog; they were not a visual acceptance pass. Do not ask the owner to rediscover already-globalized findings screen by screen.

## 5. Implemented successor foundations A–E

### A — data/storage

- generalized built-in/custom ability references;
- custom attributes and optional custom saving throws;
- custom-skill ability mapping;
- per-source spellcasting ability/DC/attack configuration;
- structured combat damage with safe legacy TEXT fallback;
- Custom Markers distinct from Resources;
- reusable binary/counter/current-max + structured recovery mechanics;
- Resource placements across General/Gestión/Equipo/Rasgos;
- durable tab order;
- persistent app-owned background-image model;
- own-format backup/import carrying successor state;
- additive SQLDelight migrations and migration regression tests.

### B — shared UX

- explicit phone portrait / phone landscape / tablet portrait / tablet landscape context;
- spacing scale including 40%;
- compact collection toolbar primitive;
- whole-card long-press drag foundation with haptic/visual feedback;
- IME-safe editor family;
- numeric normalization;
- global contextual help modes `Siempre visible` / `ⓘ / tooltip` / `Oculto`;
- compact provenance `Tipo de origen | Origen específico`.

Card movement feel is technically improved but remains pending owner real-device acceptance.

### C — navigation/settings/general/skills

- character-first root list with canonical Raza, classes/levels and campaign;
- PC Settings information architecture;
- tab order;
- custom attributes/skills/Markers configuration;
- bounded haptic strength/duration choices;
- compact General projections;
- per-source spellcasting reference rows;
- standard/custom skills integrated in Habilidades;
- `Conocimiento Arcano` terminology;
- compact passive-skill reference row.

### D — Combat + Dice

- ordered structured damage components;
- compact attack-card hierarchy;
- shared attack/damage draft transaction;
- compact `modo → categoría → objetivo → tirar` flow;
- custom attributes/saves/skills as roll targets;
- attacks and source-specific spell attacks;
- structured damage rolls from the same attack components;
- roll decomposition;
- device-wide result presentation setting.

### E — Gestión/live state/recovery

- compact fixed operational state;
- one-row death saves;
- canonical Inspiration and explicit unsaved-General-vs-persisted-state signaling;
- compact Custom Marker and Resource controls;
- placement-aware Gestión Resource projection;
- mixed typed Resource/Marker rest preview/apply;
- explicit structured recovery only for automatic proposals;
- manual/free text remains review-only;
- condition catalog infrastructure with stable source/help identity and no unapproved corpus text;
- concentration check/DC explanation routed through contextual-help mode.

## 6. Protected owner directions

These remain controlling across all later increments:

- one datum / one canonical state;
- do not spread information across several rows when one clear row suffices at the current width/text scale;
- reduce unnecessary margins/padding app-wide without shrinking required touch targets;
- whole-card drag where safe; stronger movement feel/feedback;
- phone landscape is a phone interaction model, not tablet UI;
- tablet/wide UX needs independent redesign;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- Spanish class/subclass presentation; do not foreground edition metadata without functional purpose;
- contextual explanations remain useful content and use the three-mode help system rather than being deleted for compactness;
- generic `Fuente` schema leakage should not be reintroduced;
- when provenance matters, use `Tipo de origen | Origen específico`, default `Clase`;
- Conjuros functional source association remains a real behavioral concept and may be custom/non-class.

## 7. Conditional/deferred content boundaries

### SRD-backed existing-content selectors

`Buscar existente` vs custom-create remains approved **only when an approved official corpus is actually loaded**. This cycle does not silently expand into full SRD ingestion.

### Sandy Petersen Cthulhu Mythos conditions

Architecture supports the owner's source family, but exact proprietary Spanish descriptions remain deferred until project-appropriate/owner-provided text is available.

### Armor/AC

General can project canonical AC and equipped-item references. Do not invent an automatic AC rules engine until inventory has sufficient structured armor/shield semantics.

## 8. Tablet and owner acceptance

No physical owner tablet acceptance has been completed. Tablet/wide UI is already known to need redesign, so Increment I is a redesign task rather than validation of the old wide layout.

No successor implementation artifact through Increment E should be described as owner visual acceptance.

The early targeted phone retest remains scheduled after Increment F, focusing especially on Conjuros portrait/landscape, representative editor/keyboard behavior, card drag feel, Gestión operational/death-save footprint, Habilidades passive row and 40% spacing.

## 9. Exact next execution position

Resume with **Increment F — Conjuros compact source-context redesign**.

Do not restart A–E and do not rerun build 40700 screen-by-screen.

Protected F direction:

- one compact sticky source-context bar;
- selected source clearly owns casting ability / `CD salv. conjuro` / `Mod. ataque mágico`;
- `Todos los conjuros` does not permanently show all source statistics;
- source detail/filter/search expansion is transient/collapsible;
- sticky level/slot context remains useful but must not erase the spell list;
- phone landscape must show practical spell content without switching to the old tablet interaction model.

After F, produce the planned early targeted Redmi portrait + landscape interaction build/audition.

## 10. Phase 4A closure remains open

Repository/main consolidation is development housekeeping, not the Phase 4A exit gate.

Phase 4A still requires later successor integration through F–I, targeted owner phone acceptance, redesigned tablet acceptance, blocking repair resolution, a new formal M6 freeze when appropriate, formal regression/upgrade QA and explicit owner closure.

**No DM feature implementation begins before that explicit closure.**

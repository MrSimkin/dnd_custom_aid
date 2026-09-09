# Latest project checkpoint

**Updated:** 2026-09-09  
**Canonical branch:** `main`  
**Repository consolidation:** COMPLETE under D-0066  
**Current implementation branch:** `implementation/phase4a-successor-cycle`  
**Current reconciliation state:** SUCCESSOR IMPLEMENTATION ACTIVE  
**Increment A:** COMPLETE / GREEN  
**Increment B:** COMPLETE / GREEN; drag feel pending owner-device acceptance  
**Increment C:** COMPLETE / GREEN  
**Increment D:** COMPLETE / GREEN  
**Current increment:** E — Gestión + Markers + Resources + recovery/conditions  
**Increment E state:** shared recovery engine GREEN; successor Gestión surface GREEN in standalone build but NOT YET ACTIVE in `CharacterEditorV4`  
**Current product status:** successor implementation in progress; owner visual acceptance not yet run  
**Latest owner-auditioned practical identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary owner test phone:** Redmi Note 11 Pro 5G  
**Phone audition:** prior build Stages A–F sufficiently covered; visual acceptance NOT passed  
**Tablet acceptance:** not complete; tablet/wide UX remains a redesign target  
**DM implementation:** blocked pending later Phase 4A closure acceptance

## Read next

1. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_E_PROGRESS_PAUSE.md` — exact current E resume state; compiled successor Gestión is not yet wired active;
2. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_D_COMBAT_DICE.md` — completed structured Combat + character-aware Dice family and final green gate;
3. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_C5_HABILIDADES.md` — completed inline standard/custom skills and generalized ability mapping;
4. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_C3_C4_GENERAL.md` — compact General identity/projections and per-source casting reference;
5. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_C2_PC_SETTINGS_INFORMATION_ARCHITECTURE.md` — Settings IA, tab order, custom attributes/skills/markers, Inspiration visibility and bounded haptics;
6. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_B_SHARED_UX_PRIMITIVES.md` — responsive/density/toolbar/drag/IME/help foundation;
7. `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` — controlling successor implementation order and acceptance boundaries;
8. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_A_DATA_FOUNDATION.md` — schema/domain/storage foundation;
9. `docs/checkpoints/2026-09-08_D0067_RECONCILIATION_PENDING_DECISIONS.md` — resolved owner decisions;
10. `docs/checkpoints/2026-09-08_PHASE4_PREQA_FUENTE_REDUNDANCY_AUDIT.md` — provenance/source IA follow-up.

## Branch discipline

`main` remains untouched by successor implementation at:

`698d40b7da75bb7535d83f834db7044ef3e626a8`

Successor work remains isolated on:

`implementation/phase4a-successor-cycle`

Old implementation/tmp branches are historical evidence. Frozen QA branches remain immutable.

## Current Increment E evidence

### Shared Resource + Marker recovery foundation

Key commits:

- `5aeb4977c5164e6e921a8e7a37629425793fdcd8` — one mixed typed recovery engine;
- `87e4a213da42f19ba45ca001270e7a1006241fe2` — focused mixed-recovery regression tests.

Authoritative shared-layer workflow:

`34305427239` — SUCCESS

Protected behavior includes typed Resource/Marker identity, collision safety, selected-only application, structured-rule-only automatic recovery, review-only legacy/free text, and binary/counter/current-max normalization.

### Staged successor Gestión surface

Implementation commit:

`3c5be8d499d176c37496df59ed5ae1219e4a572b`

File:

`androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterManagementSuccessorV4.kt`

Standalone compile/build workflow:

`34306095086` — SUCCESS

Verified together:

- backend/type-check: PASS;
- shared/Kotlin tests/build: PASS;
- Android debug compilation/assembly: PASS;
- desktop compilation/build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10086746569`;
- name `dnd-custom-aid-debug-apk`;
- ZIP digest `sha256:8210319be7a508d2eef8192dc68c5ff89041530a502bc3cc1ded13a96b831559`.

The staged surface includes compact operational state/death saves, live Inspiration/Markers, placement-aware Resources, mixed rests, explicit unsaved-General-vs-persisted-state signaling, and retained condition/concentration/effect/reconciliation paths.

### Failed guarded activation attempt

Pre-pause wiring workflow commit:

`a0458ee0842c0bcbb158a308e9f3f85d2e3a818a`

Workflow:

`34306315649` — FAILURE at `Apply exact guarded wiring patch`.

The exact textual guard did not match current `CharacterEditorV4.kt`. The failure happened before replacement/validation/commit, therefore:

- no partial active-editor wiring was committed;
- active Gestión must still be treated as the legacy `CharacterManagementTabV4` path;
- `CharacterManagementSuccessorTabV4` is compiled but not active;
- the temporary one-shot wiring workflow remains branch-local and should be removed during the corrected retry.

Durable E pause checkpoint:

`docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_E_PROGRESS_PAUSE.md`

Checkpoint creation commit:

`dc831fe32ed8a407ff7af8fec5c01a61f83df8a9`

## Completed successor foundations A–D

### A — data/storage

- generalized built-in/custom ability references;
- custom attributes with standard modifier math and optional saves;
- generalized custom-skill ability mapping;
- per-spellcasting-source ability/save-DC/attack configuration;
- structured combat damage with safe legacy TEXT fallback;
- reusable binary/counter/current-max and structured recovery concepts;
- Custom Markers distinct from Resources;
- controlled Resource placement across General/Gestión/Equipo/Rasgos;
- durable tab-order keys;
- own-format backup/import carrying successor state;
- SQLDelight migrations through migration 11 and regression coverage.

### B — shared UX

- explicit phone portrait / phone landscape / tablet portrait / tablet landscape context;
- centralized spacing including owner-requested 40% audition option;
- compact sticky long-collection toolbar;
- whole-card long-press drag with haptics and visible drag state;
- shared IME-safe editor family;
- numeric normalization;
- contextual help modes `Siempre visible` / `ⓘ` / `Oculto`;
- compact provenance `Tipo de origen | Origen específico`.

Drag feel remains pending real-device acceptance.

### C — first entry, Settings, General and Habilidades

- character-first root and correct Back hierarchy;
- PC Settings owns character configuration while Application Settings remains separate;
- real per-character tab ordering;
- custom attributes, custom skills and Custom Markers;
- compact General identity/projections;
- canonical quick operational values in General;
- per-source `Lanzamiento de Conjuros` references;
- custom and built-in skills integrated in Habilidades;
- compact passive Percepción/Perspicacia/Investigación row.

### D — Combat + Dice

- structured ordered DICE / FLAT / TEXT damage components;
- compact Combat card hierarchy;
- whole-card reorder interaction;
- attack + damage remain one draft/save transaction without hidden parent auto-save;
- compact Dice flow `modo → categoría → objetivo → tirar`;
- built-in/custom abilities, saves and skills plus attacks and source-specific spell attacks;
- attack damage rolls consume the same structured components edited by Combat;
- result decomposition always available;
- application result preference `Resultado compacto` / `Dados visibles`.

Final D workflow:

`34304392913` — SUCCESS

D completion checkpoint close commit:

`2eb65208faa6fb25b9a7f12387be55ea4e84ffb9`

## Resolved model directions remain controlling

- Custom attributes are full ability-like stats with standard modifier math and optional saves.
- Custom Markers remain logically distinct from Resources, although both share reusable tracker/recovery mechanics.
- Resources gain controlled multi-tab presentation placement from one canonical value.
- Spellcasting ability/DC/attack belong per spellcasting source.
- Conjuros uses one compact sticky source-context bar rather than stacking several permanent blocks.
- Defensas surfaces current AC + equipped armor/shield references, not armor proficiencies.
- `Gemas / arte` is free-form this cycle.
- Contextual help uses one canonical explanation rendered according to the global help mode.
- Provenance uses `Tipo de origen | Origen específico`, default `Clase`, only where it has user-facing value.
- Use `Raza`, never `Especie/raza`.
- Use `Electrum`, never `Electro`.
- Phone landscape remains a phone interaction model; current tablet/wide UI is not an acceptable fallback and needs separate redesign/audit.
- Global owner findings for compactness, row efficiency, margins/padding and card interaction remain app-wide and should not be rediscovered screen by screen.

## Successor implementation order

1. A — schema/domain/storage foundation: COMPLETE / GREEN;
2. B — shared UX/responsive primitives: COMPLETE / GREEN;
3. C — navigation + PC Settings + General/Habilidades: COMPLETE / GREEN;
4. D — Combat + Dados: COMPLETE / GREEN;
5. **E — Gestión + Markers + Resources + cross-domain rests/conditions: CURRENT / IN PROGRESS**;
6. F — Conjuros compact source-context redesign, followed by early phone portrait/landscape owner retest;
7. G — Equipo/Rasgos/conditional modules/Notas/Trasfondo;
8. H — full-screen Application Settings and live previews/themes;
9. I — separate tablet portrait/landscape redesign after phone primitives stabilize.

There are nine increments total. At this checkpoint, five increments remain including the current E; after E closes, four later increments remain: F, G, H and I.

## Existing QA findings remain active

Do not rerun build `40700` screen-by-screen. Existing global findings already cover app-wide density/row fragmentation, card/reorder interaction, shared IME problems, phone-landscape failure, tablet redesign, rotation context loss, fixed-area footprint, `Fuente` cleanup, Consumible/Munición UX, terminology, help behavior, 40% spacing and typography follow-up.

For Gestión specifically, the prior owner audition already established:

- fixed `Estado operativo` consumes too much height;
- death saves should become one compact horizontal row;
- persisted Gestión state can disagree visually with an unsaved General draft;
- that coherence problem must be explained/resolved without silently saving unrelated General draft edits.

## Testing/build boundaries

- focused automated tests accompany shared domain/operation work;
- full gate after coherent product boundaries;
- early owner phone retest remains after the Conjuros/interaction foundation rather than immediately after D;
- consolidated successor audition follows collection/settings/responsive integration;
- physical tablet acceptance remains required before Phase 4A closure;
- formal replacement M6 remains deferred until repaired phone/tablet baseline is acceptable.

## Exact next action — resume Increment E

Do **not** restart the E foundation and do **not** assume successor Gestión is active.

1. inspect the actual current `CharacterEditorV4.kt` Gestión call at branch head;
2. wire `CharacterTabV4.MANAGEMENT` to `CharacterManagementSuccessorTabV4` using the actual current anchor, persisted sheet, successor state and General draft projection required for coherence signaling;
3. preserve save boundaries: never silently save unrelated General draft edits;
4. remove the temporary failed one-shot wiring workflow;
5. run the integrated full gate;
6. finish/verify E5 predefined-safe condition catalog/source-help infrastructure plus concentration check/DC explanation;
7. run the final E regression gate and write the Increment E completion checkpoint;
8. only then proceed to F.

Keep `main` untouched and do not begin DM features.

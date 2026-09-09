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
**Current product status:** successor implementation in progress; owner visual acceptance not yet run  
**Latest owner-auditioned practical identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary owner test phone:** Redmi Note 11 Pro 5G  
**Phone audition:** prior build Stages A–F sufficiently covered; visual acceptance NOT passed  
**Tablet acceptance:** not complete; tablet/wide UX remains a redesign target  
**DM implementation:** blocked pending later Phase 4A closure acceptance

## Read next

1. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_D_COMBAT_DICE.md` — completed structured Combat + character-aware Dice family and final green gate;
2. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_C5_HABILIDADES.md` — completed inline standard/custom skills and generalized ability mapping;
3. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_C3_C4_GENERAL.md` — compact General identity/projections and per-source casting reference;
4. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_C2_PC_SETTINGS_INFORMATION_ARCHITECTURE.md` — Settings IA, tab order, custom attributes/skills/markers, Inspiration visibility and bounded haptics;
5. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_B_SHARED_UX_PRIMITIVES.md` — responsive/density/toolbar/drag/IME/help foundation;
6. `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` — controlling successor implementation order and acceptance boundaries;
7. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_A_DATA_FOUNDATION.md` — schema/domain/storage foundation;
8. `docs/checkpoints/2026-09-08_D0067_RECONCILIATION_PENDING_DECISIONS.md` — resolved owner decisions;
9. `docs/checkpoints/2026-09-08_PHASE4_PREQA_FUENTE_REDUNDANCY_AUDIT.md` — provenance/source IA follow-up;
10. `docs/PROJECT_STATE.md` — broader state snapshot.

## Branch discipline

`main` remains untouched by successor implementation at:

`698d40b7da75bb7535d83f834db7044ef3e626a8`

Successor work remains isolated on:

`implementation/phase4a-successor-cycle`

Old implementation/tmp branches are historical evidence. Frozen QA branches remain immutable.

## Latest automated successor product boundary — Increment D

Increment D is technically complete and green.

Final authoritative workflow:

`34304392913` — SUCCESS

Validated descendant:

`7ef476d0248609d0e1caf291d48afc5c5cc5f77d`

Verified together:

- backend/type-check: PASS;
- shared/Kotlin tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop compilation/build: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10086158879`;
- name `dnd-custom-aid-debug-apk`;
- ZIP digest `sha256:70891bc5de59f5391c60bc9823facaaacc81c91319013b88e5950e62f0fccc84`.

The first D integrated gate exposed only a private Android numeric-sanitizer visibility problem. It was corrected with a shared Android input helper at source head `96c285edbed3b957edff4ba9b52345e786f285a6`, after which the authoritative retry passed.

D completion checkpoint close commit:

`2eb65208faa6fb25b9a7f12387be55ea4e84ffb9`

This is an automated implementation boundary, **not owner-device visual acceptance**.

## Latest owner-auditioned practical build

The last owner-auditioned practical identity remains:

- version `0.4.0-preqa.7`;
- build `40700`;
- `debug`;
- prior verified product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- prior workflow `34171466714` — SUCCESS;
- prior artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

Do not interpret successor implementation artifacts as owner visual acceptance.

## Completed successor foundations A–D

### A — data/storage

The successor data layer includes:

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
5. **E — Gestión + Markers + Resources + cross-domain rests/conditions: CURRENT**;
6. F — Conjuros compact source-context redesign, followed by early phone portrait/landscape owner retest;
7. G — Equipo/Rasgos/conditional modules/Notas/Trasfondo;
8. H — full-screen Application Settings and live previews/themes;
9. I — separate tablet portrait/landscape redesign after phone primitives stabilize.

Do not convert this into dozens of isolated screen patches.

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

## Exact next action — Increment E

Proceed on `implementation/phase4a-successor-cycle` with the shared **trackable/recovery operation layer** before Android Gestión layout work.

### E foundation

1. preserve Resources and Custom Markers as separate domain identities;
2. provide one typed mixed rest-preview mechanic for both;
3. preserve the existing Resource rest API as compatibility wrappers rather than duplicating logic;
4. use explicit structured recovery rules for automatic proposals only;
5. keep legacy/free-text or manual semantics review-only and never infer numeric recovery from text;
6. apply only explicitly selected numeric proposals;
7. respect binary/counter/current-max value semantics and known maxima;
8. add focused tests including Resource/Marker identity collision safety;
9. run the automated gate before wiring the successor Gestión UI.

### E UI after shared gate

- compact `Estado operativo`;
- one-row death saves;
- canonical live Marker/Resource controls reusing General's compact interaction grammar;
- Resource placement-aware Gestión projection;
- rest preview/apply over both domains;
- custom/predefined-safe condition infrastructure without invented proprietary description text;
- explicit General-draft vs persisted Gestión coherence signaling, with no hidden global save.

Keep `main` untouched and do not begin DM features.

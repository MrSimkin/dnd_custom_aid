# Latest project checkpoint

**Updated:** 2026-09-08  
**Canonical branch:** `main`  
**Repository consolidation:** COMPLETE under D-0066  
**Current implementation branch:** `implementation/phase4a-successor-cycle`  
**Current reconciliation state:** SUCCESSOR IMPLEMENTATION ACTIVE  
**Increment A:** COMPLETE / automated foundation gate GREEN  
**Increment B:** COMPLETE / shared UX primitive gate GREEN; drag feel pending owner device acceptance  
**Increment C1:** COMPLETE / character-first entry automated gate GREEN  
**Increment C2:** COMPLETE / PC Settings information architecture automated gate GREEN  
**Current increment:** C3/C4 — General compact identity + canonical projections  
**Current product status:** successor implementation in progress; owner visual acceptance not yet run  
**Latest owner-auditioned practical identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary owner test phone:** Redmi Note 11 Pro 5G  
**Phone audition:** prior build Stages A–F sufficiently covered; visual acceptance NOT passed  
**Tablet acceptance:** not complete; tablet/wide UX is a redesign target  
**DM implementation:** blocked pending later Phase 4A closure acceptance

## Read next

1. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_C2_PC_SETTINGS_INFORMATION_ARCHITECTURE.md` — **latest completed implementation checkpoint; Settings IA, real tab order, custom attributes/skills/markers, Inspiration visibility and bounded haptics**;
2. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_C1_CHARACTER_FIRST_ENTRY.md` — completed character-first root, canonical character summaries, create/import campaign routing and Back hierarchy;
3. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_B_SHARED_UX_PRIMITIVES.md` — completed shared responsive/density/toolbar/drag/IME/help foundation and owner drag-feel qualifier;
4. `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` — controlling successor implementation order and build/retest boundaries;
5. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_A_DATA_FOUNDATION.md` — completed schema/domain/storage foundation and verified migration/compatibility evidence;
6. `docs/checkpoints/2026-09-08_D0067_RECONCILIATION_PENDING_DECISIONS.md` — despite the historical filename, records the resolved owner decisions and compact Conjuros source-context design;
7. `docs/decisions/D-0067_OWNER_NEXT_CYCLE_CHARACTER_UX_AND_FEATURE_REFINEMENTS.md` — full owner non-QA package;
8. `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_F.md` — detailed prior-build phone fixed-footprint evidence;
9. `docs/checkpoints/2026-09-08_PHASE4_PREQA_FUENTE_REDUNDANCY_AUDIT.md` — provenance/source IA follow-up;
10. `docs/PROJECT_STATE.md` — broader state snapshot.

## Canonical baseline and branch discipline

D-0066 consolidated the in-progress Phase 4 development reality into `main`. Canonical does **not** mean accepted/release-ready.

`main` remains untouched by successor implementation at:

`698d40b7da75bb7535d83f834db7044ef3e626a8`

Successor product work is isolated on:

`implementation/phase4a-successor-cycle`

Old implementation/tmp branches are historical evidence. Frozen QA branches remain immutable.

## Latest automated successor product head

C2's full automated gate passed for product commit:

`99696ea4ce5c6e212fd0beaa351ec180727c0deb`

Workflow:

`34296859639` — SUCCESS

Verified together:

- backend check: PASS;
- shared/Kotlin tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop compilation/build: PASS;
- Android debug APK artifact upload: PASS.

CI artifact:

- artifact ID `10083511887`;
- artifact name `dnd-custom-aid-debug-apk`;
- ZIP digest `sha256:47ae1a07e1995fd83efef8bd7875fd002d0492019c78c79c92f78ee5aaea5ce1`.

This is an **automated successor implementation checkpoint**, not an owner-auditioned replacement build.

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

## Increment A completed foundation

The successor data layer includes:

- generalized ability references covering built-in and custom attributes;
- full custom attributes with standard modifier math and optional saving throws;
- successor custom-skill ability mapping;
- generalized Dice target/calculation architecture for custom abilities/saves/skills;
- per-spellcasting-source ability/save-DC/attack configuration with safe legacy projection;
- structured combat damage while preserving legacy free text without speculative parsing;
- reusable binary/counter/current-max + structured recovery semantics;
- Custom Markers remaining conceptually separate from Resources;
- controlled Resource presentation placement across General/Gestión/Equipo/Rasgos;
- `Gemas / arte` free-text valuables persistence;
- durable per-character tab-order keys;
- two app-owned Trasfondo image slots;
- own-format backup v2 carrying successor state while still accepting v1 backups;
- safe import remapping of successor relational identities;
- SQLDelight migrations 9, 10 and 11;
- regression coverage proving successor extensions survive legacy child-table delete/reinsert saves.

A critical child-FK cascade issue was found during the Increment A audit and corrected with migration 11 before the foundation was closed.

## Increment B completed shared UX foundation

Shared successor interaction/layout primitives now include:

- explicit phone portrait / phone landscape / tablet portrait / tablet landscape context rather than width-only phone-to-tablet switching;
- centralized spacing/density behavior including the owner-requested 40% audition option;
- shared compact long-collection toolbar with real Equipo/Conjuros consumers;
- shared whole-card long-press drag foundation with pickup/step/drop haptics and visible drag state;
- representative whole-card drag integration in Notas without a bulky dedicated handle;
- a correction for stale drag callbacks/index state during live reorder (`975e10a87b7e0040ed01f81c3d866810d428ad26`);
- softened visual drag/drop behavior;
- shared IME-safe editor family with actions kept reachable above the keyboard;
- shared numeric normalization + focused tests for leading-zero replacement;
- reusable contextual help honoring `Siempre visible` / `ⓘ` / `Oculto`;
- persisted global help mode with compatibility-safe default `Siempre visible`;
- compact provenance primitive `Tipo de origen | Origen específico`, defaulting to `Clase` where applicable.

### Drag-feel acceptance qualifier

The owner's prior finding that card movement felt stiff/mechanical remains **open for real-device acceptance**.

The implementation now fixes a concrete stale-callback/index problem and improves visual motion, but this is not considered solved merely because it compiles. If device audition still feels stiff, prioritize geometry/midpoint-based reorder and sibling displacement animation rather than blindly reducing the drag threshold.

## Increment C1 completed character-first entry

The successor navigation root now:

- opens on a global `Personajes` directory instead of campaign-first selection;
- lists characters across campaigns through a read-only global ID query and canonical hydration;
- projects name, `Raza`, classes + levels and campaign from canonical character/campaign data rather than persisting a duplicate summary model;
- requires explicit destination campaign when creating or importing a character from the global directory;
- retains campaign administration as a secondary route;
- uses Android Back hierarchy `editor → character directory → exit` and `campaign admin → character directory → exit`;
- leaves the root character directory unhandled by app Back so the system may exit normally.

C1 is technically green but not owner visually accepted yet.

## Increment C2 completed PC Settings information architecture

C2 now:

- keeps lifecycle status and safe local backup near the top;
- requires explicit confirmation when marking a character `Retirado` or `Muerto`;
- moves `Configuración de la aplicación` near the upper portion while keeping it a separate destination;
- removes `Progreso` editing from PC Settings because it is character state, not a Settings preference;
- configures spellcasting visibility and per-character Inspiration visibility without deleting canonical state;
- persists real character-tab order and applies it to both top tabs and wide rail;
- preserves hidden conditional-module tab positions because ordering precedes visibility filtering;
- adds PC Settings management for custom attributes, generalized custom-skill ability references and Custom Markers;
- avoids exposing the legacy generic custom-skill `Fuente` field in the successor Settings editor;
- blocks deleting custom attributes while durable skill/spellcasting references still depend on them;
- keeps Custom Marker live values out of Settings and edits only structure/recovery configuration there;
- retains existing conditional-module hide/show/automatic behavior without hide-delete regression;
- keeps haptic enablement per character while adding device-wide bounded strength/duration controls;
- applies those haptic preferences to shared drag/resource/destructive feedback with hardware-capability fallback;
- uses the shared form-factor context instead of a width-only Settings breakpoint, preserving phone-landscape phone composition;
- adds schema migration 12 → 13 for `CharacterSuccessorPreferences.inspirationVisible`;
- proves default, per-character persistence, migration and own-format backup/import survival with focused tests.

The temporary redundant PC-configuration table/repository explored during implementation was removed before C2 was closed. Inspiration visibility belongs to `CharacterSuccessorPreferences`, alongside tab order, and therefore travels with successor backup state.

### C2/C5 transition

The old special `Habilidades personalizadas` card/editor still exists in the current Habilidades surface. This is **not accepted as final**. C5 must remove that duplicate legacy editing path and render custom skills inline with ordinary skills using the successor mapping already configured from PC Settings.

C2 is technically green but not owner visually accepted yet.

## Resolved model directions remain controlling

- Custom attributes are full ability-like stats with standard modifier math and optional saves.
- Custom Markers remain logically distinct from Resources, but both may share reusable tracker/recovery mechanics.
- Resources gain controlled multi-tab presentation placement from one canonical value.
- Spellcasting ability/DC/attack belong per spellcasting source.
- Conjuros uses one compact sticky source-context bar rather than stacking source selector + large collection toolbar + source-stat boxes.
- Defensas surfaces current AC + equipped armor/shield references, not armor proficiencies.
- `Gemas / arte` is a compact free-form valuables box this cycle.
- Requested Cthulhu source is Sandy Petersen's Cthulhu Mythos for D&D 5e; proprietary Spanish descriptions remain content-source dependent.
- Contextual help uses one canonical explanation rendered according to global mode: `Siempre visible` / circled-`i` tooltip-info / `Oculto`.
- Provenance uses `Tipo de origen | Origen específico`, default `Clase`, only where it has user-facing value; do not duplicate Conjuros' functional spell-source association model.
- Use `Raza`, not `Especie/raza`.
- `Electrum`, not `Electro`.

## Successor implementation order

1. **A — schema/domain/storage foundation:** COMPLETE / GREEN;
2. **B — shared UX/responsive primitives:** COMPLETE / GREEN; drag feel pending owner audition;
3. **C — navigation + PC Settings + General/Habilidades:** **CURRENT**;
   - C1 character-first entry: COMPLETE / GREEN;
   - C2 PC Settings information architecture: COMPLETE / GREEN;
   - C3/C4 General: **CURRENT**;
   - C5 Habilidades: pending;
4. **D — Combat + Dados** using structured attacks and one target engine;
5. **E — Gestión + Markers + Resources + cross-domain rests/conditions**;
6. **F — Conjuros compact source-context redesign**, followed by early phone portrait/landscape owner retest;
7. **G — Equipo/Rasgos/conditional modules/Notas/Trasfondo**;
8. **H — full-screen Application Settings and live previews/themes**;
9. **I — separate tablet portrait/landscape redesign after phone primitives stabilize**.

Do not convert this into dozens of isolated screen patches.

## Existing QA findings remain active

Do not rerun build `40700` screen-by-screen. Global findings already cover app-wide density/row fragmentation, card/reorder interaction, shared IME problems, phone-landscape failure, tablet redesign, rotation context loss, fixed-area footprint, `Fuente` cleanup, Consumible/Munición UX, terminology, help/ⓘ behavior, 40% spacing and typography follow-up.

## Testing/build boundaries

- focused automated tests accompany migrations/domain work;
- full gate after coherent product boundaries using the established Kotlin/Android/Desktop and backend checks;
- Increment B has demonstrated representative responsive/density/toolbar/drag/IME/help primitives and is technically green;
- C1 character-first navigation is automated-gate green;
- C2 PC Settings information architecture is automated-gate green;
- Increment C still requires C3/C4 General and C5 Habilidades before its configuration/identity/skills family is coherent;
- early targeted owner phone retest remains after the Conjuros/interaction foundation rather than immediately after data/shared foundations;
- consolidated successor audition remains after collection/settings/responsive integration;
- physical tablet acceptance remains required before Phase 4A closure;
- formal replacement M6 remains deferred until the repaired phone/tablet baseline is acceptable.

## Exact next action

Proceed on `implementation/phase4a-successor-cycle` with **C3/C4 — General compact identity and canonical reference/state projections**.

1. rebuild class identity compactly, with class level clear and maximum hit dice derived from class level rather than redundantly presented as separate state;
2. use Spanish class/subclass presentation and avoid surfacing edition/source metadata without functional purpose;
3. surface canonical `Raza` and visible `Idiomas`;
4. rebuild Defensas around current AC plus equipped armor/shield references, not duplicate armor proficiency state;
5. show Inspiration only when its C2 visibility preference is enabled;
6. project enabled Custom Markers and Resources configured for General placement from their canonical successor state;
7. surface custom attributes;
8. replace ambiguous global spellcasting reference with compact per-source `Lanzamiento de Conjuros` rows using the successor casting profiles.

Then complete **C5 — Habilidades**:

- render custom skills inline with ordinary skills;
- remove the transitional special custom-skill card/editor from Habilidades;
- honor generalized custom-attribute references;
- apply alphabetical Spanish `Por habilidades` order;
- use `Conocimiento Arcano`;
- retain the owner-approved compact passive-reference sticky area.

Keep `main` untouched and do not begin DM features.

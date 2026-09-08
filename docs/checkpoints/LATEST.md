# Latest project checkpoint

**Updated:** 2026-09-08  
**Canonical branch:** `main`  
**Repository consolidation:** COMPLETE under D-0066  
**Current implementation branch:** `implementation/phase4a-successor-cycle`  
**Current reconciliation state:** SUCCESSOR IMPLEMENTATION ACTIVE  
**Increment A:** COMPLETE / automated foundation gate GREEN  
**Current product status:** successor implementation in progress; owner visual acceptance not yet run  
**Latest owner-auditioned practical identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary owner test phone:** Redmi Note 11 Pro 5G  
**Phone audition:** prior build Stages A–F sufficiently covered; visual acceptance NOT passed  
**Tablet acceptance:** not complete; tablet/wide UX is a redesign target  
**DM implementation:** blocked pending later Phase 4A closure acceptance

## Read next

1. `docs/checkpoints/2026-09-08_PHASE4A_INCREMENT_A_DATA_FOUNDATION.md` — **latest completed implementation checkpoint and verified migration/compatibility evidence**;
2. `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` — controlling successor implementation order and build/retest boundaries;
3. `docs/checkpoints/2026-09-08_D0067_RECONCILIATION_PENDING_DECISIONS.md` — despite the historical filename, records the resolved owner decisions and compact Conjuros source-context design;
4. `docs/decisions/D-0067_OWNER_NEXT_CYCLE_CHARACTER_UX_AND_FEATURE_REFINEMENTS.md` — full owner non-QA package;
5. `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_F.md` — detailed prior-build phone fixed-footprint evidence;
6. `docs/checkpoints/2026-09-08_PHASE4_PREQA_FUENTE_REDUNDANCY_AUDIT.md` — provenance/source IA follow-up;
7. `docs/PROJECT_STATE.md` — broader state snapshot.

## Canonical baseline and branch discipline

D-0066 consolidated the in-progress Phase 4 development reality into `main`. Canonical does **not** mean accepted/release-ready.

`main` remains untouched by successor implementation at:

`698d40b7da75bb7535d83f834db7044ef3e626a8`

Successor product work is isolated on:

`implementation/phase4a-successor-cycle`

Old implementation/tmp branches are historical evidence. Frozen QA branches remain immutable.

## Latest automated successor foundation

Increment A's full automated gate passed for product commit:

`7bfdeed2fe353dad609060d4b933ee22eab30f2e`

Workflow:

`34286023454` — SUCCESS

Verified together:

- backend type/check surface: PASS;
- shared desktop tests: PASS;
- Android debug compilation/assembly: PASS;
- desktop build: PASS;
- Android debug APK artifact upload: PASS.

This is an **automated foundation checkpoint**, not an owner-auditioned replacement build.

## Latest owner-auditioned practical build

The last owner-auditioned practical identity remains:

- version `0.4.0-preqa.7`;
- build `40700`;
- `debug`;
- prior verified product commit `43ca1f5662123ce4d355d9d618b0bfba66d17697`;
- prior workflow `34171466714` — SUCCESS;
- prior artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

Do not interpret the new successor foundation artifact as owner visual acceptance.

## Increment A completed foundation

The successor data layer now includes:

- generalized ability references covering built-in and custom attributes;
- full custom attributes with standard modifier math and optional saving throws;
- successor custom-skill ability mapping;
- generalized Dice target/calculation architecture for custom abilities/saves/skills, with persisted successor-state injection into the legacy editor deferred to later surface rebuilding;
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

A critical child-FK cascade issue was found during the Increment A audit and corrected with migration 11 before the foundation was closed. See the Increment A checkpoint for exact rationale and test evidence.

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
- Use `Raza`, not `Especie/raza`.
- `Electrum`, not `Electro`.

## Successor implementation order

1. **A — schema/domain/storage foundation:** COMPLETE / GREEN;
2. **B — shared UX/responsive primitives:** **CURRENT**;
3. **C — navigation + PC Settings + General/Habilidades**;
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
- Increment B must demonstrate shared representative responsive/density/toolbar/drag/IME/help primitives before broad tab conversion;
- early targeted owner phone retest remains after the Conjuros/interaction foundation rather than immediately after data foundation;
- consolidated successor audition remains after collection/settings/responsive integration;
- physical tablet acceptance still required before Phase 4A closure;
- formal replacement M6 remains deferred until the repaired phone/tablet baseline is acceptable.

## Exact next action

Proceed on `implementation/phase4a-successor-cycle` with **Increment B — shared UX and responsive primitives**.

Begin with:

1. B1 form-factor-aware shell so phone landscape cannot switch into tablet composition from width alone;
2. B2 centralized successor spacing/density primitives including the 40% audition option;
3. then B3 compact toolbar, B4 card manipulation, B5 IME/editor and B6 help/provenance primitives.

Keep `main` untouched and do not begin DM features.

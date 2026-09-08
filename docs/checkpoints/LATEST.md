# Latest project checkpoint

**Updated:** 2026-09-08  
**Canonical branch:** `main`  
**Repository consolidation:** COMPLETE under D-0066  
**Current owner development package:** D-0067 RECORDED + six consequential model decisions RESOLVED  
**Current reconciliation state:** SUCCESSOR IMPLEMENTATION PLAN COMPLETE  
**Current product status:** debug / pre-QA; known defects remain  
**Latest technically verified product identity:** `0.4.0-preqa.7` / build `40700` / `debug`  
**Primary owner test phone:** Redmi Note 11 Pro 5G  
**Phone audition:** Stages A–F sufficiently covered; visual acceptance NOT passed  
**Tablet acceptance:** not complete; tablet/wide UX is a redesign target  
**DM implementation:** blocked pending later Phase 4A closure acceptance

## Read next

1. `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` — **controlling successor implementation order and build/retest boundaries**;
2. `docs/checkpoints/2026-09-08_D0067_RECONCILIATION_PENDING_DECISIONS.md` — despite the historical filename, now records the resolved owner decisions and compact Conjuros source-context design;
3. `docs/decisions/D-0067_OWNER_NEXT_CYCLE_CHARACTER_UX_AND_FEATURE_REFINEMENTS.md` — full owner non-QA package;
4. `docs/checkpoints/2026-09-07_PHASE4_PREQA_OWNER_AUDITION_PHONE_STAGE_F.md` — detailed phone fixed-footprint evidence;
5. `docs/checkpoints/2026-09-08_PHASE4_PREQA_FUENTE_REDUNDANCY_AUDIT.md` — provenance/source IA follow-up;
6. `docs/PROJECT_STATE.md` — broader state snapshot.

## Canonical baseline

D-0066 consolidated the in-progress Phase 4 development reality into `main`. Canonical does **not** mean accepted/release-ready.

The reconciliation/planning work after build `40700` is documentation/governance only. No successor product code has yet been verified.

Old implementation/tmp branches are historical evidence. Frozen QA branches remain immutable.

## Latest verified product code

The full automated gate last passed for product commit:

`43ca1f5662123ce4d355d9d618b0bfba66d17697`

Identity:

- version `0.4.0-preqa.7`;
- build `40700`;
- workflow `34171466714` — SUCCESS;
- artifact `10035895186`;
- APK SHA-256 `6e024a00c3037030c4db8f1b1e4d840c1903dee3b5ea5d601c51d9d5a9c9039d`.

## Resolved model directions

- Custom attributes are full ability-like stats with standard modifier math and optional saves.
- Custom Markers remain logically distinct from Resources, but both may share reusable tracker/recovery mechanics.
- Resources gain controlled multi-tab presentation placement from one canonical value.
- Spellcasting ability/DC/attack belong per spellcasting source.
- Conjuros uses one compact sticky source-context bar rather than stacking source selector + large collection toolbar + source-stat boxes.
- Defensas surfaces current AC + equipped armor/shield references, not armor proficiencies.
- `Gemas / arte` is a compact free-form valuables box this cycle.
- Requested Cthulhu source is Sandy Petersen's Cthulhu Mythos for D&D 5e; proprietary Spanish descriptions remain content-source dependent.

## Successor implementation order

The reconciled plan deliberately sequences work as:

1. **A — schema/domain/storage foundation**: generalized ability references/custom attributes, per-source spellcasting, structured attack damage, shared tracker mechanics with separate Marker/Resource semantics, valuables/media persistence, migrations/backup tests;
2. **B — shared UX/responsive primitives**: phone/tablet form-factor logic, density, compact toolbars, card drag, IME-safe editors, help/provenance;
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
- early targeted owner phone retest after the Conjuros/interaction foundation rather than waiting for every cosmetic surface;
- consolidated successor audition after collection/settings/responsive integration;
- physical tablet acceptance still required before Phase 4A closure;
- formal replacement M6 remains deferred until the repaired phone/tablet baseline is acceptable.

## Exact next action

The pre-branch reconciliation gate is satisfied.

Create one focused implementation branch from this canonical `main` revision and begin **Increment A — schema/domain/storage foundation**. Do not start with visual one-off patches. Checkpoint meaningful increments, keep `main` untouched during product work, and do not begin DM features.

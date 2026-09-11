# Latest project checkpoint

**Updated:** 2026-09-11  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Phase:** Phase 4A successor acceptance / closure preparation  
**Current QA build:** `0.4.0-preqa.8` / `40800` / debug  
**Release status:** NOT owner-accepted and NOT release-ready  
**Primary owner phone:** Redmi Note 11 Pro 5G  
**Owner phone QA:** consolidated pass complete enough to define the next repair pass  
**Player tablet QA:** intentionally deferred until shared/systemic phone findings are repaired  
**DM discovery:** detailed Combat Desk product/UX baseline captured in D-0068  
**DM implementation:** blocked until Phase 4A is accepted and explicitly closed

## Current boundary

The planned A–I engineering sequence and post-audition stabilization are complete and automated-green, but owner/device QA of `preqa.8 / 40800` found multiple Phase 4A acceptance blockers.

The owner completed the required in-place upgrade/data-preservation test, phone portrait sampling, representative editors/IME, core Player modules, special modes, phone landscape and representative larger-text testing.

Physical tablet portrait/landscape QA is **not cancelled**; it is deliberately deferred because several newly observed defects are shared/systemic and would predictably contaminate tablet QA. Shared defects are now part of the repair scope across phone and tablet by default, but tablet physical PASS/FAIL must still be established later on the repaired build.

The controlling owner-QA checkpoint is:

`docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`

Read it before planning or implementing any further Player repair.

The technically verified build boundary remains:

`docs/checkpoints/2026-09-09_PHASE4A_PLAYER_PREQA8_STABILIZATION.md`

Future DM Combat Desk design baseline remains:

`docs/decisions/D-0068_DM_COMBAT_DESK_PRODUCT_AND_UX.md`

D-0068 is design truth only and does not authorize DM implementation before explicit Phase 4A closure.

## QA results now established on preqa.8

### Confirmed PASS areas

- in-place upgrade over prior QA installation/data;
- campaigns/characters and representative data preservation;
- full close/reopen persistence;
- phone portrait baseline navigation;
- representative editor keyboard / Save / Cancel / Delete / reopen function;
- currency;
- Conjuros portrait source/context behavior;
- Notas normal browsing/editing/search;
- Application Settings functionality/understandability;
- representative conditional modules;
- representative backup/export;
- phone landscape remains a phone interaction model rather than switching to tablet UI;
- representative larger application text scale.

### Functional/state blockers

1. HP changes in General do not propagate correctly to Combate; this violates one-datum/one-state.
2. `Modo mesa` cannot be activated even after saving/cleaning the sheet, contrary to the intended Table-mode contract.

### Core/high-frequency UX blockers

3. damage/healing registration is too cumbersome for frequent combat use;
4. Combate fixed quick-reference area consumes too much of the viewport;
5. the new special one-column `Reordenar -> Listo` workflow is owner-rejected; target is direct drag-and-drop of cards in their normal layout, including multicolumn layouts where applicable;
6. Rasgos structured provenance still requires redundant typing of known origins; `Clase`, `Subclase`, `Raza` and `Trasfondo` must come from this character's canonical data, while `Otro` and `Don` may use free text;
7. Vista supercompacta is conceptually rejected and must be redesigned as a dense at-table PC reference using the information-density/scanning grammar of a modern D&D 5.5e monster/NPC stat block rather than being primarily a Favorites dashboard.

### Responsive/systemic blockers

8. sticky/fixed regions consume too much or effectively all usable vertical space in phone landscape;
9. Conjuros sticky `Nivel` region becomes obstructive in landscape;
10. excessive vertical margins/padding that are merely inconvenient in portrait become a major landscape usability problem;
11. responsive layout policy must consider available **height**, not only width/form-factor/orientation labels.

### Owner-rejected regressions / required design corrections

12. Trasfondo photo UX was unnecessarily redesigned; restore the previous preferred photo interaction/presentation while retaining the new durable persistence/storage and backup behavior;
13. shared editor windows are functionally safe but too often full-height regardless of content; make short editors size adaptively while preserving IME safety;
14. attack structured-damage model works but the editor interaction/labels are awkward and need a more direct compact dice/component UX;
15. PC Settings functions but needs one coherent visual/information-architecture redesign rather than scattered padding patches;
16. compactness scaling is disproportionate: inter-card/box gaps change much more than the rest of the UI;
17. theme selector should show **name + simple three-color palette shorthand + current useful representative preview**;
18. custom Habilidad geometry must match ordinary skills; italics remain the intended distinction;
19. contextual `ⓘ` behavior is good but icon positioning/alignment needs refinement.

## Cross-device repair rule

Do **not** scope these findings as phone-only merely because they were first observed on phone.

Any issue caused by shared state, shared components, shared spacing, shared editor/reorder primitives, shared special-mode logic or shared product concept must be repaired across both phone and tablet surfaces unless a device-specific exception is demonstrated.

At minimum, cross-device repair scope includes:

- canonical HP/state authority;
- shared editor sizing;
- direct drag-and-drop reorder interaction;
- Rasgos provenance selection model;
- damage/healing interaction primitive;
- compactness/spacing system;
- PC Settings shared UX/IA;
- theme selector presentation;
- Table mode;
- Supercompact concept;
- sticky/fixed-region and responsive-height policy;
- Trasfondo photo interaction where the same UI is shared.

Important evidence distinction:

- **repair scope:** phone + tablet for shared/systemic causes;
- **physical acceptance evidence:** phone only so far;
- **tablet physical QA:** still required later on the repaired build.

## Current technically verified build identity

- version `0.4.0-preqa.8`;
- build `40800`;
- type `debug`;
- product source commit `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree `c612c07345ecdfc91d972118314ee649fe2048c4`;
- validation/checkpoint head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- normal `Scaffold checks` workflow `34430548061` — **SUCCESS**;
- artifact ID `10134364621` (`dnd-custom-aid-debug-apk`);
- ZIP digest `sha256:b7ead12a7501bbef96f861321b5bebfd64c631647423b8eab9faec9580699a`;
- extracted APK digest `sha256:bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

Automated green remains evidence of technical correctness only; it did not predict all owner/device acceptance defects.

## Automated-boundary lessons from owner QA

Future repair validation must strengthen actual interaction coverage where static/model checks created false confidence:

- Rasgos: validate that choosing a structured origin type presents canonical character origins and does not require redundant typing;
- Table mode: validate actual activation from a clean persisted state, not only policy logic;
- HP: validate propagation across General/Combate/shared surfaces;
- responsive layout: explicitly exercise short-height phone landscape sticky/fixed regions;
- reorder: validate the owner-approved direct normal-layout drag interaction, not merely a vertical fallback mode.

## Exact next position

Do **not** begin tablet QA on the current build and do **not** begin DM implementation.

Next Player session:

1. read `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md`;
2. discuss the grouped findings with the owner and resolve only remaining implementation details;
3. define a bounded Phase 4A acceptance-repair plan across shared phone/tablet surfaces;
4. implement only the accepted repair scope on the active continuation branch;
5. run exact automated validation for each repaired boundary;
6. produce the next monotonic successor QA build;
7. perform targeted phone retest of the repaired blockers;
8. after shared/systemic phone blockers are acceptable, perform tablet portrait and tablet landscape physical QA on the repaired build;
9. freeze the replacement formal M6 candidate only when owner-audited behavior is acceptable;
10. complete final regression/upgrade acceptance;
11. explicitly close Phase 4A;
12. only then may DM implementation begin.

## Read next

1. `docs/checkpoints/2026-09-11_PHASE4A_PREQA8_OWNER_PHONE_QA_CONSOLIDATED.md` — controlling owner/device QA findings, cross-device repair rule and next action;
2. `docs/checkpoints/2026-09-09_PHASE4A_PLAYER_PREQA8_STABILIZATION.md` — exact current build/product/audit/full-gate evidence;
3. `docs/decisions/D-0068_DM_COMBAT_DESK_PRODUCT_AND_UX.md` — future DM Combat Desk design baseline; no implementation permission;
4. `docs/PROJECT_STATE.md` — broader project snapshot;
5. `docs/TESTING.md` — acceptance contract;
6. `docs/ROADMAP.md` — Phase 4A/4B sequencing;
7. `docs/BRANCH_STATUS.md` — repository authority map.

## Protected owner directions

- one datum / one canonical state across tabs;
- phone landscape remains a phone interaction model;
- phone and tablet share repair responsibility for shared/systemic defects;
- tablet portrait/landscape remain independently designed and still require physical acceptance later;
- reduce unnecessary margins/padding without degrading required touch targets;
- responsive layout must consider available height as well as width;
- direct drag-and-drop in the normal card layout is the reorder target;
- `Clase`, `Subclase`, `Raza` and `Trasfondo` provenance must use canonical character data; `Otro` and `Don` may use free text;
- restore preferred old UX when persistence was the actual requested underlying change;
- Supercompact should read as a dense PC stat block inspired by the 5.5e monster/NPC reading grammar;
- contextual help remains one canonical explanation rendered as `Siempre visible`, `ⓘ / tooltip`, or `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- no DM feature implementation before explicit Phase 4A closure.

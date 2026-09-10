# Latest project checkpoint

**Updated:** 2026-09-10  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Repository state:** planned successor engineering A–I plus post-audition Player stabilization complete and automated full-gate green; consolidated owner/device QA remains pending and is currently pinned until the owner can test  
**Phase:** Phase 4A successor acceptance / closure preparation  
**Current QA build:** `0.4.0-preqa.8` / `40800` / debug  
**Release status:** NOT owner-accepted and NOT release-ready  
**Primary owner phone:** Redmi Note 11 Pro 5G  
**Player tablet acceptance:** pending; redesigned tablet portrait/landscape implementation is automated-green  
**DM discovery:** detailed Combat Desk product/UX baseline captured in D-0068  
**DM implementation:** blocked until Phase 4A is later accepted and explicitly closed

## Current boundary

The planned A–I engineering sequence is complete. Post-audition stabilization is also complete through the current consolidated Player build.

There is no planned Increment J. Do not invent additional Player feature scope before owner acceptance/closure.

The owner currently cannot perform physical QA, so the consolidated Player QA is **pinned**, not cancelled. This pause does not waive any acceptance requirement.

Latest Player product checkpoint:

`docs/checkpoints/2026-09-09_PHASE4A_PLAYER_PREQA8_STABILIZATION.md`

That checkpoint records the complete repair/audit/full-gate evidence and is the controlling continuation document for the current QA build.

Future DM Combat Desk design baseline:

`docs/decisions/D-0068_DM_COMBAT_DESK_PRODUCT_AND_UX.md`

D-0068 consolidates the owner-approved DM combat discovery performed while physical Player QA is waiting. It is **design truth only** and does not authorize DM product implementation before explicit Phase 4A closure.

## Increment and stabilization status

1. A — schema/domain/storage foundation: **COMPLETE / GREEN**;
2. B — shared UX/responsive primitives: **COMPLETE / GREEN**;
3. C — character-first navigation + PC Settings + General/Habilidades: **COMPLETE / GREEN**;
4. D — Combat + Dice: **COMPLETE / GREEN**;
5. E — Gestión + Markers + Resources + cross-domain rest/conditions: **COMPLETE / GREEN**;
6. F — Conjuros compact source-context redesign + repair: **AUTOMATED GREEN / INCLUDED IN CONSOLIDATED QA**;
7. G — Equipo/Rasgos/conditional modules/Notas/Trasfondo: **COMPLETE / AUTOMATED GREEN / INCLUDED IN CONSOLIDATED QA**;
8. H — full-screen Application Settings/live previews/themes: **COMPLETE / AUTOMATED GREEN / INCLUDED IN CONSOLIDATED QA**;
9. I — separate tablet portrait/landscape redesign: **COMPLETE / AUTOMATED GREEN / PHYSICAL TABLET ACCEPTANCE PENDING**;
10. post-A–I stabilization — Rasgos provenance, linear reorder fallback, Settings/font/theme refinements, app-wide free-text density and residual acceptance audit: **COMPLETE / AUTOMATED GREEN / OWNER QA PENDING**.

## Latest automated product boundary — consolidated Player preqa.8

Product identity:

- version `0.4.0-preqa.8`;
- build `40800`;
- type `debug`;
- product source commit `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree `c612c07345ecdfc91d972118314ee649fe2048c4`;
- validation/checkpoint head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`.

Normal `Scaffold checks` workflow:

`34430548061` — **SUCCESS**

Verified together:

- backend dependency install/check: PASS;
- shared/Kotlin desktop tests: PASS;
- Android debug assembly: PASS;
- desktop build: PASS;
- stable CI debug signing: PASS;
- Android debug APK upload: PASS.

Artifact:

- ID `10134364621`;
- name `dnd-custom-aid-debug-apk`;
- ZIP size `13,321,947` bytes;
- ZIP digest `sha256:b7ead12a7501bbef96fef861321b5bebfd64c631647423b8eab9faec9580699a`;
- extracted APK size `37,996,660` bytes;
- extracted APK digest `sha256:bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

The downloaded ZIP digest was independently rechecked and matched CI exactly. The ZIP contains exactly one APK.

This is an **automated-green consolidated owner-QA build**, not owner acceptance and not yet a formal frozen M6 candidate.

## Stabilization summary

The current build includes the earlier A–I implementation plus the post-audition stabilization line:

- Rasgos provenance now reuses canonical class/race/background identity choices while retaining explicit custom origin and one persisted source authority;
- Equipo/Rasgos/Notas retain configured grid browsing but temporarily switch to one-column vertical whole-card drag while `Reordenar` is active;
- rejected fonts are hidden from normal selection with safe Manrope fallback for previously saved hidden values;
- Settings theme/font audition uses a realistic free-text preview instead of misleading miniature swatches;
- Ámbar/Pizarra/Terracota were retuned;
- old large permanent multiline text-area slabs were removed across Trasfondo, Notas and equivalent module editors;
- one shared spacing-aware text-area policy is used broadly;
- the residual acceptance audit measured 50 spacing-aware multiline references and 42 shared IME-safe editor references;
- Conjuros remains per-source, compact and phone-landscape-safe, with shared numeric normalization.

## DM Combat Desk discovery captured while Player QA is pinned

D-0068 records the current future-DM baseline without changing the implementation gate. Key confirmed directions include:

- live DM Combat Desk is tablet-landscape only;
- initiative is always visible but does not control which reference/stat block is open;
- legendary actions/reactions/interruptions do not become fake initiative turns;
- Reference Desk, Combat State, Encounter Notes/Rules of Engagement, Clocks, Markers and Quick Rules are modular hide/show surfaces;
- the owner prefers the current D&D 5.5e monster stat-block reading model;
- PCs, NPCs and monsters share a coherent DM reference grammar, while canonical Player data authority remains separate;
- Party Overview and Creature Overview provide the "forest" view while individual stat blocks provide the "tree" view;
- reusable creature definitions are separate from live creature instances;
- live encounter overrides/improvisation must allow HP/AC changes, ad-hoc reinforcements, improvised actions and partial custom combatants without silently rewriting reusable definitions;
- Quick Rules is a memory aid, not forced rules validation;
- DM working state is private by default; player initiative/projection behavior remains deliberately unresolved;
- grids, maps, tokens, automatic targeting/range, encounter balancing and VTT-style combat execution are explicit non-goals.

No DM code was implemented as part of this discovery.

## Owner/device acceptance boundary

No automated gate is owner visual/device acceptance.

The next Player activity remains one **consolidated Player QA**, not another round of isolated micro-retests. It is simply waiting until the owner can test.

The first installation of `preqa.8 / 40800` must be performed **over the existing prior QA installation/data**. Do not clear app data first. Before any fresh-install comparison, verify that campaigns/characters and representative General, Combate, Equipo/Monedas, Conjuros and Notas data survive and reopen correctly.

Physical acceptance still needs representative evidence for:

- phone portrait;
- phone landscape;
- Player tablet portrait;
- Player tablet landscape;
- representative larger application text scale;
- practical editor/IME, drag, theme and responsive behavior.

## Exact next position

Until physical QA is possible:

1. do not create a new Player engineering increment merely to fill the wait;
2. DM discovery/design may continue and be documented if the owner requests it;
3. do not implement DM product code.

When the owner can test again:

1. install the exact `0.4.0-preqa.8 / 40800` consolidated APK over the current QA app/data;
2. verify upgrade/data preservation first;
3. run consolidated Player owner QA across the repaired surfaces and remaining acceptance matrix;
4. classify findings before changing product code;
5. repair only acceptance-blocking defects actually observed;
6. freeze a replacement formal M6 candidate when the owner-audited baseline is acceptable;
7. complete required regression/upgrade QA;
8. explicitly close Phase 4A.

Only after explicit Phase 4A closure may DM implementation begin. Phase 4B should then begin from D-0068's open-question list and an owner-approved implementation plan.

## Read next

1. `docs/checkpoints/2026-09-09_PHASE4A_PLAYER_PREQA8_STABILIZATION.md` — current Player product/audit/full-gate evidence and exact QA handoff;
2. `docs/decisions/D-0068_DM_COMBAT_DESK_PRODUCT_AND_UX.md` — future DM Combat Desk design baseline and unresolved questions; no implementation permission;
3. `docs/PROJECT_STATE.md` — broader current snapshot;
4. `docs/ROADMAP.md` — current Phase 4A/4B sequencing;
5. `docs/TESTING.md` — automated and physical acceptance contract;
6. `docs/BRANCH_STATUS.md` — repository-ordering/authority map;
7. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_I_TABLET_REDESIGN.md` — final planned A–I engineering increment;
8. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_H_APPLICATION_SETTINGS.md` — Application Settings implementation;
9. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_G_COLLECTION_CONTENT_REPAIRS.md` — collection/content implementation;
10. `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_F_CONJUROS_COMPACT_SOURCE_CONTEXT.md` — Conjuros repair boundary.

## Protected owner directions

- one datum / one canonical state across tabs;
- phone landscape remains a phone interaction model;
- Player tablet portrait/landscape are independently designed rather than stretched phone layouts;
- extra tablet width must provide useful context, not permanent empty panes;
- compact compatible controls rather than unnecessary vertical stacks;
- whole-card drag where safe, without shrinking required touch targets;
- contextual help remains one canonical explanation rendered as `Siempre visible`, `ⓘ / tooltip`, or `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- no DM feature implementation before explicit Phase 4A closure;
- future DM Combat Desk work must honor D-0068's tablet-landscape, private-by-default, always-visible initiative, tree/forest reference, flexible DM-authority and no-VTT boundaries.

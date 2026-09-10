# Project State

**Last verified:** 2026-09-10  
**Canonical branch:** `main`  
**Active continuation branch:** `implementation/phase4a-successor-cycle`  
**Current implementation boundary:** planned successor A–I plus post-audition Player stabilization complete and automated full-gate green on continuation branch  
**Current phase:** Phase 4A — consolidated successor owner QA / closure preparation  
**Current QA build:** `0.4.0-preqa.8` / `40800` / debug  
**Release status:** owner/device acceptance remains open; not release-ready  
**Player QA scheduling:** consolidated physical QA is currently pinned until the owner can test; acceptance requirements remain unchanged  
**DM work:** product/UX discovery may be documented; DM implementation remains blocked until Phase 4A is accepted and explicitly closed

## 1. Canonical repository reality

`main` remains the canonical baseline and has not been advanced by the later successor implementation/stabilization sequence. The active continuation branch contains the current A–I successor implementation plus the post-audition Player stabilization repairs. It also now contains design-only DM discovery under D-0068. Canonical, documented or automated-green does not mean released, QA-accepted or owner-accepted.

Use:

- `docs/checkpoints/LATEST.md` for the exact current Player implementation/QA position and next action;
- `docs/checkpoints/2026-09-09_PHASE4A_PLAYER_PREQA8_STABILIZATION.md` for the current consolidated product/audit/full-gate evidence;
- `docs/decisions/D-0068_DM_COMBAT_DESK_PRODUCT_AND_UX.md` for the owner-approved future DM Combat Desk design baseline; this is design truth only, not implementation permission;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_I_TABLET_REDESIGN.md` for the final planned A–I engineering increment;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_H_APPLICATION_SETTINGS.md` for Application Settings;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_G_COLLECTION_CONTENT_REPAIRS.md` for collection/content work;
- `docs/checkpoints/2026-09-09_PHASE4A_INCREMENT_F_CONJUROS_COMPACT_SOURCE_CONTEXT.md` for the Conjuros repair boundary;
- `docs/checkpoints/2026-09-08_PHASE4A_RECONCILED_SUCCESSOR_IMPLEMENTATION_PLAN.md` for the controlling A–I sequence.

Frozen QA candidate branches remain historical evidence.

## 2. Successor implementation and stabilization progress

- A — schema/domain/storage foundation: **COMPLETE / GREEN**;
- B — shared UX/responsive primitives: **COMPLETE / GREEN**;
- C — character-first navigation + PC Settings + General/Habilidades: **COMPLETE / GREEN**;
- D — Combat + Dice: **COMPLETE / GREEN**;
- E — Gestión + Markers + Resources + recovery/conditions: **COMPLETE / GREEN**;
- F — compact Conjuros source-context redesign + repair: **AUTOMATED GREEN / INCLUDED IN CONSOLIDATED QA**;
- G — Equipo/Rasgos/conditional modules/Notas/Trasfondo: **COMPLETE / AUTOMATED GREEN / INCLUDED IN CONSOLIDATED QA**;
- H — full-screen Application Settings/live previews/themes: **COMPLETE / AUTOMATED GREEN / INCLUDED IN CONSOLIDATED QA**;
- I — tablet portrait/landscape redesign: **COMPLETE / AUTOMATED GREEN / PHYSICAL TABLET ACCEPTANCE PENDING**;
- post-A–I Player stabilization — Rasgos provenance, linear reorder fallback, Settings/font/theme refinements, app-wide multiline density and residual audit: **COMPLETE / AUTOMATED FULL-GATE GREEN / OWNER QA PENDING**.

The planned A–I engineering sequence is complete. There is no planned Increment J. Remaining Player work is consolidated owner/device acceptance, blocking repairs if observed, formal candidate/regression work and explicit Phase 4A closure.

The owner cannot perform physical QA at the current moment, so the consolidated Player QA is **pinned rather than decomposed into more micro-tests or replaced by further speculative Player engineering**.

## 3. Latest technically verified boundary

Current consolidated Player QA build:

- version `0.4.0-preqa.8`;
- build `40800`;
- type `debug`;
- product source commit `c78b06776f5ae7a253b5b12b791c71fa2a7da096`;
- product tree `c612c07345ecdfc91d972118314ee649fe2048c4`;
- validation/checkpoint head `2a9b682f6aca2e95facecf1f6256039fd96cfefd`;
- workflow `34430548061` — **SUCCESS**;
- artifact ID `10134364621` / `dnd-custom-aid-debug-apk`;
- artifact ZIP size `13,321,947` bytes;
- artifact ZIP digest `sha256:b7ead12a7501bbef96fef861321b5bebfd64c631647423b8eab9faec9580699a`;
- extracted APK size `37,996,660` bytes;
- extracted APK digest `sha256:bb02b413919f55551eb7d4e78dfab2c37145b852c8827126df80082bd7a40815`.

Verified in one normal gate:

- backend dependency install/check;
- shared/Kotlin desktop tests;
- Android debug assembly;
- desktop application build;
- stable CI debug signing;
- Android debug APK upload.

The downloaded ZIP was independently hashed after retrieval and matched the GitHub Actions digest exactly; it contains exactly one APK.

This is the latest technically verified Player product boundary. It has not received owner/device acceptance and is not yet a formal frozen M6 candidate.

The backend job also emitted non-blocking dependency/tooling warnings (including npm dependency findings and Node/Wrangler setup notices). They did not fail the automated gate and are not being treated as Player acceptance failures.

## 4. Implemented successor foundations A–I

### A–E foundations

The continuation line retains generalized built-in/custom ability references, custom attributes and optional custom saves, custom-skill mapping, per-source spellcasting, structured combat damage, Custom Markers, canonical Resources with structured recovery/placements, durable tab order, app-owned background-image state/backup, explicit four-form-factor layout context, compact collection/drag/editor/help primitives, character-first navigation, PC Settings, General/Habilidades, Combat/Dice and Gestión recovery/live-state work.

### F — Conjuros

Compact source context, canonical per-source casting projections, compact search/sort/filter/add behavior, whole-card reorder and IME/numeric repair are automated-green. Phone landscape continues to use the phone model rather than entering tablet/wide interaction solely because of orientation.

### G — collection/content domains

- Equipo projects canonical CA/equipped state, Equipment Resources, clearer consumables, `Electrum` and `Gemas / arte`;
- Rasgos projects Trait Resources and structured provenance;
- Artífice/Formas/Técnicas/Metamagia/Pactos/Compañeros use the compact shared collection grammar while retaining module visibility semantics;
- Notas supports title/content search, presentation-only filtering, Manual/A–Z and configurable columns without expanding its intentionally minimal data model;
- Trasfondo primary/secondary images are selectable, resized/transcoded, app-owned, persistent, removable and backup/import safe.

### H — Application Settings

- global settings is a full-screen surface while the previous app screen remains composed underneath;
- text size and spacing use stepped controls with live previews; text retains 70–200% and spacing includes 40%;
- device haptic strength/duration has one app-wide authority while activation remains per character;
- requested theme renames are applied;
- Carmesí, Ámbar, Glaciar, Lavanda, Pizarra and Terracota remain audition themes.

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

## 5. Post-audition Player stabilization included in preqa.8

### Rasgos provenance

The editor now derives known class/race/background origin options from canonical character identity data and stable class IDs. Custom origin remains available only as the explicit fallback path, while the trait continues to persist one source value rather than parallel source authorities.

### Reorder interaction

Equipo, Rasgos and Notas preserve configured multi-column browsing. Activating `Reordenar` temporarily forces one-column presentation and reuses the stable vertical whole-card drag path; `Listo` returns to the configured grid. This removes unstable 2-D drag from the acceptance path without discarding useful multi-column browsing.

### Settings / fonts / themes

Inter, Figtree, Public Sans, Cabin Condensed and Encode Sans Condensed are hidden from normal selection. Previously saved hidden choices resolve safely to Manrope. Misleading miniature theme swatches were removed, the realistic free-text preview remains, and Ámbar/Pizarra/Terracota were retuned.

### App-wide multiline density

A static audit found 58 multiline/fixed editor-field surfaces. The repair removed large permanent height slabs from Trasfondo, Notas and equivalent module editors and introduced one shared spacing-aware minimum-line policy while preserving required touch targets. The final residual audit measured 50 spacing-aware multiline references and 42 shared IME-safe editor references.

Residual audit workflow `34430378823` completed successfully and self-removed.

## 6. Future DM Combat Desk discovery captured while QA is pinned

D-0068 now preserves the owner-approved product/UX baseline for the future Phase 4B live DM combat surface. **No DM product code was implemented.**

Confirmed core directions include:

- live DM Combat Desk is **tablet landscape only**;
- initiative is always visible but independent from the selected/open reference;
- legendary actions/reactions/other interruptions do not become fake initiative turns;
- Reference Desk, Combat State, Encounter Notes/Rules of Engagement, Clocks, Markers and Quick Rules are modular hide/show surfaces whose data survives hiding;
- the owner strongly prefers the current D&D 5.5e monster-stat-block reading model;
- PCs, NPCs and monsters should share a coherent DM reference grammar; from the DM's reading perspective a PC is another stat block, without creating a second PC data authority;
- Party Overview and Creature Overview are first-class requirements so the DM can move between individual "tree" and aggregate "forest" views;
- one reusable creature definition may back many live encounter instances with separate HP/status/notes/overrides;
- live encounter state must support deliberate DM rule-of-cool/fudging: HP/AC overrides, ad-hoc reinforcements, improvised attacks/actions and partial custom combatants without silently rewriting reusable definitions;
- Quick Rules is a memory aid grounded in official rules, not forced legality validation;
- DM state is private by default and must never be mirrored wholesale to players;
- any future player-facing initiative/current-turn projection remains an explicit open choice, including the alternative of a separate player-managed PC tracker;
- explicit non-goals include grids, maps, tokens, automatic movement/range/targeting, encounter balancing and VTT-style combat execution.

The leading wireframe direction is a permanent initiative region plus a modular, reference-dominant tablet-landscape workspace. D-0068 deliberately leaves exact tracker orientation/layout and several interaction details open for owner review before Phase 4B coding.

## 7. Protected owner directions

These remain controlling:

- one datum / one canonical state;
- compact compatible controls into clear rows instead of unnecessary vertical stacks;
- reduce unnecessary margins/padding without degrading required touch targets;
- whole-card drag where safe;
- phone landscape is a phone interaction model, not tablet UI;
- Player tablet portrait and Player tablet landscape are first-class compositions rather than stretched phone layouts;
- extra tablet width must increase useful context, not create permanent empty panes;
- use `Raza`, never `Especie/raza`;
- use `Electrum`, never `Electro`;
- Spanish class/subclass presentation;
- contextual explanations remain through `Siempre visible` / `ⓘ / tooltip` / `Oculto`;
- generic `Fuente` schema leakage must not be reintroduced;
- Conjuros source association remains a real behavioral concept and may be custom/non-class;
- future DM Combat Desk work must honor D-0068: DM authority over automation, reference over enforcement, private-by-default state, independent always-visible initiative, tree/forest reference views and no-VTT boundaries.

## 8. Owner/device acceptance state

No automated gate through `preqa.8 / 40800` is owner visual/device acceptance.

The next owner Player work remains one consolidated QA pass, not isolated micro-retests. It is currently pinned until the owner can physically test. The first installation must preserve the current app/data and install `preqa.8` over it so migration/data preservation is actually exercised.

Before clearing data or doing a fresh-install comparison, verify that campaigns/characters and representative General, Combate, Equipo/Monedas, Conjuros and Notas data survive and reopen correctly.

Physical acceptance still requires representative evidence for:

- phone portrait;
- phone landscape;
- Player tablet portrait;
- Player tablet landscape;
- representative larger application text scale;
- practical editor/IME, drag, theme and responsive behavior.

The repository does not currently include a dedicated tablet screenshot/emulator regression harness; do not report screenshot/device acceptance from CI.

## 9. Conditional/deferred boundaries

- SRD-backed `Buscar existente` selectors require an approved corpus; this cycle does not silently become full SRD ingestion.
- Exact proprietary Sandy Petersen Cthulhu Mythos condition text remains deferred without an appropriate content source.
- Do not invent an automatic AC rules engine before inventory has sufficient armor/shield semantics.
- Do not begin DM implementation before Phase 4A closure, even though D-0068 now contains detailed DM product/design direction.

## 10. Exact next position

There is no new planned Player engineering increment and no authorized DM implementation increment.

When physical testing becomes possible, resume the consolidated successor acceptance / closure sequence:

1. install the exact `0.4.0-preqa.8 / 40800` APK over the existing prior QA installation/data;
2. verify upgrade/data preservation first;
3. execute consolidated Player owner QA across the repaired surfaces and full acceptance matrix;
4. classify findings before changing code;
5. implement only acceptance-blocking repairs actually observed;
6. freeze the replacement formal M6 candidate when the owner-audited baseline is acceptable;
7. complete required regression/upgrade QA;
8. explicitly close Phase 4A.

While that QA is pinned, additional DM discovery/design documentation is allowed if the owner wishes, but it must remain clearly separated from implementation.

After explicit Phase 4A closure, Phase 4B should begin by reading D-0068, resolving its open tablet-landscape interaction questions with the owner, freezing a DM implementation plan, and only then coding.

Phase 4A is **not** closed merely because the consolidated build is automated-green.

**No DM feature implementation begins before explicit owner acceptance and Phase 4A closure.**

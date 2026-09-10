# D-0069 — DM Desk family, Stage Desk and DM Screen direction

**Status:** Approved current product/design direction, with explicitly marked future/pending items below  
**Date:** 2026-09-10  
**Decision owner:** Project owner  
**Implementation status:** design/discovery only; DM implementation remains BLOCKED until Phase 4A is accepted and explicitly closed

## Purpose

This decision consolidates the owner-approved DM Desk family after the Workspace/Dungeon discovery recorded in D-0068. It defines the currently approved live DM Desks and the refined contracts for **DM Screen**, **Stage Desk**, **Dungeon Desk** and **Combat Desk**.

It also preserves deferred directions without promoting them into current implementation scope, especially Journey behavior, fast improvised NPC generation and broader house-rule-aware **Quick Rules Question** behavior.

D-0068 remains authoritative for the detailed Workspace, DM Attention Budget, Dungeon Turns, clocks, contextual notes, triggers, Encounter Readiness and dirty-improvisation direction except where this later decision explicitly refines terminology or behavior.

Nothing in this record authorizes DM implementation before the existing Phase 4A closure gate.

---

## 1. Approved current Desk family

The currently approved live DM Desk family is:

1. **DM Screen** — neutral general-purpose DM surface, party reference and quick rules/reference access;
2. **Stage Desk** — fast retrieval/navigation across the current broad adventure environment and its places, NPCs, shops and adventure/scene spine;
3. **Dungeon Desk** — dangerous structured exploration and dungeon operation;
4. **Combat Desk** — active combat operation and tactical guidance.

These are purpose-specific operational environments inside the flat Workspace model from D-0068. They are not a content taxonomy and do not imply that every campaign entity becomes a Desk.

Do not create additional Desks merely because a feature needs somewhere to live. NPCs, shops, scenes, encounters, notes, clocks, rules references, factions and similar concepts are entities/capabilities/views unless real play later demonstrates a distinct operational goal that deserves a Desk.

### Journey Desk — future candidate only

A possible **Journey Desk** is explicitly deferred.

The owner currently tends to run travel as either fast travel or a roughly linear dungeon-like sequence with random/possible encounters and the possibility that no event occurs. Because that workflow is close to Dungeon Desk behavior, future travel support should first be tested as a **special Dungeon Desk mode/profile** before creating a fifth Desk.

A separate Journey Desk should exist only if real use demonstrates a substantially different operational need such as route management, watches, supplies, weather, navigation, camps or other travel-specific work that cannot remain simple inside Dungeon Desk.

No Journey implementation is authorized now.

---

## 2. Shared Desk principles

The D-0068 Desk definition remains controlling: a Desk is a purpose-specific DM working environment organized around one coherent operational goal.

Cross-Desk principles:

- opening/viewing an entity does not automatically create a Desk;
- Workspace-level Desk switching should make currently open/suspended Desks easy to reach;
- contextual Notes, Clocks and entity references should be reusable rather than duplicated per Desk;
- prepared material and live/dirty working state remain conceptually distinct;
- app navigation does not silently assert fictional movement or state changes;
- the DM Attention Budget remains controlling: derive, remember, prefill or postpone rather than demanding live bookkeeping;
- shared campaign/Workspace state remains distinguishable from per-device/per-user presentation state.

---

## 3. DM Screen

### Operational goal

> Provide a neutral DM working surface when no specialized Desk is required, with immediate party reference and the small set of general rules/reference tools useful during play.

**DM Screen** replaces the weak working name `General Desk`.

It must not become a miscellaneous dumping ground for functionality belonging to specialized Desks.

### Core content

DM Screen contains or gives immediate access to:

- **PC Group Sheet**;
- **PC Quick Sheet**;
- **PC Full Sheet**;
- configurable/general **DM reference** material;
- **Party Lens**;
- **Quick Rules Question**.

Existing character data remains canonical. DM Screen does not create a second PC model.

### Party Lens — Approved

Party Lens is a **derived, read-only, zero-maintenance** view over data the application already knows about the PCs.

Its purpose is to answer frequent DM questions without opening several individual character sheets, for example:

- highest passive Perception or other useful passive values;
- which PCs have darkvision or other relevant senses;
- languages represented in the party;
- relevant proficiencies/skills;
- movement speeds;
- Armor Class;
- other comparable party facts that prove useful in real play.

The exact field set is **Pending real-table refinement** and should remain compact rather than becoming a giant comparative spreadsheet.

Because normal play is paper-first under D-0020, Party Lens must not silently treat potentially stale digital transient state as current table truth. Prefer stable/derived sheet facts; if transient values are ever included, freshness must be clear.

### General DM reference

DM Screen may provide quick access to frequently consulted reference topics such as conditions, cover, concentration, falling, common DC guidance, object references, house-rule references or other DM-screen material.

This is not intended to replace rulebooks or become a full rules encyclopedia. The useful interaction is rapid retrieval of a small reference the DM needs now.

---

## 4. Quick Rules Question — approved capability/placeholder

**Quick Rules Question must not be lost from future DM design.**

DM Screen reserves a small, non-dominant entry point for an AI-chatbox-like rules clarification/retrieval capability.

### Intended use

The DM should be able to ask natural-language questions in Spanish to remember or clarify rules without opening books during play. Typical future use includes:

- remembering an official rule;
- clarifying ambiguous wording;
- comparing the supported D&D 5e / D&D 5.5e SRD versions;
- asking how a campaign house rule, custom rule, homebrew rule or adopted variant modifies the official baseline;
- retrieving a rule the DM knows exists but cannot quickly locate.

It is an assistant, not a rules engine, legality enforcer, automatic adjudicator or D&D Beyond replacement.

### Existing MVP boundary remains unchanged

D-0041 and `docs/PRODUCT.md` remain controlling for MVP implementation:

- official **SRD 5.1** and **SRD 5.2.1** only;
- version/provenance-preserving SRD corpus;
- grounded retrieval before LLM clarification;
- Spanish user-facing answers with clear D&D 5e / D&D 5.5e source identity;
- no automatic campaign-house-rule awareness in MVP.

The project may ingest/store its own supported open SRD corpus for retrieval. This does not mean reproducing closed rulebook/D&D Beyond content.

### Broader intended post-MVP behavior

The future Quick Rules Question should be able to answer against a combination of:

- the supported project-controlled SRD corpus;
- DM-authored house rules;
- custom rules;
- homebrew rules/content relevant to clarification;
- campaign-adopted variant rules or overrides.

Non-SRD campaign/custom rule material is prepared and maintained through the **Desktop DM Manager**, not authored as live tablet bookkeeping.

Future answers should preserve source identity. When a campaign rule differs from an official rule, the answer should explicitly distinguish the official baseline from the rule actually used by the campaign rather than blending them into an invented rule.

The exact future retrieval/LLM architecture for mixed SRD + custom campaign rules is **Pending** and should not be designed until implementation approaches.

Quick Rules Question naturally lives on DM Screen but may later be reachable contextually from specialized Desks if that reduces navigation without duplicating the capability.

---

## 5. Stage Desk — replaces Town Desk / Hub Desk working terminology

### Operational goal

> Get the DM to the place, NPC, shop, scene, adventure/plot-hook structure or related campaign material needed **right now**, through whichever retrieval path matches what the DM currently remembers.

**Stage Desk** is the approved current name for the concept previously explored as `Town Desk` / `Hub Desk`.

`Stage` means the current broad adventure environment or operational setting, not necessarily a literal town and not necessarily one physical settlement.

A Stage may be a town/city, fort, temple complex, cave settlement, capital/metropolis, or several nearby places the DM treats as one broad roleplay/adventure environment.

### Companion to paper, not campaign wiki

Stage Desk is deliberately **not** required to replace the owner's paper adventure aid.

It may function as a digital companion, index, switchboard or dynamic appendix to paper preparation. That is a successful outcome, not a failure.

The Stage Desk must not require migration of the whole adventure/campaign into the application and must not present preparation-completeness scores/warnings.

Partial digital representation is valid by design. A scene/place may have rich digital content, a few links/notes, or merely a paper reference such as `Binder B / page 17`.

### Retrieval-first contract

The core Stage Desk requirement is **fast multi-route retrieval**.

The same canonical object should be reachable by whichever mental path the DM has available:

1. **name/search** — e.g. `Liora`, `Tercer Patio`, `Heidenreich`;
2. **area/geography** — e.g. `What is in Pórtico Alto?`;
3. **function/category** — e.g. `Where can they buy armor?`, `potion`, `lodging`;
4. **narrative context** — current adventure, act, plot hook or scene;
5. **recent/open/pinned context** — material used moments ago.

No user should be forced to traverse the "correct" hierarchy when direct search/context already identifies the desired item.

A universal Stage finder is therefore a central capability rather than a secondary convenience. Exact search UI/indexing is **Pending**.

---

## 6. Stage area navigation — digital walk-through

The owner also needs to follow the group's movement through the broad environment without relying on exact-name search.

Stage Desk therefore requires an **area/navigation view** that lets the DM browse the Stage by location and immediately see relevant places, shops/services and NPCs associated with that area.

The Stage location model is **not required to be a wireframe/topological dungeon map**. The owner's real material behaves more like a matrix/hierarchy that can be projected in several useful ways.

### One canonical Place, multiple projections

A Place should be one canonical entity with useful facets/relationships such as:

- area/district;
- function/category;
- type;
- associated NPCs;
- adventure/scene relevance;
- services/stock where applicable.

The same set of Places may therefore be browsed `By Area`, `By Function`, through search, or from a linked adventure/scene without duplicating the underlying record.

### Breadcrumbs and local browse

When inside a Place, its area breadcrumb/context should make it cheap to answer questions such as `What else is in this district?` without backing out through several screens.

Exact hierarchy depth/presentation is **Pending** and should remain permissive enough for very different Stages.

### App navigation is not fictional movement

Opening/inspecting another area or Place does **not** move the party.

A lightweight explicit party-location context may later be useful so the current area can be prioritized, but setting fictional party location must remain an intentional action. Exact party-location behavior is **Pending**.

---

## 7. Places, Shops and NPCs inside Stage Desk

### Places

A Place is a reusable campaign entity/reference, not a Desk.

A Place Brief may contain only what is useful, for example:

- short/extended presentation description;
- purpose/attitude/access;
- practical services or interactives;
- prices/stock when relevant;
- hooks;
- linked NPCs;
- contextual notes/clocks;
- paper/reference pointers.

The Stage Desk should not force Dungeon-style detail onto ordinary Places.

### Shops

A **Shop is a specialized Place**, not a separate Desk/system.

It inherits normal Place behavior and emphasizes:

- shopkeeper/associated NPCs;
- stock and/or services;
- prices or price logic;
- restrictions/requirements;
- highlighted/special inventory;
- hooks/notes.

Do not turn this into retail/inventory-management software unless later use justifies finite stock or other explicit bookkeeping. The live question is usually `What do they sell and what does it cost?`.

### NPCs

NPCs are independent reusable entities that may be linked to one or many Places, adventures/scenes and other contexts. Places **link to** NPCs; they do not own them.

Opening a Place should make its associated NPCs immediately available without forcing a separate search/navigation cycle.

The existing **Developed NPC ficha** is the authoritative rich source. Live Stage presentation may project a compact `Run NPC` / `How to run` view from useful authored fields while keeping the full NPC dossier/stat block one tap away.

NPCs may appear somewhere other than their usual associated Place without schedule-conflict or simulation warnings. The DM controls fiction.

### Future rapid NPC improvisation — deferred but protected

A future Stage capability may allow the DM to create/improvise a useful NPC with only a few taps/clicks, use it immediately, and optionally `Keep for later` for Desktop Manager cleanup/promotion.

This should follow the same **dirty live → preserve snapshot → Manager cleanup/promotion** philosophy already approved for improvised creature material.

This is a valued future direction, **not current implementation scope**.

---

## 8. Adventure / Plot Hook Scene Spine

Stage Desk does not need a complete digital copy of an adventure.

It needs the minimum structure required for the DM to **remember and retrieve the order/schema of scenes** in an adventure or plot hook.

Working concept: **Adventure Spine / Scene Spine**.

A spine may preserve:

- adventure/plot-hook identity;
- acts/sections when useful;
- ordered scenes;
- lightweight branches/alternate next scenes;
- short purpose/reminder per scene;
- links to Places, NPCs, encounters, clocks, notes or handouts;
- optional paper/document reference.

A Scene may be only a title + reminder + links, or may later contain a richer Scene Brief. Digital completeness is not required.

The Scene Spine is an orientation/retrieval structure, **not a quest-management engine, screenplay executor or mandatory workflow**.

---

## 9. Stage Desk working surface

Exact UI is not frozen, but the following direction is approved:

- **Find/search** is central;
- **Recent / Open / Pinned** material is highly valuable because live use often means returning to something used minutes ago;
- browse paths include at least **By Area** and **By Function**;
- adventure/plot-hook Scene Spine is another entry path;
- Place/NPC/Shop/Scene views reuse their own domain presentation rather than creating Stage-specific duplicate models;
- contextual Notes and Clocks remain shared capabilities;
- Encounter Readiness/Combat handoff is available where needed but does not dominate ordinary Stage navigation.

The Stage home screen does not need to be a grand campaign dashboard. Practical retrieval has priority over presenting a complete ontology of the Stage.

---

## 10. Dungeon Desk refinements after D-0068

D-0068 remains controlling for Dungeon Desk. The following owner-approved refinements are added.

### Prepared area identity is not Dungeon Turn movement zone

A prepared dungeon area/node/brief and a `zone` used as an adjudicated Dungeon Turn movement unit are related only when the DM decides they are.

Do not structurally assume `one prepared area node = one Dungeon Turn zone`.

### Areas and encounters must not be 1:1

A prepared area may have zero, one or several encounters. An encounter may begin in one area and involve/continue through several areas. Avoid ownership/schema choices that imply `one room = one encounter`.

### Open areas behave like papers on the DM table

Dungeon Desk may keep several prepared areas open in a working set, with one active/focused area.

Opening an area means putting that material on the virtual DM table. Closing removes it from the current working set only; it does not mark the area explored, cleared, abandoned or reset.

Reopening should preserve live annotations/state and, where practical, useful reading/view context.

### Zone Brief alpha organization

The approved current alpha direction for long prepared area fichas is:

- **Presentar** — establish/introduction material;
- **Interactuar** — operate/explore the place;
- **Encuentro** — stage/read encounter-related material before Combat Desk handoff.

Map/open-area navigation, Dungeon Turns, Clocks and contextual Notes remain at Dungeon Desk level rather than becoming equal content tabs inside every Zone Brief.

`Pruebas relevantes` and `Chequeos útiles` are not useful as separate live concepts and should be combined into one investigation/checks grouping.

Player-safe description/media must remain distinguishable from DM-only material.

### Encounter guidance crosses into Combat Desk

Encounter-wide `How to run the encounter` guidance is one authored source/projected capability, not duplicated independent text.

When Combat Desk is created from an encounter, relevant encounter guidance should remain exceptionally easy to reach there. This may include a compact live tactical cue layer plus the fuller authored guidance.

Creature-specific tactical guidance remains distinct from encounter-wide guidance and may surface when the relevant creature is active.

Strong product direction:

> Combat Desk is not only a tracker. It should actively remind the DM how this encounter and the currently acting creature are meant to be run.

Exact quick/full tactical-guidance UX remains **Pending**.

---

## 11. Combat Desk

The D-0068/earlier approved Combat Desk boundary remains in force.

Operational goal:

> Run and keep track of an active combat while keeping the tactical guidance needed to run that specific encounter and its creatures immediately usable.

Multiple concurrent/suspended Combat Desks remain allowed within one Workspace. Active-combat authority remains one authoritative DM device, local-first, under the existing combat architecture decisions.

Detailed Combat Desk UX remains future design work.

---

## 12. Current implementation boundary

This entire decision is **product/design discovery only**.

Current technical execution remains Phase 4A Character Foundation Closure. DM implementation remains blocked until:

- remaining Phase 4A work is completed;
- required owner/device acceptance is performed;
- blocking defects are resolved;
- the owner explicitly closes Phase 4A and authorizes DM implementation.

Do not use this decision as justification to start Stage/DM Screen/Dungeon/Combat implementation early.

---

## 13. Resume guidance for later DM design

The broad Desk taxonomy is now considered sufficiently defined to park.

When DM discovery resumes, do **not** restart the generic question `what Desks do we need?` by default.

Resume from a concrete unresolved operational slice, for example:

- exact DM Screen Party Lens/reference behavior;
- Quick Rules Question live interaction and later house-rule-aware source presentation;
- Stage Desk search/area/navigation ergonomics;
- Stage Place/Shop/NPC live ficha projections;
- Scene Spine minimum structure;
- detailed Combat Desk UX;
- any Dungeon Desk second-layer behavior revealed by playtesting.

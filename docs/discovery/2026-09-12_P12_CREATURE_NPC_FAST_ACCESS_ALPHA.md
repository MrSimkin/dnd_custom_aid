# P12 — Creature/NPC fast-access alpha exploration

**Date:** 2026-09-12  
**Status:** Discovery / recommendation; not yet approved product truth  
**Branch:** `discovery/p12-material3-audit`  
**Depends on:** D-0068, D-0069, proposed D-0071  
**Implementation boundary:** no DM product code; Phase 4A gate remains controlling

## 1. Goal of this pass

Find the smallest live interaction pattern that works for both:

- a **rich developed NPC** such as Liora Vael, with narrative preparation plus a full stat block; and
- a **lean dungeon creature** such as the Site 021 altered workers/foreman, where tactical role, behavior and stat block matter more than a rich social dossier.

A candidate that works only for one of those source shapes is rejected.

---

## 2. Alternatives considered

### Alternative A — dossier-first live view

Open the rich NPC/creature ficha as the normal live surface, perhaps with collapsible sections.

**Benefits**

- easiest mapping from Material 3;
- preserves every authored field visibly;
- little conceptual transformation.

**Problems**

- spends too much DM attention on scanning;
- makes lean creatures feel incomplete;
- encourages every NPC to acquire a giant dossier;
- conflates preparation with live operation;
- risks making Stage/Dungeon/Combat use identical even though their tasks differ;
- repeats the exact Material 3 overfitting problem that triggered this audit.

**Result:** **REJECT as the normal P12 surface.** Full dossier remains available as a deeper reference.

### Alternative B — zone-owned creature lists

Each dungeon area owns its NPC/creature records and presents them locally.

**Benefits**

- very direct when everything stays in its prepared room;
- straightforward initial UI.

**Problems**

- roaming creatures become awkward or duplicated;
- an NPC reused from Stage material risks being copied into the dungeon;
- moving between areas can accidentally become ownership transfer;
- creatures involved across several areas/encounters do not fit cleanly;
- encourages `room = creature owner` and potentially `room = encounter` assumptions already rejected by D-0069.

**Result:** **REJECT as a domain/ownership model.** Areas may project contextual links without owning the canonical entity.

### Alternative C — separate NPC/Creature Desk

Opening an NPC/creature creates a dedicated Desk.

**Benefits**

- maximum space for rich material;
- simple mental model if one treats every entity as a workspace.

**Problems**

- directly contradicts the approved purpose-specific Desk model;
- creates Desk clutter from brief inspections;
- does not solve contextual Dungeon/Stage use;
- increases navigation during live play.

**Result:** **REJECT.** Entity inspection is not itself a Desk purpose.

### Alternative D — context projection + immediate full-source escape hatch

Dungeon Desk maintains a lightweight contextual presence/reference layer. The normal live surface shows a very compact entity row and a small `run now` projection. The full prepared dossier/stat block remains one action away. If combat becomes plausible, the entity can be sent to Encounter Readiness without copying it.

**Benefits**

- respects the DM Attention Budget;
- works for rich and lean source material;
- preserves one canonical source entity;
- works for placed, roaming and unexpected entities;
- preserves distinction between exploration, staging and combat;
- permits different projections in Stage/Dungeon/Combat without separate canonical data.

**Costs / risks**

- requires deliberate projection logic rather than rendering a document verbatim;
- exact `run now` content must be learned from table use;
- poorly chosen compact fields could hide something important.

**Mitigation:** keep full source one action away, make optional sections disappear when empty, and treat exact compact field selection as an alpha/playtest question.

**Result:** **RECOMMENDED.**

---

## 3. Recommended alpha information architecture

The alpha should use **two levels**, plus immediate deeper links.

### Level 1 — compact row/card

Purpose: recognize the entity and act without opening a dossier.

Candidate content:

- name / live label;
- short role/type cue if useful;
- contextual presence/location when tracked;
- at most one high-value live cue/state if it earns the space.

Candidate quick actions should remain very few. Likely candidates are open/run-now and contextual overflow; location/staging may belong in the expanded projection rather than permanently consuming row width.

### Level 2 — `Run now` projection

Purpose: answer what the DM needs for the next few seconds/minutes.

Candidate blocks, shown only when useful:

**Portray / intent**

- one short voice/attitude cue;
- current motivation/intent;
- one high-value secret/pressure/behavior reminder where context warrants it.

**Exploration quick facts**

- only mechanics likely to matter outside full combat, e.g. senses, passive perception, movement, stealth/tracking or another exceptional capability;
- no commitment yet to a universal field list.

**Run/tactical cue**

- one concise behavior reminder when the source has it;
- creature-specific tactical guidance can remain accessible and later project into Combat Desk.

**Context**

- current/last-known area if tracked;
- prepared relationship to the focused area/encounter;
- tiny live note/state if one exists.

### Immediate deeper actions

From `Run now`, one action should reach where practical:

- **Full dossier** — rich preparation/reference;
- **Full stat block** — mechanical reference, if linked;
- **Encounter Readiness** — stage possible combat;
- **Notes** — contextual notes;
- **Presence/location** — manual move/place/untrack when useful.

No full dossier field should become permanently visible merely because it exists.

---

## 4. Recommended access paths inside Dungeon Desk

The solution should not depend on one navigation path.

### A. From the focused/open area

An area may show a compact **people/creatures context section** containing entities prepared for, placed in or currently associated with that area.

This is a projection of relationships/presence, not area ownership.

### B. Dungeon-wide quick access

Dungeon Desk needs a persistent but low-footprint way to reach relevant entities across the dungeon.

Recommended conceptual working sets:

- **Ahora** — entities in/linked to the focused live context plus explicitly pinned/current items;
- **Dungeon** — all entities currently relevant to this Dungeon Desk;
- **Recientes** — recently opened/introduced material when useful;
- **Buscar** — campaign/library retrieval.

These names are working labels only. `Ahora` should be inferred rather than manually curated where possible.

### C. Search/reuse from wider campaign material

If an existing Stage NPC such as Liora unexpectedly enters the dungeon, search/reuse the canonical NPC and attach contextual presence. Do not clone the dossier.

If the DM needs only a generic mechanical creature, allow retrieval of a reusable stat block without requiring creation of a rich narrative NPC first.

---

## 5. How the alpha handles Material 3: Liora Vael

Suppose Liora unexpectedly appears in a dungeon scene.

### What should **not** happen

Dungeon Desk should not open with all of these visible:

- physical description;
- 30-second description;
- rich description;
- attitude;
- voice;
- apparent/real identity;
- motivation;
- secret;
- wants/offers/blocks;
- access;
- best scene;
- campaign use;
- pressure;
- adventure links;
- image prompt;
- full six abilities;
- saves/skills/senses/languages;
- traits/actions/reactions/spells.

That is preparation/reference overload for the normal live moment.

### What may be useful in `Run now`

For a social/exploration moment, a projection could surface only something like:

- `Liora Vael — Primada / autoridad institucional`;
- `Voz: baja, precisa; cortesía severa`;
- `Ahora quiere: controlar el caso y su cadena de custodia`;
- a context-specific secret/pressure cue if it matters;
- relevant senses or another exploration fact only if needed.

The exact phrasing/content is not frozen. The point is that the projection is **task-shaped**, while `Ficha completa` and `Bloque completo` remain immediately available.

If violence becomes plausible, `Preparar encuentro` moves the mechanical concern to Encounter Readiness; it does not turn the Dungeon `Run now` projection into an encounter editor.

---

## 6. How the alpha handles Material 4: Site 021 creatures

The altered worker and deformed foreman are almost the opposite source shape.

Their useful live material includes:

- role;
- movement/defensive facts;
- short behavior/tactical cue;
- relation to rails/chains/edges;
- full combat mechanics when needed.

They do not need fabricated political motivations, social dossier sections or image-prompt fields merely to satisfy a universal NPC form.

A `Run now` projection before combat could therefore be mostly:

- `Obrero alterado — bruiser de empuje`;
- `Se mueve en pareja; fija blancos junto a riel/borde`;
- relevant sense/movement cue;
- `Bloque completo`;
- `Preparar encuentro`.

The same projection architecture therefore works even though the authored source is structurally different from Liora's dossier.

---

## 7. Presence/location alpha

The app should record location only when it gives immediate value.

Candidate states are intentionally minimal:

- located in a prepared area;
- dungeon-relevant but location not specifically tracked;
- no longer relevant/removed from this Dungeon context.

`Roaming` may be useful as authored/context metadata or a retrieval cue, but it should not require a simulated route.

Candidate operations:

- `Mover aquí` from the focused area;
- choose another prepared area;
- `Dejar de seguir ubicación`;
- remove from current Dungeon context.

A clock/trigger/turn may show `Patrol may move` or equivalent, but the DM chooses the actual move.

---

## 8. Unexpected / improvised creature flow

Recommended alpha flow:

1. DM opens Dungeon creature quick access.
2. Search campaign/library/recent mechanical blocks.
3. Choose an existing NPC/creature or reusable stat block.
4. Add it to current Dungeon context, optionally `here`.
5. Use `Run now` immediately.
6. If combat becomes plausible, stage it in Encounter Readiness.
7. If the resulting dirty variant becomes valuable, use the existing save-for-later lifecycle for later Manager cleanup.

The DM should not be forced through a full create-NPC wizard before the creature can exist in the moment.

---

## 9. PC group quick-reference implication

P12's PC reference should probably be a small exploration-oriented projection rather than a copy of Combat Quick Sheet.

Candidate questions it should answer:

- who notices things best without opening four sheets?;
- who has darkvision/other relevant senses?;
- who speaks this language?;
- who has a relevant skill/tool/proficiency?;
- who moves unusually fast/slow or has an unusual movement mode?;
- what stable capability matters for the current exploration situation?

This strongly overlaps with the approved Party Lens concept on DM Screen. The preferred architecture is therefore likely **one reusable derived capability projected contextually**, not a second independent Dungeon-only PC aggregation system.

Exact facts and presentation remain Pending.

---

## 10. Failure checks for later UX proposals

Reject or redesign a P12 UX proposal if it:

- requires opening the full Material 3 dossier for routine live use;
- treats all NPCs as if they have Material 3 richness;
- makes a room own an NPC/creature;
- duplicates canonical NPCs when they enter a dungeon;
- makes every creature a permanent rich library object before it can be used;
- creates a separate NPC/Creature Desk;
- duplicates Encounter Readiness patching controls;
- duplicates Combat live state;
- simulates patrol movement automatically;
- requires the DM to maintain `Here/Expected/Roaming` categories manually;
- hides the full source more than one practical action away;
- assumes digital transient PC values are current despite paper-first play;
- requires exact fields before real-table evidence exists.

---

## 11. Recommendation to owner

Adopt **Alternative D — context projection + immediate full-source escape hatch** as the P12 alpha direction.

Keep exact row fields, exact `Run now` fields, exact tablet layout and final Spanish labels Pending until a visual/interaction design pass or prototype can test them.

This resolves the Material 3 problem without discarding the useful rich dossier and without creating a parallel creature-management system.

# D-0068 — DM live Workspace, Desk model and Dungeon Desk direction

**Status:** Approved current product/design direction, with explicitly marked Proposed/Pending items below  
**Date:** 2026-09-10  
**Decision owner:** Project owner  
**Implementation status:** design/discovery only; DM implementation remains BLOCKED until Phase 4A is accepted and explicitly closed

## Purpose

This decision records the owner-approved DM live-use product direction developed while Phase 4A owner QA is temporarily unavailable. It extends, but does not implement or prematurely close, the existing DM product requirements in `docs/PRODUCT.md` and D-0024/D-0025/D-0030/D-0031/D-0033.

It deliberately separates **product/design discovery** from DM implementation. Nothing in this document authorizes Phase 4B coding before the existing Phase 4A closure gate.

Terminology such as **Workspace**, **Desk**, **Dungeon Desk**, **Town Desk**, **Event Desk**, **NPC/Shop Desk**, **Zone Brief** and similar labels is partly working terminology. Only the concepts explicitly approved below are controlling. Candidate Desk names other than Combat Desk and Dungeon Desk are not yet approved product types merely because they were mentioned during discovery.

---

## 1. Controlling live-DM principle — the application is an assistant

The DM live application is an **assistant/helper**, not another system the DM must manage while running the table.

During live play the DM's attention belongs primarily to the players, fiction and adjudication. The application must consume as little of that attention as reasonably possible.

### DM Attention Budget

For every live-DM feature, ask first:

> Can the application infer, remember, prefill, derive or postpone this instead of asking the DM to enter it now?

Preferred live interaction hierarchy:

1. **Best:** no new input; the application already knows/remembers the information.
2. **Good:** one tap/gesture.
3. **Acceptable:** a few taps for something with immediate live value.
4. **Suspicious:** typing or multi-screen navigation during play.
5. **Bad:** forms, categorization rituals, maintenance work, repeated confirmations or input whose only purpose is to keep the application internally complete.

### Consequences

- No live feature should require data entry solely so the application can be complete.
- Input during play should have immediate operational value.
- Prepared information should be consumed with minimal interaction.
- Live improvisation may be incomplete, inconsistent or ugly.
- The application must not demand that improvisation be polished before it can be used.
- Where useful information can be remembered from a prior live action, it should be remembered.
- The future desktop DM Manager is the appropriate place for deliberate preparation, cleanup, normalization, organization and promotion of interesting improvised material.

This principle is controlling over convenience features that would otherwise turn the tablet into bookkeeping.

---

## 2. Campaign → Workspace → Desks

### Approved current relationship

The current product model is:

```text
Campaign
└── Workspace
    ├── Desk
    ├── Desk
    ├── Desk
    └── Desk
```

For the current product scope, treat this as **one campaign → one live DM Workspace**.

This is a current product rule, not a structural prohibition against later evolution. The underlying design must not tie the domain so rigidly to 1:1 that later co-DM or other justified multi-workspace behavior would require disproportionate rewrite.

### Workspace meaning

The Workspace is the campaign's persistent live DM working environment. It is **not currently a formal Session object** and does not require start-session/end-session/archive-session rituals.

Formal campaign/session management may remain on paper and/or belong to the desktop DM Manager. The live Workspace exists to support the DM at the table.

### Flat Desk model

Desks are **flat within the Workspace**.

A Desk does not own another Desk hierarchically. A Desk may reference another Desk or campaign entity, but the relationship remains a contextual link rather than a parent/child tree.

This is important because live play is fluid: a dungeon situation may become a combat, a combat may become an NPC interaction, and several contexts may remain simultaneously relevant.

### Multiple concurrent Desks

The Workspace may contain multiple Desks of the same operational kind where real play requires it.

In particular, **Combat Desk is 1-to-many within a Workspace**. The application must not assume that only one combat can exist because the party may split or separate simultaneous combats may remain active/suspended.

Only one Desk being visible/focused on a device at a time is a UI concern, not a domain restriction.

### Future co-DM caveat

Current scope still has one active DM per campaign, but future evolution should be feasible. Shared campaign/Workspace state should therefore be conceptually distinguishable from purely personal/device UI state such as which panel is open, local layout proportions or temporary visual focus.

No co-DM implementation is authorized now.

---

## 3. Definition of a Desk

A **Desk** is a purpose-specific DM working environment that groups the functions, screens/views, references, shortcuts/help and operational working state needed to accomplish one coherent DM task.

A Desk is defined by its **operational purpose**, not by being one screen and not by being a campaign entity.

Examples:

- a monster is not automatically a Desk;
- an NPC is not automatically a Desk;
- a room is not automatically a Desk;
- those entities may be referenced and operated through a Desk.

The distinction between merely opening/viewing an entity and deliberately creating/keeping a Desk is important. The product should not create abandoned Desks merely because the DM inspected something briefly.

Candidate names such as Town Desk, Event Desk and NPC/Shop Desk remain discovery ideas only. They must earn existence through a coherent operational goal before becoming requirements.

---

## 4. Combat Desk current conceptual boundary

Combat Desk remains the most mature Desk concept.

### Operational goal

**Run and keep track of an active combat.**

Existing approved combat requirements remain controlling, including initiative/order, participants, individual monster/NPC live state, conditions, concentration, defeated/removed state, quick PC/creature reference, local-first DM authority and the explicit non-VTT/non-rules-engine boundary.

### Current discovery conclusion

Combat Desk is **conceptually defined enough to park during the present discovery pass**. Detailed UX remains intentionally unresolved, including exact layout, participant interactions, hidden participants, multi-combat switching, end/suspend/resume behavior and other second-layer interaction questions.

Other Desk discovery may later teach reusable navigation/reference patterns that improve Combat Desk.

---

## 5. Dungeon Desk — operational goal

Dungeon Desk is now considered a meaningful Desk candidate with a distinct live purpose.

Working operational definition:

> Dungeon Desk helps the DM operate a dynamic dungeon or equivalent dangerous bounded exploration environment: navigate its prepared structure, rapidly consume area-running material, track the minimum evolving state that matters, administer Dungeon Turns/clocks/threats, keep relevant creature/encounter material ready, and launch situations that emerge from exploration.

It is **not** a VTT, map simulator, automatic dungeon simulation engine or required campaign-management record.

The DM remains authoritative over spatial interpretation, movement, pacing, creature placement and fictional consequences.

---

## 6. Dungeon structure — topological/flowchart map

The owner normally uses a **flowchart-like map**, not a tactical/cartographic map, to represent which areas connect and how.

Dungeon Desk should support the conceptual equivalent of a topological graph:

- nodes = rooms/zones/areas/sections meaningful to exploration;
- edges = connections/passages/doors/stairs/secret paths/etc.;
- the graph communicates reachability and relationships rather than exact tactical geometry.

The application must not require tactical-square movement or automatically decide how many Dungeon Turn zones a path consumes.

### DM spatial prerogative

The owner decides in context what counts as a room, node, zone, movement or explored area. Dungeon Turn operation does **not** need the application to calculate movement through the graph.

The app only needs enough information to support the DM's own declaration that relevant movement/exploration occurred and to track activities/consequences where useful.

---

## 7. Zone/room "ficha" — prepared operational brief

The owner's existing "ficha" concept was clarified through a concrete adventure example.

The ficha is primarily a **prepared DM operational brief for running an area**, not a database-form representation and not primarily a live-state dashboard.

Working English labels such as **Zone Brief**, **Area Brief** or traditional **room key/keyed area entry** may be used during discovery; final product naming is Pending.

### Preserve authored richness

The application should not over-normalize or mutilate a rich prepared brief into dozens of mandatory tiny fields merely for schema elegance.

The concrete owner example included useful authored sections such as:

- function and atmosphere;
- 30-second paraphrase;
- 2-minute paraphrase;
- what is seen/heard/smelled;
- dimensions/spatial reading;
- cover and dangers;
- interactive elements;
- relevant clues/evidence;
- puzzles/challenges where applicable;
- useful checks;
- discoveries on success;
- likely mistakes;
- what can go wrong / failure consequences;
- connections to other areas;
- loot/findings;
- DM help if the group becomes stuck.

The example also included encounter-specific operational material directly associated with the zone:

- encounter context;
- battlemap/location description;
- dimensions/obstacles/cover/interactives;
- entrances/exits;
- real encounter purpose;
- DM tactics;
- enemy priorities/goals;
- how enemies fight;
- when they change plan;
- when they flee;
- consequences;
- loot;
- relevant creature stat blocks and tactical notes.

These sections are not all mandatory for every area. Empty/unneeded material should not produce screen clutter.

### Operational grouping rather than rigid schema

A useful current grouping hypothesis is:

- Presentation;
- Space;
- Exploration;
- Findings;
- DM help/guidance;
- Encounter readiness/guidance;
- References.

This grouping is **Proposed**, not a frozen final tab/schema design.

### Fast navigation requirement

Long prepared briefs must remain rich while being fast to use live.

The DM should be able to jump quickly between operationally meaningful sections such as description, exploration, encounter/tactics, stat blocks/references and notes **without losing context or having to scroll repeatedly through an entire long document**.

The exact UI mechanism — tabs, anchors, sticky rail, collapsible sections or another design — remains Pending.

### DM information vs player-safe information

The brief must distinguish material intended only for the DM from material safe/intended to give or show to players.

This may include separate DM/player-facing description and image treatment where useful. Exact presentation remains Pending.

### Operational DM guidance

Preparation-to-live-DM advice is valuable first-class presentation even when stored as flexible text. Examples include:

- encounter purpose;
- tactics;
- behavior changes;
- flee conditions;
- how to help if players become stuck;
- how a creature uses/suffers from PC resources;
- reminders of the intended tone/function of a location.

The live Dungeon Desk should make this material easy to reach because its value is often higher during play than another mechanical field.

---

## 8. Live annotations and notes

Prepared material and current live facts should remain distinguishable.

The application should support **lightweight live annotations/notes** without requiring a full chronological history of everything that happened.

### Unified contextual notes

Use one general note concept rather than separate incompatible Room Notes, Encounter Notes, Dungeon Notes, NPC Notes, etc.

A note may be associated with a context such as:

- Dungeon/general;
- a zone/room;
- an encounter;
- an NPC/monster/entity;
- another relevant live element.

The same Notes view may show several notes together while clearly indicating their context, e.g. `Dungeon`, `Zona 1`, `Encuentro`, `NPC`.

When created from a context, the app should infer that association by default rather than forcing the DM through categorization UI.

Example: while looking at a room, a quick note `room looted` may automatically attach to that room unless changed.

### Notes must respect the DM Attention Budget

Avoid mandatory tags, categories, priorities, due dates, folders or other maintenance rituals during play.

Free text remains available when truly useful, but common current-state changes may be faster as state marks/toggles if later approved.

### State marks — Proposed

A lightweight visible state-marker mechanism may be useful for common operational facts such as:

- explored;
- looted;
- cleared;
- danger active/resolved;
- blocked;
- secret discovered.

The mechanism is **Proposed**. The exact fixed/custom states are not yet approved and must not become an arbitrary universal taxonomy without live-use justification.

### No giant room/dungeon activity log

Do not create a forensic chronological log of every tap, room opening, monster movement or minor state edit for the DM to maintain/read.

The only live log currently considered useful is the bounded **Dungeon Turn log** described below.

---

## 9. Dungeon Turns beta and application boundary

The owner supplied a five-page beta house-rule draft for **Turnos de dungeon**.

Current rule intent:

- optional exploration procedure for dungeons/equivalent dangerous environments;
- one Dungeon Turn = 10 minutes;
- each PC declares one principal Activity each turn;
- group movement may be rapid/normal/reduced/none depending on activities;
- groups may split;
- declared conditional alternatives are allowed but do not grant two complete Activities;
- Activities are resolved sequentially for table convenience but abstracted as occurring within the same ten-minute interval;
- failed activities can create consequences other than damage;
- combat/major danger may interrupt the turn and the DM determines what completed;
- the rule is explicitly beta and intended for playtesting.

### Redeclaration is intentional

Players **redeclare Activities every Dungeon Turn**. The app must not silently carry the prior Activity forward as the current declaration.

However, the app should remember and show the prior Activity because that reduces DM/player cognitive load.

Example intent:

`Anterior: Buscar — secretos`

The previous choice is a reference/suggestion, not an automatic declaration.

### Search pressure is intentional

Searching different focuses in separate turns is intentionally allowed. Thoroughness burns Dungeon Turns and therefore creates time/pressure exposure. The application must not optimize this away with a generic `Search everything` action.

### Surprise round bridge

The owner's beta Dungeon Turn system is intended to allow a successful ambush/guard situation to open into a **surprise round** when appropriate. Exact Combat Desk handling of this house-rule bridge remains to be designed later.

### Recuperar aliento / Tratar heridas stay in the beta

`Recuperar aliento` and `Tratar heridas` are explicitly recognized as highly beta balance-sensitive Activities.

They must nevertheless remain available to the eventual test implementation because their usefulness/balance cannot be evaluated without real playtesting.

Do not remove them merely because they are uncertain.

### Proposed beta additions — not yet explicitly approved

The following assistant proposals are retained as **Proposed (`*`)**, not silently promoted to approved house rules:

- `(*) Exploración normal` — explicitly represent the ordinary up-to-2-zone exploration state already implied by the movement categories;
- `(*) Ocultarse` — stationary hiding distinct from moving silently and from preparing an ambush.

A separate `Escuchar` Activity was **not recommended** at this stage; if needed, sound/activity may fit better as an additional `Buscar` focus. This remains a proposal, not an approved rule change.

### Application does not adjudicate dungeon movement

Dungeon Desk does **not** need to calculate which node/zone a group moved through or decide whether movement is legal.

The DM decides what X explored/moved zones mean in the current fictional context.

The application only needs to help record/operate declared Activities and relevant consequences with minimal input.

---

## 10. Dungeon Turn log — bounded useful log

The earlier broad anti-log direction is refined as follows:

A **Dungeon Turn log is useful** because it helps the DM avoid losing track of Activities taken and relevant outcomes across discrete exploration turns.

This is not a general interaction/audit log.

### Desired content

Per Dungeon Turn, the application may preserve only operationally useful information such as:

- turn number;
- each PC/subgroup's declared Activity;
- optional DM-indicated movement/exploration amount if useful;
- relevant consequences/outcomes the DM actually records/applies;
- interrupted/completed/partial state where applicable.

The app should derive this log from the Dungeon Turn controller as much as possible. The DM should **not have to separately write the log**.

A turn may be marked interrupted by combat/major danger and may indicate which Activities completed if needed.

Do not pretend to reconstruct an exact chronological order inside the ten-minute abstraction.

---

## 11. Clocks/counters

Clocks are an independent reusable DM concept, not inherently part of the Dungeon Turn rule.

There may be:

- clocks that advance with Dungeon Turns;
- clocks/counters unrelated to Dungeon Turns and advanced/managed independently.

Current design direction favors **one generic clock/counter concept with different advancement sources**, rather than unrelated clock systems for every Desk.

Initial useful behavior may remain simple, e.g. manual advancement and `+1 with Dungeon Turn`, while leaving room for additive evolution if real play later needs more complex triggers.

Clocks should inform/prompt rather than silently force fiction. The DM remains able to apply, modify, delay or ignore consequences.

The same clock concept may later be useful in other Desks if those Desks are approved.

---

## 12. Creatures/NPCs inside Dungeon Desk

Dungeon Desk should provide very fast access to:

- expected/placed creatures;
- roaming creatures/NPCs;
- monsters not originally contemplated in the dungeon;
- improvised/reusable mechanical stat blocks;
- the PC group quick reference and individual PC sheets.

### Creature movement

Creature/NPC location at the dungeon topological level may be tracked manually where useful, but the app must not become an automatic patrol simulation.

The DM may deliberately move a creature closer, farther, remove it, add it or place it somewhere inconsistent with prior plans for pacing/adjudication reasons.

If a Dungeon Turn/clock implies that a creature should move, the preferred behavior is advisory/prompting, leaving the final location/action to the DM.

### PC reference

As with Combat Desk, Dungeon Desk requires quick access to:

- a compact PC-group reference;
- any specific full PC sheet.

Exact dungeon-specific quick-reference fields remain to be designed. They may emphasize exploration-relevant information rather than exactly duplicating Combat Desk's quick fields.

---

## 13. Encounter Readiness — pre-combat staging and DM improvisation

The owner clarified that encounter material in a zone brief is often a reminder such as:

> `Cultists ×4 + Priestess — keep these stat blocks on hand if combat breaks out.`

This is **not yet a Combat Desk** and not a prediction that combat must occur.

Dungeon Desk therefore needs a lightweight **Encounter Readiness / staging capability** before combat begins.

Final product naming is Pending.

### Operational goal

Help the DM rapidly prepare/cheat/improvise the mechanical composition that might become combat **without opening a formal encounter-builder workflow**.

Useful live operations include:

- change quantity;
- remove/add participants;
- duplicate;
- substitute one mechanical stat block for another;
- keep a creature/stat block in reserve;
- bring reserve material into the expected encounter;
- use recent/favorite/improvisation stat blocks;
- quick search, including CR as a retrieval aid;
- reskin/relabel mechanically useful stat blocks without requiring a permanently authored new monster.

### No automated encounter-balancing authority

This does not overturn the existing exclusion of encounter-balancing automation.

The application may expose useful facts such as CR/AC/HP or other quick mechanical information for retrieval and DM judgment, but it should not certify that an encounter is balanced or enforce legality.

The DM intentionally remains free to cheat, reskin and break normal derivation/rules assumptions.

### Base + live patches

A staged creature may conceptually be:

```text
base reusable stat block
+ optional packages
+ dirty live overrides
= staged/live variant
```

The base/library creature remains unchanged unless later explicitly edited in the Manager.

### Approved fast direct tweaks

The owner commonly performs quick pre-combat tweaks, even where derived rules no longer line up exactly. Encounter Readiness should support fast temporary overrides including at least:

- HP;
- AC;
- attack bonus;
- attack damage dice;
- attack damage bonus.

Damage type may be tweaked if useful, but the application must not require the DM to record it. The owner may intentionally remember a fictional/reskinned damage type mentally or on paper during live play.

Do not nag that attack bonus/damage/other values fail normal formula expectations; intentional DM overrides are valid.

### Small mechanical inventory

Encounter Readiness should support a small reusable inventory/library of mechanical pieces that may be rapidly added/removed from a staged creature, including:

- actions;
- traits;
- saves;
- spells/spell lists.

The goal is **quick modular patching**, not complete creature authoring during live play.

### Packages

The owner explicitly likes the **package** concept.

A package is a reusable, deliberately simple bundle of mechanical patches that can be applied rapidly to a staged/live creature.

A package may eventually contain additions, removals and small overrides, e.g. an action + trait + HP adjustment - reaction.

Packages must remain proportionate and should not become a mini class/template/inheritance rules engine.

Examples such as `Brute`, `Controller`, `Dirty Mage`, etc. are illustrative only, not approved built-in package names/content.

### Group scope

Grouped creatures create a likely need to apply a patch to one instance or to the whole group. The exact interaction is Pending, but the model must not require permanently exploding every group into separately authored monster definitions merely to modify one individual.

### Reset/reversibility

Live patches should be easily removable/resettable because experimentation is expected.

### Starting Combat Desk

Only when combat actually starts does the staged state become/populate a Combat Desk.

The new Combat Desk should inherit the relevant staged participants and temporary overrides rather than forcing reconstruction.

Dungeon Desk and Combat Desk remain flat sibling Desks that reference one another/context; neither owns the other hierarchically.

---

## 14. Dirty improvisation lifecycle and Manager boundary

**Approved principle:** live improvisation is allowed to remain ugly.

The DM may preserve an interesting improvised creature/encounter/result with a minimal `keep/save for later`-style action without first cleaning it up.

The saved material should preserve the actual dirty snapshot/provenance/overrides sufficiently for later inspection.

The future desktop DM Manager is where the owner may later:

- clean names/descriptions;
- rationalize mechanics;
- convert the result into a polished reusable creature/encounter;
- extract a useful package;
- merge/update appropriate prepared material;
- archive/delete it if it was not worth keeping.

Saving an improvisation does **not** imply that it automatically becomes polished canonical library content.

---

## 15. Triggers

Triggers are promising but remain **alpha/experimental**.

Conceptually they express a visible operational relationship such as:

`if X occurs → consider/apply Y consequence`

Examples may include:

- force door → Alarm +1;
- touch altar → possible event;
- loud combat → patrol may investigate;
- clock reaches threshold → consider consequence;
- enemy leader falls → behavior changes.

### Live behavior

Triggers should initially be advisory/manual rather than an automatic rules engine. The DM should be able to apply, modify, delay or ignore the suggested consequence.

### Authoring boundary

The owner currently expects that polished trigger configuration will **probably belong to the desktop DM Manager**, but this is not yet a final architectural/product commitment.

For alpha/playtesting before the Manager is mature, Dungeon Desk may need a crude temporary trigger-authoring capability analogous to a developer/debug/field-authoring tool.

If later hidden/removed from ordinary live UX, this early capability should not be interpreted as evidence that full trigger authoring permanently belongs on the tablet.

---

## 16. Dungeon quick DM reference

Dungeon Desk should provide an abbreviated DM reference relevant to dungeon/exploration operation, analogous in purpose to a compact contextual DM screen.

It should prioritize commonly needed exploration/action rules and house-rule reminders rather than becoming a giant static collection of every possible rule/table.

Exact contents and integration with official SRD/house-rule material remain Pending.

---

## 17. Explicit non-goals and rejected drift

For the current Dungeon Desk direction, do not assume or implement:

- tactical/VTT dungeon mapping;
- automatic movement adjudication across nodes/zones;
- automatic patrol/monster AI simulation;
- forced comprehensive live note-taking;
- giant chronological room/dungeon action logs;
- mandatory encounter-balancing judgments;
- complete creature authoring during live Encounter Readiness;
- rules-formula validation that blocks intentional DM cheating;
- requirement to polish improvised material before use/save;
- hierarchical Desk ownership;
- one-Combat-only restriction;
- formal Session-management workflow merely because the real-world game has sessions;
- permanent tablet authoring workflows that exist only because the Manager is not implemented yet.

---

## 18. Open/Pending discovery items

The following remain deliberately unresolved:

1. final product names for Workspace/Desk variants and `ficha`/Zone Brief;
2. which additional Desk types genuinely deserve existence beyond Combat/Dungeon;
3. exact navigation and layout among multiple concurrent Desks;
4. exact live presentation/navigation of long Zone Briefs;
5. final dungeon-specific PC-group quick-reference fields;
6. exact note persistence/scratch-vs-durable behavior;
7. whether state marks become a real feature and which ones merit shortcuts;
8. detailed clock model beyond the simplest currently useful behavior;
9. exact trigger model and eventual Manager/tablet authoring split;
10. exact Encounter Readiness interactions, package semantics and group-override UX;
11. exact surprise-round handoff into Combat Desk;
12. Dungeon Turn beta balance/usability, especially `Recuperar aliento` and `Tratar heridas`;
13. explicit owner decision on the proposed `(*) Exploración normal` and `(*) Ocultarse` activities;
14. later detailed Combat Desk UX.

---

## 19. Recommended discovery resume point

When DM product discovery resumes, do **not** restart from generic DM requirements.

Current best next topic:

> **Zone Brief live readability/navigation:** preserve the owner's rich prepared ficha while determining which authored blocks belong together operationally, what must be instant-access, what can be one level deeper, and what should remain free-form — all under the DM Attention Budget.

Encounter Readiness is conceptually developed enough to park temporarily, while its detailed interaction design can be revisited after broader Dungeon Desk live navigation patterns are clearer.

---

## 20. Phase boundary reminder

This document records approved product/design direction and pending discovery only.

**Do not begin DM feature implementation.**

Phase 4A remains open. The current technical execution resume point remains Increment F — Conjuros — followed by the planned owner phone interaction gate when the owner is able to perform QA.

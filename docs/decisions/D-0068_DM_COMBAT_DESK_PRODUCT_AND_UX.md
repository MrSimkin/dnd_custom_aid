# D-0068 — DM Combat Desk product and UX direction

**Status:** Owner-approved product/design baseline; implementation remains blocked by Phase 4A closure  
**Date:** 2026-09-10  
**Scope:** DM tablet live combat surface only  
**Implementation permission:** **NO** — this document preserves discovery/design while Player Phase 4A physical QA is pinned. It does not authorize DM implementation before explicit Phase 4A acceptance/closure.

## 1. Why this decision exists

The owner temporarily cannot perform the physical Player-side QA for `0.4.0-preqa.8 / 40800`. Rather than implement DM features early, the project used the waiting period to define the future DM live-combat experience.

This document consolidates the resulting owner statements, approved directions, design rationale, wireframe concepts and still-open choices so a future session does not need to reconstruct them from chat history.

The governing sequencing rule remains unchanged:

- Player Phase 4A QA is **paused/pinned, not cancelled**;
- DM-side product/design discussion is allowed;
- **no DM feature implementation begins until Phase 4A is explicitly accepted and closed by the owner**.

## 2. Real current-table workflow and problems to solve

During combat the owner currently uses several physical surfaces at once:

1. a paper initiative order;
2. a paper containing basic PC statistics;
3. one monster card/sheet per creature definition, not one duplicate card per identical creature instance;
4. a notepad for current HP and status;
5. sometimes another note for encounter-specific strategy / "rules of engagement";
6. sometimes clocks and markers.

The important practical pain points are:

- losing track of whose turn is next;
- repeatedly checking different sheets to remember what a creature/PC can do or what defenses/resistances apply;
- needing quick PC information without repeatedly asking players;
- tracking current HP/status across multiple identical creatures without duplicating their whole stat blocks;
- remembering encounter strategy while attention is divided among many things;
- supporting improvised changes during play rather than forcing pre-authored encounter truth.

The DM Combat Desk therefore exists primarily as a **memory, reference and bookkeeping assistant**, not as a battle simulator.

## 3. Product identity: a private DM desk, not a VTT

The live combat surface should feel like a better digital version of the owner's physical DM desk.

It should answer questions such as:

- Where are we in initiative?
- What does this creature/PC actually have on its sheet?
- What are the relevant stats across my whole party or creature group?
- How much damage/status has each encounter instance accumulated?
- What was my plan for this encounter?
- What clock/marker is progressing?
- How does this D&D rule work again?
- What did I just change if I made a mistake?

It should **not** attempt to run combat on behalf of the DM.

### Explicit hard non-goals

The owner deliberately does **not** want:

- tactical grids;
- battle maps;
- tokens/token movement;
- automatic movement/range computation;
- automatic targeting;
- encounter balancing/CR-budget automation;
- automatic attack/damage resolution;
- a VTT-like rules execution engine;
- rigid legality enforcement that prevents the DM from doing something;
- the DM's private working screen mirrored onto player devices.

These are deliberate product boundaries, not merely postponed implementation items.

## 4. Target device and orientation

The **DM Combat Desk is a tablet-landscape surface**.

It is not required to support:

- phone combat operation;
- tablet portrait combat operation;
- desktop live-combat parity.

This lets the design use stable spatial regions, readable 5.5e-style stat blocks, comparison tables and large touch targets without compromising for phone widths.

The exact portrait behavior is still open for later design. Reasonable candidates are orientation lock or a simple rotate-to-landscape continuity screen. No portrait DM Combat Desk should be designed merely for nominal responsiveness.

## 5. Initiative is the fixed spine

### 5.1 Always visible

The initiative tracker is the one module that remains **always visible** while the DM Combat Desk is open.

The rest of the workspace may be hidden, shown, focused or rearranged within approved layout patterns, but initiative remains available.

### 5.2 Initiative and reference selection are independent

The current initiative participant must **not drive which stat block is displayed**.

Example: it may be Liora's turn while the DM wants to inspect the Orc Shaman or Goblin in her attack range to check resistances. Selecting/opening a reference must not advance or change initiative.

This separation is fundamental:

- **Initiative** answers: "Where are we in the round?"
- **Reference Desk** answers: "What do I need to know right now?"

### 5.3 Out-of-turn actions and interruptions

Legendary actions, reactions, readied actions and similar interruptions must not be modeled as fake initiative turns.

An out-of-turn event may be recorded/nested under the current turn, but the initiative pointer stays on the original combatant until the DM explicitly advances it.

Conceptual example:

```text
24 LIORA ◀ CURRENT
   └─ ⚡ Orc Shaman — Legendary Action
   └─ ↪ Goblin — Opportunity Attack
18 Goblins ×3
15 Thoradin
12 Orc Shaman
```

The system should assist bookkeeping without enforcing the D&D action economy.

### 5.4 Hidden and uncertain participants

The DM initiative may include participants the players do not yet know exist. Hidden participants remain fully active in the DM's encounter while being excluded from any optional future player projection.

The tracker should tolerate grouped initiative, hidden entries, prepared/delayed reinforcements and other practical uncertainty without requiring a VTT simulation model.

## 6. Modular workspace beneath/beside initiative

Everything except initiative is a **workspace module** that can be shown/hidden without deleting or changing the underlying encounter state.

Confirmed modules/directions are:

- **Reference Desk**;
- **Combat State**;
- **Encounter Notes / Rules of Engagement**;
- **Clocks**;
- **Markers**;
- **Quick Rules**.

A lightweight **History / Undo** surface is a strong retained recommendation because it supports fast correction without confirmation-dialog overload.

### Visibility is not data existence

Hiding a module must never discard its data.

Examples:

- hiding Clocks does not pause/delete clocks;
- hiding Combat State does not discard HP/status;
- hiding Reference Desk does not discard pinned/open references;
- hiding Notes does not remove encounter strategy.

The encounter state and the workspace layout are therefore separate concepts.

### Layout behavior

When a module is hidden, remaining modules should use the freed space rather than leaving empty permanent holes.

The preferred direction is **constrained modular layouts**, not a free-form window manager. The app may provide a small number of useful arrangements/presets and remember the arrangement for an encounter.

## 7. Reference Desk

### 7.1 Stat-block reading model

The owner strongly likes the latest D&D 5.5e monster stat-block design. The project should use that as the primary human reading grammar rather than inventing a radically different monster combat card.

The application may provide compact/full densities or focus/split presentation later, but the underlying stat-block information and reading structure should remain recognizably 5.5e-style.

### 7.2 A PC is also a stat block from the DM perspective

For live DM reference, PCs, NPCs and monsters should participate in a common **DM reference grammar** even though their underlying domain sources differ.

A PC DM reference may expose AC, HP, movement, saves, senses, conditions, resistances, attacks/features/spells and other relevant values in a stat-block-like presentation.

This is a **presentation/reference equivalence**, not permission to duplicate or replace PC data authority. Existing character data remains canonical according to the Player-side model; encounter-specific effects/overrides belong to encounter state.

### 7.3 Independent browsing, tabs and pinning

The Reference Desk should behave more like a fast reference browser than navigation tied to initiative:

- open any PC/NPC/monster regardless of whose turn it is;
- keep useful references open/pinned;
- switch quickly among them;
- optionally show two stat blocks side by side when tablet width permits;
- focus one reference without hiding initiative;
- return to the prior workspace without losing state.

The exact tab/pinning interaction remains an implementation-design detail, but independence from initiative is confirmed.

## 8. Forest, group and tree: overview hierarchy

The owner explicitly wants to be able to see both individual stat blocks and aggregate views — sometimes the tree, sometimes the forest.

The Reference Desk therefore needs multiple zoom levels.

### Party Overview

A first-class **Party Overview** should show useful PC facts across the group at once instead of requiring the DM to open every PC individually.

Candidate/configurable columns include:

- AC;
- current/max HP where appropriate;
- passive Perception;
- movement;
- selected saving throws;
- spell save DC;
- senses;
- resistances/immunities;
- conditions/concentration;
- other DM-selected quick facts.

The exact default columns should be validated through real table use. The overview should be configurable rather than assuming every DM needs the same permanent fields.

### Creature Overview

A symmetric first-class **Creature Overview** should show encounter creatures at once, either individually or grouped by definition/type.

Example conceptual grouped view:

```text
CREATURE OVERVIEW

▼ GOBLIN ×3                      AC 15
    A      19 dmg       Prone
    B       7/14*
    C       0 dmg       Hidden 🔒

▶ ORC SHAMAN ×1                  AC 17*
    31/60* · Concentrating

▶ ASSASSIN ×1                    AC 16
    8 dmg · Hidden 🔒
```

Opening a creature group should expose its encounter instances; opening the creature definition should expose the reusable 5.5e-style stat block.

### Quick Compare

A strong retained direction is a temporary comparison mode for a specific question, for example all WIS saves, ACs, passive Perception values or resistances across PCs/creatures. This is reference assistance, not automatic targeting or tactical advice.

## 9. Reusable definition vs encounter instance

This distinction is foundational.

A reusable creature definition is not the same thing as one live encounter instance.

Example:

```text
Goblin — reusable definition/stat block
   ├─ Goblin A — encounter instance
   ├─ Goblin B — encounter instance
   └─ Goblin C — encounter instance
```

Identical creatures share one reusable stat block while each encounter instance retains independent live state such as:

- initiative participation/grouping;
- HP/damage bookkeeping;
- temporary HP;
- conditions;
- concentration where relevant;
- narrative state (active/down/dead/escaped/surrendered/etc.);
- short scratch notes;
- encounter-only overrides.

Do not clone a whole monster sheet for every identical creature merely to track separate HP/status.

## 10. DM authority and live encounter overrides

The owner deliberately DMs by "rule of cool" and by encounter pacing/entertainment. The application must be flexible enough to support deliberate behind-the-screen adjustment/fudging rather than exposing or enforcing a supposedly objective hidden monster truth.

The governing rule is:

> **Preparation provides defaults. The live encounter gives the DM final authority.**

The stat block is a reference, not a constraint.

During a live encounter the DM must be able to quickly:

- increase/decrease monster HP or effective durability;
- change/override AC;
- add/remove monsters at any time and narratively justify it later if desired;
- improvise a new attack/action/ability;
- add a completely ad-hoc combatant with partial information;
- change initiative/order/grouping;
- override status or other encounter values;
- decide when a creature is actually defeated rather than being forced by software.

### Encounter-local override layer

Improvisation should normally affect the **live encounter instance**, not silently rewrite the reusable creature definition.

Conceptually:

```text
Reusable Orc Shaman
AC 15 · HP 45 · normal actions
        ↓
Live Orc Shaman instance
AC 17* · effective HP 60* · improvised chandelier action
```

A later explicit action such as `Save as variant` / `Save to creature` may preserve a useful improvisation, but this must be deliberate.

### Flexible HP bookkeeping

The design should not force one exact monster-HP style. Useful candidate modes retained from discovery are:

- exact current/max HP (`21 / 35`);
- damage taken only (`47 dmg`) so the DM may decide when it becomes lethal;
- abstract state (`Wounded`, `Bloodied`, `Near defeat`) for intentionally fuzzy encounters.

The exact set/default remains an implementation-design choice, but rigid `HP <= 0 => dead and removed` behavior is rejected.

## 11. Combat State module

Combat State is the digital replacement for the DM's HP/status notepad. It tracks what has happened to **encounter instances** without replacing their stat blocks.

It should support fast manipulation of:

- HP or damage taken;
- temporary HP where used;
- conditions;
- concentration;
- encounter-only AC/other overrides;
- narrative state (active/down/dead/escaped/surrendered/etc.);
- a tiny freeform scratch note per participant.

Overrides should be visibly distinguishable to the DM (for example with a subtle `*` or changed-value affordance) but must not be treated as errors or trigger nagging validation.

For PCs, this module must respect the existing paper-first/digital-state authority model. A DM encounter overlay must not silently rewrite persistent Player character-sheet truth.

## 12. Encounter Notes / Rules of Engagement

Encounter Notes are not generic campaign notes. They are the DM's **encounter-specific tactical/reminder sheet**.

Typical content:

- "Shaman stays behind the guards";
- "First round: Bless, then retreat toward the stairs";
- "Goblins disengage below half strength";
- "Do not use the final spell slot until someone crosses the gate";
- "If Alarm reaches 4, consider reinforcements".

The primary purpose is to protect the DM's intended strategy from cognitive overload during combat.

A useful retained direction is the ability to pin one or more critical reminder lines so they remain visible even when the full Notes module is hidden.

Optional reminder triggers (round N, clock full, marker active, HP threshold) are plausible later enhancements, but they must remain **prompts/reminders**, never forced automation.

## 13. Clocks and Markers

These are distinct concepts.

### Clocks

Clocks represent progressive state such as:

- ritual completion;
- guards becoming alerted;
- building collapse;
- flooding;
- reinforcements approaching.

They need very fast increment/decrement interaction and should tolerate arbitrary DM-defined sizes/labels.

### Markers

Markers represent binary or small-state encounter facts such as:

- gate breached;
- alarm triggered;
- idol destroyed;
- reinforcements prepared;
- phase two active.

Both modules are hideable without changing their state.

## 14. Quick Rules: memory aid, not rules validation

The owner explicitly wants **quick rules checking** because rules-memory recall is imperfect, while rejecting forced rules validation.

The desired behavior is:

- quickly search/reference a rule (for example Grappling, concentration, legendary actions, cover);
- provide a concise official-SRD-grounded summary with identifiable D&D 5e/5.5e source/version according to existing rules-clarification policy;
- optionally open fuller reference material;
- never label the DM's chosen action as illegal or disable it merely because it differs from the official rule.

Governing principle:

> **Rules are reference material, not an execution engine.**

## 15. DM privacy and player-visible combat information

The updated baseline is **DM-private by default**.

Nothing visible on the DM Combat Desk should automatically appear on player devices, including:

- monster HP/damage;
- AC/stat overrides;
- hidden participants;
- DM notes/rules of engagement;
- clocks/markers unless explicitly shared;
- private reminders;
- improvised mechanics;
- the DM's selected stat blocks/reference history.

If a future player-facing combat projection exists, it must be an **explicit sanitized projection**, not a mirror of DM state.

Hidden active participants must be omitted from that projection.

### Player initiative remains an open product choice

The owner has **not yet decided** whether players should receive a DM-published initiative/current-turn projection at all.

Two valid future directions remain open:

1. DM tracker publishes only explicitly visible/sanitized initiative/current-turn information; or
2. Player side has a separate lightweight PC-oriented tracker that players manage themselves, independent from the DM's private tracker.

Do not bake either choice into the DM Combat Desk architecture prematurely.

This clarification supersedes older wording that could be read as requiring a player-visible DM combat projection as an already-settled UX.

## 16. Reversibility and lightweight history

A strong design recommendation retained for later implementation is **Undo-first correction** rather than confirmation dialogs around every fast combat action.

Likely valuable reversible events include:

- HP/damage adjustment;
- initiative advance/reorder;
- clock/marker changes;
- participant add/remove;
- condition changes;
- override changes.

A small collapsible event history can answer "what did I just do?" and support undo without becoming combat-history analytics.

Optional encounter snapshots (for example `Start of Round 4`) are also retained as a promising idea, especially for a DM who improvises heavily. They are not yet a required MVP behavior.

## 17. Encounter lifecycle and aftermath — direction retained, detail open

The live encounter should eventually survive interruption and resume with its working state intact: initiative position, round, combatant state, notes, clocks, markers, overrides and workspace state.

A later encounter-closing/aftermath flow should distinguish:

- encounter-only state to discard/archive;
- survivors/escaped/surrendered creatures;
- clocks/markers that persist into campaign truth;
- PC changes that must be reconciled under the existing Player/paper-first authority model;
- reusable variants intentionally saved from improvisation.

The precise lifecycle (`Prepared`, `Active`, `Paused`, `Finished`, etc.) remains to be designed when Phase 4B begins.

## 18. Leading landscape-tablet wireframe direction

The final layout is **not yet frozen**, but the strongest post-discussion candidate is a permanent initiative region plus a modular reference-dominant workspace.

Because the DM Combat Desk is tablet-landscape only, a vertical initiative rail is a strong candidate for long encounters; a horizontal tracker remains possible until the owner explicitly chooses during Phase 4B design.

Conceptual vertical-rail proposal:

```text
┌──────────────────┬───────────────────────────────────────────────────────────────┐
│ RUINED TEMPLE    │ [References ✓] [State ✓] [Notes ✓] [Clocks] [Markers] [Rules]│
│ ROUND 4          ├────────────────────────────────────┬──────────────────────────┤
│                  │ REFERENCE DESK                     │ COMBAT STATE             │
│ INITIATIVE       │                                    │                          │
│                  │ [Party] [Creatures] [Shaman] [+]  │ Goblin A                 │
│ 24  Liora      ◀ │                                    │ 19 dmg · Prone           │
│ 21  Assassin   🔒│ CREATURE OVERVIEW                  │                          │
│ 18  Goblins ×3   │ Shaman      AC17*  31/60*  WIS+5  │ Goblin B  7/14*          │
│ 15  Thoradin     │ Goblins     AC15       —    WIS-1  │ Shaman    31/60* AC17*   │
│ 12  Shaman       │ Assassin    AC16    8 dmg   WIS+3  │ Liora     42/51          │
│  9  Mirelle      │                                    │                          │
│                  │ [Open stat block] [Compare]        │                          │
│ [◀] [NEXT ▶]     │                                    │                          │
│ [⚡ INTERRUPT]    ├────────────────────────────────────┴──────────────────────────┤
│ [+ COMBATANT]    │ 📌 Shaman stays behind altar. Assassin waits for caster.     │
│ ↶ UNDO           ├───────────────────────────────────────────────────────────────┤
│                  │ Alarm ●●●○   Ritual ●●○○○○                                  │
└──────────────────┴───────────────────────────────────────────────────────────────┘
```

Reference-focused mode may temporarily collapse other modules while leaving initiative visible:

```text
┌──────────────────┬───────────────────────────────────────────────────────────────┐
│ INITIATIVE       │ ORC SHAMAN                                                    │
│ always visible   │                                                               │
│                  │                 FULL 5.5e-STYLE STAT BLOCK                    │
│ 24 Liora ◀       │                                                               │
│ 21 Assassin 🔒   │                                                               │
│ 18 Goblins ×3    │                                                               │
│ ...              │                                                 [Return desk] │
└──────────────────┴───────────────────────────────────────────────────────────────┘
```

The final wireframe should be owner-reviewed before implementation.

## 19. Conceptual state boundaries

The implementation should preserve clear authorities rather than duplicating state.

Conceptually:

```text
Reusable definitions / canonical records
  ├─ PC canonical saved character data
  ├─ NPC definitions
  └─ Creature definitions
             │
             ▼
Live encounter instances
  ├─ initiative membership/order/grouping
  ├─ combat state
  ├─ encounter-only overrides
  ├─ encounter scratch notes
  └─ hidden/revealed state
             │
             ├──────────────┐
             ▼              ▼
Encounter support       Workspace layout
  ├─ notes             ├─ module visibility
  ├─ clocks            ├─ selected references
  ├─ markers           ├─ pins/split/focus
  └─ history           └─ layout/preset

Optional future public projection
  = explicit sanitized derivation only
```

Important consequences:

- no duplicate full stat blocks for identical creature instances;
- no `DM Liora` parallel character authority;
- hiding UI modules does not change encounter data;
- encounter overrides do not silently mutate reusable definitions;
- player-visible state, if implemented, is a deliberate projection rather than shared access to the private workspace.

## 20. Confirmed principles to carry into Phase 4B

The following should be treated as the durable core of this discovery:

1. **DM authority over automation.** The live encounter must accommodate intentional improvisation and overrides.
2. **Reference over rules enforcement.** Quick Rules helps memory; it does not police the DM.
3. **Private by default.** Player visibility must be explicit and sanitized if it exists at all.
4. **Initiative always visible, but independent.** It does not dictate which reference is open.
5. **Reusable definitions are separate from encounter instances.** One Goblin stat block can back many Goblin instances.
6. **PCs, NPCs and monsters share a DM reference grammar.** A PC is effectively another stat block for DM lookup purposes, without creating duplicate PC authority.
7. **The DM can see tree or forest.** Individual stat blocks, Party Overview and Creature Overview are all first-class reference needs.
8. **The workspace is modular.** Reference Desk, Combat State, Notes, Clocks, Markers and Quick Rules may be hidden/shown independently; state survives hiding.
9. **The Combat Desk is tablet-landscape only.** Do not spend Phase 4B effort making a compromised phone/portrait live-combat desk.
10. **The product is deliberately not a VTT.** Grids/maps/tokens/targeting/balancing/rules execution are out of scope.

## 21. Open questions for Phase 4B design — do not guess

Before coding, explicitly resolve or prototype:

- vertical initiative rail vs horizontal permanent tracker;
- exact initiative row contents and group expansion behavior;
- exact treatment of delayed/reinforcement placeholders;
- exact tablet landscape size classes/minimum supported width;
- orientation lock vs rotate-to-landscape continuity screen;
- default Party Overview columns and how customization works;
- default Creature Overview columns and grouping controls;
- exact compact/full 5.5e stat-block density;
- tab/pin/recent-reference interaction and maximum simultaneous split references;
- exact monster HP bookkeeping modes/defaults;
- which combat-state fields should be structured vs scratch text;
- whether/when pinned notes support optional triggers;
- exact clocks and marker editing semantics;
- Undo/history scope and whether snapshots enter first scope;
- encounter lifecycle/aftermath persistence behavior;
- whether any DM initiative projection is shown to players, or whether players instead own an independent lightweight tracker.

These are preserved as design questions, not implementation license.

## 22. Relationship to existing product documents

This decision refines `docs/PRODUCT.md` sections covering DM tablet quick/full views, combat tracking and encounters.

Where older product wording implies that a player-visible DM combat projection is already mandatory, this later owner-approved decision controls: **DM-private is the baseline and the player initiative/projection model remains explicitly unresolved**.

All existing Player-side authority, local-first combat intent, reusable-definition/live-copy separation and no-VTT principles remain compatible unless a later explicit decision changes them.

## 23. Resume rule

When Phase 4A is eventually accepted and the owner authorizes Phase 4B work:

1. read this document before designing/implementing DM combat;
2. re-open the open-question list rather than guessing;
3. produce/refine tablet-landscape wireframes with the owner;
4. freeze a Phase 4B implementation plan only after those interaction choices are accepted;
5. then implement incrementally with the normal project gates.

Until then, this file is durable design truth only.
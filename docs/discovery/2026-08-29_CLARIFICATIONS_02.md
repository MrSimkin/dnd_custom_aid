# Product Discovery Clarifications — Round 2

**Date:** 2026-08-29  
**Status:** Confirmed owner decisions plus explicitly unresolved follow-up questions  
**Phase:** Phase 1 — Product Discovery and Design

## Purpose

This file records the owner's answers to the six detailed discovery topics that followed Round 1. Confirmed conclusions are promoted into the authoritative product/decision/convention files. Questions the owner explicitly did not understand or chose not to settle remain open and must not be silently implemented.

A recurring owner constraint in this round is important: this product is an **assistant for the DM and a backup/reference aid for players**, not a replacement for a VTT, Foundry, D&D Beyond, a campaign-builder platform, or a player-policing/anti-cheat system. The design should remain incrementally extensible, but future extensibility must not be used as an excuse for current scope creep.

---

## 1. Paper ↔ digital character workflow

### 1.1 Authority during play

**Confirmed:** use a split-authority model.

- The physical printed sheet is the normal primary play surface.
- The digital character is the durable backup/reference copy.
- If a player does not have access to a printer or loses/does not have the physical sheet, the phone/tablet may temporarily become the active character sheet.
- In that fallback case, the player can use the phone/tablet together with pen-and-paper notes during the session and reconcile the digital record at the end.

The application must therefore support paper-first play without requiring simultaneous paper + phone maintenance.

### 1.2 What the digital copy represents

**Confirmed:** the intended backup target is the **character as it was at the end of the latest digitally updated session**, not merely a permanent-build skeleton.

This means the digital backup may include transient-but-useful sheet state such as:

- current HP;
- current/remaining spell slots;
- inspiration;
- consumables;
- item charges;
- ammunition;
- other values that are actually represented on the character sheet and useful when reconstructing it.

### 1.3 Update timing

**Confirmed:** keep synchronization flexible.

Normal expectation:

- update at the end of a session;
- update between sessions when convenient;
- allow updates during a session if the player wants or needs to use the digital sheet;
- an automatic reminder after a session is desirable, but the product is not a tracking/enforcement system.

Do **not** require a formal ritual such as "confirm no changes" merely to satisfy the software.

### 1.4 Freshness / checkpoints

The owner likes visible freshness information in principle, but does not want a formal checkpoint/confirmation workflow to become administrative overhead.

**Current design interpretation:** show useful recency such as the last digital update/session when available, but do not require a special "confirmed checkpoint" action and do not design an intrusive stale-character warning system as a core requirement.

### 1.5 PDF export modes

**Confirmed:** users should be able to choose between at least two export intentions:

1. **Permanent/baseline data only** — useful when the user wants a clean durable character baseline.
2. **Full latest digital sheet state** — including transient values such as current HP where they are part of the stored sheet state.

### 1.6 Still open — what exact database state does PDF export read?

The owner did not understand the previous distinction between "latest confirmed checkpoint" and "whatever the database currently contains."

This must be re-explained before implementation. The real design question is whether an unfinished edit can affect a generated PDF, or whether edits are saved atomically as a completed character update/change set before they become the printable state.

No answer is assumed yet.

---

## 2. Character-change audit, correction and reversal

### 2.1 Ledger model

**Confirmed:** use compensating history rather than destructive erasure.

Example:

- Player changes Strength 16 → 18.
- DM later reverses it.
- Current value returns to 16.
- History retains the original change and the reversal as a new action.

A player undo should follow the same principle: it may be exposed as an "undo" action in the UI, but internally it should produce a compensating change rather than silently deleting history.

### 2.2 Human-readable change sets

**Confirmed:** group related edits into understandable change sets/transactions instead of presenting every database field mutation as a separate human log line.

The system may keep field-level detail underneath when technically needed, but the product must avoid enormous/noisy audit logs. This is not an anti-cheat application.

### 2.3 Visibility

**First-version decision:** audit/history is DM-only.

**Future-capability direction:** do not make the model unnecessarily incompatible with a later UX where:

- players can see their own character changes;
- DM corrections are visible to the affected player;
- the DM can attach private notes that players do not see.

### 2.4 What is audited

**Confirmed:** audit mechanical/rules-relevant character information, not ordinary cosmetic/biographical prose.

Examples include level, ability scores, HP, proficiencies, spells, equipment, money, features and other mechanically meaningful sheet data.

### 2.5 Reasons/comments

**Confirmed:** DM corrections/reversals do **not** require a reason.

Private DM notes are still a useful capability, but optional.

### 2.6 Still open — audit retention and bloat

The owner explicitly does not want history/logging to bloat storage or become one of the largest parts of the application.

The retention policy is therefore **not decided**.

Future discussion should compare a few bounded strategies, for example:

- keep all grouped mechanical change sets because personal-scale volume may remain small;
- keep detailed field deltas for a limited period and retain compact summaries longer;
- archive/compress older history;
- retain only a bounded number of historical change sets per character while preserving explicit DM corrections.

The solution should be driven by realistic data-volume estimates, not enterprise-audit assumptions.

---

## 3. Multiple characters, ownership/control and future co-DMs

### 3.1 Multiple PCs

**Confirmed:** one user/player may have multiple PCs in the same campaign, and all of those characters should be visible under that user's account.

### 3.2 Ownership vs control

**Confirmed:** ownership and current control are distinct concepts.

- Temporary control does not change ownership.
- A DM may temporarily transfer control of a sheet/character.
- A player may explicitly give/transfer a character permanently to another user.
- A DM may duplicate a character sheet when that is the practical solution.

The owner's statement that "owner never changes" is interpreted as the normal rule for temporary play/control, not as a prohibition on an explicit permanent transfer requested by the player.

### 3.3 Character lifecycle

**Confirmed:** inactive, dead, retired or otherwise non-active PCs remain preserved in the campaign rather than disappearing.

Exact lifecycle labels remain a UI/design detail for later.

### 3.4 DM multiplicity

**Confirmed for first version:** exactly one active DM per campaign.

**Extensibility requirement:** do not design the underlying campaign-role model in a way that makes future co-DM support unnecessarily difficult.

The project should prefer incremental models: first-version business rules can be narrow while underlying structures remain reasonably extensible when that does not materially increase current complexity.

What powers future co-DMs would have is intentionally deferred.

### 3.5 Still open — character without a player account

The owner did not understand the previous question.

Rephrased for later discussion:

> Can the campaign contain a **PC-style character record with no user account currently owning/playing it**?
>
> Examples: a pregenerated PC waiting for a guest, a former player's PC now temporarily run by the DM, or a spare replacement character prepared before assigning it to anyone.

No decision is recorded yet.

---

## 4. House rules and rules clarification

### 4.1 Scope and storage

The owner's current house rules are not maintained as highly structured records.

**Confirmed current direction:** start with **notes-style rules**, not a machine-readable rules engine and not mandatory structured tagging/category metadata.

A rule may belong to:

- one campaign; or
- a reusable personal rule library;

but each rule must clearly identify its scope/source. The personal reusable library is a useful future-facing capability, but it should not cause unnecessary first-version expansion.

### 4.2 Official-vs-campaign presentation

**Confirmed:** when relevant, rules clarification should be transparent rather than pretending the campaign uses one monolithic rules version.

The conceptual answer may show:

- SRD 5.1 says X;
- SRD 5.2.1 says Y;
- campaign rule says Z;
- therefore, for this campaign, Z is the applicable campaign rule.

A house-rule note should be able to indicate that it overrides/modifies an official rule. The owner would like assistance that helps identify/check the relevant official rule rather than requiring highly structured manual entry.

### 4.3 Temporary rulings

**Rejected for current scope:** a separate temporary-DM-ruling workflow that later converts into policy is unnecessary overkill for this product.

### 4.4 Permissions and player experience

**Confirmed:** only the DM creates/edits campaign house rules.

Players do not need a browsable campaign-rules library in the app. Their relevant experience is the quick **"help me clarify this"** rules-assistance flow.

### 4.5 User-facing edition labels vs technical source identity

**Confirmed:** inside code/repository/source provenance, use the official document identities:

- SRD 5.1;
- SRD 5.2.1.

In end-user Spanish UI/answers, present the familiar game-generation labels:

- **D&D 5e** for the earlier/2014-era rules generation;
- **D&D 5.5e** for the revised/2024-era rules generation.

Where source attribution/provenance matters, the underlying SRD document/version must still remain explicit internally and available to the rules-assistance logic.

### 4.6 Homebrew object types

Creating a full generalized content-management system for homebrew spells/items/classes/etc. is outside the current project scope. Monster/NPC tooling remains separately in scope because it is a direct DM preparation/combat need.

---

## 5. NPC and monster administration

### 5.1 NPC conceptual model

The owner does **not** want to classify the working experience primarily as narrative NPC / combat NPC / monster.

For NPCs, the owner's real distinction is:

1. **Quick NPC**;
2. **Developed NPC**.

The "quick" format is not intended to mean an empty/minimal name-and-note record. It should still be useful, preferably supported by a desktop/web generator/creator.

### 5.2 Quick NPC reference example

The provided quick-NPC example is a compact generated dossier containing categories such as:

- description;
- personality traits;
- ability scores;
- relationships;
- alignment tendencies;
- plot hook.

This is the kind of compact-but-meaningful approach to replicate or improve, not a bare minimal NPC stub.

### 5.3 Developed NPC reference example

The provided developed-NPC example combines a rich campaign dossier with a combat-capable stat block.

The dossier includes information such as physical description, short/rich summaries, attitude, voice, apparent vs real nature, motivation, DM secret, what the NPC wants from the party, what they can offer/block, access conditions, best scene, gameplay use, pressure type, adventure links and visual/image-prompt guidance.

The same example then includes a full mechanical block with ability scores, AC/HP/speed, saves, skills, senses/languages, challenge, traits, actions, reactions and spell/resource information.

This example is a design reference, not a requirement that every developed NPC must have every exact field forever.

### 5.4 Monster/stat-block quality

**Confirmed:** a creature/monster stat block should be capable of being as complete as a current D&D 5.5e Monster Manual-style stat block, rather than an intentionally reduced stat summary.

The provided custom-monster example includes the expected overall shape: identity/type/alignment, AC, HP, speed, abilities, saves, resistances/immunities, senses, languages, challenge, descriptive text, traits and actions.

### 5.5 Reusable library and creation workflow

**Confirmed desired capabilities:**

- personal reusable NPC/creature library independent of one campaign;
- campaigns can use/reference/copy library entries as appropriate;
- duplicate-and-modify is important for variants;
- official SRD monsters should be usable as starting templates where legally and technically possible;
- the desktop/web administration surface should include a monster creator/generator assistant;
- manual entry, duplication, structured import and paste/parse workflows are all desirable directions, though they do not all need to ship together.

### 5.6 Search/filter priorities

Explicitly requested useful find/filter fields:

- name;
- CR;
- type;
- alignment;
- environment.

### 5.7 Still open — structured stat-block granularity

The owner did not understand the previous "partly structured" question.

Rephrased for later discussion:

> Should attacks/traits/actions be stored as individual structured objects with dedicated fields (name, attack bonus, reach, damage, save DC, etc.), or is it acceptable for v1 to store each complete action/trait as a well-formatted rich-text block while the outer stat block remains fully structured?

This matters for editing/searching/automation, but it does **not** mean presenting an incomplete stat block to the DM.

No answer is assumed yet.

### 5.8 Encounter preparation needs deeper discussion

The owner often prepares encounters in advance but also improvises and even adjusts monsters after combat has started.

A saved encounter such as "4 goblins + 1 bugbear" that can feed the combat tracker sounds useful, but the owner explicitly wants to go deeper before this becomes a designed feature.

---

## 6. Combat working state

### 6.1 Product boundary

**Confirmed:** use a practical DM combat board, not a full VTT/rules engine.

Do not expand into automated attack resolution, player resource enforcement, movement/position tracking, or automatic persistent character-sheet mutation.

### 6.2 Player-visible combat information

Players should see only:

- visible participant order;
- the currently active participant;
- visible/public conditions.

The DM may hide participants. Hidden participants do not appear in the player view.

### 6.3 DM view — PCs

When a PC is active, the DM wants fast reference including at least:

- AC;
- current HP;
- saving throws.

PC current HP may be tracked by the DM, but this must be **optional/not forced** because paper remains the primary player workflow.

### 6.4 DM view — NPCs/monsters

When an NPC/monster is active, the DM should have access to the full stat block plus combat working state.

**Confirmed tracked state:**

- current HP;
- conditions;
- concentration;
- temporary HP;
- defeated/removed status as appropriate;
- short notes where useful.

The DM must be able to override/"cheat" current monster HP during play—for example, increase a creature's HP to adjust the encounter—without the application fighting the DM.

### 6.5 Explicitly not tracked in first scope

- death saves;
- player spell slots;
- player class resources;
- automatic rules enforcement;
- automatic persistent inventory/resource consumption.

### 6.6 Group initiative

**Confirmed:** same-type/same-group creatures normally share initiative. This matches the owner's real table practice roughly 95% of the time.

Within a shared initiative group, individual creatures still need separate HP/status.

Exceptionally, the DM must be able to remove/split one creature from the group and give it an individual initiative position for narrative reasons.

### 6.7 Persistence and network behavior

**Confirmed:** active encounter state must survive:

- app closing;
- tablet restarting;
- Internet loss;
- pausing until the next week's session.

The DM tracker is authoritative. If Internet access fails, the DM must continue locally. Player devices may temporarily stop receiving updates; when connectivity returns, they can receive the current DM-controlled state again.

### 6.8 Combat vs character-sheet persistence

**Confirmed:** live combat state is separate from the persistent character sheet.

Combat does not automatically mutate the durable player character record. The player later reconciles persistent/end-of-session consequences into the digital character sheet.

### 6.9 Combat history

**Not needed now:** do not build historical encounter logging as a current requirement. The design should not make future evolution impossible, but active scope needs persistence/recovery, not retrospective combat analytics.

### 6.10 Still open — prepared encounters

The owner did not understand the earlier question asking whether prepared encounters can be launched from desktop/tablet, while separately agreeing that saved encounter compositions sound useful.

Rephrased for later discussion:

> Before the session, could the DM create an encounter record containing its intended monsters/NPCs (for example, 4 goblins + 1 bugbear), then at the table press **Start/Load encounter** so those creatures are copied into the live combat tracker instead of being added one by one?

Because the owner also improvises and modifies encounters during combat, any future design must allow easy add/remove/change after launch.

No final workflow is assumed yet.

---

## 7. Cross-cutting state model

The round strongly confirms the usefulness of separating three concepts:

1. **Durable campaign/character state** — character sheet, ownership, campaign membership, house-rule notes, reusable creature/NPC data.
2. **Bounded history/audit** — grouped mechanical character changes and compensating corrections/reversals; retention strategy still pending.
3. **Live-session working state** — initiative, active turn, monster HP, conditions and similar combat scratchpad state, persisted for recovery/continuation but kept conceptually separate from the durable character sheet.

This separation is an organizing principle, not a mandate to build enterprise event sourcing or exhaustive logs.

---

## 8. Remaining questions after Round 2

The following are the highest-value next discussions:

1. **PDF/export state semantics:** explain atomic saved update vs unfinished current edit and decide what export reads.
2. **Audit retention/bloat:** choose a bounded history strategy after realistic volume estimates.
3. **Unassigned PC records:** decide whether PC-style characters may exist in a campaign before/without a player account.
4. **Stat-block internal granularity:** structured actions/traits vs rich-text action blocks while preserving complete presentation.
5. **Prepared encounter workflow:** explore how encounter templates should interact with improvisation and live combat.
6. **MVP boundary:** after these core data/workflow questions are clearer, decide which confirmed capabilities belong in the first usable release versus later increments.

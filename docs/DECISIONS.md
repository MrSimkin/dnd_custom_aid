# Decision Log

This file records durable project decisions. A decision is authoritative only when its status is **Approved** (or **Superseded** by a newer approved decision).

## Status meanings

- **Approved** — explicitly accepted by the owner and currently authoritative.
- **Proposed** — recommended but not yet accepted.
- **Pending** — requires an owner decision.
- **Superseded** — replaced by a newer approved decision.
- **Rejected** — explicitly declined.

---

## D-0001 — Android is the target platform

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The product will be an Android application.

### Consequences

- Development decisions must prioritize Android compatibility.
- No iOS, web, or desktop version should be assumed unless separately approved later.

---

## D-0002 — Phone and tablet support

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The application must be designed for use on both Android phones and Android tablets.

### Consequences

- UI decisions must consider more than one screen class.
- Tablet behavior must not be treated as an afterthought once UI implementation begins.

---

## D-0003 — Player and Dungeon Master audiences

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The application is intended to serve both players and Dungeon Masters.

### Consequences

- Future discovery must identify which functions are shared and which differ by role.
- No permissions or role-switching behavior is implied yet.

---

## D-0004 — Repository-based continuity

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The repository must contain the information needed for any future chat, AI, coding agent, or human contributor to understand the current project state and continue from there.

### Consequences

- Chat memory is not sufficient as the project record.
- State, decisions, unresolved questions, testing status, rationale, conventions, and next actions must be kept in repository files.
- Significant sessions must leave a durable handoff.

---

## D-0005 — AI/agents perform technical execution

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The owner is not expected to perform the coding. AI/coding agents may implement code, run tests/checks, diagnose issues, refactor, and carry out approved feature changes.

### Consequences

- Repository instructions must be understandable to agents as well as humans.
- Build/test execution should be automatable wherever practical.
- Technical documentation must be sufficient to reproduce development work.
- Agents should carry the implementation burden rather than shifting routine coding work back to the owner.

---

## D-0006 — Significant product decisions remain with the owner

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

Significant product decisions are made by the owner. Agents may recommend and explain options, but should not silently turn an unresolved product choice into an implementation assumption.

### Consequences

- Important unknowns are recorded as pending decisions.
- Explanations should respect that the owner understands programming concepts but is not a professional software developer.
- Agents should be clear and educational without unnecessary jargon or unnecessary oversimplification.
- Reversible low-level implementation choices may be made during approved implementation, but consequential choices must be surfaced.

---

## D-0007 — `main` is the canonical accepted branch

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

`main` represents the latest accepted project state. Substantial work is prepared on focused branches and merged after owner review/approval, unless the owner has explicitly delegated a category of change or requested a direct `main` change.

### Consequences

- A fresh agent can distinguish accepted project truth from experiments or work in progress.
- Unmerged branches must not be treated as canonical state.
- The repository state documentation must identify active work branches when relevant.

---

## D-0008 — Routine implementation autonomy with owner visibility

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

Agents may make routine, reversible, low-level implementation choices that do not materially change approved product behavior, while significant product, UX, data/privacy, service/cost, compatibility, rules-content, and expensive-to-reverse architecture decisions remain owner-controlled.

This autonomy does **not** mean silent implementation. The agent must explain what meaningful technical work it is doing and why. When a technical choice establishes a convention or has meaningful future consequences, the owner must be involved before that choice becomes durable project practice.

### Convention rule

When a convention first becomes relevant and no approved convention exists, the agent should:

1. explain the realistic alternatives and consequences;
2. recommend an option;
3. ask the owner to approve or modify it;
4. record the approved convention in Git;
5. follow that convention thereafter without repeatedly asking the same question unless a change is justified.

### Consequences

- The owner is not asked to approve every variable, helper, or equivalent line-level implementation detail.
- The owner remains informed about the approach being taken.
- Durable project conventions are owner-reviewed and repository-recorded.

---

## D-0009 — Android implementation technology

**Status:** Pending  
**Date:** 2026-08-28

No framework, language, UI toolkit, persistence approach, or minimum Android version has been chosen yet.

This decision is intentionally deferred until the product and interaction design is sufficiently understood and approved. See D-0011.

### Decision should eventually consider

- the approved product/design requirements;
- long-term maintainability;
- quality of phone/tablet support;
- ease of automated testing;
- maturity and documentation;
- suitability for AI-assisted development;
- offline/local-data needs once known;
- cost and lock-in;
- reversibility.

---

## D-0010 — Initial product scope / MVP

**Status:** Pending  
**Date:** 2026-08-28

The first usable feature set has not yet been defined.

### Decision should follow

A guided discovery and design process with the owner. Features and alternatives should be discussed before they are turned into implementation requirements.

---

## D-0011 — Design before technology stack

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The project will not choose the Android technology stack first. Product purpose, user workflows, behavior, relevant alternatives, and interaction/design direction must be explored and discussed with the owner before stack and architecture selection.

### Required sequence

1. Understand the intended product and users.
2. Explore realistic product/interaction alternatives with the owner.
3. Design the intended behavior and experience collaboratively.
4. Record approved design/product decisions and unresolved questions in Git.
5. Evaluate stack/architecture options against that design.
6. Explain technical alternatives, trade-offs, and a recommendation.
7. Obtain owner approval before locking in consequential stack/architecture choices.

### Consequences

- No framework, language, UI toolkit, persistence layer, sync architecture, or foundational service is to be selected merely to get coding started.
- Technical architecture must serve the approved design rather than force the design to fit an arbitrary early stack choice.

---

## D-0012 — Git is the operative memory

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

All operative memory required to continue the project must live in the Git repository.

### Operative memory includes, when relevant

- approved and pending decisions;
- project conventions;
- current implementation state;
- active branches/work items;
- important rationale and trade-offs;
- test/verification results;
- known defects, risks, and blockers;
- unresolved questions;
- next actions and handoff information.

### Consequences

- Chat history may assist discussion, but it is not authoritative project memory.
- A meaningful fact that exists only in a conversation is not safely persisted until it is recorded in Git.
- At the end of meaningful work, repository documentation must be updated so a fresh agent can continue without the previous conversation.

---

## D-0013 — Discovery input is exploratory until explicitly confirmed

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

During product discovery, the owner may intentionally describe ideas in an unstructured, exploratory way. Such statements are discovery material, not automatically approved requirements.

The agent/chat is expected to actively help shape those ideas rather than merely transcribe them.

### Required behavior during discovery

The agent should:

1. preserve important raw ideas in Git with an explicit provisional/discovery status;
2. reorganize them into coherent concepts and workflows;
3. ask clarifying questions;
4. identify hidden dependencies, contradictions, risks, and consequences;
5. present realistic alternatives;
6. recommend options and explain the reasoning;
7. suggest useful ideas the owner may not have considered;
8. challenge weak ideas constructively when appropriate;
9. distinguish brainstorming from approved scope;
10. obtain explicit owner confirmation before promoting a material idea into an approved requirement or decision.

### Consequences

- A future agent must not treat a discovery note as implementation authorization merely because the owner mentioned the idea.
- Discovery discussions should be collaborative and iterative.
- The repository should preserve both unresolved ideas and the later approved conclusions so project evolution remains understandable.

---

## D-0014 — One user identity; campaign-scoped roles

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

A person has one user account/identity. Player/DM role is associated with campaign participation rather than being a permanent account type.

### Consequences

- The same user may participate differently in different campaigns.
- Authentication identity should be separated conceptually from game/campaign role and permissions.
- This foundation also avoids unnecessary coupling to D&D-only account types.

---

## D-0015 — D&D first, future game-system extensibility

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

D&D is the first supported tabletop RPG because it is what the owner currently runs. A more advanced future version may support other game systems.

### Consequences

- Current work does not implement multi-system support merely for hypothetical future use.
- Shared foundations such as users/campaign membership should avoid unnecessary D&D-only assumptions when a general design is straightforward.
- D&D-specific data/behavior may still be used where it genuinely belongs.

---

## D-0016 — Player edits are audited, not approval-gated

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

Player character-sheet edits take effect without requiring DM approval first.

The DM must be able to audit player changes and must have the ability to correct or reverse inappropriate/mistaken changes.

### Consequences

- The system requires meaningful character-change history rather than storing only the latest state.
- Detailed audit semantics are defined further by D-0021.

---

## D-0017 — Mixed official rules and house rules are allowed

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

A campaign is not required to be exclusively SRD 5.1 or exclusively SRD 5.2.1. The owner's campaigns may mix rules from both generations and may use substantial house rules/homebrew.

Official SRD references must still identify whether they come from SRD 5.1 (earlier/2014-era foundation) or SRD 5.2.1 (revised/2024-era foundation).

### Consequences

- The application must not act as a strict ruleset enforcer.
- Full guided character creation/legality validation is not part of the current first-version intent.
- The desired SRD feature is quick rules clarification during play, not a D&D Beyond replacement.
- AI-assisted clarification is a candidate idea only; any eventual implementation must be evaluated later.

---

## D-0018 — Personal-use scale and no-cost hosting target

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The system is intended for personal use and a deliberately small foreseeable user/campaign scale.

Shared data should be hosted online, and normal expected use should remain within a no-cost hosted tier where practical. If the scope meaningfully grows later, hosting cost may be reconsidered.

### Consequences

- 'Free forever regardless of scope' is not a requirement.
- Backend selection is deferred until design requirements are understood.
- Neon/Postgres is a current candidate mentioned by the owner, not an approved provider.

---

## D-0019 — Desktop-friendly administration is a product need; implementation form is open

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

Campaign preparation and administration—especially NPC/monster entry and organization—should have a comfortable desktop/laptop-oriented surface.

### Not yet decided

Whether this should be implemented as:

- a native Windows application;
- a normal web application;
- a local web interface;
- another desktop-friendly approach.

### Consequences

- The product requirement is the administration workflow, not 'Windows native' itself.
- Technical/platform selection must follow design of the administration tasks.

---

## D-0020 — Paper-first play with a full end-of-session digital backup

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

The normal play workflow is paper-first. The physical sheet is the primary live play surface, while the digital character is a durable backup/reference copy.

The digital backup should be capable of representing the **full character sheet as of the latest digital update/end of session**, including transient sheet values such as current HP, remaining spell slots, consumables, charges, ammunition and similar values where applicable.

If the paper sheet is unavailable, the player may temporarily use the phone/tablet as the character sheet, supported by pen-and-paper notes, and reconcile the digital state at the end.

Updates are intentionally flexible: end of session is the normal expectation, but between-session or during-session updates are allowed. Automatic reminders are desirable; mandatory confirmation rituals are not.

### Consequences

- Do not force simultaneous paper + app bookkeeping during normal play.
- Displaying last-update/freshness information is useful, but a formal checkpoint/no-change-confirmation workflow is not required.
- PDF output should support both permanent/baseline-only output and full latest digital-sheet-state output.
- The exact save/export atomicity semantics remain open and must be clarified before implementation.

---

## D-0021 — Character audit uses grouped compensating history and is intentionally bounded in scope

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

Character corrections and undo operations use a ledger-style model: the original mechanical change remains represented and a correction/reversal creates a compensating change rather than erasing history.

Related edits are presented to humans as understandable change sets/transactions rather than a flood of individual database-field log lines.

First-version audit visibility is DM-only. The model should not unnecessarily block a later player-visible history plus DM-private-note experience.

Only mechanical/rules-relevant character information needs audit history. Cosmetic/biographical prose does not need anti-cheat-style tracking. DM correction reasons are optional, not required.

### Consequences

- A player-facing "undo" may exist, but it must not silently rewrite/delete history.
- Audit/history is for practical correction and understanding, not player policing.
- Retention/archival limits are **still pending** because the owner explicitly wants to avoid log/storage bloat.

---

## D-0022 — Character ownership, control and campaign-role model are incrementally extensible

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

A user may have multiple PCs in the same campaign. Character ownership and current control are separate concepts.

Temporary control changes do not change ownership. A DM may temporarily reassign control. A player may explicitly transfer/give a character permanently to another user. The DM may duplicate a character sheet when appropriate.

Inactive, dead and retired PCs remain preserved in the campaign.

First version supports exactly one active DM per campaign, but the underlying membership/role model should avoid making future co-DM support unnecessarily difficult.

### Consequences

- Do not hard-code campaign ownership around a single `dm_user_id` if a general membership/role representation is straightforward later at architecture time.
- Future co-DM permission levels are intentionally not designed yet.
- Whether a PC-style record may exist without any associated player account remains open.

---

## D-0023 — House rules start as notes; rules clarification shows source differences transparently

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

House rules are initially notes-style records, not a machine-readable rules engine and not a highly structured mandatory taxonomy.

A rule may be campaign-specific or come from a reusable personal rule collection, but its scope/source must be identifiable. This capability should be approached incrementally rather than used to justify a large rules-management subsystem.

When relevant, rules clarification should be able to distinguish:

- SRD 5.1 / the earlier D&D 5e-era source;
- SRD 5.2.1 / the revised D&D 5.5e-era source;
- the applicable campaign rule.

When they conflict, the answer should show the official difference and identify the campaign rule as the rule used in that campaign.

Only the DM creates/edits campaign house rules. Players do not require a browsable house-rule library; the player-facing need is quick rules clarification.

### Consequences

- A separate temporary-ruling-to-policy workflow is outside current scope.
- A house-rule note may indicate that it overrides/modifies an official rule, ideally with assistance to identify the relevant source.
- Generalized homebrew content management for spells/items/classes/etc. remains outside current scope unless separately approved.

---

## D-0024 — NPC/creature administration uses Quick/Developed NPC formats plus complete monster stat blocks

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

The owner's NPC workflow distinguishes **Quick NPCs** and **Developed NPCs**. A Quick NPC is compact but meaningful, not merely a minimal name-and-note stub. A Developed NPC can combine a rich dossier with combat information.

Monster/creature records must be capable of presenting a complete current D&D 5.5e Monster Manual-style stat block rather than an intentionally reduced combat summary.

The desktop-friendly administration surface should support a reusable personal NPC/creature library, campaign reuse/copy/reference as appropriate, duplicate-and-modify workflows, a monster creator/generator assistant, and official SRD monsters as starting templates where legally and technically possible.

Useful search/filter fields explicitly include name, CR, type, alignment and environment.

### Consequences

- Manual entry, duplication, structured import and paste/parse are all useful directions, but do not all have to ship in the same increment.
- The exact internal granularity of actions/traits (structured objects vs rich-text blocks) remains open.
- Prepared encounter templates need a dedicated follow-up discussion before promotion into a detailed requirement.

---

## D-0025 — Combat is a practical DM board with authoritative local DM state

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

The combat feature is a practical DM combat board, not a VTT or comprehensive D&D automation engine.

Player view shows visible initiative order, the active participant and visible/public conditions. The DM may hide participants.

For an active PC, the DM needs fast reference including AC, current HP and saving throws. DM tracking of PC current HP is optional, not forced.

For NPCs/monsters, the DM should have the full stat block and may track current HP, temporary HP, conditions, concentration, defeated/removed state and short working notes. The DM may manually override/adjust monster HP during play.

Same-group creatures normally share initiative while retaining individual HP/status. A creature can exceptionally be split from its group into an individual initiative position.

Active encounter state must survive app closure, tablet restart, Internet loss and pausing until a later session. The DM tracker remains authoritative and must continue locally during network loss; player synchronization may pause and recover later.

Combat working state does not automatically mutate the persistent character sheet. Players reconcile lasting/end-of-session changes separately.

### Explicit first-scope exclusions

- death-save tracking;
- player spell-slot/resource tracking;
- automatic attack/damage/rules enforcement;
- automatic persistent inventory/resource consumption;
- combat-history analytics/logging.

---

## D-0026 — Product scope favors an assistant architecture and incremental evolution without scope creep

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

The product should remain an assistant for DM play/preparation and a backup/reference mechanism for players. It is not intended to replace Foundry/VTTs, D&D Beyond, a campaign-maker platform, or normal tabletop play.

Where practical, first-version restrictions should be expressed as business/UI rules over reasonably extensible models rather than expensive hard structural dead ends. This does **not** mean implementing future features early.

A useful organizing model is:

1. durable campaign/character content;
2. bounded character-change history/corrections;
3. live-session combat working state persisted for recovery/continuation but conceptually separate from the durable character sheet.

### Consequences

- Extensibility is a design quality, not permission for scope creep.
- Do not infer enterprise event sourcing, exhaustive telemetry or full VTT state from this separation.
- Architecture selection remains deferred until product discovery is sufficiently complete.

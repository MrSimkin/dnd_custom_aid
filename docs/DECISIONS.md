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

- Role-specific workflows and permissions are now refined by later approved decisions, especially D-0014, D-0016, D-0022, D-0025 and D-0032.
- Player/DM role is campaign-scoped rather than a permanent account type.
- Future agents must use the newer specific role/permission decisions rather than treating this early audience decision as an unresolved permissions statement.

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

## D-0009 — Application architecture and implementation technology

**Status:** Pending  
**Date:** 2026-08-28

No overall implementation architecture or consequential technology stack has been selected yet.

This includes, as applicable, the Android framework/language/UI toolkit and minimum Android version, desktop/laptop administration implementation form, local/offline persistence approach, synchronization/shared-data architecture, hosted backend/provider, authentication provider, PDF generation approach, SRD retrieval/clarification architecture, and related foundational choices.

The approved product/MVP baseline is now sufficiently coherent to begin evaluating these alternatives. Architecture evaluation is the next project phase under D-0011; owner approval remains required before a consequential option becomes selected project truth or implementation is scaffolded.

### Decision should consider

- the approved product/MVP requirements;
- Android phone/tablet quality;
- desktop/laptop administration usability;
- local/offline DM combat resilience;
- synchronization/shared-data needs;
- authentication/authorization and personal-use security implications;
- PDF generation requirements;
- SRD retrieval/provenance/clarification requirements;
- long-term maintainability;
- ease of automated testing;
- maturity and documentation;
- suitability for AI-assisted development;
- personal-scale/no-cost hosting feasibility where practical;
- dependency/service lock-in;
- migration cost and reversibility.

---

## D-0010 — Initial product scope / MVP

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

The first usable release/MVP is now defined. Detailed scope is consolidated in `docs/PRODUCT.md` and further constrained by D-0027 through D-0032.

### MVP summary

- Player: manual PC character-sheet create/view/edit, PDF export, SRD-only natural-language rules clarification in Spanish.
- DM tablet: combat tracker; quick/full PC, PC-group, NPC, monster and encounter views; prepared and on-the-fly live encounters.
- DM desktop/laptop: basic administration; manual monster/NPC data entry; saved encounter preparation; minimum account/campaign/PC administration.
- One active campaign in first-version product/UI behavior without hard-coding a structural single-campaign dead end.
- Supporting account, persistence, synchronization, permissions and offline-combat functionality is included as required infrastructure.

### Explicit MVP exclusions

Guided/legal character building, house-rule-aware clarification, sophisticated NPC/monster generators, AI creature creation, advanced import/parsing, multiple active campaigns, co-DMs, combat-history analytics, automated combat/rules enforcement, automatic combat-to-character-sheet mutation, speculative sophisticated audit-retention machinery, encounter-balancing automation and additional RPG systems.

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
- Save/export semantics are resolved by D-0027.

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
- Initial retention policy is resolved by D-0028: keep complete grouped history while monitoring real growth.

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
- Unassigned PC-style records are explicitly allowed by D-0029.

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
- House-rule-aware clarification is outside the approved MVP; MVP rules clarification is SRD-only per D-0010.

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
- Initial action/trait granularity is resolved by D-0030.
- Prepared/on-the-fly encounter behavior is resolved by D-0031.
- Sophisticated generators/import/parsing remain post-MVP unless separately approved.

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
- Architecture selection follows the now-approved MVP and remains owner-controlled.

---

## D-0027 — PDF export may deliberately use unsaved edits without saving them

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

Normal PDF export uses the latest fully saved character state.

If unsaved edits exist when export is initiated, the application must warn the user that unsaved changes exist and ask whether to export anyway. If the user continues, the PDF may use the current edited/unsaved values.

Exporting those values does **not** save/commit them and does not create character audit/history entries. If the user cancels, they return to editing.

### Consequences

- `Save` and `Export` are distinct operations.
- Committed multi-field character updates should be atomic/grouped change sets.
- A one-off PDF may intentionally represent unsaved current editing state after explicit warning/confirmation.

---

## D-0028 — Keep complete grouped audit history initially and monitor real growth

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

At the expected personal-use scale, keep the complete grouped mechanical character-change history rather than prematurely deleting, summarizing, compressing or archiving it.

### Consequences

- Audit/history size should be measurable/observable enough to identify unexpected growth.
- Do not build enterprise-grade retention machinery speculatively.
- Architecture should allow later retention, summarization, archival or compression without unnecessary trauma if real measurements show a need.

---

## D-0029 — PC-style character records may exist without an assigned player account

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

A campaign may contain a PC-style character with no current player account assigned.

This covers pregenerated guest PCs, spare/replacement PCs, former-player characters, PCs temporarily run by the DM and other intentionally unassigned states.

### Consequences

- Character existence must not depend on current user assignment.
- Assignment/control is a relationship that may be absent, added or changed later without deleting the character.

---

## D-0030 — Stat-block actions/traits are structured objects with extensible formatted mechanics

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

Traits, actions and similar stat-block elements are first-class structured objects, but v1 should not decompose every mechanic into atomic rules-engine fields.

Stable/useful identity such as element name and category/type should be structured, while the complete mechanical description may remain formatted/rich text initially.

### Consequences

- Complete stat-block presentation remains required.
- Architecture/data boundaries should permit later deeper structured fields—attack bonus, reach, damage components, save DC, recharge, targets, etc.—through normal incremental migrations without fundamental monster/encounter/combat rewrites.
- Do not overengineer a speculative full rules engine now.

---

## D-0031 — Saved encounters create independent live copies; live encounters may also be created on the fly

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

A saved encounter is an optional reusable preparation/template, not the live combat state itself and not a prerequisite for combat.

Starting/loading a saved encounter creates a **separate live encounter copy**. Changes to the live copy do not automatically alter the saved template.

The DM may freely add, remove, duplicate, replace or modify participants before combat starts or at any point during combat.

The DM may also create a live encounter directly from scratch with no saved template.

### Consequences

- The live encounter/combat tracker is the core runtime concept.
- Prepared encounters are one convenient population path, not a separate combat system.
- The design must support improvisation and creatures/NPCs joining or leaving the fray during play.

---

## D-0032 — Campaign invitations and reversible moderation controls

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

Campaign membership remains DM-controlled.

The core first-version invitation direction is a revocable invitation code/link. QR may conveniently represent/share that invitation. Email invitation may be added as convenience but is not required for the core workflow. Standard email-based account/password recovery is preferred, and password recovery must not lose campaign membership/data.

DMs may revoke/regenerate invitations. Public campaign discovery and elaborate approval queues are outside the first scope.

The following moderation/control concepts are approved:

- **Freeze PC:** preserve a character but prevent normal player use/editing until unfrozen; DM may still inspect/administer it.
- **Kick user:** remove the user from the campaign while allowing future valid re-entry; their characters remain preserved and may become unassigned.
- **Ban player:** remove the user and prevent that account from rejoining that campaign until the ban is lifted; data remains preserved.
- **Freeze account:** application-wide temporary account disable while preserving data.

### Permission boundary

Campaign DMs control campaign-level PC freeze, kick and campaign ban. Application-wide account freeze belongs to application/system administration, not an ordinary campaign DM, because a user identity may participate in other campaigns.

### Consequences

- These controls are non-destructive and reversible where appropriate.
- Membership/moderation data must be separable from character preservation/ownership.
- Exact authentication/security implementation remains part of architecture evaluation, not preselected here.

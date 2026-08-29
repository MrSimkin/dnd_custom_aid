# Product Definition

This document describes the product as it is currently known. It separates approved facts from open questions so future contributors do not convert assumptions into requirements.

## 1. Approved product facts

The current product is:

- an Android application intended for both phones and tablets;
- intended for both players and Dungeon Masters;
- user-facing in Spanish;
- designed for personal/small-scale use;
- beginning with D&D because that is the owner's current game, while avoiding unnecessary core-design assumptions that would make future support for another tabletop RPG impossible;
- complemented by a desktop/laptop-friendly administration surface whose exact implementation form is still open.

The project is an **assistant**, not a replacement for paper play, Foundry/VTTs, D&D Beyond, or a generalized campaign-building platform.

## 2. Approved discovery/design approach

The project will **design before choosing the technology stack**.

During discovery, owner brainstorming is exploratory until clarified and confirmed. The agent is expected to reorganize ideas, ask questions, identify consequences, recommend alternatives and challenge weak assumptions rather than simply transcribing them.

The owner strongly prefers incrementally evolvable systems: first-version behavior may be intentionally narrow while the underlying model avoids obvious structural dead ends when doing so does not materially increase current complexity. Extensibility must not be used as an excuse for scope creep.

See D-0011, D-0013 and D-0026.

## 3. Product purpose and three usage surfaces

The product provides a small personal digital assistant around a primarily physical tabletop workflow.

Its purpose is to reduce friction in three places:

1. **Player phone/tablet:** digital character backup/reference, temporary fallback sheet when paper is unavailable, editing/reconciliation and PDF regeneration.
2. **DM tablet:** live-session character reference and practical initiative/combat working board.
3. **Desktop/laptop administration:** comfortable campaign, NPC and monster preparation/data entry. The implementation form (native Windows, normal web app, local web interface, etc.) is not decided.

## 4. Player character workflow — paper first, digital backup

The preferred table workflow uses physical printed character sheets.

The physical sheet is normally the primary live play surface. The digital character is the durable backup/reference state.

If the physical sheet is unavailable, the player may use the phone/tablet as the active sheet for that session, supported by ordinary pen-and-paper notes, and reconcile the digital character later.

The application must not require players to maintain paper and digital state simultaneously during normal play.

### What the digital backup represents

The intended backup is the **full character sheet as of the latest digital update/end-of-session reconciliation**, not merely a permanent-build skeleton.

It may therefore include transient sheet state such as:

- current HP;
- remaining spell slots;
- inspiration;
- consumables;
- item charges;
- ammunition;
- other sheet values useful for restoring the character exactly enough to continue play.

### Update timing

End of session is the normal update point, but the system remains flexible:

- end-of-session update;
- between-session update;
- during-session update when convenient or when the digital sheet is being used.

An automatic reminder after a session is desirable. A mandatory "confirm no changes" ritual is not.

Useful recency information such as last update/session may be displayed, but freshness must not become a player-monitoring/enforcement system.

## 5. Character data and PDF output

Character data is structured information independent from any one PDF layout.

The owner already has multiple custom character-sheet designs created in Adobe InDesign. Existing PDF exports are not fillable/editable PDFs.

The PDF requirement supports:

- printable backup/recovery;
- regeneration of a lost physical sheet;
- multiple owner-provided layouts over time;
- at least two export intentions:
  1. permanent/baseline data only;
  2. full latest stored sheet state, including transient values such as current HP when present.

PDF template files belong under `assets/character-sheets/templates/`.

If implementation reveals that a template itself needs a layout change, the required owner-side Adobe InDesign change must be recorded in `assets/character-sheets/CHANGE_REQUESTS.md` and explained to the owner.

### Open PDF/save question

The exact save/export atomicity is not yet decided. Before implementation, clarify whether PDF export reads a fully committed character update/change set only, or whether partially edited current database state can ever become printable.

## 6. Character changes and DM oversight

Player changes take effect without DM pre-approval.

The DM must be able to:

- audit mechanical/rules-relevant player changes;
- understand grouped changes rather than inspect a flood of database-field logs;
- directly correct a character;
- reverse/undo an inappropriate or mistaken change.

Corrections and undo use compensating history: the original change is not silently erased; the correction creates a new history entry/change set.

First-version audit visibility is DM-only.

The data model should not unnecessarily block a later UX where players see their own change history and the DM can also maintain private notes.

DM correction/reversal reasons are optional.

### Open audit-retention question

The owner explicitly wants to avoid history/log storage bloat. The retention/archival strategy is not yet approved and should be chosen using realistic personal-scale volume estimates rather than enterprise audit assumptions.

## 7. Accounts, campaigns, characters and roles

Every user has one account/identity.

Roles are campaign-specific rather than permanent account types. A person may be a player in one campaign and a DM in another.

A user/player may have multiple PCs in the same campaign.

### Ownership vs control

Character ownership and current control are distinct:

- temporary control may be reassigned without changing ownership;
- the DM may temporarily transfer control;
- a player may explicitly give/transfer a character permanently to another user;
- the DM may duplicate a sheet/character when that is the practical workflow.

Inactive, dead and retired PCs remain preserved in the campaign.

### DM multiplicity

First version supports exactly one active DM per campaign.

The underlying membership/role model should avoid making future co-DM support unnecessarily difficult, but future co-DM permissions are not being designed now.

### Campaign enrollment

First-version enrollment remains DM-controlled through invitation mechanisms such as code, email and/or QR. Exact invitation/recovery/membership flows remain open.

### Open ownership question

Still decide whether a PC-style character record may exist in a campaign before/without any associated player account, such as a pregenerated guest PC or former player character temporarily run by the DM.

## 8. Future game-system scope

D&D is the initial supported game because it is what the owner currently runs.

Future support for additional tabletop RPG systems may be added in a more advanced version. Current development does **not** implement multi-system support now, but shared user/campaign foundations should avoid needless D&D-only coupling when a general design is straightforward.

## 9. D&D rules model and source terminology

The owner's campaigns may mix rules from both official SRD generations and may use substantial house rules/homebrew.

The application must not require one monolithic rules generation and must not act as a strict legality/rules enforcement engine.

Internal source/provenance terminology:

- **SRD 5.1** — earlier/2014-era fifth-edition foundation;
- **SRD 5.2.1** — revised/2024-era fifth-edition foundation.

User-facing Spanish presentation should use the familiar edition labels:

- **D&D 5e** for the earlier/2014-era generation;
- **D&D 5.5e** for the revised/2024-era generation.

Internally, the exact SRD source/version must still remain identifiable for retrieval, attribution and answer provenance.

## 10. House rules and rules clarification

### House-rule storage

The owner's current house rules are not highly structured.

The current product direction is therefore **notes-style rules**, not a machine-readable rule engine and not mandatory rich categorization.

A rule may be:

- campaign-specific; or
- from a reusable personal rule collection;

but its scope/source must be identifiable. The reusable library is useful, but must be added incrementally rather than becoming a large first-version rules-management subsystem.

A house-rule note may identify that it overrides/modifies an official rule, ideally with assistance to find/check the relevant official source.

Only the DM creates/edits campaign house rules.

Players do not require a browsable house-rule library.

### Rules clarification experience

The user-facing goal is quick **"help me clarify this"** assistance during play.

When relevant, an answer may transparently distinguish:

- what D&D 5e / SRD 5.1 says;
- what D&D 5.5e / SRD 5.2.1 says;
- what the campaign rule says;
- which rule applies in that campaign.

A separate temporary-ruling workflow is outside current scope.

AI-assisted clarification remains a possible implementation idea, not an approved provider/model/architecture decision. If AI is eventually used, it should be grounded in authoritative retrieved source text rather than model memory and should identify source/version limitations.

Generalized homebrew content management for spells/items/classes/etc. is outside the current project scope unless separately approved.

## 11. Character creation

Full guided character creation/building is **not part of the current first-version intent**.

It may be reconsidered later, but should not drive the current design.

## 12. DM tablet workflow — approved starting design

Tablet is the primary live-session DM surface.

The DM needs:

- access to every campaign character sheet;
- a PC group quick view;
- quick access to the currently relevant/selected PC;
- an initiative/combat tracker;
- quick access to NPC/monster stat blocks during their turns.

Initial PC quick-view information includes:

- Armor Class (AC);
- saving throws;
- proficiency bonus;
- spell save DC;
- basic/primary attack bonus or equivalent attack summary;
- ability scores;
- passive Perception.

During combat, a focused active-PC view should include at least AC, current HP and saving throws.

This quick-view design is intentionally expected to evolve through real table use.

## 13. Combat tracker — practical board, not VTT

The combat feature is a practical DM working board.

### Player view

Players see:

- visible initiative order;
- the currently active participant;
- visible/public conditions.

DM-hidden creatures do not appear.

### DM view and tracked state

The DM controls encounter/turn state.

For PCs:

- current HP may be tracked by the DM, but it is optional/not forced;
- fast PC reference remains available.

For NPCs/monsters:

- full stat block is available;
- current HP;
- temporary HP;
- conditions;
- concentration;
- defeated/removed status;
- short working notes where useful.

The DM must be able to manually override/adjust monster HP during play without the application resisting the change.

### Group initiative

Same-group creatures normally share one initiative position while each member retains individual HP/status.

The DM may exceptionally split one creature out and give it an individual initiative position.

### Persistence and offline behavior

Active encounter state must survive:

- app closure;
- tablet restart;
- Internet loss;
- pausing until a later session.

The DM tracker is authoritative. During Internet loss the DM continues locally; player synchronization may pause and recover later.

### Separation from durable character sheets

Combat working state does not automatically mutate the persistent player character sheet. Players later reconcile lasting/end-of-session changes.

### Explicit first-scope exclusions

Do not currently build:

- death-save tracking;
- player spell-slot/class-resource tracking;
- automatic attack/damage calculation;
- automatic rules enforcement;
- movement/position tracking;
- automatic persistent inventory/resource consumption;
- combat-history analytics/logging.

## 14. NPC and monster administration

### NPC types used by the owner

The owner's useful distinction is:

1. **Quick NPC**;
2. **Developed NPC**.

A Quick NPC is compact but meaningful, preferably supported by a desktop/web generator/creator. It is not merely a minimal name-and-note record.

The supplied Quick NPC reference contains categories such as description, personality traits, ability scores, relationships, alignment tendencies and a plot hook.

A Developed NPC may combine a rich narrative/campaign dossier with a combat-capable stat block. The supplied reference includes physical description, short/rich summaries, attitude, voice, apparent/real nature, motivation, DM secret, party relationship/use, access/scene/pressure/adventure-link information, visual guidance and a substantial mechanical block.

### Monster/stat-block quality

Creature/monster records must be capable of presenting a complete current D&D 5.5e Monster Manual-style stat block rather than an intentionally reduced summary.

The supplied custom-monster example follows the expected broad shape: identity/type/alignment, AC, HP, speed, abilities, saves, resistances/immunities, senses, languages, challenge, descriptive text, traits and actions.

### Reusable library and creation workflow

Desired capabilities include:

- personal reusable NPC/creature library;
- reuse/reference/copy into campaigns as appropriate;
- duplicate-and-modify variants;
- official SRD monsters as starting templates where legally/technically possible;
- desktop/web monster creator/generator assistant;
- manual entry;
- structured import;
- paste/parse workflows over time.

These do not all need to ship in one increment.

Explicitly useful search/filter fields:

- name;
- CR;
- type;
- alignment;
- environment.

### Open stat-block data question

Still decide whether attacks/traits/actions need dedicated structured subfields internally or may initially be stored as well-formatted rich-text blocks inside an otherwise structured complete stat block.

This is an internal data/editing question, not permission to present an incomplete stat block.

## 15. Encounter preparation — promising, not yet fully designed

The owner often prepares encounters in advance, but also improvises and may modify monsters after combat starts.

A saved composition such as "4 goblins + 1 bugbear" that can populate the live combat tracker sounds useful, but the workflow needs deeper discussion before it becomes a detailed requirement.

Any future design must allow easy post-launch add/remove/change and should not assume encounters are immutable plans.

## 16. Hosted/shared data

Shared campaign data should be hosted online.

For the intended personal/small-scale use, the design should aim to remain comfortably inside a no-cost hosted tier where practical. If scope grows meaningfully later, hosting cost can be reconsidered.

Neon/Postgres remains a candidate mentioned by the owner, not an approved backend/provider.

Provider evaluation must later consider:

- authentication;
- authorization/security;
- bounded audit/history needs;
- synchronization/realtime behavior;
- local/offline DM combat behavior;
- backup/recovery;
- service limits and idle behavior;
- maintenance burden;
- future cost/lock-in.

## 17. Desktop/laptop administration

Campaign preparation, NPC/monster entry, organization and generator/creator workflows need a comfortable desktop/laptop-oriented surface.

What is **not** decided is whether that surface should be:

- a native Windows client;
- a normal web application;
- a local-only web interface;
- another practical desktop-friendly approach.

Technology choice must follow workflow design.

## 18. Cross-cutting state model

The current design is usefully organized into three concepts:

1. **Durable campaign/character content** — character sheets, ownership/membership, house-rule notes, reusable NPC/creature data.
2. **Bounded audit/history** — grouped mechanical character changes plus compensating corrections/reversals; retention policy still pending.
3. **Live-session working state** — initiative, active turn, monster HP, conditions and similar DM scratchpad state, persisted for crash/offline/session continuation but conceptually separate from durable character sheets.

This is a product/data-organizing principle, not a requirement for enterprise event sourcing or exhaustive logging.

## 19. Important unresolved design questions

Current high-priority questions are:

1. **PDF/export save semantics:** what exact committed state can be exported while edits are in progress?
2. **Audit retention/bloat:** how much grouped mechanical history is retained, summarized or archived?
3. **Unassigned PC records:** may a PC-style character exist in a campaign without a current player account?
4. **Stat-block internal granularity:** structured action/trait objects vs rich-text action/trait blocks.
5. **Prepared encounter workflow:** how should saved encounter compositions launch into a flexible live tracker?
6. **Account/invitation/recovery details:** exact personal-use flows and threat model.
7. **MVP boundary:** which approved capabilities are first usable release vs later increments?
8. **Technology stack/architecture:** intentionally deferred until the above product design is sufficiently coherent.

## 20. Discovery source material

Detailed exploratory history lives under `docs/discovery/`. Those files preserve rationale, examples and unresolved thinking but do not override approved statements in this file or `docs/DECISIONS.md`.

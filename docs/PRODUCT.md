# Product Definition

This document describes the currently approved product direction. Discovery notes preserve rationale/history, but confirmed product truth belongs here and in `docs/DECISIONS.md`.

## 1. Product identity

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D.

The product is:

- primarily an Android phone/tablet application;
- intended for both players and Dungeon Masters;
- user-facing in Spanish;
- supported by a desktop/laptop-friendly DM administration surface whose exact implementation form remains open;
- designed around paper-first tabletop play rather than replacing it;
- intentionally not a Foundry/VTT, D&D Beyond replacement, generalized campaign-builder or automatic rules-enforcement engine.

D&D is the first supported system. Future additional RPG-system support may be considered, so shared foundations should avoid unnecessary D&D-only structural dead ends when a general design is straightforward. This does not mean implementing multi-system support now.

## 2. Design principle: incremental evolution without premature scope

Product/design work precedes technology-stack selection.

The owner prefers incrementally evolvable designs: first-version behavior may be intentionally narrow while the underlying model avoids obvious expensive structural dead ends when doing so does not materially increase current complexity.

Extensibility is a design quality, not permission for scope creep.

## 3. Primary usage surfaces

### Player phone/tablet

- digital character-sheet backup/reference;
- temporary active sheet when the physical copy is unavailable;
- manual character-sheet editing/reconciliation;
- PDF regeneration/export;
- SRD-grounded rules clarification.

### DM tablet

- live-session PC/NPC/monster reference;
- PC-group quick view;
- initiative/combat tracker;
- encounter quick/full views;
- live encounter creation and modification.

### DM desktop/laptop

- basic administration;
- comfortable manual NPC/monster data entry;
- saved encounter preparation;
- minimum account/campaign/PC administration required by the product workflows.

The desktop implementation form—native Windows, normal web application, local web interface or another practical desktop-friendly approach—is not selected yet.

## 4. Player character workflow — paper first, digital backup

Physical printed character sheets are the preferred normal play surface.

The digital character is a durable backup/reference copy capable of representing the **full sheet as of the latest digital update/end-of-session reconciliation**, including transient values when useful, such as:

- current HP;
- remaining spell slots;
- inspiration;
- consumables;
- item charges;
- ammunition;
- other sheet values useful for reconstruction/continuation.

The application must not require simultaneous paper + phone bookkeeping during normal play.

If paper is unavailable, the player may temporarily use the phone/tablet as the active sheet, supported by ordinary notes, and reconcile the durable digital record later.

End of session is the normal update point, but during-session and between-session updates are allowed. Useful last-update/freshness information may be shown; no mandatory "confirm no changes" ritual is required.

## 5. Character creation/editing and PDF export

MVP character creation means **manual character-sheet data entry**, not a guided/legal character builder.

Character data is structured information independent from any specific PDF layout. The owner maintains custom sheet layouts in Adobe InDesign; existing PDFs are not fillable/editable PDFs.

PDF export supports at least:

1. permanent/baseline-only output;
2. full latest digital-sheet-state output including transient values where stored.

### Saved state vs unsaved edits

`Save` and `Export` are separate operations.

Normal export uses the latest fully saved character state. If unsaved edits exist and the user starts an export, the application must warn the user that there are unsaved changes and ask whether to export anyway.

If the user continues, the PDF may use the currently edited/unsaved values. This does **not** save or commit them and does not create audit/history entries by itself.

If the user cancels, editing continues normally.

Committed multi-field character updates should be atomic/grouped change sets.

PDF template files belong under `assets/character-sheets/templates/`. If implementation reveals that a template needs an owner-side layout change, it must be recorded in `assets/character-sheets/CHANGE_REQUESTS.md` and explained to the owner.

## 6. Character changes, audit and correction

Player edits take effect without DM pre-approval.

The DM must be able to:

- audit mechanical/rules-relevant changes;
- understand related edits as grouped human-readable change sets rather than database-field noise;
- directly correct a character;
- reverse/undo inappropriate or mistaken changes.

Corrections and undo use compensating history: an earlier change is not silently erased; a later action restores/corrects state while preserving the original history.

First-version audit visibility is DM-only. DM correction reasons are optional.

### Retention policy

For now, retain the complete grouped mechanical change history. Do not prematurely delete, summarize, compress or archive history at the expected personal-use scale.

Actual audit growth should remain measurable/observable. Architecture should allow later retention, summarization, archival or compression through normal evolution if real measurements show a problem, without requiring speculative enterprise-grade retention machinery now.

## 7. Accounts, campaigns, membership and characters

Every person has one persistent user account/identity. Player/DM roles are campaign-scoped rather than permanent account types.

A user may own/control multiple PCs in one campaign.

Character ownership and current control are distinct:

- temporary control changes do not change ownership;
- a DM may temporarily reassign control;
- a player may explicitly transfer/give a character permanently to another user;
- the DM may duplicate a character where useful.

Inactive, dead and retired PCs remain preserved.

A **PC-style character may exist without any current player account assigned**. Examples include pregenerated guest PCs, spare/replacement PCs, former-player characters and characters temporarily run by the DM. Character existence therefore must not depend on current player assignment.

First version supports one active DM per campaign. The underlying role/membership model should not make future co-DM support unnecessarily difficult.

### Invitations and recovery

First-version campaign enrollment is DM-controlled.

Core direction:

- revocable invitation code/link;
- QR may conveniently represent/share that same invitation;
- email invitation may be added as convenience but is not required for the core flow;
- standard email-based account/password recovery;
- password recovery must not lose campaign membership/data;
- DM may revoke/regenerate invitations;
- no public campaign discovery or elaborate approval queue in MVP.

### Moderation/control actions

- **Freeze PC:** preserve the character but prevent normal player use/editing until unfrozen; DM may still inspect/administer it.
- **Kick user:** remove the user from the campaign while allowing later re-entry through a valid invitation; their characters remain preserved and may become unassigned.
- **Ban player:** remove the user and prevent that account from rejoining that campaign until the ban is lifted; data remains preserved.
- **Freeze account:** application-wide temporary account disable while preserving data.

Campaign DMs control campaign-level actions such as PC freeze, kick and campaign ban. Application-wide account freeze belongs to application/system administration, not an ordinary campaign DM, because the same account may participate elsewhere.

These controls are non-destructive and reversible where appropriate.

## 8. Campaign count in MVP

The MVP supports **one active campaign** in its product/UI workflow.

This is a first-version restriction, not a structural assumption that the data model can only ever represent one campaign. Future multiple-campaign support should be addable incrementally without expensive foundational redesign.

## 9. D&D rules sources and terminology

Campaigns in the broader product direction may mix both official SRD generations and house rules.

Internal/source terminology:

- **SRD 5.1** — earlier/2014-era fifth-edition foundation;
- **SRD 5.2.1** — revised/2024-era fifth-edition foundation.

User-facing Spanish presentation uses:

- **D&D 5e** for the earlier/2014-era generation;
- **D&D 5.5e** for the revised/2024-era generation.

Source provenance must remain identifiable internally.

## 10. Rules clarification

The desired experience is quick natural-language rules clarification during play, not a rules engine or D&D Beyond replacement.

### MVP rule scope

Rules clarification is part of the MVP for both players and DM.

For MVP it is **official SRD only**. A user should be able to ask a natural-language question in Spanish and receive a Spanish answer grounded only in the supported official SRD corpus, with the relevant source/version identifiable.

This specifies the product outcome, not the implementation. AI provider/model, retrieval approach and architecture remain deferred to technology evaluation.

### Broader post-MVP direction

House rules may later be stored as notes-style records rather than a machine-readable rules engine. A rule may be campaign-specific or reusable, with identifiable source/scope. Only the DM creates/edits campaign house rules.

House-rule-aware clarification and reusable house-rule libraries are outside MVP.

## 11. DM tablet quick/full views

The DM tablet surface needs quick and full access to campaign entities.

Quick views include:

- individual PC;
- PC group;
- NPC;
- monster;
- encounter.

Full views include:

- full PC sheet;
- full NPC dossier/stat information as applicable;
- full monster stat block;
- full encounter details.

Initial PC quick-reference candidates include:

- Armor Class;
- saving throws;
- proficiency bonus;
- spell save DC;
- primary/basic attack summary;
- ability scores;
- passive Perception.

During combat, focused PC reference should include at least AC, current HP and saving throws. This quick-view design is intentionally expected to evolve through real-table use.

## 12. Combat tracker — practical DM board, not VTT

The combat tracker is the central live-table MVP validation surface.

### Player-visible combat information

Players may see:

- visible initiative order;
- current active participant;
- visible/public conditions.

DM-hidden participants do not appear.

### DM working state

For PCs, current HP tracking by the DM is optional/not forced.

For NPCs/monsters, the live encounter may track:

- current HP;
- temporary HP;
- conditions;
- concentration;
- defeated/removed state;
- short working notes.

The DM may manually override/adjust monster HP during play.

### Initiative grouping

Same-group creatures normally share one initiative position while retaining individual HP/status. A creature may be split from the group into an individual initiative position when needed.

### Persistence/offline behavior

Active combat must survive:

- app closure;
- tablet restart;
- Internet loss;
- pausing until a later session.

The DM tracker remains authoritative. During Internet loss the DM continues locally; player synchronization may pause and recover later.

Combat working state does not automatically mutate persistent player character sheets. Players reconcile lasting/end-of-session character changes separately.

### Explicit first-scope exclusions

- death-save tracking;
- forced tracking of player spell slots/class resources;
- automatic attack/damage resolution;
- automatic rules enforcement;
- movement/position/VTT tracking;
- automatic persistent inventory/resource consumption;
- combat-history analytics/logging.

## 13. NPC and monster administration

The owner's NPC workflow distinguishes:

1. **Quick NPC** — compact but meaningful;
2. **Developed NPC** — richer dossier and optionally combat-capable mechanical information.

Creature/monster records must support presentation of a complete current D&D 5.5e Monster Manual-style stat block rather than a deliberately reduced summary.

Useful search/filter directions include name, CR, type, alignment and environment.

Broader desired capabilities include a reusable personal NPC/creature library, duplicate/modify variants, campaign reuse/copy/reference and official SRD starting templates where legally/technically practical.

### Stat-block internal granularity

Traits, actions and similar elements are first-class structured objects, but MVP does **not** decompose every mechanic into atomic rules-engine fields.

Stable/useful identity such as name and category/type should be structured, while the complete mechanical description may remain formatted/rich text initially.

The data/architecture boundaries should allow deeper structured fields—attack bonus, reach, damage components, save DC, recharge, targets, etc.—to be added later through normal incremental migrations without a fundamental rewrite of monsters, encounters or combat tracking.

Do not build a speculative full rules engine for this future possibility.

## 14. Encounters

A saved encounter is an optional reusable preparation/template, not the live combat state itself and not a prerequisite for combat.

### Prepared flow

1. DM creates/saves an encounter composition.
2. Starting/loading it creates a **separate live encounter copy**.
3. Changes to the live copy do not automatically modify the saved template.

The DM may freely add, remove, duplicate, replace or modify creatures/NPCs in the live encounter **before combat starts or at any point during combat**.

### On-the-fly flow

The DM may create a new live encounter directly from scratch without a saved template and add/change participants as play develops.

The live encounter/combat tracker is therefore the core runtime concept. Prepared encounters are a convenient way to populate it, not a separate combat system.

## 15. Desktop/laptop MVP administration

The first desktop administration experience should prioritize functional data entry over polish.

MVP includes:

- basic administration;
- manual monster creation/data entry;
- manual NPC creation/data entry;
- saved encounter creation/editing;
- minimum account/campaign/PC administration required by the approved workflows.

Sophisticated NPC/monster generators, AI creature creation, advanced structured import/paste parsing and similar tooling are later increments unless a limited capability proves trivial during implementation.

## 16. Shared/hosted data

Shared campaign data should be hosted online and should normally fit a no-cost hosted tier at the intended personal scale where practical.

No provider is selected. Neon/Postgres remains only a candidate mentioned during discovery.

Architecture evaluation must consider authentication/authorization, synchronization, offline DM combat continuation, backups/recovery, service limits, maintenance burden, cost/lock-in, PDF generation and SRD retrieval/clarification.

## 17. Approved MVP boundary

### Player

- manually create/view/edit PC character sheets;
- PDF export;
- SRD-only natural-language rules clarification in Spanish with identifiable official source/version.

### DM tablet/live session

- combat tracker;
- quick/full PC, PC-group, NPC, monster and encounter views;
- prepared encounter → independent live encounter copy;
- fully on-the-fly live encounters;
- free live add/remove/modify behavior within the practical combat-board scope.

### DM desktop/laptop

- basic administration;
- manual monster data entry;
- manual NPC data entry;
- saved encounter creation/editing;
- minimum account/campaign/PC administration.

### Supporting MVP functionality

- account/login/recovery;
- campaign membership/invitations and minimum moderation;
- persistence/shared data;
- permissions/ownership/control relationships;
- local/offline combat persistence;
- synchronization required for DM/player shared views.

### Explicitly outside MVP

- guided/legal character builder;
- house-rule-aware rules clarification/reusable house-rule library;
- sophisticated NPC/monster generator;
- AI creature creation;
- advanced import/paste parsing;
- multiple active campaigns in first-version UI/workflow;
- co-DMs;
- combat-history analytics;
- automated combat resolution;
- automated rules enforcement;
- automatic combat-to-character-sheet mutation;
- speculative sophisticated audit-retention machinery;
- encounter balancing/CR automation;
- additional RPG systems.

## 18. Current remaining product/technical work

The seven high-value product questions from Round 2 are resolved.

No application technology stack or architecture is selected yet. The next major step is to evaluate architecture/technology alternatives against the approved MVP and constraints, including Android phone/tablet behavior, desktop administration, offline-resilient combat, synchronization, PDF generation, SRD retrieval/clarification, maintainability, personal-scale/no-cost hosting and future incremental extensibility.

Consequential architecture/stack choices remain owner-controlled and must be approved before implementation begins.

## 19. Discovery source material

Detailed exploratory history lives under `docs/discovery/`, including `2026-08-29_CLARIFICATIONS_03.md` for the final Round 3 decisions that closed the MVP/product-question cycle. Discovery files preserve rationale and examples but do not override this approved product definition or `docs/DECISIONS.md`.
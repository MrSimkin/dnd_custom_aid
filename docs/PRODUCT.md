# Product Definition

This document describes the currently approved product direction. Discovery notes preserve rationale/history, but confirmed product truth belongs here and in `docs/DECISIONS.md`.

## 1. Product identity

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D.

The product is:

- primarily an Android phone/tablet application;
- intended for both players and Dungeon Masters;
- user-facing in Spanish;
- supported by a native desktop/laptop DM preparation/administration companion under D-0036;
- designed around paper-first tabletop play rather than replacing it;
- intentionally not a Foundry/VTT, D&D Beyond replacement, generalized campaign-builder or automatic rules-enforcement engine.

D&D is the first supported system. Future additional RPG-system support may be considered, so shared foundations should avoid unnecessary D&D-only structural dead ends when a general design is straightforward. This does not mean implementing multi-system support now.

## 2. Design principle: incremental evolution without premature scope

Product/design work precedes technology-stack selection.

The owner prefers incrementally evolvable designs: first-version behavior may be intentionally narrow while the underlying model avoids obvious expensive structural dead ends when doing so does not materially increase current complexity.

Extensibility is a design quality, not permission for scope creep.

## 3. Primary usage surfaces

Android and desktop/laptop are intentionally asymmetric surfaces for the foreseeable scope. Android is the primary at-the-table/live-use surface; desktop/laptop is primarily a DM preparation/administration companion using the same campaign/domain data rather than a separate product or data silo.

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
- minimum account/campaign/PC administration required by the product workflows;
- character-sheet PDF regeneration/export.

The desktop implementation form is selected under D-0036: a native **Kotlin + Compose Multiplatform Desktop** application, with meaningful local/offline operation where practical.

Desktop is **not required to duplicate the whole Android application** in MVP or near-term scope. No player desktop application is required in the MVP. Android/desktop feature parity is only a possible much-later evolution, not a current implementation goal. The desktop surface does **not** require the combat tracker in the MVP. If some additional Android functionality becomes naturally available on desktop at negligible cost, that does not make feature parity a requirement.

## 4. Player character workflow — paper first, digital backup

Physical printed character sheets are the preferred normal play surface.

During normal paper-first play, **paper is the authoritative live-session state**. The digital character represents the **latest intentionally saved/reconciled digital state**, which may be older than the current physical-table state.

The digital character is a durable backup/reference copy capable of representing the **full sheet as of the latest digital update/end-of-session reconciliation**, including transient values when useful, such as:

- current HP;
- remaining spell slots;
- inspiration;
- consumables;
- item charges;
- ammunition;
- other sheet values useful for reconstruction/continuation.

The application must not require simultaneous paper + phone bookkeeping during normal play and must not assume that an older digital value supersedes a newer change written only on paper.

The application should show meaningful **last updated / freshness information** so users do not mistake an old digital backup for current live truth.

If paper is unavailable, the player may temporarily use the phone/tablet as the active authoritative working sheet, supported by ordinary notes, and reconcile the durable digital record later.

When the player performs the normal end-of-session reconciliation, the resulting saved digital state becomes the new durable baseline/backup. Returning later to paper should use the latest reconciled/exported state as the starting point.

The application does **not** attempt automatic conflict merging between paper and digital because it cannot observe changes written only on paper.

End of session is the normal update point, but during-session and between-session updates are allowed. No mandatory "confirm no changes" ritual is required.

## 5. Character creation/editing and PDF export

MVP character creation means **manual character-sheet data entry**, not a guided/legal character builder.

Character data is structured information independent from any specific PDF layout. The owner maintains custom sheet layouts in Adobe InDesign; existing PDFs are not fillable/editable PDFs.

PDF export is available on both Android and the DM desktop companion under D-0040 and supports at least:

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

The MVP is **multicampaign**. Multiple campaigns may exist and be active concurrently. A user account may participate in multiple campaigns concurrently, with independent campaign-scoped roles and permissions. The application therefore requires coherent campaign selection/switching behavior in the MVP.

A user may own/control multiple PCs in one campaign.

Character ownership and current control are distinct:

- temporary control changes do not change ownership;
- a DM may temporarily reassign control;
- a player may explicitly transfer/give a character permanently to another user;
- the DM may duplicate a character where useful.

Inactive, dead and retired PCs remain preserved.

A **PC-style character may exist without any current player account assigned**. Examples include pregenerated guest PCs, spare/replacement PCs, former-player characters and characters temporarily run by the DM. Character existence therefore must not depend on current player assignment.

First version supports one active DM **per campaign**. The underlying role/membership model should not make future co-DM support unnecessarily difficult.

### Invitations and recovery

First-version campaign enrollment is DM-controlled.

- An invitation belongs to **one specific campaign** and grants no rights in any other campaign.
- A signed-in user who follows a valid invitation joins directly; there is no second DM-approval step after the invitation has been deliberately issued.
- If the person is not signed in, the application asks them to sign in/create an account and then continues the same invitation flow rather than discarding it.
- The same campaign invite code/link may be used by **multiple people** until the DM revokes/regenerates it.
- Regenerating the invitation invalidates the previous code/link.
- A banned account cannot use an otherwise valid invitation to rejoin that campaign.
- A kicked account may rejoin later through a currently valid invitation because Kick is removal, not a ban.
- Rejoining preserves identity/history continuity and must not create duplicate user/character/history records.
- Joining a campaign does **not** automatically assign ownership/control of a PC; membership and character assignment remain separate.
- MVP invitations do **not** require mandatory expiration dates. Optional expiry, one-use invites, named invites, invitation audit and similar controls may be added later if useful.
- QR is another representation/delivery form of the same invite link/code, not a separate invitation mechanism.
- Email invitation, if supported, is a delivery convenience for that same invitation rather than a different membership workflow.
- Standard email-based account/password recovery is preferred, and password recovery must not lose campaign membership/data.
- No public campaign discovery or elaborate approval queue is required in MVP.

### Moderation/control actions

Campaign moderation and application-wide account administration are separate authority layers.

A campaign DM can administer **only their campaign** and may:

- **Freeze PC:** preserve the character but prevent normal player use/editing until unfrozen; DM may still inspect/administer it;
- **Kick user:** remove the user from the campaign while allowing later re-entry through a valid invitation; their characters remain preserved and may become unassigned;
- **Ban player:** remove the user and prevent that account from rejoining that campaign until the ban is lifted; data remains preserved;
- revoke/regenerate campaign invitations.

A campaign DM **cannot freeze, delete, disable or otherwise control the user's global application account**. Campaign kick/ban does not affect the user's other campaigns. A frozen PC remains preserved and campaign-scoped.

**Freeze account** belongs only to application/system administration. It is a global, reversible disable of application login/use while preserving campaigns, characters, audit history, ownership records, memberships and other data. Restoring the account restores those preserved relationships unless an independent campaign-level ban, kick, PC freeze or other moderation state still applies.

Campaign moderation state and global account state must therefore remain separate.

At present, the project owner happens to be the **only DM and the only application administrator**. This is an operational fact, not permission to hard-code `DM = application administrator` into the domain model.

These controls are non-destructive and reversible where appropriate. Account deletion remains conceptually separate from account freeze.

## 8. Campaign count in MVP

The previous one-active-campaign MVP restriction is withdrawn.

The MVP is **multicampaign**:

- multiple campaigns may exist and be active concurrently;
- users may belong to multiple campaigns concurrently;
- characters, NPCs, encounters, memberships, permissions, audit history and other campaign-scoped data retain explicit campaign association;
- the UI includes sufficient campaign selection/switching behavior to use this capability coherently.

This scope change deliberately avoids building a temporary single-campaign restriction that would immediately need to be removed to approach the intended real product.

## 9. D&D rules sources and terminology

Campaigns may freely mix both official SRD generations and house rules/homebrew from day one. Character, NPC and monster data must not reject content merely because it is not "legal SRD". The application is not a rules enforcer.

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

For MVP it is **official SRD only**. A user should be able to ask a natural-language question in Spanish and receive a Spanish answer grounded only in the supported official SRD corpus.

The MVP assistant may answer from **both supported official SRDs** and must clearly identify whether relevant information comes from D&D 5e / SRD 5.1 or D&D 5.5e / SRD 5.2.1.

The MVP rules assistant does **not** automatically know or apply campaign house rules. If a campaign rule differs from an official SRD rule, the DM/player applies the campaign rule manually for now. The assistant must not present campaign homebrew as if it were official SRD content.

The approved technical implementation is recorded in D-0041: versioned/provenance-preserving PostgreSQL chunks, PostgreSQL full-text retrieval first, and a replaceable LLM integration initially using Cloudflare Workers AI. That technical choice does not change the product rule that answers must be grounded in supported official SRD material.

### Broader post-MVP direction

House rules may later be stored as notes-style records rather than a machine-readable rules engine. A rule may be campaign-specific or reusable, with identifiable source/scope. Only the DM creates/edits campaign house rules.

House-rule-aware clarification and reusable house-rule libraries are outside MVP. A later house-rule-aware answer may transparently distinguish official-source rules from the campaign-specific rule actually used.

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

### Persistence, authority and offline behavior

**Shared durable campaign/domain data is normally hosted and synchronized online.** An active live encounter/combat, however, has **one authoritative DM working state at a time**.

While combat is active, every DM action is committed **locally first**, so Internet loss does not interrupt play. Server synchronization is secondary and opportunistic: it provides sharing and recovery, but successful server contact is not required to continue combat.

For MVP, one DM device is authoritative for the active encounter and authoritative combat updates use a simple increasing combat sequence/version. This is sufficient to reject delayed older updates and prevents an older hosted snapshot from replacing newer local DM state. A future explicit DM-device transfer/handoff may add additional authority-generation mechanics if and when that feature exists; they are not an MVP requirement.

Player devices receive the latest successfully synchronized **public projection** of combat. If connectivity disappears, their view may become stale while the DM continues normally.

If a player loses Internet, the app may provide a tiny **ephemeral local convenience layer** over that last received projection:

- locally advance the displayed turn with **Next turn**;
- locally add or remove visible conditions.

These temporary changes are not uploaded, do not enter synchronization, and never become authoritative. When connectivity returns, the temporary player view is discarded/replaced by the latest DM public projection. Durability of those temporary tweaks across player-app restart is not required.

The DM should be able to tell whether the active combat is **saved locally**, **synced**, or **waiting to sync**.

The MVP supports persistence/recovery on the **same DM device** and synchronizes live combat to hosted storage whenever possible. Seamless simultaneous multi-device DM editing is not required. If a later version supports moving an active combat to another DM device, it should use an explicit transfer/resume or authority-handoff mechanism rather than concurrent authoritative editing.

Changes to reusable monster/NPC definitions or saved encounter templates while combat is running do not silently rewrite the independent live copy.

Combat working state does not automatically mutate persistent player character sheets, and persistent sheet changes do not automatically rewrite the combat tracker. Players reconcile lasting/end-of-session character changes separately.

**Hosted data is the durable shared home; the active DM device is the live-combat authority while running that encounter.**

### Explicit first-scope exclusions

- death-save tracking;
- forced tracking of player spell slots/class resources;
- automatic attack/damage resolution;
- automatic rules enforcement;
- movement/position/VTT tracking;
- automatic persistent inventory/resource consumption;
- combat-history analytics/logging;
- seamless concurrent multi-device DM combat editing.

## 13. NPC and monster administration

The owner's NPC workflow distinguishes:

1. **Quick NPC** — compact but meaningful;
2. **Developed NPC** — richer dossier and optionally combat-capable mechanical information.

Creature/monster records must be capable of representing and presenting the **entire current D&D 5.5e Monster Manual-style stat block**, with nothing important omitted.

Core stable fields should be structured where useful, including name, CR, type, size, alignment, AC, HP, speeds, ability scores, saves, skills, senses, languages, resistances/immunities and similar data.

Useful search/filter directions include name, CR, type, alignment and environment.

Broader desired capabilities include a reusable personal NPC/creature library, duplicate/modify variants, campaign reuse/copy/reference and official SRD starting templates where legally/technically practical.

### Stat-block internal granularity

Traits, actions, bonus actions, reactions, legendary actions and similar elements are **individual first-class structured records** with identity/category and ordering, but MVP does **not** decompose every mechanic into atomic rules-engine fields.

Their complete mechanical wording may remain formatted/rich text initially. The application does **not** need to understand that wording as executable rules.

Combat tracking may use selected structured values—such as HP, AC, conditions and initiative—without requiring every action/trait to be machine-interpretable.

Later versions may progressively add fields such as attack bonus, reach, damage components, save DC, recharge and targets where they provide real product value. The current architecture must not impede that enrichment: deeper mechanics should be additive through normal incremental migrations rather than requiring replacement of monster, encounter or combat models.

The principle is **complete for humans, selectively structured for software**. Do not build a speculative full rules engine merely for future possibility.

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
- minimum account/campaign/PC administration required by the approved workflows;
- character-sheet PDF regeneration/export.

Desktop work is saved to local SQLite. MVP synchronization is deliberately understandable and user-driven: **Save** preserves work locally, while **Sync** sends pending local changes and retrieves applicable remote changes when connectivity is available. A failed Sync does not discard local work. A continuous background synchronization service is not required.

The desktop MVP does **not** require a player-facing desktop application, full Android feature parity or the combat tracker. Desktop remains primarily preparation/administration; broader parity may be considered much later.

Sophisticated NPC/monster generators, AI creature creation, advanced structured import/paste parsing and similar tooling are later increments unless a limited capability proves trivial during implementation.

## 16. Shared/hosted data

Shared durable campaign/domain data should be hosted online and should normally fit a no-cost hosted tier at the intended personal scale where practical.

Characters, NPCs, monster definitions, saved encounters, campaign membership and similar durable data have a shared hosted representation. Android and desktop/laptop surfaces use this same campaign/domain data according to their approved workflows and permissions.

Live combat is the deliberate exception in authority semantics: hosted storage is its durable shared/recovery home, but the active DM device remains authoritative while that encounter is running and commits locally first.

The approved hosted topology is D-0034: **Neon PostgreSQL** for durable shared relational data, **Cloudflare** for the project-owned backend/API and additional infrastructure only when actually needed, and **Descope** for authentication only. Native clients do not connect directly to Neon under D-0039.

For the initial implementation, the hosted application path should remain essentially **native clients → Cloudflare Worker/API → Neon PostgreSQL**. Ordinary HTTP request/response plus simple refresh/polling is preferred before realtime transport. R2, Durable Objects, WebSockets, queues or similar services are deferred until an implemented feature demonstrates a concrete need.

The approved architecture also includes local SQLite/SQLDelight persistence and deliberately small project-owned synchronization under D-0038. Desktop uses Save+Sync; DM combat is local-first with one authoritative device and an increasing combat sequence; rare ordinary conflicts may be surfaced simply rather than requiring a generalized merge engine.

Provider replaceability means keeping vendor-specific code reasonably localized. It does **not** require provider factories or generalized abstraction frameworks for hypothetical migrations.

Offline support is selective: character/local preparation/combat/PDF workflows receive it where it provides real value, while inherently hosted workflows such as campaign joining, invitation management, account recovery and rules-AI clarification may require connectivity.

## 17. Approved MVP boundary

### Player

- manually create/view/edit PC character sheets;
- PDF export;
- SRD-only natural-language rules clarification in Spanish with identifiable official source/version;
- campaign selection appropriate to multicampaign membership;
- view the DM's public combat projection, with only ephemeral local Next-turn/visible-condition convenience while temporarily offline.

### DM tablet/live session

- combat tracker;
- quick/full PC, PC-group, NPC, monster and encounter views;
- prepared encounter → independent live encounter copy;
- fully on-the-fly live encounters;
- free live add/remove/modify behavior within the practical combat-board scope;
- campaign selection appropriate to multicampaign management.

### DM desktop/laptop

- basic administration;
- manual monster data entry;
- manual NPC data entry;
- saved encounter creation/editing;
- minimum account/campaign/PC administration;
- multicampaign administration/selection as required by the approved workflows;
- character-sheet PDF regeneration/export;
- local Save plus explicit Sync for shared durable work.

### Supporting MVP functionality

- account/login/recovery;
- **multicampaign** creation/membership/selection;
- campaign invitations and minimum moderation;
- persistence/shared data;
- permissions/ownership/control relationships;
- local/offline combat persistence;
- deliberately simple synchronization required for DM/player shared views.

### Explicitly outside MVP

- guided/legal character builder;
- house-rule-aware rules clarification/reusable house-rule library;
- sophisticated NPC/monster generator;
- AI creature creation;
- advanced import/paste parsing;
- co-DMs within the same campaign;
- combat-history analytics;
- automated combat resolution;
- automated rules enforcement;
- automatic combat-to-character-sheet mutation;
- speculative sophisticated audit-retention machinery;
- encounter balancing/CR automation;
- additional RPG systems;
- player desktop application/full Android-desktop parity;
- desktop combat tracker requirement;
- seamless concurrent multi-device DM combat editing;
- generalized synchronization/realtime/provider-abstraction infrastructure without a concrete need.

## 18. Current remaining product/technical work

The original discovery clarification rounds and the final pre-merge product-tension pass are complete. The latter explicitly resolved:

- Android live use vs desktop administration boundaries;
- multicampaign MVP scope;
- mixed/homebrew campaign freedom vs SRD-only MVP clarification;
- complete monster stat blocks vs selective machine structure;
- paper live authority vs durable digital character state;
- local-first authoritative DM combat vs hosted shared data;
- campaign moderation vs application-wide account administration;
- campaign invitation/rejoin semantics.

No additional product-level contradiction or unresolved behavioral tension remains from the final audit pass.

The foundational Phase 2 architecture/technology choices are owner-approved under D-0034 through D-0043. The pre-main proportionality audit further clarified those existing choices without introducing a new architecture layer: player offline combat state is tiny and ephemeral, Desktop uses Save+Sync, DM combat does not pre-build authority generations, ordinary HTTP precedes realtime infrastructure, provider code is localized without abstraction-framework ceremony, and offline support is selective.

After the documentation consolidation/contradiction sweep is complete, the remaining gate before implementation scaffolding is owner-authorized merge of the architecture branch into canonical `main`.

## 19. Discovery source material

Detailed exploratory history lives under `docs/discovery/`, including `2026-08-29_CLARIFICATIONS_03.md` and `2026-08-29_TENSION_RESOLUTIONS.md`. Discovery files preserve rationale and examples but do not override this approved product definition or `docs/DECISIONS.md`.

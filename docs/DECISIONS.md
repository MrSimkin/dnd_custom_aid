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

The product will be primarily an Android application.

### Consequences

- Development decisions must prioritize Android compatibility.
- No iOS or other full-platform version should be assumed unless separately approved later.
- A later approved desktop/laptop DM administration companion surface exists under D-0019 and D-0033; it does not supersede Android as the primary live-use platform.

---

## D-0002 — Phone and tablet support

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The Android application must be designed for use on both phones and tablets.

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

- Role-specific workflows and permissions are refined by later approved decisions, especially D-0014, D-0016, D-0022, D-0025, D-0032 and D-0033.
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
- Repository state documentation must identify active work branches when relevant.

---

## D-0008 — Routine implementation autonomy with owner visibility

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

Agents may make routine, reversible, low-level implementation choices that do not materially change approved product behavior, while significant product, UX, data/privacy, service/cost, compatibility, rules-content, and expensive-to-reverse architecture decisions remain owner-controlled.

This autonomy does **not** mean silent implementation. The agent must explain meaningful technical work and why it is being done. When a technical choice establishes a convention or has meaningful future consequences, the owner must be involved before that choice becomes durable project practice.

### Convention rule

When a convention first becomes relevant and no approved convention exists, the agent should:

1. explain realistic alternatives and consequences;
2. recommend an option;
3. ask the owner to approve or modify it;
4. record the approved convention in Git;
5. follow that convention thereafter unless a change is justified.

---

## D-0009 — Application architecture and implementation technology

**Status:** Approved  
**Date:** 2026-08-28  
**Resolved:** 2026-08-30 by D-0034 through D-0043  
**Decision owner:** Project owner

The foundational application architecture and implementation technology needed to begin scaffolding have been selected through the Phase 2 decision sequence.

The approved foundation includes:

- Neon PostgreSQL + Cloudflare + Descope hosted topology (D-0034);
- native Kotlin + Jetpack Compose Android (D-0035);
- native Kotlin + Compose Multiplatform Desktop DM companion (D-0036);
- explicit relational multicampaign domain boundaries (D-0037);
- SQLite/SQLDelight local-first persistence and project-owned synchronization (D-0038);
- Cloudflare API/backend as the hosted data-access boundary with proportional authorization/security (D-0039);
- local character-sheet PDF export on Android and desktop (D-0040);
- PostgreSQL full-text SRD retrieval with replaceable LLM integration, initially Cloudflare Workers AI (D-0041);
- Android 11 / API 30 minimum supported version (D-0042);
- minimal shared/Android/Desktop/backend/database scaffold, TypeScript Worker backend, focused tests and simple CI (D-0043).

These choices are sufficient to move from architecture evaluation into implementation scaffolding after the architecture branch is reviewed and merged to canonical `main`. Routine reversible implementation details remain agent-autonomous under D-0008; new consequential architecture changes still require owner approval.

---

## D-0010 — Initial product scope / MVP

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner  
**Amended:** 2026-08-29 by D-0033 and 2026-08-30 by D-0040

The first usable release/MVP is defined. Detailed scope is consolidated in `docs/PRODUCT.md` and further constrained/refined by later decisions.

### MVP summary

- Player Android: manual PC character-sheet create/view/edit, PDF export, SRD-only natural-language rules clarification in Spanish, and campaign selection appropriate to multicampaign membership.
- DM Android tablet: combat tracker; quick/full PC, PC-group, NPC, monster and encounter views; prepared and on-the-fly live encounters; campaign selection appropriate to multicampaign management.
- DM desktop/laptop: basic administration; manual monster/NPC data entry; saved encounter preparation; minimum account/campaign/PC administration; multicampaign administration/selection as required; character-sheet PDF export under D-0040.
- **Multicampaign:** multiple campaigns may exist and be active concurrently; one account may participate in multiple campaigns concurrently with campaign-scoped roles/permissions.
- Supporting account, persistence, synchronization, permissions and offline-combat functionality is included as required infrastructure.

### Explicit MVP exclusions

Guided/legal character building, house-rule-aware clarification, sophisticated NPC/monster generators, AI creature creation, advanced import/parsing, co-DMs within the same campaign, combat-history analytics, automated combat/rules enforcement, automatic combat-to-character-sheet mutation, speculative sophisticated audit-retention machinery, encounter-balancing automation, additional RPG systems, player desktop/full Android-desktop parity, desktop combat-tracker requirement, and seamless concurrent multi-device DM combat editing.

**Multiple active campaigns are not an MVP exclusion.**

---

## D-0011 — Design before technology stack

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The project will not choose the Android technology stack first. Product purpose, user workflows, behavior, relevant alternatives, and interaction/design direction must be explored and discussed with the owner before stack and architecture selection.

### Required sequence

1. Understand the intended product and users.
2. Explore realistic product/interaction alternatives with the owner.
3. Design intended behavior and experience collaboratively.
4. Record approved design/product decisions and unresolved questions in Git.
5. Evaluate stack/architecture options against that design.
6. Explain technical alternatives, trade-offs, and a recommendation.
7. Obtain owner approval before locking in consequential stack/architecture choices.

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
- A meaningful fact that exists only in conversation is not safely persisted until recorded in Git.
- At the end of meaningful work, repository documentation must be updated so a fresh agent can continue without previous chat context.

---

## D-0013 — Discovery input is exploratory until explicitly confirmed

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

During product discovery, the owner may intentionally describe ideas in an unstructured, exploratory way. Such statements are discovery material, not automatically approved requirements.

The agent/chat is expected to actively help shape those ideas rather than merely transcribe them.

### Required behavior during discovery

The agent should preserve raw ideas as provisional where useful, reorganize them, identify dependencies/contradictions/risks, present alternatives and recommendations, challenge weak ideas constructively, distinguish brainstorming from approved scope, and obtain explicit owner confirmation before promoting a material idea into an approved requirement or decision.

---

## D-0014 — One user identity; campaign-scoped roles

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

A person has one user account/identity. Player/DM role is associated with campaign participation rather than being a permanent account type.

### Consequences

- The same user may participate differently in different campaigns.
- The MVP now explicitly supports concurrent membership in multiple campaigns under D-0033.
- Authentication identity is conceptually separate from campaign role and permissions.

---

## D-0015 — D&D first, future game-system extensibility

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

D&D is the first supported tabletop RPG. A more advanced future version may support other game systems.

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

The DM must be able to audit player changes and correct or reverse inappropriate/mistaken changes.

### Consequences

- The system requires meaningful character-change history rather than storing only latest state.
- Detailed audit semantics are defined further by D-0021 and D-0028.

---

## D-0017 — Mixed official rules and house rules are allowed

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

A campaign may freely mix SRD 5.1 / D&D 5e, SRD 5.2.1 / D&D 5.5e and substantial house rules/homebrew.

Official SRD references must still identify their source/version.

### Consequences

- Character/NPC/monster data must not reject content merely because it is not legal SRD.
- The application is not a strict ruleset enforcer.
- Full guided character creation/legality validation is not part of the MVP.
- The MVP rules assistant remains official-SRD-only under D-0033; campaign homebrew freedom does not imply homebrew-aware clarification in MVP.

---

## D-0018 — Personal-use scale and no-cost hosting target

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The system is intended for personal use and a deliberately small foreseeable user/campaign scale.

Shared data should be hosted online, and normal expected use should remain within a no-cost hosted tier where practical. If scope meaningfully grows later, hosting cost may be reconsidered.

### Consequences

- "Free forever regardless of scope" is not a requirement.
- The hosted provider choice was later resolved by D-0034.
- Personal-scale proportionality is reinforced by C-0009.

---

## D-0019 — Desktop-friendly administration is required; implementation form is open

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner  
**Clarified:** 2026-08-29 by D-0033 and resolved technically 2026-08-30 by D-0036

Campaign preparation and administration—especially NPC/monster entry and organization—must have a comfortable desktop/laptop-oriented surface.

### Implementation resolution

D-0036 selects a native **Kotlin + Compose Multiplatform Desktop** implementation. This replaces the earlier open implementation-form question without changing the product scope boundary below.

### Scope boundary

- Desktop is primarily preparation/administration.
- It uses the same shared campaign/domain data as Android.
- It does not need to duplicate the whole Android application in MVP or near-term scope.
- No player desktop application is required in MVP.
- Full Android/desktop feature parity is only a possible much-later feature.
- Desktop combat tracker is not an MVP requirement.

---

## D-0020 — Paper-first play with a durable complete digital backup

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner  
**Clarified:** 2026-08-29 by D-0033

Normal play is paper-first. During normal paper use, the physical sheet is the authoritative live-session state. The digital character is the latest intentionally saved/reconciled durable backup/reference and may lag current paper state.

The digital backup should represent the full character sheet as of its latest reconciliation, including transient values such as current HP, remaining spell slots, consumables, charges, ammunition and similar values where applicable.

If paper is unavailable, the player may temporarily use the Android app as the authoritative live working sheet and reconcile later.

### Consequences

- Do not force simultaneous paper + app bookkeeping.
- Show useful last-updated/freshness information.
- Do not assume digital automatically supersedes newer paper notes.
- End-of-session reconciliation creates the new durable digital baseline.
- Returning to paper should start from the latest reconciled/exported state.
- Do not attempt automatic paper/digital conflict merging because the app cannot observe paper-only changes.
- Save/export semantics are resolved by D-0027.

---

## D-0021 — Character audit uses grouped compensating history and is intentionally bounded in scope

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

Character corrections and undo operations use a ledger-style model: the original mechanical change remains represented and a correction/reversal creates a compensating change rather than erasing history.

Related edits are presented as understandable change sets/transactions rather than a flood of database-field log lines.

First-version audit visibility is DM-only. Only mechanical/rules-relevant character information needs audit history. Cosmetic/biographical prose does not need anti-cheat-style tracking. DM correction reasons are optional.

---

## D-0022 — Character ownership, control and campaign-role model are incrementally extensible

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

A user may have multiple PCs in the same campaign. Character ownership and current control are separate concepts.

Temporary control changes do not change ownership. A DM may temporarily reassign control. A player may explicitly transfer/give a character permanently to another user. The DM may duplicate a character sheet when appropriate.

Inactive, dead and retired PCs remain preserved.

First version supports exactly one active DM **per campaign**, but the underlying membership/role model should avoid making future co-DM support unnecessarily difficult.

### Consequences

- Do not hard-code campaign ownership around a single `dm_user_id` if a general membership/role representation is straightforward.
- Future co-DM permission levels are intentionally not designed yet.
- Unassigned PC-style records are explicitly allowed by D-0029.

---

## D-0023 — House rules start as notes; house-rule-aware clarification is later scope

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

House rules are initially notes-style records, not a machine-readable rules engine or highly structured mandatory taxonomy.

A rule may be campaign-specific or come from a reusable personal rule collection, but its scope/source must be identifiable. Only the DM creates/edits campaign house rules.

### Consequences

- Broader future clarification may distinguish SRD 5.1, SRD 5.2.1 and the applicable campaign rule transparently.
- House-rule-aware clarification is explicitly outside MVP; MVP clarification is official-SRD-only under D-0010/D-0033.
- Generalized homebrew content management remains outside current scope unless separately approved.

---

## D-0024 — NPC/creature administration uses Quick/Developed NPC formats plus complete monster stat blocks

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

The NPC workflow distinguishes **Quick NPCs** and **Developed NPCs**. A Quick NPC is compact but meaningful. A Developed NPC can combine a richer dossier with combat information.

Monster/creature records must be capable of presenting a complete current D&D 5.5e Monster Manual-style stat block rather than an intentionally reduced summary.

The desktop-friendly administration surface should support a reusable personal NPC/creature library, campaign reuse/copy/reference as appropriate, duplicate-and-modify workflows, and official SRD monsters as starting templates where legally and technically possible.

Useful search/filter fields include name, CR, type, alignment and environment.

### Consequences

- Manual entry is in MVP; sophisticated generators/import/parsing remain post-MVP unless separately approved.
- Action/trait granularity is resolved by D-0030/D-0033.
- Prepared/on-the-fly encounter behavior is resolved by D-0031.

---

## D-0025 — Combat is a practical DM board with authoritative local DM state

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner  
**Clarified:** 2026-08-29 by D-0033

The combat feature is a practical DM combat board, not a VTT or comprehensive D&D automation engine.

Player view shows visible initiative order, active participant and visible/public conditions. The DM may hide participants.

For an active PC, the DM needs fast reference including AC, current HP and saving throws. DM tracking of PC current HP is optional.

For NPCs/monsters, the DM may track current HP, temporary HP, conditions, concentration, defeated/removed state and short working notes. The DM may manually override monster HP.

Same-group creatures normally share initiative while retaining individual HP/status; a creature may be split to individual initiative when needed.

### Authority/offline semantics

- Shared durable domain data is normally hosted/synchronized online.
- An active live encounter has one authoritative DM working state at a time.
- DM combat actions commit **locally first**; successful server contact is not required to continue play.
- Cloud sync is secondary/opportunistic for sharing and recovery.
- On reconnection, an older remote combat snapshot must not overwrite newer authoritative local DM state; combat requires explicit combat-aware reconciliation rather than generic last-write-wins.
- Player devices receive the latest successfully synchronized public projection.
- Offline player tracker edits are provisional/non-authoritative and are replaced/reconciled to the DM state after reconnection; they must never overwrite DM authority.
- The DM should be able to see whether combat is saved locally, synced or waiting to sync.
- MVP supports same-DM-device persistence/recovery and hosted synchronization where possible, not seamless concurrent multi-device DM editing.
- Future cross-device DM continuation should use explicit transfer/resume/authority handoff.
- Combat working state remains separate from persistent character-sheet state.

### Explicit first-scope exclusions

Death-save tracking, forced player spell-slot/resource tracking, automatic attack/damage/rules enforcement, automatic persistent inventory/resource consumption, combat-history analytics/logging, and seamless concurrent multi-device DM combat editing.

---

## D-0026 — Product scope favors assistant architecture and incremental evolution without scope creep

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

The product remains an assistant for DM play/preparation and a backup/reference mechanism for players. It is not intended to replace Foundry/VTTs, D&D Beyond, a campaign-maker platform, or normal tabletop play.

Where practical, first-version restrictions should be expressed as business/UI rules over reasonably extensible models rather than expensive structural dead ends. This does **not** mean implementing future features early.

A useful organizing model is:

1. durable campaign/character content;
2. bounded character-change history/corrections;
3. live-session combat working state persisted for recovery/continuation but conceptually separate from the durable character sheet.

---

## D-0027 — PDF export may deliberately use unsaved edits without saving them

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

Normal PDF export uses the latest fully saved character state.

If unsaved edits exist when export starts, the application warns the user and asks whether to export anyway. If the user continues, the PDF may use current edited/unsaved values.

Exporting those values does **not** save/commit them and does not create character audit/history entries. Cancelling returns to editing.

### Consequences

- `Save` and `Export` are distinct operations.
- Committed multi-field character updates should be atomic/grouped change sets.

---

## D-0028 — Keep complete grouped audit history initially and monitor real growth

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

At expected personal-use scale, keep complete grouped mechanical character-change history rather than prematurely deleting, summarizing, compressing or archiving it.

### Consequences

- Audit/history size should be measurable enough to identify unexpected growth.
- Do not build enterprise-grade retention machinery speculatively.
- Architecture should allow later retention/summarization/archival/compression if real measurements justify it.

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
**Clarified:** 2026-08-29 by D-0033

Monster records must be capable of representing/displaying the complete stat block with nothing important omitted.

Core stable fields should be structured where useful, including name, CR, type, size, alignment, AC, HP, speeds, ability scores, saves, skills, senses, languages, resistances/immunities and similar stable data.

Traits, actions, bonus actions, reactions, legendary actions and similar elements are first-class structured records with identity/category and ordering. Their complete mechanics may remain formatted/rich text in MVP.

### Consequences

- The application does not need to interpret formatted mechanics as executable rules.
- Combat may use selected structured values without requiring every action/trait to be machine-interpretable.
- Later deeper fields—attack bonus, reach, damage components, save DC, recharge, targets, etc.—may be added when they provide value.
- Architecture must not impede that future enrichment; it should be additive through normal migration rather than fundamental monster/encounter/combat rewrite.
- Avoid both one giant undifferentiated blob and a speculative full rules engine.

---

## D-0031 — Saved encounters create independent live copies; live encounters may also be created on the fly

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

A saved encounter is an optional reusable preparation/template, not the live combat state itself and not a prerequisite for combat.

Starting/loading a saved encounter creates a **separate live encounter copy**. Changes to the live copy do not automatically alter the saved template.

The DM may freely add, remove, duplicate, replace or modify participants before combat or at any point during combat. The DM may also create a live encounter directly from scratch.

---

## D-0032 — Campaign invitations and reversible moderation controls

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner  
**Clarified:** 2026-08-29 by D-0033

Campaign membership is campaign-scoped and DM-controlled through a revocable invitation code/link. QR and optional email are delivery/representation conveniences for that same invitation mechanism.

### Invitation lifecycle

- An invite belongs to one specific campaign and grants no rights elsewhere.
- A signed-in user with a valid invite joins directly; there is no second DM approval step.
- If sign-in/account creation is needed, the same invitation flow resumes afterward.
- One invite may be reused by multiple people until the DM revokes/regenerates it.
- Regeneration invalidates the prior code/link.
- A banned account cannot rejoin through an invite until the ban is lifted.
- A kicked account may later rejoin through a valid invite.
- Rejoining preserves existing identity/history relationships rather than duplicating them.
- Campaign membership does not automatically assign PC ownership/control.
- MVP invites do not require mandatory expiration dates; optional expiry/one-use/named/audited invites are later possibilities.

Standard email-based account/password recovery is preferred and must not lose campaign membership/data.

### Campaign moderation

Campaign DMs may:

- **Freeze PC** within their campaign;
- **Kick** a campaign member;
- **Ban** an account from that campaign;
- revoke/regenerate campaign invitations.

A campaign DM can administer **only their campaign**. Campaign kick/ban does not affect participation in other campaigns. A frozen PC remains preserved and campaign-scoped.

A campaign DM **cannot** freeze, delete, disable or otherwise control the user's global application account.

### Application administration

**Freeze Account** belongs only to application/system administration. It globally blocks application login/use while preserving campaigns, characters, audit history, ownership records, memberships and other relationships. It is reversible. After restoration, preserved relationships resume unless an independent campaign-level moderation state still applies.

Campaign moderation state and global account state must remain separate.

At present, the project owner is the only DM and only application administrator, but this operational fact must not be modeled as `DM = application administrator`.

---

## D-0033 — Final pre-merge product tension resolutions

**Status:** Approved  
**Date:** 2026-08-29  
**Decision owner:** Project owner

The owner explicitly resolved the final soft product tensions identified during the Phase 1 audit. This decision clarifies/amends earlier decisions where noted and supersedes any older wording inconsistent with the following conclusions.

### 1. Android vs desktop surface boundary

Android is the primary at-the-table/live-use surface. Desktop/laptop is primarily a DM preparation/administration companion using the same campaign/domain data. No player desktop application, full Android/desktop feature parity, or desktop combat tracker is required in MVP. Full parity is only a possible much-later feature. Desktop implementation form was subsequently selected by D-0036.

### 2. MVP is multicampaign

The earlier one-active-campaign MVP restriction is withdrawn. Multiple campaigns may exist/be active concurrently, and accounts may participate in multiple campaigns concurrently with campaign-scoped roles/permissions. The MVP includes sufficient campaign selection/switching UX. This amends D-0010 and removes multiple active campaigns from MVP exclusions.

### 3. Mixed/homebrew campaigns vs SRD-only MVP clarification

Campaigns may freely mix D&D 5e/SRD 5.1, D&D 5.5e/SRD 5.2.1 and homebrew. Game data must not reject non-SRD content merely for legality reasons, and the application is not a rules enforcer. The MVP rules assistant may answer from both supported official SRDs with source/version identified, but it does not automatically know/apply campaign house rules. House-rule-aware answers remain later scope.

### 4. Complete monster representation vs selective machine structure

Monster records are human-complete, with stable/core data structured where useful and actions/traits represented as structured records. Full mechanics may remain formatted text and need not be executable. Architecture must permit additive future structured enrichment without fundamental rewrite.

### 5. Paper live authority vs durable digital state

During normal paper-first play, paper is live authority. The digital character is the latest intentionally saved/reconciled durable baseline and may lag paper. Last-updated/freshness indication is meaningful. If the app is being used instead of paper, digital may temporarily be live authority. No automatic conflict merge with paper-only changes is required.

### 6. Local-first DM combat vs hosted shared data

Hosted storage is the durable shared home, but an active encounter has one authoritative DM working state at a time. DM actions commit locally first and sync opportunistically. Older remote state cannot overwrite newer authoritative local state. Player offline tracker edits are provisional and yield to DM authority on reconnection. Same-device DM recovery is required; concurrent multi-device DM authority is not.

### 7. Campaign moderation vs global account administration

Campaign DMs control only campaign-scoped moderation. Global account freeze belongs to application administration. Campaign and global moderation state are separate even though the owner currently holds both roles.

### 8. Invitation/rejoin semantics

Invites are campaign-scoped, reusable until revoked/regenerated, and grant direct membership without a second approval step. Ban blocks rejoin; Kick allows later rejoin. Rejoining preserves identity/history continuity. Membership remains separate from PC ownership/control. Mandatory invite expiry is not required in MVP; QR/email are alternate delivery forms of the same invite.

### Authority consequence

Where earlier approved wording conflicts with these eight conclusions, this later approved decision controls. `docs/PRODUCT.md` and `docs/PROJECT_STATE.md` must reflect these conclusions before PR #2 merge.

---

## D-0034 — Hosted backend topology uses Neon, Cloudflare and Descope with stable-service boundaries

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

The initial hosted backend topology is:

- **Neon** for hosted PostgreSQL as the durable shared relational database;
- **Cloudflare** for stable application-infrastructure services where appropriate, including web hosting, backend/API execution, database connectivity/pooling, object storage and realtime transport/coordination;
- **Descope** for end-user authentication only.

The initial architecture must not depend on Neon beta/preview backend features merely because they are temporarily free. In particular, Neon Auth, Data API, Storage, Functions or similar non-GA capabilities are not selected as foundational dependencies at this time.

### Authentication and authorization boundary

Descope establishes external user identity/session only. Campaign membership, DM/player role, PC ownership/control, moderation and all application/domain authorization remain represented and enforced by project-owned application logic and PostgreSQL data.

The application should maintain an internal user identity independent of the external authentication provider, with a mapping from the internal user to provider identity. This preserves migration ability if the authentication provider changes.

Cloudflare Access may be reconsidered later as a consolidation path if its native/non-browser authentication capability reaches stable GA and fits the Android client without requiring beta dependencies.

### Provider and migration principles

- PostgreSQL remains the authoritative hosted shared-data store; do not place irreplaceable domain truth in Cloudflare or Descope.
- The active DM device remains authoritative for live local combat under D-0025/D-0033; hosted infrastructure receives synchronized/reconciled state and public projections.
- Prefer standard/portable interfaces where practical, including PostgreSQL logical backup/migration and S3-compatible object storage.
- Cloudflare infrastructure and Descope authentication should be wrapped behind project-owned application boundaries so they can be replaced independently when practical.
- Personal-scale normal use should remain within no-cost tiers where practical under D-0018.
- Future low-cost paid migration paths remain a design consideration; no provider is assumed permanent.

### Rejected/secondary alternatives

Supabase remained a strong integrated alternative but its Free-plan inactivity behavior creates an avoidable continuity risk for a sporadically used personal D&D application. A keepalive architecture was considered plausible but not preferred. Convex, Firebase as the whole backend, Turso, self-hosted Supabase and other providers were evaluated but did not provide a better combined fit for stability, dormancy, PostgreSQL portability, Android requirements, maintenance and migration. Nile is a future watch candidate if it exits public preview while retaining suitable no-pause economics.

This resolves the hosted backend/database/authentication provider portion of D-0009. The remaining portions were subsequently resolved by D-0035 through D-0043.

---

## D-0035 — Android client uses native Kotlin and Jetpack Compose

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

The primary Android application will be implemented natively in **Kotlin** using **Jetpack Compose** for its UI.

### Consequences

- Android phone/tablet quality is prioritized directly rather than through a cross-platform UI abstraction.
- Android platform capabilities may be used directly when appropriate.
- Adaptive Compose layouts must support both phone and tablet use rather than stretching one layout across screen classes.
- Flutter and React Native are not selected for the initial Android application.
- Straightforward domain/business logic should avoid unnecessary Android coupling where this enables useful sharing with the approved native desktop application without forcing UI parity.
- This decision does not itself select local persistence, synchronization, minimum Android version, PDF stack or desktop delivery technology; those are resolved by later decisions.

This resolves the Android language/framework/UI-toolkit portion of D-0009. The remaining portions were subsequently resolved by D-0036 through D-0043.

---

## D-0036 — DM desktop administration uses Kotlin + Compose Multiplatform Desktop

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Detailed checkpoint:** `docs/decisions/D-0036_DESKTOP_CLIENT.md`

The DM desktop/laptop preparation and administration companion is a native **Kotlin + Compose Multiplatform Desktop** application.

Native packaging/distribution overhead is accepted and is not a material disadvantage for this project. The desktop application should support meaningful local/offline operation where practical. Android and desktop may share Kotlin domain/business/networking/synchronization logic selectively, but UI and feature parity are not required.

---

## D-0037 — Multicampaign domain boundaries use one shared relational model with explicit campaign scope

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Detailed checkpoint:** `docs/decisions/D-0037_MULTICAMPAIGN_DOMAIN_BOUNDARIES.md`

Use one shared relational PostgreSQL model with explicit global and campaign-scoped relationships rather than database/schema-per-campaign tenancy or provider-specific tenant models.

Users exist globally; campaign roles are membership relationships; characters belong to exactly one campaign while ownership/control are separate; personal mutable reusable content is distinct from campaign copies; official versioned SRD material may be referenced canonically; saved encounters, live encounters, durable character state, audit history and live combat state remain separate domains. Mutable entities use stable globally unique IDs suitable for local/offline synchronization.

---

## D-0038 — Local persistence and synchronization use SQLDelight/SQLite with project-owned local-first sync

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Detailed checkpoint:** `docs/decisions/D-0038_LOCAL_PERSISTENCE_AND_SYNC.md`

Android and desktop use **SQLite through SQLDelight** for durable local relational persistence. Native workflows are local-first where practical, with authorized useful data cached locally.

Local mutations and sync-outbox entries are persisted atomically where applicable. Project-owned synchronization passes through Cloudflare to Neon. Ordinary durable data uses stable IDs, idempotent mutation IDs, optimistic revisions and deletion tombstones rather than blind last-write-wins. Live combat uses stricter DM-authoritative lineage/generation and sequence semantics so stale hosted state cannot overwrite newer local DM state. Player combat projections remain non-authoritative.

---

## D-0039 — Hosted data access goes through Cloudflare with proportional authorization/security

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Detailed checkpoint:** `docs/decisions/D-0039_HOSTED_API_AUTHORIZATION_BOUNDARY.md`

Native clients do not connect directly to Neon and do not hold PostgreSQL credentials. Remote reads, writes and synchronization go through project-owned Cloudflare backend/API endpoints. Descope establishes identity; project-owned logic maps to the internal user and enforces domain permissions.

PostgreSQL constraints/foreign keys protect relational integrity and the backend uses minimum sufficient database privileges. Blanket enterprise-style RLS, role hierarchies, duplicated authorization engines, extensive security audit infrastructure or similar machinery are **not** MVP requirements; add such measures only when a concrete project risk justifies them.

---

## D-0040 — Character-sheet PDF export is local on Android and DM desktop

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Detailed checkpoint:** `docs/decisions/D-0040_PDF_EXPORT.md`

Character-sheet PDF export is required on both Android and the native DM desktop application. Generation is local/offline from the owner's non-fillable InDesign-generated PDF templates.

Android uses **PdfBox-Android** and desktop uses **Apache PDFBox**. Template/layout metadata may be shared where practical without creating a generalized cross-platform PDF subsystem. Export may deliberately use unsaved edits under D-0027 without saving them.

---

## D-0041 — SRD clarification uses PostgreSQL full-text retrieval first and a replaceable LLM

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Detailed checkpoint:** `docs/decisions/D-0041_SRD_RETRIEVAL_AND_CLARIFICATION.md`

Official Spanish **SRD 5.1** and **SRD 5.2.1** content is stored as versioned, provenance-preserving PostgreSQL sections/chunks. Initial retrieval uses PostgreSQL full-text search.

Relevant official excerpts are supplied to a replaceable LLM integration, initially **Cloudflare Workers AI**. The exact model is configuration rather than architecture. MVP answers must be grounded in retrieved official SRD material and identify the applicable source/version. Embeddings/vector/hybrid retrieval are deferred unless real testing proves ordinary full-text retrieval inadequate.

---

## D-0042 — Minimum supported Android version is Android 11 / API 30

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Detailed checkpoint:** `docs/decisions/D-0042_ANDROID_MIN_SDK.md`

The Android application uses **minSdk 30 (Android 11)**. The project intentionally does not spend compatibility/testing effort on older Android releases that no actual expected user needs.

This supports the owner's priority of a modern, high-quality Android UX and follows the personal-scale proportionality rule rather than maximizing hypothetical legacy-device reach.

---

## D-0043 — Initial project structure, backend language, testing and CI stay deliberately small

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Detailed checkpoint:** `docs/decisions/D-0043_MINIMAL_PROJECT_STRUCTURE_AND_TESTING.md`

The initial scaffold uses a small shared Kotlin Multiplatform logic/data area plus separate Android and Desktop application areas, with no shared-UI requirement. The Cloudflare Workers backend uses **TypeScript** and PostgreSQL schema/migrations remain explicit SQL.

Automated tests initially focus on failures that could materially hurt the project: shared domain/synchronization/combat-authority logic, SQLDelight migration safety, and consequential backend authorization/synchronization behavior. GitHub Actions provides a simple build/test check. Coverage gates, emulator farms, staging infrastructure, automated production deployment, enterprise quality tooling and speculative module hierarchies are not required.

This completes the foundational architecture/technology decision set under D-0009 and permits implementation scaffolding after owner-reviewed merge of the architecture branch into canonical `main`.

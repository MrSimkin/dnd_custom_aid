# D-0071 — Integrated MVP: Player + Server + DM Desktop/App architecture and execution direction

**Status:** Approved current product/architecture direction, with explicitly marked Pending items below  
**Date:** 2026-09-14  
**Decision owner:** Project owner  
**Implementation authorization:** documentation/design consolidation only at this checkpoint; implementation planning is approved, but this record by itself does not authorize starting code work

## Purpose

This decision consolidates the complete owner/assistant architecture discussion held after the Phase 4A Player work reached a near-presentable state and before the project begins the next major implementation cycle.

The owner intentionally paused before implementation so the project would not continue from an obsolete assumption that the DM side could be developed as an isolated local UI after a separately completed Player product. The controlling direction is now an **integrated MVP build** in which Player, hosted/shared services, and DM clients are designed and implemented as one coherent system, with continuous internal integration and a later full Player + Server + DM acceptance QA.

This record extends D-0036 through D-0043 and D-0068 through D-0070. Where this decision explicitly changes an older scope boundary, this later owner-approved direction controls.

In particular, it supersedes the earlier MVP exclusions that treated desktop combat, explicit DM-device combat handoff/resume, and a strictly isolated `Phase 4A closes first -> DM implementation later` sequence as outside the next build. It does **not** supersede the paper-first philosophy, non-VTT boundary, local-first combat authority, proportionality rule, official-SRD-only MVP clarification corpus, or the approved DM Desk family.

---

## 1. Current baseline and why implementation paused here

The product is one application ecosystem with Player and DM experiences, not independent products or data silos.

The Player side is substantially implemented and close to its first presentable product state. Existing automated/manual evidence remains useful and should not be discarded or replayed mechanically.

The latest owner-supplied planning gate identifies four bounded Player defects still requiring closure in the next work:

- **A10** — narrow-viewport custom-dice overflow;
- **B1** — narrow/micro reorder moved-row visual inconsistency;
- **J1** — primary Save persistence/reload failure while the boundary route works;
- **J2** — collection-filter hit-count/behavior inconsistency.

These defects are important integration inputs, not a reason to restart Player design from zero.

The implementation pause exists because DM implementation necessarily activates the shared/server architecture. Continuing to polish or expand Player independently while postponing shared authority, sync and DM product design would increase later integration cost.

---

## 2. MVP-cycle philosophy — full integrated product, not a tiny next slice

The next implementation cycle targets the **actual MVP**, not a succession of separately accepted miniature products.

Implementation may be organized into vertical slices, dependency waves and parallel workstreams, and those slices should receive engineering tests as they are connected. However, those are internal development controls, not scope gates.

The next major owner-facing acceptance QA is intended to exercise the integrated system:

```text
Player Android
      <->
Hosted/shared services
      <->
DM tablet / DM Desktop App
```

The cycle therefore includes, as applicable to the approved product:

- closing the bounded Player defects;
- authentication and remembered login;
- multicampaign identity/membership/roles;
- hosted PostgreSQL/API implementation;
- local/shared synchronization and conflict handling;
- object storage;
- PC synchronization, ownership/control and audit/history;
- DM reusable/campaign content;
- saved encounters and live encounter copies;
- live combat and Player public combat projection;
- DM tablet/Desktop operational capability;
- DM Desktop authoring/administration;
- full server backup/export;
- official SRD storage/retrieval foundations;
- official-SRD grounded AI clarification, implemented as the last substantial feature of this cycle.

The project remains personal-scale. `MVP` does not authorize speculative enterprise architecture or every future idea.

---

## 3. Controlling paper/local/server authority model

The product remains paper-first.

> **Paper is authoritative during ordinary paper play. The application is a local operative reflection that eases play and preserves intentionally reconciled digital state.**

For a PC, after bookkeeping/reconciliation the latest intentionally saved/synchronized digital record is the durable digital baseline. During ordinary play, newer facts written only on paper remain the real table truth even if the server is perfectly synchronized with an older digital copy.

Typical flow:

```text
before play:
Player reconciles/updates PC
-> local Save
-> server Sync
-> DM downloads/syncs current PC

at the table:
paper and local working state drive play
-> most actions do not require continuous server contact

after play:
each participant performs their own relevant bookkeeping
-> new intentional durable baseline
-> Sync when appropriate
```

The DM is not responsible for maintaining the Player's PC sheet during play, and the application does not attempt to infer or auto-merge changes that exist only on paper.

The server is the durable shared home/exchange point for synchronized application state. It is **not** a continuously authoritative game-session engine.

---

## 4. No technical Game Session object

There is no required technical `Game Session` entity or lifecycle ritual in the current architecture.

The application does not require start-session/end-session objects merely because real tabletop sessions exist. Most functions remain usable from local data, and most live changes do not need to be visible to everyone immediately.

The server persists discrete durable/shareable states, synchronization metadata, selected recovery/history data, shared rules/reference data and other approved hosted content.

This is consistent with the D-0068 Workspace rule: the campaign Workspace is a persistent live DM working environment, not a formal Session record.

---

## 5. Data families and authority/visibility boundaries

The principal hosted/domain families remain conceptually distinct:

1. identity/account;
2. campaign/membership/roles;
3. PCs;
4. personal reusable DM content;
5. campaign-specific copies/content;
6. saved encounter templates;
7. live encounters/combat;
8. official SRD/reference corpus;
9. meaningful audit/history/recovery data;
10. binary/assets stored through object storage where appropriate.

### 5.1 PCs

A PC belongs to one campaign. Ownership and current control are separate relationships. A PC may be unassigned. Temporary control reassignment does not change ownership.

Player edits do not require DM preapproval. A campaign DM may inspect, audit, correct, undo by compensating history, duplicate where appropriate, freeze/administer and reassign control within approved campaign authority. None of that makes the DM the owner.

Other campaign Players receive only an intentionally tiny PC identity projection by default:

- character name;
- portrait/avatar;
- current controlling Player/display identity.

The portrait/avatar is always part of this minimal campaign-visible identity.

Mechanical sheet information is **not** campaign-public by default: class/subclass, level, abilities, AC, HP, saves, proficiency, spells, equipment, features, background, notes and the full sheet remain restricted according to the PC/DM authority rules.

A public combat projection does not grant sheet access. MVP does not need an elaborate `share my full sheet` permission system; ordinary social showing remains possible outside application permissions.

### 5.2 Personal reusable DM content

Personal reusable DM-authored content is private by default and owned by its creator.

Using it in a campaign normally creates an independent campaign copy with provenance. Later edits to the personal master do not silently rewrite the campaign copy, and campaign edits do not silently rewrite the personal master.

### 5.3 Campaign-specific DM content

Campaign-specific DM content is DM-only by default. Future explicit sharing/projections may be added where a real workflow requires them, but no generic sharing UI or ACL system is implied.

### 5.4 Saved encounter vs live encounter

A saved encounter template and a live encounter are different objects.

Starting from a saved encounter produces an independent live working copy. Live changes do not silently rewrite the template.

At the end of live use, the DM may explicitly:

- discard the live encounter;
- archive/keep it as history;
- create a new reusable encounter template from it.

`Archive as what happened` and `save as a new reusable template` are separate actions.

### 5.5 Official SRD

The supported official SRD corpus is application/system-managed canonical read-only reference data with exact source/version provenance. Users do not edit canonical SRD records.

If an official element is customized where product workflows allow it, the customization is an independent editable personal/campaign record with provenance rather than a mutation of canonical official material.

---

## 6. Durable definitions vs live working copies

The application must preserve a strong distinction between durable/reusable definitions and live/play copies.

Examples:

- durable PC sheet vs PC combat reference/working information;
- monster definition vs live monster participant;
- NPC definition vs live participant;
- saved encounter template vs live encounter;
- personal reusable DM content vs campaign copy;
- campaign copy vs temporary staged/live override.

Live changes must not accidentally become permanent source edits.

When the DM discovers during play that the underlying permanent source itself is wrong, the product should distinguish two intentions:

1. change only the live copy;
2. correct the permanent source as an explicit separate action.

This preserves the dirty-improvisation direction from D-0068: live material may remain ugly; interesting material may be preserved as a dirty snapshot and later cleaned/promoted through the Desktop App.

---

## 7. Synchronization and exchange contract

### 7.1 Local Save is network-independent

`Save` and hosted `Sync` are different concepts.

- Desktop: Save persists locally; explicit Sync sends pending local changes and retrieves applicable remote changes.
- Android: local save is immediate/local-first; opportunistic/automatic synchronization may occur when appropriate, and manual Sync remains available.

A failed network operation must not destroy already-saved local work.

### 7.2 Sync state must be visible

Remote synchronization must not be invisible magic. The UI should communicate meaningful states where relevant, such as:

- Saved locally;
- Pending sync;
- Synced;
- Offline / waiting to sync;
- Sync failed / needs attention;
- Remote changes available;
- Conflict.

### 7.3 Conflicts

Rare real conflicts are handled explicitly rather than silently overwritten or solved by a speculative generalized auto-merge engine.

Where appropriate the user can review differences and choose an object-appropriate action such as keeping local, using server, or postponing/cancelling resolution.

### 7.4 Scoped synchronization

Synchronization is scoped to the relevant personal library/campaign/workflow. The system should not mirror every account's entire universe to every device merely because it can.

### 7.5 Offline/reconnect

If required data is already local, ordinary use should remain available offline.

Local changes remain saved and visibly pending. On reconnect:

- Android may opportunistically retry pending sync while retaining manual Sync;
- Desktop does not silently send pending work merely because connectivity returned; explicit Sync remains the normal contract.

Avoid nagging connectivity warnings.

### 7.6 Data freshness vs sync freshness

A record can be perfectly synchronized yet stale relative to paper/real play.

Where meaningful, especially for PCs, distinguish:

- last intentionally reconciled/updated digital state;
- last successful server synchronization.

---

## 8. Combat synchronization and authority

Combat remains a special domain rather than generic durable synchronization.

The active combat has exactly one authoritative DM device at a time. DM actions are committed locally first. Hosted synchronization is secondary/opportunistic and exists for sharing/recovery; Internet contact is not required for the DM to continue play.

Players receive only the latest successfully synchronized public combat projection, including approved visible information such as visible initiative order, active participant and public conditions. A Player view may become stale while disconnected.

The server must never overwrite a newer authoritative local DM combat state with an older hosted snapshot.

Simultaneous authoritative DM editing from tablet and laptop is **not** an MVP goal.

### 8.1 New MVP requirement — explicit authority recovery/resume on another DM device

The owner now requires the Desktop App to function as a complete DM fallback if the tablet is unavailable/lost. Therefore the earlier deferral of combat-device handoff is superseded for this MVP.

A second DM device must be able to explicitly resume an active encounter from the latest successfully synchronized state and become the new authority.

A simple authority generation/epoch plus per-authority increasing combat sequence is an appropriate conceptual model:

```text
Tablet: authority generation 4, local sequence 87
Server: latest synchronized generation 4, sequence 84
Tablet unavailable
Desktop explicitly resumes server snapshot
-> authority generation 5, sequence continues from recovered state
-> later stale generation-4 writes are rejected
```

The exact user-facing wording and low-level implementation remain implementation/design details, but the safety contract is approved:

- resume is explicit;
- only one authority generation is current;
- stale old-device writes cannot silently retake authority;
- no realtime/distributed-lock platform is required merely to implement this bounded handoff.

A recovered device can only resume from what was actually synchronized; unsynchronized actions that existed solely on the lost device cannot be reconstructed magically.

---

## 9. Audit, history, recovery and deletion

History should be meaningful, not exhaustive telemetry.

### 9.1 Current state, audit and recovery are separate

For important durable records, especially PCs, distinguish:

1. current complete state;
2. meaningful grouped audit/history where required;
3. selected historical recovery checkpoints/snapshots.

Restoring an older state creates a **new current version** based on that older state. It does not erase the later history.

Example: if v10 is bad and v8 is restored, the result becomes a new v11 derived from v8 while the intervening history remains visible.

### 9.2 Recovery strength by data type

- PCs: strongest recovery, grouped mechanical audit plus selected recovery checkpoints;
- important campaign records: recoverable with proportionate/lighter history;
- personal reusable DM content: normal/light recovery/versioning;
- saved encounter templates: recoverable;
- live encounters: no automatic permanent version archive; preserve only if the DM explicitly archives/keeps them;
- combat sequence/revision history: operational synchronization mechanism, not a permanent blow-by-blow archive;
- canonical SRD: system-managed, not user recovery data.

### 9.3 Avoid history bloat

Do not log every keystroke, UI event or transient combat operation permanently. Do not copy large binary assets into every historical revision.

Retain the approved grouped PC mechanical history for now, measure real storage growth, and introduce archival/compression/retention only if evidence justifies it.

### 9.4 Deletion

- PCs: soft-delete/recoverable;
- important campaign records: normally recoverable;
- personal reusable content: proportionate recovery;
- saved encounters: recoverable;
- deliberately archived live encounters: recoverable;
- temporary/unarchived live combat state: may be truly discarded;
- canonical SRD: not user-deletable;
- meaningful audit/history: not casually deletable.

Tombstones remain part of synchronization so a stale offline client does not resurrect a deleted record.

---

## 10. Full server backup/export — MVP foundation now

The DM Desktop App's system-administration surface must support an application administrator requesting and downloading a **full server backup/export** as a disaster-recovery/restoration point.

This is separate from object-level history/recovery and separate from ordinary Sync.

The backup should contain or reference everything required for a genuinely complete recovery of durable application state, including as applicable:

- internal application identities/mappings;
- campaigns, memberships and roles;
- PCs;
- ownership/control relations;
- reusable DM content and campaign copies;
- encounters/templates and deliberately retained records;
- permissions/moderation state;
- audit/history/recovery data;
- other durable relational state;
- sufficient asset/object-storage manifest/reference/integrity information so binary content is not silently omitted from the disaster-recovery story.

The native desktop client must **not** receive direct PostgreSQL/Neon credentials. Backup is requested through the project backend/API.

Backup export is required in this MVP cycle **before valuable hosted data is trusted without an external recovery point**.

A polished one-click whole-server restore UI is not required yet. Restoration is intentionally harder/destructive and may initially remain a controlled administrator procedure.

Backup artifacts should carry useful metadata such as:

- creation date/time;
- application/schema/data-format version;
- approximate content scope;
- integrity/checksum information.

Optional periodic reminder UX (for example, warning that no full backup has been downloaded recently) is desirable later but not required to block the basic export capability.

---

## 11. Identity, login, roles and practical permissions

### 11.1 Remembered login/device

The app should remember a normal authenticated user on a trusted/configured device so ordinary launches do not require login every time.

Reauthentication occurs for real reasons such as explicit sign-out, authentication/session invalidation/expiry that cannot be refreshed, or a security-sensitive action requiring confirmation.

If the app already has locally authorized data, lack of Internet at launch should not make that local data unusable merely because the authentication provider cannot be contacted at that moment.

There must be an explicit Sign out / remove account from this device action.

### 11.2 Campaign-scoped roles and view switching

Identity is global; Player/DM role is campaign-scoped.

A user may be DM in one campaign and Player in another. A user may also be the campaign DM and own/control a PC in that same campaign.

The app should allow switching between the relevant DM and Player/PC surfaces. Changing surface does not change authority: Player actions remain Player/owner/controller actions even if the same account also has DM authority.

### 11.3 Owner / Editor / Viewer vocabulary

Owner/Editor/Viewer is a useful capability vocabulary and leaves room for future delegation, but the MVP does not need a generic per-object ACL editor.

Normal access should be derived from:

- object type;
- campaign membership/role;
- ownership;
- current control;
- explicit domain rules.

A domain-specific authorization model is required now; a generalized SharePoint-like ACL framework is not.

### 11.4 Invitations and membership loss

Existing campaign invitation rules remain controlling: campaign-specific reusable invite until revoked/regenerated, authentication then continuation of the same invite flow, no automatic PC assignment, kick permits later rejoin, ban prevents rejoin until lifted, and rejoin preserves identity/history continuity.

When campaign membership is removed, future hosted access/synchronization for that campaign stops immediately. Local cached campaign data should **not** be silently destroyed at that instant. It becomes inactive/unauthorized and may be deliberately cleaned later.

### 11.5 Freeze / kick / ban / global account freeze

- frozen PC remains preserved and visible to its owner/controller **read-only**, clearly marked frozen; DM may still inspect/administer it;
- Kick removes campaign membership but is not a ban;
- Ban prevents the same account from rejoining until lifted;
- global account Freeze belongs only to application/system administration, not ordinary campaign-DM authority;
- none of these actions silently delete PCs, ownership/history or campaign data.

The project owner currently happens to be both DM and system administrator. The domain must not hard-code `DM = system admin`.

---

## 12. Hosted foundation that must become real in this MVP

The existing backend/database scaffold is not yet a real hosted product. The next build activates the already-approved architecture rather than redesigning it from zero.

Required foundation includes:

- Descope identity verification and internal application-user mapping;
- campaigns, memberships, roles, invitations and moderation state;
- PostgreSQL schema/migrations;
- Cloudflare Worker/API authorization boundary;
- project-specific synchronization endpoints/contracts;
- stable IDs, revisions, idempotent mutation handling and tombstones;
- shared/local sync machinery in Kotlin where genuinely reusable;
- object storage integration;
- audit/history/recovery persistence appropriate to each domain;
- full server backup/export;
- operational diagnostics/tests sufficient to know the system is dependable;
- SRD corpus/schema/provenance foundations early enough that the final clarification feature does not require architectural rework.

Player participates in this first real hosted implementation. The server cannot be built as a DM-only subsystem because the normal workflow includes Player PC -> hosted state -> DM authorized local copy.

---

## 13. Object storage is required; provider remains Pending

The MVP requires object storage for binary/file assets. **Cloudflare R2 is not yet selected by this decision.** It is one possible provider.

A concrete first need is the PC portrait/avatar, which must be able to travel from its owner/device to hosted storage and then to authorized DM/companion views.

The application-level model should use stable asset identity and useful metadata rather than embedding a provider-specific key into the domain. Provider-specific integration should be localized, consistent with C-0009 and D-0043, without building a speculative provider-factory framework.

Likely metadata includes, as useful:

- stable asset ID;
- owning/context relation;
- media type;
- size;
- integrity/checksum;
- provider/storage reference inside infrastructure code.

Future images/PDFs/audio may reuse the same facility only where approved workflows require hosted copies.

**Pending:** select the actual object-storage provider during implementation planning when concrete cost/fit can be evaluated.

---

## 14. Infrastructure intentionally not generalized now

The following distinctions are controlling.

### Must exist now in bounded form

- domain-specific authorization;
- project-specific synchronization;
- basic meaningful audit/history for domains that require it;
- object storage;
- full backup/export;
- conflict/revision/tombstone/idempotency behavior;
- HTTP/request-response and polling/refresh behavior sufficient for the approved workflows.

### Deferred unless a concrete MVP problem proves otherwise

- WebSockets;
- Cloudflare Durable Objects or equivalent coordination service;
- background queues;
- generalized realtime/event architecture;
- arbitrary/general ACL framework;
- elaborate history-retention/cold-storage service;
- generic public/community homebrew publishing system;
- generalized synchronization platform.

WebSockets/realtime may later improve near-instant public combat updates, but the current product tolerates latest-synchronized projections and does not require an always-live server.

Durable Objects or equivalent are especially unnecessary for the current authority model because the DM device, not a server room/session coordinator, is authoritative during combat.

Queues should be introduced only when a real asynchronous/long-running job requires them.

---

## 15. SRD retrieval + AI clarification belongs to this MVP cycle

Rules clarification remains an MVP feature for both Player and DM under D-0041 and D-0070.

The approved architecture remains:

- official Spanish SRD 5.1 and SRD 5.2.1 corpus;
- versioned/provenance-preserving PostgreSQL sections/chunks;
- PostgreSQL full-text retrieval first;
- relevant retrieved official text supplied to a replaceable LLM integration;
- Cloudflare Workers AI as the initial provider unless a later approved implementation decision changes it;
- Spanish grounded answers with clear D&D 5e / D&D 5.5e source identity;
- no vector database/general RAG platform unless measured retrieval quality proves it necessary.

The owner explicitly requires this feature in the current MVP build, but wants it implemented **last among the substantial user-facing features of this cycle**.

Therefore:

- establish SRD schema/provenance/data-loading foundations early while the hosted database is being built;
- implement the actual retrieval -> grounded LLM -> Player/DM answer flow near the end of the cycle after the core Player/Server/DM product path is working.

Homebrew Rules authoring/storage is in the MVP Desktop App. **House-rule-aware AI clarification is not**: the MVP clarification assistant remains official-SRD-only.

---

## 16. Full-MVP implementation organization

The following is an engineering organization, not a sequence of separately accepted products.

### Workstream 1 — shared MVP spine/contracts

Establish shared semantics for IDs, domain boundaries, network contracts/DTO meaning, revisions, authorization vocabulary, local-vs-hosted state, asset references and database conventions.

### Workstream 2 — Player stabilization/integration

Close the bounded Player defects and preserve valid QA evidence. Adapt the existing Player product to authentication, campaign selection, hosted PC sync, assets, audit/history/conflict state and public combat projection without rewriting mature Player behavior merely because a server now exists.

### Workstream 3 — hosted foundation

Implement PostgreSQL migrations/schema, Cloudflare API, Descope identity verification/internal mapping, domain authorization, synchronization endpoints, object storage, audit/recovery and backup/export. Start SRD storage/provenance foundations here.

### Workstream 4 — shared Kotlin data/sync layer

Evolve SQLDelight/local persistence, outbox/pending mutations, revisions, tombstones, idempotency, conflict detection, network contracts, asset references and reconciliation logic that genuinely benefit Android and Desktop.

### Workstream 5 — DM Desktop product

Implement the DM Desktop App's operational Desks plus its richer creation/preparation/campaign/system-administration capabilities described below.

### Workstream 6 — cross-client live-play exchange

Implement combat authority, hosted combat revisions, public Player projection, refresh/poll behavior, reconnect handling, stale-update protection and explicit authority resume on another DM device.

### Workstream 7 — SRD retrieval/AI

Finish the corpus retrieval + grounded clarification UX for Player and DM after the core integrated product path works.

### Internal execution waves

- **Wave 0 — protect current Player:** close/pin the known A10/B1/J1/J2 defects; backend/Desktop scaffolding may proceed in parallel. J1 deserves early attention so local-save failures are not confused with synchronization failures.
- **Wave 1 — shared spine:** stabilize the common meaning of User, Campaign, Membership/Role, PC, Ownership, Control, Revision, Mutation ID, tombstone, audit event, Asset, Sync status, reusable DM object, campaign copy, saved/live encounter and combat authority metadata.
- **Wave 2 — heavy parallel work:** Player, hosted services, Desktop and shared Kotlin advance in parallel, but connect frequently. Temporary fixtures are acceptable; long-lived disconnected fake universes are not.
- **Wave 3 — combat integration:** integrate the special local-DM-authority model and Player projection across devices/server.
- **Wave 4 — complete MVP:** finish remaining administration/recovery/permission edges and implement SRD retrieval + AI clarification last, then prepare the full integrated QA candidate.

The largest engineering risk is **parallel semantic drift**, not simply the amount of work. Enforce one coherent domain meaning, one evolving API/database contract, shared sync primitives where appropriate, and frequent real integration.

---

## 17. DM Desktop App — expanded MVP role

The earlier description of Desktop as only a preparation/administration companion is no longer sufficient.

The owner requires the **DM Desktop App** to serve two roles in the same native Kotlin + Compose Multiplatform program:

1. a richer authoring/preparation/campaign/system-management surface;
2. a **complete functional DM operational fallback** if the tablet is unavailable.

This is not a Player desktop application. The DM may inspect/administer PCs, but Player-specific desktop parity is not implied.

Desktop and tablet should share appropriate domain/business/network/sync logic, not identical UI. Desktop should exploit keyboard, mouse, larger displays, tables/panes and comfortable data-entry workflows.

### 17.1 All approved DM Desks must be usable on Desktop

The current approved live Desk family from D-0069 must have functional Desktop counterparts:

- **DM Screen**;
- **Stage Desk**;
- **Dungeon Desk**;
- **Combat Desk**.

Desktop may present them differently from tablet, but losing the tablet must not leave the DM unable to run the campaign with the laptop.

The D-0068/D-0069 principles continue to apply on Desktop: DM Attention Budget, flat Workspace/Desk model, multiple concurrent Desks where allowed, rich-but-fast prepared material, shared contextual Notes/Clocks, Encounter Readiness, tactical guidance and non-VTT/non-rules-engine boundaries.

### 17.2 Desktop authoring/manager counterparts

Desktop is the natural deliberate-authoring surface for persistent data consumed by the DM Desks.

The owner explicitly requires at least:

- **Monster Creator/Manager** — create/edit/duplicate/organize reusable monster data and campaign copies used by Monster references/encounters/combat;
- **NPC Creator/Manager** — create/edit/organize the rich NPC material used by Stage/Dungeon/other live projections;
- **Homebrew Rules Input/Manager** — author/maintain campaign/reusable homebrew-rule records for human consultation; this does not make the MVP AI house-rule-aware;
- **Zone Creator/Manager** — author the rich Zone/Area Brief data consumed by Dungeon Desk, preserving authored richness rather than forcing every idea into mandatory tiny fields;
- **Encounter Creator/Manager** — prepare saved encounter templates and authored encounter/tactical guidance that can instantiate independent live encounters.

General rule:

> Every persistent/content-oriented DM Desk capability must have sufficient Desktop authoring/management support to create and maintain the data it consumes.

That rule may require additional creator/manager surfaces for existing Stage/Dungeon content such as Places/Shops, Scene/Adventure Spine material, clocks/triggers/notes or related records as their detailed authoring contracts are designed. Do not invent redundant `Creator` screens merely for naming symmetry; the authoring counterpart should match the real domain/workflow.

PCs are primarily Player-owned, so the Desktop counterpart is a **PC Manager/Audit** surface rather than a generic PC creator mirroring all Player UX.

Combat is a live operational Desk rather than a `Combat Creator`.

### 17.3 Campaign/game management

Desktop MVP also includes the appropriate rich management surfaces for:

- campaign selection/creation/administration;
- members, invitations and campaign roles;
- PC ownership/control assignment;
- PC freeze/unfreeze, kick/ban and other campaign moderation;
- full DM-authorized PC inspection/audit/correction/compensating undo;
- character duplication where approved;
- PDF regeneration/export where already required;
- reusable personal DM content vs independent campaign copies;
- saved encounters, archived live material and explicit promotion/cleanup of dirty improvisation;
- synchronization/conflict state.

### 17.4 System Administration in the same Desktop App

The same Desktop program contains a distinct **System Administration** area visible only to application/system administrators.

Campaign-DM authority and system-admin authority remain separate even though the owner currently has both.

System-administration responsibilities may include:

- global account freeze/unfreeze;
- full server backup/export;
- useful storage/backup/diagnostic information;
- later controlled recovery/maintenance capabilities as approved.

A separate standalone Admin application is not required for this personal-scale project.

---

## 18. Existing DM Desk family remains controlling

This decision does not reopen generic Desk taxonomy.

Current approved live Desks remain:

1. **DM Screen** — neutral DM surface with PC Group/Quick/Full access, Party Lens, general reference and the shared rules-question capability;
2. **Stage Desk** — fast multi-route retrieval/navigation for the broad current adventure environment, Places, Shops, NPCs, area/function/search/recent routes and Adventure/Scene Spine;
3. **Dungeon Desk** — dangerous structured exploration, Zone Briefs, Dungeon Turns, clocks/threats, notes and Encounter Readiness;
4. **Combat Desk** — active combat operation plus encounter-wide and creature-specific tactical guidance.

Journey remains a future candidate/special Dungeon profile question unless later approved.

Entities such as NPCs, Shops, Places, notes, clocks, rules references and encounters do not automatically become separate Desks merely because they exist.

The Desktop requirement is **Desk capability parity for the DM role**, not identical layout/navigation parity.

---

## 19. Homebrew rules in MVP vs AI scope

A Desktop **Homebrew Rules Input/Manager** is in MVP because the DM needs a persistent place to author/maintain campaign/custom rules for human use and future evolution.

This does not expand the current AI corpus boundary.

For this MVP:

```text
Homebrew rules
-> can be authored/stored/consulted by approved human-facing workflows

Rules clarification AI
-> answers from supported official SRD 5.1 / SRD 5.2.1 only
```

Later house-rule-aware clarification remains post-MVP and must clearly distinguish official baseline from campaign override rather than blending them.

---

## 20. Next integrated QA philosophy

Internal tests/integration checks must occur throughout development, but the next major owner-facing acceptance gate is the **full MVP integrated QA**, not an acceptance stop after one tiny server slice.

A representative end-to-end scenario should eventually prove behavior such as:

```text
Player signs in / remembered login
-> joins/switches campaign
-> reconciles and locally saves PC
-> syncs PC/assets/history
-> DM syncs/downloads authorized PC
-> DM prepares reusable content and campaign copies
-> builds/prepares encounter/zone/stage material
-> starts live encounter/combat on an authoritative DM device
-> Players receive only public combat projection
-> connectivity can disappear without stopping local DM play
-> reconnect/sync resumes safely
-> if authoritative tablet is unavailable, Desktop explicitly resumes latest synchronized combat state
-> stale old authority cannot overwrite the resumed state
-> end-of-play bookkeeping remains explicit rather than automatic
-> durable state/history is correct
-> full server backup can be exported
-> official SRD question can be asked by Player/DM and answered from the correct supported source/version
```

QA must also cover meaningful failure cases: stale revisions, conflicts, failed sync, membership removal, frozen PCs, offline behavior, deletion/tombstones, asset authorization/integrity and backup export.

Existing Player evidence remains evidence for the boundaries actually exercised; it is not erased by the integrated MVP plan.

---

## 21. Explicitly deferred/generalized capabilities

Unless later approved from a concrete need, this MVP does not require:

- always-online play;
- a formal technical Session object;
- generalized realtime architecture;
- WebSockets merely for architectural fashion;
- Durable Objects/equivalent server-room coordination;
- queues without a real asynchronous job;
- simultaneous authoritative multi-device DM combat editing;
- generalized auto-merge conflict engine;
- generalized arbitrary ACL framework;
- public/community homebrew publishing/marketplace;
- elaborate automated history-retention/cold-storage machinery;
- automatic rules enforcement or legality engine;
- VTT movement/grid/simulation architecture;
- house-rule-aware AI clarification;
- Player desktop application/full Player UI parity.

The complete outer post-MVP boundary is **not yet fully reviewed**. That is a later discussion step and must not be inferred from this non-exhaustive list.

---

## 22. Pending consequential decisions

The following remain pending and should be resolved only when the relevant design/implementation work reaches them:

- actual object-storage provider;
- exact Desktop App navigation/information architecture and detailed area-by-area UX;
- detailed creator/manager schemas/interactions where D-0068/D-0069 intentionally left them open;
- exact implementation/UI of explicit combat authority resume/handoff, while the safety contract in this decision is approved;
- exact backup file/package format and later restore procedure/UI;
- detailed sync conflict comparison UX per object type;
- detailed SRD ingestion/retrieval/answer UX within the already-approved architecture;
- final product/UI name for the shared rules-question capability;
- exact post-MVP boundary (future 7E discussion).

---

## 23. Exact resume point after this consolidation

The project is intentionally paused after this documentation checkpoint.

Do **not** restart generic server or DM requirements discovery.

When discussion resumes, continue with the current architecture roadmap from:

> **DM Desktop App product definition in detail (current 7D): navigation/structure and area-by-area operational + authoring/administration behavior, preserving the approved DM Desk family and today's full-desktop-fallback requirement.**

Only after the Desktop App MVP is sufficiently defined should the discussion proceed to:

> **7E — carefully define what remains outside this expanded MVP.**

After that, derive the final Git/development topology, implementation roadmap/gates and explicit authorization for coding.

No implementation code was changed or authorized by this documentation checkpoint.
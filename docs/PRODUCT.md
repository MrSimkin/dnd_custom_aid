# Product Definition

This document describes the currently approved product direction. Detailed decisions preserve rationale and exact contracts; later specific Approved decisions control older historical prose.

Current controlling expansion: **D-0071 — Integrated MVP: Player + Server + DM Desktop/App architecture and execution direction**.

## 1. Product identity

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D.

The product is:

- a single application ecosystem serving Players and Dungeon Masters;
- primarily Android phone/tablet for Player and live DM use;
- supported by a native Kotlin + Compose Multiplatform **DM Desktop App** using the same domain/campaign data;
- user-facing in Spanish;
- designed around paper-first tabletop play rather than replacing it;
- intentionally not a Foundry/VTT, D&D Beyond replacement, generalized campaign-builder, automatic combat resolver or rules-enforcement engine.

D&D is the first supported system. Shared foundations should avoid unnecessary structural dead ends where a more general model is straightforward, but multi-system support is not part of the current MVP.

## 2. Product principles

### 2.1 Paper first

Physical printed character sheets are the preferred normal Player surface.

During ordinary paper-first play, **paper is the authoritative live-session PC state**. The application is a local operative reflection and durable intentionally reconciled backup/reference. A perfectly synchronized digital PC can still be older than facts written only on paper.

The application must not force simultaneous paper + phone bookkeeping and cannot automatically merge changes it never observed on paper.

### 2.2 DM application is an assistant

The DM live application must reduce DM attention cost rather than create bookkeeping work.

D-0068's **DM Attention Budget** remains controlling: infer, remember, prefill, derive or postpone rather than asking the DM for unnecessary live input. Prepared information should be fast to consume; live improvisation may remain incomplete/dirty and be cleaned later on Desktop.

### 2.3 Incremental but real MVP

Extensibility is a design quality, not permission for speculative scope.

The next implementation cycle nevertheless targets the **actual integrated MVP**, not a tiny isolated server proof or local-only DM prototype. Internal slices/waves are engineering organization; the next major owner-facing product QA is intended to exercise Player + hosted/shared services + DM together.

## 3. Primary usage surfaces

Android and Desktop are intentionally asymmetric in UX but share the same approved campaign/domain model.

### 3.1 Player phone/tablet

Player Android provides, within the approved character-foundation scope:

- digital character-sheet backup/reference;
- temporary active sheet when physical paper is unavailable;
- manual character-sheet editing/reconciliation;
- local Save and hosted synchronization when implemented;
- PDF regeneration/export;
- campaign membership/selection;
- minimal public combat projection from the DM;
- official-SRD grounded natural-language rules clarification.

No Player-facing desktop application is required in the MVP.

### 3.2 DM tablet / Android live client

DM Android/tablet is optimized for live table use and provides the approved DM Workspace/Desk family:

1. **DM Screen**;
2. **Stage Desk**;
3. **Dungeon Desk**;
4. **Combat Desk**.

It also provides the quick/full campaign-entity access and live operational behavior defined in D-0068/D-0069.

### 3.3 DM Desktop App

The earlier preparation-only companion concept is superseded by D-0071.

The Desktop App has **two MVP roles**:

1. richer DM authoring/preparation/campaign/system administration;
2. a complete functional DM operational fallback/client if the tablet is unavailable.

Therefore all approved live DM Desks must be usable on Desktop:

- DM Screen;
- Stage Desk;
- Dungeon Desk;
- Combat Desk.

Desktop does not need pixel/UI parity with tablet. It should exploit keyboard, mouse, large-screen tables/panes and comfortable data entry while preserving the same domain/authority contracts.

Desktop additionally provides the Creator/Manager/Admin surfaces described later in this document.

## 4. Player character workflow — paper first, digital durable baseline

The digital character represents the **latest intentionally saved/reconciled digital state**. It may include transient values useful for reconstruction/continuation, such as:

- current HP;
- remaining spell slots;
- inspiration;
- consumables;
- item charges;
- ammunition;
- other stored current sheet values.

The application should expose meaningful freshness information so a synchronized-but-old backup is not mistaken for current paper truth.

If paper is unavailable, the phone/tablet may temporarily become the active working sheet. Later reconciliation produces the new durable digital baseline.

End of play is a natural bookkeeping point, but during-session and between-session updates are allowed. There is no mandatory `confirm no changes` ritual.

Typical shared workflow:

```text
Player reconciles PC
-> local Save
-> Sync to server
-> DM syncs/downloads current PC
-> play happens locally/paper-first
-> each participant performs relevant bookkeeping later
-> new durable state is intentionally saved/synchronized
```

## 5. Character creation/editing and PDF export

MVP character creation is **manual structured data entry**, not a guided/legal character builder.

Character data is independent from a PDF layout. Existing owner-maintained InDesign/PDF layouts remain presentation artifacts, not the canonical data model.

PDF export is available on Android and the DM Desktop App under D-0040 and supports at least:

1. permanent/baseline-only output;
2. full latest digital-sheet-state output including stored transient values.

`Save` and `Export` are separate operations. Normal export uses fully saved state. If unsaved edits exist, export warns and may explicitly use current unsaved values without committing them or creating audit history.

Committed multi-field character updates should remain atomic/grouped change sets.

D-0047 and the current Player successor line remain controlling for the detailed character-foundation domains, conditional modules, settings, responsive behavior and current Player implementation evidence.

## 6. Character ownership, control, visibility, audit and correction

Every PC belongs to one campaign. PC existence, ownership and current control are distinct.

A PC may be unassigned. Temporary control reassignment does not transfer ownership. Permanent transfer is explicit.

Player edits take effect without DM preapproval.

The DM may:

- inspect the full PC under campaign authority;
- audit mechanical/rules-relevant grouped changes;
- directly correct a PC;
- reverse/correct through compensating history rather than deleting the original event;
- temporarily reassign control;
- freeze/unfreeze within campaign moderation authority;
- duplicate a PC where useful.

These powers **do not make the DM the PC owner**.

### 6.1 Tiny default campaign-visible PC identity

Other Players in the campaign see only the deliberately minimal default identity:

- character name;
- portrait/avatar;
- current controlling Player/display identity.

The portrait/avatar is always part of this minimum identity.

Mechanical/full-sheet data such as class, level, AC, HP, saves, spells, equipment, features, background and notes is not campaign-public by default.

The Player-visible combat projection does not grant character-sheet access.

## 7. Accounts, remembered login, campaigns and permissions

Every person has one persistent global identity. Player/DM roles are campaign-scoped.

The MVP is multicampaign. A user may participate in multiple campaigns with independent roles and permissions.

The same account may be DM and also own/control a PC in the same campaign. The UI may switch between DM and Player/PC surfaces; changing surface does not magically convert Player actions into administrator actions.

### 7.1 Remembered login/device

A normal configured device should remember the authenticated user so ordinary launches do not require login every time.

Reauthentication occurs for a real reason such as explicit logout, invalid/expired authentication that cannot be refreshed, or a sensitive action requiring confirmation.

Locally authorized data should remain usable offline when the auth service cannot currently be reached.

An explicit Sign out/remove-account-from-device action is required.

### 7.2 Owner / Editor / Viewer vocabulary

Owner/Editor/Viewer is useful permission vocabulary and leaves room for future delegation, but the MVP does not require a generic per-object ACL editor.

Normal access derives from object type, campaign role, ownership, control and explicit domain rules.

### 7.3 Invitations

First-version campaign enrollment remains DM-controlled:

- invitation belongs to one campaign;
- signed-in user follows a valid invite and joins directly;
- unauthenticated user signs in/creates account then continues the same invite flow;
- invite may be reused until revoked/regenerated;
- banned account cannot rejoin until ban is lifted;
- kicked account may later rejoin with a valid invite;
- rejoin preserves identity/history continuity;
- joining does not automatically assign a PC;
- QR/email are delivery representations/conveniences of the same invitation concept;
- public campaign discovery/elaborate approval queues are not required.

If membership is removed, future hosted access/synchronization stops immediately. Local cached campaign data is not silently destroyed at that exact moment; it becomes inactive/unauthorized and may be cleaned deliberately later.

### 7.4 Moderation vs system administration

Campaign DM authority includes campaign-scoped actions such as Freeze PC, Kick, Ban and invitation management.

A frozen PC remains visible to its owner/controller **read-only** with a clear frozen state; DM may still inspect/administer it.

Global account Freeze is system-administrator authority, not ordinary DM authority. Campaign actions do not affect unrelated campaigns.

The project owner currently happens to be both DM and system administrator; the model must not hard-code those identities together.

## 8. DM Workspace and approved Desk family

D-0068/D-0069 remain controlling.

Current relationship:

```text
Campaign
└── Workspace
    ├── Desk
    ├── Desk
    └── ...
```

Workspace is the persistent live DM working environment, **not a formal technical Session object**. No start-session/end-session/archive-session ritual is required.

Desks are flat, purpose-specific working environments rather than hierarchical entity containers. Multiple concurrent Desks of the same operational kind may exist where real play requires it, especially multiple Combat Desks.

### 8.1 DM Screen

Neutral general-purpose DM surface when no specialized Desk is required. It includes/links to:

- PC Group Sheet;
- PC Quick/Full Sheet access;
- Party Lens;
- general DM reference;
- the shared Player/DM rules-question capability.

### 8.2 Stage Desk

Fast retrieval/navigation across the broad current adventure environment.

It supports finding current material through name/search, area/geography, function/category, narrative context and recent/open/pinned context.

Places are canonical entities with multiple projections; Shops are specialized Places; NPCs remain independent linked entities. Adventure/Scene Spine is an orientation/retrieval structure, not a quest engine.

Partial digital representation and paper references are valid by design.

### 8.3 Dungeon Desk

Supports dynamic dangerous structured exploration, including:

- prepared Zone/Area Briefs;
- topological/flowchart-like area relationships rather than tactical VTT geometry;
- Dungeon Turns beta support;
- contextual notes;
- clocks/counters;
- relevant creature/NPC/PC references;
- Encounter Readiness/staging;
- transition/handoff to Combat Desk.

Prepared areas are not structurally identical to Dungeon Turn movement zones. Areas and encounters are not 1:1.

Zone Briefs preserve authored richness and distinguish DM-only from player-safe information. Current alpha organization remains `Presentar / Interactuar / Encuentro` under D-0069.

### 8.4 Combat Desk

Combat Desk is both:

- a practical active-combat tracker;
- a tactical reminder assistant for how the encounter and active creatures are intended to be run.

It is not a VTT or automatic rules engine.

## 9. Notes, clocks, Encounter Readiness and dirty improvisation

D-0068 remains controlling for these shared live concepts.

### Notes

Use one contextual note concept instead of incompatible per-Desk note systems. Context should be inferred where possible to respect the DM Attention Budget.

### Clocks/counters

Use a generic reusable clock/counter concept with different advancement sources rather than unrelated clock systems for every Desk. Clocks inform/prompt; they do not force fiction automatically.

### Encounter Readiness

Before combat, the DM may stage/cheat/improvise encounter composition without entering a formal encounter-builder ritual.

Useful live operations include quantity changes, add/remove/duplicate/substitute, reserve material, search/recent/favorites, reskin/relabel, temporary HP/AC/attack/damage overrides, mechanical pieces and simple reusable packages.

The application does not certify encounter balance or enforce derivation formulas against intentional DM overrides.

### Dirty live -> preserve -> Desktop cleanup

Live improvisation may remain ugly.

A minimal Keep/Save-for-later action may preserve an improvised creature/encounter/result snapshot without polishing it first.

Desktop is the natural place to later clean names/descriptions, rationalize mechanics, extract packages, promote to reusable content, merge/update prepared material or archive/delete it.

## 10. DM Desktop App authoring and administration

Desktop is the deliberate rich authoring/management surface for persistent data consumed by the Desks.

The owner explicitly requires at least:

- **Monster Creator/Manager**;
- **NPC Creator/Manager**;
- **Homebrew Rules Input/Manager**;
- **Zone Creator/Manager**;
- **Encounter Creator/Manager**.

General rule:

> Every persistent/content-oriented DM Desk capability must have sufficient Desktop authoring/management support to create and maintain the data it consumes.

Additional Stage/Dungeon authoring surfaces—such as Places/Shops, Adventure/Scene Spine, clocks/triggers/notes or other records—should be designed from their real workflow rather than created merely for naming symmetry.

PCs are principally Player-owned, so Desktop provides a **PC Manager/Audit** rather than implying a full Player desktop application.

Combat is an operational Desk rather than a `Combat Creator`.

### 10.1 Campaign/game management

Desktop includes appropriate rich surfaces for:

- campaign creation/selection/administration;
- members, invitations and roles;
- PC ownership/control assignment;
- moderation;
- DM PC audit/correction/compensating undo;
- reusable personal DM content vs independent campaign copies;
- saved encounters and deliberately archived live material;
- synchronization/conflict state;
- existing PDF regeneration/export responsibilities.

### 10.2 System Administration

The same Desktop program contains a distinct permission-gated **System Administration** area for application administrators.

It may include:

- global account freeze/unfreeze;
- full server backup/export;
- useful storage/backup/diagnostic information;
- later controlled recovery/maintenance tools as approved.

A separate standalone administrator application is not required for this personal-scale project.

## 11. NPC and monster records

The existing NPC direction distinguishes:

1. Quick NPC — compact but meaningful;
2. Developed NPC — richer dossier and optionally combat-capable mechanical information.

Creature/monster records must be capable of representing/presenting the complete current D&D 5.5e Monster Manual-style stat-block information needed by a human DM.

Stable mechanics should be structured where useful. Traits/actions/bonus actions/reactions/legendary actions and similar elements are first-class ordered records, but their complete mechanical wording may remain rich/formatted text; the application need not interpret every mechanic as executable rules.

Principle: **complete for humans, selectively structured for software**.

Reusable personal NPC/creature definitions are private by default. Using them in a campaign normally creates independent campaign copies with provenance rather than live links that silently rewrite one another.

## 12. Encounters and live copies

A saved encounter is an optional reusable preparation/template, not live combat itself and not required before combat.

Prepared flow:

1. DM creates/saves encounter template;
2. starting it creates a separate live encounter copy;
3. live changes do not rewrite the template automatically.

On-the-fly live encounters may also start from scratch.

The DM may freely add/remove/duplicate/replace/modify live participants before or during combat.

After play, the DM may explicitly:

- discard the live encounter;
- archive/keep it as history;
- save it as a **new** encounter template.

Archiving what happened and creating a reusable template are separate actions.

## 13. Combat player projection, persistence and device authority

### 13.1 Public Player projection

Players may receive only approved public combat information such as:

- visible initiative order;
- current active participant;
- visible/public conditions.

DM-hidden participants do not appear.

### 13.2 DM live state

For PCs, DM current-HP tracking is optional/not forced.

For NPCs/monsters, live state may track current HP, temp HP, conditions, concentration, removed/defeated state and short working notes. Same-group creatures may share an initiative position while retaining individual state.

### 13.3 Local-first authority

An active encounter has exactly one authoritative DM device at a time.

Every authoritative DM action commits locally first. Hosted synchronization is secondary/opportunistic and exists for sharing/recovery. Internet loss does not stop the authoritative DM device from continuing play.

The server must never overwrite newer authoritative local state with an older hosted snapshot.

Combat working state does not automatically mutate persistent PC sheets, and persistent PC edits do not automatically rewrite the combat tracker.

### 13.4 Explicit Desktop/tablet authority resume — MVP

Because Desktop must be able to replace an unavailable/lost tablet, explicit DM-device combat resume/handoff is now an MVP requirement.

The second DM device resumes from the latest **actually synchronized** hosted combat state and becomes the new authority through an explicit action.

A simple authority generation/epoch plus increasing sequence/version is the approved conceptual safety direction so stale writes from the old device cannot silently retake authority.

Simultaneous authoritative tablet + Desktop combat editing is not required.

Unsynchronized actions that existed only on a lost device cannot be recovered magically.

## 14. Shared/hosted data and synchronization

Shared durable application data is hosted, but the application is not continuously synchronous.

The server stores/exchanges discrete durable/shareable state and recovery/reference data. There is no required formal technical Game Session object.

### 14.1 Hosted topology

Approved hosted path remains essentially:

```text
native clients -> Cloudflare Worker/API -> Neon PostgreSQL
```

Descope provides authentication. Application logic performs campaign/domain authorization. Native clients do not connect directly to Neon or hold database credentials.

### 14.2 Save vs Sync

Local Save never depends on Internet.

- Desktop: Save locally + explicit Sync.
- Android: local Save immediately; opportunistic/automatic retry may occur when appropriate plus manual Sync.

Remote synchronization state should be visible: Saved locally, Pending sync, Synced, Offline/waiting, failure/attention, remote changes and conflict where applicable.

### 14.3 Synchronization primitives

Use a small project-specific design with, as applicable:

- stable IDs;
- explicit revisions;
- idempotent mutations;
- outbox/pending changes;
- tombstones;
- scoped campaign/library synchronization;
- explicit human conflict handling instead of generalized automatic merge.

### 14.4 Offline/reconnect

If required data is local, ordinary workflows should remain usable offline.

Desktop does not silently send pending work merely because connectivity returned; explicit Sync remains normal. Android may opportunistically retry while preserving manual control.

### 14.5 Freshness

For data such as PCs, digital-data freshness and server-sync freshness are different facts and should be presented separately where useful.

## 15. Object storage

Object storage is required in the current MVP cycle for binary/file assets.

The provider remains **Pending**; Cloudflare R2 is only a candidate, not an approved requirement.

The first concrete cross-device asset need is the PC portrait/avatar.

Application/domain records should reference stable asset identity and useful metadata rather than embedding provider-specific keys throughout business models. Provider-specific integration stays localized without a generalized provider-abstraction framework.

## 16. Audit, recovery, deletion and full backup

### 16.1 Meaningful history

History is meaningful grouped domain history, not exhaustive telemetry.

For important durable records distinguish:

1. current state;
2. meaningful grouped audit/history;
3. selected recovery checkpoints/snapshots.

Restoring an old state creates a new current version based on that state; it does not erase subsequent history.

PCs receive the strongest recovery. Campaign records/personal reusable content/saved encounters use proportionate recovery. Live encounters are retained only when deliberately archived; combat sequence logs are operational, not permanent blow-by-blow analytics.

Avoid every-keystroke logging, repeated binary copies in revisions and permanent transient-combat noise.

### 16.2 Deletion

PCs and important durable records should generally be recoverable/soft-deleted. Tombstones prevent stale offline clients from resurrecting deleted synchronized records.

Temporary/unarchived live combat may be truly discarded. Canonical SRD is not user-deletable.

### 16.3 Full server backup/export — MVP now

The Desktop System Administration area must allow an application administrator to request/download a full server backup/export as a disaster-recovery point.

The backup must account for all durable application state and the binary/object-storage recovery story. Useful metadata includes creation date, schema/application/data-format version and checksum/integrity information.

Backup is requested through the backend/API; Desktop never receives raw PostgreSQL credentials.

A polished one-click whole-server restore UI is **not required yet**. Controlled restoration can be designed later. Backup/export itself belongs early in the hosted MVP foundation.

## 17. Rules sources, Homebrew Rules and AI clarification

Campaign/domain content remains permissive: official D&D 5e/5.5e foundations and homebrew may coexist; the app is not a legality engine.

Internal source labels preserve:

- SRD 5.1 — earlier/2014-era foundation;
- SRD 5.2.1 — revised/2024-era foundation.

User-facing Spanish labels use D&D 5e and D&D 5.5e while retaining exact provenance internally.

### 17.1 Homebrew Rules records

A Desktop **Homebrew Rules Input/Manager** is part of the MVP. The DM can author/maintain homebrew/custom/campaign rule records for human consultation and future evolution.

### 17.2 Shared Player/DM rules question

Natural-language rules clarification is a shared capability available to both Player and DM. `Quick Rules Question` remains a working/discovery label only.

MVP clarification remains **official SRD only**:

- supported corpus = SRD 5.1 + SRD 5.2.1;
- corpus stored as versioned/provenance-preserving PostgreSQL sections/chunks;
- PostgreSQL full-text retrieval first;
- retrieved official material grounds the LLM answer;
- answers are Spanish and identify relevant D&D 5e / D&D 5.5e source/version;
- initially approved LLM provider = Cloudflare Workers AI, replaceable by configuration/normal evolution.

Homebrew Rules records are **not** silently injected into MVP AI clarification.

House-rule-aware clarification remains post-MVP and must later distinguish official baseline from campaign override explicitly.

### 17.3 Implementation ordering

SRD schema/provenance/data-loading foundations should begin early during hosted database work.

The actual retrieval -> grounded AI -> Player/DM clarification feature is deliberately the **last substantial user-facing feature implemented in this MVP cycle**.

## 18. Infrastructure proportionality

Required now in bounded project-specific form:

- domain-specific authorization;
- project-specific synchronization/revisions/idempotency/tombstones;
- PostgreSQL/API implementation;
- object storage;
- meaningful audit/history/recovery;
- full backup/export;
- ordinary HTTP/request-response and polling/refresh sufficient for approved flows.

Deferred unless a concrete requirement proves otherwise:

- generalized realtime architecture;
- WebSockets;
- Durable Objects/equivalent server-room coordination;
- queues without a real asynchronous/long-running job;
- arbitrary/general ACL framework;
- generalized synchronization platform;
- elaborate history-retention/cold-storage service;
- public/community homebrew publishing/marketplace.

## 19. Integrated MVP implementation/QA direction

The next implementation cycle is organized across coordinated workstreams for shared contracts, Player integration, hosted foundation, shared Kotlin sync/data, DM Desktop product, live-play exchange and SRD retrieval/AI.

Implementation may proceed in dependency waves and safe parallelism. Player, Server and DM must integrate frequently so shared concepts do not drift.

The next major owner-facing product QA is intended to test the complete integrated MVP, including representative authentication/campaign/PC sync/DM content/combat/offline/handoff/backup/SRD flows plus material error cases.

This does not defer engineering testing until the end; internal tests/integration checks occur continuously.

## 20. Explicit MVP non-goals / deferred generalized capabilities

Unless later approved from a concrete need, current MVP does not require:

- guided/legal character builder;
- automatic combat resolution;
- automatic rules enforcement;
- VTT movement/position/grid simulation;
- encounter-balance certification;
- automatic combat-to-PC-sheet mutation;
- co-DM simultaneous campaign operation;
- simultaneous authoritative multi-device DM combat editing;
- Player desktop application/full Player UI parity;
- house-rule-aware AI clarification;
- sophisticated AI creature generation;
- advanced third-party character import/paste parsing;
- combat-history analytics;
- generalized realtime/sync/ACL/provider infrastructure;
- public/community homebrew publishing;
- additional RPG systems.

This list is **not the final complete post-MVP boundary**. That boundary is the future 7E design step.

## 21. Current detailed Player foundation

D-0047 and the current `implementation/phase4a-successor-cycle` remain controlling for the extensive Player character-foundation closure: character data/workflow expansion, Gestión, PC Settings, conditional modules, interaction patterns, Supercompact/Table modes, phone/tablet responsive behavior and the latest repair/QA evidence.

Do not duplicate that implementation history here or restart it from old Product prose.

## 22. Current exact continuation

The owner intentionally paused after D-0071 consolidation.

Resume with:

> **7D — detailed DM Desktop App product definition:** overall navigation/structure, then area-by-area live Desk + Creator/Manager/Campaign/Admin behavior.

Do not reopen generic Desk taxonomy; D-0068/D-0069 already define the current live family.

Then:

> **7E — define the exact outside-MVP boundary.**

After 7E, derive final Git/development topology and concrete implementation gates, then obtain explicit coding authorization.

The D-0071 documentation checkpoint did not itself authorize code implementation.
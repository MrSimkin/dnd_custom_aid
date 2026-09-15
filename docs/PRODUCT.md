# Product Definition

This document summarizes the **current approved product direction** for implementation. Detailed decision records preserve the exact rationale/contracts and control where this summary is less specific.

The integrated-MVP implementation is authorized and in progress. The validated baseline convergence is recorded in `docs/checkpoints/2026-09-14_INTEGRATED_MVP_BASELINE_CONVERGENCE.md`.

## 1. Product identity

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D.

It is one application ecosystem serving Players and Dungeon Masters through:

- Android phone/tablet for Player use;
- Android/tablet for live DM use;
- a native Kotlin + Compose Multiplatform DM Desktop App;
- hosted/shared services for durable exchange/recovery;
- paper-first tabletop play as the normal Player experience.

It is intentionally **not** a Foundry/VTT, D&D Beyond replacement, automatic character legality engine, automatic combat resolver, generalized campaign-builder or generic RPG platform.

User-facing application language is Spanish.

## 2. Core product principles

### 2.1 Paper first

Physical printed character sheets are the normal Player surface.

During ordinary play, paper is the authoritative live-session PC truth. The application is the durable intentionally reconciled digital baseline/reference and may temporarily act as the working character sheet when paper is unavailable.

The application must not require simultaneous paper + phone bookkeeping and cannot infer changes that exist only on paper.

### 2.2 DM Attention Budget

The live DM application should reduce attention cost rather than create bookkeeping.

Preferred interaction cost:

1. no input;
2. one tap;
3. a few taps;
4. typing/multi-screen only when genuinely useful;
5. maintenance rituals/forms during live play are suspicious.

Prepared information should be quick to consume. Live improvisation may remain incomplete/dirty and be cleaned later on Desktop.

### 2.3 Proportional implementation

Use the simplest safe implementation that satisfies real approved requirements. Do not import enterprise infrastructure, generalized frameworks or speculative abstractions merely because they might be useful someday.

### 2.4 One integrated MVP

The current implementation target is the real integrated product:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Internal waves are engineering organization, not separate products or excuses to silently trim approved scope.

## 3. Player character workflow

The digital PC is the latest intentionally saved/reconciled digital state. It may also store current/transient values useful for recovery/reference, such as current HP, remaining spell slots/resources, inspiration, consumables, charges, ammunition and analogous sheet values.

Digital-data freshness and server-sync freshness are separate facts and should be presented separately where useful.

Typical flow:

```text
Player reconciles PC
-> local Save
-> optional/available Sync
-> DM may retrieve authorized durable PC
-> play happens paper-first/local-first
-> later reconciliation creates a new durable digital baseline
```

There is no mandatory end-session confirmation ritual.

MVP character creation/editing is manual structured data entry, not a guided/legal character builder.

## 4. PC Sheet PDF export

PC Sheet PDF export is a protected MVP capability available from:

- Player Android;
- authorized DM Android/tablet;
- authorized DM Desktop / PC Manager.

DM export generates the same Player-facing character sheet, not a special audit/admin document.

All visual families render the same canonical PC data.

Approved export families:

1. Classic D&D-style, independently designed;
2. Custom v1;
3. Custom v2 — per Attribute;
4. Custom v2 — per Ability.

The two v2 first pages are alternatives, not both mandatory.

Custom Attributes/Abilities are never silently omitted. When they exist, the user may choose:

- Extended Page only;
- App Modified Sheet;
- Modified Sheet + complete Extended Custom Statistics page.

Each visual family has its own matching Extended-page family. Overflow uses wrapping/moderate condensation down to a readability floor, then a visible continuation cue + matching Extended page. No silent truncation. Blank writable paper space remains useful.

Portrait export supports Crop-to-fill or Fit-entire-image. Missing uncached portrait does not block offline export; warn and leave the frame blank.

User chooses Permanent/Character Sheet state or Current Snapshot.

Output is a static local/offline PDF with Save/Share. No dedicated Print action, fillable-form requirement or app/export metadata footer.

Optional **Include Spell Descriptions** appends an application-designed Spellbook:

- grouped by spell level;
- alphabetized within level;
- index at front;
- only spells actually attached to the PC;
- full practical spell reference + description/scaling;
- PC-specific casting source/ability/DC/attack values where known.

Actual tracked character resources use suitable base-sheet space or matching Extended Resources/Options pages. The exporter does not invent unrelated subsystems merely because they can exist in D&D.

## 5. Identity, campaigns, roles and PC authority

Every person has one global account identity. Player/DM roles are campaign-scoped.

The same account may be DM in one campaign and Player in another, and may be DM while also owning/controlling a PC in the same campaign.

Campaign role, membership, PC ownership and PC control are distinct concepts.

A PC belongs to one campaign. Owner and current controller may differ. A PC may be unassigned where approved. Temporary control reassignment does not transfer ownership.

DM campaign authority does not imply PC ownership.

### 5.1 Default Player-to-Player PC visibility

Other campaign Players see only the deliberately tiny default identity projection:

- character name;
- portrait/avatar;
- current controller/display identity.

Full mechanical sheet data is not campaign-public by default.

### 5.2 Remembered login/offline

A configured device should remember authentication so ordinary launches do not require login every time. Reauthentication happens for real reasons such as logout, invalid/expired credentials or a sensitive action.

Locally available authorized data should remain usable offline where the workflow permits it.

Explicit sign-out/remove-account-from-device is required.

## 6. Invitations, membership and moderation

Campaign enrollment is DM-controlled through reusable campaign invitation links/codes until revoked/regenerated.

A signed-in valid invite joins directly; an unauthenticated user authenticates then continues the same invite flow. Joining does not automatically assign a PC.

A kicked user may rejoin with a valid invite. A banned user cannot rejoin until the ban is lifted. Rejoin preserves identity/history continuity.

Membership removal stops future hosted access/sync but does not silently wipe local cached data immediately.

Campaign moderation includes Freeze PC, Kick and Ban. Global Freeze Account belongs to System Administration, not ordinary campaign DM authority.

A frozen PC remains visible read-only to its owner/controller; DM may still inspect/administer under campaign authority.

## 7. Server/shared-state role

The server is the durable shared home/exchange/recovery point, **not an always-live game-session engine**.

There is no required formal technical `Game Session` entity.

Local Save never depends on Internet.

- Desktop: local Save + explicit Sync.
- Android: local Save immediately; opportunistic retry may occur plus manual Sync.

Visible sync states should distinguish saved locally, pending, synced, offline/waiting, failure/attention, remote changes and conflict.

Synchronization is deliberately project-specific using stable IDs, revisions, idempotent mutations, outbox/pending changes, tombstones, scoped pull/push and explicit human conflict handling where needed.

No generalized CRDT/automatic merge platform is required.

## 8. Durable data vs live working state

Preserve clear separation between reusable/durable definitions and live working copies:

- durable PC sheet != combat working state;
- creature/NPC definition != live combat participant;
- saved encounter != live encounter;
- reusable Personal content != independent Campaign copy;
- prepared Zone/Encounter Readiness != live changes during play.

Live edits do not silently mutate source definitions. If the DM discovers source data is wrong, fixing the source is a separate explicit action.

## 9. Personal, Campaign and Official content

Personal reusable DM content is private/creator-owned.

Using Personal content in a Campaign creates an independent Campaign copy with provenance. Later Personal edits do not silently rewrite the Campaign copy.

Campaign content is DM-controlled and DM-only by default unless a specific projection/reveal rule says otherwise.

Official SRD content is canonical, versioned/provenance-preserving and read-only. Customizing official material creates an independent custom copy with provenance.

## 10. DM Workspace and Desks

Each Campaign has one persistent live DM Workspace. There is no formal start-session/end-session ritual.

Desks are flat purpose-specific working environments and may reference one another. Multiple concurrent Desks of the same kind are allowed when real play requires it, especially Combat.

Approved Desk family:

1. **DM Screen**;
2. **Stage Desk**;
3. **Dungeon Desk**;
4. **Combat Desk**.

Opening a normal entity does not create a Desk.

### 10.1 DM Screen

General-purpose live DM surface including PC Group/Quick/Full access, Party Lens, general reference and the shared Player/DM natural-language rules question capability.

Party Lens is derived/read-only and should require no maintenance.

### 10.2 Stage Desk

Fast retrieval/navigation across the broad current adventure environment.

Core concepts:

- canonical Places projected through search/area/function/narrative/recent routes;
- Shops are specialized Places;
- NPCs are independent linked entities;
- lightweight Scene Spine supports adventure orientation/retrieval without becoming a quest engine;
- partial digital representation and paper references remain valid.

### 10.3 Dungeon Desk

Supports dynamic dangerous structured exploration using:

- topological/flowchart-like relationships, not VTT geometry;
- rich Zone Briefs;
- Presentar / Interactuar / Encuentro live grouping;
- player-safe vs DM-only information;
- contextual Notes;
- generic clocks/counters;
- Dungeon Turns beta;
- Encounter Readiness/staging;
- transition to Combat Desk.

Prepared areas are not identical to Dungeon Turn movement zones and areas are not 1:1 with encounters.

### 10.4 Combat Desk

Combat Desk is a practical tracker + tactical reminder assistant, not a VTT or automatic rules engine.

It may track NPC/monster HP/temp HP/conditions/concentration/removed state/notes and optional PC current HP. Same-group creatures may share initiative position while retaining individual live state.

Exactly one DM device is authoritative for an active combat at a time.

Authoritative actions commit locally first. Hosted sync is secondary/opportunistic. Internet loss does not stop the authoritative device.

Explicit DM-device resume/handoff is MVP so Desktop can replace an unavailable tablet. Resume uses the latest actually synchronized hosted state and advances authority generation/epoch so stale writes from the old authority cannot overwrite the new one.

Simultaneous authoritative tablet + Desktop editing is not required.

## 11. Dirty live improvisation lifecycle

Live improvisation may remain incomplete/ugly.

Typical lifecycle:

```text
live improv
-> Keep / preserve dirty snapshot
-> later Desktop cleanup
-> optionally promote to reusable content / new encounter / prepared material
-> archive or delete as appropriate
```

Do not force polishing during live play.

## 12. DM Desktop App

Desktop is one workbench with three major areas:

```text
LIVE / WORKSPACE
PREPARE / MANAGE
ADMINISTRATION
```

Desktop Live has the same game/domain semantics as DM Android/tablet but uses desktop-appropriate wide layouts, tabs, side panels, keyboard/mouse, shortcuts, drag/drop and dense tables.

Desktop must be capable of running the game if the tablet is unavailable.

## 13. Desktop Prepare / Manage

### 13.1 Monster Manager + Creature Creator Assistant

Supports Official/Personal/Campaign/dirty content, full human-usable stat information, ordered traits/actions/etc., DM tactics/guidance, import/export, independent copies/provenance, dirty-improv cleanup and simple reusable Packages.

Creature Creator Assistant is advisory only: benchmarks/ranges, role/durability/damage/control/mobility/action-economy observations and similar guidance. It does not certify `balanced/unbalanced` or enforce formulas against DM intent.

### 13.2 NPC Manager / Creator

One NPC may progress Quick -> Developed -> optional mechanics/stat block.

NPCs do not require combat statistics. The helper may ask useful questions/suggest ideas without requiring a giant dossier. Stage can project a compact `Run NPC` view.

### 13.3 Homebrew & Rules Manager

Supports Rules/Variants/Rulings plus structured custom races/sub-races, classes/subclasses, backgrounds, feats, spells, items/magic items and other justified content families.

Use structured editors where software consumes structure; rich text for human-use sections. Do not turn this into an automatic legality/rules engine.

### 13.4 Stage / Place / Dungeon / Zone preparation

Stage Manager handles Places/Shops/NPC links, retrieval organization and Scene Spine.

Dungeon Manager handles topology, rich Zone Briefs, Encounter Readiness, clocks and advisory triggers. No grid/LOS/movement simulation.

### 13.5 Encounter Manager

Saved Encounter != live encounter != Combat.

Saved encounter may include participant groups, expected/reserve/conditional participants, environment, hazards/interactives, guidance and references. Starting creates an independent live copy. Live changes never silently rewrite the template.

After play: discard live state, archive history or explicitly save as a new template.

### 13.6 PC Manager / Audit

DM can inspect authorized full PCs, review meaningful grouped history, directly correct as DM using compensating history, manage ownership/control/freeze/lifecycle/duplication and regenerate the Player-facing PDF sheet.

It is not a Desktop Player character builder.

### 13.7 Media & Handouts

Lightweight object-storage-backed library for images, handouts, maps, documents and other media.

Upload once/reference many. Stable logical asset identity. DM-only vs Player-safe distinction. Player-safe means safe to reveal, not automatically visible. Explicit reveal is separate.

## 14. Campaign Administration

Campaign Manager covers campaign identity/lifecycle, members/roles, invitations, moderation, PC assignment shortcuts, campaign-wide sync/status and links into content Managers.

Membership, campaign role, PC ownership and PC control remain distinct.

Active/Archived lifecycle is sufficient for MVP; destructive permanent-delete UX can remain cautious/later.

## 15. System Administration

The owner is the sole global administrator for the current personal project.

Desktop System Administration may provide practical application/operator functions for:

- accounts/global freeze;
- backups/recovery;
- storage/data overview;
- health/versions/last backup;
- useful provider operations/integrations;
- protected credentials/connections where genuinely useful.

Do not rebuild provider dashboards wholesale.

Prefer backend/project APIs. Direct provider admin API integration is allowed when it has concrete benefit. Any local provider credentials must use suitable OS/protected storage and never be hard-coded/committed/plaintext.

## 16. Audit, recovery, deletion and backup

History is meaningful grouped domain history, not exhaustive telemetry.

For important durable records distinguish:

1. current state;
2. meaningful audit/history;
3. selected recovery snapshots/checkpoints.

Restoring an older state creates a new current revision and preserves intervening history.

PCs receive strongest recovery. Campaign/Personal/saved-encounter content uses proportionate recovery. Live encounters persist only when deliberately archived. Canonical SRD is not user-deletable.

Important synchronized deletion uses tombstones to prevent stale offline resurrection.

Full verifiable server backup/export is MVP and must include durable relational state plus a complete asset recovery story, version/schema metadata and integrity/checksum information.

A polished one-click catastrophic whole-server restore UI is not required in MVP.

## 17. Rules sources and natural-language clarification

Supported official sources:

- SRD 5.1 — D&D 5e foundation;
- SRD 5.2.1 — revised/5.5e foundation.

Store official corpus as versioned/provenance-preserving PostgreSQL sections/chunks.

Initial retrieval uses PostgreSQL full-text search. Retrieved official material grounds a replaceable LLM integration, initially Cloudflare Workers AI.

Natural-language rules clarification is shared by Player and DM surfaces. MVP answers are Spanish and identify relevant source/version.

MVP clarification is **official SRD only**. Homebrew content may be authored/stored in MVP but is not silently injected into AI answers. House-rule-aware clarification remains later scope.

## 18. Object storage

Object storage is required for portraits, images, maps, handouts and documents.

Current technical recommendation is Cloudflare R2 Standard, pending owner/service activation when implementation reaches that dependency.

Domain records use stable logical asset identity; provider-specific keys remain infrastructure metadata.

Native clients must not hold durable storage-provider credentials.

## 19. Explicit MVP non-goals / deferred generalization

Current MVP does not require:

- VTT grid/token movement, LOS or fog-of-war;
- guided/legal character builder or comprehensive automatic legality enforcement;
- automatic combat resolution;
- automatic executable homebrew/rules engine;
- automatic encounter-balance authority/simulation;
- automatic combat-to-PC permanent mutation;
- simultaneous authoritative co-DM/multi-device combat editing;
- always-live formal Game Session server;
- WebSockets/generalized realtime as a requirement;
- Durable Objects or queues without concrete need;
- generic arbitrary ACL/RBAC framework;
- generalized sync/CRDT/auto-merge platform;
- homebrew-aware AI clarification;
- arbitrary non-SRD rules corpus;
- public/community marketplace/social network;
- support for every third-party import format;
- polished one-click whole-server restore UI;
- every-keystroke/universal event sourcing;
- enterprise observability/provider-dashboard clone;
- vector infrastructure without demonstrated retrieval need;
- autonomous multi-agent campaign management;
- generic RPG-system framework;
- a dedicated Journey Desk unless real play demonstrates it is distinct enough to deserve one.

Deferred technologies are not forbidden if implementation later proves one is the simplest safe way to satisfy an already-approved requirement.

## 20. Current implementation direction

Implementation is **AUTHORIZED and IN PROGRESS**.

The former Player runtime and `main` product/governance lines have been converged and validated. After promotion, `main` is the integrated trunk.

Current sequence:

```text
validated baseline convergence
-> Shared Integrated-MVP Spine
-> hosted foundation
-> Player <-> hosted integration
-> Desktop/admin/content architecture
-> Desktop authoring Managers
-> DM Live Workspace
-> live combat/public projection/handoff
-> SRD retrieval + grounded clarification
-> backup/recovery/operator completion
-> integrated owner-facing QA
```

The next technical package is the Shared Integrated-MVP Spine covering identity/account, Campaign/Membership/Role, PC owner/controller, stable IDs, revisions, tombstones, scope/provenance and basic audit/sync invariants.

Routine schema/API/class/migration/rendering/test/package choices are delegated. Return to the owner only for material product/scope/security/privacy/cost/lock-in/destructive-behavior decisions, external account/service actions or manual/physical QA gates.

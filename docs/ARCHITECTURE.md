# Architecture Record

## Current status

**Date:** 2026-09-14  
**Architecture state:** foundational stack retained; integrated Player + Server + DM MVP architecture consolidated under D-0071  
**Player runtime authority:** `implementation/phase4a-successor-cycle`  
**Current Player frozen physical candidate:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`  
**Implementation authorization:** not granted by the D-0071 documentation checkpoint alone

D-0034 through D-0043 remain the foundational stack/architecture set. D-0068 through D-0070 remain controlling for the DM Workspace/Desk product direction. D-0071 adds the current integrated-MVP authority/sync/recovery/Desktop scope and supersedes the older MVP exclusions explicitly identified below.

C-0009 remains controlling: choose the simplest safe architecture that satisfies concrete approved requirements.

## 1. Product topology

The product is one coherent application ecosystem:

```text
Player Android
      │
      ├──────────────┐
      │              │
      ▼              ▼
shared Kotlin     hosted API/data
logic/data          boundary
      ▲              ▲
      │              │
      ├──────────────┘
      │
DM Android/tablet + DM Desktop App
```

Android and Desktop share Kotlin domain/business/network/synchronization/persistence logic where that materially reduces duplication. They do **not** need shared UI, identical navigation or indiscriminate feature parity.

The DM Desktop App is now both a rich authoring/administration client and a complete functional DM operational fallback. This supersedes the older assumption that desktop combat was necessarily post-MVP.

## 2. Approved implementation technologies

### Android

- Kotlin;
- Jetpack Compose;
- native phone/tablet application;
- `minSdk 30 / Android 11`;
- phone and tablet are first-class form factors;
- portrait and landscape remain meaningful targets.

### Desktop

- Kotlin;
- Compose Multiplatform Desktop;
- native desktop/laptop DM application;
- meaningful local/offline use;
- local Save + explicit Sync;
- purpose-built keyboard/mouse/large-screen UX rather than copied Android layouts.

### Shared/local data

- shared Kotlin code only where genuinely useful;
- SQLite via SQLDelight for local durable state where offline/local-first behavior matters;
- stable globally unique IDs for mutable domain identity;
- explicit migrations and proportionate migration tests.

### Backend/data

- TypeScript Cloudflare Worker/API as project-owned backend boundary;
- Neon PostgreSQL for hosted relational durable data;
- Descope for authentication/identity proof only;
- application logic owns campaign/domain authorization;
- native clients never connect directly to Neon/PostgreSQL or hold database credentials.

### SRD AI

- official SRD 5.1 and SRD 5.2.1 stored as versioned/provenance-preserving PostgreSQL sections/chunks;
- PostgreSQL full-text retrieval first;
- retrieved supported chunks supplied to a replaceable LLM;
- Cloudflare Workers AI remains the initially approved provider unless a later consequential decision changes it;
- no vector database/general RAG platform without measured need.

## 3. Hosted application path

The basic path remains deliberately small:

```text
Android / Desktop
       │
       ▼
Cloudflare Worker/API
       │
       ├── authorization / sync / backup orchestration
       │
       ▼
Neon PostgreSQL
       │
       └── references to approved object storage for binary assets
```

Descope authenticates users before the application backend applies its own campaign/domain authorization.

The backend currently exists mostly as scaffold and must become real during the integrated MVP build; this is activation of the approved architecture, not a stack redesign.

## 4. Object storage — required, provider Pending

The integrated MVP has a concrete binary-file requirement. Object storage is therefore required.

The provider is **not selected by D-0071**. Cloudflare R2 is one possible candidate, not a default requirement merely because Cloudflare already hosts the Worker.

A first concrete asset is the PC portrait/avatar, which is part of the minimum campaign-visible PC identity and must be available to authorized devices.

Domain/application code should refer to stable assets, not provider-specific object keys. Useful asset metadata may include stable asset ID, owning/context relation, media type, size and checksum/integrity information. Provider-specific references remain localized in infrastructure code.

Do not build a generalized provider factory merely to make hypothetical migration elegant.

## 5. Paper/local/server authority model

### PCs and normal tabletop play

Paper remains authoritative during ordinary paper-first play.

The digital PC represents the latest intentionally saved/reconciled digital state and durable backup/reference. A perfectly synchronized digital copy may still be stale relative to changes written only on paper.

Therefore distinguish, where useful:

- **data freshness** — when the PC was intentionally reconciled/updated digitally;
- **sync freshness** — when that digital state last synchronized successfully.

The application cannot auto-merge paper changes it cannot observe.

### Server role

The server is the durable shared home/exchange point for synchronized application state, recovery data, shared content and reference corpora.

It is **not** an always-live authoritative game-session engine.

There is no required formal technical `Game Session` object. The DM Workspace remains a persistent campaign live-work environment under D-0068, not a start/end-session lifecycle object.

## 6. Domain boundaries

Keep these concepts distinct:

- global identity;
- campaign membership/role;
- PC existence;
- PC ownership;
- PC current control;
- reusable personal DM content;
- independent campaign copies;
- saved encounter template;
- live encounter working copy;
- live participant state;
- canonical official SRD reference;
- meaningful audit/history/recovery;
- binary asset identity;
- purely local presentation/navigation state.

### Reusable vs campaign copies

Personal reusable DM content is private by default. Using it in a campaign normally creates an independent campaign copy with source provenance. Changes do not silently propagate either direction.

### Durable definitions vs live copies

A live participant/encounter copy does not silently mutate its durable source. The DM must be able to distinguish `change live copy only` from `also correct permanent source`.

This preserves D-0068's dirty-improvisation lifecycle: use now, optionally preserve the dirty snapshot, clean/promote later in Desktop.

## 7. PC visibility/authority

A PC belongs to one campaign; owner and current controller are separate.

DM audit/correction/admin authority does not make the DM the owner.

Other campaign Players receive only the tiny default PC identity projection:

- character name;
- portrait/avatar;
- current controlling Player/display identity.

Mechanical/full-sheet data does not become campaign-public by default.

DM public combat projection is separate from PC-sheet authorization.

## 8. Synchronization architecture

Synchronization remains application-specific rather than a generalized platform.

### Save and Sync

- Local Save never depends on Internet.
- Desktop: Save locally, explicit Sync.
- Android: local-first Save; opportunistic retry/sync may occur plus manual Sync.
- failed Sync leaves local saved work intact.

### Required synchronization primitives

Use the already-approved small set as applicable:

- stable IDs;
- explicit record/revision/version checks;
- idempotent mutation identifiers/handling;
- local outbox/pending mutations where applicable;
- tombstones for synchronized deletion;
- scoped pull/push rather than universal full-account mirroring;
- human-visible conflict state;
- object-specific resolution instead of generalized auto-merge.

### Visible state

Relevant UI should distinguish states such as saved locally, pending sync, synced, offline/waiting, failure/attention, remote changes and conflict.

## 9. Combat authority architecture

Combat is intentionally special.

### Normal active combat

- one authoritative DM device at a time;
- every authoritative action commits locally first;
- hosted synchronization is secondary/opportunistic;
- public Player projection is based on the latest successfully synchronized DM state;
- Internet loss does not stop the authoritative DM device from continuing;
- older hosted state must never overwrite newer authoritative local state;
- Player temporary offline convenience remains non-authoritative/ephemeral where used.

### Sequence/version

Within an authority generation, authoritative combat changes use a monotonically increasing sequence/version sufficient to reject delayed older writes.

### New MVP authority resume/handoff

Desktop must be able to replace a lost/unavailable tablet as the operational DM client. Therefore an explicit authority-resume mechanism is now an MVP requirement and supersedes the older deferral.

Conceptually:

```text
old authoritative device
  authority_generation = 4
  local combat_sequence = 87

server latest synchronized
  generation = 4
  sequence = 84

Desktop explicitly resumes
  generation = 5
  recovered state = server generation 4 / sequence 84
  new writes use generation 5

stale later generation-4 writes are rejected
```

Exact storage/UI details remain implementation/design work, but the contract is approved:

- resume is explicit;
- only one current authority generation;
- stale old-device writes cannot silently take authority back;
- no simultaneous authoritative tablet/Desktop editing;
- no realtime/distributed-lock infrastructure is required merely for this bounded mechanism;
- state never synchronized from a lost device cannot be reconstructed.

## 10. Audit/history/recovery architecture

History should be meaningful, not exhaustive telemetry.

For important records distinguish:

1. current state;
2. meaningful grouped audit where required;
3. selected recovery checkpoints/snapshots.

Restoring old state creates a new current revision derived from the older version; it does not delete subsequent history.

Recovery strength is domain-specific:

- PCs strongest;
- important campaign records proportionate/lighter;
- personal reusable content normal/light;
- saved encounters recoverable;
- live encounters retained only when deliberately archived/kept;
- combat sequence history operational, not permanent blow-by-blow;
- canonical SRD system-managed.

Avoid every-keystroke/event logging, repeated binary copies in revisions and permanent transient-combat noise.

Tombstones prevent stale offline records from resurrecting deleted synchronized data.

## 11. Full server backup/export

A complete administrator-triggered server backup/export is part of the MVP foundation **now**, before valuable hosted data is trusted without an external recovery point.

The Desktop System Administration area requests the backup through the backend/API. It never receives direct database credentials.

A genuinely complete export must account for durable relational state and the binary/object-storage recovery story, including useful manifest/integrity information.

Backup metadata should include useful format/schema/application version and checksum/integrity information.

A polished one-click whole-server restore UI is not required yet. Restoration may initially be a controlled administrator procedure and must later receive stronger destructive-operation safeguards.

## 12. Identity, roles and remembered login

- one persistent global account identity;
- campaign-scoped roles;
- same account may be DM and owner/controller of a PC in the same campaign;
- surface switching does not change the authority under which an action is performed;
- normal devices remember authenticated login so users do not sign in every launch;
- local authorized data remains usable offline when the auth service cannot be reached;
- explicit sign-out/remove-account-from-device exists;
- campaign moderation and system administration remain separate.

Owner/Editor/Viewer is a useful vocabulary/future capability, but normal MVP authorization derives from domain relationships rather than arbitrary per-object ACL configuration.

Loss of campaign membership stops future hosted access/sync immediately but does not silently destroy local cached data at that instant.

Frozen PCs remain visible read-only to owner/controller and DM-administerable.

## 13. DM Desktop App architecture role

Desktop is no longer only a preparation companion.

It has two approved MVP responsibilities:

### Operational DM client

All approved D-0069 live Desks are functionally available on Desktop:

- DM Screen;
- Stage Desk;
- Dungeon Desk;
- Combat Desk.

Tablet and Desktop may have different layouts/interaction patterns. Capability availability for the DM is the important fallback contract.

### Rich authoring/administration client

Desktop is also the deliberate-authoring surface for persistent content consumed by the Desks, explicitly including:

- Monster Creator/Manager;
- NPC Creator/Manager;
- Homebrew Rules Input/Manager;
- Zone Creator/Manager;
- Encounter Creator/Manager;
- PC Manager/Audit;
- campaign/member/permission administration;
- System Administration.

Every persistent/content-oriented Desk capability needs sufficient Desktop authoring support, but do not create redundant screens merely for naming symmetry.

System Administration is permission-gated and distinct from campaign-DM authority.

## 14. Homebrew vs rules AI

Homebrew Rules authoring/storage is part of the Desktop MVP.

MVP natural-language clarification remains grounded only in the supported official SRD 5.1 / 5.2.1 corpus. House-rule-aware AI remains future work.

This separation allows campaign rules to exist/usefully be consulted without prematurely building mixed-source retrieval/adjudication logic.

## 15. Infrastructure proportionality / explicit non-generalization

Required now in bounded form:

- domain-specific authorization;
- project-specific synchronization;
- PostgreSQL/API implementation;
- object storage;
- meaningful audit/history where required;
- backup/export;
- HTTP/request-response and polling/refresh sufficient for approved flows.

Do **not** add merely because available:

- generalized realtime architecture;
- WebSockets;
- Durable Objects/equivalent room coordinators;
- queues without a concrete asynchronous-job need;
- generalized arbitrary ACL framework;
- generalized synchronization platform;
- elaborate history-retention/cold-storage machinery;
- public/community homebrew publishing infrastructure.

These may be reconsidered if an actual approved requirement makes the simpler design inadequate.

## 16. Implementation organization

D-0071 defines seven coordinated workstreams:

1. shared MVP spine/contracts;
2. Player stabilization/integration;
3. hosted foundation;
4. shared Kotlin data/sync;
5. DM Desktop product;
6. cross-client live-play exchange;
7. SRD retrieval/AI.

It also defines Waves 0–4. These are engineering dependency/integration controls, not separate owner-acceptance products.

The largest risk is semantic drift between Player, Server and DM. The implementation must preserve one coherent domain meaning, one evolving API/database contract and frequent real integration.

## 17. Testing consequence

Engineering testing occurs continuously. The next major owner-facing product acceptance QA is intended to exercise the complete integrated MVP across Player + hosted/shared services + DM tablet/Desktop.

Existing Player evidence remains valid for what it actually tested; the broader QA strategy does not erase historical proof.

## 18. Current unresolved architecture/design items

Pending consequential items include:

- object-storage provider;
- detailed DM Desktop App navigation/information architecture;
- detailed creator/manager interactions/schema where earlier Desk decisions intentionally left them open;
- exact UX/persistence details of authority resume/handoff;
- backup package format and later restore UX/process;
- detailed object-specific conflict UI;
- final shared rules-question product name/placement;
- complete post-MVP boundary.

## 19. Exact resume point

The project is intentionally paused after the 2026-09-14 documentation consolidation.

Resume with **7D — detailed DM Desktop App product definition**, preserving D-0068/D-0069 rather than reopening generic Desk taxonomy. Then perform **7E — exact outside-MVP boundary**, derive final Git/development topology and implementation gates, and obtain explicit coding authorization.

No application/server/database implementation is authorized merely by this architecture record.
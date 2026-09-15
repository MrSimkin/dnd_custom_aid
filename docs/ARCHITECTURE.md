# Architecture Record

## Current status

**Updated:** 2026-09-15  
**Architecture state:** integrated Player + hosted/shared + DM MVP architecture approved and implementation in progress  
**Normal implementation trunk:** `main`  
**Provider-neutral hosted/sync checkpoint:** `8248e7e2c0a34c67a4296f4abaf1effb0d76c8c3`  
**Hosted DEV provider activation:** COMPLETE / VERIFIED  
**Owner implementation authorization:** GRANTED

D-0034 through D-0043 remain the foundational stack/architecture set. D-0068 through D-0070 remain controlling for the DM Workspace/Desk direction. D-0071 through D-0075 control the integrated MVP, Desktop product, MVP scope/governance, PC Sheet PDF export and zero-budget/provider policy.

C-0009 remains controlling: choose the simplest safe architecture that satisfies concrete approved requirements.

For exact current implementation continuation, `docs/checkpoints/LATEST.md` and the checkpoint it references supersede older operational resume prose.

## 1. Product topology

The product is one coherent ecosystem:

```text
Player Android
      |
      +-------------------------+
      |                         |
      v                         v
shared Kotlin              hosted API/data
logic/data                  Cloudflare Worker
      ^                         |
      |                         +---- Descope identity proof
      |                         |
      +-------------------------+
      |                         v
DM Android/tablet          Neon PostgreSQL
+ DM Desktop App
```

Android and Desktop share Kotlin domain/business/network/synchronization/persistence logic where useful. They do not require shared UI or identical navigation.

The DM Desktop App is both a rich authoring/administration client and a complete functional DM operational fallback.

## 2. Approved implementation technologies

### Android

- Kotlin;
- Jetpack Compose;
- native phone/tablet app;
- `minSdk 30 / Android 11`;
- phone and tablet are first-class form factors.

### Desktop

- Kotlin;
- Compose Multiplatform Desktop;
- native DM application;
- meaningful local/offline use;
- local Save + explicit Sync;
- desktop-appropriate keyboard/mouse/wide-layout UX.

### Shared/local data

- Kotlin Multiplatform shared code where materially useful;
- SQLite via SQLDelight for local durable state where local-first/offline behavior matters;
- stable globally unique IDs;
- explicit migrations and proportionate migration tests.

### Backend/data

- TypeScript Cloudflare Worker/API as project-owned backend boundary;
- Neon PostgreSQL for hosted relational durable data;
- Descope for authentication/identity proof only;
- application logic owns campaign/domain authorization;
- native clients never connect directly to Neon or hold database credentials.

### Native HTTP

- Ktor Client shared Android/Desktop transport;
- provider-specific session/token acquisition remains at the platform edge;
- shared code consumes the existing `HostedAccessTokenProvider` boundary.

### SRD AI

- official SRD 5.1 and SRD 5.2.1 stored as versioned/provenance-preserving PostgreSQL sections/chunks;
- PostgreSQL full-text retrieval first;
- retrieved supported chunks supplied to a replaceable LLM;
- Workers AI remains the later approved initial direction only while safely usable at `$0`;
- no vector database/general RAG platform without measured need.

## 3. Real hosted DEV path

The current verified development path is:

```text
Android / Desktop
       |
       v
Cloudflare Worker/API
       |
       +---- Descope identity proof
       |
       v
Neon PostgreSQL
```

Activated DEV resources and real verification are recorded in `docs/checkpoints/2026-09-15_HOSTED_DEV_PROVIDER_ACTIVATION_COMPLETE.md`.

The current Worker code requires `DATABASE_URL` + `DESCOPE_PROJECT_ID`, with optional `DESCOPE_BASE_URL`. Database credentials live only in server-side/provider secret storage.

The representative authenticated `/v1/me` path has been verified against real Descope + Worker + Neon and observed at about 1 ms Worker CPU per visible invocation with no observed benchmark errors. This is a proof for the tested path, not a blanket performance guarantee for every future endpoint.

## 4. Paper/local/server authority model

### PCs and normal tabletop play

Paper remains authoritative during ordinary paper-first play.

The digital PC is the latest intentionally saved/reconciled digital state and durable backup/reference. A perfectly synchronized digital copy may still be stale relative to changes written only on paper.

Distinguish, where useful:

- **data freshness** — when the PC was intentionally reconciled/updated digitally;
- **sync freshness** — when that digital state last synchronized successfully.

### Server role

The server is the durable shared home/exchange point for synchronized application state, recovery data, shared content and reference corpora.

It is not an always-live authoritative game-session engine. There is no required formal technical `Game Session` object.

## 5. Domain boundaries

Keep these concepts distinct:

- global identity;
- campaign membership/role;
- PC existence;
- PC ownership;
- PC current control;
- reusable Personal DM content;
- independent Campaign copies;
- saved encounter template;
- live encounter working copy;
- live participant state;
- canonical Official SRD reference;
- meaningful audit/history/recovery;
- binary asset identity;
- purely local presentation/navigation state.

DM campaign authority does not imply PC ownership. PC owner and current controller may differ.

Other campaign Players receive only the approved tiny default PC identity projection; full mechanical sheet data is not campaign-public by default.

## 6. Synchronization architecture

Synchronization remains application-specific rather than a generalized platform.

### Save and Sync

- Local Save never depends on Internet.
- Desktop: Save locally, explicit Sync.
- Android: local-first Save; opportunistic retry/sync may occur plus manual Sync.
- failed Sync leaves local saved work intact.

### Required primitives

Use the bounded existing set:

- stable IDs;
- explicit revisions/version checks;
- idempotent mutation IDs;
- durable local outbox/pending mutations;
- tombstones for synchronized deletion;
- scoped pull/push;
- explicit conflict state;
- object-specific resolution rather than generalized auto-merge.

Preserve no-silent-overwrite semantics for stale/equal/local-ahead state and prevent stale tombstone resurrection.

## 7. Hosted PC persistence and authorization

Hosted current PC state uses the versioned application-owned Player serialization family as JSONB rather than duplicating the full SQLDelight character graph relationally.

Relational metadata owns authorization/revision/lifecycle concepts such as PC ID, campaign, owner, controller, revision, deletion state, snapshot format/version and timestamps.

Server-side authorization applies on protected reads/writes. DM authority remains distinct from ownership. Membership removal must stop future hosted access without silently deleting local cached/recovery data.

## 8. Object storage — required, provider deferred

The integrated MVP requires object storage for portraits/images/maps/handouts/documents, but the provider is **not currently selected/activated**.

R2 remains a candidate, not a default simply because Cloudflare hosts the Worker. At real Media/Handouts integration, perform a fresh D-0075 `$0` review.

Domain/application code should use stable logical asset identity; provider-specific keys remain infrastructure metadata. Native clients must not hold durable provider credentials.

## 9. Combat authority architecture

Combat remains intentionally local-first and single-authority:

- one authoritative DM device at a time;
- authoritative actions commit locally first;
- hosted synchronization is secondary/opportunistic;
- Internet loss does not stop the authoritative device;
- older hosted state must never overwrite newer authority;
- explicit resume/handoff advances an authority generation/epoch;
- stale old-device writes cannot silently regain authority;
- simultaneous authoritative tablet + Desktop editing is not required.

State never synchronized from a lost device cannot be reconstructed.

## 10. Audit/history/recovery

For important records distinguish:

1. current state;
2. meaningful grouped audit where required;
3. selected recovery checkpoints/snapshots.

Restoring old state creates a new current revision derived from the older version; it does not erase intervening history.

Tombstones prevent stale offline records from resurrecting deleted synchronized data.

Avoid every-keystroke/event logging, repeated binary copies in revisions and permanent transient-combat noise.

## 11. Full server backup/export

A complete administrator-triggered server backup/export remains MVP.

Desktop System Administration requests backup through the backend/API and never receives direct database credentials.

A complete export must account for durable relational state plus the object-storage recovery story when assets exist, with useful version/schema metadata and integrity/checksum information.

A polished one-click catastrophic restore UI is not required for MVP.

## 12. Identity, roles and remembered login

- one persistent global account identity;
- campaign-scoped roles;
- same account may be DM and owner/controller of a PC in the same campaign;
- surface switching does not change authority;
- configured devices should remember authenticated login;
- local authorized data remains usable offline where workflow permits;
- explicit sign-out/remove-account-from-device exists;
- campaign moderation and system administration remain separate.

The next Player <-> Server package wires real remembered Android Descope session/token acquisition into the existing shared token-provider seam.

## 13. DM Desktop architecture role

Desktop has two approved MVP responsibilities:

### Operational DM client

All approved live Desks are functionally available:

- DM Screen;
- Stage Desk;
- Dungeon Desk;
- Combat Desk.

### Rich authoring/administration client

Desktop is the deliberate-authoring surface for persistent content including Monster/Creature, NPC, Homebrew/Rules, Stage/Place/Scene, Dungeon/Zone, Encounter, PC Manager/Audit, Campaign Administration, System Administration and Media/Handouts.

System Administration is permission-gated and distinct from campaign-DM authority.

## 14. Infrastructure proportionality

Required in bounded form:

- domain-specific authorization;
- project-specific synchronization;
- PostgreSQL/API implementation;
- object storage when assets reach integration;
- meaningful audit/history where required;
- backup/export;
- ordinary HTTP/request-response/polling where sufficient.

Do **not** add merely because available:

- generalized realtime architecture;
- WebSockets;
- Durable Objects/room coordinators;
- queues without a concrete asynchronous-job need;
- generalized arbitrary ACL framework;
- generalized synchronization/CRDT platform;
- elaborate enterprise observability.

## 15. Security architecture state

D-0075 controls the hard **USD $0** budget, intentional public repository and secret hygiene.

Current verified good properties include server-side DB credentials, real authenticated Worker verification and application-owned identity mapping/authorization boundaries.

Open security residuals include:

- JWT/fail-closed review;
- object-level authorization regression coverage;
- SQL/query and error/log hygiene;
- replay/idempotency authorization;
- exact investigation of the locally reported **3 high severity npm vulnerabilities**;
- evaluation of a dedicated least-privilege Neon runtime role instead of the current project-owner runtime credential;
- production Descope region/configuration review.

Do not run `npm audit fix --force` blindly or make destructive live privilege changes without a deliberate plan.

## 16. Current implementation organization

`main` is the integrated trunk. Use short-lived outcome-oriented branches and frequent reintegration.

Current implementation sequence:

```text
completed convergence
-> completed Shared Integrated-MVP Spine
-> completed provider-neutral hosted foundation
-> completed real DEV provider activation
-> NOW: real authenticated Player <-> Server integration
-> Desktop/admin/content/live waves
-> combat exchange/handoff
-> SRD clarification
-> backup/operator completion
-> integrated owner QA
```

The next primary package is the Player <-> Server integration described in `docs/checkpoints/LATEST.md`.

No new owner authorization is required for routine implementation. Return to the owner only for material product/scope/security/privacy/cost/lock-in/destructive behavior, new external account/service actions or manual/physical QA gates.

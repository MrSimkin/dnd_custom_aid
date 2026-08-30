# Architecture Record

## Current status

**Phase:** Phase 2 — Technical Options and Foundation / Architecture & Technology Evaluation  
**Architecture state:** Partially decided; evaluation is **active**. Overall multi-client target topology, MVP desktop delivery, native Android client approach, Android local persistence technology, and local combat/outbox persistence semantics are approved, but the application architecture/technology set is not complete.  
**Application code:** Not scaffolded.  
**Reason:** Phase 1 is complete and the approved product/MVP baseline is detailed enough to evaluate consequential architecture and technology alternatives against real requirements (D-0010, D-0011, D-0033).

The project has reached the architecture-evaluation gate, **not** the implementation gate.

## Architecture decision gate

Do not choose or scaffold unresolved synchronization/reconciliation semantics, backend/provider, minimum Android version, PDF library, SRD retrieval/clarification approach, API protocol/style, desktop native framework, or other unresolved foundational technology merely to begin coding.

The approved product baseline includes:

- Android as the primary live/table surface for players and DMs;
- an intentionally narrower desktop/laptop DM preparation/administration surface using the same shared domain data, without MVP feature-parity or desktop-combat requirements;
- a true native desktop application as an expected later product feature in addition to a web-capable desktop surface;
- multicampaign membership, campaign selection and campaign-scoped roles/permissions;
- paper-first live character authority plus a durable freshness-visible digital baseline;
- complete human-readable monster stat blocks with selective structured mechanics and additive future enrichment;
- local-first authoritative DM combat persistence;
- combat-aware hosted synchronization and recovery rather than generic last-write-wins;
- provisional/non-authoritative offline player combat views that yield to DM state on reconnection;
- shared durable campaign/domain data hosted online;
- campaign-scoped moderation separated from application-wide account administration;
- campaign invitation/rejoin semantics;
- PDF generation/export semantics;
- official-SRD-only MVP natural-language clarification across both supported SRDs with source provenance;
- mixed/homebrew campaign content without rules enforcement;
- personal/small-scale hosting and cost expectations;
- future-extensibility constraints that must not become present scope creep.

The technical evaluation must be discussed with the owner, including realistic alternatives, trade-offs and a recommendation. Consequential choices require owner approval before becoming project truth or being used to scaffold implementation.

## Approved Phase 2 architecture decisions so far

### A-0001 — Multi-client target shape

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

The target product shape is a **multi-client architecture**:

- a dedicated Android client;
- a web-capable desktop administration client;
- an expected future **true native desktop client**.

These clients are separate product surfaces centered on the same durable shared domain/backend. One client must not be treated as the implementation foundation of the others merely to maximize UI/code reuse.

#### Consequences

- Android may be optimized for phone/tablet and local-first live combat requirements.
- Web administration may be optimized for large-screen preparation/data-entry workflows.
- A future native desktop client should be able to become a first-class client without requiring replacement of the shared durable domain/backend solely because the web client existed first.
- Shared contracts/service boundaries are expected to matter, but this decision does **not** select REST, GraphQL, RPC, WebSockets, a backend SDK, or any other protocol/mechanism.
- This decision does **not** require three clients to be implemented in MVP. The true native desktop client remains later product scope.

### A-0002 — MVP desktop administration is local-web/localhost

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

For the MVP, the desktop DM administration web client will be delivered **locally on the user's PC through a localhost application/server** rather than requiring public frontend hosting.

The intended finished-user experience is launcher/application-like: the user should not need to run development commands manually merely to use the admin surface.

The local web client may require Internet connectivity to access shared durable application data. Desktop offline synchronization is **not** an MVP requirement.

The web client should remain suitable for later hosted deployment without fundamental product/client redesign.

#### Consequences

- “Local web” refers to where the desktop UI is served/run, not to relocating shared authoritative campaign/domain data onto the Windows PC.
- Do not turn the localhost launcher/server into a second authoritative backend merely because it is local.
- A later hosted web deployment should be an evolution of the web client rather than a total rewrite where practical.
- A later true native desktop application remains an independent expected client under A-0001.
- Frontend public-hosting cost is therefore not an MVP requirement; hosted shared-data/backend cost constraints remain a separate architecture decision.

### A-0003 — Native Android client: Kotlin + Jetpack Compose

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

The Android client will be developed **natively in Kotlin using Jetpack Compose** as its UI toolkit.

The Android application begins as an Android-focused client rather than a Flutter, React Native, or shared-UI Compose Multiplatform application.

Kotlin Multiplatform remains a possible later, selective technique for extracting/sharing non-UI logic **only if real reuse with another client justifies it**.

#### Consequences

- Android-specific phone/tablet UX, lifecycle, local persistence, networking and offline-combat behavior may use the native Android platform directly.
- Cross-platform UI reuse is not an architectural goal because A-0001 intentionally treats Android, web and future native desktop as separate product surfaces.
- Do not introduce Kotlin Multiplatform complexity speculatively. Re-evaluate it only when concrete reusable logic exists and another implemented client would benefit.
- This decision does **not** select minimum Android version, local database/persistence technology, navigation details, dependency injection, networking library, backend/API style, synchronization implementation, or project/module structure.

### A-0004 — Android structured local persistence uses Room / SQLite

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

The Android client will use **Room**, backed by SQLite, as its primary structured on-device persistence technology.

Room is responsible for durable relational Android data that must survive process death/restart, including authoritative local live-combat state and other structured local/cached data that later approved repository/synchronization design requires.

Small settings/preferences may later use DataStore where appropriate, but DataStore is not the primary domain/combat store.

The local Room schema is **not required to mirror the hosted database schema**. Local and hosted storage have different responsibilities and may use different representations while preserving explicit domain/contracts between layers.

#### Consequences

- Live combat state can be committed to durable local storage before network synchronization.
- Android UI/domain reads that must remain offline-capable can be backed by a local source of truth rather than depending on a successful network request.
- Room migrations must be treated as real persistent-data migrations once implementation begins.
- Do not introduce SQLDelight or another cross-platform persistence abstraction merely because a future native desktop client is planned; the future desktop client may choose its own local persistence technology.
- This decision does **not** yet select synchronization scheduling, WorkManager usage, hosted database/provider, API style, or conflict-resolution protocol.

### A-0005 — Active combat uses durable current state plus a synchronization outbox

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

Active combat uses a **hybrid current-state + durable outbox model** on Android.

Room stores the durable current live-combat state/snapshot used for normal reads, rendering and same-device recovery. Every locally committed combat change that requires remote synchronization also creates a durable pending synchronization operation in an outbox **as part of the same local database transaction**.

The UI may treat the DM action as locally saved only after the current-state mutation and corresponding required outbox entry have committed together.

Each outbox operation must have a stable unique identity and explicit encounter-local ordering/revision information sufficient for later ordered, retryable and idempotent synchronization semantics.

The outbox is **synchronization infrastructure**, not a permanent combat-history log and not an event-sourced domain model. Successfully synchronized operations may later be removed or compacted according to the synchronization design.

#### Consequences

- A process/app interruption after a local DM action does not lose either the resulting current combat state or the knowledge that remote synchronization may still be pending.
- Retrying a network request can later be made idempotent because the same logical operation retains the same operation identity.
- Encounter-local sequence/revision information is used for ordering rather than depending on device wall-clock timestamps as the authority.
- Current combat state remains directly queryable; the application does not reconstruct normal state by replaying a permanent event history.
- Whole-state snapshots may still be transmitted or stored where useful; A-0005 does not require every remote sync to be a fine-grained command.
- A-0005 does **not** yet determine server-side authority tokens/epochs, acknowledgement protocol, remote revision handling, snapshot-repair behavior, synchronization scheduling/WorkManager policy, or player public-projection transport. Those are the next synchronization/reconciliation decisions.

### Product requirement carried into architecture evaluation

`docs/architecture/2026-08-30_FUTURE_DESKTOP_REQUIREMENT.md` records that a true native desktop application is an expected future product feature. That requirement predates and constrains A-0001; it does not itself select implementation technology.

## Active evaluation order

Architecture evaluation began with **overall application topology and surface relationship**, not with a framework name.

Current sequence:

1. ~~overall Android + desktop/laptop topology and shared-domain relationship~~ — approved in A-0001;
2. ~~Android client approach~~ — approved in A-0003;
3. ~~MVP desktop/laptop administration delivery approach~~ — approved in A-0002;
4. ~~Android structured local persistence technology~~ — approved in A-0004;
5. ~~local-first combat state/change persistence and synchronization queue semantics~~ — approved in A-0005;
6. **combat synchronization/reconciliation and player public projection**;
7. multicampaign domain/data-model boundaries;
8. hosted backend/database/authentication/authorization and moderation boundaries;
9. PDF generation/rendering;
10. SRD corpus storage/retrieval/clarification and provenance;
11. testing/build/CI and durable module/project conventions.

### Current decision under evaluation

**Combat synchronization/reconciliation and DM authority semantics.**

The next comparison should define how the hosted side recognizes the authoritative DM combat writer, orders/acknowledges retries, prevents stale remote state from overwriting newer local DM state, and recovers synchronization after reconnect while preserving the approved one-authority model. Player public projection should then be defined against that authoritative synchronized state.

## Evaluation criteria

Evaluate options against criteria such as:

- fit to approved product workflows;
- Android phone/tablet UI quality;
- desktop/laptop administration usability without unnecessary parity work;
- coherent multicampaign data isolation/selection and campaign-scoped permissions;
- one-authority local-first DM combat behavior;
- same-device combat persistence/recovery;
- combat-aware synchronization where older remote state cannot overwrite newer authoritative DM state;
- public player-projection synchronization and provisional offline-player reconciliation;
- authentication/authorization fit and security implications;
- separation of campaign moderation from global application administration;
- PDF rendering/generation suitability;
- SRD 5.1 + SRD 5.2.1 storage/retrieval/provenance and Spanish clarification suitability;
- freedom to store mixed/homebrew game content without turning the application into a rules enforcer;
- complete monster representation with future additive mechanic enrichment;
- maintainability;
- testability;
- stability/maturity;
- documentation and ecosystem quality;
- suitability for AI-assisted implementation;
- performance requirements if known;
- dependency/service lock-in risk;
- privacy implications;
- personal-scale/no-cost hosting feasibility where practical;
- build reproducibility;
- long-term migration cost and approved incremental extensibility.

Do not treat this list as implying that every criterion is equally important; importance depends on the approved product design.

## Important non-requirements

Architecture must not be selected on the false assumption that MVP requires:

- Android/desktop full feature parity;
- a player desktop application;
- desktop combat tracking;
- desktop offline synchronization;
- seamless concurrent multi-device DM combat editing;
- a full executable D&D rules engine;
- automatic paper/digital conflict resolution;
- house-rule-aware MVP rules clarification;
- co-DMs within one campaign;
- multiple RPG systems in MVP;
- implementation of the future native desktop client during MVP;
- Kotlin Multiplatform or shared UI simply because a future native desktop client is planned;
- identical local and hosted database schemas;
- permanent event sourcing for combat merely because an outbox exists.

## Future architecture record format

As the architecture set is completed, this file should record:

1. **Approved architecture summary**
2. **Decision IDs / architecture decision IDs**
3. **Alternatives considered**
4. **Why the chosen approach fits the approved design**
5. **Important trade-offs / rejected alternatives**
6. **Application surfaces and module/layer responsibilities**
7. **Multicampaign domain/data boundaries**
8. **Data flow**
9. **Persistence/networking/synchronization model**
10. **Offline/local-first DM combat and authority strategy**
11. **Player public-projection/offline-reconciliation strategy**
12. **Phone/tablet adaptation strategy**
13. **Desktop/laptop administration strategy**
14. **Authentication/authorization/moderation strategy**
15. **Testing strategy relationship**
16. **Known constraints / debt**
17. **Migration considerations**

D-0009 remains pending until the consequential architecture/technology set is sufficiently resolved; these approved sub-decisions do not imply that the entire architecture has already been selected.

## Convention relationship

Architecture and project-structure conventions that become durable must also be recorded in `docs/CONVENTIONS.md` when appropriate. The owner should be consulted when such a convention first becomes relevant.
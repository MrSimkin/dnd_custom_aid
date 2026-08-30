# Architecture Record

## Current status

**Phase:** Phase 2 — Technical Options and Foundation / Architecture & Technology Evaluation  
**Architecture state:** Partially decided; evaluation is **active**. A-0001 through A-0007 are approved, covering multi-client topology, MVP localhost desktop delivery, native Android, Room/SQLite local persistence, local combat outbox semantics, DM combat authority/reconnect semantics, and evolvable multicampaign/reuse domain boundaries. The broader architecture/technology set is not complete.  
**Application code:** Not scaffolded.  
**Reason:** Phase 1 is complete and the approved product/MVP baseline is detailed enough to evaluate consequential architecture and technology alternatives against real requirements (D-0010, D-0011, D-0033), with additional approved evolution constraints in `docs/PRODUCT_EVOLUTION_REQUIREMENTS.md`.

The project has reached the architecture-evaluation gate, **not** the implementation gate.

## Architecture decision gate

Do not choose or scaffold unresolved hosted backend/provider, hosted database, authentication/authorization implementation, minimum Android version, PDF library, SRD retrieval/clarification approach, API protocol/style, desktop native framework, web framework/launcher runtime, or other unresolved foundational technology merely to begin coding.

The approved product baseline includes:

- Android as the primary live/table surface for players and DMs;
- an intentionally narrower desktop/laptop DM preparation/administration surface using the same shared domain data, without MVP feature-parity or desktop-combat requirements;
- a true native desktop application as an expected later product feature in addition to a web-capable desktop surface;
- multicampaign membership, campaign selection and campaign-scoped roles/permissions;
- deliberate future cross-campaign reuse/link/update/transfer capabilities defined in `docs/PRODUCT_EVOLUTION_REQUIREMENTS.md`;
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

The technical evaluation must be discussed with the owner, including realistic alternatives, trade-offs, practical examples, reversibility/migration consequences, and a recommendation. Consequential choices require owner approval before becoming project truth or being used to scaffold implementation.

Approvals are revisable under `docs/GOVERNANCE.md`; later requirements or clearer understanding may trigger deliberate amendment/supersession rather than blind preservation of an earlier architecture choice.

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
- This decision does **not** yet select synchronization scheduling, WorkManager usage, hosted database/provider, API style, or generic conflict-resolution protocol.

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

### A-0006 — Single authoritative DM combat writer with ordered acknowledgement and repair

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner

Each active encounter has one explicit **authoritative DM device/session identity**. The hosted service accepts authoritative live-combat mutations only from that current authority.

Authoritative operations use the stable operation identities and encounter-local monotonic ordering/revision information established by A-0005. The hosted side tracks accepted/acknowledged progress so a reconnecting authoritative client can safely resend missing operations. Duplicate retries of the same operation must be idempotent rather than applying the logical change twice.

On reconnect, an older hosted snapshot must **never automatically overwrite newer authoritative local DM state** merely because the hosted copy is online. Normal recovery first brings the hosted copy forward using the authoritative client's pending ordered operations.

If operation replay cannot restore a coherent hosted state, the current valid authoritative DM may perform an **authoritative full-snapshot repair**, replacing/repairing the hosted live-combat copy from the current local authoritative snapshot together with the corresponding authority/revision context.

Authority does **not** expire merely because connectivity is lost. The current DM device remains locally authoritative while offline. Another device cannot silently seize live-combat authority in MVP.

The authority identity and required local metadata are persisted sufficiently for **same-device restart/recovery**. Future cross-device takeover/transfer must be explicit rather than inferred from timeouts or last-writer behavior and remains later scope.

Player combat clients are not authoritative combat writers. They consume the latest synchronized public projection. Any player-side offline tracker edits remain provisional/local and yield to the synchronized authoritative DM projection on reconnect; they must not enter the authoritative DM mutation stream.

#### Consequences

- Generic last-write-wins is explicitly unsuitable for authoritative live combat.
- Server acknowledgement/retry semantics must understand operation identity and encounter ordering rather than relying on timestamps alone.
- A stale DM device/session lacking current authority cannot repair or authoritatively mutate hosted live combat.
- Network outages do not create automatic authority expiration, avoiding split-brain simply because the server temporarily cannot see the current tablet.
- MVP deliberately prefers one writer plus explicit future handoff over simultaneous authoritative DM editing.
- The exact token format, API protocol, transport, authentication mechanism, server implementation and synchronization scheduler remain unresolved implementation choices.

### A-0007 — Explicit, evolvable ownership/context boundaries with deliberate cross-campaign reuse

**Status:** Approved  
**Date:** 2026-08-30  
**Decision owner:** Project owner  
**Approval note:** Owner explicitly requested that this decision remain reviewable under `docs/GOVERNANCE.md` if later implications prove misunderstood or undesirable.

The domain must keep **ownership/context boundaries explicit** without turning those boundaries into permanent walls that prevent legitimate future cross-campaign continuity.

The architecture distinguishes conceptually between:

- application/user identity and global account state;
- campaign participation, roles, permissions, moderation and campaign-specific context/state;
- reusable personal-library/source content;
- official/versioned reference content;
- live-session encounter state.

This is a **domain-boundary principle, not a frozen database schema**.

#### Reusable NPC/creature behavior

The same conceptual NPC or creature may legitimately appear in multiple campaigns, including simultaneous campaigns.

The model must preserve the possibility of deliberate reuse relationships such as:

1. **independent copy** — campaign use can diverge from the source;
2. **linked + manual update** — provenance/linkage is retained and an explicit future **Update from Library** action can refresh/adopt changes;
3. **selected live link** — some records may deliberately follow future source-library changes automatically.

Automatic propagation must be intentional/explicit. Sharing an origin or identity must not by itself cause hidden cross-campaign mutation.

Campaign-specific context/state may differ even when a reusable/canonical NPC identity is shared across campaigns.

Exact MVP support for these three modes is not implied; `docs/PRODUCT_EVOLUTION_REQUIREMENTS.md` records them as expected future capabilities that the architecture should not structurally prevent.

#### Characters across campaigns

Character identity must not be structurally imprisoned inside one campaign forever.

The future product must be able to support both:

- **copying** a character into another campaign when an independent resulting character is desired; and
- explicitly **moving/transferring the same character** between campaigns while retaining durable identity and relevant continuity/history.

The exact rules for simultaneous campaign participation, historical association, audit visibility, permissions and ownership/control during movement remain future product/design decisions. A-0007 requires architectural feasibility, not premature implementation of those workflows.

#### Official/reference content

Immutable/versioned official source content may retain stable source/version identity and provenance. Source/version should be explicit rather than silently mutating historical meaning.

#### Live-session isolation remains unchanged

A live encounter remains campaign-associated but is an independent runtime working state under D-0031/A-0005/A-0006. Editing a library source, campaign NPC/monster definition, or saved encounter must not silently rewrite an encounter already in progress, even when the source relationship is otherwise live-linked outside the running encounter.

#### Reversibility/evolution consequence

Future requirements may refine the schema, add relationship types, introduce linking/update/transfer workflows, or require data migrations. Those changes are allowed. The architecture should avoid both extremes:

- hidden coupling where changes unexpectedly leak across campaigns; and
- rigid isolation where legitimate shared identity, updating or transfer becomes a fundamental rewrite.

### Product requirements carried into architecture evaluation

- `docs/architecture/2026-08-30_FUTURE_DESKTOP_REQUIREMENT.md` records that a true native desktop application is an expected future product feature.
- `docs/PRODUCT_EVOLUTION_REQUIREMENTS.md` records expected future cross-campaign reuse/link/update/transfer capabilities.
- `docs/GOVERNANCE.md` requires active contradiction detection and deliberate review of approvals when later evidence exposes misunderstood implications.

These requirements constrain architecture evaluation without automatically expanding MVP implementation scope.

## Active evaluation order

Architecture evaluation began with **overall application topology and surface relationship**, not with a framework name.

Current sequence:

1. ~~overall Android + desktop/laptop topology and shared-domain relationship~~ — approved in A-0001;
2. ~~Android client approach~~ — approved in A-0003;
3. ~~MVP desktop/laptop administration delivery approach~~ — approved in A-0002;
4. ~~Android structured local persistence technology~~ — approved in A-0004;
5. ~~local-first combat state/change persistence and synchronization queue semantics~~ — approved in A-0005;
6. ~~combat authority/reconnect semantics~~ — approved in A-0006;
7. ~~multicampaign/reuse domain boundaries~~ — approved in A-0007;
8. **hosted backend/database/authentication/authorization/moderation boundaries and API/service approach**;
9. player public-projection transport/delivery details and synchronization scheduling where still unresolved;
10. minimum Android version and remaining Android infrastructure choices where needed;
11. local web implementation framework/launcher details;
12. PDF generation/rendering;
13. SRD corpus storage/retrieval/clarification and provenance;
14. testing/build/CI and durable module/project conventions.

### Current decision under evaluation

**Hosted backend/database/authentication/authorization foundation.**

The next comparison should evaluate realistic personal-scale/no-cost hosted approaches against the approved requirements, including relational domain data, multicampaign authorization, invitation/moderation rules, client-independent service boundaries, future native/web clients, combat authority/revision semantics, backup/migration/lock-in, and operational burden.

No provider or API style is selected merely because it appears as a candidate.

## Evaluation criteria

Evaluate options against criteria such as:

- fit to approved product workflows;
- Android phone/tablet UI quality;
- desktop/laptop administration usability without unnecessary parity work;
- coherent multicampaign data isolation/selection and campaign-scoped permissions;
- deliberate cross-campaign reuse/link/update/transfer evolution;
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
- implementation of every future library-link/update/PC-transfer workflow in MVP;
- Kotlin Multiplatform or shared UI simply because a future native desktop client is planned;
- identical local and hosted database schemas;
- permanent event sourcing for combat merely because an outbox exists;
- authority expiry solely from temporary connectivity loss.

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

If a later concrete requirement exposes that an approved architecture decision was based on a misunderstood implication, follow `docs/GOVERNANCE.md`: surface the conflict, explain reversibility/cost, and deliberately amend/supersede rather than silently preserving or silently violating the old decision.

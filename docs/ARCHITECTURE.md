# Architecture Record

## Current status

**Phase:** Phase 2 — Technical Options and Foundation / Architecture & Technology Evaluation  
**Architecture state:** Partially selected; evaluation remains **active**.  
**Application code:** Not scaffolded.  
**Reason:** Phase 1 is complete and the owner has approved the hosted backend/provider topology (D-0034), Android client approach (D-0035), native desktop administration delivery approach (D-0036), and multicampaign domain/data boundaries (D-0037). Remaining consequential architecture choices must still be evaluated before implementation scaffolding.

The project remains at the architecture-evaluation gate, **not** the implementation gate.

## Approved architecture choices so far

### Hosted backend/provider topology — D-0034

- **Neon** provides hosted PostgreSQL as the durable shared relational database.
- **Cloudflare** provides stable application infrastructure where appropriate, including backend/API execution, database connectivity/pooling, object storage and realtime transport/coordination.
- **Descope** provides end-user authentication only.
- Domain authorization remains project-owned in application logic/PostgreSQL; Descope does not own campaign roles, PC ownership/control or moderation semantics.
- The initial architecture must not depend on Neon beta/preview backend features merely because they are temporarily free.
- Irreplaceable domain truth must not be moved into Cloudflare or Descope.
- Provider boundaries should preserve practical migration paths.
- Cloudflare Access is a possible later authentication-consolidation candidate only if the needed native/non-browser path is stable/GA and fits Android requirements.

### Android client — D-0035

- Native **Kotlin** is the Android implementation language.
- **Jetpack Compose** is the Android UI toolkit.
- Phone and tablet layouts must be adaptive rather than treating tablet as a stretched phone UI.
- Flutter and React Native are not selected for the initial Android app.
- Straightforward domain/business code should avoid unnecessary Android coupling where this preserves useful future sharing without forcing cross-platform UI parity.

### DM desktop administration — D-0036

- The DM desktop/laptop preparation and administration companion is a native **Kotlin + Compose Multiplatform Desktop** application.
- Native packaging, installers and update/distribution overhead are explicitly accepted by the owner and are not considered material disadvantages for this project.
- The owner values avoiding unnecessary continuous-online dependency; the desktop architecture should therefore permit meaningful local/offline operation.
- Native implementation does not itself settle local persistence, offline data scope or synchronization/conflict behavior; those remain separate architecture decisions.
- Android and desktop may share Kotlin domain/business/networking/synchronization code selectively where useful, but they must not be forced into shared UI or feature parity.
- Cloudflare remains backend infrastructure; a hosted browser frontend is no longer required for the desktop companion.

### Multicampaign domain/data boundaries — D-0037

- One shared relational PostgreSQL domain model is used; separate database/schema-per-campaign tenancy is not selected.
- Internal user identity is global and separate from external Descope identity.
- Campaign participation and DM/player role are campaign-membership relationships rather than global user properties.
- Characters belong to exactly one campaign; existence, ownership and current control are separate relationships.
- Mutable personal reusable content and campaign-specific content are distinct. Campaign use normally creates an independent copy retaining provenance rather than a live mutable cross-scope reference.
- Immutable/versioned official SRD material may be referenced canonically and copied when customized.
- Saved encounter templates, live encounter working copies, durable character-sheet state, grouped character audit/history and live combat state remain distinct entities/domains.
- Mutable entities should use stable globally unique identities suitable for Android/desktop/hosted synchronization.
- Exact revision, outbox, tombstone and conflict/reconciliation mechanics remain unresolved and belong to the next architecture decision.

These approvals resolve only their corresponding portions of D-0009. D-0009 remains Pending until the remaining foundational choices are approved.

## Architecture decision gate

Do not choose or scaffold remaining unresolved technologies merely to begin coding. Consequential choices still requiring owner approval include, as applicable:

- local/offline persistence technology and scope for Android and desktop;
- synchronization/reconciliation implementation details;
- minimum Android version;
- PDF generation/rendering technology;
- SRD storage/retrieval/clarification architecture;
- testing/build/CI and durable module/project conventions.

The approved product baseline includes:

- Android as the primary live/table surface for players and DMs;
- a native desktop/laptop DM preparation/administration companion using the same shared domain data, without MVP feature-parity or desktop-combat requirements;
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

## Active evaluation order

Approved evaluation sequence:

1. overall Android + desktop/laptop topology and shared-domain relationship;
2. Android client approach;
3. desktop/laptop administration delivery approach;
4. multicampaign domain/data-model boundaries;
5. local-first combat persistence, combat authority and synchronization/reconciliation;
6. hosted backend/database/authentication/authorization and moderation boundaries;
7. PDF generation/rendering;
8. SRD corpus storage/retrieval/clarification and provenance;
9. testing/build/CI and durable module/project conventions.

### Current decision under evaluation

**Local persistence, offline data scope, synchronization and reconciliation across Android and desktop.**

The domain boundaries now establish what is global, campaign-scoped, character-scoped, reusable personal content, durable character state and live combat working state. The next architecture decision must determine how Android and desktop persist useful data locally, which operations work offline, how local changes are queued/synchronized, and how conflicts are handled without violating the already-approved DM-authoritative combat model.

This evaluation should distinguish normal durable-domain synchronization from live-combat authority rather than attempting to solve every entity with one generic last-write-wins rule.

## Evaluation criteria

Evaluate options against criteria such as:

- reliable same-device recovery for active DM combat;
- meaningful offline desktop preparation and Android use without continuous connectivity;
- stable local relational persistence where appropriate;
- selective sharing of Kotlin persistence/domain/sync code without forcing UI parity;
- explicit local mutation/outbox behavior and retry semantics;
- predictable delete/tombstone behavior;
- conflict detection for ordinary durable data;
- stricter DM-authoritative reconciliation for live combat;
- player public-projection behavior that never overwrites DM authority;
- sync indicators/freshness understandable to users;
- campaign-selective caching/synchronization rather than one account-wide blob;
- migration/testability and AI-assisted implementation;
- avoidance of beta/preview foundational dependencies where stable alternatives exist.

## Important non-requirements

Architecture must not be selected on the false assumption that MVP requires:

- Android/desktop full feature parity;
- a player desktop application;
- desktop combat tracking;
- seamless concurrent multi-device DM combat editing;
- a full executable D&D rules engine;
- automatic paper/digital conflict resolution;
- house-rule-aware MVP rules clarification;
- co-DMs within one campaign;
- multiple RPG systems in MVP.

## Future architecture record format

When the architecture set is sufficiently complete, this file should record:

1. **Approved architecture summary**
2. **Decision IDs** from `docs/DECISIONS.md` and dedicated architecture decision checkpoints pending consolidation
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

## Convention relationship

Architecture and project-structure conventions that become durable must also be recorded in `docs/CONVENTIONS.md` when appropriate. The owner should be consulted when such a convention first becomes relevant.
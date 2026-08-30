# Architecture Record

## Current status

**Phase:** Phase 2 — Technical Options and Foundation / Architecture & Technology Evaluation  
**Architecture state:** Partially selected; evaluation remains **active**.  
**Application code:** Not scaffolded.  
**Reason:** Phase 1 is complete and the owner has approved the hosted backend/provider topology (D-0034) and Android client approach (D-0035). Remaining consequential architecture choices must still be evaluated before implementation scaffolding.

The project remains at the architecture-evaluation gate, **not** the implementation gate.

## Approved architecture choices so far

### Hosted backend/provider topology — D-0034

- **Neon** provides hosted PostgreSQL as the durable shared relational database.
- **Cloudflare** provides stable application infrastructure where appropriate, including web hosting, backend/API execution, database connectivity/pooling, object storage and realtime transport/coordination.
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
- Kotlin Multiplatform / Compose Multiplatform is not required initially; straightforward domain code should avoid unnecessary Android coupling where that preserves a reasonable future extraction path.

These approvals resolve only their corresponding portions of D-0009. D-0009 remains Pending until the remaining foundational choices are approved.

## Architecture decision gate

Do not choose or scaffold remaining unresolved technologies merely to begin coding. Consequential choices still requiring owner approval include, as applicable:

- desktop/laptop administration delivery form and web/native technology;
- local/offline persistence technology;
- synchronization/reconciliation implementation details;
- multicampaign domain/data boundaries;
- minimum Android version;
- PDF generation/rendering technology;
- SRD storage/retrieval/clarification architecture;
- testing/build/CI and durable module/project conventions.

The approved product baseline includes:

- Android as the primary live/table surface for players and DMs;
- an intentionally narrower desktop/laptop DM preparation/administration surface using the same shared domain data, without MVP feature-parity or desktop-combat requirements;
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

**Desktop/laptop administration delivery approach.**

The Android client approach and hosted backend/provider topology are approved. The next comparison should determine whether the intentionally narrower DM preparation/administration surface should be delivered as a normal browser-based web application, native desktop application, local web application, or another approach that materially fits the requirements better.

No option is selected merely by being listed.

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
- seamless concurrent multi-device DM combat editing;
- a full executable D&D rules engine;
- automatic paper/digital conflict resolution;
- house-rule-aware MVP rules clarification;
- co-DMs within one campaign;
- multiple RPG systems in MVP.

## Future architecture record format

When the architecture set is sufficiently complete, this file should record:

1. **Approved architecture summary**
2. **Decision IDs** from `docs/DECISIONS.md`
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
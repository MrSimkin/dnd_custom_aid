# Architecture Record

## Current status

**Architecture state:** Not selected; evaluation is ready to begin after Phase 1 merge closure.  
**Application code:** Not scaffolded.  
**Reason:** The approved product/MVP baseline is now sufficiently coherent to evaluate consequential architecture and technology alternatives against real requirements (D-0010, D-0011, D-0033).

This is intentional. The project has reached the architecture-evaluation gate, not the implementation gate.

## Architecture decision gate

Do not choose or scaffold an Android framework, language, UI toolkit, desktop implementation approach, persistence layer, sync/backend approach, minimum Android version, hosted provider, PDF library, SRD retrieval/clarification approach, or major architecture pattern merely to begin coding.

The approved product baseline now provides enough information to evaluate technical alternatives against real needs, including:

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
- SRD-only MVP natural-language clarification across both supported official SRDs with source provenance;
- mixed/homebrew campaign content without rules enforcement;
- personal/small-scale hosting and cost expectations;
- future-extensibility constraints that must not become present scope creep.

The technical evaluation must be discussed with the owner, including realistic alternatives, trade-offs, and a recommendation. Consequential choices require owner approval before becoming project truth or being used to scaffold implementation.

## Evaluation order

Architecture evaluation should begin with **overall application topology and surface relationship**, not with a framework name.

Recommended order:

1. overall Android + desktop/laptop topology and shared-domain relationship;
2. Android client approach;
3. desktop/laptop administration delivery approach;
4. multicampaign domain/data model boundaries;
5. local-first combat persistence, combat authority and synchronization/reconciliation;
6. hosted backend/database/authentication/authorization and moderation boundaries;
7. PDF generation/rendering;
8. SRD corpus storage/retrieval/clarification and provenance;
9. testing/build/CI and durable module/project conventions.

## Evaluation criteria for architecture choices

Evaluate options against criteria such as:

- fit to approved product workflows;
- Android phone/tablet UI quality;
- desktop/laptop administration usability without unnecessary parity work;
- coherent multicampaign data isolation/selection and campaign-scoped permissions;
- one-authority local-first DM combat behavior;
- same-device combat persistence/recovery;
- combat-aware synchronization where older remote state cannot overwrite newer authoritative DM state;
- public player projection synchronization and provisional offline-player reconciliation;
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

## Important non-requirements during architecture evaluation

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

When an architecture choice is approved, this file should record:

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

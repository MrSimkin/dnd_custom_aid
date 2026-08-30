# Architecture Record

## Current status

**Architecture state:** Not selected; evaluation is ready to begin.  
**Application code:** Not scaffolded.  
**Reason:** The approved product/MVP baseline is now sufficiently coherent to evaluate consequential architecture and technology alternatives against real requirements (D-0010, D-0011).

This is intentional. The project has reached the architecture-evaluation gate, not the implementation gate.

## Architecture decision gate

Do not choose or scaffold an Android framework, language, UI toolkit, desktop implementation approach, persistence layer, sync/backend approach, minimum Android version, hosted provider, PDF library, SRD retrieval/clarification approach, or major architecture pattern merely to begin coding.

The approved product baseline now provides enough information to evaluate technical alternatives against real needs, including:

- player and Dungeon Master workflows;
- expected phone and tablet interactions;
- desktop/laptop administration requirements;
- approved MVP boundaries and explicit exclusions;
- local/offline DM combat persistence;
- shared-data and synchronization requirements;
- account/authentication/authorization and moderation needs;
- PDF generation/export semantics;
- SRD-only natural-language clarification with source provenance;
- personal/small-scale hosting and cost expectations;
- future-extensibility constraints that must not become present scope creep.

The technical evaluation must be discussed with the owner, including realistic alternatives, trade-offs, and a recommendation. Consequential choices require owner approval before becoming project truth or being used to scaffold implementation.

## Evaluation criteria for architecture choices

Evaluate options against criteria such as:

- fit to approved product workflows;
- Android phone/tablet UI quality;
- desktop/laptop administration usability;
- offline/local-data robustness for the DM combat tracker;
- synchronization and recovery behavior;
- authentication/authorization fit and security implications;
- PDF rendering/generation suitability;
- SRD storage/retrieval/provenance and clarification suitability;
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

## Future architecture record format

When an architecture choice is approved, this file should record:

1. **Approved architecture summary**
2. **Decision IDs** from `docs/DECISIONS.md`
3. **Alternatives considered**
4. **Why the chosen approach fits the approved design**
5. **Important trade-offs / rejected alternatives**
6. **Application surfaces and module/layer responsibilities**
7. **Data flow**
8. **Persistence/networking/synchronization model**
9. **Offline DM combat strategy**
10. **Phone/tablet adaptation strategy**
11. **Desktop/laptop administration strategy**
12. **Testing strategy relationship**
13. **Known constraints / debt**
14. **Migration considerations**

## Convention relationship

Architecture and project-structure conventions that become durable must also be recorded in `docs/CONVENTIONS.md` when appropriate. The owner should be consulted when such a convention first becomes relevant.

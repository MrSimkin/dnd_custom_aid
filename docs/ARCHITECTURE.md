# Architecture Record

## Current status

**Architecture state:** Not selected.  
**Application code:** Not scaffolded.  
**Reason:** Product discovery and interaction/design must precede consequential technology-stack selection (D-0011).

This is intentional, not missing work.

## Architecture decision gate

Do not choose an Android framework, language, UI toolkit, persistence layer, sync/backend approach, minimum Android version, or major architecture pattern merely to begin coding.

Before architecture is selected, the project should have enough approved product/design information to evaluate technical alternatives against real needs, including as applicable:

- player and Dungeon Master workflows;
- expected phone and tablet interactions;
- MVP boundaries;
- data/offline/privacy expectations;
- game-system/content constraints;
- major usability requirements;
- sharing/sync requirements if any.

The technical evaluation must then be discussed with the owner, including realistic alternatives, trade-offs, and a recommendation. Consequential choices require owner approval before becoming project truth.

## Evaluation criteria for future architecture choices

When the design is sufficiently defined, evaluate options against criteria such as:

- fit to approved product workflows;
- phone/tablet UI quality;
- maintainability;
- testability;
- stability/maturity;
- documentation and ecosystem quality;
- suitability for AI-assisted implementation;
- offline/local-data requirements;
- sync/network requirements if applicable;
- performance requirements if known;
- dependency/lock-in risk;
- privacy/security implications;
- build reproducibility;
- long-term migration cost.

Do not treat this list as implying that every criterion is equally important; importance depends on the approved product design.

## Future architecture record format

When an architecture choice is approved, this file should record:

1. **Approved architecture summary**
2. **Decision IDs** from `docs/DECISIONS.md`
3. **Alternatives considered**
4. **Why the chosen approach fits the approved design**
5. **Important trade-offs / rejected alternatives**
6. **Module/layer responsibilities**
7. **Data flow**
8. **Persistence/networking model** if applicable
9. **Phone/tablet adaptation strategy**
10. **Testing strategy relationship**
11. **Known constraints / debt**
12. **Migration considerations**

## Convention relationship

Architecture and project-structure conventions that become durable must also be recorded in `docs/CONVENTIONS.md` when appropriate. The owner should be consulted when such a convention first becomes relevant.

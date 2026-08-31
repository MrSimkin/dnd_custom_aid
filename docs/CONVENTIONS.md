# Project Conventions

This file records durable working conventions approved by the project owner.

A convention is different from a product decision: it defines **how the project is worked on or expressed consistently**, rather than what the product does.

## 1. Authority

- New conventions must be discussed with the owner when they first become relevant.
- The agent should explain realistic alternatives, practical consequences, and its recommendation.
- Once the owner approves a convention and it is recorded here, agents should follow it without repeatedly asking the same question.
- A convention may be changed later, but the reason and replacement must be recorded.
- If a convention conflicts with an approved decision in `docs/DECISIONS.md`, the approved decision takes precedence.

## 2. Approved conventions

### C-0001 — Canonical accepted branch

**Status:** Approved  
**Date:** 2026-08-28

`main` represents the latest accepted project state. Substantial work normally occurs on a focused branch and is merged after owner approval.

Source decision: D-0007.

### C-0002 — Meaningful technical work is explained

**Status:** Approved  
**Date:** 2026-08-28

The agent performing technical work explains what it is doing, why it is doing it, and important consequences or alternatives. The owner is not required to approve every line-level detail, but meaningful technical approaches must not be hidden.

Source decision: D-0008.

### C-0003 — First-use convention discussion

**Status:** Approved  
**Date:** 2026-08-28

When a new durable coding, naming, structure, formatting, testing, documentation, or similar convention becomes relevant, the agent presents the realistic alternatives and a recommendation to the owner before establishing the convention. Once approved and recorded, the convention is reused without repeated approval unless a change is justified.

Source decision: D-0008.

### C-0004 — Design precedes stack selection

**Status:** Approved  
**Date:** 2026-08-28

Product and interaction design is explored and discussed before the Android technology stack is selected. Architecture must be evaluated against approved design needs rather than chosen in advance.

Source decision: D-0011.

### C-0005 — Git stores operative memory

**Status:** Approved  
**Date:** 2026-08-28

Information required to continue the project must be persisted in the repository. Chat history is not an authoritative substitute.

Source decision: D-0012.

### C-0006 — Product Spanish; technical project English

**Status:** Approved  
**Date:** 2026-08-28

All end-user-facing application UI, labels, messages, help/rules responses, exports intended for users, and other product-facing content should be Spanish unless a specific exception is later approved.

Source code, identifiers, technical repository documentation, architecture/testing notes, development instructions, commit/PR technical prose, and agent-oriented project documentation should be English.

The purpose is to keep the user experience fully accessible to the intended Spanish-speaking users while keeping development aligned with the predominantly English Android/software ecosystem and reference material.

### C-0007 — SRD source provenance and user-facing D&D edition labels

**Status:** Approved  
**Date:** 2026-08-29

Technical/project material, data provenance and rules-source identity must use the official document versions:

- **SRD 5.1** — the earlier/2014-era fifth-edition rules foundation;
- **SRD 5.2.1** — the revised/2024-era fifth-edition rules foundation.

End-user Spanish UI and rules answers should use the familiar generation labels:

- **D&D 5e** for the earlier/2014-era generation;
- **D&D 5.5e** for the revised/2024-era generation.

The user-facing label must not erase source provenance. Internally, the exact SRD document/version remains identifiable for retrieval, attribution, comparison and rules-assistance logic.

In technical discussion with the owner, agents may mention both forms when useful, for example `D&D 5.5e (SRD 5.2.1)`.

Source decisions: D-0017 and D-0023.

### C-0008 — Explain relational/data-model decisions with SQL when useful

**Status:** Approved  
**Date:** 2026-08-30

When discussing database schemas, queries, migrations, synchronization metadata or other relational/data-model behavior with the owner, agents should show concise representative SQL whenever it materially improves understanding.

Framework annotations, ORM abstractions or implementation terminology should not be used as the only explanation when the same idea can be made clearer through SQL. SQL examples are explanatory unless an approved schema/migration explicitly makes them authoritative implementation artifacts.

This convention exists because SQL is a useful technical communication language for the owner and helps them review architecture decisions directly.

Source decision: D-0038 and explicit owner instruction during its approval.

### C-0009 — Personal-scale proportionality; avoid enterprise overengineering

**Status:** Approved  
**Date:** 2026-08-30  
**Clarified:** 2026-08-30 by the pre-main architecture proportionality audit

The project is a personal, deliberately limited tool. Architecture, security, observability, administration, deployment and data-management mechanisms should be the **simplest approach that safely satisfies approved requirements**.

Do not add enterprise-grade layers, generalized infrastructure, elaborate role hierarchies, duplicated safeguards, speculative scale machinery or operational processes merely because they are common in commercial SaaS systems. Add complexity only when a concrete requirement, measurable risk or real implementation problem justifies it.

Concrete consequences for implementation include:

- selecting a provider or platform does **not** authorize scaffolding all of its available services;
- start with ordinary HTTP/request-response behavior and simple polling/refresh before realtime infrastructure;
- do not create provider-abstraction factories/frameworks merely to make a hypothetical future migration elegant; localize vendor-specific code sensibly instead;
- do not build a generalized synchronization platform when small application-specific Save/Sync/outbox/revision behavior is sufficient;
- offline capability is selective and should exist where it materially benefits real workflows, not as a universal requirement for every feature;
- prefer simple human conflict handling for rare genuine concurrent edits over speculative automatic merge systems;
- project documentation should remain sufficient for continuity but should not grow into ceremony for routine reversible implementation choices.

When presenting technical decisions to the owner, prioritize the materially relevant trade-offs and avoid exhaustive treatment of enterprise-only concerns unless they could realistically affect this project.

Source decisions: D-0038, D-0039, D-0043 and repeated explicit owner instruction during Phase 2.

### C-0010 — Intended-device acceptance and repeatable post-build QA

**Status:** Approved  
**Date:** 2026-08-30

Manual feature acceptance should be performed first on the device/form factor for which that feature is primarily intended. A secondary form factor may receive a proportional sanity check when useful, but it does not replace testing on the intended device.

After a build reaches a manual-testable state, the project should use a **defined, repeatable QA checklist/test suite** rather than inventing acceptance checks ad hoc each time. The suite should combine:

- a small persistent regression core for already accepted behavior;
- feature-specific checks for the current build/slice;
- persistence/restart or migration checks when the build changes durable data;
- intended-device usability checks appropriate to the feature;
- explicit recording of pass/fail, concrete defects and non-blocking observations.

The QA suite should remain proportional under C-0009: it is a practical repeatable acceptance tool, not an enterprise test-management process. As accepted features accumulate, the persistent regression core should grow only where regression would materially matter.

Examples of intended-device priority:

- player character-sheet workflows: phone first;
- DM combat tracker/live DM board: tablet first;
- DM preparation/administration workflows: desktop first.

A build should not be described as manually accepted until the relevant intended-device QA suite has been executed successfully or any deviations are explicitly recorded and approved.

Source: explicit owner instruction during Phase 4 character-foundation testing.

## 3. Conventions intentionally not chosen yet

No convention has yet been approved for:

- programming language style;
- identifier/naming style beyond whatever a chosen language strongly requires;
- detailed package/module naming beyond the few high-level areas approved by D-0043;
- UI/component naming;
- formatting tool;
- linting tool;
- test naming;
- commit-message format beyond being descriptive;
- branch naming pattern beyond using focused non-`main` branches for substantial work;
- dependency-management style;
- detailed application presentation architecture pattern.

These should be discussed only when they first become materially relevant rather than invented prematurely.

## 4. Adding a convention

Use this compact format:

```text
### C-NNNN — Name
Status: Approved
Date: YYYY-MM-DD

Convention statement.

Why / consequences if useful.
Source decision: D-NNNN, if applicable.
```

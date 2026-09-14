# Project Conventions

This file records durable working conventions used consistently across the project.

A convention is different from a product decision: it defines **how the project is worked on or expressed consistently**, rather than what the product does.

## 1. Authority

D-0073 clarifies the owner-vs-technical boundary and supersedes older process wording that required the owner to approve every new low-level implementation convention.

Current rule:

- owner approval is required for conventions that materially affect product behavior, UX, game semantics, privacy/security, compatibility, cost, irreversible lock-in or approved scope;
- routine low-level coding/naming/package/testing/serialization conventions may be selected and recorded by the technical assistant/Worker when needed;
- technical agents should explain meaningful approaches in practical terms, but should not ask the owner to rubber-stamp implementation minutiae;
- once a convention is recorded, follow it consistently unless a concrete reason justifies changing it;
- a convention never overrides a later specific Approved product/architecture decision.

## 2. Approved conventions

### C-0001 — Canonical accepted branch

**Status:** Approved / currently transitioning under D-0073  
**Date:** 2026-08-28  
**Clarified:** 2026-09-14

Historically, `main` represents the canonical accepted/integrated project state and substantial work normally occurs on focused branches.

Current temporary exception: before D-0073's planned convergence, `main` is global integrated-MVP product/architecture truth while `implementation/phase4a-successor-cycle` remains authoritative Player runtime/QA truth.

After successful validated convergence, `main` again becomes the normal single integrated-MVP trunk. See `docs/BRANCH_STATUS.md`.

Source decisions: D-0007, D-0073.

### C-0002 — Meaningful technical work is explained

**Status:** Approved  
**Date:** 2026-08-28  
**Clarified:** 2026-09-14

The agent performing technical work explains what it is doing, why it is doing it, and important owner-relevant consequences. The owner is not required to approve line-level or routine low-level implementation details.

Transparency does not mean transferring engineering responsibility back to the owner.

Source decisions: D-0008, D-0073.

### C-0003 — First-use convention handling

**Status:** Approved / clarified  
**Date:** 2026-08-28  
**Clarified:** 2026-09-14

Original rule required presenting every new durable coding/naming/structure/testing/documentation convention to the owner before establishing it.

D-0073 narrows that requirement:

- if a convention has meaningful product, UX, privacy/security, cost, compatibility, irreversible-lock-in or scope consequences, explain realistic alternatives/recommendation and obtain owner approval;
- if it is a routine reversible engineering convention, the technical assistant/Worker may choose and record it without owner rubber-stamping;
- once established and recorded, reuse it consistently unless evidence justifies changing it.

Source decisions: D-0008, D-0073.

### C-0004 — Design precedes stack selection

**Status:** Approved  
**Date:** 2026-08-28

Product and interaction design is explored before consequential technology choices. Architecture is evaluated against approved design needs rather than chosen in advance.

Current integrated-MVP product design and architecture are already substantially closed under D-0071/D-0072/D-0073, so implementation should not reopen them without a concrete reason.

Source decision: D-0011.

### C-0005 — Git stores operative memory

**Status:** Approved  
**Date:** 2026-08-28

Information required to continue the project must be persisted in the repository. Chat history is not an authoritative substitute.

Source decision: D-0012.

### C-0006 — Product Spanish; technical project English

**Status:** Approved  
**Date:** 2026-08-28  
**Clarified:** 2026-09-01

All end-user-facing application UI, labels, messages, help/rules responses, exports intended for users, and other product-facing content should be Spanish unless a specific exception is later approved.

Source code, identifiers, technical repository documentation, architecture/testing notes, development instructions, commit/PR technical prose, and agent-oriented project documentation should be English.

Technical working conversations between the owner and technical assistants/coding agents should also be conducted in English by default. Spanish end-user UI labels/product text may be quoted verbatim inside those English technical conversations when useful.

### C-0007 — SRD source provenance and user-facing D&D edition labels

**Status:** Approved  
**Date:** 2026-08-29

Technical/project material, data provenance and rules-source identity use official document versions:

- **SRD 5.1** — earlier/2014-era fifth-edition foundation;
- **SRD 5.2.1** — revised/2024-era fifth-edition foundation.

End-user Spanish UI/rules answers use familiar generation labels:

- **D&D 5e** for the earlier/2014-era generation;
- **D&D 5.5e** for the revised/2024-era generation.

The user-facing label must not erase exact SRD source/version provenance.

Source decisions: D-0017, D-0023.

### C-0008 — Explain relational/data-model decisions with SQL when useful

**Status:** Approved  
**Date:** 2026-08-30  
**Clarified:** 2026-09-14

When discussing database schemas, queries, migrations, synchronization metadata or other relational/data-model behavior with the owner, show concise representative SQL when it materially improves understanding.

D-0073 does **not** require the owner to approve routine schema/table details. SQL remains a useful explanatory language when a data-model choice has owner-relevant consequences or the owner asks to understand it.

Framework annotations/ORM abstractions should not be the only explanation when representative SQL would be clearer.

Source decisions: D-0038, D-0073 and explicit owner instruction.

### C-0009 — Personal-scale proportionality; avoid enterprise overengineering

**Status:** Approved  
**Date:** 2026-08-30  
**Clarified:** 2026-08-30

The project is a personal, deliberately limited tool. Architecture, security, observability, administration, deployment and data-management mechanisms should be the **simplest approach that safely satisfies approved requirements**.

Do not add enterprise-grade layers, generalized infrastructure, elaborate role hierarchies, duplicated safeguards, speculative scale machinery or operational processes merely because they are common in commercial SaaS systems.

Concrete consequences include:

- selecting a provider does not authorize scaffolding all its services;
- start with ordinary HTTP/request-response/polling before generalized realtime;
- do not create speculative provider-abstraction factories;
- do not build a generalized sync platform when application-specific outbox/revision behavior is sufficient;
- offline capability is selective where workflows benefit;
- prefer simple human conflict handling for rare genuine concurrent edits over speculative auto-merge;
- documentation must preserve continuity without becoming ceremony for routine reversible implementation choices.

Source decisions: D-0038, D-0039, D-0043 and repeated owner instruction.

### C-0010 — Intended-device acceptance and repeatable post-build QA

**Status:** Approved  
**Date:** 2026-08-30

Manual feature acceptance should be performed first on the device/form factor where its real use matters. A secondary form factor may receive proportional sanity checking but does not replace testing on the intended device.

After a build reaches a manual-testable state, use a defined repeatable QA checklist rather than inventing acceptance checks ad hoc each time. Combine:

- a small persistent regression core for already accepted behavior;
- feature-specific checks;
- persistence/restart/migration checks when durable data changes;
- intended-device usability checks;
- explicit pass/fail/defect/non-blocking observations.

Keep the suite proportional under C-0009.

Examples:

- Player character-sheet workflows: phone first;
- DM combat/live board: tablet first, with Desktop fallback also exercised where relevant;
- DM preparation/administration: Desktop first.

A build is not manually accepted until relevant intended-device QA is executed successfully or deviations are explicitly recorded/accepted.

Source: explicit owner instruction during Phase 4 testing; expanded integrated context under D-0071/D-0073.

## 3. Technical conventions intentionally chosen only when needed

Do not invent a giant style guide up front.

Items such as detailed identifier naming, presentation architecture, linting/formatting choices, test naming or package layout may be selected by technical agents when implementation makes them relevant, provided they remain consistent with D-0073/C-0009 and are recorded if future contributors need the convention.

Escalate only when the convention creates an owner-consequential tradeoff.

## 4. Adding a convention

For owner-consequential conventions:

```text
### C-NNNN — Name
Status: Approved
Date: YYYY-MM-DD

Convention statement.
Why / consequences if useful.
Source decision: D-NNNN, if applicable.
```

For routine technical conventions, record them in the most proportionate technical documentation/location rather than manufacturing an `Approved` owner decision when no owner decision was required.
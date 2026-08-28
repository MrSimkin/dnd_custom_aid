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

## 3. Conventions intentionally not chosen yet

No convention has yet been approved for:

- programming language style;
- identifier/naming style beyond whatever a future chosen language strongly requires;
- package/module structure;
- UI/component naming;
- formatting tool;
- linting tool;
- test naming;
- commit-message format beyond being descriptive;
- branch naming pattern beyond using focused non-`main` branches for substantial work;
- dependency-management style;
- documentation language/style beyond clarity and durable continuity;
- application architecture pattern.

These must be discussed when they become relevant rather than invented prematurely.

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

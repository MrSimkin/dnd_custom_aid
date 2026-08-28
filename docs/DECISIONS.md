# Decision Log

This file records durable project decisions. A decision is authoritative only when its status is **Approved** (or **Superseded** by a newer approved decision).

## Status meanings

- **Approved** — explicitly accepted by the owner and currently authoritative.
- **Proposed** — recommended but not yet accepted.
- **Pending** — requires an owner decision.
- **Superseded** — replaced by a newer approved decision.
- **Rejected** — explicitly declined.

---

## D-0001 — Android is the target platform

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The product will be an Android application.

### Consequences

- Development decisions must prioritize Android compatibility.
- No iOS, web, or desktop version should be assumed unless separately approved later.

---

## D-0002 — Phone and tablet support

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The application must be designed for use on both Android phones and Android tablets.

### Consequences

- UI decisions must consider more than one screen class.
- Tablet behavior must not be treated as an afterthought once UI implementation begins.

---

## D-0003 — Player and Dungeon Master audiences

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The application is intended to serve both players and Dungeon Masters.

### Consequences

- Future discovery must identify which functions are shared and which differ by role.
- No permissions or role-switching behavior is implied yet.

---

## D-0004 — Repository-based continuity

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The repository must contain the information needed for any future chat, AI, or coding agent to understand the current project state and continue from there.

### Consequences

- Chat memory is not sufficient as the project record.
- State, decisions, unresolved questions, testing status, and next actions must be kept in repository files.
- Significant sessions must leave a durable handoff.

---

## D-0005 — AI/agents perform technical execution

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

The owner is not expected to perform the coding. AI/coding agents may implement code, run tests/checks, diagnose issues, refactor, and carry out approved feature changes.

### Consequences

- Repository instructions must be understandable to agents as well as humans.
- Build/test execution should be automatable wherever practical.
- Technical documentation must be sufficient to reproduce development work.

---

## D-0006 — Significant product decisions remain with the owner

**Status:** Approved  
**Date:** 2026-08-28  
**Decision owner:** Project owner

Significant product decisions are made by the owner. Agents may recommend and explain options, but should not silently turn an unresolved product choice into an implementation assumption.

### Consequences

- Important unknowns are recorded as pending decisions.
- Agents should explain trade-offs in accessible language because the owner is not expected to be technical.
- Reversible low-level implementation choices may be made during approved implementation, but consequential choices must be surfaced.

---

## D-0007 — `main` as canonical published branch

**Status:** Proposed  
**Date:** 2026-08-28  
**Proposed by:** Initial repository foundation

Use `main` as the canonical published project state. Substantial work is prepared on branches and merged after review/approval.

### Why proposed

This makes it easier for a fresh agent to know which version represents the accepted project rather than an experiment in progress.

### Owner decision required

Approve, modify, or reject this workflow.

---

## D-0008 — Decision boundary for routine implementation

**Status:** Proposed  
**Date:** 2026-08-28  
**Proposed by:** Initial repository foundation

Allow agents to make routine, reversible, low-level implementation choices that do not materially change product behavior, while requiring owner approval for significant product, UX, data/privacy, service/cost, compatibility, rules-content, and expensive-to-reverse architecture decisions.

### Why proposed

Requiring the owner to approve every variable name, library helper, refactor, or equivalent low-level coding choice would make agent-led development impractical. The proposed boundary preserves owner control over meaningful decisions without forcing technical micromanagement.

### Owner decision required

Approve, tighten, or loosen this boundary.

---

## D-0009 — Android implementation technology

**Status:** Pending  
**Date:** 2026-08-28

No framework, language, UI toolkit, persistence approach, or minimum Android version has been chosen yet.

### Decision should consider

- long-term maintainability;
- quality of phone/tablet support;
- ease of automated testing;
- maturity and documentation;
- suitability for AI-assisted development;
- offline/local-data needs once known;
- cost and lock-in;
- reversibility.

---

## D-0010 — Initial product scope / MVP

**Status:** Pending  
**Date:** 2026-08-28

The first usable feature set has not yet been defined.

### Decision should follow

A guided discovery session with the owner after the repository foundation is approved.

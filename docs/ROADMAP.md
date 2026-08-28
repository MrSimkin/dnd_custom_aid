# Roadmap

This roadmap defines development stages, not a fixed feature list. Product content within each stage remains subject to owner approval.

## Phase 0 — Project Foundation

**Goal:** make the repository safe for long-running, multi-chat, multi-agent development.

### Exit criteria

- repository entry point exists;
- agent operating rules exist;
- current-state tracking exists;
- durable decision log exists;
- product discovery boundaries exist;
- workflow and testing expectations exist;
- owner has reviewed and approved or modified the operating model.

**Current status:** In progress.

---

## Phase 1 — Product Discovery

**Goal:** define what the first useful version should actually do.

### Expected outputs

- product purpose;
- player use cases;
- Dungeon Master use cases;
- MVP feature list;
- explicit non-MVP list;
- content/rules-system scope;
- high-level offline/data/privacy expectations;
- initial usability expectations for phone and tablet;
- acceptance criteria for the first release.

### Exit criterion

The owner has approved a coherent MVP scope that can be implemented without guessing major product behavior.

---

## Phase 2 — Technical Foundation

**Goal:** choose and scaffold an Android architecture that fits the approved MVP.

### Expected outputs

- approved technology/architecture decision;
- reproducible local build;
- automated build/check commands;
- app skeleton;
- phone/tablet layout foundation;
- testing foundation;
- dependency and version management;
- CI where useful;
- developer/agent setup instructions.

### Exit criterion

A fresh agent can clone the repository, follow documented commands, build the app, and run baseline tests.

---

## Phase 3 — First Vertical Slice

**Goal:** implement one small end-to-end feature that proves the architecture and product workflow.

The feature itself must be chosen during discovery.

### Exit criterion

A real approved user task works end-to-end on representative phone and tablet configurations, with tests and documented behavior.

---

## Phase 4 — MVP Buildout

**Goal:** implement the remaining approved MVP scope incrementally.

### Working principle

Prefer complete, testable slices over many half-finished screens. Each feature should have explicit acceptance criteria and leave project-state documentation current.

---

## Phase 5 — MVP Hardening

**Goal:** make the first release dependable enough for real use.

Potential areas include, as applicable to the approved product:

- regression testing;
- phone/tablet usability checks;
- accessibility review;
- data migration/recovery checks;
- performance;
- offline behavior;
- backup/export behavior;
- crash handling;
- privacy/security review;
- packaging/release process.

Only applicable items become requirements.

---

## Phase 6 — Post-MVP Evolution

**Goal:** add features based on owner priorities and actual usage while preserving continuity and compatibility.

Every significant expansion should go through the same sequence: decision → specification → implementation → testing → documentation → owner review.

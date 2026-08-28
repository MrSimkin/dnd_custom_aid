# Roadmap

This roadmap defines development stages, not a fixed feature list. Product content within each stage remains subject to owner approval.

## Phase 0 — Project Foundation

**Goal:** make the repository safe for long-running, multi-chat, multi-agent development.

### Exit criteria

- repository entry point exists;
- agent operating rules exist;
- current-state tracking exists;
- durable decision log exists;
- durable conventions registry exists;
- product discovery boundaries exist;
- workflow and testing expectations exist;
- owner has reviewed and approved or modified the operating model.

**Current status:** Ready to complete once PR #1 is merged.

---

## Phase 1 — Product Discovery and Design

**Goal:** understand and design what the first useful version should actually do before selecting the technology stack.

### Working method

For meaningful product and interaction questions:

1. identify the problem or design choice;
2. explore realistic alternatives with the owner;
3. explain practical consequences and trade-offs;
4. recommend an option when justified;
5. let the owner decide;
6. record approved results and unresolved questions in Git.

### Expected outputs

- product purpose;
- player use cases;
- Dungeon Master use cases;
- major workflows and interaction model;
- MVP feature list;
- explicit non-MVP list;
- content/rules-system scope;
- high-level offline/data/privacy expectations;
- meaningful phone/tablet behavior expectations;
- acceptance criteria for the first release;
- known unresolved design questions.

### Exit criterion

The owner has approved a coherent product/design baseline that is detailed enough to evaluate technical options without forcing the design to fit an arbitrary technology choice.

**Important:** do not select or scaffold the Android technology stack during this phase merely to start coding.

---

## Phase 2 — Technical Options and Foundation

**Goal:** evaluate technical alternatives against the approved product/design baseline, obtain owner approval, then scaffold the chosen Android architecture.

### Step 1 — Evaluate with the owner

Discuss relevant alternatives for matters such as:

- native Android vs any realistic alternative;
- language/UI toolkit;
- supported Android baseline;
- persistence/data approach;
- offline behavior implementation;
- sync/backend approach if the approved product needs one;
- test architecture;
- project/module structure and other durable conventions.

For each consequential choice, explain practical trade-offs and recommendation before approval.

### Step 2 — Scaffold after approval

Expected outputs after the required choices are approved:

- recorded technology/architecture decisions;
- recorded coding/project conventions as they become relevant;
- reproducible local build;
- automated build/check commands;
- app skeleton;
- phone/tablet layout foundation appropriate to the design;
- testing foundation;
- dependency and version management;
- CI where useful;
- developer/agent setup instructions.

### Exit criterion

A fresh agent can clone the repository, follow documented commands, build the app, and run baseline tests, and the technical foundation can be traced back to approved product/design needs.

---

## Phase 3 — First Vertical Slice

**Goal:** implement one small end-to-end feature that proves the architecture and product workflow.

The feature itself must be selected from the approved product/design scope.

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

Every significant expansion should go through the same sequence: alternatives/discussion → decision/design → specification → implementation → testing → Git continuity update → owner review.

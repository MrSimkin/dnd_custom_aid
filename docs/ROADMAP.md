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

**Current status:** Complete. Owner-approved foundation merged to `main` via PR #1 on 2026-08-28.

---

## Phase 1 — Product Discovery and Design

**Goal:** understand and design what the first useful version should actually do before selecting the technology stack.

**Current status:** **Complete.** The approved product/MVP baseline, including the final eight-item tension-resolution pass and multicampaign scope, was merged to canonical `main` via PR #2 on 2026-08-29 (`b5a059b8e7fb9312232ad684356af05e27331b65`).

### Outputs achieved

- product purpose and boundaries;
- player and Dungeon Master use cases;
- major workflows and interaction model;
- approved MVP and explicit non-MVP list;
- content/rules-system scope;
- multicampaign membership/selection behavior;
- paper/digital authority semantics;
- desktop-vs-Android surface responsibilities;
- local-first combat authority/synchronization behavior;
- account/moderation/invitation semantics;
- NPC/monster/encounter direction;
- high-level offline/data/privacy expectations;
- sufficient product clarity to evaluate architecture without forcing the design to fit an arbitrary stack.

Detailed feature-level acceptance criteria do **not** need to have been exhaustively authored during Phase 1. They must be defined before the corresponding implementation slice is considered complete, using `docs/templates/FEATURE_SPEC_TEMPLATE.md` and the testing rules as appropriate.

### Exit criterion

The owner has approved a coherent product/design baseline detailed enough to evaluate technical options.

**Result:** satisfied and merged. Do not reopen resolved Phase 1 questions merely because historical discovery notes describe earlier uncertainty; reopen only for a genuinely new requirement or contradiction.

---

## Phase 2 — Technical Options and Foundation

**Goal:** evaluate technical alternatives against the approved product/design baseline, obtain owner approval for consequential choices, then scaffold the chosen architecture.

**Current status:** **Active — architecture/technology evaluation.** No architecture or stack is selected and no application code has been scaffolded.

### Step 1 — Evaluate with the owner

Start with **overall application topology/surface relationship**, then discuss relevant alternatives for matters such as:

1. overall Android + desktop/laptop topology and shared-domain relationship;
2. Android implementation approach, language and UI toolkit;
3. desktop/laptop administration implementation approach without requiring feature parity;
4. supported Android baseline;
5. multicampaign persistence/domain-data model;
6. local-first authoritative DM combat persistence;
7. combat-aware synchronization/shared-data architecture, including provisional player offline views;
8. hosted backend/database and authentication/authorization/moderation boundaries;
9. PDF generation/rendering;
10. SRD 5.1 + SRD 5.2.1 storage/retrieval/clarification architecture;
11. test architecture;
12. project/module structure and other durable conventions.

For each consequential choice, explain practical alternatives, trade-offs and a recommendation before owner approval.

### Step 2 — Scaffold after approval

Expected outputs after the required choices are approved:

- recorded technology/architecture decisions;
- recorded coding/project conventions as they become relevant;
- reproducible local build;
- automated build/check commands;
- application skeleton appropriate to the approved asymmetric surfaces;
- phone/tablet layout foundation appropriate to the design;
- desktop/laptop administration foundation appropriate to the approved architecture;
- multicampaign shared-data foundation;
- local-first combat persistence/synchronization foundation;
- testing foundation;
- dependency and version management;
- CI where useful;
- developer/agent setup instructions.

### Exit criterion

A fresh agent can clone the repository, follow documented commands, build the approved application foundation and run baseline tests, with the technical foundation traceable to approved product/design needs.

**Important:** evaluation is active, but implementation/scaffolding must not begin until the relevant consequential architecture/stack choices are approved.

---

## Phase 3 — First Vertical Slice

**Goal:** implement one small end-to-end feature that proves the architecture and product workflow.

The feature itself must be selected from the approved product/design scope and must have explicit observable acceptance criteria before it is considered complete.

The combat tracker is currently identified as the most important live-table MVP validation surface, but the exact first vertical slice should be selected after architecture evaluation so it proves the most useful cross-cutting risks rather than being chosen arbitrarily.

### Exit criterion

A real approved user task works end-to-end on representative configurations, with tests and documented behavior.

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
- desktop/laptop administration checks;
- multicampaign navigation/isolation checks;
- cross-surface shared-data checks;
- local-first combat/offline/reconnection checks;
- provisional player-view reconciliation checks;
- accessibility review;
- data migration/recovery checks;
- performance;
- backup/export behavior;
- crash handling;
- privacy/security review;
- packaging/release process.

Only applicable items become requirements.

---

## Phase 6 — Post-MVP Evolution

**Goal:** add features based on owner priorities and actual usage while preserving continuity and compatibility.

Possible later directions already distinguished from MVP include broader Android/desktop feature parity, player desktop access, desktop combat tracking, co-DMs, explicit DM-device combat handoff, house-rule-aware clarification, and other approved future expansions.

Every significant expansion should go through the same sequence: alternatives/discussion → decision/design → specification → implementation → testing → Git continuity update → owner review.
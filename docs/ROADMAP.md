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

**Current status:** Product/MVP baseline complete pending final PR #2 merge closure. The original clarification rounds and final eight-item product-tension pass are resolved; the MVP is now explicitly multicampaign. Technical architecture has not yet been selected.

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
- multicampaign membership/selection behavior;
- high-level offline/data/privacy expectations;
- meaningful phone/tablet and desktop-role expectations;
- high-level first-release success/validation intent;
- known unresolved design questions.

Detailed feature-level acceptance criteria do **not** need to be exhaustively authored during Phase 1. They must be defined before the corresponding implementation slice is considered complete, using `docs/templates/FEATURE_SPEC_TEMPLATE.md` and the testing rules as appropriate.

### Current milestone

The MVP feature boundary and major workflows are approved in `docs/PRODUCT.md` and `docs/DECISIONS.md`. The final tension pass resolved Android-vs-desktop scope, multicampaign, mixed/homebrew-vs-SRD assistant scope, monster-structure depth, paper-vs-digital authority, local-first combat authority, moderation boundaries, and invitation/rejoin semantics.

The product baseline is sufficiently coherent to evaluate technical options. Remaining Phase 1 work is repository/PR closure and final verification—not reopening already resolved product questions by default.

### Exit criterion

The owner has approved a coherent product/design baseline that is detailed enough to evaluate technical options without forcing the design to fit an arbitrary technology choice.

This criterion is satisfied by the approved product baseline; Phase 1 becomes formally complete when PR #2 is consistency-checked, owner-approved and merged to canonical `main`.

**Important:** architecture evaluation begins after Phase 1 closure, but do not scaffold implementation before consequential architecture/stack choices are approved.

---

## Phase 2 — Technical Options and Foundation

**Goal:** evaluate technical alternatives against the approved product/design baseline, obtain owner approval, then scaffold the chosen architecture.

### Step 1 — Evaluate with the owner

Start with **overall application topology/surface relationship**, then discuss relevant alternatives for matters such as:

- Android implementation approach, language and UI toolkit;
- desktop/laptop administration implementation approach without requiring feature parity;
- supported Android baseline;
- multicampaign persistence/data model;
- local-first authoritative DM combat behavior;
- combat-aware synchronization/shared-data architecture, including provisional player offline views;
- authentication/authorization, campaign-scoped moderation and application-admin boundaries;
- PDF generation/rendering;
- SRD 5.1 + SRD 5.2.1 storage/retrieval/clarification architecture;
- hosted backend/provider and cost/limits;
- test architecture;
- project/module structure and other durable conventions.

For each consequential choice, explain practical trade-offs and recommendation before approval.

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

A fresh agent can clone the repository, follow documented commands, build the application foundation, and run baseline tests, and the technical foundation can be traced back to approved product/design needs.

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

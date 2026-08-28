# Project State

**Last verified:** 2026-08-28  
**Proposed canonical branch:** `main`  
**Current working branch:** `foundation/continuity-structure`  
**Open review:** PR #1 — `Establish repository continuity and agent governance foundation`  
**Phase:** Phase 0 — Project Foundation  
**Status:** Continuity/governance structure complete on review branch; awaiting owner review

## 1. Project in one paragraph

`dnd_custom_aid` is intended to become an Android companion application for both phones and tablets, serving both players and Dungeon Masters. The owner is not expected to write or operate the code directly: coding, implementation, testing, and technical execution may be performed by AI/coding agents, while significant product decisions remain with the owner. The repository must contain enough durable context for a new chat, AI, or agent to resume work safely without depending on previous conversation memory.

## 2. What exists now

There is no Android application code yet.

The repository foundation now contains:

- a single entry point (`README.md`);
- mandatory instructions for future agents (`AGENTS.md`);
- a repository-control inventory (`MANIFEST.md`);
- explicit project-state tracking (this file);
- a durable decision log (`docs/DECISIONS.md`);
- product-scope/discovery documentation (`docs/PRODUCT.md`);
- a staged roadmap (`docs/ROADMAP.md`);
- implementation/review workflow (`docs/WORKFLOW.md`);
- architecture record (`docs/ARCHITECTURE.md`);
- testing/verification policy (`docs/TESTING.md`);
- reusable feature, decision, and handoff templates;
- a pull-request review template.

The full foundation is currently on `foundation/continuity-structure` and is presented for owner review in PR #1.

## 3. What is approved

The following facts were explicitly supplied by the owner and are treated as approved:

- The target is an Android application.
- It must be usable on phones and tablets.
- It is intended for players and Dungeon Masters.
- AI/agents may perform coding, implementation, testing, and technical execution.
- Significant product decisions remain with the owner.
- The repository must be sufficient for continuity across chats, AIs, and agents.

See `docs/DECISIONS.md` for the formal record.

## 4. What is NOT decided yet

No decision has yet been approved for:

- exact game system or supported rulesets;
- initial feature set or MVP;
- screen/navigation structure;
- visual design;
- native Android vs another framework;
- programming language;
- minimum Android version;
- local data model;
- cloud sync, accounts, multiplayer, or sharing;
- offline requirements;
- licensing/distribution model;
- monetization;
- external services;
- privacy model;
- exact test-device matrix beyond the requirement to support phone and tablet;
- whether `main`/branch review workflow D-0007 is approved;
- whether routine implementation autonomy boundary D-0008 is approved.

These must not be treated as settled facts.

## 5. Current work

**Active task:** owner review of the repository continuity/governance structure before application development begins.

**Implementation state:** foundation complete on `foundation/continuity-structure`; PR #1 is open and intentionally unmerged pending owner review.

**Verification performed:** repository root and `docs/` branch contents were inspected after creation. This is documentation-only work; no application build or automated app tests exist yet because there is no application code.

## 6. Current blockers

There is no technical blocker to beginning discovery once the foundation is approved.

The current gate is owner review of the proposed operating model.

## 7. Next owner decision

Review and decide:

1. **D-0007:** whether `main` should be the canonical accepted state and substantial changes should normally be developed on branches and reviewed before merge;
2. **D-0008:** whether agents may make routine, reversible, low-level implementation choices without separate approval while significant decisions remain owner-controlled;
3. whether the mandatory continuity/read/update structure is sufficient or should be modified.

## 8. Next implementation action

After owner approval or requested revisions:

1. update D-0007/D-0008 to reflect the owner's decisions;
2. incorporate any requested foundation changes;
3. merge PR #1 into `main` only after owner approval;
4. update this file to reflect the merged state;
5. begin a structured product-discovery session with the owner;
6. record approved product decisions before choosing/scaffolding Android architecture.

## 9. Handoff note for the next agent

Do not begin Android coding yet unless the owner has approved the foundation and required product/architecture decisions have been recorded. Read `AGENTS.md` and `docs/DECISIONS.md` before proposing implementation. If PR #1 remains open, treat it as proposed foundation work rather than canonical accepted state.

# Project State

**Last verified:** 2026-08-28  
**Canonical branch:** `main`  
**Current working branch:** `foundation/continuity-structure`  
**Phase:** Phase 0 — Project Foundation  
**Status:** Continuity/governance structure under owner review

## 1. Project in one paragraph

`dnd_custom_aid` is intended to become an Android companion application for both phones and tablets, serving both players and Dungeon Masters. The owner is not expected to write or operate the code directly: coding, implementation, testing, and technical execution may be performed by AI/coding agents, while significant product decisions remain with the owner. The repository must contain enough durable context for a new chat, AI, or agent to resume work safely without depending on previous conversation memory.

## 2. What exists now

At this stage there is no Android application code yet.

The repository foundation is being created to provide:

- a single entry point;
- mandatory instructions for future agents;
- explicit project-state tracking;
- a durable decision log;
- product-scope documentation;
- a roadmap;
- implementation/review workflow;
- architecture and testing records;
- reusable templates for future decisions and feature work.

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
- test-device matrix beyond the requirement to support phone and tablet.

These must not be treated as settled facts.

## 5. Current work

**Active task:** establish and review the repository continuity/governance structure before application development begins.

**Implementation state:** documentation foundation in progress on `foundation/continuity-structure`.

**Last verification:** repository was confirmed empty before the bootstrap commit. The first `README.md` commit established `main`; all additional foundation work is being prepared on the review branch.

## 6. Current blockers

There is no technical blocker.

The next meaningful development step requires owner review of this continuity structure and then a guided product-discovery pass to define the first application scope.

## 7. Next owner decision

Review the proposed project operating model, especially:

1. whether `main` should remain the canonical published state;
2. whether substantial changes should normally be developed on branches and reviewed before merge;
3. whether the proposed boundary between owner decisions and routine implementation choices is acceptable;
4. whether any additional continuity information should always be recorded.

## 8. Next implementation action

After owner approval of the foundation:

1. merge the continuity foundation into `main`;
2. update this file to reflect the merge;
3. run a structured discovery session with the owner;
4. record the resulting approved product decisions;
5. only then choose and scaffold the Android technical architecture.

## 9. Handoff note for the next agent

Do not begin Android coding yet unless the owner has approved the foundation and the required product/architecture decisions have been recorded. Read `AGENTS.md` and `docs/DECISIONS.md` before proposing implementation.

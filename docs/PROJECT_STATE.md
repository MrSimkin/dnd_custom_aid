# Project State

**Last verified:** 2026-08-28  
**Canonical branch:** `main`  
**Current working branch:** none  
**Open review:** none  
**Phase:** Phase 1 — Product Discovery and Design  
**Status:** Foundation complete and merged; product discovery/design is the next active work

## 1. Project in one paragraph

`dnd_custom_aid` is intended to become an Android companion application for both phones and tablets, serving both players and Dungeon Masters. The owner understands programming concepts but delegates the heavy technical execution to AI/coding agents. Agents must explain meaningful technical work and alternatives, while significant product/UX/data/privacy/cost/architecture decisions and new durable project conventions remain owner-controlled. Git is the project's operative memory: another chat, AI, agent, or human must be able to resume from repository state without relying on prior conversation memory.

## 2. What exists now

There is no Android application code yet.

The approved repository foundation is now merged into `main` and contains:

- project entry point (`README.md`);
- mandatory agent/human operating rules (`AGENTS.md`);
- repository-control inventory (`MANIFEST.md`);
- current-state tracking (this file);
- significant decision log (`docs/DECISIONS.md`);
- durable convention registry (`docs/CONVENTIONS.md`);
- product/discovery/design documentation (`docs/PRODUCT.md`);
- staged roadmap (`docs/ROADMAP.md`);
- implementation/review/communication workflow (`docs/WORKFLOW.md`);
- architecture record (`docs/ARCHITECTURE.md`);
- testing/verification policy (`docs/TESTING.md`);
- reusable feature, decision, and handoff templates;
- pull-request review template.

PR #1, `Establish repository continuity and agent governance foundation`, was owner-approved and merged into `main` on 2026-08-28.

## 3. Approved operating model

The owner explicitly approved:

- `main` is the canonical accepted project state;
- substantial work normally happens on focused branches and is merged after owner approval;
- agents perform the technical heavy lifting;
- the owner remains the decision owner for significant choices;
- agents may make routine reversible implementation choices, but meaningful technical work must be explained;
- when a new durable technical/project convention first matters, the agent discusses alternatives and recommendation with the owner, records the approved convention in Git, and then follows it consistently;
- Git is the operative memory; continuation-critical information must not exist only in chat;
- product discovery/design and alternatives are discussed before stack selection;
- technical stack/architecture is evaluated only after enough product/design work exists to judge the alternatives properly.

Formal records: D-0007, D-0008, D-0011, D-0012 and `docs/CONVENTIONS.md`.

## 4. Product facts already approved

- Target platform: Android.
- Device classes: phones and tablets.
- Audiences: players and Dungeon Masters.

No further product scope is approved yet.

## 5. What is NOT decided yet

No decision has yet been approved for:

- exact game system or supported rulesets;
- product purpose beyond the broad audience/platform statement;
- player workflows;
- DM workflows;
- initial feature set or MVP;
- screen/navigation structure;
- interaction design;
- visual design;
- native Android vs another implementation approach;
- programming language or UI toolkit;
- minimum Android version;
- local data model;
- cloud sync, accounts, multiplayer, or sharing;
- offline requirements;
- licensing/distribution model;
- monetization;
- external services;
- privacy model;
- exact test-device matrix;
- coding/naming/module/testing conventions that only become meaningful after technology/design choices exist.

These must not be treated as settled facts.

## 6. Verification performed

For the foundation:

- repository and branch contents were inspected;
- the owner-approved governance decisions were recorded in `docs/DECISIONS.md`;
- the conventions registry was added;
- README, manifest, product definition, roadmap, workflow, agent rules, architecture record, PR template, and current state were aligned with the approved operating model;
- branch comparison confirmed the review branch was ahead of `main` and not behind before merge;
- PR #1 merged successfully into `main`;
- no application build or automated app tests exist yet because there is no application code.

## 7. Current blockers

There is no repository/governance blocker.

The project is intentionally not ready for stack selection because product discovery/design has not yet been completed.

## 8. Active phase

**Phase 1 — Product Discovery and Design** is active.

The next work is not stack selection and not application scaffolding. The agent should guide the owner through product/design questions, realistic alternatives, consequences, and recommendations, recording approved results in Git.

## 9. Next action

Begin product discovery/design with the owner. Establish the product purpose and then work through player/DM use cases, alternatives, workflows, scope, and interaction design before evaluating implementation technology.

## 10. Handoff note for the next agent

Do not begin Android stack selection or application coding yet. Read `AGENTS.md`, `docs/DECISIONS.md`, `docs/CONVENTIONS.md`, and `docs/PRODUCT.md`. The active task is collaborative product discovery/design. Explain meaningful alternatives to the owner and persist all continuation-critical results in Git.

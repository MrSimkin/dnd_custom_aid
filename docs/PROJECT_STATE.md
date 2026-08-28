# Project State

**Last verified:** 2026-08-28  
**Canonical branch:** `main`  
**Current working branch:** `discovery/initial-product-picture`  
**Open review:** PR #2 — `Capture initial product discovery picture without promoting it to requirements`  
**Phase:** Phase 1 — Product Discovery and Design  
**Status:** Initial product picture captured provisionally; collaborative clarification/design is active

## 1. Project in one paragraph

`dnd_custom_aid` is intended to become an Android companion application for both phones and tablets, serving both players and Dungeon Masters. The owner understands programming concepts but delegates the heavy technical execution to AI/coding agents. Agents must explain meaningful technical work and alternatives, while significant product/UX/data/privacy/cost/architecture decisions and new durable project conventions remain owner-controlled. Git is the project's operative memory: another chat, AI, agent, or human must be able to resume from repository state without relying on prior conversation memory.

## 2. What exists now

There is no Android application code yet.

The approved repository foundation is merged into `main`. Product discovery has begun.

Current discovery work is on `discovery/initial-product-picture` in PR #2. That branch contains:

- a provisional structured capture of the owner's first product picture;
- a proposed manifest entry for `docs/discovery/`;
- D-0013, reflecting the owner's explicit instruction that brainstorming must be treated as exploratory and actively discussed rather than automatically promoted into requirements.

The actual feature ideas on that branch are **not yet approved product requirements**.

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

Formal records currently on `main`: D-0007, D-0008, D-0011, D-0012 and `docs/CONVENTIONS.md`.

## 4. Product facts already approved

- Target platform: Android.
- Device classes: phones and tablets.
- Audiences: players and Dungeon Masters.

No further product scope has yet been promoted to approved status.

## 5. Current provisional discovery themes

The owner's initial exploratory picture currently includes, without yet making them approved requirements:

- Spanish-only user-facing app;
- player viewing/editing of character sheets;
- DM visibility/auditability of player changes;
- character-sheet PDF export using existing owner-provided formats/templates;
- shared online hosted data with a free/no-cost option preferred if practical;
- DM tablet-first access to all PCs and a quick-stat overview;
- combat/initiative tracking and monster stat-block access;
- user accounts and campaign enrollment;
- a possible Windows desktop companion for basic campaign management;
- direct Spanish consultation of both earlier and revised fifth-edition SRD content.

The detailed provisional record is on PR #2 and must be reviewed/discussed before promotion into approved product scope.

## 6. Current external feasibility observations

These are research observations, not technology selections:

- official Spanish SRD material exists for both SRD 5.1 and SRD 5.2.1 under Creative Commons terms, making integrated Spanish SRD reference a realistic design possibility;
- free/no-cost hosted backend tiers currently exist from multiple providers, making the owner's online-data cost target plausible for development/small-scale use;
- no backend/provider is selected and stack evaluation remains deferred until the design is sufficiently defined.

## 7. Current blockers

There is no repository/governance blocker.

The project is intentionally not ready for stack selection because product discovery/design has not yet been completed.

## 8. Active phase

**Phase 1 — Product Discovery and Design** is active.

The owner is intentionally providing a broad, partially unorganized product picture. The active job is to help organize it, ask clarifying questions, identify implications, propose alternatives, recommend options, suggest useful ideas, and only then confirm product requirements.

## 9. Next action

Continue the collaborative discovery conversation. Priority areas include:

- exact player character-sheet workflow and what DM audit/approval means;
- DM quick-access information and combat workflow;
- campaign/account/enrollment model from the user's perspective;
- role and priority of the possible Windows desktop companion;
- desired SRD browsing/search/contextual-reference experience;
- which ideas belong in the first usable release versus later phases.

After the owner reviews the structured discovery capture, update/merge PR #2 as appropriate and then promote confirmed conclusions into authoritative product/decision documents.

## 10. Handoff note for the next agent

Do not begin Android stack selection or application coding yet. Start from `main`, then inspect PR #2 / `discovery/initial-product-picture` for current provisional discovery material. Do not treat those feature ideas as approved requirements. Continue the collaborative clarification/design process and persist material outcomes in Git.

# Product Definition

This document describes the product as it is currently known. It separates approved facts from open questions so future contributors do not convert assumptions into requirements.

## 1. Approved product facts

The product is:

- an Android application;
- intended for both phones and tablets;
- intended for both players and Dungeon Masters.

That is the full approved product scope at this stage.

## 2. Approved discovery/design approach

The project will **design before choosing the technology stack**.

Before Android implementation technology is selected, the owner and agent will:

- clarify the problems the application should solve;
- explore realistic feature and workflow alternatives;
- discuss advantages, disadvantages, and consequences;
- design intended player and DM behavior collaboratively;
- consider phone and tablet use as part of the design rather than after implementation;
- record approved design choices and unresolved questions in Git.

The agent should recommend options when useful, but should not silently choose product behavior or use technical convenience to force an early design decision.

See D-0011.

## 3. Product goal — to be defined

A concise statement of the problem the app should solve has not yet been approved.

During discovery/design, define in plain but technically meaningful language:

- what problem a player should be able to solve with the app;
- what problem a Dungeon Master should be able to solve with the app;
- what makes this app worth using instead of notes, PDFs, existing apps, or websites;
- what the smallest useful first release would be.

## 4. User groups

### Player

**Approved:** players are a target audience.

**Not yet defined:**

- player workflows;
- character management;
- rules/reference access;
- dice or combat support;
- campaign sharing;
- permissions;
- whether players can create/edit custom content.

### Dungeon Master

**Approved:** Dungeon Masters are a target audience.

**Not yet defined:**

- campaign management;
- encounter tools;
- NPC/creature tools;
- custom content;
- rules/reference access;
- sharing with players;
- hidden vs shared information;
- permissions.

## 5. Game-system scope

**Pending.**

The repository name suggests a D&D-related purpose, but the exact supported game edition/system and the role of custom/homebrew content have not been formally approved in this repository. Future agents must not infer those details from the repository name alone.

## 6. Candidate discovery areas — not approved features

The following are questions to explore, not requirements:

- character sheets or character reference;
- spell/reference tools;
- creature/NPC reference;
- custom/homebrew content;
- campaign notes;
- encounter or initiative support;
- inventory/equipment;
- dice tools;
- player/DM sharing;
- offline use;
- import/export;
- backups;
- search and filtering;
- tablet-specific layouts.

No item in this list should be implemented merely because it appears here.

## 7. Constraints currently known

- The owner should not need to write application code.
- The owner understands programming but is delegating professional-level implementation work to AI/coding agents.
- Meaningful technical work must be explained: what is being done, why, and important alternatives/consequences.
- New durable technical conventions must be discussed with the owner before they become project practice.
- The project must remain resumable from repository state alone; Git is the operative memory.
- Technology stack selection comes after sufficient product/interaction design, not before.

## 8. Product/design output required before stack selection

Before evaluating the Android stack as a consequential decision, discovery/design should produce enough clarity to judge technology against real needs. At minimum, this should include:

1. a one-paragraph product purpose;
2. primary player use cases;
3. primary Dungeon Master use cases;
4. major workflows/interactions for the first useful version;
5. an explicitly approved MVP feature list;
6. an explicit out-of-scope list for the MVP;
7. intended phone/tablet behavior at a meaningful level;
8. high-level data/privacy/offline expectations;
9. required game-system/content constraints;
10. acceptance criteria for the first usable release;
11. unresolved design questions clearly marked as such.

Approved results should be recorded here and in `docs/DECISIONS.md` where appropriate.

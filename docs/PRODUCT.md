# Product Definition

This document describes the product as it is currently known. It separates approved facts from open questions so future contributors do not convert assumptions into requirements.

## 1. Approved product facts

The product is:

- an Android application;
- intended for both phones and tablets;
- intended for both players and Dungeon Masters.

That is the full approved product scope at this stage.

## 2. Product goal — to be defined

A concise statement of the problem the app should solve has not yet been approved.

During discovery, define in plain language:

- what problem a player should be able to solve with the app;
- what problem a Dungeon Master should be able to solve with the app;
- what makes this app worth using instead of notes, PDFs, existing apps, or websites;
- what the smallest useful first release would be.

## 3. User groups

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

## 4. Game-system scope

**Pending.**

The repository name suggests a D&D-related purpose, but the exact supported game edition/system and the role of custom/homebrew content have not been formally approved in this repository. Future agents must not infer those details from the repository name alone.

## 5. Candidate discovery areas — not approved features

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

## 6. Constraints currently known

- The owner should not need to write application code.
- Significant decisions should be presented in understandable, non-jargon-heavy language.
- The project must remain resumable from repository state alone.

## 7. Product discovery output required before MVP implementation

The discovery phase should produce, at minimum:

1. a one-paragraph product purpose;
2. primary player use cases;
3. primary Dungeon Master use cases;
4. an explicitly approved MVP feature list;
5. an explicit out-of-scope list for the MVP;
6. the intended data/privacy/offline behavior at a high level;
7. any required game-system/content constraints;
8. acceptance criteria for the first usable release.

Approved results should be recorded here and in `docs/DECISIONS.md` where appropriate.

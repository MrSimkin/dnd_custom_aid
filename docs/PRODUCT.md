# Product Definition

This document describes the product as it is currently known. It separates approved facts from open questions so future contributors do not convert assumptions into requirements.

## 1. Approved product facts

The current product is:

- an Android application intended for both phones and tablets;
- intended for both players and Dungeon Masters;
- user-facing in Spanish;
- designed for personal/small-scale use;
- beginning with D&D because that is the owner's current game, while avoiding unnecessary core-design assumptions that would make future support for another tabletop RPG impossible.

The project is not intended to become a general commercial D&D platform or a D&D Beyond replacement.

## 2. Approved discovery/design approach

The project will **design before choosing the technology stack**.

During discovery, owner brainstorming is exploratory until clarified and confirmed. The agent is expected to reorganize ideas, ask questions, identify consequences, recommend alternatives and challenge weak assumptions rather than simply transcribing them.

See D-0011 and D-0013.

## 3. Current product purpose

The emerging product purpose is to provide a small, personal digital assistant around a primarily physical tabletop workflow.

The app should reduce friction during play and campaign administration without trying to replace paper character sheets, automate all D&D rules, or become a full character-building platform.

The current design picture has three main usage surfaces:

1. **Player phone/tablet:** backup/reference access to a character and the ability to maintain/reprint the latest digitally recorded character sheet.
2. **DM tablet:** live-session quick information and combat/initiative support.
3. **Desktop/laptop administration:** comfortable preparation/data-entry for campaign, NPC and monster information. The implementation form (native Windows, web app, local web interface, etc.) is not decided.

## 4. Player experience — approved direction

The preferred table workflow uses physical printed character sheets.

The player-facing app is primarily a backup/reference/recovery tool. Players should be able to:

- see their latest digitally recorded character information on phone or tablet;
- update that digital character information;
- export/reprint the latest digitally recorded sheet as PDF using approved owner-provided templates.

Full guided character creation is **not part of the current first-version intent**. It may be reconsidered later.

### Important open workflow

The physical-sheet preference creates a key design question: how and when the digital record is kept synchronized with changes made on paper so the 'latest digital copy' is actually useful.

## 5. Character data and PDF output — approved direction

Character data should be treated conceptually as structured information independent from any one PDF layout.

The owner already has multiple custom character-sheet designs created in Adobe InDesign. Existing PDF exports are not fillable/editable PDFs.

The PDF requirement is primarily:

- preserve a printable backup;
- allow the player to regenerate a lost physical sheet;
- support multiple owner-provided sheet layouts over time.

PDF template files belong under `assets/character-sheets/templates/`.

If implementation reveals that a template itself needs a layout change, the required owner-side Adobe InDesign change must be recorded in `assets/character-sheets/CHANGE_REQUESTS.md` and explained to the owner.

## 6. Character changes and DM oversight — approved direction

Player changes should not require DM pre-approval before taking effect.

The DM must be able to:

- audit player-made changes;
- understand what changed;
- directly correct a character when necessary;
- reverse/undo an inappropriate or mistaken change.

The detailed audit-storage design is not yet approved. A current recommendation is to preserve history rather than deleting the original action when a DM corrects/reverses something.

## 7. Accounts, campaigns and roles — approved direction

Every user has an account/identity.

Roles are campaign-specific rather than permanent account types. A person may therefore be a player in one campaign and a DM in another.

A DM creates/administers a campaign and player characters are enrolled/associated with that campaign.

First-version enrollment is intended to be DM-controlled through invitation mechanisms such as code, email and/or QR. Exact invitation/recovery/membership flows remain open.

## 8. Future game-system scope — approved direction

D&D is the initial supported game because it is what the owner currently runs.

Future support for additional tabletop RPG systems may be added in a more advanced version. Current development does **not** need to implement multi-system support now, but account/campaign foundations should avoid needless D&D-only coupling when a more general design costs little or nothing.

## 9. D&D rules model — approved direction

The current campaign mixes rules, uses house rules/homebrew heavily, and may gradually move more toward SRD 5.2.1 over time.

The application must not require a campaign to be exclusively one official SRD generation.

Official project terminology:

- **SRD 5.1** = earlier/2014-era fifth-edition foundation;
- **SRD 5.2.1** = revised/2024-era fifth-edition foundation.

The app is an assistant, not an automated rules enforcer or character legality checker.

## 10. Rules clarification assistant — approved product goal, implementation open

The desired SRD-related user outcome is quick rules clarification during play.

A player or DM should be able to ask a rules question that arises during an action or combat and receive a useful response quickly.

The owner has suggested AI-assisted clarification as a possible implementation idea. AI itself is **not approved as the implementation yet**, and no provider/model/service is selected.

If AI is later used, a current recommended quality direction is:

- retrieve/check authoritative SRD material rather than answer from model memory;
- identify the applicable official SRD version/source;
- say when the question is not covered by the SRD;
- distinguish official SRD material from campaign house rules/homebrew.

The feature is not intended to download the SRD merely to power automated character creation or legality checks.

## 11. DM tablet workflow — approved starting design

Tablet is the primary live-session DM surface.

The DM needs:

- access to every campaign character sheet;
- a **PC group quick view**;
- quick access to the currently relevant/selected PC;
- an initiative/combat tracker;
- quick access to NPC/monster information and stat blocks during their turns.

Initial quick-view information requested by the owner:

- Armor Class (AC);
- saving throws;
- proficiency bonus;
- spell save DC;
- basic/primary attack bonus or equivalent attack summary;
- ability scores;
- passive Perception.

This first quick-view set is intentionally expected to evolve through real table use and trial-and-error.

## 12. Combat tracker — approved starting behavior

DM view should:

- track initiative order;
- show whose turn is active;
- expose the active PC's quick information when relevant;
- expose the active monster/NPC stat block or quick sheet when relevant;
- support several relevant creatures on the same turn/initiative position through an appropriate multi-entity presentation;
- allow the DM to hide selected creatures from the player-visible initiative order.

Player view should show:

- visible initiative order;
- the currently active participant;
- no hidden creatures.

Further combat-state tracking beyond initiative/current turn remains open.

## 13. Hosted/shared data — approved constraint, provider open

Shared campaign data should be hosted online.

For the intended personal/small-scale use, the design should aim to remain comfortably inside a no-cost hosted tier where practical. If scope grows meaningfully later, hosting cost can be reconsidered responsibly.

A current candidate mentioned by the owner is Neon/Postgres, but no backend/provider is approved yet.

Provider evaluation must later consider more than raw storage size, including:

- authentication;
- authorization/security;
- audit/history needs;
- synchronization/realtime behavior;
- backup/recovery;
- service limits and idle behavior;
- maintenance burden;
- future cost/lock-in.

## 14. Desktop/laptop administration — approved need, platform open

The owner expects that campaign preparation, NPC/monster entry and organization may be more comfortable from a laptop/desktop than from a tablet.

A desktop-friendly administration surface is therefore a real product need to design.

What is **not** decided is whether that surface should be:

- a native Windows client;
- a normal web application;
- a local-only web interface;
- another practical desktop-friendly approach.

Technology choice must follow workflow design.

## 15. Language convention

All end-user-facing product content is Spanish.

Technical repository documentation, source code and development material remain English. See `docs/CONVENTIONS.md` C-0006.

## 16. Important unresolved design questions

Current high-priority questions include:

1. What is the practical paper-to-digital update workflow?
2. Should every DM correction/reversal preserve immutable historical entries exactly as currently recommended?
3. Confirm campaign multiplicity rules: multiple PCs per player, co-DM behavior and future extensibility.
4. How should campaign house rules be stored and surfaced, particularly when they override an official SRD answer?
5. What is the first useful NPC/monster data-entry workflow on the administration surface?
6. Beyond initiative/current turn, what combat state should be synchronized/persisted in the first version?
7. What exact invitation/recovery/account flows are appropriate for the personal-use threat model?
8. What constitutes the smallest useful first release/MVP?

## 17. Discovery source material

Detailed exploratory history lives under `docs/discovery/`. Those files preserve rationale and unresolved thinking but do not override approved statements in this file or `docs/DECISIONS.md`.

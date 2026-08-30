# Product Discovery Clarifications — Round 1

**Date:** 2026-08-28  
**Status:** Mixed: confirmed conclusions plus explicitly unresolved design questions  
**Phase:** Phase 1 — Product Discovery and Design

## Purpose

This file records clarifications supplied by the owner after review of the initial product picture. Items marked **Confirmed** are explicit owner conclusions and may be promoted into product/decision records. Items marked **Open** remain discovery questions and must not be silently implemented.

## 1. Player-side purpose and physical character sheets

**Confirmed:** The group's preferred play style uses physical printed character sheets.

The player-facing application is primarily a digital backup/reference and recovery mechanism rather than an attempt to replace paper at the table.

Players should be able to:

- consult the latest digitally recorded character sheet on phone/tablet when the physical sheet is unavailable;
- keep/update the digital character data;
- export/reprint the latest digitally recorded character sheet to PDF when the physical copy is lost or needs replacing.

The owner already has multiple custom character-sheet layouts made in Adobe InDesign. Existing PDFs are not fillable/editable PDFs.

**Design consequence:** character data should be conceptually separate from the PDF layout. PDF generation should render the stored character state into a chosen output template rather than making the PDF itself the primary database.

**Open:** define the real-world synchronization habit between paper and digital state: who updates the digital record, when, and how to reduce stale backups.

## 2. Player change audit and DM correction

**Confirmed:** Player edits should take effect without a DM approval gate.

The DM must be able to:

- audit player-made changes;
- see enough history to understand what changed;
- directly correct a character when necessary;
- reverse/undo an inappropriate or mistaken change.

The owner prefers this over requiring the DM to approve each material player change before it becomes active.

**Recommended design direction:** preserve an append-only/tamper-resistant change history. A DM correction or reversal should create a new recorded action rather than deleting the original history. This recommendation still needs explicit owner confirmation before becoming a detailed requirement.

## 3. Accounts and campaign roles

**Confirmed:** A user should have one account/identity; the person's role is campaign-specific rather than permanently being a Player account or DM account.

This is also important because D&D is the first supported game, not necessarily the only game system the personal tool may ever support.

**Confirmed intent:** every character used in shared campaign functionality belongs/enrolls in a campaign created/administered by the DM.

**Open:** exact membership lifecycle, invitations, leaving/transferring campaigns, account recovery and role-management UI.

## 4. Future game-system scope

**Confirmed:** D&D is the starting system because it is what the owner currently runs.

The owner may want support for additional tabletop RPG systems in a more advanced future version.

**Design consequence:** current D&D-specific design should avoid unnecessarily making the entire identity/account/campaign core impossible to extend to another game system later. This does not mean implementing multi-system support now.

## 5. DM tablet workflow

**Confirmed:** tablet is the primary live-session DM surface.

Initial DM quick-access needs include both:

- a **PC group quick view**; and
- quick access to the **current/selected PC**.

Initial quick-view candidate statistics explicitly requested by the owner:

- Armor Class (AC);
- saving throws;
- proficiency bonus;
- spell save DC;
- basic attack bonus / primary attack information;
- ability scores;
- passive Perception.

The owner expects this area to evolve through real table use and trial-and-error. The initial design should therefore be easy to revise rather than pretending the first quick-view layout is final.

## 6. Combat tracker

**Confirmed DM-side behavior:**

- track initiative order;
- identify the active turn;
- when a PC is active, provide access to that PC's quick information;
- when a monster/NPC is active, provide access to its stat block/quick sheet;
- if several creatures share or occupy a relevant turn position, a tabbed or equivalent multi-entity presentation is a possible design direction;
- the DM may mark some creatures as hidden from players.

**Confirmed player-side behavior:**

- players can see initiative order and who is currently active;
- hidden creatures must not appear in the player-visible initiative view.

The DM remains the authority controlling encounter/turn state unless a later design decision says otherwise.

## 7. Rules mixture, homebrew and SRD versions

**Confirmed:** campaigns may mix rules from the two official SRD generations and may be heavily modified by house rules/homebrew.

The project must use official document version terminology when referring to SRD sources:

- **SRD 5.1** = earlier/2014-era fifth-edition foundation;
- **SRD 5.2.1** = revised/2024-era fifth-edition foundation.

The owner currently mixes rules and expects to move more toward SRD 5.2.1 over time, but the application must not force a campaign to be exclusively one version.

## 8. Intended SRD/rules-assistant use

**Confirmed product intent:** the SRD feature is not intended to make a D&D Beyond replacement, automated character builder, or rules-enforcement engine.

The desired use is quick rules clarification during play: a player or DM asks a question that arises during an action/combat and receives a useful answer quickly.

The owner proposed AI-assisted clarification as an idea: frame the question with a rules-clarification prompt and have an AI check the applicable SRD material before answering.

**Open implementation/design question:** whether AI is used at all, and if so how. No AI provider, free service, model, retrieval method or architecture is approved.

**Recommended safety/quality direction for later discussion:** if AI is used, answers should be grounded in retrieved authoritative SRD text rather than model memory, identify the SRD version/source, and say when the SRD does not cover the question. House rules should be distinguishable from official SRD answers.

## 9. Character creation

**Confirmed:** full character creation/building is not part of the current intended first scope.

It may be reconsidered in a future version, but should not drive current design.

## 10. Hosted data and personal-use scale

**Confirmed:** this is a personal-use system with a deliberately small foreseeable scale.

The owner wants shared data hosted online and expects that the normal personal-use workload should fit within a no-cost hosted tier.

The owner is willing to revisit hosting cost if the product scope meaningfully changes in the future.

**Current candidate mentioned by owner:** Neon (Postgres). This is a technical candidate only, not an approved provider.

**Design principle:** choose the backend later against actual requirements such as authentication, audit history, synchronization, security, backups, limits and maintenance; do not choose purely from storage size.

## 11. Administration surface: Windows or web

The earlier 'Windows app' idea is now better understood as an **administration/preparation surface**, not necessarily a requirement for a native Windows executable.

The owner expects that entering and managing NPCs/monsters and organizing campaign data may be more comfortable from a laptop/desktop than from a tablet.

Current product-surface picture:

- **Phone:** primarily player consultation/recovery when a physical sheet is missing;
- **Tablet:** primarily live-session DM dashboard, quick stats and combat tracking;
- **Desktop/laptop:** campaign/NPC/monster administration and data entry using whichever form is most practical (possible Windows client, web application, or local web interface).

**Open:** native Windows vs browser/web/local-web administration. This should be evaluated only after administration workflows are designed.

## 12. Campaign invitations

**Confirmed direction for first version:** campaign enrollment should be initiated/controlled by the DM rather than public discovery.

Candidate invitation mechanisms include:

- code;
- email invitation;
- QR code.

The exact combination and approval flow will be expanded later.

## 13. Language

**Confirmed:** all end-user-facing application content/UI must be Spanish because the intended users are Spanish speakers.

**Confirmed owner preference:** source code, technical repository documentation and development discussion/conventions should remain in English.

## 14. PDF source/template responsibility

**Confirmed:** character-sheet layouts are owned/maintained by the owner in Adobe InDesign.

If implementation reveals that a layout change is needed, the agent must record the requested change in the project and tell the owner what must be changed in InDesign.

The owner is willing to perform this owner-side design work when needed.

## 15. Ambiguity requiring clarification

The previous question about campaign multiplicity received the answer `Yes / No / I agree` to several subparts. Before recording a formal requirement, clarify:

1. one player may own/control more than one PC in the same campaign — interpreted as **Yes**;
2. more than one DM/co-DM in the same campaign — interpreted as **No for the first version**;
3. whether the underlying model should nevertheless avoid making future co-DM support impossible — likely what `I agree` referred to, but this must be confirmed.

## Next discovery questions

Highest-priority open questions now appear to be:

1. What is the paper-to-digital update workflow so the 'latest digital copy' stays useful?
2. Should DM corrections/reversals preserve immutable audit history exactly as recommended?
3. Confirm the one-player/multiple-PC and first-version co-DM interpretation above.
4. How should house rules be stored and consulted, especially when they override an official SRD answer?
5. What is the first practical NPC/monster data-entry workflow for the administration surface?
6. What parts of combat state, beyond initiative/current turn, need persistence or synchronization in the first version?

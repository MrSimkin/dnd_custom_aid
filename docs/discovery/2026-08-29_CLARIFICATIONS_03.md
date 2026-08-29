# Product Discovery Clarifications — Round 3

**Date:** 2026-08-29  
**Status:** Confirmed owner decisions  
**Phase:** Phase 1 — Product Discovery and Design

## Purpose

This file records the owner decisions made while closing the remaining high-value product questions after Round 2. These conclusions are confirmed, not brainstorming. They should be promoted into authoritative product/decision records before PR #2 is merged.

---

## 1. PDF export and unsaved edits

**Confirmed:** PDF export must distinguish saved character state from unsaved edits.

Normal export uses the latest fully saved character state. If the current edit screen contains unsaved changes and the user initiates export, the application must warn the user that unsaved changes exist and ask whether to export anyway.

If the user chooses to continue, the generated PDF may use the current edited values, including unsaved values. Exporting does **not** save or commit those values and does not create audit/change-history entries by itself.

If the user cancels, editing continues normally.

### Design consequence

`Export` and `Save` are separate operations. A normal committed character update should be atomic/grouped, but the user may deliberately generate a one-off PDF preview/output from the current unsaved edit state after acknowledging the warning.

---

## 2. Audit retention and storage growth

**Confirmed current policy:** keep complete grouped mechanical change history for now rather than prematurely deleting, summarizing, compressing or archiving it.

The project should trust the expected personal-use scale while **keeping an eye on actual growth**.

### Design consequence

- audit/history size must be measurable/observable enough to detect unexpected growth;
- implementation should avoid architectural choices that make later retention, summarization, archival or compression unnecessarily traumatic;
- no retention limit is required for the initial version unless real measurements justify one;
- do not build enterprise-grade retention machinery speculatively.

---

## 3. PC-style characters without an assigned player

**Confirmed:** a campaign may contain PC-style character records that currently have no player account assigned.

Examples include:

- pregenerated characters waiting for a guest/player;
- replacement/spare PCs prepared in advance;
- former player characters retained in the campaign;
- a PC temporarily run by the DM;
- a character intentionally left unassigned between controllers/owners as the product workflow requires.

### Design consequence

Character existence must not depend on having a current player account assignment. Assignment/control is a relationship that may be absent and added or changed later. The character itself remains preserved.

---

## 4. Stat-block internal granularity

**Confirmed:** traits, actions and similar stat-block elements should be first-class structured objects, but v1 should not decompose every game mechanic into atomic rules-engine fields.

The initial model should structure stable/useful identity such as the element name and category/type while allowing the complete mechanical description to remain formatted/rich text.

### Extensibility constraint

Architecture/data boundaries should allow later addition of deeper structured fields—such as attack bonus, reach, damage components, save DC, recharge or targets—through normal incremental migrations without requiring a fundamental rewrite of monster records, encounters or the combat tracker.

This is not permission to overengineer a future rules engine now.

---

## 5. Prepared and on-the-fly encounters

**Confirmed:** a saved encounter is an optional reusable preparation/template, not the live combat state itself and not a prerequisite for combat.

### Prepared flow

1. DM creates/saves an encounter composition in advance.
2. Starting/loading that encounter creates a **separate live encounter copy**.
3. Changes to the live copy do not modify the saved encounter template automatically.

### Live flexibility

The live encounter may be changed freely both before combat starts and at any point during combat. The DM may add, remove, duplicate, replace or modify creatures/NPCs as play develops—for example when reinforcements arrive or an unexpected participant joins the fray.

### On-the-fly flow

The DM may also create a new live encounter directly from scratch without any saved template and add participants as needed.

### Design consequence

The live encounter/combat tracker is the core runtime concept. Prepared encounters are one convenient way to populate a live encounter, not a separate combat system.

---

## 6. Account, campaign membership and moderation controls

**Confirmed baseline direction:**

- one persistent user account/identity per person;
- campaign membership remains DM-controlled;
- revocable invitation code/link is the core invitation mechanism;
- QR may represent/share the same invitation conveniently;
- email invitation may be added as convenience but is not required for the core workflow;
- standard email-based account/password recovery is preferred;
- campaign membership/data survives password recovery;
- DM may revoke/regenerate invitations;
- public campaign discovery and elaborate approval queues are outside the first scope.

### Confirmed moderation/control concepts

The owner explicitly wants the product to support:

- **Freeze PC** — preserve a character but prevent normal player use/editing until unfrozen; DM can still inspect/administer it.
- **Kick user** — remove the user from the campaign while allowing possible future re-entry through a valid invitation. Their campaign characters remain preserved rather than being deleted and may become unassigned.
- **Ban player** — remove the user and prevent that account from rejoining that campaign until the ban is lifted. Their characters/data remain preserved.
- **Freeze account** — temporarily disable the account application-wide while preserving data.

### Permission boundary

A campaign DM should control campaign-level actions such as kick, campaign ban and PC freeze. Application-wide account freeze is an application-owner/system-administration power, not a normal campaign-DM power, because the same account may participate in other campaigns.

These actions are non-destructive and should be reversible where appropriate.

---

## 7. Approved first usable release / MVP boundary

The owner approved the following MVP direction and the agent recommendations/refinements discussed with it.

### Player

- PC character sheet: create, view and edit through manual sheet-style data entry;
- PDF export using the approved template/output model;
- no guided/legal character-builder workflow in MVP;
- rules clarification using supported official SRD material only.

### DM — tablet/live session

- combat tracker;
- quick view of individual PC;
- PC-group quick view;
- NPC quick view;
- monster quick view;
- encounter quick view;
- full PC sheet view;
- full NPC sheet/dossier/stat view as applicable;
- full monster stat block view;
- full encounter view;
- create a live encounter on the fly;
- load a saved encounter into an independent live copy;
- freely modify the live encounter before or during combat.

### DM — desktop/laptop administration

- basic administration;
- manual monster data entry/creation;
- manual NPC data entry/creation;
- create/edit saved encounters;
- minimum account/campaign/PC administration required by the approved workflows.

The first desktop administration experience should prioritize functional data entry over polish. Sophisticated generators/importers/parsers are later increments unless implementation makes a limited capability trivial.

### Rules clarification

Rules clarification belongs in the MVP for both player and DM use.

MVP scope is **official SRD only**; house-rule-aware clarification is deferred.

The product requirement is the user outcome, not a premature AI architecture choice: a user should be able to ask a natural-language rules question in Spanish and receive a Spanish answer grounded only in the supported official SRD corpus with relevant source/version identifiable.

AI/provider/retrieval/implementation decisions remain deferred to architecture evaluation.

### Campaign count

MVP product behavior supports **one active campaign**.

This is a first-version product/UI restriction, **not** a requirement to hard-code the underlying model so that multiple campaigns become expensive to add later.

### Supporting functionality required by the MVP

Although not separate headline modules, the MVP necessarily includes enough of the following to make the approved workflows function:

- account/login/recovery;
- campaign membership and minimum administration;
- persistence/shared data;
- permissions/ownership/control relationships;
- local/offline persistence required by the combat tracker;
- synchronization needed for DM/player shared views.

### Explicitly outside MVP

- guided character builder;
- house-rule-aware clarification and reusable house-rule library;
- sophisticated NPC/monster generator;
- AI creature creation;
- advanced structured import/paste parsing;
- multiple active campaigns in the first-version UI/workflow;
- co-DM support;
- combat-history analytics;
- automated combat resolution;
- automated rules enforcement;
- automatic combat-to-character-sheet resource/HP mutation;
- sophisticated audit retention management unless measurements demonstrate a need;
- encounter balancing/CR automation;
- additional RPG systems.

### Priority observation

The combat tracker is the most important live-table MVP validation surface. A basic administration form can improve later; the live combat workflow must be fast, resilient and unobtrusive enough to provide real value during an actual session.

---

## 8. Discovery status after Round 3

The previously open questions covering PDF/export semantics, audit retention, unassigned PCs, stat-block granularity, prepared encounters, basic account/invitation/recovery behavior and MVP scope now have confirmed owner direction.

Technology stack and architecture remain intentionally unselected. The next product/technical step should be to promote these conclusions into authoritative product/decision documents, reconcile any stale `Pending` wording, then evaluate architecture/stack alternatives against the now-defined MVP and approved future-extensibility constraints rather than beginning implementation from an arbitrary technology choice.

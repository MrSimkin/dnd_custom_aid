# Project State

**Last verified:** 2026-08-29  
**Canonical branch:** `main`  
**Current working branch:** `discovery/initial-product-picture`  
**Open review:** PR #2 — `Capture and refine initial product discovery without premature implementation`  
**Phase:** Phase 1 — Product Discovery and Design  
**Status:** Three clarification rounds captured; the seven previously highlighted product questions are resolved, the MVP boundary is approved, and Round 3 decisions are reconciled into authoritative repository documents. No application code or technology stack exists yet.

## 1. Project in one paragraph

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D. It centers on a paper-first player workflow backed by a full digital character copy, a DM tablet live-session/combat surface, and a desktop-friendly preparation/administration surface. It is not intended to replace Foundry/VTTs, D&D Beyond or normal tabletop play. User-facing product content is Spanish; technical repository material is English. Git is the operative memory. The product baseline is now sufficiently coherent to evaluate architecture/technology alternatives, but consequential stack choices remain owner-controlled and no implementation should begin from an unapproved stack.

## 2. What exists now

There is no application code and no selected technology stack.

The active branch contains:

- the approved governance/continuity foundation;
- initial discovery plus Clarification Rounds 1, 2 and 3 under `docs/discovery/`;
- authoritative approved product/MVP direction in `docs/PRODUCT.md`;
- approved significant decisions through D-0032 in `docs/DECISIONS.md`;
- approved conventions in `docs/CONVENTIONS.md`;
- character-sheet template/change-request locations under `assets/character-sheets/`.

Three owner-provided DOCX examples—Quick NPC, Developed NPC and custom monster—were reviewed during Round 2 and their relevant design characteristics were captured in repository documentation. The binaries themselves remain conversation attachments rather than repository assets.

## 3. Approved MVP

### Player

- manually create/view/edit PC character sheets;
- export character sheets to PDF;
- use SRD-only natural-language rules clarification in Spanish with identifiable official source/version.

Manual PC creation is sheet-style data entry, not a guided/legal character builder.

### DM — tablet/live session

- combat tracker;
- quick views for individual PC, PC group, NPC, monster and encounter;
- full views for PC, NPC, monster and encounter;
- create live encounters completely on the fly;
- load saved encounters into independent live copies;
- freely add/remove/duplicate/replace/modify live participants before or during combat.

### DM — desktop/laptop

- basic administration;
- manual monster data entry/creation;
- manual NPC data entry/creation;
- saved encounter creation/editing;
- minimum account/campaign/PC administration required by approved workflows.

The first desktop experience should prioritize functional data entry over polish.

### First-version campaign restriction

One active campaign in product/UI behavior. This is not a structural requirement that the underlying model can only ever represent one campaign.

### Required supporting functionality

Account/login/recovery, campaign membership/invitations/moderation, persistence/shared data, permissions/ownership/control, local/offline combat persistence and synchronization needed for DM/player shared views.

## 4. Round 3 decisions now authoritative

### PDF export vs unsaved edits

Normal export uses latest saved state. If unsaved edits exist, warn the user and ask whether to export anyway. If accepted, current unsaved values may be used for that PDF without saving/committing them or creating audit entries. `Save` and `Export` are separate operations. See D-0027.

### Audit retention

Keep complete grouped mechanical history initially; monitor actual growth; add retention/archive/compression only if real measurements justify it. Avoid speculative enterprise retention machinery while keeping later evolution practical. See D-0028.

### Unassigned PCs

PC-style characters may exist without a current player assignment. Assignment/control is an optional/changeable relationship, not a prerequisite for character existence. See D-0029.

### Stat-block granularity

Traits/actions are first-class structured objects, but v1 keeps complete mechanics primarily as formatted descriptions rather than a full atomic rules engine. Deeper structured fields should remain incrementally addable later without fundamental rewrite. See D-0030.

### Encounter model

Saved encounters are reusable templates. Starting one creates an independent live encounter copy. Live encounters may be changed freely before/during combat, and may also be created entirely on the fly without a saved template. See D-0031.

### Accounts/invitations/moderation

Core direction: one persistent identity; DM-controlled revocable invitation code/link; QR as convenient sharing; email invitation optional; standard email-based recovery; DM invitation revoke/regenerate; no public campaign discovery/elaborate approval queue in MVP.

Approved controls are **Freeze PC**, **Kick user**, **Ban player**, and application-level **Freeze account**. Campaign DMs control campaign actions; application-wide account freeze is system/application administration. Data is preserved and controls are reversible where appropriate. See D-0032.

### MVP boundary

`D-0010 — Initial product scope / MVP` is now Approved. Rules clarification is SRD-only in MVP; one active campaign is a first-version UI/business restriction; combat tracker is the most important live-table validation surface.

## 5. Other major approved direction still in force

- Android phone/tablet remains the primary application target.
- Desktop/laptop-friendly administration is required, but native Windows vs web/local-web remains open.
- Physical sheets remain the preferred normal play surface; digital character is the durable full backup/reference.
- Player edits are not DM approval-gated; DM corrections use grouped compensating history.
- One user identity can have campaign-scoped roles; one user may have multiple PCs.
- Ownership and temporary control are distinct; inactive/dead/retired PCs remain preserved.
- First version has one active DM per campaign while avoiding an unnecessary future co-DM dead end.
- Broader product direction may mix SRD 5.1/D&D 5e, SRD 5.2.1/D&D 5.5e and house rules; MVP clarification is official-SRD-only.
- Quick/Developed NPC concepts remain the preferred NPC models.
- Monster records must support complete current D&D 5.5e Monster Manual-style stat blocks.
- Same-group creatures normally share initiative while retaining individual HP/status and may be split when needed.
- DM may manually override monster HP.
- Active combat survives app close/restart/network loss/session pause; DM local state remains authoritative during Internet loss.
- Combat does not automatically mutate persistent character sheets.
- Shared data should be hosted online and normally fit a no-cost tier at personal scale where practical; no provider is selected.

## 6. Explicit MVP exclusions

- guided/legal character builder;
- house-rule-aware clarification/reusable house-rule library;
- sophisticated NPC/monster generators;
- AI creature creation;
- advanced import/paste parsing;
- multiple active campaigns in first-version workflow;
- co-DMs;
- combat-history analytics;
- automated combat/rules enforcement;
- automatic combat-to-character-sheet mutation;
- speculative sophisticated audit-retention machinery;
- encounter balancing/CR automation;
- additional RPG systems.

## 7. Current technical status

No framework, language, Android UI toolkit, persistence architecture, hosted database provider, authentication provider, AI provider/model, PDF library, desktop implementation approach or build system has been selected.

Neon/Postgres remains only a candidate mentioned during discovery, not an approved choice.

The rules-clarification requirement specifies the product outcome—Spanish natural-language questions answered from supported official SRD material with identifiable source/version—not an AI/provider architecture.

No application tests exist because there is no application code.

## 8. Remaining work before implementation

The previous seven product questions are closed. The next major work is **architecture/technology evaluation**, especially:

1. Android + desktop administration architecture alternatives;
2. local/offline DM combat persistence and synchronization;
3. hosted shared data, authentication and the personal-use security/threat model;
4. PDF generation/rendering against owner-provided templates;
5. SRD storage/retrieval/clarification and provenance;
6. data-model boundaries that preserve approved incremental extensibility;
7. testing/build/release strategy once a stack is proposed.

Consequential technical alternatives must be explained to and approved by the owner before becoming durable architecture.

## 9. Current review state and next action

PR #2 remains open and intentionally unmerged. Round 3 is now reconciled into `docs/PRODUCT.md`, `docs/DECISIONS.md`, `docs/PROJECT_STATE.md`, `README.md`, `MANIFEST.md` and the PR description.

`main` remains canonical until the owner approves/merges PR #2 under D-0007.

**Next action:** verify PR #2 documentation consistency, then conclude/merge the discovery PR when owner-approved and begin a dedicated architecture/technology evaluation. Do not begin application implementation before consequential architecture/stack choices are reviewed and approved.

## 10. Handoff note

A fresh agent should read `README.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/CONVENTIONS.md`, `docs/PRODUCT.md` and the three clarification rounds.

Treat Round 3 conclusions as confirmed owner direction. The seven previous product questions are no longer open. Do not re-ask them unless a genuine contradiction or new requirement emerges. Do not infer technology choices from Neon, AI ideas, native Windows/web ideas or any PDF implementation concept.
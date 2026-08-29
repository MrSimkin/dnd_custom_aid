# Project State

**Last verified:** 2026-08-29  
**Canonical branch:** `main`  
**Current working branch:** `discovery/initial-product-picture`  
**Open review:** PR #2 — `Capture and refine initial product discovery without premature implementation`  
**Phase:** Phase 1 — Product Discovery and Design  
**Status:** Three clarification rounds captured; the seven previously highlighted product questions are resolved and the MVP boundary is approved. Technology/architecture evaluation is now the next major step; no application code exists yet.

## 1. Project in one paragraph

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D. It centers on a paper-first player workflow backed by a full digital character copy, a DM tablet live-session/combat surface, and a desktop-friendly preparation/administration surface. It is explicitly not intended to replace Foundry/VTTs, D&D Beyond or normal tabletop play. User-facing product content is Spanish; technical repository material is English. Git is the operative memory. Product behavior is now sufficiently defined to begin evaluating architecture/technology alternatives, but consequential stack choices remain owner-controlled and implementation must not begin from an arbitrary unapproved stack.

## 2. What exists now

There is still no application code and no selected technology stack.

The active discovery branch contains:

- approved governance/continuity foundation from PR #1;
- initial product brainstorming in `docs/discovery/2026-08-28_INITIAL_PRODUCT_PICTURE.md`;
- clarification Round 1 in `docs/discovery/2026-08-28_CLARIFICATIONS_01.md`;
- clarification Round 2 in `docs/discovery/2026-08-29_CLARIFICATIONS_02.md`;
- clarification Round 3 in `docs/discovery/2026-08-29_CLARIFICATIONS_03.md`;
- consolidated approved product direction and MVP in `docs/PRODUCT.md`;
- significant product decisions in `docs/DECISIONS.md`;
- approved conventions in `docs/CONVENTIONS.md`;
- PDF template location at `assets/character-sheets/templates/`;
- owner-side InDesign change log at `assets/character-sheets/CHANGE_REQUESTS.md`.

Three owner-provided DOCX examples were reviewed during Round 2 and their relevant design characteristics were captured in repository documentation:

- Quick NPC example;
- Developed NPC example;
- custom monster example.

The DOCX binaries themselves remain conversation attachments rather than repository assets.

## 3. Approved MVP boundary

### Player

- manually create/view/edit PC character sheets;
- export character sheets to PDF;
- use SRD-only natural-language rules clarification in Spanish with identifiable official source/version.

Manual PC creation means sheet-style data entry, not a guided/legal character builder.

### DM — tablet/live session

- combat tracker;
- quick view of individual PC, PC group, NPC, monster and encounter;
- full view of PC sheet, NPC information, monster stat block and encounter;
- create live encounters completely on the fly;
- load a saved encounter into an independent live encounter copy;
- freely add/remove/duplicate/replace/modify live participants before or during combat.

### DM — desktop/laptop

- basic administration;
- manual monster data entry/creation;
- manual NPC data entry/creation;
- saved encounter creation/editing;
- minimum account/campaign/PC administration required by the approved workflows.

The initial desktop administration surface should prioritize functional data entry over polish.

### MVP campaign restriction

- one active campaign in first-version product/UI behavior;
- this must not be encoded as an expensive structural dead end for future multiple-campaign support.

### Supporting functionality required by MVP

- account/login/recovery;
- campaign membership/invitations and minimum moderation;
- persistence/shared data;
- ownership/control/permission relationships;
- local/offline combat persistence;
- synchronization needed for DM/player shared views.

## 4. Confirmed Round 3 decisions

### PDF/export behavior

- normal export uses latest saved character state;
- if unsaved edits exist, warn the user and ask whether to export anyway;
- if the user continues, current unsaved values may be used for that PDF;
- exporting unsaved values does not save/commit them and does not create audit entries;
- `Save` and `Export` remain separate operations.

### Audit retention

- keep complete grouped mechanical change history initially;
- do not prematurely build deletion/summarization/archive machinery;
- keep actual growth observable/measurable;
- architecture should allow later retention/archive evolution if real measurements justify it.

### Unassigned PCs

- PC-style characters may exist without a current player account assigned;
- assignment/control is optional and may change without deleting the character.

### Stat-block granularity

- traits/actions are first-class structured objects;
- v1 keeps complete mechanics primarily as formatted/rich descriptions rather than a full atomic rules engine;
- deeper structured fields must be addable later through normal migrations without fundamental monster/encounter/combat redesign.

### Encounter model

- saved encounter = reusable preparation/template;
- starting it creates a separate independent live encounter copy;
- live copies may be freely changed before/during combat without automatically modifying the saved template;
- live encounters may also be created entirely from scratch/on the fly;
- the live encounter/combat tracker is the core runtime model.

### Account/membership/moderation direction

- one persistent user identity per person;
- DM-controlled revocable invitation code/link as core campaign invitation;
- QR may represent/share it; email invitation is optional convenience;
- standard email-based account/password recovery;
- campaign membership/data survives password recovery;
- DM may revoke/regenerate invitations;
- no public campaign discovery or elaborate approval queue in MVP;
- **Freeze PC:** reversible campaign-level character restriction while preserving data;
- **Kick user:** remove from campaign but allow later valid re-entry; characters remain preserved/unassigned as needed;
- **Ban player:** prevent that account from rejoining the campaign until lifted; data remains preserved;
- **Freeze account:** application-wide temporary disable controlled by application/system administration, not an ordinary campaign DM.

## 5. Other major approved direction still in force

- Android phone/tablet is the main application target.
- Desktop/laptop administration is a real workflow need; native Windows vs web/local-web remains open.
- Physical character sheets remain preferred for normal play.
- Digital characters are full backup/reference copies and may contain transient end-of-session state.
- Player edits are not DM approval-gated.
- Audit uses grouped mechanical change sets and compensating correction/reversal history.
- One user identity may have different campaign-scoped roles.
- One player may have multiple PCs in one campaign.
- Character ownership and temporary control are distinct.
- Inactive/dead/retired PCs remain preserved.
- First version supports one active DM per campaign while avoiding an unnecessary future co-DM dead end.
- D&D is first; additional RPG systems are later possibilities only.
- Broader product direction may mix SRD 5.1 / D&D 5e, SRD 5.2.1 / D&D 5.5e and house rules.
- MVP rules clarification is deliberately SRD-only; house-rule-aware clarification is later.
- Quick and Developed NPC concepts remain the owner-preferred NPC models.
- Monster records must support complete current D&D 5.5e Monster Manual-style stat blocks.
- Same-group creatures normally share initiative while retaining individual HP/status and may be split when needed.
- DM may manually override monster HP.
- Active combat survives app close/restart/network loss/session pause; DM local state remains authoritative during Internet loss.
- Combat working state does not automatically mutate persistent character sheets.
- Shared data should be hosted online and normally fit a no-cost tier at personal scale where practical; no provider is selected.

## 6. Explicit MVP exclusions

Do not currently build:

- guided/legal character builder;
- house-rule-aware rules clarification/reusable house-rule library;
- sophisticated NPC/monster generators;
- AI creature creation;
- advanced structured import/paste parsing;
- multiple active campaigns in first-version UI/workflow;
- co-DMs;
- combat-history analytics;
- automated combat resolution;
- automated rules enforcement;
- automatic combat-to-character-sheet mutation;
- speculative sophisticated audit-retention machinery;
- encounter balancing/CR automation;
- additional RPG systems.

## 7. Current technical status

No stack, framework, language, Android UI toolkit, persistence architecture, hosted database provider, authentication provider, AI provider/model, PDF library, desktop implementation approach or build system has been selected.

Neon/Postgres remains only a candidate mentioned during discovery, not an approved choice.

The rules-clarification requirement defines the outcome—Spanish natural-language questions answered from supported official SRD material with identifiable source/version—not an AI/provider implementation choice.

No application tests exist because there is no application code.

## 8. Remaining design/technical questions

The previous seven high-value product questions are closed.

Remaining work now shifts to technical/product-detail evaluation needed before implementation, especially:

1. architecture/stack alternatives for Android + desktop administration;
2. local/offline DM combat persistence and synchronization model;
3. hosted shared-data/authentication approach and personal-use security/threat model;
4. PDF generation/rendering approach against owner-provided templates;
5. SRD storage/retrieval/clarification architecture and source provenance;
6. practical data model boundaries consistent with approved future extensibility;
7. testing/build/release strategy once the stack is proposed.

These are not pre-approved technical choices. Consequential alternatives must be explained and approved by the owner before becoming durable architecture.

## 9. Current review state

PR #2 remains open and intentionally unmerged.

Round 3 conclusions have been recorded in discovery and are being reconciled into authoritative product/decision/state documentation on the branch. `main` remains canonical until the owner approves/merges the PR under D-0007.

Do not interpret the approved MVP as permission to begin application implementation before the architecture/stack evaluation and owner approval step.

## 10. Next action

Complete authoritative decision-log reconciliation for Round 3, verify PR #2 documentation consistency, then begin a dedicated architecture/technology evaluation against the approved MVP.

The architecture discussion should compare realistic alternatives against actual product requirements rather than selecting technologies by fashion or familiarity.

## 11. Handoff note for the next agent

Start with `README.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/CONVENTIONS.md`, `docs/PRODUCT.md`, and the three discovery clarification rounds.

Treat Round 3 decisions as confirmed owner direction. The seven previous product questions are no longer open. Do not re-ask them unless a genuine contradiction or new requirement emerges. Do not infer technology choices from Neon, AI ideas, native Windows/web ideas or any PDF implementation concept.
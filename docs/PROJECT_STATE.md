# Project State

**Last verified:** 2026-08-29  
**Canonical branch:** `main`  
**Current working branch:** `discovery/initial-product-picture`  
**Open review:** PR #2 — `Capture and refine initial product discovery without premature implementation`  
**Phase:** Phase 1 — Product Discovery and Design (final pre-merge closure)  
**Status:** Product/MVP baseline approved; final consistency audit completed; final product-tension clarification pass completed and being consolidated into authoritative records. The MVP is now multicampaign. No application code or technology stack exists yet. PR #2 remains intentionally unmerged pending owner approval after final re-verification.

## 1. Project in one paragraph

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D. It centers on a paper-first player workflow backed by a full digital character copy, a DM Android tablet live-session/combat surface, and an intentionally narrower desktop/laptop DM preparation/administration surface using the same shared campaign/domain data. The MVP is multicampaign. It is not intended to replace Foundry/VTTs, D&D Beyond or normal tabletop play. User-facing product content is Spanish; technical repository material is English. Git is the operative memory. The product baseline is sufficiently coherent to evaluate architecture/technology alternatives, but consequential stack choices remain owner-controlled and no implementation should begin from an unapproved stack.

## 2. What exists now

There is no application code and no selected technology stack.

The active branch contains:

- the approved governance/continuity foundation;
- initial discovery plus Clarification Rounds 1, 2 and 3 under `docs/discovery/`;
- a final pre-merge tension-resolution record at `docs/discovery/2026-08-29_TENSION_RESOLUTIONS.md`;
- authoritative approved product/MVP direction in `docs/PRODUCT.md`;
- approved significant decisions in `docs/DECISIONS.md`, with final tension-pass consolidation still to be completed there before merge;
- approved conventions in `docs/CONVENTIONS.md`;
- architecture-evaluation readiness in `docs/ARCHITECTURE.md`;
- phone/tablet and future desktop/laptop verification expectations in `docs/TESTING.md`;
- character-sheet template/change-request locations under `assets/character-sheets/`.

Three owner-provided DOCX examples—Quick NPC, Developed NPC and custom monster—were reviewed during Round 2 and their relevant design characteristics were captured in repository documentation. The binaries themselves remain conversation attachments rather than repository assets.

## 3. Approved MVP

### Player — Android phone/tablet

- manually create/view/edit PC character sheets;
- export character sheets to PDF;
- use SRD-only natural-language rules clarification in Spanish with identifiable official source/version;
- select/use campaigns appropriate to multicampaign membership.

Manual PC creation is sheet-style data entry, not a guided/legal character builder.

### DM — Android tablet/live session

- combat tracker;
- quick views for individual PC, PC group, NPC, monster and encounter;
- full views for PC, NPC, monster and encounter;
- create live encounters completely on the fly;
- load saved encounters into independent live copies;
- freely add/remove/duplicate/replace/modify live participants before or during combat;
- select/manage the relevant campaign in the multicampaign workflow.

### DM — desktop/laptop

- basic administration;
- manual monster data entry/creation;
- manual NPC data entry/creation;
- saved encounter creation/editing;
- minimum account/campaign/PC administration required by approved workflows;
- multicampaign administration/selection as required.

The first desktop experience should prioritize functional preparation/data entry over polish. Desktop is not required to duplicate the Android application, does not require a player-facing desktop app, does not require feature parity, and does not require the combat tracker in the MVP. Broader parity may be considered much later.

### Campaign scope

The previous one-active-campaign MVP restriction is withdrawn. The MVP is **multicampaign**:

- multiple campaigns may exist and be active concurrently;
- one account may participate in multiple campaigns concurrently;
- roles and moderation remain campaign-scoped;
- campaign-scoped entities/history retain explicit campaign association;
- sufficient campaign selection/switching UX is part of the MVP.

### Required supporting functionality

Account/login/recovery, multicampaign membership/selection, invitations/moderation, persistence/shared data, permissions/ownership/control, local/offline combat persistence and synchronization needed for DM/player shared views.

## 4. Final pre-merge tension resolutions now authoritative product direction

The owner explicitly resolved the following eight soft tensions/underspecified boundaries on 2026-08-29. `docs/PRODUCT.md` has been promoted accordingly; `docs/DECISIONS.md` must carry the durable decision consolidation before merge.

### 4.1 Android live use vs desktop administration

- Android remains the primary at-the-table/live-use surface.
- Desktop/laptop remains primarily preparation/administration.
- Both use the same campaign/domain data.
- No player desktop application is required in MVP.
- Android/desktop feature parity is a very-future possibility, not current or near-term scope.
- Desktop combat tracker is not an MVP requirement.
- Desktop implementation form remains a Phase 2 architecture choice.

### 4.2 Multicampaign MVP

- The MVP is multicampaign.
- The previous single-active-campaign restriction is obsolete.
- Multiple campaigns and concurrent membership are supported now rather than deferred.

### 4.3 Mixed/homebrew campaigns vs SRD-only assistant

- Campaigns may freely mix D&D 5e/SRD 5.1, D&D 5.5e/SRD 5.2.1 and homebrew.
- Character/NPC/monster data does not reject content merely because it is not legal SRD.
- The application is not a rules enforcer.
- MVP clarification may answer from both supported official SRDs with source/version identified.
- MVP clarification does not know/apply house rules automatically; house-rule-aware answers remain later scope.

### 4.4 Complete monster records vs selective machine structure

- Monster records must represent/display the complete stat block.
- Stable/core fields are structured where useful.
- Traits/actions/bonus actions/reactions/legendary actions and similar elements are separate structured records with category/order.
- Their complete mechanics may remain formatted text.
- No executable rules understanding is required.
- Current architecture must permit later additive structured enrichment without fundamental rewrite.

### 4.5 Paper live authority vs durable digital state

- Paper is authoritative during normal paper-first play.
- Digital is the latest intentionally saved/reconciled durable baseline, which may lag live paper state.
- Last-updated/freshness indication is required.
- If the app is used instead of paper, digital may temporarily be the live authority.
- No automatic paper/digital conflict merge is attempted.

### 4.6 Local-first DM combat vs hosted shared data

- Shared durable domain data is normally hosted/synchronized online.
- An active live encounter has one authoritative DM working state at a time.
- DM combat actions commit locally first; cloud sync is secondary/opportunistic.
- Older remote combat state must not overwrite newer authoritative local DM state after reconnection.
- Player combat views use the latest synchronized public projection.
- Offline player tracker edits are provisional and are replaced/reconciled to DM authority after reconnection.
- DM UI should distinguish saved locally / synced / waiting to sync.
- MVP supports same-device combat persistence/recovery, not seamless concurrent multi-device DM editing.
- Future DM-device movement should use explicit authority transfer/resume rather than concurrent authority.

### 4.7 Campaign moderation vs global account administration

- Campaign DM authority is limited to that campaign.
- DM may Freeze PC, Kick, Ban from campaign and revoke/regenerate invitations.
- DM cannot globally disable/delete/control a user's application account.
- Campaign kick/ban does not affect other campaigns.
- Global Freeze Account belongs only to application administration, is reversible, and preserves data/relationships.
- Campaign moderation state and global account state remain separate.
- At present the project owner is the only DM and only application administrator, but the model must not equate those roles.

### 4.8 Invitation lifecycle/rejoining

- Invitations are campaign-specific.
- Valid signed-in invite joins directly with no second DM approval.
- Sign-in/account creation resumes the same invite flow.
- Invite code/link is reusable by multiple people until revoked/regenerated.
- Regeneration invalidates the prior invite.
- Ban prevents rejoin; Kick allows later rejoin with a valid invite.
- Rejoin preserves existing identity/history relationships rather than duplicating them.
- Membership and PC ownership/control remain separate.
- No mandatory invite expiry in MVP.
- QR/email are delivery/representation conveniences for the same invite workflow.

## 5. Other major approved direction still in force

- Android phone/tablet remains the primary application target.
- Desktop/laptop-friendly DM administration is required, but native Windows vs web/local-web remains open.
- Physical sheets remain the preferred normal play surface; digital character is the durable full backup/reference after reconciliation.
- Player edits are not DM approval-gated; DM corrections use grouped compensating history.
- One user identity can have campaign-scoped roles; one user may have multiple PCs.
- Ownership and temporary control are distinct; inactive/dead/retired PCs remain preserved.
- First version has one active DM per campaign while avoiding an unnecessary future co-DM dead end.
- Quick/Developed NPC concepts remain the preferred NPC models.
- Same-group creatures normally share initiative while retaining individual HP/status and may be split when needed.
- DM may manually override monster HP.
- Combat does not automatically mutate persistent character sheets.
- Shared durable data should be hosted online and normally fit a no-cost tier at personal scale where practical; no provider is selected.

## 6. Explicit MVP exclusions

- guided/legal character builder;
- house-rule-aware clarification/reusable house-rule library;
- sophisticated NPC/monster generators;
- AI creature creation;
- advanced import/paste parsing;
- co-DMs within the same campaign;
- combat-history analytics;
- automated combat/rules enforcement;
- automatic combat-to-character-sheet mutation;
- speculative sophisticated audit-retention machinery;
- encounter balancing/CR automation;
- additional RPG systems;
- player desktop application/full Android-desktop parity;
- desktop combat tracker requirement;
- seamless concurrent multi-device DM combat editing.

**Multiple active campaigns are no longer an exclusion.**

## 7. Current technical status

No framework, language, Android UI toolkit, persistence architecture, hosted database provider, authentication provider, AI provider/model, PDF library, desktop implementation approach or build system has been selected.

Neon/Postgres remains only a candidate mentioned during discovery, not an approved choice.

The rules-clarification requirement specifies the product outcome—Spanish natural-language questions answered from supported official SRD material with identifiable source/version—not an AI/provider architecture.

No application tests exist because there is no application code.

## 8. Phase 1 consistency audit and final tension pass

The final audit reviewed the authority/lifecycle chain across `AGENTS.md`, README, manifest, decisions, product definition, project state, roadmap, architecture, testing, workflow, conventions, Round 3 discovery record and PR #2.

Earlier corrections included:

- `AGENTS.md` no longer says the project is only entering discovery; it permits architecture evaluation while blocking unapproved implementation/scaffolding.
- D-0003 points to the later approved role/permission model instead of describing permissions as unresolved.
- D-0009 represents the full pending application-architecture/technology decision rather than only an Android-stack placeholder, and explicitly states that evaluation may begin.
- `docs/ARCHITECTURE.md` says the architecture-evaluation gate has been reached and includes Android, desktop administration, offline/sync, auth, PDF and SRD requirements.
- `docs/ROADMAP.md` clarifies Phase 1 closure and incremental feature-level acceptance criteria.
- `docs/TESTING.md` includes the approved desktop/laptop administration surface and future cross-surface verification.
- Round 3 and `MANIFEST.md` state that promotion of Round 3 decisions into authoritative records is complete.
- README orientation/read-order wording is aligned with `AGENTS.md`.

The subsequent owner-led tension pass explicitly resolved all eight product tensions listed in Section 4, including the substantive scope change from single-campaign to multicampaign MVP.

**Current audit conclusion:** no additional product-level contradiction or unresolved behavioral tension remains from the identified Phase 1 set. The intentionally pending question is application architecture/technology selection (D-0009), which is Phase 2 work rather than deferred product behavior.

## 9. Branch/merge reconciliation

The discovery branch had previously become one commit behind `main` because `main` received an early project-state bookkeeping commit after the branch split. That commit did not contain competing product decisions; its state text was superseded by the much newer branch documentation.

The branch was reconciled with current `main` using an explicit merge commit while preserving the newer discovery-branch state where the same project-state file overlapped.

Before the final tension-pass commits, verification showed:

- compare base: current `main` commit `03d8bc3fdcc54cde9e29f9fc977235944e8b3d48`;
- discovery branch was **0 commits behind**;
- GitHub reported PR #2 **mergeable/clean**.

Because additional tension-resolution/promotional commits have now been added, PR mergeability/branch comparison must be re-verified again before the owner is asked for final merge approval.

## 10. Remaining work before implementation

Product behavior is now sufficiently resolved for Phase 1 closure. The remaining pre-merge bookkeeping is:

1. consolidate the final tension resolutions into `docs/DECISIONS.md`;
2. update any remaining stale multicampaign/surface wording in roadmap/manifest/PR description as needed;
3. rerun final branch/PR consistency and mergeability verification;
4. present that final verification to the owner for merge approval.

After PR #2 is merged, the next major work is **Phase 2 architecture/technology evaluation**, especially:

1. overall Android + desktop administration topology;
2. local-first/offline DM combat persistence and combat-aware synchronization;
3. multicampaign hosted shared data, authentication and the personal-use security/threat model;
4. PDF generation/rendering against owner-provided templates;
5. SRD storage/retrieval/clarification and provenance;
6. data-model boundaries that preserve approved incremental extensibility;
7. testing/build/release strategy once a stack is proposed.

Consequential technical alternatives must be explained to and approved by the owner before becoming durable architecture or implementation foundation.

## 11. Current review state and next action

PR #2 remains open and intentionally unmerged. `main` remains canonical until the owner approves/merges PR #2 under D-0007.

**Next action:** finish authoritative decision/roadmap/PR promotion of the final tension pass, rerun final consistency/mergeability verification, then present the result to the owner. Only after explicit owner merge approval should PR #2 be merged.

After merge, immediately update canonical project state to **Phase 1 complete / Phase 2 architecture evaluation active** before beginning the architecture discussion.

## 12. Handoff note

A fresh agent should read `README.md`, `AGENTS.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/CONVENTIONS.md`, `docs/PRODUCT.md`, `docs/ROADMAP.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`, the three clarification rounds, and `docs/discovery/2026-08-29_TENSION_RESOLUTIONS.md`.

Treat the final tension-pass conclusions as confirmed owner direction. Do not re-ask them unless a genuine contradiction or new requirement emerges. Do not infer technology choices from Neon, AI ideas, native Windows/web ideas or any PDF implementation concept.
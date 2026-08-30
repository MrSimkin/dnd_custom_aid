# Project State

**Last verified:** 2026-08-29  
**Canonical branch:** `main`  
**Current working branch:** `discovery/initial-product-picture`  
**Open review:** PR #2 — `Capture and refine initial product discovery without premature implementation`  
**Phase:** Phase 1 — Product Discovery and Design (final pre-merge closure)  
**Status:** Product/MVP baseline approved; final consistency audit and eight-item product-tension clarification pass completed and promoted into authoritative records. The MVP is multicampaign. No application code or technology stack exists. PR #2 remains intentionally unmerged pending explicit owner approval.

## 1. Project in one paragraph

`dnd_custom_aid` is a personal/small-scale tabletop RPG assistant beginning with D&D. It centers on a paper-first player workflow backed by a full digital character copy, a DM Android tablet live-session/combat surface, and an intentionally narrower desktop/laptop DM preparation/administration surface using the same shared campaign/domain data. The MVP is multicampaign. It is not intended to replace Foundry/VTTs, D&D Beyond or normal tabletop play. User-facing product content is Spanish; technical repository material is English. Git is the operative memory. The product baseline is sufficiently coherent to evaluate architecture/technology alternatives, but consequential stack choices remain owner-controlled and no implementation should begin from an unapproved stack.

## 2. What exists now

There is no application code and no selected technology stack.

The active branch contains:

- approved governance/continuity foundation;
- initial discovery plus Clarification Rounds 1, 2 and 3;
- final tension-resolution rationale in `docs/discovery/2026-08-29_TENSION_RESOLUTIONS.md`;
- authoritative approved product/MVP direction in `docs/PRODUCT.md`;
- approved significant decisions through **D-0033** in `docs/DECISIONS.md`;
- approved conventions in `docs/CONVENTIONS.md`;
- architecture-evaluation criteria in `docs/ARCHITECTURE.md`;
- verification expectations in `docs/TESTING.md`;
- character-sheet template/change-request locations under `assets/character-sheets/`.

Three owner-provided DOCX examples—Quick NPC, Developed NPC and custom monster—were reviewed during Round 2 and their relevant design characteristics were captured in repository documentation. The binaries themselves remain conversation attachments rather than repository assets.

## 3. Approved MVP

### Player — Android phone/tablet

- manually create/view/edit PC character sheets;
- export character sheets to PDF;
- use official-SRD-only natural-language rules clarification in Spanish across the supported SRDs with identifiable source/version;
- select/use campaigns appropriate to multicampaign membership.

Manual PC creation is sheet-style data entry, not a guided/legal character builder.

### DM — Android tablet/live session

- combat tracker;
- quick/full PC, PC-group, NPC, monster and encounter views;
- create live encounters completely on the fly;
- load saved encounters into independent live copies;
- freely add/remove/duplicate/replace/modify live participants before or during combat;
- select/manage the relevant campaign in the multicampaign workflow;
- continue authoritative live combat locally during Internet loss.

### DM — desktop/laptop

- basic preparation/administration;
- manual monster data entry/creation;
- manual NPC data entry/creation;
- saved encounter creation/editing;
- minimum account/campaign/PC administration required by approved workflows;
- multicampaign administration/selection as required.

Desktop is not required to duplicate the Android application, does not require a player-facing desktop app, does not require feature parity, and does not require the combat tracker in MVP. Broader parity may be considered much later.

### Campaign scope

The previous one-active-campaign MVP restriction is withdrawn. The MVP is **multicampaign**:

- multiple campaigns may exist and be active concurrently;
- one account may participate in multiple campaigns concurrently;
- roles and moderation remain campaign-scoped;
- campaign-scoped entities/history retain explicit campaign association;
- sufficient campaign selection/switching UX is part of MVP.

### Required supporting functionality

Account/login/recovery, multicampaign membership/selection, invitations/moderation, persistence/shared data, permissions/ownership/control, local/offline combat persistence and synchronization needed for DM/player shared views.

## 4. Final pre-merge tension resolutions

The owner explicitly resolved eight soft tensions/underspecified boundaries on 2026-08-29. They are durable in D-0033 and promoted through product/state/roadmap/architecture/testing documentation.

### 4.1 Android live use vs desktop administration

- Android remains the primary at-the-table/live-use surface.
- Desktop/laptop remains primarily preparation/administration.
- Both use the same campaign/domain data.
- No player desktop application is required in MVP.
- Android/desktop feature parity is a very-future possibility, not current or near-term scope.
- Desktop combat tracker is not an MVP requirement.
- Desktop implementation form remains a Phase 2 architecture choice.

### 4.2 Multicampaign MVP

- MVP is multicampaign.
- The previous single-active-campaign restriction is obsolete.
- Multiple campaigns and concurrent account memberships are supported now rather than deferred.

### 4.3 Mixed/homebrew campaigns vs SRD-only assistant

- Campaigns may freely mix D&D 5e/SRD 5.1, D&D 5.5e/SRD 5.2.1 and homebrew.
- Character/NPC/monster data does not reject content merely because it is not legal SRD.
- The application is not a rules enforcer.
- MVP clarification may answer from both supported official SRDs with source/version identified.
- MVP clarification does not know/apply house rules automatically; house-rule-aware answers remain later scope.

### 4.4 Complete monster records vs selective machine structure

- Monster records represent/display the complete stat block.
- Stable/core fields are structured where useful.
- Traits/actions/bonus actions/reactions/legendary actions and similar elements are separate structured records with category/order.
- Their complete mechanics may remain formatted text.
- No executable rules understanding is required.
- Architecture must permit later additive structured enrichment without fundamental rewrite.

### 4.5 Paper live authority vs durable digital state

- Paper is authoritative during normal paper-first play.
- Digital is the latest intentionally saved/reconciled durable baseline and may lag live paper state.
- Last-updated/freshness indication is required.
- If the app is used instead of paper, digital may temporarily be live authority.
- No automatic paper/digital conflict merge is attempted.

### 4.6 Local-first DM combat vs hosted shared data

- Shared durable domain data is normally hosted/synchronized online.
- An active encounter has one authoritative DM working state at a time.
- DM combat actions commit locally first; cloud sync is secondary/opportunistic.
- Older remote state must not overwrite newer authoritative local DM state after reconnection.
- Player combat views use the latest synchronized public projection.
- Offline player tracker edits are provisional and yield to DM authority after reconnection.
- DM UI should distinguish saved locally / synced / waiting to sync.
- MVP supports same-device combat persistence/recovery, not seamless concurrent multi-device DM editing.
- Future DM-device movement should use explicit authority transfer/resume.

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
- A valid signed-in invite joins directly with no second DM approval.
- Sign-in/account creation resumes the same invite flow.
- Invite code/link is reusable by multiple people until revoked/regenerated.
- Regeneration invalidates the prior invite.
- Ban prevents rejoin; Kick allows later rejoin with a valid invite.
- Rejoin preserves existing identity/history relationships rather than duplicating them.
- Membership and PC ownership/control remain separate.
- No mandatory invite expiry in MVP.
- QR/email are delivery/representation conveniences for the same invite workflow.

## 5. Other major approved direction still in force

- Player edits are not DM approval-gated; DM corrections use grouped compensating history.
- One user identity can have campaign-scoped roles; one user may have multiple PCs.
- Ownership and temporary control are distinct; inactive/dead/retired PCs remain preserved.
- First version has one active DM **per campaign** while avoiding an unnecessary future co-DM dead end.
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

D-0009 — application architecture and implementation technology — remains intentionally **Pending**.

No framework, language, Android UI toolkit, persistence architecture, hosted database provider, authentication provider, AI provider/model, PDF library, desktop implementation approach or build system has been selected.

Neon/Postgres remains only a candidate mentioned during discovery, not an approved choice.

No application tests exist because there is no application code.

## 8. Phase 1 consistency result

The original final audit reviewed the authority/lifecycle chain across `AGENTS.md`, README, manifest, decisions, product definition, project state, roadmap, architecture, testing, workflow, conventions, discovery records and PR #2.

The later owner-led tension pass then explicitly resolved all eight remaining soft product tensions, including the substantive scope change from single-campaign to multicampaign MVP.

Promotion is complete across:

- `README.md`;
- `MANIFEST.md`;
- `docs/PRODUCT.md`;
- `docs/DECISIONS.md` through D-0033;
- `docs/PROJECT_STATE.md`;
- `docs/ROADMAP.md`;
- `docs/ARCHITECTURE.md`;
- `docs/TESTING.md`;
- `docs/discovery/2026-08-29_TENSION_RESOLUTIONS.md` as historical rationale.

**Conclusion:** no additional product-level contradiction or unresolved behavioral tension remains from the identified Phase 1 set. D-0009 is intentionally technical Phase 2 work rather than deferred product behavior.

## 9. Branch/PR state

The branch was previously reconciled with current `main`; the main commit `03d8bc3fdcc54cde9e29f9fc977235944e8b3d48` remains the merge base.

Immediately before this final project-state bookkeeping commit:

- discovery branch was **58 commits ahead and 0 behind** `main`;
- PR #2 was open and unmerged;
- GitHub reported **`mergeable: true`** after recalculating the latest documentation commits.

Because this file update itself advances the PR head by one documentation-only commit, the pre-merge workflow must re-fetch the current head/compare/mergeability immediately before any merge. No merge has been authorized yet.

## 10. Remaining work before implementation

Phase 1 product behavior is resolved. The only remaining Phase 1 gate is final PR verification plus explicit owner approval to merge PR #2.

After merge, the next major work is **Phase 2 architecture/technology evaluation**, beginning with overall application topology/surface relationship, then:

1. Android client approach;
2. desktop/laptop administration delivery approach;
3. multicampaign domain/data model boundaries;
4. local-first combat persistence and combat-aware synchronization;
5. hosted backend/database/authentication/authorization/moderation boundaries;
6. PDF generation/rendering;
7. SRD storage/retrieval/clarification and provenance;
8. testing/build/CI and durable project/module conventions.

Consequential technical alternatives must be explained to and approved by the owner before becoming durable architecture or implementation foundation.

## 11. Current review state and next action

PR #2 remains open and intentionally unmerged. `main` remains canonical until the owner explicitly approves/merges PR #2 under D-0007.

**Next action:** re-fetch current PR head and mergeability after this bookkeeping commit. If clean, present the final merge-readiness result to the owner. Only explicit owner approval authorizes merge.

After merge, immediately update canonical project state to **Phase 1 complete / Phase 2 architecture evaluation active** before beginning architecture discussion.

## 12. Handoff note

A fresh agent should read `README.md`, `AGENTS.md`, `MANIFEST.md`, this file, `docs/DECISIONS.md`, `docs/CONVENTIONS.md`, `docs/PRODUCT.md`, `docs/ROADMAP.md`, `docs/ARCHITECTURE.md`, `docs/TESTING.md`, the three clarification rounds, and `docs/discovery/2026-08-29_TENSION_RESOLUTIONS.md`.

Treat all eight tension-pass conclusions as confirmed owner direction. Do not re-ask them unless a genuine contradiction or new requirement emerges. Do not infer technology choices from Neon, AI ideas, native Windows/web ideas or any PDF implementation concept.
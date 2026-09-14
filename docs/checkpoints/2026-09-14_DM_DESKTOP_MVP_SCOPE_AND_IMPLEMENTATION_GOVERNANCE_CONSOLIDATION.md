# 2026-09-14 checkpoint — DM Desktop product closure, MVP boundary and implementation governance

**Branch at checkpoint creation:** `docs/mvp-product-design-consolidation-2026-09-14`  
**Base `main` before checkpoint:** `2fe118bacdf13123835af56d69ee91101d5cae99`  
**Player runtime authority remains:** `implementation/phase4a-successor-cycle`  
**Player frozen candidate known at this checkpoint:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`  
**Code changes in this checkpoint:** none; documentation/design consolidation only

## Why this checkpoint exists

After D-0071, the owner and technical assistant completed the detailed DM Desktop product-definition pass, exact integrated-MVP boundary, implementation-wave direction and Git convergence strategy. The owner then explicitly requested another safety/continuity consolidation so this progress would not remain dependent on chat context.

The owner also corrected the collaboration boundary: low-level technical matters are outside their intended competence and should not be repeatedly presented for rubber-stamp approval. Product/scope/user-facing decisions remain owner decisions; technical implementation details are delegated unless they materially alter behavior, risk, cost, privacy/security or approved scope.

## Read first when resuming

1. `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`;
2. `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`;
3. `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`;
4. `docs/PROJECT_STATE.md`;
5. `docs/BRANCH_STATUS.md`;
6. for exact Player runtime/QA evidence, switch to `implementation/phase4a-successor-cycle` and read that branch's checkpoint.

## What was closed since the previous checkpoint

### 7D — DM Desktop detailed product definition — CLOSED

The Desktop App is one workbench with:

- **Live / Workspace:** DM Screen, Stage Desk, Dungeon Desk, Combat Desk;
- **Prepare / Manage:** PCs, NPCs, Monsters, Homebrew & Rules, Stages/Places, Dungeons/Zones, Encounters, Media/Handouts and justified related content;
- **Administration:** Campaign Administration plus permission-gated System Administration.

Desktop Live has the same game/domain semantics as DM Android/tablet; only presentation/UX differs.

### Monster Manager

Approved MVP capabilities include full human-usable editing, Personal/Campaign/Official scope, provenance/copies, dirty-improv cleanup, Packages, import/export and a Creature Creator Assistant. The assistant is advisory, role/profile/benchmark based and does not claim mathematically objective balance.

### NPC Manager

Approved model supports Quick NPC -> Developed NPC -> optional mechanics/full stat block. NPCs never require mechanics to exist. A helper may ask useful questions/suggest ideas without mandatory completeness scoring. Import/export applies.

### Homebrew & Rules Manager

Expanded beyond simple rules to include rules/rulings/variants plus structured races/sub-races, classes/subclasses, backgrounds, feats, spells, items/magic items and other justified custom content. Campaign content integrates with relevant Player/DM surfaces but does not create an automatic legality/rules engine. Import/export/bulk import apply. Homebrew-aware AI remains outside MVP.

### Stage / Dungeon preparation

Stage Manager handles Places/Shops/NPC links/retrieval/Scene Spine. Dungeon Manager handles topology, rich Zone Briefs, Encounter Readiness, clocks and advisory triggers. Paper references and partial digital preparation remain valid; no VTT behavior is introduced.

### Encounter Manager

Saved encounter != live encounter. Personal reusable encounters may carry Personal Monster/NPC dependencies; using the encounter in a campaign may create the needed independent campaign dependency copies. Reserves/conditionals/guidance/overrides/import/export are included; no automatic encounter-balance authority.

### PC Manager / Audit

Desktop DM PC management covers full inspection, grouped history/audit, compensating corrections, ownership/control, freeze/lifecycle, duplication and PDF export. It is not a Desktop Player character-builder.

### Campaign Manager

Campaign identity/lifecycle, members, DM/Player roles, reusable invites, kick/ban, PC assignment shortcuts, campaign-level sync/status and navigation are included. Campaign role, membership, PC ownership and PC control remain separate concepts.

### System Administration

The owner intends to be the sole global administrator. The Desktop App may therefore be a practical operator console. It may use backend-mediated operations and, where concretely useful, direct provider API integration with locally protected/scoped credentials. Secrets must not be hard-coded, committed or casually stored in plaintext.

### Media & Handouts

A lightweight object-storage-backed manager handles images, maps, documents and handouts, multiple references, replacement without breaking logical links, DM-only vs Player-safe designation and explicit reveal. Direct upload from consuming Managers is allowed.

## 7E — exact integrated-MVP boundary — CLOSED

The real integrated MVP includes Player + Server/shared + DM Android/tablet + DM Desktop + sync/auth/permissions + persistent DM authoring + combat exchange + official-SRD clarification.

Important in-scope items must not later be silently trimmed as “stretch”: Desktop live parity, handoff/resume, the Managers above, structured homebrew, import/export, object storage/media, PC audit, Campaign/System Admin, backup/export and official-SRD clarification.

Explicitly outside this cycle unless a concrete implementation need proves otherwise: full VTT, comprehensive automated legality/character builder, simultaneous authoritative co-DM combat, generic realtime/WebSockets requirement, Durable Objects by default, queues without need, generic ACL/sync platforms, automatic encounter-balance authority, executable homebrew engine, homebrew-aware AI, public marketplace/community system, every third-party import format, polished one-click catastrophic restore, exhaustive event sourcing, enterprise observability and generic RPG framework.

## 8A — implementation strategy — CLOSED

Implementation should use dependency-driven integration waves rather than completing isolated applications and integrating at the end. Parallel work is allowed after shared semantics exist, but workstreams must not independently redefine shared contracts.

The final major owner-facing QA is integrated Player + Server + DM.

## 8B — Git/repository strategy — CLOSED

Current authority remains split:

- `main`: product/architecture/documentation truth;
- `implementation/phase4a-successor-cycle`: Player runtime/QA truth.

Before normal integrated implementation, use a dedicated convergence branch from `main` to deliberately reconcile the Player successor runtime. Player runtime/evidence wins for Player implementation; `main` decisions/docs win for later global architecture/product truth. Validate, then merge to `main`, which becomes the normal integrated trunk.

After convergence, prefer short-lived outcome-oriented branches and frequent integration; do not create permanent Player/Server/Desktop silos or a months-long integration branch.

## 8C — first implementation-package direction — CLOSED / delegated technical scope

The first technical package is the minimum shared integrated-MVP spine covering already-approved semantics such as global identity, campaign membership/role, PC owner/controller distinction, stable IDs, revisions, tombstones, Personal/Campaign/System scope where applicable and provenance for independent copies.

Exact schema/classes/API/tests are technical implementation responsibilities and do not require owner rubber-stamping unless they alter product behavior/risk/cost/scope.

## Owner-vs-technical collaboration rule

Ask the owner to decide:

- product behavior and workflows;
- visibility/privacy expectations;
- MVP vs later scope;
- user-facing destructive/safety behavior;
- meaningful convenience/cost/risk tradeoffs.

Do not ask the owner to approve routine low-level matters such as table layout, endpoint shape, class decomposition, canonical import serialization format, migration mechanics or test architecture unless a real product/risk consequence exists.

## Exact resume point

The conversation intentionally pauses here before detailing the next hosted-foundation technical package.

When resuming:

1. verify `main` and Player successor state have not drifted;
2. keep D-0072/D-0073 as controlling post-D-0071 decisions;
3. do not reopen already-closed 7D/7E product design unless the owner deliberately changes scope;
4. do not make the owner rubber-stamp technical implementation packages;
5. before product coding, deliberately execute the planned `main` + Player successor convergence under explicit coding authorization;
6. technical assistant/Worker owns implementation detail within approved architecture, escalating only material product/scope/security/cost decisions.

No application code was changed in this checkpoint.

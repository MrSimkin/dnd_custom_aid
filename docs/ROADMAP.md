# Roadmap

This roadmap defines development stages and current integration direction. Product content remains controlled by approved decisions and owner authorization.

## Phase 0 — Project Foundation

**Status:** Complete.

---

## Phase 1 — Product Discovery and Design

**Status:** Current integrated-MVP product-design pass complete through D-0073.

Future targeted product design occurs only when implementation exposes a real unresolved user-facing decision or the owner deliberately changes scope.

---

## Phase 2 — Technical Foundation

**Status:** Foundational architecture selected; technical-readiness review complete; hosted implementation not yet activated.

Approved base remains Kotlin/Compose Android, Kotlin + Compose Multiplatform Desktop, genuinely shared Kotlin logic/data where useful, SQLDelight/SQLite local persistence, TypeScript Cloudflare Worker/API, Neon PostgreSQL and Descope authentication.

The 2026-09-14 technical-readiness review additionally establishes the preferred delegated implementation direction:

- Ktor Client for shared native HTTP networking;
- small versioned HTTP/JSON API;
- optimistic revisions + idempotent client mutation IDs;
- SQLDelight local outbox + scoped project-specific sync;
- Neon serverless driver initially from the Worker;
- explicit hosted SQL migrations;
- versioned JSONB PC snapshot plus relational authorization/index metadata;
- Descope client authentication plus backend token validation;
- R2 Standard as preferred first object storage, pending owner/service activation;
- versioned JSON import/export family;
- on-demand versioned backup archive with manifest/integrity information.

These are technical implementation choices under D-0073 unless later evidence creates a material owner-level tradeoff.

C-0009 remains controlling: add complexity only for concrete requirements.

---

## Phase 3 — First Vertical Slice

**Status:** Complete.

---

## Phase 4A — Player Character Foundation

**Status:** substantial implementation complete / automation-green candidate exists / targeted physical revalidation pending / integration convergence not yet executed.

Authoritative Player branch before convergence:

`implementation/phase4a-successor-cycle`

Observed branch HEAD during technical readiness:

`b9dea8ad6b17dcf3feeabba263eff1ee498f1536`

Frozen physical candidate:

- `0.4.0-preqa.13 / 41300`;
- candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation pending.

The Actions run was independently rechecked during technical readiness and is successful against the exact candidate SHA.

Existing Player evidence remains valid for the exact tested boundaries. Do not restart historical repair work absent new evidence.

---

## Phase 4B — Integrated MVP Build

**Status:** product scope + implementation governance + technical readiness complete; **waiting for explicit owner implementation authorization**.

Controlling records:

- D-0071 — integrated Player + Server + DM architecture;
- D-0072 — DM Desktop product and authoring/management surfaces;
- D-0073 — exact MVP boundary, implementation governance and Git convergence;
- `docs/checkpoints/2026-09-14_INTEGRATED_MVP_TECHNICAL_READINESS_REVIEW.md` — technical implementation recommendations/readiness.

Target:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

The build is one integrated product cycle. Internal waves/packages are engineering controls, not separate owner-acceptance products.

### Workstream A — shared MVP spine/contracts

Establish common semantic meaning for identity, campaigns/membership/roles, PC ownership/control, stable IDs, revisions, tombstones, scope/provenance and other shared invariants without pre-modeling the entire application.

### Workstream B — Player stabilization and hosted integration

Preserve the mature Player foundation and integrate remembered authentication, campaigns, hosted PC sync, assets, audit/history/conflicts and public combat projection without gratuitous Player rewrites.

### Workstream C — hosted foundation

Implement PostgreSQL migrations/schema, Cloudflare API, Descope identity mapping/verification, domain authorization, sync endpoints, object storage, audit/recovery and full backup/export. Begin SRD storage/provenance foundations early enough to avoid later architectural rework.

### Workstream D — shared Kotlin data/sync

Implement genuinely reusable local/shared data and sync behavior across Android/Desktop where appropriate, including revisions, pending mutations, tombstones, conflicts and asset references.

### Workstream E — DM Desktop / DM Android product

Live Workspace/Desks:

1. DM Screen;
2. Stage Desk;
3. Dungeon Desk;
4. Combat Desk.

Desktop uses the same game/domain semantics as Android/tablet and can replace the tablet operationally.

Prepare/Manage includes:

- Monster Manager + Creature Creator Assistant + import/export;
- NPC Manager + Quick/Developed NPC helper + optional mechanics + import/export;
- Homebrew & Rules Manager including structured races/sub-races, classes/subclasses, backgrounds, feats, spells, items/magic items and other justified custom content;
- Stage/Place/Scene Spine preparation;
- Dungeon/Zone preparation, Encounter Readiness, clocks/advisory triggers;
- Encounter Manager;
- PC Manager/Audit;
- Media & Handouts.

Administration includes Campaign Manager and separate System Administration.

### Workstream F — live-play exchange and combat authority

Implement local-first authoritative DM combat, hosted opportunistic sync, Player public projection, stale-update rejection, reconnect behavior and explicit resume/handoff to another DM device from the latest synchronized state.

Exactly one DM device remains authoritative at a time. Simultaneous authoritative multi-device/co-DM editing is not required.

### Workstream G — SRD retrieval + AI clarification

SRD schema/provenance/loading foundation begins earlier in hosted work. The actual PostgreSQL retrieval -> grounded LLM -> Player/DM answer feature remains a late substantial user-facing feature of the cycle.

MVP AI remains official SRD 5.1 / SRD 5.2.1 only even though homebrew content is authored/stored in the application.

---

## Implementation waves

Broad dependency direction:

1. continuity protection and repository-state verification — **COMPLETE for planning/readiness**;
2. deliberate `main` + Player-successor convergence — **NEXT, after owner authorization**;
3. shared MVP semantic spine;
4. hosted foundation;
5. Player <-> Server end-to-end integration;
6. Desktop shell + campaign administration;
7. reusable/persistent DM content architecture;
8. Desktop authoring Managers;
9. DM Live Workspace on shared semantics;
10. combat/public-projection/device-authority integration;
11. SRD retrieval + grounded clarification;
12. backup/recovery/operator-console completion;
13. integrated owner-facing QA candidate.

Exact low-level package boundaries remain delegated engineering decisions. Parallelism is encouraged after shared semantics exist, but parallel streams must not independently redefine shared contracts.

---

## Git convergence gate — next implementation activity

Current authority remains split:

- `main` = global product/architecture/governance truth;
- `implementation/phase4a-successor-cycle` = current Player runtime/QA truth.

The technical-readiness review observed the refs as diverged (successor substantially ahead in Player runtime commits; `main` ahead in later integrated documentation) and confirmed there is no competing mature backend/Desktop implementation to reconcile.

After explicit owner implementation authorization:

1. refresh both refs;
2. create a dedicated convergence branch from current `main`;
3. deliberately reconcile the Player successor runtime;
4. preserve successor Player runtime, SQLDelight migrations, tests/guards and evidence;
5. preserve current integrated product/architecture/governance from `main`;
6. reconcile CI/navigation intentionally;
7. run all Player guards + aggregate shared/Android/Desktop build/tests + backend check;
8. inspect for semantic loss;
9. merge to `main` only when coherent/buildable.

After successful convergence, `main` becomes the integrated trunk. Prefer short-lived outcome-oriented branches and frequent reintegration; avoid permanent Player/Server/Desktop silos and months-long catch-all integration branches.

---

## Integrated MVP QA gate

The next major owner-facing product acceptance QA is intended to exercise **Player + Server + DM together**.

Representative end-to-end coverage should include:

- remembered login, campaign membership/switching and role behavior;
- Player local Save and PC/assets/history synchronization;
- authorized DM PC retrieval/audit/correction without ownership confusion;
- Personal -> Campaign content-copy workflows;
- Monster/NPC/Homebrew/Stage/Dungeon/Encounter preparation;
- saved encounter -> independent live encounter;
- local-first combat and public Player projection;
- offline/reconnect behavior;
- stale revision/conflict/tombstone behavior;
- explicit DM-device combat resume from the latest synchronized state;
- Media/Handout use where applicable;
- full server backup/export;
- official-SRD grounded rules clarification for Player and DM.

Internal automated/integration checks run throughout implementation.

---

## Integrated MVP boundary — closed

Approved in-scope items must not later be silently demoted to stretch goals merely to make implementation appear complete sooner. This includes Desktop live parity, combat authority resume/handoff, the approved authoring Managers, structured homebrew, import/export, object storage/media, PC audit, Campaign/System Administration, backup/export and official-SRD clarification.

Explicitly deferred/generalized unless concrete evidence requires otherwise:

- full VTT/grid/token/LOS/fog-of-war behavior;
- comprehensive automatic character building/legality enforcement;
- simultaneous authoritative co-DM combat;
- always-live formal game-session server;
- generalized realtime/WebSockets as a product requirement;
- Durable Objects/queues by default;
- generic arbitrary ACL/RBAC framework;
- generalized sync platform;
- automatic encounter-balance authority/combat simulation;
- executable homebrew/rules engine;
- homebrew-aware AI clarification;
- arbitrary non-SRD rules corpus;
- public/community marketplace/social network;
- mandatory support for every third-party import ecosystem;
- polished one-click catastrophic whole-server restore;
- every-keystroke/universal event sourcing;
- enterprise observability;
- vector infrastructure without demonstrated need;
- autonomous campaign-management agents;
- generic RPG-system framework.

Deferred technologies remain permissible if they prove the simplest safe/proportional implementation of an already-approved requirement.

---

## Owner/technical collaboration rule

The owner decides product behavior/workflow, visibility/privacy, MVP vs later scope, user-facing destructive/safety behavior and meaningful convenience/cost/security/lock-in tradeoffs.

The technical assistant/Worker normally decides exact schema/table design, class/type decomposition, endpoint shapes, internal sync structures, migration mechanics, canonical import serialization, testing architecture and detailed technical package granularity.

Do not ask the owner to rubber-stamp routine technical matters.

---

## Current owner intervention

Technical readiness is complete. No unresolved low-level technical question currently requires owner selection.

The next meaningful owner decision is:

> **Authorize beginning the integrated-MVP implementation, starting with protected branch convergence.**

After that authorization, routine technical work proceeds under D-0073. Return to the owner only for material product/scope/security/cost choices, external account/service setup (for example enabling R2 or configuring provider secrets) and manual/physical QA gates.

No product coding has been performed by the technical-readiness documentation pass.

---

## Post-MVP evolution

Future evolution remains evidence/priority driven. Candidates may include co-DMs, richer restore tooling, homebrew-aware clarification, realtime transport if polling proves inadequate, external import adapters, broader sharing/delegation, Journey-specific behavior if Dungeon mode proves insufficient and other owner-approved expansions.

## Branch/continuity rule

`docs/BRANCH_STATUS.md` controls branch authority/lifecycle. `docs/checkpoints/LATEST.md` controls the practical global resume point.
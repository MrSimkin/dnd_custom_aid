# Roadmap

This roadmap defines development stages and current integration direction. Product content remains controlled by approved decisions and owner authorization.

## Phase 0 — Project Foundation

**Status:** Complete.

---

## Phase 1 — Product Discovery and Design

**Status:** Current integrated-MVP product-design pass complete through D-0073. Future targeted product design occurs only when implementation exposes a real unresolved user-facing decision or the owner deliberately changes scope.

---

## Phase 2 — Technical Foundation

**Status:** Foundational architecture selected; hosted implementation not yet fully activated.

Approved base remains Kotlin/Compose Android, Kotlin + Compose Multiplatform Desktop, genuinely shared Kotlin logic/data where useful, SQLDelight/SQLite local persistence, TypeScript Cloudflare Worker/API, Neon PostgreSQL and Descope authentication.

C-0009 remains controlling: add complexity only for concrete requirements.

Low-level technical choices are delegated to the technical assistant/Worker unless they materially alter product behavior, security/privacy, cost, lock-in or approved scope.

---

## Phase 3 — First Vertical Slice

**Status:** Complete.

---

## Phase 4A — Player Character Foundation

**Status:** substantial implementation complete / automation-green candidate exists / physical revalidation and final integration reconciliation remain.

Authoritative Player branch:

`implementation/phase4a-successor-cycle`

Current frozen physical candidate known at the 2026-09-14 consolidation:

- `0.4.0-preqa.13 / 41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation pending.

Existing Player evidence remains valid for the exact tested boundaries. Before implementation/convergence, refresh the successor branch's current checkpoint and reconcile older planning labels against branch evidence.

---

## Phase 4B — Integrated MVP Build

**Status:** product scope and implementation governance defined; coding has not begun from this checkpoint.

Controlling records:

- D-0071 — integrated Player + Server + DM architecture;
- D-0072 — DM Desktop product and authoring/management surfaces;
- D-0073 — exact MVP boundary, implementation governance and Git convergence.

Target:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

The build is one integrated product cycle. Internal waves/packages are engineering controls, not separate owner-acceptance products.

### Workstream A — shared MVP spine/contracts

Establish common semantic meaning for identity, campaigns/membership/roles, PC ownership/control, stable IDs, revisions, tombstones, scope/provenance and other shared invariants without pre-modeling the entire application.

### Workstream B — Player stabilization and hosted integration

Preserve the mature Player foundation, close/reconcile remaining bounded defects and integrate remembered authentication, campaigns, hosted PC sync, assets, audit/history/conflicts and public combat projection without gratuitous Player rewrites.

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

The implementation should be dependency-driven and integrated frequently. Exact low-level package boundaries remain delegated engineering decisions.

Broad direction:

1. documentation/continuity protection and repository-state verification;
2. deliberate `main` + Player-successor convergence;
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

Parallelism is encouraged after shared semantics exist. Parallel streams may share contracts; they must not independently redefine them.

---

## Git convergence gate before normal integrated coding

Current authority remains split:

- `main` = global product/architecture/documentation truth;
- `implementation/phase4a-successor-cycle` = current Player runtime/QA truth.

Before ordinary integrated-MVP coding, create a dedicated convergence branch from current `main`, deliberately reconcile the authoritative Player successor runtime, validate the result and merge the coherent baseline into `main`.

Semantic precedence during reconciliation:

- Player implementation/evidence -> successor branch;
- later global product/architecture/documentation -> current `main`.

After successful convergence, `main` becomes the integrated trunk. Prefer short-lived outcome-oriented branches and frequent reintegration; avoid permanent Player/Server/Desktop silos and avoid a months-long catch-all integration branch.

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

Internal automated/integration checks run throughout implementation. This final integrated gate does not mean deferring testing until the end.

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

The owner decides:

- what the product should do;
- workflows and user-facing behavior;
- visibility/privacy;
- MVP vs later scope;
- destructive/safety behavior;
- meaningful convenience/cost/risk tradeoffs.

The technical assistant/Worker normally decides:

- exact schema/table design;
- class/type decomposition;
- endpoint shapes;
- internal sync data structures;
- migration mechanics;
- canonical import serialization format;
- testing architecture;
- detailed implementation package granularity.

Do not ask the owner to rubber-stamp routine technical matters. Escalate technical choices only when they materially change behavior, security/privacy, cost, irreversible lock-in or approved scope.

---

## Current pause / resume point

The owner intentionally requested a security/continuity consolidation after closing:

- 7D — Desktop product definition;
- 7E — exact MVP boundary;
- 8A — implementation-wave strategy;
- 8B — Git convergence strategy;
- 8C — first shared-spine package direction.

Discussion pauses before expanding the next hosted-foundation technical package.

When resuming, refresh `main` and the Player successor branch, preserve D-0071/D-0072/D-0073 as controlling, and do not turn low-level technical package design into an owner approval exercise.

No product coding was authorized or performed by this documentation checkpoint.

---

## Post-MVP evolution

Future evolution remains evidence/priority driven. Candidates may include co-DMs, richer restore tooling, homebrew-aware clarification, realtime transport if polling proves inadequate, external import adapters, broader sharing/delegation, Journey-specific behavior if Dungeon mode proves insufficient and other owner-approved expansions.

## Branch/continuity rule

`docs/BRANCH_STATUS.md` controls branch authority/lifecycle. `docs/checkpoints/LATEST.md` controls the practical global resume point.

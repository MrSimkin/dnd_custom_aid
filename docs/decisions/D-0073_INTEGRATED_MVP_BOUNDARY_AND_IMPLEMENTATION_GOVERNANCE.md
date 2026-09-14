# D-0073 — Integrated MVP boundary, implementation governance and branch convergence direction

**Status:** Approved product/scope and implementation-governance direction  
**Date:** 2026-09-14  
**Decision owner:** Project owner for product/scope; technical design delegated to the technical assistant/implementation workers within these approved boundaries  
**Implementation authorization:** this record defines how implementation should be organized, but does not itself authorize starting code changes

## Purpose

This decision closes the post-D-0071 scope-control discussion. It defines what belongs in the integrated MVP, what remains intentionally outside it, how implementation should be organized, how the divergent `main` and Player successor lines should converge, and the practical boundary between owner-level product decisions and delegated technical engineering decisions.

The owner explicitly clarified that low-level technical matters are outside their intended competence and should not be repeatedly presented for rubber-stamp approval. Product behavior, scope, workflows and user-facing tradeoffs remain owner decisions. Technical implementation details should be recommended and carried by the technical assistant/Worker unless they materially alter product behavior, risk, cost, privacy/security or agreed scope.

---

## 1. Integrated MVP scope is intentionally substantial

The next cycle targets the real integrated product:

```text
Player Android
+ hosted/shared services
+ DM Android/tablet
+ DM Desktop
+ shared data/sync/auth/permissions
+ persistent DM authoring/content
+ live combat exchange
+ official SRD clarification
```

The purpose of the MVP boundary is not to shrink this into a server demo. It prevents scope drift into unrelated/generalized products.

### 1.1 Required MVP capabilities protected from later trimming

The following are part of the approved integrated MVP and are not stretch goals merely because they are broad:

- stabilize/finish the existing Player App and integrate it with hosted/shared state;
- authentication, remembered login and multicampaign roles;
- hosted API/database and project-specific synchronization;
- object storage;
- PC ownership/control, audit/history/recovery and sync/freshness behavior;
- complete DM live capability on Android/tablet and Desktop;
- explicit combat authority resume/handoff between DM devices;
- Monster Manager + Creature Creator Assistant + import/export;
- NPC Manager + Quick/Developed NPC helper + optional mechanics + import/export;
- Homebrew & Rules Manager including structured races/sub-races, classes/subclasses, backgrounds, feats, spells, items/magic items and other justified custom content;
- Stage/Place/Scene Spine preparation;
- Dungeon/Zone preparation, Encounter Readiness, clocks and advisory triggers;
- Encounter Manager and saved/live encounter lifecycle;
- Media & Handouts/object-storage workflows;
- PC Manager/Audit;
- Campaign Manager;
- System Administration sole-admin/operator console;
- meaningful audit/recovery substrate;
- full verifiable server backup/export;
- official SRD storage/retrieval/provenance and grounded clarification for Player and DM.

---

## 2. Explicit outside-MVP/generalization boundary

The following are intentionally outside this cycle unless implementation proves one is the simplest safe way to satisfy an approved requirement:

- guided/automatic D&D character builder and comprehensive legality enforcement;
- full VTT functionality such as grid/token movement, LOS, fog of war or tactical distance engine;
- simultaneous authoritative multi-device/co-DM combat editing;
- generalized always-live game-session server;
- WebSockets/generalized realtime as a product requirement;
- Durable Objects/equivalent coordination merely for architectural fashion;
- background queues without a concrete asynchronous job requiring them;
- generic arbitrary ACL/RBAC framework;
- generalized synchronization platform;
- automatic encounter-balance authority or combat simulation;
- automatic executable homebrew/rules engine;
- homebrew-aware AI clarification in this MVP;
- arbitrary non-SRD books/corpora for the official rules assistant;
- public/community marketplace/repository/social network;
- support for every third-party import ecosystem as an MVP obligation;
- polished one-click catastrophic whole-server restore UI;
- every-keystroke/universal forensic event sourcing;
- enterprise observability/monitoring platform;
- vector/semantic-search infrastructure unless a real need appears;
- autonomous multi-agent campaign management;
- generic RPG-system framework.

Architecture should leave reasonable room for future co-DM, richer restore, external adapters, homebrew-aware AI, realtime transport or Journey-specific behavior without building them now.

### 2.1 Technology escape hatch

Deferred technologies are not forbidden. If implementation evidence shows that a deferred technology is the simplest safe/proportional way to implement an already-approved requirement, it may be selected through normal technical design review.

The controlling principle remains:

> Do not add infrastructure because it might be useful someday; add it when a concrete approved requirement demonstrates the need.

---

## 3. Implementation strategy — dependency-driven integration waves

Implementation should proceed through vertical dependency waves rather than app-by-app silos.

The approved direction is broadly:

1. documentation/contract safety checkpoint;
2. shared MVP semantic/domain spine;
3. hosted foundation;
4. Player <-> Server integration;
5. Desktop shell and campaign administration;
6. reusable/persistent content architecture;
7. Desktop authoring Managers;
8. DM Live Workspace on shared semantics;
9. live combat/public projection/device-authority exchange;
10. official SRD retrieval + grounded clarification;
11. backup/recovery/operator-console completion;
12. integrated Player + Server + DM owner-facing QA.

Exact package boundaries, class/table/API design, storage schema and low-level sequencing are delegated engineering decisions as long as they preserve the approved contracts.

Parallel work is encouraged after shared semantics exist. Parallel streams may share contracts; they must not independently redefine them.

---

## 4. Integrated acceptance philosophy

The next major owner-facing acceptance QA is end-to-end rather than isolated by app.

Representative integrated scenarios should eventually cover:

```text
Create campaign
-> invite Player
-> create/use/sync PC
-> DM retrieves/inspects appropriately
-> author monster/NPC/location/zone/encounter
-> start live encounter on tablet
-> Player receives public combat projection
-> tablet unavailable
-> Desktop explicitly resumes authority
-> old authority cannot overwrite new authority
-> finish/archive/discard as appropriate
-> audit/correct PC
-> export verifiable server backup
-> ask official-SRD clarification from Player/DM surface
```

Internal automated/integration checks run continuously; this integrated acceptance gate does not mean deferring engineering testing until the end.

---

## 5. Git/repository convergence direction

Current authority is intentionally split:

- `main` = global product/architecture/documentation truth;
- `implementation/phase4a-successor-cycle` = authoritative current Player runtime/QA line.

Before normal integrated-MVP coding proceeds, create a dedicated convergence branch from current `main` and deliberately reconcile the authoritative Player successor runtime into it.

Conceptual precedence during reconciliation:

- Player runtime implementation/evidence: successor branch is authoritative;
- integrated product/architecture/documentation decisions: current `main` is authoritative.

The convergence must be validated before merging to `main`. After successful convergence, `main` becomes the single ongoing integrated-MVP trunk. The old Player branch may remain frozen for traceability but should no longer be the normal development home.

### 5.1 Ongoing branch strategy

After convergence:

- use short-lived outcome-oriented feature/integration branches;
- merge shared contracts/foundations early when later work depends on them;
- avoid permanent `player-main`, `desktop-main`, `server-main` silos;
- avoid a months-long `integration/full-mvp` branch that postpones integration until the end;
- keep `main` coherent/buildable at normal merge points;
- update durable docs/checkpoints proportionally when operational truth changes.

Branches are temporary implementation vehicles. Durable decision records/documentation remain semantic authority.

Meaningful tags may be used at integrated milestones such as baseline convergence, Player-Server end-to-end, live-combat end-to-end and final QA candidate.

---

## 6. First technical implementation package — delegated engineering scope

The first technical package after documentation/convergence is the minimum shared integrated-MVP spine.

Its engineering intent is to establish consistent semantics for concepts already approved at product level, including:

- global identity/account;
- campaign and membership/role;
- PC owner vs current controller;
- stable object identity;
- revisions/conflict prevention;
- deletion/tombstone semantics;
- Personal/Campaign/System-or-Official scope vocabulary where applicable;
- provenance for independent copies;
- basic audit/sync metadata/invariants.

The technical implementation may choose appropriate Kotlin/domain types, database representation, API structures and tests. It should not pre-model the entire future product or force every domain entity into one giant universal abstraction.

Key approved invariants include:

- account role may differ by campaign;
- DM authority does not imply PC ownership;
- owner and controller may differ;
- Personal -> Campaign copy produces a new independent object identity with provenance;
- later personal-master edits do not silently update campaign copies;
- stale revisions must not silently overwrite newer state;
- deletion/tombstones must prevent stale offline resurrection where applicable.

Later domain schemas (Monster/NPC/Zone/Encounter/combat/SRD), complete sync UI/engine and user-facing screens are outside this first technical package unless strictly required to prove the foundation.

---

## 7. Owner vs technical decision boundary

The owner should be asked to decide matters such as:

- what the application should do;
- what workflows feel right;
- what information should be visible/private;
- what belongs in MVP vs later;
- user-facing safety/destructive behavior;
- meaningful tradeoffs that change convenience, cost, risk or scope.

The technical assistant/Worker should normally decide and document matters such as:

- exact table/schema layout;
- class/type decomposition;
- API endpoint shapes;
- migration mechanics;
- internal synchronization data structures;
- canonical import serialization format;
- testing architecture;
- branch/package granularity;
- provider-specific technical implementation within approved product/cost/security constraints.

Do **not** repeatedly ask the owner to approve low-level technical details merely because they exist. Escalate only when a technical choice materially changes product behavior, introduces meaningful cost/security/privacy risk, creates irreversible lock-in, or conflicts with approved architecture.

---

## 8. Security/credential principle retained

System Administration is designed for the owner as sole administrator.

Convenient Desktop provider integration is allowed when useful. Prefer project/backend APIs and scoped credentials, but local administrative provider credentials may be used when genuinely needed and stored using suitable OS/local secure-credential facilities.

Secrets must not be hard-coded, committed to the repository or casually persisted in plaintext.

---

## 9. Current pause/resume point

This record intentionally stops before expanding the next hosted-foundation technical package in detail.

The owner requested a security/continuity consolidation before further implementation planning.

After this consolidation, resume from:

1. verify current repository/Player branch state has not drifted;
2. perform the planned `main` + authoritative Player successor convergence design/execution when coding authorization is explicitly granted;
3. let the technical assistant/Worker carry low-level package design without owner rubber-stamping;
4. escalate only product/scope/risk decisions back to the owner.

No product code is changed by this decision record itself.

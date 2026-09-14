# Project State — global repository navigation

**Last verified:** 2026-09-14  
**Canonical navigation/discovery branch:** `main`  
**Authoritative current Player implementation branch:** `implementation/phase4a-successor-cycle` until planned convergence  
**Current Player frozen physical candidate:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`  
**Current Player evidence:** automation green; targeted cross-device physical revalidation pending on the successor branch  
**Current global design state:** integrated MVP architecture + DM Desktop product definition + exact MVP boundary consolidated under D-0071/D-0072/D-0073  
**Implementation authorization:** not granted by this documentation consolidation; no product code changed

## 1. Two authoritative lines still exist

`main` remains the canonical place for global repository navigation and DM/product/architecture decisions. It intentionally does not yet contain the latest Player runtime implementation.

Current Player code/QA authority remains:

`implementation/phase4a-successor-cycle`

That branch's current known `docs/checkpoints/LATEST.md` identifies `0.4.0-preqa.13 / 41300` at `92aa9b6...` as the frozen automation-green physical candidate. Refresh the branch before implementation rather than assuming this checkpoint can never drift.

The two lines must not be mechanically force-moved over one another. D-0073 now defines the future convergence: dedicated convergence branch from `main`, deliberate reconciliation of the authoritative Player successor runtime, validation, then merge into `main` so `main` becomes the ongoing integrated-MVP trunk.

## 2. Current Player state

Authoritative source:

`implementation/phase4a-successor-cycle`

Known frozen candidate at this checkpoint:

- version: `0.4.0-preqa.13`;
- build/versionCode: `41300`;
- commit: `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630: **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted physical revalidation: **pending**;
- Phase 4A on that branch: formally **OPEN**.

Older chat defect labels A10/B1/J1/J2 remain planning input only. Before convergence/implementation, reconcile them against the then-current successor-branch evidence rather than overriding the branch from memory.

Existing QA evidence remains valuable for the boundaries actually exercised. Do not restart Player design or replay all historical QA merely because the next product cycle is broader.

## 3. Controlling global decisions

### D-0071 — integrated Player + Server + DM architecture

`docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`

Controls the one-product ecosystem, paper/local/server authority, project-specific synchronization, revisions/conflicts/tombstones, hosted foundation, object storage requirement, audit/recovery, backup/export, full DM Desktop operational capability, combat authority fallback and official-SRD clarification direction.

### D-0072 — DM Desktop product and authoring Managers

`docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md`

Closes the detailed 7D product pass.

Desktop is one workbench with:

1. **Live / Workspace** — DM Screen, Stage Desk, Dungeon Desk, Combat Desk;
2. **Prepare / Manage** — PCs, NPCs, Monsters, Homebrew & Rules, Stages/Places, Dungeons/Zones, Encounters, Media/Handouts;
3. **Administration** — Campaign Administration plus distinct System Administration.

Desktop Live uses the same DM game/domain semantics as Android/tablet. The difference is desktop-appropriate UX/presentation.

### D-0073 — exact MVP boundary and implementation governance

`docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md`

Closes 7E and the current implementation-planning pass. It protects the substantial integrated-MVP scope from accidental later trimming, records deferred/generalized directions, establishes dependency-driven implementation waves, defines the planned Git convergence and records the owner-vs-technical responsibility boundary.

## 4. Approved Desktop Prepare/Manage direction

### Monster Manager

Includes full human-usable editing, Personal/Campaign/Official scope, explicit independent copies with provenance, dirty-improvisation cleanup, simple Packages, import/export and a Creature Creator Assistant. Balance guidance is advisory rather than mathematical authority.

### NPC Manager

Supports Quick NPC -> Developed NPC -> optional mechanics/full stat block. NPCs do not require combat mechanics. A helper may guide ideation/identify useful missing questions without completeness scoring. Import/export applies.

### Homebrew & Rules Manager

Includes rules/variants/rulings/custom subsystems plus structured custom races/sub-races, classes/subclasses, backgrounds, feats, spells, items/magic items and other justified game-content families. Campaign custom content integrates with relevant Player/DM surfaces without becoming an automatic legality/rules engine. Homebrew-aware AI remains post-MVP.

### Stage / Dungeon preparation

Stage Manager handles Places/Shops/NPC links, multiple retrieval paths and Scene Spine. Dungeon Manager handles topology, rich Zone Briefs, Encounter Readiness, clocks and advisory triggers. Partial/paper-backed preparation remains valid; no VTT behavior is introduced.

### Encounter Manager

Saved Encounter remains distinct from Live Encounter. Expected/Reserve/Conditional participants, environment/guidance, encounter-specific overrides, archive/save-as-new-template, import/export and Personal reusable encounters are in scope. Personal Encounter dependencies may be copied into a campaign together.

### PC Manager / Audit

DM Desktop supports complete PC inspection, meaningful grouped audit/history, compensating corrections, owner/controller administration, freeze/lifecycle, duplication and approved PDF export concepts. It is not a Desktop Player character-builder.

### Media & Handouts

Object-storage-backed images/maps/documents/handouts can be linked to multiple entities, replaced without breaking logical references, marked DM-only vs Player-safe and explicitly revealed where appropriate. Uploading directly from consuming Managers is allowed.

## 5. Campaign/System Administration

Campaign Manager covers campaign identity/lifecycle, members, simple campaign roles, reusable invitations, kick/ban, PC assignment shortcuts and campaign-wide status/navigation. Campaign membership, role, PC ownership and PC control remain separate concepts.

System Administration is designed for the owner as sole global administrator. The Desktop App may serve as a practical operator console. Prefer project/backend APIs where sensible, but direct provider API operations and locally protected scoped administrative credentials are permitted when they materially improve convenience. Secrets must not be hard-coded, committed or casually stored in plaintext.

Full server backup/export remains MVP. Polished destructive whole-server restore may come later; normal mistakes should use object-level recovery/history instead.

## 6. Exact integrated-MVP boundary

The next cycle remains the full integrated MVP:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

In-scope items that must not be silently demoted to stretch goals include:

- Player hosted integration;
- complete DM live capability on Android/Desktop;
- combat authority resume/handoff;
- Monster/NPC/Homebrew/Stage/Dungeon/Encounter/PC/Media Managers;
- structured homebrew and import/export;
- object storage;
- Campaign/System Administration;
- audit/recovery/backup;
- official-SRD storage/retrieval/grounded clarification.

Deferred/generalized unless evidence requires them include full VTT functionality, comprehensive automatic legality/character-building, simultaneous authoritative co-DM combat, generic realtime/WebSockets requirement, Durable Objects/queues by default, generic ACL/sync platforms, automatic encounter-balance authority, executable homebrew engine, homebrew-aware AI, public marketplace/community features, every external import ecosystem, polished one-click catastrophic restore, exhaustive event sourcing, enterprise observability and generic RPG architecture.

Deferred technologies are not prohibited if a concrete approved requirement makes one the simplest safe/proportional implementation.

## 7. Implementation organization

### 8A — dependency-driven waves — CLOSED

Implementation should proceed through integrated dependency waves rather than isolated app silos. Parallel work is allowed after shared semantics exist, but workstreams must not independently redefine shared contracts.

The next major owner-facing QA remains Player + Server + DM end-to-end.

### 8B — Git convergence — CLOSED

Before normal integrated implementation, deliberately reconcile current `main` and `implementation/phase4a-successor-cycle` through a dedicated convergence branch. Player runtime/evidence is authoritative for Player implementation; current `main` decisions/docs are authoritative for later integrated product/architecture truth. Validate, merge to `main`, then use short-lived outcome-oriented branches and frequent reintegration.

### 8C — first technical package direction — CLOSED / delegated

The first technical package is the minimum shared semantic spine for already-approved concepts: identity, campaigns/membership/role, PC owner/controller, stable IDs, revisions, tombstones, Personal/Campaign/System scope where applicable and provenance for independent copies.

Exact schema/classes/API/migrations/tests are technical responsibilities and should not be presented to the owner for routine rubber-stamp approval.

## 8. Owner-vs-technical responsibility rule

Owner decisions:

- product behavior/workflow;
- visibility/privacy expectations;
- MVP vs later scope;
- user-facing destructive/safety behavior;
- meaningful convenience/cost/risk tradeoffs.

Delegated technical decisions unless materially consequential:

- exact database/table layout;
- class/type decomposition;
- endpoint/request shapes;
- migration mechanics;
- internal sync data structures;
- canonical import serialization format;
- testing architecture;
- detailed technical package granularity.

Escalate when a technical decision materially changes product behavior, security/privacy, cost, irreversible lock-in or approved scope.

## 9. Exact continuation

The owner intentionally requested a safety/continuity pause after closing 7D, 7E, 8A, 8B and the 8C direction.

When resuming:

1. verify `main` and Player successor have not drifted;
2. read D-0071/D-0072/D-0073 plus the latest checkpoint;
3. do not reopen already-closed product design unless scope deliberately changes;
4. do not make the owner approve low-level technical package design;
5. before product coding, execute the planned branch convergence only under explicit coding authorization;
6. then let the technical assistant/Worker carry technical implementation within the approved architecture and escalate only material product/scope/security/cost decisions.

## 10. Release/acceptance status

The project remains development/debug and is not release-ready.

The current Player candidate is not physically owner-accepted merely because automation is green. The integrated-MVP product scope and implementation strategy do not retroactively fabricate Player QA evidence or owner acceptance.

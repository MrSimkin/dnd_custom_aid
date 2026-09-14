# Latest project checkpoint — global resume map

**Updated:** 2026-09-14  
**Branch:** `main` after this consolidation is merged  
**Role:** canonical global navigation + current DM/MVP product/architecture decisions  
**Player code authority:** `implementation/phase4a-successor-cycle` until the planned convergence is explicitly executed  
**Current Player frozen physical candidate:** `0.4.0-preqa.13 / 41300` at `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`  
**Current global decisions:** D-0071 + D-0072 + D-0073  
**Implementation authorization:** documentation/design consolidation only at this checkpoint; no product code changed

## Read first

1. `docs/checkpoints/2026-09-14_DM_DESKTOP_MVP_SCOPE_AND_IMPLEMENTATION_GOVERNANCE_CONSOLIDATION.md` — exact current resume point;
2. `docs/decisions/D-0073_INTEGRATED_MVP_BOUNDARY_AND_IMPLEMENTATION_GOVERNANCE.md` — MVP boundary, implementation governance and Git convergence direction;
3. `docs/decisions/D-0072_DM_DESKTOP_PRODUCT_AND_AUTHORING_MANAGERS.md` — closed Desktop product/Manager definition;
4. `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md` — controlling integrated-MVP architecture;
5. `docs/PROJECT_STATE.md` — global state and execution rules;
6. `docs/BRANCH_STATUS.md` — branch roles/history;
7. D-0068 / D-0069 / D-0070 — live Workspace/Desk family and shared rules-question direction;
8. for exact Player code/QA, switch to `implementation/phase4a-successor-cycle` and read that branch's `docs/checkpoints/LATEST.md`.

## Current branch topology

Two authoritative lines still exist until the planned integration is deliberately executed:

- `main` = global product/design/architecture/documentation truth;
- `implementation/phase4a-successor-cycle` = current Player runtime/QA authority.

Do not mechanically overwrite one with the other.

D-0073 now defines the intended convergence: create a dedicated convergence branch from current `main`, reconcile the authoritative Player successor runtime into it, preserve successor authority for Player implementation/evidence and `main` authority for later global product/architecture decisions, validate, then merge the coherent baseline to `main`.

After successful convergence, `main` becomes the normal integrated-MVP trunk and the Player successor branch becomes frozen historical evidence rather than the ongoing Player development home.

## Current Player source/evidence

Authoritative branch remains:

`implementation/phase4a-successor-cycle`

Known frozen candidate at this checkpoint:

- `0.4.0-preqa.13 / 41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — **SUCCESS**;
- artifact `10331503478` / `dnd-custom-aid-debug-apk`;
- targeted cross-device physical revalidation — **pending**;
- Phase 4A — formally **OPEN** on that branch.

Do not infer physical PASS from green automation. Before implementation/convergence, refresh the successor branch's own checkpoint and reconcile any older chat defect labels against branch evidence.

## 7D — DM Desktop product definition — CLOSED

Desktop is one workbench with:

```text
LIVE / WORKSPACE
PREPARE / MANAGE
ADMINISTRATION
```

Live/Workspace provides the same DM game/domain semantics as Android/tablet for:

- DM Screen;
- Stage Desk;
- Dungeon Desk;
- Combat Desk.

Prepare/Manage now has approved product definitions for:

- Monster Manager + Creature Creator Assistant + import/export;
- NPC Manager with Quick/Developed NPC flow, helper and optional mechanics;
- Homebrew & Rules Manager including rules plus races/sub-races, classes/subclasses, backgrounds, feats, spells, items/magic items and other justified structured custom content;
- Stage/Place/Scene Spine preparation;
- Dungeon/Zone preparation, Encounter Readiness, clocks/advisory triggers;
- Encounter Manager;
- PC Manager/Audit;
- Media & Handouts.

Administration includes Campaign Manager plus permission-gated System Administration.

The owner intends to be the sole global system administrator. Desktop may therefore act as a practical operator console, using backend-mediated operations by preference and direct provider APIs/local securely stored scoped credentials where that concretely improves convenience.

## 7E — exact integrated-MVP boundary — CLOSED

The next cycle remains the substantial real integrated MVP:

```text
Player Android <-> hosted/shared services <-> DM Android/tablet/Desktop
```

Do not silently trim already-approved items such as Desktop live parity, combat handoff/resume, full authoring Managers, structured homebrew, import/export, object storage/media, PC audit, Campaign/System Admin, backup/export or official-SRD clarification into post-MVP stretch goals.

Explicitly deferred/generalized unless a concrete implementation need proves otherwise include full VTT behavior, comprehensive automatic legality/character building, simultaneous authoritative co-DM combat, generic realtime/WebSockets requirement, Durable Objects/queues by default, generic ACL/sync platforms, executable homebrew engine, homebrew-aware AI, public marketplace/community features, every third-party import format, polished one-click catastrophic restore, exhaustive event sourcing, enterprise observability and a generic RPG framework.

Deferred technologies remain permitted if implementation evidence shows one is the simplest safe/proportional way to satisfy an approved requirement.

## Implementation planning state

### 8A — implementation strategy — CLOSED

Use dependency-driven integration waves and frequent cross-client integration rather than completing isolated Player/Server/Desktop products and integrating at the end.

The next major owner-facing QA remains integrated Player + Server + DM.

### 8B — Git/repository strategy — CLOSED

First converge current `main` + authoritative Player successor through a dedicated validated convergence branch. After that, use `main` as the integrated trunk with short-lived outcome-oriented branches. Avoid permanent app silos and avoid a months-long integration branch.

### 8C — first implementation-package direction — CLOSED / delegated technical scope

The first technical package is the minimum shared integrated-MVP semantic spine: identity, campaign membership/role, PC owner/controller distinction, stable IDs, revisions, tombstones, scope vocabulary and independent-copy provenance.

Exact schema/classes/endpoints/tests are delegated engineering decisions, not owner product decisions.

## Owner-vs-technical decision rule

Ask the owner to decide product behavior, workflow, visibility/privacy, MVP/later scope, user-facing destructive/safety behavior and meaningful cost/risk/convenience tradeoffs.

Do **not** repeatedly ask the owner to rubber-stamp routine low-level implementation matters such as schema layout, endpoint shapes, class decomposition, serialization format, migration mechanics or test architecture.

Escalate technical choices only when they materially change product behavior, security/privacy, cost, irreversible lock-in or approved scope.

## Exact resume point

The owner intentionally requested a safety/continuity pause here.

When work resumes:

1. refresh `main` and the Player successor branch to ensure neither has drifted;
2. keep D-0071/D-0072/D-0073 as controlling post-Phase-4A product/architecture decisions;
3. do not reopen closed 7D/7E design without a deliberate owner scope change;
4. do not expand low-level technical package design as an owner approval exercise;
5. before product coding, execute the planned branch convergence only under explicit coding authorization;
6. after convergence, the technical assistant/Worker should carry implementation detail within the approved architecture and escalate only material product/scope/security/cost decisions.

No application implementation was performed by this checkpoint.

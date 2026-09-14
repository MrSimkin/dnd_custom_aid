# Repository Manifest

This file maps the authoritative project-control files and implemented areas so a fresh human or AI can orient without reconstructing history from branch names or old checkpoints.

## Current authority model

There are currently **two active authoritative lines**:

- `main` — canonical global navigation plus current DM/integrated-MVP product and architecture decisions;
- `implementation/phase4a-successor-cycle` — authoritative current Player runtime, repair and QA line.

These refs intentionally contain different valid work. `main` is not the latest Player runtime. The Player successor branch must not overwrite later `main`-only DM/MVP design truth.

`docs/BRANCH_STATUS.md` remains the branch-lifecycle map. Branch existence alone does not make a branch active.

## Root control files

### `README.md`
Project entry point and mandatory read sequence. Where older status prose conflicts with a later specific approved decision/checkpoint, use the authority rule below.

### `AGENTS.md`
Mandatory operating rules for humans and AI/coding agents. Branch/gate prose predating D-0071 must be read together with current `PROJECT_STATE`/`LATEST`; no coding is authorized by the D-0071 documentation checkpoint itself.

### `MANIFEST.md`
This inventory.

## Core `docs/` truth

### `docs/PROJECT_STATE.md`
Current global state, Player authority pointer and exact product/design continuation.

### `docs/checkpoints/LATEST.md`
Stable practical global resume pointer.

### `docs/checkpoints/2026-09-14_MVP_INTEGRATION_ARCHITECTURE_CONSOLIDATION.md`
Current architecture/product checkpoint. Read this before resuming the integrated MVP discussion.

### `docs/decisions/D-0071_MVP_INTEGRATION_PLAYER_SERVER_DM_DESKTOP_ARCHITECTURE.md`
Detailed controlling decision for the expanded integrated MVP: paper/local/server authority, synchronization, permissions, recovery/backup, object storage requirement, workstreams/waves, DM Desktop App expansion, full Desktop Desk capability and combat-authority fallback.

### `docs/decisions/D-0068_DM_LIVE_WORKSPACE_DESKS_AND_DUNGEON_DIRECTION.md`
Detailed DM Workspace/Desk, DM Attention Budget, Dungeon Desk, Zone Brief, Dungeon Turns, Encounter Readiness and dirty-improvisation direction.

### `docs/decisions/D-0069_DM_DESK_FAMILY_STAGE_DESK_AND_DM_SCREEN.md`
Approved current live Desk family: DM Screen, Stage Desk, Dungeon Desk and Combat Desk.

### `docs/decisions/D-0070_RULES_QUESTION_SHARED_PLAYER_DM_CAPABILITY.md`
Shared Player/DM natural-language rules-question capability; final product name still Pending.

### `docs/checkpoints/2026-09-12_REPOSITORY_CONTINUITY_RECONCILED.md`
Historical cross-branch reconciliation explaining why `main` and the Player successor intentionally diverged and how both valid lines must be preserved.

### Player successor checkpoint

For exact current Player source/evidence, switch to `implementation/phase4a-successor-cycle` and read its own `docs/checkpoints/LATEST.md`.

As of the 2026-09-14 consolidation, that record identifies:

- `0.4.0-preqa.13 / 41300`;
- commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- Scaffold `34801612526` / #1630 — SUCCESS;
- targeted cross-device physical revalidation pending.

### `docs/DECISIONS.md`
Historical chronological master decision log through its existing older sequence.

### `docs/DECISIONS_RECENT.md`
Navigation/index bridge for the later Approved D-0068 through D-0071 records. The detailed decision files remain authoritative; this index prevents a future reader from missing them because the older chronological master has not been rewritten wholesale.

### `docs/CONVENTIONS.md`
Approved recurring project conventions.

### `docs/PRODUCT.md`
Current approved product scope aligned with D-0071, including full DM Desktop operational fallback, Desktop creator/manager surfaces, Homebrew Rules authoring, object storage, backup/export and explicit combat-device resume.

### `docs/ROADMAP.md`
Current integrated-MVP roadmap and exact pre-coding 7D/7E design continuation.

### `docs/WORKFLOW.md`
Approved design/implementation/verification/checkpoint/review workflow. No implementation is authorized merely by D-0071; explicit coding authorization still follows the remaining design/topology work.

### `docs/ARCHITECTURE.md`
Current integrated Player/Server/DM architecture, including required object storage, backup/export and explicit combat-authority resume.

### `docs/TESTING.md`
Current Player evidence pointer plus planned continuous engineering verification and full integrated-MVP owner QA strategy.

### `docs/BRANCH_STATUS.md`
Canonical branch-lifecycle/history map. Until the future integration topology is explicitly approved, current Player runtime remains on the successor branch and global DM/MVP design belongs on `main`.

### `docs/TEST_DEVICES.md`
Owner-confirmed physical test devices.

## Current Player boundary

Current Player runtime/evidence is **not** reconstructed from `main`.

Use `implementation/phase4a-successor-cycle`.

As of the 2026-09-14 consolidation:

- current frozen candidate `0.4.0-preqa.13 / 41300`;
- exact candidate commit `92aa9b6e94575c0b5a3e13dfe587aa1a625238a4`;
- automation green;
- targeted physical revalidation pending;
- automation is not owner/device acceptance.

D-0071 does not erase that evidence. It changes the intended next-cycle product/integration strategy.

## Current integrated-MVP / DM boundary

D-0071 establishes that the next implementation cycle is intended to reach the first integrated MVP across:

```text
Player Android <-> hosted/shared services <-> DM tablet/Desktop
```

This design is approved. **Coding is not yet authorized by this checkpoint alone.**

Current exact design resume point:

1. 7D — detailed DM Desktop App product definition;
2. 7E — exact outside-MVP boundary;
3. derive final Git/development topology and implementation gates;
4. obtain explicit coding authorization.

## DM Desktop App current direction

Do not describe `desktopApp/` as only a small preparation shell in future planning.

The intended MVP Desktop App is:

- a full functional DM operational fallback/client containing all approved live Desks (DM Screen, Stage, Dungeon, Combat);
- a richer authoring/management client including Monster, NPC, Homebrew Rules, Zone and Encounter creator/manager capabilities plus the necessary authoring counterparts for persistent Desk data;
- a PC Manager/Audit and campaign/member/permission administration surface;
- a permission-gated System Administration surface including full server backup/export;
- a local Save + explicit Sync client.

Tablet/Desktop UI parity is not required. DM capability fallback is.

## Hosted/shared current direction

The MVP requires:

- Cloudflare Worker/API -> Neon PostgreSQL hosted boundary;
- Descope authentication plus application-owned authorization;
- project-specific sync/revisions/idempotency/tombstones;
- object storage, provider still Pending;
- meaningful audit/history/recovery;
- full server backup/export;
- SRD storage/provenance foundation and later official-SRD grounded AI clarification.

Do not add generalized realtime/WebSockets, Durable Objects, queues, generic ACL/sync platforms or public homebrew publishing merely because they exist.

## Implemented application areas

### `shared/`
Kotlin Multiplatform shared domain/persistence module using SQLDelight. Current Player work includes campaign-scoped characters and the established character domains/features on the successor branch.

### `androidApp/`
Native Kotlin + Jetpack Compose Android application. Current Player runtime authority is the successor branch; future integrated work will add hosted/DM-side behavior according to approved design rather than replacing the mature Player foundation gratuitously.

### `desktopApp/`
Compose Multiplatform Desktop scaffold/current shell. The **target MVP role is substantially larger** under D-0071, but that implementation has not yet been performed.

### `backend/`
TypeScript Cloudflare Worker/API scaffold. The current implementation is still minimal and must become the real hosted authorization/sync/backup boundary during the integrated MVP build.

### `database/`
Hosted PostgreSQL schema/migration/data-loading area. The real hosted application schema/migrations remain to be implemented as part of the integrated MVP.

### `.github/workflows/scaffold-check.yml`
Current normal automated build/test surface. Future integrated work should extend tests proportionately as real backend/database/sync behavior is added.

## Historical/discovery material

Historical checkpoints and milestone branches remain evidence; their old `next` or MVP-exclusion prose may be superseded.

- `docs/checkpoints/LATEST.md` controls practical global resume;
- successor-branch `LATEST.md` controls current Player source/evidence;
- `docs/BRANCH_STATUS.md` controls current branch lifecycle until future integration is approved;
- later specific approved decisions control older general prose;
- frozen QA refs stay immutable;
- deliberately removed refs remain documented in archive material.

Do not restart work from an old checkpoint merely because its historical prose says `next`.

## Authority rule

If documents appear to conflict:

1. `AGENTS.md` controls general governance/working rules, except later explicit owner-approved branch/scope decisions may require a documented governance update before implementation;
2. later specific Approved decisions/clarifications control over older general product/architecture prose;
3. D-0071 controls the current integrated-MVP changes it explicitly makes;
4. `docs/BRANCH_STATUS.md` controls the current branch lifecycle until a later explicit integration decision changes it;
5. `docs/PROJECT_STATE.md` controls current global state/navigation;
6. `docs/checkpoints/LATEST.md` controls the current practical global resume point;
7. the Player successor's own `LATEST.md` controls exact current Player source/evidence;
8. current detailed checkpoints provide exact evidence;
9. domain documents (`PRODUCT`, `ROADMAP`, `WORKFLOW`, `ARCHITECTURE`, `TESTING`) control where not superseded by later specific decisions;
10. historical discovery/checkpoints are contextual evidence only when superseded;
11. surface material contradictions instead of guessing.

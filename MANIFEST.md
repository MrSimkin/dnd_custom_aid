# Repository Manifest

This file maps repository authority. It is not the normal first-read file.

For fast start/resume use:

1. `START_HERE.md`;
2. `CURRENT_TASK.md`;
3. `AGENTS.md`;
4. only the deeper files required by the active task.

## File roles

### Fast operational layer

- `START_HERE.md` — normal entry point and short read order.
- `CURRENT_TASK.md` — volatile current execution pointer only; replace it rather than accumulating history.
- `docs/recovery/INTERRUPTION_RECOVERY.md` — interrupted-chat recovery procedure.
- `docs/recovery/PROJECT_RECOVERY_PROMPT.md` — reusable new-chat bootstrap prompt.

### Durable integrated authority

- `docs/PROJECT_STATE.md` — whole-project integrated state.
- `docs/checkpoints/LATEST.md` — latest durable milestone pointer.
- checkpoint referenced by `LATEST.md` — milestone evidence.
- `docs/BRANCH_STATUS.md` — branch lifecycle/context when needed.
- `docs/decisions/` plus `docs/DECISIONS.md` / `docs/DECISIONS_RECENT.md` — approved product/architecture decisions.
- `docs/ROADMAP.md` — dependency-driven waves.
- `docs/WORKFLOW.md` — deeper development/provider workflow.
- `docs/ARCHITECTURE.md` — integrated architecture.
- `docs/TESTING.md` — verification/evidence model.

Historical checkpoints remain evidence for their time, not automatic resume instructions.

## Authority rules

`main` is the sole normal integrated-MVP trunk.

`CURRENT_TASK.md` may describe work ahead of `main` on an active branch/PR. That does **not** make the unmerged work integrated truth.

If live Git disagrees with `CURRENT_TASK.md`, Git wins and `CURRENT_TASK.md` must be corrected before continuing.

Fast-changing branch/PR/CI state belongs in `CURRENT_TASK.md`, not duplicated across README, AGENTS, PROJECT_STATE or this manifest.

## Current integrated wave map

```text
Wave 1 baseline convergence                       COMPLETE
Wave 2 Shared Integrated-MVP Spine                COMPLETE
Wave 3 hosted foundation / real DEV activation    COMPLETE
Wave 4 Player <-> Server                          COMPLETE
Wave 5 Desktop shell + Campaign Administration    COMPLETE / OWNER-QA ACCEPTED / INTEGRATED
Wave 6 reusable/persistent content architecture   ACTIVE — FOUNDATION INTEGRATED (#46)
Wave 7 Desktop authoring Managers                 AFTER RELEVANT WAVE 6 FOUNDATIONS
```

Wave 5 PR #44 merged as `306377df1a453f531af4b670d2b231c88a3c9419`; post-merge Scaffold `35168920031` passed.

The first Wave 6 reusable-content persistence foundation is integrated through PR #46 merge `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`; post-merge Scaffold `35220099721` passed.

The exact live Wave 6 task must be read from `CURRENT_TASK.md`.

## Controlling recent decisions

- D-0071 — integrated Player + hosted/shared + DM architecture;
- D-0072 — Desktop product and authoring/management semantics;
- D-0073 — integrated MVP boundary, waves and delegation;
- D-0074 — PC Sheet PDF export;
- D-0075 — hard `$0` provider policy, public repo and owner guidance.

D-0072/D-0073 approve the Wave 6 Personal -> Campaign independent-copy/provenance model; routine schema/class/test details remain delegated engineering.

## Implemented areas

- `shared/` — Kotlin Multiplatform domain/persistence/networking/sync foundations, including integrated spine scope/provenance/revision primitives and the integrated Wave 6 local reusable-content catalog/persistence foundation;
- `androidApp/` — Player runtime with real hosted auth/campaign/PC synchronization for the recorded Wave 4 scope;
- `desktopApp/` — persistent local workbench plus real hosted authentication/Campaign Administration integrated in Wave 5, with safe local DB migration support for the Wave 6 foundation;
- `backend/` — TypeScript Worker/API with identity verification and application-owned authorization;
- `database/` — hosted PostgreSQL migrations/contracts;
- `scripts/` — permanent guard scripts;
- `assets/character-sheets/templates/` — PC Sheet PDF visual authorities.

## Provider/cost/security memory

Cloudflare/Descope/Neon are authenticated capability boundaries. An agent without capability stops probing alternatives, finishes safe repo work and gives one exact owner-action packet. Repository CI and real-provider evidence are different. Completed provider actions are not repeated merely because docs changed.

Normal hosted DEV owner/DM identity is Outlook-backed; Gmail is historical/inactive by default. Existing DEV Worker is `dnd-custom-aid-api`; do not recreate/redeploy it without material Worker changes or new evidence.

External-service budget is USD $0. The repository is intentionally public. Never commit/request secrets. Object-storage provider selection remains deferred until assets require it.

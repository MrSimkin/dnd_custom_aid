# Repository Manifest

This file maps current project-control authority so a fresh human or AI can resume without branch archaeology.

## Current authority

`main` is the sole normal integrated-MVP trunk. Current practical truth is controlled by:

- `docs/checkpoints/LATEST.md` — practical resume pointer;
- `docs/PROJECT_STATE.md` — global current state;
- `docs/BRANCH_STATUS.md` — branch lifecycle;
- the checkpoint referenced by `LATEST.md` — milestone evidence;
- approved detailed decisions under `docs/decisions/`.

Wave 5 is complete/integrated. PR #44 merged as `306377df1a453f531af4b670d2b231c88a3c9419`; post-merge Scaffold `35168920031` passed all jobs.

Wave 6 reusable/persistent content architecture is active. Its first reusable-content persistence foundation is integrated through PR #46 merge `013abbb9e57af0ba04fe1e8b678e8ed29522bedd`; post-merge Scaffold `35220099721` passed. The next bounded Wave 6 package must be determined from current authority rather than assumed from stale branch history.

## Start/resume files

- `README.md` — entry point/read order;
- `AGENTS.md` — mandatory operating/provider/security rules;
- `docs/PROJECT_STATE.md` — current implementation state;
- `docs/checkpoints/LATEST.md` — exact practical continuation;
- `docs/BRANCH_STATUS.md` — branch lifecycle;
- `docs/DECISIONS_RECENT.md` — recent decision navigation;
- `docs/ROADMAP.md` — dependency-driven waves;
- `docs/WORKFLOW.md` — development and provider-handoff workflow;
- `docs/ARCHITECTURE.md` — integrated architecture;
- `docs/TESTING.md` — verification/evidence model;
- `docs/recovery/PROJECT_RECOVERY_PROMPT.md` — fresh-chat recovery;
- `docs/recovery/EXTERNAL_PROVIDER_HANDOFF_PROMPT.md` — provider-boundary recovery.

## Controlling recent decisions

- D-0071 — integrated Player + hosted/shared + DM architecture;
- D-0072 — Desktop product and authoring/management semantics;
- D-0073 — integrated MVP boundary, waves and delegation;
- D-0074 — PC Sheet PDF export;
- D-0075 — hard `$0` provider policy, public repo and owner guidance.

D-0072/D-0073 approve the Wave 6 Personal -> Campaign independent-copy/provenance model; routine schema/class/test details remain delegated engineering.

## Implemented areas

- `shared/` — Kotlin Multiplatform domain/persistence/networking/sync foundations, including integrated spine scope/provenance/revision primitives and the Wave 6 local reusable-content catalog/persistence foundation;
- `androidApp/` — Player runtime with real hosted auth/campaign/PC synchronization for the recorded Wave 4 scope;
- `desktopApp/` — persistent local workbench plus real hosted authentication/Campaign Administration integrated in Wave 5, with safe local DB migration support for the Wave 6 foundation;
- `backend/` — TypeScript Worker/API with identity verification and application-owned authorization;
- `database/` — hosted PostgreSQL migrations/contracts;
- `scripts/` — permanent guard scripts;
- `assets/character-sheets/templates/` — PC Sheet PDF visual authorities.

## Current wave map

```text
Wave 1 baseline convergence                       COMPLETE
Wave 2 Shared Integrated-MVP Spine                COMPLETE
Wave 3 hosted foundation / real DEV activation    COMPLETE
Wave 4 Player <-> Server                          COMPLETE
Wave 5 Desktop shell + Campaign Administration    COMPLETE / OWNER-QA ACCEPTED / INTEGRATED
Wave 6 reusable/persistent content architecture   ACTIVE — FOUNDATION INTEGRATED (#46)
Wave 7 Desktop authoring Managers                 AFTER RELEVANT WAVE 6 FOUNDATIONS
```

The next Wave 6 implementation package is not predeclared by this manifest. Determine it from the current architecture, decisions and roadmap.

## Provider/cost/security memory

Cloudflare/Descope/Neon are authenticated capability boundaries. An agent without capability stops probing alternatives, finishes safe repo work and gives one exact owner-action packet. Repository CI and real provider evidence are different. Completed provider actions are not repeated merely because docs changed.

Normal hosted DEV owner/DM identity is Outlook-backed; Gmail is historical/inactive by default. Existing DEV Worker is `dnd-custom-aid-api`; do not recreate/redeploy it without material Worker changes or new evidence.

External-service budget is USD $0. The repository is intentionally public. Never commit/request secrets. Object-storage provider selection remains deferred until assets require it.